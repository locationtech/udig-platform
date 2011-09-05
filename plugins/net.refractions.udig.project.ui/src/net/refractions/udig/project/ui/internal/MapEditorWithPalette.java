/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.internal.ui.UDIGControlDropListener;
import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.command.UndoRedoCommand;
import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.ChangeCRSCommand;
import net.refractions.udig.project.internal.commands.selection.SelectLayerCommand;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.render.ViewportModelEvent.EventType;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.UDIGEditorInput;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.controls.ScaleRatioLabel;
import net.refractions.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.viewers.MapEditDomain;
import net.refractions.udig.project.ui.viewers.MapViewer;
import net.refractions.udig.ui.CRSChooserDialog;
import net.refractions.udig.ui.IBlockingSelection;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.PreShutdownTask;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.ShutdownTaskList;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.UDIGDragDropUtilities.DragSourceDescriptor;
import net.refractions.udig.ui.UDIGDragDropUtilities.DropTargetDescriptor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineLayoutData;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class is the Eclipse editor Part in which a ViewportPane is embedded. The ViewportPane
 * displays and edits Maps. MapViewport is used to intialize ViewportPane and the RenderManager.
 * <p>
 * Note:
 * <ul>
 * <li>The super class GraphicalEditorWithFlyoutPalette will smoothly switch between displaying
 * the palette inline; and hiding it when the normal PaletteView is opened
 * </li>
 * </ul>
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
// TODO: Rename this to MapEditor to prevent code bloat / code duplication
public class MapEditorWithPalette extends GraphicalEditorWithFlyoutPalette implements IDropTargetProvider, IAdaptable, MapEditorPart {
    private static final String LAYER_DIRTY_KEY = "DIRTY"; //$NON-NLS-1$
    
    /** The id of the MapViewport View */
    public final static String ID = "net.refractions.udig.project.ui.mapEditor"; //$NON-NLS-1$
    final MapEditorWithPalette editor = this;
    
    /**
     * This is responsible for tracking the active tool; it is a facility provided
     * by GEF. We are not using GEF tools directly; simply borrowing some of their
     * infrastructure to support a nice visual palette.
     * <p>
     * The *id* of the current tool in the editDomain is used to determine the 
     * active tool for the map.
     * <p>
     * Holds onto the paletteViewer while delegating to ToolManager
     */
    private MapEditDomain editDomain;

    final StatusLineManager statusLineManager = new StatusLineManager();
    private MapEditorSite mapEditorSite;
    private boolean dirty = false;
    
    private PaletteRoot paletteRoot;
    
    // private ViewportPane viewportPane;
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
    public MapEditorWithPalette() {
        // Make sure the featureEditorProcessor has been started.
        // This will load all the tools so we can use them        
        ProjectUIPlugin.getDefault().getFeatureEditProcessor();
    }

    public Composite getComposite() {
        return composite;
    }

    @Override
    protected PaletteRoot getPaletteRoot() {
        if (paletteRoot == null) {
            paletteRoot = MapToolPaletteFactory.createPalette();
        }
        return paletteRoot;
    }
    
    protected FlyoutPreferences getPalettePreferences() {
        return MapToolPaletteFactory.createPalettePreferences();
    }

