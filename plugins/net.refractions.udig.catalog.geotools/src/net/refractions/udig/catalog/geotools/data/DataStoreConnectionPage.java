package net.refractions.udig.catalog.geotools.data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.eclipse.ui.part.IPage;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;

import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC4NClob;

import net.miginfocom.layout.LC;
import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;

/**
 * Wizard page constructed based on datastore factory params.
 * <p>
 * This wizard page allows the user to choose a factory for use by the next page.
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DataStoreConnectionPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private static final String DEFAULT_PROMPT = "Choose a data store you wish to connect to.";
    private static final String TYPE_FILTER_TEXT = "type filter text";
    
    /** Currently selected factory */
    protected DataAccessFactory factory = null;
    
    public class DataStoreLabelProvider extends LabelProvider {
        @Override
        public String getText( Object element ) {
            if (element instanceof DataAccessFactory) {
                DataAccessFactory factory = (DataAccessFactory) element;
                return factory.getDisplayName();
            }
            return super.getText(element);
        }

        @Override
        public Image getImage( Object element ) {
            ISharedImages images = CatalogUIPlugin.getDefault().getImages();
            if (element instanceof JDBCDataStoreFactory) {
                return images.get(ISharedImages.DATABASE_OBJ);
            }
            if (element instanceof FileDataStoreFactorySpi) {
                return images.get(ISharedImages.FILE_OBJ);
            }
            if (element instanceof WFSDataStoreFactory) {
                return images.get(ISharedImages.WFS_OBJ);
            }
            return super.getImage(element);
        }
    }

    private LabelProvider labelProvider = new DataStoreLabelProvider();
    private ListViewer viewer;
    private Text filterText;
    private ViewerFilter filter = new ViewerFilter(){
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            DataAccessFactory factory = (DataAccessFactory) element;
            String search = filterText.getText();
            if( search == null || search.length()==0 || search.equals(TYPE_FILTER_TEXT)){
                return true; // it is all good
            }
            // promote to uppercase to ignore case when searching
            search = search.toUpperCase();
            if( factory.getDisplayName().toUpperCase().contains(search)||
                    factory.getDescription().toUpperCase().contains(search)){
                // System.out.println("Search:<"+search+"> "+factory.getDisplayName());
                return true; // this one is okay
            }
            return false; // not included
        }
    };
    
    private ISelectionChangedListener listener = new ISelectionChangedListener(){        
        public void selectionChanged( SelectionChangedEvent event ) {
            if( event.getSelection() instanceof IStructuredSelection){
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if( !selection.isEmpty() ){
                    DataAccessFactory factory = (DataAccessFactory) selection.getFirstElement();
                    if( !factory.isAvailable() ){
                        setErrorMessage( "This factory is not avaiable, usually indicating a missing JDBC driver, or ImageIO-EXT not being installed in your JRE.");
                        return;                        
                    }
                    setMessage(factory.getDescription(), IMessageProvider.INFORMATION);  
                }
            }
            setErrorMessage(null);
        }
    };

    public DataAccessFactory getFactory(){
        if( viewer != null && !viewer.getControl().isDisposed()){
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            if( selection.isEmpty() ) {
                factory = null;
            }
            else {
                factory = (DataAccessFactory) selection.getFirstElement();
            }
        }
        return factory;
    }
    
    public DataStoreConnectionPage() {
        super("DataStore");
        setTitle("Select");
        setDescription(DEFAULT_PROMPT);
    }

    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new MigLayout("","[grow]","[pref!]rel[pref!]rel[grow]"));
        setControl( composite );

        Label prompt = new Label(composite, SWT.HORIZONTAL|SWT.LEFT);
        prompt.setLayoutData("wrap");
        prompt.setText("Select an input source:");

        filterText = new Text(composite, SWT.SEARCH | SWT.SINGLE);
        filterText.setLayoutData("growx,wrap");
        filterText.setText(TYPE_FILTER_TEXT);
        filterText.setSelection(0, filterText.getCharCount());
        filterText.addKeyListener( new KeyListener(){            
            public void keyReleased( KeyEvent e ) {
                viewer.refresh();
            }
            public void keyPressed( KeyEvent e ) {
            }
        });
        ScrolledComposite scroll = new ScrolledComposite( composite, SWT.V_SCROLL|SWT.BORDER);
        scroll.setLayoutData("growx");
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        scroll.setMinHeight(100);
        viewer = new ListViewer(scroll, SWT.SINGLE);
        scroll.setContent( viewer.getControl()); // scroll this thing!
        viewer.setLabelProvider(labelProvider);
        viewer.setContentProvider(ArrayContentProvider.getInstance());

        SortedSet<DataAccessFactory> sorted = new TreeSet<DataAccessFactory>(
                new Comparator<DataAccessFactory>(){
                    public int compare( DataAccessFactory factory1, DataAccessFactory factory2 ) {
                        return factory1.getDisplayName().compareTo(factory2.getDisplayName());
                    }
                });
        for( Iterator<DataAccessFactory> iter = DataAccessFinder.getAllDataStores(); iter.hasNext(); ) {
            sorted.add(iter.next());
        }
        viewer.setInput(sorted);        
        viewer.setFilters(new ViewerFilter[]{ filter });
        viewer.addSelectionChangedListener(listener);
    }

    @Override
    public boolean isPageComplete() {
        DataAccessFactory factory = getFactory();
        return factory != null && factory.isAvailable();
    }
    
}
