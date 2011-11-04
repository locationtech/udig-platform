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
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This service is set by the Boundary Navigation Tool and provides a filter service based on the
 * selection of boundary layer features.
 * 
 * @author leviputna
 * @since 1.2.3
 */
public class BoundaryLayerStrategy extends IBoundaryStrategy {

    private static String name = "Layer";
    private CoordinateReferenceSystem crs;
    private Geometry geometry;
    private ILayer activeLayer = null;
//    private ILayer featureLayer = null;
//    private SimpleFeatureCollection features = null;

    /**
     * Set the CRS of the current boundary
     * 
     * @param crs of the current boundary
     */
    public void setCrs( CoordinateReferenceSystem crs ) {
        this.crs = crs;
        
//        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
//        notifyListeners(boundaryEvent);
    }

    /**
     * Set the geometry to be used as the boundary
     * 
     * @param geometry The geometry to use as the boundary must be type Polygon or Multy Polygon
     */
    public void setGeometry( Geometry geometry ) {
        this.geometry = geometry;
        
        BoundaryListener.Event boundaryEvent = new BoundaryListener.Event( BoundaryLayerStrategy.this);
        notifyListeners(boundaryEvent);
    }
    
    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getExtent()
     */
    @Override
    public ReferencedEnvelope getExtent() {
        if (geometry != null) return new ReferencedEnvelope(geometry.getEnvelopeInternal(), crs);
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getBoundary()
     */
    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.boundary.IBoundaryStrategy#getCrs()
     */
    @Override
    public CoordinateReferenceSystem getCrs() {
        return crs;
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
        if (activeLayer == null) {
            List<ILayer> layers = getBoundaryLayers();
            if (layers.size() > 0) {
                return layers.get(0);
            }
        }
        return activeLayer;
    }

    /**
     * Sets the active layer and notifies listeners
     * @param activeLayer the activeLayer to set
     */
    public void setActiveLayer( ILayer activeLayer ) {
        this.activeLayer = activeLayer;
        
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
        
        activeLayer = getActiveLayer();
        
        
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