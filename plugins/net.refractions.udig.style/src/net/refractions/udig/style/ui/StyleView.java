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
package net.refractions.udig.style.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.style.StylePlugin;
import net.refractions.udig.style.internal.Messages;
import net.refractions.udig.style.internal.StyleLayer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Responsible for allowing the user to choose between applicable StyleConfigurators
 * for the current blackboard.
 * </p>
 * <p>
 * What does this mean?
 * <ul>
 * <li>Listens to any workbench selection and will engage when a Layer is selected
 * <li>Will use both the Layer.getResource and StyleBlackboard when figuring out which
 * StyleConfigurators are applicable
 * <li>Will display a select control in the viewpart toolbar if their is more then one to choose
 * from, if there is only one a Label showing the StyleConfigurator name will be shown.
 * <li>The StyleConfigurator will be supplied with the Layer & Blackboard to edit, note this is
 * *not* the same black board as used by the Layer for live rendering!
 * <li>Is responsible for applying any changes to the Blackboard, this is done using an apply
 * button on the viewpart toolbar. Changes will also be applied when the workbench looses focus on
 * the layer.
 * </ul>
 * </p>
 * 
 * @author jdeolive
 * @since 0.5
 */
@SuppressWarnings("deprecation")
public class StyleView extends ViewPart {
    /** ID used in the extension point to identify this view */
    public final static String VIEW_ID = "net.refractions.udig.style.styleView"; //$NON-NLS-1$

    private final static String STYLE_MENU_GROUP = "style"; //$NON-NLS-1$
    private final static String CONFIG_MENU_GROUP = "config"; //$NON-NLS-1$
    private final static String CHOOSER_MENU_GROUP = "chooser"; //$NON-NLS-1$

    /**
     * Choose which styleConfigurator to use.
     * <p>
     * This is contributed to the toolbar via an IContributionItem.
     * </p>
     */
    Combo configChooser;

    /** Page book used to switch between available StyleConfigurators */
    PageBook book;
    Label pleaseSelectLayer;

    private Action applyAction;
    private Action cancelAction;

    /** Current layer being worked on (wrapped as a StyleLayer) or null if we don't have a victim * */
    private StyleLayer currentLayer;

    /**
     * List of StyleViewSites each one manages a IStyleConfigurator.
     * <p>
     * Note: This list should be accessed via getStyleConfigurators, that method will only show you
     * sites that are applicable to the current layer.
     * </p>
     */
    List<StyleViewSite> sites;
    private IStyleConfigurator currentConfig;

