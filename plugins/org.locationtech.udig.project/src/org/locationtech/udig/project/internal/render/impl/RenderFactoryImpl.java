/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import java.util.SortedSet;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererDecorator;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.joda.time.DateTime;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.osgi.framework.Bundle;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory </b>! <!--
 * end-user-doc -->
 * @generated
 */
public class RenderFactoryImpl extends EFactoryImpl implements RenderFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RenderFactory init() {
        try {
            RenderFactory theRenderFactory = (RenderFactory) EPackage.Registry.INSTANCE
                    .getEFactory("http:///net/refractions/udig/project/internal/render.ecore"); //$NON-NLS-1$ 
            if (theRenderFactory != null) {
                return theRenderFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new RenderFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    public RenderFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case RenderPackage.RENDER_EXECUTOR:
            return createRenderExecutor();
        case RenderPackage.RENDER_MANAGER:
            return createRenderManager();
        case RenderPackage.VIEWPORT_MODEL:
            return createViewportModel();
        default:
            throw new IllegalArgumentException(
                    "The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * @see org.locationtech.udig.project.internal.render.RenderFactory#createRenderExecutor()
     */
    public RenderExecutor createRenderExecutor() {
        return new RenderExecutorImpl();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
        case RenderPackage.COORDINATE_REFERENCE_SYSTEM:
            return createCoordinateReferenceSystemFromString(eDataType, initialValue);
        case RenderPackage.ENVELOPE:
            return createEnvelopeFromString(eDataType, initialValue);
        case RenderPackage.IGEO_RESOURCE:
            return createIGeoResourceFromString(eDataType, initialValue);
        case RenderPackage.BUFFERED_IMAGE:
            return createBufferedImageFromString(eDataType, initialValue);
        case RenderPackage.RECTANGLE:
            return createRectangleFromString(eDataType, initialValue);
        case RenderPackage.QUERY:
            return createQueryFromString(eDataType, initialValue);
        case RenderPackage.GRAPHICS2_D:
            return createGraphics2DFromString(eDataType, initialValue);
        case RenderPackage.LIST:
            return createListFromString(eDataType, initialValue);
        case RenderPackage.COORDINATE:
            return createCoordinateFromString(eDataType, initialValue);
        case RenderPackage.POINT:
            return createPointFromString(eDataType, initialValue);
        case RenderPackage.AFFINE_TRANSFORM:
            return createAffineTransformFromString(eDataType, initialValue);
        case RenderPackage.MAP_DISPLAY:
            return createMapDisplayFromString(eDataType, initialValue);
        case RenderPackage.IPROGRESS_MONITOR:
            return createIProgressMonitorFromString(eDataType, initialValue);
        case RenderPackage.RENDER_EXCEPTION:
            return createRenderExceptionFromString(eDataType, initialValue);
        case RenderPackage.SORTED_SET:
            return createSortedSetFromString(eDataType, initialValue);
        case RenderPackage.REFERENCED_ENVELOPE:
            return createReferencedEnvelopeFromString(eDataType, initialValue);
        case RenderPackage.DATE_TIME:
            return createDateTimeFromString(eDataType, initialValue);
        case RenderPackage.ILLEGAL_ARGUMENT_EXCEPTION:
            return createIllegalArgumentExceptionFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
        case RenderPackage.COORDINATE_REFERENCE_SYSTEM:
            return convertCoordinateReferenceSystemToString(eDataType, instanceValue);
        case RenderPackage.ENVELOPE:
            return convertEnvelopeToString(eDataType, instanceValue);
        case RenderPackage.IGEO_RESOURCE:
            return convertIGeoResourceToString(eDataType, instanceValue);
        case RenderPackage.BUFFERED_IMAGE:
            return convertBufferedImageToString(eDataType, instanceValue);
        case RenderPackage.RECTANGLE:
            return convertRectangleToString(eDataType, instanceValue);
        case RenderPackage.QUERY:
            return convertQueryToString(eDataType, instanceValue);
        case RenderPackage.GRAPHICS2_D:
            return convertGraphics2DToString(eDataType, instanceValue);
        case RenderPackage.LIST:
            return convertListToString(eDataType, instanceValue);
        case RenderPackage.COORDINATE:
            return convertCoordinateToString(eDataType, instanceValue);
        case RenderPackage.POINT:
            return convertPointToString(eDataType, instanceValue);
        case RenderPackage.AFFINE_TRANSFORM:
            return convertAffineTransformToString(eDataType, instanceValue);
        case RenderPackage.MAP_DISPLAY:
            return convertMapDisplayToString(eDataType, instanceValue);
        case RenderPackage.IPROGRESS_MONITOR:
            return convertIProgressMonitorToString(eDataType, instanceValue);
        case RenderPackage.RENDER_EXCEPTION:
            return convertRenderExceptionToString(eDataType, instanceValue);
        case RenderPackage.SORTED_SET:
            return convertSortedSetToString(eDataType, instanceValue);
        case RenderPackage.REFERENCED_ENVELOPE:
            return convertReferencedEnvelopeToString(eDataType, instanceValue);
        case RenderPackage.DATE_TIME:
            return convertDateTimeToString(eDataType, instanceValue);
        case RenderPackage.ILLEGAL_ARGUMENT_EXCEPTION:
            return convertIllegalArgumentExceptionToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderManager createRenderManager() {
        RenderManagerImpl renderManager = new RenderManagerImpl();
        return renderManager;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public RenderManager createRenderManagerViewer() {
        RenderManagerImpl renderManager = new RenderManagerImpl();
        renderManager.setViewer(true);
        return renderManager;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ViewportModel createViewportModel() {
        ViewportModelImpl viewportModel = new ViewportModelImpl();
        return viewportModel;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public ViewportModel createViewportModelViewer() {
        ViewportModelImpl viewportModel = new ViewportModelImpl();
        viewportModel.setViewer(true);
        return viewportModel;
    }

    /**
     * <!-- begin-user-doc --> Processes the RenderExecutor Extension Point and
     * creates the appropriate Executor <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public RenderExecutor createRenderExecutor(Renderer renderer) {

        RenderExecutor executor = locateMatch(renderer);
        if (executor == null)
            executor = locateClosestFit(renderer);

        executor.setRenderer(renderer);
        return executor;
    }

    /**
     * searches through the RenderExecutor Extension point and locates the
     * RenderExecutor whose associated renderer is closest in hierarchical terms
     * to the renderer parameter.
     * 
     * @return The closest renderexecutor for the renderer.
     */
    private RenderExecutor locateClosestFit(Renderer r) {
        Renderer ofInterest = r;
        if (r instanceof RendererDecorator) {
            ofInterest = ((RendererDecorator) r).getRenderer();
        }
        final Renderer renderer = ofInterest;
        List<IConfigurationElement> list = new ArrayList<IConfigurationElement>();
        for (Iterator<IConfigurationElement> iter = ExtensionPointList.getExtensionPointList(
                RenderExecutor.EXTENSION_ID).iterator(); iter.hasNext();) {
            IConfigurationElement elem = iter.next();
            try {
                Bundle bundle = Platform.getBundle(elem.getNamespaceIdentifier());
                Class<?> rendererClass = bundle.loadClass(elem
                        .getAttribute(RenderExecutor.RENDERER_ATTR));
                if (rendererClass.isAssignableFrom(renderer.getClass()))
                    list.add(elem);
            } catch (Exception e) {
                ProjectPlugin.log(null, e);
                // do nothing
            }
        }
        if (!list.isEmpty()) {
            Collections.sort(list, new Comparator<IConfigurationElement>() {

                public int compare(IConfigurationElement o1, IConfigurationElement o2) {
                    try {
                        Bundle bundle = Platform.getBundle(o1.getNamespaceIdentifier());
                        Class clazz1 = bundle.loadClass(o1
                                .getAttribute(RenderExecutor.RENDERER_ATTR));

                        bundle = Platform.getBundle(o2.getNamespaceIdentifier());
                        Class clazz2 = bundle.loadClass(o2
                                .getAttribute(RenderExecutor.RENDERER_ATTR));

                        int dist1 = getDistance(renderer.getClass(), clazz1);
                        int dist2 = getDistance(renderer.getClass(), clazz2);
                        if (dist1 == dist2)
                            return 0;
                        return dist1 < dist2 ? -1 : 1;
                    } catch (Exception e) {
                        ProjectPlugin.log(null, e);
                        // do nothing
                    }
                    return 0;
                }

                private int getDistance(Class<?> rendererClass, Class<?> target) {
                    if (!target.isAssignableFrom(rendererClass))
                        return -1;
                    if (target == rendererClass)
                        return 0;

                    for (Class iClass : rendererClass.getInterfaces()) {
                        if (iClass == target)
                            return 1;
                        else {
                            int distance = getDistance(iClass, target);
                            if (distance != -1)
                                return 1 + distance;
                        }
                    }
                    return 1 + getDistance(rendererClass.getSuperclass(), target);
                }
            });

            try {
                return (RenderExecutor) list.get(0).createExecutableExtension(
                        RenderExecutor.EXECUTOR_ATTR);
            } catch (CoreException e) {
                ProjectPlugin.log(null, e);
            }
        }
        return createRenderExecutor();
    }

    /**
     * searches through the RenderExecutor Extension point and locates the
     * RenderExecutor whose associated renderer and exact match to the renderer
     * class passed in as a parameter
     * 
     * @return a renderExecutor for the renderer
     */
    private RenderExecutor locateMatch(Renderer renderer) {
        Renderer ofInterest = renderer;
        if (renderer instanceof RendererDecorator) {
            ofInterest = ((RendererDecorator) renderer).getRenderer();
        }
        List<IConfigurationElement> list = new ArrayList<IConfigurationElement>();
        for (Iterator<IConfigurationElement> iter = ExtensionPointList.getExtensionPointList(
                RenderExecutor.EXTENSION_ID).iterator(); iter.hasNext();) {
            IConfigurationElement elem = iter.next();
            if (elem.getAttribute(RenderExecutor.RENDERER_ATTR).equals(
                    ofInterest.getClass().getName()))
                list.add(elem);
        }

        if (!list.isEmpty()) {
            // TODO if list has more than one element determine which is best.
            try {
                return (RenderExecutor) list.get(0).createExecutableExtension(
                        RenderExecutor.EXECUTOR_ATTR);
            } catch (CoreException e) {
                ProjectPlugin.log(null, e);
            }
        }
        return null;

    }

    /**
     * Creates a new RenderContext object for a renderer that renders
     * selections.
     * 
     * @param selection
     *            Indicates whether the getQuery method will return a query for
     *            selected features.
     * @return a new RenderContext object for a renderer that renders
     *         selections.
     */
    public RenderContextImpl createRenderContext(boolean selection) {
        RenderContextImpl renderContext = new RenderContextImpl(selection);
        return renderContext;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Rectangle createRectangleFromString(EDataType eDataType, String initialValue) {
        return (Rectangle) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertRectangleToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public AffineTransform createAffineTransformFromString(EDataType eDataType, String initialValue) {
        return (AffineTransform) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertAffineTransformToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Point createPointFromString(EDataType eDataType, String initialValue) {
        return (Point) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertPointToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IMapDisplay createMapDisplayFromString(EDataType eDataType, String initialValue) {
        return (IMapDisplay) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertMapDisplayToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IProgressMonitor createIProgressMonitorFromString(EDataType eDataType,
            String initialValue) {
        return (IProgressMonitor) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertIProgressMonitorToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated public InfoList createInfoListFromString(EDataType eDataType,
     *            String initialValue) { return
     *            (InfoList)super.createFromString(eDataType, initialValue); }
     */

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RenderException createRenderExceptionFromString(EDataType eDataType, String initialValue) {
        return (RenderException) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertRenderExceptionToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SortedSet<?> createSortedSetFromString(EDataType eDataType, String initialValue) {
        return (SortedSet<?>) super.createFromString(initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertSortedSetToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Graphics2D createGraphics2DFromString(EDataType eDataType, String initialValue) {
        return (Graphics2D) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertGraphics2DToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Coordinate createCoordinateFromString(EDataType eDataType, String initialValue) {
        return (Coordinate) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertCoordinateToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public CoordinateReferenceSystem createCoordinateReferenceSystemFromString(EDataType eDataType,
            String initialValue) {

        return (CoordinateReferenceSystem) ProjectFactory.eINSTANCE.createFromString(
                ProjectPackage.eINSTANCE.getCoordinateReferenceSystem(), initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String convertCoordinateReferenceSystemToString(EDataType eDataType, Object instanceValue) {
        return ProjectFactory.eINSTANCE.convertToString(
                ProjectPackage.eINSTANCE.getCoordinateReferenceSystem(), instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Envelope createEnvelopeFromString(EDataType eDataType, String initialValue) {
        return (Envelope) ProjectFactory.eINSTANCE.createFromString(
                ProjectPackage.eINSTANCE.getEnvelope(), initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String convertEnvelopeToString(EDataType eDataType, Object instanceValue) {
        return ProjectFactory.eINSTANCE.convertToString(ProjectPackage.eINSTANCE.getEnvelope(),
                instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public ReferencedEnvelope createReferencedEnvelopeFromString(EDataType eDataType,
            String initialValue) {
        return (ReferencedEnvelope) ProjectFactory.eINSTANCE.createFromString(
                ProjectPackage.eINSTANCE.getReferencedEnvelope(), initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public String convertReferencedEnvelopeToString(EDataType eDataType, Object instanceValue) {
        return ProjectFactory.eINSTANCE.convertToString(
                ProjectPackage.eINSTANCE.getReferencedEnvelope(), instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DateTime createDateTimeFromString(EDataType eDataType, String initialValue) {
        return (DateTime) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertDateTimeToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IllegalArgumentException createIllegalArgumentExceptionFromString(EDataType eDataType,
            String initialValue) {
        return (IllegalArgumentException) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertIllegalArgumentExceptionToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public BufferedImage createBufferedImageFromString(EDataType eDataType, String initialValue) {
        return (BufferedImage) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertBufferedImageToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Query createQueryFromString(EDataType eDataType, String initialValue) {
        return (Query) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertQueryToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public List createListFromString(EDataType eDataType, String initialValue) {
        return (List) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertListToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IGeoResource createIGeoResourceFromString(EDataType eDataType, String initialValue) {
        return (IGeoResource) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertIGeoResourceToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    public TiledCompositeRendererImpl createTiledCompositeRenderer() {
        return new TiledCompositeRendererImpl();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderPackage getRenderPackage() {
        return (RenderPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static RenderPackage getPackage() {
        return RenderPackage.eINSTANCE;
    }

    /**
     * @see org.locationtech.udig.project.internal.render.RenderFactory#createCompositeRenderer()
     */
    public MultiLayerRenderer createCompositeRenderer() {
        return new CompositeRendererImpl();
    }

}
