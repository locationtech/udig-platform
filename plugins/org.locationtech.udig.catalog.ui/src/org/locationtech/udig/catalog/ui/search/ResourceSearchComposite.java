/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012 Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.ui.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ISearch;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ResolveContentProvider;
import org.locationtech.udig.catalog.ui.ResolveLabelProviderSimple;
import org.locationtech.udig.catalog.ui.ResolveTitlesDecorator;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Composite used to provide list of resource to be searched.
 * <p>
 * Intended for use in the AddLayer wizard, but packaged as a reusable composite.
 * <p>
 * When embedding this composite are interaction is provided using the ISelectionProvider
 * interface. Since the primary selection of interest is a GeoResource a helper method
 * has been provided for easy retrieval.
 * 
 * @author Levi Putna
 */
public class ResourceSearchComposite extends Composite implements ISelectionProvider {
    private Text text;
    private List<ISearch> catalogs;
    private TreeViewer treeViewer;
    private static final long DELAY = 500;

    /**
     * Listen for key press and issue a search.
     * <p>
     * For most keys a small delay is used (to allow the user to type more); for return a
     * search is issued immediately.
     */
    private KeyAdapter keyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            doSearch(DELAY); // search in a bit (incase they keep typing)
        }
    };
    private SelectionListener selectionListener = new SelectionAdapter() {
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            doSearch(0); // search now!
        }
    };

    // Single job used for searching in the background
    SearchJob search = new SearchJob();

    
    /**
     * Creates a searchable catalog resource viewer.
     * 
     * @param parent the parent that this composite will be added to
     * @param style the stile used on this composite {@see SWT}
     */
    public ResourceSearchComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
    
        Label hintLabel = new Label(this, SWT.NONE);
        hintLabel.setText("Start typing in search term");
    
        text = new Text(this, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH );
        text.addKeyListener(keyListener);
        text.addSelectionListener( selectionListener );
    
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    
        // Using SWT.SINGLE until we have "join" functionality
        treeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI ); //  SWT.CHECK ?
        
        Tree tree = treeViewer.getTree();
    
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    
        treeViewer.setContentProvider(createContentProvider());
        treeViewer.setLabelProvider(createLabelProvider());
        
        catalogs = Arrays.asList(CatalogPlugin.getDefault().getCatalogs());
    }

    public Control getControl(){
        return treeViewer != null ? treeViewer.getControl() : null;
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection) treeViewer.getSelection();
    }

    @Override
    public void setSelection(ISelection selection) {
        treeViewer.setSelection( selection );
    }

    /**
     * Adds a listener for selection changes in this selection provider. Has no effect if an
     * identical listener is already registered.
     * 
     * @param listener the listener to add
     */
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.addSelectionChangedListener(listener);
    }
    /**
     * Removes the given selection change listener from this selection provider. Has no effect if an
     * identical listener is not registered.
     * 
     * @param listener the listener to remove
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.removeSelectionChangedListener(listener);
    }

    public void setSearchText( final String search ){
        PlatformGIS.asyncInDisplayThread(
        new Runnable() {
            
            @Override
            public void run() {
                if( text != null && !text.isDisposed() ){
                    text.setText( search != null ? search : "" );
                    doSearch( 0 );                
                }
            }
        },true );
    }
    public String getSearchText(){
        if( text != null && !text.isDisposed() ){
            return text.getText();
        }
        return null;
    }
    
    /**
     * LabelProvider; override to take charge of your labels and icons.
     * 
     * @return LabelProvider for use with the viewer
     */
    protected IBaseLabelProvider createLabelProvider() {
        ResolveLabelProviderSimple base = new ResolveLabelProviderSimple();
        return new DecoratingLabelProvider(base, new ResolveTitlesDecorator(base));
    }
    
    /**
     * Default implementation will work for lists, please overide if you are into the whole tree
     * thing.
     * 
     * @return
     */
    protected IStructuredContentProvider createContentProvider() {
        return new ResolveContentProvider();
    }

    /**
     * Search the catalogs for the input text and create tree items for results. This is ran in a
     * background thread and will need to call {@link #doSearchCallback(List)} to update UI with
     * search results.
     * @param delay a time delay in milliseconds before the search should run
     */
    protected void doSearch(long delay) {
        if( search.getState() == Job.RUNNING ){
            search.cancel();
        }
        String searchText = text.getText();
        if( !searchText.isEmpty() ){
            search.setSearchText( searchText );
            search.schedule( delay );
        }
        else {
            List<IResolve> empty = new ArrayList<IResolve>();
            doSearchCallback( empty );
        }
    }
    
    /**
     * {@link #doSearch()} callback to update the UI.
     * 
     * @param resolves
     */
    protected void doSearchCallback(final List<IResolve> resolves) {
        reviewResults( resolves );
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                treeViewer.setInput(resolves);
            }
        });
    }
    protected void reviewResults(List<IResolve> resolves) {
        // do nothing
    }
    /**
     * Search catalogs in a separate job.
     * 
     * @author Jody Garnett
     */
    class SearchJob extends Job {
        
        private String searchText;
        private ReferencedEnvelope bounds = new ReferencedEnvelope();
        
        public SearchJob(){
            super("Search Catalog"); //$NON-NLS-1$
        }
        
        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

        public ReferencedEnvelope getBounds() {
            return bounds;
        }

        public void setBounds(ReferencedEnvelope bounds) {
            this.bounds = bounds;
        }

        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            if( monitor == null ) monitor = new NullProgressMonitor();
            
            try {
                monitor.beginTask( "Seaching for "+searchText, catalogs.size()*100 );
                final List<IResolve> resolves = new CopyOnWriteArrayList<IResolve>();
                for (ISearch search : catalogs) {
                    try {
                        monitor.subTask("Search "+search.getTitle() +" for "+searchText );
                        List<IResolve> searchResults = search.search(searchText, bounds, null);
                        monitor.worked(10);
                        IProgressMonitor searchMonitor = new SubProgressMonitor( monitor,10, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK );
                        
                        for (IResolve member : searchResults) {
                            if (member.canResolve(FeatureSource.class)) {
                                resolves.add(member);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                doSearchCallback(resolves);
                return Status.OK_STATUS;
            } catch (Exception e) {
                String message = "Unable to complete search for '"+searchText+"':"+e;
                Status failure = new Status( IStatus.ERROR, CatalogUIPlugin.ID, message, e );
                return failure;
            } finally {
                monitor.done();
            }
        }
    }
    
}
