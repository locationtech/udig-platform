/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2009 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.locationtech.udig.feature.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.tabbed.AbstractOverridableTabListPropertySection;
import org.eclipse.ui.views.properties.tabbed.IOverridableTabListContentProvider;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.locationtech.udig.feature.editor.FeatureEditorPlugin;
import org.locationtech.udig.feature.editor.IFeaturePage;
import org.locationtech.udig.project.ui.IFeaturePanel;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.feature.FeaturePanelEntry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A tabbed UI showing feature panels.
 * <p>
 * The use of ISelectionListener.selectionChanged(IWorkbenchPart,ISelection) is used to track what
 * is going on; it is the responsibility of the container to feed this page events via this method.
 * <ul>
 * <li>IWorkbenchPart - is checked to see if it can adapt to a Map
 * <li>ISelection -
 * </ul>
 *
 * @see TabbedPropertySheetPage
 */
public class FeaturePanelPage extends Page
        implements IFeaturePage, ILabelProviderListener, ISelectionListener {
    /**
     * FeatureSite allowing page to interact with the current EditManager etc...
     */
    private IFeatureSite site;

    /**
     * Normal tabbed property composite will display fine
     */
    private FeaturePanelComposite tabbedPropertyComposite;

    /**
     * Subclass of form view tricked out for display
     */
    private FeaturePanelWidgetFactory widgetFactory;

    /**
     * This is the contributor that is hosting this page
     */
    private FeaturePanelPageContributor contributor;

    private FeaturePanelRegistry registry;

    /**
     * In the event we are provided a selection from another workbench part this selection
     * contributor will due the dead.
     */
    private FeaturePanelPageContributor selectionContributor = null;

    /**
     * The currently active schema, which may not match the schema from the workbench part that
     * created this instance.
     */
    private SimpleFeatureType currentSchema;

    protected IStructuredContentProvider tabListContentProvider;

    private ISelection currentSelection;

    private boolean activePropertySheet;

    private FeaturePanelViewer tabbedPropertyViewer;

    private IFeaturePanel currentTab;

    private Map<FeaturePanelTabDescriptor, IFeaturePanel> descriptorToTab;

    private Map<IFeaturePanel, Composite> tabToComposite;

    private List<String> selectionQueue;

    private boolean selectionQueueLocked;

    private List<TabSelectionListener> tabSelectionListeners;

    private IWorkbenchWindow cachedWorkbenchWindow;

    private boolean hasTitleBar = true;

    /**
     * A listener that is interested in part activation events.
     */
    private IPartListener partActivationListener = new IPartListener() {

        @Override
        public void partActivated(IWorkbenchPart part) {
            handlePartActivated(part);
        }

        @Override
        public void partBroughtToTop(IWorkbenchPart part) {
        }

        @Override
        public void partClosed(IWorkbenchPart part) {
        }

        @Override
        public void partDeactivated(IWorkbenchPart part) {
        }

        @Override
        public void partOpened(IWorkbenchPart part) {
        }
    };

    /**
     * This is used to communicate a selected SimpleFeatureType to the page. This can be used adapt
     * a normal workbench part up as a IFeaturePanelPageContributor.
     */
    private class IFeaturePanelPageContributorFromSelection implements FeaturePanelPageContributor {

        private SimpleFeatureType schema;

        /**
         * Constructor that takes in a contributor id taken from a selection.
         *
         * @param contributorId the contributor id.
         */
        public IFeaturePanelPageContributorFromSelection(SimpleFeatureType schema) {
            this.schema = schema;
        }

        @Override
        public SimpleFeatureType getSchema() {
            return schema;
        }
    }

    /**
     * Label provider for the ListViewer.
     */
    class TabbedPropertySheetPageLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof FeaturePanelTabDescriptor) {
                return ((FeaturePanelTabDescriptor) element).getLabel();
            }
            return null;
        }
    }

    /**
     * SelectionChangedListener for the ListViewer.
     */
    class SelectionChangedListener implements ISelectionChangedListener {

        /**
         * Shows the tab associated with the selection.
         */
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            IFeaturePanel tab = null;
            FeaturePanelTabDescriptor descriptor = (FeaturePanelTabDescriptor) selection
                    .getFirstElement();

            if (descriptor == null) {
                // pretend the tab is empty.
                hideTab(currentTab);
            } else {
                // create tab if necessary
                // can not cache based on the id - tabs may have the same id,
                // but different section depending on the selection
                //
                tab = descriptorToTab.get(descriptor);

                if (tab != currentTab) {
                    hideTab(currentTab);
                }

                Composite tabComposite = tabToComposite.get(tab);
                if (tabComposite == null) {
                    tabComposite = createTabComposite();
                    tab.createPartControl(tabComposite);
                    try {
                        tab.init(site, null);
                    } catch (PartInitException e) {
                        IStatus status = new Status(IStatus.ERROR, descriptor.getEntry().getId(),
                                "Problem initializing feature panel", e); //$NON-NLS-1$
                        FeatureEditorPlugin.getDefault().getLog().log(status);
                    }
                    tabToComposite.put(tab, tabComposite);
                }

                // store tab selection
                storeCurrentTabSelection(descriptor.getLabel());

                if (tab != currentTab) {
                    showTab(tab);
                }
                tab.refresh();
            }
            tabbedPropertyComposite.getTabComposite().layout(true);
            currentTab = tab;
            resizeScrolledComposite();

            if (descriptor != null) {
                handleTabSelection(descriptor);
            }
        }

        /**
         * Shows the given tab.
         */
        private void showTab(IFeaturePanel target) {
            if (target != null) {
                Composite tabComposite = tabToComposite.get(target);
                if (tabComposite != null) {
                    /**
                     * the following method call order is important - do not change it or the
                     * widgets might be drawn incorrectly
                     */
                    tabComposite.moveAbove(null);
                    target.aboutToBeShown();
                    tabComposite.setVisible(true);
                }
            }
        }

        /**
         * Hides the given tab.
         */
        private void hideTab(IFeaturePanel currentTab) {
            if (currentTab != null) {
                Composite tabComposite = tabToComposite.get(currentTab);
                if (tabComposite != null) {
                    currentTab.aboutToBeHidden();
                    tabComposite.setVisible(false);
                }
            }
        }

    }

    /**
     * create a new tabbed property sheet page.
     *
     * @param tabbedPropertySheetPageContributor the tabbed property sheet page contributor.
     */
    public FeaturePanelPage(FeaturePanelPageContributor featurePanelPageContributor) {
        this(featurePanelPageContributor, true);
    }

    /**
     * create a new tabbed property sheet page.
     *
     * @param tabbedPropertySheetPageContributor the tabbed property sheet page contributor.
     * @param showTitleBar boolean indicating if the title bar should be shown; default value is
     *        <code>true</code>
     * @since 3.5
     */
    public FeaturePanelPage(FeaturePanelPageContributor contributor, boolean showTitleBar) {
        hasTitleBar = showTitleBar;
        this.contributor = contributor;
        tabToComposite = new HashMap<>();
        selectionQueue = new ArrayList<>(10);
        tabSelectionListeners = new ArrayList<>();
        initContributor(contributor.getSchema());
    }

    /**
     * Handle the part activated event.
     *
     * @param part the new activated part.
     */
    protected void handlePartActivated(IWorkbenchPart part) {
        // Check if the part is a PageBookView with this page as its
        // current page.
        boolean thisActivated = part instanceof PageBookView
                && ((PageBookView) part).getCurrentPage() == this;

        // When the active part changes and does not select a FeatureType
        // we still need to trigger aboutToBeHidden() and aboutToBeShown().
        if (!thisActivated && !part.equals(contributor)) {
            // check if this view is a proxy for another contributor
            IContributedContentsView view = part.getAdapter(IContributedContentsView.class);

            if (view != null && view.getContributingPart() != null
                    && view.getContributingPart().equals(contributor)) {
                // this view is a proxy for our current contributor
            } else {
                // we are actually switching contributors
                if (activePropertySheet) {
                    if (currentTab != null) {
                        currentTab.aboutToBeHidden();
                    }
                    activePropertySheet = false;
                }
                return;
            }
        }
        if (!activePropertySheet && currentTab != null) {
            currentTab.aboutToBeShown();
            currentTab.refresh();
        }
        activePropertySheet = true;
    }

    @Override
    public void createControl(Composite parent) {
        widgetFactory = new FeaturePanelWidgetFactory();
        tabbedPropertyComposite = new FeaturePanelComposite(parent, widgetFactory, hasTitleBar);
        widgetFactory.paintBordersFor(tabbedPropertyComposite);
        tabbedPropertyComposite.setLayout(new FormLayout());
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.top = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(100, 0);
        tabbedPropertyComposite.setLayoutData(formData);

        FeaturePanelList list = tabbedPropertyComposite.getList();
        tabbedPropertyViewer = new FeaturePanelViewer(list);
        tabbedPropertyViewer.setContentProvider(tabListContentProvider);
        tabbedPropertyViewer.setLabelProvider(new TabbedPropertySheetPageLabelProvider());
        tabbedPropertyViewer.addSelectionChangedListener(new SelectionChangedListener());
        tabbedPropertyComposite.getScrolledComposite().addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                resizeScrolledComposite();
            }
        });

        /**
         * Add a part activation listener.
         */
        cachedWorkbenchWindow = getSite().getWorkbenchWindow();
        cachedWorkbenchWindow.getPartService().addPartListener(partActivationListener);

        /**
         * Add a label provider change listener.
         */
        if (hasTitleBar) {
            registry.getLabelProvider().addListener(this);
        }
    }

    /**
     * Initialize the contributor
     *
     * @param contributorId the contributor id.
     */
    private void initContributor(SimpleFeatureType schema) {
        descriptorToTab = new HashMap<>();

        if (currentSchema == schema) {
            // default contributor from the workbench part.
            registry = FeaturePanelRegistryFactory.getInstance().createRegistry(contributor);
        } else {
            // selection contributor.
            selectionContributor = new IFeaturePanelPageContributorFromSelection(schema);
            registry = FeaturePanelRegistryFactory.getInstance()
                    .createRegistry(selectionContributor);
        }

        currentSchema = schema;
        tabListContentProvider = getTabListContentProvider();
        hasTitleBar = hasTitleBar && registry.getLabelProvider() != null;

        if (tabbedPropertyViewer != null) {
            tabbedPropertyViewer.setContentProvider(tabListContentProvider);
        }

        /**
         * Add a label provider change listener.
         */
        if (hasTitleBar) {
            registry.getLabelProvider().addListener(this);
        }

    }

    /**
     * Gets the tab list content provider for the contributor.
     *
     * @return the tab list content provider for the contributor.
     */
    protected IStructuredContentProvider getTabListContentProvider() {
        return registry.getTabListContentProvider();
    }

    /**
     * Dispose the contributor with the provided contributor id. This happens on part close as well
     * as when contributors switch between the workbench part and contributor from a selection.
     */
    private void disposeContributor() {
        /**
         * If the current tab is about to be disposed we have to call aboutToBeHidden
         */
        if (currentTab != null) {
            currentTab.aboutToBeHidden();
            currentTab = null;
        }

        disposeTabs(descriptorToTab.values());
        descriptorToTab = new HashMap();

        /**
         * Remove the label provider change listener.
         */
        if (hasTitleBar && registry != null) {
            registry.getLabelProvider().removeListener(this);
        }

        if (selectionContributor != null) {
            /**
             * remove the selection contributed registry.
             */
            FeaturePanelRegistryFactory.getInstance().disposeRegistry(selectionContributor);
            selectionContributor = null;
        }
    }

    @Override
    public void dispose() {

        disposeContributor();

        if (widgetFactory != null) {
            widgetFactory.dispose();
            widgetFactory = null;
        }
        /**
         * Remove the part activation listener.
         */
        if (cachedWorkbenchWindow != null) {
            cachedWorkbenchWindow.getPartService().removePartListener(partActivationListener);
            cachedWorkbenchWindow = null;
        }

        if (registry != null) {
            // registry.dispose();
            registry = null;
        }
        currentSchema = null;
        currentSelection = null;
    }

    @Override
    public Control getControl() {
        return tabbedPropertyComposite;
    }

    @Override
    public void setActionBars(IActionBars actionBars) {
        // Override the undo and redo global action handlers
        // to use the contributor action handlers
        IActionBars partActionBars = null;
        if (contributor instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart) contributor;
            partActionBars = editorPart.getEditorSite().getActionBars();
        } else if (contributor instanceof IViewPart) {
            IViewPart viewPart = (IViewPart) contributor;
            partActionBars = viewPart.getViewSite().getActionBars();
        }

        if (partActionBars != null) {
            IAction action = partActionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
            if (action != null) {
                actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), action);
            }
            action = partActionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
            if (action != null) {
                actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), action);
            }
        }
    }

    @Override
    public void setFocus() {
        getControl().setFocus();
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        setInput(part, selection);
        tabbedPropertyViewer.refresh();
    }

    /**
     * Stores the current tab label in the selection queue. Tab labels are used to carry the tab
     * context from one input object to another. The queue specifies the selection priority. So if
     * the first tab in the queue is not available for the input we try the second tab and so on. If
     * none of the tabs are available we default to the first tab available for the input.
     */
    private void storeCurrentTabSelection(String label) {
        if (!selectionQueueLocked) {
            selectionQueue.remove(label);
            selectionQueue.add(0, label);
        }
    }

    /**
     * Resize the scrolled composite enclosing the sections, which may result in the addition or
     * removal of scroll bars.
     *
     * @since 3.5
     */
    public void resizeScrolledComposite() {
        Point currentTabSize = new Point(0, 0);
        if (currentTab != null) {
            Composite sizeReference = tabToComposite.get(currentTab);
            if (sizeReference != null) {
                currentTabSize = sizeReference.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            }
        }
        tabbedPropertyComposite.getScrolledComposite().setMinSize(currentTabSize);

        ScrollBar verticalScrollBar = tabbedPropertyComposite.getScrolledComposite()
                .getVerticalBar();
        if (verticalScrollBar != null) {
            Rectangle clientArea = tabbedPropertyComposite.getScrolledComposite().getClientArea();
            int increment = clientArea.height - 5;
            verticalScrollBar.setPageIncrement(increment);
        }

        ScrollBar horizontalScrollBar = tabbedPropertyComposite.getScrolledComposite()
                .getHorizontalBar();
        if (horizontalScrollBar != null) {
            Rectangle clientArea = tabbedPropertyComposite.getScrolledComposite().getClientArea();
            int increment = clientArea.width - 5;
            horizontalScrollBar.setPageIncrement(increment);
        }
    }

    private void disposeTabs(Collection tabs) {
        for (Iterator iter = tabs.iterator(); iter.hasNext();) {
            IFeaturePanel tab = (IFeaturePanel) iter.next();
            Composite composite = tabToComposite.remove(tab);
            tab.dispose();
            if (composite != null) {
                composite.dispose();
            }
        }
    }

    /**
     * Returns the last known selected tab for the given input.
     */
    private int getLastTabSelection(IWorkbenchPart part, ISelection input) {
        List<FeaturePanelTabDescriptor> descriptors = registry.getTabDescriptors(part, input);
        if (!descriptors.isEmpty()) {
            for (String text : selectionQueue) {
                int i = 0;
                for (FeaturePanelTabDescriptor descriptor : descriptors) {
                    if (text.equals(descriptor.getLabel())) {
                        return i;
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    /**
     * Update the current tabs to represent the given input object. When tabs apply for both the old
     * and new input they are reused otherwise they are disposed. If the current visible tab will
     * not be reused (i.e. will be disposed) we have to send it an aboutToBeHidden() message.
     *
     * @since 3.4
     */
    protected void updateTabs(List<FeaturePanelTabDescriptor> descriptors) {
        Map<FeaturePanelTabDescriptor, IFeaturePanel> newTabs = new HashMap<>(
                descriptors.size() * 2);
        boolean disposingCurrentTab = (currentTab != null);
        for (FeaturePanelTabDescriptor descriptor : descriptors) {
            IFeaturePanel tab = descriptorToTab.remove(descriptor);

            if (tab != null && tab.controlsHaveBeenCreated()) {
                if (tab == currentTab) {
                    disposingCurrentTab = false;
                }
            } else {
                tab = createTab(descriptor);
            }

            newTabs.put(descriptor, tab);
        }
        if (disposingCurrentTab) {
            /**
             * If the current tab is about to be disposed we have to call aboutToBeHidden
             */
            currentTab.aboutToBeHidden();
            currentTab = null;
        }
        disposeTabs(descriptorToTab.values());
        descriptorToTab = newTabs;
    }

    /**
     * Create the tab contents for the provided tab descriptor.
     *
     * @param tabDescriptor the tab descriptor.
     * @return the tab contents.
     * @since 3.4
     */
    protected IFeaturePanel createTab(FeaturePanelTabDescriptor tabDescriptor) {
        FeaturePanelEntry entry = tabDescriptor.getEntry();
        return entry.createFeaturePanel();
    }

    /**
     * Helper method for creating property tab composites.
     *
     * @return the property tab composite.
     */
    private Composite createTabComposite() {
        Composite result = widgetFactory.createComposite(tabbedPropertyComposite.getTabComposite(),
                SWT.NO_FOCUS);
        result.setVisible(false);
        result.setLayout(new FillLayout());
        FormData data = new FormData();
        if (hasTitleBar) {
            data.top = new FormAttachment(tabbedPropertyComposite.getTitle(), 0);
        } else {
            data.top = new FormAttachment(0, 0);
        }
        data.bottom = new FormAttachment(100, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        result.setLayoutData(data);
        return result;
    }

    private void setInput(IWorkbenchPart part, ISelection selection) {
        if (selection != null && selection.equals(currentSelection)) {
            return;
        }

        this.currentSelection = selection;

        // see if the selection provides a new contributor
        validateRegistry(selection);
        List<FeaturePanelTabDescriptor> descriptors = registry.getTabDescriptors(part,
                currentSelection);
        // If there are no descriptors for the given input we do not need to
        // touch the tab objects. We might reuse them for the next valid
        // input.
        if (!descriptors.isEmpty()) {
            updateTabs(descriptors);
            ILabelProvider header = null;
            for (FeaturePanelTabDescriptor descriptor : descriptors) {
                header = descriptor.getEntry().getLabelProvider();
            }
            if (header != null) {
                tabbedPropertyViewer.setLabelProvider(new TabLabelProvider(header));
            } else {
                tabbedPropertyViewer.setLabelProvider(null);
            }
        }
        // update tabs list
        tabbedPropertyViewer.setInput(part, currentSelection);
        int lastTabSelectionIndex = getLastTabSelection(part, currentSelection);
        Object selectedTab = tabbedPropertyViewer.getElementAt(lastTabSelectionIndex);
        selectionQueueLocked = true;
        try {
            if (selectedTab == null) {
                tabbedPropertyViewer.setSelection(null);
            } else {
                tabbedPropertyViewer.setSelection(new StructuredSelection(selectedTab));
            }
        } finally {
            selectionQueueLocked = false;
        }
        refreshTitleBar();
    }

    /**
     * Refresh the currently active tab.
     */
    public void refresh() {
        if (currentTab != null) {
            currentTab.refresh();
        }
    }

    /**
     * Get the currently active tab.
     *
     * @return the currently active tab.
     * @since 3.4
     */
    public IFeaturePanel getCurrentTab() {
        return currentTab;
    }

    /**
     * Handle the tab selected change event.
     *
     * @param tabDescriptor the new selected tab.
     */
    private void handleTabSelection(FeaturePanelTabDescriptor tabDescriptor) {
        if (selectionQueueLocked) {
            /**
             * Don't send tab selection events for non user changes.
             */
            return;
        }
        for (TabSelectionListener listener : tabSelectionListeners) {
            listener.tabSelected(tabDescriptor);
        }
    }

    /**
     * Add a tab selection listener.
     *
     * @param listener a tab selection listener.
     */
    public void addTabSelectionListener(TabSelectionListener listener) {
        tabSelectionListeners.add(listener);
    }

    /**
     * Remove a tab selection listener.
     *
     * @param listener a tab selection listener.
     */
    public void removeTabSelectionListener(TabSelectionListener listener) {
        tabSelectionListeners.remove(listener);
    }

    /**
     * Override the tabs with a new set of tabs. The tab list is obtained from the
     * {@link AbstractOverridableTabListPropertySection} by the
     * {@link IOverridableTabListContentProvider}.
     *
     * @since 3.4
     */
    public void overrideTabs() {
        if (tabListContentProvider instanceof IOverridableTabListContentProvider) {
            IOverridableTabListContentProvider overridableTabListContentProvider = (IOverridableTabListContentProvider) tabListContentProvider;
            overridableTabListContentProvider.overrideTabs();
        }
    }

    /**
     * Get the widget factory.
     *
     * @return the widget factory.
     */
    public FeaturePanelWidgetFactory getWidgetFactory() {
        return widgetFactory;
    }

    /**
     * Update the title bar of the contributor has a label provider.
     */
    private void refreshTitleBar() {
        if (hasTitleBar) {
            FeaturePanelTitle title = tabbedPropertyComposite.getTitle();
            if (currentTab == null) {
                /**
                 * No tabs are shown so hide the title bar, otherwise you see "No properties
                 * available" and a title bar for the selection.
                 */
                title.setTitle(null, null);
            } else {
                String text = registry.getLabelProvider().getText(currentSelection);
                Image image = registry.getLabelProvider().getImage(currentSelection);
                title.setTitle(text, image);
            }
        }
    }

    @Override
    public void labelProviderChanged(LabelProviderChangedEvent event) {
        refreshTitleBar();
    }

    /**
     * Retrieve the contributor from the selection; the contributor is responsible for obtaining a
     * Schema.
     *
     * @param object - the selected element
     * @return the IFeaturePanelPageContributor or null if not applicable
     */
    private FeaturePanelPageContributor getFeaturePanelPageContributor(Object object) {
        if (object instanceof FeaturePanelPageContributor) {
            return (FeaturePanelPageContributor) object;
        }

        if (object instanceof IAdaptable
                && ((IAdaptable) object).getAdapter(FeaturePanelPageContributor.class) != null) {
            return (((IAdaptable) object).getAdapter(FeaturePanelPageContributor.class));
        }

        if (Platform.getAdapterManager().hasAdapter(object,
                FeaturePanelPageContributor.class.getName())) {
            return (FeaturePanelPageContributor) Platform.getAdapterManager().loadAdapter(object,
                    FeaturePanelPageContributor.class.getName());
        }

        return null;
    }

    /**
     * The workbench part creates this instance of the TabbedFeaturePanelPage and implements
     * ITabbedFeaturePanelPageContributor which is unique contributor id. This unique contributor id
     * is used to load a registry with the extension point This id matches the registry.
     * <p>
     * It is possible for elements in a selection to implement ITabbedFeaturePanelPageContributor to
     * provide a different contributor id and thus a different registry.
     *
     * @param selection the current selection in the active workbench part.
     */
    private void validateRegistry(ISelection selection) {
        if (selection == null) {
            return;
        }

        if (!(selection instanceof IStructuredSelection)) {
            return;
        }

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (structuredSelection.isEmpty()) {
            return;
        }

        FeaturePanelPageContributor newContributor = getFeaturePanelPageContributor(
                structuredSelection.getFirstElement());

        if (newContributor == null) {
            /**
             * selection does not implement or adapt ITabbedFeaturePanelPageContributor.
             */
            newContributor = contributor;
        }

        SimpleFeatureType selectionSchema = newContributor.getSchema();
        if (selectionSchema == null) {
            return;
        }
        if (selectionSchema.equals(currentSchema)) {
            // selection has the same contributor id as current, so leave
            // existing registry.
            return;
        }

        /**
         * Selection implements ITabbedFeaturePanelPageContributor different than current
         * contributor id, so make sure all elements implement the new id. If all contributor id do
         * not match, then fall back to default contributor from the workbench part.
         */
        Iterator i = structuredSelection.iterator();
        i.next();
        while (i.hasNext()) {
            newContributor = getFeaturePanelPageContributor(i.next());
            if (newContributor == null || !newContributor.getSchema().equals(selectionSchema)) {
                /**
                 * fall back to use the default contributor id from the workbench part.
                 */
                if (selectionContributor != null) {
                    disposeContributor();
                    // currentSchema = contributor.getSchema();
                    initContributor(contributor.getSchema());
                }
                return;
            }
        }

        /**
         * All the elements in the selection implement a new contributor id, so use that id.
         */
        disposeContributor();
        currentSchema = selectionSchema;
        initContributor(currentSchema);
        overrideActionBars();
    }

    /**
     * Override the action bars for the selection based contributor.
     */
    private void overrideActionBars() {
        if (registry.getActionProvider() != null) {
            ActionProvider actionProvider = registry.getActionProvider();
            actionProvider.setActionBars(contributor, getSite().getActionBars());
        }
    }

    /**
     * Returns the currently selected tab.
     *
     * @return the currently selected tab or <code>null</code> if there is no tab selected.
     * @since 3.5
     */
    public FeaturePanelTabDescriptor getSelectedTab() {
        int selectedTab = tabbedPropertyViewer.getSelectionIndex();
        if (selectedTab != -1) {
            Object object = tabbedPropertyViewer.getElementAt(selectedTab);
            if (object instanceof FeaturePanelTabDescriptor) {
                return (FeaturePanelTabDescriptor) object;
            }
        }
        return null;
    }

    /**
     * Returns the list of currently active tabs.
     */
    public List<FeaturePanelTabDescriptor> getActiveTabs() {
        List<FeaturePanelTabDescriptor> elements = tabbedPropertyViewer.getElements();
        if (elements != null && elements.size() > 0) {
            return elements;
        }
        return elements;
    }

    /**
     * Set the currently selected tab to be that of the provided tab id.
     *
     * @param id The string id of the tab to select.
     */
    public void setSelectedTab(String id) {
        List<FeaturePanelTabDescriptor> elements = tabbedPropertyViewer.getElements();
        if (elements != null && elements.size() > 0) {
            for (FeaturePanelTabDescriptor tabDescriptor : elements) {
                if (tabDescriptor.getId() != null && tabDescriptor.getId().equals(id)) {
                    tabbedPropertyViewer.setSelection(new StructuredSelection(tabDescriptor), true);
                }
            }
        }
    }

    /**
     * Returns text of the feature panel title for given selection. If selection is null, then
     * currentSelection is used
     *
     * @param selection Selection whose properties title text is to be returned
     * @return String representing title text.
     *
     */
    public String getTitleText(ISelection selection) {
        if (selection == null) {
            selection = currentSelection;
        }
        return registry.getLabelProvider().getText(selection);
    }

    /**
     * Returns the title image for given selection. If selection is null, then currentSelection is
     * used.
     *
     * @param selection Selection whose properties title image is to be returned
     * @return Image that is used as a title image.
     *
     */
    public Image getTitleImage(ISelection selection) {
        if (selection == null) {
            selection = currentSelection;
        }
        return registry.getLabelProvider().getImage(selection);
    }

    @Override
    public void editFeatureChanged(SimpleFeature feature) {
        if (feature != null) {
            StructuredSelection selection = new StructuredSelection(feature);
            selectionChanged(null, selection);
        } else {
            selectionChanged(null, null);
        }
    }

    @Override
    public IFeatureSite getFeatureSite() {
        return this.site;
    }

    @Override
    public void setFeatureSite(IFeatureSite site) {
        this.site = site;
    }
}
