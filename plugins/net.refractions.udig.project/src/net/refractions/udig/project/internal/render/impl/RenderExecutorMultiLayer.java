/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.Point;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.ExecutorVisitor;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderContext;

import org.eclipse.emf.common.notify.Notification;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A RenderExecutor that runs MultiLayerRenderers such as WMSRenderer and MapGraphic Renderers.
 * 
 * @author   Jesse
 * @since   1.0.0
 */
public class RenderExecutorMultiLayer extends RenderExecutorImpl {

    protected final static class MultiLayerRendererLayerListener extends LayerListener {

        /**
         * Construct <code>CompositeRendererLayerListener</code>.
         * 
         * @param executor
         */
        MultiLayerRendererLayerListener( RenderExecutorMultiLayer executor ) {
            super(executor);
        }

        /**
         * @see net.refractions.udig.project.internal.render.impl.RenderExecutorAdapters.ILayerListener#styleBlackboardChanged(org.eclipse.emf.common.notify.Notification)
         */
        protected void styleBlackboardChanged( Notification msg ) {
            if (executor.getRenderer() instanceof MultiLayerRenderer)
                return;

            executor.getContext().getRenderManager().refresh((ILayer) msg.getNotifier(),
                    (Envelope) null);

        }

        /**
         * @see net.refractions.udig.project.internal.render.impl.RenderExecutorAdapters.ILayerListener#layerNotVisible(net.refractions.udig.project.Layer,
         *      org.eclipse.emf.common.notify.Notification)
         */
        protected void layerNotVisible( Layer layer, Notification msg ) {
            executor.getContext().getLabelPainter().disableLayer(executor.getContext().getLayer().getID().toString());
            CompositeRenderContext context = ((RenderExecutorMultiLayer) executor).getContext();
            if (context.isVisible() && context.getContexts().size() > 1) {
                executor.getContext().getRenderManager().refresh(layer, (Envelope) null);
            } else {
                super.layerNotVisible(layer, msg);
            }
        }

        /**
         * @see net.refractions.udig.project.internal.render.impl.RenderExecutorAdapters.ILayerListener#layerVisible(net.refractions.udig.project.Layer,
         *      org.eclipse.emf.common.notify.Notification)
         */
        protected void layerVisible( Layer layer, Notification msg ) {
            CompositeRenderContext context = ((RenderExecutorMultiLayer) executor).getContext();

            executor.getContext().getLabelPainter().disableLayer(executor.getContext().getLayer().getID().toString());
            if (context.getContexts().size() > 1 )
                executor.getContext().getRenderManager().refresh(layer, (Envelope) null);
            else
                super.layerVisible(layer, msg);
        }
    }

    protected class ContextListener implements CompositeContextListener {

        public void notifyChanged( CompositeRenderContext context, List<RenderContext> contexts, boolean added ) {
            if( added ){
                for( RenderContext context2 : contexts ) {
                    addLayerListener(context2);
                }
            }else{
                for( RenderContext context2 : contexts ) {
                    removeLayerListener(context2);
                }
            }
        }
    }


    protected static class MultiLayerRendererListener extends RendererListener {
        /**
         * Construct <code>CompositeRendererListener</code>.
         * 
         * @param executor
         */
        MultiLayerRendererListener( RenderExecutor executor ) {
            super(executor);
        }

        /**
         * @see net.refractions.udig.project.internal.render.impl.RenderExecutorAdapters.RendererListener#stateChanged(org.eclipse.emf.common.notify.Notification)
         */
        protected void stateChanged( Notification msg ) {
            super.stateChanged(msg);
        }
    }

    /**
     * Construct <code>CompositeRendererExecutor</code>.
     */
    public RenderExecutorMultiLayer() {
        renderJob = new RenderJob(this);
    }

    String jobName;

    ContextListener listener = new ContextListener();

    /**
     * @see net.refractions.udig.project.internal.render.impl.RenderExecutorImpl#getRenderJobName()
     */
    protected String getRenderJobName() {
        if (jobName == null) {
            synchronized (this) {
                if (jobName == null) {
                    StringBuffer buffer = new StringBuffer("["); //$NON-NLS-1$
                    CompositeRenderContext context = (CompositeRenderContext) getContext();
                    for( IRenderContext rc : context.getContexts() ) {
                        buffer.append(rc.getLayer().getName());
                        buffer.append(","); //$NON-NLS-1$
                    }
                    buffer.replace(buffer.length() - 1, buffer.length(), "]"); //$NON-NLS-1$
                    jobName = buffer.toString();
                }
            }
        }
        return jobName;
    }

	/**
	 * @see net.refractions.udig.project.internal.render.impl.RenderExecutorImpl#setRenderer(net.refractions.udig.project.render.Renderer)
	 */
	@SuppressWarnings("unchecked") 
	public void setRenderer(Renderer newRenderer) {
		if (getContext() != null)
			for (IRenderContext context : getContext().getContexts()) {
				removeLayerListener(context);
			}
		
		if (getContext() != null )
			getContext().removeListener(listener);
		setRendererInternal(newRenderer);
		if (getContext() != null)
		    getContext().addListener(listener);
		for (IRenderContext context : ((CompositeRenderContext) newRenderer.getContext()).getContexts()) {
			addLayerListener(context);
		}
	}

	protected void setRendererInternal(Renderer newRenderer) {
		super.setRenderer(newRenderer);
	}

    /**
     * @see net.refractions.udig.project.internal.render.impl.RenderExecutorImpl#getContext()
     */
    public CompositeRenderContext getContext() {
        return (CompositeRenderContext) super.getContext();
    }

    protected void resyncState( Renderer renderer ) {
//        if (getContext() == null)
//            return;
//        List<IRenderContext> contexts = getContext().getContexts();
//        synchronized (contexts) {
//            for( IRenderContext context : contexts ) {
//                setLayerState(context, renderer.getState());
//            }
//        }

    }

    /**
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl#getInfo(Point, Layer)
     *      public InfoList getInfo(Point screenLocation) throws IOException { return
     *      renderer.getInfo(screenLocation); }
     */

    protected LayerListener getLayerListener() {
        return new MultiLayerRendererLayerListener(this);
    }

    protected RendererListener getRendererListener() {
        return new MultiLayerRendererListener(this);
    }

    /**
     * @see net.refractions.udig.project.internal.render.impl.RenderExecutorImpl#visit(net.refractions.udig.project.render.ExecutorVisitor)
     */
    public void visit( ExecutorVisitor visitor ) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void dispose() {

        for( Layer layer : getContext().getMapInternal().getLayersInternal() ) {
            layer.eAdapters().remove(listener);
        }
        super.dispose();
    }
}
