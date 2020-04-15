/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.info.internal;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureEvent.Type;
import org.geotools.data.FeatureSource;
import org.geotools.ows.wms.Layer;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.AdaptableFeature;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.LayerEvent.EventType;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.internal.impl.LayerImpl;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.properties.FeaturePropertySource;
import org.locationtech.udig.tool.info.CoveragePointInfo;
import org.locationtech.udig.tool.info.InfoPlugin;
import org.locationtech.udig.tool.info.InfoTool;
import org.locationtech.udig.tool.info.LayerPointInfo;
import org.locationtech.udig.tool.info.internal.display.BrowserInfoDisplay;
import org.locationtech.udig.tool.info.internal.display.TextInfoDisplay;
import org.locationtech.udig.ui.SearchPart;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

/**
 * Info recast as a search part.
 * 
 * @author jgarnett
 * @since 1.0.0
 */
public class InfoView2 extends SearchPart {
    /** <code>VIEW_ID</code> field */
    public static final String VIEW_ID = "org.locationtech.udig.tool.info.infoView"; //$NON-NLS-1$
    private Text information;
    private TextInfoDisplay textDisplay;
    private BrowserInfoDisplay browserDisplay;
    //private FeatureDisplay featureDisplay;
    private PropertySheetPage featureDisplay;
	private IMapCompositionListener mapListener;
	private ILayerListener layerListener;
	private List<ILayer>  layerList;
	private Thread	fatherThread= null;
    protected ImageRegistry registry;
    
    private class InfoViewLabelProvider extends LabelProvider implements IColorProvider {
        
        private static final String FEATURE_LABEL = "FEATURE_LABEL"; //$NON-NLS-1$
        
        public String getText(Object element) {
            if (element instanceof AdaptableFeature) {
                final AdaptableFeature feature = (AdaptableFeature) element;
                return getFeatureLabel(feature);
            }
            else if (element instanceof LayerPointInfo) {
                LayerPointInfo info = (LayerPointInfo) element;                    
                return info.getLayer().getName();
            }
            else if (element instanceof CoveragePointInfo) {
                CoveragePointInfo info = (CoveragePointInfo) element;                    
                return info.getLayer().getName();
            }
            return super.getText(element);
        }
        
        /**
         * Gets the feature label by running the feature label expression set for the feature.
         * 
         * @param feature
         * @return feature label
         */
        private String getFeatureLabel(AdaptableFeature feature) {
            
            final ILayer layer = (ILayer) feature.getAdapter(ILayer.class);
            final IGeoResource resource = layer.getGeoResource();
            
            return getFeatureLabel(resource, feature);
            
        }
        
        /**
         * Gets the feature label by running the feature label expression set for the feature.
         * 
         * @param resource
         * @param feature
         * @return feature label
         */
        private String getFeatureLabel(IGeoResource resource, SimpleFeature feature) {
            
            final String labelExpression = (String) resource.getPersistentProperties().get(
                    FEATURE_LABEL);
            
            if (labelExpression != null) {
                try {
                    final Expression exp = ECQL.toExpression(labelExpression);
                    return (String) exp.evaluate(feature);
                } catch (CQLException e) {
                    e.printStackTrace();
                }    
            }
            
            return feature.getID();
            
        }
        
        /**
         * @see org.locationtech.udig.project.internal.provider.LayerItemProvider
         */
        public Color getBackground(Object element) {
            if (element instanceof AdaptableFeature) {
                AdaptableFeature feature = (AdaptableFeature) element;
                LayerImpl layer = (LayerImpl) feature.getAdapter(ILayer.class);
                IColorProvider colorProvider = (IColorProvider) layer.getAdapter(IColorProvider.class);
                if (colorProvider == null) return null;
                return colorProvider.getBackground(layer);
            }            
            return null;
        }

        /**
         * @see org.locationtech.udig.project.internal.provider.LayerItemProvider
         */
        public Color getForeground(Object element) {
            if (element instanceof AdaptableFeature) {
                AdaptableFeature feature = (AdaptableFeature) element;
                LayerImpl layer = (LayerImpl) feature.getAdapter(ILayer.class);
                IColorProvider colorProvider = (IColorProvider) layer.getAdapter(IColorProvider.class);
                if (colorProvider == null) return null;
                return colorProvider.getForeground(layer);
            }
            return null;
        }
        
