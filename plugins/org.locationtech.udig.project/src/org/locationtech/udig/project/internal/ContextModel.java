/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.opengis.filter.Filter;

import org.locationtech.jts.geom.Envelope;

/**
 * Holds onto the list of layers for the IMap.
 * <p>
 * This interface has been deprecated; please may use of the IMap interface
 * directly.
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 * @deprecated Please use IMap for read-only methods
 */
public interface ContextModel extends EObject {
    /**
     * Select all selectable layers, with a BoundingBox Geometry filter. Boundingbox must be in the
     * same CRS as the ViewportModel
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param boundingBox the bounding box in Viewportmodel CRS to create a filter with
     * @deprecated use {@link Map#select(Envelope)}
     */
    public void select(Envelope boundingBox);

    /**
     * Select all selectable layers, with a BoundingBox Geometry filter. Boundingbox must be in the
     * same CRS as the ViewportModel
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param boundingBox the bounding box in Viewportmodel CRS to create a filter with
     * @param and true adds (or with current filter)to the current selection, false removes from the
     *        current selection(and with current filter).
     * @deprecated use {@link Map#select(Envelope, boolean)}
     */
    public void select(Envelope boundingBox, boolean and);

    /**
     * Select all selectable layers, with the provided filter.
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param filter new selection filter
     * @deprecated use {@link Map#select(Filter)}
     */
    public void select(Filter filter);

    /**
     * Combines selection, from the selectable layers, with the provided filter.
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param filter the new filter
     * @param and true adds (or with current filter)to the current selection, false removes from the
     *        current selection(and with current filter).
     * @deprecated use {@link Map#select(Filter, boolean)}
     */
    public void select(Filter filter, boolean and);

    /**
     * Gets the complete set of Layer objects. The order of the objects represents the the z-order
     * in which they are renderered
     * 
     * @return A List of all the Layers in the object
     * @model type="Layer" containment="true" opposite="contextModel"
     * @deprecated use {@link Map#getLayersInternal()}
     */
    public List<Layer> getLayers();

    /**
     * Gets the complete set of Layer objects. The order of the objects represents the the z-order
     * in which they are renderered
     * 
     * @return A List of all the Layers in the object
     * @see getLayers for the EMF data model
     * @deprecated use {@link Map#getLayersInternal()}
     */
    public List<Layer> layers();

    /**
     * Gets the owning map object.
     * 
     * @return the owning map object.
     * @model opposite="contextModel" many="false"
     */
    public Map getMap();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.ContextModel#getMap <em>Map</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map</em>' container reference.
     * @see #getMap()
     * @generated
     */
    void setMap(Map value);

    /**
     * Adds an adapter to the context model and all of the layers in the model.
     * Each time a layer is added the adapter will be added to the model as well.
     *
     * @param adapter adapter to add as a deep adapter
     * @deprecated use {@link Map#addDeepAdapter(Adapter)}
     */
    public void addDeepAdapter(Adapter adapter);

    /**
     * Removes the adapter from all layers and context model.
     *
     *@see #addDeepAdapter(Adapter)
     * @param adapter adapter to remove.
     * @deprecated use {@link Map#removeDeepAdapter(Adapter)}
     */
    public void removeDeepAdapter(Adapter adapter);

    /**
     * Increases the ZOrder of the layer so it is rendered earlier with incomparison to the other
     * layers. If the Layer is at the bottom of the render list(first to be drawn) it is not
     * affected.
     * 
     * @param layer The layer whose rendering order will be modified
     * @deprecated use {@link Map#lowerLayer(Layer)}
     */
    public void lowerLayer(Layer layer);

    /**
     * Decreases the ZOrder of the layer so it is rendered later with incomparison to the other
     * layers. If the Layer is at the top of the render list(last to be drawn) it is not affected.
     * 
     * @param layer The layer whose rendering order will be modified
     * @deprecated use {@link Map#raiseLayer(Layer)}
     */
    public void raiseLayer(Layer layer);

}
