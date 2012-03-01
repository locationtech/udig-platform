/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import java.util.Iterator;
import java.util.List;

import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILegendItem;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.map.LayerMoveBackCommand;
import net.refractions.udig.project.command.map.LayerMoveDownCommand;
import net.refractions.udig.project.command.map.LayerMoveFrontCommand;
import net.refractions.udig.project.command.map.LayerMoveUpCommand;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.AddFolderItemCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.ui.AdapterFactoryLabelProviderDecorator;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.Delete;
import net.refractions.udig.project.ui.internal.actions.MylarAction;
import net.refractions.udig.project.ui.internal.actions.RenameFolderAction;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.ZoomingDialog;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
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
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * The Legend View. This view allows the user to group layers together and set them into different
 * categories. The view also provides facilities to show/hide map graphics and background layers.
 * Also, additional layer sorting functionalities (similar to power-point sorting) are implemented.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendView extends ViewPart implements IDropTargetProvider, ISelectionChangedListener {
    
    public static final String ID = "net.refractions.udig.project.ui.legendManager"; //$NON-NLS-1$
    
    private Map currentMap;
    
    private LegendViewGridHandler gridHandler = new LegendViewGridHandler();
    private LegendViewFiltersHandler filtersHandler = new LegendViewFiltersHandler(this);
    
    private LayerAction downAction;
    private LayerAction upAction;    
    private LayerAction frontAction;
    private LayerAction backAction;
    
    private IAction newFolderAction;
    private RenameFolderAction renameFolderAction;
    
    private CheckboxTreeViewer viewer;
    private LegendViewContentProvider contentProvider;
    private AdapterFactoryLabelProviderDecorator labelProvider;
    private ILabelProviderListener labelProviderListener = new LabelProviderListerner();
    private CheckStateListener checkStateListener = new CheckStateListener();
    private CollapseExpandListener collapeExpandListener = new CollapseExpandListener();
    
    private Action propertiesAction;
    private IAction deleteAction;
    
    //Listens to changes to selected views/editors
    private MapEditorListener partServiceListener = new MapEditorListener();
    //Listens to changes to current map edit manager
    private EditManagerListener editManagerListener;
    private IBlackboardListener mylarListener = new BlackboardListener();
    private IViewportModelListener zoomListener = new ViewportModelListener();
    private Adapter mapDeepListener = new MapDeepListener();
    
    
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
        viewer = new CheckboxTreeViewer(parent, SWT.MULTI);
        
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

                contextMenu.add(newFolderAction());
                if (LegendViewUtils.isFolderSelected(viewer.getSelection())) {
                    contextMenu.add(renameFolderAction());    
                }
                contextMenu.add(new Separator());
                
                contextMenu.add(ApplicationGIS.getToolManager().getCOPYAction(LegendView.this));
                contextMenu.add(ApplicationGIS.getToolManager().getPASTEAction(LegendView.this));
                contextMenu.add(getDeleteAction());
                contextMenu.add(new Separator());
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(
                        viewer.getSelection()));
                contextMenu.add(new Separator());
                contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
                contextMenu.add(new Separator());

                if (viewer.getTree().getSelectionCount() == 1) {
                    contextMenu.add(getPropertiesAction());
                }

            }

            private LayerApplicabilityMenuCreator applicabilityCreator;

            private LayerApplicabilityMenuCreator getApplicabilityMenu() {
                if (applicabilityCreator == null) {
                    applicabilityCreator = new LayerApplicabilityMenuCreator();
                }

                IStructuredSelection selection = (IStructuredSelection) targetViewer.getSelection();
                for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                    Object element = iter.next();
                    if (!(element instanceof Layer)) {
                        return null;
                    }
                }

                return applicabilityCreator;
            }

        });

        // Create menu
        final Menu menu = contextMenu.createContextMenu(targetViewer.getControl());
        targetViewer.getControl().setMenu(menu);

        // Register menu for extension
        getSite().registerContextMenu(contextMenu, targetViewer);

    }

    private IAction getDeleteAction() {
        
        if (deleteAction == null) {
            
            deleteAction = new Action(){
                @Override
                public void run() {
                    Delete delete = new Delete(false);
                    ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getSelectionService().getSelection();
                    delete.selectionChanged(this, selection);
                    delete.run(this);
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

    protected IAction getPropertiesAction() {
        
        if (propertiesAction == null) {
            
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final PropertyDialogAction propDialogAction = new PropertyDialogAction(new SameShellProvider(shell),
                    viewer);

            propertiesAction = new Action(){
                @Override
                public void runWithEvent( Event event ) {
                    ZoomingDialog dialog = new ZoomingDialog(shell, propDialogAction.createDialog(),
                            ZoomingDialog.calculateBounds(viewer.getTree().getSelection()[0], -1));
                    dialog.open();
                }
            };

            propertiesAction.setText(propDialogAction.getText());
            propertiesAction.setActionDefinitionId(propDialogAction.getActionDefinitionId());
            propertiesAction.setDescription(propDialogAction.getDescription());
            propertiesAction.setHoverImageDescriptor(propDialogAction.getHoverImageDescriptor());
            propertiesAction.setImageDescriptor(propDialogAction.getImageDescriptor());
            propertiesAction.setToolTipText(propDialogAction.getToolTipText());

        }
        
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
                propertiesAction);
        
        return propertiesAction;
        
    }
    
    private void initToobarActions() {
        
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        
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
                if (isSelectionAllOrNothing(selection)) return;
                getCurrentMap().sendCommandASync(new LayerMoveDownCommand(selection));
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
                if (isSelectionAllOrNothing(selection)) return;
                getCurrentMap().sendCommandASync(new LayerMoveUpCommand(selection));
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
                if (isSelectionAllOrNothing(selection)) return;
                getCurrentMap().sendCommandASync(new LayerMoveFrontCommand(currentMap, selection));
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
                if (isSelectionAllOrNothing(selection)) return;
                getCurrentMap().sendCommandASync(new LayerMoveBackCommand(currentMap, selection));
            }
        };
        backAction.setEnabled(false);
        backAction.setToolTipText(Messages.LegendView_back_tooltip);
        backAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.BACK_CO));
        return backAction;
    }
    
    private boolean isSelectionAllOrNothing(IStructuredSelection selection) {
      //TODO - Update to get LegendItems list
        /*
        if (selection.isEmpty() || selection.size() == currentMap.getMapLayers().size()) {
            return true;
        }
        */
        return false;
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
            newFolderAction.setText("Add folder");
            newFolderAction.setToolTipText(Messages.LegendView_new_folder_tooltip);
            newFolderAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.NEW_FOLDER_CO));
            setNewFolderActionState();
        }
        return newFolderAction;
    }
    
    private void doNewFolderAction() {
        final Folder folder = ProjectFactory.eINSTANCE.createFolder();
        folder.setName("New Folder");
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
            renameFolderAction = new RenameFolderAction(viewer);
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
            LegendViewCheckboxUtils.updateCheckboxes(this);
        }

    }

    /**
     * Initialises the current map's listeners
     */
    private void addCurrentMapListeners() {
        
        if (this.currentMap != null) {

            if (editManagerListener == null) {
                editManagerListener = new EditManagerListener();
            }
            editManagerListener.setCurrentMap(this.currentMap);
            if (!(this.currentMap.getEditManager()).containsListener(editManagerListener)) {
                this.currentMap.getEditManager().addListener(editManagerListener);
            }
            
            if (mapDeepListener == null) {
                mapDeepListener = new MapDeepListener();
            }
            this.currentMap.addDeepAdapter(mapDeepListener);
            addLegendItemsDeepListeners();
            
            if (mylarListener == null) {
                mylarListener = new BlackboardListener();
            }
            this.currentMap.getBlackboard().addListener(mylarListener);
            
            if (zoomListener == null) {
                zoomListener = new ViewportModelListener();
            }
            this.currentMap.getViewportModel().addViewportModelListener(zoomListener);
            
        }
                
    }
    
    /**
     * Removes the current map's listeners
     */
    private void removeCurrentMapListeners() {
        
        if (this.currentMap != null) {
        
            //Remove edit manager listener
            IEditManager editManager = this.currentMap.getEditManager();
            if (editManager.containsListener(editManagerListener)) {
                editManager.removeListener(editManagerListener);
                editManagerListener = null;
            }
            
            //Remove deep listener
            this.currentMap.removeDeepAdapter(mapDeepListener);
            removeLegendItemsDeepListeners();
            mapDeepListener = null;
            
            //Remove other listeners
            this.currentMap.getBlackboard().removeListener(mylarListener);
            mylarListener = null;
            this.currentMap.getViewportModel().removeViewportModelListener(zoomListener);
            zoomListener = null;
            
        }
        
    }
    
    /**
     * Adds the deepAdapter to the LegendItems list and to the contents of the list
     */
    private void addLegendItemsDeepListeners() {
        
        //Add to list
        ((EObjectContainmentEList<ILegendItem>) this.currentMap.getLegend()).getEObject()
                .eAdapters().add(mapDeepListener);
        
        //Add to list children
        for( ILegendItem legendItem : this.currentMap.getLegend() ) {
            if (legendItem instanceof Folder) {
                final Folder folder = (Folder) legendItem;
                for( ILegendItem folderItem : folder.getItems() ) {
                    final Layer layer = (Layer) folderItem;
                    layer.eAdapters().add(mapDeepListener);
                }
                folder.eAdapters().add(mapDeepListener);
            } else if (legendItem instanceof Layer) {
                final Layer layer = (Layer) legendItem;
                layer.eAdapters().add(mapDeepListener);
            }
        }
    }
    
    /**
     * Removes the deepAdapter from the LegendItems list and from the contents of the list
     */
    private void removeLegendItemsDeepListeners() {
        
        //Remove from list
        ((EObjectContainmentEList<ILegendItem>) this.currentMap.getLegend()).getEObject()
                .eAdapters().remove(mapDeepListener);
        
        //Remove from list children
        for( ILegendItem legendItem : currentMap.getLegend() ) {
            if (legendItem instanceof Folder) {
                final Folder folder = (Folder) legendItem;
                for( ILegendItem folderItem : folder.getItems() ) {
                    final Layer layer = (Layer) folderItem;
                    layer.eAdapters().remove(mapDeepListener);
                }
                folder.eAdapters().remove(mapDeepListener);
            } else if (legendItem instanceof Layer) {
                final Layer layer = (Layer) legendItem;
                layer.eAdapters().remove(mapDeepListener);
            }
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
     * Method implemented from IDropTargetProvider
     */
    @Override
    public Object getTarget( DropTargetEvent event ) {
        
        if (getCurrentMap() == null) {
            return this;
        }
        
        List<ILayer> mapLayers = getCurrentMap().getMapLayers();
        if (mapLayers.isEmpty()) {
            return this;
        }

        return mapLayers.get(mapLayers.size() - 1);
        
        /* TODO - Implement when rendering has been migrated
        final List<ILegendItem> legendItems = getCurrentMap().getLegend();
        if (legendItems.isEmpty()) {
            return this;
        }

        return legendItems.get(legendItems.size() - 1);
        */
    }

    /**
     * Function sets the status message on the bottom left of the application relative to the status
     * of the currently selected item on the view. Method implemented from
     * ISelectionChangedListener.
     */
    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        
        gridHandler.getClass();
        
        if (event.getSelection().isEmpty()) {
            return;
        }

        if (!(event.getSelection() instanceof IStructuredSelection)) { 
            return;
        }
        
        IStructuredSelection structured = (IStructuredSelection) event.getSelection();
        Object firstElement = structured.getFirstElement();
        
        IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
        
        if (firstElement instanceof ILayer) {
            
            ILayer layer = ((ILayer) firstElement);
            
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
                        final Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement(); 
                        if (selection != event.getNewValue()) {
                            StructuredSelection structuredSelection = new StructuredSelection(event.getNewValue());
                            // Note: Selection provider is the CheckboxTreeViewer
                            //TODO - Update to get LegendItems list
                            //getSite().getSelectionProvider().setSelection(structuredSelection);
                        }
                        if (mylarOn()) {
                            //TODO - Update to get LegendItems list
                            //viewer.update(map.getLayersInternal().toArray(), null);
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
    private abstract class LayerAction extends Action implements ISelectionListener {

        protected IStructuredSelection selection;

        /**
         * Construct <code>LayerAction</code>.
         */
        public LayerAction() {
            getSite().getPage().addSelectionListener(this);
        }

        /**
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
         *      org.eclipse.jface.viewers.ISelection)
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
            if (!(selection instanceof IStructuredSelection)) return;
            this.selection = (IStructuredSelection) selection;
            if (part instanceof LegendView && selection != null && !selection.isEmpty()) {
                setEnabled(true);
            }
        }

        /**
         * @see org.eclipse.jface.action.Action#setEnabled(boolean)
         */
        @SuppressWarnings("unchecked")
        public void setEnabled( boolean enabled ) {
            super.setEnabled(false);

            if (!enabled || selection == null || selection.isEmpty()) {
                return;
            }

            for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                final Object obj = iter.next();
                if (!(obj instanceof Layer)) {
                    return;
                }
            }
            
            super.setEnabled(true);
        }

    }
    
    /**
     * TODO Seems we don't really need that LayersView listens selection changing. To display layers
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
            //If newly activated part is different map, set current map to refresh view
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
                  //TODO - Update to get LegendItems list
                    /*
                    viewer.update(getCurrentMap().getMapLayers().toArray(),
                            new String[]{ MylarAction.KEY });
                            */
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
     * This listener is designed to act as 'zoom change listener' and listens to viewport model
     * changes and updates the viewer as necessary.
     */
    private class ViewportModelListener implements IViewportModelListener {

        @Override
        public void changed( ViewportModelEvent event ) {
            viewer.getControl().getDisplay().asyncExec(new Runnable(){
                public void run() {
                    //TODO - Update to get LegendItems list
                    //viewer.update(currentMap.getMapLayers().toArray(), null);
                }
            });
        }
        
    }
    
    /**
     * This listener is designed to be added as a deep adapter to map and listens to changes both to
     * the map and its contained layers and updates the checkboxes as necessary to reflect changes.
     */
    private class MapDeepListener extends AdapterImpl {
        
        public void notifyChanged( final Notification msg ) {
            
            //Skip processing if workbench is closing
            if (PlatformUI.getWorkbench().isClosing()) {
                return;
            }
            
            //Do processing
            if (msg.getNotifier() instanceof Map) {
                
                if (ProjectPackage.MAP__LEGEND == msg.getFeatureID(Map.class)) {
                    switch( msg.getEventType() ) {
                    case Notification.ADD: {
                        System.out.println("[LegendView] LegendItem - Add Event"); //$NON-NLS-1$
                        setAdapter(msg.getNewValue(), false);
                        filtersHandler.refresh();
                        gridHandler.refresh(msg.getEventType(), msg.getNewValue());
                        LegendViewCheckboxUtils.updateCheckboxes(LegendView.this);
                        refreshView();
                        break;
                    }
                    case Notification.REMOVE: {
                        System.out.println("[LegendView] LegendItem - Remove Event"); //$NON-NLS-1$
                        setAdapter(msg.getOldValue(), true);
                        filtersHandler.refresh();
                        gridHandler.refresh(msg.getEventType(), msg.getOldValue());
                        refreshView();
                        break;
                    }
                    }
                }
                
            } else if (msg.getNotifier() instanceof Folder) {
                
                if (ProjectPackage.FOLDER__ITEMS == msg.getFeatureID(Folder.class)) {
                    
                    final Folder folder = (Folder) msg.getNotifier();
                    
                    switch( msg.getEventType() ) {
                    case Notification.ADD: {
                        System.out.println("[LegendView] FolderItem - Add Event"); //$NON-NLS-1$
                        setAdapter(msg.getNewValue(), false);
                        filtersHandler.refresh();
                        gridHandler.refresh(msg.getEventType(), msg.getNewValue());
                        setExpandedState((Folder) msg.getNotifier());
                        LegendViewCheckboxUtils.updateCheckbox(LegendView.this, folder, true);
                        viewer.getTree();
                        refreshView();
                        break;
                    }
                    case Notification.REMOVE: {
                        System.out.println("[LegendView] LegendItem - Remove Event"); //$NON-NLS-1$
                        setAdapter(msg.getOldValue(), true);
                        filtersHandler.refresh();
                        gridHandler.refresh(msg.getEventType(), msg.getOldValue());
                        setExpandedState((Folder) msg.getNotifier());
                        LegendViewCheckboxUtils.updateCheckbox(LegendView.this, folder, false);
                        refreshView();
                        break;
                    }
                    }
                }

            } else if (msg.getNotifier() instanceof Layer) {
                // When layer is set to visible/invisible outside of UI
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__VISIBLE) {
                    if (msg.getOldBooleanValue() != msg.getNewBooleanValue()) {
                        final Layer layer = (Layer) msg.getNotifier();
                        LegendViewCheckboxUtils.updateCheckbox(LegendView.this, layer);
                    }
                } else if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__INTERACTION_MAP) {
                    //TODO - Update filters display when a layer's interaction property changes
                    System.out.println("LAYER__INTERACTION_MAP"); //$NON-NLS-1$
                }
                   
            }
            
        }

        private void setAdapter(Object object, boolean doRemove) {
            final EObject eObject = (EObject) object;
            if (doRemove) {
                if (eObject.eAdapters().contains(this)) {
                    eObject.eAdapters().remove(this);    
                }
            } else { 
                if (!eObject.eAdapters().contains(this)) {
                    eObject.eAdapters().add(this);    
                }
            }
        }
        
        private void setExpandedState(final Folder folder) {
            final Runnable run = new Runnable(){
                @Override
                public void run() {
                    doSetExpandedState(folder);
                }
            };
            if (Display.getCurrent() == null) {
                Display.getDefault().asyncExec(run);
            } else {
                run.run();
            }
        }
        
        private void doSetExpandedState(Folder folder) {
            if (folder.getItems().size() > 0) {
                if (!viewer.getExpandedState(folder)) {
                    viewer.setExpandedState(folder, true);    
                }
            } else {
                if (viewer.getExpandedState(folder)) {
                    viewer.setExpandedState(folder, false);    
                }
            }
        }
        
        private void refreshView() {
            final Runnable run = new Runnable(){
                @Override
                public void run() {
                    viewer.refresh();
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
            LegendViewCheckboxUtils.updateCheckbox(LegendView.this, (Folder) event.getElement(), true);
        }
        
    }
   
    /**
     * Inner-class listens to the changes on the check state of tree items
     */
    private class CheckStateListener implements ICheckStateListener {

        @Override
        public void checkStateChanged( CheckStateChangedEvent event ) {
            if (event.getElement() instanceof Folder) {
                checkFolder(event);
            } else if (event.getElement() instanceof Layer) {
                checkLayer(event);
            }
        }
        
        private void checkLayer(CheckStateChangedEvent event) {
            final Layer eventLayer = (Layer) event.getElement();
            if (eventLayer.isVisible() != event.getChecked()) {
                eventLayer.setVisible(event.getChecked());
                processParentFolder(eventLayer);
            }
        }
        
        private void checkFolder(CheckStateChangedEvent event) {
            final Folder eventFolder = (Folder) event.getElement();
            viewer.setGrayed(eventFolder, false);
            for( ILegendItem item : eventFolder.getItems() ) {
                final Layer layer = (Layer) item;
                if (layer.isVisible() != event.getChecked()) {
                    layer.setVisible(event.getChecked());
                    viewer.setChecked(layer, event.getChecked());    
                }
            }
        }
        
        private void processParentFolder(Layer layer) {
            final Object parent = LegendViewUtils.getParent(layer);
            if (parent instanceof Folder) {
                LegendViewCheckboxUtils.setFolderCheckbox(viewer, (Folder) parent);
            }
        }
        
    }
    
}


