/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Collections;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 * <p>
 * Class representing the rendered AOI (Area of Interest)
 * </p>
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class AOILayerDecorator implements MapGraphic {

    public static final String ID = "org.locationtech.udig.omsbox.processingregion.aoiLayerDecorator";

    public AOILayerDecorator() {
    }

    /*
     * Listens to the global IAOIService and updates the decorator if anything changes!
     */
    private AOIListener serviceWatcher = new AOIListener(){
        
        public void handleEvent( AOIListener.Event event ) {
            ReferencedEnvelope view = ApplicationGIS.getActiveMap().getViewportModel().getBounds();
            
            for (ILayer layer: ApplicationGIS.getActiveMap().getMapLayers()) {
                if (layer.findGeoResource(AOILayerDecorator.class) != null) {
                    ApplicationGIS.getActiveMap().getRenderManager().refresh(layer, view);
                }
            }
         }
    };

    protected void listenService( boolean listen ) {
        IAOIService aOIService = PlatformGIS.getAOIService();
        if (listen) {
            aOIService.addListener(serviceWatcher);
        } else {
            aOIService.removeListener(serviceWatcher);
        }
    }

    public void draw( MapGraphicContext context ) {
        context.getLayer().setStatus(ILayer.WORKING);

        listenService(true);
        
        // initialise the graphics handle
        ViewportGraphics graphic = context.getGraphics();
        if (graphic instanceof AWTGraphics) {
            AWTGraphics awtG = (AWTGraphics) graphic;
            Graphics2D g2D = awtG.g;
            // setting rendering hints
            @SuppressWarnings("unchecked")
            RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
            hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g2D.addRenderingHints(hints);
        }

        Dimension screen = context.getMapDisplay().getDisplaySize();

        // get the AOI
        IAOIService aOIService = PlatformGIS.getAOIService();
        Geometry multiGeometry = aOIService.getGeometry();
        if (multiGeometry != null) {
        
            Geometry innerPolygons = null; 
            Polygon polygon = null;
            // draw the rectangle around the active region green:143
            //float[] rgba = style.backgroundColor.getColorComponents(null);
            //g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.bAlpha));
            graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.5));
    
            int screenWidth = screen.width;
            int screenHeight = screen.height;
    
            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(0, 0);
            path.lineTo(screenWidth, 0);
            path.lineTo(screenWidth, screenHeight);
            path.lineTo(0, screenHeight);
            path.closePath();
            
            // if multi geometry - loop through each geometry
            for (int g=0; g<multiGeometry.getNumGeometries(); g++) {
                Geometry geometry = multiGeometry.getGeometryN(g);
                if (geometry.getGeometryType().equals("Polygon")){
                    polygon = (Polygon) geometry;
                    
                    // get the exterior rings
                    LineString exterior = polygon.getExteriorRing();
                    
                    // collects the internal holes if there are any
                    for (int i=0; i<polygon.getNumInteriorRing(); i++) {
                        if (innerPolygons == null) {
                            innerPolygons = polygon.getInteriorRingN(i);
                        }
                        else {
                            innerPolygons = innerPolygons.union(polygon.getInteriorRingN(i));
                        }
                        
                    }
                    
                    Coordinate[] coordinates = exterior.getCoordinates();
                    
                    // move to the first point
                    Point point = null;
                    if (coordinates.length > 0) {
                        point = context.worldToPixel(coordinates[0]);
                        path.moveTo(point.x, point.y);
                    }
                    // draw all points
                    for (int c=0; c<coordinates.length; c++) {
                        point = context.worldToPixel(coordinates[c]);
                        path.lineTo(point.x, point.y);
                    }
                    path.closePath();
                }
            }
            
            graphic.fill(path);
    
            //rgba = style.foregroundColor.getColorComponents(null);
            //g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.fAlpha));
            graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.75));
            graphic.setStroke(ViewportGraphics.LINE_SOLID, 1);
            graphic.draw(path);
            
            
            
            // draw inner rings / holes
            if (innerPolygons != null) {
                for (int g=0; g<innerPolygons.getNumGeometries(); g++) {
                    path.reset();
                    Geometry geometry = innerPolygons.getGeometryN(g);
                    
                    Coordinate[] coordinates = geometry.getCoordinates();
                    
                    // move to the first point
                    Point point = null;
                    if (coordinates.length > 0) {
                        point = context.worldToPixel(coordinates[0]);
                        path.moveTo(point.x, point.y);
                    }
                    // draw all points
                    for (int c=0; c<coordinates.length; c++) {
                        point = context.worldToPixel(coordinates[c]);
                        path.lineTo(point.x, point.y);
                    }
                    path.closePath();
                    graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.5));
                    graphic.fill(path);
                    graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.75));
                    graphic.draw(path);
                }
            }
            
        }
        context.getLayer().setStatus(ILayer.DONE);
        context.getLayer().setStatusMessage("Layer rendered");

    }
    
    public void dispose() {
        listenService(false);
    }

}
