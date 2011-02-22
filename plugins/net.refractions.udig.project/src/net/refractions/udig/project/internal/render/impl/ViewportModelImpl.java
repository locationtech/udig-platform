/**
 * <copyright></copyright> $Id: ViewportModelImpl.java 30936 2008-10-29 12:21:56Z jeichar $
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.render.ViewportModelEvent.EventType;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.render.displayAdapter.MapDisplayEvent;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.spatialschema.geometry.DirectPosition;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of net.refractions.udig.project.internal.render.impl
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ViewportModelImpl extends EObjectImpl implements ViewportModel {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getCRS() <em>CRS</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see #getCRS()
     * @generated NOT
     * @ordered
     */
    protected CoordinateReferenceSystem cRS = DEFAULT_CRS;

    /**
     * This is true if the CRS attribute has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated false
     * @ordered
     */
    protected boolean cRSESet = false;

    /**
     * The default value of the '{@link #getBounds() <em>Bounds</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getBounds()
     * @generated NOT
     * @ordered
     */
    protected static final ReferencedEnvelope BOUNDS_EDEFAULT = NIL_BBOX; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getBounds() <em>Bounds</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getBounds()
     * @generated NOT
     * @ordered
     */
    protected ReferencedEnvelope bounds = BOUNDS_EDEFAULT;

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
     * The default value of the '{@link #getWidth() <em>Width</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
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
     * The cached value of the '{@link #getRenderManagerInternal() <em>Render Manager Internal</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getRenderManagerInternal()
     * @generated
     * @ordered
     */
    protected RenderManager renderManagerInternal = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    protected ViewportModelImpl() {
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected EClass eStaticClass() {
        return RenderPackage.eINSTANCE.getViewportModel();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public CoordinateReferenceSystem getCRS() {
        return cRS;
    }

    public void setCRS( CoordinateReferenceSystem newCRS ) {
        double scale = getScaleDenominator();
        if (newCRS == null)
            throw new IllegalArgumentException("A CRS cannot be null"); //$NON-NLS-1$
        if (newCRS.equals(cRS))
            return;
        CoordinateReferenceSystem oldCRS = getCRS();
        if (getBounds().isNull() || !validState() ) {
            setCRSGen(newCRS);

        } else {
            Coordinate center = getCenter();
            DirectPosition2D position = new DirectPosition2D(oldCRS, center.x, center.y);
            try {
                eSetDeliver(false);
                cRS = newCRS;
                bounds = new ReferencedEnvelope(bounds, cRS);
                if (oldCRS != DefaultEngineeringCRS.GENERIC_2D
                        && oldCRS != DefaultEngineeringCRS.GENERIC_3D
                        && oldCRS != DefaultEngineeringCRS.CARTESIAN_2D
                        && oldCRS != DefaultEngineeringCRS.CARTESIAN_3D) {
                    MathTransform transform = CRS.findMathTransform(oldCRS, newCRS, true);
                    DirectPosition newCenter = transform.transform(position, new DirectPosition2D());
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
    public void setCRSGen( CoordinateReferenceSystem newCRS ) {
        CoordinateReferenceSystem oldCRS = cRS;
        cRS = newCRS;
        boolean oldCRSESet = cRSESet;
        cRSESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__CRS, oldCRS, cRS, !oldCRSESet));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
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
    public boolean isSetCRS() {
        return cRSESet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public ReferencedEnvelope getBounds() {
        if (bounds == null) {
            return getMapInternal().getBounds(ProgressManager.instance().get());
        }

        return bounds;
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setBounds(ReferencedEnvelope newBounds) {
		setCRS(newBounds.getCoordinateReferenceSystem());
		setBounds((Envelope)newBounds);
	}

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="bounds"
     * @generated NOT
     */
    public void setBounds( Envelope newBounds ) {
        Envelope oldBounds = bounds == null ? new Envelope() : bounds;
        if (!getBounds().isNull() && !Double.isNaN(getAspectRatio())
                && !Double.isNaN(newBounds.getWidth()) && !Double.isNaN(newBounds.getHeight())) {
            double nRatio = newBounds.getWidth() / newBounds.getHeight();
            if (Double.isNaN(nRatio))
                nRatio = 0.0;
            double dRatio = getAspectRatio();
            if (Math.abs(nRatio - dRatio) > ACCURACY) {
                // Returning the same newBounds box is ok, but sometimes causes an infinite loop if
                // zoomToBox's calculations don't affect the size. Making this arbitrary change to
                // the
                // x-axis solves the problem.
                newBounds.init(newBounds.getMinX() - 2 * ACCURACY, newBounds.getMaxX() + 2
                        * ACCURACY, newBounds.getMinY(), newBounds.getMaxY());

                zoomToBox(newBounds);
                return;
            }
        }
        bounds = new ReferencedEnvelope(newBounds,bounds.getCoordinateReferenceSystem());
        fireNotification(oldBounds);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Coordinate getCenter() {
        return new Coordinate(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY()
                + bounds.getHeight() / 2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void setCenter( Coordinate newCenter ) {
        // Coordinate center=getCenter();
        double dw = getBounds().getWidth() / 2, dh = getBounds().getHeight() / 2;
        setBounds(new Envelope(newCenter.x - dw, newCenter.x + dw, newCenter.y - dh, newCenter.y
                + dh));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public double getHeight() {
        return bounds.getHeight();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void setHeight( double newHeight ) {
        zoom(getHeight() / newHeight);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public double getWidth() {
        return getBounds().getWidth();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void setWidth( double newWidth ) {
        zoom(getWidth() / newWidth);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
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
    public Map getMapInternal() {
        if (eContainerFeatureID != RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL)
            return null;
        return (Map) eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void setMapInternalGen( Map newMapInternal ) {
        if (newMapInternal != eContainer
                || (eContainerFeatureID != RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL && newMapInternal != null)) {
            if (EcoreUtil.isAncestor(this, (EObject) newMapInternal))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMapInternal != null)
                msgs = ((InternalEObject) newMapInternal).eInverseAdd(this,
                        ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, Map.class, msgs);
            msgs = eBasicSetContainer((InternalEObject) newMapInternal,
                    RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, newMapInternal, newMapInternal));
    }

    boolean viewer = false;

    private static final double ACCURACY = 0.0000001;

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
    public void setViewer( boolean viewer ) {
        this.viewer = viewer;
    }

    /**
     * @see net.refractions.udig.project.internal.render.RenderManager#setMap(IMap)
     */
    public void setMapInternal( Map newMap ) {
        if (isViewer()) {
            eBasicSetContainer((InternalEObject) newMap, RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL);
        } else
            setMapInternalGen(newMap);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public RenderManager getRenderManagerInternal() {
        return renderManagerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain basicSetRenderManagerInternal( RenderManager newRenderManagerInternal,
            NotificationChain msgs ) {
        RenderManager oldRenderManagerInternal = renderManagerInternal;
        renderManagerInternal = newRenderManagerInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL,
                    oldRenderManagerInternal, newRenderManagerInternal);
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
    public void setRenderManagerInternal( RenderManager newRenderManagerInternal ) {
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
                    RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL,
                    newRenderManagerInternal, newRenderManagerInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
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
    public void setBounds( double minx, double maxx, double miny, double maxy ) {
        setBounds(new Envelope(minx, maxx, miny, maxy));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public AffineTransform worldToScreenTransform() {
        if (!validState())
            return null;
        // set up the affine transform and calculate scale values
        return worldToScreenTransform(getBounds(), getRenderManagerInternal().getMapDisplay()
                .getDisplaySize());
    }

    /**
     * @see ViewportModel#worldToScreenTransform(Envelope, Dimension)
     */
    public AffineTransform worldToScreenTransform( Envelope mapExtent, Dimension screenSize ) {
        if (!validState())
            return null;

        return ScaleUtils.worldToScreenTransform(mapExtent, screenSize);
    }

    public Point worldToPixel( Coordinate coord ) {
        if (!validState())
            return null;
        return ScaleUtils.worldToPixel(coord, getBounds(), getRenderManagerInternal()
                .getMapDisplay().getDisplaySize());
    }

    public Coordinate pixelToWorld( int x, int y ) {
        if (!validState())
            return null;

        return ScaleUtils.pixelToWorld(x, y, getBounds(), getRenderManagerInternal()
                .getMapDisplay().getDisplaySize());
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public ViewportModel panUsingScreenCoords( int xpixels, int ypixels ) {
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
    public ViewportModel panUsingWorldCoords( double x, double y ) {
        Envelope bounds = getBounds();
        setBounds(bounds.getMinX() + x, bounds.getMaxX() + x, bounds.getMinY() + y, bounds
                .getMaxY()
                + y);
        return this;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public ViewportModel zoom( double zoom ) {
        Coordinate center = getCenter();
        double height = getHeight() / zoom, width = getWidth() / zoom;
        double dh = height / 2, dw = width / 2;
        setBounds(center.x - dw, center.x + dw, center.y - dh, center.y + dh);
        return this;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void zoomToExtent() {
        try {
            if (!validState())
                return;

            ReferencedEnvelope bounds2 = new ReferencedEnvelope(getCRS());
            boolean hasVisibleLayer = false;
            // search the map for visible layers and construct a bounds from those layers.
            // otherwise default to what the map's extent is.
            List<ILayer> layers = getMap().getMapLayers();
            for( ILayer layer : layers ) {
                ReferencedEnvelope layerBounds = layer.getBounds(ProgressManager.instance().get(),
                        getCRS());
                if (layer.isVisible() && !layerBounds.isNull()) {
                    hasVisibleLayer = true;
                    bounds2.expandToInclude(ScaleUtils.fitToMinAndMax(layerBounds, layer));
                }
            }

            if (!hasVisibleLayer) {
                bounds2 = getMap().getBounds(ProgressManager.instance().get());
            }

            if (bounds2.getCoordinateReferenceSystem() == null || getCRS() == null
                    || bounds2.getCoordinateReferenceSystem().equals(getCRS())) {
                zoomToBox(bounds2);
            } else {
                MathTransform transform = null;
                transform = CRS.findMathTransform(bounds2.getCoordinateReferenceSystem(), getCRS(),
                        true);
                zoomToBox(JTS.transform(bounds2, transform));
            }

        } catch (FactoryException e) {
            zoomToBox(new Envelope(-180, 180, -90, 90));
        } catch (TransformException e) {
            zoomToBox(new Envelope(-180, 180, -90, 90));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID, Class baseClass,
            NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
                if (eContainer != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL,
                        msgs);
            case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
                if (renderManagerInternal != null)
                    msgs = ((InternalEObject) renderManagerInternal).eInverseRemove(this,
                            RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL,
                            RenderManager.class, msgs);
                return basicSetRenderManagerInternal((RenderManager) otherEnd, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            Class baseClass, NotificationChain msgs ) {
        if (featureID >= 0) {
            switch( eDerivedStructuralFeatureID(featureID, baseClass) ) {
            case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
                return eBasicSetContainer(null, RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, msgs);
            case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
                return basicSetRenderManagerInternal(null, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch( eContainerFeatureID ) {
            case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
                return eContainer.eInverseRemove(this, ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL,
                        Map.class, msgs);
            default:
                return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null,
                msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            return getCRS();
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            return getBounds();
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            return getCenter();
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            return new Double(getHeight());
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            return new Double(getWidth());
        case RenderPackage.VIEWPORT_MODEL__ASPECT_RATIO:
            return new Double(getAspectRatio());
        case RenderPackage.VIEWPORT_MODEL__PIXEL_SIZE:
            return getPixelSize();
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return getMapInternal();
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            return getRenderManagerInternal();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            setCRS((CoordinateReferenceSystem) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            setBounds((Envelope) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            setCenter((Coordinate) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            setHeight(((Double) newValue).doubleValue());
            return;
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            setWidth(((Double) newValue).doubleValue());
            return;
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            setMapInternal((Map) newValue);
            return;
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            setRenderManagerInternal((RenderManager) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
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
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.VIEWPORT_MODEL__CRS:
            return isSetCRS();
        case RenderPackage.VIEWPORT_MODEL__BOUNDS:
            return BOUNDS_EDEFAULT == null ? bounds != null : !BOUNDS_EDEFAULT.equals(bounds);
        case RenderPackage.VIEWPORT_MODEL__CENTER:
            return CENTER_EDEFAULT == null ? getCenter() != null : !CENTER_EDEFAULT
                    .equals(getCenter());
        case RenderPackage.VIEWPORT_MODEL__HEIGHT:
            return getHeight() != HEIGHT_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__WIDTH:
            return getWidth() != WIDTH_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__ASPECT_RATIO:
            return getAspectRatio() != ASPECT_RATIO_EDEFAULT;
        case RenderPackage.VIEWPORT_MODEL__PIXEL_SIZE:
            return PIXEL_SIZE_EDEFAULT == null ? getPixelSize() != null : !PIXEL_SIZE_EDEFAULT
                    .equals(getPixelSize());
        case RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL:
            return getMapInternal() != null;
        case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL:
            return renderManagerInternal != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (cRS: "); //$NON-NLS-1$
        if (cRSESet)
            result.append(cRS);
        else
            result.append("<unset>"); //$NON-NLS-1$
        result.append(", bounds: "); //$NON-NLS-1$
        result.append(bounds);
        result.append(')');
        return result.toString();
    }

    /**
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplayListener#sizeChanged(net.refractions.udig.project.render.displayAdapter.MapDisplayEvent)
     */
    public void sizeChanged( MapDisplayEvent event ) {
        if (event.getSize().width < 1 || event.getSize().height < 1)
            return;

        Envelope oldBounds = getBounds();

        if (newSizeIsSmaller(event)) {
            calculateNewBounds(event, oldBounds);
            return;
        }

        if (oldBounds.isNull()) {
            zoomToExtent();
        } else {
            if (oldSizeIsValid(event)) {
                calculateNewBounds(event, oldBounds);
                fireNotification(oldBounds);
            } else {
                zoomToBox(getBounds());
            }
        }
    }

    private void fireNotification( Envelope oldBounds ) {
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.VIEWPORT_MODEL__BOUNDS, oldBounds, bounds));

            notifyListeners(new ViewportModelEvent(this, EventType.BOUNDS, bounds, oldBounds));
        }
    }

    private void calculateNewBounds( MapDisplayEvent event, Envelope oldBounds ) {
        double oldXscale = getWidth() / event.getOldSize().width;
        double oldYscale = getHeight() / event.getOldSize().height;
        double minx = oldBounds.getMinX();
        double maxy = oldBounds.getMaxY();
        double maxx = minx + (event.getSize().width * oldXscale);
        double miny = maxy - (event.getSize().height * oldYscale);
        this.bounds = new ReferencedEnvelope(minx, maxx, miny, maxy, getCRS());
    }

    private boolean oldSizeIsValid( MapDisplayEvent event ) {
        return event.getOldSize() != null && event.getOldSize().width != 0
                && event.getOldSize().height != 0;
    }

    private boolean newSizeIsSmaller( MapDisplayEvent event ) {
        if (event.getOldSize() == null)
            return false;
        return event.getOldSize().width > event.getSize().width
                && event.getOldSize().height > event.getSize().height;
    }

    /**
     * @see net.refractions.udig.project.internal.render.ViewportModel#zoomToBox(com.vividsolutions.jts.geom.Envelope)
     */
    public void zoomToBox( Envelope newbbox ) {
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
            // newbbox.init(validateEnvelope(newbbox));
        }
        this.setBounds(newbbox);
    }

    /**
     * Returns the system wide default CRS
     */
    public static CoordinateReferenceSystem getDefaultCRS() {
        try {
            IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
            int i = store.getInt(PreferenceConstants.P_DEFAULT_CRS);
            if (i == -1)
                return CRS.decode("EPSG:4326");//$NON-NLS-1$
            return CRS.decode("EPSG:" + i); //$NON-NLS-1$
        } catch (NoSuchAuthorityCodeException e) {
            return ViewportModel.BAD_DEFAULT;
        }
    }

    /**
     * FIXME, This method was added as a work around for the fact that we do not have the ability to
     * chain or batch commands.
     *
     * @param firing
     */
    public void setFiringEvents( boolean firing ) {
        if (firing) {
            eFlags |= EDELIVER;
        } else {
            eFlags &= ~EDELIVER;

        }
    }

    /**
     * @see net.refractions.udig.project.internal.render.ViewportModel#isInitialized()
     * @uml.property name="initialized"
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized The initialized to set.
     * @uml.property name="initialized"
     */
    public void setInitialized( boolean initialized ) {
        this.initialized = initialized;
    }

    /**
     * @see net.refractions.udig.project.render.IViewportModel#getMap()
     */
    public IMap getMap() {
        return getMapInternal();
    }

    public double getScaleDenominator() {
        if (!validState()) {
            return -1;
        }
        RenderManager renderManager = getRenderManagerInternal();
        ReferencedEnvelope bounds2 = getBounds();
        if (renderManager == null || renderManager.getMapDisplay() == null)
            return -1;

        IMapDisplay display = renderManager.getMapDisplay();
        return ScaleUtils.calculateScaleDenominator(bounds2, display.getDisplaySize(), display
                .getDPI());

    }

    public void setScale( double scaleDenominator ) {
        IMapDisplay display = getRenderManagerInternal().getMapDisplay();
        ReferencedEnvelope newExtents = ScaleUtils.calculateBoundsFromScale(scaleDenominator,
                display.getDisplaySize(), display.getDPI(), getBounds());
        setWidth(newExtents.getWidth());
    }

    CopyOnWriteArraySet<IViewportModelListener> listeners = new CopyOnWriteArraySet<IViewportModelListener>();

    public void addViewportModelListener( IViewportModelListener listener ) {
        listeners.add(listener);
    }

    public void removeViewportModelListener( IViewportModelListener listener ) {
        listeners.remove(listener);
    }

    private void notifyListeners( ViewportModelEvent event ) {
        for( IViewportModelListener listener : listeners ) {
            try {
                listener.changed(event);
            } catch (Throwable t) {
                ProjectPlugin.log("", t); //$NON-NLS-1$
            }
        }
    }

}
