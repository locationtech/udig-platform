package net.refractions.udig.style.sld.editor.internal;
/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * History for navigating preference pages.
 * 
 * @since 3.1
 */
class PageHistoryHolder {

    /**
     * The history toolbar.
     */
    private ToolBarManager historyToolbar;

    /**
     * A list of preference history domain elements that stores the history of
     * the visited preference pages.
     */
    private List<PageHistoryEntry> history = new ArrayList<PageHistoryEntry>();

    /**
     * Stores the current entry into <code>history</code> and
     * <code>historyLabels</code>.
     */
    private int historyIndex = -1;

    /**
     * The preference dialog we implement the history for.
     */
    private final FilteredEditorDialog dialog;

    /**
     * The handler submission for these controls.
     */
    private Set<IHandlerActivation> activations = new HashSet<IHandlerActivation>();

    /**
     * Creates a new history for the given dialog.
     * 
     * @param dialog
     *            the preference dialog to create a history for
     */
    public PageHistoryHolder(FilteredEditorDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Returns the preference page path (for now: its id) for the history at
     * <code>index</code>.
     * 
     * @param index
     *            the index into the history
     * @return the preference page path at <code>index</code> or
     *         <code>null</code> if <code>index</code> is not a valid
     *         history index
     */
    private PageHistoryEntry getHistoryEntry(int index) {
        if (index >= 0 && index < history.size())
            return (PageHistoryEntry) history.get(index);
        return null;
    }

    /**
     * Adds the preference page path and its label to the page history.
     * 
     * @param entry
     *            the preference page history entry
     */
    public void addHistoryEntry(PageHistoryEntry entry) {
        if (historyIndex == -1 || !history.get(historyIndex).equals(entry)) {
            history.subList(historyIndex + 1, history.size()).clear();
            history.add(entry);
            historyIndex++;
            updateHistoryControls();
        }
    }

    /**
     * Sets the current page to be the one corresponding to the given index in
     * the page history.
     * 
     * @param index
     *            the index into the page history
     */
    private void jumpToHistory(int index) {
        if (index >= 0 && index < history.size()) {
            historyIndex = index;
            dialog.setCurrentPageId(getHistoryEntry(index).getId());
        }
        updateHistoryControls();
    }

    /**
     * Updates the history controls.
     * 
     */
    private void updateHistoryControls() {
        historyToolbar.update(false);
        IContributionItem[] items = historyToolbar.getItems();
            for (int i = 0; i < items.length; i++) {
                items[i].update(IAction.ENABLED);
                items[i].update(IAction.TOOL_TIP_TEXT);
            }
        }

        /**
         * Creates the history toolbar and initializes <code>historyToolbar</code>.
         * 
         * @param historyBar
         * @param manager
         * @return the control of the history toolbar
         */
        public ToolBar createHistoryControls(ToolBar historyBar,
                ToolBarManager manager) {

            historyToolbar = manager;
            /**
             * Superclass of the two for-/backward actions for the history.
             */
            abstract class HistoryNavigationAction extends Action implements
                    IMenuCreator {
                private Menu lastMenu;

                protected final static int MAX_ENTRIES = 5;

                HistoryNavigationAction() {
                    super("", IAction.AS_DROP_DOWN_MENU); //$NON-NLS-1$
                }

                @Override
                public IMenuCreator getMenuCreator() {
                    return this;
                }

                public void dispose() {
                    if (lastMenu != null) {
                        lastMenu.dispose();
                        lastMenu = null;
                    }
                }

                public Menu getMenu(Control parent) {
                    if (lastMenu != null) {
                        lastMenu.dispose();
                    }
                    lastMenu = new Menu(parent);
                    createEntries(lastMenu);
                    return lastMenu;

                }

                public Menu getMenu(Menu parent) {
                    return null;
                }

                protected void addActionToMenu(Menu parent, IAction action) {
                    ActionContributionItem item = new ActionContributionItem(action);
                    item.fill(parent, -1);
                }

                protected abstract void createEntries(Menu menu);
            }

            /**
             * Menu entry for the toolbar dropdowns. Instances are direct-jump
             * entries in the navigation history.
             */
            class HistoryItemAction extends Action {

                private final int index;

                HistoryItemAction(int index, String label) {
                    super(label, IAction.AS_PUSH_BUTTON);
                    this.index = index;
                }

                @Override
                public void run() {
                    jumpToHistory(index);
                }
            }

            HistoryNavigationAction backward = new HistoryNavigationAction() {
                @Override
                public void run() {
                    jumpToHistory(historyIndex - 1);
                }

                @Override
                public boolean isEnabled() {
                    boolean enabled = historyIndex > 0;
                    if (enabled)
                        setToolTipText(NLS.bind(WorkbenchMessages.NavigationHistoryAction_backward_toolTipName,getHistoryEntry(historyIndex - 1).getLabel() )); 
                    return enabled;
                }

                @Override
                protected void createEntries(Menu menu) {
                    int limit = Math.max(0, historyIndex - MAX_ENTRIES);
                    for (int i = historyIndex - 1; i >= limit; i--) {
                        IAction action = new HistoryItemAction(i,
                                getHistoryEntry(i).getLabel());
                        addActionToMenu(menu, action);
                    }
                }
            };
            backward.setText(WorkbenchMessages.NavigationHistoryAction_backward_text); 
            backward
                    .setActionDefinitionId("org.eclipse.ui.navigate.backwardHistory"); //$NON-NLS-1$
            backward.setImageDescriptor(WorkbenchPlugin.getDefault()
                    .getSharedImages().getImageDescriptor(
                            ISharedImages.IMG_TOOL_BACK));
            registerKeybindings(backward);
            historyToolbar.add(backward);

            HistoryNavigationAction forward = new HistoryNavigationAction() {
                @Override
                public void run() {
                    jumpToHistory(historyIndex + 1);
                }

                @Override
                public boolean isEnabled() {
                    boolean enabled = historyIndex < history.size() - 1;
                    if (enabled)
                        setToolTipText(NLS.bind(WorkbenchMessages.NavigationHistoryAction_forward_toolTipName, getHistoryEntry(historyIndex + 1).getLabel() )); 
                    return enabled;
                }

                @Override
                protected void createEntries(Menu menu) {
                    int limit = Math.min(history.size(), historyIndex + MAX_ENTRIES
                            + 1);
                    for (int i = historyIndex + 1; i < limit; i++) {
                        IAction action = new HistoryItemAction(i,
                                getHistoryEntry(i).getLabel());
                        addActionToMenu(menu, action);
                    }
                }
            };
            forward.setText(WorkbenchMessages.NavigationHistoryAction_forward_text); 
            forward.setActionDefinitionId("org.eclipse.ui.navigate.forwardHistory"); //$NON-NLS-1$
            forward.setImageDescriptor(WorkbenchPlugin.getDefault()
                    .getSharedImages().getImageDescriptor(
                            ISharedImages.IMG_TOOL_FORWARD));
            registerKeybindings(forward);
            historyToolbar.add(forward);

            return historyBar;
        }

        /**
         * Registers the given action with the workbench command support.
         * 
         * @param action
         *            the action to register.
         */
        private void registerKeybindings(IAction action) {
            final IHandler handler = new ActionHandler(action);
            final IHandlerService handlerService = (IHandlerService) PlatformUI
                    .getWorkbench().getAdapter(IHandlerService.class);
            final IHandlerActivation activation = handlerService.activateHandler(
                    action.getActionDefinitionId(), handler);
//                    new ActiveShellExpression(dialog.getShell()),
//                    ISources.ACTIVE_SHELL);
            activations.add(activation);
        }

        /**
         * Dispose the receiver and clear out the references.
         *
         */
        public void dispose() {
            final IHandlerService handlerService = (IHandlerService) PlatformUI
                    .getWorkbench().getAdapter(IHandlerService.class);
            final Iterator iterator = activations.iterator();
            while (iterator.hasNext()) {
                handlerService.deactivateHandler((IHandlerActivation) iterator
                        .next());
            }
            activations.clear();
            
        }

        /**
         * Create the history control in the parent
         * 
         * @param parent
         * @return Control
         */
        public Control createHistoryControls(Composite parent) {
            ToolBar historyBar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL);

            ToolBarManager historyManager = new ToolBarManager(historyBar);

            createHistoryControls(historyBar, historyManager);
            return historyBar;
        }
}
