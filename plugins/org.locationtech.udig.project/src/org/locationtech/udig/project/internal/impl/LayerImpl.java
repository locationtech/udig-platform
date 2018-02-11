/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.impl;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolve.Status;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IResolveDelta.Kind;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.util.SearchIDDeltaVisitor;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.internal.CatalogRef;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.SimpleBlackboard;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.locationtech.udig.ui.palette.ColourScheme;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.geotools.util.Range;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Standard implementation of a Layer.
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class LayerImpl extends EObjectImpl implements Layer {
    /**
     * The default value of the '{@link #getFilter() <em>Filter</em>}' attribute.
     * 
     * @see #getFilter()
     * @generated NOT
     * @ordered
     */
    protected static final Filter FILTER_EDEFAULT = Filter.EXCLUDE;

    /**
     * The cached value of the '{@link #getFilter() <em>Filter</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getFilter()
     * @generated NOT
     * @ordered
     */
    protected volatile Filter filter = FILTER_EDEFAULT;

    /**
     * The cached value of the '{@link #getStyleBlackboard() <em>Style Blackboard</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getStyleBlackboard()
     * @generated NOT
     * @ordered
     */
    protected volatile StyleBlackboard styleBlackboard = ProjectFactory.eINSTANCE
            .createStyleBlackboard();

    /**
     * The default value of the '{@link #getZorder() <em>Zorder</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getZorder()
     * @generated
     * @ordered
     */
    protected static final int ZORDER_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getZorder() <em>Zorder</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getZorder()
     * @generated
     * @ordered
     */
    protected int zorder = ZORDER_EDEFAULT;

    /**
     * The default value of the '{@link #getStatus() <em>Status</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getStatus()
     * @generated
     * @ordered
     */
    protected static final int STATUS_EDEFAULT = 0;

    /**
     * The following adapter registers itself with a contextmodel when one is assigned it also
     * deregisters itself when reassigned.
     * <p>
     * As for functionality the adapter listens for zorder changes and notifies its listeners when a
     * change occurs
     * </p>
     */
    Adapter contextModelAdapter = new AdapterImpl() {
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        @SuppressWarnings("unchecked")
        public void notifyChanged(Notification msg) {
            registerWithContainers(msg);
            raiseEvents(msg);

        }

        private void raiseEvents(Notification msg) {
            switch (msg.getFeatureID(Layer.class)) {
            case ProjectPackage.LAYER__FILTER:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.FILTER,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__NAME:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.NAME,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__GEO_RESOURCES:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.RESOURCE,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__VISIBLE:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.VISIBILITY,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__ZORDER:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.ZORDER,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__FEATURE_CHANGES:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.EDIT_EVENT,
                        msg.getOldValue(), msg.getNewValue()));
                break;
            case ProjectPackage.LAYER__STYLE_BLACKBOARD:
                fireLayerChange(new LayerEvent(LayerImpl.this, LayerEvent.EventType.STYLE,
                        msg.getOldValue(), msg.getNewValue()));
                break;

            default:
                break;
            }
        }

        @SuppressWarnings("unchecked")
        private void registerWithContainers(Notification msg) {
            // register itself with container objects
            switch (msg.getEventType()) {
            case Notification.SET: {
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__CONTEXT_MODEL) {
                    if (msg.getOldValue() instanceof ContextModel)
                        ((ContextModel) msg.getOldValue()).eAdapters().remove(this);
                    if (msg.getNewValue() instanceof ContextModel)
                        ((ContextModel) msg.getNewValue()).eAdapters().add(this);
                }
                return;
            }
            case Notification.MOVE: {
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__CONTEXT_MODEL)
                    if (LayerImpl.this.eNotificationRequired())
                        zorderNotify(((Integer) msg.getOldValue()).intValue(),
                                ((Layer) msg.getNewValue()).getZorder());
                return;
            }
            }// switch
        }
    };

    FeatureListener featureListener = new FeatureListener() {

        @SuppressWarnings("unchecked")
        public void changed(FeatureEvent featureEvent) {
            try {
                FeatureSource<?, ?> featureSource = (FeatureSource<?, ?>) featureEvent
                        .getFeatureSource();
                FeatureSource<?, ?> resource = getResource(FeatureSource.class,
                        new NullProgressMonitor());

                if (resource instanceof UDIGStore) {
                    UDIGStore layerResource = (UDIGStore) resource;
                    if (featureSource != layerResource.wrapped()) {
                        featureSource.removeFeatureListener(this);
                    }
                }
            } catch (IOException e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
            if (featureEvent.getBounds() != null) {
                if (featureEvent.getEventType() == FeatureEvent.FEATURES_ADDED
                        || featureEvent.getEventType() == FeatureEvent.FEATURES_CHANGED) {
                    if (bounds == null) {
                        ReferencedEnvelope bounds2 = getInfo(getGeoResource(),
                                new NullProgressMonitor()).getBounds();
                        if (bounds2 == null) {
                            bounds2 = new ReferencedEnvelope(getCRS());
                        }
                        if (bounds2.isNull()
                                || !bounds2.contains((Envelope) featureEvent.getBounds())) {
                            bounds = bounds2;
                        }
                    }

                    // if bounds are still null event is probably a commit
                    if (bounds != null) {
                        ReferencedEnvelope delta = featureEvent.getBounds();
                        if( bounds.isEmpty() ){
                            bounds = delta;
                        }
                        else if (CRS.equalsIgnoreMetadata(delta.getCoordinateReferenceSystem(), bounds.getCoordinateReferenceSystem())){
                            bounds.expandToInclude(delta);
                        }
                        else {
                            try {
                                bounds.expandToInclude(
                                        delta.transform( bounds.getCoordinateReferenceSystem(), true )
                                );
                            } catch (TransformException e) {
                                // reproject or give up and copy ...
                                bounds = delta;
                            } catch (FactoryException e) {
                                // reproject or give up and copy ...
                                bounds = delta;
                            }
                            // reproject or give up and copy ...
                            bounds = delta;
                        }
                    }
                }
                getFeatureChanges().add(featureEvent);
            } else {
                if (getFeatureChanges().size() != 0) {
                    bounds = null;
                    getFeatureChanges().clear();
                }
            }

        }
    };

    /**
     * Listeners to this layer.
     * <p>
     * Note this will need to be hooked into the usual EMF adapater mechasim.
     * </p>
     */
    CopyOnWriteArraySet<ILayerListener> listeners = new CopyOnWriteArraySet<ILayerListener>();

    /**
     * Ensures that a warning about georesources not found is only logged once
     */
    private volatile boolean warned = false;

    /**
     * Lock used for wait and notify. ONLY used by {@link #getGeoResources()}!.
     */
    private final Lock geoResourceLock = new UDIGDisplaySafeLock();

    /**
     * Lock used for protecting {@link #getGeoResource()}. ONLY used by getGeoResource()!.
     */
    private final Lock geoResourceCacheLock = new UDIGDisplaySafeLock();

    /**
     * indicates whether or not the CRS is a known CRS (the georesources return null when asked for
     * a CRS). If null then it has not been computed yet. if false and crsLoader !=null then it is
     * being computed.
     */
    private volatile AtomicBoolean unknownCRS;

    /**
     * Used in {@link #isUnknownCRS()} to lazily compute the CRS. If null and unknownCRS is null
     * then the crs has not been computed. if not null then the computatin is taking place.
     */
    private volatile ISafeRunnable crsLoader;

    private Lock unknownCRSLock = new UDIGDisplaySafeLock();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    protected LayerImpl() {
        super();
        eAdapters().add(contextModelAdapter);
        CatalogPlugin.addListener(this);
    }

    @Override
    protected void finalize() throws Throwable {
        CatalogPlugin.removeListener(this);
    }

    /*
     * @see
     * org.locationtech.udig.project.Layer#addListener(org.locationtech.udig.project.LayerListener)
     */
    public void addListener(final ILayerListener listener) {
        listeners.add(listener);
    }

    /*
     * @see
     * org.locationtech.udig.project.Layer#removeListener(org.locationtech.udig.project.LayerListener)
     */
    public void removeListener(final ILayerListener listener) {
        listeners.remove(listener);
    }

    protected void fireLayerChange(LayerEvent event) {
        for (ILayerListener listener : listeners) {
            try {
                if (listener != null)
                    listener.refresh(event);
            } catch (Throwable t) {
                ProjectPlugin.log("", t); //$NON-NLS-1$
            }
        }
    }

    protected void zorderNotify(int old, int current) {
        if (old == current)
            return;
        ENotificationImpl notification = new ENotificationImpl(LayerImpl.this, Notification.SET,
                ProjectPackage.LAYER__ZORDER, old, current, old == current);
        LayerImpl.this.eNotify(notification);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.LAYER;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ContextModel getContextModel() {
        if (eContainerFeatureID() != ProjectPackage.LAYER__CONTEXT_MODEL)
            return null;
        return (ContextModel) eContainer();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetContextModel(ContextModel newContextModel,
            NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject) newContextModel,
                ProjectPackage.LAYER__CONTEXT_MODEL, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setContextModel(ContextModel newContextModel) {
        if (newContextModel != eInternalContainer()
                || (eContainerFeatureID() != ProjectPackage.LAYER__CONTEXT_MODEL && newContextModel != null)) {
            if (EcoreUtil.isAncestor(this, newContextModel))
                throw new IllegalArgumentException(
                        "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newContextModel != null)
                msgs = ((InternalEObject) newContextModel).eInverseAdd(this,
                        ProjectPackage.CONTEXT_MODEL__LAYERS, ContextModel.class, msgs);
            msgs = basicSetContextModel(newContextModel, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__CONTEXT_MODEL, newContextModel, newContextModel));
    }

    /**
     * Get ZOrder of layer with regards to content model
     * 
     * @model
     */
    public int getZorder() {
        if (getContextModel() == null)
            return Integer.MAX_VALUE;
        return getMap().getMapLayers().indexOf(this);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public void setZorder(int newZorder) {
        if (getContextModel() == null || getMapInternal().getLayersInternal() == null) {
            return;
        }

        if (newZorder >= getMapInternal().getLayersInternal().size())
            ((EList) getMapInternal().getLayersInternal()).move(getMapInternal()
                    .getLayersInternal().size() - 1,
                    getMapInternal().getLayersInternal().indexOf(this));
        else
            ((EList) getMapInternal().getLayersInternal()).move(newZorder, getMapInternal()
                    .getLayersInternal().indexOf(this));
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
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__NAME,
                    oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public URL getID() {
        return iD;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setID(URL newID) {
        URL oldID = iD;
        iD = newID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__ID, oldID,
                    iD));
    }

    private static final String DIVIDER = "@type@"; //$NON-NLS-1$

    public void setResourceID(ID id) {
        String qualifier = id.getTypeQualifier();
        String url = id.toURL().toString();
        URL newid;

        String spec = url;
        if (qualifier != null) {
            spec += DIVIDER + qualifier;
        }

        newid = CorePlugin.createSafeURL(spec);
        setID(newid);

    }

    public ID getResourceID() {
        if (getID() == null) {
            return null;
        }
        String rid = getID().toString();
        String[] parts = rid.split(DIVIDER);
        String qualifier = null;
        if (parts.length == 2) {
            qualifier = parts[1];
        }
        ID id;
        if (parts[0].startsWith("file")) { //$NON-NLS-1$
            String[] fileParts = parts[0].split("#", 2); //$NON-NLS-1$
            File file = URLUtils.urlToFile(CorePlugin.createSafeURL(fileParts[0]));
            URL url = CorePlugin.createSafeURL(parts[0]);
            URI uri = CorePlugin.createSafeURI(parts[0]);
            id = new ID(file.getPath() + "#" + fileParts[1], url, file, uri, qualifier); //$NON-NLS-1$
        } else {
            id = new ID(CorePlugin.createSafeURL(parts[0]));
        }

        return id;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setVisible(boolean newVisible) {
        boolean oldVisible = visible;
        visible = newVisible;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__VISIBLE,
                    oldVisible, visible));
    }

    /**
     * <!-- begin-user-doc --> Used to find the associated service in the catalog.
     * <p>
     * On the off chance *no* services exist an empty list is returned. All this means is that the
     * service is down, or the user has not connected to it yet (perhaps they are waiting on
     * security permissions.
     * </p>
     * <p>
     * When the real service comes along we will find out based on catalog events.
     * </p>
     * <b>New implementation of the method:
     * <p>
     * getGeoResource() is a blocking method but it must not block UI thread. With this purpose the
     * new imlementation is done to avoid UI thread blocking because of synchronization. </b> <!--
     * end-user-doc -->
     * 
     * @uml.property name="geoResources"
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public List<IGeoResource> getGeoResources() {

        assert assertNotInDisplayAccess();

        if (eIsProxy() || getMap() == null)
            return geoResources == null ? NULL : geoResources;

        if (geoResources != null || geoResources == NULL)
            return geoResources;

        geoResourceLock.lock();
        gettingResources.set(true);
        try {

            final ID id = getResourceID();
            if (id == null) {
                return NULL;
            }

            if (geoResources != null && geoResources != NULL)
                return geoResources;

            if (!catalogRef.isLoaded()) {
                catalogRef.load();
            }
            final List<IGeoResource> resourceList = new ArrayList<IGeoResource>();

            final ICatalog connections = CatalogPlugin.getDefault().getLocalCatalog();

            try {
                IProgressMonitor monitor = new NullProgressMonitor();

                IRunnableWithProgress object = new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException {
                        try {
                            List<IResolve> resources = connections.find(id.toURL(), monitor);
                            for (IResolve resolve : resources) {
                                if (resolve.getStatus() == Status.BROKEN
                                        || resolve.getStatus() == Status.BROKEN)
                                    continue;
                                if (resolve instanceof IGeoResource) {
                                    LayerResource resource = new LayerResource(LayerImpl.this,
                                            (IGeoResource) resolve);
                                    if (resolve.getID().equals(id)) {
                                        resourceList.add(0, resource);
                                    } else {
                                        resourceList.add(resource);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new InvocationTargetException(e);
                        }
                    }
                };
                if (Display.getCurrent() != null)
                    PlatformGIS.runBlockingOperation(object, monitor);
                else {
                    object.run(monitor);
                }
            } catch (Exception e) {
                ProjectPlugin.log("Error getting layer resources", e); //$NON-NLS-1$
            }

            if (resourceList.size() == 0) {
                if (!warned) {
                    ProjectPlugin
                            .log("Layer: " + getName() + " could not find a GeoResource with id:" + getID()); //$NON-NLS-1$//$NON-NLS-2$
                    warned = true;
                }
                setStatus(ERROR);
                setStatusMessage(Messages.LayerImpl_connectionFailed);
                geoResources = NULL;
            } else {
                eSetDeliver(false);
                geoResources = new EDataTypeUniqueEList<IGeoResource>(IGeoResource.class, this,
                        ProjectPackage.LAYER__GEO_RESOURCES);
                geoResources.addAll(resourceList);
                eSetDeliver(true);

                if (status == ERROR && statusMessage.equals(Messages.LayerImpl_connectionFailed)) {
                    setStatus(ILayer.DONE);
                    setStatusMessage(""); //$NON-NLS-1$
                }
            }
        } finally {

            gettingResources.set(false);
            geoResourceLock.unlock();
        }
        return geoResources;
    }

    private boolean assertNotInDisplayAccess() {
        if (Display.getCurrent() != null) {
            //            ProjectPlugin.log("getGeoResources was called in display Thread", new Exception("JUST A WARNING NOT CRITICAL")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return true;
    }

    /**
     * will be returned by getGeoResources and getGeoResource in the real connection is broken.
     */
    static public final EList<IGeoResource> NULL;
    static {
        NULL = new BasicEList<IGeoResource>(Collections.singletonList(new NullGeoResource()));
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getGeoResources(int)
     * @deprecated
     */
    public <T> IGeoResource getGeoResource(Class<T> clazz) {
        List<IGeoResource> resources = getGeoResources();
        for (IGeoResource resource : resources) {
            if (resource.canResolve(clazz))
                return resource;
        }

        return null;
    }

    public IGeoResource getGeoResource() {
        if (geoResource == null) {
            geoResourceCacheLock.lock();
            try {
                if (geoResource == null) {
                    if (getGeoResources() != NULL && getGeoResources().size() > 0)
                        geoResource = getGeoResources().get(0);
                    else {
                        return (IGeoResource) NULL.get(0);
                    }
                }
            } finally {
                geoResourceCacheLock.unlock();
            }
        }
        return geoResource;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="geoResource"
     * @generated NOT
     */
    public void setGeoResource(IGeoResource newPreferredGeoResource) {
        IGeoResource oldPreferredGeoResource = geoResource;
        geoResource = newPreferredGeoResource;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__GEO_RESOURCE, oldPreferredGeoResource, geoResource));
    }

    public <E> E getResource(Class<E> resourceType, IProgressMonitor monitor) throws IOException {

        IProgressMonitor monitor2 = monitor;
        if (monitor2 == null)
            monitor2 = ProgressManager.instance().get();
        try {
            for (IGeoResource georesource : getGeoResources()) {
                if (georesource.canResolve(resourceType)) {
                    return georesource.resolve(resourceType, monitor2);
                }
            }
            return null;
        } finally {
            monitor2.done();
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="catalogRef"
     * @generated NOT
     */
    public CatalogRef getCatalogRef() {
        if (catalogRef.getLayer() != this)
            catalogRef.setLayer(this);
        return catalogRef;
    }

    public void setCatalogRef(CatalogRef newCatalogRef) {
        newCatalogRef.setLayer(this);
        setCatalogRefGen(newCatalogRef);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setCatalogRefGen(CatalogRef newCatalogRef) {
        CatalogRef oldCatalogRef = catalogRef;
        catalogRef = newCatalogRef;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__CATALOG_REF, oldCatalogRef, catalogRef));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public StyleBlackboard getStyleBlackboard() {
        return styleBlackboard;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetStyleBlackboard(StyleBlackboard newStyleBlackboard,
            NotificationChain msgs) {
        StyleBlackboard oldStyleBlackboard = styleBlackboard;
        styleBlackboard = newStyleBlackboard;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__STYLE_BLACKBOARD, oldStyleBlackboard, newStyleBlackboard);
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
     * @uml.property name="styleBlackboard"
     * @generated NOT
     */
    public void setStyleBlackboard(StyleBlackboard newStyleBlackboard) {
        if (newStyleBlackboard != styleBlackboard) {
            NotificationChain msgs = null;
            if (styleBlackboard != null)
                msgs = ((InternalEObject) styleBlackboard)
                        .eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                                - ProjectPackage.LAYER__STYLE_BLACKBOARD, null, msgs);
            if (newStyleBlackboard != null)
                msgs = ((InternalEObject) newStyleBlackboard)
                        .eInverseAdd(this, EOPPOSITE_FEATURE_BASE
                                - ProjectPackage.LAYER__STYLE_BLACKBOARD, null, msgs);
            msgs = basicSetStyleBlackboard(newStyleBlackboard, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__STYLE_BLACKBOARD, newStyleBlackboard, newStyleBlackboard));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setFilter(Filter newFilter) {
        Filter oldFilter = filter;
        filter = newFilter;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__FILTER,
                    oldFilter, filter));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public int getStatus() {
        if (geoResources == NULL || status == ILayer.ERROR)
            return status;
        if (isUnknownCRS())
            return ILayer.WARNING;

        return status;
    }

    private boolean isUnknownCRS() {
        unknownCRSLock.lock();
        try {
            if (unknownCRS == null) {
                if (crsLoader == null) {
                    crsLoader = new ISafeRunnable() {

                        public void handleException(Throwable exception) {
                            ProjectPlugin.log("", exception); //$NON-NLS-1$
                        }

                        public void run() throws Exception {
                            if (getCRS() == UNKNOWN_CRS) {
                                synchronized (LayerImpl.this) {
                                    if (unknownCRS == null) {
                                        unknownCRS = new AtomicBoolean(true);
                                        setStatus(WARNING);
                                        crsLoader = null;
                                    }
                                }
                            }
                        }

                    };
                    PlatformGIS.run(crsLoader);
                }
                return false;
            } else {
                return unknownCRS.get();
            }
        } finally {
            unknownCRSLock.unlock();

        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setStatus(int newStatus) {
        int oldStatus = status;
        status = newStatus;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__STATUS,
                    oldStatus, status));
    }

    public <T> IGeoResource findGeoResource(Class<T> resourceType) {
        for (IGeoResource resource : getGeoResources()) {
            if (resource.canResolve(resourceType)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * @deprecated
     */
    public <T> boolean isType(Class<T> resourceType) {
        return hasResource(resourceType);
    }

    public <T> boolean hasResource(Class<T> resourceType) {
        for (IGeoResource resource : getGeoResources()) {
            if (resource.canResolve(resourceType))
                return true;
        }
        return false;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ImageDescriptor getIcon() {
        return icon;
    }

    /**
     * please use getIcon
     * @deprecated
     * @generated NOT
     */
    public ImageDescriptor getGlyph() {
        return getIcon();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIcon(ImageDescriptor newIcon) {
        ImageDescriptor oldIcon = icon;
        icon = newIcon;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__ICON,
                    oldIcon, icon));
    }

    /**
     * please use setIcon
     * @deprecated
     * @generated NOT
     */
    public void setGlyph(ImageDescriptor icon) {
        setIcon(icon);
    }

    /**
     * @deprecated use getInteraction(Interaction.SELECT)
     */
    public boolean isSelectable() {
        return getInteraction(Interaction.SELECT);
    }

    /**
     * @deprecated use setInteraction(Interaction.SELECT, value)
     */
    public void setSelectable(boolean newSelectable) {
        setInteraction(Interaction.SELECT, newSelectable);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String toString() {
        if (eIsProxy()) {
            return super.toString();
        }
        StringBuffer result = new StringBuffer();
        result.append("Layer ( name: "); //$NON-NLS-1$
        result.append(name);
        result.append(')');
        return result.toString();
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getQuery(org.locationtech.udig.project.Layer,
     *      boolean)
     * @generated NOT
     */
    public Query getQuery(boolean selection) {
        try {
            if (selection) {
                return new Query(getSchema().getName().getLocalPart(), getFilter());
            } else {
                return Query.ALL;
            }
        } catch (Exception e) {
            if (selection) {
                Query q = new Query();
                q.setFilter(Filter.EXCLUDE);
                return q;
            } else {
                return Query.ALL;
            }
        }
    }

    private volatile int status;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated not
     * @ordered
     */
    protected volatile String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getCatalogRef() <em>Catalog Ref</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getCatalogRef()
     * @generated
     * @ordered
     */
    protected static final CatalogRef CATALOG_REF_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCatalogRef() <em>Catalog Ref</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getCatalogRef()
     * @generated NOT
     * @ordered
     */
    protected volatile CatalogRef catalogRef = new CatalogRef(this);

    /**
     * The default value of the '{@link #getID() <em>ID</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getID()
     * @generated
     * @ordered
     */
    protected static final URL ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getID() <em>ID</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getID()
     * @generated NOT
     * @ordered
     */
    protected volatile URL iD = ID_EDEFAULT;

    /**
     * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isVisible()
     * @generated NOT
     * @ordered
     */
    protected static final boolean VISIBLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isVisible()
     * @generated not
     * @ordered
     */
    protected volatile boolean visible = VISIBLE_EDEFAULT;

    /**
     * The default value of the '{@link #getGeoResource() <em>Geo Resource</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getGeoResource()
     * @generated
     * @ordered
     */
    protected static final IGeoResource GEO_RESOURCE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getGeoResource() <em>Geo Resource</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getGeoResource()
     * @generated NOT
     * @ordered
     */
    protected volatile IGeoResource geoResource = GEO_RESOURCE_EDEFAULT;

    /**
     * The cached value of the '{@link #getGeoResources() <em>Geo Resources</em>}' attribute list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getGeoResources()
     * @generated NOT
     * @ordered
     */
    protected volatile EList<IGeoResource> geoResources = null;

    /**
     * The default value of the '{@link #getCRS() <em>CRS</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
    protected volatile CoordinateReferenceSystem cRS = CRS_EDEFAULT;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getProperties()
     * @generated NOT
     * @ordered
     */
    protected final IBlackboard properties = new SimpleBlackboard();

    /**
     * The default value of the '{@link #getColourScheme() <em>Colour Scheme</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getColourScheme()
     * @generated
     * @ordered
     */
    protected static final ColourScheme COLOUR_SCHEME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getColourScheme() <em>Colour Scheme</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getColourScheme()
     * @generated not
     * @ordered
     */
    protected volatile ColourScheme colourScheme = COLOUR_SCHEME_EDEFAULT;

    /**
     * The default value of the '{@link #getDefaultColor() <em>Default Color</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDefaultColor()
     * @generated
     * @ordered
     */
    protected static final Color DEFAULT_COLOR_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultColor() <em>Default Color</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDefaultColor()
     * @generated NOT
     * @ordered
     */
    protected volatile Color defaultColor = DEFAULT_COLOR_EDEFAULT;

    /**
     * The cached value of the '{@link #getFeatureChanges() <em>SimpleFeature Changes</em>}'
     * attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getFeatureChanges()
     * @generated NOT
     * @ordered
     */
    protected volatile EList featureChanges = null;

    /**
     * The default value of the '{@link #getMinScaleDenominator() <em>Min Scale Denominator</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMinScaleDenominator()
     * @generated not
     * @ordered
     */
    protected static final double MIN_SCALE_DENOMINATOR_EDEFAULT = Double.NaN;

    /**
     * The cached value of the '{@link #getMinScaleDenominator() <em>Min Scale Denominator</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMinScaleDenominator()
     * @generated not
     * @ordered
     */
    protected volatile double minScaleDenominator = MIN_SCALE_DENOMINATOR_EDEFAULT;

    /**
     * The default value of the '{@link #getMaxScaleDenominator() <em>Max Scale Denominator</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMaxScaleDenominator()
     * @generated not
     * @ordered
     */
    protected static final double MAX_SCALE_DENOMINATOR_EDEFAULT = Double.NaN;

    /**
     * The cached value of the '{@link #getMaxScaleDenominator() <em>Max Scale Denominator</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMaxScaleDenominator()
     * @generated not
     * @ordered
     */
    protected volatile double maxScaleDenominator = MAX_SCALE_DENOMINATOR_EDEFAULT;

    /**
     * The cached value of the '{@link #getInteractionMap() <em>Interaction Map</em>}' map. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getInteractionMap()
     * @generated
     * @ordered
     */
    protected EMap<Interaction, Boolean> interactionMap;

    /**
     * The default value of the '{@link #isShown() <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isShown()
     * @generated
     * @ordered
     */
    protected static final boolean SHOWN_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isShown() <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isShown()
     * @generated
     * @ordered
     */
    protected boolean shown = SHOWN_EDEFAULT;

    /**
     * The default value of the '{@link #getIcon() <em>Icon</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIcon()
     * @generated
     * @ordered
     */
    protected static final ImageDescriptor ICON_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getIcon() <em>Icon</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIcon()
     * @generated
     * @ordered
     */
    protected ImageDescriptor icon = ICON_EDEFAULT;

    private volatile String statusMessage = Messages.LayerImpl_status;

    /**
     * @see org.locationtech.udig.project.internal.Layer#getSchema()
     */
    public SimpleFeatureType getSchema() {
        FeatureSource<SimpleFeatureType, SimpleFeature> data;
        try {
            data = getResource(FeatureSource.class, null);
            if (data != null) {
                return data.getSchema();
            }
        } catch (IOException e) {
            ProjectPlugin.log(null, e);
        }
        // XXX: rgould how do I process a getWMS().createDescribeLayerRequest()?

        // URL wfsURL = null;
        //
        // try {
        // DescribeLayerRequest request = null;
        // request = getWMS().createDescribeLayerRequest();
        // request.setLayers(getName());
        // DescribeLayerResponse response = (DescribeLayerResponse)
        // getWMS().issueRequest(request);
        // wfsURL = response.getLayerDescs()[0].getWfs();
        // } catch (SAXException e1) {
        // // TODO Catch e1
        // } catch (UnsupportedOperationException e) {
        // // TODO Catch e
        // } catch (IOException e) {
        // // TODO Catch e
        // }

        // WFS URL now possibly has a URL

        return null;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ILayer arg0) {
        return doComparison(this, arg0);
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getInteraction(java.lang.String)
     */
    public boolean getInteraction(Interaction interaction) {
        // special cases handled as fields
        if (Interaction.VISIBLE.equals(interaction)) {
            return isVisible();
        }
        Boolean applicable = getInteractionMap().get(interaction);
        if (applicable == null) {
            applicable = getDefaultApplicable(interaction);
        }
        return applicable;
    }

    /**
     * Logic to sort out a good default value for the request interaction.
     * 
     * @param interaction
     * @return <code>true</code> if the interaction defaults to on
     */
    protected boolean getDefaultApplicable(Interaction interaction) {
        // not available create a good default for people to see
        if (Interaction.INFO.equals(interaction)) {
            return true; // info is supported by most layers
        }
        if (Interaction.SELECT.equals(interaction)) {
            return true; // selection is supported by most layers
        } else if (Interaction.EDIT.equals(interaction)) {
            IGeoResource found = this.getGeoResource(FeatureStore.class);
            return found != null;
        }
        return false;
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setInteraction(java.lang.String, boolean)
     */
    public void setInteraction(Interaction interaction, boolean applicable) {
        if (Interaction.VISIBLE.equals(interaction)) {
            setVisible(applicable);
        } else {
            getInteractionMap().put(interaction, applicable);
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public CoordinateReferenceSystem getCRS() {
        return getCRS(null);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public void setCRS(CoordinateReferenceSystem newCRS) {

        setCRSGen(newCRS);
        boolean setStatus = false;
        synchronized (this) {
            if (unknownCRS == null) {
                unknownCRS = new AtomicBoolean(false);
            } else if (unknownCRS.get()) {
                unknownCRS.set(false);
                setStatus = true;
            }
        }
        if (setStatus)
            setStatus(status);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setCRSGen(CoordinateReferenceSystem newCRS) {
        CoordinateReferenceSystem oldCRS = cRS;
        cRS = newCRS;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__CRS,
                    oldCRS, cRS));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public IBlackboard getProperties() {
        return properties;
    }

    public IBlackboard getBlackboard() {
        return properties;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ColourScheme getColourScheme() {
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
                    ProjectPackage.LAYER__COLOUR_SCHEME, oldColourScheme, colourScheme));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultColor(Color newDefaultColor) {
        Color oldDefaultColor = defaultColor;
        defaultColor = newDefaultColor;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__DEFAULT_COLOR, oldDefaultColor, defaultColor));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated not
     */
    public List<FeatureEvent> getFeatureChanges() {
        if (featureChanges == null) {
            featureChanges = new EDataTypeUniqueEList(FeatureEvent.class, this,
                    ProjectPackage.LAYER__FEATURE_CHANGES) {
                @Override
                public void add(int index, Object object) {
                    if (size() > 10) {
                        clear();
                    }
                    super.add(index, object);
                }

                @Override
                public boolean add(Object arg0) {
                    return super.add(arg0);
                }
            };
        }
        return featureChanges;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public double getMinScaleDenominator() {
        if (Double.isNaN(minScaleDenominator) || minScaleDenominator <= 0) {
            return getMinMaxScaleDenominatorFromMetrics().getLeft();
        }
        return minScaleDenominator;
    }

    /**
     * Returns the min and max scale denominator (in that order) as obtained from the RenderMetrics
     */
    private Pair<Double, Double> getMinMaxScaleDenominatorFromMetrics() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        boolean newMin = false;
        boolean newMax = false;
        Set<Range> ranges = getScaleRange();
        for (Range range : ranges) {
            if (min > (Double) range.getMinValue()) {
                min = (Double) range.getMinValue();
                newMin = true;
            }
            if (max < (Double) range.getMaxValue()) {
                max = (Double) range.getMaxValue();
                newMax = true;
            }
        }

        if (!newMin) {
            min = Double.MIN_VALUE;
        }
        if (!newMax) {
            max = Double.MAX_VALUE;
        }

        return new Pair<Double, Double>(min, max);
    }

    /**
     * @generated
     */
    public void setMinScaleDenominator(double newMinScaleDenominator) {
        double oldMinScaleDenominator = minScaleDenominator;
        minScaleDenominator = newMinScaleDenominator;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__MIN_SCALE_DENOMINATOR, oldMinScaleDenominator,
                    minScaleDenominator));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    public double getMaxScaleDenominator() {
        if (Double.isNaN(maxScaleDenominator) || maxScaleDenominator <= 0) {
            return getMinMaxScaleDenominatorFromMetrics().getRight();
        }
        return maxScaleDenominator;

    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMaxScaleDenominator(double newMaxScaleDenominator) {
        double oldMaxScaleDenominator = maxScaleDenominator;
        maxScaleDenominator = newMaxScaleDenominator;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__MAX_SCALE_DENOMINATOR, oldMaxScaleDenominator,
                    maxScaleDenominator));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Map<Interaction, Boolean> getInteractionMap() {
        if (interactionMap == null) {
            interactionMap = new EcoreEMap<Interaction, Boolean>(
                    ProjectPackage.Literals.INTERACTION_TO_EBOOLEAN_OBJECT_MAP_ENTRY,
                    InteractionToEBooleanObjectMapEntryImpl.class, this,
                    ProjectPackage.LAYER__INTERACTION_MAP);
        }
        return interactionMap.map();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setShown(boolean newShown) {
        boolean oldShown = shown;
        shown = newShown;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.LAYER__SHOWN,
                    oldShown, shown));
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        // Used to allow workbench selection to adapt an
        // ILayer to a IGeoResource - this allows catalog views
        // to interact with ILayer smoothly
        if (adapter == IGeoResource.class) {
            System.out.println("testing IGeoResource");
        }
        if (adapter.isAssignableFrom(IGeoResource.class)) {
            // note use of isAssignableFrom to allow adapting
            // IGeoResource or super class IResolve
            // this is important for menu and property page enablement
            // (see CanResolvePropertyTester)
            return getGeoResource();
        }
        if (IGeoResource.class.isAssignableFrom(adapter)) {
            // Now we can check the other way, for menu items or actions
            // that want to test for a specific implementation such as
            // ShpGeoResource
            IGeoResource resource = getGeoResource();
            if (resource != null) {
                if (adapter.isAssignableFrom(resource.getClass())) {
                    return resource;
                }
            }
        }
        if (IMap.class.isAssignableFrom(adapter)) {
            return getMap();
        }
        EList adapters = eAdapters();
        if (adapters instanceof SynchronizedEList) {
            ((SynchronizedEList) adapters).lock();
        }
        try {
            for (Iterator i = adapters.iterator(); i.hasNext();) {
                Object o = i.next();
                if (adapter.isAssignableFrom(o.getClass()))
                    return o;
            }
        } finally {
            if (adapters instanceof SynchronizedEList) {
                ((SynchronizedEList) adapters).unlock();
            }
        }

        /*
         * Adapt to an IWorkbenchAdapter. Other aspects of Eclipse can read the properties we
         * provide access to. (example: Property page dialogs can read the label and display that in
         * their title.)
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

    public CoordinateReferenceSystem getCRS(IProgressMonitor monitor) {

        if (cRS != null)
            return cRS;
        return getCRSInternal(monitor);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            return basicSetContextModel((ContextModel) otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            return basicSetContextModel(null, msgs);
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            return basicSetStyleBlackboard(null, msgs);
        case ProjectPackage.LAYER__INTERACTION_MAP:
            return ((InternalEList<?>) ((EMap.InternalMapView<Interaction, Boolean>) getInteractionMap())
                    .eMap()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            return eInternalContainer().eInverseRemove(this, ProjectPackage.CONTEXT_MODEL__LAYERS,
                    ContextModel.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            return getContextModel();
        case ProjectPackage.LAYER__FILTER:
            return getFilter();
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            return getStyleBlackboard();
        case ProjectPackage.LAYER__ZORDER:
            return getZorder();
        case ProjectPackage.LAYER__STATUS:
            return getStatus();
        case ProjectPackage.LAYER__NAME:
            return getName();
        case ProjectPackage.LAYER__CATALOG_REF:
            return getCatalogRef();
        case ProjectPackage.LAYER__ID:
            return getID();
        case ProjectPackage.LAYER__VISIBLE:
            return isVisible();
        case ProjectPackage.LAYER__GEO_RESOURCE:
            return getGeoResource();
        case ProjectPackage.LAYER__GEO_RESOURCES:
            return getGeoResources();
        case ProjectPackage.LAYER__CRS:
            return getCRS();
        case ProjectPackage.LAYER__PROPERTIES:
            return getProperties();
        case ProjectPackage.LAYER__COLOUR_SCHEME:
            return getColourScheme();
        case ProjectPackage.LAYER__DEFAULT_COLOR:
            return getDefaultColor();
        case ProjectPackage.LAYER__FEATURE_CHANGES:
            return getFeatureChanges();
        case ProjectPackage.LAYER__MIN_SCALE_DENOMINATOR:
            return getMinScaleDenominator();
        case ProjectPackage.LAYER__MAX_SCALE_DENOMINATOR:
            return getMaxScaleDenominator();
        case ProjectPackage.LAYER__INTERACTION_MAP:
            if (coreType)
                return ((EMap.InternalMapView<Interaction, Boolean>) getInteractionMap()).eMap();
            else
                return getInteractionMap();
        case ProjectPackage.LAYER__SHOWN:
            return isShown();
        case ProjectPackage.LAYER__ICON:
            return getIcon();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            setContextModel((ContextModel) newValue);
            return;
        case ProjectPackage.LAYER__FILTER:
            setFilter((Filter) newValue);
            return;
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            setStyleBlackboard((StyleBlackboard) newValue);
            return;
        case ProjectPackage.LAYER__ZORDER:
            setZorder((Integer) newValue);
            return;
        case ProjectPackage.LAYER__STATUS:
            setStatus((Integer) newValue);
            return;
        case ProjectPackage.LAYER__NAME:
            setName((String) newValue);
            return;
        case ProjectPackage.LAYER__CATALOG_REF:
            setCatalogRef((CatalogRef) newValue);
            return;
        case ProjectPackage.LAYER__ID:
            setID((URL) newValue);
            return;
        case ProjectPackage.LAYER__VISIBLE:
            setVisible((Boolean) newValue);
            return;
        case ProjectPackage.LAYER__GEO_RESOURCE:
            setGeoResource((IGeoResource) newValue);
            return;
        case ProjectPackage.LAYER__CRS:
            setCRS((CoordinateReferenceSystem) newValue);
            return;
        case ProjectPackage.LAYER__COLOUR_SCHEME:
            setColourScheme((ColourScheme) newValue);
            return;
        case ProjectPackage.LAYER__DEFAULT_COLOR:
            setDefaultColor((Color) newValue);
            return;
        case ProjectPackage.LAYER__FEATURE_CHANGES:
            getFeatureChanges().clear();
            getFeatureChanges().addAll((Collection<? extends FeatureEvent>) newValue);
            return;
        case ProjectPackage.LAYER__MIN_SCALE_DENOMINATOR:
            setMinScaleDenominator((Double) newValue);
            return;
        case ProjectPackage.LAYER__MAX_SCALE_DENOMINATOR:
            setMaxScaleDenominator((Double) newValue);
            return;
        case ProjectPackage.LAYER__INTERACTION_MAP:
            ((EStructuralFeature.Setting) ((EMap.InternalMapView<Interaction, Boolean>) getInteractionMap())
                    .eMap()).set(newValue);
            return;
        case ProjectPackage.LAYER__SHOWN:
            setShown((Boolean) newValue);
            return;
        case ProjectPackage.LAYER__ICON:
            setIcon((ImageDescriptor) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            setContextModel((ContextModel) null);
            return;
        case ProjectPackage.LAYER__FILTER:
            setFilter(FILTER_EDEFAULT);
            return;
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            setStyleBlackboard((StyleBlackboard) null);
            return;
        case ProjectPackage.LAYER__ZORDER:
            setZorder(ZORDER_EDEFAULT);
            return;
        case ProjectPackage.LAYER__STATUS:
            setStatus(STATUS_EDEFAULT);
            return;
        case ProjectPackage.LAYER__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ProjectPackage.LAYER__CATALOG_REF:
            setCatalogRef(CATALOG_REF_EDEFAULT);
            return;
        case ProjectPackage.LAYER__ID:
            setID(ID_EDEFAULT);
            return;
        case ProjectPackage.LAYER__VISIBLE:
            setVisible(VISIBLE_EDEFAULT);
            return;
        case ProjectPackage.LAYER__GEO_RESOURCE:
            setGeoResource(GEO_RESOURCE_EDEFAULT);
            return;
        case ProjectPackage.LAYER__CRS:
            setCRS(CRS_EDEFAULT);
            return;
        case ProjectPackage.LAYER__COLOUR_SCHEME:
            setColourScheme(COLOUR_SCHEME_EDEFAULT);
            return;
        case ProjectPackage.LAYER__DEFAULT_COLOR:
            setDefaultColor(DEFAULT_COLOR_EDEFAULT);
            return;
        case ProjectPackage.LAYER__FEATURE_CHANGES:
            getFeatureChanges().clear();
            return;
        case ProjectPackage.LAYER__MIN_SCALE_DENOMINATOR:
            setMinScaleDenominator(MIN_SCALE_DENOMINATOR_EDEFAULT);
            return;
        case ProjectPackage.LAYER__MAX_SCALE_DENOMINATOR:
            setMaxScaleDenominator(MAX_SCALE_DENOMINATOR_EDEFAULT);
            return;
        case ProjectPackage.LAYER__INTERACTION_MAP:
            getInteractionMap().clear();
            return;
        case ProjectPackage.LAYER__SHOWN:
            setShown(SHOWN_EDEFAULT);
            return;
        case ProjectPackage.LAYER__ICON:
            setIcon(ICON_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case ProjectPackage.LAYER__CONTEXT_MODEL:
            return getContextModel() != null;
        case ProjectPackage.LAYER__FILTER:
            return FILTER_EDEFAULT == null ? filter != null : !FILTER_EDEFAULT.equals(filter);
        case ProjectPackage.LAYER__STYLE_BLACKBOARD:
            return styleBlackboard != null;
        case ProjectPackage.LAYER__ZORDER:
            return zorder != ZORDER_EDEFAULT;
        case ProjectPackage.LAYER__STATUS:
            return status != STATUS_EDEFAULT;
        case ProjectPackage.LAYER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ProjectPackage.LAYER__CATALOG_REF:
            return CATALOG_REF_EDEFAULT == null ? catalogRef != null : !CATALOG_REF_EDEFAULT
                    .equals(catalogRef);
        case ProjectPackage.LAYER__ID:
            return ID_EDEFAULT == null ? iD != null : !ID_EDEFAULT.equals(iD);
        case ProjectPackage.LAYER__VISIBLE:
            return visible != VISIBLE_EDEFAULT;
        case ProjectPackage.LAYER__GEO_RESOURCE:
            return GEO_RESOURCE_EDEFAULT == null ? geoResource != null : !GEO_RESOURCE_EDEFAULT
                    .equals(geoResource);
        case ProjectPackage.LAYER__GEO_RESOURCES:
            return geoResources != null && !geoResources.isEmpty();
        case ProjectPackage.LAYER__CRS:
            return CRS_EDEFAULT == null ? cRS != null : !CRS_EDEFAULT.equals(cRS);
        case ProjectPackage.LAYER__PROPERTIES:
            return properties != null;
        case ProjectPackage.LAYER__COLOUR_SCHEME:
            return COLOUR_SCHEME_EDEFAULT == null ? colourScheme != null : !COLOUR_SCHEME_EDEFAULT
                    .equals(colourScheme);
        case ProjectPackage.LAYER__DEFAULT_COLOR:
            return DEFAULT_COLOR_EDEFAULT == null ? defaultColor != null : !DEFAULT_COLOR_EDEFAULT
                    .equals(defaultColor);
        case ProjectPackage.LAYER__FEATURE_CHANGES:
            return featureChanges != null && !featureChanges.isEmpty();
        case ProjectPackage.LAYER__MIN_SCALE_DENOMINATOR:
            return minScaleDenominator != MIN_SCALE_DENOMINATOR_EDEFAULT;
        case ProjectPackage.LAYER__MAX_SCALE_DENOMINATOR:
            return maxScaleDenominator != MAX_SCALE_DENOMINATOR_EDEFAULT;
        case ProjectPackage.LAYER__INTERACTION_MAP:
            return interactionMap != null && !interactionMap.isEmpty();
        case ProjectPackage.LAYER__SHOWN:
            return shown != SHOWN_EDEFAULT;
        case ProjectPackage.LAYER__ICON:
            return ICON_EDEFAULT == null ? icon != null : !ICON_EDEFAULT.equals(icon);
        }
        return super.eIsSet(featureID);
    }

    /**
     * queries the georesources for a CRS
     * 
     * @param monitor
     * @return
     */
    private CoordinateReferenceSystem getCRSInternal(IProgressMonitor monitor) {
        try {
            CoordinateReferenceSystem crs = getGeoResource().getInfo(monitor).getCRS();
            if (crs != null)
                return crs;
        } catch (Exception e) {
            ProjectPlugin.log(null, e);
        }

        List<IGeoResource> list = getGeoResources();
        for (IGeoResource resource : list) {
            try {
                if (resource.getInfo(monitor).getCRS() != null)
                    return resource.getInfo(monitor).getCRS();
            } catch (Exception e) {
                ProjectPlugin.log(null, e);
            }
        }

        return UNKNOWN_CRS;
    }

    public void refresh(Envelope bounds) {
        if (!isVisible())
            return;
        if (getMap() == null || getMap().getRenderManager() == null)
            return;
        Envelope transformedbounds = bounds;
        if (bounds != null) {
            try {
                transformedbounds = JTS.transform(bounds, layerToMapTransform());
            } catch (TransformException e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
                transformedbounds = bounds;
            }
        }
        getMap().getRenderManager().refresh(this, transformedbounds);
    }

    /**
     * Layers bounds. Usually null but not null if an edit has caused the bounds to be changed. If
     * null then getBounds will obtain the bounds from the IGeoResource. Else will use this bounds.
     */
    private volatile ReferencedEnvelope bounds;

    public MathTransform layerToMapTransform() {
        MathTransform layerToMapTransform;
        try {
            layerToMapTransform = CRS.findMathTransform(getCRS(), getMap().getViewportModel()
                    .getCRS(), true);
        } catch (Exception e) {
            layerToMapTransform = IdentityTransform.create(2);
        }
        return layerToMapTransform;
    }

    public MathTransform mapToLayerTransform() {
        MathTransform mapToLayerTransform;
        try {
            mapToLayerTransform = CRS.findMathTransform(getMap().getViewportModel().getCRS(),
                    getCRS(), true);
        } catch (Exception e) {
            mapToLayerTransform = IdentityTransform.create(2);
        }
        return mapToLayerTransform;
    }

    public void setBounds(ReferencedEnvelope bounds) {
        this.bounds = bounds;
    }

    public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) {
        if (crs == null) {
            crs = getCRS();
        }
        ReferencedEnvelope result;

        if (bounds == null) {
            result = obtainBoundsFromResources(monitor);
        } else {
            result = bounds;
        }

        if (result != null && !result.isNull()) {
            DefaultEngineeringCRS generic2d = DefaultEngineeringCRS.GENERIC_2D;
            if (crs != null && result.getCoordinateReferenceSystem() != generic2d) {
                try {
                    result = result.transform(crs, true);
                } catch (Exception fe) {
                    ProjectPlugin.log("failure to transform layer bounds", fe); //$NON-NLS-1$
                }
            }
        } else {
            return new ReferencedEnvelope(new Envelope(), null);
        }

        return result;

    }

    private ReferencedEnvelope obtainBoundsFromResources(IProgressMonitor monitor) {
        ReferencedEnvelope result = null;
        for (IGeoResource resource : getGeoResources()) {
            //IGeoResourceInfo info = resource.getInfo(monitor);
            IGeoResourceInfo info = getInfo(resource, monitor);
            Envelope tmp = null;
            if (info != null)
                tmp = info.getBounds();

            if (tmp instanceof ReferencedEnvelope
                    && ((ReferencedEnvelope) tmp).getCoordinateReferenceSystem() != null) {
                result = (ReferencedEnvelope) tmp;
            } else {
                result = new ReferencedEnvelope(tmp.getMinX(), tmp.getMaxX(), tmp.getMinY(),
                        tmp.getMaxY(), getCRS());
            }

            if (result != null) {
                break;
            }
        }
        return result;
    }

    private IGeoResourceInfo getInfo(final IGeoResource resource, final IProgressMonitor monitor) {
        final Callable<IGeoResourceInfo> job = new Callable<IGeoResourceInfo>() {

            @Override
            public IGeoResourceInfo call() throws Exception {
                return resource.getInfo(monitor);
            }

        };
        FutureTask<IGeoResourceInfo> task = new FutureTask<IGeoResourceInfo>(job);
        Thread t = new Thread(task);
        t.start();
        IGeoResourceInfo info = null;

        try {
            info = task.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

        return info;
    }

    /**
     * Creates A geometry filter for the given layer.
     * 
     * @param boundingBox in the same crs as the viewport model.
     * @return a Geometry filter in the correct CRS or null if an exception occurs.
     */
    public Filter createBBoxFilter(Envelope boundingBox, IProgressMonitor monitor) {
        FilterFactory2 factory = (FilterFactory2) CommonFactoryFinder.getFilterFactory(GeoTools
                .getDefaultHints());
        Filter bboxFilter = null;
        if (!hasResource(FeatureSource.class))
            return Filter.EXCLUDE;
        try {

            Envelope bbox;
            try {
                MathTransform transform = mapToLayerTransform();
                bbox = JTS.transform(boundingBox, transform);
            } catch (Exception e) {
                bbox = boundingBox;
            }
            String geom = getSchema().getGeometryDescriptor().getName().getLocalPart();
            Object bboxGeom = new GeometryFactory().toGeometry(bbox);
            bboxFilter = factory.intersects(factory.property(geom), factory.literal(bboxGeom));

        } catch (Exception e) {
            ProjectPlugin.getPlugin().log(e);
        }
        return bboxFilter;
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getMap()
     */
    public org.locationtech.udig.project.internal.Map getMapInternal() {
        ContextModel context = getContextModel();
        if (context == null)
            return null;
        return context.getMap();
    }

    /**
     * @see org.locationtech.udig.project.ILayer#getMap()
     */
    public IMap getMap() {
        return getMapInternal();
    }

    /**
     * @see org.locationtech.udig.core.IBlockingAdaptable#getAdapter(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T getAdapter(final Class<T> adapter, IProgressMonitor monitor) throws IOException {
        if (hasResource(adapter)) {
            final List<T> list = new ArrayList<T>();
            monitor.beginTask(Messages.LayerImpl_resolveAdapter, IProgressMonitor.UNKNOWN);
            list.add(getResource(adapter, monitor));
            return list.get(0);
        }
        if (adapter.isAssignableFrom(CoordinateReferenceSystem.class)) {
            return adapter.cast(getCRS(monitor));
        }
        return null;
    }

    /**
     * @see org.locationtech.udig.core.IBlockingAdaptable#canAdaptTo(java.lang.Class)
     */
    public <T> boolean canAdaptTo(Class<T> adapter) {
        return hasResource(adapter) || adapter.isAssignableFrom(CoordinateReferenceSystem.class);
    }

    /**
     * @see org.locationtech.udig.project.internal.LayerDecorator#setStatusMessage(java.lang.String)
     * @uml.property name="statusMessage"
     */
    public void setStatusMessage(String message) {
        if (message == null) {
            this.statusMessage = ""; //$NON-NLS-1$
        } else
            this.statusMessage = message;
    }

    /**
     * @see org.locationtech.udig.project.internal.LayerDecorator#getStatusMessage()
     * @uml.property name="statusMessage"
     */
    public String getStatusMessage() {
        if (geoResources == NULL || status == ILayer.ERROR)
            return statusMessage;
        if (isUnknownCRS())
            return Messages.LayerImpl_unkownCRS;
        return statusMessage;
    }

    private volatile EList eAdapters;

    private AtomicBoolean gettingResources = new AtomicBoolean(false);

    @Override
    public EList eAdapters() {
        if (eAdapters == null) {
            synchronized (this) {
                if (eAdapters == null) {
                    eAdapters = new SynchronizedEList(super.eAdapters());
                }
            }
        }
        return eAdapters;
    }

    void resetConnection(IResolveDelta delta) {
        if (PlatformUI.getWorkbench().isClosing() || getMap() == null)
            return;

        warned = false;
        if (delta.getKind() == Kind.CHANGED) {

            // the resource has changed so this means it could have moved or
            // parameters may have changed
            // so set modified on the map so the new params will be saved on
            // shutdown.
            org.locationtech.udig.project.internal.Map map = getMapInternal();
            Resource eResource = map.eResource();
            if (eResource != null) {
                eResource.setModified(true);
            }
            if (delta.getNewValue() == null) {
                // no change
                if (delta.getOldValue() == null)
                    return;
                else {
                    // TODO do something
                    return;
                }
            }
            // no change
            if (delta.getNewValue().equals(delta.getOldValue()))
                return;

            if (delta.getNewValue() instanceof Envelope) {

                updateBounds();
            } else {
                // look for things we're interested in...
                resetGeoResources();
            }

        } else if (delta.getKind() == Kind.REPLACED) {
            resetGeoResources();
        }
    }

    private void updateBounds() {
        ProjectPlugin.trace(Trace.MODEL, getClass(), "bounds changed " + getID(), null); //$NON-NLS-1$
        refresh(null);
    }

    /**
     *
     */
    private void resetGeoResources() {
        synchronized (this) {
            this.geoResources = null;
            this.geoResource = null;
            cRS = null;
            unknownCRS = null;
        }
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__GEO_RESOURCES, null, null));
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__GEO_RESOURCE, null, null));

            ProjectPlugin.trace(Trace.MODEL, getClass(), "Smack " + getID(), null); //$NON-NLS-1$
            refresh(null);
        }
    }

    @SuppressWarnings("unchecked")
    public void changed(IResolveChangeEvent event) {
        if (eIsProxy()) {
            CatalogPlugin.removeListener(this);
            return;
        }

        if (getMap() == null) {
            geoResources = null;
            return;
        }

        if (geoResources == null)
            return;

        if (gettingResources.get()) {
            return;
        }

        if (event.getType() != IResolveChangeEvent.Type.POST_CHANGE) {
            return;
        }

        IResolveDelta delta = event.getDelta();
        IResolve hit = event.getResolve();

        // Temporary solution while migrating to URI identifiers
        if (delta.getKind() == IResolveDelta.Kind.CHANGED && hit != null) {

            ID affected = hit.getID();
            ID id = getResourceID();
            if (id == null)
                return;

            List<IGeoResource> resources = geoResources;
            for (IGeoResource resource : resources) {
                if (affected.equals(resource.getID())) {
                    resetConnection(delta);
                    return;
                }
            }
        }

        // should search throug the whole delta?
        delta = SearchIDDeltaVisitor.search(getID(), event);
        if (delta != null) {
            ProjectPlugin.trace(Trace.MODEL, getClass(), "Reset resource" + getID(), null); //$NON-NLS-1$
            resetConnection(delta);
        }
    }

    public static int doComparison(ILayer layer, ILayer layer2) {
        if (layer2 == null)
            return 1;

        if (layer2 == layer)
            return 0;

        int i1 = layer.getZorder();
        int i2 = layer2.getZorder();

        if (i1 == i2)
            return 0;
        return i1 < i2 ? -1 : 1;
    }

    public Set<Range> getScaleRange() {
        org.locationtech.udig.project.internal.Map mapInternal = getMapInternal();
        if (mapInternal == null) {
            // we're in the middle of a map closing or map deleteing or something.
            return Collections.emptySet();
        }
        RenderManager manager = mapInternal.getRenderManagerInternal();
        if (manager == null) {
            return Collections.emptySet();
        }
        RendererCreator rendererCreator = manager.getRendererCreator();
        if (rendererCreator == null) {
            return Collections.emptySet();
        }
        Collection<AbstractRenderMetrics> metrics = rendererCreator
                .getAvailableRendererMetrics(this);
        Set<Range> allRanges = new HashSet<Range>();
        for (AbstractRenderMetrics metrics2 : metrics) {
            try {
                if (metrics2.getRenderMetricsFactory().canRender(metrics2.getRenderContext())) {
                    allRanges.addAll(metrics2.getValidScaleRanges());
                }
            } catch (IOException e) {
                continue;
            }
        }
        return allRanges;
    }

}
