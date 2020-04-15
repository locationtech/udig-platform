/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.locationtech.udig.internal.ui.IDropTargetProvider;
import org.locationtech.udig.project.BlackboardEvent;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IBlackboardListener;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.map.LayerMoveBackCommand;
import org.locationtech.udig.project.command.map.LayerMoveDownCommand;
import org.locationtech.udig.project.command.map.LayerMoveFrontCommand;
import org.locationtech.udig.project.command.map.LayerMoveUpCommand;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.AddFolderItemCommand;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.ui.AdapterFactoryLabelProviderDecorator;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.actions.Delete;
import org.locationtech.udig.project.ui.internal.actions.MylarAction;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.UDIGDragDropUtilities;

/**
 * The Legend View. This view allows the user to group layers together and set them into different
 * categories. The view also provides facilities to show/hide map graphics and background layers.
 * Also, additional layer sorting functionalities (similar to power-point sorting) are implemented.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendView extends ViewPart implements IDropTargetProvider, ISelectionChangedListener {
    
    public static final String ID = "org.locationtech.udig.project.ui.legendManager"; //$NON-NLS-1$
    
    private Map currentMap;
    
    private LegendViewGridHandler gridHandler = new LegendViewGridHandler();
    private LegendViewFiltersHandler filtersHandler = new LegendViewFiltersHandler(this);
    
    private LayerAction downAction;
    private LayerAction upAction;    
    private LayerAction frontAction;
    private LayerAction backAction;
    
    private IAction newFolderAction;
    private LegendViewRenameFolderAction renameFolderAction;
    
    private CheckboxTreeViewer viewer;
    private LegendViewContentProvider contentProvider;
    private AdapterFactoryLabelProviderDecorator labelProvider;
    private ILabelProviderListener labelProviderListener = new LabelProviderListerner();
    private CheckStateListener checkStateListener = new CheckStateListener();
    private CollapseExpandListener collapeExpandListener = new CollapseExpandListener();
    
    private IAction deleteAction;
    
    //Listens to changes to selected views/editors
    private MapEditorListener partServiceListener = new MapEditorListener();
    //Listens to changes to current map edit manager
    private EditManagerListener editManagerListener;
    private IBlackboardListener mylarListener = new BlackboardListener();
    private Adapter mapDeepListener = new MapDeepListener();
    private ZoomListener zoomListener = new ZoomListener();
    
    
    /**
     * For test case use only
     * @return LegendViewGridHandler
     */
    public LegendViewGridHandler getGridHandler() {
        return gridHandler;
    }

    /**
     * For test case use only
     * @return LegendViewFiltersHandler
     */
    public LegendViewFiltersHandler getFiltersHandler() {
        return filtersHandler;
    }

    /**
     * For test case use only
     * @return frontAction
     */
    public LayerAction getFrontAction() {
        return frontAction;
    }

    /**
     * For test case use only
     * @return backAction
     */
    public LayerAction getBackAction() {
        return backAction;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Returns checkbox tree viewer of the LegendView 
     * @return viewer
     */
    public CheckboxTreeViewer getViewer() {
        return viewer;
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {
        
        //Init listerner for the changes/actions on Map Editor 
        getSite().getWorkbenchWindow().getPartService().addPartListener(partServiceListener);

        initViewer(parent);
        initContextMenu(viewer);
        initToobarActions();
        setGlobalActions();

        UDIGDragDropUtilities.addDragDropSupport(viewer, this);

    }
    
    protected void disposeInternal() {
        
        if (PlatformUI.getWorkbench().isClosing()) {
            ProjectPlugin.getPlugin().turnOffEvents();
        }

        removeViewerListeners();
        removeCurrentMapListeners();

        gridHandler.disposeHandler();
        filtersHandler.disposeHandler();
        
        getSite().getWorkbenchWindow().getPartService().removePartListener(partServiceListener);
        
    }
    
    /**
     * Initialises the viewer with necessary properties and listeners. Called on creation of view.
     * @param parent
     */
    private void initViewer(Composite parent) {
        
        //Init viewer object
        viewer = new CheckboxTreeViewer(parent, SWT.SINGLE);
        
        //Set content provider settings
        contentProvider = new LegendViewContentProvider(this);
        viewer.setContentProvider(contentProvider);

        //Set label provider settings
        labelProvider = new AdapterFactoryLabelProviderDecorator(ProjectExplorer
                .getProjectExplorer().getAdapterFactory(), viewer);
        
        // In dispose() method we need to remove this listener manually!
        if (labelProviderListener == null) {
            labelProviderListener = new LabelProviderListerner();
        }
        labelProvider.addListener(labelProviderListener);
        viewer.setLabelProvider(labelProvider);

        // Listens to check/uncheck of tree items
        if (checkStateListener == null) {
            checkStateListener = new CheckStateListener();
        }
        viewer.addCheckStateListener(checkStateListener);
        // Listens to expand/collapse of tree items
        if (collapeExpandListener == null) {
            collapeExpandListener = new CollapseExpandListener();
        }
        viewer.addTreeListener(collapeExpandListener);
        //Listens to selection changes of tree items 
        viewer.addSelectionChangedListener(this);
        
        //Set filter settings
        viewer.setFilters(this.filtersHandler.getFilters());
        
        // We need to set the selection provider before creating the global actions
        // (so ToolManager can hook us up to the global actions like properties and delete)
        getViewSite().setSelectionProvider(viewer);

        //Initialises the current map on creation of the view
        initMapFromEditor();
        
    }
    
    /**
     * Cleans up the viewer of listeners.
     */
    private void removeViewerListeners() {
        
        labelProvider.removeListener(labelProviderListener);
        labelProviderListener = null;
        labelProvider.dispose();
        labelProvider = null;
        
        viewer.removeCheckStateListener(checkStateListener);
        checkStateListener = null;
        viewer.removeTreeListener(collapeExpandListener);
        collapeExpandListener = null;
        viewer.removeSelectionChangedListener(this);
        
    }
    
    /**
     * Initialises the current map on creation of the view
     */
    private void initMapFromEditor() {
        IEditorPart activeEditor = getSite().getPage().getActiveEditor();
        if (activeEditor != null && activeEditor instanceof IAdaptable) {
            Object mapObj = ((IAdaptable) activeEditor).getAdapter(Map.class);
            if (mapObj != null) {
                setCurrentMap((Map) mapObj);
            }
        }
    }
    
    /**
     * Creates a context menu
     * 
     * @param targetViewer
     */
    private void initContextMenu( final Viewer targetViewer ) {

        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {
                
                if (canAddFolder()) {
                    contextMenu.add(newFolderAction());    
                }
                
                if (LegendViewUtils.isFolderSelected(viewer.getSelection())) {
                    contextMenu.add(renameFolderAction());    
                }
                
                if (canDelete()) {
                    contextMenu.add(new Separator());
                    contextMenu.add(getDeleteAction());    
                }
                
            }
            
        });

        // Create menu against viewer's control
        final Control control = targetViewer.getControl();
        final Menu menu = contextMenu.createContextMenu(control);
        control.setMenu(menu);

        // Register menu for extension
        getSite().registerContextMenu(contextMenu, targetViewer);

    }
    
    /**
     * Checks if the current state of the view allows adding a folder.
     * 
     * @return true if allowed, otherwise false
     */
    private boolean canAddFolder() {
        return newFolderAction.isEnabled();
    }
    
    /**
     * Checks if the current selection allows deletion.
     * 
     * @return true if allowed, otherwise false
     */
    private boolean canDelete() {
        final ISelection selection = viewer.getSelection(); 
        if (!selection.isEmpty() && selection instanceof StructuredSelection) {
            final StructuredSelection structSelection = (StructuredSelection) selection;
            final Object selectedObj = structSelection.getFirstElement(); 
            if (selectedObj instanceof LayerLegendItem) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets an adapted selection from the current selection. This method checks each object in the
     * selection if they can adapt to an object that can be deleted.
     * <p>
     * Example. if LayerLegendItem, return LayerLegendItem.getLayer()
     * 
     * @return selection to be deleted
     */
    private ISelection getDeleteSelection() {
        
        final ISelection selection = viewer.getSelection();
        final StructuredSelection strucSelection = (StructuredSelection) selection;
        
        final List<Object> adaptedObjs = new ArrayList<Object>();
        for (Object obj : strucSelection.toList()) {
            if (obj instanceof Folder) {
                adaptedObjs.add(obj);
            } else if (obj instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) obj;
                adaptedObjs.add(layerItem.getLayer());
            }    
        }
        
        final StructuredSelection adaptedSelection = new StructuredSelection(adaptedObjs);
        return adaptedSelection;
        
    }
    
    private IAction getDeleteAction() {
        
        if (deleteAction == null) {
            
            deleteAction = new Action(){
                @Override
                public void run() {
                    final ISelection selection = getDeleteSelection();
                    if (!selection.isEmpty()) {
                        final Delete delete = new Delete(false);
                        delete.selectionChanged(this, selection);
                        delete.run(this);
                    }
                }
            };
            
            deleteAction.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
            IWorkbenchAction actionTemplate = ActionFactory.DELETE.create(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow());
            deleteAction.setText(actionTemplate.getText());
            deleteAction.setToolTipText(actionTemplate.getToolTipText());
            deleteAction.setImageDescriptor(actionTemplate.getImageDescriptor());
            deleteAction.setDescription(actionTemplate.getDescription());
            deleteAction.setDisabledImageDescriptor(actionTemplate.getDisabledImageDescriptor());
            
        }
        
        return deleteAction;
        
    }
    
    private void initToobarActions() {
        
        final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        
        mgr.add(newFolderAction());
        // Just to initialise object, will be shown on context menu
        renameFolderAction();
        mgr.add(new Separator());
        
        mgr.add(moveFrontAction());
        mgr.add(upAction());
        mgr.add(downAction());
        mgr.add(moveBackAction());
        mgr.add(new Separator());
        
        //Added new functionalities for Legend View
        mgr.add(filtersHandler.getToggleMgAction());
        mgr.add(filtersHandler.getToggleBgAction());
        mgr.add(gridHandler.getGridAction());
        
        filtersHandler.setToggleLayersActionState();
        
    }

    /**
     * Create an action that moves a layer down in the rendering order
     */
    private LayerAction downAction() {
        downAction = new LayerAction(){
            public void run() {
                getCurrentMap().sendCommandASync(new LayerMoveDownCommand(structSelection));
            }
        };
        downAction.setEnabled(false);
        downAction.setToolTipText(Messages.LegendView_down_tooltip);
        downAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.DOWN_CO));
        return downAction;
    }

    /**
     * Create an action that moves a layer up in the rendering order
     */
    private LayerAction upAction() {
        upAction = new LayerAction(){
            /**
             * @see org.eclipse.jface.action.Action#run()
             */
            public void run() {
                getCurrentMap().sendCommandASync(new LayerMoveUpCommand(structSelection));
            }
        };
        upAction.setEnabled(false);
        upAction.setToolTipText(Messages.LegendView_up_tooltip);
        upAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.UP_CO));
        return upAction;
    }

    /**
     * Create an action that moves a layer down in the rendering order
     */
    private LayerAction moveFrontAction() {
        frontAction = new LayerAction(){
            public void run() {
                getCurrentMap().sendCommandASync(new LayerMoveFrontCommand(currentMap, structSelection));
            }
        };
        frontAction.setEnabled(false);
        frontAction.setToolTipText(Messages.LegendView_front_tooltip);
        frontAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.FRONT_CO));
        return frontAction;
    }

    /**
     * Create an action that moves a layer up in the rendering order
     */
    private LayerAction moveBackAction() {
        backAction = new LayerAction(){
            /**
             * @see org.eclipse.jface.action.Action#run()
             */
            public void run() {
                getCurrentMap().sendCommandASync(new LayerMoveBackCommand(currentMap, structSelection));
            }
        };
        backAction.setEnabled(false);
        backAction.setToolTipText(Messages.LegendView_back_tooltip);
        backAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.BACK_CO));
        return backAction;
    }
    
    /**
     * Create an action that creates a new folder
     */
    private IAction newFolderAction() {
        if (newFolderAction == null) {
            newFolderAction = new Action(){
                public void run() {
                    doNewFolderAction();
                }
            };
            newFolderAction.setText(Messages.LegendView_new_folder_action_lbl);
            newFolderAction.setToolTipText(Messages.LegendView_new_folder_tooltip);
            newFolderAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.NEW_FOLDER_CO));
            setNewFolderActionState();
        }
        return newFolderAction;
    }
    
    private void doNewFolderAction() {
        final Folder folder = ProjectFactory.eINSTANCE.createFolder();
        folder.setName(Messages.LegendView_new_folder_default_lbl);
        currentMap.sendCommandSync(new AddFolderItemCommand(folder));
        viewer.refresh();
    }
    
    private void setNewFolderActionState() {
        if (newFolderAction != null) { 
            if (this.currentMap == null) {
                newFolderAction.setEnabled(false);
            } else {
                newFolderAction.setEnabled(true);
            }            
        }
    }
    
    /**
     * Create an action that renames folder
     */
    private IAction renameFolderAction() {
        if (renameFolderAction == null) {
            renameFolderAction = new LegendViewRenameFolderAction(viewer);
        }
        return renameFolderAction;
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        disposeInternal();
        super.dispose();
    }

    /**
     * @return Returns the currentMap.
     */
    public synchronized Map getCurrentMap() {
        return currentMap;
    }
    
    /**
     * @param map The currentMap to set.
     */
    public synchronized void setCurrentMap( final Map map ) {

        // Remove map listeners
        removeCurrentMapListeners();

        // Set new current map
        this.currentMap = map;

        // Set current map as viewer's input
        if (viewer != null) {
            viewer.setInput(this.currentMap);
            gridHandler.setMap(this.currentMap);
            filtersHandler.setMap(this.currentMap);
            setNewFolderActionState();
        }

        if (this.currentMap != null) {

            // Add map listeners
            addCurrentMapListeners();

            // Set initial selection
            Object selectedLayer = this.currentMap.getEditManager().getSelectedLayer();
            if (selectedLayer != null && viewer != null) {
                viewer.setSelection(new StructuredSelection(selectedLayer));
            }

            // Set checkbox state
            LegendViewCheckboxUtils.updateCheckboxesAsync(this);
        }

    }

    /**
     * Initialises the current map's listeners
     */
    private void addCurrentMapListeners() {
        
        if (this.currentMap != null) {

            // Add edit manager listener
            if (editManagerListener == null) {
                editManagerListener = new EditManagerListener();
            }
            editManagerListener.setCurrentMap(this.currentMap);
            if (!(this.currentMap.getEditManager()).containsListener(editManagerListener)) {
                this.currentMap.getEditManager().addListener(editManagerListener);
            }
            
            // Add deep listener
            if (mapDeepListener == null) {
                mapDeepListener = new MapDeepListener();
            }
            this.currentMap.addDeepAdapter(mapDeepListener);
            addLegendListListener();
            
            // Add other listeners
            if (mylarListener == null) {
                mylarListener = new BlackboardListener();
            }
            this.currentMap.getBlackboard().addListener(mylarListener);
            
            if (zoomListener == null) {
                zoomListener = new ZoomListener();
            }
            currentMap.getViewportModel().addViewportModelListener(zoomListener);    
            
        }
                
    }
    
    private void addLegendListListener() {
        if (mapDeepListener != null) {
            EObjectContainmentEList<ILegendItem> legendEObject = (EObjectContainmentEList<ILegendItem>) currentMap
                    .getLegend();
            legendEObject.getEObject().eAdapters().add(mapDeepListener);
        }
    }
    
    /**
     * Removes the current map's listeners
     */
    private void removeCurrentMapListeners() {
        
        if (this.currentMap != null) {
        
            // Remove edit manager listener
            IEditManager editManager = this.currentMap.getEditManager();
            if (editManager.containsListener(editManagerListener)) {
                editManager.removeListener(editManagerListener);
                editManagerListener = null;
            }

            // Remove deep listener
            this.currentMap.removeDeepAdapter(mapDeepListener);
            removeLegendListListener();
            mapDeepListener = null;

            // Remove other listeners
            this.currentMap.getBlackboard().removeListener(mylarListener);
            mylarListener = null;
            
            currentMap.getViewportModel().removeViewportModelListener(zoomListener);
            zoomListener = null;
            
        }
        
    }
    
    private void removeLegendListListener() {
        if (mapDeepListener != null) {
            EObjectContainmentEList<ILegendItem> legendEObject = (EObjectContainmentEList<ILegendItem>) currentMap
                    .getLegend();
            legendEObject.getEObject().eAdapters().remove(mapDeepListener);
        }
    }
    
    /**
     * We use this method to contribute some global actions from the ToolManager and hook up a
     * custom delete action that is willing to delete a layer.
     */
    private void setGlobalActions() {
        IActionBars actionBars = getViewSite().getActionBars();

        IToolManager toolManager = ApplicationGIS.getToolManager();
        toolManager.contributeGlobalActions(this, actionBars);
        toolManager.registerActionsWithPart(this);

        IKeyBindingService keyBindings = getSite().getKeyBindingService();
        IAction delAction = getDeleteAction();
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delAction);
        keyBindings.registerAction(delAction);
    }

    /**
     * This is how the framework determines which interfaces we implement. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param key The desired class
     * @return An object of type key or null;
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getAdapter( Class key ) {
        if (key.equals(IPropertySheetPage.class)) {
            return ProjectUIPlugin.getDefault().getPropertySheetPage();
        }
        if (key.isAssignableFrom(IMap.class)) {
            return getCurrentMap();
        }
        return super.getAdapter(key);
    }
    
    /**
     * Method implemented from IDropTargetProvider.
     */
    @Override
    public Object getTarget( DropTargetEvent event ) {
        // Add additional processing if needed
        return null;
    }

    /**
     * Function sets the status message on the bottom left of the application relative to the status
     * of the currently selected item on the view. 
     */
    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        
        final ISelection selection = event.getSelection();
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            
            final IStructuredSelection structSelection = (IStructuredSelection) selection;
            final Object obj = structSelection.getFirstElement();
            final IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
            
            if (obj instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = ((LayerLegendItem) obj);
                final Layer layer = layerItem.getLayer();
                if (layer.getStatus() == ILayer.ERROR) {
                    statusLineManager.setErrorMessage(layer.getStatusMessage());
                } else {
                    statusLineManager.setErrorMessage(null);
                    statusLineManager.setMessage(layer.getStatusMessage());
                }
            } else {
                statusLineManager.setMessage(null);
                statusLineManager.setErrorMessage(null);
            }
            
        }
        
    }
    
    
    /**
     * Listens to viewport model events such as zoom events, etc.
     */
    private class ZoomListener implements IViewportModelListener {
        public void changed(ViewportModelEvent event) {
            viewer.getControl().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    viewer.refresh();
                }
            });
        }
    }
    
    /**
     * Listens to changes on layer selection of the current map. Updates the viewer when necessary
     * to reflect the changes. Ex. A new layer is selected.
     */
    private class EditManagerListener implements IEditManagerListener {
        
        private Map map;
        private synchronized void setCurrentMap( final Map currentMap ) {
            this.map = currentMap;
        }

        public void changed( final EditManagerEvent event ) {
            
            // Just in case
            if (getCurrentMap() != this.map) {
                this.map.getEditManager().removeListener(this);
                return;
            }
            
            if (event.getType() == EditManagerEvent.SELECTED_LAYER) {

                final Runnable runnable = new Runnable(){
                    public void run() {
                        final Object eventObj = event.getNewValue(); 
                        final Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement(); 
                        if (selection != eventObj) {
                            final StructuredSelection structSelection = new StructuredSelection(eventObj);
                            getSite().getSelectionProvider().setSelection(structSelection);
                        }
                        if (mylarOn()) {
                            viewer.refresh();
                        }
                    }

                    private boolean mylarOn() {
                        final Object mylarValue = map.getBlackboard().get(MylarAction.KEY);
                        if (mylarValue instanceof Boolean) {
                            return ((Boolean) mylarValue).booleanValue();
                        }
                        return false;
                    }
                };
                
                if (Display.getCurrent() == null) {
                    Display.getDefault().asyncExec(runnable);
                } else {
                    runnable.run();
                }
                    
            }
        }

    }
    
    /**
     * The abstract class for the sorting actions. This also acts as a selection listener to manage
     * enabling/disabling of the buttons.
     */
    private abstract class LayerAction extends Action implements ISelectionChangedListener {

        protected IStructuredSelection structSelection;

        public LayerAction() {
            viewer.addSelectionChangedListener(this);
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            
            final ISelection selection = event.getSelection();
            if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
                structSelection = (IStructuredSelection) selection;
                setEnabled(isValidSelection(structSelection));
            }
            
        }

        private boolean isValidSelection(IStructuredSelection selection) {
            if (selection.size() == 1) {
                if (selection.getFirstElement() instanceof LayerLegendItem) {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    /**
     * Seems we don't really need that LayersView listens selection changing. To display layers
     * we need to listen only activating of MapEditor. Also it solves some problems and bugs with
     * listeners hell during LayersView closing and opening multiple times.
     * 
     * @author Vitalus
     */
    private class MapEditorListener implements IPartListener {

        /*
         * Basically: If an editor is activated, then we ask it if it can turn into a map. If the
         * selection changes, we ask if the selection can turn into a map. If it can't, we ask the
         * editor part again. If the editor part cannot serve up a map, blank out the layers view.
         * (This way editor parts with only one map will still display the layers if something else
         * is selected. Comment by Vitalus: do we really need?
         */

        private IWorkbenchPart currentPart;

        /**
         * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partActivated( IWorkbenchPart part ) {
            // If newly activated part is different map, set current map to refresh view
            if (part != currentPart) {
                if (part instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) part;
                    Object obj = adaptable.getAdapter(Map.class);
                    if (obj instanceof Map) {
                        currentPart = part;
                        setCurrentMap(((Map) obj));
                    }
                }
            }
        }
        
        /**
         * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
         */
        public void partBroughtToTop( IWorkbenchPart part ) {
            partActivated(part);
        }

        /**
         * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
         */
        public void partClosed( IWorkbenchPart part ) {
            // [Naz] This seems to be wrong.
            if (part == this) { 
                disposeInternal();
                return;
            }
            //If closed part is current map, set current map to null and refresh viewer
            if (part == currentPart) {
                currentPart = null;
                setCurrentMap(null);
                viewer.refresh(true);                
            }
        }

        /**
         * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partDeactivated( IWorkbenchPart part ) {
            //Nothing
        }

        /**
         * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
         */
        public void partOpened( IWorkbenchPart part ) {
            //Nothing
        }

    }
    
    /**
     * Listens to changes in the blackboard and sets/refreshes the viewer as necessary.
     */
    private class BlackboardListener implements IBlackboardListener {

        @Override
        public void blackBoardChanged( BlackboardEvent event ) {
            if (event.getKey() == MylarAction.KEY && event.getOldValue() != event.getNewValue()) {
                updateViewer();
            }
        }

        @Override
        public void blackBoardCleared( IBlackboard source ) {
            updateViewer();
        }
        
        /**
         * Updates the viewer due to blackboard changes. See IBlackboardListener mylarListener variable.
         */
        private void updateViewer() {
            final Runnable runnable = new Runnable(){
                public void run() {
                    viewer.refresh();
                }
            };
            if (Display.getCurrent() == null) {
                Display.getDefault().asyncExec(runnable);
            } else {
                runnable.run();
            }
        }
        
    }
    
    /**
     * This listener listens to label provider changes and refreshes the viewer as necessary to
     * reflect the changes.
     */
    private class LabelProviderListerner implements ILabelProviderListener {

        @Override
        public void labelProviderChanged( LabelProviderChangedEvent event ) {

            // Synchronize map variable to view's map attribute
            final Map currentMap;
            synchronized (this) {
                currentMap = LegendView.this.currentMap;
            }

            // Refresh the viewer to reflect any change in the labels
            if (currentMap != null) {
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        if (!PlatformUI.getWorkbench().isClosing()) {
                            if (viewer != null) {
                                viewer.refresh(true);
                            }
                        }
                    }
                });
            }

        }

    }
    
    /**
     * This listener is designed to be added as a deep adapter to map and listens to changes both to
     * the map and its contained layers and updates the checkboxes as necessary to reflect changes.
     */
    private class MapDeepListener extends AdapterImpl {
        
        public void notifyChanged( final Notification msg ) {
            
            // Skip processing if workbench is closing
            if (PlatformUI.getWorkbench().isClosing()) {
                return;
            }
            
            final Object notifier = msg.getNotifier();
            if (notifier instanceof Map) {
                
                if (ProjectPackage.MAP__LEGEND == msg.getFeatureID(Map.class)) {
                    switch( msg.getEventType() ) {
                    case Notification.ADD: {
                        addListeners(msg);
                        refreshOnAddAndRemove(msg);
                        break;
                    }
                    case Notification.REMOVE: {
                        removeListeners(msg);
                        refreshOnAddAndRemove(msg);
                        break;
                    }
                    }
                }
                
            } else if (notifier instanceof Folder) {
                
                if (ProjectPackage.FOLDER__ITEMS == msg.getFeatureID(Folder.class)) {
                    switch( msg.getEventType() ) {
                    case Notification.ADD: {
                        addListeners(msg);
                        refreshOnAddAndRemove(msg);
                        break;
                    }
                    case Notification.REMOVE: {
                        removeListeners(msg);
                        refreshOnAddAndRemove(msg);
                        break;
                    }
                    }
                }

            } else if (notifier instanceof Layer) {
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__VISIBLE) {
                    // When layer's visibility is changed
                    if (msg.getOldBooleanValue() != msg.getNewBooleanValue()) {
                        LegendViewCheckboxUtils.updateCheckboxesAsync(LegendView.this);
                    }

                } else if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__INTERACTION_MAP) {
                    // When layer's interaction property is changed - note that the event does not
                    // fire properly maybe a bug in the properties page?
                    refreshViewer();
                }
            }
            
        }
        
        /**
         * Adds needed listeners to the event object.
         * 
         * @param msg
         */
        private void addListeners(Notification msg) {
            if (msg.getNewValue() instanceof Folder) {
                final Folder folder = (Folder) msg.getNewValue();
                folder.eAdapters().add(mapDeepListener);
            }
        }

        /**
         * Removes needed listeners to the event object.
         * 
         * @param msg
         */
        private void removeListeners(Notification msg) {
            if (msg.getOldValue() instanceof Folder) {
                final Folder folder = (Folder) msg.getOldValue();
                folder.eAdapters().remove(mapDeepListener);
            }
        }
        
        /**
         * Refresh the handlers and ui elements when a legend item is added/removed.
         * 
         * @param msg
         */
        private void refreshOnAddAndRemove(Notification msg) {
            
            // Refresh handlers' variables
            filtersHandler.refresh();
            gridHandler.refresh(msg);
            
            // Refresh viewer
            final Object notifier = msg.getNotifier();
            if (notifier instanceof Folder) {
                final Folder folder = (Folder) msg.getNotifier(); 
                refreshViewer(folder);
            } else if (notifier instanceof Map) {
                refreshViewer();
            }
            
        }

        /**
         * Refreshes the viewer.
         */
        private void refreshViewer() {
            refreshViewer(null);
        }
        
        /**
         * Refreshes the viewer and sets the expanded state of the folder.
         * 
         * @param folder
         */
        private void refreshViewer(final Folder folder) {
            final Runnable run = new Runnable(){
                @Override
                public void run() {
                    viewer.refresh();
                    LegendViewCheckboxUtils.updateCheckboxes(LegendView.this);
                }
            };
            if (Display.getCurrent() == null) {
                Display.getDefault().asyncExec(run);
            } else {
                run.run();
            }
        }
    }
    
    /**
     * Inner-class listens to the expansion/collapsing of tree items
     * 
     */
    private class CollapseExpandListener implements ITreeViewerListener {

        @Override
        public void treeCollapsed( TreeExpansionEvent event ) {
            //Do nothing
        }

        @Override
        public void treeExpanded( TreeExpansionEvent event ) {
            LegendViewCheckboxUtils.updateCheckboxesAsync(LegendView.this);
        }
        
    }
   
    /**
     * Inner-class listens to the changes on the check state of tree items
     */
    private class CheckStateListener implements ICheckStateListener {

        @Override
        public void checkStateChanged( CheckStateChangedEvent event ) {
            final Object eventObj = event.getElement();
            if (eventObj instanceof ILegendItem) {
                final ILegendItem item = (ILegendItem) eventObj;
                final boolean isChecked = event.getChecked();
                setVisibilityState(item, isChecked);
                LegendViewCheckboxUtils.updateCheckboxes(LegendView.this);
            }
        }
        
        private void setVisibilityState(ILegendItem item, boolean isChecked) {
            if (item instanceof Folder) {
                final Folder folder = (Folder) item;
                setVisibilityState(folder, isChecked);
            } else if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                setVisibilityState(layerItem, isChecked);
            }
        }
        
        private void setVisibilityState(Folder folder, boolean isChecked) {
            for (ILegendItem item : folder.getItems()) {
                setVisibilityState(item, isChecked);
            }
            viewer.setGrayed(folder, false);
            viewer.setChecked(folder, isChecked);
        }

        private void setVisibilityState(LayerLegendItem layerItem, boolean isChecked) {
            final Layer layer = layerItem.getLayer();
            if (layer.isVisible() != isChecked) {
                layer.setVisible(isChecked);                
            }
        }
        
    }
    
}


