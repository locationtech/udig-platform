/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.render.displayAdapter.impl;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.Map;
import java.util.Set;

import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.Tile;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.commands.IMapTransformCommand;
import org.locationtech.udig.project.ui.commands.IPreMapDrawCommand;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawEditFeatureCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.GC;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Draws to the Viewport. Handles command processing.
 * 
 * @author jeichar
 * @since 0.3
 */
public class ViewportPainter {

    private CommandList commands = new CommandList();
    private ViewportPane pane;
    private int renderState = 0;
    private static final int RENDERING = 1;
    private static final int STARTING = 1 << 1;
    private static final int DONE = 1 << 2;
    private static final AffineTransform IDENTITY = new AffineTransform();
    
    private static final boolean TESTING = ProjectUIPlugin.getDefault().isDebugging();
    
    private DrawEditFeatureCommand editfeatureCommand;

    /**
     * Whether to draw custom <code>IDrawCommand</code>s during painting.
     */
    private boolean enable = true;
    
    /**
     * Construct <code>ViewportPainter</code>.
     * 
     * @param pane owner pane
     */
    public ViewportPainter( ViewportPane pane ) {
        this.pane = pane;
    }

    /**
     * Paints the image and all the currently valid commands on the viewport.
     * 
     * @param g The graphics to draw on.
     * @param image The image to draw.
     * @param minHeight
     * @param minWidth
     */
    public void paint( ViewportGraphics g, Image image, int minWidth, int minHeight ) {
        processCommands(g, true);
        clearPane(g, minWidth, minHeight);
        g.drawImage(image, 0, 0, minWidth, minHeight, 0, 0, minWidth, minHeight);
        
        //draw glass pane
        drawGlassPane(g);
        
        processCommands(g, false);
        runEditFeatureCommand(g);
    }
    
    /**
     * Switches on/off graphics being rendered by custom <code>IDrawCommand</code>s.
     * <br><code>IMapTransformCommand</code>s are processed always regardless 
     * enablement.
     * 
     * @param enable
     */
    public void switchOnOff(boolean enable){
    	this.enable = enable;
    }

    /**
     * @param g
     * @param minWidth
     * @param minHeight
     */
    private void clearPane( ViewportGraphics g, int minWidth, int minHeight ) {
        if (!g.getTransform().isIdentity()) {
            AffineTransform t = g.getTransform();
            g.setTransform(IDENTITY);
            g.clearRect(0, 0, pane.getWidth(), pane.getHeight());
            g.setTransform(t);
        } else {
            if (minWidth < pane.getWidth())
                g.clearRect(minWidth, 0, pane.getWidth() - minWidth, pane.getHeight());
            if (minHeight < pane.getHeight())
                g.clearRect(0, minHeight, pane.getWidth(), pane.getHeight() - minHeight);
        }
    }

    /**
     * Paints the image and all the currently valid commands on the viewport.
     * 
     * @param g The graphics to draw on.
     * @param image The image to draw.
     * @param minHeight
     * @param minWidth
     */
    public void paint( ViewportGraphics g, org.eclipse.swt.graphics.Image image, int minWidth,
            int minHeight ) {
        processCommands(g, true);
        clearPane(g, minWidth, minHeight);
        if( image!=null ) {
            g.drawImage(image, 0, 0, minWidth, minHeight, 0, 0, minWidth, minHeight);
        }
        //draw glass pane
        drawGlassPane(g);
        
        processCommands(g, false);
        runEditFeatureCommand(g);
    }
    
