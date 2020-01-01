/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.render
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model kind="package"
 * @generated
 */
public interface RenderPackage extends EPackage {

    /**
     * The package name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "render"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///net/refractions/udig/project/internal/render.ecore"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "org.locationtech.udig.project.internal.render"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    RenderPackage eINSTANCE = org.locationtech.udig.project.internal.render.impl.RenderPackageImpl
            .init();

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.IRenderManager <em>IRender Manager</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.IRenderManager
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderManager()
     * @generated
     */
    int IRENDER_MANAGER = 0;

    /**
     * The number of structural features of the the '<em>IRender Manager</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IRENDER_MANAGER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.IViewportModel <em>IViewport Model</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.IViewportModel
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIViewportModel()
     * @generated
     */
    int IVIEWPORT_MODEL = 1;

    /**
     * The number of structural features of the the '<em>IViewport Model</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IVIEWPORT_MODEL_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.IMultiLayerRenderer <em>IMulti Layer Renderer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.IMultiLayerRenderer
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIMultiLayerRenderer()
     * @generated
     */
    int IMULTI_LAYER_RENDERER = 2;

    /**
     * The number of structural features of the the '<em>IMulti Layer Renderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IMULTI_LAYER_RENDERER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.IRenderer <em>IRenderer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.IRenderer
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderer()
     * @generated
     */
    int IRENDERER = 3;

    /**
     * The number of structural features of the the '<em>IRenderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IRENDERER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.ILayer <em>ILayer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.ILayer
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getILayer()
     * @generated
     */
    int ILAYER = 4;

    /**
     * The number of structural features of the '<em>ILayer</em>' class.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ILAYER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.internal.render.impl.RendererImpl <em>Renderer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.internal.render.impl.RendererImpl
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderer()
     * @generated
     */
    int RENDERER = 10;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDERER__STATE = IRENDERER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDERER__NAME = IRENDERER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDERER__CONTEXT = IRENDERER_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Renderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int RENDERER_FEATURE_COUNT = IRENDERER_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.internal.render.impl.MultiLayerRendererImpl <em>Multi Layer Renderer</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.internal.render.impl.MultiLayerRendererImpl
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getMultiLayerRenderer()
     * @generated
     */
    int MULTI_LAYER_RENDERER = 5;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER__STATE = RENDERER__STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER__NAME = RENDERER__NAME;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER__CONTEXT = RENDERER__CONTEXT;

    /**
     * The number of structural features of the the '<em>Multi Layer Renderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER_FEATURE_COUNT = RENDERER_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl <em>Executor</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderExecutor()
     * @generated
     */
    int RENDER_EXECUTOR = 6;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__STATE = RENDERER__STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__NAME = RENDERER__NAME;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__CONTEXT = RENDERER__CONTEXT;

    /**
     * The feature id for the '<em><b>Renderer</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__RENDERER = RENDERER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Executor</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR_FEATURE_COUNT = RENDERER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.internal.render.impl.RenderManagerImpl <em>Manager</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.internal.render.impl.RenderManagerImpl
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderManager()
     * @generated
     */
    int RENDER_MANAGER = 7;

    /**
     * The feature id for the '<em><b>Render Executor</b></em>' reference.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__RENDER_EXECUTOR = IRENDER_MANAGER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Map Display</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__MAP_DISPLAY = IRENDER_MANAGER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Map Internal</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__MAP_INTERNAL = IRENDER_MANAGER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Viewport Model Internal</b></em>' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL = IRENDER_MANAGER_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Manager</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int RENDER_MANAGER_FEATURE_COUNT = IRENDER_MANAGER_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIMapDisplayListener()
     * @generated
     */
    int IMAP_DISPLAY_LISTENER = 11;

    /**
     * The number of structural features of the the '<em>IMap Display Listener</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int IMAP_DISPLAY_LISTENER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.internal.render.impl.ViewportModelImpl <em>Viewport Model</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.locationtech.udig.project.internal.render.impl.ViewportModelImpl
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getViewportModel()
     * @generated
     */
    int VIEWPORT_MODEL = 8;