        @Override
        public Image getImage( Object element ) {
            ILayer layer = null;
            if( element instanceof AdaptableFeature){
                AdaptableFeature feature = (AdaptableFeature) element;
                layer = (ILayer) feature.getAdapter(ILayer.class);                
            }
            if( element instanceof LayerPointInfo){
                LayerPointInfo info = (LayerPointInfo) element;
                layer = info.getLayer();
            }
            if( element instanceof CoveragePointInfo){
                CoveragePointInfo info = (CoveragePointInfo) element;
                layer = info.getLayer();
            }
            String key = layer.getID().toExternalForm();                
            Image image = registry.get( key );
            if( image == null ){
                ImageDescriptor icon;
                icon = (ImageDescriptor) layer.getProperties().get("generated icon");
                if( icon == null ){
                    icon = layer.getIcon();
                }
                if( icon == null ){
                    IGeoResource resource = layer.getGeoResource();
                    try {
                        icon = resource.getInfo(null).getImageDescriptor();
                    } catch (IOException e) {
                        // not expecting this to block or throw IO as the layer was available
                        // to be hit by the info tool
                    }                    
                }
                if( icon != null ){
                    registry.put( key, icon );
                    image = registry.get(key);
                }
            }
            return image;
        }
    }

    public static class InfoRequest {
        public ReferencedEnvelope bbox;
        public List<ILayer> layers;
    };
    
    /**
     * 
     */
    public InfoView2() {
        super( InfoPlugin.getDefault().getDialogSettings() );
    }
        
	private void initiListeners() {

		this.mapListener = new IMapCompositionListener() {

			public void changed(MapCompositionEvent event) {

				updatedMapLayersActions(event);
			}
		};
		
		this.layerListener = new ILayerListener() {
			
			public void refresh(LayerEvent event) {
				
				updateLayerActions(event);
			}
		};
	}
    
    @Override
    protected Composite createDetails( SashForm splitter ) {
    	initiListeners();
    	this.fatherThread = Thread.currentThread();
        PageBook book = new PageBook( splitter, SWT.NONE );        
        splitter.setWeights(new int[]{10, 90});

        {
            information = new Text(book, SWT.WRAP );
            information.setText(Messages.InfoView_instructions_text); 
            book.showPage(information);            
        }
        {
            textDisplay = new TextInfoDisplay();
            textDisplay.createDisplay(book);
            //textDisplay.getControl().setVisible(false);
        }
        {
            browserDisplay = new BrowserInfoDisplay();
            browserDisplay.createDisplay(book);
            //browserDisplay.getControl().setVisible(false);
        }
        {
            //featureDisplay = new FeatureDisplay();
            //featureDisplay.createDisplay(book);
            featureDisplay = new PropertySheetPage();
            featureDisplay.createControl(book);
            //featureDisplay.getControl().setVisible(false);
        }              
        return book;
    }


	@Override    
    protected void fillActionBars() {
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBar = actionBars.getToolBarManager();
     
        IAction infoTool = ApplicationGIS.getToolManager().getToolAction(InfoTool.ID, InfoTool.CATEGORY_ID);
        assert( infoTool != null );
        if( toolBar != null ){
            toolBar.add( infoTool );
        }
        super.fillActionBars();
        
    }
    
    @Override
    protected PageBook getDetails() {
        return (PageBook) super.getDetails();
    }
    @Override
    protected void showDetail( Object selection ) {
        if (selection == null ) {
            getDetails().showPage(information);
            return;
        }
        if( selection instanceof SimpleFeature ) {
            getDetails().showPage(featureDisplay.getControl());
            FeaturePropertySource source = new FeaturePropertySource( (SimpleFeature)selection, false, false);
            StructuredSelection sel = new StructuredSelection(source);
            featureDisplay.selectionChanged(null, sel);
            //featureDisplay.setInfo( (SimpleFeature) selection );
        }
        if (selection instanceof LayerPointInfo) {
            LayerPointInfo info = (LayerPointInfo) selection;
            if (info.getMimeType() == null) {
                getDetails().showPage(information);
            } else if (info.getMimeType().startsWith(LayerPointInfo.GML)) {
                getDetails().showPage(featureDisplay.getControl());
                try {
                    SimpleFeature feature = (SimpleFeature) info.acquireValue();
                    FeaturePropertySource src = new FeaturePropertySource(feature);
                    StructuredSelection sel = new StructuredSelection(src);
                    featureDisplay.selectionChanged(null, sel);
                } catch(IOException ex) {
                    InfoPlugin.log("GML value could not be acquired.", ex); //$NON-NLS-1$
                }
                //featureDisplay.setInfo(info);
            } else if (info.getRequestURL() != null &&
                       info.getMimeType().startsWith(LayerPointInfo.HTML)) {
                getDetails().showPage( browserDisplay.getControl() );
                browserDisplay.setInfo(info);        
            } else if (info.getRequestURL() != null && info.getMimeType().startsWith(LayerPointInfo.XML)) {
                getDetails().showPage( browserDisplay.getControl() );
                browserDisplay.setInfo(info);
            } else if (info.getRequestURL() != null && info.getMimeType().startsWith(LayerPointInfo.ERROR)) {
                getDetails().showPage( browserDisplay.getControl() );
                browserDisplay.setInfo(info);        
            } else if (info.getRequestURL() != null && info.getMimeType().startsWith(LayerPointInfo.TEXT)) {
                getDetails().showPage( browserDisplay.getControl() );
                browserDisplay.setInfo(info);
            } else {
                    getDetails().showPage(information);
            }
        }        
        if (selection instanceof CoveragePointInfo) {
            CoveragePointInfo info = (CoveragePointInfo) selection;
            information.setText(info.getInfo());
            getDetails().showPage(information);
        }        
    
    }
    
