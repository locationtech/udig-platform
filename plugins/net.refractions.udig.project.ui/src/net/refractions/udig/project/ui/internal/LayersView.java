/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
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
import net.refractions.udig.project.command.map.LayerMoveDownCommand;
import net.refractions.udig.project.command.map.LayerMoveUpCommand;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
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
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * The Layers View.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class LayersView extends ViewPart
        implements
            IDropTargetProvider,
            IDoubleClickListener,
            ISelectionChangedListener {

    public static final String ID = "net.refractions.udig.project.ui.layerManager"; //$NON-NLS-1$

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
        if (currentMap != null) {
            currentMap.removeDeepAdapter(checkboxContextListener);
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

    IMap placeholder = ApplicationGIS.NO_MAP;

    class EditManagerListener implements IEditManagerListener {
        Map map;

        synchronized void setCurrentMap( final Map currentMap ) {
            this.map = currentMap;
        }

        public void changed( final EditManagerEvent event ) {
            if (getCurrentMap() != map) {
                map.getEditManager().removeListener(this);
                return;
            }
            if (event.getType() == EditManagerEvent.SELECTED_LAYER) {

                Runnable runnable = new Runnable(){
                    public void run() {
                        if (((IStructuredSelection) viewer.getSelection()).getFirstElement() != event
                                .getNewValue()) {
                            StructuredSelection structuredSelection = new StructuredSelection(event
                                    .getNewValue());
                            getSite().getSelectionProvider().setSelection(structuredSelection);
                        }
                        if (mylarOn()) {
                            viewer.update(map.getLayersInternal().toArray(), null);
                        }
                    }

                    private boolean mylarOn() {
                        Object on = map.getBlackboard().get(MylarAction.KEY);
                        if (on instanceof Boolean)
                            return ((Boolean) on).booleanValue();
                        return false;
                    }
                };
                if (Display.getCurrent() == null)
                    Display.getDefault().asyncExec(runnable);
                else
                    runnable.run();
            }
        }

    }

    private EditManagerListener editManagerListener;

    private IBlackboardListener mylarListener = new IBlackboardListener(){

        public void blackBoardChanged( BlackboardEvent event ) {
            if (event.getKey() == MylarAction.KEY && event.getOldValue() != event.getNewValue()) {
                updateViewer();
            }
        }

        public void blackBoardCleared( IBlackboard source ) {
            updateViewer();
        }

    };

    private IViewportModelListener zoomListener = new IViewportModelListener(){

        public void changed( ViewportModelEvent event ) {
            viewer.getControl().getDisplay().asyncExec(new Runnable(){
                public void run() {
                    viewer.update(currentMap.getMapLayers().toArray(), null);
                }
            });
        }

    };

    private void updateViewer() {
        Runnable runnable = new Runnable(){
            public void run() {
                viewer.update(getCurrentMap().getMapLayers().toArray(),
                        new String[]{MylarAction.KEY});
            }
        };
        if (Display.getCurrent() == null)
            Display.getDefault().asyncExec(runnable);
        else
            runnable.run();
    }
    /**
     * @param currentMap The currentMap to set.
     */
    @SuppressWarnings("unchecked")
    public synchronized void setCurrentMap( final Map currentMap ) {

        if (this.currentMap != null) {
            this.currentMap.removeDeepAdapter(checkboxContextListener);
            this.currentMap.getBlackboard().removeListener(mylarListener);
            this.currentMap.getViewportModel().removeViewportModelListener(zoomListener);
        }

        this.currentMap = currentMap;
        if (viewer != null)
            viewer.setInput(currentMap == null ? placeholder : currentMap);

        if (editManagerListener == null)
            editManagerListener = new EditManagerListener();

        if (currentMap != null) {
            this.currentMap.getViewportModel().addViewportModelListener(zoomListener);
            editManagerListener.setCurrentMap(currentMap);
            this.currentMap.getBlackboard().addListener(mylarListener);
            currentMap.addDeepAdapter(checkboxContextListener);

            if (!(currentMap.getEditManager()).containsListener(editManagerListener))
                currentMap.getEditManager().addListener(editManagerListener);

            Object selectedLayer = currentMap.getEditManager().getSelectedLayer();
            if (selectedLayer != null && viewer != null) {
                viewer.setSelection(new StructuredSelection(selectedLayer));
            }
            updateCheckboxes();
        }
    }

    /**
     * TODO Seems we don't really need that LayersView listens selection changing. To display layers
     * we need to listen only activating of MapEditor. Also it solves some problems and bugs with
     * listeners hell during LayersView closing and opening multiple times.
     * 
     * @author Vitalus
     */
    private class MapEditorListener implements IPartListener, ISelectionChangedListener {

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
            if (part == currentPart)
                return;
            if (part instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) part;
                Object obj = adaptable.getAdapter(Map.class);

                if (obj instanceof Map) {
                    currentPart = part;
                    setCurrentMap(((Map) obj));
                }
            }
        }

        // private LayerApplicabilityMenuCreator applicabilityCreator;
        // private LayerApplicabilityMenuCreator getApplicabilityMenu() {
        // if (applicabilityCreator == null) {
        // applicabilityCreator = new LayerApplicabilityMenuCreator();
        // }
        //
        // return applicabilityCreator;
        // }
        // private void addLayersMenu(EditorPart editor) {
        // IMenuManager
        // manager=editor.getEditorSite().getActionBars().getMenuManager();
        // IMenuManager layerMenu=manager.findMenuUsingPath("layer");
        // //$NON-NLS-1$
        // layerMenu.add(getApplicabilityMenu().getMenuManager());
        // editor.getEditorSite().getActionBars().updateActionBars();
        // }

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
            if (part == this) {
                disposeInternal();
                return;
            }
            if (part != currentPart)
                return;
            // if (part.getSite() != null) {
            // ISelectionProvider provider = part.getSite().getSelectionProvider();
            // if (provider != null) {
            // provider.removeSelectionChangedListener(this);
            // }
            // }
            currentPart = null;
            setCurrentMap(null);
            if (part.getSite().getPage().getEditorReferences().length == 0 && part instanceof EditorPart)
                removeApplicabilityMenu((EditorPart) part);
            viewer.refresh(true);
        }

        private void removeApplicabilityMenu( EditorPart part ) {

            // IMenuManager manager = part.getEditorSite().getActionBars()
            // .getMenuManager();
            // manager.findMenuUsingPath("layer/,)
        }

        /**
         * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partDeactivated( IWorkbenchPart part ) {
        }

        /**
         * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
         */
        public void partOpened( IWorkbenchPart part ) {
            // partActivated(part);
        }

        public void selectionChanged( SelectionChangedEvent event ) {
            // StructuredSelection selection = (StructuredSelection) event.getSelection();
            // Map found = null;
            // Iterator iter = selection.iterator();
            // while( iter.hasNext() ) {
            // Object obj = iter.next();
            // Map map = null;
            // if (obj instanceof Map) {
            // map = (Map) obj;
            // } else if (obj instanceof IAdaptable) {
            // IAdaptable adaptable = (IAdaptable) obj;
            // map = (Map) adaptable.getAdapter(Map.class);
            // }
            // if (map != null) {
            // // Abort if we find two valid maps - want only one
            // if (found != null) {
            // map = null;
            // break;
            // } else {
            // found = map;
            // }
            // }
            // }
            // if (found != null) {
            // setCurrentMap(found);
            // } else {
            // partBroughtToTop(currentPart);
            // }
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
            if (!(selection instanceof IStructuredSelection))
                return;
            this.selection = (IStructuredSelection) selection;
            if (part instanceof LayersView && selection != null && !selection.isEmpty())
                setEnabled(true);
        }

        /**
         * @see org.eclipse.jface.action.Action#setEnabled(boolean)
         */
        @SuppressWarnings("unchecked")
        public void setEnabled( boolean enabled ) {
            super.setEnabled(false);

            if (!enabled || selection == null || selection.isEmpty())
                return;

            for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                Object obj = iter.next();
                if (!(obj instanceof Layer))
                    return;
            }
            super.setEnabled(true);
        }

    }

    CheckboxTreeViewer viewer;

    net.refractions.udig.project.ui.internal.LayersView.LayerAction downAction;

    Map currentMap;

    net.refractions.udig.project.ui.internal.LayersView.LayerAction upAction;

    private MapEditorListener partServiceListener = new MapEditorListener();

    void updateLabels() {
        final Map currentMap;
        synchronized (this) {
            currentMap = this.currentMap;
        }
        if (currentMap == null)
            return;

        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                if (PlatformUI.getWorkbench().isClosing())
                    return;
                if (viewer != null) {
                    viewer.refresh(true);
                }
            }
        });
    }

    void updateCheckboxes() {
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                if (PlatformUI.getWorkbench().isClosing())
                    return;

                final Map currentMap;
                synchronized (this) {
                    currentMap = LayersView.this.currentMap;
                }
                if (currentMap == null)
                    return;

                List<Layer> layers = currentMap.getLayersInternal();
                if (!requiresCheckboxUpdate(layers)) {
                    return;
                }

                final List<Layer> checkedLayers = new ArrayList<Layer>();
                for( Layer layer : layers ) {
                    if (layer.isVisible()) {
                        checkedLayers.add(layer);
                    }
                }

                if (viewer != null) {
                    // viewer.refresh(false);
                    viewer.setCheckedElements(checkedLayers.toArray());
                    ILayer selectedLayer = currentMap.getEditManager().getSelectedLayer();
                    if (selectedLayer != null)
                        viewer.setSelection(new StructuredSelection(selectedLayer), true);
                }
            }

        }, true);
    }

    private boolean requiresCheckboxUpdate( List<Layer> layers ) {
        for( Layer layer : layers ) {
            if (!(layer.isVisible() == viewer.getChecked(layer))) {
                return true;
            }
        }
        return false;
    }

    void updateCheckbox( final Layer layer ) {
        PlatformGIS.asyncInDisplayThread(new Runnable(){
            public void run() {
                if (PlatformUI.getWorkbench().isClosing())
                    return;
                // viewer.refresh(false);
                viewer.setChecked(layer, layer.isVisible());
            }
        }, true);
    }

    Adapter checkboxContextListener = new AdapterImpl(){
        @SuppressWarnings("unchecked")
        public void notifyChanged( final Notification msg ) {

            if (msg.getNotifier() instanceof ContextModel) {
                ContextModel contextModel = (ContextModel) msg.getNotifier();
                Map map = contextModel.getMap();

                if (getCurrentMap() != map) {
                    // Just in case
                    map.removeDeepAdapter(this);
                    return;
                }

                if (PlatformUI.getWorkbench().isClosing())
                    contextModel.eAdapters().remove(this);

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
    };

    private AdapterFactoryContentProvider contentProvider;

    private AdapterFactoryLabelProviderDecorator labelProvider;

    private Action propertiesAction;

    private IAction deleteAction;

    private static LayersView instance;

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {

        getSite().getWorkbenchWindow().getPartService().addPartListener(partServiceListener);

        viewer = new CheckboxTreeViewer(parent, SWT.MULTI);
        contentProvider = new AdapterFactoryContentProvider(ProjectUIPlugin.getDefault()
                .getAdapterFactory()){
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
        labelProvider = new AdapterFactoryLabelProviderDecorator(ProjectExplorer
                .getProjectExplorer().getAdapterFactory(), viewer);
        viewer.setLabelProvider(labelProvider);
        /*
         * In dispose() method we need to remove this listener manually!
         */
        labelProvider.addListener(labelProviderListener);

        if (getSite().getPage().getActiveEditor() != null && getSite().getPage().getActiveEditor() instanceof IAdaptable){
            Object obj = ((IAdaptable)getSite().getPage().getActiveEditor()).getAdapter(Map.class);
            if (obj != null){
                setCurrentMap((Map)obj);
            }
        }
                
        viewer.setSorter(new ViewerLayerSorter());

        // sets the layer visibility to match the check box setting.
        viewer.addCheckStateListener(new ICheckStateListener(){
            public void checkStateChanged( CheckStateChangedEvent event ) {
                if (((Layer) event.getElement()).isVisible() != event.getChecked())
                    ((Layer) event.getElement()).setVisible(event.getChecked());
            }
        });
        // We need to set the selection provider before creating the global actions
        // (so ToolManager can hook us up to the global actions like properties and delete)
        //
        getViewSite().setSelectionProvider(viewer);
        viewer.addSelectionChangedListener(this);
        viewer.addDoubleClickListener(this);

        createContextMenuFor(viewer);
        addMenuActions();
        addToobarActions();
        setGlobalActions();

        UDIGDragDropUtilities.addDragDropSupport(viewer, this);

    }

    private ILabelProviderListener labelProviderListener = new ILabelProviderListener(){
        public void labelProviderChanged( LabelProviderChangedEvent event ) {
            updateLabels();
        }
    };

    /**
     * Updates the viewer with new selected layer.
     * 
     * @param newSelection
     */
    protected void updateSelection( final ILayer newSelection ) {
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                if (PlatformUI.getWorkbench().isClosing())
                    return;

                if (getCurrentMap().getLayersInternal().size() > 0) {
                    if (getCurrentMap() != null) {
                        if (getCurrentMap().getLayersInternal().contains(newSelection)) {
                            viewer.setSelection(new StructuredSelection(newSelection));
                        }
                    }
                }
            }
        });
    }

    private void setGlobalActions() {
        IToolManager toolManager = ApplicationGIS.getToolManager();
        IActionBars actionBars = getViewSite().getActionBars();
        toolManager.contributeGlobalActions(this, actionBars);
        toolManager.registerActionsWithPart(this);
        IKeyBindingService service = getSite().getKeyBindingService();
        Action action = new Action(){
            Delete delete = new Delete();

            @Override
            public void run() {
                delete.selectionChanged(this, viewer.getSelection());
                delete.run(this);
            }
        };

        action.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), action);
        service.registerAction(action);

    }

    /**
     * This is how the framework determines which interfaces we implement. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param key The desired class
     * @return An object of type key or null;
     */
    public Object getAdapter( Class key ) {
        if (key.equals(IPropertySheetPage.class)){
            return ProjectUIPlugin.getDefault().getPropertySheetPage();
        }
        if( key.isAssignableFrom(IMap.class)){
            return getCurrentMap();
        }
        return super.getAdapter(key);
    }

    private void addToobarActions() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(upAction());
        mgr.add(downAction());
    }

    /**
     * Create an action that moves a layer down in the rendering order
     */
    private LayerAction downAction() {
        downAction = new LayerAction(){
            public void run() {
                if( selection.isEmpty() ) return;
                IMap map = getCurrentMap();
                // map.sendCommandSync( new LayerMoveDownCommand( selection ));
                map.sendCommandASync( new LayerMoveDownCommand( selection ));
            }
        };
        downAction.setEnabled(false);
        downAction.setToolTipText(Messages.LayersView_down_tooltip);
        downAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.DOWN_CO));
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
                if( selection.isEmpty() ) return;
                IMap map = getCurrentMap();
                //map.sendCommandSync( new LayerMoveUpCommand( selection ));
                map.sendCommandASync( new LayerMoveUpCommand( selection ));
            }
        };
        upAction.setEnabled(false);
        upAction.setToolTipText(Messages.LayersView_up_tooltip);
        upAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.UP_CO));
        return upAction;
    }

    private void addMenuActions() {
        // do nothing
    }

    /**
     * Creates a context menu
     * 
     * @param viewer2
     */
    private void createContextMenuFor( final Viewer viewer2 ) {
        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {
                contextMenu.add(ApplicationGIS.getToolManager().getCOPYAction(LayersView.this));
                contextMenu.add(ApplicationGIS.getToolManager().getPASTEAction(LayersView.this));
                contextMenu.add(getDeleteAction());
                contextMenu.add(new Separator());
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                // LayerApplicabilityMenuCreator creator = getApplicabilityMenu();
                // if (creator != null)
                // contextMenu.add(creator.getMenuManager());
                contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(
                        viewer.getSelection()));
                contextMenu.add(new Separator());
                contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
                contextMenu.add(new Separator());
                if (viewer.getTree().getSelectionCount() == 1)
                    contextMenu.add(getPropertiesAction());
            }

            private LayerApplicabilityMenuCreator applicabilityCreator;

            private LayerApplicabilityMenuCreator getApplicabilityMenu() {
                if (applicabilityCreator == null) {
                    applicabilityCreator = new LayerApplicabilityMenuCreator();
                }

                IStructuredSelection selection = (IStructuredSelection) viewer2.getSelection();
                for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                    Object element = iter.next();
                    if (!(element instanceof Layer))
                        return null;
                }

                return applicabilityCreator;
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(viewer2.getControl());
        viewer2.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, viewer2);

    }

    private IAction getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new Action(){
                @Override
                public void run() {
                    Delete delete = new Delete();
                    ISelection s = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getSelectionService().getSelection();
                    delete.selectionChanged(this, s);
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
            // propertiesAction=ActionFactory.PROPERTIES.create(getSite().getWorkbenchWindow());
            // propertiesAction.setEnabled(true);

            final PropertyDialogAction tmp = new PropertyDialogAction(new SameShellProvider(shell),
                    getViewer());

            propertiesAction = new Action(){
                @Override
                public void runWithEvent( Event event ) {
                    ZoomingDialog dialog = new ZoomingDialog(shell, tmp.createDialog(),
                            ZoomingDialog.calculateBounds(viewer.getTree().getSelection()[0], -1));
                    dialog.open();
                }
            };

            propertiesAction.setText(tmp.getText());
            propertiesAction.setActionDefinitionId(tmp.getActionDefinitionId());
            propertiesAction.setDescription(tmp.getDescription());
            propertiesAction.setHoverImageDescriptor(tmp.getHoverImageDescriptor());
            propertiesAction.setImageDescriptor(tmp.getImageDescriptor());
            propertiesAction.setToolTipText(tmp.getToolTipText());

        }
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
                propertiesAction);
        return propertiesAction;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public static Viewer getViewer() {
        LayersView viewPart = getViewPart();
        if (viewPart == null)
            return null;
        return viewPart.viewer;
    }

    @Override
    public void init( IViewSite site ) throws PartInitException {
        super.init(site);
        instance = this;
    }

    public static LayersView getViewPart() {
        return instance;
    }

    public Object getTarget( DropTargetEvent event ) {
        if (getCurrentMap() == null)
            return this;
        List<ILayer> mapLayers = getCurrentMap().getMapLayers();
        if (mapLayers.isEmpty())
            return this;

        return mapLayers.get(mapLayers.size() - 1);
    }

    public void doubleClick( DoubleClickEvent event ) {

        final Object obj = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        if (!(obj instanceof IProjectElement)) {
            Display.getDefault().asyncExec(new Runnable(){

                public void run() {
                    // TODO for future when styling is in properties view
                    // getPropertiesAction().runWithEvent(new Event());

                }
            });
            return;
        }

    }

    public void selectionChanged( SelectionChangedEvent event ) {
        if (event.getSelection().isEmpty()) {
            return;
        }

        if (!(event.getSelection() instanceof IStructuredSelection))
            return;
        IStructuredSelection structured = (IStructuredSelection) event.getSelection();
        Object firstElement = structured.getFirstElement();
        if (firstElement instanceof ILayer) {
            ILayer layer = ((ILayer) firstElement);
            if (layer.getStatus() == ILayer.ERROR) {
                getViewSite().getActionBars().getStatusLineManager().setErrorMessage(
                        layer.getStatusMessage());
            } else {
                getViewSite().getActionBars().getStatusLineManager().setErrorMessage(null);
                getViewSite().getActionBars().getStatusLineManager().setMessage(
                        layer.getStatusMessage());

            }
        } else {
            getViewSite().getActionBars().getStatusLineManager().setMessage(null);
            getViewSite().getActionBars().getStatusLineManager().setErrorMessage(null);
        }

    }
}