    /**
     * The layer listener will listen for edit events and mark the
     * map as dirty if the layer is modified.
     */
    ILayerListener layerListener = new ILayerListener(){

        public void refresh( LayerEvent event ) {
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
    IMapCompositionListener mapCompositionListener = new IMapCompositionListener(){

        @SuppressWarnings("unchecked")
        public void changed( MapCompositionEvent event ) {
            switch( event.getType() ) {
            case MANY_ADDED:
                Collection<ILayer> added = (Collection<ILayer>) event.getNewValue();
                for( ILayer layer : added ) {
                    layer.addListener(layerListener);
                }
                break;
            case MANY_REMOVED:
                Collection<ILayer> removed = (Collection<ILayer>) event.getOldValue();
                for( ILayer layer : removed ) {
                    removeListenerFromLayer(event, layer);
                }
                break;
            case ADDED:

                ((ILayer) event.getNewValue()).addListener(layerListener);
                break;
            case REMOVED:

                ILayer removedLayer = ((ILayer) event.getOldValue());
                removeListenerFromLayer(event, removedLayer);
                break;
            default:
                break;
            }

            boolean dirty = isMapDirty();

            if (dirty != isDirty()) {
                setDirty(dirty);
            }
        }

        private void removeListenerFromLayer( MapCompositionEvent event, ILayer removedLayer ) {
            removedLayer.removeListener(layerListener);
            setDirty(isMapDirty());
        }

    };
    /**
     * Listens to the Map and will change the editor title if the map name
     * is changed. Also marks a layer as ditry if the edit manager has
     * some kind of event.
     */
    IMapListener mapListener = new IMapListener(){

        public void changed( final MapEvent event ) {
            if (composite == null)
                return; // the composite hasn't been created so chill out
            if (getMap() == null || composite.isDisposed()) {
                event.getSource().removeMapListener(this);
                return;
            }

            MapEditorWithPalette.this.composite.getDisplay().asyncExec(new Runnable(){
                public void run() {
                    switch( event.getType() ) {
                    case NAME:
                        setPartName((String) event.getNewValue()); // rename the map
                        break;
                    case EDIT_MANAGER:
                        for( ILayer layer : event.getSource().getMapLayers() ) {
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
     * Listens to the edito manager; will clear the dirty status after a commit or
     * after a rollback (as long as a map does not have temporary layers).
     */
    IEditManagerListener editListener = new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
            switch( event.getType() ) {
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
            if (getMap() == null)
                return false;
            List<Layer> layers = getMap().getLayersInternal();
            if (layers == null)
                return false;
            for( Layer layer : layers ) {
                if (layer.hasResource(ITransientResolve.class)) {
                    return true;
                }
            }
            return false;
        }
    };

    private LayerSelectionListener layerSelectionListener;

    private ReplaceableSelectionProvider replaceableSelectionProvider;
    private PreShutdownTask shutdownTask = new PreShutdownTask(){

        public int getProgressMonitorSteps() {
            return 3;
        }

        public boolean handlePreShutdownException( Throwable t, boolean forced ) {
            ProjectUIPlugin.log("error prepping map editors for shutdown", t); //$NON-NLS-1$
            return true;
        }

        public boolean preShutdown( IProgressMonitor monitor, IWorkbench workbench, boolean forced )
                throws Exception {
            monitor.beginTask("Saving Map Editor", 3); //$NON-NLS-1$
            save(new SubProgressMonitor(monitor, 1));
            if (dirty) {
                if (!forced) {
                    return false;
                } else {
                    setDirty(false);
                }
            }
            removeTemporaryLayers(ProjectPlugin.getPlugin().getPreferenceStore());
            monitor.worked(1);

            // save the map's URI in the preferences so that it will be loaded the next time udig is
            // run.
            Resource resource = getMap().eResource();
            if (resource != null) {
                // save editor
                try {
                    IPreferenceStore p = ProjectUIPlugin.getDefault().getPreferenceStore();
                    int numEditors = p.getInt(ID);
                    String id = ID + ":" + numEditors; //$NON-NLS-1$
                    numEditors++;
                    p.setValue(ID, numEditors);
                    String value = resource.getURI().toString();
                    p.setDefault(id, ""); //$NON-NLS-1$
                    p.setValue(id, value);
                } catch (Exception e) {
                    ProjectUIPlugin.log("Failure saving which maps are open", e); //$NON-NLS-1$
                }
            }

            monitor.worked(1);
            monitor.done();

            return true;
        }

        private void save( final IProgressMonitor monitor ) {
            if (dirty) {
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        IconAndMessageDialog d = new SaveDialog(Display.getCurrent()
                                .getActiveShell(), getMap());
                        int result = d.open();

                        if (result == IDialogConstants.YES_ID)
                            doSave(monitor);
                        else if (result != Window.CANCEL) {
                            setDirty(false);
                        }
                    }
                });
            }
        }

    };

    public Object getAdapter( Class adaptee ) {
        if (adaptee.isAssignableFrom(Map.class)) {
            return getMap();
        }
        if (adaptee.isAssignableFrom(ViewportPane.class)) {
            return viewer.getViewport();
        }
        return super.getAdapter(adaptee);
    }

    private void clearLayerDirtyFlag() {
        List<ILayer> layers = getMap().getMapLayers();
        for( ILayer layer : layers ) {
            layer.getBlackboard().put(LAYER_DIRTY_KEY, null);
        }
    }

    public void setFont( Control control ) {
        viewer.setFont(control);
        /*
        Display display = control.getDisplay();
        FontData[] data = display.getFontList("courier", true); //$NON-NLS-1$
        if (data.length <1) {
            data=control.getFont().getFontData();
        }
        for( int i = 0; i < data.length; i++ ) {
            if ( Platform.OS_MACOSX == Platform.getOS() )
                data[i].setHeight(12);
            else
                data[i].setHeight(10);
        }
        control.setFont(new Font(control.getDisplay(), data));
        */
    }

    /**
     * Displays a the current CRS and allows to change it
     */
    class StatusBarButton extends ContributionItem {
        static final String CRS_ITEM_ID = "CRS Display"; //$NON-NLS-1$

        static final String BOUNDS_ITEM_ID = "Bounds Display"; //$NON-NLS-1$
        static final int MAX_LENGTH = 12;
        private Button button;
        private String value;

        private String full;

        /**
         * Create new StatusBarLabel object
         */
        public StatusBarButton( String id, String initialValue ) {
            super(id);
            setText(initialValue);
        }

        /**
         * sets the current text.
         */
        public void setText( String text ) {
            value = text;
            full = value;
            if (value.length() > MAX_LENGTH) {
                int start2 = value.length() - 6;
                value = value.substring(0, 6) + "..." + value.substring(start2, value.length()); //$NON-NLS-1$
                System.out.println(value.length());
            }
            if (button != null && !button.isDisposed()) {
                button.setText(value);
            }
        }
        /**
         * @see org.eclipse.jface.action.IContributionItem#isDynamic()
         */
        public boolean isDynamic() {
            return true;
        }
        /**
         * @see org.eclipse.jface.action.ContributionItem#dispose()
         */
        public void dispose() {
            if (button != null)
                button.dispose();
            if (popup != null)
                popup.dispose();
        }
        /**
         * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void fill( Composite c ) {
            Label separator = new Label(c, SWT.SEPARATOR);
            StatusLineLayoutData data = new StatusLineLayoutData();
            separator.setLayoutData(data);
            data.widthHint = 1;
            data.heightHint = ScaleRatioLabel.STATUS_LINE_HEIGHT;
            button = new Button(c, SWT.PUSH | SWT.FLAT);
            setFont(button);
            data = new StatusLineLayoutData();
            button.setLayoutData(data);
            button.setText(value);
            button.addListener(SWT.Selection, new Listener(){

                public void handleEvent( Event event ) {
                    promptForCRS();
                }

            });
            button.addListener(SWT.MouseEnter, new Listener(){
                public void handleEvent( final Event event ) {
                    showFullText();
                }
            });
            data.widthHint = 132;
            data.heightHint = ScaleRatioLabel.STATUS_LINE_HEIGHT;
        }
        Label textLabel;
        Shell popup;

        private void promptForCRS() {
            /*
            CRSPropertyPage page = new CRSPropertyPage();
            page.setStrategy(new CRSPropertyPage.MapStrategy(getMap()));
            PreferenceManager mgr = new PreferenceManager();
            IPreferenceNode node = new PreferenceNode("1", page); //$NON-NLS-1$
            mgr.addToRoot(node);

            PreferenceDialog pdialog = new PreferenceDialog(getSite().getShell(), mgr);
            ZoomingDialog dialog = new ZoomingDialog(getSite().getShell(), pdialog, ZoomingDialog
                    .calculateBounds(button));
            dialog.open();
            */
            CoordinateReferenceSystem crs = getMap().getViewportModel().getCRS();            
            CRSChooserDialog dialog = new CRSChooserDialog( getSite().getShell(), crs );
            int code = dialog.open();
            if( Window.OK == code ){
                CoordinateReferenceSystem result = dialog.getResult();
                if( !result.equals(crs)){
                    getMap().sendCommandSync(new ChangeCRSCommand(result));
                    updateCRS();
                }
            }

        }

        void showFullText() {
            final Display display = button.getDisplay();
            if (popup == null) {
                popup = new Shell(display.getActiveShell(), SWT.NO_FOCUS | SWT.ON_TOP);
                popup.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                popup.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                popup.setLayout(new RowLayout());
                Composite composite = new Composite(popup, SWT.NONE);
                composite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                composite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                composite.setLayout(new RowLayout());
                textLabel = new Label(popup, SWT.NONE);
                textLabel.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                textLabel.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                textLabel.setFont(button.getFont());

            }
            Point location = statusLineManager.getControl().toDisplay(button.getLocation());
            location.y = location.y - ScaleRatioLabel.STATUS_LINE_HEIGHT;
            if (popup.isDisposed()) {
                return;
            }
            popup.setLocation(location);
            textLabel.setText(full);
            popup.setVisible(true);
            popup.pack(true);
            display.timerExec(500, new Runnable(){
                public void run() {
                    checkforMouseOver(display);
                }
            });
        }

        private void checkforMouseOver( final Display display ) {
            if (display.getCursorControl() == button) {
                display.timerExec(500, new Runnable(){
                    public void run() {
                        if (display.getCursorControl() == button) {
                            checkforMouseOver(display);
                        } else {
                            popup.setVisible(false);
                        }
                    }
                });
            } else {
                popup.setVisible(false);
            }

        }

        /**
         * Get current text.
         */
        public String getText() {
            return full;
        }
    }

    /**
     * Updates the crs label in the statusbar.
     */
    protected void updateCRS() {
        Map map = getMap();
        if (map == null) {
            getSite().getPage().closeEditor(this, false);
            return;
        }
        CoordinateReferenceSystem crs = map.getViewportModel().getCRS();
        if (crs == null || crs.getName() == null) {
            return;
        }

        final String full = crs.getName().getCode();
        if (full == null || isSame(full))
            return;

        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display == null)
            display = Display.getDefault();

        display.asyncExec(new Runnable(){
            public void run() {

                IContributionManager bar = mapEditorSite.getActionBars().getStatusLineManager();
                if (bar == null)
                    return;
                StatusBarButton label = (StatusBarButton) bar.find(StatusBarButton.CRS_ITEM_ID);
                if (label == null) {
                    label = new StatusBarButton(StatusBarButton.CRS_ITEM_ID, full);
                    bar.appendToGroup(StatusLineManager.MIDDLE_GROUP, label);
                    label.setVisible(true);
                    bar.update(true);
                    return;
                }
                label.setText(full);
            }
        });

    }

    /**
     * Makes sure the scale is displayed
     */
    protected void updateScaleLabel() {
        if (composite.isDisposed())
            return;
        if (Display.getCurrent() != null) {
            doUpdateScaleLabel();
            return;
        }
        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display == null)
            display = Display.getDefault();
        display.asyncExec(new Runnable(){
            public void run() {
                doUpdateScaleLabel();
            }

        });

    }
    void doUpdateScaleLabel() {
        IContributionManager bar = mapEditorSite.getActionBars().getStatusLineManager();
        if (bar == null)
            return;
        ScaleRatioLabel label = (ScaleRatioLabel) bar.find(ScaleRatioLabel.SCALE_ITEM_ID);
        if (label == null) {
            label = new ScaleRatioLabel(this);
            bar.appendToGroup(StatusLineManager.MIDDLE_GROUP, label);
            label.setVisible(true);
            bar.update(true);
        }
        label.setViewportModel(getMap().getViewportModel());
    }

    private boolean isSame( String crs ) {
        IContributionManager bar = getActionbar().getStatusLineManager();

        if (bar != null) {
            StatusBarButton label = (StatusBarButton) bar.find(StatusBarButton.CRS_ITEM_ID);
            if (label != null && crs.equals(label.getText()))
                return true;
        }
        return false;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {

        if (isTesting)
            return;
        if (getSite() == null || getSite().getPage() == null) {
            // Exception occurred before instantiating editor
            return;
        }

        runMapClosingInterceptors();

        deregisterFeatureFlasher();
        getSite().getPage().removePartListener(partlistener);
        if( viewer != null ){
        	viewer.getViewport().removePaneListener(getMap().getViewportModelInternal());
        }
        getMap().getViewportModelInternal().setInitialized(false);

        selectFeatureListener = null;
        partlistener = null;

        if (statusLineManager != null)
            statusLineManager.dispose();
        
        MapToolPaletteFactory.dispose( paletteRoot );
        paletteRoot = null;
        
        final ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        if (!PlatformUI.getWorkbench().isClosing()) {
            ShutdownTaskList.instance().removePreShutdownTask(shutdownTask);
            try {
                // kill rending now - even if it is moving
                getRenderManager().dispose();
            } catch (Throwable t) {
                ProjectUIPlugin.log("Shutting down rendering - " + t, null);
            }
            getMap().getEditManagerInternal().setEditFeature(null, null);
            try {
                PlatformGIS.run(new ISafeRunnable(){

                    public void handleException( Throwable exception ) {
                        ProjectUIPlugin.log("error saving map: " + getMap().getName(), exception); //$NON-NLS-1$
                    }

                    public void run() throws Exception {

                        removeTemporaryLayers(store);
                        Project p = getMap().getProjectInternal();
                        if (p != null) {
                            if (p.eResource() != null && p.eResource().isModified()) {
                                p.eResource().save(ProjectPlugin.getPlugin().saveOptions);
                            }
                            
                            /*
                             * when closing a map the platform wants to save the map resource,
                             * but if you are removing the map, its no longer available.
                             */
                            final Map map = getMap();
                            final Resource resource = map.eResource();
                            if (resource != null)
                                resource.save(ProjectPlugin.getPlugin().saveOptions);

                            // need to kick the Project so viewers will update
                            p.eNotify(new ENotificationImpl((InternalEObject) p, Notification.SET,
                                    ProjectPackage.PROJECT__ELEMENTS_INTERNAL, null, null));

                        } else {
                            final Resource resource = getMap().eResource();
                            if (resource != null)
                                resource.save(ProjectPlugin.getPlugin().saveOptions);
                        }
                        if( viewer != null ){
                            viewer.dispose();
                            viewer = null;
                        }
                        // setMap(null);
                    }

                });
            } catch (Exception e) {
                ProjectPlugin.log("Exception while saving Map", e); //$NON-NLS-1$
            }
        }

        super.dispose();

    }

    private void runMapClosingInterceptors() {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
            if (!MapInterceptor.CLOSING_ID.equals(element.getName())) //$NON-NLS-1$
                continue;
            try {
                MapInterceptor interceptor = (MapInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(getMap());
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    private void removeTemporaryLayers( IPreferenceStore store ) {
        if (store
                .getBoolean(net.refractions.udig.project.preferences.PreferenceConstants.P_REMOVE_LAYERS)) {
            List<Layer> layers = getMap().getLayersInternal();
            List<Layer> layersToRemove = new ArrayList<Layer>();
            for( Layer layer : layers ) {
                if (layer.getGeoResources().get(0).canResolve(ITransientResolve.class)) {
                    layersToRemove.add(layer);
                }
            }

            if (!layers.isEmpty()) {
                if (getMap().eResource() != null)
                    getMap().eResource().setModified(true);
                layers.removeAll(layersToRemove);
            }
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor ) {
        final boolean[] success = new boolean[]{false};

        PlatformGIS.syncInDisplayThread(new SaveMapPaletteRunnable(this, success));

        if (success[0]) {
            setDirty(false);
        } else {
            // abort shutdown if in progress
            monitor.setCanceled(true);
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) {
        setSite(site);
        setInput(input);
        // initialize ToolManager
        ApplicationGIS.getToolManager();
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    protected void setInput( IEditorInput input ) {
        if (getEditorInput() != null) {
            Map map = (Map) ((UDIGEditorInput) getEditorInput()).getProjectElement();
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

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public boolean isDirty() {
        return dirty;
    }

    private boolean isMapDirty() {
        boolean dirty = false;
        for( ILayer layer : getMap().getMapLayers() ) {
            if (layer.hasResource(ITransientResolve.class)) {
                dirty = true;
            }
            boolean layerIsDirty = layer.getBlackboard().get(LAYER_DIRTY_KEY) != null;
            if (layerIsDirty) {
                dirty = true;
            }
        }
        return dirty;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return true;
    }

    void setDirty( boolean dirty ) {
        if (dirty == this.dirty)
            return;

        this.dirty = dirty;

        if (!dirty) {
            clearLayerDirtyFlag();
        }

        Display.getDefault().asyncExec(new Runnable(){
            /**
             * @see java.lang.Runnable#run()
             */
            @SuppressWarnings("synthetic-access")
            public void run() {
                firePropertyChange(PROP_DIRTY);
            }
        });
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveOnCloseNeeded()
     */
    public boolean isSaveOnCloseNeeded() {
        return true;
    }


    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( final Composite parent ) {
        ShutdownTaskList.instance().addPreShutdownTask(shutdownTask);
        if( editDomain == null ){
            editDomain = new MapEditDomain(this);
        }
        setEditDomain( editDomain );
        
        // super class sets up the splitter; it needs the setEditDomain to be defined
        // prior to the method being called (so the FlyoutPaletteComposite split can latch on)
        
    	super.createPartControl(parent);
    	// the above sets up a splitter; and then calls GraphicalEditor.createGraphicalViewer
    	// which we can use to set up our display area...        
    }
    
    protected Control getGraphicalControl() {
    	return composite;
    }
    
	/**
	 * Hijacked; supposed to create a GraphicalViewer on the specified <code>Composite</code>.
	 * <p>
	 * Instead we steal the composite for our MapViewer.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createGraphicalViewer(Composite parent) {
		
		// GraphicalViewer viewer = new GraphicalViewerImpl();
		// viewer.createControl(parent);
		// setGraphicalViewer(viewer);
		// configureGraphicalViewer();
		// hookGraphicalViewer();
		// initializeGraphicalViewer();
		
    	composite = new Composite( parent, SWT.NO_BACKGROUND);
        
        composite.setLayout(new FormLayout());
        composite.setFont(parent.getFont());
        setPartName(getMap().getName());

        setTitleToolTip(Messages.MapEditor_titleToolTip);
        setTitleImage(ProjectUIPlugin.getDefault().getImage(ISharedImages.MAP_OBJ));

        final IPreferenceStore preferenceStore = ProjectPlugin.getPlugin().getPreferenceStore();
        boolean istiled = preferenceStore
                .getBoolean(net.refractions.udig.project.preferences.PreferenceConstants.P_TILED_RENDERING);

        if (!istiled) {
            viewer = new MapViewer(composite, SWT.DOUBLE_BUFFERED);
        } else {
            viewer = new MapViewer(composite, SWT.MULTI | SWT.NO_BACKGROUND);
        }
        // we need an edit domain for GEF
        // This represents the "Current Tool" for the Palette
        // We should not duplicate the idea of current tools so we may
        // need to delegate to getEditDomain; and just use the MapEditTool *id*
        // editDomain = new MapEditDomain(this);
        // setEditDomain( editDomain );

        // allow the viewer to open our context menu; work with our selection proivder etc
        viewer.init(this);
        
        // if a map was provided as input we can ask the viewer to use it
        Map input = (Map) ((UDIGEditorInput) getEditorInput()).getProjectElement();
        if (input != null) {
            viewer.setMap(input);
        }

        FormData formdata = new FormData();
        formdata.top = new FormAttachment(0);
        formdata.bottom = new FormAttachment(100, -ScaleRatioLabel.STATUS_LINE_HEIGHT);
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

        layerSelectionListener = new LayerSelectionListener(new LayerSelectionListener.Callback(){

            public void callback( List<Layer> layers ) {
                if (composite.isDisposed()) {
                    getSite().getPage().removePostSelectionListener(layerSelectionListener);
                    return; // component.isVisible cannot be called on a disposed component
                } else if (!composite.isVisible())
                    return;
                Layer layer = layers.get(0);
                // Second condition excludes unnecessary UI call
                if (layer.getMap() == getMap()
                        && getMap().getEditManager().getSelectedLayer() != layer) {
                    SelectLayerCommand selectLayerCommand = new SelectLayerCommand(layer);
                    selectLayerCommand.setMap(getMap());
                    try {
                        selectLayerCommand.run(ProgressManager.instance().get());
                    } catch (Exception e) {
                        throw (RuntimeException) new RuntimeException().initCause(e);
                    }
                    getMap().sendCommandSync(new UndoRedoCommand(selectLayerCommand));
                }
            }

        });
        getSite().getPage().addPostSelectionListener(layerSelectionListener);

        for( Layer layer : getMap().getLayersInternal() ) {
            layer.addListener(layerListener);
        }

        dropTarget = UDIGDragDropUtilities.addDropSupport(viewer.getViewport().getControl(), this);
        this.replaceableSelectionProvider = new ReplaceableSelectionProvider();
        getSite().setSelectionProvider(replaceableSelectionProvider);
        runMapOpeningInterceptor(getMap());
        mapEditorSite = new MapEditorSite(super.getSite(), this);
        updateCRS();
        updateScaleLabel();

        getMap().getViewportModel().addViewportModelListener(new IViewportModelListener(){

            public void changed( ViewportModelEvent event ) {
                if (getMap() == null) {
                    event.getSource().removeViewportModelListener(this);
                    return;
                }
                if (event.getType() == EventType.CRS) {
                    updateCRS();
                }
            }

        });

        setDirty(isMapDirty());
	}

    private void runMapOpeningInterceptor( Map map ) {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
            if (!MapInterceptor.OPENING_ID.equals(element.getName())) //$NON-NLS-1$
                continue;
            try {
                MapInterceptor interceptor = (MapInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(map);
            } catch (Exception e) {
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
            IWorkbenchPage page = getSite().getPage();
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
            contextMenu.addMenuListener(new IMenuListener(){
                public void menuAboutToShow( IMenuManager mgr ) {
                    IToolManager tm = ApplicationGIS.getToolManager();
                    
                    contextMenu.add(tm.getENTERAction());
                    contextMenu.add(new Separator());
                    
                    contextMenu.add(tm.getZOOMTOSELECTEDAction());
                    contextMenu.add(new Separator());
                    contextMenu.add(tm.getBACKWARD_HISTORYAction());
                    contextMenu.add(tm.getFORWARD_HISTORYAction());
                    contextMenu.add(new Separator());
                    //contextMenu.add(tm.createCUTAction(MapEditorWithPalette.this));
                    contextMenu.add(tm.getCOPYAction(MapEditorWithPalette.this));
                    contextMenu.add(tm.getPASTEAction(MapEditorWithPalette.this));
                    contextMenu.add(tm.getDELETEAction());

                    /*
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
                    new ISelectionProvider(){

                        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
                        }

                        public ISelection getSelection() {
                            return new StructuredSelection(getMap());
                        }

                        public void removeSelectionChangedListener(
                                ISelectionChangedListener listener ) {
                        }

                        public void setSelection( ISelection selection ) {
                        }

                    });

            propertiesAction = new Action(){
                @Override
                public void runWithEvent( Event event ) {
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
    public void setFocus() {
        composite.setFocus();
        updateCRS();
        updateScaleLabel();
    }

    /**
     * Returns the map that this editor edits
     * 
     * @return Returns the map that this editor edits
     */
    public Map getMap() {
        // return viewer.getMap();
        UDIGEditorInput editorInput = (UDIGEditorInput) getEditorInput();
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

    IPartListener2 partlistener = new IPartListener2(){
        public void partActivated( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapEditorWithPalette.this) {
                registerFeatureFlasher();
                IToolManager tools = ApplicationGIS.getToolManager();
                tools.setCurrentEditor(editor);
                //editor.viewer.setModalTool( (ModalTool) tools.getActiveTool() );
            }
        }

        public void partBroughtToTop( IWorkbenchPartReference partRef ) {
        }

        public void partClosed( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapEditorWithPalette.this) {
                deregisterFeatureFlasher();
                visible = false;
            }
        }

        public void partDeactivated( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        public void partOpened( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        public void partHidden( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapEditorWithPalette.this) {
                deregisterFeatureFlasher();
                visible = false;
            }
        }

        public void partVisible( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapEditorWithPalette.this) {
                registerFeatureFlasher();
                visible = true;
            }
        }

        public void partInputChanged( IWorkbenchPartReference partRef ) {
        }

    };

    private boolean draggingEnabled;
    private volatile boolean visible = false;

    /**
     * Opens the map's context menu.
     */
    public void openContextMenu() {
        viewer.openContextMenu();
        /*getEditorSite().getShell().getDisplay().asyncExec(new Runnable(){
            public void run() {
                menu.setVisible(true);
            }
        });
        */
    }

    public UDIGDropHandler getDropHandler() {
        return ((UDIGControlDropListener) dropTarget.listener).getHandler();
    }

    public Object getTarget( DropTargetEvent event ) {
        return this;
    }

    /**
     * Enables or disables dragging (drag and drop) from the map editor.
     */
    public void setDragging( boolean enable ) {
        if (draggingEnabled == enable)
            return;
        if (enable) {
            dragSource = UDIGDragDropUtilities.addDragSupport(viewer.getViewport().getControl(),
                    getSite().getSelectionProvider());
        } else {
            dragSource.source.dispose();
        }
        draggingEnabled = enable;
    }

    public boolean isDragging() {
        return draggingEnabled;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public void setSelectionProvider( IMapEditorSelectionProvider selectionProvider ) {
        if (selectionProvider == null) {
            throw new NullPointerException("selection provider must not be null!"); //$NON-NLS-1$
        }
        selectionProvider.setActiveMap(getMap(), this);
        if (selectionProvider != this.replaceableSelectionProvider.getSelectionProvider()) {
            this.replaceableSelectionProvider.setProvider(selectionProvider);
        }
        createContextMenu();
    }

    public MapEditorSite getMapEditorSite() {
        return mapEditorSite;
    }

    private class FlashFeatureListener implements ISelectionListener {

        public void selectionChanged( IWorkbenchPart part, final ISelection selection ) {
            if (part == MapEditorWithPalette.this || getSite().getPage().getActivePart() != part
                    || selection instanceof IBlockingSelection)
                return;

            ISafeRunnable sendAnimation = new ISafeRunnable(){
                public void run() {
                    if (selection instanceof IStructuredSelection) {
                        IStructuredSelection s = (IStructuredSelection) selection;
                        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
                        for( Iterator iter = s.iterator(); iter.hasNext(); ) {
                            Object element = iter.next();

                            if (element instanceof SimpleFeature) {
                                SimpleFeature feature = (SimpleFeature) element;
                                features.add(feature);
                            }
                        }
                        if (features.size() == 0)
                            return;
                        if (!getRenderManager().isDisposed()) {
                            IAnimation anim = createAnimation(features);
                            if (anim != null)
                                AnimationUpdater.runTimer(getMap().getRenderManager()
                                        .getMapDisplay(), anim);
                        }
                    }
                }
                public void handleException( Throwable exception ) {
                    ProjectUIPlugin.log("Exception preparing animation", exception); //$NON-NLS-1$
                }
            };

            try {
                sendAnimation.run();
            } catch (Exception e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
            }
            // PlatformGIS.run(sendAnimation);
        }

        private IAnimation createAnimation( List<SimpleFeature> current ) {
            final List<IDrawCommand> commands = new ArrayList<IDrawCommand>();
            for( SimpleFeature feature : current ) {
                if (feature == null || feature.getFeatureType().getGeometryDescriptor() == null)
                    continue;
                DrawFeatureCommand command = null;
                if (feature instanceof IAdaptable) {
                    Layer layer = (Layer) ((IAdaptable) feature).getAdapter(Layer.class);
                    if (layer != null)
                        try {
                            command = new DrawFeatureCommand(feature, layer);
                        } catch (IOException e) {
                            // do nothing... thats life
                        }
                }
                if (command == null) {
                    command = new DrawFeatureCommand(feature);
                }
                command.setMap(getMap());
                command.preRender();
                commands.add(command);
            }
            Rectangle2D rect = new Rectangle();
            // for( IDrawCommand command : commands ) {
            // rect=rect.createUnion(command.getValidArea());
            // }
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

    public IStatusLineManager getStatusLineManager() {
    	return statusLineManager;
    }
    public MapEditDomain getEditDomain(){
        return editDomain;
    }

}
