/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderListenerAdapter;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * This is a composite renderer that does not use composite contexts to track the children.  Instead
 * this renderer keeps a list of the children as a list of RenderInfo objects.  Each of these
 * render info objects has a render executor, and a render metrics.  
 * 
 * <p>This is used by the Tile Rendering system so that the TileRendererCreator
 * does not have to keep track of the render metrics inorder to create a renderer.  The default
 * CompositeRenderer requires the the TileRendererCreator create the renderers.  This renderer
 * uses the information in the render metrics to create the children renderers as required.
 * </p>
 * 
 * @author Emily Gouge
 * @since 1.1.0
 */
public class TiledCompositeRendererImpl extends CompositeRendererImpl implements MultiLayerRenderer {

    /**
     * Used to sort the layers by there z order for composition of images
     */
    private final static Comparator<? super RenderExecutor> comparator = new Comparator<RenderExecutor>(){
        public int compare(RenderExecutor e1,RenderExecutor e2){
            return e1.getContext().getLayer().compareTo(
                    e2.getContext().getLayer());
        }
    };

    /**
     * This is the collection of children this 
     * renderer is responsible for rendering.
     */
    private final Set<RenderInfo> childrenRenderers = new HashSet<RenderInfo>();
    

    
    
    /**
     * Creates a new Renderer
     * 
     * @generated NOT   
     */
    protected TiledCompositeRendererImpl() {
        super();
    }

    /**
     * 
     * Returns a list that is a copy of all the children renders.  This list
     * is a list of RenderInfo which contains the render executor and render metrics.
     * 
     */
    public List<RenderInfo> getChildren() {
        List<RenderInfo> children = new ArrayList<RenderInfo>();
        for( RenderInfo victim : childrenRenderers ) {
            children.add(victim);
        }
        return children;
    }

    
    /**
     * 
     * Returns a list that is a copy of all the children render metrics.
     * 
     */
    public List<AbstractRenderMetrics> getChildrenMetrics() {
        List<AbstractRenderMetrics> children = new ArrayList<AbstractRenderMetrics>();
        for( RenderInfo victim : childrenRenderers ) {
            children.add(victim.getMetrics());
        }
        return children;
    }

    /**
     * Used to create a render executor for a particular renderer.  Also
     * adds a listner to the executor to listen to state events
     * and set the state for the executor.
     * 
     * @param renderer
     */
    protected RenderExecutor createRenderExecutor( Renderer renderer ) {
         final RenderExecutor executor = RenderFactory.eINSTANCE.createRenderExecutor(renderer);
         
        executor.eAdapters().add(new RenderListenerAdapter(){

            /**
             * @see org.locationtech.udig.project.internal.render.RenderListenerAdapter#renderDisposed(org.eclipse.emf.common.notify.Notification)
             */
            protected void renderDisposed( Notification msg ) {
                EObject obj = (EObject) getTarget();
                obj.eAdapters().remove(this);
            }

            /**
             * @see org.locationtech.udig.project.internal.render.RenderListenerAdapter#renderDone()
             */
            protected void renderDone() {
               setState(DONE);
            }

            protected void renderRequest() {
                setRenderBounds(executor.getRenderBounds());
                setState(RENDER_REQUEST);
            }

            @Override
            protected void renderStarting() {
//                setState(STARTING);
            }

            /**
             * @see org.locationtech.udig.project.internal.render.RenderListenerAdapter#renderUpdate()
             */
            protected void renderUpdate() {
                synchronized (TiledCompositeRendererImpl.this) {
                    setState(RENDERING);
                }
            }
        });
        return executor;
    }

    /**
     * Disposes of each of the executors
     */
    public synchronized void dispose() {
        for (RenderInfo renderer : childrenRenderers) {
            renderer.getExecutor().dispose();
        }
        childrenRenderers.clear();
    }

    /**
     * Returns a composite context; however the children of this composite context are not
     * maintained.  This should only be used to get things such as viewport bounds and map information.
     * 
     */
    public CompositeRenderContext getContext() {
        return super.getContext();
    }

