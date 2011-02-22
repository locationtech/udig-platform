package net.refractions.udig.project.internal.render.impl;

import java.io.IOException;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Used by Renderer creator for storing extra information about the RenderMetrics, such as the name and id provided by the Extension point.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class InternalRenderMetricsFactory implements IRenderMetricsFactory{
    final IRenderMetricsFactory delegate;
    private IConfigurationElement element;

    public InternalRenderMetricsFactory( IRenderMetricsFactory delegate, IConfigurationElement element) {
        this.delegate = delegate;
        this.element=element;
    }

    public boolean canRender( IRenderContext context ) throws IOException {
        return delegate.canRender(context);
    }

    public InternalRenderMetricsFactory.InternalRenderMetrics createMetrics( IRenderContext context ) {
        return new InternalRenderMetrics(delegate.createMetrics(context), element);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return delegate.getRendererType();
    }


    public static class InternalRenderMetrics implements IRenderMetrics{
        final IRenderMetrics delegate;
        private String name;
        private String description;
        private String id;

        public InternalRenderMetrics( final IRenderMetrics delegate, IConfigurationElement element) {
            super();
            this.delegate = delegate;
            name=element.getAttribute("name"); //$NON-NLS-1$
            id=element.getNamespaceIdentifier()+"."+element.getAttribute("id"); //$NON-NLS-1$ //$NON-NLS-2$
            IConfigurationElement[] descChild = element.getChildren("description"); //$NON-NLS-1$
            if( descChild.length>0 ){
                description=descChild[0].getValue();
            }
        }

        public boolean canAddLayer( ILayer layer ) {
            return delegate.canAddLayer(layer);
        }

        public boolean canStyle( String styleID, Object value ) {
            return delegate.canStyle(styleID, value);
        }

        public Renderer createRenderer() {
            return delegate.createRenderer();
        }

        public IRenderContext getRenderContext() {
            return delegate.getRenderContext().copy();
        }

        public IRenderMetricsFactory getRenderMetricsFactory() {
            return delegate.getRenderMetricsFactory();
        }

        public boolean isOptimized() {
            return delegate.isOptimized();
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }
        public String getId() {
            return id;
        }
        @Override
        public String toString() {
            return delegate.toString();
        }

        public Set<Range> getValidScaleRanges() {
            return delegate.getValidScaleRanges();
        }
    }
}
