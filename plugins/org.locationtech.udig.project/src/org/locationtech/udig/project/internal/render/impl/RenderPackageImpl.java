/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import java.util.SortedSet;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.element.ElementPackage;
import org.locationtech.udig.project.element.impl.ElementPackageImpl;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.impl.ProjectPackageImpl;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IMultiLayerRenderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.joda.time.DateTime;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package </b>. <!-- end-user-doc -->
 * @generated
 */
public class RenderPackageImpl extends EPackageImpl implements RenderPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iRenderManagerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iViewportModelEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iMultiLayerRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iLayerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass multiLayerRendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass renderExecutorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass renderManagerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass rendererEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iMapDisplayListenerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass iRenderContextEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass viewportModelEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass comparableEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType coordinateReferenceSystemEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType envelopeEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType iGeoResourceEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType bufferedImageEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType rectangleEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType queryEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType graphics2DEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType listEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType coordinateEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType pointEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType affineTransformEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType mapDisplayEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType iProgressMonitorEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType renderExceptionEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType sortedSetEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType referencedEnvelopeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType dateTimeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType illegalArgumentExceptionEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.locationtech.udig.project.internal.render.RenderPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private RenderPackageImpl() {
        super(eNS_URI, RenderFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link RenderPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
                .get(eNS_URI) instanceof RenderPackageImpl ? EPackage.Registry.INSTANCE
                .get(eNS_URI) : new RenderPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackage.eINSTANCE.eClass();

        // Obtain or create and register interdependencies
        ElementPackageImpl theElementPackage = (ElementPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(ElementPackage.eNS_URI) instanceof ElementPackageImpl ? EPackage.Registry.INSTANCE
                .getEPackage(ElementPackage.eNS_URI) : ElementPackage.eINSTANCE);
        ProjectPackageImpl theProjectPackage = (ProjectPackageImpl) (EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI) instanceof ProjectPackageImpl ? EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI) : ProjectPackage.eINSTANCE);

        // Create package meta-data objects
        theRenderPackage.createPackageContents();
        theElementPackage.createPackageContents();
        theProjectPackage.createPackageContents();

        // Initialize created meta-data
        theRenderPackage.initializePackageContents();
        theElementPackage.initializePackageContents();
        theProjectPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theRenderPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(RenderPackage.eNS_URI, theRenderPackage);
        return theRenderPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIRenderManager() {
        return iRenderManagerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIViewportModel() {
        return iViewportModelEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIMultiLayerRenderer() {
        return iMultiLayerRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIRenderer() {
        return iRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getILayer() {
        return iLayerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMultiLayerRenderer() {
        return multiLayerRendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getRenderExecutor() {
        return renderExecutorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRenderExecutor_Renderer() {
        return (EReference) renderExecutorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getRenderManager() {
        return renderManagerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRenderManager_RenderExecutor() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRenderManager_MapDisplay() {
        return (EAttribute) renderManagerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRenderManager_MapInternal() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRenderManager_ViewportModelInternal() {
        return (EReference) renderManagerEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getRenderer() {
        return rendererEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRenderer_State() {
        return (EAttribute) rendererEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRenderer_Name() {
        return (EAttribute) rendererEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRenderer_Context() {
        return (EReference) rendererEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIMapDisplayListener() {
        return iMapDisplayListenerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIRenderContext() {
        return iRenderContextEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getViewportModel() {
        return viewportModelEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_CRS() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_Bounds() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_Center() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_Height() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_Width() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_AspectRatio() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_PixelSize() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getViewportModel_MapInternal() {
        return (EReference) viewportModelEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getViewportModel_RenderManagerInternal() {
        return (EReference) viewportModelEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_PreferredScaleDenominators() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_AvailableTimesteps() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_CurrentTimestep() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_AvailableElevation() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getViewportModel_CurrentElevation() {
        return (EAttribute) viewportModelEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getComparable() {
        return comparableEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getCoordinateReferenceSystem() {
        return coordinateReferenceSystemEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEnvelope() {
        return envelopeEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIGeoResource() {
        return iGeoResourceEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getBufferedImage() {
        return bufferedImageEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getRectangle() {
        return rectangleEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getQuery() {
        return queryEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getGraphics2D() {
        return graphics2DEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getList() {
        return listEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getCoordinate() {
        return coordinateEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getPoint() {
        return pointEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getAffineTransform() {
        return affineTransformEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getMapDisplay() {
        return mapDisplayEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIProgressMonitor() {
        return iProgressMonitorEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getRenderException() {
        return renderExceptionEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getSortedSet() {
        return sortedSetEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getReferencedEnvelope() {
        return referencedEnvelopeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getDateTime() {
        return dateTimeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIllegalArgumentException() {
        return illegalArgumentExceptionEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated public EDataType getInfoList() { return infoListEDataType; }
     */

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderFactory getRenderFactory() {
        return (RenderFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__AVAILABLE_TIMESTEPS);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__CURRENT_TIMESTEP);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__AVAILABLE_ELEVATION);
        createEAttribute(viewportModelEClass, VIEWPORT_MODEL__CURRENT_ELEVATION);

        comparableEClass = createEClass(COMPARABLE);

        rendererEClass = createEClass(RENDERER);
        createEAttribute(rendererEClass, RENDERER__STATE);
        createEAttribute(rendererEClass, RENDERER__NAME);
        createEReference(rendererEClass, RENDERER__CONTEXT);

        iMapDisplayListenerEClass = createEClass(IMAP_DISPLAY_LISTENER);

        iRenderContextEClass = createEClass(IRENDER_CONTEXT);

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
        renderExceptionEDataType = createEDataType(RENDER_EXCEPTION);
        sortedSetEDataType = createEDataType(SORTED_SET);
        referencedEnvelopeEDataType = createEDataType(REFERENCED_ENVELOPE);
        dateTimeEDataType = createEDataType(DATE_TIME);
        illegalArgumentExceptionEDataType = createEDataType(ILLEGAL_ARGUMENT_EXCEPTION);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
        ProjectPackage theProjectPackage = (ProjectPackage) EPackage.Registry.INSTANCE
                .getEPackage(ProjectPackage.eNS_URI);

        // Create type parameters
        addETypeParameter(sortedSetEDataType, "T"); //$NON-NLS-1$

        // Set bounds for type parameters

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

        EOperation op = addEOperation(multiLayerRendererEClass, null,
                "refreshImage", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEException(op, this.getRenderException());

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
                "mapInternal", null, 0, 1, RenderManager.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(
                getRenderManager_ViewportModelInternal(),
                this.getViewportModel(),
                this.getViewportModel_RenderManagerInternal(),
                "viewportModelInternal", null, 0, 1, RenderManager.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(renderManagerEClass, null, "refresh", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, this.getEnvelope(), "bounds", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        addEOperation(renderManagerEClass, null, "dispose", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        initEClass(viewportModelEClass, ViewportModel.class,
                "ViewportModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_CRS(),
                this.getCoordinateReferenceSystem(),
                "cRS", null, 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_Bounds(),
                this.getReferencedEnvelope(),
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
        EGenericType g1 = createEGenericType(this.getSortedSet());
        EGenericType g2 = createEGenericType(ecorePackage.getEDoubleObject());
        g1.getETypeArguments().add(g2);
        initEAttribute(
                getViewportModel_PreferredScaleDenominators(),
                g1,
                "preferredScaleDenominators", null, 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_AvailableTimesteps(),
                this.getDateTime(),
                "availableTimesteps", null, 0, -1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_CurrentTimestep(),
                this.getDateTime(),
                "currentTimestep", null, 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_AvailableElevation(),
                ecorePackage.getEDoubleObject(),
                "availableElevation", null, 0, -1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getViewportModel_CurrentElevation(),
                ecorePackage.getEDoubleObject(),
                "currentElevation", null, 0, 1, ViewportModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, null, "setBounds", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "minx", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "maxx", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "miny", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "maxy", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEException(op, this.getIllegalArgumentException());

        addEOperation(viewportModelEClass, this.getAffineTransform(),
                "worldToScreenTransform", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getPoint(),
                "worldToPixel", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, this.getCoordinate(), "coord", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getCoordinate(),
                "pixelToWorld", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "x", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "y", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(),
                "panUsingScreenCoords", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "xpixels", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEInt(), "ypixels", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(),
                "panUsingWorldCoords", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "x", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "y", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(),
                "zoom", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "zoom", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, this.getViewportModel(),
                "zoom", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEDouble(), "zoom", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, this.getCoordinate(), "fixedPoint", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        addEOperation(viewportModelEClass, null, "zoomToExtent", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        op = addEOperation(viewportModelEClass, null, "zoomToBox", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
        addEParameter(op, this.getEnvelope(), "box", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        initEClass(comparableEClass, Object.class,
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
        initEReference(
                getRenderer_Context(),
                this.getIRenderContext(),
                null,
                "context", null, 0, 1, Renderer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(rendererEClass, null, "dispose", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        initEClass(iMapDisplayListenerEClass, IMapDisplayListener.class,
                "IMapDisplayListener", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(iRenderContextEClass, IRenderContext.class,
                "IRenderContext", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

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
        initEDataType(renderExceptionEDataType, RenderException.class,
                "RenderException", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(sortedSetEDataType, SortedSet.class,
                "SortedSet", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(referencedEnvelopeEDataType, ReferencedEnvelope.class,
                "ReferencedEnvelope", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(dateTimeEDataType, DateTime.class,
                "DateTime", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(illegalArgumentExceptionEDataType, IllegalArgumentException.class,
                "IllegalArgumentException", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} // RenderPackageImpl