    /**
     * The feature id for the '<em><b>CRS</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CRS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Bounds</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__BOUNDS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Center</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CENTER = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Height</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__HEIGHT = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Width</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__WIDTH = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Aspect Ratio</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__ASPECT_RATIO = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pixel Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__PIXEL_SIZE = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Map Internal</b></em>' container reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__MAP_INTERNAL = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Render Manager Internal</b></em>' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Preferred Scale Denominators</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Available Timesteps</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__AVAILABLE_TIMESTEPS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Current Timestep</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CURRENT_TIMESTEP = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Available Elevation</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__AVAILABLE_ELEVATION = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Current Elevation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CURRENT_ELEVATION = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 13;

    /**
     * The number of structural features of the the '<em>Viewport Model</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL_FEATURE_COUNT = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 14;

    /**
     * The meta object id for the '{@link Comparable <em>Comparable</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see Comparable
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getComparable()
     * @generated
     */
    int COMPARABLE = 9;

    /**
     * The number of structural features of the the '<em>Comparable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int COMPARABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.locationtech.udig.project.render.IRenderContext <em>IRender Context</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.IRenderContext
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderContext()
     * @generated
     */
    int IRENDER_CONTEXT = 12;

    /**
     * The number of structural features of the '<em>IRender Context</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IRENDER_CONTEXT_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '<em>Coordinate Reference System</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getCoordinateReferenceSystem()
     * @generated
     */
    int COORDINATE_REFERENCE_SYSTEM = 13;

    /**
     * The meta object id for the '<em>Envelope</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.locationtech.jts.geom.Envelope
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getEnvelope()
     * @generated
     */
    int ENVELOPE = 14;

    /**
     * The meta object id for the '<em>IGeo Resource</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.locationtech.udig.catalog.IGeoResource
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIGeoResource()
     * @generated
     */
    int IGEO_RESOURCE = 15;

    /**
     * The meta object id for the '<em>Buffered Image</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.awt.image.BufferedImage
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getBufferedImage()
     * @generated
     */
    int BUFFERED_IMAGE = 16;

    /**
     * The meta object id for the '<em>Rectangle</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.awt.Rectangle
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRectangle()
     * @generated
     */
    int RECTANGLE = 17;

    /**
     * The meta object id for the '<em>Query</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.geotools.data.Query
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getQuery()
     * @generated
     */
    int QUERY = 18;

    /**
     * The meta object id for the '<em>Graphics2 D</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.awt.Graphics2D
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getGraphics2D()
     * @generated
     */
    int GRAPHICS2_D = 19;

    /**
     * The meta object id for the '<em>List</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.util.List
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getList()
     * @generated
     */
    int LIST = 20;

    /**
     * The meta object id for the '<em>Coordinate</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.locationtech.jts.geom.Coordinate
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getCoordinate()
     * @generated
     */
    int COORDINATE = 21;

    /**
     * The meta object id for the '<em>Point</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see java.awt.Point
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getPoint()
     * @generated
     */
    int POINT = 22;

    /**
     * The meta object id for the '<em>Affine Transform</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.awt.geom.AffineTransform
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getAffineTransform()
     * @generated
     */
    int AFFINE_TRANSFORM = 23;

    /**
     * The meta object id for the '<em>Map Display</em>' data type.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplay
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getMapDisplay()
     * @generated
     */
    int MAP_DISPLAY = 24;

    /**
     * The meta object id for the '<em>IProgress Monitor</em>' data type.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIProgressMonitor()
     * @generated
     */
    int IPROGRESS_MONITOR = 25;

    /**
     * The meta object id for the '<em>Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.locationtech.udig.project.render.RenderException
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderException()
     * @generated
     */
    int RENDER_EXCEPTION = 26;

    /**
     * The meta object id for the '<em>Sorted Set</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.SortedSet
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getSortedSet()
     * @generated
     */
    int SORTED_SET = 27;

    /**
     * The meta object id for the '<em>Referenced Envelope</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.geotools.geometry.jts.ReferencedEnvelope
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getReferencedEnvelope()
     * @generated
     */
    int REFERENCED_ENVELOPE = 28;

    /**
     * The meta object id for the '<em>Date Time</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.joda.time.DateTime
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getDateTime()
     * @generated
     */
    int DATE_TIME = 29;

    /**
     * The meta object id for the '<em>Illegal Argument Exception</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.IllegalArgumentException
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIllegalArgumentException()
     * @generated
     */
    int ILLEGAL_ARGUMENT_EXCEPTION = 30;

