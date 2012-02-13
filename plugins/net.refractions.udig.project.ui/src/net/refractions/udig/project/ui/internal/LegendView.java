/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.command.map.LayerMoveBackCommand;
import net.refractions.udig.project.command.map.LayerMoveDownCommand;
import net.refractions.udig.project.command.map.LayerMoveFrontCommand;
import net.refractions.udig.project.command.map.LayerMoveUpCommand;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.project.ui.AdapterFactoryLabelProviderDecorator;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.Delete;
import net.refractions.udig.project.ui.internal.actions.MylarAction;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.ZoomingDialog;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * TODO Purpose of
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * @author nchan
 * @since 1.2.0
 */
public class LegendView extends ViewPart
        implements
            IDropTargetProvider,
            IDoubleClickListener,
            ISelectionChangedListener {

    //TODO - Class variables start here
    
    public static final String ID = "net.refractions.udig.project.ui.legendManager"; //$NON-NLS-1$
    
    private static LegendView instance;
    
    private Map currentMap;
    private IMap emptyMap = ApplicationGIS.NO_MAP;
    
    private LegendViewGridHandler gridHandler = new LegendViewGridHandler();
    private LegendViewFiltersHandler filtersHandler = new LegendViewFiltersHandler(this);
    
    private LayerAction downAction;
    private LayerAction upAction;    
    private LayerAction frontAction;
    private LayerAction backAction;

    
    private CheckboxTreeViewer viewer;
    private AdapterFactoryContentProvider contentProvider;
    private AdapterFactoryLabelProviderDecorator labelProvider;
    private ILabelProviderListener labelProviderListener = new LabelProviderListerner();
    
    private Action propertiesAction;
    private IAction deleteAction;
    
    //Listens to changes to selected views/editors
    private MapEditorListener partServiceListener = new MapEditorListener();
    //Listens to changes to current map edit manager
    private EditManagerListener editManagerListener;
    private IBlackboardListener mylarListener = new BlackboardListener();
    private IViewportModelListener zoomListener = new ViewportModelListener();
    private Adapter checkboxContextListener = new CheckboxContextListener();
    
    //TODO - Methods start here
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {

        //Init listerner for the changes/actions on Map Editor 
        getSite().getWorkbenchWindow().getPartService().addPartListener(partServiceListener);

        initViewer(parent);
        createContextMenuFor(viewer);
        addToobarActions();
        setGlobalActions();

        UDIGDragDropUtilities.addDragDropSupport(viewer, this);

    }
    
    private void initViewer(Composite parent) {
        
        //Init viewer object
        viewer = new CheckboxTreeViewer(parent, SWT.MULTI);
        
        //Set content provider settings
        contentProvider = new AdapterFactoryContentProvider(ProjectUIPlugin.getDefault().getAdapterFactory()){
            @Override
            public void notifyChanged( Notification notification ) {
                super.notifyChanged(notification);

                switch( notification.getFeatureID(Map.class) ) {
                case ProjectPackage.MAP__CONTEXT_MODEL: {
                    if (notification.getNotifier() == getCurrentMap()) {
                        updateCheckboxes();
                    }
                    break;
                }
                }
            }
        };
        viewer.setContentProvider(contentProvider);

        //Set label provider settings
        labelProvider = new AdapterFactoryLabelProviderDecorator(ProjectExplorer
                .getProjectExplorer().getAdapterFactory(), viewer);
        // In dispose() method we need to remove this listener manually!
        labelProvider.addListener(labelProviderListener);
        viewer.setLabelProvider(labelProvider);
        
        //Initialises the current map on creation of the view
        IEditorPart activeEditor = getSite().getPage().getActiveEditor();
        if (activeEditor != null && activeEditor instanceof IAdaptable) {
            Object mapObj = ((IAdaptable) activeEditor).getAdapter(Map.class);
            if (mapObj != null) {
                setCurrentMap((Map) mapObj);
            }
        }

        //Set sorter settings
        viewer.setSorter(new ViewerLayerSorter());

        //Set filter settings
        viewer.setFilters(this.filtersHandler.getFilters());
        
        // sets the layer visibility to match the check box setting.
        viewer.addCheckStateListener(new ICheckStateListener(){
            public void checkStateChanged( CheckStateChangedEvent event ) {
                final Layer eventLayer = (Layer) event.getElement(); 
                if (eventLayer.isVisible() != event.getChecked()) {
                    eventLayer.setVisible(event.getChecked());
                }
            }
        });
        // We need to set the selection provider before creating the global actions
        // (so ToolManager can hook us up to the global actions like properties and delete)
        //
        getViewSite().setSelectionProvider(viewer);
        viewer.addSelectionChangedListener(this);
        viewer.addDoubleClickListener(this);
        
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        disposeInternal();
        super.dispose();
    }

    protected void disposeInternal() {
        
        if (PlatformUI.getWorkbench().isClosing()) {
            ProjectPlugin.getPlugin().turnOffEvents();
        }
        
        gridHandler.disposeHandler();
        filtersHandler.disposeHandler();
        
        if (currentMap != null) {
            currentMap.removeDeepAdapter(checkboxContextListener);

            IViewportModel viewportModel = currentMap.getViewportModel();
            if (viewportModel != null) {
                viewportModel.removeViewportModelListener(zoomListener);
            }
        }

        labelProvider.dispose();
        labelProvider.removeListener(labelProviderListener);
        labelProvider = null;
        labelProviderListener = null;

        getSite().getWorkbenchWindow().getPartService().removePartListener(partServiceListener);
    }

    /**
     * @return Returns the currentMap.
     */
    public synchronized Map getCurrentMap() {
        return currentMap;
    }
    
    /**
     * @param currentMap The currentMap to set.
     */
    @SuppressWarnings("unchecked")
    public synchronized void setCurrentMap( final Map currentMap ) {

        //Remove listeners from current map
        if (this.currentMap != null) {
            this.currentMap.removeDeepAdapter(checkboxContextListener);
            this.currentMap.getBlackboard().removeListener(mylarListener);
            this.currentMap.getViewportModel().removeViewportModelListener(zoomListener);
        }

        // Set new current map
        this.currentMap = currentMap;

        //Set current map as viewer's input
        if (viewer != null) {
            final Object mapObj = (currentMap == null) ? emptyMap : currentMap;
            viewer.setInput(mapObj);
        }
        
        //Init Edit Manager Listerner
        if (editManagerListener == null) {
            editManagerListener = new EditManagerListener();
        }

        //Init listeners to current map
        if (currentMap != null) {
            
            this.currentMap.getViewportModel().addViewportModelListener(zoomListener);
            this.currentMap.getBlackboard().addListener(mylarListener);
            currentMap.addDeepAdapter(checkboxContextListener);
            
            editManagerListener.setCurrentMap(currentMap);
            if (!(currentMap.getEditManager()).containsListener(editManagerListener)) {
                currentMap.getEditManager().addListener(editManagerListener);
            }
                
            Object selectedLayer = currentMap.getEditManager().getSelectedLayer();
            if (selectedLayer != null && viewer != null) {
                viewer.setSelection(new StructuredSelection(selectedLayer));
            }
            
            updateCheckboxes();
        }
        
        gridHandler.setMap(currentMap);
        filtersHandler.setMap(currentMap);
        
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

    //TODO - Viewer's checkboxes maintenance functions 

    /**
     * Updates the viewer's checkbox display of the layer parameter with respect to its visibility.
     * Method called from the notifications of checkboxContextListener
     * 
     * @param layer
     */
    private void updateCheckbox( final Layer layer ) {
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                if (!PlatformUI.getWorkbench().isClosing()) {
                    viewer.setChecked(layer, layer.isVisible());    
                }
            }
        }, true);
    }
    
    /**
     * Updates the viewer's checkbox display with respect to the current layer's visibility.
     * Method called from the notifications of checkboxContextListener
     */
    protected void updateCheckboxes() {
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {

                List<Layer> layers = currentMap.getLayersInternal();

                if (canUpdateCheckboxes(layers)) {
                
                    final List<Layer> checkedLayers = new ArrayList<Layer>();
                    for( Layer layer : layers ) {
                        if (layer.isVisible()) {
                            checkedLayers.add(layer);
                        }
                    }

                    if (viewer != null) {
                        viewer.setCheckedElements(checkedLayers.toArray());
                        final ILayer selectedLayer = currentMap.getEditManager().getSelectedLayer();
                        if (selectedLayer != null) {
                            viewer.setSelection(new StructuredSelection(selectedLayer), true);
                        }
                    }
                    
                }
                
            }

        }, true);
    }

    /**
     * Checks if the current status of workbench and current map allows checkbox update. Also calls
     * requiresCheckboxUpdate() to check current viewer display
     * 
     * @param layers
     * @return
     */
    private boolean canUpdateCheckboxes(List<Layer> layers) {
        
        if (PlatformUI.getWorkbench().isClosing()) {
            return false;
        }

        final Map currentMap;
        synchronized (this) {
            currentMap = LegendView.this.currentMap;
        }
        if (currentMap == null) {
            return false;
        }

        if (!requiresCheckboxUpdate(layers)) {
            return false;
        }
        
        return true;
        
    }
    
    /**
     * Checks if the current visibility of the layers are not in sync with the viewer's checkbox display.
     * @param layers
     * @return true - requires update, otherwise false
     */
    private boolean requiresCheckboxUpdate( List<Layer> layers ) {
        for( Layer layer : layers ) {
            if (!(layer.isVisible() == viewer.getChecked(layer))) {
                return true;
            }
        }
        return false;
    }

    //TODO - Toolbar actions creation/initialization methods
    
    private void addToobarActions() {
        
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        
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
                if (selection.isEmpty()) return;
                IMap map = getCurrentMap();
                // map.sendCommandSync( new LayerMoveDownCommand( selection ));
                map.sendCommandASync(new LayerMoveDownCommand(selection));
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
                if (selection.isEmpty()) return;
                IMap map = getCurrentMap();
                // map.sendCommandSync( new LayerMoveUpCommand( selection ));
                map.sendCommandASync(new LayerMoveUpCommand(selection));
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
                if (selection.isEmpty()) return;
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
                if (selection.isEmpty()) return;
                getCurrentMap().sendCommandASync(new LayerMoveBackCommand(currentMap, selection));
            }
        };
        backAction.setEnabled(false);
        backAction.setToolTipText(Messages.LegendView_back_tooltip);
        backAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.BACK_CO));
        return backAction;
    }
    
    //TODO - Context menu creation/initialization methods
    
    /**
     * Creates a context menu
     * 
     * @param targetViewer
     */
    private void createContextMenuFor( final Viewer targetViewer ) {

        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {

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
                    getViewer());

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

    //TODO - Singleton methods of view

    @Override
    public void init( IViewSite site ) throws PartInitException {
        super.init(site);
        instance = this;
    }
    
    public static LegendView getViewPart() {
        return instance;
    }
    
    public static Viewer getViewer() {
        
        LegendView viewPart = getViewPart();
        
        if (viewPart == null) {
            return null;
        }
        
        return viewPart.viewer;
        
    }
    
    //TODO - Methods from interfaces
    
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
    }
    

    /**
     * Method implemented from IDoubleClickListener
     */
    @Override
    public void doubleClick( DoubleClickEvent event ) {

        final Object obj = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        
        if (!(obj instanceof IProjectElement)) {
            
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    // For future when styling is in properties view
                    // getPropertiesAction().runWithEvent(new Event());
                }
            });
            
            return;
            
        }

    }

    /**
     * Method implemented from ISelectionChangedListener
     */
    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        
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

    
    //TODO - Internal classes
    
    /**
     * Listens to changes on layer selection of the current map. Updates the viewer when necessary.
     * Ex. A new layer is selected.
     * 
     * <p>
     * <ul>
     * <li></li>
     * </ul>
     * </p>
     * 
     * @author nchan
     * @since 1.2.0
     */
    private class EditManagerListener implements IEditManagerListener {
        
        private Map map;

        private synchronized void setCurrentMap( final Map currentMap ) {
            this.map = currentMap;
        }

        public void changed( final EditManagerEvent event ) {
            
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
                            //Selection provider is the CheckboxTreeViewer
                            getSite().getSelectionProvider().setSelection(structuredSelection);
                        }
                        if (mylarOn()) {
                            viewer.update(map.getLayersInternal().toArray(), null);
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
            
            //TODO - [nchan] Will this ever happen?
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
                    viewer.update(getCurrentMap().getMapLayers().toArray(),
                            new String[]{MylarAction.KEY});
                }
            };
            if (Display.getCurrent() == null) {
                Display.getDefault().asyncExec(runnable);
            } else {
                runnable.run();
            }
        }
        
    }
    
    private class LabelProviderListerner implements ILabelProviderListener {

        @Override
        public void labelProviderChanged( LabelProviderChangedEvent event ) {
            updateLabels();
        }
        
        /**
         * Refreshes the viewer's labels
         */
        private void updateLabels() {
            
            final Map currentMap;
            
            synchronized (this) {
                currentMap = LegendView.this.currentMap;
            }
            
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
    
    private class ViewportModelListener implements IViewportModelListener {

        @Override
        public void changed( ViewportModelEvent event ) {
            viewer.getControl().getDisplay().asyncExec(new Runnable(){
                public void run() {
                    viewer.update(currentMap.getMapLayers().toArray(), null);
                }
            });
        }
        
    }
    
    private class CheckboxContextListener extends AdapterImpl {
        
        @SuppressWarnings({"unchecked", "deprecation"})
        public void notifyChanged( final Notification msg ) {

            if (msg.getNotifier() instanceof ContextModel) {
                ContextModel contextModel = (ContextModel) msg.getNotifier();
                Map map = contextModel.getMap();

                if (getCurrentMap() != map) {
                    // Just in case
                    map.removeDeepAdapter(this);
                    return;
                }

                if (PlatformUI.getWorkbench().isClosing()) contextModel.eAdapters().remove(this);

                if (msg.getFeatureID(ContextModel.class) == ProjectPackage.CONTEXT_MODEL__LAYERS) {
                    switch( msg.getEventType() ) {
                    case Notification.ADD: {
                        Layer layer = (Layer) msg.getNewValue();
                        updateCheckbox(layer);
                        break;
                    }
                    case Notification.ADD_MANY: {
                        updateCheckboxes();
                        break;
                    }
                    case Notification.SET: {
                        Layer layer = (Layer) msg.getNewValue();
                        updateCheckbox(layer);
                        break;
                    }
                    }
                }
            } else if (msg.getNotifier() instanceof Layer) {
                Layer layer = (Layer) msg.getNotifier();
                if (getCurrentMap() != layer.getMapInternal()) {
                    // Just in case
                    layer.getMapInternal().removeDeepAdapter(this);
                    return;
                }
                if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__VISIBLE)
                    if (msg.getNewBooleanValue() != msg.getOldBooleanValue()) {
                        if (Display.getCurrent() == null) {
                            viewer.getControl().getDisplay().asyncExec(new Runnable(){
                                public void run() {
                                    viewer.setChecked(msg.getNotifier(), msg.getNewBooleanValue());
                                }
                            });
                        } else {
                            viewer.setChecked(msg.getNotifier(), msg.getNewBooleanValue());
                        }
                    }
            }
        }
        
    }
   
}