    /**
     * Paints the image of all the given tiles, and all the currently valid commands on 
     * the viewport.
     * 
     * @param g The graphics to draw on.
     * @param tiles the tiles to draw.
     * @param minHeight of total area to be painted
     * @param minWidth of total area to be painted
     */
    public void paint( ViewportGraphics g, Map<ReferencedEnvelope, Tile> tiles, int minWidth, int minHeight ) {
        processCommands(g, true);
        clearPane(g, minWidth, minHeight);

        if (tiles != null) {
        	// determine if we are drawing AWT or SWT
        	GC gc = g.getGraphics(GC.class);
        	if (gc != null ) {
        		// SWT, draw all the tiles
        		Set<ReferencedEnvelope> keySet = tiles.keySet();
            	for (ReferencedEnvelope env : keySet) {
            		Tile tile = tiles.get(env);
            		//System.out.println("SWTImage: "+tile.getSWTImage());
            		ViewportModel viewportModelInternal = tile.getRenderExecutor().getRenderer().getContext().getViewportModelInternal();
            		java.awt.Point a = viewportModelInternal.worldToPixel(new Coordinate(env.getMinX(), env.getMinY()));
		        	java.awt.Point b = viewportModelInternal.worldToPixel(new Coordinate(env.getMaxX(), env.getMaxY()));
                    int width = b.x - a.x;
                    int height = a.y - b.y;
		        	synchronized (tile) {
	                    //an exception can be thrown here if the image has been disposed of
	                    //since it was retrieved or something else bad happens while drawing
	                    //this is OK in our case because another paint event should be coming soon
	                    //which will fix the issue; so for now we just catch the errors
	                    //and don't worry about them.
	                    try{
	                        org.eclipse.swt.graphics.Image im = tile.getSWTImage();
	                        gc.drawImage(im, 0, 0, im.getBounds().width, im.getBounds().height, a.x, b.y, width, height);
	                    }catch (Exception ex){
	                    }    
                    }
		        	if( TESTING ){
                		g.setColor(Color.BLUE);
    	                g.drawLine(a.x, a.y, a.x, b.y);
    	                g.drawLine(a.x, b.y, b.x, b.y);
    	                g.drawLine(b.x, b.y, b.x, a.y);
    	                g.drawLine(b.x, a.y, a.x, a.y);            		
		        	}
            	}   
            }
        	else {
        		// AWT, draw all the tiles
        		Set<ReferencedEnvelope> keySet = tiles.keySet();
            	for (ReferencedEnvelope env : keySet) {
            		Tile tile = tiles.get(env);
            		//System.out.println("BufferedImage: "+tile.getBufferedImage());
            		ViewportModel viewportModelInternal = tile.getRenderExecutor().getRenderer().getContext().getViewportModelInternal();
            		java.awt.Point a = viewportModelInternal.worldToPixel(new Coordinate(env.getMinX(), env.getMinY()));
            		java.awt.Point b = viewportModelInternal.worldToPixel(new Coordinate(env.getMaxX(), env.getMaxY()));
            		g.drawImage((RenderedImage)tile.getBufferedImage(), a.x, b.y);
            		g.setColor(Color.BLUE);
	                g.drawLine(a.x, a.y, a.x, b.y);
	                g.drawLine(a.x, b.y, b.x, b.y);
	                g.drawLine(b.x, b.y, b.x, a.y);
	                g.drawLine(b.x, a.y, a.x, a.y);              		
            	}
        	}
        	
        }
        //draw glass pane
        drawGlassPane(g);
        processCommands(g, false);
        runEditFeatureCommand(g);
    } 

    private void runEditFeatureCommand( ViewportGraphics g ) {
        if (editfeatureCommand == null
                && pane.getMapEditor().getMap().getViewportModelInternal() != null) {
            editfeatureCommand = new DrawEditFeatureCommand(pane.getMapEditor().getMap()
                    .getViewportModelInternal());
        }
        if (editfeatureCommand != null) {
            editfeatureCommand.setGraphics(g, pane);
//            editfeatureCommand.run(null);
        }
    }

    private void processCommands( ViewportGraphics g, boolean pre ) {
        //System.out.println("process commands");
//    	if(enable){
	        try {
	            Object[] varray = null;
	            if (pre)
	                varray = commands.getPreCommands();
	            else
	                varray = commands.getPostCommands();
	            for( int i = 0; i < varray.length; i++ ) {
	                IDrawCommand command = (IDrawCommand) varray[i];
	                
//	                //TODO probably add other command types as  exceptions.
//	                if(!enable && !(command instanceof IMapTransformCommand))
//	                	continue;
	                
	                if (command.isValid()) {
	                    command.setGraphics(g, pane);
	                    try {
	                        command.setMap(this.pane.getMapEditor().getMap());
	                        command.run(new NullProgressMonitor());
	                    } catch (Exception e) {
	                        ProjectUIPlugin.log("", e); //$NON-NLS-1$
                            if(pre){
                                commands.removePre(command);
                            }else{
                                commands.removePost(command);
                            }
	                    }
	                } else {
	                    if (pre)
	                        commands.removePre(command);
	                    else
	                        commands.removePost(command);
	                }
	            }
	        } catch (Exception e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
	            commands.clearCommands();
	        }
//    	}
    }

