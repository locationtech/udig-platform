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
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Range;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.core.IBlockingAdaptable;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.ui.palette.ColourScheme;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Read/Write interface for layers
 *
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface Layer
        extends EObject, ILayer, IAdaptable, IBlockingAdaptable, IResolveChangeListener {

    /**
     * Returns the owning ContextModel object
     *
     * @return the owning ContextModel object
     * @model many="false" opposite="layers"
     */
    public ContextModel getContextModel();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getContextModel
     * <em>Context Model</em>}' container reference. <!-- begin-user-doc --> TODO: Remove Context
     * Model (1 to 1 relationship does not added anything) <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Context Model</em>' container reference.
     * @see #getContextModel()
     * @generated
     */
    void setContextModel(ContextModel value);

    /**
     * Filter indicating the selected features.
     * <p>
     * In order for this value to be useful the layer should be selectable, often a single fid
     * filter during user edit opperations.
     * </p>
     * <p>
     * Note: Filter.EXCLUDE indicates no selected Features. (All features are filtered out)
     * </p>
     * <p>
     * A tool may wish to record the previous Filter, before replacing (or adding to) this value.
     * </p>
     * XXX: Consider making use of the General Purpose Blackboard
     *
     * @return Filter indicating the selected features. Filter.EXCLUDE indicates no selected
     *         Features.
     * @uml.property name="filter"
     * @model transient="true" dataType="org.opengis.filter.Filter"
     */
    @Override
    Filter getFilter();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getFilter
     * <em>Filter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Filter</em>' attribute.
     * @see #getFilter()
     * @generated
     */
    void setFilter(Filter value);

    /**
     * Sets the spatial bounds of this layer. This property is normally derived from the
     * {@link IGeoResourceInfo} but this provides an override. This will affect the "Zoom to Extent"
     * and "Zoom to Layer" actions.
     *
     * @param bounds a ReferencedEnvelope indicating the new bounds for the layer
     */
    public void setBounds(ReferencedEnvelope bounds);

    /**
     * StyleBlackboard used to persist user supplied appearance settings.
     * <p>
     * This method forms part of the EMF model of a Layer, client code should use style(). The
     * style() method allows access the StyleBlackboard without being troubled by all the model
     * methods.
     * </p>
     *
     * @see style();
     * @model many="false" containment="true"
     */
    @Override
    public StyleBlackboard getStyleBlackboard();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getStyleBlackboard
     * <em>Style Blackboard</em>}' containment reference. <!-- begin-user-doc --> Note: The
     * Rendering Process will be restarted as appearance information changes, this is usual limited
     * to a single Layer. <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Style Blackboard</em>' containment reference.
     * @see #getStyleBlackboard()
     * @generated
     */
    void setStyleBlackboard(StyleBlackboard value);

    /**
     * Returns the zorder of the layer
     *
     * @model
     */
    @Override
    public int getZorder();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getZorder
     * <em>Zorder</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Zorder</em>' attribute.
     * @see #getZorder()
     * @generated
     */
    void setZorder(int value);

    /**
     * Indication of Layer status.
     * <p>
     * This is used to provide feedback for a Layers rendering status.
     * </p>
     *
     * @return
     * @uml.property name="status"
     * @model transient='true' default='0'
     */
    @Override
    public int getStatus();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getStatus
     * <em>Status</em>}' attribute. <!-- begin-user-doc --> Indication of Layer status.
     * <p>
     * This is used to provide feedback for a Layers rendering status.
     * </p>
     *
     * @see ILayer#DONE
     * @see ILayer#ERROR
     * @see ILayer#MISSING
     * @see ILayer#UNCONFIGURED
     * @see ILayer#WARNING
     * @see ILayer#WORKING
     * @param status Status should be one of DONE, ERROR, MISSING, UNCONFIGURED, WARNING, WORKING
     *        <!-- end-user-doc -->
     * @param value the new value of the '<em>Status</em>' attribute.
     * @see #getStatus()
     * @generated
     */
    @Override
    void setStatus(int value);

    /**
     * Sets the current rendering status message
     *
     * @param message the status message
     */
    @Override
    void setStatusMessage(String message);

    /**
     * @model keyType="Interaction" valueType="java.lang.Boolean"
     */
    public Map<Interaction, Boolean> getInteractionMap();

    /**
     * Returns the value of the '<em><b>Shown</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Shown</em>' attribute isn't clear, there really should be more of
     * a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Shown</em>' attribute.
     * @see #setShown(boolean)
     * @see org.locationtech.udig.project.internal.ProjectPackage#getLayer_Shown()
     * @model
     * @generated
     */
    @Override
    boolean isShown();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#isShown
     * <em>Shown</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Shown</em>' attribute.
     * @see #isShown()
     * @generated
     */
    void setShown(boolean value);

    /**
     * Set interaction applicability.
     *
     * @param interaction of the layer being considered
     * @param isApplicable true if layer is to be used with indicated interaction
     */
    public void setInteraction(Interaction interaction, boolean isApplicable);

    /**
     * Indicates this layer is capable of selectable.
     * <p>
     * A Selectable Layer can be used with the Utilities.getQuery opperation. The selection tool
     * category will maintain a separate user interface concept of applicability. The selection tool
     * will not be capabile of considering a non selectable layer applicable.
     * </p>
     * IMPORTANT FUTURE CHANGE NOTE: future design: Instead of isSelectable and isInfoable a single
     * boolean isApplicable( String toolkitID ) will be used to determine whether the layer is
     * applicable to the current tool cateogry. If the layer is not capable of Each tool category
     * extension will declare a color, which will be an underlay for the layer decorator, and an
     * optional validator class, to determine whether the capability for the layer can be set.
     * Indicates this layer is selectable.
     *
     * @return <code>true</code> if layer is selectable, <code>false</code> otherwise.
     * @deprecated use getInteraction(Interaction.SELECT)
     */
    @Deprecated
    public boolean isSelectable();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#isSelectable
     * <em>Selectable</em>}' attribute. <!-- begin-user-doc --> Used by the user to control which
     * layers are selectable, may be ignored for GeoResources that do not support editing. <!--
     * end-user-doc -->
     *
     * @param value the new value of the '<em>Selectable</em>' attribute.
     * @see #isSelectable()
     * @deprecated use setInteraction(Interaction.SELECT, value)
     */
    @Deprecated
    void setSelectable(boolean value);

    /**
     * Gets the name from the associated metadata.
     *
     * @return the name from the associated metadata
     * @uml.property name="name"
     * @model
     */
    @Override
    public String getName();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getName
     * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the layer reference information..
     *
     * @model type="org.locationtech.udig.project.internal.CatalogRef"
     */
    CatalogRef getCatalogRef();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getCatalogRef
     * <em>Catalog Ref</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Catalog Ref</em>' attribute.
     * @see #getCatalogRef()
     * @generated
     */
    void setCatalogRef(CatalogRef value);

    /**
     * Gets the id of the IGeoResource that the layer uses as its data source.
     *
     * @return the id of the layerRef
     * @uml.property name="iD"
     * @model id="true"
     */
    @Override
    public URL getID();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getID
     * <em>ID</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>ID</em>' attribute.
     * @see #getID()
     * @generated
     */
    void setID(URL value);

    /**
     * Returns whether this layer is currently visible
     *
     * @return whether this layer is currently visible
     * @uml.property name="visible"
     * @model
     */
    @Override
    public boolean isVisible();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#isVisible
     * <em>Visible</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Visible</em>' attribute.
     * @see #isVisible()
     * @generated
     */
    void setVisible(boolean value);

    /**
     * Returns the currently preferred.
     *
     * @model transient="true" changeable="true"
     */
    @Override
    public IGeoResource getGeoResource();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getGeoResource
     * <em>Geo Resource</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Geo Resource</em>' attribute.
     * @see #getGeoResource()
     * @generated
     */
    void setGeoResource(IGeoResource value);

    /**
     * Access to resources that hold data for this layer.
     *
     * @see resources() for type safe access
     * @return IGeoResources that can used to obtain layer data
     * @model transient="true" changeable="false"
     */
    @Override
    public List<IGeoResource> getGeoResources();

    /**
     * ImageDescriptor for this Layer.
     * <p>
     * Note we need to do the decorator exention on Layer to reflect status.
     *
     * @return Custom glyph - or null if none available.
     * @uml.property name="glyph"
     * @model transient="true"
     */
    @Override
    public ImageDescriptor getIcon();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getIcon
     * <em>Icon</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Icon</em>' attribute.
     * @see #getIcon()
     * @generated
     */
    void setIcon(ImageDescriptor value);

    /**
     * Query that selects all the features for the layer.
     * <p>
     * The selected flag is used with respect to {@link getFilter()}:
     * <ul>
     * <li><b>false </b>: Query for layers contents
     * <li><b>true </b>: Query for the layer's selected features
     * </ul>
     * </p>
     *
     * @param layer The layer the Query is associated with.
     * @param selection true will return a query for the selected features.
     * @return If selection if false then the features that are not selected are returned, otherwise
     *         a query that selects all the selected features is returned.
     * @model volatile="true"
     */
    @Override
    public Query getQuery(boolean selection);

    /**
     * Gets the CRS for the layer. NOTE: THIS METHOD MAY BLOCK!!!
     *
     * @param monitor may be null.
     * @return the CoordinateReferenceSystem of the layer or if the CRS cannot be determined. the
     *         current map's CRS will be returned, or if this fails the CRS will be WGS84.
     * @model
     */
    @Override
    CoordinateReferenceSystem getCRS(IProgressMonitor monitor);

    /**
     * A convenience method for getCRS(null). Logs any exceptions with the Plugin log.
     * <p>
     * This method also allows the CRS to be viewed as an attribute by EMF so ui components and
     * events can be raised. This method may block.
     * </p>
     *
     * @return the CoordinateReferenceSystem of the layer or if the CRS cannot be determined.
     * @model transient="true" changeable="true"
     */
    @Override
    CoordinateReferenceSystem getCRS();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getCRS
     * <em>CRS</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>CRS</em>' attribute.
     * @see #getCRS()
     * @generated
     */
    void setCRS(CoordinateReferenceSystem value);

    /**
     * Temporary layer properties, used for lightweight collaboration.
     * <p>
     * Note these values are not persisted, this can act as a blackboard for plugin collabaration.
     * These properties are not saved and are reset when a map is opened.
     * </p>
     * If you need long term collaboration we can set up a persistent blackboard in the same manner
     * as StyleBlackbord.
     * </p>
     * <p>
     * Note: Please don't use this to work around limitations of our object model, instead send
     * email and we can set up a long term solution.
     * </p>
     *
     * @return Blackboard used for lightweight collaboration.
     * @model changeable="false" transient="true" resolveProxies="false"
     */
    @Override
    IBlackboard getProperties();

    /**
     * Gets Containing Map.
     *
     * @return
     */
    org.locationtech.udig.project.internal.Map getMapInternal();

    /**
     * @return
     * @model
     */
    ColourScheme getColourScheme();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getColourScheme
     * <em>Colour Scheme</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Colour Scheme</em>' attribute.
     * @see #getColourScheme()
     * @generated
     */
    void setColourScheme(ColourScheme value);

    /**
     * @return
     * @uml.property name="defaultColor"
     * @model
     */
    Color getDefaultColor();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.Layer#getDefaultColor
     * <em>Default Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Default Color</em>' attribute.
     * @see #getDefaultColor()
     * @generated
     */
    void setDefaultColor(Color value);

    /**
     * Returns a list of all the FeatureEvents since the last commit.
     *
     * @return a list of all the FeatureEvents since the last commit.
     * @model transient='true' type='org.geotools.data.FeatureEvent'
     */
    List<FeatureEvent> getFeatureChanges();

    /**
     * Returns the ranges at which the layer is visible
     *
     * @return the ranges at which the layer is visible
     * @see #getMaxScaleDenominator()
     * @see #getMinScaleDenominator()
     */
    public Set<Range> getScaleRange();

    /**
     * Gets the min scale. Will never return Double.NaN or 0
     *
     * @see #getScaleRange()
     * @model
     */
    public double getMinScaleDenominator();

    /**
     * Sets the min scale. If <= 0 or Double.NaN then it indicates that the scale calculated from
     * the style or IGeoResource (such as WMS) should be returned.
     *
     * @param value the new value of the '<em>Min Scale Denominator</em>' attribute.
     * @see #getMinScaleDenominator()
     */
    public void setMinScaleDenominator(double value);

    /**
     * Gets the min scale. Will never return Double.NaN or 0
     *
     * @see #getScaleRange()
     * @model
     */
    public double getMaxScaleDenominator();

    /**
     * Sets the max scale. If <= 0 or Double.NaN then it indicates that the scale calculated from
     * the style or IGeoResource (such as WMS) should be returned.
     *
     * @param value the new value of the '<em>Max Scale Denominator</em>' attribute.
     * @see #getMaxScaleDenominator()
     */
    public void setMaxScaleDenominator(double value);

}
