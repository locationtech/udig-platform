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
package net.refractions.udig.tool.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticBlockingProvider;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.command.provider.FIDFeatureProvider;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.edit.DeleteFeatureCommand;
import net.refractions.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.internal.AdaptingFilter;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.ToolsConstants;
import net.refractions.udig.tool.select.internal.Messages;
import net.refractions.udig.tool.select.internal.ZoomSelection;
import net.refractions.udig.ui.FeatureTableControl;
import net.refractions.udig.ui.IFeatureTableLoadingListener;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.GeometryFilter;
import org.geotools.filter.IllegalFilterException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Table view for selected Layer, may choose
 * to display FeatureSource with out supporting selection
 * in the future.
 * </p>
 * Currently this is a playground using the FeatureTable
 * to look at a FeautreSource, syncing up the selection
 * with the Layer's filter will come next.
 * </p>
 * <p>
 * Long term responsibilities include:
 * <ul>
 * <li>Access to a Filter editor for selection specification
 * <li>Allowing in view edits
 * <li>Real random access for shapefile allowing the table view
 *     everyone expects (tm)
 * </ul>
 * @author Jody Garnett, Refractions Research, Inc.
 * @since 0.6
 */
public class TableView extends ViewPart implements ISelectionProvider, IUDIGView {

    private static final String INITIAL_TEXT = Messages.TableView_search;

    protected static final String ANY = Messages.TableView_search_any;
    protected static final String CQL = "CQL";

    /** Used to show the current feature source */
    FeatureTableControl table;

    /** Current editor */
    MapPart currentEditor;

    /** Current layer under study */
    Layer layer;

    /** Toolbar entry used to turn on selection mode */
    private IAction select;

    private ISelectionListener workbenchSelectionListener;

    /**
     * page that the part listener and the workbenchSelectionListener are listening to.
     */
    private IWorkbenchPage page;

    /**
     * Listener that deactivates/reactivates view when it is hidden/shown
     */
    private IPartListener2 activePartListener;

    private MenuManager contextMenu;

    /**
     * Indicates whether the view is visible and therefore is active
     */
    private volatile boolean active=false;

    /**
     * Indicates that the features in the view need to be reloaded when the view is visible again.
     */
    protected volatile boolean reloadNeeded=false;

    /**
     * Indicates that the selection filter has changed while inactive
     */
    protected volatile boolean filterChange=false;

    /**
     * Indicates that the a feature is being updated by the table view so
     * it is not necessary to load the change indicated by the feature event.
     */
    protected volatile boolean editing=false;

    /**
     * Indicates that the selection of the table is being updated by the view because
     * the filter has been updated on the layer.
     */
    private volatile boolean updatingSelection=false;

    /**
     * Indicates that the view is updating the layer's filter because the selection on the table has changed.
     */
    protected volatile boolean updatingLayerFilter;

    private PromoteSelectionAction promoteSelection;

    private Label featuresSelected;

    private Combo attributeCombo;

    private IAction zoom;
    private IAction deleteAction;

	private Button selectAllCheck;

	private Text searchWidget;

    /**
     * Construct <code>SelectView</code>.
     * <p>
     * Don't do setup here - there is an init method you can override that has
     * access to configuration and stuff.
     * </p>
     */
    public TableView() {
        super();
    }

