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

import net.refractions.udig.catalog.IGeoResource;
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
import net.refractions.udig.project.internal.commands.selection.SelectLayerCommand;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.render.ViewportModelEvent.EventType;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.UDIGEditorInput;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import net.refractions.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneSWT;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.ui.IBlockingSelection;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.PreShutdownTask;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.ShutdownTaskList;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.ZoomingDialog;
import net.refractions.udig.ui.UDIGDragDropUtilities.DragSourceDescriptor;
import net.refractions.udig.ui.UDIGDragDropUtilities.DropTargetDescriptor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.geotools.feature.Feature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class is the Eclipse editor Part in which a ViewportPane is embedded. The ViewportPane
 * displays and edits Maps. MapViewport is used to intialize ViewportPane and the RenderManager.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class MapEditor extends EditorPart implements IDropTargetProvider, IAdaptable, MapPart {
    private static final String LAYER_DIRTY_KEY = "DIRTY"; //$NON-NLS-1$
    /** The id of the MapViewport View */
    public final static String ID = "net.refractions.udig.project.ui.mapEditor"; //$NON-NLS-1$
    final static int STATUS_LINE_HEIGHT;
    static {
        if (Platform.getWS().equals(Platform.WS_WIN32)) {
            STATUS_LINE_HEIGHT = 24;
        } else {
            STATUS_LINE_HEIGHT = 32;
        }
    }
    RenderManager renderManager = null;

    final MapEditor editor = this;
    final StatusLineManager statusLineManager=new StatusLineManager();
    private MapEditorSite mapEditorSite;
    private boolean dirty = false;

    protected Map map;

    private Composite composite;

    Menu menu;

    private ViewportPane viewportPane;

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

    ILayerListener layerListener = new ILayerListener(){

        public void refresh( LayerEvent event ) {
            if (event.getType() == LayerEvent.EventType.EDIT_EVENT) {
                setDirty(true);
                event.getSource().getBlackboard().put(LAYER_DIRTY_KEY, "true"); //$NON-NLS-1$
            }
        }

    };

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


            if( dirty!=isDirty() ){
                setDirty(dirty);
            }
        }


        private void removeListenerFromLayer( MapCompositionEvent event, ILayer removedLayer ) {
            removedLayer.removeListener(layerListener);
            setDirty(isMapDirty());
        }

    };

    IMapListener mapListener = new IMapListener(){

        public void changed( final MapEvent event ) {
            if (composite == null)
                return; // the composite hasn't been created so chill out
            if (map == null || composite.isDisposed()) {
                event.getSource().removeMapListener(this);
                return;
            }

            MapEditor.this.composite.getDisplay().asyncExec(new Runnable(){
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

    IEditManagerListener editListener = new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
            switch( event.getType() ) {
            case EditManagerEvent.POST_COMMIT:
            	if( !hasTemporaryLayers() ){
            		setDirty(false);
            	}
                break;
            case EditManagerEvent.POST_ROLLBACK:
            	if( !hasTemporaryLayers() ){
            		setDirty(false);
            	}
                break;
            default:
                break;
            }
        }

		private boolean hasTemporaryLayers() {
			List<Layer> layers = map.getLayersInternal();
			for (Layer layer : layers) {
				if( layer.hasResource(ITransientResolve.class) ){
					return true;
				}
			}
			return false;
		}
    };

    private LayerSelectionListener layerSelectionListener;

    private ReplaceableSelectionProvider selectionProvider;
    private PreShutdownTask shutdownTask=new PreShutdownTask(){

        public int getProgressMonitorSteps() {
            return 3;
        }

        public boolean handlePreShutdownException( Throwable t, boolean forced ) {
            ProjectUIPlugin.log("error prepping map editors for shutdown", t); //$NON-NLS-1$
            return true;
        }

        public boolean preShutdown( IProgressMonitor monitor, IWorkbench workbench, boolean forced ) throws Exception {
            monitor.beginTask("Saving Map Editor", 3); //$NON-NLS-1$
            save(new SubProgressMonitor(monitor, 1));
            if( dirty){
                if( !forced ){
                    return false;
                }else{
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

        private void save(final IProgressMonitor monitor) {
            if( dirty ){
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        IconAndMessageDialog d=new SaveDialog(Display.getCurrent().getActiveShell(),map);
                        int result = d.open();

                        if( result==IDialogConstants.YES_ID )
                            doSave(monitor);
                        else if( result!=Window.CANCEL ){
                            setDirty(false);
                        }
                    }
                });
            }
        }

    };

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adaptee ) {
        if (adaptee.isAssignableFrom(Map.class)) {
            return map;
        }
        if (adaptee.isAssignableFrom(ViewportPane.class)) {
            return viewportPane;
        }
        return super.getAdapter(adaptee);
    }

    private void clearLayerDirtyFlag() {
        List<ILayer> layers = getMap().getMapLayers();
        for( ILayer layer : layers ) {
            layer.getBlackboard().put(LAYER_DIRTY_KEY, null);
        }
    }

    public void setFont( Control textArea2 ) {
        Display display = textArea2.getDisplay();
        FontData[] data = display.getFontList("courier", true); //$NON-NLS-1$
        if (data.length <1) {
            data=textArea2.getFont().getFontData();
        }
        for( int i = 0; i < data.length; i++ ) {
            if ( Platform.OS_MACOSX == Platform.getOS() )
                data[i].setHeight(12);
            else
                data[i].setHeight(10);
        }
        textArea2.setFont(new Font(textArea2.getDisplay(), data));
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
                int start2=value.length()-6;
                value = value.substring(0, 6)
                        + "..." + value.substring(start2, value.length()); //$NON-NLS-1$
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
            if( popup !=null )
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
            data.heightHint = STATUS_LINE_HEIGHT;
            button = new Button(c, SWT.PUSH|SWT.FLAT);
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
            data.heightHint = STATUS_LINE_HEIGHT;
        }
        Label textLabel;
        Shell popup;


        private void promptForCRS() {
            CRSPropertyPage page = new CRSPropertyPage();
            page.setStrategy(new CRSPropertyPage.MapStrategy(getMap()));
            PreferenceManager mgr = new PreferenceManager();
            IPreferenceNode node = new PreferenceNode("1", page); //$NON-NLS-1$
            mgr.addToRoot(node);

            PreferenceDialog pdialog = new PreferenceDialog(getSite().getShell(), mgr);
            ZoomingDialog dialog = new ZoomingDialog(getSite().getShell(), pdialog, ZoomingDialog.calculateBounds(button));
            dialog.open();
            updateCRS();
        }

        void showFullText(){
            final Display display=button.getDisplay();
            if( popup==null ){
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
            location.y = location.y - STATUS_LINE_HEIGHT;
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

        private void checkforMouseOver(final Display display){
            if (display.getCursorControl()==button ){
                display.timerExec(500, new Runnable(){
                    public void run() {
                        if (display.getCursorControl()==button ){
                            checkforMouseOver(display);
                        }else{
                            popup.setVisible(false);
                        }
                    }
                });
            }else{
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
        if ( map == null ){
            getSite().getPage().closeEditor(this, false);
            return;
        }
        CoordinateReferenceSystem crs = map.getViewportModel().getCRS();
        final String full = crs.getName().getCode();
        if (isSame(full))
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
        if( composite.isDisposed() )
            return;
        if( Display.getCurrent()!=null ){
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
        if( getSite()==null || getSite().getPage()==null ){
        	// Exception occurred before instantiating editor
        	return;
        }


        runMapClosingInterceptors();

        deregisterFeatureFlasher();
        getSite().getPage().removePartListener(partlistener);
        viewportPane.removePaneListener(getMap().getViewportModelInternal());
        getMap().getViewportModelInternal().setInitialized(false);

        selectFeatureListener=null;
        partlistener=null;

        if( statusLineManager!=null )
            statusLineManager.dispose();

        final ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        if (!PlatformUI.getWorkbench().isClosing()) {
            ShutdownTaskList.instance().removePreShutdownTask(shutdownTask);
            renderManager.dispose();
            map.getEditManagerInternal().setEditFeature(null, null);
            try {
                PlatformGIS.run(new ISafeRunnable(){

                    public void handleException( Throwable exception ) {
                        ProjectUIPlugin.log("error saving map: "+map.getName(), exception); //$NON-NLS-1$
                    }

                    public void run() throws Exception {

                        removeTemporaryLayers(store);
                        Project p = map.getProjectInternal();
                            if (p != null) {
                                if (p.eResource() != null && p.eResource().isModified()) {
                                    p.eResource().save(ProjectPlugin.getPlugin().saveOptions);
                                }

                                final Resource resource = map.eResource();
                                resource.save(ProjectPlugin.getPlugin().saveOptions);

                                // need to kick the Project so viewers will update
                                p.eNotify(new ENotificationImpl((InternalEObject) p, Notification.SET,
                                        ProjectPackage.PROJECT__ELEMENTS_INTERNAL, null, null));

                            } else {
                                final Resource resource = map.eResource();
                                if( resource != null )
                                    resource.save(ProjectPlugin.getPlugin().saveOptions);
                            }

                            map = null;
                    }

                });
            } catch (Exception e) {
                ProjectPlugin.log("Exception while saving Map", e);  //$NON-NLS-1$
            }
        }


        super.dispose();

    }

    private void runMapClosingInterceptors() {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
            if (!"mapClosing".equals(element.getName())) //$NON-NLS-1$
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

    private void removeTemporaryLayers( IPreferenceStore store ) {
        if (store
                .getBoolean(net.refractions.udig.project.preferences.PreferenceConstants.P_REMOVE_LAYERS)) {
            List<Layer> layers = map.getLayersInternal();
            List<Layer> layersToRemove = new ArrayList<Layer>();
            for( Layer layer : layers ) {
                List<IGeoResource> geoResources = layer.getGeoResources();
                if (!geoResources.isEmpty() && geoResources.get(0).canResolve(ITransientResolve.class)) {
                    layersToRemove.add(layer);
                }
            }

            if (!layers.isEmpty()) {
                if (map.eResource() != null)
                    map.eResource().setModified(true);
                layers.removeAll(layersToRemove);
            }
        }
    }


    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor ) {
        final boolean[] success = new boolean[] {false};
        PlatformGIS.syncInDisplayThread(new SaveMapRunnable(this,success));

        if (success[0]) {
            setDirty(false);
        } else {
            //abort shutdown if in progress
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
            map.removeMapCompositionListener(mapCompositionListener);
            map.removeMapListener(mapListener);
            map.getEditManager().removeListener(editListener);
        }
        if (input != null) {
            map = (Map) ((UDIGEditorInput) input).getProjectElement();
            map.addMapCompositionListener(mapCompositionListener);
            map.addMapListener(mapListener);
            map.getEditManager().addListener(editListener);
        }
        super.setInput(input);
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public boolean isDirty() {
        return dirty;
    }

    private boolean isMapDirty() {
        boolean dirty=false;
        for( ILayer layer : getMap().getMapLayers() ) {
            if( layer.hasResource(ITransientResolve.class) ){
                dirty=true;
            }
            boolean layerIsDirty=layer.getBlackboard().get(LAYER_DIRTY_KEY)!=null;
            if( layerIsDirty ){
                dirty=true;
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

        if( !dirty ){
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

        this.composite = new Composite(parent, SWT.NO_BACKGROUND);
        composite.setLayout(new FormLayout());
        composite.setFont(parent.getFont());
        setPartName(map.getName());

        setTitleToolTip(Messages.MapEditor_titleToolTip);
        setTitleImage(Images.get(ISharedImages.MAP_OBJ));

        viewportPane = new ViewportPaneSWT(composite, this);
//        viewportPane = new ViewportPaneJava(composite, this);

        FormData formdata=new FormData();
        formdata.top=new FormAttachment(0);
        formdata.bottom=new FormAttachment(100,-STATUS_LINE_HEIGHT);
        formdata.left=new FormAttachment(0);
        formdata.right=new FormAttachment(100);
        viewportPane.getControl().setLayoutData(formdata);

        statusLineManager.add(new GroupMarker(StatusLineManager.BEGIN_GROUP));
        statusLineManager.add(new GroupMarker(StatusLineManager.MIDDLE_GROUP));
        statusLineManager.add(new GroupMarker(StatusLineManager.END_GROUP));
        statusLineManager.createControl(composite, SWT.BORDER);
        formdata=new FormData();
        formdata.left=new FormAttachment(0);
        formdata.right=new FormAttachment(100);
        formdata.top=new FormAttachment(viewportPane.getControl(), 0,SWT.BOTTOM);
        formdata.bottom=new FormAttachment(100);
        statusLineManager.getControl().setLayoutData(formdata);

        getSite().getPage().addPartListener(partlistener);
        registerFeatureFlasher();
        if (map.getRenderManager() == null)
            map.setRenderManagerInternal(new RenderManagerDynamic());

        renderManager = map.getRenderManagerInternal();
        viewportPane.setRenderManager(renderManager);
        renderManager.setMapDisplay(viewportPane);

        viewportPane.addPaneListener(getMap().getViewportModelInternal());

        layerSelectionListener = new LayerSelectionListener(new LayerSelectionListener.Callback(){

            public void callback( List<Layer> layers ) {
                if (composite.isDisposed()) {
                    getSite().getPage().removePostSelectionListener(layerSelectionListener);
                    return; // component.isVisible cannot be called on a disposed component
                } else if (!composite.isVisible())
                    return;
                Layer layer = layers.get(0);
                //Second condition excludes unnecessary UI call
                if( layer.getMap()==map
                		&& map.getEditManager().getSelectedLayer() != layer){
                    SelectLayerCommand selectLayerCommand = new SelectLayerCommand(layer);
                    selectLayerCommand.setMap(map);
                    try {
                        selectLayerCommand.run(ProgressManager.instance().get());
                    } catch (Exception e) {
                        throw (RuntimeException) new RuntimeException( ).initCause( e );
                    }
                    map.sendCommandSync(new UndoRedoCommand(selectLayerCommand) );
                }
            }

        });
        getSite().getPage().addPostSelectionListener(layerSelectionListener);

        for (Layer layer : getMap().getLayersInternal()) {
            layer.addListener(layerListener);
        }

        dropTarget = UDIGDragDropUtilities.addDropSupport(viewportPane.getControl(), this);
        this.selectionProvider = new ReplaceableSelectionProvider();
        getSite().setSelectionProvider(selectionProvider);
        runMapOpeningInterceptor(map);
        mapEditorSite=new MapEditorSite(super.getSite(), this);
        updateCRS();
        updateScaleLabel();

        map.getViewportModel().addViewportModelListener(new IViewportModelListener(){

            public void changed( ViewportModelEvent event ) {
                if( getMap()==null ){
                    event.getSource().removeViewportModelListener(this);
                    return;
                }
                if(event.getType()==EventType.CRS ){
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
            if (!"mapOpening".equals(element.getName())) //$NON-NLS-1$
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

    private FlashFeatureListener selectFeatureListener=new FlashFeatureListener();
    private boolean flashFeatureRegistered=false;
    private Action propertiesAction;
    /**
     * registers a listener with the current page that flashes a feature each time the current
     * selected feature changes.
     */
    protected synchronized void registerFeatureFlasher() {
        if( !flashFeatureRegistered ){
            flashFeatureRegistered=true;
            IWorkbenchPage page=getSite().getPage();
            page.addPostSelectionListener(selectFeatureListener);
        }
    }

    protected synchronized void deregisterFeatureFlasher() {
        flashFeatureRegistered=false;
        getSite().getPage().removePostSelectionListener(selectFeatureListener);
    }

    void createContextMenu() {
    	if(menu == null){
    		final MenuManager contextMenu = new MenuManager();
    		contextMenu.setRemoveAllWhenShown(true);
    		contextMenu.addMenuListener(new IMenuListener(){
    			public void menuAboutToShow( IMenuManager mgr ) {
    				IToolManager tm = ApplicationGIS.getToolManager();
    				contextMenu.add(tm.getBACKWARD_HISTORYAction());
    				contextMenu.add(tm.getFORWARD_HISTORYAction());
    				contextMenu.add(new Separator());
    				// contextMenu.add(tm.createCUTAction(MapEditor.this));
    				contextMenu.add(tm.getCOPYAction(MapEditor.this));
    				contextMenu.add(tm.getPASTEAction(MapEditor.this));
    				contextMenu.add(tm.getDELETEAction());

                    /*
                     * Gets contributions from active modal tool if possible
                     */
                    tm.contributeActiveModalTool(contextMenu);

                    contextMenu.add(new Separator());
    				contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    				if (map.getEditManager().getEditFeature() != null) {
    					contextMenu.add(ProjectUIPlugin.getDefault().getFeatureEditProcessor()
    							.getEditFeatureAction(getSite().getSelectionProvider().getSelection()));

    					contextMenu
    					.add(ProjectUIPlugin.getDefault().getFeatureEditProcessor()
    							.getEditWithFeatureMenu(
    									getSite().getSelectionProvider().getSelection()));
    				}
    				contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(selectionProvider.getSelection()));
                    contextMenu.add(new Separator());
                    contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
                    contextMenu.add(new Separator());
                    contextMenu.add(getPropertiesAction());
    			}
    		});

    		// Create menu.
    		menu = contextMenu.createContextMenu(composite);
    		getSite().registerContextMenu(contextMenu, getSite().getSelectionProvider());
    	}
    }

    protected IAction getPropertiesAction() {
        if (propertiesAction == null) {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final PropertyDialogAction tmp = new PropertyDialogAction(new SameShellProvider(shell),
                    new ISelectionProvider() {

                        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
                        }

                        public ISelection getSelection() {
                            return new StructuredSelection(map);
                        }

                        public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
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
        return map;
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
            if (partRef.getPart(false) == MapEditor.this){
                registerFeatureFlasher();
                ApplicationGIS.getToolManager().setCurrentEditor(editor);
            }
        }

        public void partBroughtToTop( IWorkbenchPartReference partRef ) {
        }

        public void partClosed( IWorkbenchPartReference partRef ) {
                if (partRef.getPart(false) == MapEditor.this){
                    deregisterFeatureFlasher();
                    visible=false;
                }
        }

        public void partDeactivated( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        public void partOpened( IWorkbenchPartReference partRef ) {
            // do nothing
        }

        public void partHidden( IWorkbenchPartReference partRef ) {
             if (partRef.getPart(false) == MapEditor.this){
                 deregisterFeatureFlasher();
                 visible=false;
             }
        }


        public void partVisible( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapEditor.this){
                    registerFeatureFlasher();
                    visible=true;
            }
        }

        public void partInputChanged( IWorkbenchPartReference partRef ) {
        }

    };

    private boolean draggingEnabled;
    private volatile boolean visible=false;

    /**
     * Opens the map's context menu.
     */
    public void openContextMenu() {
        getEditorSite().getShell().getDisplay().asyncExec(new Runnable(){
            public void run() {
                menu.setVisible(true);
            }
        });
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
            dragSource = UDIGDragDropUtilities.addDragSupport(viewportPane.getControl(), getSite()
                    .getSelectionProvider());
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

    /**
     * @return Returns the composite.
     */
    public Composite getComposite() {
        return composite;
    }

    public void setSelectionProvider( IMapEditorSelectionProvider selectionProvider ) {
        if (selectionProvider == null) {
            throw new NullPointerException("selection provider must not be null!"); //$NON-NLS-1$
        }
        selectionProvider.setActiveMap(getMap(), this);
        if (selectionProvider != this.selectionProvider.getSelectionProvider()) {
            this.selectionProvider.setProvider(selectionProvider);
        }
        createContextMenu();
    }

    public MapEditorSite getMapEditorSite(){
        return mapEditorSite;
    }

	private class FlashFeatureListener implements ISelectionListener {

        public void selectionChanged( IWorkbenchPart part, final ISelection selection ) {
            if (part == MapEditor.this || getSite().getPage().getActivePart() != part || selection instanceof IBlockingSelection)
                return;

            ISafeRunnable sendAnimation=new ISafeRunnable(){
                public void run() {
                    List<Feature> features = new ArrayList<Feature>();
                    IStructuredSelection s = (IStructuredSelection) selection;
                    for( Iterator iter = s.iterator(); iter.hasNext(); ) {
                        Object element = iter.next();

                        if (element instanceof Feature) {
                            Feature feature = (Feature) element;
                            features.add(feature);
                        }
                    }
                    if (features.size() == 0)
                        return;
                    if (!renderManager.isDisposed()) {
                        IAnimation anim = createAnimation(features);
                        if (anim != null)
                            AnimationUpdater.runTimer(map.getRenderManager().getMapDisplay(), anim);
                    }
                }
                public void handleException(Throwable exception){
                    ProjectUIPlugin.log("Exception preparing animation", exception); //$NON-NLS-1$
                }
            };

            try{
                sendAnimation.run();
            }catch (Exception e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
            }
//            PlatformGIS.run(sendAnimation);
        }

        private IAnimation createAnimation( List<Feature> current ) {
            final List<IDrawCommand> commands = new ArrayList<IDrawCommand>();
            for( Feature feature : current ) {
                if( feature==null || feature.getFeatureType().getDefaultGeometry()==null )
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
                if (command == null){
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
            class FeatureAnimation extends AbstractDrawCommand implements IAnimation {

                private int runs = 0;

                public short getFrameInterval() {
                    return 300;
                }

                public void nextFrame() {
                    runs++;
                }

                public boolean hasNext() {
                    return runs < 6;
                }

                public void run( IProgressMonitor monitor ) throws Exception {
                    if (runs % 2 == 0) {
                        for( IDrawCommand command : commands ) {
                            command.setGraphics(graphics, display);
                            command.setMap(getMap());
                            command.run(monitor);
                        }
                    }
                }

                public Rectangle getValidArea() {
                    return validArea;
                }

                public void setValid(boolean valid){
                    super.setValid(valid);
                    for( IDrawCommand command : commands ) {
                        command.setValid(valid);
                    }
                }

            }
            return new FeatureAnimation();
        }
    }

    public boolean isVisible() {
        return visible;
    }

}
