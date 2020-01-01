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
package org.locationtech.udig.tutorials.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.SimpleInternationalString;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ActiveMapTracker;
import org.locationtech.udig.tutorials.tracking.trackingitem.Seagull;
import org.locationtech.udig.tutorials.tracking.trackingitem.SeagullFlock;
import org.locationtech.udig.tutorials.tracking.trackingitem.TrackingItem;
import org.locationtech.udig.ui.operations.IOp;

public class CreateSeagulls implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        
        //System.out.println("create seagulls called"); //$NON-NLS-1$
        // build some flock data to use
        SeagullFlock flock = new SeagullFlock("flock 01",  //$NON-NLS-1$
                new SimpleInternationalString("flock 01"), DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        
        flock.addChild(new Seagull(flock, "seagull A",  //$NON-NLS-1$
                new SimpleInternationalString("seagull A"), //$NON-NLS-1$
                new Coordinate(-123,48))); 
        
        flock.addChild(new Seagull(flock, "seagull B",  //$NON-NLS-1$
                new SimpleInternationalString("seagull B"), //$NON-NLS-1$  
                new Coordinate(-77,-12))); 
        
        flock.addChild(new Seagull(flock, "seagull C",  //$NON-NLS-1$
                new SimpleInternationalString("seagull C"), //$NON-NLS-1$ 
                new Coordinate(-80,20)));      
        
        flock.addChild(new Seagull(flock, "seagull D",  //$NON-NLS-1$
                new SimpleInternationalString("seagull D"), //$NON-NLS-1$
                new Coordinate(-86,22)));   
        
        flock.addChild(new Seagull(flock, "seagull E",  //$NON-NLS-1$
                new SimpleInternationalString("seagull E"), //$NON-NLS-1$
                new Coordinate(-90,0)));   
        
        flock.addChild(new Seagull(flock, "seagull F",  //$NON-NLS-1$
                new SimpleInternationalString("seagull F"), //$NON-NLS-1$
                new Coordinate(-60,34)));   
        
        flock.addChild(new Seagull(flock, "seagull G",  //$NON-NLS-1$
                new SimpleInternationalString("seagull G"), //$NON-NLS-1$
                new Coordinate(-118,2)));           
        
        // stick our test flock on the blackboard
        IMap map = (IMap) target;  //ApplicationGIS.getActiveMap();
        if (map == null)
            return;  
        
        IBlackboard blackboard = map.getBlackboard();
        List<SeagullFlock> flocks = new ArrayList<SeagullFlock>();
        flocks.add(flock);
        blackboard.put(SeagullFlock.BLACKBOARD_KEY, flocks);  
        
        //System.out.println("created segaulls for map: " + map.getID()); //$NON-NLS-1$
        
        // refresh any trackinggraphic layers to show the new flock
        //map.getRenderManager().refresh(null);
        List<ILayer> mapLayers = map.getMapLayers();
        if (mapLayers != null) {
            for (ILayer layer : mapLayers) {
                if (layer.getGeoResource().canResolve(TrackingGraphic.class)) {
                    layer.refresh(null);
                }
            }
        }
        
        // flock is created, now setup a job to randomly update it
        long delay = 500; // 0.5 secs
        UpdateFlockJob newJob = new UpdateFlockJob(map, delay);
        newJob.schedule(delay);
        
        
    }
    
    /**
     * This job will randomly update the seagull locations and reschedule itself to run again
     * 
     * @author GDavis
     * @since 1.1.0
     */
    private class UpdateFlockJob extends Job {
        private long delay;
        private IMap map;

        public UpdateFlockJob(IMap map, long delay) {
            super("Update Flock Job for map: " + map.getID());   //$NON-NLS-1$
            this.map = map;
            this.delay = delay;
        }

        protected IStatus run(IProgressMonitor monitor) {
            // check the blackboard of the given map for a current flock
            //System.out.println("update job called for map: " + map.getID()); //$NON-NLS-1$
            if (map == null)
                return Status.OK_STATUS;  
            
            IBlackboard blackboard = map.getBlackboard();      
            List<SeagullFlock> flocks = (List<SeagullFlock>) blackboard.get(SeagullFlock.BLACKBOARD_KEY); 
            
            if (flocks == null) {
                return Status.OK_STATUS; // no seagull flocks to update
            }   
            
            // for each flock, update each seagull coordinate randomly
            Random generator = new Random();
            for (SeagullFlock flock : flocks) {
               for (TrackingItem seagull : flock.getChildren()) {
                   Coordinate coordinate = seagull.getCoordinate();
                   double x = generator.nextDouble() * 2 - 1; // num between -1 and 1
                   double y = generator.nextDouble() * 2 - 1;
                   coordinate.x += x;
                   coordinate.y += y;
                   seagull.setCoordinate(coordinate);
               }
            }
            
            // flock exists and was updated, so reschedule this job to go again
            UpdateFlockJob newJob = new UpdateFlockJob(map, delay);
            newJob.schedule(delay);

            return Status.OK_STATUS;
        }
    }    

}