    /**
     * Watch workbench selection, any Layer anywhere must be styled.
     * <p>
     * Latches onto the first Layer found.
     * </p>
     * <p>
     * Will call setCurrentLayer based on what is found.
     * </p>
     */
    private ISelectionListener workbenchWatcher = new ISelectionListener(){
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
            if (part == StyleView.this)
                return;

            if (selection instanceof IStructuredSelection) {
                IStructuredSelection sselection = (IStructuredSelection) selection;

                // look for a Layer selection(s)
                for( Iterator<?> itr = sselection.iterator(); itr.hasNext(); ) {
                    Object obj = itr.next();
                    if (obj instanceof Layer) {
                        Layer layer = (Layer) obj;
                        setCurrentLayer(layer);

                        // System.out.println("Selected layer "+layer );
                        return;
                    }
                }
            }
        }
    };
    SelectionListener chooserListener = new SelectionListener(){
        /** Change current page in book */
        public void widgetSelected( SelectionEvent e ) {
            if (configChooser != null && configChooser.getSelectionIndex() > -1) {
                int index = configChooser.getSelectionIndex();
                IStyleConfigurator config = new ArrayList<IStyleConfigurator>(
                        getStyleConfigurators()).get(index);
                // System.out.println("You have selected "+index+" aka "+config );
                setStyleConfigurator(config);
                return;
            }
            // not found!
            // System.out.println("You have selected .. nothing!" ); //$NON-NLS-1$
            book.showPage(pleaseSelectLayer);
        }
        /**
         * aka double click in a list, return in chooser
         * <p>
         * Makes a call to applyStyle?
         * </p>
         */
        public void widgetDefaultSelected( SelectionEvent e ) {
            // book.showPage( (Control) e.data );
            // applyStyle();
        }
    };
    ILayerListener layerListener = new ILayerListener(){
        public void refresh( LayerEvent event ) {
            StyleView.this.refresh();
        }
    };
    /**
     * Construct <code>StyleView</code>.
     * <p>
     * Note since we are a view - nothing much happens here.
     * <ul>
     * <li>init will be called allowing us to grab our prefs
     * <li>createPartControl control will be called allowing us to set up before display
     * <li>dispose will be called when we are closed
     * </ul>
     * </p>
     */
    public StyleView() {
        super();
    }

    /**
     * Called before createPartControl to give us chance to organize ourselves.
     * <p>
     * We used this to latch onto the defined StyleConfigurators.
     * </p>
     * 
     * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
     */
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        if (sites != null) { // horrible must be a mistake
            throw new IllegalStateException("StyleView init called twice!"); //$NON-NLS-1$
        }
        sites = new ArrayList<StyleViewSite>();

        ExtensionPointUtil.process(StylePlugin.getDefault(), IStyleConfigurator.XPID, new ExtensionPointProcessor(){
            public void process( IExtension extension, IConfigurationElement element )
                    throws Exception {
                IStyleConfigurator config = (IStyleConfigurator) element
                        .createExecutableExtension("class"); //$NON-NLS-1$

                String id = element.getAttribute("styleId"); //$NON-NLS-1$
                String label = element.getAttribute("label"); //$NON-NLS-1$
                config.setStyleId(id);
                config.setLabel(label);

                StyleViewSite styleSite = new StyleViewSite(extension, element, config);
                try {
                    config.init(styleSite);
                    sites.add(styleSite);
                } catch (Exception e) {
                    IStatus status = new Status(IStatus.ERROR, element.getNamespaceIdentifier(), IStatus.OK,
                            null, e);
                    StylePlugin.getDefault().getLog().log(status);
                } catch (Throwable t) {
                    IStatus status = new Status(IStatus.ERROR, element.getNamespaceIdentifier(), IStatus.OK,
                            "Could not create " + element.getName(), t); //$NON-NLS-1$
                    StylePlugin.getDefault().getLog().log(status);
                    // could not process element
                }
            }
        });
    }

    /**
     * Creates the style editor layout, and uses a PageBook place-holder for ui widgets to be placed
     * into as Styles are selected.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    public void createPartControl( Composite parent ) {
        createActions();
        createToolBar();

        parent.setLayout(new FormLayout());

        FormData layout;
        layout = new FormData();
        layout.top = new FormAttachment(null, 0);
        layout.left = new FormAttachment(null, 1);
        layout.width = 200;

        book = new PageBook(parent, SWT.NONE);
        layout = new FormData();
        layout.top = new FormAttachment(0);
        layout.left = new FormAttachment(0);
        layout.right = new FormAttachment(100);
        layout.bottom = new FormAttachment(100);
        book.setLayoutData(layout);

        pleaseSelectLayer = new Label(book, SWT.DEFAULT);
        pleaseSelectLayer.setAlignment(SWT.LEFT);
        pleaseSelectLayer.setText(Messages.StyleView_label_selectLayer_text); 
        book.showPage(pleaseSelectLayer);

        // Add in all the config controls as pages in the book
        for( StyleViewSite site : sites ) {
            Composite page = new Composite(book, SWT.NONE);
            page.setLayout(new FillLayout());
            try {
                site.createControl(page);
                // note this may result in a call to StyleViewPart.getToolbarManager
                // or something.
            } catch (Throwable t) {

                t.printStackTrace();
                // TODO log exception
                page.dispose();
                site.dispose();
                sites.remove(this);
            }
        }

        // listen to selections from the workbench
        IWorkbenchPage page2 = getSite().getPage();
        page2.addSelectionListener(workbenchWatcher);

        IWorkbenchPage page = getViewSite().getPage();
        workbenchWatcher.selectionChanged(page.getActivePart(), page.getSelection());

        // Find the current layer
        ISelection sel = page2.getSelection();
        if (findLayer(sel))
            return;

        IEditorPart activeEditor = page2.getActiveEditor();
        if (activeEditor != null) {
            sel = activeEditor.getSite().getSelectionProvider().getSelection();
            if (findLayer(sel))
                return;
        }

        sel = page2.getSelection("net.refractions.udig.project.ui.layerManager"); //$NON-NLS-1$
        if (findLayer(sel))
            return;
        sel = page2.getSelection("net.refractions.udig.project.ui.projectExplorer"); //$NON-NLS-1$
        if (findLayer(sel))
            return;

    }

    private boolean findLayer( ISelection sel ) {
        Layer layer = null;
        if (sel == null || sel.isEmpty())
            return false;

        if (!(sel instanceof IStructuredSelection))
            return false;

        IStructuredSelection selection = (IStructuredSelection) sel;
        for( Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
            Object e = iter.next();
            if (e instanceof Layer) {
                layer = (Layer) e;
                break;
            }
            if (e instanceof IAdaptable) {
                IAdaptable adapter = (IAdaptable) e;
                Object adapted = adapter.getAdapter(Layer.class);
                if (adapted != null) {
                    layer = (Layer) adapted;
                    break;
                }
            }
        }

        if (layer != null) {
            setCurrentLayer(layer);
            return true;
        }
        return false;
    }

    /**
     * New layer, or null if there is no selected layer.
     */
    public void setCurrentLayer( Layer layer ) {
        if (currentLayer == null && layer == null) {
            return;
        }
        if (layer != null && layer.equals(currentLayer)) {
            return;
        }
        if (currentLayer != null) {
            currentLayer.removeListener(layerListener);
        }
        if (layer == null) {
            currentLayer = null;
        } else {
            currentLayer = new StyleLayer(layer);
            currentLayer.addListener(layerListener);
        }
        enableActions(currentLayer != null);

        // Check if the current site still works
        //
        IStyleConfigurator config = null;
        if (layer == null) {
            config = null;
        } else if (currentConfig != null && currentConfig.canStyle(layer)) {
            config = currentConfig;
        } else {
            for( StyleViewSite site : sites ) {
                if (site.getConfig().canStyle(layer)) {
                    config = site.getConfig();
                    break;
                }
            }
        }
        setStyleConfigurator(config);
        // let's update the chooser
        //
        updateChooser();
    }

    /**
     * Update chooser to reflect getStyleConfigurators list and currentConfig.
     */
    void updateChooser() {
        if (configChooser == null)
            return; // chooser is not created yet

        List<String> items = new ArrayList<String>();

        // populate the list with each of the configurators labels, or id if null
        //
        Set<IStyleConfigurator> configs = getStyleConfigurators();
        for( IStyleConfigurator config : configs ) {
            String label = config.getLabel();
            items.add(label);
        }

        // update the chooser list
        configChooser.setItems(items.toArray(new String[items.size()]));

        if (configChooser.getItemCount() == 0) {
            // no items, set a default message
            String message = Messages.StyleView_chooser_message; 
            configChooser.add(message);
            configChooser.setEnabled(false);
            configChooser.setText(message);
            return;
        } else if (configChooser.getItemCount() == 1) {
            // one item, set first as active
            configChooser.setVisibleItemCount(1);
            configChooser.setEnabled(true);
        } else {
            configChooser.setVisibleItemCount(Math.min(5, items.size()));
            configChooser.setEnabled(true);
        }

        if (currentConfig == null) {
            configChooser.setText("--"); //$NON-NLS-1$
        } else {
            configChooser.setText(currentConfig.getLabel());
        }
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Acquire configurators for the current layer.
     * <p>
     * <ul>
     * <li>WARNING: this method is only valid to call *after* init has been called.
     * <li>ARNING: these IStyleConfigurators will only have their ui available after
     * createPartControl has completed. Please don't call setFocus( IStyleConfigurator ) before
     * hand.
     * </ul>
     * </p>
     * <p>
     * This means you should *not* assume these IStyleConfigurators are totally happy and ready to
     * work. They will only listen to events when they are the current page for example.
     * </p>
     * 
     * @return Set of configurators for the current layer, may be empty
     */
    public Set<IStyleConfigurator> getStyleConfigurators() { // FIXME - make this a List!
        Layer layer = getCurrentLayer();

        Set<IStyleConfigurator> set = new TreeSet<IStyleConfigurator>(
                new Comparator<IStyleConfigurator>(){
                    public int compare( IStyleConfigurator a, IStyleConfigurator b ) {
                        if (a == b)
                            return 0;
                        if (a == null || a.getLabel() == null)
                            return -1;
                        if (b == null || b.getLabel() == null)
                            return 1;
                        return a.getLabel().compareTo(b.getLabel());
                    }
                });

        if (layer == null || sites == null || sites.size() == 0)
            return set;

        Set<IStyleConfigurator> badSites = new HashSet<IStyleConfigurator>();
        for( StyleViewSite site : sites ) {
            try {
                IStyleConfigurator config = site.getConfig();
                if (config.canStyle(layer)) {
                    set.add(config);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                // site was bad and must die!
                StylePlugin.getDefault().getLog().log(
                        new Status(IStatus.INFO, site.getPluginId(), IStatus.OK, null, t));
                badSites.add(site.config);
            }
        }
        if (!badSites.isEmpty())
            sites.removeAll(badSites);
        return set;
    }

    /**
     * Current AbstractStyleConfigurator or null if we don't have one
     * <p>
     * Grab the IStyleConfigurator
     * 
     * @return IStyleConfigurator public IStyleConfigurator getCurrent(){ int index =
     *         chooser.getSelectionIndex(); if( index == -1 ) return null; Set<IStyleConfigurator>
     *         set = getStyleConfigurators( ); if( set == null || set.isEmpty() || set.size() <
     *         index ) return null; return new ArrayList<IStyleConfigurator>( set ).get(index); }
     */

    /**
     * Focuses the style configurator.
     */
    void focusConfigurator( IStyleConfigurator config ) {
        if (currentLayer == null)
            return;
        config.focus(currentLayer);

        /*
         * StyleLayer styleLayer = layer2styleLayer.get(currentLayer); if (styleLayer == null) {
         * styleLayer = new StyleLayer(currentLayer); layer2styleLayer.put(currentLayer,
         * styleLayer); } config.focus(styleLayer);
         */
    }
    /**
     * Set focus to the chooser if available.
     * <p>
     * TODO: Should set the focus to the current StyleView being displayed.
     * </p>
     */
    public void setFocus() {
        if (configChooser != null) {
            configChooser.setFocus();
        }
    }
    /**
     * Set the current StyleConfigurator used by this StyleView to the provided config.
     * <p>
     * Note if there is only one chooser, we should set the focus to the styleConfigurator.
     * </p>
     * <p>
     * Responsibilities:
     * <ul>
     * <li>set the chooser text to the config.getLabel!
     * <li>call site.focus() for the config - so the page gets show, and the toolbar gets shown
     * </ul>
     * 
     * @param config IStyleConfigurator to be displayed by StyleView
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setStyleConfigurator( IStyleConfigurator config ) {
        currentConfig = config;
        /*
         * if( configChooser != null && config != null ){ configChooser.setText( config.getLabel() ); }
         */
        if (currentConfig != null) {
            currentConfig.setAction(applyAction);
            for( StyleViewSite site : sites ) {
                if (site.getConfig() == currentConfig) {
                    // System.out.println( "I am trying to show "+site +" for "+config );
                    site.focus(); // show this site
                    return;
                }
            }
        }
        book.showPage(pleaseSelectLayer);
        // should hide toolbar?
    }
    /**
     * This *is* the current styleConfigurator used by this style view.
     * <p>
     * The following controls need to be kept in sync:
     * <ul>
     * <li>configChooser (if it exists yet) needs to use getStyleConfigurator().getLabel as its
     * text
     * <li>book needs to use StyleViewSite page associated with this StyleConfigurator
     * <li>the toolbar from the StyleViewSite also needs to be displayed
     * </ul>
     * 
     * @return IStyleConfigurator currently being displayed
     */
    public IStyleConfigurator getStyleConfigurator() {
        return currentConfig;
    }

    private void enableActions( boolean enable ) {
        applyAction.setEnabled(enable);
        cancelAction.setEnabled(enable);
    }

    private void createActions() {
        /*
         * Handles input when user presses Apply button. Alerts the current configurator to apply
         * the style to the layer.
         */
        applyAction = new Action("apply"){ //$NON-NLS-1$
            public void run() {
                apply();
            }
        };
        applyAction.setEnabled(false);
        applyAction.setToolTipText(Messages.StyleView_apply_tooltip); 
        applyAction.setImageDescriptor(StylePlugin.getDefault().getImageDescriptor(ImageConstants.APPLY_STYLE));
        // applyCI = new ActionContributionItem(applyAction);

        cancelAction = new Action("cancel"){ //$NON-NLS-1$
            public void run() {
                revert();
            }
        };
        cancelAction.setEnabled(false);
        cancelAction.setToolTipText(Messages.StyleView_cancel_tooltip); 
        cancelAction.setImageDescriptor(StylePlugin.getDefault().getImageDescriptor(ImageConstants.CANCEL_STYLE));
        // cancelCI = new ActionContributionItem(cancelAction);
    }

    void apply() {
        if (currentLayer == null)
            return;
        currentLayer.apply();
        /*
         * StyleLayer styleLayer = layer2styleLayer.get(currentLayer); if (styleLayer == null)
         * return; styleLayer.apply();
         */
    }

    void revert() {
        if (currentLayer == null)
            return;

        currentLayer.revert();
        refresh();
        /*
         * StyleLayer styleLayer = layer2styleLayer.get(currentLayer); if (styleLayer == null)
         * return; styleLayer.revert(); //refresh everyone for (StyleViewSite site : sites) {
         * site.config.focus(currentLayer); }
         */
    }

    /**
     * Refresh all the viewsites, aka force stylecon figurators to reset.
     */
    public void refresh() {
        for( StyleViewSite site : sites ) {
            site.config.focus(currentLayer);
        }
    }

    private void createToolBar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();

        mgr.add(new GroupMarker(CHOOSER_MENU_GROUP));
        mgr.appendToGroup(CHOOSER_MENU_GROUP, new ControlContribution("none"){ //$NON-NLS-1$
                    protected Control createControl( Composite parent ) {
                        configChooser = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

                        // update the chooser with the currently selected layer
                        //
                        updateChooser();

                        configChooser.addSelectionListener(chooserListener);

                        // setFocus();
                        // updateChooser();
                        /*
                         * XXX Justin what are you doing?
                         * PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable() { public
                         * void run() { chooserListener.widgetSelected(null); } } );
                         */
                        return configChooser;
                    }

                    protected int computeWidth( Control control ) {
                        return 125;
                    }
                });
        mgr.add(new GroupMarker(STYLE_MENU_GROUP));
        mgr.appendToGroup(STYLE_MENU_GROUP, new Separator());
        mgr.appendToGroup(STYLE_MENU_GROUP, applyAction);
        mgr.appendToGroup(STYLE_MENU_GROUP, cancelAction);

        mgr.add(new GroupMarker(CONFIG_MENU_GROUP));
        mgr.add(new Separator());
    }

    /**
     * Note: createPartControl may not even of been called
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose() {
        if (sites != null) {
            for( StyleViewSite site : sites ) {
                try {
                    site.dispose();
                } catch (Throwable t) {
                    // problem cleaning up after site
                }
            }
        }
        if (workbenchWatcher != null) {
            getSite().getPage().removeSelectionListener(workbenchWatcher);
        }
        super.dispose();
    }

    /**
     * This is the "site" for the IStyleConfigurator - it provides context for the part to work
     * against.
     * 
     * @author jgarnett
     * @since 0.9.0
     */
    class StyleViewSite implements IViewSite {
        String idPlugin;
        String idExtention;
        String idStyle;
        IActionBars actionBars;

        /** Config being managed */
        IStyleConfigurator config;

        /** Page holding the config ui, page of book */
        private Composite page;

        /**
         * Page holding the toolbar, page of toolBook ToolBar toolbar;
         */

        ToolBarManager toolbarManager;

        /**
         * Grabs a viewsite so we can talk to and manage resources associated with the part.
         * 
         * @param extention extension point being processed
         * @param element element providing content
         * @param part
         */
        public StyleViewSite( IExtension extention, IConfigurationElement element,
                IStyleConfigurator part ) {
            idPlugin = element.getNamespaceIdentifier();
            idExtention = extention.getUniqueIdentifier();
            idStyle = part.getStyleId();
            config = part;
            actionBars = null; // will create only if config asks for it
            // toolbar = null;
            toolbarManager = null; // toolbar created by our actionBars
        }

        /**
         * Call config.createControl with a empty page from book.
         * 
         * @param parent
         */
        public void createControl( Composite parent ) {
            config.createControl(parent);

            page = parent;
        }

        public IActionBars getActionBars() {
            if (actionBars != null)
                return actionBars;

            final IActionBars global = getViewSite().getActionBars();
            actionBars = new IActionBars(){
                public void clearGlobalActionHandlers() {
                    global.clearGlobalActionHandlers();
                }

                public IAction getGlobalActionHandler( String actionId ) {
                    return global.getGlobalActionHandler(actionId);
                }

                public IMenuManager getMenuManager() {
                    return null;
                }

                public IStatusLineManager getStatusLineManager() {
                    return global.getStatusLineManager();
                }

                public IToolBarManager getToolBarManager() {
                    if (toolbarManager == null) {
                        toolbarManager = new ToolBarManager(){
                            public void update( boolean force ) {
                                super.update(force);
                                /*
                                 * IToolBarManager gtbm = global.getToolBarManager(); //remove all
                                 * the items in the config group IContributionItem[] items =
                                 * gtbm.getItems(); boolean remove = false; for (int i = 0; i <
                                 * items.length; i++) { if (items[i].isGroupMarker()) { GroupMarker
                                 * marker = (GroupMarker)items[i]; remove =
                                 * marker.getId().equals(CONFIG_MENU_GROUP); } else { if (remove) {
                                 * gtbm.remove(items[i]); } } } //add items to the config group
                                 * items = getItems(); for (int i = 0; i < items.length; i++) {
                                 * gtbm.appendToGroup(CONFIG_MENU_GROUP, items[i]); }
                                 * gtbm.update(false);
                                 */
                            }
                        };
                    }
                    return toolbarManager;
                }

                public void setGlobalActionHandler( String actionId, IAction handler ) {
                    global.setGlobalActionHandler(actionId, handler);
                }

                public void updateActionBars() {
                    global.updateActionBars();
                }

				public IServiceLocator getServiceLocator() {
					return global.getServiceLocator();
				}
            };
            return actionBars;
        }
        /**
         * Returns the secondary id for this part site's part, or <code>null</code> if it has
         * none.
         */
        public String getSecondaryId() {
            return idStyle;
        }

        /**
         * Returns the part registry extension id for this part.
         * <p>
         * The name comes from the <code>id</code> attribute in the configuration element.
         * </p>
         * 
         * @return the registry extension id
         */
        public String getId() {
            return idExtention;
        }

        /**
         * Returns the unique identifier of the plug-in that defines this workbench site's part.
         * 
         * @return the unique identifier of the declaring plug-in
         */
        public String getPluginId() {
            return idPlugin;
        }

        /*
         * Returns the registered name for this workbench site's part. <p> The name comes from the
         * <code>label</code> attribute in the configuration element. </p>
         */
        public String getRegisteredName() {
            return config.getLabel();
        }
        public void registerContextMenu( String menuId, MenuManager menuManager,
                ISelectionProvider selectionProvider ) {
            // nope!
        }
        public void registerContextMenu( MenuManager menuManager,
                ISelectionProvider selectionProvider ) {
            // nope!
        }
        public IKeyBindingService getKeyBindingService() {
            return null;
        }
        public IWorkbenchPage getPage() {
            return StyleView.this.getViewSite().getPage();
        }
        public ISelectionProvider getSelectionProvider() {
            return StyleView.this.getViewSite().getSelectionProvider();
        }
        public Shell getShell() {
            return StyleView.this.getViewSite().getShell();
        }
        public IWorkbenchWindow getWorkbenchWindow() {
            return StyleView.this.getViewSite().getWorkbenchWindow();
        }
        public void setSelectionProvider( ISelectionProvider provider ) {
            StyleView.this.getViewSite().setSelectionProvider(provider);
        }
        @SuppressWarnings("unchecked")
        public Object getAdapter( Class adapter ) {
            return null;
        }
        /**
         * Grab config object associated with the site.
         * <p>
         * Note this object may be lazy loaded.
         * </p>
         * 
         * @return Config object for the site
         */
        public IStyleConfigurator getConfig() {
            return config;
        }
        /**
         * Clean up after site - will at least call config.dispose() if needed.
         */
        public void dispose() {
            if (page != null && !page.isDisposed()) {
                page.dispose();
                page = null;
            }
            /*
             * if( toolbar != null && !page.isDisposed()){ toolbar.dispose(); toolbar = null; }
             */
            if (config != null) {
                config.dispose();
                config = null;
            }
        }
        /**
         * Focus on this style configurator.
         * <p>
         * This needs to:
         * <ul>
         * <li>Show the page for this site
         * <li>Show the toolbars for this site
         * </ul>
         */
        public void focus() {
            if (currentLayer == null) {
                // run screaming!
                book.showPage(pleaseSelectLayer);
                return;
            }
            if (!getConfig().canStyle(currentLayer)) {
                throw new IllegalStateException("Trying to use " + config //$NON-NLS-1$
                        + " with a layer it cannot style"); //$NON-NLS-1$
            }
            if (page == null || page.isDisposed()) {
                throw new IllegalStateException(
                        "Framework should have called createPartControl before trying to focus"); //$NON-NLS-1$
            }
            getConfig().focus(currentLayer); // smack config with layer to display

            book.setVisible(true);
            book.showPage(page);
            page.setVisible(true);

            // How do I grab the toolbar for this site toolbar?
            // FIXME: help with sub toolbar
            if (toolbarManager != null) {
                IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
                mgr.add(new GroupMarker(CONFIG_MENU_GROUP));
                for( IContributionItem item : toolbarManager.getItems() ) {
                    mgr.appendToGroup(CONFIG_MENU_GROUP, item);
                }
            } else {
                IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
                mgr.removeAll();
                createToolBar();
                mgr.remove(CONFIG_MENU_GROUP);
                // mgr.add(new GroupMarker(CONFIG_MENU_GROUP));
            }
            getViewSite().getActionBars().updateActionBars();
        }
        /*
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Site<" + config.getLabel() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        public IWorkbenchPart getPart() {
            return StyleView.this;
        }

		@SuppressWarnings("unchecked")
        public Object getService(Class api) {
			// TODO3.2 Auto-generated method stub
			return null;
		}

		@SuppressWarnings("unchecked")
        public boolean hasService(Class api) {
			// TODO3.2 Auto-generated method stub
			return false;
		}
    }
}