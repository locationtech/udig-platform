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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;

import org.geotools.util.Range;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RasterRenderMetrics extends AbstractRenderMetrics {

    public RasterRenderMetrics( IRenderContext context, RasterRenderMetricsFactory factory,
            List<String> styleIds ) {
        super(context, factory, styleIds);
    }

    public Renderer createRenderer() {
        RasterRenderer rasterRenderer = new RasterRenderer();
        rasterRenderer.setContext(context);
        return rasterRenderer;
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    public boolean canStyle( String styleID, Object value ) {
        return false;
    }

    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<Range<Double>>();
    }

}