    @Override
    public void createPartControl( Composite parent ) {
        active=true;

        featuresSelected = new Label(parent, SWT.NONE);
        featuresSelected.setText(Messages.TableView_featureSelected+0);

        attributeCombo=new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
        attributeCombo.setItems(new String[]{ANY, CQL});
        attributeCombo.select(0);
        attributeCombo.setEnabled(false);

        SearchBox search = new SearchBox();
        searchWidget = search.createPart(parent);

        IProvider<IProgressMonitor> provider = new IProvider<IProgressMonitor>(){

            public IProgressMonitor get(Object... params) {
                IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
                statusLineManager.setCancelEnabled(true);
                return statusLineManager.getProgressMonitor();
            }

        };
        selectAllCheck=new Button(parent, SWT.CHECK);
        selectAllCheck.setText(Messages.TableView_allCheckText);
        selectAllCheck.setToolTipText(Messages.TableView_allToolTip);
        selectAllCheck.setEnabled(false);
        selectAllCheck.setSelection(true);

        table = new FeatureTableControl(provider);
        table.createTableControl( parent );
        table.setSelectionColor(new IProvider<RGB>(){

            public RGB get(Object... params) {
                ScopedPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
                String key = net.refractions.udig.project.preferences.PreferenceConstants.P_SELECTION_COLOR;
                RGB color = PreferenceConverter.getColor(store, key);
                return color;
            }

        });
        table.addLoadingListener(new IFeatureTableLoadingListener(){

            public void loadingStarted( IProgressMonitor monitor ) {
                searchWidget.setEnabled(false);
                selectAllCheck.setEnabled(false);
                attributeCombo.setEnabled(false);
            }

            public void loadingStopped( boolean canceled ) {
                searchWidget.setEnabled(true);
                selectAllCheck.setEnabled(true);
                attributeCombo.setEnabled(true);
            }

        });
        layoutComponents(parent);

        // Create menu and toolbars
        createActions();
        createMenu();
        createToolbar();
        hookGlobalActions();
        createContextMenu();

        // restore state (from previous session)

        page = getSite().getPage();

        if( page.getActiveEditor() instanceof MapEditor ){
            editorActivated( (MapPart) page.getActiveEditor() );
        }

        addTableSelectionListener();
        addSelectionListener();
        addPageListener();

        //provide workbench selections
        getSite().setSelectionProvider( this );



        ApplicationGIS.getToolManager().registerActionsWithPart(this);
    }


    private void addTableSelectionListener() {
        table.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if( event.getSource()==table)
                    featuresSelected.setText(Messages.TableView_featureSelected+table.getSelectionCount());
                if( updatingSelection ){
                	updatingSelection=false;
                    return;
                }

                ISelection selection = getSelection();
                if( selection.isEmpty() ){
                    updateLayerFilter(Filter.ALL);
                }else if( selection instanceof IStructuredSelection ){
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    Filter firstElement = (Filter) structuredSelection.getFirstElement();
                    updateLayerFilter(firstElement);
                }

                layer.getMapInternal().getEditManagerInternal().setEditFeature(null, null);

                fireSelectionChanged();
            }