    /**
     * Sets the context - the children associated with this context should be null (or empty list).
     * This context is not maintained.
     *
     * @param context
     */
    public void setContext(CompositeRenderContext context){
        super.setContext(context);
    }


    /**
     * @see org.locationtech.udig.project.render.IMultiLayerRenderer#getIContext()
     */
    public IRenderContext getIContext() {
        throw new UnsupportedOperationException("Cannot get the context for a tile composite thing."); //$NON-NLS-1$
    }

    /**
     * Returns all the children render executors
     * 
     * @uml.property name="renderExecutors"
     * @generated NOT
     */
    public Collection<RenderExecutor> getRenderExecutors() {
        ArrayList<RenderExecutor> executors = new ArrayList<RenderExecutor>();
        for( RenderInfo renderExecutor : this.childrenRenderers ) {
            executors.add(renderExecutor.getExecutor());
        }
        return executors;
    }

    /**
     * Determines if a particular layer has been mylared.
     */
    private boolean isFullAlphaUsed(RenderExecutor executor) {
        
        Object object = getContext().getMap().getBlackboard().get("MYLAR"); //$NON-NLS-1$
        
        if( object==null || !((Boolean)object).booleanValue() )
            return true;
        
        if( executor.getContext() instanceof CompositeRenderContext ){
            CompositeRenderContext context=(CompositeRenderContext) executor.getContext();
            if (context.getLayers().contains(getContext().getSelectedLayer()) )
                return true;
            
            return false;
        }
        
        if (executor.getContext().getLayer()==getContext().getSelectedLayer())
            return true;
        
        return false;
    }

    /**
     * Re-composes the image
     * 
     * @throws RenderException
     * @generated NOT
     */
    public void refreshImage() throws RenderException {
        refreshImage(true);
    }

