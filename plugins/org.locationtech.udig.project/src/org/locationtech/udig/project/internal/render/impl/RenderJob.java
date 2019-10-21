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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Envelope;

/**
 * An eclipse job that renders a layer. Allows each renderer to run in a separate thread.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class RenderJob extends Job {
    protected volatile IProgressMonitor monitor;

    protected RenderExecutor executor;

    protected ReferencedEnvelope bounds;
    /**
     * The queue of requests that must be serviced.  It is expected
     */
    private Queue<ReferencedEnvelope> requests = new LinkedBlockingQueue<ReferencedEnvelope>(); 

    /**
     * Creates an new instance of AbstractRenderer.RenderThread
     */
    public RenderJob( RenderExecutor executor ) {
        super(Messages.RenderExecutorImpl_title); 
        this.executor = executor;
        init();
        
    }

    protected void init() {
        setRule(new RenderJobRule()); 
        setSystem(true);
    }


    /**
     * @return Returns the executor.
     */
    public RenderExecutor getExecutor() {
        return executor;
    }

    /**
     * Returns the progress monitor or null if not rendering.
     * 
     * @return the progress monitor or null if not rendering.
     * @uml.property name="monitor"
     */
    public IProgressMonitor getMonitor() {
        return monitor;
    }

    protected void startRendering( Envelope bounds2, IProgressMonitor monitor ) throws Throwable {
        Envelope bounds=bounds2;
        //validate bounds
        IRenderContext context2 = getExecutor().getContext();
        
        //TODO: should this use getImageBounds from the context instead of the viewport model?
        if (bounds == null
                || bounds.contains(context2.getViewportModel().getBounds())) {
            bounds=null;
        } 
        
        clearBounds(bounds);

        // If the start of the renderer is a render request then 
        if( executor.getRenderer().getState()!=IRenderer.RENDER_REQUEST)
            executor.getRenderer().setRenderBounds(bounds);
        
        monitor.beginTask(Messages.RenderExecutorImpl_1, IProgressMonitor.UNKNOWN);
        if( context2.getLayer()!=null ) {
            initializeLabelPainter(context2);
        }

        executor.getRenderer().render(new SubProgressMonitor(monitor,0));
    }

    protected void clearBounds( Envelope bounds2 ) {
        RenderExecutorImpl.clearImage(bounds, getExecutor());
    }

    protected void initializeLabelPainter( IRenderContext context2 ) {
    	if( !(context2.getLayer() instanceof SelectionLayer) ) {
	        String layerId = getLayerId(context2);
	
	        context2.getLabelPainter().clear(layerId);
	        context2.getLabelPainter().startLayer(layerId);
    	}
    }

    private String getLayerId( IRenderContext context2 ) {
        String layerId = context2.getLayer().getID().toString();
        if ( context2.getLayer() instanceof SelectionLayer )
            layerId = layerId+"-Selection"; //$NON-NLS-1$
        return layerId;
    }

    protected void finalizeLabelPainter( IRenderContext context2 ) {
        String layerId = getLayerId(context2);

        IMapDisplay mapDisplay = context2.getMapDisplay();
        if( mapDisplay==null ){
        	return;
        }
		Dimension displaySize = context2.getImageSize();
        Graphics2D graphics=context2.getImage().createGraphics();
        try{
            context2.getLabelPainter().endLayer(layerId, graphics, new Rectangle(0,0,displaySize.width, displaySize.height));
        }finally{
            graphics.dispose();
        }
    }
    protected void handleException( Throwable renderError ) {

        finalizeLabelPainter(executor.getContext());
        try {
            ProjectPlugin.getPlugin().log(renderError);
            if( renderError.getCause() instanceof NullPointerException )
                handleNullPointerException(renderError);
            else
                handleUnknownError(renderError);
        } catch (Exception e) {
            ProjectPlugin.log(renderError.getLocalizedMessage(), renderError);
        }
    }

    /**
     *
     * @param renderError
     */
    private void handleNullPointerException( Throwable renderError ) {
        executor.getContext().setStatus(ILayer.WARNING);
        executor.getContext().setStatusMessage( Messages.RenderExecutorImpl_2 );  
    }

    /**
     *
     * @param renderError
     */
    private void handleUnknownError( Throwable renderError ) {
            executor.getContext().setStatus(ILayer.ERROR); // indicate to
            // user
            executor
                    .getContext()
                    .setStatusMessage(
                            Messages.RenderExecutorImpl_1 + renderError.getLocalizedMessage()); 
    }

    protected void postRendering() {
        finalizeLabelPainter(executor.getContext());
        // if monitor is cancelled change state to CANCELLED
        if (monitor.isCanceled()) {
            executor.getRenderer().setState(RenderExecutorImpl.CANCELLED);
            executor.setState(RenderExecutorImpl.CANCELLED);
            return;
        }
        executor.getRenderer().setState(IRenderer.DONE);
    }

    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run( IProgressMonitor monitor ) {
    	
    	if( !((RenderManager)getExecutor().getContext().getRenderManager()).isRenderingEnabled() ){
    		return Status.OK_STATUS;
    	}
    	
        setThread(Thread.currentThread());

        this.monitor = monitor;
        while( !requests.isEmpty() && !monitor.isCanceled() ) {
            try {
                bounds = combineRequests();
                if( bounds.isNull() || bounds.isEmpty()){
                	// nothing to draw! Should we draw everything?
                	System.out.println("We combined requests down to nothing?");
                	continue;
                }
                startRendering(bounds, monitor);
                postRendering();
            } catch (Throwable renderError) {
                handleException(renderError);
                renderError.printStackTrace();
                getExecutor().getRenderer().setState(IRenderer.DONE);
            }
        }
        return Status.OK_STATUS;
    }
    
    private synchronized ReferencedEnvelope combineRequests() throws TransformException, FactoryException {
        CoordinateReferenceSystem targetCRS = getExecutor().getContext().getCRS();
    	ReferencedEnvelope bounds = new ReferencedEnvelope( targetCRS );
    	try{
    		for( ReferencedEnvelope env : requests ) {
    			CoordinateReferenceSystem envCRS = env.getCoordinateReferenceSystem();
    			if( env.isNull() || env.isEmpty() || envCRS == null){
    				// these are "invalid" requests and we will skip them
    				System.out.println("We are skipping an empty request");
    				continue; // skip!
    			}
    			
    			//Vitalus: fix for deadlock in RenderJob because of MismatchedReferenceSystemException
    			//during transforming from DefaultEngineeringCRS.GENERIC_2D
    			//to EPSG projection.  (DefaultEngineeringCRS.GENERIC_2D to EPSG 2393 e.g.)
    			if (envCRS != DefaultEngineeringCRS.GENERIC_2D
    					&& envCRS != DefaultEngineeringCRS.GENERIC_3D
    					&& envCRS != DefaultEngineeringCRS.CARTESIAN_2D
    					&& envCRS != DefaultEngineeringCRS.CARTESIAN_3D) {

    				if (!CRS.equalsIgnoreMetadata(envCRS, targetCRS)) {
    					env = env.transform(targetCRS, true);
    				}
    			}
    			if( bounds.isNull() ){
    				bounds.init((Envelope)env);
    			}
    			else {
    				bounds.include(env);            	
    				// bounds.expandToInclude(env);
    			}
    		}
    	}finally{
    		requests.clear();
    	}
        return bounds;
    }

    @Override
    public boolean belongsTo( Object family ) {
        if (executor.getContext() == null)
            return false;
        return family == executor.getContext().getRenderManager();
    }

    /**
     * @param object
     */
    public void setBounds( ReferencedEnvelope bounds ) {
        this.bounds = bounds;
    }

    /**
     * Add a request to draw the provided envelope.
     * <p>
     * Please note that the envelope is referenced with a CoordinateReferenceSystem,
     * the result will be transformed to the viewport CRS (ie the world) in order to
     * determine what part of the screen needs refreshing.
     * <p>
     * @param envelope ReferencedEvelope describing area to draw, or <code>null</code> for the entire screen.
     */
    public synchronized void addRequest(ReferencedEnvelope envelope){
    	if (envelope == null) {
            requests.add( getExecutor().getContext().getImageBounds() );
    	}
    	else if (envelope.getCoordinateReferenceSystem() == null ){
    		throw new IllegalArgumentException("You have asked us to draw a region of the screen without a CRS. Did you intend the viewport CRS?");
    	}
    	else if (envelope.isNull()){
    		throw new IllegalArgumentException("The provided envelope had isNull true");
    	}
    	else if (envelope.isEmpty()){
    		throw new IllegalArgumentException("The provided envelope was empty");
    	}
    	else {
            requests.add(envelope);
        }
        schedule();
    }
    
    /**
     * A scheduling rule that serializes all rendering operations that hit the local filesystem, to
     * avoid bogging down the PC, and lets other kind of georesources be accessed and rendered in a
     * parallel fashion. <br/>
     * The georesource id is used as a discriminator, a Georesource is supposed to be local if its
     * id starts with "file:/" 
     */
    private class RenderJobRule implements ISchedulingRule {
    
        public boolean isConflicting(ISchedulingRule rule) {
            if (!(rule instanceof RenderJobRule))
                return false;
    
            RenderJobRule other = (RenderJobRule) rule;
            if (other == this)
                return true;
            else if (getRenderer() instanceof CompositeRendererImpl)
                return false;
            else
                return other.getRenderer().getClass().equals(
                        getRenderer().getClass())
                        && !isCanceled() && !other.isCanceled() &&
                        isUsingLocalResources(getRenderer()) && isUsingLocalResources(other.getRenderer());
    
        }
    
        private boolean isUsingLocalResources(Renderer renderer) {
            try {
                return renderer.getContext().getGeoResource().getIdentifier().toString().startsWith("file:/"); //$NON-NLS-1$
            } catch(Exception e) {
                return false;
            }
        }
    
        public boolean contains(ISchedulingRule rule) {
            return isConflicting(rule);
        }
    
        Renderer getRenderer() {
            return executor.getRenderer();
        }
    
        boolean isCanceled() {
            if (getMonitor() != null)
                return getMonitor().isCanceled();
            else
                return false;
        }
    
    }

}
