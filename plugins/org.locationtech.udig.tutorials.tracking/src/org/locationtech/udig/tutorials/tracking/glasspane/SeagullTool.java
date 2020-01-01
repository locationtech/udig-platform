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
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.eclipse.swt.graphics.GC;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

/**
 * Another example of the GlassPane. This tool when activated causes the seagulls to follow the
 * cursor around the screen.
 * <p>
 * </p>
 * 
 * @author Emily Gouge
 * @since 1.2.0
 */
public class SeagullTool extends SimpleTool {

    private GlassPane gp; // glasspane
    private Collection<Seagull> gulls; // seagulls

    private Timer mapupdatetimer; // time to redraw the map

    /**
     * Creates a new tool that listens to mouse motion events
     */
    public SeagullTool() {
        super(MOUSE | MOTION);
    }

    /**
     * When activated we create a glasspane and start the map update time working. When disabled
     * the timer is cancelled and the glasspane removed.
     */
    public void setActive( boolean active ) {
        super.setActive(active);
        if (active) {
            init();
        } else {
            mapupdatetimer.cancel();
            ((ViewportPane) getContext().getMapDisplay()).setGlass(null);
            gp = null;
            gulls = null;
        }
    }

    /**
     * Sets up the glass pane and seagull flock
     */
    private void init() {
        // get the map
        IMap map = getContext().getMap();
        if (map == null)
            return;

        final ViewportPane viewer = (ViewportPane) map.getRenderManager().getMapDisplay();
        // creates seagulls
        gulls = createSeagulls(map.getViewportModel().getBounds(), map.getViewportModel().getCRS());
        // creates glass panel
        addFlockGlassPane(viewer, gulls);

        // timer to update map
        int refreshrate = 100;

        // redraw map
        mapupdatetimer = new Timer();
        mapupdatetimer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                viewer.repaint();

            }
        }, new Date(), refreshrate);
    }

    /**
     * Called when a moved event occurs. 
     * 
     * <p>When the mouse is moved the target location of all the
     * seagulls is set.  This will cause the seagulls to start to move
     * towards this location.</p>
     * 
     * @param e the mouse event
     */
    protected void onMouseMoved( MapMouseEvent e ) {
        int x = e.x;
        int y = e.y;
        for( Iterator<Seagull> iterator = gulls.iterator(); iterator.hasNext(); ) {
            Seagull gull = (Seagull) iterator.next();
            gull.setTargetCoordinate(gp.getSite().pixelToWorld(x, y));
        }
    }

    /**
     * Creates a colleciton of seagulls within the bounds
     *
     * @param bounds
     * @param initial
     * @return
     */
    private Collection<Seagull> createSeagulls( ReferencedEnvelope bounds,
            CoordinateReferenceSystem initial ) {
        int numseagulls = 10;
        ArrayList<Seagull> gulls = new ArrayList<Seagull>();

        for( int i = 0; i < numseagulls; i++ ) {
            Seagull s = new Seagull("Seagull " + i, gulls); //$NON-NLS-1$

            double x = (bounds.getMaxX() - bounds.getMinX()) * Math.random() + bounds.getMinX();
            double y = (bounds.getMaxY() - bounds.getMinY()) * Math.random() + bounds.getMinY();
            Coordinate pos = new Coordinate(x, y);
            s.setPosition(pos, initial);
            gulls.add(s);
        }
        return gulls;
    }

    /**
     * Adds a glasspane to the viewport pane that draws seagulls
     *
     * @param p
     * @param gulls
     */
    private void addFlockGlassPane( ViewportPane p, final Collection<Seagull> gulls ) {
        gp = new GlassPane(p){

            @Override
            public void draw( GC graphics ) {
                for( Iterator<Seagull> iterator = gulls.iterator(); iterator.hasNext(); ) {
                    Seagull bird = (Seagull) iterator.next();
                    bird.drawSeagull(graphics, this);
                }
            }
        };

        p.setGlass(gp);

    }
}
