package net.refractions.udig.feature.editor;

import net.refractions.udig.feature.editor.internal.Messages;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.opengis.feature.simple.SimpleFeature;

/**
 * View allowing direct editing of the currently selected feature.
 * <p>
 * The currently selected feature is handled by the EditManager.
 * 
 * @author jodyg
 * @since 1.2.0
 */
public class FeatureView extends PageBookView implements IUDIGView {
    public static final String ID = "net.refractions.udig.feature.editor.featureView";
    
    private boolean viewInPage = true;
    
    private IPerspectiveListener perspectiveListener = new IPerspectiveListener() {
        public void perspectiveChanged(IWorkbenchPage page,
                IPerspectiveDescriptor perspective, String changeId) {
        }
        // fix for bug 109245 and 69098 - fake a partActivated when the perpsective is switched
        public void perspectiveActivated(IWorkbenchPage page,
                IPerspectiveDescriptor perspective) {
            viewInPage = page.findViewReference(ID) != null;
            // getBootstrapPart could return null; but isImportant() can handle null
            partActivated(getBootstrapPart());
        }
    };
    
    /**
     * Add a perspective listener so we can update when the perspective is switched.
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
    }

    /**
     * Remove the perspective listener.
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        getSite().getPage().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);
        super.dispose();
    }
    
    @Override
    protected IPage createDefaultPage( PageBook book ) {
        /*MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("Please select a feature"); //$NON-NLS-1$
        return page;
        */
        PropertySheetPage page = new PropertySheetPage();
        initPage(page);
        page.createControl(book);
        
        final IMap map = ApplicationGIS.getActiveMap();        
        if (map != ApplicationGIS.NO_MAP) {
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
        if (current != null)
            value = current;
        else
            value = defaultSource;
        selection = new StructuredSelection(value);
        IPage currentPage = getCurrentPage();
        if( currentPage instanceof PropertySheetPage){
            PropertySheetPage sheet = (PropertySheetPage) currentPage;
            sheet.selectionChanged( null, selection );
        }
    }    

    @Override
    protected PageRec doCreatePage( IWorkbenchPart part ) {
        // Try to get a IMap
        IMap map = (IMap) part.getAdapter(IMap.class);        
        if (map != null && map instanceof IMap) {
            IEditManager editManager = map.getEditManager();
            
            IPage page = (IPage) new FeaturePage( editManager );
            page.createControl(getPageBook());
            initPage((IPageBookViewPage) page);
            return new PageRec(part, page);
        }
        // Use the default page by returning null
        return null;
    }

    @Override
    protected void doDestroyPage( IWorkbenchPart part, PageRec pageRecord ) {
        
        pageRecord.page.dispose();
    }

    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null)
            return page.getActiveEditor();
        return null;
    }

    @Override
    protected boolean isImportant( IWorkbenchPart part ) {
        return false;
        /*
        IMap map = (IMap) part.getAdapter(IMap.class);
        return map != null; */
    }

    private IToolContext context;
    //private PropertySheetPage featureDisplay;
    private SimpleFeature current;
    
    IAdaptable defaultSource = new IAdaptable(){

        public Object getAdapter( Class adapter ) {
            if (IPropertySource.class.isAssignableFrom(adapter))
                return new IPropertySource(){

                    public void setPropertyValue( Object id, Object value ) {
                        // TODO Auto-generated method stub

                    }

                    public void resetPropertyValue( Object id ) {
                        // TODO Auto-generated method stub

                    }

                    public boolean isPropertySet( Object id ) {
                        // TODO Auto-generated method stub
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
    
    
    public void setContext( IToolContext context ) {
        this.context = context;
    }

    /**
     * @see net.refractions.udig.project.ui.IUDIGView#getContext()
     */
    public IToolContext getContext() {
        return context;
    }

}
