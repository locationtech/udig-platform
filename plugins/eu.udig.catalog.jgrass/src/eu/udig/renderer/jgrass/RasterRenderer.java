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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Collections;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.TileCache;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.ViewType;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * The renderer for GRASS type rasters, as wrapped by the JGrassMapGeoResource
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RasterRenderer extends RendererImpl {

    private static final String THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION = "The map is outside of the visible region.";

    public void render( Graphics2D g2d, IProgressMonitor monitor ) throws RenderException {
        try {
            final IRenderContext currentContext = getContext();
            currentContext.setStatus(ILayer.WAIT);
            CoordinateReferenceSystem destinationCRS = currentContext.getCRS();

            // the bounds of the visible area in world coordinates
            // get the envelope and the screen extent
            Envelope envelope = getRenderBounds();
            if (envelope == null || envelope.isNull()) {
                envelope = context.getImageBounds();
            }

            Point upperLeft = currentContext.worldToPixel(new Coordinate(envelope.getMinX(), envelope.getMinY()));
            Point bottomRight = currentContext.worldToPixel(new Coordinate(envelope.getMaxX(), envelope.getMaxY()));
            Rectangle screenSize = new Rectangle(upperLeft);
            screenSize.add(bottomRight);

            final IGeoResource resource = getContext().getGeoResource();
            if (resource == null || !resource.canResolve(JGrassMapGeoResource.class)) {
                return;
            }
            JGrassMapGeoResource grassMapGeoResource = resource.resolve(JGrassMapGeoResource.class, monitor);

            JGrassRegion fileWindow = new JGrassRegion(grassMapGeoResource.getFileWindow());
            JGrassMapsetGeoResource parent = (JGrassMapsetGeoResource) grassMapGeoResource.parent(new NullProgressMonitor());
            CoordinateReferenceSystem grassCrs = parent.getLocationCrs();
            JGrassRegion screenDrawWindow = new JGrassRegion(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(),
                    envelope.getMaxY(), fileWindow.getRows(), fileWindow.getCols());

            // to intersect with the data window, we transform the screen window
            JGrassRegion reprojectedScreenDrawWindow = screenDrawWindow;
            if (!CRS.equalsIgnoreMetadata(destinationCRS, grassCrs)) {
                reprojectedScreenDrawWindow = screenDrawWindow.reproject(destinationCRS, grassCrs, true);
            }

            /*
             * if the map is not visible, do not render it
             */
            // JGrassRegion fileWindow = grassMapGeoResource.getFileWindow();
            Rectangle2D.Double fileRectDouble = fileWindow.getRectangle();
            Double reprojScreenRectangle = reprojectedScreenDrawWindow.getRectangle();
            if (!reprojScreenRectangle.intersects(fileRectDouble)) {
                getContext().setStatus(ILayer.DONE);
                getContext().setStatusMessage(THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION);
                System.out.println(THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION);
                return;
            }
            /*
             * we will draw only the intersection of the map in the display system = part of visible map
             */
            Rectangle2D drawMapRectangle = reprojectedScreenDrawWindow.getRectangle().createIntersection(fileRectDouble);
            // Rectangle2D drawMapRectangle = fileRectDouble.getBounds2D();
            // resolution is that of the file window
            double ewRes = fileWindow.getWEResolution();
            double nsRes = fileWindow.getNSResolution();
            if (fileRectDouble.getWidth() < ewRes || fileRectDouble.getHeight() < nsRes) {
                getContext().setStatus(ILayer.DONE);
                getContext().setStatusMessage(THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION);
                System.out.println(THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION);
                return;
            }
            MathTransform transform = CRS.findMathTransform(destinationCRS, grassCrs, true);
            Coordinate pixelSize = getContext().getPixelSize();

            Coordinate c1 = new Coordinate(envelope.getMinX(), envelope.getMinY());
            Coordinate c2 = new Coordinate(envelope.getMinX() + pixelSize.x, envelope.getMinY() + pixelSize.y);
            Envelope envy = new Envelope(c1, c2);
            Envelope envyTrans = JTS.transform(envy, transform);

            pixelSize = new Coordinate(envyTrans.getWidth(), envyTrans.getHeight());
            /*
             * if the resolution is higher of that of the screen, it doesn't make much sense to draw it
             * all. So for visualization we just use the screen resolution to do things faster.
             */
            if (ewRes < pixelSize.x) {
                ewRes = pixelSize.x;
            }
            if (nsRes < pixelSize.y) {
                nsRes = pixelSize.y;
            }
            fileWindow.setNSResolution(nsRes);
            fileWindow.setWEResolution(ewRes);
            nsRes = fileWindow.getNSResolution();
            ewRes = fileWindow.getWEResolution();
            /*
             * redefine the region of the map to be drawn
             */
            /*
             * snap the screen to fit into the active region grid. This is mandatory for the exactness
             * of the query of the pixels (ex. d.what.rast).
             */
            JGrassRegion activeWindow = grassMapGeoResource.getActiveWindow();
            Coordinate minXY = JGrassRegion.snapToNextHigherInRegionResolution(drawMapRectangle.getMinX(),
                    drawMapRectangle.getMinY(), activeWindow);
            Coordinate maxXY = JGrassRegion.snapToNextHigherInRegionResolution(drawMapRectangle.getMaxX(),
                    drawMapRectangle.getMaxY(), activeWindow);

            JGrassRegion drawMapRegion = new JGrassRegion(minXY.x, maxXY.x, minXY.y, maxXY.y, ewRes, nsRes);
            // JGrassRegion drawMapRegion = new JGrassRegion(drawMapRectangle.getMinX(),
            // drawMapRectangle.getMaxX(), drawMapRectangle.getMinY(), drawMapRectangle
            // .getMaxY(), ewRes, nsRes);
            JGrassMapEnvironment grassMapEnvironment = grassMapGeoResource.getjGrassMapEnvironment();
            GridCoverage2D coverage = JGrassCatalogUtilities.getGridcoverageFromGrassraster(grassMapEnvironment, drawMapRegion);
            coverage = coverage.view(ViewType.RENDERED);
            if (coverage != null) {

                // setting rendering hints
                RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
                hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED));
                hints.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
                hints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED));
                hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED));
                hints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
                hints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
                hints.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF));
                hints.add(new RenderingHints(JAI.KEY_INTERPOLATION, new InterpolationNearest()));
                g2d.addRenderingHints(hints);
                final TileCache tempCache = JAI.createTileCache();
                tempCache.setMemoryCapacity(16 * 1024 * 1024);
                tempCache.setMemoryThreshold(1.0f);
                hints.add(new RenderingHints(JAI.KEY_TILE_CACHE, tempCache));

                // draw

                AffineTransform worldToScreen = RendererUtilities.worldToScreenTransform(envelope, screenSize, destinationCRS);
                final GridCoverageRenderer paint = new GridCoverageRenderer(destinationCRS, envelope, screenSize, worldToScreen,
                        hints);
                RasterSymbolizer rasterSymbolizer = CommonFactoryFinder.getStyleFactory(null).createRasterSymbolizer();

                paint.paint(g2d, coverage, rasterSymbolizer);

                tempCache.flush();

                // IBlackboard blackboard = context.getMap().getBlackboard();
                // String legendString = coverageReader.getLegendString();
                // String name = grassMapGeoResource.getTitle();
                // blackboard.putString(JGrassMapGeoResource.READERID + "#" + name, legendString);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getContext().setStatus(ILayer.DONE);
            getContext().setStatusMessage(null);
        }

    }

    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g2d = getContext().getImage().createGraphics();
        render(g2d, monitor);
    }

    public void dispose() {
        // do nothing
    }

}
