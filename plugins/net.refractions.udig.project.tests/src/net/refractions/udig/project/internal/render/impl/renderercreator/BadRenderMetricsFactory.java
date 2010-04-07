/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.internal.render.impl.renderercreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.util.Range;

/**
 * A Rendermetrics factory that makes a RenderMetrics that behaves badly which makes a renderer that
 * behaves badly :).  For testing obviously.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BadRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * if the georesource can resolve to this string then every method will throw an exception
     */
    public final static String ALWAYS_EXCEPTION="ALWAYS_EXCEPTION"; //$NON-NLS-1$
    /**
     * if the georesource can resolve to this string then every method except
     * {@link #canRender(IRenderContext)} will throw an exception
     */
    public final static String CAN_RENDER_NO_EXCEPTION="CAN_RENDER_NO_EXCEPTION"; //$NON-NLS-1$
    /**
     * if the georesource can resolve to this string then all methods in the metrics will throw an exception
     * but not in the metrics factory
     */
    public final static String CAN_CREATE_METRICS="CAN_CREATE_METRICS_LAYER"; //$NON-NLS-1$
    /**
     * if the georesource can resolve to this string then only the canAddLayerMethod will throw an exception
     */
    public final static String CAN_ADD_LAYER_EXCEPTION="CAN_ADD_LAYER_EXCEPTION"; //$NON-NLS-1$
    
    private final static List<String> ALL=Arrays.asList(new String[]{
      ALWAYS_EXCEPTION, CAN_CREATE_METRICS, CAN_RENDER_NO_EXCEPTION,
      CAN_ADD_LAYER_EXCEPTION
    
    });
    public boolean canRender( IRenderContext context ) throws IOException {
        String resolve=context.getGeoResource().resolve(String.class, new NullProgressMonitor());
        if( ALL.contains(resolve) ){
            return true;
        }
        throw new RuntimeException();
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        String resolve;
        try {
            resolve = context.getGeoResource().resolve(String.class, new NullProgressMonitor());
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        if( resolve==CAN_CREATE_METRICS ){
            return new BadRenderMetrics(context, this);
        }
        if( resolve==CAN_ADD_LAYER_EXCEPTION ){
            return new BadRenderMetrics(context, this){
                @Override
                public boolean canStyle( String styleID, Object value ) {
                    return false;
                }
                @Override
                public Renderer createRenderer() {
                    return new MultiLayerRenderer();
                }
                @Override
                public boolean isOptimized() {
                    return false;
                }
                @Override
                public IRenderContext getRenderContext() {
                    return context;
                }
                
                @Override
                public IRenderMetricsFactory getRenderMetricsFactory() {
                    return factory;
                }
            };
        }
        throw new RuntimeException();
    }

    public Class< ? extends IRenderer> getRendererType() {
        return MultiLayerRenderer.class;
    }
    
    class BadRenderMetrics extends AbstractRenderMetrics{

        public BadRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
            super(context, factory, new ArrayList<String>());
        }

        public boolean canAddLayer( ILayer layer ) {
            throw new RuntimeException();
        }

        public boolean canStyle( String styleID, Object value ) {
            throw new RuntimeException();
        }

        public Renderer createRenderer() {
            throw new RuntimeException();
        }

        public IRenderContext getRenderContext() {
            throw new RuntimeException();
        }

        public IRenderMetricsFactory getRenderMetricsFactory() {
            throw new RuntimeException();
        }

        public boolean isOptimized() {
            throw new RuntimeException();
        }

        public Set<Range<Double>> getValidScaleRanges() {
            return null;
        }
        
    }

}
