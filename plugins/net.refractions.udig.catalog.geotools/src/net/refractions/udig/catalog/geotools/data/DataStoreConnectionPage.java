/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.jdbc.JDBCDataStoreFactory;

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
//            ISharedImages images = CatalogUIPlugin.getDefault().getImages();
            String name = element.getClass().getSimpleName();
            
            if (element instanceof JDBCDataStoreFactory) {
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.DATABASE_OBJ);
            }
            if (element instanceof FileDataStoreFactorySpi) {
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.FEATURE_FILE_OBJ);
            }
            if (name.indexOf("WFSDataStoreFactory") != -1) {
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.WFS_OBJ);
            }
            if (name.indexOf("PropertyDataStoreFactory") != -1){
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.FEATURE_FILE_OBJ );
            }
            if (name.indexOf("PostgisDataStoreFactory")!=-1){
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.DATABASE_OBJ);
            }
            if (element instanceof DataStoreFactorySpi) {
                return CatalogUIPlugin.getDefault().getImage(ISharedImages.DATASTORE_OBJ );
            }
            return super.getImage(element);
        }
    }

    private LabelProvider labelProvider = new DataStoreLabelProvider();
    private TableViewer viewer;
    private Text filterText;
    private ViewerFilter filter = new ViewerFilter(){
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            DataAccessFactory factory = (DataAccessFactory) element;
            String search = filterText.getText();
            if (search == null || search.length() == 0 || search.equals(TYPE_FILTER_TEXT)) {
                return true; // it is all good
            }
            // promote to uppercase to ignore case when searching
            search = search.toUpperCase();
            if (factory.getDisplayName().toUpperCase().contains(search)
                    || factory.getDescription().toUpperCase().contains(search)) {
                // System.out.println("Search:<"+search+"> "+factory.getDisplayName());
                return true; // this one is okay
            }
            return false; // not included
        }
    };

    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            if (event.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!selection.isEmpty()) {
                    DataAccessFactory factory = (DataAccessFactory) selection.getFirstElement();
                    if (!factory.isAvailable()) {
                        setErrorMessage("This factory is not avaiable, usually indicating a missing JDBC driver, or ImageIO-EXT not being installed in your JRE.");
                        return;
                    }
                    setMessage(factory.getDescription(), IMessageProvider.INFORMATION);
                }
            }
            setErrorMessage(null);
        }
    };
    private IDoubleClickListener clicked = new IDoubleClickListener(){

        public void doubleClick( DoubleClickEvent event ) {
            IWizardContainer container = getContainer();
            if (container instanceof Dialog) {
                Dialog d = (Dialog) container;
                Button button = findButton(d.buttonBar, IDialogConstants.NEXT_ID);
                if (button != null)
                    button.notifyListeners(SWT.Selection, new Event());
            }
        }
        protected Button findButton(Control buttonBar, int buttonID) {
            if (buttonBar instanceof Composite) {
                Composite composite = (Composite) buttonBar;
                Control[] children = composite.getChildren();
                for (Control control : children) {
                    if (control instanceof Button) {
                        Button button = (Button) control;
                        if (((Integer) button.getData()).intValue() == buttonID)
                            return button;
                    } else if (control instanceof Composite) {
                        Button button = findButton(control, buttonID);
                        if (button != null)
                            return button;
                    }
                }
            }
            if (buttonBar instanceof Button) {
                Button button = (Button) buttonBar;
                if (((Integer) button.getData()).intValue() == buttonID)
                    return button;
            }

            return null;
        }
    };

    public DataAccessFactory getFactory() {
        if (viewer != null && !viewer.getControl().isDisposed()) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            if (selection.isEmpty()) {
                factory = null;
            } else {
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
        composite.setLayout(new MigLayout("", "[grow]", "[pref!]rel[pref!]rel[grow]"));
        setControl(composite);

        Label prompt = new Label(composite, SWT.HORIZONTAL | SWT.LEFT);
        prompt.setLayoutData("wrap");
        prompt.setText("Select an input source:");

        filterText = new Text(composite, SWT.SEARCH | SWT.SINGLE);
        filterText.setLayoutData("growx,wrap");
        filterText.setText(TYPE_FILTER_TEXT);
        filterText.setSelection(0, filterText.getCharCount());
        filterText.addKeyListener(new KeyListener(){
            public void keyReleased( KeyEvent e ) {
                viewer.refresh();
            }
            public void keyPressed( KeyEvent e ) {
            }
        });
        ScrolledComposite scroll = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.BORDER);
        scroll.setLayoutData("growx,growy");
        scroll.setAlwaysShowScrollBars(true);
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        scroll.setMinSize(300, 100);
        scroll.setSize(300,100);

        viewer = new TableViewer(scroll, SWT.SINGLE);
        scroll.setContent(viewer.getControl()); // scroll this thing!
        viewer.setLabelProvider(labelProvider);
        viewer.setContentProvider(ArrayContentProvider.getInstance());

        SortedSet<DataAccessFactory> sorted = new TreeSet<DataAccessFactory>(
                new Comparator<DataAccessFactory>(){
                    public int compare( DataAccessFactory factory1, DataAccessFactory factory2 ) {
                        return factory1.getDisplayName().compareTo(factory2.getDisplayName());
                    }
                });
        for( Iterator<DataAccessFactory> iter = DataAccessFinder.getAllDataStores(); iter.hasNext(); ) {
            DataAccessFactory entry = iter.next();
            String name = entry.getDisplayName();
//            if( !entry.isAvailable() ){
//                continue;
//            }
            if( name == null || name.indexOf("JNDI") != -1){
                continue;
            }
            sorted.add(entry);
        }
        viewer.setInput(sorted);
        viewer.setFilters(new ViewerFilter[]{filter});
        viewer.addSelectionChangedListener(listener);
        viewer.addDoubleClickListener(clicked);
    }

    @Override
    public boolean isPageComplete() {
        DataAccessFactory factory = getFactory();
        return factory != null && factory.isAvailable();
    }

}
