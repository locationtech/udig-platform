package net.refractions.udig.feature.editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.core.Util;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

/**
 * Create your own PageBookView.
 * <p>
 * This is an alternative to the Eclipse PageBookView that lets you choose (and cache) your pages
 * based on other criteria then workbench parts.
 * </p>
 * In its easiest implementation you can switch between a message page and a page working on a
 * specific kind of content (such as the last selected IMap, or the currently selected EditFeature).
 * <p>
 * This class still uses IPageBookViewPage, it is just that you get a chance to define the internal
 * cache yourself.
 * <p>
 * *
 * <p>
 * The default implementation tracks both the workbench selection and the workbench parts. And does
 * its best to convert them to a useful target for use by the page book view.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public abstract class AbstractPageBookView<K> extends ViewPart implements IPartListener2 {
    /**
     * The pagebook control, or <code>null</code> if not initialized.
     */
    private PageBook book;

    /**
     * The page record for the default page (usually a MessagePage)
     */
    private PageRec<K> defaultPageRec;

    /**
     * Map from parts to part records (key type: <code>IWorkbenchPart</code>; value type:
     * <code>PartRec</code>).
     */
    private Map<K, PageRec<K>> mapPartToRec = new HashMap<K, PageRec<K>>();

    /**
     * Map from pages to view sites Note that view sites were not added to page recs to avoid
     * breaking binary compatibility with previous builds
     */
    // private Map mapPageToSite = new HashMap();

    /**
     * Map from pages to the number of pageRecs actively associated with a page.
     */
    // private Map mapPageToNumRecs = new HashMap();

    /**
     * The page rec which provided the current page or <code>null</code>
     */
    private PageRec<K> activeRec;

    /**
     * If the part is hidden (usually an editor) then store it so we can continue to track it when
     * it becomes visible.
     */
    private IWorkbenchPart hiddenPart = null;

    /**
     * The action bar property listener.
     */
    private IPropertyChangeListener actionBarPropListener = new IPropertyChangeListener(){
        public void propertyChange( PropertyChangeEvent event ) {
            if (event.getProperty().equals(SubActionBars.P_ACTION_HANDLERS) && activeRec != null
                    && event.getSource() == activeRec.subActionBars) {
                refreshGlobalActionHandlers();
            }
        }
    };

    /**
     * Selection change listener to listen for page selection changes
     */
    private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            pageSelectionChanged(event);
        }
    };

    /**
     * Selection change listener to listen for page selection changes
     */
    private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            postSelectionChanged(event);
        }
    };

    private boolean viewInPage = true;

    private IPerspectiveListener perspectiveListener = new IPerspectiveListener(){
        public void perspectiveChanged( IWorkbenchPage page, IPerspectiveDescriptor perspective,
                String changeId ) {
        }
        // fix for bug 109245 and 69098 - fake a partActivated when the perpsective is switched
        public void perspectiveActivated( IWorkbenchPage page, IPerspectiveDescriptor perspective ) {
            viewInPage = page.findViewReference(getViewSite().getId()) != null;
            // getBootstrapPart could return null; but isImportant() can handle null
            activated(getBootstrapTarget());
        }
    };

    /**
     * Selection provider for this view's site
     */
    private SelectionProvider selectionProvider = new SelectionProvider();

    /**
     * A data structure used to store the information about a single page within a pagebook view.
     */
    protected static class PageRec<T> {

        /**
         * The target.
         */
        protected T target;

        /**
         * The page.
         */
        protected IPage page;

        /**
         * The page's action bars
         */
        protected SubActionBars subActionBars;

        /**
         * The page's site, we are gathering everything up into a single PageRec here (PageBookView
         * had separate maps).
         */
        protected IPageSite pageSite;

        /**
         * Creates a new page record initialized to the given part and page.
         * 
         * @param part
         * @param page
         */
        public PageRec( T target, IPage page ) {
            this.target = target;
            this.page = page;
        }

        public IPage getPage() {
            return page;
        }

        public T getTarget() {
            return target;
        }

        public SubActionBars getActionBars() {
            return subActionBars;
        }

        /**
         * Disposes of this page record by <code>null</code>ing its fields.
         */
        public void dispose() {
            target = null;
            page = null;
        }
    }

    private static class SelectionManager extends EventManager {
        /**
         * @param listener listen
         */
        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
            addListenerObject(listener);
        }

        /**
         * @param listener listen
         */
        public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
            removeListenerObject(listener);
        }

        /**
         * @param event the event
         */
        public void selectionChanged( final SelectionChangedEvent event ) {
            // pass on the notification to listeners
            Object[] listeners = getListeners();
            for( int i = 0; i < listeners.length; ++i ) {
                final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
                Platform.run(new SafeRunnable(){
                    public void run() {
                        l.selectionChanged(event);
                    }
                });
            }
        }

    }

    /**
     * A selection provider/listener for this view.
     * <p>
     * It is a selection provider for this view's site.
     */
    protected class SelectionProvider implements IPostSelectionProvider {

        private SelectionManager selectionListeners = new SelectionManager();

        private SelectionManager postSelectionListeners = new SelectionManager();

        /*
         * (non-Javadoc) Method declared on ISelectionProvider.
         */
        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
            selectionListeners.addSelectionChangedListener(listener);
        }

        /*
         * (non-Javadoc) Method declared on ISelectionProvider.
         */
        public ISelection getSelection() {
            // get the selection provider from the current page
            IPage currentPage = getCurrentPage();
            // during workbench startup we may be in a state when
            // there is no current page
            if (currentPage == null) {
                return StructuredSelection.EMPTY;
            }
            IPageSite site = getPageSite(currentPage);
            if (site == null) {
                return StructuredSelection.EMPTY;
            }
            ISelectionProvider selProvider = site.getSelectionProvider();
            if (selProvider != null) {
                return selProvider.getSelection();
            }
            return StructuredSelection.EMPTY;
        }

        /*
         * (non-Javadoc) Method declared on ISelectionProvider.
         */
        public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
            selectionListeners.removeSelectionChangedListener(listener);
        }

        /**
         * The selection has changed. Process the event, notifying selection listeners and post
         * selection listeners.
         * 
         * @param event the change
         */
        public void selectionChanged( final SelectionChangedEvent event ) {
            selectionListeners.selectionChanged(event);
        }

        /**
         * The selection has changed, so notify any post-selection listeners.
         * 
         * @param event the change
         */
        public void postSelectionChanged( final SelectionChangedEvent event ) {
            postSelectionListeners.selectionChanged(event);
        }

        /*
         * (non-Javadoc) Method declared on ISelectionProvider.
         */
        public void setSelection( ISelection selection ) {
            // get the selection provider from the current page
            IPage currentPage = getCurrentPage();
            // during workbench startup we may be in a state when
            // there is no current page
            if (currentPage == null) {
                return;
            }
            IPageSite site = getPageSite(currentPage);
            if (site == null) {
                return;
            }
            ISelectionProvider selProvider = site.getSelectionProvider();
            // and set its selection
            if (selProvider != null) {
                selProvider.setSelection(selection);
            }
        }

        /*
         * (non-Javadoc)
         * @see
         * org.eclipse.jface.viewers.IPostSelectionProvider#addPostSelectionChangedListener(org.
         * eclipse.jface.viewers.ISelectionChangedListener)
         */
        public void addPostSelectionChangedListener( ISelectionChangedListener listener ) {
            postSelectionListeners.addSelectionChangedListener(listener);
        }

        /*
         * (non-Javadoc)
         * @see
         * org.eclipse.jface.viewers.IPostSelectionProvider#removePostSelectionChangedListener(org
         * .eclipse.jface.viewers.ISelectionChangedListener)
         */
        public void removePostSelectionChangedListener( ISelectionChangedListener listener ) {
            postSelectionListeners.removeSelectionChangedListener(listener);
        }
    }

    /**
     * Creates a new pagebook view.
     */
    protected AbstractPageBookView() {
        super();
    }

    /**
     * Creates and returns the default page for this view.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * <p>
     * Subclasses must call initPage with the new page (if it is an <code>IPageBookViewPage</code>)
     * before calling createControl on the page.
     * </p>
     * 
     * @param book the pagebook control
     * @return the default page
     */
    protected abstract IPage createDefaultPage( PageBook book );

    /**
     * Creates a page for a given part. Adds it to the pagebook but does not show it.
     * 
     * @param part The part we are making a page for.
     * @return IWorkbenchPart
     */
    private PageRec<K> createPage( K part ) {
        PageRec<K> rec = doCreatePage(part);
        if (rec != null) {
            mapPartToRec.put(part, rec);
            preparePage(rec);
        }
        return rec;
    }

    /**
     * Prepares the page in the given page rec for use in this view.
     * 
     * @param rec
     */
    private void preparePage( PageRec<K> rec ) {
        IPageSite site = null;
        // Integer count;

        if (!doesPageExist(rec.page)) {
            if (rec.page instanceof IPageBookViewPage) {
                site = ((IPageBookViewPage) rec.page).getSite();
            }
            if (site == null) {
                // We will create a site for our use
                site = new PageSite(getViewSite());
            }
            // mapPageToSite.put(rec.page, site);
            rec.pageSite = site;

            rec.subActionBars = (SubActionBars) site.getActionBars();
            rec.subActionBars.addPropertyChangeListener(actionBarPropListener);

            // for backward compability with IPage
            rec.page.setActionBars(rec.subActionBars);

            // count = new Integer(0);
        } else {
            site = (IPageSite) rec.pageSite;
            rec.subActionBars = (SubActionBars) site.getActionBars();
            // count = ((Integer) mapPageToNumRecs.get(rec.page));
        }
        // mapPageToNumRecs.put(rec.page, new Integer(count.intValue() + 1));
    }

    /**
     * Initializes the given page with a page site.
     * <p>
     * Subclasses should call this method after the page is created but before creating its
     * controls.
     * </p>
     * <p>
     * Subclasses may override
     * </p>
     * 
     * @param page The page to initialize
     */
    protected void initPage( IPageBookViewPage page ) {
        try {
            page.init(new PageSite(getViewSite()));
        } catch (PartInitException e) {
            UiPlugin.log(getClass(), "initPage", e); //$NON-NLS-1$
        }
    }

    /**
     * The <code>PageBookView</code> implementation of this <code>IWorkbenchPart</code> method
     * creates a <code>PageBook</code> control with its default page showing. Subclasses may extend.
     */
    public void createPartControl( Composite parent ) {

        // Create the page book.
        book = new PageBook(parent, SWT.NONE);

        // Create the default page rec.
        IPage defaultPage = createDefaultPage(book);
        defaultPageRec = new PageRec<K>(null, defaultPage);
        preparePage(defaultPageRec);

        // Show the default page
        showPageRec(defaultPageRec);

        // Listen to part activation events.
        getSite().getPage().addPartListener(this);
        showBootstrapPart();

        getSite().getPage().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
    }

    /**
     * The <code>PageBookView</code> implementation of this <code>IWorkbenchPart</code> method
     * cleans up all the pages. Subclasses may extend.
     */
    @SuppressWarnings("unchecked")
    public void dispose() {
        getSite().getPage().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);

        // stop listening to part activation
        getSite().getPage().removePartListener(this);

        // Deref all of the pages.
        activeRec = null;
        if (defaultPageRec != null) {
            // check for null since the default page may not have
            // been created (ex. perspective never visible)
            defaultPageRec.page.dispose();
            defaultPageRec = null;
        }
        Map<K, PageRec<K>> clone = (Map<K, PageRec<K>>) ((HashMap<K, PageRec<K>>) mapPartToRec).clone();
        Iterator<PageRec<K>> itr = clone.values().iterator();
        while( itr.hasNext() ) {
            PageRec<K> rec = itr.next();
            removePage(rec);
        }

        // Run super.
        super.dispose();
    }

    /**
     * Creates a new page in the pagebook for a particular part. This page will be made visible
     * whenever the part is active, and will be destroyed with a call to <code>doDestroyPage</code>.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * <p>
     * Subclasses must call initPage with the new page (if it is an <code>IPageBookViewPage</code>)
     * before calling createControl on the page.
     * </p>
     * 
     * @param part the input part
     * @return the record describing a new page for this view
     * @see #doDestroyPage
     */
    protected abstract PageRec<K> doCreatePage( K part );

    /**
     * Destroys a page in the pagebook for a particular part. This page was returned as a result
     * from <code>doCreatePage</code>.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @param part the input part
     * @param pageRecord a page record for the part
     * @see #doCreatePage
     */
    protected abstract void doDestroyPage( K part, PageRec<K> pageRecord );

    /**
     * Returns true if the page has already been created.
     * 
     * @param page the page to test
     * @return true if this page has already been created.
     */
    protected boolean doesPageExist( IPage page ) {
        // can consider keeping a separate map to track this
        for( PageRec<K> rec : mapPartToRec.values() ) {
            if (rec.page == page) {
                return true;
            }
        }
        return false;
    }

    /**
     * The <code>PageBookView</code> implementation of this <code>IAdaptable</code> method delegates
     * to the current page, if it implements <code>IAdaptable</code>.
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class key ) {
        // delegate to the current page, if supported
        IPage page = getCurrentPage();
        Object adapter = Util.getAdapter(page, key);
        if (adapter != null) {
            return adapter;
        }
        // if the page did not find the adapter, look for one provided by
        // this view before delegating to super.
        adapter = getViewAdapter(key);
        if (adapter != null) {
            return adapter;
        }
        // delegate to super
        return super.getAdapter(key);
    }

    /**
     * Returns an adapter of the specified type, as provided by this view (not the current page), or
     * <code>null</code> if this view does not provide an adapter of the specified adapter.
     * <p>
     * The default implementation returns <code>null</code>. Subclasses may override.
     * </p>
     * 
     * @param adapter the adapter class to look up
     * @return a object castable to the given class, or <code>null</code> if this object does not
     *         have an adapter for the given class
     * @since 3.2
     */
    @SuppressWarnings("unchecked")
    protected Object getViewAdapter( Class adapter ) {
        return null;
    }

    /**
     * Returns the active, target object for when the view is first created.
     * <p>
     * Usually this involves reviewing the workbench state, current selection, current workbench
     * part or active editor and searching for something that adapts to the kind of information
     * desired.
     * <p>
     * Implementors of this method should return a target if found, or null if not available. The
     * default implementation checks the current active part with getCurrent( part ) to see if it is
     * something we care about.
     * </p>
     * 
     * @return the active target, or <code>null</code> if none
     */
    protected K getBootstrapTarget() {
        IWorkbenchPage page = getSite().getPage();
        if (page == null) {
            return null;
        }
        IWorkbenchPart part = page.getActivePart();
        if (part == null) {
            return null;
        }
        K target = getCurrent(part);
        return target;
    }

    /**
     * Check if the indicated workbench part has anything to do with the content we are displaying.
     * <p>
     * This method is used to manage our targets in response to IPartListener events.
     * <p>
     * The default implementation returns null indicating that there is no relationship between
     * workbench parts and our target.
     * 
     * @param part WorkbenchPart
     * @return target if available, or null otherwise.
     */
    protected K getCurrent( IWorkbenchPart part ) {
        return null;
    }
    protected K getCurrent( IWorkbenchPartReference partRef ) {
        IWorkbenchPart part = partRef.getPart(false);
        if (part == null)
            return null;

        return getCurrent(part);
    }

    /**
     * Returns the part which contributed the current page to this view.
     * 
     * @return the part which contributed the current page or <code>null</code> if no part
     *         contributed the current page
     */
    protected K getCurrentContributingPart() {
        if (activeRec == null) {
            return null;
        }
        return activeRec.getTarget();
    }

    /**
     * Returns the currently visible page for this view or <code>null</code> if no page is currently
     * visible.
     * 
     * @return the currently visible page
     */
    public IPage getCurrentPage() {
        if (activeRec == null) {
            return null;
        }
        return activeRec.page;
    }

    /**
     * Returns the view site for the given page of this view.
     * 
     * @param page the page
     * @return the corresponding site, or <code>null</code> if not found
     */
    protected IPageSite getPageSite( IPage page ) {
        for( PageRec<K> rec : this.mapPartToRec.values() ) {
            if (page == rec.page) {
                return rec.pageSite;
            }
        }
        return null;
    }

    /**
     * Returns the default page for this view.
     * 
     * @return the default page
     */
    public IPage getDefaultPage() {
        return defaultPageRec.page;
    }

    /**
     * Returns the pagebook control for this view.
     * 
     * @return the pagebook control, or <code>null</code> if not initialized
     */
    protected PageBook getPageBook() {
        return book;
    }

    /**
     * Returns the page record for the given part.
     * 
     * @param part the part
     * @return the corresponding page record, or <code>null</code> if not found
     */
    protected PageRec<K> getPageRec( K part ) {
        return (PageRec<K>) mapPartToRec.get(part);
    }

    /**
     * Returns the page record for the given page of this view.
     * 
     * @param page the page
     * @return the corresponding page record, or <code>null</code> if not found
     */
    protected PageRec<K> getPageRec( IPage page ) {
        Iterator<PageRec<K>> itr = mapPartToRec.values().iterator();
        while( itr.hasNext() ) {
            PageRec<K> rec = itr.next();
            if (rec.page == page) {
                return rec;
            }
        }
        return null;
    }

    /**
     * Returns whether the given part should be added to this view.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @param part the input part
     * @return <code>true</code> if the part is relevant, and <code>false</code> otherwise
     */
    protected abstract boolean isImportant( K part );

    /*
     * (non-Javadoc) Method declared on IViewPart.
     */
    public void init( IViewSite site ) throws PartInitException {
        site.setSelectionProvider(selectionProvider);
        super.init(site);
    }

    /**
     * The <code>PageBookView</code> implementation of this <code>IPartListener</code> method shows
     * the page when the given part is activated. Subclasses may extend.
     */
    public void activated( K part ) {
        // Is this important? If not just return.
        if (!isImportant(part)) {
            return;
        }
        hiddenPart = null;

        // Create a page for the part.
        PageRec<K> rec = getPageRec((K) part);
        if (rec == null) {
            rec = createPage((K) part);
        }

        // Show the page.
        if (rec != null) {
            showPageRec(rec);
        } else {
            showPageRec(defaultPageRec);
        }
    }

    public void partActivated( IWorkbenchPartReference partRef ) {
        IWorkbenchPart part = partRef.getPart(false);
        partActivated(part);
    }
    public void partActivated( IWorkbenchPart part ) {
        if (part != null) {
            K target = getCurrent(part);
            if (target != null) {
                activated(target);
            }
        }
    }

    /**
     * Default implementation does nothing
     * 
     * @param target
     */
    public void broughtToTop( K target ) {

    }

    /**
     * Calls broughtToTop if we can determine the target for the provided workbench part.
     */
    public void partBroughtToTop( IWorkbenchPart part ) {
        K target = getCurrent(part);
        if (target != null) {
            activated(target);
        }
    }

    public void partClosed( IWorkbenchPart part ) {
        if (part == null)
            return;
        K target = getCurrent(part);
        if (target != null) {
            closed(target);
        }
    }

    public void partClosed( IWorkbenchPartReference partRef ) {
        IWorkbenchPart part = partRef.getPart(false);
        if (part != null) {
            K target = getCurrent(part);
            if (target != null) {
                closed(target);
            }
        }
    }

    /**
     * Deal with closing of the active target. Subclasses may extend.
     */
    public void closed( K part ) {
        // Update the active part.
        if (activeRec != null && activeRec.getTarget() == part) {
            showPageRec(defaultPageRec);
        }

        // Find and remove the part page.
        PageRec<K> rec = getPageRec(part);
        if (rec != null) {
            removePage(rec);
        }
        if (part == hiddenPart) {
            hiddenPart = null;
        }
    }

    /**
     * Subclasses may extend in order to "stop listening" to the provided target.
     */
    public void deactivated( K target ) {
        // Do nothing.
    }
    /**
     * Calls deactivated if possible from the provided workbench part.
     * 
     * @param part
     */
    public void partDeactivated( IWorkbenchPart part ) {
        K target = getCurrent(part);
        if (target != null) {
            deactivated(target);
        }
    }
    /**
     * Called when an opened workbench part can provided a target.
     * 
     * @param target
     */
    public void opened( K target ) {
    }

    /**
     * Call to opened if possible
     */
    public void partOpened( IWorkbenchPart part ) {
        K target = getCurrent(part);
        if (target != null) {
            opened(target);
        }
    }

    /**
     * Refreshes the global actions for the active page.
     */
    @SuppressWarnings("unchecked")
    private void refreshGlobalActionHandlers() {
        // Clear old actions.
        IActionBars bars = getViewSite().getActionBars();
        bars.clearGlobalActionHandlers();

        // Set new actions.
        Map newActionHandlers = activeRec.subActionBars.getGlobalActionHandlers();
        if (newActionHandlers != null) {
            Set keys = newActionHandlers.entrySet();
            Iterator iter = keys.iterator();
            while( iter.hasNext() ) {
                Map.Entry entry = (Map.Entry) iter.next();
                bars.setGlobalActionHandler((String) entry.getKey(), (IAction) entry.getValue());
            }
        }
    }

    /**
     * Removes a page record. If it is the last reference to the page dispose of it - otherwise just
     * decrement the reference count.
     * 
     * @param rec
     */
    private void removePage( PageRec<K> rec ) {
        // remove from cache
        mapPartToRec.remove(rec.getTarget());

        IPageSite site = rec.pageSite;

        Control control = rec.page.getControl();
        if (control != null && !control.isDisposed()) {
            // Dispose the page's control so pages don't have to do this in
            // their
            // dispose method.
            // The page's control is a child of this view's control so if
            // this view
            // is closed, the page's control will already be disposed.
            control.dispose();
        }

        // free the page
        doDestroyPage(rec.getTarget(), rec);

        if (rec.subActionBars != null) {
            rec.subActionBars.dispose();
        }

        if (site instanceof PageSite) {
            try {
                Method dispose = PageSite.class.getMethod("dispose", new Class[0]);
                dispose.invoke(site, new Object[0]);
            } catch (IllegalArgumentException e) {
                if (UiPlugin.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (UiPlugin.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                if (UiPlugin.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                if (UiPlugin.getDefault().isDebugging()) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc) Method declared on IWorkbenchPart.
     */
    public void setFocus() {
        // first set focus on the page book, in case the page
        // doesn't properly handle setFocus
        if (book != null) {
            book.setFocus();
        }
        // then set focus on the page, if any
        if (activeRec != null) {
            activeRec.page.setFocus();
        }
    }

    /**
     * Handle page selection changes.
     * 
     * @param event
     */
    private void pageSelectionChanged( SelectionChangedEvent event ) {
        // forward this change from a page to our site's selection provider
        SelectionProvider provider = (SelectionProvider) getSite().getSelectionProvider();
        if (provider != null) {
            provider.selectionChanged(event);
        }
    }

    /**
     * Handle page selection changes.
     * 
     * @param event
     */
    private void postSelectionChanged( SelectionChangedEvent event ) {
        // forward this change from a page to our site's selection provider
        SelectionProvider provider = (SelectionProvider) getSite().getSelectionProvider();
        if (provider != null) {
            provider.postSelectionChanged(event);
        }
    }

    /**
     * Shows a page for the active workbench part.
     */
    private void showBootstrapPart() {
        K part = getBootstrapTarget();
        if (part != null) {
            activated(part);
        }
    }

    /**
     * Shows page contained in the given page record in this view. The page record must be one from
     * this pagebook view.
     * <p>
     * The <code>PageBookView</code> implementation of this method asks the pagebook control to show
     * the given page's control, and records that the given page is now current. Subclasses may
     * extend.
     * </p>
     * 
     * @param pageRec the page record containing the page to show
     */
    protected void showPageRec( PageRec<K> pageRec ) {
        // If already showing do nothing
        if (activeRec == pageRec) {
            return;
        }
        // If the page is the same, just set activeRec to pageRec
        if (activeRec != null && pageRec != null && activeRec.page == pageRec.page) {
            activeRec = pageRec;
            return;
        }

        // Hide old page.
        if (activeRec != null) {
            IPageSite pageSite = activeRec.pageSite;

            activeRec.subActionBars.deactivate();

            // deactivate the nested services
            if (pageSite instanceof PageSite) {
                ((PageSite) pageSite).deactivate();
            }
            // remove our selection listener
            ISelectionProvider provider = pageSite.getSelectionProvider();
            if (provider != null) {
                provider.removeSelectionChangedListener(selectionChangedListener);
                if (provider instanceof IPostSelectionProvider) {
                    ((IPostSelectionProvider) provider)
                            .removePostSelectionChangedListener(postSelectionListener);
                }
            }
        }

        // Show new page.
        activeRec = pageRec;
        Control pageControl = activeRec.page.getControl();
        if (pageControl != null && !pageControl.isDisposed()) {
            IPageSite pageSite = activeRec.pageSite;

            // Verify that the page control is not disposed
            // If we are closing, it may have already been disposed
            book.showPage(pageControl);
            activeRec.subActionBars.activate();
            refreshGlobalActionHandlers();

            // activate the nested services
            if (pageSite instanceof PageSite) {
                ((PageSite) pageSite).activate();
            }

            // add our selection listener
            ISelectionProvider provider = pageSite.getSelectionProvider();
            if (provider != null) {
                provider.addSelectionChangedListener(selectionChangedListener);
                if (provider instanceof IPostSelectionProvider) {
                    ((IPostSelectionProvider) provider)
                            .addPostSelectionChangedListener(postSelectionListener);
                }
            }
            // Update action bars.
            getViewSite().getActionBars().updateActionBars();
        }
    }

    /**
     * Returns the selectionProvider for this page book view.
     * 
     * @return a SelectionProvider
     */
    protected SelectionProvider getSelectionProvider() {
        return selectionProvider;
    }

    /**
     * Make sure that the part is not considered if it is hidden.
     * 
     * @param part
     * @since 3.5
     */
    protected void partHidden( IWorkbenchPart part ) {
        if (part == null || part != getCurrentContributingPart()) {
            return;
        }
        // if we've minimized the editor stack, that's no reason to
        // drop our content
        if (getSite().getPage().getPartState(getSite().getPage().getReference(part)) == IWorkbenchPage.STATE_MINIMIZED) {
            return;
        }
        // if we're switching from a part source in our own stack,
        // we also don't want to clear our content.
        if (part instanceof IViewPart) {
            final IViewPart[] viewStack = getSite().getPage().getViewStack(this);
            if (containsPart(viewStack, part)) {
                return;
            }
        }
        hiddenPart = part;
        showPageRec(defaultPageRec);
    }

    /**
     * @param viewStack
     * @param part
     * @return <code>true</code> if the part is in the viewStack
     */
    private boolean containsPart( IViewPart[] viewStack, IWorkbenchPart part ) {
        if (viewStack == null) {
            return false;
        }
        for( int i = 0; i < viewStack.length; i++ ) {
            if (viewStack[i] == part) {
                return true;
            }
        }
        return false;
    }

    /**
     * Make sure that the part is not considered if it is hidden.
     * 
     * @param part
     * @since 3.5
     */
    protected void partVisible( IWorkbenchPart part ) {
        if (part == null || part != hiddenPart) {
            return;
        }
        partActivated(part);
    }

    public void partBroughtToTop( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            broughtToTop(target);
        }
    }

    public void partDeactivated( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            deactivated(target);
        }
    }

    public void partHidden( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            hidden(target);
        }
    }

    protected void hidden( K target ) {
    }

    public void partInputChanged( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            inputChanged(target);
        }
    }

    protected void inputChanged( K target ) {

    }

    public void partOpened( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            opened(target);
        }
    }

    public void partVisible( IWorkbenchPartReference partRef ) {
        K target = getCurrent(partRef);
        if (target != null) {
            visible(target);
        }
    }

    protected void visible( K target ) {
    }
}
