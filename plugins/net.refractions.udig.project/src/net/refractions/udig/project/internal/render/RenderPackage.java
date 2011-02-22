/**
 * <copyright></copyright> $Id: RenderPackage.java 21423 2006-09-14 19:17:05Z jeichar $
 */
package net.refractions.udig.project.internal.render;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * TODO Purpose of net.refractions.udig.project.internal.render
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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String eNAME = "render"; //$NON-NLS-1$

    /**
     * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_URI = "http:///net/refractions/udig/project/internal/render.ecore"; //$NON-NLS-1$

    /**
     * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String eNS_PREFIX = "net.refractions.udig.project.internal.render"; //$NON-NLS-1$

    /**
     * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    RenderPackage eINSTANCE = net.refractions.udig.project.internal.render.impl.RenderPackageImpl
            .init();

    /**
     * The meta object id for the '{@link net.refractions.udig.project.render.IRenderManager <em>IRender Manager</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.render.IRenderManager
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIRenderManager()
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
     * The meta object id for the '{@link net.refractions.udig.project.render.IViewportModel <em>IViewport Model</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.render.IViewportModel
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIViewportModel()
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
     * The meta object id for the '{@link net.refractions.udig.project.render.IMultiLayerRenderer <em>IMulti Layer Renderer</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.render.IMultiLayerRenderer
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIMultiLayerRenderer()
     * @generated
     */
    int IMULTI_LAYER_RENDERER = 3;

    /**
     * The number of structural features of the the '<em>IMulti Layer Renderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int IMULTI_LAYER_RENDERER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.render.IRenderer <em>IRenderer</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.render.IRenderer
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIRenderer()
     * @generated
     */
    int IRENDERER = 5;

    /**
     * The number of structural features of the the '<em>IRenderer</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int IRENDERER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.ILayer <em>ILayer</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.ILayer
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getILayer()
     * @generated
     */
    int ILAYER = 6;

    /**
     * The number of structural features of the the '<em>ILayer</em>' class. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int ILAYER_FEATURE_COUNT = 0;


    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.render.impl.RendererImpl <em>Renderer</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getRenderer()
     * @generated
     */
    int RENDERER = 14;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDERER__STATE = IRENDERER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDERER__NAME = IRENDERER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
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
     * The meta object id for the '{@link net.refractions.udig.project.internal.render.impl.MultiLayerRendererImpl <em>Multi Layer Renderer</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.internal.render.impl.MultiLayerRendererImpl
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getMultiLayerRenderer()
     * @generated
     */
    int MULTI_LAYER_RENDERER = 8;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER__STATE = RENDERER__STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int MULTI_LAYER_RENDERER__NAME = RENDERER__NAME;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
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
     * The meta object id for the '{@link net.refractions.udig.project.internal.render.impl.RenderExecutorImpl <em>Executor</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.internal.render.impl.RenderExecutorImpl
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getRenderExecutor()
     * @generated
     */
    int RENDER_EXECUTOR = 10;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__STATE = RENDERER__STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__NAME = RENDERER__NAME;

    /**
     * The feature id for the '<em><b>Context</b></em>' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDER_EXECUTOR__CONTEXT = RENDERER__CONTEXT;

    /**
     * The feature id for the '<em><b>Renderer</b></em>' reference. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
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
     * The meta object id for the '{@link net.refractions.udig.project.internal.render.impl.RenderManagerImpl <em>Manager</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.internal.render.impl.RenderManagerImpl
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getRenderManager()
     * @generated
     */
    int RENDER_MANAGER = 11;

    /**
     * The feature id for the '<em><b>Render Executor</b></em>' reference. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__RENDER_EXECUTOR = IRENDER_MANAGER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Map Display</b></em>' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int RENDER_MANAGER__MAP_DISPLAY = IRENDER_MANAGER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Map Internal</b></em>' reference. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
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
     * The meta object id for the '{@link net.refractions.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplayListener
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIMapDisplayListener()
     * @generated
     */
    int IMAP_DISPLAY_LISTENER = 16;

    /**
     * The number of structural features of the the '<em>IMap Display Listener</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int IMAP_DISPLAY_LISTENER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link net.refractions.udig.project.internal.render.impl.ViewportModelImpl <em>Viewport Model</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see net.refractions.udig.project.internal.render.impl.ViewportModelImpl
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getViewportModel()
     * @generated
     */
    int VIEWPORT_MODEL = 12;

    /**
     * The feature id for the '<em><b>CRS</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CRS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Bounds</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__BOUNDS = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Center</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__CENTER = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Height</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__HEIGHT = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Width</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__WIDTH = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Aspect Ratio</b></em>' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL__ASPECT_RATIO = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Pixel Size</b></em>' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
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
     * The number of structural features of the the '<em>Viewport Model</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int VIEWPORT_MODEL_FEATURE_COUNT = IMAP_DISPLAY_LISTENER_FEATURE_COUNT + 9;

    /**
     * The meta object id for the '{@link Comparable <em>Comparable</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see Comparable
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getComparable()
     * @generated
     */
    int COMPARABLE = 13;

    /**
     * The number of structural features of the the '<em>Comparable</em>' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     */
    int COMPARABLE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '<em>Coordinate Reference System</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getCoordinateReferenceSystem()
     * @generated
     */
    int COORDINATE_REFERENCE_SYSTEM = 17;

    /**
     * The meta object id for the '<em>Envelope</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see com.vividsolutions.jts.geom.Envelope
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getEnvelope()
     * @generated
     */
    int ENVELOPE = 18;

    /**
     * The meta object id for the '<em>IGeo Resource</em>' data type. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see net.refractions.udig.catalog.IGeoResource
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIGeoResource()
     * @generated
     */
    int IGEO_RESOURCE = 19;

    /**
     * The meta object id for the '<em>Buffered Image</em>' data type. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see java.awt.image.BufferedImage
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getBufferedImage()
     * @generated
     */
    int BUFFERED_IMAGE = 20;

    /**
     * The meta object id for the '<em>Rectangle</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see java.awt.Rectangle
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getRectangle()
     * @generated
     */
    int RECTANGLE = 21;

    /**
     * The meta object id for the '<em>Query</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see org.geotools.data.Query
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getQuery()
     * @generated
     */
    int QUERY = 22;

    /**
     * The meta object id for the '<em>Graphics2 D</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see java.awt.Graphics2D
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getGraphics2D()
     * @generated
     */
    int GRAPHICS2_D = 23;

    /**
     * The meta object id for the '<em>List</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see java.util.List
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getList()
     * @generated
     */
    int LIST = 24;

    /**
     * The meta object id for the '<em>Coordinate</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see com.vividsolutions.jts.geom.Coordinate
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getCoordinate()
     * @generated
     */
    int COORDINATE = 25;

    /**
     * The meta object id for the '<em>Point</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see java.awt.Point
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getPoint()
     * @generated
     */
    int POINT = 26;

    /**
     * The meta object id for the '<em>Affine Transform</em>' data type. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see java.awt.geom.AffineTransform
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getAffineTransform()
     * @generated
     */
    int AFFINE_TRANSFORM = 27;

    /**
     * The meta object id for the '<em>Map Display</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplay
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getMapDisplay()
     * @generated
     */
    int MAP_DISPLAY = 28;

    /**
     * The meta object id for the '<em>IProgress Monitor</em>' data type. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getIProgressMonitor()
     * @generated
     */
    int IPROGRESS_MONITOR = 29;

    /**
     * The meta object id for the '<em>Info List</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see net.refractions.udig.project.render.InfoList
     * @see net.refractions.udig.project.internal.render.impl.RenderPackageImpl#getInfoList()
     * @generated int INFO_LIST = 30;
     */

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IRenderManager
     * @model instanceClass="net.refractions.udig.project.render.IRenderManager"
     * @generated
     */
    EClass getIRenderManager();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IViewportModel
     * @model instanceClass="net.refractions.udig.project.render.IViewportModel"
     * @generated
     */
    EClass getIViewportModel();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IMultiLayerRenderer
     * @model instanceClass="net.refractions.udig.project.render.IMultiLayerRenderer"
     * @generated
     */
    EClass getIMultiLayerRenderer();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.render.IRenderer
     * @model instanceClass="net.refractions.udig.project.render.IRenderer"
     * @generated
     */
    EClass getIRenderer();

    /**
     * Returns the meta object for class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Comparable</em>'.
     * @see net.refractions.udig.project.ILayer
     * @model instanceClass="net.refractions.udig.project.ILayer"
     * @generated
     */
    EClass getILayer();


    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.render.MultiLayerRenderer <em>Multi Layer Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Multi Layer Renderer</em>'.
     * @see net.refractions.udig.project.internal.render.MultiLayerRenderer
     * @generated
     */
    EClass getMultiLayerRenderer();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.render.RenderExecutor <em>Executor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Executor</em>'.
     * @see net.refractions.udig.project.internal.render.RenderExecutor
     * @generated
     */
    EClass getRenderExecutor();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.RenderExecutor#getRenderer <em>Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Renderer</em>'.
     * @see net.refractions.udig.project.internal.render.RenderExecutor#getRenderer()
     * @see #getRenderExecutor()
     * @generated
     */
    EReference getRenderExecutor_Renderer();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.render.RenderManager <em>Manager</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Manager</em>'.
     * @see net.refractions.udig.project.internal.render.RenderManager
     * @generated
     */
    EClass getRenderManager();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.RenderManager#getRenderExecutor <em>Render Executor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Render Executor</em>'.
     * @see net.refractions.udig.project.internal.render.RenderManager#getRenderExecutor()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_RenderExecutor();
    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.RenderManager#getMapDisplay <em>Map Display</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Map Display</em>'.
     * @see net.refractions.udig.project.internal.render.RenderManager#getMapDisplay()
     * @see #getRenderManager()
     * @generated
     */
    EAttribute getRenderManager_MapDisplay();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.RenderManager#getMapInternal <em>Map Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Map Internal</em>'.
     * @see net.refractions.udig.project.internal.render.RenderManager#getMapInternal()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_MapInternal();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.RenderManager#getViewportModelInternal <em>Viewport Model Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Viewport Model Internal</em>'.
     * @see net.refractions.udig.project.internal.render.RenderManager#getViewportModelInternal()
     * @see #getRenderManager()
     * @generated
     */
    EReference getRenderManager_ViewportModelInternal();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.render.Renderer <em>Renderer</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Renderer</em>'.
     * @see net.refractions.udig.project.internal.render.Renderer
     * @generated
     */
    EClass getRenderer();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.Renderer#getState <em>State</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>State</em>'.
     * @see net.refractions.udig.project.internal.render.Renderer#getState()
     * @see #getRenderer()
     * @generated
     */
    EAttribute getRenderer_State();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.Renderer#getName <em>Name</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see net.refractions.udig.project.internal.render.Renderer#getName()
     * @see #getRenderer()
     * @generated
     */
    EAttribute getRenderer_Name();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.Renderer#getContext <em>Context</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Context</em>'.
     * @see net.refractions.udig.project.internal.render.Renderer#getContext()
     * @see #getRenderer()
     * @generated
     */
    EReference getRenderer_Context();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>IMap Display Listener</em>'.
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplayListener
     * @model instanceClass="net.refractions.udig.project.render.displayAdapter.IMapDisplayListener"
     * @generated
     */
    EClass getIMapDisplayListener();

    /**
     * Returns the meta object for class '{@link net.refractions.udig.project.internal.render.ViewportModel <em>Viewport Model</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for class '<em>Viewport Model</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel
     * @generated
     */
    EClass getViewportModel();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getCRS <em>CRS</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>CRS</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getCRS()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_CRS();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getBounds <em>Bounds</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Bounds</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getBounds()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Bounds();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getCenter <em>Center</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Center</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getCenter()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Center();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getHeight <em>Height</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Height</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getHeight()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Height();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getWidth <em>Width</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Width</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getWidth()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_Width();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getAspectRatio <em>Aspect Ratio</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Aspect Ratio</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getAspectRatio()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_AspectRatio();

    /**
     * Returns the meta object for the attribute '{@link net.refractions.udig.project.internal.render.ViewportModel#getPixelSize <em>Pixel Size</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the attribute '<em>Pixel Size</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getPixelSize()
     * @see #getViewportModel()
     * @generated
     */
    EAttribute getViewportModel_PixelSize();

    /**
     * Returns the meta object for the container reference '{@link net.refractions.udig.project.internal.render.ViewportModel#getMapInternal <em>Map Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the container reference '<em>Map Internal</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getMapInternal()
     * @see #getViewportModel()
     * @generated
     */
    EReference getViewportModel_MapInternal();

    /**
     * Returns the meta object for the reference '{@link net.refractions.udig.project.internal.render.ViewportModel#getRenderManagerInternal <em>Render Manager Internal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for the reference '<em>Render Manager Internal</em>'.
     * @see net.refractions.udig.project.internal.render.ViewportModel#getRenderManagerInternal()
     * @see #getViewportModel()
     * @generated
     */
    EReference getViewportModel_RenderManagerInternal();

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
     *
     * @return the meta object for data type '<em>Coordinate Reference System</em>'.
     * @see org.opengis.referencing.crs.CoordinateReferenceSystem
     * @model instanceClass="org.opengis.referencing.crs.CoordinateReferenceSystem"
     * @generated
     */
    EDataType getCoordinateReferenceSystem();

    /**
     * Returns the meta object for data type '{@link com.vividsolutions.jts.geom.Envelope <em>Envelope</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Envelope</em>'.
     * @see com.vividsolutions.jts.geom.Envelope
     * @model instanceClass="com.vividsolutions.jts.geom.Envelope"
     * @generated
     */
    EDataType getEnvelope();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.catalog.IGeoResource <em>IGeo Resource</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>IGeo Resource</em>'.
     * @see net.refractions.udig.catalog.IGeoResource
     * @model instanceClass="net.refractions.udig.catalog.IGeoResource"
     * @generated
     */
    EDataType getIGeoResource();

    /**
     * Returns the meta object for data type '{@link java.awt.image.BufferedImage <em>Buffered Image</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
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
     *
     * @return the meta object for data type '<em>Query</em>'.
     * @see org.geotools.data.Query
     * @model instanceClass="org.geotools.data.Query"
     * @generated
     */
    EDataType getQuery();

    /**
     * Returns the meta object for data type '{@link java.awt.Graphics2D <em>Graphics2 D</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
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
     * Returns the meta object for data type '{@link com.vividsolutions.jts.geom.Coordinate <em>Coordinate</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Coordinate</em>'.
     * @see com.vividsolutions.jts.geom.Coordinate
     * @model instanceClass="com.vividsolutions.jts.geom.Coordinate"
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
     *
     * @return the meta object for data type '<em>Affine Transform</em>'.
     * @see java.awt.geom.AffineTransform
     * @model instanceClass="java.awt.geom.AffineTransform"
     * @generated
     */
    EDataType getAffineTransform();

    /**
     * Returns the meta object for data type '{@link net.refractions.udig.project.render.displayAdapter.IMapDisplay <em>Map Display</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>Map Display</em>'.
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplay
     * @model instanceClass="net.refractions.udig.project.render.displayAdapter.IMapDisplay"
     * @generated
     */
    EDataType getMapDisplay();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IProgressMonitor <em>IProgress Monitor</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the meta object for data type '<em>IProgress Monitor</em>'.
     * @see org.eclipse.core.runtime.IProgressMonitor
     * @model instanceClass="org.eclipse.core.runtime.IProgressMonitor"
     * @generated
     */
    EDataType getIProgressMonitor();

    /**
     * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RenderFactory getRenderFactory();

} // RenderPackage
