/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.renderer.jgttms;

import java.io.IOException;
import java.util.ArrayList;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import eu.udig.catalog.jgrass.core.JGTtmsGeoResource;

/**
 * This renderer can render JGTtms maps
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGTtmsRenderMetricsFactory implements IRenderMetricsFactory {

    public JGTtmsRenderMetricsFactory() {
    }

    public boolean canRender( IRenderContext context ) throws IOException {
        // check if it is a Property resource
        IGeoResource resource = context.getGeoResource();
        boolean isRightResource = resource.canResolve(JGTtmsGeoResource.class);
        //return isRightResource;
        
        return false; // TODO: Fix implementation of JGTmsRenderer calaculation of tile bounds
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        ArrayList<String> styleIds = new ArrayList<String>();
        return new JGTtmsRenderMetrics(context, this, styleIds);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return JGTtmsRenderer.class;
    }

}
