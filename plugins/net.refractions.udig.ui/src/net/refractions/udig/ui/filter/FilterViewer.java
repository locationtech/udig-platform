package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.PageBook;
import org.opengis.filter.Filter;

/**
 * {@link IFilterViewer} allowing user to switch between implementations.
 * <p>
 * Note this implementation makes use of FilterViewerFactory when creating each viewer. We ask that
 * you remember the "viewerId" in dialog settings or IMento so the user is not forced to choose
 * which viewer is displayed each time. You can also use this facility as a hint when configuring
 * the viewer for use.
 * 
 * <pre>
 * FilterViewer viewer = new FilterViewer(composite, SWT.MULTI);
 * viewer.setViewerId(&quot;cql&quot;);
 * </pre>
 * 
 * You will need to consult the extension point for the list of valid viewerIds.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class FilterViewer extends IFilterViewer {
    /**
     * IFilterViewer currently displayed in {@link #pageBook}.
     */
    protected IFilterViewer delegate;

    private ISelectionChangedListener listener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            internalUpdate(delegate.getFilter());
            // The above internalUpdate will issue a fireSelectionChanged(event)
        }
    };

    /** Control used to display {@link #pageBook} and {@link #viewerCombo}. */
    Composite control;

    /**
     * PageBook acting as our control; used to switch between availabel implementations.
     */
    private PageBook pageBook;

    /**
     * Id of the viewer set by the user using the provided combo; may be supplied as an initial hint
     * or saved and restored using IMemento or DialogSettings in order to preserve user context.
     */
    private String viewerId;
    
    /**
     * Remember the style used so we can pass it on when we create a delegate
     */
    private int style;

    private SelectionListener menuListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            MenuItem menuItem = (MenuItem) e.widget;
            
            Object data = menuItem.getData();
            boolean selected = menuItem.getSelection();

            if( selected && data instanceof String ){
                showViewer( (String) data );
            }
            if( selected && data instanceof ContributionItem ){
                ContributionItem item = (ContributionItem) data;
                
                showViewer( (String) item.getId() );
            }
        }
    };

    // private Menu menu;

    /** Cache of viewers responsible filter display and input */
    private HashMap<String, IFilterViewer> pages;

    /** Placeholder displayed to request the user choose a viewer */
    private Label placeholder;

    /** Label offering the popup menu symbol */
    private Label config;

    public FilterViewer(Composite parent) {
        this( parent, SWT.DEFAULT );
    }
    /**
     * Creates an FilterViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation
     * <li>
     * <li>SWT.MULTI - A multi line text field</li>
     * <li>SWT.READ_ONLY - read only display of a filter</li>
     * </ul>
     * 
     * @param parent
     * @param style
     */
    public FilterViewer(Composite parent, int style) {
        control = new Composite(parent, SWT.NO_SCROLL);
        control.setLayout(new MigLayout("insets 0", "[fill][]", "[fill]"));
        
        pageBook = new PageBook(control, SWT.NO_SCROLL);
        pageBook.setLayoutData("cell 0 0,grow,width 200:100%:100%,height 18:75%:100%");
        
        placeholder = new Label( pageBook, SWT.SINGLE );
        placeholder.setText("Choose filter editor");
        
        delegate = new DefaultFilterViewer(pageBook, style);
        delegate.addSelectionChangedListener(listener);
        pageBook.showPage(delegate.getControl());
        
        this.pages = new HashMap<String,IFilterViewer>();
        pages.put("builder", delegate );
        
        config = new Label(control, SWT.SINGLE);
        config.setImage(JFaceResources.getImage(PopupDialog.POPUP_IMG_MENU));
        config.setLayoutData("cell 1 0,aligny top,height 16!, width 16!");
        
        createContextMenu( config );
//        menu = new Menu( config );
//        MenuItem builderMenuItem = new MenuItem(menu, SWT.RADIO);
//        builderMenuItem.setSelection(true);
//        builderMenuItem.setText("Builder");
//        builderMenuItem.setData("builder");
//        builderMenuItem.addSelectionListener(menuListener);
//
//        MenuItem cqlMenuItem = new MenuItem(menu, SWT.RADIO);
//        cqlMenuItem.setText("Constratin Query Language");
//        cqlMenuItem.setData("cql");
//        cqlMenuItem.addSelectionListener(menuListener);
//
        config.addMouseListener(new MouseAdapter() {
            public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                Menu menu = config.getMenu();
                if( menu != null ){
                    menu.setVisible(true);
                }
            }
        });
        this.style = style;
    }

    protected void showViewer(String newViewerId) {
        if( newViewerId == null ){
            // show place holder label or default to CQL
            newViewerId = "cql";
        }
        this.viewerId = newViewerId;
        
        // update the menu - (bad design) yes we could of just generated this on the fly
//        for (MenuItem item : menu.getItems()) {
//            if( item.getData().equals( viewerId ) ){
//                // this is the one we are selecting!
//                // we need to change to match
//                if (!item.getSelection()) {
//                    // select this one
//                    item.removeSelectionListener(menuListener);
//                    item.setSelection(true);
//                    item.addSelectionListener(menuListener);
//                }
//            } else {
//                if (item.getSelection()) { // unselect this one
//                    item.removeSelectionListener(menuListener);
//                    item.setSelection(false);
//                    item.addSelectionListener(menuListener);
//                }
//            }
//        }
        
        // update the pagebook if needed
        IFilterViewer viewer = getViewer( this.viewerId );
        
        String cqlText = null;
        if( delegate instanceof CQLFilterViewer ){
            CQLFilterViewer cqlViewer = (CQLFilterViewer) delegate;
            cqlText = cqlViewer.text.getText();
        }
        
        if( viewer == null ){
            pageBook.showPage(placeholder);
        }
        else {
            feedback(); // clear any warnings
            
            // configure viewer before display!
            FilterInput currentInput = getInput();
            Filter currentFilter = getFilter();
            
            viewer.setInput( currentInput );
            viewer.setFilter( currentFilter );
            viewer.refresh();
            
            // if available we can carry over the users text - typos and all
            if( cqlText != null && viewer instanceof CQLFilterViewer){
                CQLFilterViewer cqlViewer = (CQLFilterViewer) viewer;
                cqlViewer.text.setText( cqlText );
            }
            // show page and listen to it for changes
            pageBook.showPage(viewer.getControl());
            viewer.addSelectionChangedListener(listener);
        }
        if( delegate != null ){
            // showPage has already hidden delegate.getControl()
            // so now we need to unplug it
            delegate.removeSelectionChangedListener( listener );
            delegate.setInput(null);
        }
        delegate = viewer;
    }
    /**
     * Lookup viewer implementation for the provided viewerId.
     * <p>
     * The viewer will be created if needed; however it will not be hooked
     * up with {@link #setInput}, {@link #setFilter} and {@link #addSelectionChangedListener}
     * as this is done by {@link #showViewer(String)} when on an as needed basis.
     * 
     * @param viewerId
     * @return IFilterViewer or null if not available
     */
    private IFilterViewer getViewer(String lookupId) {
        IFilterViewer viewer = pages.get( lookupId );
        if( viewer != null ){
            // already constructed
            return viewer;
        }
        for( FilterViewerFactory factory : filterViewerFactoryList() ){
            if( factory.getId().equals( lookupId )){
                viewer = factory.createViewer( pageBook, this.style );
                pages.put( factory.getId(), viewer );
                
                return viewer;
            }
        }
        return null; // user has requested an unknown id - please display placeholder
    }

    public void setViewerId(String id) {
        this.viewerId = id;
    }

    public String getViewerId() {
        return viewerId;
    }

    /**
     * This is the widget used to display the Filter; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return control;
    }

    @Override
    public void refresh() {
        if (delegate != null) {
            delegate.refresh();
        }
    }

    @Override
    public void setInput(Object filterInput) {
        super.setInput(filterInput);
        if (delegate != null) {
            delegate.setInput(filterInput);
        }
    }

    /** Used to supply a filter for display or editing */
    @Override
    public void setFilter(Filter filter) {
        if (this.filter == filter) {
            return;
        }
        this.filter = filter;
        if (delegate != null && delegate.getControl() != null
                && !delegate.getControl().isDisposed()) {
            try {
                delegate.removeSelectionChangedListener(listener);
                delegate.setFilter(filter);
            } finally {
                delegate.addSelectionChangedListener(listener);
            }
        }
        fireSelectionChanged(new SelectionChangedEvent(FilterViewer.this, getSelection()));
    }
    
    private void createContextMenu( Control control ){
        final MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true); // we are going to generate
        
        menuManager.addMenuListener( new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                
                for( FilterViewerFactory factory : filterViewerFactory( getInput() ) ){
                    FilterViewerFactoryContributionItem contributionItem = new FilterViewerFactoryContributionItem(factory);
                    menuManager.add( contributionItem );
                }
            }
        });
        Menu menu = menuManager.createContextMenu( control );
        control.setMenu( menu );
    }
    
    class FilterViewerFactoryContributionItem extends ContributionItem {
        
        private FilterViewerFactory factory;
        
        FilterViewerFactoryContributionItem( FilterViewerFactory factory ){
            setId( factory.getId() );
            this.factory = factory;
        }
        @Override
        public void fill(Menu menu, int index) {
            MenuItem item = new MenuItem( menu, SWT.RADIO, index );
            
            item.setText( factory.getDisplayName() );
            item.setData( factory.getId() );
            item.setSelection( factory.getId().equals( viewerId ) );
            item.addSelectionListener( menuListener );
        }
    }
    //
    // Factory and Extension Point Support
    //
    /** Extension point ID */
    public static final String FILTER_VIEWER_EXTENSION = "net.refractions.udig.ui.filterViewer";

    private static List<FilterViewerFactory> filterViewerFactoryList;
    
    private static List<FilterViewerFactory> filterViewerFactory( FilterInput input ){
        return filterViewerFactoryList();
    }
    
    private synchronized static List<FilterViewerFactory> filterViewerFactoryList() {
        if (filterViewerFactoryList == null) {
            ArrayList<FilterViewerFactory> list = new ArrayList<FilterViewerFactory>();
            
            IExtensionRegistry registery = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registery.getExtensionPoint(FILTER_VIEWER_EXTENSION);
            
            IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
            for (IConfigurationElement configuration : configurationElements) {
                if ("filterViewer".equals(configuration.getName())) {
                    try {
                        FilterViewerFactory factory;
                        factory = (FilterViewerFactory) configuration.createExecutableExtension("class");
                        factory.init(configuration);

                        list.add(factory);
                    } catch (CoreException e) {
                        String pluginId = configuration.getContributor().getName();
                        IStatus status = new Status(IStatus.WARNING, pluginId, e.getMessage(), e);
                        UiPlugin.log(status);
                    }
                } else {
                    // skip as it is probably a expressionViewer element
                }
            }
            filterViewerFactoryList = Collections.unmodifiableList( list );
        }
        return filterViewerFactoryList;
    }
}