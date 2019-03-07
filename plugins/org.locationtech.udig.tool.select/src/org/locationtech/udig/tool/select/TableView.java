/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tool.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.Schema;
import org.geotools.filter.FilterAttributeExtractor;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.core.StaticBlockingProvider;
import org.locationtech.udig.core.feature.AdaptableFeatureCollection;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.core.filter.AdaptingFilterFactory;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.CompositeCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.command.factory.SelectionCommandFactory;
import org.locationtech.udig.project.command.provider.FIDFeatureProvider;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.edit.DeleteFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.project.ui.tool.ToolsConstants;
import org.locationtech.udig.tool.select.internal.Messages;
import org.locationtech.udig.tool.select.internal.ZoomSelection;
import org.locationtech.udig.ui.FeatureTableControl;
import org.locationtech.udig.ui.IFeatureTableLoadingListener;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Table view for selected Layer, may choose
 * to display FeatureSource with out supporting selection
 * in the future.
 * </p>
 * Currently this is a playground using the FeatureTable
 * to look at a FeautreSource, syncing up the slection
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
 * @version 1.3.0
 */
public class TableView extends ViewPart implements ISelectionProvider, IUDIGView {

    private static final String INITIAL_TEXT = Messages.TableView_search;

    protected static final String ANY = Messages.TableView_search_any;
    protected static final String CQL = "CQL";
    
    /** filter the content by the current AOI */
    private boolean aoiFilter = false;


    /** Used to show the current feature source */
    FeatureTableControl table;
    
    /** Current editor */
    MapPart currentEditor;
    
    /** Current layer under study */
    Layer layer;
    
    /** Toolbar entry used to turn on selection mode */
    private IAction select;

