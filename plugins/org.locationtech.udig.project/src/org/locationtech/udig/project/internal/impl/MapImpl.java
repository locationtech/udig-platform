/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.command.CommandListener;
import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.command.CommandStack;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.NavCommandStack;
import org.locationtech.udig.project.internal.Blackboard;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.DefaultErrorHandler;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.locationtech.udig.ui.palette.ColourScheme;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Not;
import org.opengis.filter.spatial.SpatialOperator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Project element used by MapEditorInput.
 * <p>
 * For more information please see *org.locationtech.udig.project.internal.ui.mapEditorInput*.
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class MapImpl extends EObjectImpl implements Map {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getName()
     * @generated NOT
     * @ordered
     */
    protected static final String NAME_EDEFAULT = "";

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getName()
     * @generated NOT
     * @ordered
     */
    protected volatile String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getProjectInternal() <em>Project Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getProjectInternal()
     * @generated not
     * @ordered
     */
    protected volatile Project projectInternal = null;

    /**
     * The cached value of the '{@link #getContextModel() <em>Context Model</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getContextModel()
     * @generated not
     * @ordered
     */
    protected volatile ContextModel contextModel = null;

    /**
     * The default value of the '{@link #getAbstract() <em>Abstract</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getAbstract()
     * @generated NOT
     * @ordered
     */
    protected static final String ABSTRACT_EDEFAULT = "";

    /**
     * The cached value of the '{@link #getAbstract() <em>Abstract</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getAbstract()
     * @generated not
     * @ordered
     */
    protected volatile String abstract_ = ABSTRACT_EDEFAULT;

    /**
     * The default value of the '{@link #getNavCommandStack() <em>Nav Command Stack</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getNavCommandStack()
     * @generated
     * @ordered
     */
    protected static final NavCommandStack NAV_COMMAND_STACK_EDEFAULT = null;

    /**
     * The default value of the '{@link #getCommandStack() <em>Command Stack</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getCommandStack()
     * @generated
     * @ordered
     */
    protected static final CommandStack COMMAND_STACK_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLayerFactory() <em>Layer Factory</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getLayerFactory()
     * @generated not
     * @ordered
     */
    protected volatile LayerFactory layerFactory = null;

    /**
     * The cached value of the '{@link #getViewportModelInternal() <em>Viewport Model Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getViewportModelInternal()
     * @generated not
     * @ordered
     */
    protected volatile ViewportModel viewportModelInternal = null;

    /**
     * The default value of the '{@link #getColorPalette() <em>Color Palette</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getColorPalette()
     * @generated NOT
     * @ordered
     */
    protected final BrewerPalette COLOR_PALETTE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getColorPalette() <em>Color Palette</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getColorPalette()
     * @generated not
     * @ordered
     */
    protected volatile BrewerPalette colorPalette = null;

    /**
     * The cached value of the '{@link #getEditManagerInternal() <em>Edit Manager Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getEditManagerInternal()
     * @generated not 
     * @ordered
     */
    protected volatile EditManager editManagerInternal = null;

    /**
     * The cached value of the '{@link #getRenderManagerInternal() <em>Render Manager Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getRenderManagerInternal()
     * @generated not 
     * @ordered
     */
    protected volatile RenderManager renderManagerInternal = null;

    /**
     * The default value of the '{@link #getColourScheme() <em>Colour Scheme</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getColourScheme()
     * @generated NOT
     * @ordered
     */
    protected static final ColourScheme COLOUR_SCHEME_EDEFAULT = null;

    private static final List<Layer> EMPTY_LIST = Collections.<Layer> emptyList();

    /**
     * The cached value of the '{@link #getColourScheme() <em>Colour Scheme</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getColourScheme()
     * @generated not 
     * @ordered
     */
    protected volatile ColourScheme colourScheme = null;

    /**
     * The cached value of the '{@link #getBlackBoardInternal() <em>Black Board Internal</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getBlackBoardInternal()
     * @generated not
     * @ordered
     */
    protected volatile Blackboard blackBoardInternal = null;

    /**
     * The cached value of the '{@link #getLegend() <em>Legend</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLegend()
     * @generated
     * @ordered
     */
    protected EList<ILegendItem> legend;

    /**
     * <p>
     * A Listener to be passed on to the command manager. Will be notified of command completion.
     * </p>
     *
     * @author aalam
     * @since 0.6.0
     */
    public class MapCommandListener implements CommandListener {
        /** <code>COMMAND</code> field */
        public static final int COMMAND = 0;

        /** <code>NAV_COMMAND</code> field */
        public static final int NAV_COMMAND = 1;

        /**
         * CommandManager will call this function once the command is completed.
         *
         * @param commandType
         */
        public void commandExecuted(int commandType) {
            switch (commandType) {
            case COMMAND:
                notifyCommandDone();
                break;
            case NAV_COMMAND:
                notifyNavCommandDone();
                break;
            }
        }

    };

    private volatile CommandManager commandManager;

    private volatile CommandManager navCommandManager;

    private final Lock lock = new UDIGDisplaySafeLock();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public MapImpl() {
        addMapListenerAdapter();
        setContextModel(new ContextModelImpl());
    }

    /**
     * Adds an adapter that fires the events to IMapListeners
     */
    @SuppressWarnings("unchecked")
    private void addMapListenerAdapter() {
        eAdapters().add(new EMFEventListenerToMapEvents(this));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.MAP;
    }

    /**
     * Retrieves this map's project, searching its parents until it finds one, or returns null if it
     * can't find one.
     *
     * @uml.property name="projectInternal"
     */
    public Project getProjectInternal() {
        Project genResult = getProjectInternalGen();
        if (genResult == null) {
            EObject parent = eContainer();
            while (parent != null) {
                if (parent instanceof Project) {
                    return (Project) parent;
                }

                parent = parent.eContainer();
            }
        }
        return genResult;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Project getProjectInternalGen() {
        if (projectInternal != null && projectInternal.eIsProxy()) {
            InternalEObject oldProjectInternal = (InternalEObject) projectInternal;
            projectInternal = (Project) eResolveProxy(oldProjectInternal);
            if (projectInternal != oldProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ProjectPackage.MAP__PROJECT_INTERNAL, oldProjectInternal,
                            projectInternal));
            }
        }
        return projectInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Project basicGetProjectInternal() {
        return projectInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetProjectInternal(Project newProjectInternal,
            NotificationChain msgs) {
        Project oldProjectInternal = projectInternal;
        projectInternal = newProjectInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__PROJECT_INTERNAL, oldProjectInternal, newProjectInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public void setProjectInternal(Project newProjectInternal) {
        if (newProjectInternal != projectInternal) {
            NotificationChain msgs = null;

            if (projectInternal != null) {
                msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            }
            if (newProjectInternal != null)
                msgs = ((InternalEObject) newProjectInternal).eInverseAdd(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            msgs = basicSetProjectInternal(newProjectInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__PROJECT_INTERNAL, newProjectInternal, newProjectInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="contextModel"
     * @generated NOT
     */
    @SuppressWarnings("deprecation")
    public ContextModel getContextModel() {
        if (contextModel == null) {
            lock.lock();
            try {
                if (contextModel == null)
                    setContextModel(ProjectFactory.eINSTANCE.createContextModel());
            } finally {
                lock.unlock();
            }
        }
        return contextModel;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("deprecation")
    public NotificationChain basicSetContextModel(ContextModel newContextModel,
            NotificationChain msgs) {
        ContextModel oldContextModel = contextModel;
        contextModel = newContextModel;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__CONTEXT_MODEL, oldContextModel, newContextModel);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    final ContextModelListenerAdapter contextModelListener = new EMFCompositionEventToMapCompositionEventListener(
            this);

    /**
     * @see org.locationtech.udig.project.internal.Map#setContextModel(org.locationtech.udig.project.ContextModel)
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void setContextModel(ContextModel newContextModel) {
        if (contextModel != null)
            contextModel.eAdapters().remove(contextModelListener);
        if (newContextModel != null) {
            newContextModel.eAdapters().add(contextModelListener);
        }
        setContextModelGen(newContextModel);
    }

    /**
     * @generated
     */
    @SuppressWarnings("deprecation")
    public void setContextModelGen(ContextModel newContextModel) {
        if (newContextModel != contextModel) {
            NotificationChain msgs = null;
            if (contextModel != null)
                msgs = ((InternalEObject) contextModel).eInverseRemove(this,
                        ProjectPackage.CONTEXT_MODEL__MAP, ContextModel.class, msgs);
            if (newContextModel != null)
                msgs = ((InternalEObject) newContextModel).eInverseAdd(this,
                        ProjectPackage.CONTEXT_MODEL__MAP, ContextModel.class, msgs);
            msgs = basicSetContextModel(newContextModel, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__CONTEXT_MODEL, newContextModel, newContextModel));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="viewportModelInternal"
     * @generated NOT
     */
    public ViewportModel getViewportModelInternal() {
        if (viewportModelInternal == null)
            setViewportModelInternalGen(RenderFactory.eINSTANCE.createViewportModel());
        return viewportModelInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetViewportModelInternal(ViewportModel newViewportModelInternal,
            NotificationChain msgs) {
        ViewportModel oldViewportModelInternal = viewportModelInternal;
        viewportModelInternal = newViewportModelInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, oldViewportModelInternal,
                    newViewportModelInternal);
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
     * @uml.property name="viewportModelInternal"
     */
    @SuppressWarnings("unchecked")
    public void setViewportModelInternal(ViewportModel newViewportModelInternal) {
        setViewportModelInternalGen(newViewportModelInternal);
        if (getRenderManager() != null) {
            getRenderManagerInternal().setViewportModelInternal(newViewportModelInternal);
        }
        newViewportModelInternal.eAdapters().add(adapter);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setViewportModelInternalGen(ViewportModel newViewportModelInternal) {
        if (newViewportModelInternal != viewportModelInternal) {
            NotificationChain msgs = null;
            if (viewportModelInternal != null)
                msgs = ((InternalEObject) viewportModelInternal).eInverseRemove(this,
                        RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, ViewportModel.class, msgs);
            if (newViewportModelInternal != null)
                msgs = ((InternalEObject) newViewportModelInternal).eInverseAdd(this,
                        RenderPackage.VIEWPORT_MODEL__MAP_INTERNAL, ViewportModel.class, msgs);
            msgs = basicSetViewportModelInternal(newViewportModelInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, newViewportModelInternal,
                    newViewportModelInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public BrewerPalette getColorPalette() {
        if (colorPalette == null) {
            String defaultPalette = ProjectPlugin.getPlugin().getPreferenceStore()
                    .getString(PreferenceConstants.P_DEFAULT_PALETTE);
            if (defaultPalette == null || !PlatformGIS.getColorBrewer().hasPalette(defaultPalette))
                defaultPalette = "Dark2"; //failsafe default //$NON-NLS-1$
            colorPalette = PlatformGIS.getColorBrewer().getPalette(defaultPalette);
        }
        return colorPalette;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setColorPalette(BrewerPalette newColorPalette) {
        BrewerPalette oldColorPalette = colorPalette;
        colorPalette = newColorPalette;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__COLOR_PALETTE, oldColorPalette, colorPalette));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getAbstract() {
        return abstract_;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setAbstract(String newAbstract) {
        String oldAbstract = abstract_;
        abstract_ = newAbstract;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.MAP__ABSTRACT,
                    oldAbstract, abstract_));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.MAP__NAME,
                    oldName, name));
    }

    public URI getID() {
        if (eResource() == null)
            return URI.createFileURI(getName());
        return eResource().getURI();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @throws IOException
     * @generated NOT
     */
    public ReferencedEnvelope getBounds(IProgressMonitor monitor) {
        if (getLayersInternal() != null) {
            ReferencedEnvelope bounds = new ReferencedEnvelope(getViewportModel().getCRS());
            bounds.setToNull();
            List<Layer> layers = new ArrayList<Layer>(getLayersInternal());
            for (Layer layer : layers) {
                ReferencedEnvelope bbox;
                bbox = layer.getBounds(ProgressManager.instance().get(), getViewportModel()
                        .getCRS());
                if (!bbox.isNull()) {
                    if (bounds.isNull())
                        bounds.init(((Envelope) bbox));
                    else
                        bounds.expandToInclude(bbox);
                }
            }
            if (getLayersInternal() != EMPTY_LIST) {
                if (bounds.isNull())
                    return getDefaultBounds();

            }
            return bounds;
        } else {
            return getDefaultBounds();
        }

    }

    private ReferencedEnvelope getDefaultBounds() {
        if (getViewportModel().getCRS().getDomainOfValidity() != null) {
            Extent extent = getViewportModel().getCRS().getDomainOfValidity();
            ReferencedEnvelope env = toReferencedEnvelope(extent, getViewportModel().getCRS());
            if (env != null) {
                ProjectPlugin
                        .log("MapImpl#getDefaultBounds(): Returning valid area of " + env.getCoordinateReferenceSystem().getName().toString()); //$NON-NLS-1$
                return env;
            }
        }
        ProjectPlugin.log("MapImpl#getDefaultBounds(): Returning Default bounds (entire world)"); //$NON-NLS-1$
        return new ReferencedEnvelope(new Envelope(-180, 180, -90, 90), DefaultGeographicCRS.WGS84);
    }

    /**
     * Takes an Extent, usually from a {@link CoordinateReferenceSystem}, and converts it to a ReferencedEnvelope
     *
     * @param extent the extent to convert.
     * @param crs the desired CRS of the ReferencedEnvelope.
     * @return
     */
    public static ReferencedEnvelope toReferencedEnvelope(Extent extent,
            CoordinateReferenceSystem crs) {
        if (extent == null)
            return null;
        Collection<? extends GeographicExtent> elems = extent.getGeographicElements();
        for (GeographicExtent extent2 : elems) {
            ReferencedEnvelope env = null;
            if (extent2 instanceof GeographicBoundingBox) {
                GeographicBoundingBox box = (GeographicBoundingBox) extent2;
                env = new ReferencedEnvelope(box.getWestBoundLongitude(),
                        box.getEastBoundLongitude(), box.getSouthBoundLatitude(),
                        box.getNorthBoundLatitude(), DefaultGeographicCRS.WGS84);
            } else if (extent2 instanceof BoundingPolygon) {
                BoundingPolygon boundingpoly = (BoundingPolygon) extent2;
                Collection<? extends Geometry> polygons = boundingpoly.getPolygons();
                for (Geometry geometry : polygons) {
                    Polygon poly = (Polygon) geometry;
                    org.opengis.geometry.Envelope envelope = poly.getBoundary().getEnvelope();
                    env = new ReferencedEnvelope(envelope.getMinimum(0), envelope.getMaximum(0),
                            envelope.getMinimum(1), envelope.getMaximum(1), envelope
                                    .getLowerCorner().getCoordinateReferenceSystem());
                    break;
                }
            }

            if (env != null) {
                try {
                    env = env.transform(crs, true);
                } catch (TransformException e) {
                    ProjectPlugin.log(
                            "error transforming " + env + " to " + crs.getName().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (FactoryException e) {
                    ProjectPlugin.log(
                            "error transforming " + env + " to " + crs.getName().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return env;
            }
        }
        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public double getAspectRatio(IProgressMonitor monitor) {
        ReferencedEnvelope bounds = getBounds(monitor);
        return bounds.getWidth() / bounds.getHeight();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public NavCommandStack getNavCommandStack() {
        synchronized (CommandManager.class) {
            if (this.navCommandManager == null) {
                this.navCommandManager = new CommandManager(
                        Messages.MapImpl_NavigationCommandStack, new DefaultErrorHandler(),
                        new MapCommandListener());

            }
        }
        return navCommandManager;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public CommandStack getCommandStack() {
        synchronized (CommandManager.class) {
            if (this.commandManager == null) {
                this.commandManager = new CommandManager(Messages.MapImpl_CommandStack,
                        new DefaultErrorHandler(), new MapCommandListener());

            }
        }
        return commandManager;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="layerFactory"
     * @generated NOT
     */
    public LayerFactory getLayerFactory() {
        if (layerFactory == null)
            setLayerFactory(ProjectFactory.eINSTANCE.createLayerFactory());
        return layerFactory;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetLayerFactory(LayerFactory newLayerFactory,
            NotificationChain msgs) {
        LayerFactory oldLayerFactory = layerFactory;
        layerFactory = newLayerFactory;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__LAYER_FACTORY, oldLayerFactory, newLayerFactory);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setLayerFactory(LayerFactory newLayerFactory) {
        if (newLayerFactory != layerFactory) {
            NotificationChain msgs = null;
            if (layerFactory != null)
                msgs = ((InternalEObject) layerFactory).eInverseRemove(this,
                        ProjectPackage.LAYER_FACTORY__MAP, LayerFactory.class, msgs);
            if (newLayerFactory != null)
                msgs = ((InternalEObject) newLayerFactory).eInverseAdd(this,
                        ProjectPackage.LAYER_FACTORY__MAP, LayerFactory.class, msgs);
            msgs = basicSetLayerFactory(newLayerFactory, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__LAYER_FACTORY, newLayerFactory, newLayerFactory));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    private void sendCommand(MapCommand command, boolean async) {
        sendCommandInternal(command, async);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void sendCommandInternal(MapCommand command, boolean async) {
        // make sure to initialize command manager
        getCommandStack();

        command.setMap(this);
        commandManager.execute(command, async);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    private void sendCommand(EditCommand command, boolean async) {
        sendCommandInternal(command, async);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    private void sendCommand(NavCommand command, boolean async) {

        // init command manager
        getNavCommandStack();

        command.setMap(this);
        command.setViewportModel(getViewportModelInternal());
        navCommandManager.execute(command, async);
    }

    private void notifyCommandStackChange() {
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__COMMAND_STACK, null, commandManager));
    }

    private void notifyNavCommandStackChange() {
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__NAV_COMMAND_STACK, null, commandManager));
    }

    /**
     * notifyNavCommandDone is called by the CommandManager when a nav command is finished running.
     */
    public void notifyNavCommandDone() {
        notifyNavCommandStackChange();
    }

    /**
     * notifyCommandDone is called by the CommandManager when a non-nav command is finished running.
     */
    public void notifyCommandDone() {
        notifyCommandStackChange();
    }

    /**
     * Creates and fires a batch notification.
     *
     * @param featureID the id of the layer's feature that has changed
     * @param eventType the type of event that has occurred (see {@linkplain Notification})
     */
    void notifyBatchNotification(int featureID, int eventType) {
        if (eNotificationRequired()) {
            // create bas notification
            ENotificationImpl notifications = new BatchNotification(this, eventType,
                    ProjectPackage.MAP_FEATURE_COUNT + 1);
            // Create layer notifications
            for (Layer layer : getContextModel().getLayers()) {
                EStructuralFeature feature = layer.eClass().getEStructuralFeature(featureID);
                ENotificationImpl notification = new ENotificationImpl((InternalEObject) layer,
                        eventType, featureID, null, layer.eGet(feature));
                notifications.add(notification);
            }
            eNotify(notifications);
        }
    }

    private static class BatchNotification extends ENotificationImpl implements
            Iterable<Notification> {

        /**
         * Construct <code>BatchNotification</code>.
         */
        public BatchNotification(MapImpl notifier, int eventType, int featureid) {
            super(notifier, eventType, featureid, null, null);
        }

        List<Notification> notifications = new ArrayList<Notification>();

        /**
         * @see org.eclipse.emf.common.notify.impl.NotificationImpl#add(org.eclipse.emf.common.notify.Notification)
         */
        public boolean add(Notification newNotification) {
            if (notifications.contains(newNotification))
                return false;

            notifications.add(newNotification);
            Collections.sort(notifications, new Comparator<Notification>() {

                public int compare(Notification o1, Notification o2) {
                    Layer layer1 = (Layer) o1.getNotifier();
                    Layer layer2 = (Layer) o2.getNotifier();
                    return layer1.compareTo(layer2);
                }

            });
            return true;
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<Notification> iterator() {
            return notifications.iterator();
        }

    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void redo() {
        if (commandManager == null || !commandManager.hasForwardHistory())
            return;
        commandManager.redo(true);
        notifyCommandStackChange();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void undo() {
        if (commandManager == null || !commandManager.hasBackHistory())
            return;
        commandManager.undo(true);
        notifyCommandStackChange();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void backwardHistory() {
        if (navCommandManager == null || !navCommandManager.hasBackHistory())
            return;
        navCommandManager.undo(true);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void forwardHistory() {
        if (navCommandManager == null || !navCommandManager.hasForwardHistory())
            return;
        navCommandManager.redo(true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            if (projectInternal != null)
                msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
            return basicSetProjectInternal((Project) otherEnd, msgs);
        case ProjectPackage.MAP__CONTEXT_MODEL:
            if (contextModel != null)
                msgs = ((InternalEObject) contextModel).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - ProjectPackage.MAP__CONTEXT_MODEL, null, msgs);
            return basicSetContextModel((ContextModel) otherEnd, msgs);
        case ProjectPackage.MAP__LAYER_FACTORY:
            if (layerFactory != null)
                msgs = ((InternalEObject) layerFactory).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - ProjectPackage.MAP__LAYER_FACTORY, null, msgs);
            return basicSetLayerFactory((LayerFactory) otherEnd, msgs);
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            if (viewportModelInternal != null)
                msgs = ((InternalEObject) viewportModelInternal).eInverseRemove(this,
                        EOPPOSITE_FEATURE_BASE - ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL, null,
                        msgs);
            return basicSetViewportModelInternal((ViewportModel) otherEnd, msgs);
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            if (editManagerInternal != null)
                msgs = ((InternalEObject) editManagerInternal).eInverseRemove(this,
                        EOPPOSITE_FEATURE_BASE - ProjectPackage.MAP__EDIT_MANAGER_INTERNAL, null,
                        msgs);
            return basicSetEditManagerInternal((EditManager) otherEnd, msgs);
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            if (renderManagerInternal != null)
                msgs = ((InternalEObject) renderManagerInternal).eInverseRemove(this,
                        RenderPackage.RENDER_MANAGER__MAP_INTERNAL, RenderManager.class, msgs);
            return basicSetRenderManagerInternal((RenderManager) otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            return basicSetProjectInternal(null, msgs);
        case ProjectPackage.MAP__CONTEXT_MODEL:
            return basicSetContextModel(null, msgs);
        case ProjectPackage.MAP__LAYER_FACTORY:
            return basicSetLayerFactory(null, msgs);
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            return basicSetViewportModelInternal(null, msgs);
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            return basicSetEditManagerInternal(null, msgs);
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            return basicSetRenderManagerInternal(null, msgs);
        case ProjectPackage.MAP__BLACK_BOARD_INTERNAL:
            return basicSetBlackBoardInternal(null, msgs);
        case ProjectPackage.MAP__LEGEND:
            return ((InternalEList<?>) getLegend()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.MAP__NAME:
            return getName();
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            if (resolve)
                return getProjectInternal();
            return basicGetProjectInternal();
        case ProjectPackage.MAP__CONTEXT_MODEL:
            return getContextModel();
        case ProjectPackage.MAP__ABSTRACT:
            return getAbstract();
        case ProjectPackage.MAP__NAV_COMMAND_STACK:
            return getNavCommandStack();
        case ProjectPackage.MAP__COMMAND_STACK:
            return getCommandStack();
        case ProjectPackage.MAP__LAYER_FACTORY:
            return getLayerFactory();
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            return getViewportModelInternal();
        case ProjectPackage.MAP__COLOR_PALETTE:
            return getColorPalette();
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            return getEditManagerInternal();
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            if (resolve)
                return getRenderManagerInternal();
            return basicGetRenderManagerInternal();
        case ProjectPackage.MAP__COLOUR_SCHEME:
            return getColourScheme();
        case ProjectPackage.MAP__BLACK_BOARD_INTERNAL:
            return getBlackBoardInternal();
        case ProjectPackage.MAP__LEGEND:
            return getLegend();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ProjectPackage.MAP__NAME:
            setName((String) newValue);
            return;
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            setProjectInternal((Project) newValue);
            return;
        case ProjectPackage.MAP__CONTEXT_MODEL:
            setContextModel((ContextModel) newValue);
            return;
        case ProjectPackage.MAP__ABSTRACT:
            setAbstract((String) newValue);
            return;
        case ProjectPackage.MAP__LAYER_FACTORY:
            setLayerFactory((LayerFactory) newValue);
            return;
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            setViewportModelInternal((ViewportModel) newValue);
            return;
        case ProjectPackage.MAP__COLOR_PALETTE:
            setColorPalette((BrewerPalette) newValue);
            return;
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            setEditManagerInternal((EditManager) newValue);
            return;
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            setRenderManagerInternal((RenderManager) newValue);
            return;
        case ProjectPackage.MAP__COLOUR_SCHEME:
            setColourScheme((ColourScheme) newValue);
            return;
        case ProjectPackage.MAP__BLACK_BOARD_INTERNAL:
            setBlackBoardInternal((Blackboard) newValue);
            return;
        case ProjectPackage.MAP__LEGEND:
            getLegend().clear();
            getLegend().addAll((Collection<? extends ILegendItem>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case ProjectPackage.MAP__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            setProjectInternal((Project) null);
            return;
        case ProjectPackage.MAP__CONTEXT_MODEL:
            setContextModel((ContextModel) null);
            return;
        case ProjectPackage.MAP__ABSTRACT:
            setAbstract(ABSTRACT_EDEFAULT);
            return;
        case ProjectPackage.MAP__LAYER_FACTORY:
            setLayerFactory((LayerFactory) null);
            return;
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            setViewportModelInternal((ViewportModel) null);
            return;
        case ProjectPackage.MAP__COLOR_PALETTE:
            setColorPalette(COLOR_PALETTE_EDEFAULT);
            return;
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            setEditManagerInternal((EditManager) null);
            return;
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            setRenderManagerInternal((RenderManager) null);
            return;
        case ProjectPackage.MAP__COLOUR_SCHEME:
            setColourScheme(COLOUR_SCHEME_EDEFAULT);
            return;
        case ProjectPackage.MAP__BLACK_BOARD_INTERNAL:
            setBlackBoardInternal((Blackboard) null);
            return;
        case ProjectPackage.MAP__LEGEND:
            getLegend().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case ProjectPackage.MAP__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ProjectPackage.MAP__PROJECT_INTERNAL:
            return projectInternal != null;
        case ProjectPackage.MAP__CONTEXT_MODEL:
            return contextModel != null;
        case ProjectPackage.MAP__ABSTRACT:
            return ABSTRACT_EDEFAULT == null ? abstract_ != null : !ABSTRACT_EDEFAULT
                    .equals(abstract_);
        case ProjectPackage.MAP__NAV_COMMAND_STACK:
            return NAV_COMMAND_STACK_EDEFAULT == null ? getNavCommandStack() != null
                    : !NAV_COMMAND_STACK_EDEFAULT.equals(getNavCommandStack());
        case ProjectPackage.MAP__COMMAND_STACK:
            return COMMAND_STACK_EDEFAULT == null ? getCommandStack() != null
                    : !COMMAND_STACK_EDEFAULT.equals(getCommandStack());
        case ProjectPackage.MAP__LAYER_FACTORY:
            return layerFactory != null;
        case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
            return viewportModelInternal != null;
        case ProjectPackage.MAP__COLOR_PALETTE:
            return COLOR_PALETTE_EDEFAULT == null ? colorPalette != null : !COLOR_PALETTE_EDEFAULT
                    .equals(colorPalette);
        case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
            return editManagerInternal != null;
        case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
            return renderManagerInternal != null;
        case ProjectPackage.MAP__COLOUR_SCHEME:
            return COLOUR_SCHEME_EDEFAULT == null ? colourScheme != null : !COLOUR_SCHEME_EDEFAULT
                    .equals(colourScheme);
        case ProjectPackage.MAP__BLACK_BOARD_INTERNAL:
            return blackBoardInternal != null;
        case ProjectPackage.MAP__LEGEND:
            return legend != null && !legend.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="editManagerInternal"
     * @generated NOT
     */
    public EditManager getEditManagerInternal() {
        if (editManagerInternal == null) {
            setEditManagerInternal(ProjectFactory.eINSTANCE.createEditManager());
        }
        return editManagerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEditManagerInternal(EditManager newEditManagerInternal,
            NotificationChain msgs) {
        EditManager oldEditManagerInternal = editManagerInternal;
        editManagerInternal = newEditManagerInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__EDIT_MANAGER_INTERNAL, oldEditManagerInternal,
                    newEditManagerInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setEditManagerInternal(EditManager newEditManagerInternal) {
        if (newEditManagerInternal != editManagerInternal) {
            NotificationChain msgs = null;
            if (editManagerInternal != null)
                msgs = ((InternalEObject) editManagerInternal).eInverseRemove(this,
                        ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, EditManager.class, msgs);
            if (newEditManagerInternal != null)
                msgs = ((InternalEObject) newEditManagerInternal).eInverseAdd(this,
                        ProjectPackage.EDIT_MANAGER__MAP_INTERNAL, EditManager.class, msgs);
            msgs = basicSetEditManagerInternal(newEditManagerInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__EDIT_MANAGER_INTERNAL, newEditManagerInternal,
                    newEditManagerInternal));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderManager getRenderManagerInternal() {
        if (renderManagerInternal != null && renderManagerInternal.eIsProxy()) {
            InternalEObject oldRenderManagerInternal = (InternalEObject) renderManagerInternal;
            renderManagerInternal = (RenderManager) eResolveProxy(oldRenderManagerInternal);
            if (renderManagerInternal != oldRenderManagerInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, oldRenderManagerInternal,
                            renderManagerInternal));
            }
        }
        return renderManagerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderManager basicGetRenderManagerInternal() {
        return renderManagerInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetRenderManagerInternal(RenderManager newRenderManagerInternal,
            NotificationChain msgs) {
        RenderManager oldRenderManagerInternal = renderManagerInternal;
        renderManagerInternal = newRenderManagerInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, oldRenderManagerInternal,
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
     * @generated
     */
    public void setRenderManagerInternalGen(RenderManager newRenderManagerInternal) {
        if (newRenderManagerInternal != renderManagerInternal) {
            NotificationChain msgs = null;
            if (renderManagerInternal != null)
                msgs = ((InternalEObject) renderManagerInternal).eInverseRemove(this,
                        RenderPackage.RENDER_MANAGER__MAP_INTERNAL, RenderManager.class, msgs);
            if (newRenderManagerInternal != null)
                msgs = ((InternalEObject) newRenderManagerInternal).eInverseAdd(this,
                        RenderPackage.RENDER_MANAGER__MAP_INTERNAL, RenderManager.class, msgs);
            msgs = basicSetRenderManagerInternal(newRenderManagerInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__RENDER_MANAGER_INTERNAL, newRenderManagerInternal,
                    newRenderManagerInternal));
    }

    Adapter adapter = new AdapterImpl() {
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification msg) {
            switch (msg.getFeatureID(RenderManager.class)) {
            case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL: {
                if (msg.getEventType() == Notification.ADD)
                    if (getViewportModel() != msg.getNewValue())
                        setViewportModelInternal((ViewportModel) msg.getNewValue());
                break;
            }
            }
            switch (msg.getFeatureID(ViewportModel.class)) {
            case RenderPackage.VIEWPORT_MODEL__RENDER_MANAGER_INTERNAL: {
                if (msg.getEventType() == Notification.ADD)
                    if (getRenderManager() != msg.getNewValue())
                        setRenderManagerInternal((RenderManager) msg.getNewValue());
                break;
            }
            }
        }
    };

    /**
     * @see org.locationtech.udig.project.internal.Map#setRenderManager(org.locationtech.udig.project.render.RenderManager)
     * @uml.property name="renderManagerInternal"
     */
    @SuppressWarnings("unchecked")
    public void setRenderManagerInternal(RenderManager newRenderManager) {
        setRenderManagerInternalGen(newRenderManager);
        if (newRenderManager != null)
            newRenderManager.eAdapters().add(adapter);
        if (getViewportModel() != null) {
            getViewportModelInternal().setRenderManagerInternal(newRenderManager);
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ColourScheme getColourScheme() {
        if (colourScheme == null) {
            colourScheme = ColourScheme.getDefault(getColorPalette());
        }
        return colourScheme;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setColourScheme(ColourScheme newColourScheme) {
        ColourScheme oldColourScheme = colourScheme;
        colourScheme = newColourScheme;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__COLOUR_SCHEME, oldColourScheme, colourScheme));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.locationtech.udig.project.IMap#getBlackboard()
     */
    public IBlackboard getBlackboard() {
        return getBlackBoardInternal();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Blackboard getBlackBoardInternal() {
        return blackBoardInternal;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetBlackBoardInternal(Blackboard newBlackBoardInternal,
            NotificationChain msgs) {
        Blackboard oldBlackBoardInternal = blackBoardInternal;
        blackBoardInternal = newBlackBoardInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__BLACK_BOARD_INTERNAL, oldBlackBoardInternal,
                    newBlackBoardInternal);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setBlackBoardInternal(Blackboard newBlackBoardInternal) {
        if (newBlackBoardInternal != blackBoardInternal) {
            NotificationChain msgs = null;
            if (blackBoardInternal != null)
                msgs = ((InternalEObject) blackBoardInternal).eInverseRemove(this,
                        EOPPOSITE_FEATURE_BASE - ProjectPackage.MAP__BLACK_BOARD_INTERNAL, null,
                        msgs);
            if (newBlackBoardInternal != null)
                msgs = ((InternalEObject) newBlackBoardInternal).eInverseAdd(this,
                        EOPPOSITE_FEATURE_BASE - ProjectPackage.MAP__BLACK_BOARD_INTERNAL, null,
                        msgs);
            msgs = basicSetBlackBoardInternal(newBlackBoardInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.MAP__BLACK_BOARD_INTERNAL, newBlackBoardInternal,
                    newBlackBoardInternal));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List<ILegendItem> getLegend() {
        if (legend == null) {
            legend = new EObjectContainmentEList<ILegendItem>(ILegendItem.class, this,
                    ProjectPackage.MAP__LEGEND);
        }
        return legend;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", abstract: "); //$NON-NLS-1$
        result.append(abstract_);
        result.append(", colorPalette: "); //$NON-NLS-1$
        result.append(colorPalette);
        result.append(", colourScheme: "); //$NON-NLS-1$
        result.append(colourScheme);
        result.append(')');
        return result.toString();
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        for (Iterator i = eAdapters().iterator(); i.hasNext();) {
            Object o = i.next();
            if (adapter.isAssignableFrom(o.getClass()))
                return o;
        }

        /*
         * Adapt to an IWorkbenchAdapter. Other aspects of Eclipse can read the
         * properties we provide access to. (example: Property page dialogs
         * can read the label and display that in their title.)
         */
        if (adapter.isAssignableFrom(IWorkbenchAdapter.class)) {
            return new WorkbenchAdapter() {

                @Override
                public String getLabel(Object object) {
                    return getName();
                }

            };
        }

        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /**
     * @see org.locationtech.udig.project.IProjectElement#getProject()
     */
    public IProject getProject() {
        return getProjectInternal();
    }

    /**
     * @see org.locationtech.udig.project.IMap#getViewportModel()
     */
    public IViewportModel getViewportModel() {
        return getViewportModelInternal();
    }

    /**
     * @see org.locationtech.udig.project.IMap#getEditManager()
     */
    public IEditManager getEditManager() {
        return getEditManagerInternal();
    }

    /**
     * @see org.locationtech.udig.project.IMap#getRenderManager()
     */
    public IRenderManager getRenderManager() {
        return getRenderManagerInternal();
    }

    /**
     * @see org.locationtech.udig.project.IMap#getMapLayers()
     */
    @SuppressWarnings("unchecked")
    public List getMapLayers() {
        return Collections.unmodifiableList(getLayersInternal());
    }

    @SuppressWarnings("unchecked")
    public List<Layer> getLayersInternal() {
        if (getContextModel() == null)
            return EMPTY_LIST;
        return getContextModel().getLayers();
    }

    public void sendCommandSync(MapCommand command) {
        if (command instanceof NavCommand)
            sendCommand((NavCommand) command, false);
        else if (command instanceof EditCommand)
            sendCommand((EditCommand) command, false);
        else
            sendCommand(command, false);
    }

    public void sendCommandASync(MapCommand command) {
        if (command instanceof NavCommand)
            sendCommand((NavCommand) command, true);
        else if (command instanceof EditCommand)
            sendCommand((EditCommand) command, true);
        else
            sendCommand(command, true);
    }

    public void executeSyncWithoutUndo(final MapCommand command) {
        command.setMap(this);
        if (Display.getCurrent() != null) {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell());
            dialog.setOpenOnRun(true);
            try {
                dialog.run(false, false, new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException,
                            InterruptedException {
                        try {
                            command.run(monitor);
                        } catch (Exception e) {
                            ProjectPlugin.log("Error executing command: " + command.getName(), e); //$NON-NLS-1$
                        }
                    }

                });
            } catch (InvocationTargetException e) {
                ProjectPlugin.log("Error executing command: " + command.getName(), e); //$NON-NLS-1$
            } catch (InterruptedException e) {
                ProjectPlugin.log("Error executing command: " + command.getName(), e); //$NON-NLS-1$
            }
        } else {
            try {
                command.run(new NullProgressMonitor());
            } catch (Exception e) {
                ProjectPlugin.log("Error executing command: " + command.getName(), e); //$NON-NLS-1$
            }
        }
    }

    public void executeASyncWithoutUndo(final MapCommand command) {
        PlatformGIS.run(new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {
                try {
                    command.run(monitor);
                } catch (Exception e) {
                    ProjectPlugin.log("Error executing command: " + command.getName(), e); //$NON-NLS-1$
                }
            }

        });
    }

    public String getFileExtension() {
        return "umap"; //$NON-NLS-1$
    }

    CopyOnWriteArraySet<IMapListener> mapListeners = new CopyOnWriteArraySet<IMapListener>();

    CopyOnWriteArraySet<IMapCompositionListener> compositionListeners = new CopyOnWriteArraySet<IMapCompositionListener>();

    public void addMapListener(IMapListener listener) {
        mapListeners.add(listener);
    }

    public void removeMapListener(IMapListener listener) {
        mapListeners.remove(listener);
    }

    public void addMapCompositionListener(IMapCompositionListener listener) {
        compositionListeners.add(listener);
    }

    public void removeMapCompositionListener(IMapCompositionListener listener) {
        compositionListeners.remove(listener);
    }

    public List<Color> getMapDefaultColours() {
        List<Layer> layers = getLayersInternal();
        List<Color> colours = new ArrayList<Color>();
        for (Layer layer : layers) {
            Color thisColour = layer.getDefaultColor();
            if (thisColour != null) {
                colours.add(thisColour);
            }
        }
        return colours;
    }

    public void addDeepAdapter(Adapter adapter) {
        ((LayersList2) getLayersInternal()).addDeepAdapter(adapter);
    }

    public void removeDeepAdapter(Adapter adapter) {
        ((LayersList2) getLayersInternal()).removeDeepAdapter(adapter);
    }

    public void lowerLayer(Layer layer) {
        int index = getLayersInternal().indexOf(layer);
        if (index == 0)
            return;
        ((LayersList2) getLayersInternal()).move(index--, index);
    }

    public void raiseLayer(Layer layer) {
        int index = getLayersInternal().indexOf(layer);
        if (index > getLayersInternal().size() - 2)
            return;
        ((LayersList2) getLayersInternal()).move(index++, index);
    }

    public void sendToFrontLayer(Layer layer) {
        int index = getLayersInternal().indexOf(layer);
        ((LayersList2) getLayersInternal()).move(getLayersInternal().size() - 1, index);
    }

    public void sendToBackLayer(Layer layer) {
        int index = getLayersInternal().indexOf(layer);
        ((LayersList2) getLayersInternal()).move(0, index);
    }

    public void sendToIndexLayer(Layer layer, int index) {
        int currentIndex = getLayersInternal().indexOf(layer);
        ((LayersList2) getLayersInternal()).move(index, currentIndex);
    }

    private SpatialOperator localize(SimpleFeatureType schema, SpatialOperator filter) {
        DuplicatingFilterVisitor copier = new DuplicatingFilterVisitor();
        return (SpatialOperator) filter.accept(copier, null);
    }

    /*
     * Constructs a filter for the provided layer, based on the GeometryFilter.
     */
    private Filter target(Layer layer, Filter filter) {
        if (!layer.isSelectable()) {
            return Filter.INCLUDE;
        }
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(
                    FeatureSource.class, null);
            if (featureSource == null) {
                return Filter.INCLUDE;
            }

            SimpleFeatureType schema = layer.getSchema();

            Filter copy;
            if (filter instanceof SpatialOperator)
                copy = localize(schema, (SpatialOperator) filter);
            else
                copy = filter;

            if (copy == null) {
                return Filter.INCLUDE;
            }
            return copy;
        } catch (IOException e) {
            ProjectPlugin.log(null, e);
            return Filter.INCLUDE;
        }
    }

    public void select(Envelope boundingBox) {
        Layer selected = getEditManagerInternal().getSelectedLayer();
        LAYERS: for (Layer layer : getLayersInternal()) {
            if (layer == selected) {
                Filter newFilter = layer.createBBoxFilter(boundingBox, null);
                if (newFilter == null)
                    continue LAYERS;
                layer.setFilter(newFilter);
            } else {
                layer.setFilter(Filter.EXCLUDE);
            }
        }
        notifyBatchNotification(ProjectPackage.LAYER__FILTER, Notification.SET);
    }

    public void select(Envelope boundingBox, boolean add) {
        Layer selected = getEditManagerInternal().getSelectedLayer();
        LAYERS: for (Layer layer : getLayersInternal()) {
            if (layer == selected) {
                Filter oldFilter = layer.getFilter();
                Filter newFilter = null;
                Filter newFilterCopy = null;
                newFilter = layer.createBBoxFilter(boundingBox, null);
                newFilterCopy = layer.createBBoxFilter(boundingBox, null);

                if (newFilter == null)
                    continue LAYERS;
                if (oldFilter == null || oldFilter == Filter.EXCLUDE
                        || oldFilter.equals(Filter.EXCLUDE)) {
                    layer.setFilter(newFilter);
                } else {
                    org.opengis.filter.FilterFactory fac = CommonFactoryFinder
                            .getFilterFactory(GeoTools.getDefaultHints());
                    if (!add) {
                        Not notFilter = fac.not(newFilter);
                        And logicFilter = fac.and(notFilter, oldFilter);

                        layer.setFilter(logicFilter);
                    } else {
                        Filter orFilter = fac.or(newFilter, oldFilter);

                        layer.setFilter(orFilter);
                    }
                }
            } else {
                layer.setFilter(Filter.EXCLUDE);
            }

        }
    }

    /**
     * @see org.locationtech.udig.project.internal.ContextModel#select(Filter)
     * @generated NOT
     */
    public void select(Filter filter) {
        Layer selected = getEditManagerInternal().getSelectedLayer();
        for (Layer layer : getLayersInternal()) {
            if (layer == selected) {
                layer.setFilter(target(layer, filter)); // replace
            } else {
                layer.setFilter(Filter.EXCLUDE);
            }
        }
    }

    /**
     * @see org.locationtech.udig.project.internal.ContextModel#select(Filter, boolean)
     * @generated NOT
     */
    public void select(Filter filter, boolean and) {
        Layer selected = getEditManagerInternal().getSelectedLayer();
        for (Layer layer : getLayersInternal()) {
            if (layer == selected) {
                Filter oldFilter = layer.getFilter();

                if (oldFilter == null || oldFilter == Filter.EXCLUDE
                        || oldFilter.equals(Filter.EXCLUDE)) {
                    layer.setFilter(target(layer, filter));
                } else {

                    FilterFactory createFilterFactory = CommonFactoryFinder
                            .getFilterFactory(GeoTools.getDefaultHints());

                    if (!and) {
                        Filter logicFilter;
                        logicFilter = createFilterFactory.and(oldFilter, target(layer, filter));
                        layer.setFilter(logicFilter);
                    } else {
                        Filter logicFilter;
                        logicFilter = createFilterFactory.or(oldFilter, target(layer, filter));
                        layer.setFilter(logicFilter);
                    }
                }
            } else {
                layer.setFilter(Filter.EXCLUDE);
            }
        }
    }

    public void select(Filter filter, ILayer layerObj) {

        Layer layer = (Layer) layerObj;
        if (getLayersInternal().contains(layer)) {
            for (Layer layer2 : getLayersInternal()) {
                if (layer == layer2) {
                    layer2.setFilter(target(layer, filter)); // replace
                } else {
                    layer2.setFilter(Filter.EXCLUDE);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List getElements(Class type) {
        List lists = new ArrayList();
        for (Iterator iter = getLayersInternal().iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (type.isAssignableFrom(obj.getClass()))
                lists.add(obj);
        }
        return lists;
    }

    @SuppressWarnings("unchecked")
    public List getElements() {
        return getLayersInternal();
    }

} // MapImpl
