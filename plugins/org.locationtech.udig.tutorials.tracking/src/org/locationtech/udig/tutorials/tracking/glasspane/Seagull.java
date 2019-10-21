/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tutorials.tracking.glasspane;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.locationtech.udig.project.ui.render.glass.GlassPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Simple class to represent your domain object. In this case we are representing
 * seagulls.  Seagulls have a name, location, and crs.
 * 
 * @author Emily Gouge
 * @since 1.2.0
 */
public class Seagull {

    //attributes associated with seagulls
    private String name;
    private CoordinateReferenceSystem crs;
    private Coordinate location;
    private int color;
    
    //for moving seagull
    private Random generator = new Random();
    
    //the flock this seagull belongs to
    private Collection<Seagull> flock = null;
    private double resolution = 0;
    
    private SeagullLocationListener moveListener = null;
    /**
     * Creates a new seagull with a given name and id
     * 
     * @param name
     * @param id
     */
    public Seagull(String name){
        this.name = name;
        this.color = SWT.COLOR_DARK_BLUE;
    }
    
    /**
     * Sets the color to draw the seagull
     *
     * @param color  the swt color of the seagull (ex. SWT.COLOR_DARK_BLUE)
     * @return
     */
    public void setColor(int color){
        this.color = color;
    }
    
    /**
     * Sets the location listener associated with the seagull.  This code will
     * be called when the seagull moves.
     *
     * @param listener
     */
    public void setLocationListener(SeagullLocationListener listener){
        this.moveListener = listener;
    }
    
    /**
     * Creates a new seagull with a given name and id
     * 
     * @param name
     * @param id
     */
    public Seagull(String name, Collection<Seagull> flock){
        this.name = name;
        this.flock = flock;
    }
    
    /**
     * Sets the location of the seagull.  Next time the seagull
     * is draw it will be drawn at this location
     *
     * @param c
     * @param crs
     */
    public void setPosition(Coordinate c, CoordinateReferenceSystem crs){
        this.location = c;
        this.crs = crs;
    }
    
    /**
     * Draws a seagull onto the given graphics.  A seagull is represented
     * by a red circle with the seagull name.  
     * 
     * <p>
     * If no location has been set the seagull is not drawn.
     * </p>
     *
     * @param graphics
     * @param gp
     */
    private boolean up = (Math.random() < 0.5);
    public void drawSeagull(GC graphics, GlassPane gp){
        if (location == null) {
            return;
        }
        
        // initialize the graphics handle
        graphics.setForeground(Display.getCurrent().getSystemColor(this.color));
        graphics.setBackground(Display.getCurrent().getSystemColor(this.color));
        
        // figure out our CRS
        CoordinateReferenceSystem ourCRS = crs;
        if (ourCRS == null) {
            ourCRS = gp.getSite().getCRS();
        }

        resolution = gp.getSite().getViewportModel().getWidth() / gp.getSite().getMapDisplay().getWidth();
        
        // figure out how to map our coordinate to the world
        CoordinateReferenceSystem worldCRS =gp.getSite().getMap().getViewportModel().getCRS();
        MathTransform dataToWorld;
        try {
            dataToWorld = CRS.findMathTransform(ourCRS, worldCRS, false);
        } catch (FactoryException e1) {
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }

        Coordinate worldLocation = new Coordinate();
           
        Coordinate dataLocation = location;
        try {
            JTS.transform(dataLocation, worldLocation, dataToWorld);
        } catch (TransformException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }                
              
        java.awt.Point p = gp.getSite().worldToPixel(worldLocation);
//        graphics.fillOval(p.x, p.y, 8, 8);
        if (up){
            graphics.drawRectangle(p.x, p.y, 1, 1);
            graphics.drawRectangle(p.x+1, p.y-1, 2, 1);
            graphics.drawRectangle(p.x+6, p.y-1, 2, 1);
            graphics.drawRectangle(p.x+8, p.y, 1, 1);
            graphics.drawRectangle(p.x+4, p.y+1, 1, 1);
         
        }else{
            graphics.drawRectangle(p.x, p.y, 1, 1);
            graphics.drawRectangle(p.x+1, p.y, 2, 1);
            graphics.drawRectangle(p.x+6, p.y, 2, 1);
            graphics.drawRectangle(p.x+8, p.y, 1, 1);
            graphics.drawRectangle(p.x+4, p.y+1, 1, 1);
        }
        up = !up;
//        graphics.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
//        graphics.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//        graphics.drawString(name, p.x + 8, p.y+8);
        
    }
    