    /**
     * Dispose of resources
     */
    public void dispose() {
        // do nothing
    }

    /**
     * Adds a draw command to the set of commands that are executed during a paint.
     * 
     * @param command The new command.
     */
    public synchronized void addDrawCommand( IDrawCommand command ) {
        if( command==null )
            throw new NullPointerException();
        commands.add(command);
    }

    private static class CommandList {
        /**
         * Commands that transform the entire display area and are only removed when a rerender
         * occurs Pre commands occur before drawing. Post commands occur after drawing preTI is the
         * count of pre Transform commands postTI is the count of post Transform commands
         */
        IDrawCommand[] preTransform = new IMapTransformCommand[5];
        IDrawCommand[] postTransform = new IMapTransformCommand[5];
        int preTI = 0, postTI = 0;
        /**
         * Commands that decorate display area and are only checked for valid every redraw if
         * invalid they are removed Pre commands occur before drawing. Post commands occur after
         * drawing preNI is the count of pre Normal/Decorator commands postNI is the count of post
         * Normal/Decorator commands
         */
        IDrawCommand[] preNorm = new IDrawCommand[5];
        IDrawCommand[] postNorm = new IDrawCommand[5];
        int preNI = 0, postNI = 0;

        synchronized IDrawCommand[] getPreCommands() {
            if (preNI + preTI == 0)
                return new IDrawCommand[0];
            IDrawCommand[] carray = new IDrawCommand[preNI + preTI];
            if (preTI > 0)
                System.arraycopy(preTransform, 0, carray, 0, preTI);
            if (preNI > 0)
                System.arraycopy(preNorm, 0, carray, preTI, preNI);
            return carray;
        }

        /**
         * 
         */
        public void clearCommands() {
            for (IDrawCommand command : preTransform) {
                if(command != null)
                {
                    command.setValid(false);
                    command.dispose();
                }
            }
            for (IDrawCommand command : postTransform) {
                if(command != null)
                {
                    command.setValid(false);
                    command.dispose();
                }
            }
            preTransform = new IMapTransformCommand[5];
            postTransform = new IMapTransformCommand[5];
            preTI = 0;
            postTI = 0;

            for (IDrawCommand command : preNorm) {
                if(command != null)
                {
                    command.setValid(false);
                    command.dispose();
                }
            }
            for (IDrawCommand command : postNorm) {
                if(command != null)
                {
                    command.setValid(false);
                    command.dispose();
                }
            }
            preNorm = new IDrawCommand[5];
            postNorm = new IDrawCommand[5];
            preNI = 0;
            postNI = 0;
        }

        synchronized void removePost( IDrawCommand command ) {
            command.setValid(false);
            for( int i = 0; i < postNI; i++ ) {
                if (command == postNorm[i]) {
                    if(i < postNI - 1){
                        System.arraycopy(postNorm, i + 1, postNorm, i, postNI - i - 1);
                    }
                    setPostNI(postNI - 1);
                    return;
                }
            }
            for( int i = 0; i < postTI; i++ ) {
                if (command == postTransform[i]) {
                    if(i < postTI - 1){
                        System.arraycopy(postTransform, i + 1, postTransform, i, postTI - i - 1);
                    }
                    setPostTI(postTI - 1);
                    return;
                }
            }
            command.dispose();
        }

