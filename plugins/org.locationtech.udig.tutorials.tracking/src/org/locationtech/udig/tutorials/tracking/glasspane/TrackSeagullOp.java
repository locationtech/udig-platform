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
package org.locationtech.udig.tutorials.tracking.glasspane;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.command.navigation.SetViewportCenterCommand;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneTiledSWT;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;
import org.locationtech.udig.ui.operations.IOp;

public class TrackSeagullOp implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {

        //get the map 
        final IMap map = (IMap) target;  //ApplicationGIS.getActiveMap();
        if (map == null)
            return;

        
        final ViewportPane viewer = (ViewportPane)map.getRenderManager().getMapDisplay();
        
        //create a flock of seagulls
        final Collection<Seagull> gulls = createSeagulls(map.getViewportModel().getCenter(), map.getViewportModel().getCRS());
        
        Seagull trackingGull = gulls.iterator().next();
        trackingGull.setColor(SWT.COLOR_RED);
        trackingGull.moveSeagull(new Coordinate(0,0));
        trackingGull.setLocationListener(new Seagull.SeagullLocationListener(){
            public void gullMoved( final Coordinate newLoc ) {
                Display.getDefault().asyncExec(new Runnable(){

                    private void scroll(int newx, int newy, int startx, int starty){
                        ViewportModelImpl vm =  (ViewportModelImpl)map.getViewportModel();
                        final ViewportPaneTiledSWT viewera = (ViewportPaneTiledSWT)viewer;
                        
                        org.eclipse.swt.graphics.Point p = Display.getCurrent().map((Canvas)viewera, null, newx, newy);
                        org.eclipse.swt.graphics.Point p2 = Display.getCurrent().map((Canvas)viewera, null, startx, starty);
                        int xdiff = p2.x - p.x;
                        int ydiff = p2.y - p.y;
                        ((Canvas)viewera).scroll(xdiff, ydiff, 0,0, map.getRenderManager().getMapDisplay().getWidth(), map.getRenderManager().getMapDisplay().getHeight(), true);
                        
                    }
                    public void run() {
                        ViewportModelImpl vm =  (ViewportModelImpl)map.getViewportModel();
                        final ViewportPaneTiledSWT viewera = (ViewportPaneTiledSWT)viewer;
                        final ReferencedEnvelope bounds = vm.getBounds();
                        Coordinate currentc = vm.getCenter();
                      
                        final Point newpnt = vm.worldToPixel(newLoc);
                        final Point oldpnt = vm.worldToPixel(currentc);
                        
                        vm.setIsBoundsChanging(true);
                        
                        int xoffset =  oldpnt.x - newpnt.x;
                        int yoffset = oldpnt.y - newpnt.y;
                        
                        int diffx = 0;
                        int diffy = 0;
                        
                        int xdiff = (int)(xoffset / 10.0);
                        int ydiff = (int)(yoffset / 10.0);
                        if (xdiff == 0){
                            if (xoffset > 0){
                                xdiff = 1;
                            }else{
                                xdiff = -1;
                            }
                        }
                        if (ydiff == 0){
                            if (yoffset > 0){
                                ydiff = 1;
                            }else{
                                ydiff = -1;
                            }
                        }
                        int lastx = oldpnt.x;
                        int lasty = oldpnt.y;
                        
                        while(Math.abs(diffx) < Math.abs(xoffset)  || Math.abs(diffy) < Math.abs(yoffset) ){
                            if (Math.abs(diffx) < Math.abs(xoffset))
                                diffx += xdiff;
                            if (Math.abs(diffy) < Math.abs(yoffset))
                                diffy += ydiff;
                            
                            scroll(lastx - xdiff, lasty-ydiff, lastx,lasty  );
                            lastx -= xdiff;
                            lasty -= ydiff;     
                        }
                        lastx -= xdiff;
                        lasty -=ydiff;
                        Coordinate newCoo = vm.pixelToWorld(lastx, lasty);
                        vm.setIsBoundsChanging(false);
                        vm.setCenter(newCoo);
                    }});
                
//                SetViewportCenterCommand cmd = new SetViewportCenterCommand(newLoc);
//                map.sendCommandASync(cmd);
            }
        });
        
        
        
        //create a glass pane that draws the seagulls
        addFlockGlassPane(viewer, gulls);
     
        //timer to update map
        int refreshrate = 300;

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
//        while(true){
//            trackingGull.moveSeagull(new Coordinate(5,5));
//            trackingGull.moveSeagull(new Coordinate(0,0));
//            if (1==2){
//                break;
//            }
//        }
        
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
