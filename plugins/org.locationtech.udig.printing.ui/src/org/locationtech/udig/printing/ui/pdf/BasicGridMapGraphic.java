/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.pdf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.mapgraphic.grid.GridStyle;
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
 * 
 * A grid that labels grid lines based on the current CRS 
 * <p>
 *
 * </p>
 * @author brocka
 * @since 1.1.0
 */
public class BasicGridMapGraphic implements MapGraphic {

    public void draw( MapGraphicContext context ) {

        boolean showLabels = true;
        Color gridColor = Color.black;
        Font font = new Font("Times", Font.PLAIN, 8);
        
        Envelope bounds = context.getViewportModel().getBounds();
        List<Double>[] gridLines = chooseGridLines(bounds);
       
        ViewportGraphics graphics = context.getGraphics();
        int mapPixelWidth = context.getMapDisplay().getWidth();
        int mapPixelHeight = context.getMapDisplay().getHeight();
        graphics.setColor(gridColor);
        
        List<Double> xCoords = gridLines[0];
        List<Double> yCoords = gridLines[1];
        
        
        
        //draw vert lines
        for (int i = 0; i < xCoords.size(); i++) {
            double x = xCoords.get(i).doubleValue();

            for (int j = 0; j < yCoords.size() - 1; j++) {
                
                double y1 = yCoords.get(j).doubleValue();
                Coordinate lastCoord = new Coordinate(x, y1);
                double y2 = yCoords.get(j+1).doubleValue();
                Coordinate thisCoord = new Coordinate(x, y2);
                
             
                Point lastPixel = context.worldToPixel(lastCoord);
                Point thisPixel = context.worldToPixel(thisCoord);
            
                graphics.drawLine(lastPixel.x, 
                        lastPixel.y,
                        thisPixel.x,
                        thisPixel.y);
            }
        }

        //draw horiz lines
        
        for (int i = 0; i < yCoords.size(); i++) {
            double y = yCoords.get(i).doubleValue();
            for (int j = 0; j < xCoords.size() - 1; j++) {
                
                double x1 = xCoords.get(j).doubleValue();
                Coordinate lastCoord = new Coordinate(x1, y);
                double x2 = xCoords.get(j+1).doubleValue();
                Coordinate thisCoord = new Coordinate(x2, y);
                
 
                Point lastPixel = context.worldToPixel(lastCoord);
                Point thisPixel = context.worldToPixel(thisCoord);
                
                graphics.drawLine(lastPixel.x, 
                        lastPixel.y,
                        thisPixel.x,
                        thisPixel.y);
    
            }
        }
     
        //cover the right side and bottom of map with thin strips
        final int RIGHT_STRIP_WIDTH = (int)(mapPixelWidth * 0.05);
        final int BOTTOM_STRIP_HEIGHT = (int)(mapPixelHeight * 0.05);
        final int GRID_LINE_EXTENSION = (int)(RIGHT_STRIP_WIDTH * 0.1);
        graphics.setColor(Color.white);
        graphics.setBackground(Color.white);
        
        graphics.clearRect(mapPixelWidth - RIGHT_STRIP_WIDTH, 
                          0, 
                          RIGHT_STRIP_WIDTH, 
                          mapPixelHeight); 
        graphics.clearRect(0, 
                          mapPixelHeight - BOTTOM_STRIP_HEIGHT, 
                          mapPixelWidth, 
                          BOTTOM_STRIP_HEIGHT);   
       
        
        //outline the map        
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 
                          0, 
                          mapPixelWidth - RIGHT_STRIP_WIDTH - 1, 
                          mapPixelHeight - BOTTOM_STRIP_HEIGHT - 1);
        
        //draw labels
        graphics.setFont(font);
        if (showLabels) {
            graphics.setColor(gridColor);
            for (int i = 1; i < yCoords.size() - 1; i++) {
                double y = yCoords.get(i).doubleValue();
                double x = xCoords.get(xCoords.size() - 1).doubleValue();
                Point pixel = context.worldToPixel(new Coordinate(x,y));
                //line is covered by white overlay
                if (pixel.y > mapPixelHeight - BOTTOM_STRIP_HEIGHT)
                    break;
                 graphics.drawString((int)(y)+"", 
                                mapPixelWidth - RIGHT_STRIP_WIDTH + GRID_LINE_EXTENSION, 
                                pixel.y, 
                                ViewportGraphics.ALIGN_LEFT, 
                                ViewportGraphics.ALIGN_MIDDLE);
            }
            for (int i = 1; i < xCoords.size() - 1; i++) {
                double x = xCoords.get(i).doubleValue();
                double y = yCoords.get(yCoords.size() - 1).doubleValue();
                Point pixel = context.worldToPixel(new Coordinate(x,y));
                //line is covered by white overlay
                if (pixel.x > mapPixelWidth - RIGHT_STRIP_WIDTH)
                    break;
                graphics.drawString((int)(x)+"", 
                                pixel.x, 
                                mapPixelHeight - BOTTOM_STRIP_HEIGHT + GRID_LINE_EXTENSION, 
                                ViewportGraphics.ALIGN_MIDDLE, 
                                ViewportGraphics.ALIGN_TOP);
            }
        } //showlabels

    }

    /*
     * returns a two element array, where each element is list.
     * the first list represents x-coordinates of vertical grid lines, and
     * the second list represents y coordinates of horizintal grid lines. 
     */
    private List<Double>[] chooseGridLines(Envelope bounds) {
        double[] gridSizeChoices = { 1, 2, 5, 10, 25, 50, 100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000 };
        int preferredNumberOfGridLines = 2; //approximately, but +/- a couple is okay
        
        double xSizeGuess = (bounds.getMaxX() - bounds.getMinX()) / preferredNumberOfGridLines;
        double xSize = findClosest(gridSizeChoices, xSizeGuess);
        
        double ySizeGuess = (bounds.getMaxY() - bounds.getMinY()) / preferredNumberOfGridLines;
        double ySize = findClosest(gridSizeChoices, ySizeGuess);
        
        double max = Math.max(xSize, ySize);
        xSize = max;
        ySize = max;
        
        // make list of all the x-coordinates (vertical grid lines)
        double firstX = Math.round(bounds.getMinX() / xSize) * xSize;
        if (firstX > bounds.getMinX()){
            firstX -= xSize;
        }
        List xList = new ArrayList();
        double xCoord = firstX;
        while (xCoord < bounds.getMaxX() + xSize) {
            xList.add(xCoord);
            xCoord += xSize;
        }
        
        // make list of all the y-coordinates (hortizontal grid lines)
        double firstY = Math.round(bounds.getMaxY() / ySize) * ySize;
        if (firstY < bounds.getMaxY()){
            firstY += ySize;
        }
        List yList = new ArrayList();
        double yCoord = firstY;
        while (yCoord > bounds.getMinY() - ySize) {
            yList.add(yCoord);
            yCoord -= ySize;
        }
        
        List[] result = {xList, yList};
        return result;
        
    }
    
    private double findClosest(double[] choices, double val) {
        if (val < choices[0])
            return choices[0];
        for (int i = 0; i < choices.length -1; i++) {
            if (val > choices[i] && val < choices[i+1])
                return choices[i];
        }
        return choices[choices.length - 1];
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
    private void drawGrid( MapGraphicContext context, boolean showLabels ) {
        
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
                    graphics.drawString((int)(coord.y)+"", 
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
                    graphics.drawString((int)(coord.x)+"", 
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
            AssertionError e=new AssertionError("Should be impossible to reach here: "+getStyle(layer).getType());
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
