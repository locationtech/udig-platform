/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.aoi.AOIProxy;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tool.select.SelectPlugin;
import org.locationtech.udig.tool.select.internal.AOILayerStrategy;
import org.locationtech.udig.tool.select.preferences.SelectionToolPreferencePage;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Queries the current AOI layer (Area of Interest) for selection and updates the AOILayerStrategy
 * 
 * @see org.locationtech.udig.tools.internal.AOILayerStrategy
 * @author leviputna
 * @since 1.2.3
 */
public class SetAOILayerCommand extends AbstractCommand implements UndoableMapCommand {

    private Envelope bbox = null;
    private ReferencedEnvelope bounds;
    private MapMouseEvent mouseEvent;
    private static String AOI_LAYER_ID = "org.locationtech.udig.tool.select.internal.aoiLayer";

    /**
     * Creates a new instance of SetConndaryCommand
     * 
     * @param bbox
     */
    public SetAOILayerCommand( MapMouseEvent e, Envelope bbox ) {
        this.bbox = bbox;
        this.mouseEvent = e;
    }

    /**
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {

    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {

        // Get Preference
//        boolean zoomToSelection = SelectPlugin.getDefault().getPreferenceStore()
//                .getBoolean(SelectionToolPreferencePage.ZOOM_TO_SELECTION);
        boolean isNavigate = SelectPlugin.getDefault().getPreferenceStore()
                .getBoolean(SelectionToolPreferencePage.NAVIGATE_SELECTION);
        
        AOILayerStrategy strategy = getAOILayerStrategy();
        ILayer activeLayer = strategy.getActiveLayer();
        ILayer previousLayer = strategy.getPreviousLayer();
        ILayer nextLayer = strategy.getNextLayer();
        
        ILayer selectedLayer = activeLayer;
        
        // change the layer we are looking at based on navigation
        Geometry geometry = strategy.getGeometry();
        Polygon testLocation = JTS.toGeometry(this.bbox);
        if(isNavigate && (mouseEvent.button == MapMouseEvent.BUTTON3)){
            if( previousLayer != null ){
                selectedLayer = previousLayer;
            }
            else {
                // end of the world!
                updateAOIService(null, null);
                bounds = getMap().getBounds(null);
                ViewportModelImpl vmi = (ViewportModelImpl) getMap().getViewportModel();
                vmi.zoomToBox(bounds);
                return;
            }
        }
        else if (isNavigate && (mouseEvent.button == MapMouseEvent.BUTTON1)) {
            selectedLayer = nextLayer != null ? nextLayer : activeLayer;
            if( activeLayer != null ){
                // please stay on the active layer until you have a geometry
                if( geometry == null || !geometry.contains( testLocation)){
                    // we need a selected geometry before we let people navigate away
                    selectedLayer = activeLayer;
                }
            }
        }
        
        if( selectedLayer == null){
            return; // nothing to do!
        }
        if (!selectedLayer.getInteraction(Interaction.AOI)){
            return; // eek!
        }
        // use the bbox to see if we hit anything!
        SimpleFeatureCollection featureCollection = getFeaturesInBbox(selectedLayer, bbox, monitor);
        
        if (featureCollection.isEmpty()) {
            // the user did not click on anything useful (so sad)!
            // see if they were trying to click around on the active layer instead!
            if( selectedLayer == activeLayer){
                return; // give up no change to AOI stuffs
            }
            else {
                // quickly test to see if they clicked on a neighbour
                SimpleFeatureCollection testCollection = getFeaturesInBbox(activeLayer, bbox, monitor);
                if(!testCollection.isEmpty() ){
                    // okay let us go to neighbour
                    selectedLayer = activeLayer;
                    featureCollection = testCollection;
                }
                else {
                    return; // user really did not find anything to click on
                }
            }
        }        
        bounds = featureCollection.getBounds();
        Geometry newAOI = unionGeometry(featureCollection);
        
        updateAOIService(selectedLayer,newAOI );

        if (isNavigate) {
            IMap map = selectedLayer.getMap();
            ViewportModelImpl vmi = (ViewportModelImpl) map.getViewportModel();
            vmi.zoomToBox(bounds);
        }
    }

    /*
     * returns an AOILayerStrategy object for quick access
     */
    private AOILayerStrategy getAOILayerStrategy() {
        IAOIService aOIService = PlatformGIS.getAOIService();
        IAOIStrategy aOIStrategy = aOIService.findProxy(AOI_LAYER_ID)
                .getStrategy();

        if (aOIStrategy instanceof AOILayerStrategy) {
            return (AOILayerStrategy) aOIStrategy;
        }
        return null;
    }
    
    private SimpleFeatureCollection getFeaturesInBbox( ILayer layer, Envelope bbox,
            IProgressMonitor monitor ) throws IOException {

        SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getResource(
                FeatureSource.class, new SubProgressMonitor(monitor, 1));

        if (featureSource == null) return null;

        Filter bboxFilter = layer.createBBoxFilter(bbox, monitor);
        return featureSource.getFeatures(bboxFilter);
    }

    private Geometry unionGeometry( SimpleFeatureCollection featureCollection ) {
        if (featureCollection.size() < 0) return null;

        List<Geometry> geoms = new ArrayList<Geometry>();
        SimpleFeatureIterator featureIterator = featureCollection.features();

        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geoms.add(geometry.reverse());

        }

        GeometryFactory factory = new GeometryFactory();
        Geometry combined = factory.buildGeometry(geoms);

        return combined.union();
    }

    private void updateAOIService( ILayer layer, Geometry newAOI )
            throws IOException {
        IAOIService aOIService = PlatformGIS.getAOIService();
        AOIProxy aoiLayerProxy = aOIService.findProxy(AOI_LAYER_ID);
        
        AOILayerStrategy aOILayerStrategy = (AOILayerStrategy)aoiLayerProxy.getStrategy();
        aOILayerStrategy.setActiveLayer(layer);
        aOILayerStrategy.setGeometry(newAOI);
        
        // if the current stragegy does not equal the bounary layer strategy set it
        if (!aOIService.getProxy().equals(aoiLayerProxy)) {
            aOIService.setProxy(aoiLayerProxy);
        }
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.BBoxSelectionCommand_boxSelection;
    }

}
