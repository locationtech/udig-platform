/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2022, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.render.ViewportModelEvent.EventType;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.render.impl
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ViewportModelImpl extends EObjectImpl implements ViewportModel {
    private static final int DEFAULT_CRS = 4326; // WGS84

    /**
     * The default value of the '{@link #getCRS() <em>CRS</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see #getCRS()
     * @generated
     * @ordered
     */
    protected static final CoordinateReferenceSystem CRS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCRS() <em>CRS</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see #getCRS()
     * @generated NOT
     * @ordered
     */
    protected CoordinateReferenceSystem cRS = ViewportModelImpl.getDefaultCRS();

    /**
     * This is true if the CRS attribute has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated false
     * @ordered
     */
    protected boolean cRSESet = false;

    /**
     * The cached value of the '{@link #getBounds() <em>Bounds</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see #getBounds()
     * @generated NOT
     * @ordered
     */
    protected ReferencedEnvelope bounds = null;

    /**
     * The default value of the '{@link #getBounds() <em>Bounds</em>}' attribute.
     */
    protected static final ReferencedEnvelope BOUNDS_EDEFAULT = ViewportModel
            .getNullReferenceEnvelope();

    /**
     * The default value of the '{@link #getCenter() <em>Center</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getCenter()
     * @generated
     * @ordered
     */
    protected static final Coordinate CENTER_EDEFAULT = null;

    /**
     * The default value of the '{@link #getHeight() <em>Height</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getHeight()
     * @generated
     * @ordered
     */
    protected static final double HEIGHT_EDEFAULT = 0.0;

    /**
     * The default value of the '{@link #getWidth() <em>Width</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see #getWidth()
     * @generated
     * @ordered
     */
    protected static final double WIDTH_EDEFAULT = 0.0;

    /**
     * The default value of the '{@link #getAspectRatio() <em>Aspect Ratio</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getAspectRatio()
     * @generated
     * @ordered
     */
    protected static final double ASPECT_RATIO_EDEFAULT = 0.0;

    /**
     * The default value of the '{@link #getPixelSize() <em>Pixel Size</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getPixelSize()
     * @generated
     * @ordered
     */
    protected static final Coordinate PIXEL_SIZE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRenderManagerInternal() <em>Render Manager
     * Internal</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getRenderManagerInternal()
     * @generated
     * @ordered
     */
    protected RenderManager renderManagerInternal;

    /**
     * The cached value of the '{@link #getPreferredScaleDenominators() <em>Preferred Scale
     * Denominators</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getPreferredScaleDenominators()
     * @generated
     * @ordered
     */
    protected SortedSet<Double> preferredScaleDenominators;

    private SortedSet<Double> defaultScaleDenominators = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    protected ViewportModelImpl() {
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RenderPackage.Literals.VIEWPORT_MODEL;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public CoordinateReferenceSystem getCRS() {
        return cRS;
    }

    @Override
    public void setCRS(CoordinateReferenceSystem newCRS) {
        double scale = getScaleDenominator();
        if (newCRS == null)
            throw new IllegalArgumentException("A CRS cannot be null"); //$NON-NLS-1$
        if (newCRS.equals(cRS))
            return;
        CoordinateReferenceSystem oldCRS = getCRS();
        ReferencedEnvelope oldBounds = getBounds();
        if (oldBounds.isNull() || !validState()) {
            setCRSGen(newCRS);
        } else {
            Coordinate center = getCenter();
            DirectPosition2D position = new DirectPosition2D(oldCRS, center.x, center.y);
            try {
                eSetDeliver(false);
                cRS = newCRS;
                bounds = new ReferencedEnvelope(oldBounds, cRS);
                if (oldCRS != DefaultEngineeringCRS.GENERIC_2D
                        && oldCRS != DefaultEngineeringCRS.GENERIC_3D
                        && oldCRS != DefaultEngineeringCRS.CARTESIAN_2D
                        && oldCRS != DefaultEngineeringCRS.CARTESIAN_3D) {
                    MathTransform transform = CRS.findMathTransform(oldCRS, newCRS, true);
                    DirectPosition newCenter = transform.transform(position,
                            new DirectPosition2D());
                    setCenter(new Coordinate(newCenter.getOrdinate(0), newCenter.getOrdinate(1)));
                    setScale(scale);
                }
            } catch (FactoryException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } catch (MismatchedDimensionException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } catch (TransformException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } finally {
                eSetDeliver(true);
                // Now that the scale is correct, use setCRSGen to correctly fire events
                cRS = oldCRS;

                setCRSGen(newCRS);
                notifyListeners(new ViewportModelEvent(this, EventType.CRS, newCRS, oldCRS));
            }
        }
    }

    /**
     * @generated
     */
    public void setCRSGen(CoordinateReferenceSystem newCRS) {
        CoordinateReferenceSystem oldCRS = cRS;
        cRS = newCRS;
        boolean oldCRSESet = cRSESet;
        cRSESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RenderPackage.VIEWPORT_MODEL__CRS,
                    oldCRS, cRS, !oldCRSESet));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void unsetCRS() {
        CoordinateReferenceSystem oldCRS = cRS;
        boolean oldCRSESet = cRSESet;
        cRS = getDefaultCRS();
        cRSESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET,
                    RenderPackage.VIEWPORT_MODEL__CRS, oldCRS, getDefaultCRS(), oldCRSESet));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean isSetCRS() {
        return cRSESet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public synchronized ReferencedEnvelope getBounds() {
        ReferencedEnvelope nullReferenceEnvelope = ViewportModel.getNullReferenceEnvelope();
        if (bounds == null || nullReferenceEnvelope.equals(bounds)) {
            ReferencedEnvelope calculateExtent = calculateExtent(getMap(), getCRS());
            bounds = (calculateExtent == null ? nullReferenceEnvelope : calculateExtent);
        }

        return bounds;
    }

    @Override
    public void setBounds(ReferencedEnvelope newBounds) {
        setBounds(newBounds, false);
    }

    @Override
    public void setBounds(ReferencedEnvelope newBounds, boolean forceContainBBoxZoom) {
        setCRS(newBounds.getCoordinateReferenceSystem());
        setBoundsInternal(newBounds, forceContainBBoxZoom);
    }

    @Override
    public void setBounds(Envelope newBounds) {
        setBoundsInternal(newBounds, false);
    }

    public void setBoundsInternal(Envelope newBounds, boolean forceContainBBoxZoom) {
        Envelope finalBounds = newBounds;
        if (getDefaultPreferredScaleDenominators() != getPreferredScaleDenominators()
                && validState()) {
            IMapDisplay mapDisplay = getRenderManagerInternal().getMapDisplay();
            ReferencedEnvelope referenced;
            if (newBounds instanceof ReferencedEnvelope) {
                referenced = (ReferencedEnvelope) newBounds;
            } else {
                referenced = new ReferencedEnvelope(newBounds, getCRS());
            }
            double scale = ScaleUtils.calculateScaleDenominator(referenced,
                    mapDisplay.getDisplaySize(), mapDisplay.getDPI());
            scale = ScaleUtils.calculateClosestScale(getPreferredScaleDenominators(), scale,
                    ScaleUtils.zoomClosenessPreference());

            finalBounds = ScaleUtils.calculateBoundsFromScale(scale, mapDisplay.getDisplaySize(),
                    mapDisplay.getDPI(), referenced);
            if (forceContainBBoxZoom && !finalBounds.contains(newBounds)) {
                Iterator<Double> tail = getPreferredScaleDenominators().tailSet(scale).iterator();
                // the tail will include scale because scale is one of the elements in the set. So
                // drop that
                tail.next();
                Double nextLargest = tail.next();
                if (nextLargest != null) {
                    finalBounds = ScaleUtils.calculateBoundsFromScale(nextLargest,
                            mapDisplay.getDisplaySize(), mapDisplay.getDPI(), referenced);
                }
            }
        }

        Envelope oldBounds = getBounds();
        if (!oldBounds.isNull() && !Double.isNaN(getAspectRatio())
                && !Double.isNaN(finalBounds.getWidth())
                && !Double.isNaN(finalBounds.getHeight())) {
            double nRatio = finalBounds.getWidth() / finalBounds.getHeight();
            if (Double.isNaN(nRatio))
                nRatio = 0.0;
            double dRatio = getAspectRatio();
            if (validState() && Math.abs(nRatio - dRatio) > ACCURACY) {
                // Returning the same newBounds box is ok, but sometimes causes an infinite loop if
                // zoomToBox's calculations don't affect the size. Making this arbitrary change to
                // the
                // x-axis solves the problem.
                final double arbitraryChange = 2 * 0.0000001;
                finalBounds.init(finalBounds.getMinX() - (arbitraryChange),
                        (finalBounds.getMaxX() + arbitraryChange), finalBounds.getMinY(),
                        finalBounds.getMaxY());

                zoomToBox(finalBounds);
                return;
            }
        }
        bounds = new ReferencedEnvelope(finalBounds, getCRS());
        fireNotification(oldBounds, bounds);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Coordinate getCenter() {
        final ReferencedEnvelope currentBounds = getBounds();
        return new Coordinate(currentBounds.getMinX() + currentBounds.getWidth() / 2, currentBounds.getMinY()
                + currentBounds.getHeight() / 2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void setCenter(Coordinate newCenter) {
        final ReferencedEnvelope currentBounds = getBounds();
        final double dw = currentBounds.getWidth() / 2;
        final double dh = currentBounds.getHeight() / 2;
        setBounds(new Envelope(newCenter.x - dw, newCenter.x + dw, newCenter.y - dh,
                newCenter.y + dh));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public double getHeight() {
        return getBounds().getHeight();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void setHeight(double newHeight) {
        zoom(getHeight() / newHeight);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public double getWidth() {
        return getBounds().getWidth();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void setWidth(double newWidth) {
        zoom(getWidth() / newWidth);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public double getAspectRatio() {
        if (!validState())
            return Double.NaN;
        return getRenderManagerInternal().getMapDisplay().getWidth()
                / (double) getRenderManagerInternal().getMapDisplay().getHeight();
    }

    /**
     * TODO summary sentence for validState ...
     *
     * @return validState
     */
    private boolean validState() {
        return getRenderManagerInternal() != null
                && getRenderManagerInternal().getMapDisplay() != null
                && getRenderManagerInternal().getMapDisplay().getDisplaySize() != null
                && getRenderManagerInternal().getMapDisplay().getWidth() > 0
                && getRenderManagerInternal().getMapDisplay().getHeight() > 0;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Map getMapInternal() {
        if (eContainerFeatureID() != RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL)
            return null;
        return (Map) eInternalContainer();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetMapInternal(Map newMapInternal, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject) newMapInternal,
                RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void setMapInternalGen(Map newMapInternal) {
        if (newMapInternal != eInternalContainer()
                || (eContainerFeatureID() != RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL
                        && newMapInternal != null)) {
            if (EcoreUtil.isAncestor(this, newMapInternal))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMapInternal != null)
                msgs = ((InternalEObject) newMapInternal).eInverseAdd(this,
                        ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, Map.class, msgs);
            msgs = basicSetMapInternal(newMapInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, newMapInternal, newMapInternal));
    }

    boolean viewer = false;

    // can't expect to great of accuracy since ASPECT RATIO is based on integers
    private static final double ACCURACY = 0.0001;

    private boolean initialized;

    /**
     * @return Returns the viewer.
     * @uml.property name="viewer"
     */
    public boolean isViewer() {
        return viewer;
    }

    /**
     * @param viewer The viewer to set.
     * @uml.property name="viewer"
     */
    public void setViewer(boolean viewer) {
        this.viewer = viewer;
    }

    @Override
    public void setMapInternal(Map newMap) {
        if (isViewer()) {
            eBasicSetContainer((InternalEObject) newMap,
                    RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL);
        } else
            setMapInternalGen(newMap);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public RenderManager getRenderManagerInternal() {
        return renderManagerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetRenderManagerInternal(RenderManager newRenderManagerInternal,
            NotificationChain msgs) {
        RenderManager oldRenderManagerInternal = renderManagerInternal;
        renderManagerInternal = newRenderManagerInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL, oldRenderManagerInternal,
                    newRenderManagerInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setRenderManagerInternal(RenderManager newRenderManagerInternal) {
        if (newRenderManagerInternal != renderManagerInternal) {
            NotificationChain msgs = null;
            if (renderManagerInternal != null)
                msgs = ((InternalEObject) renderManagerInternal).eInverseRemove(this,
                        RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL, RenderManager.class,
                        msgs);
            if (newRenderManagerInternal != null)
                msgs = ((InternalEObject) newRenderManagerInternal).eInverseAdd(this,
                        RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL, RenderManager.class,
                        msgs);
            msgs = basicSetRenderManagerInternal(newRenderManagerInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL, newRenderManagerInternal,
                    newRenderManagerInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Coordinate getPixelSize() {
        return new Coordinate(getXPixelToWorldScale(), getYPixelToWorldScale());
    }

    private double getXPixelToWorldScale() {
        if (!validState())
            return Double.NaN;
        return getWidth() / getRenderManagerInternal().getMapDisplay().getWidth();
    }

    private double getYPixelToWorldScale() {
        if (!validState())
            return Double.NaN;
        return getHeight() / getRenderManagerInternal().getMapDisplay().getHeight();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void setBounds(double minx, double maxx, double miny, double maxy) {
        setBounds(new Envelope(minx, maxx, miny, maxy));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public AffineTransform worldToScreenTransform() {
        if (!validState())
            return new AffineTransform(); // Identity (screen-space is arbitrary here.)
        // set up the affine transform and calculate scale values
        return worldToScreenTransform(getBounds(),
                getRenderManagerInternal().getMapDisplay().getDisplaySize());
    }

    @Override
    public AffineTransform worldToScreenTransform(Envelope mapExtent, Dimension screenSize) {
        if (!validState())
            return null;

        return ScaleUtils.worldToScreenTransform(mapExtent, screenSize);
    }

    @Override
    public Point worldToPixel(Coordinate coord) {
        if (!validState())
            return null;
        return ScaleUtils.worldToPixel(coord, getBounds(),
                getRenderManagerInternal().getMapDisplay().getDisplaySize());
    }

    @Override
    public Coordinate pixelToWorld(int x, int y) {
        if (!validState())
            return null;

        return ScaleUtils.pixelToWorld(x, y, getBounds(),
                getRenderManagerInternal().getMapDisplay().getDisplaySize());
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public ViewportModel panUsingScreenCoords(int xpixels, int ypixels) {
        if (!validState())
            return this;
        panUsingWorldCoords(xpixels * getXPixelToWorldScale(), -ypixels * getYPixelToWorldScale());
        return this;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public ViewportModel panUsingWorldCoords(double x, double y) {
        Envelope bounds = getBounds();
        setBounds(bounds.getMinX() + x, bounds.getMaxX() + x, bounds.getMinY() + y,
                bounds.getMaxY() + y);
        return this;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public ViewportModel zoom(double zoom) {
        Coordinate center = getCenter();
        zoom(zoom, center);
        return this;
    }

    @Override
    public ViewportModel zoom(double zoom, Coordinate fixedPoint) {
        if (fixedPoint == null) {
            fixedPoint = getCenter();
        }
        double effectiveZoom = zoom;
        AffineTransform transformer = ScaleUtils.createScaleTransformWithFixedPoint(effectiveZoom,
                fixedPoint);
        Envelope transformedEnvelope = ScaleUtils.transformEnvelope(getBounds(), transformer);
        setBounds(transformedEnvelope);
        return this;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void zoomToExtent() {
        try {
            if (!validState())
                return;

            ReferencedEnvelope bounds2 = calculateExtent(getMap(), getCRS());

            if (bounds2.getCoordinateReferenceSystem() == null || getCRS() == null
                    || bounds2.getCoordinateReferenceSystem().equals(getCRS())) {
                zoomToBox(bounds2);
            } else {
                MathTransform transform = null;
                transform = CRS.findMathTransform(bounds2.getCoordinateReferenceSystem(), getCRS(),
                        true);
                zoomToBox(JTS.transform(bounds2, transform));
            }

        } catch (FactoryException | TransformException e) {
            zoomToBox(new Envelope(-180, 180, -90, 90));
        }
    }

    private ReferencedEnvelope calculateExtent(final IMap map,
            final CoordinateReferenceSystem crs) {
        ReferencedEnvelope calculatedBounds = new ReferencedEnvelope(crs);

        // check the limit service
        IAOIService aOIService = PlatformGIS.getAOIService();
        ReferencedEnvelope extent = aOIService.getExtent();

        if (extent != null && !extent.isNull() && !extent.isEmpty()) {
            calculatedBounds = new ReferencedEnvelope(extent);
        } else {
            IProgressMonitor progressMonitor = ProgressManager.instance().get();
            boolean hasVisibleLayer = false;
            // search the map for visible layers and construct a bounds from those layers.
            // otherwise default to what the map's extent is.
            List<ILayer> layers = (map == null ? Collections.emptyList() : map.getMapLayers());
            for (ILayer layer : layers) {
                ReferencedEnvelope layerBounds = layer.getBounds(progressMonitor, crs);
                if (layer.isVisible() && !layerBounds.isNull()) {
                    hasVisibleLayer = true;
                    // consider zooming in (or zooming out) to a scale the layer is visible
                    ReferencedEnvelope fitted = ScaleUtils.fitToMinAndMax(layerBounds, layer);
                    if (fitted.getCoordinateReferenceSystem() == calculatedBounds
                            .getCoordinateReferenceSystem()) {
                        calculatedBounds.expandToInclude(fitted);
                    } else if (calculatedBounds.getCoordinateReferenceSystem() == layerBounds
                            .getCoordinateReferenceSystem()) {
                        // We have a small problem here? Should do the fitting
                        // before transform to viewport CRS

                        // use layerBounds which should match
                        calculatedBounds.expandToInclude(layerBounds);
                    } else {
                        // ignore as it probably does not have a CRS
                    }
                }
            }
            if (!hasVisibleLayer && map != null) {
                calculatedBounds = map.getBounds(progressMonitor);
            }
        }
        return calculatedBounds;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (cRS: "); //$NON-NLS-1$
        if (cRSESet)
            result.append(cRS);
        else
            result.append("<unset>"); //$NON-NLS-1$
        result.append(", bounds: "); //$NON-NLS-1$
        result.append(bounds);
        result.append(", preferredScaleDenominators: "); //$NON-NLS-1$
        result.append(preferredScaleDenominators);
        result.append(')');
        return result.toString();
    }

    @Override
    public void sizeChanged(final MapDisplayEvent event) {
        if (event.getSize().width < 1 || event.getSize().height < 1)
            return;

        Runnable handler = new Runnable() {
            @Override
            public void run() {
                Envelope oldBounds = getBounds();

                if (newSizeIsSmaller(event)) {
                    calculateNewBounds(event, oldBounds);
                    return;
                }

                if (oldBounds.isNull()) {
                    zoomToExtent();
                } else {
                    if (oldSizeIsValid(event)) {
                        bounds = calculateNewBounds(event, oldBounds);
                        fireNotification(oldBounds, bounds);
                    } else {
                        zoomToBox(getBounds());
                    }
                }
            }
        };

        if (Display.getCurrent() != null) {
            Thread thread = new Thread(handler);
            thread.setDaemon(true);
            thread.start();
        } else {
            handler.run();
        }
    }

    private void fireNotification(Envelope oldBounds, Envelope newBounds) {
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__BOUNDS, oldBounds, newBounds));

            notifyListeners(new ViewportModelEvent(this, EventType.BOUNDS, newBounds, oldBounds));
        }
    }

    private ReferencedEnvelope calculateNewBounds(MapDisplayEvent event, Envelope oldBounds) {
        double oldXscale = getWidth() / event.getOldSize().width;
        double oldYscale = getHeight() / event.getOldSize().height;
        double minx = oldBounds.getMinX();
        double maxy = oldBounds.getMaxY();
        double maxx = minx + (event.getSize().width * oldXscale);
        double miny = maxy - (event.getSize().height * oldYscale);
        return new ReferencedEnvelope(minx, maxx, miny, maxy, getCRS());
    }

    private boolean oldSizeIsValid(MapDisplayEvent event) {
        return event.getOldSize() != null && event.getOldSize().width != 0
                && event.getOldSize().height != 0;
    }

    private boolean newSizeIsSmaller(MapDisplayEvent event) {
        if (event.getOldSize() == null)
            return false;
        return event.getOldSize().width > event.getSize().width
                && event.getOldSize().height > event.getSize().height;
    }

    @Override
    public void zoomToBox(Envelope newbbox) {
        setInitialized(true);
        if (Math.abs(newbbox.getWidth() / newbbox.getHeight() - this.getAspectRatio()) > ACCURACY) {
            IMapDisplay display = this.getRenderManagerInternal().getMapDisplay();
            double scaley = newbbox.getHeight() / display.getHeight();
            double scalex = newbbox.getWidth() / display.getWidth();
            double scale;
            if (scalex > scaley)
                scale = scalex;
            else
                scale = scaley;

            double height = display.getHeight() * scale;
            double width = display.getWidth() * scale;

            double dw = width / 2, dh = height / 2;
            Envelope mapBounds = newbbox;
            double x = (mapBounds.getMaxX() + mapBounds.getMinX()) / 2;
            double y = (mapBounds.getMaxY() + mapBounds.getMinY()) / 2;
            newbbox.init(x - dw, x + dw, y - dh, y + dh);
        }
        this.setBounds(newbbox);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            return basicSetMapInternal((Map) otherEnd, msgs);
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            if (renderManagerInternal != null)
                msgs = ((InternalEObject) renderManagerInternal).eInverseRemove(this,
                        RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL, RenderManager.class,
                        msgs);
            return basicSetRenderManagerInternal((RenderManager) otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return basicSetMapInternal(null, msgs);
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            return basicSetRenderManagerInternal(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return eInternalContainer().eInverseRemove(this,
                    ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, Map.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            return getCRS();
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            return getBounds();
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            return getCenter();
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            return getHeight();
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            return getWidth();
        case RenderPackage.VIEWPORT_MODEL__ASPECT_RATIO:
            return getAspectRatio();
        case RenderPackage.VIEWPORT_MODEL__PIXEL_SIZE:
            return getPixelSize();
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return getMapInternal();
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            return getRenderManagerInternal();
        case RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS:
            return getPreferredScaleDenominators();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            setCRS((CoordinateReferenceSystem) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            setBounds((ReferencedEnvelope) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            setCenter((Coordinate) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            setHeight((Double) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            setWidth((Double) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            setMapInternal((Map) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            setRenderManagerInternal((RenderManager) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS:
            setPreferredScaleDenominators((SortedSet<Double>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            unsetCRS();
            return;
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            setBounds(BOUNDS_EDEFAULT);
            return;
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            setCenter(CENTER_EDEFAULT);
            return;
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            setHeight(HEIGHT_EDEFAULT);
            return;
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            setWidth(WIDTH_EDEFAULT);
            return;
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            setMapInternal((Map) null);
            return;
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            setRenderManagerInternal((RenderManager) null);
            return;
        case RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS:
            setPreferredScaleDenominators((SortedSet<Double>) null);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            return isSetCRS();
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            return BOUNDS_EDEFAULT == null ? bounds != null : !BOUNDS_EDEFAULT.equals(bounds);
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            return CENTER_EDEFAULT == null ? getCenter() != null
                    : !CENTER_EDEFAULT.equals(getCenter());
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            return getHeight() != HEIGHT_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            return getWidth() != WIDTH_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__ASPECT_RATIO:
            return getAspectRatio() != ASPECT_RATIO_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__PIXEL_SIZE:
            return PIXEL_SIZE_EDEFAULT == null ? getPixelSize() != null
                    : !PIXEL_SIZE_EDEFAULT.equals(getPixelSize());
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return getMapInternal() != null;
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            return renderManagerInternal != null;
        case RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS:
            return preferredScaleDenominators != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * @returns the system wide default CRS
     */
    public static CoordinateReferenceSystem getDefaultCRS() {
        try {
            IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
            int epsgCode = store.getInt(PreferenceConstants.P_DEFAULT_CRS);
            if (epsgCode == -1) {
                epsgCode = DEFAULT_CRS;
            }
            return CRS.decode("EPSG:" + epsgCode); //$NON-NLS-1$
        } catch (FactoryException e) {
            return ViewportModel.BAD_DEFAULT;
        }
    }

    /**
     * FIXME, This method was added as a work around for the fact that we do not have the ability to
     * chain or batch commands.
     *
     * @param firing
     */
    public void setFiringEvents(boolean firing) {
        if (firing) {
            eFlags |= EDELIVER;
        } else {
            eFlags &= ~EDELIVER;

        }
    }

    /**
     * @see org.locationtech.udig.project.internal.render.ViewportModel#isInitialized()
     * @uml.property name="initialized"
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized The initialized to set.
     * @uml.property name="initialized"
     */
    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public IMap getMap() {
        return getMapInternal();
    }

    @Override
    public double getScaleDenominator() {
        if (!validState()) {
            return -1;
        }
        RenderManager renderManager = getRenderManagerInternal();
        if (renderManager == null || renderManager.getMapDisplay() == null) {
            return -1;
        }

        IMapDisplay display = renderManager.getMapDisplay();
        return ScaleUtils.calculateScaleDenominator(getBounds(), display.getDisplaySize(),
                display.getDPI());

    }

    @Override
    public synchronized SortedSet<Double> getDefaultPreferredScaleDenominators() {
        if (defaultScaleDenominators == null) {
            TreeSet<Double> scales = new TreeSet<>();
            scales.add(1000000.0);
            scales.add(100000.0);
            scales.add(50000.0);
            scales.add(20000.0);
            scales.add(10000.0);
            scales.add(5000.0);
            scales.add(2500.0);
            scales.add(1000.0);
            defaultScaleDenominators = Collections.unmodifiableSortedSet(scales);
        }

        return defaultScaleDenominators;
    }

    @Override
    public SortedSet<Double> getPreferredScaleDenominators() {
        if (preferredScaleDenominators == null) {
            return getDefaultPreferredScaleDenominators();
        }
        return preferredScaleDenominators;
    }

    @Override
    public void setPreferredScaleDenominators(SortedSet<Double> newPreferredScaleDenominators) {
        SortedSet<Double> oldPreferredScaleDenominators = preferredScaleDenominators;
        if (newPreferredScaleDenominators == getDefaultPreferredScaleDenominators()) {
            preferredScaleDenominators = null;
        } else {
            preferredScaleDenominators = Collections
                    .unmodifiableSortedSet(new TreeSet<>(newPreferredScaleDenominators));
        }
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS,
                    oldPreferredScaleDenominators, preferredScaleDenominators));
    }

    /**
     * This method will calculate the current width based on ScaleUtils and the current
     * RenderManager.
     */
    @Override
    public void setScale(double scaleDenominator) {
        RenderManager rm = getRenderManagerInternal();
        IMapDisplay display = rm.getMapDisplay();
        ReferencedEnvelope newExtents = ScaleUtils.calculateBoundsFromScale(scaleDenominator,
                display.getDisplaySize(), display.getDPI(), getBounds());
        setWidth(newExtents.getWidth());
    }

    /**
     * Calculates new map bounds according to the given scale, DPI, and display dimensions
     */
    @Override
    public void setScale(double scaleDenominator, int dpi, int displayWidth, int displayHeight) {

        ReferencedEnvelope newExtents = ScaleUtils.calculateBoundsFromScale(scaleDenominator,
                new Dimension(displayWidth, displayHeight), dpi, getBounds());
        setWidth(newExtents.getWidth());
    }

    CopyOnWriteArraySet<IViewportModelListener> listeners = new CopyOnWriteArraySet<>();

    @Override
    public void addViewportModelListener(IViewportModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeViewportModelListener(IViewportModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(ViewportModelEvent event) {
        for (IViewportModelListener listener : listeners) {
            try {
                listener.changed(event);
            } catch (Throwable t) {
                ProjectPlugin.log("", t); //$NON-NLS-1$
            }
        }
    }

    private boolean boundsChanging = false;

    @Override
    public void setIsBoundsChanging(boolean changing) {
        this.boundsChanging = changing;
    }

    @Override
    public boolean isBoundsChanging() {
        return this.boundsChanging;
    }
}
