/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_CONTENT_PROVIDER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_EXPANSION_PROVIDER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_LABEL_PROVIDER;
import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_VIEW_SORTER;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.issues.Column;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesContentProvider;
import org.locationtech.udig.issues.IIssuesExpansionProvider;
import org.locationtech.udig.issues.IIssuesLabelProvider;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IIssuesViewSorter;
import org.locationtech.udig.issues.IRemoteIssuesList;
import org.locationtech.udig.issues.IssuesList;
import org.locationtech.udig.issues.internal.ImageConstants;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.Messages;
import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IIssuesManagerListener;
import org.locationtech.udig.issues.listeners.IssuesListEvent;
import org.locationtech.udig.issues.listeners.IssuesManagerEvent;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;

/**
 * Lists the current issues and allows the issues to fixed.
 *
 * @see org.locationtech.udig.issues.IIssue for more information how how they can be fixed.
 * @author jones
 * @since 1.0.0
 */
public class IssuesView extends ViewPart implements ISelectionChangedListener, ISaveablePart2 {

    private final IssuesList resolvedIssues = new IssuesList();

    IssuesTreeViewer viewer;

    private StrategizedSorter sorter;

    // The listener that refreshes the viewer when the issues list changes.
    private final IIssuesListListener issuesListener = createIssuesListener();

    // Listens for setIssuesList to be called
    private final IIssuesManagerListener managerListener = createIssuesManagerListener();

    public static final String CONFIGURATION_EXTENSION_POINT_ID = "org.locationtech.udig.issues.issuesViewConfiguration"; //$NON-NLS-1$

    public static final String VIEW_ID = "org.locationtech.udig.issues.view.issues"; //$NON-NLS-1$

    public static final int RESOLUTION_COLUMN = 0;

    public static final int PRIORITY_COLUMN = 1;

    public static final int OBJECT_COLUMN = 2;

    public static final int DESC_COLUMN = 3;

    private static final String SHOW_GROUPS_KEY = "SHOW_GROUPS"; //$NON-NLS-1$

    private static final int SHOW_GROUPS = 1;

    boolean resolvedIssuesShown = false;

    private IAction fixAction;

    private IAction deleteAction;

    private IAction deleteGroupAction;

    private final List<ISelectionChangedListener> deleteGroupListeners = new LinkedList<>();

    private IIssuesExpansionProvider expansionProvider;

    private IAction showGroupAction;

    private TreeColumn resolutionColumn;

    private TreeColumn priorityColumn;

    private TreeColumn problemObjectColumn;

    private TreeColumn descriptionColumn;

    private boolean defaultShowGroup = false;

    private void showResolvedssues(boolean show) {
        if (resolvedIssuesShown == show)
            return;

        resolvedIssuesShown = show;
        if (show) {
            IIssuesManager.defaultInstance.removeIssuesListListener(issuesListener);
            resolvedIssues.addListener(issuesListener);
            viewer.setInput(resolvedIssues);
        } else {
            IIssuesManager.defaultInstance.addIssuesListListener(issuesListener);
            resolvedIssues.removeListener(issuesListener);
            viewer.setInput(IIssuesManager.defaultInstance.getIssuesList());
        }
        doExpand();
    }

    @Override
    public void createPartControl(Composite parent) {

        viewer = new IssuesTreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION);
        viewer.addSelectionChangedListener(this);
        viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
        Tree table = viewer.getTree();
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        // resolution
        resolutionColumn = new TreeColumn(table, SWT.CENTER);
        resolutionColumn.setWidth(60);
        resolutionColumn.addListener(SWT.Selection, new HeaderListener(RESOLUTION_COLUMN));

        // priority
        priorityColumn = new TreeColumn(table, SWT.CENTER);
        priorityColumn.setWidth(20);
        priorityColumn.addListener(SWT.Selection, new HeaderListener(PRIORITY_COLUMN));