    /**
     * This listener watches the workbench selection and reports
     * back anything that.
     */
    private ISelectionListener workbenchSelectionListener = new ISelectionListener(){
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
            if (part instanceof MapPart) {
                editorActivated((MapPart) part);
                return; // we already have sorted out map / layer
            }
            if (!(selection instanceof IStructuredSelection)){
                return;
            }
            if( part == getSite().getPart() ){
                // we are swapping to ourself!
                return; // ignore
            }
            
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            final Layer selectedLayer;
            // this is horribly inelegant. is there not some other way?
            if (selected instanceof Map) {
                selectedLayer = ((Map) selected).getEditManagerInternal().getSelectedLayer();
            } else if (selected instanceof Layer) {
                selectedLayer = (Layer) selected;
            } else if (selected instanceof IAdaptable) {
                // This is often an AdaptableFilter
                IAdaptable adaptable = (IAdaptable) selected;
                selectedLayer = (Layer) adaptable.getAdapter(Layer.class);
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
                
                //todo add layer event
            }
        }
    };
    
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

    private AOIListener aoiServiceListener;

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
        
        // Select All Button
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
                String key = org.locationtech.udig.project.preferences.PreferenceConstants.P_SELECTION_COLOR;
                RGB color = PreferenceConverter.getColor(store, key);
                return color;
            }
            
        });
        
        IAOIService aOIService = PlatformGIS.getAOIService();
        
        aoiServiceListener = new AOIListener(){
            @Override
            public void handleEvent( Event event ) {
                if(isAOIFilter()){
                    reloadFeatures(layer);
                }
            }            
        };
        
        aOIService.addListener(aoiServiceListener);
        
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
        
        if( page.getActiveEditor() instanceof MapPart ){            
            editorActivated( (MapPart) page.getActiveEditor() );
        }
        
        addTableSelectionListener();
        addWorkbenchSelectionListener();
        addPageListener();        
        
        //provide workbench selections
        getSite().setSelectionProvider( this );

        ApplicationGIS.getToolManager().registerActionsWithPart(this);

    }
    
    public boolean isAOIFilter() {
        return aoiFilter;
    }

    public void setAOIFilter( boolean aoiFilter ) {
        this.aoiFilter = aoiFilter;
        reloadFeatures(layer);
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
                    updateLayerFilter((Filter)Filter.EXCLUDE);
                }else if( selection instanceof IStructuredSelection ){
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    Filter firstElement = (Filter) structuredSelection.getFirstElement();
                    updateLayerFilter(firstElement);
                }
                //TODO Sprint Mauricio Aritz: The next line provoke the SelectionTool cannot execute the commit operation (acceptChangeBehaviour line 109). 
                // Actually we do not understand the purpose of this line, but it is the source of problem.  
                //layer.getMapInternal().getEditManagerInternal().setEditFeature(null, null);
                
                fireSelectionChanged();
            }
        });
    }

    private void updateLayerFilter( Filter filter ) {
        updatingLayerFilter=true;
        MapCommand createSelectCommand = SelectionCommandFactory.getInstance().createSelectCommand(layer, filter);
        layer.getMap().sendCommandSync(createSelectCommand);
        updatingLayerFilter=false;

        setZoomToSelectionToolEnablement();
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
    private void addWorkbenchSelectionListener() {
        // page.addPostSelectionListener(workbenchSelectionListener);        
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.addPostSelectionListener(workbenchSelectionListener);
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
                if (partRef.getPart(false) == TableView.this)
                    deactivate();
            }
            public void partVisible( IWorkbenchPartReference partRef ) {
                if (partRef.getPart(false) == TableView.this)
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
        
        zoom=ApplicationGIS.getToolManager().createToolAction(ZoomSelection.ID, ToolsConstants.ZOOM_CATEGORY);
        icon = AbstractUIPlugin.imageDescriptorFromPlugin( SelectPlugin.ID, "icons/elcl16/zoom_select_co.png"); //$NON-NLS-1$
        zoom.setImageDescriptor( icon );
        zoom.setText(Messages.TableView_zoomToolText);
        zoom.setToolTipText(Messages.TableView_zoomToolToolTip);
        
        deleteAction = new DeleteAction();
    }

    /* Called when a Layer is selected - will need to check if we care */
    void layerSelected( ILayer selected ){
        if( layer == selected ) {
            return; // we already know
        }        
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
        
        if( layer.getMap()==ApplicationGIS.getActiveMap() && layer.getFilter()!=Filter.EXCLUDE )
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
    
    /**
     * Listener that watches the current layer; and will update the table selection to match
     */
    private ILayerListener layerListener = new ILayerListener(){

        public void refresh( LayerEvent event ) {
            final ILayer notifierLayer = event.getSource();
            assert layer == notifierLayer;
            
            switch( event.getType() ) {
            case EDIT_EVENT:
                if (!editing) {
                    if (event.getNewValue() == null) {
                        // there are now bounds associated with this event so we are going to have to reload everyone!
                        reloadFeatures(notifierLayer);
                        return;
                    }
                    // okay we will add these bounds to the list of "updates" and updateTable can fetch everything
                    // when we are back to being "active"
                    updates.add((FeatureEvent) event.getNewValue());
                    if (active) {
                        updateTable(notifierLayer);
                    }
                }
                break;
            case FILTER:
                if (active) {
                    updateSelection(notifierLayer);
                } else {
                    filterChange = true;
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
                if( ((List<?>)event.getNewValue()).contains(layer) )
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
        
        if( aoiServiceListener!=null){
            IAOIService aOIService = PlatformGIS.getAOIService();
            aOIService.removeListener(aoiServiceListener);
        }
        
        super.dispose();
        if( activePartListener!=null )
            page.removePartListener(activePartListener);
        if( workbenchSelectionListener!=null ){
            ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
            selectionService.removePostSelectionListener(workbenchSelectionListener);
        }
        if( layer!=null && layerListener!=null )
            layer.removeListener(layerListener);
        table = null;
        activePartListener = null;
        workbenchSelectionListener = null;
        layer = null;
        layerListener = null;
    }

    protected void updateSelection( final ILayer notifierLayer ) {
        if (!active) {
            filterChange = true;
            return;
        }
        if (updatingLayerFilter) {
            return; // our own table view is updating the selection (so we can ignore this
                    // notification)
        }

        try {
            final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = notifierLayer
                    .getResource(FeatureSource.class, null);
            filterChange = false;
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    updatingSelection = true;
                    if (updatingLayerFilter){
                        return; // we are updating table so please ignore this one
                    }
                    Filter filter = (Filter) notifierLayer.getFilter();
                    if (filter == Filter.EXCLUDE) {
                        table.setSelection(new StructuredSelection());
                        return;
                    }
                    AdaptingFilter adaptingFilter = AdaptingFilterFactory
                            .createAdaptingFilter(filter);
                    adaptingFilter.addAdapter(featureSource);

                    StructuredSelection selection = new StructuredSelection(adaptingFilter);
                    table.setSelection(selection);
                }
            });
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
                        if( bounds != null ){
                            if( addedBounds==null ){
                                addedBounds=new Envelope(bounds);
                            }else{
                                addedBounds.expandToInclude(bounds);
                            }
                        }
                        break;
                    case FeatureEvent.FEATURES_REMOVED:
                        // With current Event API there is no way to know what was removed
                        reloadNeeded=true;
                        if( active )
                            reloadFeatures(notifierLayer);
                        return;
                        
                    case FeatureEvent.FEATURES_CHANGED:
                        if (event.getBounds() == null) {
                            return;
                        }
                        if (modifiedBounds == null) {
                            modifiedBounds = new Envelope(bounds);
                        } else {
                            modifiedBounds.expandToInclude(bounds);
                        }
                        break;

                    default:
                        break;
                    }
                }
                updates.clear();
            }
            // check if we actually go something out of all that
            if( addedBounds == null && modifiedBounds == null){
                // fine we did not get anything we will need to reload
                if( active ){
                    reloadFeatures(notifierLayer);
                }else{
                    reloadNeeded=true;
                }
                return;
            }
            // okay now we will do a query for everything in the added or modified bounds
            FeatureSource<SimpleFeatureType, SimpleFeature> source = notifierLayer.getResource(FeatureSource.class, ProgressManager.instance().get());
            SimpleFeatureType schema=source.getSchema();
            
            FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
            final List<String> queryAtts = obtainQueryAttributesForFeatureTable(schema);
            final DefaultQuery query=new DefaultQuery(schema.getName().getLocalPart(), Filter.EXCLUDE, queryAtts.toArray(new String[0]));

            String name = schema.getGeometryDescriptor().getName().getLocalPart();
			// add new features
            if( addedBounds!=null ){
            	double minx=addedBounds.getMinX();
				double miny=addedBounds.getMinY();
				double maxx=addedBounds.getMaxX();
				double maxy=addedBounds.getMaxY();
				String srs=CRS.lookupIdentifier(schema.getCoordinateReferenceSystem(), false);
				BBOX bboxFilter = fac.bbox(name, minx, miny, maxx, maxy, srs);
				
                query.setFilter(bboxFilter);
                FeatureCollection<SimpleFeatureType, SimpleFeature>  features = source.getFeatures(query);
                this.table.update(features);
            }
            // update modified features
            if( modifiedBounds!=null ){
            	double minx=modifiedBounds.getMinX();
				double miny=modifiedBounds.getMinY();
				double maxx=modifiedBounds.getMaxX();
				double maxy=modifiedBounds.getMaxY();
				String srs=CRS.lookupIdentifier(schema.getCoordinateReferenceSystem(), false);
				BBOX bboxFilter = fac.bbox(name, minx, miny, maxx, maxy, srs);

                query.setFilter(bboxFilter);
                FeatureCollection<SimpleFeatureType, SimpleFeature>  features = source.getFeatures(query);
                this.table.update(features);
            }        
        } catch (IOException e) {
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
        } catch (FactoryException e) {
            if( active ){
                reloadFeatures(notifierLayer);
            }else{
                reloadNeeded=true;
            }
        }
    }
    
    private Filter addAOIFilter(Filter filter, CoordinateReferenceSystem dataCRS){
        IAOIService aOIService = PlatformGIS.getAOIService();
        Geometry geometry = aOIService.getGeometry();
        
        if(aOIService.getExtent() == null)
            return filter;
        
        if(geometry == null){
            // note we could make a BBOX query here and go faster
            geometry = JTS.toGeometry( aOIService.getExtent());
            if(geometry == null){
                return filter; // no change!
            }
        }
        CoordinateReferenceSystem aoiCRS = aOIService.getCrs();
        if( aoiCRS != null && !CRS.equalsIgnoreMetadata(aoiCRS, dataCRS )){
            try {
                MathTransform transform = CRS.findMathTransform( aoiCRS, dataCRS);
                geometry = JTS.transform(geometry, transform);
            }
            catch( TransformException outOfBounds ){
                return filter; // unable to use this bounds
            } catch (FactoryException notSupported) {
                return filter; // unable to use this bounds
            }
        }
        
        FilterFactory2 ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(null);
        
        String geomProperty = layer.getSchema().getGeometryDescriptor().getLocalName();
        Filter aoiFilter = ff.intersects(ff.property(geomProperty), ff.literal(geometry));
        
        return ff.and(aoiFilter, filter );
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
            protected void makeModification( SimpleFeature feature, ILayer layer, String property, Object value, Item item ) {
                //if( value == null ){
                //    // not a valid entry.
                //    return;
                //}
                TableItem tableItem=(TableItem) item;
                Schema schema = new Schema();
                int columnIndex = schema.getIndexOf( feature.getFeatureType(), property );
                tableItem.setText(columnIndex+1, value != null ? value.toString() : "");
                
                UndoableComposite composite = new UndoableComposite();
                composite.getCommands().add(new SetEditingFlag(true));
                
                composite.getCommands().add(
                        EditCommandFactory.getInstance().createSetAttributeCommand(feature, layer, property, value));
                composite.getFinalizerCommands().add(new SetEditingFlag(false));
                layer.getMap().sendCommandASync(composite);
            }
        };
        
        final SimpleFeatureType schema = notifierLayer.getSchema();
        Filter filter = Filter.INCLUDE;
        final  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = notifierLayer.getResource(FeatureSource.class, null);
        final List<String> queryAtts = obtainQueryAttributesForFeatureTable(schema);

        //if the filter action is true, filter our results by the AOI service
        if(isAOIFilter()){
            filter = addAOIFilter(filter, schema.getCoordinateReferenceSystem());
        }
        final Query query = new DefaultQuery(schema.getName().getLocalPart(), filter, queryAtts.toArray(new String[0]));
        FeatureCollection<SimpleFeatureType, SimpleFeature>  featuresF = featureSource.getFeatures(query);        
        final FeatureCollection<SimpleFeatureType, SimpleFeature>  features = featuresF;
        
        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                    if (!table.showWarning(table.getControl().getDisplay())){
                        //user doesn't want to show table.
                        return;
                    }

                    // we don't need to display the geometries, that's what the map is for.
                    queryAtts.add(0,ANY);
                    queryAtts.add(CQL);
                    attributeCombo.setItems(queryAtts.toArray(new String[0]));                    
                    attributeCombo.select(0);
                    
                    AdaptableFeatureCollection adaptableCollection = new AdaptableFeatureCollection(features);
                    adaptableCollection.addAdapter(featureSource); // used to listen for changes

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

    

    private List<String> obtainQueryAttributesForFeatureTable( final SimpleFeatureType schema ) {
        final List<String> queryAtts=new ArrayList<String>();
        
        for( int i = 0; i < schema.getAttributeCount(); i++ ) {
            AttributeDescriptor attr = schema.getDescriptor(i);
            if( !(attr instanceof GeometryDescriptor) ){
                queryAtts.add(attr.getName().getLocalPart());
            }
        }
        return queryAtts;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangeListeners.add(listener);
    }

    public ISelection getSelection() {
        Id firstElement=getFilter();
        if( firstElement ==null ){
            return new StructuredSelection();
        }
        AdaptingFilter filter = AdaptingFilterFactory.createAdaptingFilter(firstElement, layer);
        if (layer.getGeoResource().canResolve(FeatureSource.class)) {
            try {
                FeatureSource<?,?> resolve = layer.getGeoResource().resolve(FeatureSource.class, null);
                FeatureCollection<?,?> features = resolve.getFeatures(filter);
                filter.addAdapter(features);
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        return new StructuredSelection( filter );
    }

    private Id getFilter() {
        IStructuredSelection selection=(IStructuredSelection) table.getSelection();
        if( selection.isEmpty() )
            return null;
        
        Id firstElement = (Id) selection.getFirstElement();
        return firstElement;
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangeListeners.remove(listener);
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
    /**
     * Delete Action used to delete the currently selected feature.
     */
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
            
            Id filter=(Id) selection.getFirstElement();

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
        private CompositeCommand deleteManyFeatures( Id filter ) {
            CompositeCommand composite=new CompositeCommand();
            composite.setName(Messages.TableView_compositeName);
            composite.getCommands().add(new SetEditingFlag(true));
            composite.getCommands().add(new DeleteManyFeaturesCommand(layer,filter));
            composite.getCommands().add(new DeleteFromTableCommand(layer));
            composite.getFinalizerCommands().add(new SetEditingFlag(false));
            return composite;
        }
    }
    private class DeleteFromTableCommand extends AbstractCommand implements UndoableCommand, IBlockingProvider<SimpleFeature>{

        private FeatureCollection<SimpleFeatureType, SimpleFeature> deletedFeatures;
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

        public SimpleFeature get( IProgressMonitor monitor, Object... params ) throws IOException {

            IBlockingProvider<ILayer> layerProvider=new StaticBlockingProvider<ILayer>(layer);
            String featureID = deletedFeatures.features().next().getID();
            IBlockingProvider<SimpleFeature> featureProvider=new FIDFeatureProvider(featureID, layerProvider);

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

    public void editFeatureChanged( SimpleFeature feature ) {
        if (feature == null ){
           return;
        }
        if( getContext().getEditManager().getSelectedLayer()!=layer || updatingLayerFilter )
            return;

        if( table.getSelectionCount()==1 && getFilter().getIDs().toArray(new String[0])[0].equals(feature.getID()) )
            return;
        
        updatingLayerFilter=true;
        try{
            StructuredSelection structuredSelection;
            structuredSelection = new StructuredSelection(feature);
    
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
                        if (searchWidget.getForeground().equals(systemColor)) {
                            searchWidget.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
                            searchWidget.setText(""); //$NON-NLS-1$
                        }
                        ApplicationGIS.getToolManager().unregisterActions(TableView.this);
	            }
	        });
	        searchWidget.addListener(SWT.FocusOut, new Listener(){
	            public void handleEvent( Event e ) {
                        if (!searchWidget.getForeground().equals(systemColor)
                                && searchWidget.getText().trim().length() == 0) {
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

		@SuppressWarnings("unchecked")
        private void doSearch() {
			if (searchWidget.getText().trim().length()==0 ){
		        searchWidget.setText(INITIAL_TEXT);
		        searchWidget.setForeground(systemColor);
		    }
		    
		    String[] attsToSearch;
		    String item = attributeCombo.getItem(attributeCombo.getSelectionIndex());
		    boolean selectAll=selectAllCheck.getSelection();                
		    if( item.equals(CQL) ){
		        try {
		            String txt = searchWidget.getText().trim();
		            //table.select( txt, selectAll);
                    //searchWidget.setToolTipText(null);
                    Filter filter = (Filter) org.geotools.filter.text.cql2.CQL.toFilter( txt );
                    //updateLayerFilter( filter );
                    
                    FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, ProgressManager.instance().get());
                    SimpleFeatureType schema=source.getSchema();               
                    //FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
                    //final List<String> queryAtts = obtainQueryAttributesForFeatureTable(schema);
                    
                    Set<String> required = (Set<String>) filter.accept( new FilterAttributeExtractor(), null );
                    String[] names = required.toArray( new String[ required.size()]);
                    final DefaultQuery query=new DefaultQuery(schema.getName().getLocalPart(), filter, names );
                    
                    FeatureCollection<SimpleFeatureType, SimpleFeature> features;
                    features = source.getFeatures( query ); // we just want the FeatureID no attributes needed
                    
                    //features = source.getFeatures( filter );
                    
                    final Set<FeatureId> selection = new HashSet<FeatureId>();
                    features.accepts( new FeatureVisitor(){
                        public void visit( Feature feature) {
                            // we are using FeatureId to allow for a "temporary" FID when inserting content
                            // (a real FID is not assigned until commit)
                            //
                            FeatureId identifier = feature.getIdentifier();
                            selection.add( identifier );
                        }                        
                    }, null );
                    table.select( selection );
                } catch (Exception e ){
                    Status status = new Status(Status.WARNING, "org.locationtech.udig.ui", e.getLocalizedMessage(), e );
                    UIPlugin.getDefault().getLog().log( status );
                    searchWidget.setToolTipText( e.getLocalizedMessage() );
                }
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
