/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tool.select.internal;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.boundary.BoundaryListener;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This service is set by the Boundary Navigation Tool and provides a filter service based on the
 * selection of boundary layer features.
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class BoundaryLayerStrategy extends IBoundaryStrategy {

    private static final String BOUNDARY_GEOMETRY = "boundaryGeometry";
    private static final String BOUNDARY_LAYER = "boundaryLayer";
    private static String name = "Layer";    
    /**
     * This layer is looked up by id on the layer blackboard
     */
    //private ILayer activeLayer = null;

//    private ILayer featureLayer = null;
//    private SimpleFeatureCollection features = null;

    /**
     * Set the geometry to be used as the boundary
     * 
     * @param geometry The geometry to use as the boundary must be type Polygon or Multy Polygon
     */
    public void setGeometry( Geometry geometry ) {
        ILayer victim = getActiveLayer();
        IBlackboard mapBlackboard;
        if( victim != null ){
            IBlackboard blackboard = victim.getBlackboard();
            if( geometry != null ){
                blackboard.put(BOUNDARY_GEOMETRY, geometry );            
            }
            else {
                blackboard.remove(BOUNDARY_GEOMETRY);
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
            mapBlackboard.put(BOUNDARY_GEOMETRY, wkt );
        }
        else {
            mapBlackboard.remove(BOUNDARY_GEOMETRY );            
        }
        
        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
        notifyListeners(boundaryEvent);
    }
    
    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getExtent()
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
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getBoundary()
     */
    @Override
    public Geometry getGeometry() {
        ILayer victim = getActiveLayer();
        if( victim == null ){
            return null;
        }
        IBlackboard blackboard = victim.getBlackboard();
        Geometry geometry = (Geometry) blackboard.get(BOUNDARY_GEOMETRY );
        return geometry;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getCrs()
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
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets a list of Boundary Layers from the active map
     * The list is in z-order
     * @return List<ILayer>
     */
    public List<ILayer> getBoundaryLayers() {

        List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers(); // immutable must copy
        List<ILayer> boundaryLayers = new ArrayList<ILayer>();
        
        for (ILayer layer: layers) {
            if (layer.isApplicable(ILayer.Interaction.BOUNDARY)) {
                boundaryLayers.add(layer);
            }
        }
        return boundaryLayers;
    }
    
    /**
     * @return the activeLayer
     */
    public ILayer getActiveLayer() {
        IMap map = ApplicationGIS.getActiveMap();
        
        // check if this map has a boundary layer marked already
        ILayer layer = restoreFromMapBlackboard(map);
        if( layer != null ){
            return layer;
        }
        
        // fine we will assume the last boundary layer for this map
//        List<ILayer> layers = getBoundaryLayers();
//        if (layers.size() > 0) {
//            ILayer defaultLayer = layers.get(0);
//            setActiveLayer(defaultLayer);
//            return defaultLayer;
//        }
        return null; // not found!
    }

    private ILayer restoreFromMapBlackboard( IMap map ) {
        String layerId = (String) map.getBlackboard().get(BOUNDARY_LAYER);
        if( layerId != null ){
            for( ILayer layer : map.getMapLayers() ){
                if( layerId.equals( layer.getID().toExternalForm())){
                    // we found a match!
                    // test if it has a geometry already
                    IBlackboard blackboard = layer.getBlackboard();
                    if( blackboard.get(BOUNDARY_GEOMETRY) == null ){
                        // try and restore from WKT
                        String wkt = map.getBlackboard().getString(BOUNDARY_GEOMETRY);
                        if( wkt != null ){
                            // we got one!
                            GeometryFactory gf = JTSFactoryFinder.getGeometryFactory(null);
                            WKTReader reader = new WKTReader(gf);
                            try {
                                Geometry geometry = reader.read(wkt);
                                blackboard.put(BOUNDARY_GEOMETRY, geometry );
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
            // mark this one as the "boundaryLayer" for the map
            map.getBlackboard().put(BOUNDARY_LAYER, layerId );
        }
        else {
            map = ApplicationGIS.getActiveMap();
            if( map == null){
                return;
            }
            map.getBlackboard().remove(BOUNDARY_LAYER);
        }        
        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
        notifyListeners(boundaryEvent);
    }
    
    /**
     * Look up the "next" layer in the map layer list; only boundary layers are considered.
     * 
     * @return The next layer; or null if it is not available.
     */
    public ILayer getNextLayer() {
        List<ILayer> layers = getBoundaryLayers();
        ILayer activeLayer = getActiveLayer();
        
        int index = layers.indexOf(activeLayer);
        if (index < layers.size()-1) {
            return layers.get(index+1);
        }
        return null; // nothing to see here move along
    }
    
    /**
     * Look up the the previous layer in the map layer list; only boundary
     * layers are considered.
     * 
     * @return The previous layer if available; or null if we are already the "last" layer
     */
    public ILayer getPreviousLayer() {
        ILayer activeLayer = getActiveLayer();
        if (activeLayer == null) {
            return null; // nothing to see here move along
        }        
        List<ILayer> layers = getBoundaryLayers();
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