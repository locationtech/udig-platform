package net.refractions.udig.feature.editor;

import java.util.List;

import net.refractions.udig.feature.editor.internal.Messages;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.AdaptingFeatureType;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor.FeaturePanelEntry;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager.
 * 
 * @author jodyg
 * @since 1.2.0
 */
public class FeatureView extends AbstractPageBookView<SimpleFeatureType> {
    public static final String ID = "net.refractions.udig.feature.editor.featureView";

    private IFeatureSite context;
    private SimpleFeature current;

    private IEditManagerListener editListener = new IEditManagerListener(){        
        public void changed( EditManagerEvent event ) {
            if( event.getType() == EditManagerEvent.EDIT_LAYER ){
                //ILayer previousLayer = (ILayer) event.getOldValue();
                ILayer editLayer = (ILayer) event.getNewValue();                
                if( editLayer != null ){
                    activated( editLayer.getSchema() );
                }
                else {
                    activated( null ); // show default page
                }
            }
        }
    };

    public FeatureView(){
    }
    
    @Override
    protected IPage createDefaultPage( PageBook book ) {
        PropertySheetPage page = new PropertySheetPage();
        initPage(page);
        page.createControl(getPageBook());
        
        IMap map = ApplicationGIS.getActiveMap();
        if (map != null && map != ApplicationGIS.NO_MAP) {
            try {
                editFeatureChanged(map.getEditManager().getEditFeature());
            } catch (Throwable e) {
                UiPlugin.log("Default SimpleFeature Editor threw an exception", e); //$NON-NLS-1$
            }
        }
        return page;
    }

    public void editFeatureChanged( SimpleFeature feature ) {
        current = feature;
        StructuredSelection selection;
        Object value = defaultSource;
        if (current != null) {
            value = current;
        } else {
            value = defaultSource;
        }
        selection = new StructuredSelection(value);
        IPage currentPage = getCurrentPage();
        if (currentPage instanceof PropertySheetPage) {
            PropertySheetPage sheet = (PropertySheetPage) currentPage;
            sheet.selectionChanged(null, selection);
        }
    }

    @Override
    protected PageRec<SimpleFeatureType> doCreatePage( SimpleFeatureType schema ) {
        if (schema == null){
            return null; // no feature yet
        }
        FeaturePanelProcessor panels = ProjectUIPlugin.getDefault().getFeaturePanelProcessor();
        IFeatureSite site = null;
        List<FeaturePanelEntry> avaialble = panels.search(schema, site);

        IMap map = ApplicationGIS.getActiveMap();
        if (!avaialble.isEmpty()) {
            MessagePage page = new MessagePage();
            page.setMessage(schema.getTypeName() + " has " + avaialble.size() + " panels");
            
            initPage(page);
            page.createControl( getPageBook() );

            return new PageRec<SimpleFeatureType>(schema, page);
        } else if (map != null) {
            IPage page = (IPage) new FeaturePage(map.getEditManager());
            initPage((IPageBookViewPage) page);
            page.createControl(getPageBook());
            return new PageRec<SimpleFeatureType>(schema, page);
        } else {
            MessagePage page = new MessagePage();
            page.setMessage(schema.getTypeName() + " has no pannels");
            
            initPage(page);
            page.createControl( getPageBook() );


            return new PageRec<SimpleFeatureType>(schema, page);
        }
    }

    @Override
    protected void doDestroyPage( SimpleFeatureType part, PageRec<SimpleFeatureType> pageRecord ) {
        pageRecord.page.dispose();
    }
    
    @Override
    protected SimpleFeatureType getCurrent( IWorkbenchPart part ) {
        if (part == null) {
            return null;
        }
        IMap map = (IMap) part.getAdapter(IMap.class);
        if (map == null) {
            return null;
        }
        IEditManager manager = map.getEditManager();
        ILayer editLayer = manager.getEditLayer();
        if( editLayer == null ){
            return null; // not editing
        }
        SimpleFeatureType schema = editLayer.getSchema();
        SimpleFeatureType schema2 = new AdaptingFeatureType( schema, manager );
        return schema2;
    }

    /**
     * We are interested in grabbing the schema of the current editLayer
     * (if such a beast is available).
     * @return SimpleFeatureType that can adapt to it's edit manager if needed
     */
    protected SimpleFeatureType getCurrent( ISelection selection ) {
        IMap map = selection( selection, IMap.class );
        if( map == null ){
            return null;
        }
        IEditManager manager = map.getEditManager();
        ILayer editLayer = manager.getEditLayer();
        if( editLayer == null ){
            return null; // not editing
        }
        SimpleFeatureType schema = editLayer.getSchema();
        SimpleFeatureType schema2 = new AdaptingFeatureType( schema, manager );
        return schema2; 
    }
    
    /**
     * Will grab the active Map's selected layer, if available as the initial bootstrap part for our
     * feature view.
     */
    protected SimpleFeatureType getBootstrapTarget() {
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null){
            return null;
        }
        ILayer editLayer = map.getEditManager().getEditLayer();
        if( editLayer == null ){
            return null;
        }
        return editLayer.getSchema();
    }

    /**
     * Only consider the selectedLayer important enough to force a refresh.
     */
    protected boolean isImportant( SimpleFeatureType schema ) {
        if( schema == null ) return false;

        if( schema instanceof AdaptingFeatureType){
            schema = ((AdaptingFeatureType)schema).getObject();
        }
        
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null) {
            return false;
        }
        ILayer selectedLayer = map.getEditManager().getSelectedLayer();
        if (schema == selectedLayer.getSchema()) {
            return true;
        }
        return false;
    }

    protected void listen( boolean listen, IWorkbenchPart part ){
        if( part == null){
            return; // we cannot listen to what does not exist
        }
        
        IMap map = (IMap) part.getAdapter( IMap.class );
        if( map == null ){
            return; // nothing to attach to
        }
        if(listen){
            map.getEditManager().addListener(editListener);
        }
        else {
            map.getEditManager().removeListener(editListener);
        }
    }
    
    IAdaptable defaultSource = new IAdaptable(){
        @SuppressWarnings("unchecked")
        public Object getAdapter( Class adapter ) {
            if (IPropertySource.class.isAssignableFrom(adapter))
                return new IPropertySource(){
                    public void setPropertyValue( Object id, Object value ) {
                    }
                    public void resetPropertyValue( Object id ) {
                    }
                    public boolean isPropertySet( Object id ) {
                        return false;
                    }
                    public Object getPropertyValue( Object id ) {
                        return ""; //$NON-NLS-1$
                    }

                    public IPropertyDescriptor[] getPropertyDescriptors() {
                        return new PropertyDescriptor[]{new PropertyDescriptor(
                                "ID", Messages.DefaultEditor_1)}; //$NON-NLS-1$
                    }
                    public Object getEditableValue() {
                        return null;
                    }
                };
            return null;
        }

    };

    public IFeatureSite getContext() {
        return context;
    }

}
