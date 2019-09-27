/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.grid;

import java.awt.Color;
import java.awt.Point;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Draws the grid on the map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GridMapGraphic implements MapGraphic {

    public void draw( MapGraphicContext context ) {
        ViewportGraphics graphics = context.getGraphics();
        GridStyle style = getStyle(context.getLayer());
        
        graphics.setColor(style.getColor());
        graphics.setStroke(style.getLineStyle(), style.getLineWidth());

        switch( getStyle(context.getLayer()).getType() ) {
        case SCREEN:
            screenMapGraphic(context);
            break;
        case WORLD:
            if (style.isCenterGrid()) {
                worldCenteredGridMapGraphic(context, style.isShowLabels());
            }
            else {
                worldMapGraphic(context);
            }
            break;

        default:            
            throw new AssertionError("Should be impossible to reach here: "+getStyle(context.getLayer()).getType()); //$NON-NLS-1$

        }
    }

    /**
     * Draws grid so that x/y rows/columns show up on the screen
     *
     * @param context
     */
    private void screenMapGraphic( MapGraphicContext context ) {
        GridStyle style = getStyle(context.getLayer());
        final double[] gridSize = style.getGridSize();
        final int dx = (int) gridSize[0];
        final int dy = (int) gridSize[1];
        
        int width = context.getMapDisplay().getWidth();
        int height = context.getMapDisplay().getHeight();

        
        ViewportGraphics g = context.getGraphics();
        
        int x=0,y=0;
        
        while( x<width){
            x+=dx;
            g.drawLine(x, 0, x, height);
        }
        
        while ( y<height ){
            y+=dy;
            g.drawLine(0, y, width, y);
        }
        
    }

    /**
     * Draws Grid so that there is a line ever so many degrees.  Number of degrees is declared in the style.
     *
     * @param context
     */
    private void worldMapGraphic( MapGraphicContext context ) {
        GridStyle style = getStyle(context.getLayer());
        double[] gridSize=style.getGridSize();
        try{
            MathTransform mt = CRS.findMathTransform(DefaultGeographicCRS.WGS84, context.getCRS(), true);
        
            if( !mt.isIdentity() ){
                double x=gridSize[0]/2.0;
                double y=gridSize[1]/2.0;
                double[] toTransform=new double[]{-x,-y,x,y};
                double[] dest=new double[4];
                mt.transform(toTransform,0,dest,0,2);
                gridSize=new double[]{Math.abs(dest[2]-dest[0]), Math.abs(dest[3]-dest[1])};
            }
        }catch (Exception e) {
            MapGraphicPlugin.log("",e); //$NON-NLS-1$
        }

        Envelope bounds = context.getViewportModel().getBounds();
        double newx = Math.round(bounds.getMinX()/gridSize[0])*gridSize[0];
        double newy = Math.round(bounds.getMaxY()/gridSize[1])*gridSize[1];
        Coordinate coord=new Coordinate(newx, newy);
        while( context.worldToPixel(coord).x<0 ){
            coord.x+=gridSize[0];
        }
        while( context.worldToPixel(coord).y<0 ){
            coord.y-=gridSize[1];
        }
        ViewportGraphics graphics = context.getGraphics();
        Point pixel = null;
        while(true){
            pixel = context.worldToPixel(coord);            
            coord.x+=gridSize[0];
            coord.y-=gridSize[1];
            Point next=context.worldToPixel(coord);
            if( next.x-pixel.x<2 || next.y-pixel.y<2 ){
                context.getLayer().setStatus(ILayer.WARNING);
                context.getLayer().setStatusMessage(Messages.GridMapGraphic_grids_too_close);
                break;
            }
            if( (pixel.x>=context.getMapDisplay().getWidth() && pixel.y>=context.getMapDisplay().getHeight()) )
                break;
            
            if( pixel.x<context.getMapDisplay().getWidth())
                graphics.drawLine(pixel.x,0,pixel.x,context.getMapDisplay().getHeight());
            if( pixel.y<context.getMapDisplay().getHeight())
                graphics.drawLine(0, pixel.y,context.getMapDisplay().getWidth(),pixel.y);
            pixel=next;
        }
    }

    /**
     * Draws grid similarly to the "world" version, but with some changes:
     *  - Center the grid on the screen
     *  - label the grid lines in CRS units
     *  - outline the grid
     *
     * @param context
     * @param showLabels is true, the bottom and right edges of the map are covered with
     * white strips, and labels for the grid lines are placed in the white strips.
     */
    private void worldCenteredGridMapGraphic( MapGraphicContext context, boolean showLabels ) {
        
        GridStyle style = getStyle(context.getLayer());
        double[] gridSize=style.getGridSize();
     

        try{
            MathTransform mt = CRS.findMathTransform(DefaultGeographicCRS.WGS84, context.getCRS(), true);
        
            if( !mt.isIdentity() ){
                double x=gridSize[0]/2.0;
                double y=gridSize[1]/2.0;
                double[] toTransform=new double[]{-x,-y,x,y};
                double[] dest=new double[4];
                mt.transform(toTransform,0,dest,0,2);
                gridSize=new double[]{Math.abs(dest[2]-dest[0]), Math.abs(dest[3]-dest[1])};
            }
        }catch (Exception e) {
            MapGraphicPlugin.log("",e); //$NON-NLS-1$
        }

        Envelope bounds = context.getViewportModel().getBounds();
        
        Coordinate centerCoord = bounds.centre();
        gridSize = style.getGridSize();
        Coordinate topLeftCenterCoord = new Coordinate(centerCoord.x - gridSize[0]/2, 
                                                       centerCoord.y + gridSize[1]/2);
        Coordinate topLeftMostCoord = new Coordinate(topLeftCenterCoord);
        while (topLeftMostCoord.x - gridSize[0] > bounds.getMinX()) {
            topLeftMostCoord.x -= gridSize[0];
        }
        while (topLeftMostCoord.y + gridSize[1] < bounds.getMaxY()) {
            topLeftMostCoord.y += gridSize[1];
        }
        Coordinate coord = topLeftMostCoord;

        ViewportGraphics graphics = context.getGraphics();
        int mapPixelWidth = context.getMapDisplay().getWidth();
        int mapPixelHeight = context.getMapDisplay().getHeight();
        
        //cover the right side and bottom of map with thin strips
        final int RIGHT_STRIP_WIDTH = (int)(mapPixelWidth * 0.05);
        final int BOTTOM_STRIP_HEIGHT = (int)(mapPixelHeight * 0.03);
        final int GRID_LINE_EXTENSION = (int)(RIGHT_STRIP_WIDTH * 0.1);
        graphics.setColor(Color.white);
        graphics.fillRect(mapPixelWidth - RIGHT_STRIP_WIDTH, 
                          0, 
                          RIGHT_STRIP_WIDTH, 
                          mapPixelHeight); 
        graphics.fillRect(0, 
                          mapPixelHeight - BOTTOM_STRIP_HEIGHT, 
                          mapPixelWidth, 
                          BOTTOM_STRIP_HEIGHT);   
        
        //draw grid lines
        graphics.setColor(style.getColor());
        Point pixel = null;
        while(true){
            pixel = context.worldToPixel(coord);            
            coord.x+=gridSize[0];
            coord.y-=gridSize[1];
            Point next=context.worldToPixel(coord);
            if( next.x-pixel.x<2 || next.y-pixel.y<2 ){
                context.getLayer().setStatus(ILayer.WARNING);
                context.getLayer().setStatusMessage(Messages.GridMapGraphic_grids_too_close);
                break;
            }
            if( (pixel.x>=mapPixelWidth && pixel.y>=mapPixelHeight) )
                break;
            
            //draw vertical lines and labels
            if( pixel.x<mapPixelWidth)
                graphics.drawLine(pixel.x,
                                  0,
                                  pixel.x,
                                  mapPixelHeight - BOTTOM_STRIP_HEIGHT + GRID_LINE_EXTENSION);
                if (showLabels) {
                    graphics.drawString( String.valueOf( (int) coord.y ), 
                                    pixel.x, 
                                    mapPixelHeight - BOTTOM_STRIP_HEIGHT + GRID_LINE_EXTENSION, 
                                    ViewportGraphics.ALIGN_MIDDLE, 
                                    ViewportGraphics.ALIGN_TOP);
                }
            
            //draw horizontal lines and labels
            if( pixel.y<mapPixelHeight)
                graphics.drawLine(0, 
                                  pixel.y,
                                  mapPixelWidth - RIGHT_STRIP_WIDTH + GRID_LINE_EXTENSION,
                                  pixel.y);
                if (showLabels) {
                    graphics.drawString( String.valueOf( (int)coord.x) , 
                                    mapPixelWidth - RIGHT_STRIP_WIDTH + GRID_LINE_EXTENSION, 
                                    pixel.y, 
                                    ViewportGraphics.ALIGN_LEFT, 
                                    ViewportGraphics.ALIGN_MIDDLE);
                }
            pixel=next;
        }
    
        //outline the map        
        graphics.drawRect(0, 
                          0, 
                          mapPixelWidth - RIGHT_STRIP_WIDTH, 
                          mapPixelHeight - BOTTOM_STRIP_HEIGHT);
      
    }        
    
    private GridStyle getStyle( ILayer layer ) {
        GridStyle gridStyle = (GridStyle) layer.getStyleBlackboard().get(GridStyle.ID);
        if( gridStyle==null ){
            return GridStyle.DEFAULT_STYLE;
        }
        return gridStyle;
    }

    /**
     * calculates the closest point to x,y.  
     *
     * @param x x coord in screen coords
     * @param y y coord in screen coords
     * @param layer layer containing this map graphic
     * @return the closest point on the grid  in map coords  
     * @throws FactoryException 
     */
    public double[] closest( int x, int y, ILayer layer ) throws FactoryException {
        switch( getStyle(layer).getType() ) {
        case SCREEN:
            
            return screenClosest(x,y,layer);
        case WORLD:
            return worldClosest(x, y,layer);
        default:
            AssertionError e=new AssertionError("Should be impossible to reach here: "+getStyle(layer).getType()); //$NON-NLS-1$
            MapGraphicPlugin.log(null, e );
            throw e;
        }
    }

    private double[] screenClosest( int x, int y, ILayer layer ) {
        double[] gridSize = getStyle(layer).getGridSize();

        double newx = Math.round(x/gridSize[0])*gridSize[0];
        double newy = Math.round(y/gridSize[1])*gridSize[1];
        
        Coordinate result = layer.getMap().getViewportModel().pixelToWorld((int)newx, (int)newy);
        return new double[]{result.x, result.y};
    }

    private double[] worldClosest( int x1, int y1, ILayer layer ) throws FactoryException {
        Coordinate coord = layer.getMap().getViewportModel().pixelToWorld(x1, y1);
        CoordinateReferenceSystem crs = layer.getMap().getViewportModel().getCRS();
        MathTransform mt = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);

        double[] gridSize = getStyle(layer).getGridSize();
        try{
        
            if( !mt.isIdentity() ){
                double tx=gridSize[0]/2.0;
                double ty=gridSize[1]/2.0;
                double[] toTransform=new double[]{-tx,-ty,tx,ty};
                double[] dest=new double[4];
                mt.transform(toTransform,0,dest,0,2);
                gridSize=new double[]{Math.abs(dest[2]-dest[0]), Math.abs(dest[3]-dest[1])};
            }
        }catch (Exception e) {
            MapGraphicPlugin.log("",e); //$NON-NLS-1$
        }
        double newx = Math.round(coord.x/gridSize[0])*gridSize[0];
        double newy = Math.round(coord.y/gridSize[1])*gridSize[1];
        return new double[]{newx,newy};
    }

}
