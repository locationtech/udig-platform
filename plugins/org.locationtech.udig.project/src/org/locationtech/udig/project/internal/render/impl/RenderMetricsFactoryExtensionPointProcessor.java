/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

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
     * @see org.locationtech.udig.core.internal.ExtensionPointProcessor#process(org.eclipse.core.runtime.IExtension,
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
