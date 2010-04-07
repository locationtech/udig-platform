package net.refractions.udig.project.internal.render.impl;

import java.io.IOException;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.geotools.util.Range;

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
    

    public static class InternalRenderMetrics extends AbstractRenderMetrics {
        final AbstractRenderMetrics delegate;
        private String name;
        private String description;
        private String id;

        public InternalRenderMetrics( final AbstractRenderMetrics delegate, IConfigurationElement element) {
            super(delegate.getRenderContext(), delegate.getRenderMetricsFactory(), delegate.getExpectedStyles());
            this.delegate = delegate;
            name=element.getAttribute("name"); //$NON-NLS-1$
            id=element.getNamespaceIdentifier()+"."+element.getAttribute("id"); //$NON-NLS-1$ //$NON-NLS-2$
            IConfigurationElement[] descChild = element.getChildren("description"); //$NON-NLS-1$
            if( descChild.length>0 ){
                description=descChild[0].getValue();
            }
        }

        @Override
        public boolean canAddLayer( ILayer layer ) {
            return delegate.canAddLayer(layer);
        }

        @Override
        public boolean canStyle( String styleID, Object value ) {
            return delegate.canStyle(styleID, value);
        }

        @Override
        public Renderer createRenderer() {
            return delegate.createRenderer();
        }

        @Override
        public IRenderContext getRenderContext() {
            return delegate.getRenderContext().copy();
        }

        @Override
        public IRenderMetricsFactory getRenderMetricsFactory() {
            return delegate.getRenderMetricsFactory();
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

        @Override
        public Set<Range<Double>> getValidScaleRanges() {
            return delegate.getValidScaleRanges();
        }
        
        @Override
        public long getDrawingTimeMetric(){
            return delegate.getDrawingTimeMetric();
        }
        
        @Override
        public long getLatencyMetric(){    
            return delegate.getLatencyMetric();
        }
        
        @Override
        public double getResolutionMetric(){
            return delegate.getResolutionMetric();
        }
        
        @Override
        public double getRenderAppearanceMetric(IStyleBlackboard blackboard){
            return delegate.getRenderAppearanceMetric(blackboard);
        }
        
        @Override
        public double getUserAppearanceMetric(IStyleBlackboard blackboard){
            return delegate.getUserAppearanceMetric(blackboard);
        }
        
    }
}