            private void updateLayerFilter( Filter filter ) {
                updatingLayerFilter=true;
                UndoableMapCommand createSelectCommand = SelectionCommandFactory.getInstance().createSelectCommand(layer, filter);
                layer.getMap().sendCommandSync(createSelectCommand);
                updatingLayerFilter=false;

                setZoomToSelectionToolEnablement();
            }

        });
    }

    private void layoutComponents( Composite parent ) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0,5);
        dLabel.top = new FormAttachment(attributeCombo,2);
        dLabel.right = new FormAttachment(100,-5);
        featuresSelected.setLayoutData(dLabel);

        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(0,5);
        dCombo.top = new FormAttachment(0);
        dCombo.right = new FormAttachment(30, -5);
        attributeCombo.setLayoutData(dCombo);

        FormData dText = new FormData(); // bind to label and text
        dText.left = new FormAttachment(attributeCombo);
        dText.top = new FormAttachment(0);
        dText.right = new FormAttachment(95,-5);
        searchWidget.setLayoutData(dText);

        FormData dCheck = new FormData(); // bind to top, label, bbox
        dCheck.top = new FormAttachment(2);
        dCheck.left = new FormAttachment(searchWidget, 5);
        dCheck.right = new FormAttachment( 100,-5);
        selectAllCheck.setLayoutData(dCheck);

        FormData dContents = new FormData(100, 100); // text & bottom
        dContents.right = new FormAttachment(100); // bind to right of form
        dContents.left = new FormAttachment(0); // bind to left of form
        dContents.top = new FormAttachment(featuresSelected, 2); // attach with 2 pixel offset
        dContents.bottom = new FormAttachment(100); // bind to bottom of form
        table.getControl().setLayoutData(dContents);
    }

    /**
     * Adds a post selection listener that listens to the workbench's selection for maps, layers or MapEditor.
     */
    private void addSelectionListener( ) {
        workbenchSelectionListener = new ISelectionListener(){
                    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
                        if (part instanceof MapEditor ){
                            editorActivated((MapPart) part);
                            return;
                        }
                        if (!(selection instanceof IStructuredSelection)) return;

                        Object selected = ((IStructuredSelection) selection).getFirstElement();

                        final Layer selectedLayer;
                        // this is horribly inelegant.  is there not some other way?
                        if (selected instanceof Map) {
                            selectedLayer = ((Map) selected).getEditManagerInternal().getSelectedLayer();
                        } else if (selected instanceof Layer) {
                            selectedLayer = (Layer) selected;
                        } else if (selected instanceof AdaptingFilter) {
                            AdaptingFilter adaptingFilter = (AdaptingFilter) selected;
                            selectedLayer = (Layer) adaptingFilter.getAdapter(Layer.class);
                        } else {
                            return;
                        }

                        if (selectedLayer != null) {
                            PlatformGIS.run(new ISafeRunnable(){

                                public void handleException( Throwable exception ) {
                                    SelectPlugin.log("error selecting layer", exception); //$NON-NLS-1$
                                }

                                public void run() throws Exception {
                                    layerSelected(selectedLayer);
                                }

                            });
                        }
                    }
                };
        // listen to selections
        page.addPostSelectionListener(workbenchSelectionListener);
    }

    /**
     * Adds a listener to {@link #page} that deactivates the view when part is hidden and reactivates it when it is made
     * visible.  This is to prevent a bunch of featurestore accesses when view is not visible.
     */
    private void addPageListener( ) {
        activePartListener = new IPartListener2(){
                    public void partActivated( IWorkbenchPartReference partRef ) {
                    }
                    public void partBroughtToTop( IWorkbenchPartReference partRef ) {
                    }
                    public void partClosed( IWorkbenchPartReference partRef ) {
                    }
                    public void partDeactivated( IWorkbenchPartReference partRef ) {
                    }
                    public void partOpened( IWorkbenchPartReference partRef ) {
                    }
                    public void partHidden( IWorkbenchPartReference partRef ) {
                        if( partRef.getPart(false)==TableView.this )
                            deactivate();
                    }
                    public void partVisible( IWorkbenchPartReference partRef ) {
                        if( partRef.getPart(false)==TableView.this )
                            activate();
                    }
                    public void partInputChanged( IWorkbenchPartReference partRef ) {
                    }
                };
        // listen for editor changes
        page.addPartListener(activePartListener);
    }

    protected void activate() {
        if( active )
            return;

        PlatformGIS.run(new ISafeRunnable(){

            public void handleException( Throwable exception ) {
                SelectPlugin.log("error activating table", exception); //$NON-NLS-1$
            }

            public void run() throws Exception {
                active=true;
                if( reloadNeeded )
                    reloadFeatures(layer);
                if( !updates.isEmpty() )
                    updateTable(layer);
                if ( filterChange )
                    updateSelection(layer);
            }

        });
    }

    protected void deactivate() {
        active=false;
    }

    private void hookGlobalActions() {
        ApplicationGIS.getToolManager().contributeGlobalActions(this, getViewSite().getActionBars());
        IKeyBindingService service = getSite().getKeyBindingService();
        IAction action = deleteAction;
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), action);
        service.registerAction(action);

    }

    /**
     * Create actions, linking view to current map.
     */
    private void createActions() {
        select=ApplicationGIS.getToolManager().createToolAction(BBoxSelection.ID, ToolsConstants.SELECTION_CATEGORY);
        ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin( SelectPlugin.ID, "icons/eview16/select_view.gif"); //$NON-NLS-1$
        select.setImageDescriptor( icon );

        this.promoteSelection=new PromoteSelectionAction();

        zoom=((ToolManager) ApplicationGIS.getToolManager()).createToolAction(ZoomSelection.ID, ToolsConstants.ZOOM_CATEGORY);
        icon = AbstractUIPlugin.imageDescriptorFromPlugin( SelectPlugin.ID, "icons/elcl16/zoom_select_co.png"); //$NON-NLS-1$
        zoom.setImageDescriptor( icon );
        zoom.setText(Messages.TableView_zoomToolText);
        zoom.setToolTipText(Messages.TableView_zoomToolToolTip);

        deleteAction = new DeleteAction();
    }

    /* Called when a Layer is selected - will need to check if we care */
    void layerSelected( ILayer selected ){
        if( layer == selected ) return; // we already know

        if( layer!=null ){
            layer.removeListener(layerListener);
            if( layer.getMap()!=null ){
                layer.getMap().removeMapCompositionListener(compositionListener);
                layer.getMap().getEditManager().removeListener(editManagerListener);
            }
        }

        if( selected==null || !selected.hasResource( FeatureSource.class ) || selected.getMap()==null ){
            if( currentEditor!=null ){
                currentEditor.getMap().getEditManager().addListener(editManagerListener);
            }
            layer = null;
            filterChange=false;
            reloadNeeded=false;
            table.getControl().getDisplay().asyncExec(new Runnable(){
                public void run() {
                    table.clear();
                    table.message(Messages.TableView_noFeatureWarning );
                }
            });
            return;
        }

        layer = (Layer) selected;
        layer.addListener(layerListener);
        layer.getMap().addMapCompositionListener(compositionListener);
        layer.getMap().getEditManager().addListener(editManagerListener);

        if( !active ){
            filterChange=true;
            reloadNeeded=true;
            return;
        }

        setZoomToSelectionToolEnablement();
        reloadFeatures(layer);
        updateSelection(layer);
    }

    private void setZoomToSelectionToolEnablement() {
        final boolean enabled;

        if( layer.getMap()==ApplicationGIS.getActiveMap() && layer.getFilter()!=Filter.ALL )
            enabled=true;
        else
            enabled=false;

        table.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                zoom.setEnabled(enabled);
            }
        });
    }

    /**
     * The list of updates that have occurred but have not yet been applied to the FeatureTable
     */
    private List<FeatureEvent> updates=Collections.synchronizedList(new ArrayList<FeatureEvent>());
    private ILayerListener layerListener=new ILayerListener(){

        public void refresh( LayerEvent event ) {
          final ILayer notifierLayer = event.getSource();
          assert layer==notifierLayer;

        switch (event.getType()) {
          case EDIT_EVENT:
              if( !editing ){
                  if( event.getNewValue()==null )
                      return;

                  updates.add((FeatureEvent) event.getNewValue());
                  if( active ){
                      updateTable(notifierLayer);
                  }
              }
              break;
          case FILTER:
              if( active )
                updateSelection(notifierLayer);
            else{
                  filterChange=true;
              }
              break;
          }

        }


    };

    private IMapCompositionListener compositionListener=new IMapCompositionListener(){

        public void changed( MapCompositionEvent event ) {

            if( event.getType()==MapCompositionEvent.EventType.REMOVED ){
                if( event.getLayer()==layer ){
                    layerSelected(null);
                }
            }else if( event.getType()==MapCompositionEvent.EventType.MANY_REMOVED ){
                if( ((List)event.getNewValue()).contains(layer) )
                    layerSelected(null);
            }
        }

    };

    private IEditManagerListener editManagerListener=new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
            assert layer==null || (layer!=null && layer.getMap()==event.getSource().getMap());

            switch( event.getType() ) {
            case EditManagerEvent.POST_COMMIT:
            case EditManagerEvent.POST_ROLLBACK:
                if( !active ){
                    reloadNeeded=true;
                    return;
                }
                reloadFeatures(layer);
                break;
            case EditManagerEvent.SELECTED_LAYER:
                layerSelected((ILayer) event.getNewValue());
                break;
            default:
                break;
            }
        }

    };
    private Set<ISelectionChangedListener> selectionChangeListeners=new CopyOnWriteArraySet<ISelectionChangedListener>();

    private IToolContext currentContext;

    /**
     * Watch current editor to indicate current selectable layers.
     *
     * @param editor
     */
    protected void editorActivated( MapPart editor ) {
        if( currentEditor == editor ) return;

        currentEditor=editor;
        if( editor == null ){
            select.setEnabled(false);
            table.clear();
            table.update();
            return;
        }

        Map map = currentEditor.getMap();
        final ILayer selectedLayer = map.getEditManager().getSelectedLayer();
        // layerSelecte returns if layer==newLayer.  If both are null we still want to listen to map for a
        // layer being added (and selected).
        if( selectedLayer==null && layer == null ){
            map.getEditManager().addListener(editManagerListener);
            return;
        }

        PlatformGIS.run(new ISafeRunnable(){

            public void handleException( Throwable exception ) {
                SelectPlugin.log("error selecting layer", exception); //$NON-NLS-1$
            }

            public void run() throws Exception {
                layerSelected(selectedLayer);
            }

        });
        if( selectedLayer!=null )
            select.setEnabled(true);
    }

    private void createToolbar() {
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( select );
        toolbar.add( zoom );
        toolbar.add( promoteSelection );
    }

    private void createContextMenu() {
        contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager mgr) {
                contextMenu.add(deleteAction);
                contextMenu.add(zoom);
                contextMenu.add(promoteSelection);
                contextMenu.add(new GroupMarker(
                        IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(getSelection()));
                contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
            }

        });

        // Create menu.
        table.setMenuManager(contextMenu);
        getSite().registerContextMenu(contextMenu, this);
    }

    private void createMenu() {
        // create view menu, consider sync
    }

    @Override
    public void setFocus() {
        hookGlobalActions();
        // select your "main" control
        if(table.getControl()!=null)
            table.getControl().setFocus();
    }

    @Override
    public void dispose() {
        if( table!=null)
            table.dispose();
        super.dispose();
        if( activePartListener!=null )
            page.removePartListener(activePartListener);
        if( workbenchSelectionListener!=null )
            page.removePostSelectionListener(this.workbenchSelectionListener);
        if( layer!=null && layerListener!=null )
            layer.removeListener(layerListener);
        table = null;
        activePartListener = null;
        workbenchSelectionListener = null;
        layer = null;
        layerListener = null;
    }

    protected void updateSelection( final ILayer notifierLayer ) {
        if( !active ){
            filterChange=true;
            return;
        }
        if( updatingLayerFilter )
            return;
        try{
        final FeatureSource featureSource=notifierLayer.getResource(FeatureSource.class, null);
        filterChange=false;
        Display.getDefault().asyncExec(
              new Runnable() {
                  public void run() {
                      updatingSelection=true;
                      if( updatingLayerFilter )
                          return;
                      Filter filter = notifierLayer.getFilter();
                      if( filter == Filter.ALL ){
                          table.setSelection(new StructuredSelection());
                          return;
                      }
                      AdaptingFilter adaptingFilter = new AdaptingFilter(filter);
                      adaptingFilter.addAdapter(featureSource);
                      table.setSelection(new StructuredSelection(adaptingFilter));
                  }
              }
          );
        } catch (IOException e) {
            SelectPlugin.log("", e); //$NON-NLS-1$
        }
    }
    protected void updateTable( final ILayer notifierLayer ) {
        try{
            // Envelope indicating the bounds of the added features from all the updates currently available in the
            // updates field;
            Envelope addedBounds = null;
            // Envelope indicating the bounds of the modified features from all the updates currently available in the
            // updates field;
            Envelope modifiedBounds = null;

            synchronized (updates) {
                for( FeatureEvent event : updates ) {
                    Envelope bounds = event.getBounds();
                    switch( event.getEventType() ) {
                    case FeatureEvent.FEATURES_ADDED:
                        if( addedBounds==null ){
                            addedBounds=new Envelope(bounds);
                        }else{
                            addedBounds.expandToInclude(bounds);
                        }
                        break;
                    case FeatureEvent.FEATURES_REMOVED:
                        // With current Event API there is no way to know what was removed
                        reloadNeeded=true;
                        if( active )
                            reloadFeatures(notifierLayer);
                        return;

                    case FeatureEvent.FEATURES_CHANGED:
                        if( event.getBounds()==null )
                            return;
                        if( modifiedBounds==null ){
                            modifiedBounds=new Envelope(bounds);
                        }else{
                            modifiedBounds.expandToInclude(bounds);
                        }

                        break;

                    default:
                        break;
                    }
                }
                updates.clear();
            }
            FeatureSource source = notifierLayer.getResource(FeatureSource.class, ProgressManager.instance().get());
            FeatureType schema=source.getSchema();

            FilterFactory fac=FilterFactoryFinder.createFilterFactory();
            final List<String> queryAtts = obtainQueryAttributesForFeatureTable(schema);
            final DefaultQuery query=new DefaultQuery(schema.getTypeName(), Filter.ALL, queryAtts.toArray(new String[0]));



            // add new features
            if( addedBounds!=null ){
                GeometryFilter bboxFilter = fac.createGeometryFilter(FilterType.GEOMETRY_BBOX);
                bboxFilter.addLeftGeometry(fac.createBBoxExpression(addedBounds));
                bboxFilter.addRightGeometry(fac.createAttributeExpression(schema.getDefaultGeometry().getName()));

                query.setFilter(bboxFilter);
                FeatureCollection features = source.getFeatures(query);
                this.table.update(features);
            }
            // update modified features
            if( modifiedBounds!=null ){
                GeometryFilter bboxFilter = fac.createGeometryFilter(FilterType.GEOMETRY_BBOX);
                bboxFilter.addLeftGeometry(fac.createBBoxExpression(modifiedBounds));
                bboxFilter.addRightGeometry(fac.createAttributeExpression(schema.getDefaultGeometry().getName()));

                query.setFilter(bboxFilter);
                FeatureCollection features = source.getFeatures(query);
                this.table.update(features);
            }

        }catch (IOException e) {
            if( active ){
                reloadFeatures(notifierLayer);
            }else{
                reloadNeeded=true;
            }
        } catch (IllegalFilterException e) {
            if( active ){
                reloadFeatures(notifierLayer);
            }else{
                reloadNeeded=true;
            }
        }
    }

    protected void reloadFeatures( final ILayer notifierLayer ) {
        try {
        reloadNeeded = false;
        updates.clear();

//        icon = getLayerIcon()
        final FeatureTypeCellModifier featureTypeCellModifier = new FeatureTypeCellModifier(notifierLayer){
            @Override
            public Object getValue( Object element, String property ) {
                ApplicationGIS.getToolManager().unregisterActions(TableView.this);

                return super.getValue(element, property);
            }
            @Override
            protected void makeModification( Feature feature, ILayer layer, String property, Object value, Item item ) {
                if( value == null ){
                    // not a valid entry.
                    return;
                }
                TableItem tableItem=(TableItem) item;
                int columnIndex = feature.getFeatureType().find(property);
                tableItem.setText(columnIndex+1, value.toString());

                UndoableComposite composite = new UndoableComposite();
                composite.getCommands().add(new SetEditingFlag(true));

                composite.getCommands().add(
                        EditCommandFactory.getInstance().createSetAttributeCommand(feature, layer, property, value));
                composite.getFinalizerCommands().add(new SetEditingFlag(false));
                layer.getMap().sendCommandASync(composite);
            }
        };
        final FeatureType schema = notifierLayer.getSchema();
        final FeatureSource featureSource = notifierLayer.getResource(FeatureSource.class, null);
        final List<String> queryAtts = obtainQueryAttributesForFeatureTable(schema);

        final Query query=new DefaultQuery(schema.getTypeName(), Filter.NONE, queryAtts.toArray(new String[0]));

        final FeatureCollection features = featureSource.getFeatures(query);
//        final FeatureCollection features = featureSource.getFeatures();

        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                    table.showWarning(table.getControl().getDisplay());

                    // we don't need to display the geometries, that's what the map is for.
                    queryAtts.add(0,ANY);
                    queryAtts.add(CQL);
                    attributeCombo.setItems(queryAtts.toArray(new String[0]));
                    attributeCombo.select(0);

                    AdaptableFeatureCollection adaptableCollection = new AdaptableFeatureCollection(features);

                    if( featureSource instanceof FeatureStore )
                        enableEditing(featureTypeCellModifier, query, adaptableCollection);

                    table.setFeatures(adaptableCollection);
            }

            private void enableEditing( final FeatureTypeCellModifier featureTypeCellModifier, final Query query, AdaptableFeatureCollection adaptableCollection ) {
                adaptableCollection.addAdapter(featureTypeCellModifier);
                ICellEditorListener[] keyBindingActivators=new ICellEditorListener[query.getPropertyNames().length];
                for( int i = 0; i < keyBindingActivators.length; i++ ) {
                    keyBindingActivators[i]=new ICellEditorListener(){
                        public void applyEditorValue() {
                            ApplicationGIS.getToolManager().registerActionsWithPart(TableView.this);
                        }

                        public void cancelEditor() {
                            applyEditorValue();
                        }

                        public void editorValueChanged( boolean oldValidState, boolean newValidState ) {

                        }

                    };
                }
                adaptableCollection.addAdapter(keyBindingActivators);
            }
        }
          );
        } catch (final IOException e) {
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    table.message(e.getMessage());
                }
            });
        }
    }

    private List<String> obtainQueryAttributesForFeatureTable( final FeatureType schema ) {
        final List<String> queryAtts=new ArrayList<String>();

        for( int i = 0; i < schema.getAttributeCount(); i++ ) {
            AttributeType attr = schema.getAttributeType(i);
            if( !(attr instanceof GeometryAttributeType) ){
                queryAtts.add(attr.getName());
            }
        }
        return queryAtts;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangeListeners.add(listener);
    }

    public ISelection getSelection() {
        FidFilter firstElement=getFilter();
        if( firstElement ==null )
            return new StructuredSelection();
        return new StructuredSelection( new AdaptingFilter(firstElement, layer) );
    }

    private FidFilter getFilter() {
        IStructuredSelection selection=(IStructuredSelection) table.getSelection();
        if( selection.isEmpty() )
            return null;

        FidFilter firstElement = (FidFilter) selection.getFirstElement();
        return firstElement;
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangeListeners.add(listener);
    }

    protected void fireSelectionChanged(){
        final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
        for( ISelectionChangedListener listener : selectionChangeListeners ){
            final ISelectionChangedListener l = listener;
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(event);
                }
            });
        }
    }

    public void setSelection( ISelection selection ) {
        table.setSelection(selection);
    }

	private class PromoteSelectionAction extends Action{

        public PromoteSelectionAction() {
            setText(Messages.TableView_promote_text);
            setToolTipText(Messages.TableView_promote_tooltip);
            setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(SelectPlugin.ID, "icons/elcl16/promote_selection_co.gif")); //$NON-NLS-1$
        }

        @Override
        public void run() {
            table.promoteSelection();
        }

    }

    private class DeleteAction extends Action{
        public DeleteAction(){
            setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
            IWorkbenchAction actionTemplate = ActionFactory.DELETE.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            setText(actionTemplate.getText());
            setToolTipText(actionTemplate.getToolTipText());
            setImageDescriptor(actionTemplate.getImageDescriptor());
            setDescription(actionTemplate.getDescription());
            setDisabledImageDescriptor(actionTemplate.getDisabledImageDescriptor());
        }

        @Override
        public void run() {
            IStructuredSelection selection = ((IStructuredSelection)table.getSelection());
            if( selection == null || selection.isEmpty() || table.getSelectionCount()==0 )
                return;

            FidFilter filter=(FidFilter) selection.getFirstElement();

            CompositeCommand composite;
            if( table.getSelectionCount()==1 ){
                composite = deleteFeature();
            }else{
                composite = deleteManyFeatures(filter);
            }

            layer.getMap().sendCommandASync(composite);
        }

        private CompositeCommand deleteFeature( ) {
            CompositeCommand composite=new CompositeCommand();
            composite.setName(Messages.TableView_compositeName);
            composite.getCommands().add(new SetEditingFlag(true));
            DeleteFromTableCommand deleteFromTableCommand = new DeleteFromTableCommand(layer);
            composite.getCommands().add(deleteFromTableCommand);
            IBlockingProvider<ILayer> layerProvider=new StaticBlockingProvider<ILayer>(layer);
            composite.getCommands().add(new DeleteFeatureCommand(deleteFromTableCommand, layerProvider));
            composite.getFinalizerCommands().add(new SetEditingFlag(false));
            return composite;
        }
        private CompositeCommand deleteManyFeatures( FidFilter filter ) {
            CompositeCommand composite=new CompositeCommand();
            composite.setName(Messages.TableView_compositeName);
            composite.getCommands().add(new SetEditingFlag(true));
            composite.getCommands().add(new DeleteManyFeaturesCommand(layer,filter));
            composite.getCommands().add(new DeleteFromTableCommand(layer));
            composite.getFinalizerCommands().add(new SetEditingFlag(false));
            return composite;
        }
    }
    private class DeleteFromTableCommand extends AbstractCommand implements UndoableCommand, IBlockingProvider<Feature>{

        private FeatureCollection deletedFeatures;
        private Layer layer;

        public DeleteFromTableCommand( Layer layer ) {
            this.layer=layer;
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
            table.update(deletedFeatures);
        }

        public String getName() {
            return Messages.TableView_deleteCommandName;
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            deletedFeatures=table.deleteSelection();
        }

        public Feature get( IProgressMonitor monitor, Object... params ) throws IOException {

            IBlockingProvider<ILayer> layerProvider=new StaticBlockingProvider<ILayer>(layer);
            String featureID = deletedFeatures.features().next().getID();
            IBlockingProvider<Feature> featureProvider=new FIDFeatureProvider(featureID, layerProvider);

            return featureProvider.get(monitor);
        }

    }

    private class SetEditingFlag extends AbstractCommand implements UndoableCommand{
        boolean oldState;
        final boolean newState;
        public SetEditingFlag(boolean newState){
            this.newState=newState;
        }
        public void rollback( IProgressMonitor monitor ) throws Exception {
            editing=oldState;
        }

        public String getName() {
            return "Set Editing Flag"; //$NON-NLS-1$
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            oldState=editing;
            editing=newState;
        }

    }

    public void editFeatureChanged( Feature feature ) {
        if (feature == null ){
           return;
        }
        if( getContext().getEditManager().getSelectedLayer()!=layer || updatingLayerFilter )
            return;

        if( table.getSelectionCount()==1 && getFilter().getFids()[0].equals(feature.getID()) )
            return;

        updatingLayerFilter=true;
        try{
            StructuredSelection structuredSelection;
            if( feature!=null )
                structuredSelection = new StructuredSelection(feature);
            else
                structuredSelection = new StructuredSelection();

            setSelection(structuredSelection);
        }finally{
            updatingLayerFilter=false;
        }
    }

    public IToolContext getContext() {
        return currentContext;
    }

    public void setContext( IToolContext newContext ) {
        this.currentContext=newContext;
    }

    private class SearchBox extends AbstractHandler implements Listener{
		private Color systemColor;

		public void handleEvent( Event e ) {
			if( e.keyCode==SWT.CR || e.keyCode==SWT.LF ){
				doSearch();
			}
		}



		public Text createPart(Composite parent) {
	        final Text searchWidget = new Text(parent, SWT.BORDER|SWT.SEARCH|SWT.CANCEL );
	        searchWidget.setEnabled(false);

	        systemColor = searchWidget.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

	        searchWidget.setText(INITIAL_TEXT);
	        searchWidget.setForeground(systemColor);
	        searchWidget.setEditable(true);
	        searchWidget.addListener(SWT.FocusIn, new Listener(){
	            public void handleEvent( Event e ) {
                    if( searchWidget.getForeground().equals(systemColor) ){
                        searchWidget.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
                        searchWidget.setText(""); //$NON-NLS-1$
                    }
					ApplicationGIS.getToolManager().unregisterActions(TableView.this);
	            }
	        });
	        searchWidget.addListener(SWT.FocusOut, new Listener(){
	            public void handleEvent( Event e ) {
	                    if( !searchWidget.getForeground().equals(systemColor) && searchWidget.getText().trim().length()==0 ){
	                        searchWidget.setForeground(systemColor);
	                        searchWidget.setText(""); //$NON-NLS-1$
	                    }
	                    ApplicationGIS.getToolManager().registerActionsWithPart(TableView.this);
	            }
	        });
			searchWidget.addListener(SWT.KeyUp, this);
			return searchWidget;
		}


		public Object execute(ExecutionEvent arg0) throws ExecutionException {
			doSearch();
			return null;
		}

		private void doSearch() {
			if (searchWidget.getText().trim().length()==0 ){
		        searchWidget.setText(INITIAL_TEXT);
		        searchWidget.setForeground(systemColor);
		    }

		    String[] attsToSearch;
		    String item = attributeCombo.getItem(attributeCombo.getSelectionIndex());
		    boolean selectAll=selectAllCheck.getSelection();
		    if( item.equals(CQL) ){
		        table.select( searchWidget.getText().trim(), selectAll);
		    }
		    else {
		        if( item.equals(ANY) ){
		            attsToSearch=FeatureTableControl.ALL;
		        }
		        else{
		            attsToSearch=new String[]{item};
		        }
		        table.select(searchWidget.getText().trim(), attsToSearch, selectAll);
		    }
		}

    }

}
