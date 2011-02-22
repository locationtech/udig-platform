package net.refractions.udig.project.internal.render.impl;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.internal.render.impl.InternalRenderMetricsFactory.InternalRenderMetrics;
import net.refractions.udig.project.render.IMultiLayerRenderer;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Processes the net.refractions.udig.project.renderer extension point adding the RendererMetricsFactories that apply to the layer to
 * a cache of legal RendererMetricsFactories.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class RendererExtensionProcessor implements ExtensionPointProcessor {
    Layer layer;

    List<InternalRenderMetrics> rFactories = new ArrayList<InternalRenderMetrics>();

    private net.refractions.udig.project.internal.Map map;

    private RenderManager rm;


    /**
     * Creates an new instance of Processor
     *
     * @param layer The layer which needs to be rendered
     */
    public RendererExtensionProcessor( Layer layer, net.refractions.udig.project.internal.Map map, RenderManager rm ) {
        this.layer = layer;
        this.rm=rm;
        this.map=map;
    }

    /**
     * @see net.refractions.udig.core.internal.ExtensionPointProcessor#process(org.eclipse.core.runtime.IExtension,
     *      org.eclipse.core.runtime.IConfigurationElement)
     */
    public void process( IExtension extension, IConfigurationElement element ) {

        try {
            IRenderMetricsFactory createExecutableExtension = (IRenderMetricsFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
            InternalRenderMetricsFactory metricsFactory = new InternalRenderMetricsFactory(
                    createExecutableExtension, element);


            List<IGeoResource> data = layer.getGeoResources();
            for( IGeoResource resource : data ) {

                RenderContext context;

                try{
                    if (IMultiLayerRenderer.class
                            .isAssignableFrom(metricsFactory.getRendererType())) {
                        context = new CompositeRenderContextImpl();
                    } else {
                        context = new RenderContextImpl(layer instanceof SelectionLayer);
                    }
                }catch(Throwable e){
                    context = new RenderContextImpl(layer instanceof SelectionLayer);
                }

                context.setMapInternal(map);
                context.setRenderManagerInternal(rm);
                context.setLayerInternal(layer);
                context.setGeoResourceInternal(resource);

                rFactories.add(metricsFactory.createMetrics(context));
            }
        } catch (CoreException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public List<InternalRenderMetrics> getRFactories() {
        return rFactories;
    }
}
