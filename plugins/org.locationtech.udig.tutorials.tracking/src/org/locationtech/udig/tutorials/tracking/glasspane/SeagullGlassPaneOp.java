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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;


/**
 * Example operation that sets a glass pane.  The glass pane contains a collection
 * of seagulls that randomly move around the screen.
 * 
 * @author Emily Gouge
 * @since 1.2.0
 */
public class SeagullGlassPaneOp implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {

        //get the map 
        final IMap map = (IMap) target;  //ApplicationGIS.getActiveMap();
        if (map == null)
            return;
        
        
        final ViewportPane viewer = (ViewportPane)map.getRenderManager().getMapDisplay();
        
        //create a flock of seagulls
        final Collection<Seagull> gulls = createSeagulls(map.getViewportModel().getCenter(), map.getViewportModel().getCRS());
        
        //create a glass pane that draws the seagulls
        addFlockGlassPane(viewer, gulls);
     
        //timer to update map
        int refreshrate = 250;

        //draws the map at given intervals
        Timer mapupdatetimer = new Timer();
        mapupdatetimer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                viewer.repaint();
                
            }}, new Date(), refreshrate);
        

        //moves the seagulls at given intervals
        Timer gulltimer = new Timer();
        gulltimer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
               //move gulls
                ReferencedEnvelope mapbounds = map.getViewportModel().getBounds();
                Envelope bounds = new Envelope(mapbounds.getMinX(),mapbounds.getMaxX(), mapbounds.getMinY(), mapbounds.getMaxY());
                for( Iterator<Seagull> iterator = gulls.iterator(); iterator.hasNext(); ) {
                    Seagull seagull = (Seagull) iterator.next();
                    seagull.moveSeagull(bounds);
                }
                
            }}, new Date(), refreshrate);
        
        
    }

    /**
     * Creates a collection of seagulls that start near the center
     * coordinate.
     *
     * @param center
     * @param initial
     * @return
     */
    private Collection<Seagull> createSeagulls(Coordinate center, CoordinateReferenceSystem initial){
        int numseagulls = 10;
        ArrayList<Seagull>  gulls = new ArrayList<Seagull>();
        
        for( int i = 0; i < numseagulls; i++ ) {
            Seagull s = new Seagull("Seagull " + i); //$NON-NLS-1$
            s.setPosition(new Coordinate(center.x - i, center.y - i), initial);
            gulls.add(s);
        }
        return gulls;
    }
    
    /**
     * Adds a glass pane to the viewport pane.  This glass pane draws
     * the collection of seagulls.
     *
     * @param p
     * @param gulls
     */
    private void addFlockGlassPane(ViewportPane p, final Collection<Seagull> gulls){
        p.setGlass(new GlassPane(p){

            @Override
            public void draw( GC graphics ) {
                for( Iterator<Seagull> iterator = gulls.iterator(); iterator.hasNext(); ) {
                    Seagull bird = (Seagull) iterator.next();
                    bird.drawSeagull(graphics, this);
                }
            }});
        
    }
}
