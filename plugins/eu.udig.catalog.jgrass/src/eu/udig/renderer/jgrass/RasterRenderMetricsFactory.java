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
 package eu.udig.renderer.jgrass;

import java.io.IOException;
import java.util.ArrayList;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.geotools.gce.grassraster.JGrassConstants;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;

/**
 * This renderer can render all the JGrass supported raster maps.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RasterRenderMetricsFactory implements IRenderMetricsFactory {

    public RasterRenderMetricsFactory() {
    }

    public boolean canRender( IRenderContext context ) throws IOException {
        // check if it is a Property resource
        IGeoResource resource = context.getGeoResource();
        boolean isRightResource = resource.canResolve(JGrassMapGeoResource.class);
        boolean isRightType = false;

        if (resource.getInfo(null).getDescription() == null) {
            isRightType = false;
        } else {

            String descString = resource.getInfo(null).getDescription();

            if (descString.equals(JGrassConstants.GRASSBINARYRASTERMAP)
                    || descString.equals(JGrassConstants.GRASSASCIIRASTERMAP)
                    || descString.equals(JGrassConstants.ESRIRASTERMAP)
                    || descString.equals(JGrassConstants.FTRASTERMAP)) {
                isRightType = true;
            } else {
                isRightType = false;
            }
        }
        return isRightResource && isRightType;
    }

    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        ArrayList<String> styleIds = new ArrayList<String>();
        return new RasterRenderMetrics(context, this, styleIds);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return RasterRenderer.class;
    }

}
