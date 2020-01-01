/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.renderer.jgttms;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.catalog.jgrass.core.JGTtmsGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGTtmsProperties;

/**
 * The renderer for JGTtms maps
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGTtmsRenderer extends RendererImpl {

    // private static final String THE_MAP_IS_OUTSIDE_OF_THE_VISIBLE_REGION =
    // "The map is outside of the visible region.";

    //private static GlobalMercator gm = new GlobalMercator();
    private static final String EPSG_MERCATOR = "EPSG:3857";

    /**
     * http://wiki.openstreetmap.org/wiki/FAQ#What_is_the_map_scale_for_a_particular_zoom_level_of_the_map.3F
     */
    public static double[][] scaleList = {//
    // Zoom level,Scale at 72dpi (equator), Meters per pixel (equator),Mpp at 45 degrees (Milano,
    // Lyon, Zagreb) , Mpp at 60 degrees (Stockholm, Oslo)
            {18, 1693, 0.597164, 0.844525, 1.194329}, //
            {17, 3385, 1.194329, 1.689051, 2.388657}, //
            {16, 6771, 2.388657, 3.378103, 4.777314}, //
            {15, 14000, 4.777314, 6.756207, 9.554629}, //
            {14, 27000, 9.554629, 13.512415, 19.109257}, //
            {13, 54000, 19.109257, 27.024829, 38.218514}, //
            {12, 108000, 38.218514, 54.049659, 76.437028}, //
            {11, 217000, 76.437028, 108.099318, 152.874057}, //
            {10, 433000, 152.874057, 216.198638, 305.748113}, //
            {9, 867000, 305.748113, 432.397274, 611.496226}, //
            {8, 2000000, 611.496226, 864.794549, 1222.992453}, //
            {7, 3000000, 1222.992453, 1729.589100, 2445.984905}, //
            {6, 7000000, 2445.984905, 3459.178199, 4891.969810}, //
            {5, 14000000, 4891.969810, 6918.356399, 9783.939621}, //
            {4, 28000000, 9783.939621, 13836.712800, 19567.879241}, //
            {3, 55000000, 19567.879241, 27673.425598, 39135.758482}, //
            {2, 111000000, 39135.758482, 55346.851197, 78271.516964}//
    };

    public void render( Graphics2D g2d, IProgressMonitor monitor ) throws RenderException {
        try {
            final IRenderContext currentContext = getContext();
            currentContext.setStatus(ILayer.WAIT);

            CoordinateReferenceSystem mercatorCrs = CRS.decode(EPSG_MERCATOR);
            CoordinateReferenceSystem latLongCrs = DefaultGeographicCRS.WGS84;

            ReferencedEnvelope renderREnv = getRenderBounds();
            if (renderREnv == null || renderREnv.isNull()) {
                renderREnv = context.getImageBounds();
            }

            ReferencedEnvelope mercatorREnv = renderREnv.transform(mercatorCrs, true);
            ReferencedEnvelope latlongREnv = renderREnv.transform(latLongCrs, true);

            Point upperLeft = currentContext.worldToPixel(new Coordinate(renderREnv.getMinX(), renderREnv.getMinY()));
            Point bottomRight = currentContext.worldToPixel(new Coordinate(renderREnv.getMaxX(), renderREnv.getMaxY()));
            Rectangle screenSize = new Rectangle(upperLeft);
            screenSize.add(bottomRight);

            final IGeoResource resource = getContext().getGeoResource();
            if (resource == null || !resource.canResolve(JGTtmsGeoResource.class)) {
                getContext().setStatus(ILayer.DONE);
                return;
            }
            JGTtmsGeoResource jgtTmsGeoResource = resource.resolve(JGTtmsGeoResource.class, monitor);
            JGTtmsProperties tmsProperties = jgtTmsGeoResource.getTmsProperties();
            RasterSymbolizer rasterSymbolizer = CommonFactoryFinder.getStyleFactory(null).createRasterSymbolizer();

            // double scale = getContext().getViewportModel().getScaleDenominator();

            double widthMeters = mercatorREnv.getWidth();
            int widthPixels = screenSize.width;

            double metersXPixel = widthMeters / widthPixels;
            int nearestZoomLevel = getZoomLevelMetersXPixel(metersXPixel, latlongREnv.centre().y);

            // get tiles range
            double w = latlongREnv.getMinX();
            double s = latlongREnv.getMinY();
            double e = latlongREnv.getMaxX();
            double n = latlongREnv.getMaxY();
            int z = nearestZoomLevel;

            // get ul and lr tile number in GOOGLE tiles
            // TODO: Replace with logic from OSMTile or other
            int[] llTileXY = new int[]{0,1}; // TODO: rplce GlobalMercator.tile(s, w, z,256);
            int[] urTileXY = new int[]{0,2}; // TODO: replace GlobalMercator.tile(n, e, z,256);

            int startXTile = Math.min(llTileXY[0], urTileXY[0]);
            int endXTile = Math.max(llTileXY[0], urTileXY[0]);
            int startYTile = Math.min(llTileXY[1], urTileXY[1]);
            int endYTile = Math.max(llTileXY[1], urTileXY[1]);

            int tileNum = 0;
            for( int i = startXTile; i <= endXTile; i++ ) {
                for( int j = startYTile; j <= endYTile; j++ ) {
                    tileNum++;
                }
            }
            if (tileNum > 30) {
                getContext().setStatus(ILayer.DONE);
                getContext().setStatusMessage("Too many tiles needed for this zoomlevel");
                return;
            }

            for( int i = startXTile; i <= endXTile; i++ ) {
                for( int j = startYTile; j <= endYTile; j++ ) {

                    double west = i / Math.pow(2.0, z) * 360.0 - 180;
                    double nn = Math.PI - (2.0 * Math.PI * j) / Math.pow(2.0, z);
                    double north = Math.toDegrees(Math.atan(Math.sinh(nn)));
                    double east = (i + 1) / Math.pow(2.0, z) * 360.0 - 180;
                    nn = Math.PI - (2.0 * Math.PI * (j + 1)) / Math.pow(2.0, z);
                    double south = Math.toDegrees(Math.atan(Math.sinh(nn)));

                    ReferencedEnvelope tileBounds = new ReferencedEnvelope(west, east, south, north, latLongCrs);

                    if (!latlongREnv.intersects((BoundingBox) tileBounds)) {
                        continue;
                    }

                    int[] fileNameTileNumbers = {i, j};
                    if (tmsProperties.type == JGTtmsProperties.TILESCHEMA.tms) {
                        fileNameTileNumbers = new int[]{i,j};// TODO replace GlobalMercator.swap( i,j,z);
                    } else if (tmsProperties.type == JGTtmsProperties.TILESCHEMA.google) {
                        // is already
                    }

                    String tilePart = tmsProperties.tilePart.replaceFirst("ZZZ", String.valueOf(z));
                    tilePart = tilePart.replaceFirst("XXX", String.valueOf(fileNameTileNumbers[0]));
                    tilePart = tilePart.replaceFirst("YYY", String.valueOf(fileNameTileNumbers[1]));

                    File imageFile = new File(tmsProperties.HOST_NAME, tilePart);
                    if (!imageFile.exists()) {
                        continue;
                    }

                    String imgPath = imageFile.getAbsolutePath();
                    BufferedImage image = ImageCache.getInstance().getImage(imgPath);
                    if (image == null) {
                        image = ImageIO.read(imageFile);
                        ImageCache.getInstance().addImage(imgPath, image);
                    }

                    ReferencedEnvelope mercatorTileBounds = tileBounds.transform(mercatorCrs, true);
                    renderTile(g2d, image, mercatorTileBounds, rasterSymbolizer);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getContext().setStatus(ILayer.DONE);
            getContext().setStatusMessage(null);
        }

    }

    private void renderTile( Graphics2D graphics, BufferedImage image, ReferencedEnvelope tileEnvelope, RasterSymbolizer style )
            throws Exception {

        if (image == null) {
            return;
        }

        GridCoverageFactory factory = new GridCoverageFactory();
        GridCoverage2D coverage = (GridCoverage2D) factory.create("GridCoverage", image, tileEnvelope); //$NON-NLS-1$        

        CoordinateReferenceSystem mapCrs = getContext().getCRS();
        ReferencedEnvelope tileMapCrsEnvelope = tileEnvelope.transform(mapCrs, true);

        // determine screen coordinates of tiles
        Point upperLeft = getContext().worldToPixel(new Coordinate(tileMapCrsEnvelope.getMinX(), tileMapCrsEnvelope.getMinY()));
        Point bottomRight = getContext().worldToPixel(new Coordinate(tileMapCrsEnvelope.getMaxX(), tileMapCrsEnvelope.getMaxY()));
        Rectangle tileSize = new Rectangle(upperLeft);
        tileSize.add(bottomRight);

        // render
        try {
            AffineTransform worldToScreen = RendererUtilities.worldToScreenTransform(tileMapCrsEnvelope, tileSize);
            GridCoverageRenderer paint = new GridCoverageRenderer(mapCrs, tileMapCrsEnvelope, tileSize, worldToScreen);
            paint.paint(graphics, coverage, style);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Finds out the best fitting zoom-level for a given map-scale.
     *
     * @param wmtSource
     * @param tempScaleList
     * @return
     */
    public int getZoomLevelMetersXPixel( double metersXPixel, double lat ) {
        int column = 3;
        if (lat > -30 && lat < 30) {
            column = 2;
        }
        if (lat < -75 || lat > 75) {
            column = 4;
        }

        if (metersXPixel <= scaleList[0][column]) {
            return (int) scaleList[0][0];
        } else if (metersXPixel >= scaleList[scaleList.length - 1][column]) {
            return (int) scaleList[scaleList.length - 1][0];
        } else {
            for( int i = 0; i < scaleList.length - 1; i++ ) {
                double s1 = scaleList[i][column];
                double s2 = scaleList[i + 1][column];
                if (metersXPixel >= s1 && metersXPixel < s2) {
                    return (int) scaleList[i + 1][0];
                }
            }
        }
        throw new RuntimeException();
    }

    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g2d = getContext().getImage().createGraphics();
        render(g2d, monitor);
    }

    public void dispose() {
        ImageCache.getInstance().dispose();
    }

}
