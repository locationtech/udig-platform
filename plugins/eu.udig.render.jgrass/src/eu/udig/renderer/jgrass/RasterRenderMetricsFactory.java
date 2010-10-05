/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
