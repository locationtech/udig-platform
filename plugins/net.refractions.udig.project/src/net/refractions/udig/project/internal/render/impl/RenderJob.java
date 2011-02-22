package net.refractions.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * An eclipse job that renders a layer. Allows each renderer to run in a separate thread.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class RenderJob extends Job {
    protected volatile IProgressMonitor monitor;

    protected RenderExecutor executor;

    protected Envelope bounds;
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
		Dimension displaySize = mapDisplay.getDisplaySize();
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
        while( !requests.isEmpty() ) {
            try {
                bounds = combineRequests();
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

    private synchronized Envelope combineRequests() throws TransformException, FactoryException {
        Envelope bounds = new Envelope();
        for( ReferencedEnvelope env : requests ) {
            if( !CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), getExecutor().getContext().getCRS()) ){
                env.transform(getExecutor().getContext().getCRS(), true);
            }
            if( bounds.isNull() ){
                bounds.init(env);
            }else{
                bounds.expandToInclude(env);
            }
        }
        requests.clear();
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
    public void setBounds( Envelope bounds ) {
        this.bounds = bounds;
    }


    public synchronized void addRequest(ReferencedEnvelope envelope){
        if (envelope == null) {
            requests.add((ReferencedEnvelope) getExecutor().getContext().getViewportModel().getBounds());
        } else {
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
