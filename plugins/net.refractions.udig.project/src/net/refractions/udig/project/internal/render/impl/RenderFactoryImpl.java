/**
 * <copyright></copyright> $Id: RenderFactoryImpl.java 30936 2008-10-29 12:21:56Z jeichar $
 */
package net.refractions.udig.project.internal.render.impl;

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

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.impl.ProjectFactoryImpl;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.RendererDecorator;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.FactoryFinder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.osgi.framework.Bundle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory </b>! <!--
 * end-user-doc -->
 *
 * @generated
 */
public class RenderFactoryImpl extends EFactoryImpl implements RenderFactory {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$
	private static final String CARTESIAN_2D = "CARTESIAN_2D";
	private static final String CARTESIAN_3D = "CARTESIAN_3D";
	private static final String GENERIC_2D = "GENERIC_2D";
	private static final String GENERIC_3D = "GENERIC_3D";

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public RenderFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
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
	 * @see net.refractions.udig.project.internal.render.RenderFactory#createRenderExecutor()
	 */
	public RenderExecutor createRenderExecutor() {
		return new RenderExecutorImpl();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case RenderPackage.COORDINATE_REFERENCE_SYSTEM:
			return createCoordinateReferenceSystemFromString(eDataType,
					initialValue);
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
		default:
			throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case RenderPackage.COORDINATE_REFERENCE_SYSTEM:
			return convertCoordinateReferenceSystemToString(eDataType,
					instanceValue);
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
		default:
			throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
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
	 *
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
		for (Iterator<IConfigurationElement> iter = ExtensionPointList
				.getExtensionPointList(RenderExecutor.EXTENSION_ID).iterator(); iter
				.hasNext();) {
			IConfigurationElement elem = iter.next();
			try {
				Bundle bundle = Platform.getBundle(elem
						.getNamespaceIdentifier());
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

				public int compare(IConfigurationElement o1,
						IConfigurationElement o2) {
					try {
						Bundle bundle = Platform.getBundle(o1
								.getNamespaceIdentifier());
						Class clazz1 = bundle.loadClass(o1
								.getAttribute(RenderExecutor.RENDERER_ATTR));

						bundle = Platform
								.getBundle(o2.getNamespaceIdentifier());
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
					return 1 + getDistance(rendererClass.getSuperclass(),
							target);
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
		for (Iterator<IConfigurationElement> iter = ExtensionPointList
				.getExtensionPointList(RenderExecutor.EXTENSION_ID).iterator(); iter
				.hasNext();) {
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
	 *
	 * @generated
	 */
	public Rectangle createRectangleFromString(EDataType eDataType,
			String initialValue) {
		return (Rectangle) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertRectangleToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AffineTransform createAffineTransformFromString(EDataType eDataType,
			String initialValue) {
		return (AffineTransform) super
				.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertAffineTransformToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Point createPointFromString(EDataType eDataType, String initialValue) {
		return (Point) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertPointToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public IMapDisplay createMapDisplayFromString(EDataType eDataType,
			String initialValue) {
		return (IMapDisplay) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertMapDisplayToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public IProgressMonitor createIProgressMonitorFromString(
			EDataType eDataType, String initialValue) {
		return (IProgressMonitor) super.createFromString(eDataType,
				initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertIProgressMonitorToString(EDataType eDataType,
			Object instanceValue) {
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertInfoListToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Graphics2D createGraphics2DFromString(EDataType eDataType,
			String initialValue) {
		return (Graphics2D) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertGraphics2DToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Coordinate createCoordinateFromString(EDataType eDataType,
			String initialValue) {
		return (Coordinate) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertCoordinateToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public CoordinateReferenceSystem createCoordinateReferenceSystemFromString(
			EDataType eDataType, String initialValue) {
		if (initialValue == CARTESIAN_2D) {
			return DefaultEngineeringCRS.CARTESIAN_2D;
		} else if (initialValue == CARTESIAN_3D) {
			return DefaultEngineeringCRS.CARTESIAN_3D;
		} else if (initialValue == GENERIC_2D) {
			return DefaultEngineeringCRS.GENERIC_2D;
		} else if (initialValue == GENERIC_3D) {
			return DefaultEngineeringCRS.GENERIC_3D;
		}

		try {
			return FactoryFinder.getCRSFactory(null)
					.createFromWKT(initialValue);
		} catch (Exception e) {
			return ViewportModelImpl.getDefaultCRS();
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public String convertCoordinateReferenceSystemToString(EDataType eDataType,
			Object instanceValue) {
		try {
			if (instanceValue == DefaultEngineeringCRS.CARTESIAN_2D) {
				return CARTESIAN_2D;
			} else if (instanceValue == DefaultEngineeringCRS.CARTESIAN_3D) {
				return CARTESIAN_3D;
			} else if (instanceValue == DefaultEngineeringCRS.GENERIC_2D) {
				return GENERIC_2D;
			} else if (instanceValue == DefaultEngineeringCRS.GENERIC_3D) {
				return GENERIC_3D;
			}
			return ((CoordinateReferenceSystem) instanceValue).toWKT().replace(
					"\n", " "); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			ProjectPlugin.log("Couldn't write crs"); //$NON-NLS-1$
			return DefaultGeographicCRS.WGS84.toWKT().replace("\n", " "); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public Envelope createEnvelopeFromString(EDataType eDataType,
			String initialValue) {
		return (Envelope) ProjectFactory.eINSTANCE.createFromString(
				ProjectPackage.eINSTANCE.getEnvelope(), initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public String convertEnvelopeToString(EDataType eDataType,
			Object instanceValue) {
		return ProjectFactory.eINSTANCE.convertToString(
				ProjectPackage.eINSTANCE.getEnvelope(), instanceValue);
	}
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ReferencedEnvelope createReferencedEnvelopeFromString(
			EDataType eDataType, String initialValue) {
		return (ReferencedEnvelope) ProjectFactory.eINSTANCE.createFromString(
				ProjectPackage.eINSTANCE.getReferencedEnvelope(), initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String convertReferencedEnvelopeToString(EDataType eDataType,
			Object instanceValue) {
		return ProjectFactory.eINSTANCE.convertToString(
				ProjectPackage.eINSTANCE.getReferencedEnvelope(), instanceValue);
	}


	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BufferedImage createBufferedImageFromString(EDataType eDataType,
			String initialValue) {
		return (BufferedImage) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertBufferedImageToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Query createQueryFromString(EDataType eDataType, String initialValue) {
		return (Query) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertQueryToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public List createListFromString(EDataType eDataType, String initialValue) {
		return (List) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertListToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public IGeoResource createIGeoResourceFromString(EDataType eDataType,
			String initialValue) {
		return (IGeoResource) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertIGeoResourceToString(EDataType eDataType,
			Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public RenderPackage getRenderPackage() {
		return (RenderPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	public static RenderPackage getPackage() {
		return RenderPackage.eINSTANCE;
	}

	/**
	 * @see net.refractions.udig.project.internal.render.RenderFactory#createCompositeRenderer()
	 */
	public MultiLayerRenderer createCompositeRenderer() {
		return new CompositeRendererImpl();
	}

}