    /**
     * The meta object id for the '<em>Info List</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see org.locationtech.udig.project.render.InfoList
     * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getInfoList()
     * @generated int INFO_LIST = 30;
     */

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see org.locationtech.udig.project.render.IRenderManager
     * @model instanceClass="org.locationtech.udig.project.render.IRenderManager"
     * @generated
     */
    EClass getIRenderManager();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see org.locationtech.udig.project.render.IViewportModel
     * @model instanceClass="org.locationtech.udig.project.render.IViewportModel"
     * @generated
     */
    EClass getIViewportModel();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see org.locationtech.udig.project.render.IMultiLayerRenderer
     * @model instanceClass="org.locationtech.udig.project.render.IMultiLayerRenderer"
     * @generated
     */
    EClass getIMultiLayerRenderer();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see org.locationtech.udig.project.render.IRenderer
     * @model instanceClass="org.locationtech.udig.project.render.IRenderer"
     * @generated
     */
    EClass getIRenderer();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see org.locationtech.udig.project.ILayer
     * @model instanceClass="org.locationtech.udig.project.ILayer"
     * @generated
     */
    EClass getILayer();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.internal.render.MultiLayerRenderer <em>Multi Layer Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Multi Layer Renderer</em>'.
     * @see org.locationtech.udig.project.internal.render.MultiLayerRenderer
     * @generated
     */
    EClass getMultiLayerRenderer();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.internal.render.RenderExecutor <em>Executor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Executor</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderExecutor
     * @generated
     */
    EClass getRenderExecutor();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.RenderExecutor#getRenderer <em>Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Renderer</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderExecutor#getRenderer()
     * @see #getRenderExecutor()
     * @generated
     */
    EReference getRenderExecutor_Renderer();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.internal.render.RenderManager <em>Manager</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Manager</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderManager
     * @generated
     */
    EClass getRenderManager();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.RenderManager#getRenderExecutor <em>Render Executor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Render Executor</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderManager#getRenderExecutor()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_RenderExecutor();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.RenderManager#getMapDisplay <em>Map Display</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Map Display</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderManager#getMapDisplay()
     * @see #getRenderManager()
     * @generated
     */
    EAttribute getRenderManager_MapDisplay();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.RenderManager#getMapInternal <em>Map Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Map Internal</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderManager#getMapInternal()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_MapInternal();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.RenderManager#getViewportModelInternal <em>Viewport Model Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Viewport Model Internal</em>'.
     * @see org.locationtech.udig.project.internal.render.RenderManager#getViewportModelInternal()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_ViewportModelInternal();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.internal.render.Renderer <em>Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Renderer</em>'.
     * @see org.locationtech.udig.project.internal.render.Renderer
     * @generated
     */
    EClass getRenderer();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.Renderer#getState <em>State</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>State</em>'.
     * @see org.locationtech.udig.project.internal.render.Renderer#getState()
     * @see #getRenderer()
     * @generated
     */
    EAttribute getRenderer_State();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.Renderer#getName <em>Name</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.locationtech.udig.project.internal.render.Renderer#getName()
     * @see #getRenderer()
     * @generated
     */
    EAttribute getRenderer_Name();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.Renderer#getContext <em>Context</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Context</em>'.
     * @see org.locationtech.udig.project.internal.render.Renderer#getContext()
     * @see #getRenderer()
     * @generated
     */
    EReference getRenderer_Context();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>IMap Display Listener</em>'.
     * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener
     * @model instanceClass="org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener"
     * @generated
     */
    EClass getIMapDisplayListener();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.render.IRenderContext <em>IRender Context</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>IRender Context</em>'.
     * @see org.locationtech.udig.project.render.IRenderContext
     * @model instanceClass="org.locationtech.udig.project.render.IRenderContext"
     * @generated
     */
    EClass getIRenderContext();

    /**
     * Returns the meta object for class '{@link org.locationtech.udig.project.internal.render.ViewportModel <em>Viewport Model</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for class '<em>Viewport Model</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel
     * @generated
     */
    EClass getViewportModel();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCRS <em>CRS</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>CRS</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getCRS()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_CRS();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getBounds <em>Bounds</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Bounds</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getBounds()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Bounds();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCenter <em>Center</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Center</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getCenter()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Center();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getHeight <em>Height</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Height</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getHeight()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Height();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getWidth <em>Width</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Width</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getWidth()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Width();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getAspectRatio <em>Aspect Ratio</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Aspect Ratio</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getAspectRatio()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_AspectRatio();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getPixelSize <em>Pixel Size</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pixel Size</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getPixelSize()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_PixelSize();

