/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.text.MessageFormat;
import java.util.Collection;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.internal.render.ExecutorVisitor;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Envelope;

/**
 * An Executor specifically for executing CompositeRenderers.
 * <p>
 * This class appears to actually *be* the uDig appplication :-)
 * <p>
 * This class is supposed to listen to all events going down and is supposed to queue up some work
 * and let 'er rip.
 * <ul>
 * <li>RENDER_REQUEST causes this to refresh()
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class RenderExecutorComposite extends RenderExecutorMultiLayer {

    protected static class CompositeRendererJob extends RenderJob {

        private static final int TIMEOUT = 20000;

        /**
         * Construct <code>CompositeRendererJob</code>.
         * 
         * @param executor
         */
        public CompositeRendererJob( RenderExecutorComposite executor ) {
            super(executor);
            setPriority(Job.INTERACTIVE);
        }

        @Override
        protected void clearBounds( Envelope bounds2 ) {
            // ignore because we don't want to clear until first update is received from one of the
            // contained renderers
        }

        @Override
        protected void finalizeLabelPainter( IRenderContext context2 ) {
            // do nothing
        }

        /**
         * @see org.locationtech.udig.project.internal.render.impl.RenderJob#getExecutor()
         */
        public RenderExecutorComposite getExecutor() {
            return (RenderExecutorComposite) executor;
        }

        /**
         * Incrementally updates the display until rendering is complete.
         * 
         * @throws InterruptedException
         * @throws RenderException
         */
        public void incrementalUpdate() throws InterruptedException, RenderException {
            getExecutor().resetTimeout();
            final int wait_period = 400;

            synchronized (getExecutor()) {
                getExecutor().inUpdate = true;
            }

            CompositeRendererImpl renderer2 = getExecutor().getRenderer();
            long start = System.currentTimeMillis();
            synchronized (getExecutor()) {
                while( (getMonitor() != null && !getMonitor().isCanceled())
                        && isRendering(renderer2) && getExecutor().timeout < TIMEOUT ) {
                    getExecutor().wait(wait_period);
                    long now = System.currentTimeMillis();
                    getExecutor().timeout = (int)(now - start);
                    if (isRendering(renderer2)) {
                        renderer2.refreshImage(false);
                        getExecutor().setState(RENDERING);
                    }
                }
            }
//            if (getExecutor().timeout>= TIMEOUT){ 
//                System.out.println("Timed Out"); 
//            }            

            if( ProjectPlugin.getPlugin().isDebugging()){
                String message = "Time taken to render is: " + (System.currentTimeMillis() - start) / 1000 + "seconds"; //$NON-NLS-1$ //$NON-NLS-2$
                ProjectPlugin.trace(Trace.RENDER, getClass(), message, null);
            }
            synchronized (getExecutor()) {
                if (getMonitor() != null && !getMonitor().isCanceled()) {
                    renderer2.refreshImage();
                }
                getExecutor().setState(IRenderer.DONE);
                getExecutor().inUpdate = false;
            }
        }

        @Override
        protected void init() {
        }

        @Override
        protected void initializeLabelPainter( IRenderContext context2 ) {
            // do nothing
        }

        /**
         * @see org.locationtech.udig.project.internal.render.impl.RenderJob#postRendering()
         */
        protected void postRendering() {

            if (monitor.isCanceled()) {
                executor.getRenderer().setState(CANCELLED);
                for( RenderExecutor renderer : ((CompositeRendererImpl) executor.getRenderer())
                        .getRenderExecutors() ) {
                    if (renderer.getContext().isVisible() && executor.getState() != IRenderer.DONE) {
                        renderer.getContext().setStatus(ILayer.WARNING);
                        renderer
                                .getContext()
                                .setStatusMessage(
                                        "Timed out while rendering this layer.  Seems to have blocked, check that the server is up"); //$NON-NLS-1$
                    }
                }
                return;
            }
        }

        /**
         * @see org.locationtech.udig.project.internal.render.impl.RenderJob#startRendering()
         */
        protected void startRendering( Envelope bounds, IProgressMonitor monitor ) throws Throwable {
            // need to show that we are in update just in case a renderer triggers a state event
            // in the RenderExecutor.render() method.
            synchronized (getExecutor()) {
                getExecutor().inUpdate = true;
            }
            super.startRendering(bounds, monitor);
            incrementalUpdate();
        }
    }

    /**
     * Listens to the render events and when a RENDER_REQUEST occurs it will start the job that
     * composes the image every 400 ms.
     * <p>
     * It will also set some time out states so the screen is not hammered with refreshes as the
     * children are busy.
     * 
     * @author jeichar
     * @since 0.6.0
     */
    protected static class CompositeRendererListener extends MultiLayerRendererListener {

        /**
         * Construct <code>CompositeRendererListener</code>.
         * 
         * @param executor
         */
        CompositeRendererListener( RenderExecutorComposite executor ) {
            super(executor);
        }

        RenderExecutorComposite getExecutor() {
            return (RenderExecutorComposite) executor;
        }

        /**
         * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer.MultiLayerRendererListener#stateChanged(org.eclipse.emf.common.notify.Notification)
         */
        protected void stateChanged( Notification msg ) {
            switch( msg.getNewIntValue() ) {
            case Renderer.RENDERING:
                getExecutor().resetTimeout();
                synchronized (getExecutor()) {
                    // I think I want to do an update.
                    // lets make it so if a renderer says update then we do it.
                    getExecutor().setState(RENDERING);
                }
                break;
            case Renderer.DONE:
                try {
                    synchronized (getExecutor()) {
                        if (!getExecutor().inUpdate) {
                            getExecutor().getRenderer().refreshImage();
                            executor.setState(Renderer.DONE);
                        }
                        if (!isRendering(getExecutor().getRenderer())
                                || (getExecutor().renderJob.getMonitor() != null && getExecutor().renderJob
                                        .getMonitor().isCanceled())) {
                            getExecutor().notifyAll();
                        }
                    }
                    // state will be set in next else statement
                } catch (RenderException e) {
                    // won't happen.
                    ProjectPlugin.log("", e); //$NON-NLS-1$
                }
                break;
            case Renderer.RENDER_REQUEST:
                //use this block and synchronization, such that we won't lose delivery because of too many rendering calls
                synchronized (getExecutor()) {
                    boolean oldValue = executor.eDeliver();
                    try {
                        executor.eSetDeliver(false);
                        executor.setState(msg.getNewIntValue());
                    } finally {
                        executor.eSetDeliver(oldValue);
                    }
                }
                ((RenderExecutorComposite) executor).refresh();
                break;
            case Renderer.STARTING:
                synchronized (getExecutor()) {
                    boolean oldValue2 = executor.eDeliver();
                    try {
                        executor.eSetDeliver(false);
                        executor.setState(msg.getNewIntValue());
                    } finally {
                        executor.eSetDeliver(oldValue2);
                    }
                }
                break;
            }
        }

    }

    class RedrawJob implements Runnable {

        ReferencedEnvelope bounds;

        public void run() {

            stopRendering();

            if (!Thread.currentThread().isInterrupted())
                runRenderJob();
            return;
        }

        void runRenderJob() {
            dirty = false;
            if (getContext().getMapDisplay() == null || getContext().getMapDisplay().getWidth() < 1
                    || getContext().getMapDisplay().getHeight() < 1)
                return;
            if (RenderExecutorComposite.this.getState() == IRenderer.DISPOSED)
                throw new RuntimeException("attempted to run a disposed renderer"); //$NON-NLS-1$

            clearImage(bounds, RenderExecutorComposite.this);
            renderJob.setName(getRenderJobName());
            renderJob.addRequest(bounds);

            if (!Thread.currentThread().isInterrupted()) {
                redraw = null;
            }
        }

    }

    /**
     * @param renderer2
     * @return
     */
    static boolean isRendering( CompositeRendererImpl renderer2 ) {
        Collection<RenderExecutor> executors = renderer2.getRenderExecutors();
        for( RenderExecutor executor : executors ) {
            if (executor.getContext().isVisible()
                    && (executor.getState() == IRenderer.RENDERING
                            || executor.getState() == IRenderer.STARTING || executor.getState() == IRenderer.RENDER_REQUEST))
                return true;
        }
        return false;
    }

    private boolean inUpdate = false;

    private volatile Thread redraw;

    Job refreshJob;

    int timeout = 0;

    /**
     * Construct <code>CompositeRendererExecutorImpl</code>.
     */
    public RenderExecutorComposite() {
        renderJob = new CompositeRendererJob(this);
    }

    @Override
    protected LayerListener getLayerListener() {
        return new LayerListener(this){
            @Override
            public void notifyChanged( Notification msg ) {
                // do nothingsuper.notifyChanged(msg);
            }
        };
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl#getRenderer()
     */
    public CompositeRendererImpl getRenderer() {
        return (CompositeRendererImpl) renderer;
    }

    @Override
    protected RendererListener getRendererListener() {
        return new CompositeRendererListener(this);
    }

    @Override
    protected String getRenderJobName() {
        return MessageFormat.format(Messages.RenderExecutorImpl_message, new Object[]{getContext()
                .getMap().getName()});
    }

    /**
     * Continually refresh the display until all renderers are done.
     */
    public synchronized void refresh() {
        if (refreshJob == null) {
            refreshJob = new Job(getRenderJobName()){
                @Override
                protected IStatus run( IProgressMonitor monitor ) {
                    try {
                        ((CompositeRendererJob) renderJob).incrementalUpdate();
                    } catch (Exception e) {
                        // log error
                        ProjectPlugin.log(null, e);
                    }
                    return Status.OK_STATUS;
                }

            };
            refreshJob.setSystem(false);
            refreshJob.setPriority(Job.INTERACTIVE);
        }
        refreshJob.schedule();
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer#registerFeatureListener()
     */
    protected void registerFeatureListener() {
        // do nothing
    }

    @Override
    public synchronized void render() {

        RedrawJob runnable = new RedrawJob();
        runnable.bounds = getRenderBounds();
        if (redraw != null)
            redraw.interrupt();
        redraw = new Thread(runnable);
        redraw.start();
    }

    /**
     * Signals that the timeout for rendering should be reset.
     * <p>
     * The timeout is used to determine whether a renderer has stalled
     * </p>
     */
    protected synchronized void resetTimeout() {
        timeout = 0;
    }
    protected void resyncState( Renderer renderer ) {

        // FIXME sync sub renderers, witness composite renderer
        //
        // for( Renderer child : parent.children() ) resyncState( child );
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl#setRenderer(org.locationtech.udig.project.render.Renderer)
     */
    public void setRenderer( Renderer newRenderer ) {
        renderJob.setSystem(false);
        super.setRendererInternal(newRenderer);
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl#stopRendering()
     */
    public void stopRendering() {
        for( RenderExecutor executor : getRenderer().getRenderExecutors() ) {
            executor.stopRendering();
        }
        super.stopRendering();
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl#visit(org.locationtech.udig.project.render.ExecutorVisitor)
     */
    public void visit( ExecutorVisitor visitor ) {
        visitor.visit(this);
    };
}
