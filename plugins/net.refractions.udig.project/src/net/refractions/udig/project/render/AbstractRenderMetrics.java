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
package net.refractions.udig.project.render;


/**
 * Abstract implementation of the IRenderMetrics.
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractRenderMetrics implements IRenderMetrics {

    protected final IRenderContext context;
    protected final IRenderMetricsFactory factory;

    /**
     * Create  New instance
     * @param context context to use for determining the metrics of the associated renderer
     * @param factory the factory associated with this metrics.
     */
    public AbstractRenderMetrics( final IRenderContext context, final IRenderMetricsFactory factory ) {
        this.context = context;
        this.factory = factory;
    }

    public IRenderContext getRenderContext() {
        return context;
    }

    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    public boolean isOptimized() {
        return false;
    }

}