    public void moveSeagull( Coordinate new_loc ) {
        this.location = new_loc;

        if (moveListener != null) {
            moveListener.gullMoved(this.location);
        }
    }
    
    /**
     * Moves the seagull to some random new point inside the bounding box.
     *
     * @param bounds
     */
    public void moveSeagull( Envelope bounds ) {
        
        
        if (moveListener != null){
            moveListener.gullMoved(this.location);
        }
        
        double x = location.x;
        double offset = 0.008;
        if (generator.nextDouble() < 0.5 ){
            x = x - bounds.getWidth() * offset;
        }else{
            x = x + bounds.getWidth() * offset;
        }
        double y = location.y;
        if (generator.nextDouble() < 0.5){
            y = y - bounds.getHeight() * offset;
        }else{
            y = y + bounds.getHeight() * offset;
        }
//        double x = generator.nextDouble() * 2 - 1; // num between -1 and 1
//        double y = generator.nextDouble() * 2 - 1;
        
//        Coordinate new_loc = new Coordinate(location.x + x,location.y + y);
        Coordinate new_loc = new Coordinate(x,y);
        if (bounds.contains(new_loc)){
            this.location = new_loc;
        }else{
            //center on screen
            this.location = new Coordinate(bounds.getWidth()/2 + bounds.getMinX(), bounds.getHeight()/2 + bounds.getMinY());
        }

    }
    
    /*
     * The following code allows the seaguls to move toward
     * a target point.  The seagull moves toward the point
     * at a given rate until the seagull reaches the point
     * 
     */
    private Timer moveTimer = null;     //timer for continuously moving seagull     
    private Coordinate target = null;   //target coordinate
    private boolean isCancelled = true; //if we have reached the target
    
    /*
     * sets up an event that slowly moves the cursor closer to the target point
     */
    /**
     * Sets the target coordinate to move the seagull to.
     * <p>
     * Well also start a timer and start moving the seagull towards this point
     * until the seagull has reached the point at wich time the timer stops.
     * </p>
     * 
     * @param c
     * 
     */
    public void setTargetCoordinate( Coordinate c ) {
        this.target = c;
        if (moveTimer == null) {
            moveTimer = new Timer();
        }
        if (isCancelled) {
            int movedelay = 100;
            
            final Collection<Seagull> sflock = this.flock;
            final Seagull current = this;
            final double closedistance = resolution * 10;
            
            moveTimer.scheduleAtFixedRate(new TimerTask(){

                @Override
                public void run() {
                    Coordinate next = new Coordinate(location);
                    
                    // move 5% closer to the point 
                    double distance = target.x - location.x;
                    next.x = location.x + distance * 0.05;
                    
                    distance = target.y - location.y;
                    next.y = location.y + distance * 0.05;
                    
                    //see if the next x is 'too close' to one of the other seagulls
                    //in the flock
                    boolean move = true;
                    if (sflock != null) {

                        for( Iterator iterator = sflock.iterator(); iterator.hasNext(); ) {
                            Seagull seagull = (Seagull) iterator.next();
                            if (seagull != current) {
                                // if i am too close then set move to false
                                if (seagull.location.distance(next) < closedistance) {
                                    move = false;
                                }
                            }
                        }
                    }

                    if (move) {
                        location = next;

                        if (location.distance(target) < 0.1) {
                            // close enough;
                            this.cancel();
                            isCancelled = true;
                        }
                    }
                }
            }, new Date(), movedelay);
            
            isCancelled = false;
        }
    }

    
    public interface SeagullLocationListener {
        public void gullMoved(Coordinate newLoc);
    }
}
