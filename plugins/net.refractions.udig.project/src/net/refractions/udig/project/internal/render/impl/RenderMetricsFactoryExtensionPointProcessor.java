/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.project.internal.render.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 *
 * This class creates a list of render metric factories that are registered.
 * 
 * 
 * @author Emily Gouge
 * @since 1.2.0
 */
public class RenderMetricsFactoryExtensionPointProcessor  implements ExtensionPointProcessor {
     
    private List<IRenderMetricsFactory> rFactories = new ArrayList<IRenderMetricsFactory>();


    /**
     * Creates an new instance of Processor
     * 
     * @param layer The layer which needs to be rendered
     */
    public RenderMetricsFactoryExtensionPointProcessor() {
    }

    /**
     * @see net.refractions.udig.core.internal.ExtensionPointProcessor#process(org.eclipse.core.runtime.IExtension,
     *      org.eclipse.core.runtime.IConfigurationElement)
     */
    public void process( IExtension extension, IConfigurationElement element ) {

        try {
            IRenderMetricsFactory createExecutableExtension = (IRenderMetricsFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
            
            InternalRenderMetricsFactory metricsFactory = new InternalRenderMetricsFactory(createExecutableExtension, element);             
            rFactories.add(new IdRenderMetricsFactory(metricsFactory, element.getNamespaceIdentifier() + "." + element.getAttribute("id"))); //$NON-NLS-1$ //$NON-NLS-2$);
            
        } catch (CoreException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } 
    }

    public List<IRenderMetricsFactory> getRFactories() {
        return rFactories;
    }

    /**
     * A wrapper class that tracks an id with the render metrics factory
     * extension processor. 
     * 
     * @author Emily Gouge
     * @since 1.2.0
     */
    public static class IdRenderMetricsFactory implements IRenderMetricsFactory{
        private IRenderMetricsFactory renderMetricsFactory = null;
        private String id = null;
        
        public IdRenderMetricsFactory(IRenderMetricsFactory factory , String id){
            this.id = id;
            this.renderMetricsFactory = factory;
        }

        public boolean canRender( IRenderContext context ) throws IOException {
            return this.renderMetricsFactory.canRender(context);
        }

        public AbstractRenderMetrics createMetrics( IRenderContext context ) {
            return this.renderMetricsFactory.createMetrics(context);
        }

        public Class< ? extends IRenderer> getRendererType() {
            return this.renderMetricsFactory.getRendererType();
        }
        
        public String getId(){
            return this.id;
        }
    }
}
