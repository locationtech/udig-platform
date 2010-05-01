package net.refractions.udig.feature.editor;

import java.util.List;

import net.refractions.udig.feature.editor.AbstractPageBookView.PageRec;
import net.refractions.udig.feature.panel.FeaturePanelPage;
import net.refractions.udig.feature.panel.FeaturePanelPageContributor;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor;
import net.refractions.udig.project.ui.feature.FeatureSiteImpl;
import net.refractions.udig.project.ui.feature.FeaturePanelProcessor.FeaturePanelEntry;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager; and is communicated with
 * the a page via a FeatureSite. We also have a special EditFeature implementation
 * where each setAttribute call is backed by a command.
 * <p>
 * This is the "most normal" implementation directly extending PageBookView resulting
 * in one "feature panel page" per workbench part. This should provide exellent isolation
 * between maps allowing the user to quickly switch between them.
 * 
 * @author Jody
 * @since 1.2.0
 */
public class FeatureView2 extends PageBookView implements ISelectionListener, FeaturePanelPageContributor {

    public static final String ID = "net.refractions.udig.feature.editor.featureView";
    
    //private IWorkbenchPart contributor;
        
    /**
     * The current selection of the property sheet
     */
    private ISelection currentSelection;

    /**
     * The current part for which this property sheets is active
     */
    private IWorkbenchPart currentPart;
    
    public void setContributor( IWorkbenchPart part ) {
        this.currentPart = part;
    }
    
    @Override
    public void init( IViewSite site ) throws PartInitException {
        site.getPage().addSelectionListener(this);
        super.init(site);
    }
    
    public void dispose() {
        super.dispose(); // run super

        // remove ourselves as a selection listener
        getSite().getPage().removeSelectionListener(this);
        
        currentPart = null;
        currentSelection = null;
    }
    
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel == null || !isImportant(part) || sel.equals(currentSelection)) {
            return;
        }
        
        // we ignore selection if we are hidden OR selection is coming from another source as the last one
        if(part == null || !part.equals(currentPart)){
            return;
        }
        
        currentPart = part;
        currentSelection = sel;
        
        // pass the selection to the page       
        IPage page = getCurrentPage(); // We are expecting a FeaturePanelPage here
        if (page != null && page instanceof ISelectionListener) {
            ISelectionListener notifyPage = (ISelectionListener) page;
            notifyPage.selectionChanged(part, currentSelection);
        }
    }    
    
    public SimpleFeatureType getSchema() {
        if(  getCurrentContributingPart() != null ){
            currentPart = getCurrentContributingPart();
        }
        return getSchema( currentPart );
    }
    
    private SimpleFeatureType getSchema( IWorkbenchPart part ){
        IMap map = (IMap) part.getAdapter( IMap.class );        
        if( map == null ) return null;
        
        IEditManager editManager = map.getEditManager();
        if( editManager == null ) return null;
                
        ILayer selectedLayer = editManager.getSelectedLayer();
        if( selectedLayer == null ) return null;
        
        return selectedLayer.getSchema();
    }
    
    
    @Override
    protected IPage createDefaultPage( PageBook book ) {
        MessagePage page = new MessagePage();        
        page.setMessage("Default Page");
        
        initPage((IPageBookViewPage) page);        
        page.createControl( getPageBook() );
        
        return page;
    }
    
    @Override
    protected PageRec doCreatePage( IWorkbenchPart part ) {
        IMap map = (IMap) part.getAdapter( IMap.class );        
        if( map == null ) {
            MessagePage page = new MessagePage();
            page.setMessage( "Please select a Map" );
            initPage( page );
            page.createControl(getPageBook());
            
            PageRec rec = new PageRec( part, page );        
            return rec;
        }
        setContributor( part );
        IPage page = (IPage) new FeaturePanelPage( this );
        initPage((IPageBookViewPage) page);
        page.createControl(getPageBook());
        return new PageRec(part, page);    
    }

    @Override
    protected void doDestroyPage( IWorkbenchPart part, PageRec pageRecord ) {
    }

    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        if( window == null ) return null;
        
        IWorkbenchPage page = window.getActivePage();
        if( page == null ) return null;
        
        IEditorPart editor = page.getActiveEditor();
        if( editor == null ) return null;
        
        IMap map = (IMap) editor.getAdapter( IMap.class );
        if( map == null ) return null;
        
        return editor;
    }

    @Override
    protected boolean isImportant( IWorkbenchPart part ) {
        IMap map = (IMap) part.getAdapter( IMap.class );
        
        return map != null;
    }



}