    /**
     * Returns the meta object for the container reference '{@link org.locationtech.udig.project.internal.render.ViewportModel#getMapInternal <em>Map Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Map Internal</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getMapInternal()
     * @see #getViewportModel()
     * @generated
     */
    EReference getViewportModel_MapInternal();

    /**
     * Returns the meta object for the reference '{@link org.locationtech.udig.project.internal.render.ViewportModel#getRenderManagerInternal <em>Render Manager Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Render Manager Internal</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getRenderManagerInternal()
     * @see #getViewportModel()
     * @generated
     */
    EReference getViewportModel_RenderManagerInternal();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getPreferredScaleDenominators <em>Preferred Scale Denominators</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Preferred Scale Denominators</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getPreferredScaleDenominators()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_PreferredScaleDenominators();

    /**
     * Returns the meta object for the attribute list '{@link org.locationtech.udig.project.internal.render.ViewportModel#getAvailableTimesteps <em>Available Timesteps</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Available Timesteps</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getAvailableTimesteps()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_AvailableTimesteps();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCurrentTimestep <em>Current Timestep</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Current Timestep</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getCurrentTimestep()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_CurrentTimestep();

    /**
     * Returns the meta object for the attribute list '{@link org.locationtech.udig.project.internal.render.ViewportModel#getAvailableElevation <em>Available Elevation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Available Elevation</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getAvailableElevation()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_AvailableElevation();

    /**
     * Returns the meta object for the attribute '{@link org.locationtech.udig.project.internal.render.ViewportModel#getCurrentElevation <em>Current Elevation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Current Elevation</em>'.
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getCurrentElevation()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_CurrentElevation();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Comparable</em>'.
     * @see Comparable
     * @model instanceClass="Comparable"
     * @generated
     */
    EClass getComparable();

    /**
     * Returns the meta object for data type '{@link org.opengis.referencing.crs.CoordinateReferenceSystem <em>Coordinate Reference System</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Coordinate Reference System</em>'.
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @model instanceClass="org.opengis.referencing.crs.CoordinateReferenceSystem"
     * @generated
     */
    EDataType getCoordinateReferenceSystem();

    /**
     * Returns the meta object for data type '{@link org.locationtech.jts.geom.Envelope <em>Envelope</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Envelope</em>'.
     * @see org.locationtech.jts.geom.Envelope
     * @model instanceClass="org.locationtech.jts.geom.Envelope"
     * @generated
     */
    EDataType getEnvelope();

    /**
     * Returns the meta object for data type '{@link org.locationtech.udig.catalog.IGeoResource <em>IGeo Resource</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>IGeo Resource</em>'.
     * @see org.locationtech.udig.catalog.IGeoResource
     * @model instanceClass="org.locationtech.udig.catalog.IGeoResource"
     * @generated
     */
    EDataType getIGeoResource();

    /**
     * Returns the meta object for data type '{@link java.awt.image.BufferedImage <em>Buffered Image</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Buffered Image</em>'.
     * @see java.awt.image.BufferedImage
     * @model instanceClass="java.awt.image.BufferedImage"
     * @generated
     */
    EDataType getBufferedImage();

    /**
     * Returns the meta object for data type '{@link java.awt.Rectangle <em>Rectangle</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>Rectangle</em>'.
     * @see java.awt.Rectangle
     * @model instanceClass="java.awt.Rectangle"
     * @generated
     */
    EDataType getRectangle();

    /**
     * Returns the meta object for data type '{@link org.geotools.data.Query <em>Query</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Query</em>'.
     * @see org.geotools.data.Query
     * @model instanceClass="org.geotools.data.Query"
     * @generated
     */
    EDataType getQuery();

    /**
     * Returns the meta object for data type '{@link java.awt.Graphics2D <em>Graphics2 D</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Graphics2 D</em>'.
     * @see java.awt.Graphics2D
     * @model instanceClass="java.awt.Graphics2D"
     * @generated
     */
    EDataType getGraphics2D();

    /**
     * Returns the meta object for data type '{@link java.util.List <em>List</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>List</em>'.
     * @see java.util.List
     * @model instanceClass="java.util.List"
     * @generated
     */
    EDataType getList();

    /**
     * Returns the meta object for data type '{@link org.locationtech.jts.geom.Coordinate <em>Coordinate</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Coordinate</em>'.
     * @see org.locationtech.jts.geom.Coordinate
     * @model instanceClass="org.locationtech.jts.geom.Coordinate"
     * @generated
     */
    EDataType getCoordinate();

