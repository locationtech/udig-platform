/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.internal.ui.IDropTargetProvider;
import org.locationtech.udig.internal.ui.UDIGControlDropListener;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.MapEvent;
import org.locationtech.udig.project.interceptor.MapInterceptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.UDIGEditorInput;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.controls.ScaleRatioLabel;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.project.ui.viewers.MapViewer;
import org.locationtech.udig.ui.IBlockingSelection;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.PreShutdownTask;
import org.locationtech.udig.ui.ShutdownTaskList;
import org.locationtech.udig.ui.UDIGDragDropUtilities;
import org.locationtech.udig.ui.UDIGDragDropUtilities.DragSourceDescriptor;
import org.locationtech.udig.ui.UDIGDragDropUtilities.DropTargetDescriptor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This class is the Eclipse editor Part in which a ViewportPane is embedded. The ViewportPane
 * displays and edits Maps. MapViewport is used to initialize ViewportPane and the RenderManager.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class MapEditor extends EditorPart
        implements IDropTargetProvider, IAdaptable, MapEditorPart {
    /** The id of the MapViewport View */
    public static final String ID = "org.locationtech.udig.project.ui.mapEditorOld"; //$NON-NLS-1$

    private static final int STATUS_LINE_HEIGHT;
    static {
        if (Platform.getWS().equals(Platform.WS_WIN32)) {
            STATUS_LINE_HEIGHT = 24;
        } else {
            STATUS_LINE_HEIGHT = 32;
        }
    }

    final MapEditor editor = this;

    final StatusLineManager statusLineManager = new StatusLineManager();

    private MapEditorSite mapEditorSite;

    private IContributionItem scaleContributionItem;

    private IContributionItem crsContributionItem;

    private boolean dirty = false;

    private MapViewer viewer = null;

    /**
     * This composite is used to hold the view; and the status line.
     */
    private Composite composite;

    private DropTargetDescriptor dropTarget;

    /** This is for testing only DO NOT USE OTHERWISE */
    public boolean isTesting;

    private DragSourceDescriptor dragSource;

    /**
     * Creates a new MapViewport object.
     */
    public MapEditor() {
        super();
        // Make sure the featureEditorProcessor has been started.
        ProjectUIPlugin.getDefault().getFeatureEditProcessor();
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    /**
     * The layer listener will listen for edit events and mark the map as dirty if the layer is
     * modified.
     */
    ILayerListener layerListener = new ILayerListener() {

        @Override
        public void refresh(final LayerEvent event) {
            if (event.getType() == LayerEvent.EventType.EDIT_EVENT) {
                setDirty(true);
                event.getSource().getBlackboard().put(LAYER_DIRTY_KEY, "true"); //$NON-NLS-1$
            }
        }

    };

    /**
     * This listener is called when layers are added and removed.
     * <p>
     * This method will add the layerListener to each layer on the map.
     * </p>
     */
    IMapCompositionListener mapCompositionListener = new IMapCompositionListener() {

        @Override
        @SuppressWarnings("unchecked")
        public void changed(final MapCompositionEvent event) {
            switch (event.getType()) {
            case MANY_ADDED:
                final Collection<ILayer> added = (Collection<ILayer>) event.getNewValue();
                for (final ILayer layer : added) {
                    layer.addListener(layerListener);
                }
                break;
            case MANY_REMOVED:
                final Collection<ILayer> removed = (Collection<ILayer>) event.getOldValue();
                for (final ILayer layer : removed) {
                    removeListenerFromLayer(event, layer);
                }
                break;
            case ADDED:

                ((ILayer) event.getNewValue()).addListener(layerListener);
                break;
            case REMOVED:

                final ILayer removedLayer = ((ILayer) event.getOldValue());
                removeListenerFromLayer(event, removedLayer);
                break;
            default:
                break;
            }

            final boolean isMapDirty = isMapDirty();

            if (isMapDirty != isDirty()) {
                setDirty(isMapDirty);
            }
        }

        private void removeListenerFromLayer(final MapCompositionEvent event,
                final ILayer removedLayer) {
            removedLayer.removeListener(layerListener);
            setDirty(isMapDirty());
        }

    };

    /**
     * Listens to the Map and will change the editor title if the map name is changed. Also marks a
     * layer as dirty if the edit manager has some kind of event.
     */
    IMapListener mapListener = new IMapListener() {

        @Override
        public void changed(final MapEvent event) {
            if (composite == null) {
                return; // the composite hasn't been created so chill out
            }
            if (getMap() == null || composite.isDisposed()) {
                event.getSource().removeMapListener(this);
                return;
            }

            MapEditor.this.composite.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    switch (event.getType()) {
                    case NAME:
                        setPartName((String) event.getNewValue()); // rename the map
                        break;
                    case EDIT_MANAGER:
                        for (final ILayer layer : event.getSource().getMapLayers()) {
                            if (layer.hasResource(ITransientResolve.class)) {
                                setDirty(true);
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                    }
                }
            });
        }

    };

    /**
     * Listens to the editor manager; will clear the dirty status after a commit or after a rollback
     * (as long as a map does not have temporary layers).
     */
    IEditManagerListener editListener = new IEditManagerListener() {

        @Override
        public void changed(final EditManagerEvent event) {
            switch (event.getType()) {
            case EditManagerEvent.POST_COMMIT:
                if (!hasTemporaryLayers()) {
                    setDirty(false);
                }
                break;
            case EditManagerEvent.POST_ROLLBACK:
                if (!hasTemporaryLayers()) {
                    setDirty(false);
                }
                break;
            default:
                break;
            }
        }

        private boolean hasTemporaryLayers() {
            if (getMap() == null) {
                return false;
            }
            final List<Layer> layers = getMap().getLayersInternal();
            if (layers == null) {
                return false;
            }
            for (final Layer layer : layers) {
                if (layer.hasResource(ITransientResolve.class)) {
                    return true;
                }
            }
            return false;
        }
    };

    private LayerSelectionListener layerSelectionListener;

    private ReplaceableSelectionProvider replaceableSelectionProvider;

    private final PreShutdownTask shutdownTask = new PreShutdownTask() {

        @Override
        public int getProgressMonitorSteps() {
            return 3;
        }

        @Override
        public boolean handlePreShutdownException(final Throwable t, final boolean forced) {
            ProjectUIPlugin.log("error prepping map editors for shutdown", t); //$NON-NLS-1$
            return true;
        }

        @Override
        public boolean preShutdown(final IProgressMonitor monitor, final IWorkbench workbench,
                final boolean forced) throws Exception {
            monitor.beginTask("Saving Map Editor", 3);
            save(SubMonitor.convert(monitor, 1));
            if (dirty) {
                if (!forced) {
                    return false;
                } else {
                    setDirty(false);
                }
            }
            removeTemporaryLayers(ProjectPlugin.getPlugin().getPreferenceStore());
            monitor.worked(1);

            // save the map's URI in the preferences so that it will be loaded the next time uDig is
            // run.
            final Resource resource = getMap().eResource();
            if (resource != null) {
                // save editor
                try {
                    final IPreferenceStore p = ProjectUIPlugin.getDefault().getPreferenceStore();
                    int numEditors = p.getInt(ID);
                    final String id = ID + ":" + numEditors; //$NON-NLS-1$
                    numEditors++;
                    p.setValue(ID, numEditors);
                    final String value = resource.getURI().toString();
                    p.setDefault(id, ""); //$NON-NLS-1$
                    p.setValue(id, value);
                } catch (final Exception e) {
                    ProjectUIPlugin.log("Failure saving which maps are open", e); //$NON-NLS-1$
                }
            }

            monitor.worked(1);
            monitor.done();

            return true;
        }

        private void save(final IProgressMonitor monitor) {
            if (dirty) {
                PlatformGIS.syncInDisplayThread(new Runnable() {
                    @Override
                    public void run() {
                        final IconAndMessageDialog d = new SaveDialog(
                                Display.getCurrent().getActiveShell(), getMap());
                        final int result = d.open();

                        if (result == IDialogConstants.YES_ID) {
                            doSave(monitor);
                        } else if (result != Window.CANCEL) {
                            setDirty(false);
                        }
                    }
                });
            }
        }

    };

    @Override
    public Object getAdapter(final Class adaptee) {
        if (adaptee.isAssignableFrom(Map.class)) {
            return getMap();
        }
        if (adaptee.isAssignableFrom(ViewportPane.class)) {
            return viewer.getViewport();
        }
        return super.getAdapter(adaptee);
    }

    private void clearLayerDirtyFlag() {
        final List<ILayer> layers = getMap().getMapLayers();
        for (final ILayer layer : layers) {
            layer.getBlackboard().put(LAYER_DIRTY_KEY, null);
        }
    }

    @Override
    public void setFont(final Control control) {
        viewer.setFont(control);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {

        if (isTesting) {
            return;
        }
        if (getSite() == null || getSite().getPage() == null) {
            // Exception occurred before instantiating editor
            return;
        }

        runMapClosingInterceptors();

        deregisterFeatureFlasher();
        final IWorkbenchPage page = getSite().getPage();
        page.removePostSelectionListener(layerSelectionListener);
        page.removePartListener(partlistener);

        viewer.getViewport().removePaneListener(getMap().getViewportModelInternal());
        getMap().getViewportModelInternal().setInitialized(false);

        selectFeatureListener = null;
        partlistener = null;

        if (statusLineManager != null) {
            statusLineManager.dispose();
        }

        final ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        if (!PlatformUI.getWorkbench().isClosing()) {
            ShutdownTaskList.instance().removePreShutdownTask(shutdownTask);
            try {
                // kill rending now - even if it is moving
                getRenderManager().dispose();
            } catch (final Throwable t) {
                ProjectUIPlugin.log("Shutting down rendering - " + t, null); //$NON-NLS-1$
            }
            getMap().getEditManagerInternal().setEditFeature(null, null);
            try {
                PlatformGIS.run(new ISafeRunnable() {

                    @Override
                    public void handleException(final Throwable exception) {
                        ProjectUIPlugin.log("error saving map: " + getMap().getName(), exception); //$NON-NLS-1$
                    }

                    @Override
                    public void run() throws Exception {

                        removeTemporaryLayers(store);
                        final Project p = getMap().getProjectInternal();
                        if (p != null) {
                            if (p.eResource() != null && p.eResource().isModified()) {
                                p.eResource().save(ProjectPlugin.getPlugin().saveOptions);
                            }

                            final Resource resource = getMap().eResource();
                            resource.save(ProjectPlugin.getPlugin().saveOptions);

                            // need to kick the Project so viewers will update
                            p.eNotify(new ENotificationImpl((InternalEObject) p, Notification.SET,
                                    ProjectPackage.PROJECT__ELEMENTS_INTERNAL, null, null));

                        } else {
                            final Resource resource = getMap().eResource();
                            if (resource != null) {
                                resource.save(ProjectPlugin.getPlugin().saveOptions);
                            }
                        }
                        viewer.dispose();
                        // setMap(null);
                    }

                });
            } catch (final Exception e) {
                ProjectPlugin.log("Exception while saving Map", e); //$NON-NLS-1$
            }
        }

        super.dispose();

    }

    private void runMapClosingInterceptors() {
        final List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for (final IConfigurationElement element : interceptors) {
            if (!MapInterceptor.CLOSING_ID.equals(element.getName())) {
                continue;
            }
            try {
                final MapInterceptor interceptor = (MapInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(getMap());
            } catch (final Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    private void removeTemporaryLayers(final IPreferenceStore store) {
        if (store.getBoolean(
                org.locationtech.udig.project.preferences.PreferenceConstants.P_REMOVE_LAYERS)) {
            final List<Layer> layers = getMap().getLayersInternal();
            final List<Layer> layersToRemove = new ArrayList<>();
            for (final Layer layer : layers) {
                if (layer.getGeoResources().get(0).canResolve(ITransientResolve.class)) {
                    layersToRemove.add(layer);
                }
            }

            if (!layers.isEmpty()) {
                if (getMap().eResource() != null) {
                    getMap().eResource().setModified(true);
                }
                layers.removeAll(layersToRemove);
            }
        }
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        final boolean[] success = new boolean[] { false };

        PlatformGIS.syncInDisplayThread(new SaveMapRunnable(this, success));

        if (success[0]) {
            setDirty(false);
        } else {
            // abort shutdown if in progress
            monitor.setCanceled(true);
        }
    }

    @Override
    public void doSaveAs() {
        throw new UnsupportedOperationException("Do Save As is not implemented yet"); //$NON-NLS-1$
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) {
        setSite(site);
        setInput(input);
        // initialize ToolManager
        ApplicationGIS.getToolManager();
    }

    @Override
    protected void setInput(final IEditorInput input) {
        if (getEditorInput() != null) {
            final Map map = (Map) ((UDIGEditorInput) getEditorInput()).getProjectElement();
            if (viewer != null) {
                viewer.setMap(null);
            }
            map.removeMapCompositionListener(mapCompositionListener);
            map.removeMapListener(mapListener);
            map.getEditManager().removeListener(editListener);
        }
        super.setInput(input);
        if (input != null) {
            if (viewer != null) {
                viewer.setMap((Map) ((UDIGEditorInput) input).getProjectElement());
            }
            getMap().addMapCompositionListener(mapCompositionListener);
            getMap().addMapListener(mapListener);
            getMap().getEditManager().addListener(editListener);
        }

    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    private boolean isMapDirty() {
        boolean mapIsDirty = false;
        for (final ILayer layer : getMap().getMapLayers()) {
            if (layer.hasResource(ITransientResolve.class)) {
                mapIsDirty = true;
            }
            final boolean layerIsDirty = layer.getBlackboard().get(LAYER_DIRTY_KEY) != null;
            if (layerIsDirty) {
                mapIsDirty = true;
            }
        }
        return mapIsDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void setDirty(final boolean dirty) {
        if (dirty == this.dirty) {
            return;
        }

        this.dirty = dirty;

        if (!dirty) {
            clearLayerDirtyFlag();
        }

        Display.getDefault().asyncExec(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                firePropertyChange(PROP_DIRTY);
            }
        });
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return true;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {

        ShutdownTaskList.instance().addPreShutdownTask(shutdownTask);

        composite = new Composite(parent, SWT.NO_BACKGROUND);

        composite.setLayout(new FormLayout());
        composite.setFont(parent.getFont());
        setPartName(getMap().getName());

        setTitleToolTip(Messages.MapEditor_titleToolTip);
        setTitleImage(ProjectUIPlugin.getDefault().getImage(ISharedImages.MAP_OBJ));

        viewer = new MapViewer(composite, this, SWT.DOUBLE_BUFFERED);
        // allow the viewer to open our context menu; work with our selection provider etc
        viewer.init(this);
        // if a map was provided as input we can ask the viewer to use it
        final Map input = (Map) ((UDIGEditorInput) getEditorInput()).getProjectElement();
        if (input != null) {
            viewer.setMap(input);
        }

        FormData formdata = new FormData();
        formdata.top = new FormAttachment(0);
        formdata.bottom = new FormAttachment(100, -STATUS_LINE_HEIGHT);
        formdata.left = new FormAttachment(0);
        formdata.right = new FormAttachment(100);
        viewer.getViewport().getControl().setLayoutData(formdata);

        statusLineManager.add(new GroupMarker(StatusLineManager.BEGIN_GROUP));
        statusLineManager.add(new GroupMarker(StatusLineManager.MIDDLE_GROUP));
        statusLineManager.add(new GroupMarker(StatusLineManager.END_GROUP));
        statusLineManager.createControl(composite, SWT.BORDER);
        formdata = new FormData();
        formdata.left = new FormAttachment(0);
        formdata.right = new FormAttachment(100);
        formdata.top = new FormAttachment(viewer.getViewport().getControl(), 0, SWT.BOTTOM);
        formdata.bottom = new FormAttachment(100);
        statusLineManager.getControl().setLayoutData(formdata);

        getSite().getPage().addPartListener(partlistener);
        registerFeatureFlasher();
        viewer.getViewport().addPaneListener(getMap().getViewportModelInternal());

        layerSelectionListener = new LayerSelectionListener(
                new MapLayerSelectionCallback(getMap(), composite));

        getSite().getPage().addPostSelectionListener(layerSelectionListener);

        for (final Layer layer : getMap().getLayersInternal()) {
            layer.addListener(layerListener);
        }

        dropTarget = UDIGDragDropUtilities.addDropSupport(viewer.getViewport().getControl(), this);
        this.replaceableSelectionProvider = new ReplaceableSelectionProvider();
        getSite().setSelectionProvider(replaceableSelectionProvider);
        runMapOpeningInterceptor(getMap());
        mapEditorSite = new MapEditorSite(super.getSite(), this);

        final IContributionManager statusBar = mapEditorSite.getActionBars().getStatusLineManager();

        scaleContributionItem = new ScaleRatioLabel(this);
        scaleContributionItem.setVisible(true);
        statusBar.appendToGroup(StatusLineManager.MIDDLE_GROUP, scaleContributionItem);

        crsContributionItem = new CRSContributionItem(this);
        crsContributionItem.setVisible(true);
        statusBar.appendToGroup(StatusLineManager.MIDDLE_GROUP, crsContributionItem);
        scaleContributionItem.update();
        crsContributionItem.update();

        getMap().getViewportModel().addViewportModelListener(new IViewportModelListener() {

            @Override
            public void changed(final ViewportModelEvent event) {
                if (getMap() == null) {
                    event.getSource().removeViewportModelListener(this);
                }
            }
        });

        setDirty(isMapDirty());
    }

    private void runMapOpeningInterceptor(final Map map) {
        final List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for (final IConfigurationElement element : interceptors) {
            if (!MapInterceptor.OPENING_ID.equals(element.getName())) {
                continue;
            }
            try {
                final MapInterceptor interceptor = (MapInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(map);
            } catch (final Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    private FlashFeatureListener selectFeatureListener = new FlashFeatureListener();

    private boolean flashFeatureRegistered = false;

    private Action propertiesAction;

    /**
     * registers a listener with the current page that flashes a feature each time the current
     * selected feature changes.
     */
    protected synchronized void registerFeatureFlasher() {
        if (!flashFeatureRegistered) {
            flashFeatureRegistered = true;
            final IWorkbenchPage page = getSite().getPage();
            page.addPostSelectionListener(selectFeatureListener);
        }
    }

    protected synchronized void deregisterFeatureFlasher() {
        flashFeatureRegistered = false;
        getSite().getPage().removePostSelectionListener(selectFeatureListener);
    }

    void createContextMenu() {
        Menu menu;
        menu = viewer.getMenu();
        if (menu == null) {
            final MenuManager contextMenu = new MenuManager();
            contextMenu.setRemoveAllWhenShown(true);
            contextMenu.addMenuListener(new IMenuListener() {
                @Override
                public void menuAboutToShow(final IMenuManager mgr) {
                    final IToolManager tm = ApplicationGIS.getToolManager();

                    contextMenu.add(tm.getENTERAction());
                    contextMenu.add(new Separator());

                    contextMenu.add(tm.getZOOMTOSELECTEDAction());
                    contextMenu.add(new Separator());
                    contextMenu.add(tm.getBACKWARD_HISTORYAction());
                    contextMenu.add(tm.getFORWARD_HISTORYAction());
                    contextMenu.add(new Separator());
                    contextMenu.add(tm.getCOPYAction(MapEditor.this));
                    contextMenu.add(tm.getPASTEAction(MapEditor.this));
                    contextMenu.add(tm.getDELETEAction());

                    /**
                     * Gets contributions from active modal tool if possible
                     */
                    tm.contributeActiveModalTool(contextMenu);

                    contextMenu.add(new Separator());
                    contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                    if (getMap().getEditManager().getEditFeature() != null) {
                        contextMenu.add(ProjectUIPlugin.getDefault().getFeatureEditProcessor()
                                .getEditFeatureAction(
                                        getSite().getSelectionProvider().getSelection()));

                        contextMenu.add(ProjectUIPlugin.getDefault().getFeatureEditProcessor()
                                .getEditWithFeatureMenu(
                                        getSite().getSelectionProvider().getSelection()));
                    }
                    contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(
                            replaceableSelectionProvider.getSelection()));
                    contextMenu.add(new Separator());
                    contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
                    contextMenu.add(new Separator());
                    contextMenu.add(getPropertiesAction());
                }
            });

            // Create menu.
            menu = contextMenu.createContextMenu(composite);
            viewer.setMenu(menu);
            getSite().registerContextMenu(contextMenu, getSite().getSelectionProvider());
        }
    }

    protected IAction getPropertiesAction() {
        if (propertiesAction == null) {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final PropertyDialogAction tmp = new PropertyDialogAction(new SameShellProvider(shell),
                    new ISelectionProvider() {

                        @Override
                        public void addSelectionChangedListener(
                                final ISelectionChangedListener listener) {
                        }

                        @Override
                        public ISelection getSelection() {
                            return new StructuredSelection(getMap());
                        }

                        @Override
                        public void removeSelectionChangedListener(
                                final ISelectionChangedListener listener) {
                        }

                        @Override
                        public void setSelection(final ISelection selection) {
                        }

                    });

            propertiesAction = new Action() {
                @Override
                public void runWithEvent(final Event event) {
                    tmp.createDialog().open();
                }
            };

            propertiesAction.setText(tmp.getText());
            propertiesAction.setActionDefinitionId(tmp.getActionDefinitionId());
            propertiesAction.setDescription(tmp.getDescription());
            propertiesAction.setHoverImageDescriptor(tmp.getHoverImageDescriptor());
            propertiesAction.setImageDescriptor(tmp.getImageDescriptor());
            propertiesAction.setToolTipText(tmp.getToolTipText());

        }
        getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
                propertiesAction);
        return propertiesAction;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        composite.setFocus();
        if (crsContributionItem != null) {
            crsContributionItem.update();
        }
        if (scaleContributionItem != null) {
            scaleContributionItem.update();
        }
    }

    /**
     * Returns the map that this editor edits
     *
     * @return Returns the map that this editor edits
     */
    @Override
    public Map getMap() {
        final UDIGEditorInput editorInput = (UDIGEditorInput) getEditorInput();
        if (editorInput != null) {
            return (Map) editorInput.getProjectElement();
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionbarContributor for the Editor.
     *
     * @return the ActionbarContributor for the Editor.
     */
    public SubActionBars2 getActionbar() {
        return (SubActionBars2) getEditorSite().getActionBars();
    }

    IPartListener2 partlistener = new IPartListener2() {
        @Override
        public void partActivated(final IWorkbenchPartReference partRef) {
            if (partRef.getPart(false) == MapEditor.this) {
                registerFeatureFlasher();
                ApplicationGIS.getToolManager().setCurrentEditor(editor);
            }
        }

        @Override
        public void partBroughtToTop(final IWorkbenchPartReference partRef) {
        }

        @Override
        public void partClosed(final IWorkbenchPartReference partRef) {
            if (partRef.getPart(false) == MapEditor.this) {
                deregisterFeatureFlasher();
                visible = false;
            }
        }

        @Override
        public void partDeactivated(final IWorkbenchPartReference partRef) {
            // do nothing
        }

        @Override
        public void partOpened(final IWorkbenchPartReference partRef) {
            // do nothing
        }

        @Override
        public void partHidden(final IWorkbenchPartReference partRef) {
            if (partRef.getPart(false) == MapEditor.this) {
                deregisterFeatureFlasher();
                visible = false;
            }
        }

        @Override
        public void partVisible(final IWorkbenchPartReference partRef) {
            if (partRef.getPart(false) == MapEditor.this) {
                registerFeatureFlasher();
                visible = true;
            }
        }

        @Override
        public void partInputChanged(final IWorkbenchPartReference partRef) {
        }

    };

    private boolean draggingEnabled;

    private volatile boolean visible = false;

    /**
     * Opens the map's context menu.
     */
    @Override
    public void openContextMenu() {
        viewer.openContextMenu();
    }

    @Override
    public UDIGDropHandler getDropHandler() {
        return ((UDIGControlDropListener) dropTarget.listener).getHandler();
    }

    @Override
    public Object getTarget(final DropTargetEvent event) {
        return this;
    }

    /**
     * Enables or disables dragging (drag and drop) from the map editor.
     */
    @Override
    public void setDragging(final boolean enable) {
        if (draggingEnabled == enable) {
            return;
        }
        if (enable) {
            dragSource = UDIGDragDropUtilities.addDragSupport(viewer.getViewport().getControl(),
                    getSite().getSelectionProvider());
        } else {
            dragSource.source.dispose();
        }
        draggingEnabled = enable;
    }

    @Override
    public boolean isDragging() {
        return draggingEnabled;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public void setSelectionProvider(final IMapEditorSelectionProvider selectionProvider) {
        if (selectionProvider == null) {
            throw new NullPointerException("selection provider must not be null!"); //$NON-NLS-1$
        }
        selectionProvider.setActiveMap(getMap(), this);
        if (selectionProvider != this.replaceableSelectionProvider.getSelectionProvider()) {
            this.replaceableSelectionProvider.setProvider(selectionProvider);
        }
        createContextMenu();
    }

    @Override
    public MapEditorSite getMapEditorSite() {
        return mapEditorSite;
    }

    private class FlashFeatureListener implements ISelectionListener {

        @Override
        public void selectionChanged(IWorkbenchPart part, final ISelection selection) {
            if (part == MapEditor.this || getSite().getPage().getActivePart() != part
                    || selection instanceof IBlockingSelection) {
                return;
            }

            ISafeRunnable sendAnimation = new ISafeRunnable() {
                @Override
                public void run() {
                    if (selection instanceof IStructuredSelection) {
                        final IStructuredSelection s = (IStructuredSelection) selection;
                        final List<SimpleFeature> features = new ArrayList<>();
                        for (Iterator iter = s.iterator(); iter.hasNext();) {
                            final Object element = iter.next();

                            if (element instanceof SimpleFeature) {
                                final SimpleFeature feature = (SimpleFeature) element;
                                features.add(feature);
                            }
                        }
                        if (features.isEmpty()) {
                            return;
                        }
                        if (!getRenderManager().isDisposed()) {
                            final IAnimation anim = createAnimation(features);
                            if (anim != null) {
                                AnimationUpdater.runTimer(
                                        getMap().getRenderManager().getMapDisplay(), anim);
                            }
                        }
                    }
                }

                @Override
                public void handleException(Throwable exception) {
                    ProjectUIPlugin.log("Exception preparing animation", exception); //$NON-NLS-1$
                }
            };

            try {
                sendAnimation.run();
            } catch (final Exception e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
            }
        }

        private IAnimation createAnimation(List<SimpleFeature> current) {
            final List<IDrawCommand> commands = new ArrayList<>();
            for (SimpleFeature feature : current) {
                if (feature == null || feature.getFeatureType().getGeometryDescriptor() == null) {
                    continue;
                }
                DrawFeatureCommand command = null;
                if (feature instanceof IAdaptable) {
                    Layer layer = ((IAdaptable) feature).getAdapter(Layer.class);
                    if (layer != null) {
                        try {
                            command = new DrawFeatureCommand(feature, layer);
                        } catch (final IOException e) {
                            // do nothing... thats life
                        }
                    }
                }
                if (command == null) {
                    command = new DrawFeatureCommand(feature);
                }
                command.setMap(getMap());
                command.preRender();
                commands.add(command);
            }
            final Rectangle2D rect = new Rectangle();

            final Rectangle validArea = (Rectangle) rect;

            return new FeatureAnimation(commands, validArea);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    RenderManager getRenderManager() {
        return viewer.getRenderManager();
    }

    @Override
    public IStatusLineManager getStatusLineManager() {
        return statusLineManager;
    }

    @Override
    public boolean isTesting() {
        return this.isTesting;
    }

    @Override
    public void setTesting(boolean testing) {
        this.isTesting = testing;
    }

}
