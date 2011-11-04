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
        IBlackboard blackboard = victim.getBlackboard();
        blackboard.put(BOUNDARY_GEOMETRY, geometry );
        
        // record a WKT note in the mapblackboard for a rainy day
        IBlackboard mapBlackboard = victim.getMap().getBlackboard();
        String wkt = geometry.toText();
        mapBlackboard.put(BOUNDARY_GEOMETRY, wkt );
        
        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
        notifyListeners(boundaryEvent);
    }
    
    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getExtent()
     */
    @Override
    public ReferencedEnvelope getExtent() {
        if (getGeometry() != null){
            CoordinateReferenceSystem crs = getCrs();
            return new ReferencedEnvelope(getGeometry().getEnvelopeInternal(), crs);
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
        List<ILayer> layers = getBoundaryLayers();
        if (layers.size() > 0) {
            ILayer defaultLayer = layers.get(0);
            setActiveLayer(defaultLayer);
            return defaultLayer;
        }
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
        IMap map = activeLayer.getMap();
        // mark this one as the "boundaryLayer" for the map
        String layerId = activeLayer.getID().toExternalForm();
        map.getBlackboard().put(BOUNDARY_LAYER, layerId );
        
        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
        notifyListeners(boundaryEvent);
    }
    
    /**
     * Sets the Active layer to be the next available boundary layer and notifies listeners. 
     * If the active layer has not been set it will choose the first layer. 
     * No change if the active layer is the last layer (z-order) or there are no boundary layers.
     * @return ILayer the new active layer or null if no boundary layers exist
     */
    public ILayer selectNextLayer() {
        List<ILayer> layers = getBoundaryLayers();
        
        ILayer activeLayer = getActiveLayer();
        
        
        // set active layer to next in list 
        int index = layers.indexOf(activeLayer);
        if (index < layers.size()-1) {
            setActiveLayer(layers.get(index+1)); // also triggers event
        }
        return activeLayer;
    }
    
    /**
     * Sets the active layer to the previous boundary layer in z-order.
     * Returns the new layer or the first if no previous available.
     * Returns null if there are no boundary layers
     * 
     * @return ILayer the previous layer 
     */
    public ILayer selectPreviousLayer() {
        ILayer activeLayer = getActiveLayer();
        if (activeLayer == null) {
            return null;
        }
        
        List<ILayer> layers = getBoundaryLayers();
        int index = layers.indexOf(activeLayer);
        if (index > 0) {
            ILayer previousLayer = layers.get(index-1);
            setActiveLayer(previousLayer); // also triggers event
            return previousLayer;
        }
        if (layers.size()>0) {
            return layers.get(0);
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