        // name
        problemObjectColumn = new TreeColumn(table, SWT.LEFT);
        problemObjectColumn.setAlignment(SWT.LEFT);
        problemObjectColumn.setWidth(100);
        problemObjectColumn.addListener(SWT.Selection, new HeaderListener(OBJECT_COLUMN));

        // description
        descriptionColumn = new TreeColumn(table, SWT.LEFT);
        descriptionColumn.setAlignment(SWT.LEFT);
        descriptionColumn.setWidth(300);
        descriptionColumn.addListener(SWT.Selection, new HeaderListener(DESC_COLUMN));

        viewer.setColumnProperties(
                new String[] { String.valueOf(RESOLUTION_COLUMN), String.valueOf(PRIORITY_COLUMN),
                        String.valueOf(OBJECT_COLUMN), String.valueOf(DESC_COLUMN), });

        viewer.setCellEditors(new CellEditor[] { new ResolutionCellEditor(table),
                new PriorityCellEditor(table), null, new TextCellEditor(table) });

        viewer.setCellModifier(new IssuesCellModifier(this));

        priorityColumn.setResizable(true);
        problemObjectColumn.setResizable(true);
        descriptionColumn.setResizable(true);

        problemObjectColumn.setAlignment(SWT.CENTER);
        descriptionColumn.setAlignment(SWT.CENTER);

        viewer.setUseHashlookup(true);

        initViewerProviders();

        viewer.setInput(IIssuesManager.defaultInstance.getIssuesList());
        doExpand();

