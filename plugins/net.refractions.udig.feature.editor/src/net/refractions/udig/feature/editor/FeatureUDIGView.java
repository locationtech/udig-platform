package net.refractions.udig.feature.editor;

import java.util.Iterator;

import net.refractions.udig.feature.panel.FeaturePanelPage;
import net.refractions.udig.feature.panel.FeaturePanelPageContributor;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IFeatureSite;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.feature.FeatureSiteImpl;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager; and is communicated with the a page
 * via a FeatureSite. We also have a special EditFeature implementation where each setAttribute call
 * is backed by a command.
 * <p>
 * This is the "most normal" implementation directly extending PageBookView resulting in one
 * "feature panel page" per workbench part. This should provide exellent isolation between maps
 * allowing the user to quickly switch between them.
 * 
 * @author Jody
 * @since 1.2.0
 */
public class FeatureUDIGView extends ViewPart implements FeaturePanelPageContributor, IUDIGView {
    public static final String ID = "net.refractions.udig.feature.editor.featureView";

    private IToolContext context;
    private FeaturePanelPage featurePage;
    private MessagePage messagePage;
    private PageBook book;
    private SimpleFeature current;

    private PageSite pageSite;
    
    @Override
    public void init( IViewSite site ) throws PartInitException {
        super.init(site);
        this.pageSite = new PageSite( site );
    }
    
    public void createPartControl( Composite parent ) {
        book = new PageBook( parent, SWT.NONE );
        messagePage = new MessagePage();
        messagePage.setMessage("Please select a feature with the edit geometry tool");
        messagePage.init( pageSite );
        messagePage.createControl(book);
        
        featurePage = new FeaturePanelPage(this);
        
        featurePage.init( pageSite );        
        featurePage.setFeatureSite(new FeatureSiteImpl());
        featurePage.createControl(book);
        final IMap map = ApplicationGIS.getActiveMap();
        if (map != ApplicationGIS.NO_MAP) {
            try {
                editFeatureChanged(map.getEditManager().getEditFeature());
            } catch (Throwable e) {
                UiPlugin.log("Default SimpleFeature Editor threw an exception", e); //$NON-NLS-1$
            }
        }
        
        book.showPage( messagePage.getControl() );
    }
    
    public SimpleFeatureType getSchema() {
        if( current == null ) return null;
        return current.getFeatureType();
    }
    
    public void setFocus() {
        if (current == null){
            book.showPage( messagePage.getControl() );
            messagePage.setFocus();
            
            featurePage.editFeatureChanged(null);
        }
        else {
            book.showPage( featurePage.getControl() );
            featurePage.setFocus();
            if (current == null){
                featurePage.editFeatureChanged(null);
            }
        }
    }
    
    public void editFeatureChanged( SimpleFeature feature ) {
        this.current = feature;
        if( feature == null ){
            book.showPage( messagePage.getControl() );
            return;
        }
        // pass the selection to the page
        
        IMap activeMap = ApplicationGIS.getActiveMap();
        IFeatureSite site = featurePage.getFeatureSite();
        if( site == null ){
            site = new FeatureSiteImpl(activeMap);
            featurePage.setFeatureSite(site);       
        }
        else {
            ((FeatureSiteImpl)site).setMapInternal( (Map) activeMap);
            ((FeatureSiteImpl)site).setFeature( feature );
        }
        featurePage.editFeatureChanged(feature);
        featurePage.refresh();
        book.showPage( featurePage.getControl() );
    }

    public IToolContext getContext() {
        return context;
    }

    public void setContext( IToolContext newContext ) {
        this.context = newContext;
    }

}
