/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.search;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ICatalogInfo;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveChangeEvent.Type;
import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.catalog.ISearch;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.ui.ImageConstants;
import org.locationtech.udig.catalog.ui.CatalogTreeViewer;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ResolveContentProvider;
import org.locationtech.udig.catalog.ui.ResolveLabelProviderSimple;
import org.locationtech.udig.catalog.ui.ResolveTitlesDecorator;
import org.locationtech.udig.catalog.ui.StatusLineMessageBoardAdapter;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.SearchPart;
import org.locationtech.udig.ui.UDIGDragDropUtilities;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.PageBook;

import org.locationtech.jts.geom.Envelope;

/**
 * Search view is a distinct, simple implementation focused on Catalog searches.
 * <p>
 * We are unable to make use of the usual eclipse search facilities (ie
 * org.eclipse.search.searchPages) as this would cause our application to be dependent on
 * org.eclipse.core.resources - aka IResource. This would represent a signigicant jump in download
 * size etc...
 * </p>
 * <p>
 * Of Note:
 * <ul>
 * <li>At this time Search only works against the Local Catalog
 * <li>As more Catalogs are implemented they will be made available in a manner similar to existing
 * Filter specification.
 * </ul>
 * </p>
 */
public class SearchView extends SearchPart {
    /** ID for this view */
    public static final String VIEW_ID = "org.locationtech.udig.catalog.ui.search.searchView"; //$NON-NLS-1$

    public Text text;
    private Button bbox;
    private Label label;

    /** Cache for info used by details */
    private Info cache;

    private Text summary;

    private PageBook book;
    private Action refreshAction;

    /**
     * @param dialogSettings
     */
    public SearchView() {
        super(CatalogUIPlugin.getDefault().getDialogSettings());
    }

    static class Query {
        String text; // match against everything we can
        Envelope bbox; // latlong bbox
        List<ISearch> scope; // list of catalogs to search
    }

    @Override
    protected void setOrientation( Orientation orientation ) {
        super.setOrientation(orientation);

        if (splitter.getOrientation() == SWT.HORIZONTAL) {
            label.setText(Messages.SearchView_prompt); 
            //bbox.setText(Messages.SearchView_bbox);             
        } else {
            label.setText(""); //$NON-NLS-1$
            //bbox.setText(""); //$NON-NLS-1$
        }
        parent.layout();
    }
    @Override
    public void createPartControl( Composite aParent ) {
        label = new Label(aParent, SWT.NONE);
        label.setText(Messages.SearchView_prompt); 

        text = new Text(aParent, SWT.BORDER);
        text.setText(Messages.SearchView_default); 
        text.setEditable(true);
        text.addSelectionListener(new SelectionListener(){
            public void widgetDefaultSelected( SelectionEvent e ) {
                search(createQuery()); // search according to filter
            }
            public void widgetSelected( SelectionEvent e ) {
                quick(text.getText());
            }
        });

        // Create bbox button
//        bbox = new Button(aParent, SWT.CHECK);
//        bbox.setText(Messages.SearchView_bbox); 
//        bbox.setToolTipText(Messages.SearchView_bboxTooltip); 
//        bbox.setSelection(true);

        super.createPartControl(aParent);

        // Layout using Form Layout (+ indicates FormAttachment)
        // +
        // +label+text+bbox+
        // +
        // contents
        // +
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        aParent.setLayout(layout);

        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0);
        dLabel.top = new FormAttachment(text, 5, SWT.CENTER);
        label.setLayoutData(dLabel);

        FormData dText = new FormData(); // bind to top, label, bbox
        dText.top = new FormAttachment(1);
        dText.left = new FormAttachment(label, 5);
        dText.right = new FormAttachment(100);
        text.setLayoutData(dText);

        FormData dBbox = new FormData(); // text & right
        dBbox.right = new FormAttachment(100);
        dBbox.top = new FormAttachment(text, 0, SWT.CENTER);
        //bbox.setLayoutData(dBbox);

        FormData dsashForm = new FormData(100, 100); // text & bottom
        dsashForm.right = new FormAttachment(100); // bind to right of form
        dsashForm.left = new FormAttachment(0); // bind to left of form
        dsashForm.top = new FormAttachment(text, 2); // attach with 5 pixel offset
        dsashForm.bottom = new FormAttachment(100); // bind to bottom of form