    /**
     * Vitalus:
     * Refreshes map image from buffered images of renderers with or without
     * labels cache painting.
     * 
     * @param paintLabels
     */
    void refreshImage(boolean paintLabels) throws RenderException{
        if( getContext().getMapDisplay()==null ){
            // we've been disposed lets bail
            return;
        }
        synchronized (getContext()) {
            Graphics2D g = null;
            try {
                BufferedImage current = getContext().getImage();
                
                //create a copy of the image to draw into
            
                BufferedImage copy = new BufferedImage(current.getWidth(), current.getHeight(),current.getType() );
            
                g = (Graphics2D)copy.getGraphics();
                
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                
                IMap map = getContext().getMap();
                Object object = map.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
                if( object==null ){
                    IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
                    RGB background = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND); 
                    map.getBlackboard().put(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR, new Color(background.red, background.green, background.blue ));
                    object = map.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
                }
                g.setBackground((Color) object);
                g.clearRect(0,0,copy.getWidth(), copy.getHeight());
                
                SortedSet<RenderExecutor> executors;
                synchronized (childrenRenderers) {
                    executors = new TreeSet<RenderExecutor>(comparator);
                    executors.addAll(getRenderExecutors());
                }
                
                ILabelPainter cache = getContext().getLabelPainter();
                RENDERERS: for( RenderExecutor executor : executors ) {
                    if (!executor.getContext().isVisible()){
                        if(paintLabels && !(executor.getContext().getLayer() instanceof SelectionLayer)){
                            //disable layer from label cache
                              cache.disableLayer(executor.getContext().getLayer().getID().toString());
                          }
                        continue RENDERERS;
                    }
                    if (executor.getState() == NEVER || executor.getState() == STARTING || executor.getState() == RENDER_REQUEST) {
                        continue RENDERERS;
                    } 
                    if( isFullAlphaUsed(executor) ){
                        g.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 1.0f));
                    }else{
                        g.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 0.5f));                        
                    }
                    g.drawRenderedImage(executor.getContext().getImage(), IDENTITY);
                }
                if(paintLabels){
                    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHints(hints);
                    Dimension displaySize = getContext().getImageSize();
                    cache.end(g, new Rectangle(displaySize));
                }
                //update the context with the new image
                ((RenderContextImpl)getContext()).setImage(copy);
                
            } catch (IllegalStateException e) {
                stopRendering();
                return;
            } finally {
                if (g != null)
                    g.dispose();
            }
        }
    }

    /**
     * Kicks all the children renderers to render.
     * <p>
     * First all the states are set to starting then it calls render.
     * </p>
     * 
     * @throws RenderException
     * @generated NOT
     */
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
        // notify that they are starting
        for( RenderInfo renderinfo : childrenRenderers ) {
            renderinfo.getRenderer().setState(STARTING);
        }
        for( RenderInfo renderinfo : childrenRenderers ) {
            renderinfo.getExecutor().render();
        }
    }

    
    /**
     * 
     * Kicks all the children renderers to render.
     * @throws RenderException
     * @see org.locationtech.udig.project.internal.render.Renderer#render(org.locationtech.jts.geom.Envelope)
     */
    public void render( IProgressMonitor monitor ) throws RenderException {
        if (this.childrenRenderers.size() == 0)
            setState(DONE);
        for (RenderInfo renderinfo : this.childrenRenderers) {
            renderinfo.getExecutor().setRenderBounds(getRenderBounds());
            renderinfo.getExecutor().render();
        }
    }

  

    /**
     *  
     * @generated NOT
     */
    public void stopRendering() {
        for (RenderExecutor element : getRenderExecutors()) {
            element.stopRendering();
        }
    }
    
    /**
     * Removes a child from the list of children.
     *
     * @param childtoremove
     */
    public void removeChild(AbstractRenderMetrics childtoremove){
        ArrayList<RenderInfo> toRemove = new ArrayList<RenderInfo>();
        synchronized (childrenRenderers) {
            
            for( RenderInfo child : childrenRenderers ) {
                if (child.getMetrics().equals(childtoremove)){
                    child.getExecutor().dispose();
                    toRemove.add(child);
                }
            }    
            
        }   
        childrenRenderers.removeAll(toRemove);
    }
    
    /**
     * Removes a set of children
     *
     * @param childrentoremove
     */
    public void removeChildren(Collection<AbstractRenderMetrics> childrentoremove){
        for( AbstractRenderMetrics child : childrentoremove ) {
           removeChild(child);
        }
    }
    
    /**
     * Removes all children
     */
    public void removeAllChildren(){
        ArrayList<RenderInfo> toRemove = new ArrayList<RenderInfo>();
        synchronized (childrenRenderers) {
            for( RenderInfo child : childrenRenderers ) {
                    child.getExecutor().dispose();
                    toRemove.add(child);
            }    
            
        }   
        childrenRenderers.removeAll(toRemove);
    }
    
    /**
     * Adds a child and creates a render and render executor for the child.
     *
     * @param metrics
     */
    public void addChild(AbstractRenderMetrics metrics){
        synchronized (childrenRenderers) {
            Renderer render = metrics.createRenderer();
            render.setContext(metrics.getRenderContext());
            RenderExecutor executor = createRenderExecutor(render);
            RenderInfo ri = new RenderInfo(executor, metrics);
            childrenRenderers.add(ri);
        }
    }
    
  
    
    /**
     * Adds a set of children, creating renderers and render executors for the children
     *
     * @param metrics
     */
    public void addChildren(Collection<AbstractRenderMetrics> metrics){
        for( AbstractRenderMetrics abstractRenderMetrics : metrics ) {
            addChild(abstractRenderMetrics);
        }
    }

    
    /**
     * Class to track the information need for succesfull rendering.  This includes
     * a render executor (which references a renderer) and a metrics for creating the renderer.
     * 
     * <p>The metrics contains the context.</p>
     * 
     * @author Emily Gouge
     * @since 1.1.0
     */
    public class RenderInfo{
        private RenderExecutor executor;
        private AbstractRenderMetrics metrics;
        
        public RenderInfo(RenderExecutor executor, AbstractRenderMetrics metrics){
            this.executor = executor;
            this.metrics = metrics;
        }
        
        public AbstractRenderMetrics getMetrics(){
            return this.metrics;
        }
        
        public RenderExecutor getExecutor(){
            return this.executor;
        }
        public Renderer getRenderer(){
            return getExecutor().getRenderer();
        }
        
        
    }
    
    
} // CompositeRendererImpl


