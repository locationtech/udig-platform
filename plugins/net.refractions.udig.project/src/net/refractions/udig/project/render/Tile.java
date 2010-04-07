/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.impl.RenderExecutorComposite;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;


/**
 * At tile represents a single space on the map within a specific ReferencedEnvelope.
 * It holds a RenderExecutorComposite for fetching its image, and an SWTImage (which is 
 * disposed at various times).  It listens to events for when to fetch, 
 * dispose, and construct new images.
 * 
 * @author GDavis
 *
 */
public class Tile {
	
    /**
     * These are the states of the tile.  This state represents
     * if the tile needs to be re-rendered or not.  A state of
     * new or invalid means the tile should be re-rendered
     * 
     */
    public enum RenderState { NEW, RENDERED, INVALID };
    
    /**
     * These states represent the state of the context.  If the context
     * is invalid than the rendering stack no longer matches the rendering
     * stack the user has defined and the rendering stack needs
     * to be updated.
     */
    public enum ContextState { OKAY, INVALID };
    
    /**
     * These states represent if the tile is on or off screen.  This information
     * is used to determine what tiles can be disposed.
     */
    public enum ScreenState { ONSCREEN, OFFSCREEN };
    
    /**
     * These states represent if the tile has been validated in response to a user
     * event.  
     * <p>
     * This information is used along with the screen state to determine
     * if a tile can be disposed.
     */
    public enum ValidatedState {VALIDATED, OLD};
    
    /**
     * The bounds of the tile
     */
	private ReferencedEnvelope env;
	
	/**
	 * The top level render executor that is responsible for
	 * rendering the stack. 
	 * <p>This render executor should have TiledCompositeRendererImpl
	 * as it's render.</p>
	 */
	private RenderExecutorComposite renderExecutorComp;
	
	/**
	 * The SWT image that represents the tile.
	 */
	private org.eclipse.swt.graphics.Image swtImage;
	
	/**
	 * The size of the tile in pixels
	 */
	private int tileSize;

	/**
	 * The state of the image.
	 */
	private RenderState renderState = RenderState.NEW;
	
	/**
	 * The state of the rendering stack
	 */
	private ContextState contextState = ContextState.INVALID;
	
	/**
	 * If the tile is on screen or not.
	 */
	private ScreenState screenState = ScreenState.OFFSCREEN;

	/**
	 * If after an update event the tile has been validated
	 */
	private ValidatedState tileState = ValidatedState.VALIDATED;
	
	/**
	 * A listener that is notified when the state is changed.
	 */
	private TileStateChangedListener listener = null;
	
	/**
	 * for locking on the SWT image to prevent creating it multiple times
	 */
	private Object SWTLock = new Object();
	
	public Tile(ReferencedEnvelope env, RenderExecutorComposite rec, int tileSize) {
		this.env = env;
		this.renderExecutorComp = rec;
		this.tileSize = tileSize;
		
		
		rec.eAdapters().add(new AdapterImpl(){
		    
		    /**
		     * Does nothing; clients may override so that it does something.
		     */
		    @Override
		    public void notifyChanged(Notification msg)
		    {
		      //we want to listen to rendering events.
		        if (msg.getFeatureID(Renderer.class) == RenderPackage.RENDERER__STATE) {
		            //this first time around the swtimage will be null
		            //if something changes the second time around we only
		            //want to invalidate the swt image once we are done
		            //rendering
//		            System.out.println("render state changed on tile");
//		            System.out.println((msg.getNewIntValue() == IRenderer.DONE)  + "; state = done");
		            if(  swtImage == null || msg.getNewIntValue()==IRenderer.DONE){
		                //we only care about done events
		                disposeSWTImage();
		            }
		        }
		    }
		    
		});
	}
	
	public void setStateChangedListener(TileStateChangedListener listener){
	    this.listener = listener;
	}
	
	/**
	 * Disposes of the swt image.
	 */
	public void disposeSWTImage() {
		/**
		 * synchronize this code to ensure we don't have concurrency issues
		 */
		synchronized (SWTLock) {
			if (swtImage != null) {
				swtImage.dispose();
				swtImage = null;
			}
		}
		//can we assume that once the image is disposed it is offscreen?
		setScreenState(ScreenState.OFFSCREEN);
	}
	
	/**
	 * Disposes of the tile.
	 */
	public void dispose(){
	    disposeSWTImage();
	    setScreenState(ScreenState.OFFSCREEN);
	    //TODO: figure out how to properly dispose of the render executors
	    //we cannot just call dispose because this drops all the layer listeners
	    //that listen for events 
//	    getRenderExecutor().dispose();
	}
	
	/**
	 * Gets the SWT image; if the image is null or disposed then
	 * it tried to create it before it is returned.
	 *
	 * @return
	 */
	public org.eclipse.swt.graphics.Image getSWTImage() {
		if (swtImage == null || swtImage.isDisposed()) {
			createSWTImage();
		}
		return swtImage;
	}
	