        splitter.setWeights(new int[]{60, 40});
        splitter.setLayoutData(dsashForm);
        createContextMenu();
    }

    @Override
    protected void initDragAndDrop() {

        UDIGDragDropUtilities.addDragSupport(this.viewer.getControl(), viewer);
    }

    @Override
    protected Composite createDetails( SashForm parent ) {
        book = new PageBook(parent, SWT.NONE);
        summary = new Text(book, SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
        summary.setText(""); //$NON-NLS-1$
        Color white = new Color(parent.getDisplay(), 255, 255, 255);
        summary.setBackground(white);
        white.dispose();

        CatalogPlugin.addListener(sync);
        book.showPage(summary);
        return book;
    }

    IResolveChangeListener sync = new IResolveChangeListener(){
        public void changed( IResolveChangeEvent event ) {
            // changes made to the search results reported in the event
            final IResolve res = event.getResolve();
            Type type = event.getType();

            // we only care about changes to it
            if (type != IResolveChangeEvent.Type.POST_CHANGE) {
                return;
            }
            if (res == null) {
                return;
            }
            if (Display.getCurrent() == null) {
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                        refresh(res);
                    }
                });
            } else {
                refresh(res);
            }
        }
        /**
         * Attempts to resolve the URL from the IResolve parameter If cached data shares same URL
         * with changed data found, and the Info can be resolved,then we update the cached data and
         * redraw the infoDisplay text field.
         * 
         * @param res
         */
        private void refresh( IResolve res ) {
            URL resolveURL = res.getIdentifier();
            URL cachedURL = null;
            if (cache != null) {
                cachedURL = cache.getId();
            }

            if (resolveURL != null && resolveURL.equals(cachedURL)) {
                if (res.canResolve(ICatalogInfo.class)) {
                    try {
                        showInfo(new Info(cache.getId(), res.resolve(ICatalogInfo.class,
                                searchMonitor)));
                    } catch (IOException e) {
                        CatalogUIPlugin.log(null, e);
                    }
                }

                if (res.canResolve(IServiceInfo.class)) {
                    try {
                        showInfo(new Info(cache.getId(), res.resolve(IServiceInfo.class,
                                searchMonitor)));
                    } catch (IOException e) {
                        CatalogUIPlugin.log(null, e);
                    }
                }

                if (res.canResolve(IGeoResourceInfo.class)) {
                    try {
                        showInfo(new Info(cache.getId(), res.resolve(IGeoResourceInfo.class,
                                searchMonitor)));
                    } catch (IOException e) {
                        CatalogUIPlugin.log(null, e);
                    }
                }
            }

        }// end refresh
    };

    private boolean aoiFilter;

    private void createContextMenu() {
        final MenuManager contextMenu = new MenuManager();

        refreshAction = new Action(){
            public void run() {
                IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
                sel.getFirstElement();
                viewer.refresh();
            }
        };

        Messages.initAction(refreshAction, "action_refresh"); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(new Separator());

                refreshAction.setImageDescriptor(CatalogUIPlugin.getDefault().getImageDescriptor(ImageConstants.REFRESH_CO));

                // contextMenu.add(refreshAction);
                IWorkbenchWindow window = getSite().getWorkbenchWindow();
                IAction action = ActionFactory.IMPORT.create(window);
                contextMenu.add(action);
                contextMenu.add(new Separator());
                contextMenu.add(UiPlugin.getDefault().getOperationMenuFactory().getContextMenu(viewer.getSelection()));
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, viewer);

    }

    @Override
    protected StructuredViewer createViewer( Composite parent ) {
        CatalogTreeViewer catalogTreeViewer = new CatalogTreeViewer(parent, true);
        catalogTreeViewer.setMessageBoard(new StatusLineMessageBoardAdapter(getViewSite().getActionBars().getStatusLineManager()));

        return catalogTreeViewer;
    }
    /**
     * Construct a query based on the state of the user interface controls, and possibly workbecnh.
     * 
     * @return A catalog query
     */
    Query createQuery() {
        Query filter = new Query();
        filter.text = text.getText();
        filter.bbox = new Envelope();
        //if (bbox.getSelection()) {
        boolean aoiFilter2 = isAOIFilter();
        if (aoiFilter2) {
        	IAOIService aOIService = PlatformGIS.getAOIService();
        	try {
        		filter.bbox = aOIService.getExtent();
        	} catch (Throwable t) {
        		CatalogUIPlugin.log("Unable to create search:"+t, t); //$NON-NLS-1$
        	}
        }
        return filter;
    }

    @Override
    protected IBaseLabelProvider createLabelProvider() {
        ResolveLabelProviderSimple base = new ResolveLabelProviderSimple();
        return new DecoratingLabelProvider(base, new ResolveTitlesDecorator(base));
    }
    protected void showInfo( Info info ) {
        book.showPage(summary);
        if (cache != null) {
            if (info.getId().equals(cache.getId())) {
                return;
            }
        }
        cache = info;
        summary.setText(""); //$NON-NLS-1$

        URL url = cache.getId();
        String serverName = ""; //$NON-NLS-1$
        if (url != null) {
            serverName = url.getHost();
            // summary.append("Server: " + serverName + "\n\n");
        }

        if (cache.title != null && !(cache.title.equals(""))) { //$NON-NLS-1$
            summary.append(MessageFormat.format(Messages.SearchView_title, new Object[] {cache.title}) + "\n\n");  //$NON-NLS-1$
        }

        if (cache.name != null && !(cache.name.equals(""))) { //$NON-NLS-1$
            summary.append(MessageFormat.format(Messages.SearchView_name, new Object[] {cache.name, serverName}) + ")\n\n");  //$NON-NLS-1$
        } else {
            if (serverName != null && !serverName.equals("")) { //$NON-NLS-1$
                summary.append(MessageFormat.format(Messages.SearchView_server, new Object[] {serverName}) + "\n\n");  //$NON-NLS-1$
            }
        }

        if (cache.keys != null && cache.keys.length > 0) {
            summary.append(Messages.SearchView_keywords); 
            for( int i = 0; i < cache.keys.length; i++ ) {
                String keyword = cache.keys[i];
                keyword = keyword == null ? null : keyword.trim();

                if (keyword != null && !(keyword).equalsIgnoreCase("")) { //$NON-NLS-1$
                    if (i == cache.keys.length - 1) {
                        summary.append(keyword + "\n\n"); //$NON-NLS-1$
                    } else {
                        summary.append(keyword + ", "); //$NON-NLS-1$
                    }
                }
            }
        }

        if (cache.description != null && !(cache.description.equals(""))) { //$NON-NLS-1$
            summary.append(MessageFormat.format(Messages.SearchView_description, new Object[] {cache.description}));
        }
    }
    @Override
    protected void showDetail( Object selection ) {
        if (!(selection instanceof IResolve)) {
            return;
        }
        IResolve record = (IResolve) selection;
        if (record instanceof ICatalog) {
            ICatalog catalog = (ICatalog) record;
            try {
                ICatalogInfo info = catalog.getInfo(searchMonitor);
                showInfo(new Info(catalog.getIdentifier(), info));
            } catch (IOException e) {
                CatalogUIPlugin.log("No information for catalog", e); //$NON-NLS-1$
            }
        } else if (record instanceof IService) {
            IService service = (IService) record;
            IServiceInfo info;
            try {
                info = service.getInfo(searchMonitor);
                showInfo(new Info(service.getIdentifier(), info));
            } catch (IOException e) {
                CatalogUIPlugin.log("No information for service", e); //$NON-NLS-1$
            }

        } else if (record instanceof IGeoResource) {
            IGeoResource layer = (IGeoResource) record;
            try {
                IGeoResourceInfo info = layer.getInfo(searchMonitor);
                showInfo(new Info(layer.getIdentifier(), info));
            } catch (IOException e) {
                CatalogUIPlugin.log("No information for layer", e); //$NON-NLS-1$
            }
        }
    }

    protected IStructuredContentProvider createContentProvider() {
        return new ResolveContentProvider();
    }

    /** Status message ... */
    public void setStatus( String message ) {
        // change book to status and show message
    }
    /**
     * Search the catalog for text and update view contents
     * 
     * @param pattern
     */
    @Override
    protected void searchImplementation( Object filter, IProgressMonitor monitor, ResultSet results ) {
        Query query = (Query) filter;
        if (query == null)
            return ;

        if ((query.bbox == null || query.bbox.isNull())
                && (query.text == null || query.text.trim().length() == 0)) {
            return ; // no actual query
        }
        if (query.bbox == null) {
            query.bbox = new Envelope();
        }
        if (query.scope == null) {
            query.scope = Arrays.asList(CatalogPlugin.getDefault().getCatalogs());
        }
        monitor.beginTask(Messages.SearchView_searching, query.scope.size() * 3); 
        int work = 0;
        for( ISearch catalog : query.scope ) {
            String name = null;
            try {
                name = catalog.getInfo(null).getTitle();
                if (name == null && catalog.getIdentifier() != null)
                    name = "url: " + catalog.getIdentifier(); //$NON-NLS-1$
                if (name == null)
                    name = catalog.getClass().getSimpleName();
            } catch (Exception unknown) {
                name = catalog.getClass().getSimpleName();
            }
            List<IResolve> records = null;
            try {
                monitor.subTask(MessageFormat.format(Messages.SearchView_searching_for, new Object[] {query.text, name}));
                monitor.worked(++work);
                records = catalog.search(query.text, query.bbox, monitor);
                if (records != null && !records.isEmpty()) {
                    results.addAll(records);
                }
            } catch (Throwable t) {
                CatalogUIPlugin.log("Search for " + name + " failed", t); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
    
    
    public void setAOIFilter( boolean filter ) {
        aoiFilter = filter; 
        search(createQuery());
    }
    
    public boolean isAOIFilter() {
        return aoiFilter;
    }

}

class Info {
    public String title;
    public String name;
    public String description;
    public String[] keys;
    private URL id;

    public URL getId() {
        return id;
    }

    public Info( URL id, IServiceInfo info ) {
        title = info.getTitle();
        name = null;
        description = info.getDescription();
        keys = info.getKeywords().toArray(new String[0]);
        this.id = id;
    }

    public Info( URL id, ICatalogInfo info ) {
        title = info.getTitle();
        name = null;
        description = info.getDescription();
        keys = info.getKeywords();
        this.id = id;
    }
    public Info( URL id, IGeoResourceInfo info ) {
        title = info.getTitle();
        name = info.getName();
        description = info.getDescription();
        keys = info.getKeywords().toArray(new String[0]);
        this.id = id;
    }
}