    /**
     * Returns the meta object for data type '{@link java.awt.Point <em>Point</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for data type '<em>Point</em>'.
     * @see java.awt.Point
     * @model instanceClass="java.awt.Point"
     * @generated
     */
    EDataType getPoint();

    /**
     * Returns the meta object for data type '{@link java.awt.geom.AffineTransform <em>Affine Transform</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Affine Transform</em>'.
     * @see java.awt.geom.AffineTransform
     * @model instanceClass="java.awt.geom.AffineTransform"
     * @generated
     */
    EDataType getAffineTransform();

    /**
     * Returns the meta object for data type '{@link org.locationtech.udig.project.render.displayAdapter.IMapDisplay <em>Map Display</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>Map Display</em>'.
     * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplay
     * @model instanceClass="org.locationtech.udig.project.render.displayAdapter.IMapDisplay"
     * @generated
     */
    EDataType getMapDisplay();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IProgressMonitor <em>IProgress Monitor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for data type '<em>IProgress Monitor</em>'.
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @model instanceClass="org.eclipse.core.runtime.IProgressMonitor"
     * @generated
     */
    EDataType getIProgressMonitor();

    /**
     * Returns the meta object for data type '{@link org.locationtech.udig.project.render.RenderException <em>Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Exception</em>'.
     * @see org.locationtech.udig.project.render.RenderException
     * @model instanceClass="org.locationtech.udig.project.render.RenderException"
     * @generated
     */
    EDataType getRenderException();

    /**
     * Returns the meta object for data type '{@link java.util.SortedSet <em>Sorted Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Sorted Set</em>'.
     * @see java.util.SortedSet
     * @model instanceClass="java.util.SortedSet" typeParameters="T"
     * @generated
     */
    EDataType getSortedSet();

    /**
     * Returns the meta object for data type '{@link org.geotools.geometry.jts.ReferencedEnvelope <em>Referenced Envelope</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Referenced Envelope</em>'.
     * @see org.geotools.geometry.jts.ReferencedEnvelope
     * @model instanceClass="org.geotools.geometry.jts.ReferencedEnvelope"
     * @generated
     */
    EDataType getReferencedEnvelope();

    /**
     * Returns the meta object for data type '{@link org.joda.time.DateTime <em>Date Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Date Time</em>'.
     * @see org.joda.time.DateTime
     * @model instanceClass="org.joda.time.DateTime"
     * @generated
     */
    EDataType getDateTime();

    /**
     * Returns the meta object for data type '{@link java.lang.IllegalArgumentException <em>Illegal Argument Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Illegal Argument Exception</em>'.
     * @see java.lang.IllegalArgumentException
     * @model instanceClass="java.lang.IllegalArgumentException"
     * @generated
     */
    EDataType getIllegalArgumentException();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RenderFactory getRenderFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.IRenderManager <em>IRender Manager</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.IRenderManager
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderManager()
         * @generated
         */
        EClass IRENDER_MANAGER = eINSTANCE.getIRenderManager();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.IViewportModel <em>IViewport Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.IViewportModel
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIViewportModel()
         * @generated
         */
        EClass IVIEWPORT_MODEL = eINSTANCE.getIViewportModel();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.IMultiLayerRenderer <em>IMulti Layer Renderer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.IMultiLayerRenderer
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIMultiLayerRenderer()
         * @generated
         */
        EClass IMULTI_LAYER_RENDERER = eINSTANCE.getIMultiLayerRenderer();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.IRenderer <em>IRenderer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.IRenderer
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderer()
         * @generated
         */
        EClass IRENDERER = eINSTANCE.getIRenderer();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.ILayer <em>ILayer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.ILayer
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getILayer()
         * @generated
         */
        EClass ILAYER = eINSTANCE.getILayer();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.internal.render.impl.MultiLayerRendererImpl <em>Multi Layer Renderer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.internal.render.impl.MultiLayerRendererImpl
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getMultiLayerRenderer()
         * @generated
         */
        EClass MULTI_LAYER_RENDERER = eINSTANCE.getMultiLayerRenderer();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl <em>Executor</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderExecutor()
         * @generated
         */
        EClass RENDER_EXECUTOR = eINSTANCE.getRenderExecutor();