        viewer.getControl().addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ISelection selection = viewer.getSelection();
                if (selection.isEmpty()) {
                    fixIssue(IIssuesManager.defaultInstance.getIssuesList().get(0));
                } else if (selection instanceof IStructuredSelection) {
                    IStructuredSelection structured = (IStructuredSelection) selection;
                    fixIssue((IIssue) structured.getFirstElement());
                }

            }
        });

        initAction();

        addActions();

        addContextMenu();

        IIssuesManager.defaultInstance.addIssuesListListener(issuesListener);

        IIssuesManager.defaultInstance.addListener(managerListener);
    }

    /**
     * Loads providers from preference store. public only for testing should only be called by tests
     * and this class.
     */
    public void initViewerProviders() {
        sorter = new StrategizedSorter();
        sorter.setStrategy(load("sorter", KEY_VIEW_SORTER, new IssuesSorter())); //$NON-NLS-1$
        viewer.setComparator(sorter);

        setExpansionProvider(load("expansionProvider", KEY_VIEW_EXPANSION_PROVIDER, //$NON-NLS-1$
                new IssueExpansionProvider()));

        IssuesContentProvider contentProvider = load("contentProvider", KEY_VIEW_CONTENT_PROVIDER, //$NON-NLS-1$
                new IssuesContentProvider());
        if (contentProvider instanceof IssuesContentProvider) {
            contentProvider.setShowGroup(defaultShowGroup);
        }
        setContentProvider(contentProvider);
        setLabelProvider(load("labelProvider", KEY_VIEW_LABEL_PROVIDER, new IssuesLabelProvider())); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    private <T> T load(String expectedConfigurationElementName, String preferenceID,
            T defaultValue) {
        String extensionPoint = IssuesActivator.getDefault().getPreferenceStore()
                .getString(preferenceID);
        if (extensionPoint == null || extensionPoint.trim().length() == 0)
            return defaultValue;
        List<IConfigurationElement> extensions = ExtensionPointList
                .getExtensionPointList(CONFIGURATION_EXTENSION_POINT_ID);
        try {
            for (IConfigurationElement element : extensions) {
                String string = element.getNamespaceIdentifier() + "." + element.getAttribute("id");//$NON-NLS-1$//$NON-NLS-2$
                String extensionElementName = element.getName();
                if ((string).equals(extensionPoint)
                        && extensionElementName.equals(expectedConfigurationElementName)) {
                    return (T) element.createExecutableExtension("class"); //$NON-NLS-1$
                }
            }
        } catch (Exception e) {
            IssuesActivator.log("Error loading Issues View Content Provider", e); //$NON-NLS-1$
        }
        return defaultValue;
    }

    private void initAction() {
        this.fixAction = createFixAction();
        this.deleteAction = createDeleteAction();
        this.saveAction = createSaveAction();
        this.refreshAction = createRefreshAction();
        this.deleteGroupAction = createDeleteGroupAction();
        this.showGroupAction = createShowGroupsAction();
    }

    private IIssuesManagerListener createIssuesManagerListener() {
        return new IIssuesManagerListener() {

            @Override
            public void notifyChange(IssuesManagerEvent event) {
                boolean enabled = event.getSource().getIssuesList() instanceof IRemoteIssuesList;
                switch (event.getType()) {
                case DIRTY_ISSUE:
                    if (!saveAction.isEnabled())
                        saveAction.setEnabled(enabled);
                    break;
                case ISSUES_LIST_CHANGE:
                    refreshAction.setEnabled(enabled);

                    break;
                case SAVE:
                    if (saveAction.isEnabled())
                        saveAction.setEnabled(false);

                    break;

                default:
                    break;
                }
            }

        };
    }

    private IIssuesListListener createIssuesListener() {
        return new IIssuesListListener() {
            @Override
            public void notifyChange(IssuesListEvent event) {
                final Collection<? extends IIssue> changed = event.getChanged();
                if (!changed.isEmpty()) {
                    PlatformGIS.syncInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewer.getControl().isDisposed())
                                return;
                            refresh(true);
                            viewer.setSelection(new StructuredSelection(changed.toArray()), true);
                        }
                    });
                }
            }

        };
    }

    private void addContextMenu() {
        final MenuManager contextMenu = new MenuManager();
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                contextMenu.add(fixAction);
                contextMenu.add(deleteAction);
                contextMenu.add(saveAction);
                contextMenu.add(refreshAction);
                contextMenu.add(deleteGroupAction);
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, viewer);
    }

    private boolean testing = false;

    private IAction createDeleteGroupAction() {
        IAction action = new Action(Messages.IssuesView_deleteRelatedAction,
                IssuesActivator.getDefault().getImageDescriptor(ImageConstants.DELETE_GROUP)) {
            @Override
            public void runWithEvent(Event event) {
                IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
                if (sel.isEmpty())
                    return;

                if (!(sel.getFirstElement() instanceof IIssue)) {
                    ProjectUIPlugin.log("IssuesView somehow has a non Issue Selected", //$NON-NLS-1$
                            null);
                    return;
                }
                boolean doDelete;
                if (!testing) {
                    doDelete = MessageDialog.openConfirm(event.display.getActiveShell(),
                            Messages.IssuesView_deleteGroup, Messages.IssuesView_dialogMessage);
                } else {
                    doDelete = true;
                }

                if (!doDelete)
                    return;

                IIssue issue = (IIssue) sel.getFirstElement();
                getCurrentInputList().removeIssues(issue.getGroupId());
            };
        };
        if (viewer.getSelection().isEmpty()) {
            action.setEnabled(false);
        } else {
            if (viewer.getTree().getSelectionCount() == 1) {
                action.setEnabled(true);
            } else {
                action.setEnabled(false);
            }
        }
        createDeleteActionListener(action, false);
        return action;
    }

    private void createDeleteActionListener(final IAction action, final boolean doMultiSelect) {
        ISelectionChangedListener l = new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (viewer.getSelection().isEmpty()) {
                    action.setEnabled(false);
                } else if (!doMultiSelect) {
                    if (viewer.getTree().getSelectionCount() == 1) {
                        action.setEnabled(true);
                    } else {
                        action.setEnabled(false);
                    }
                } else {
                    action.setEnabled(true);
                }
            }

        };
        viewer.addPostSelectionChangedListener(l);
        this.deleteGroupListeners.add(l);
    }

    private IAction createDeleteAction() {
        Action action = new Action(Messages.IssuesView_DeleteIssueAction,
                IssuesActivator.getDefault().getImageDescriptor(ImageConstants.DELETE)) {
            @SuppressWarnings("unchecked")
            @Override
            public void runWithEvent(Event event) {
                IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
                LinkedList<IIssue> issues = new LinkedList<>();
                for (Iterator<IIssue> iter = sel.iterator(); iter.hasNext();) {
                    IIssue issue = iter.next();
                    issues.add(issue);
                }
                getCurrentInputList().removeAll(issues);

            }
        };
        action.setToolTipText(Messages.IssuesView_DeleteIssueTooltip);
        if (viewer.getSelection().isEmpty()) {
            action.setEnabled(false);
        } else {
            action.setEnabled(true);
        }

        createDeleteActionListener(action, true);
        return action;
    }

    private void addActions() {
        getViewSite().getActionBars().getToolBarManager().add(saveAction);
        getViewSite().getActionBars().getToolBarManager().add(refreshAction);
        getViewSite().getActionBars().getToolBarManager().add(fixAction);
        getViewSite().getActionBars().getToolBarManager().add(deleteAction);
        getViewSite().getActionBars().getMenuManager().add(createShowResolvedIssuesAction());
        getViewSite().getActionBars().getMenuManager().add(deleteGroupAction);
        getViewSite().getActionBars().getMenuManager().add(showGroupAction);
    }

    private IAction createShowGroupsAction() {
        final Action action = new Action(Messages.IssuesView_showGroups, IAction.AS_CHECK_BOX) {
            @Override
            public void runWithEvent(Event event) {
                IContentProvider contentProvider = viewer.getContentProvider();
                if (contentProvider instanceof IssuesContentProvider) {
                    IssuesContentProvider provider = (IssuesContentProvider) contentProvider;
                    provider.setShowGroup(isChecked());
                    refresh(true);
                }
            }
        };
        action.setChecked(false);

        action.setEnabled(viewer.getContentProvider() instanceof IssuesContentProvider);
        return action;
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {

        if (memento != null && memento.getInteger(SHOW_GROUPS_KEY) == SHOW_GROUPS) {
            defaultShowGroup = true;
        }
        super.init(site, memento);
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        if (showGroupAction != null && showGroupAction.isChecked()) {
            memento.putInteger(SHOW_GROUPS_KEY, SHOW_GROUPS);
        } else {
            memento.putInteger(SHOW_GROUPS_KEY, SHOW_GROUPS - 1);
        }
    }

    private IAction createShowResolvedIssuesAction() {
        final Action action = new Action(Messages.IssuesView_showIssues_action_name,
                IAction.AS_CHECK_BOX) {
            @Override
            public void runWithEvent(Event event) {
                showResolvedssues(isChecked());
            }
        };
        action.setChecked(resolvedIssuesShown);
        action.setToolTipText(Messages.IssuesView_showIssues_action_tooltip);
        return action;
    }

    private IAction createFixAction() {
        final Action action = new Action() {
            @Override
            public void runWithEvent(Event event) {
                IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
                fixIssue((IIssue) sel.getFirstElement());
            }
        };
        action.setImageDescriptor(
                IssuesActivator.getDefault().getImageDescriptor(ImageConstants.GOTO_ISSUE));
        action.setText(Messages.IssuesView_fix_action_name);
        action.setToolTipText(Messages.IssuesView_fix_action_tooltip);
        if (viewer.getSelection().isEmpty()) {
            action.setEnabled(false);
        } else {
            action.setEnabled(true);
        }
        createDeleteActionListener(action, false);
        return action;
    }

    private IAction createSaveAction() {
        final IIssuesManager issuesManager = IIssuesManager.defaultInstance;
        final Action action = new Action() {
            @Override
            public void runWithEvent(Event event) {
                try {
                    issuesManager.save(ProgressManager.instance().get());
                } catch (IOException e) {
                    IssuesActivator.log("", e); //$NON-NLS-1$
                }
            }
        };
        IWorkbenchAction template = ActionFactory.SAVE.create(getSite().getWorkbenchWindow());

        action.setImageDescriptor(template.getImageDescriptor());
        action.setText(template.getText());
        action.setToolTipText(template.getToolTipText());
        action.setDisabledImageDescriptor(template.getDisabledImageDescriptor());
        action.setHoverImageDescriptor(template.getHoverImageDescriptor());

        action.setEnabled((issuesManager.getIssuesList() instanceof IRemoteIssuesList)
                && issuesManager.isDirty());

        return action;
    }

    private IAction createRefreshAction() {
        final Action action = new Action() {
            @Override
            public void runWithEvent(Event event) {
                IIssuesList list = IIssuesManager.defaultInstance.getIssuesList();
                if (list instanceof IRemoteIssuesList)
                    try {
                        ((IRemoteIssuesList) list).refresh();
                    } catch (IOException e) {
                        IssuesActivator.log("", e); //$NON-NLS-1$
                    }
            }
        };
        IWorkbenchAction template = ActionFactory.REFRESH.create(getSite().getWorkbenchWindow());

        action.setText(template.getText());
        action.setToolTipText(template.getToolTipText());
        ImageDescriptor dtool = IssuesActivator.imageDescriptorFromPlugin(IssuesActivator.PLUGIN_ID,
                "icons/dtool16/refresh_co.gif"); //$NON-NLS-1$
        ImageDescriptor etool = IssuesActivator.imageDescriptorFromPlugin(IssuesActivator.PLUGIN_ID,
                "icons/etool16/refresh_co.gif"); //$NON-NLS-1$
        action.setImageDescriptor(etool);
        action.setDisabledImageDescriptor(dtool);

        action.setEnabled(
                IIssuesManager.defaultInstance.getIssuesList() instanceof IRemoteIssuesList);

        return action;
    }

    public void setSorter(Column sortColumn) {
        if (sorter.getColumn() != sortColumn) {
            sorter.setColumn(sortColumn);
            sorter.setReverse(false);
        } else {
            sorter.setReverse(!sorter.isReverse());
        }

        refresh(false);
    }

    /**
     * sets the sorter to use
     *
     * @param sorter2
     */
    public void setSorter(IIssuesViewSorter sorter2) {
        sorter.setStrategy(sorter2);
        refresh(false);
    }

    /**
     * Sets the perspective, opens the workbench part, opens the editor and calls fixIssue on the
     * issue.
     */
    public void fixIssue(IIssue issue) {
        if (issue.getResolution() == Resolution.UNRESOLVED)
            issue.setResolution(Resolution.IN_PROGRESS);
        refresh(issue, true);
        IssueHandler handler = IssueHandler.createHandler(issue);
        handler.restorePerspective();
        handler.restoreViewPart();
        handler.restoreEditor();
        handler.fixIssue();
    }

    @Override
    public void dispose() {
        try {
            IIssuesManager.defaultInstance.save(ProgressManager.instance().get());
        } catch (IOException e) {
            IssuesActivator.log("Error Saving issues List!!!", e); //$NON-NLS-1$
        }
        updateTimerJob.cancel();
        disposeListeners();
        super.dispose();
    }

    public void disposeListeners() {
        IIssuesManager.defaultInstance.removeIssuesListListener(issuesListener);
        IIssuesManager.defaultInstance.removeListener(managerListener);
        resolvedIssues.listeners.clear();
        for (ISelectionChangedListener l : deleteGroupListeners) {
            viewer.removeSelectionChangedListener(l);
        }
        deleteGroupListeners.clear();
    }

    /**
     * <b>FOR TESTING ONLY</b>
     */
    public void restoreListeners() {
        IIssuesManager.defaultInstance.addIssuesListListener(issuesListener);
        IIssuesManager.defaultInstance.addListener(managerListener);
    }

    @Override
    public void setFocus() {
        updateViewer();

        if (viewer != null)
            viewer.getControl().setFocus();
    }

    private void updateViewer() {
        IIssuesList issues = IIssuesManager.defaultInstance.getIssuesList();
        IIssuesList resolvedIssues = this.resolvedIssues;

        removeDeadItems(issues, resolvedIssues, false);
        removeDeadItems(resolvedIssues, issues, true);

    }

    private void removeDeadItems(IIssuesList list1, IIssuesList list2, boolean resolveList) {

        LinkedList<IIssue> toRemove = new LinkedList<>();
        for (IIssue issue : list1) {
            if (resolveList) {
                if (issue.getResolution() != Resolution.RESOLVED)
                    toRemove.add(issue);
            } else {
                if (issue.getResolution() == Resolution.RESOLVED)
                    toRemove.add(issue);
            }
        }
        if (!toRemove.isEmpty()) {
            list1.removeAll(toRemove);
            list2.addAll(0, toRemove);
            if (!resolveList) {
                if (list2.size() - 10 > 0) {
                    toRemove.clear();
                    toRemove.addAll(list2.subList(9, list2.size() - 1));
                    list2.removeAll(toRemove);
                }
            }
        }
    }

    private Display findDisplay() {
        try {
            return getSite().getShell().getDisplay();
        } catch (Exception e) {
            return Display.getDefault();
        }
    }

    class HeaderListener implements Listener {
        private int index;

        public HeaderListener(int columnIndex) {
            this.index = columnIndex;
        }

        @Override
        public void handleEvent(Event event) {
            setSorter(toColumn(index));
        }
    }

    // updates the viewer. Usually called when the resolution has changed to
    // resolved.
    // It waits a couple seconds before updating so user can change mind.
    Job updateTimerJob = new Job(Messages.IssuesView_timer_name) {

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            findDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (PlatformUI.getWorkbench().isClosing() || monitor.isCanceled())
                        return;

                    updateViewer();
                }
            });
            return Status.OK_STATUS;
        }

    };

    private IAction refreshAction;

    private IAction saveAction;

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public TreeColumn forTestingGetProblemObjectColumnHeader() {
        return problemObjectColumn;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public TreeViewer forTestingGetViewer() {
        return viewer;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IIssuesList forTestingGetResolvedIssues() {
        return resolvedIssues;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IAction forTestingGetDeleteAction() {
        return deleteAction;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IAction forTestingGetDeleteGroupAction() {
        return deleteGroupAction;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IAction forTestingGetFixAction() {
        return fixAction;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IAction forTestingGetRefreshButton() {
        return refreshAction;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public IAction forTestingGetSaveButton() {
        return saveAction;
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public void forTestingShowResolvedssues(boolean show) {
        showResolvedssues(show);
    }

    /**
     * <b>DO NOT USE THIS IS PUBLIC FOR TESTING PURPOSES ONLY</b>
     */
    public void forTestingSetTesting() {
        testing = true;
    }

    /**
     * Set to true when we are internally setting the selection and don't want to be renotified.
     */
    private boolean settingSelection = false;

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        setSelection(selection);
    }

    /**
     * Sets the selection on the view
     *
     * @param selection the new selection, should be issues.
     */
    public void setSelection(ISelection selection) {
        if (settingSelection)
            return;
        if (selection instanceof IStructuredSelection) {
            List<IIssue> newselection = new LinkedList<>();
            IStructuredSelection sel = (IStructuredSelection) selection;
            for (Iterator iter = sel.iterator(); iter.hasNext();) {
                Object selected = iter.next();
                if (selected instanceof IIssue) {
                    IIssue issue = (IIssue) selected;
                    newselection.add(issue);
                } else if (selected instanceof String) {
                    newselection.addAll(getCurrentInputList().getIssues((String) selected));
                }
            }
            try {
                settingSelection = true;
                viewer.setSelection(new StructuredSelection(newselection));
            } finally {
                settingSelection = false;
            }
        }
    }

    private IIssuesList getCurrentInputList() {
        return ((IIssuesList) viewer.getInput());
    }

    /**
     * @author jones
     *
     */
    public static class IssuesTreeViewer extends TreeViewer {

        public IssuesTreeViewer(Composite parent, int style) {
            super(parent, style);
        }

        boolean canfindItem(Object item) {
            return findItem(item) != null;
        }

        Widget findTreeItem(Object item) {
            return super.findItem(item);
        }

    }

    public static int columnToIndex(Column column) {
        switch (column) {
        case DESCRIPTION:
            return DESC_COLUMN;
        case PROBLEM_OBJECT:
            return OBJECT_COLUMN;
        case PRIORITY:
            return PRIORITY_COLUMN;
        case RESOLUTION:
            return RESOLUTION_COLUMN;
        default:
            return -1;
        }
    }

    private static Column toColumn(int index2) {
        switch (index2) {
        case DESC_COLUMN:
            return Column.DESCRIPTION;
        case OBJECT_COLUMN:
            return Column.PROBLEM_OBJECT;
        case PRIORITY_COLUMN:
            return Column.PRIORITY;

        case RESOLUTION_COLUMN:
            return Column.RESOLUTION;

        default:
            return null;
        }
    }

    public void setExpansionProvider(IIssuesExpansionProvider provider) {
        expansionProvider = provider;
        doExpand();
    }

    public void setLabelProvider(IIssuesLabelProvider provider) {

        String headerText = provider.getHeaderText(Column.PROBLEM_OBJECT);
        problemObjectColumn.setText(headerText == null ? "" : headerText); //$NON-NLS-1$
        headerText = provider.getHeaderText(Column.DESCRIPTION);
        descriptionColumn.setText(headerText == null ? "" : headerText); //$NON-NLS-1$
        headerText = provider.getHeaderText(Column.PRIORITY);
        priorityColumn.setText(headerText == null ? "" : headerText); //$NON-NLS-1$
        headerText = provider.getHeaderText(Column.RESOLUTION);
        resolutionColumn.setText(headerText == null ? "" : headerText); //$NON-NLS-1$

        viewer.setLabelProvider(provider);
    }

    public void setContentProvider(IIssuesContentProvider provider) {
        viewer.setContentProvider(provider);
        if (showGroupAction != null)
            showGroupAction.setEnabled(provider instanceof IssuesContentProvider);
        doExpand();
    }

    public IIssuesExpansionProvider getExpansionProvider() {
        return expansionProvider;
    }

    /**
     * Refreshes the viewer.
     *
     * @param updateLabels true if labels should be updated
     */
    void refresh(boolean updateLabels) {
        viewer.refresh(updateLabels);
        doExpand();
    }

    private void doExpand() {
        Tree tree = viewer.getTree();

        TreeItem[] children = tree.getItems();
        for (TreeItem item : children) {
            doExpand(item);
        }
    }

    private void doExpand(TreeItem item) {
        int autoExpandLevel = viewer.getAutoExpandLevel();
        if (autoExpandLevel == TreeViewer.ALL_LEVELS)
            autoExpandLevel = Integer.MAX_VALUE;

        if (depth(item, 0) >= autoExpandLevel)
            item.setExpanded(expansionProvider.expand(viewer, item, item.getData()));
    }

    private int depth(TreeItem item, int depth) {
        if (item.getParentItem() != null)
            return depth(item.getParentItem(), depth + 1);
        return 0;
    }

    /**
     * Refreshes the viewer
     *
     * @param element element to refresh
     * @param updateLabels true if labels should be updated
     */
    void refresh(Object element, boolean updateLabels) {
        viewer.refresh(element, updateLabels);
        Widget findTreeItem = viewer.findTreeItem(element);
        if (findTreeItem instanceof TreeItem)
            doExpand((TreeItem) findTreeItem);
        else
            doExpand();
    }

    /**
     * Only for testing purposes.
     */
    public void testingAddListeners() {
        IIssuesList list = IIssuesManager.defaultInstance.getIssuesList();
        list.addListener(this.issuesListener);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        try {
            IIssuesManager.defaultInstance.save(monitor);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException(
                    org.locationtech.udig.issues.internal.Messages.IssuesView_saveError)
                            .initCause(e);
        }
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return IIssuesManager.defaultInstance.isDirty();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return true;
    }

    @Override
    public int promptToSaveOnClose() {
        return ISaveablePart2.YES;
    }

}
