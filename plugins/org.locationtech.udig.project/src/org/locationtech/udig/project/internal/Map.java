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

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.CommandStack;
import org.locationtech.udig.project.command.NavCommandStack;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.ui.palette.ColourScheme;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;

import org.locationtech.jts.geom.Envelope;

/**
 * TODO Purpose of org.locationtech.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface Map extends ProjectElement, IMap {
    /**
     * Return's the map's context model
     * 
     * @return the map's context model
     * @model containment="true" opposite="map" many="false" settable="false"
     */
    public ContextModel getContextModel();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getContextModel <em>Context Model</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Context Model</em>' containment reference.
     * @see #getContextModel()
     * @generated
     */
    void setContextModel(ContextModel value);

    /**
     * Returns the Viewport model for this map.
     * 
     * @return the Viewport model for this map.
     * @model containment="true" opposite="mapInternal" many="false"
     */
    public ViewportModel getViewportModelInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getViewportModelInternal <em>Viewport Model Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Viewport Model Internal</em>' containment reference.
     * @see #getViewportModelInternal()
     * @generated
     */
    void setViewportModelInternal(ViewportModel value);

    /**
     * Returns the colour palette that layer's default colours are picked from.
     * 
     * @return
     * @model
     */
    BrewerPalette getColorPalette();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getColorPalette <em>Color Palette</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Color Palette</em>' attribute.
     * @see #getColorPalette()
     * @generated
     */
    void setColorPalette(BrewerPalette value);

    /**
     * @return
     * @model
     */
    public ColourScheme getColourScheme();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getColourScheme <em>Colour Scheme</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Colour Scheme</em>' attribute.
     * @see #getColourScheme()
     * @generated
     */
    void setColourScheme(ColourScheme value);

    /**
     * Iterates through the current layers and returns the default colours in use.
     *
     * @return 
     */
    List<Color> getMapDefaultColours();

    /**
     * Returns the map's abstract
     * 
     * @return the map's abstract
     * @uml.property name="abstract"
     * @model
     */
    String getAbstract();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getAbstract <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Abstract</em>' attribute.
     * @see #getAbstract()
     * @generated
     */
    void setAbstract(String value);

    /**
     * Gets the Envelope that indicates the maximum bounding box of the map.
     * <p>
     * The bounds returned are in Lat Long and each time the method is called a new object is
     * returned. Therefore the object can be modified as desired without affecting the model.
     * </p>
     * <p>
     * Note: this is a constant for a given map. It is related to the size of the map data, and is
     * not dependent on the viewport.
     * </p>
     * 
     * @return The Envelope in Lat Long that indicates the maximum bounding box of the map.
     * @throws IOException
     * @model transient="true" changeable="false" unsettable='true' default=''
     */
    public ReferencedEnvelope getBounds(IProgressMonitor monitor);

    /**
     * Returns the Aspect ratio of the map. It is normally no the same as the aspect ratio of the
     * viewport.
     * 
     * @return The aspect ratio of the map.
     * @model volatile="true" changeable="false" transient="true"
     */
    public double getAspectRatio(IProgressMonitor monitor);

    /**
     * Returns the Rendermanager for the current map.
     * 
     * @return the Rendermanager for the current map.
     * @model containment="true" opposite="mapInternal" many="false" transient="true"
     */
    public EditManager getEditManagerInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getEditManagerInternal <em>Edit Manager Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Edit Manager Internal</em>' containment reference.
     * @see #getEditManagerInternal()
     * @generated
     */
    void setEditManagerInternal(EditManager value);

    /**
     * Returns the SelectionManager for the current map.
     * 
     * @return the SelectionManager for the current map.
     * @model containment="false" transient="true" opposite="mapInternal" many="false"
     */
    public RenderManager getRenderManagerInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getRenderManagerInternal <em>Render Manager Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Render Manager Internal</em>' reference.
     * @see #getRenderManagerInternal()
     * @generated
     */
    void setRenderManagerInternal(RenderManager value);

    /**
     * Redo the last command undone.
     * 
     * @model
     */
    public void redo();

    /**
     * Undo the last command.
     * 
     * @model
     */
    public void undo();

    /**
     * move back to the last viewed location
     * 
     * @model
     */
    public void backwardHistory();

    /**
     * move back to the last viewed location
     * 
     * @model
     */
    public void forwardHistory();

    /**
     * Returns the number of Navigation Commands in the command stack
     * 
     * @model volatile="true" changeable="false" transient="true"
     */
    NavCommandStack getNavCommandStack();

    /**
     * Returns the number of Normal(Selection and Editing) Commands in the command stack
     * 
     * @model volatile="true" changeable="false" transient="true"
     */
    CommandStack getCommandStack();

    /**
     * @model resolveProxies="false" containment="true" opposite="map"
     */
    LayerFactory getLayerFactory();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getLayerFactory <em>Layer Factory</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Layer Factory</em>' containment reference.
     * @see #getLayerFactory()
     * @generated
     */
    void setLayerFactory(LayerFactory value);

    /**
     * @return the blackboard for the map.
     * @model containment="true"
     */
    Blackboard getBlackBoardInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Map#getBlackBoardInternal <em>Black Board Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Black Board Internal</em>' containment reference.
     * @see #getBlackBoardInternal()
     * @generated
     */
    void setBlackBoardInternal(Blackboard value);

    /**
     * Returns the value of the '<em><b>Legend</b></em>' containment reference list.
     * The list contents are of type {@link org.locationtech.udig.project.ILegendItem}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Legend</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Legend</em>' containment reference list.
     * @see org.locationtech.udig.project.internal.ProjectPackage#getMap_Legend()
     * @model type="org.locationtech.udig.project.internal.ILegendItem" containment="true"
     * @generated
     */
    List<ILegendItem> getLegend();

    /**
     * Returns all the layers contained in the map. The list is mutable and events will be raised if
     * the list is modified.
     * 
     * @return all the layers contained in the map.
     */
    List<Layer> getLayersInternal();

    /**
     * Adds an adapter to the context model and all of the layers in the model.
     * Each time a layer is added the adapter will be added to the model as well.
     *
     * @param adapter adapter to add as a deep adapter
     */
    public void addDeepAdapter(Adapter adapter);

    /**
     * Removes the adapter from all layers and context model.
     *
     *@see #addDeepAdapter(Adapter)
     * @param adapter adapter to remove.
     */
    public void removeDeepAdapter(Adapter adapter);

    /**
     * Increases the ZOrder of the layer so it is rendered earlier with incomparison to the other
     * layers. If the Layer is at the bottom of the render list(first to be drawn) it is not
     * affected.
     * 
     * @param layer The layer whose rendering order will be modified
     */
    public void lowerLayer(Layer layer);

    /**
     * Decreases the ZOrder of the layer so it is rendered later with incomparison to the other
     * layers. If the Layer is at the top of the render list(last to be drawn) it is not affected.
     * 
     * @param layer The layer whose rendering order will be modified
     */
    public void raiseLayer(Layer layer);

    /**
     * Increases the ZOrder of the layer so it is rendered first with incomparison to the other
     * layers. If the Layer is at the bottom of the render list(first to be drawn) it is not
     * affected.
     * 
     * @param layer The layer whose rendering order will be modified
     */
    public void sendToFrontLayer(Layer layer);

    /**
     * Decreases the ZOrder of the layer so it is rendered last with incomparison to the other
     * layers. If the Layer is at the top of the render list(last to be drawn) it is not affected.
     * 
     * @param layer The layer whose rendering order will be modified
     */
    public void sendToBackLayer(Layer layer);

    /**
     * Sets the ZOrder of the layer to the specified index so it is rendered relative to the new
     * ordering created by the change.
     * 
     * @param layer The layer whose rendering order will be modified
     */
    public void sendToIndexLayer(Layer layer, int index);

    /**
     * Select all selectable layers, with a BoundingBox Geometry filter. Boundingbox must be in the
     * same CRS as the ViewportModel
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param boundingBox the bounding box in Viewportmodel CRS to create a filter with
     * 
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
     * @param add true adds (or with current filter)to the current selection, false removes from the
     *        current selection(and with current filter).
     */
    public void select(Envelope boundingBox, boolean add);

    /**
     * Select all selectable layers, with the provided filter.
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param filter new selection filter
     */
    public void select(Filter filter);

    /**
     * Combines selection, from the selectable layers, with the provided filter.
     * <p>
     * Any Tool that wishes to be undoable would will need to remember the previous state.
     * </p>
     * 
     * @param filter the new filter
     * @param add true adds (or with current filter)to the current selection, false removes from the
     *        current selection.
     */
    public void select(Filter filter, boolean add);

    /**
     * Makes a selection in a specified layer of this map (not necessarily a selected layer ).
     * Does nothing if the specified layer does not belong to this map.
     * 
     * @param filter
     * @param layer
     */
    public void select(Filter filter, ILayer layer);

}