        /**
         * The meta object literal for the '<em><b>Renderer</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference RENDER_EXECUTOR__RENDERER = eINSTANCE.getRenderExecutor_Renderer();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.internal.render.impl.RenderManagerImpl <em>Manager</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.internal.render.impl.RenderManagerImpl
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderManager()
         * @generated
         */
        EClass RENDER_MANAGER = eINSTANCE.getRenderManager();

        /**
         * The meta object literal for the '<em><b>Render Executor</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference RENDER_MANAGER__RENDER_EXECUTOR = eINSTANCE.getRenderManager_RenderExecutor();

        /**
         * The meta object literal for the '<em><b>Map Display</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RENDER_MANAGER__MAP_DISPLAY = eINSTANCE.getRenderManager_MapDisplay();

        /**
         * The meta object literal for the '<em><b>Map Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference RENDER_MANAGER__MAP_INTERNAL = eINSTANCE.getRenderManager_MapInternal();

        /**
         * The meta object literal for the '<em><b>Viewport Model Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL = eINSTANCE
                .getRenderManager_ViewportModelInternal();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.internal.render.impl.ViewportModelImpl <em>Viewport Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.internal.render.impl.ViewportModelImpl
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getViewportModel()
         * @generated
         */
        EClass VIEWPORT_MODEL = eINSTANCE.getViewportModel();

        /**
         * The meta object literal for the '<em><b>CRS</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__CRS = eINSTANCE.getViewportModel_CRS();

        /**
         * The meta object literal for the '<em><b>Bounds</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__BOUNDS = eINSTANCE.getViewportModel_Bounds();

        /**
         * The meta object literal for the '<em><b>Center</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__CENTER = eINSTANCE.getViewportModel_Center();

        /**
         * The meta object literal for the '<em><b>Height</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__HEIGHT = eINSTANCE.getViewportModel_Height();

        /**
         * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__WIDTH = eINSTANCE.getViewportModel_Width();

        /**
         * The meta object literal for the '<em><b>Aspect Ratio</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__ASPECT_RATIO = eINSTANCE.getViewportModel_AspectRatio();

        /**
         * The meta object literal for the '<em><b>Pixel Size</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__PIXEL_SIZE = eINSTANCE.getViewportModel_PixelSize();

        /**
         * The meta object literal for the '<em><b>Map Internal</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference VIEWPORT_MODEL__MAP_INTERNAL = eINSTANCE.getViewportModel_MapInternal();

        /**
         * The meta object literal for the '<em><b>Render Manager Internal</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL = eINSTANCE
                .getViewportModel_RenderManagerInternal();

        /**
         * The meta object literal for the '<em><b>Preferred Scale Denominators</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__PREFERRED_SCALE_DENOMINATORS = eINSTANCE
                .getViewportModel_PreferredScaleDenominators();

        /**
         * The meta object literal for the '<em><b>Available Timesteps</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__AVAILABLE_TIMESTEPS = eINSTANCE
                .getViewportModel_AvailableTimesteps();

        /**
         * The meta object literal for the '<em><b>Current Timestep</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__CURRENT_TIMESTEP = eINSTANCE.getViewportModel_CurrentTimestep();

        /**
         * The meta object literal for the '<em><b>Available Elevation</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__AVAILABLE_ELEVATION = eINSTANCE
                .getViewportModel_AvailableElevation();

        /**
         * The meta object literal for the '<em><b>Current Elevation</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VIEWPORT_MODEL__CURRENT_ELEVATION = eINSTANCE
                .getViewportModel_CurrentElevation();

        /**
         * The meta object literal for the '{@link Comparable <em>Comparable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see Comparable
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getComparable()
         * @generated
         */
        EClass COMPARABLE = eINSTANCE.getComparable();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.internal.render.impl.RendererImpl <em>Renderer</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.internal.render.impl.RendererImpl
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderer()
         * @generated
         */
        EClass RENDERER = eINSTANCE.getRenderer();

        /**
         * The meta object literal for the '<em><b>State</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RENDERER__STATE = eINSTANCE.getRenderer_State();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RENDERER__NAME = eINSTANCE.getRenderer_Name();

        /**
         * The meta object literal for the '<em><b>Context</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference RENDERER__CONTEXT = eINSTANCE.getRenderer_Context();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIMapDisplayListener()
         * @generated
         */
        EClass IMAP_DISPLAY_LISTENER = eINSTANCE.getIMapDisplayListener();

