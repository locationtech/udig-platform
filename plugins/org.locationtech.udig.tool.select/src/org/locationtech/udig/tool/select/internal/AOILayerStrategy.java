/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select.internal;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * This service is set by the AOI Navigation Tool and provides a filter service based on the
 * selection of AOI layer features.
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class AOILayerStrategy extends IAOIStrategy {

    private static final String AOI_GEOMETRY = "aoiGeometry";
    private static final String AOI_LAYER = "aoiLayer";
    private static String name = "Layer";    
    /**
     * This layer is looked up by id on the layer blackboard
     */
    //private ILayer activeLayer = null;

//    private ILayer featureLayer = null;
//    private SimpleFeatureCollection features = null;

    /**
     * Set the geometry to be used as the AOI
     * 
     * @param geometry The geometry to use as the AOI must be type Polygon or Multy Polygon
     */
    public void setGeometry( Geometry geometry ) {
        ILayer victim = getActiveLayer();
        IBlackboard mapBlackboard;
        if( victim != null ){
            IBlackboard blackboard = victim.getBlackboard();
            if( geometry != null ){
                blackboard.put(AOI_GEOMETRY, geometry );            
            }
            else {
                blackboard.remove(AOI_GEOMETRY);
            }
            mapBlackboard = victim.getMap().getBlackboard();
        }
        else {
            IMap activeMap = ApplicationGIS.getActiveMap();
            if( activeMap == null){
                return;
            }
            mapBlackboard = activeMap.getBlackboard();
        }
        if( geometry != null ){
            // record a WKT note in the mapblackboard for a rainy day
            String wkt = geometry.toText();
            mapBlackboard.put(AOI_GEOMETRY, wkt );
        }
        else {
            mapBlackboard.remove(AOI_GEOMETRY );            
        }
        
        AOIListener.Event aoiEvent = new AOIListener.Event( AOILayerStrategy.this);
        notifyListeners(aoiEvent);
    }
    
    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.aoi.IAOIStrategy#getExtent()
     */
    @Override
    public ReferencedEnvelope getExtent() {
        Geometry geometry = getGeometry();
        if (geometry != null){
            CoordinateReferenceSystem crs = getCrs();
            return new ReferencedEnvelope(geometry.getEnvelopeInternal(), crs);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.aoi.IAOIStrategy#getAOI()
     */
    @Override
    public Geometry getGeometry() {
        ILayer victim = getActiveLayer();
        if( victim == null ){
            return null;
        }
        IBlackboard blackboard = victim.getBlackboard();
        Geometry geometry = (Geometry) blackboard.get(AOI_GEOMETRY );
        return geometry;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.aoi.IAOIStrategy#getCrs()
     */
    @Override
    public CoordinateReferenceSystem getCrs() {
        ILayer activeLayer = getActiveLayer();
        if( activeLayer == null ){
            return null; // I am confused please try again
        }
        return activeLayer.getCRS();
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.aoi.IAOIStrategy#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets a list of AOI Layers from the active map
     * The list is in z-order
     * @return List<ILayer>
     */
    public List<ILayer> getAOILayers() {

        List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers(); // immutable must copy
        List<ILayer> aoiLayers = new ArrayList<ILayer>();
        
        for (ILayer layer: layers) {
            if (layer.getInteraction(Interaction.AOI)) {
                aoiLayers.add(layer);
            }
        }
        return aoiLayers;
    }
    
    /**
     * @return the activeLayer
     */
    public ILayer getActiveLayer() {
        IMap map = ApplicationGIS.getActiveMap();
        
        // check if this map has a AOI layer marked already
        ILayer layer = restoreFromMapBlackboard(map);
        if( layer != null ){
            return layer;
        }
        
        // fine we will assume the last AOI layer for this map
//        List<ILayer> layers = getAOILayers();
//        if (layers.size() > 0) {
//            ILayer defaultLayer = layers.get(0);
//            setActiveLayer(defaultLayer);
//            return defaultLayer;
//        }
        return null; // not found!
    }

    private ILayer restoreFromMapBlackboard( IMap map ) {
        String layerId = (String) map.getBlackboard().get(AOI_LAYER);
        if( layerId != null ){
            for( ILayer layer : map.getMapLayers() ){
                if( layerId.equals( layer.getID().toExternalForm())){
                    // we found a match!
                    // test if it has a geometry already
                    IBlackboard blackboard = layer.getBlackboard();
                    if( blackboard.get(AOI_GEOMETRY) == null ){
                        // try and restore from WKT
                        String wkt = map.getBlackboard().getString(AOI_GEOMETRY);
                        if( wkt != null ){
                            // we got one!
                            GeometryFactory gf = JTSFactoryFinder.getGeometryFactory(null);
                            WKTReader reader = new WKTReader(gf);
                            try {
                                Geometry geometry = reader.read(wkt);
                                blackboard.put(AOI_GEOMETRY, geometry );
                            } catch (ParseException e) {
                                // no can do!
                            }
                        }
                    }
                    // found!
                    return layer;
                }
            }
        }
        return null; // not found!
    }

    /**
     * Sets the active layer and notifies listeners
     * @param activeLayer the activeLayer to set
     */
    public void setActiveLayer( ILayer activeLayer ) {
        IMap map;
        if( activeLayer != null ){
            map = activeLayer.getMap();
            String layerId = activeLayer.getID().toExternalForm();
            // mark this one as the "AOILayer" for the map
            map.getBlackboard().put(AOI_LAYER, layerId );
        }
        else {
            map = ApplicationGIS.getActiveMap();
            if( map == null){
                return;
            }
            map.getBlackboard().remove(AOI_LAYER);
        }        
        AOIListener.Event aoiEvent = new AOIListener.Event( AOILayerStrategy.this);
        notifyListeners(aoiEvent);
    }
    
    /**
     * Look up the "next" layer in the map layer list; only AOI layers are considered.
     * 
     * @return The next layer; or null if it is not available.
     */
    public ILayer getNextLayer() {
        List<ILayer> layers = getAOILayers();
        ILayer activeLayer = getActiveLayer();
        
        int index = layers.indexOf(activeLayer);
        if (index < layers.size()-1) {
            return layers.get(index+1);
        }
        return null; // nothing to see here move along
    }
    
    /**
     * Look up the the previous layer in the map layer list; only AOI
     * layers are considered.
     * 
     * @return The previous layer if available; or null if we are already the "last" layer
     */
    public ILayer getPreviousLayer() {
        ILayer activeLayer = getActiveLayer();
        if (activeLayer == null) {
            return null; // nothing to see here move along
        }        
        List<ILayer> layers = getAOILayers();
        int index = layers.indexOf(activeLayer);
        if (index > 0) {
            ILayer previousLayer = layers.get( index-1 );
            return previousLayer;
        }
        return null;
    }
    
//    /**
//     * Gets the features that have been selected
//     * @return List of SimpleFeature
//     */
//    public List<SimpleFeature> getFeatures() {
//        List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
//        if (features != null) {
//            SimpleFeatureIterator featuresIterator = features.features();
//            while (featuresIterator.hasNext()) {
//                featureList.add(featuresIterator.next());
//            }
//        }
//        return featureList;
//    }
//
//    /**
//     * Sets the current features selected
//     * @param featureCollection
//     */
//    public void setFeatures(SimpleFeatureCollection featureCollection) {
//        features = featureCollection;
//    }
    
}