        synchronized void removePre( IDrawCommand command ) {
            command.setValid(false);
            for( int i = 0; i < preNI; i++ ) {
                if (command == preNorm[i]) {
                    if(i < preNI - 1){
                        System.arraycopy(preNorm, i + 1, preNorm, i, preNI - i - 1);
                    }
                    setPreNI(preNI - 1);
                    return;
                }
            }
            for( int i = 0; i < preTI; i++ ) {
                if (command == preTransform[i]) {
                    // TODO this is a bug sometimes
                    if(i < preTI - 1){
                        System.arraycopy(preTransform, i + 1, preTransform, i, preTI - i - 1);
                    }
                    setPreTI(preTI - 1);
                    return;
                }
            }
            command.dispose();
        }
        synchronized IDrawCommand[] getPostCommands() {
            if (postNI + postTI == 0)
                return new IDrawCommand[0];
            IDrawCommand[] carray = new IDrawCommand[postNI + postTI];
            if (postTI > 0)
                System.arraycopy(postTransform, 0, carray, 0, postTI);
            if (postNI > 0)
                System.arraycopy(postNorm, 0, carray, postTI, postNI);
            return carray;
        }
        synchronized void add( IDrawCommand command ) {
            if (command instanceof IPreMapDrawCommand) {
                if (command instanceof IMapTransformCommand) {
                    preTransform = add(command, preTransform, preTI);
                    setPreTI(preTI + 1);
                } else {
                    preNorm = add(command, preNorm, preNI);
                    setPreNI(preNI + 1);
                }
            } else {
                if (command instanceof IMapTransformCommand) {
                    postTransform = add(command, postTransform, postTI);
                    setPostTI(postTI + 1);
                } else {
                    postNorm = add(command, postNorm, postNI);
                    setPostNI(postNI + 1);
                }
            }
        }

        void renderstart() {
            if (preNI > 0) {
                for( IDrawCommand command : preNorm ) {
                    if (command != null)
                        command.setValid(false);
                }
                preNorm = new IDrawCommand[preNI];
                setPreNI(0);
            }
            // if( postNI>0 ){
            // postNorm=new IDrawCommand[postNI];
            // setPostNI(0);
            // }
        }

        void renderupdate() {
            if (preTI > 0) {
                for( IDrawCommand command : preTransform ) {
                    if (command != null)
                        command.setValid(false);
                }
                preTransform = new IDrawCommand[preTI];
                setPreTI(0);
            }
            // if( postTI>0 ){
            // postTransform=new IDrawCommand[postTI];
            // setPostTI(0);
            // }
        }

        private IDrawCommand[] add( IDrawCommand command, IDrawCommand[] array, int index ) {
            if (index == array.length) {
                IDrawCommand[] tmp = array;
                array = new IDrawCommand[index * 2];
                System.arraycopy(tmp, 0, array, 0, index);
            }
            array[index] = command;
            return array;
        }

        private synchronized void setPostNI( int postNI ) {
            if (postNI > -1)
                this.postNI = postNI;
        }
        private synchronized void setPostTI( int postTI ) {
            if (postTI > -1)
                this.postTI = postTI;
        }
        private synchronized void setPreNI( int preNI ) {
            if (preNI > -1)
                this.preNI = preNI;
        }
        private synchronized void setPreTI( int preTI ) {
            if (preTI > -1)
                this.preTI = preTI;
        }
    }

    void renderStart() {
        commands.renderstart();
        renderState = STARTING | RENDERING;
    }

    void renderUpdate() {
        if ((renderState & STARTING) == STARTING) {
            commands.renderupdate();
            renderState = RENDERING;
        }
    }

    void renderDone() {
        renderState = DONE;
    }
    
    /**
     * Returns the viewport pane associated with this painter.
     *
     * @return
     */
    public ViewportPane getPane(){
        return this.pane;
    }
    
    /**
     * Draws the glass pane if the ViewportGraphics is 
     * a SWTGraphics or NonAdvancedSWTGraphics.
     * 
     * Will not draw the glasspane if it is null or the
     * Viewport Graphics is a AWT Graphics.
     *
     * @param g
     */
    private void drawGlassPane(ViewportGraphics g){
        GC gc = g.getGraphics(GC.class);
        if (gc != null ){
            GlassPane glass = this.pane.getGlass();
            if (glass != null){
                glass.draw(gc);
            }    
        }    
    }
}
