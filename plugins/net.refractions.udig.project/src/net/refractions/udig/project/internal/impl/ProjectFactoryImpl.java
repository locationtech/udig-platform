/**
 * <copyright></copyright> $Id: ProjectFactoryImpl.java 30936 2008-10-29 12:21:56Z jeichar $
 */
package net.refractions.udig.project.internal.impl;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.command.CommandStack;
import net.refractions.udig.project.command.EditCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.NavCommandStack;
import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.BlackboardEntry;
import net.refractions.udig.project.internal.CatalogRef;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.ProjectRegistry;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.StyleEntry;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.palette.ColourScheme;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureResults;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.filter.Filter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.SLD;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.picocontainer.MutablePicoContainer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The EMF factory for project model objects
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class ProjectFactoryImpl extends EFactoryImpl implements ProjectFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EObject create( EClass eClass ) {
        switch( eClass.getClassifierID() ) {
        case ProjectPackage.CONTEXT_MODEL:
            return createContextModel();
        case ProjectPackage.EDIT_MANAGER:
            return createEditManager();
        case ProjectPackage.LAYER:
            return createLayer();
        case ProjectPackage.MAP:
            return createMap();
        case ProjectPackage.PROJECT:
            return createProject();
        case ProjectPackage.PROJECT_REGISTRY:
            return createProjectRegistry();
        case ProjectPackage.STYLE_BLACKBOARD:
            return createStyleBlackboard();
        case ProjectPackage.STYLE_ENTRY:
            return createStyleEntry();
        case ProjectPackage.LAYER_FACTORY:
            return createLayerFactory();
        case ProjectPackage.BLACKBOARD:
            return createBlackboard();
        case ProjectPackage.BLACKBOARD_ENTRY:
            return createBlackboardEntry();
        default:
            throw new IllegalArgumentException(
                    "The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object createFromString( EDataType eDataType, String initialValue ) {
        switch( eDataType.getClassifierID() ) {
        case ProjectPackage.COORDINATE:
            return createCoordinateFromString(eDataType, initialValue);
        case ProjectPackage.MAP_DISPLAY:
            return createMapDisplayFromString(eDataType, initialValue);
        case ProjectPackage.FEATURE_RESULTS:
            return createFeatureResultsFromString(eDataType, initialValue);
        case ProjectPackage.LIST:
            return createListFromString(eDataType, initialValue);
        case ProjectPackage.AFFINE_TRANSFORM:
            return createAffineTransformFromString(eDataType, initialValue);
        case ProjectPackage.NAV_COMMAND_STACK:
            return createNavCommandStackFromString(eDataType, initialValue);
        case ProjectPackage.IGEO_RESOURCE:
            return createIGeoResourceFromString(eDataType, initialValue);
        case ProjectPackage.FILTER:
            return createFilterFromString(eDataType, initialValue);
        case ProjectPackage.COORDINATE_REFERENCE_SYSTEM:
            return createCoordinateReferenceSystemFromString(eDataType, initialValue);
        case ProjectPackage.COMMAND_STACK:
            return createCommandStackFromString(eDataType, initialValue);
        case ProjectPackage.FEATURE:
            return createFeatureFromString(eDataType, initialValue);
        case ProjectPackage.POINT:
            return createPointFromString(eDataType, initialValue);
        case ProjectPackage.ADAPTER:
            return createAdapterFromString(eDataType, initialValue);
        case ProjectPackage.IPROGRESS_MONITOR:
            return createIProgressMonitorFromString(eDataType, initialValue);
        case ProjectPackage.QUERY:
            return createQueryFromString(eDataType, initialValue);
        case ProjectPackage.URL:
            return createURLFromString(eDataType, initialValue);
        case ProjectPackage.IMAGE_DESCRIPTOR:
            return createImageDescriptorFromString(eDataType, initialValue);
        case ProjectPackage.EDIT_COMMAND:
            return createEditCommandFromString(eDataType, initialValue);
        case ProjectPackage.NAV_COMMAND:
            return createNavCommandFromString(eDataType, initialValue);
        case ProjectPackage.ENVELOPE:
            return createEnvelopeFromString(eDataType, initialValue);
        case ProjectPackage.COMMAND:
            return createCommandFromString(eDataType, initialValue);
        case ProjectPackage.URI:
            return createURIFromString(eDataType, initialValue);
        case ProjectPackage.CATALOG_REF:
            return createCatalogRefFromString(eDataType, initialValue);
        case ProjectPackage.COLOUR_PALETTE:
            return createColourPaletteFromString(eDataType, initialValue);
        case ProjectPackage.COLOUR_SCHEME:
            return createColourSchemeFromString(eDataType, initialValue);
        case ProjectPackage.DEFAULT_COLOR:
            return createDefaultColorFromString(eDataType, initialValue);
        case ProjectPackage.MUTABLE_PICO_CONTAINER:
            return createMutablePicoContainerFromString(eDataType, initialValue);
        case ProjectPackage.REFERENCED_ENVELOPE:
            return createReferencedEnvelopeFromString(eDataType, initialValue);
        case ProjectPackage.FEATURE_EVENT:
            return createFeatureEventFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertToString( EDataType eDataType, Object instanceValue ) {
        switch( eDataType.getClassifierID() ) {
        case ProjectPackage.COORDINATE:
            return convertCoordinateToString(eDataType, instanceValue);
        case ProjectPackage.MAP_DISPLAY:
            return convertMapDisplayToString(eDataType, instanceValue);
        case ProjectPackage.FEATURE_RESULTS:
            return convertFeatureResultsToString(eDataType, instanceValue);
        case ProjectPackage.LIST:
            return convertListToString(eDataType, instanceValue);
        case ProjectPackage.AFFINE_TRANSFORM:
            return convertAffineTransformToString(eDataType, instanceValue);
        case ProjectPackage.NAV_COMMAND_STACK:
            return convertNavCommandStackToString(eDataType, instanceValue);
        case ProjectPackage.IGEO_RESOURCE:
            return convertIGeoResourceToString(eDataType, instanceValue);
        case ProjectPackage.FILTER:
            return convertFilterToString(eDataType, instanceValue);
        case ProjectPackage.COORDINATE_REFERENCE_SYSTEM:
            return convertCoordinateReferenceSystemToString(eDataType, instanceValue);
        case ProjectPackage.COMMAND_STACK:
            return convertCommandStackToString(eDataType, instanceValue);
        case ProjectPackage.FEATURE:
            return convertFeatureToString(eDataType, instanceValue);
        case ProjectPackage.POINT:
            return convertPointToString(eDataType, instanceValue);
        case ProjectPackage.ADAPTER:
            return convertAdapterToString(eDataType, instanceValue);
        case ProjectPackage.IPROGRESS_MONITOR:
            return convertIProgressMonitorToString(eDataType, instanceValue);
        case ProjectPackage.QUERY:
            return convertQueryToString(eDataType, instanceValue);
        case ProjectPackage.URL:
            return convertURLToString(eDataType, instanceValue);
        case ProjectPackage.IMAGE_DESCRIPTOR:
            return convertImageDescriptorToString(eDataType, instanceValue);
        case ProjectPackage.EDIT_COMMAND:
            return convertEditCommandToString(eDataType, instanceValue);
        case ProjectPackage.NAV_COMMAND:
            return convertNavCommandToString(eDataType, instanceValue);
        case ProjectPackage.ENVELOPE:
            return convertEnvelopeToString(eDataType, instanceValue);
        case ProjectPackage.EDIT_MANAGER_CONTROL_COMMAND:
            return convertEditManagerControlCommandToString(eDataType, instanceValue);
        case ProjectPackage.COMMAND:
            return convertCommandToString(eDataType, instanceValue);
        case ProjectPackage.URI:
            return convertURIToString(eDataType, instanceValue);
        case ProjectPackage.CATALOG_REF:
            return convertCatalogRefToString(eDataType, instanceValue);
        case ProjectPackage.COLOUR_PALETTE:
            return convertColourPaletteToString(eDataType, instanceValue);
        case ProjectPackage.COLOUR_SCHEME:
            return convertColourSchemeToString(eDataType, instanceValue);
        case ProjectPackage.DEFAULT_COLOR:
            return convertDefaultColorToString(eDataType, instanceValue);
        case ProjectPackage.MUTABLE_PICO_CONTAINER:
            return convertMutablePicoContainerToString(eDataType, instanceValue);
        case ProjectPackage.REFERENCED_ENVELOPE:
            return convertReferencedEnvelopeToString(eDataType, instanceValue);
        case ProjectPackage.FEATURE_EVENT:
            return convertFeatureEventToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException(
                    "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ContextModel createContextModel() {
        ContextModelImpl contextModel = new ContextModelImpl();
        return contextModel;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Layer createLayer() {
        LayerImpl layer = new LayerImpl();
        return layer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Project createProject() {
        ProjectImpl project = new ProjectImpl();
        return project;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectRegistry createProjectRegistry() {
        ProjectRegistryImpl projectRegistry = new ProjectRegistryImpl();
        return projectRegistry;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public StyleBlackboard createStyleBlackboard() {
        StyleBlackboardImpl styleBlackboard = new StyleBlackboardImpl();
        return styleBlackboard;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public StyleEntry createStyleEntry() {
        StyleEntryImpl styleEntry = new StyleEntryImpl();
        return styleEntry;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public LayerFactory createLayerFactory() {
        LayerFactoryImpl layerFactory = new LayerFactoryImpl();
        return layerFactory;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Blackboard createBlackboard() {
        BlackboardImpl blackboard = new BlackboardImpl();
        return blackboard;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public BlackboardEntry createBlackboardEntry() {
        BlackboardEntryImpl blackboardEntry = new BlackboardEntryImpl();
        return blackboardEntry;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EditManager createEditManager() {
        EditManagerImpl editManager = new EditManagerImpl();
        return editManager;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Query createQueryFromString( EDataType eDataType, String initialValue ) {
        return (Query) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertQueryToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }


    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public Envelope createEnvelopeFromString(EDataType eDataType,
			String initialValue) {
		if (initialValue.equals("")) //$NON-NLS-1$
			return new Envelope();
		String[] coords = initialValue.split(","); //$NON-NLS-1$
		return new ReferencedEnvelope(new Envelope(Double
				.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double
				.parseDouble(coords[2]), Double.parseDouble(coords[3])), null);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	public String convertEnvelopeToString(EDataType eDataType,
			Object instanceValue) {
		Envelope env = (Envelope) instanceValue;
		if (env.isNull())
			return ""; //$NON-NLS-1$

		return env.getMinX()
				+ "," + env.getMaxX() + "," + env.getMinY() + "," + env.getMaxY(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
    public Coordinate createCoordinateFromString( EDataType eDataType, String initialValue ) {
        return (Coordinate) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertCoordinateToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NavCommand createNavCommandFromString( EDataType eDataType, String initialValue ) {
        return (NavCommand) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertNavCommandToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Feature createFeatureFromString( EDataType eDataType, String initialValue ) {
        return (Feature) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertFeatureToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ImageDescriptor createImageDescriptorFromString( EDataType eDataType, String initialValue ) {
        return (ImageDescriptor) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertImageDescriptorToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public AffineTransform createAffineTransformFromString( EDataType eDataType, String initialValue ) {
        return (AffineTransform) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertAffineTransformToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NavCommandStack createNavCommandStackFromString( EDataType eDataType, String initialValue ) {
        return (NavCommandStack) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertNavCommandStackToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public CoordinateReferenceSystem createCoordinateReferenceSystemFromString(
            EDataType eDataType, String initialValue ) {
        return (CoordinateReferenceSystem) RenderFactory.eINSTANCE.createFromString(
                RenderPackage.eINSTANCE.getCoordinateReferenceSystem(), initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public String convertCoordinateReferenceSystemToString( EDataType eDataType,
            Object instanceValue ) {
        return RenderFactory.eINSTANCE.convertToString(RenderPackage.eINSTANCE
                .getCoordinateReferenceSystem(), instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public CommandStack createCommandStackFromString( EDataType eDataType, String initialValue ) {
        return (CommandStack) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertCommandStackToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Point createPointFromString( EDataType eDataType, String initialValue ) {
        return (Point) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertPointToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Adapter createAdapterFromString( EDataType eDataType, String initialValue ) {
        return (Adapter) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertAdapterToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }


    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public URL createURLFromString( EDataType eDataType, String initialValue ) {
        try {
            return new URL(null, initialValue, CorePlugin.RELAXED_HANDLER);
        } catch (MalformedURLException e) {
            throw (RuntimeException) new RuntimeException( e );
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertURLToString( EDataType eDataType, Object instanceValue ) {
        return ((URL)instanceValue).toExternalForm();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EditCommand createEditCommandFromString( EDataType eDataType, String initialValue ) {
        return (EditCommand) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertEditCommandToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public URI createURIFromString( EDataType eDataType, String initialValue ) {
        return (URI) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertURIToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MapCommand createCommandFromString( EDataType eDataType, String initialValue ) {
        return (MapCommand) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertCommandToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IMapDisplay createMapDisplayFromString( EDataType eDataType, String initialValue ) {
        return (IMapDisplay) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertMapDisplayToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public List createListFromString( EDataType eDataType, String initialValue ) {
        return (List) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertListToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public FeatureResults createFeatureResultsFromString( EDataType eDataType, String initialValue ) {
        return (FeatureResults) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertFeatureResultsToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IGeoResource createIGeoResourceFromString( EDataType eDataType, String initialValue ) {
        return (IGeoResource) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertIGeoResourceToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public CatalogRef createCatalogRefFromString( EDataType eDataType, String initialValue ) {
        CatalogRef ref = new CatalogRef();
        try {
            ref.parseResourceParameters(initialValue);
            return ref;
        } catch (Exception e) {
            return ref;
        }

    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public String convertCatalogRefToString( EDataType eDataType, Object instanceValue ) {
        try {
            return ((CatalogRef) instanceValue).marshalConnectionParameters();
        } catch (Exception e) {
            return ""; //$NON-NLS-1$
        }
    }

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public BrewerPalette createBrewerPaletteFromString(EDataType eDataType,
			String initialValue) {
		return PlatformGIS.getColorBrewer().getPalette(initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String convertBrewerPaletteToString(EDataType eDataType,
			Object instanceValue) {
		return ((BrewerPalette) instanceValue).getName();
	}

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public BrewerPalette createColourPaletteFromString( EDataType eDataType, String initialValue ) {
        return PlatformGIS.getColorBrewer().getPalette(initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public String convertColourPaletteToString( EDataType eDataType, Object instanceValue ) {
        return ((BrewerPalette) instanceValue).getName();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public ColourScheme createColourSchemeFromString( EDataType eDataType, String initialValue ) {
        String[] parts = initialValue.split(";"); //$NON-NLS-1$
        if (parts.length != 4) {
            //something is wrong...
            BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette("Dark2"); //$NON-NLS-1$
            return new ColourScheme(palette, 0);
        }
        String[] strap = parts[2].split(",");  //$NON-NLS-1$
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(parts[0]);
        HashMap<Integer, Integer> colourMapping = new HashMap<Integer, Integer>();
        for (int i = 0; i < strap.length; i++) {
            colourMapping.put(i, Integer.parseInt(strap[i]));
        }
        String[] strap2 = parts[3].split(",");  //$NON-NLS-1$
        HashMap<String, Integer> idMapping = new HashMap<String, Integer>();
        for (int i = 0; i < strap2.length; i++) {
            idMapping.put(strap2[i], Integer.parseInt(strap2[++i]));
        }
        ColourScheme scheme = new ColourScheme(palette, colourMapping, idMapping, strap.length, Integer.parseInt(parts[1]));
        return scheme;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public String convertColourSchemeToString( EDataType eDataType, Object instanceValue ) {
        ColourScheme instance = (ColourScheme) instanceValue;
        String value = instance.getColourPalette().getName() + ";"; //$NON-NLS-1$;
        int numItems = instance.getSizeScheme();
        int numColours = instance.getSizePalette();
        value = value + numColours + ";"; //$NON-NLS-1$
        HashMap colourMap = instance.getColourMap();
        for (int i = 0; i < numItems; i++) {
            if (i > 0)
                value = value + ","; //$NON-NLS-1$
            value = value + colourMap.get(i);
        }
        value = value + ";"; //$NON-NLS-1$
        HashMap<String, Integer> idMap = instance.getIdMap();
        Iterator<Entry<String, Integer>> it = idMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            value = value + entry.getKey() + "," + entry.getValue(); //$NON-NLS-1$
            if (it.hasNext()) {
                value = value + ","; //$NON-NLS-1$
            }
        }
        return value;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Color createDefaultColorFromString( EDataType eDataType, String initialValue ) {

        return SLD.toColor(initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public String convertDefaultColorToString( EDataType eDataType, Object instanceValue ) {
        Color instance = (Color) instanceValue;
        return SLD.toHTMLColor(instance);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Color createColorFromString( EDataType eDataType, String initialValue ) {

        return SLD.toColor(initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public String convertColorToString( EDataType eDataType, Object instanceValue ) {
        Color instance = (Color) instanceValue;
        return SLD.toHTMLColor(instance);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MutablePicoContainer createMutablePicoContainerFromString( EDataType eDataType,
            String initialValue ) {
        return (MutablePicoContainer) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertMutablePicoContainerToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ReferencedEnvelope createReferencedEnvelopeFromString(
			EDataType eDataType, String initialValue) {
		String[] parts = initialValue.split("@", 2);
		Envelope env = createEnvelopeFromString(ProjectPackage.eINSTANCE.getEnvelope(), parts[0]);
		CoordinateReferenceSystem crs = createCoordinateReferenceSystemFromString(ProjectPackage.eINSTANCE.getCoordinateReferenceSystem(), parts[0]);
		return new ReferencedEnvelope(env,crs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String convertReferencedEnvelopeToString(EDataType eDataType,
			Object instanceValue) {
		String env = convertEnvelopeToString(ProjectPackage.eINSTANCE.getEnvelope(), instanceValue);
		String crs = convertCoordinateReferenceSystemToString(ProjectPackage.eINSTANCE.getCoordinateReferenceSystem(), ((ReferencedEnvelope)instanceValue).getCoordinateReferenceSystem());
		return env+"@"+crs;
	}

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public FeatureEvent createFeatureEventFromString( EDataType eDataType, String initialValue ) {
        return (FeatureEvent) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertFeatureEventToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Filter createFilterFromString( EDataType eDataType, String initialValue ) {
        return (Filter) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertFilterToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IProgressMonitor createIProgressMonitorFromString( EDataType eDataType,
            String initialValue ) {
        return (IProgressMonitor) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertIProgressMonitorToString( EDataType eDataType, Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String convertEditManagerControlCommandToString( EDataType eDataType,
            Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ProjectPackage getProjectPackage() {
        return (ProjectPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static ProjectPackage getPackage() {
        return ProjectPackage.eINSTANCE;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Map createMap() {
        MapImpl map = new MapImpl();
        map.setLayerFactory(createLayerFactory());
        map.setBlackBoardInternal(createBlackboard());
        return map;
    }

    private void runMapCreationInterceptors(Map map) {
        List<IConfigurationElement> interceptors = ExtensionPointList.getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT); //$NON-NLS-1$
        for( IConfigurationElement element : interceptors ) {
            if( !"mapCreation".equals(element.getName())) //$NON-NLS-1$
                continue;
            try  {
               MapInterceptor interceptor=(MapInterceptor) element.createExecutableExtension("class"); //$NON-NLS-1$
               interceptor.run(map);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see net.refractions.udig.project.ProjectFactory#createMap(net.refractions.udig.project.Project,
     *      java.lang.String, java.util.List)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Map createMap( Project owner, String name, List layers ) {
        Map map = createMap();

        map.setName(name);
        map.setProjectInternal(owner);
        map.setViewportModelInternal(RenderFactory.eINSTANCE.createViewportModel());
        runMapCreationInterceptors(map);
        if(!layers.isEmpty())
            map.getLayersInternal().addAll(layers);

        return map;
    }

} // ProjectFactoryImpl
