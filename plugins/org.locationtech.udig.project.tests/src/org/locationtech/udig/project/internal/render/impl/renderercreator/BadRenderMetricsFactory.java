/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl.renderercreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.util.Range;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

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