        /**
         * The meta object literal for the '{@link org.locationtech.udig.project.render.IRenderContext <em>IRender Context</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.IRenderContext
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIRenderContext()
         * @generated
         */
        EClass IRENDER_CONTEXT = eINSTANCE.getIRenderContext();

        /**
         * The meta object literal for the '<em>Coordinate Reference System</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.opengis.referencing.crs.CoordinateReferenceSystem
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getCoordinateReferenceSystem()
         * @generated
         */
        EDataType COORDINATE_REFERENCE_SYSTEM = eINSTANCE.getCoordinateReferenceSystem();

        /**
         * The meta object literal for the '<em>Envelope</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.jts.geom.Envelope
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getEnvelope()
         * @generated
         */
        EDataType ENVELOPE = eINSTANCE.getEnvelope();

        /**
         * The meta object literal for the '<em>IGeo Resource</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.catalog.IGeoResource
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIGeoResource()
         * @generated
         */
        EDataType IGEO_RESOURCE = eINSTANCE.getIGeoResource();

        /**
         * The meta object literal for the '<em>Buffered Image</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.image.BufferedImage
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getBufferedImage()
         * @generated
         */
        EDataType BUFFERED_IMAGE = eINSTANCE.getBufferedImage();

        /**
         * The meta object literal for the '<em>Rectangle</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Rectangle
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRectangle()
         * @generated
         */
        EDataType RECTANGLE = eINSTANCE.getRectangle();

        /**
         * The meta object literal for the '<em>Query</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.data.Query
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getQuery()
         * @generated
         */
        EDataType QUERY = eINSTANCE.getQuery();

        /**
         * The meta object literal for the '<em>Graphics2 D</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Graphics2D
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getGraphics2D()
         * @generated
         */
        EDataType GRAPHICS2_D = eINSTANCE.getGraphics2D();

        /**
         * The meta object literal for the '<em>List</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.util.List
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getList()
         * @generated
         */
        EDataType LIST = eINSTANCE.getList();

        /**
         * The meta object literal for the '<em>Coordinate</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.jts.geom.Coordinate
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getCoordinate()
         * @generated
         */
        EDataType COORDINATE = eINSTANCE.getCoordinate();

        /**
         * The meta object literal for the '<em>Point</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.Point
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getPoint()
         * @generated
         */
        EDataType POINT = eINSTANCE.getPoint();

        /**
         * The meta object literal for the '<em>Affine Transform</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.awt.geom.AffineTransform
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getAffineTransform()
         * @generated
         */
        EDataType AFFINE_TRANSFORM = eINSTANCE.getAffineTransform();

        /**
         * The meta object literal for the '<em>Map Display</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.displayAdapter.IMapDisplay
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getMapDisplay()
         * @generated
         */
        EDataType MAP_DISPLAY = eINSTANCE.getMapDisplay();

        /**
         * The meta object literal for the '<em>IProgress Monitor</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.core.runtime.IProgressMonitor
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIProgressMonitor()
         * @generated
         */
        EDataType IPROGRESS_MONITOR = eINSTANCE.getIProgressMonitor();

        /**
         * The meta object literal for the '<em>Exception</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.locationtech.udig.project.render.RenderException
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getRenderException()
         * @generated
         */
        EDataType RENDER_EXCEPTION = eINSTANCE.getRenderException();

        /**
         * The meta object literal for the '<em>Sorted Set</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.util.SortedSet
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getSortedSet()
         * @generated
         */
        EDataType SORTED_SET = eINSTANCE.getSortedSet();

        /**
         * The meta object literal for the '<em>Referenced Envelope</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.geotools.geometry.jts.ReferencedEnvelope
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getReferencedEnvelope()
         * @generated
         */
        EDataType REFERENCED_ENVELOPE = eINSTANCE.getReferencedEnvelope();

        /**
         * The meta object literal for the '<em>Date Time</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.joda.time.DateTime
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getDateTime()
         * @generated
         */
        EDataType DATE_TIME = eINSTANCE.getDateTime();

        /**
         * The meta object literal for the '<em>Illegal Argument Exception</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.IllegalArgumentException
         * @see org.locationtech.udig.project.internal.render.impl.RenderPackageImpl#getIllegalArgumentException()
         * @generated
         */
        EDataType ILLEGAL_ARGUMENT_EXCEPTION = eINSTANCE.getIllegalArgumentException();

    }

} // RenderPackage
