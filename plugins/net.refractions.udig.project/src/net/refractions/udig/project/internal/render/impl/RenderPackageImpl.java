/**
 * <copyright></copyright> $Id: RenderPackageImpl.java 22389 2006-10-25 22:10:46Z chorner $
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.impl.ProjectPackageImpl;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.render.IMultiLayerRenderer;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.render.displayAdapter.IMapDisplayListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.geotools.data.Query;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package </b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class RenderPackageImpl extends EPackageImpl implements RenderPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iRenderManagerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iViewportModelEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iMultiLayerRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iLayerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass multiLayerRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass renderExecutorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass renderManagerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass rendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass iMapDisplayListenerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass viewportModelEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EClass comparableEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType coordinateReferenceSystemEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType envelopeEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType iGeoResourceEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType bufferedImageEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType rectangleEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType queryEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType graphics2DEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType listEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType coordinateEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType pointEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType affineTransformEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType mapDisplayEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private EDataType iProgressMonitorEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package package URI
     * value.
     * <p>
     * Note: the correct way to create the package is via the static factory method
     * {@link #init init()}, which also performs initialization of the package, or returns the
     * registered package, if one already exists. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see net.refractions.udig.project.internal.render.RenderPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private RenderPackageImpl() {
        super(eNS_URI, RenderFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others
     * upon which it depends. Simple dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else. This method drives initialization for
     * interdependent packages directly, in parallel with this package, itself.
     * <p>
     * Of this package and its interdependencies, all packages which have not yet been registered by
     * their URI values are first created and registered. The packages are then initialized in two
     * steps: meta-model objects for all of the packages are created before any are initialized,
     * since one package's meta-model objects may refer to those of another.
     * <p>
     * Invocation of this method will not affect any packages that have already been initialized.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static RenderPackage init() {
        if (isInited)
            return (RenderPackage) EPackage.Registry.INSTANCE.getEPackage(RenderPackage.eNS_URI);

        // Obtain or create and register package
        RenderPackageImpl theRenderPackage = (RenderPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(eNS_URI) instanceof RenderPackageImpl ? EPackage.Registry.INSTANCE
                .getEPackage(eNS_URI) : new RenderPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        ProjectPackageImpl theProjectPackage = (ProjectPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI) instanceof ProjectPackageImpl
                ? EPackage.Registry.INSTANCE.getEPackage(ProjectPackage.eNS_URI)
                : ProjectPackage.eINSTANCE);

        // Create package meta-data objects
        theRenderPackage.createPackageContents();
        theProjectPackage.createPackageContents();

        // Initialize created meta-data
        theRenderPackage.initializePackageContents();
        theProjectPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theRenderPackage.freeze();

        return theRenderPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getIRenderManager() {
        return iRenderManagerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getIViewportModel() {
        return iViewportModelEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getIMultiLayerRenderer() {
        return iMultiLayerRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getIRenderer() {
        return iRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getILayer() {
        return iLayerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getMultiLayerRenderer() {
        return multiLayerRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getRenderExecutor() {
        return renderExecutorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getRenderExecutor_Renderer() {
        return (EReference) renderExecutorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getRenderManager() {
        return renderManagerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getRenderManager_RenderExecutor() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRenderManager_MapDisplay() {
        return (EAttribute) renderManagerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getRenderManager_MapInternal() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getRenderManager_ViewportModelInternal() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getRenderer() {
        return rendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRenderer_State() {
        return (EAttribute) rendererEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getRenderer_Name() {
        return (EAttribute) rendererEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getRenderer_Context() {
        return (EReference) rendererEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getIMapDisplayListener() {
        return iMapDisplayListenerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getViewportModel() {
        return viewportModelEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_CRS() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_Bounds() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_Center() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_Height() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_Width() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_AspectRatio() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EAttribute getViewportModel_PixelSize() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getViewportModel_MapInternal() {
        return (EReference) viewportModelEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EReference getViewportModel_RenderManagerInternal() {
        return (EReference) viewportModelEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EClass getComparable() {
        return comparableEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getCoordinateReferenceSystem() {
        return coordinateReferenceSystemEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getEnvelope() {
        return envelopeEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getIGeoResource() {
        return iGeoResourceEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getBufferedImage() {
        return bufferedImageEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getRectangle() {
        return rectangleEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getQuery() {
        return queryEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getGraphics2D() {
        return graphics2DEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getList() {
        return listEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getCoordinate() {
        return coordinateEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getPoint() {
        return pointEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getAffineTransform() {
        return affineTransformEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getMapDisplay() {
        return mapDisplayEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public EDataType getIProgressMonitor() {
        return iProgressMonitorEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated public EDataType getInfoList() { return infoListEDataType; }
     */

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public RenderFactory getRenderFactory() {
        return (RenderFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package. This method is guarded to have no affect on
     * any invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;

        // Create classes and their features
        iRenderManagerEClass = createEClass(IRENDER_MANAGER);

        iViewportModelEClass = createEClass(IVIEWPORT_MODEL);


        iMultiLayerRendererEClass = createEClass(IMULTI_LAYER_RENDERER);


        iRendererEClass = createEClass(IRENDERER);

        iLayerEClass = createEClass(ILAYER);


        multiLayerRendererEClass = createEClass(MULTI_LAYER_RENDERER);

        renderExecutorEClass = createEClass(RENDER_EXECUTOR);
        createEReference(renderExecutorEClass, RENDER_EXECUTOR__RENDERER);

        renderManagerEClass = createEClass(RENDER_MANAGER);
        createEReference(renderManagerEClass, RENDER_MANAGER__RENDER_EXECUTOR);
        createEAttribute(renderManagerEClass, RENDER_MANAGER__MAP_DISPLAY);
        createEReference(renderManagerEClass, RENDER_MANAGER__MAP_INTERNAL);
        createEReference(renderManagerEClass, RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL);

        viewportModelEClass = createEClass(VIEWPORT_MODEL);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__CRS);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__BOUNDS);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__CENTER);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__HEIGHT);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__WIDTH);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__ASPECT_RATIO);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__PIXEL_SIZE);
        createEReference(viewportModelEClass, VIEWPORT_MODEL__MAP_INTERNAL);
        createEReference(viewportModelEClass, VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL);

        comparableEClass = createEClass(COMPARABLE);

        rendererEClass = createEClass(RENDERER);
        createEAttribute(rendererEClass, RENDERER__STATE);
        createEAttribute(rendererEClass, RENDERER__NAME);
        createEReference(rendererEClass, RENDERER__CONTEXT);


        iMapDisplayListenerEClass = createEClass(IMAP_DISPLAY_LISTENER);

        // Create data types
        coordinateReferenceSystemEDataType = createEDataType(COORDINATE_REFERENCE_SYSTEM);
        envelopeEDataType = createEDataType(ENVELOPE);
        iGeoResourceEDataType = createEDataType(IGEO_RESOURCE);
        bufferedImageEDataType = createEDataType(BUFFERED_IMAGE);
        rectangleEDataType = createEDataType(RECTANGLE);
        queryEDataType = createEDataType(QUERY);
        graphics2DEDataType = createEDataType(GRAPHICS2_D);
        listEDataType = createEDataType(LIST);
        coordinateEDataType = createEDataType(COORDINATE);
        pointEDataType = createEDataType(POINT);
        affineTransformEDataType = createEDataType(AFFINE_TRANSFORM);
        mapDisplayEDataType = createEDataType(MAP_DISPLAY);
        iProgressMonitorEDataType = createEDataType(IPROGRESS_MONITOR);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model. This method is guarded to have
     * no affect on any invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @SuppressWarnings("unchecked")
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        ProjectPackageImpl theProjectPackage = (ProjectPackageImpl) EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI);

        // Add supertypes to classes
        multiLayerRendererEClass.getESuperTypes().add(this.getRenderer());
        multiLayerRendererEClass.getESuperTypes().add(this.getIMultiLayerRenderer());
        renderExecutorEClass.getESuperTypes().add(this.getRenderer());
        renderManagerEClass.getESuperTypes().add(this.getIRenderManager());
        viewportModelEClass.getESuperTypes().add(this.getIMapDisplayListener());
        viewportModelEClass.getESuperTypes().add(this.getIViewportModel());
        rendererEClass.getESuperTypes().add(this.getIRenderer());

        // Initialize classes and features; add operations and parameters
        initEClass(iRenderManagerEClass, IRenderManager.class,
                "IRenderManager", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(iViewportModelEClass, IViewportModel.class,
                "IViewportModel", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(iMultiLayerRendererEClass, IMultiLayerRenderer.class,
                "IMultiLayerRenderer", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(iRendererEClass, IRenderer.class,
                "IRenderer", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(iLayerEClass, ILayer.class,
                "ILayer", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$


        initEClass(multiLayerRendererEClass, MultiLayerRenderer.class,
                "MultiLayerRenderer", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(multiLayerRendererEClass, null, "refreshImage"); //$NON-NLS-1$

        initEClass(renderExecutorEClass, RenderExecutor.class,
                "RenderExecutor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(
                getRenderExecutor_Renderer(),
                this.getRenderer(),
                null,
                "renderer", null, 1, 1, RenderExecutor.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(renderManagerEClass, RenderManager.class,
                "RenderManager", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(
                getRenderManager_RenderExecutor(),
                this.getRenderExecutor(),
                null,
                "renderExecutor", null, 0, 1, RenderManager.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getRenderManager_MapDisplay(),
                this.getMapDisplay(),
                "mapDisplay", null, 0, 1, RenderManager.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(
                getRenderManager_MapInternal(),
                theProjectPackage.getMap(),
                theProjectPackage.getMap_RenderManagerInternal(),
                "mapInternal", null, 0, 1, RenderManager.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(
                getRenderManager_ViewportModelInternal(),
                this.getViewportModel(),
                this.getViewportModel_RenderManagerInternal(),
                "viewportModelInternal", null, 0, 1, RenderManager.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        EOperation op = addEOperation(renderManagerEClass, null, "refresh"); //$NON-NLS-1$
        addEParameter(op, this.getEnvelope(), "bounds"); //$NON-NLS-1$

        addEOperation(renderManagerEClass, null, "dispose"); //$NON-NLS-1$

        initEClass(viewportModelEClass, ViewportModel.class,
                "ViewportModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_CRS(),
                this.getCoordinateReferenceSystem(),
                "cRS", null, 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_Bounds(),
                this.getEnvelope(),
                "bounds", "", 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(
                getViewportModel_Center(),
                this.getCoordinate(),
                "center", null, 0, 1, ViewportModel.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_Height(),
                ecorePackage.getEDouble(),
                "height", null, 0, 1, ViewportModel.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_Width(),
                ecorePackage.getEDouble(),
                "width", null, 0, 1, ViewportModel.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_AspectRatio(),
                ecorePackage.getEDouble(),
                "aspectRatio", null, 0, 1, ViewportModel.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_PixelSize(),
                this.getCoordinate(),
                "pixelSize", null, 0, 1, ViewportModel.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(
                getViewportModel_MapInternal(),
                theProjectPackage.getMap(),
                theProjectPackage.getMap_ViewportModelInternal(),
                "mapInternal", null, 0, 1, ViewportModel.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(
                getViewportModel_RenderManagerInternal(),
                this.getRenderManager(),
                this.getRenderManager_ViewportModelInternal(),
                "renderManagerInternal", null, 0, 1, ViewportModel.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, null, "setBounds"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "minx"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "maxx"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "miny"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "maxy"); //$NON-NLS-1$

        addEOperation(viewportModelEClass, this.getAffineTransform(), "worldToScreenTransform"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getPoint(), "worldToPixel"); //$NON-NLS-1$
        addEParameter(op, this.getCoordinate(), "coord"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getCoordinate(), "pixelToWorld"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "x"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "y"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(), "panUsingScreenCoords"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "xpixels"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "ypixels"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(), "panUsingWorldCoords"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "x"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "y"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(), "setZoom"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "zoom"); //$NON-NLS-1$

        addEOperation(viewportModelEClass, null, "zoomToExtent"); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, null, "zoomToBox"); //$NON-NLS-1$
        addEParameter(op, this.getEnvelope(), "box"); //$NON-NLS-1$

        initEClass(comparableEClass, Comparable.class,
                "Comparable", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(rendererEClass, Renderer.class,
                "Renderer", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getRenderer_State(),
                ecorePackage.getEInt(),
                "state", "0", 0, 1, Renderer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(
                getRenderer_Name(),
                ecorePackage.getEString(),
                "name", null, 0, 1, Renderer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(rendererEClass, null, "render"); //$NON-NLS-1$
        addEParameter(op, this.getGraphics2D(), "destination"); //$NON-NLS-1$
        addEParameter(op, this.getIProgressMonitor(), "monitor"); //$NON-NLS-1$

        op = addEOperation(rendererEClass, null, "render"); //$NON-NLS-1$
        addEParameter(op, this.getEnvelope(), "bounds"); //$NON-NLS-1$
        addEParameter(op, this.getIProgressMonitor(), "monitor"); //$NON-NLS-1$

        addEOperation(rendererEClass, null, "dispose"); //$NON-NLS-1$

        addEParameter(op, theProjectPackage.getLayer(), "layer"); //$NON-NLS-1$

        initEClass(iMapDisplayListenerEClass, IMapDisplayListener.class,
                "IMapDisplayListener", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Initialize data types
        initEDataType(coordinateReferenceSystemEDataType, CoordinateReferenceSystem.class,
                "CoordinateReferenceSystem", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(envelopeEDataType, Envelope.class,
                "Envelope", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(iGeoResourceEDataType, IGeoResource.class,
                "IGeoResource", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(bufferedImageEDataType, BufferedImage.class,
                "BufferedImage", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(rectangleEDataType, Rectangle.class,
                "Rectangle", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(queryEDataType, Query.class,
                "Query", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(graphics2DEDataType, Graphics2D.class,
                "Graphics2D", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(listEDataType, List.class,
                "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(coordinateEDataType, Coordinate.class,
                "Coordinate", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(pointEDataType, Point.class,
                "Point", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(affineTransformEDataType, AffineTransform.class,
                "AffineTransform", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(mapDisplayEDataType, IMapDisplay.class,
                "MapDisplay", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(iProgressMonitorEDataType, IProgressMonitor.class,
                "IProgressMonitor", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} // RenderPackageImpl