    @Override
    public void createPartControl( Composite aParent ) {
        super.createPartControl(aParent);
        ApplicationGIS.getToolManager().registerActionsWithPart(this);
        registry = new ImageRegistry( aParent.getDisplay() );        
    }
    
    /**
     * Called by search( Object filter ) in a job to get stuff done.
     * <p>
     * Resulting like is provided to the viewer via setInput.
     * </p>
     */
    @Override
    protected void searchImplementation( Object filter, IProgressMonitor monitor, ResultSet set ) {
        InfoRequest request = (InfoRequest) filter;

        if( monitor != null ) {
            monitor.beginTask( Messages.InfoView2_information_request, request.layers.size() );
        }
        //add listener to the current map
        addMapListener();
        layerList = new LinkedList<ILayer>();
        int work = 0;
        for( int i = request.layers.size()-1; i > -1; i-- ) {
            ILayer layer = request.layers.get(i); //navigate the list backwards
            monitor.subTask( layer.getName() );
            monitor.worked( ++work );
        	List<ILayer> currentLayerList = ApplicationGIS.getActiveMap().getMapLayers();
        	if (!currentLayerList.contains(layer)) continue;
            if( !layer.isVisible() ) continue;
            if( !layer.getInteraction(Interaction.INFO)) continue;
            
            //if( !layer.isApplicable( "information" )) continue;
            //add listener to the current layer and add that layer to be processed.
            addLayerListener(layer);
            layerList.add(layer);
            if( layer.hasResource( FeatureSource.class ) ) {
                try {
                    List<SimpleFeature> more = DataStoreDescribeLayer.info2( layer, request.bbox, monitor );
                    if( !more.isEmpty() ) {
                        set.addAll( more );
                    }
                }
                catch( Throwable t ) {
                    InfoPlugin.log( "Information request "+layer.getName()+" failed "+t, t ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                continue;
            }            
            if( layer.hasResource( GridCoverage.class ) ) {
                try {
                    CoveragePointInfo hit = CoverageDescribeLayer.info2( layer, request.bbox, monitor );
                    if( hit != null ) set.add( hit );
                }
                catch( Throwable t ) {
                    InfoPlugin.log( "Information request "+layer.getName()+" failed "+t, t ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                continue;
            }            
            if( layer.hasResource( Layer.class ) ) {
                try {
                    Layer wmsLayer = layer.getResource(Layer.class, new NullProgressMonitor());
                    if( wmsLayer.isQueryable()){
                        LayerPointInfo hit = WMSDescribeLayer.info2( layer, request.bbox );
                        if( hit != null ) {
                            set.add( hit );
                        }
                    }
                }
                catch( Throwable t ) {
                    InfoPlugin.log( "Information request "+layer.getName()+" failed "+t, t ); //$NON-NLS-1$ //$NON-NLS-2$
                    t.printStackTrace();
                }
                continue;
            }
        }
    }
    @Override
    protected IBaseLabelProvider createLabelProvider() {
        return new InfoViewLabelProvider();
    }
  
    @Override
    protected StructuredViewer createViewer( Composite parent ) {
        TreeViewer viewer = new TreeViewer(parent);
        return viewer;
    }

    @Override
    protected ISelection getSelection( List<Object> results ) {

        for( Object object : results ) {
            ILayer layer=null ;
            if( object instanceof LayerPointInfo ){
                LayerPointInfo info=(LayerPointInfo) object;
                layer = info.getLayer();
            }
            if (object instanceof CoveragePointInfo) {
                CoveragePointInfo info = (CoveragePointInfo) object;
                layer = info.getLayer();
            }
            if( object instanceof AdaptableFeature ){
                AdaptableFeature feature=(AdaptableFeature) object;
                layer=(ILayer) feature.getAdapter(ILayer.class);
            }
            if( layer==ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer() ){
                return new StructuredSelection(object);
            }
        }
        if( results.isEmpty() )
            return new StructuredSelection();
        
        return new StructuredSelection(results.get(0));
    }

    @Override
    protected IStructuredContentProvider createContentProvider() {
        return new ITreeContentProvider() {
            @SuppressWarnings("unchecked")
            public Object[] getElements( Object inputElement ) {
                if( inputElement instanceof List ) {
                    return ((List)inputElement).toArray();
                }
                return null;
            }
            public void dispose() {
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                //assert( newInput instanceof List );
                // lists don't have events for us to watch
            }
            public Object[] getChildren( Object parentElement ) {
                return null;
            }
            public Object getParent( Object element ) {
                return null;
            }
            public boolean hasChildren( Object element ) {
                return false;
            }            
        };
    }
    
    /**
     * If the layer is removed then removed all the items from the tree because they won't be
     * accessible anymore.
     * 
     * @param event
     */
    private void updatedMapLayersActions( MapCompositionEvent event ) {

        MapCompositionEvent.EventType eventType = event.getType();

        switch( eventType ) {
        case REMOVED:
        case MANY_REMOVED:

            // if the layer was removed, then it isn't in the list anymore, so the feature/s showed
            // on the tree could be removed.
            List<ILayer> currentLayerList = ApplicationGIS.getActiveMap().getMapLayers();
            for( ILayer layer : layerList ) {
                if (!currentLayerList.contains(layer)) {
                    removeItemsFromTree();
                    break;
                }
            }
            break;
        default:
            break;
        }
    }

    /**
     * If the feature is deleted and also it's contained on the tree, remove it.
     * 
     * @param event
     */
    private void updateLayerActions( LayerEvent event ) {

        EventType eventType = event.getType();

        switch( eventType ) {
        case EDIT_EVENT:
            FeatureEvent featureEvent = (FeatureEvent) event.getNewValue();

            if (featureEvent.getType() == Type.REMOVED) {
                removeFeatureFromTree();
            }
            break;
        default:
            break;
        }
    }

    /**
     * With each entry from the tree, creates a FidFilter, retrieves from the store that query. If
     * a feature is returned, then this feature still exist, if not, then remove the associated treeItem.
     */
    private void removeFeatureFromTree() {

        Display.findDisplay(fatherThread).asyncExec(new Runnable(){

            public void run() {
                
                Tree tree = ((TreeViewer) viewer).getTree();
                TreeItem[] treeItems = tree.getItems();
                TreeItem removedItem = null;
                for( int i = 0; i < treeItems.length; i++ ) {
                    TreeItem item = treeItems[i];

                    if (item.getData() instanceof SimpleFeature) {
                        SimpleFeature feature = (SimpleFeature) item.getData();
                        Filter id = createFidFiler(feature.getID());
                        if (!isFeatureOnStore(id)) {
                            // this feature was removed, so remove from tree.
                            removedItem = treeItems[i];
                            break;
                        }
                    }
                }
                if (removedItem != null) {
                    // if a feature is deleted, show the initial style
                    getDetails().showPage(information);
                    tree.deselectAll();
                    tree.getItem(tree.indexOf(removedItem)).dispose();
                }
            }
            /**
             * Check if the feature is on the store.
             * @param id
             * @return True if the feature is in the store.
             */
            private boolean isFeatureOnStore( Filter id ) {

                SimpleFeatureCollection collection = null;
                SimpleFeatureIterator iter = null;
                try {
                    collection = layerList.get(0).getResource(SimpleFeatureSource.class, null).getFeatures(id);
                    iter = collection.features();
                    if (iter.hasNext()) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(iter != null) iter.close();
                }
                return false;
            }
        });
    }

    private Filter createFidFiler( String id ) {
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools
                .getDefaultHints());
        Filter filterId = filterFactory.id(FeatureUtils.stringToId(filterFactory, id));
        return filterId;
    }

    private void removeItemsFromTree() {

        Display.findDisplay(fatherThread).asyncExec(new Runnable(){

            public void run() {
                getDetails().showPage(information);
                Tree tree = ((TreeViewer) viewer).getTree();
                if (tree != null && !tree.isDisposed()) {
                    tree.removeAll();
                }
            }
        });
    }

    private void addMapListener() {
        IMap map = ApplicationGIS.getActiveMap();
        map.addMapCompositionListener(mapListener);
    }

    private void addLayerListener( ILayer layer ) {
        layer.addListener(layerListener);
    }
    
    @Override
    public void dispose() {
        if( registry != null ){
            registry.dispose();
            registry = null;
        }
        super.dispose();
    }
}
