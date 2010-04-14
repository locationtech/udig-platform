package net.refractions.udig.feature.editor;

import net.refractions.udig.feature.editor.internal.Messages;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IFeatureSite;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageBook;
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
public class FeatureView extends AbstractPageBookView<ILayer> {
    public static final String ID = "net.refractions.udig.feature.editor.featureView";
    
    private IFeatureSite context;
    private SimpleFeature current;
    
    @Override
    protected IPage createDefaultPage( PageBook book ) {
        /*
         * MessagePage page = new MessagePage(); initPage(page); page.createControl(book);
         * page.setMessage("Please select a feature"); //$NON-NLS-1$ return page;
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
        if (currentPage instanceof PropertySheetPage) {
            PropertySheetPage sheet = (PropertySheetPage) currentPage;
            sheet.selectionChanged(null, selection);
        }
    }

    @Override
    protected PageRec<ILayer> doCreatePage( ILayer part ) {
        // Try to get a IMap
        IMap map = part.getMap();
        if (map != null && map instanceof IMap) {
            IEditManager editManager = map.getEditManager();

            IPage page = (IPage) new FeaturePage(editManager);
            page.createControl(getPageBook());
            initPage((IPageBookViewPage) page);
            return new PageRec<ILayer>(part, page);
        }
        // Use the default page by returning null
        return null;
    }

    @Override
    protected void doDestroyPage( ILayer part, PageRec<ILayer> pageRecord ) {

        pageRecord.page.dispose();
    }

    /**
     * Will grab the active Map's selected layer, if available as the initial bootstrap part for our
     * feature view.
     */
    protected ILayer getBootstrapTarget() {
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null)
            return null;

        ILayer selectedLayer = map.getEditManager().getSelectedLayer();

        return selectedLayer;
    }

    /**
     * Only consider the selectedLayer important enough to force a refresh.
     */
    protected boolean isImportant( ILayer layer ) {
        if (layer.getMap().getEditManager().getSelectedLayer() == layer) {
            return true;
        }
        return layer.isVisible();
    }

    public void partActivated(IWorkbenchPart part) {
        
    };
    IAdaptable defaultSource = new IAdaptable(){
        @SuppressWarnings("unchecked")
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

    /**
     * @see net.refractions.udig.project.ui.IUDIGView#getContext()
     */
    public IFeatureSite getContext() {
        return context;
    }



}
