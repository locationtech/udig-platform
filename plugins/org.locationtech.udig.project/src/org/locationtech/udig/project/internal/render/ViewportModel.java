/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.SortedSet;

import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;

import org.eclipse.emf.ecore.EObject;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.joda.time.DateTime;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.render
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface ViewportModel extends EObject, IMapDisplayListener, IViewportModel {

    /**
     * The CRS used if the preferences indicate an illegal EPSG code
     */
    CoordinateReferenceSystem BAD_DEFAULT = DefaultEngineeringCRS.GENERIC_2D;

    /**
     * Returns the local coordinate system. The local coordinate system is the CRS that all the
     * layer data will be transformed into. Once the layer data is transformed into the local CRS
     * then it is transformed for display onto the screen
     * 
     * @return the local coordinate system
     * @uml.property name="cRS"
     * @model unsettable='true'
     */
    public CoordinateReferenceSystem getCRS();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCRS <em>CRS</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>CRS</em>' attribute.
     * @see #isSetCRS()
     * @see #unsetCRS()
     * @see #getCRS()
     * @generated
     */
    void setCRS(CoordinateReferenceSystem value);

    /**
     * List of preferred scale denominators for the map.
     * <p>
     * This set is used to provide good options for a user to change the scale
     * </p>
     * <p>
     * The values will always be present but if the object returned by {@link #getDefaultPreferredScaleDenominators()} and this method
     * are the same <em>instance</em> then they are simply defaults and can be ignored if desired.  However if they are not the same
     * then assume that the values are only hints and can be ignored
     * </p>
     * 
     * @see #getScaleDenominator() for a definition of scale denominator
     * @return Unmodifiable Set of preferred scale denominator values for the map
     * @model 
     */
    public SortedSet<Double> getPreferredScaleDenominators();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getPreferredScaleDenominators <em>Preferred Scale Denominators</em>}' attribute.
     * 
     * If set to null getPreferredScaleDenominators will return the defaults.
     * 
     * @param value the new value of the '<em>Preferred Scale Denominators</em>' attribute.
     * @see #getPreferredScaleDenominators()
     * @generated NOTE
     */
    void setPreferredScaleDenominators(SortedSet<Double> value);

    /**
     * Unsets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCRS <em>CRS</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #isSetCRS()
     * @see #getCRS()
     * @see #setCRS(CoordinateReferenceSystem)
     * @generated
     */
    void unsetCRS();

    /**
     * Returns whether the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCRS <em>CRS</em>}' attribute is set.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return whether the value of the '<em>CRS</em>' attribute is set.
     * @see #unsetCRS()
     * @see #getCRS()
     * @see #setCRS(CoordinateReferenceSystem)
     * @generated
     */
    boolean isSetCRS();

    /**
     * Returns the bounding box of the Viewport in world coordinates.
     * <p>
     * Note: Since Envelope is not a UDIG element changes to the bounds envelope object will not
     * raise events. Therefore the bounds should only be modified via the ViewportModel interface
     * </p>
     * 
     * @return the bounding box of the Viewport in world coordinates.
     * @uml.property name="bounds"
     * @model default=""
     */
    public ReferencedEnvelope getBounds();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getBounds <em>Bounds</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Bounds</em>' attribute.
     * @see #getBounds()
     * @generated
     */
    void setBounds(ReferencedEnvelope value);

    /**
     * Set the bounds of the viewport model
     * @param value the new desired bounds
     * @param forceContainBBoxZoom Whether or not the {@link #setBounds(ReferencedEnvelope)} will always contain the
     * 				envelope after execution.  In normal execution this is not an issue but if
     * 				{@link #setPreferredScaleDenominators(SortedSet)} has been called then this does matter because
     * 				zoom to features will not necessary contain all the features.  When taking into account
     * 				preferredScaleDenominators setting the bounds the closest scale to the desired scale is chosen.
     * 				This can mean that the scale chosen will not be able to fit the entire bounds.  This is fine
     * 				when zooming but in other cases it is not acceptable
     */
    void setBounds(ReferencedEnvelope value, boolean forceContainBBoxZoom);

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getBounds <em>Bounds</em>}'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * The bbox must have a positive width and height and must have a aspect ratio within 0.0000001
     * units of the value returned by {@linkplain #getAspectRatio()} .
     * </p>
     * <p>
     * It is recommended that setHeight() or setWidth() methods are used since they preserve the
     * aspect ratio of the Viewport
     * <p>
     * <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Bounds</em>' attribute.
     * @see #getBounds()
     * @generated NOT
     */
    void setBounds(Envelope value);

    /**
     * Sets the viewport's bounding box. The bounding box will be fit to the window based on the MapDisplay so
     * the bounds provided here may not be the final bounds.
     * 
     * @param minx the minimum x value of the new bounding box.
     * @param maxx the maximum x value of the new bounding box.
     * @param miny the minimum y value of the new bounding box.
     * @param maxy the maximum x value of the new bounding box.
     * @throws IllegalArgumentException If the above criteria are not met this exception is thrown.
     * @model
     */
    public void setBounds(double minx, double maxx, double miny, double maxy)
            throws IllegalArgumentException;

    /**
     * Returns the center of the viewport in world coordinates.
     * 
     * @return the center of the viewport in world coordinates
     * @uml.property name="center"
     * @model volatile="true" transient="true"
     */
    public Coordinate getCenter();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCenter <em>Center</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Center</em>' attribute.
     * @see #getCenter()
     * @generated
     */
    void setCenter(Coordinate value);

    /**
     * Returns the Viewport's height in world coordinates.
     * 
     * @return the Viewport's height in world coordinates.
     * @uml.property name="height"
     * @model volatile="true" transient="true"
     */
    public double getHeight();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getHeight <em>Height</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Height</em>' attribute.
     * @see #getHeight()
     * @generated
     */
    void setHeight(double value);

    /**
     * Returns the Viewport's width in world coordinates.
     * 
     * @return the Viewport's width in world coordinates.
     * @uml.property name="width"
     * @model volatile="true" transient="true"
     */
    public double getWidth();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getWidth <em>Width</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Width</em>' attribute.
     * @see #getWidth()
     * @generated
     */
    void setWidth(double value);

    /**
     * Returns the aspect ratio of the viewport.
     * 
     * @return The aspect ratio of the viewport.
     * @model volatile="true" changeable="false" transient="true"
     */
    public double getAspectRatio();

    /**
     * Gets the Map that contains the current ViewportModel
     * 
     * @return the Map that contains the current ViewportModel
     * @model opposite="viewportModelInternal" many="false"
     */
    public Map getMapInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getMapInternal <em>Map Internal</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map Internal</em>' container reference.
     * @see #getMapInternal()
     * @generated
     */
    void setMapInternal(Map value);

    /**
     * Returns the value of the '<em><b>Render Manager Internal</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.locationtech.udig.project.internal.render.RenderManager#getViewportModelInternal <em>Viewport Model Internal</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Render Manager</em>' reference isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Render Manager Internal</em>' reference.
     * @see #setRenderManagerInternal(RenderManager)
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getViewportModel_RenderManagerInternal()
     * @see org.locationtech.udig.project.internal.render.RenderManager#getViewportModelInternal
     * @model opposite="viewportModelInternal" resolveProxies="false" transient="true"
     * @generated
     */
    RenderManager getRenderManagerInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getRenderManagerInternal <em>Render Manager Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Render Manager Internal</em>' reference.
     * @see #getRenderManagerInternal()
     * @generated
     */
    void setRenderManagerInternal(RenderManager value);

    /**
     * Gets up the affine transform that will transform from the world to screen. A convenience
     * method.
     * 
     * @return a transform that maps from real world coordinates to the screen
     * @model
     */
    public AffineTransform worldToScreenTransform();

    /**
     * Returns the pixel on the screen for a given coordinate in world space.
     * 
     * @param coord A coordinate in world space.
     * @return The pixel on the screen that the world coordinate is drawn on.
     * @model
     */
    public Point worldToPixel(Coordinate coord);

    /**
     * Converts a coordinate expressed on the device space back to real world coordinates
     * 
     * @param x horizontal coordinate on device space
     * @param y vertical coordinate on device space
     * @return The correspondent real world coordinate
     * @model
     */
    public Coordinate pixelToWorld(int x, int y);

    /**
     * Returns the size of a pixel in world units.
     * <p>For example if the world is in WGS 84(lat long) then 
     * the size will be in degrees
     * </p>
     * @return the size of a pixel in world units. 
     * @model volatile="true" changeable="false" transient="true"
     */
    public Coordinate getPixelSize();

    /**
     * Pans the viewport in terms of pixels on the screen. Each pixel represents a distance in world
     * coordinates, the x and y distances differ, so a pan of 8 pixels in the x direction will be
     * translated to a pan of 8*xdistance in the world.
     * 
     * @param xpixels The amount to pan in the x direction
     * @param ypixels The amount to pan in the y direction
     * @return this ViewportModel
     * @model
     */
    public ViewportModel panUsingScreenCoords(int xpixels, int ypixels);

    /**
     * Pans the viewport in terms of world units.
     * 
     * @param x The amount to pan in the x direction
     * @param y The amount to pan in the y direction
     * @return this ViewportModel
     * @model
     */
    public ViewportModel panUsingWorldCoords(double x, double y);

    /**
     * Increases or decreases the size of the viewport(in world space) by a constant factor, zoom.
     * The zoom is equal in both directions. The function used is: bbox.height=bbox.height/divisor
     * bbox.width=bbox.width/divisor
     * <ul>
     * <li>A zoom must be greater than 1.</li>
     * <li>A zoom greater than 1 is a zoom towards the map(SimpleFeature appear larger.)</li>
     * <li>A zoom less than 1 is a zoom away from the map</li>
     * </ul>
     * 
     * @param zoom the zoom factor
     * @return This ViewportModel, allows for command chaining.
     * @model
     */
    public ViewportModel zoom(double zoom);

    /**
     * Increases or decreases the size of the viewport(in world space) by a constant factor, zoom.
     * The zoom is equal in both directions. The function used is: bbox.height=bbox.height/divisor
     * bbox.width=bbox.width/divisor
     * <ul>
     * <li>A zoom must be greater than 1.</li>
     * <li>A zoom greater than 1 is a zoom towards the map(SimpleFeature appear larger.)</li>
     * <li>A zoom less than 1 is a zoom away from the map</li>
     * </ul>
     * When get not null parameter fixedPoint then keep it fixed after zoom transform. In case of
     * null parameter center point of map will be fixed
     *
     * @param zoom the zoom factor
     * @param fixedPoint the point that will remain fixed after zoom, can be null
     * @return This ViewportModel, allows for command chaining.
     * @model
     */
    public ViewportModel zoom(double zoom, Coordinate fixedPoint);

    /**
     * sets the Viewport bounding box so that it fully contains the visible map extent
     * 
     * @model
     */
    public void zoomToExtent();

    /**
     * sets the Viewport bounding box so that it contains the new box
     * 
     * @model
     */
    public void zoomToBox(Envelope box);

    /**
     * Return true if it has been initialized with a display. If no display is active then the
     * viewportmodel is not initialized.
     * 
     * @return true if it has been initialized with a display
     * @uml.property name="initialized"
     */
    public boolean isInitialized();

    /**
     * Sets the initialized state of ViewportModel.
     * 
     * @param initialized
     * @uml.property name="initialized"
     */
    public void setInitialized(boolean initialized);

    /**
     * Sets the zoom level of the viewport so that the scale denominator will be equal to
     * scale
     *
     * @param scaleDenominator desired scale denominator 
     */
    public void setScale(double scaleDenominator);

    /**
     * Sets the zoom level of the viewport so that the scale denominator will be equal to
     * scale
     *
     * @param scaleDenominator desired scale denominator 
     */
    public void setScale(double scaleDenominator, int dpi, int displayWidth, int displayHeight);

    /**
     * The attribute indicates that any upcoming changes to the value
     * of the model should be consider a single event.
     *
     * @param changing
     */
    public void setIsBoundsChanging(boolean changing);

    /**
     * Returns the available list of timesteps or null, if no time is defined. 
     * 
     * @model
     */
    public List<DateTime> getAvailableTimesteps();

    /**
     * Returns the current timestep or null if no time has been defined. 
     * 
     * @model
     */
    public DateTime getCurrentTimestep();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCurrentTimestep <em>Current Timestep</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Current Timestep</em>' attribute.
     * @see #getCurrentTimestep()
     * @generated
     */
    void setCurrentTimestep(DateTime value);

    /**
     * Returns the available list of elevations or null if no elevation has been defined. 
     * 
     * @model
     */
    public List<Double> getAvailableElevation();

    /**
     * Returns the current elevation or null if no time has been defined. 
     * 
     * @model
     */
    public Double getCurrentElevation();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCurrentElevation <em>Current Elevation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Current Elevation</em>' attribute.
     * @see #getCurrentElevation()
     * @generated
     */
    void setCurrentElevation(Double value);
}