	/**
	 * Gets the buffered image (representing the tile) from the render executor.
	 *
	 * @return
	 */
	public BufferedImage getBufferedImage() {
		BufferedImage buffImage = null;
		try {
			if (renderExecutorComp == null) {
	            //create an empty image
				buffImage = new BufferedImage(getTileSize(), getTileSize(), BufferedImage.TYPE_INT_ARGB);
	            Graphics2D graphics = buffImage.createGraphics();
	            graphics.setColor(Color.WHITE);
	            graphics.fillRect(0, 0, getTileSize(), getTileSize());
	            graphics.setColor(Color.RED);
	            graphics.drawLine(0, 0, getTileSize(), getTileSize());
	            graphics.drawLine(0, getTileSize(), getTileSize(), 0);
	            graphics.drawString("No Render Executor For Tile", getTileSize() / 2, getTileSize()/2); //$NON-NLS-1$
	            graphics.dispose();
			}
			else {
				buffImage = renderExecutorComp.getContext().getImage();
			}
		} catch (Exception ex) {
            ex.printStackTrace();
        }
		return buffImage;
	}

	/**
	 * Creates an swt image from the tiles buffered image.
	 */
	private void createSWTImage() {
		/**
		 * synchronize this code to prevent multiple threads from creating the
		 * SWT image more times than needed
		 */
		synchronized (SWTLock) {
			// if the SWTImage is created once the lock is gained, exit
			if (swtImage != null && !swtImage.isDisposed()) {
				return;
			}
			// otherwise try creating the SWTImage now
			try {
				BufferedImage buffImage = getBufferedImage();
				swtImage = AWTSWTImageUtils.createSWTImage(buffImage, false);
			} catch (Exception ex) {
	            ex.printStackTrace();
	        }
		}
	}

	/**
	 * 
	 *
	 * @return The size of the tile in pixels.
	 */
	public int getTileSize() {
		return tileSize;
	}
	
	/**
	 * 
	 *
	 * @return  the bounds of the tile
	 */
	public ReferencedEnvelope getReferencedEnvelope() {
		return env;
	}
	
	/**
	 * 
	 *
	 * @return the parent render executor
	 */
	public RenderExecutorComposite getRenderExecutor() {
		return renderExecutorComp;
	}
	
	/**
	 * Sets the state of the tiles image.
	 * 
	 * <p>
	 * See getRenderState() for a description of the valid states.
	 *
	 * @param newState
	 */
	public void setRenderState(RenderState newState){
	    this.renderState = newState;
        if (listener != null) {
            listener.renderStateChanged(this);
        }
    }
	
	/**
	 * Gets the state of the tiled image.  
	 * <p>
	 * One Of:
	 * <ul>
	 *  <li>RenderState.NEW - a new tile that needs to be rendered
	 *  <li>RenderState.Renderer - the tile has been rendered or is in the state of being rendered
	 *  <li>RenderState.Invalid - something has changed and the tile's rendered image is not 
	 *  longer valid and needs to be re-rendered
	 * </ul>
	 *
	 * @return
	 */
	public RenderState getRenderState(){
	    return this.renderState;
	}
	
	/**
	 * This function returns the state of the tile render stack.  If the context is invalid then
	 * the context needs to be updated before the tile is rendered.
	 * <p>
	 * Should be one of:
	 * <ul>
	 *   <li>INVALID - The context needs to be updated.
	 *   <li>OKAY - The context is okay and does not need updating.
	 * </ul>
	 * 
	 * @return the state of the tiles rendering stack
	 */
	public ContextState getContextState(){
	    return this.contextState;
	}
	
	/**
	 * Sets the state of the tile rendering stack. 
	 * <p>
	 * See getContextState() for valid value descriptions.
	 * 
	 * @param newState
	 */
	public void setContextState(ContextState newState){
	    this.contextState = newState;
        if (listener != null){
            listener.contextStateChanged(this);
        }

	}
	
	/**
	 * Sets if the tile is on screen or not.  
	 * <p>
	 * This is used with other information to determine if a tile can
	 * be disposed of.  Valid values include:
	 * <ul>
	 *  <li>ONSCREEN - the tile has been requested by the viewport therefore we assume it is on screen
	 *  <li>OFFSCREEN - this tile was not requested by the viewport
	 * </ul>
	 *
	 * @return
	 */
	public ScreenState getScreenState(){
	    return this.screenState;
	}
	/**
	 * Sets the screen state.
	 * <p>
	 * See getScreenState() for a description of the valid values.
	 *
	 * @param newState
	 */
	public void setScreenState(ScreenState newState){
	    this.screenState = newState;
        if (listener != null){
            listener.screenStateChanged(this);
        }

	}
	
	/**
	 * Gets the validation state.
	 * <p>
	 * This is used in conjunction with the screen state to determine it a tile can be disposed of. This
	 * state is set during a refresh event that is triggered from some gui event. Valid values include:
	 * <ul>
	 *   <li>VALIDATED - The tile is validated and ready to be used for painting on the screen.  Don't remove this tile.
	 *   <li>OLD - This tile is an old tile that if off screen can be removed.
	 * </ul>
	 *  
	 *
	 * @return
	 */
	public ValidatedState getTileState(){
	    return this.tileState;
	}
	/**
	 * Sets the validation state.  
	 * <p>
	 * See getTileState() for a description of valid values.
	 *
	 * @param newState
	 */
	public void setTileState(ValidatedState newState){
	    this.tileState = newState;
	    if (listener != null){
	        listener.validationStateChanged(this);
	    }
	    
	}
}
