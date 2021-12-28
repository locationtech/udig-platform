/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.ui;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.CatalogTreeViewer;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.StatusLineMessageBoardAdapter;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.internal.ui.IDropTargetProvider;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.UDIGDragDropUtilities;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Catalog view for visualization and management if resources.
 * <p>
 * This class will be rather heavy on documentation, because it central (literally) to the uDig
 * application, and because it is one of the first views we are creating.
 * </p>
 * <p>
 * Of Note:
 * <ul>
 * <li>The catalog is strange in that there is only *one*, represent a global registry of all the
 * data sources in use by any uDig plugins.</li>
 * </ul>
 * </p>
 * From the requirements document (where CatalogView is known as LocalCatalog): <i>The Local Catalog
 * serves as a central repository of data and server information.
 * <p>
 * Non-Functional Requirements:
 * <ul>
 * <li><b>Ease of Data Location </b>, intent is for the user to be separated from the data source;
 * so they need as little technical knowledge as possible.
 * <li><b>Security </b>, name/password should be left out of the export/share.
 * </ul>
 * </p>
 * <p>
 * Functional Requirements:
 * <ul>
 * <li><b>Servers </b>, store server connection information for sharing between projects.
 * <li><b>Data Directories </b>, store data directories for sharing between projects.
 * <li><b>Metadata </b>, provide access to metadata on Servers/Data Directories.
 * <li><b>Data Discovery </b>, provide enough information for a user to define a new layer in their
 * context.
 * <li><b>Persist Settings </b>, permit exporting and sharing DataStore connection information.
 * <li><b>DataStores Management </b>, lookup actualized DataStores that are in use.
 * <li><b>Missing Data </b>, entries referred to by imported projects should be maintained, allowing
 * the user one location to correct data connection information.
 * </ul>
 * </p>
 * <p>
 * Catalog View has a strong interaction with the preferences maintained by the local installation
 * of uDig for the current user.
 * </p>
 */
public class CatalogView extends ViewPart implements ISetSelectionTarget, IDropTargetProvider {

    /** <code>VIEW_ID</code> field */
    public static final String VIEW_ID = "org.locationtech.udig.catalog.ui.CatalogView"; //$NON-NLS-1$

    CatalogTreeViewer treeviewer;

    Action removeAction; // addAction

    private Action saveAction;

    private Action loadAction;

    private Action propertiesAction;

    /**
     * Creates the SWT controls for this workbench part.
     * <p>
     * The details (from IWorkbenchPart.createPartControl( Composite ))
     * </p>
     * <p>
     * Multi-step process:
     * <ol>
     * <li>Create one or more controls within the parent.</li>
     * <li>Set the parent layout as needed.</li>
     * <li>Register any global actions with the <code>IActionService</code>.</li>
     * <li>Register any pop-up menus with the <code>IActionService</code>.</li>
     * <li>Register a selection provider with the <code>ISelectionService</code> (optional).</li>
     * </ol>
     * </p>
     *
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        // create viewer
        treeviewer = new CatalogTreeViewer(parent, true);
        treeviewer.setMessageBoard(new StatusLineMessageBoardAdapter(
                getViewSite().getActionBars().getStatusLineManager()));

        UDIGDragDropUtilities.addDragDropSupport(treeviewer, this);

        getSite().setSelectionProvider(treeviewer);
        // Create menu and toolbars
        createActions();
        createMenu();
        createToolbar();
        createContextMenu();
        hookGlobalActions();

        // restore state (from previous session)

    }

    /**
     * We need to hook up to a few global actions such as Properties and Delete.
     * <ul>
     * <li>
     */
    protected void hookGlobalActions() {
        getViewSite().getActionBars().setGlobalActionHandler(IWorkbenchActionConstants.PROPERTIES,
                propertiesAction);
    }

    private void createContextMenu() {
        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(new Separator());
                contextMenu.add(removeAction);
                IWorkbenchWindow window = getSite().getWorkbenchWindow();
                IAction action = ActionFactory.IMPORT.create(window);
                contextMenu.add(action);
                contextMenu.add(new Separator());
                contextMenu.add(UiPlugin.getDefault().getOperationMenuFactory()
                        .getContextMenu(treeviewer.getSelection()));
                contextMenu.add(new Separator());
                contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(treeviewer.getControl());
        treeviewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, treeviewer);

    }

    /**
     * Create a few actions such as add, remove, properties and so on.
     * <p>
     * These properties will be registered in our view menu, as global handlers and so forth.
     * </p>
     */
    private void createActions() {
        propertiesAction = new PropertyDialogAction(getViewSite().getWorkbenchWindow(), treeviewer);

        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
                propertiesAction);
        getSite().getKeyBindingService().registerAction(propertiesAction);

        removeAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selected = (IStructuredSelection) treeviewer.getSelection();
                removeSelected(selected);
            }
        };

        Messages.initAction(removeAction, "action_remove"); //$NON-NLS-1$
        removeAction.setEnabled(false);
        removeAction.setImageDescriptor(
                CatalogUIPlugin.getDefault().getImageDescriptor(ImageConstants.REMOVE_CO));
        removeAction.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(),
                removeAction);
        getSite().getKeyBindingService().registerAction(removeAction);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(removeAction,
                IHelpContextIds.REMOVE_SERVICE_ACTION);

        saveAction = new Action(Messages.CatalogView_save_label) {
            @Override
            public void run() {
                try {
                    CatalogPlugin.getDefault().storeToPreferences(ProgressManager.instance().get());
                } catch (BackingStoreException e) {
                    CatalogPlugin.log(null, e);
                } catch (IOException e) {
                    CatalogPlugin.log(null, e);
                }
            }
        };

        loadAction = new Action(Messages.CatalogView_load_label) {
            @Override
            public void run() {
                try {
                    CatalogPlugin.getDefault().restoreFromPreferences();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Add selection listener.
        treeviewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateActionEnablement();
            }
        });
    }

    void registerDatasource() {
        // Call to wizard here...
    }

    protected void showProperties(IStructuredSelection selected) {
        if (selected.isEmpty())
            return; // action should of been disabled!
        Object content = selected.getFirstElement();

        for (Iterator iter = selected.iterator(); iter.hasNext();) {
            @SuppressWarnings("all")
            Object o = iter.next();
            if (o instanceof IService)
                remove((IService) o);
            else if (o instanceof IGeoResource)
                remove((IGeoResource) o);
        }
    }

    /**
     * Remove selected stuff from the catalog.
     * <p>
     * Please note that this just smacks the Catalog; any Maps or Pages holding references to this
     * Service will just be confused. The even is sent out but chances are they may just recreate
     * this Service from scratch next time they are opened.
     * </p>
     * So if this Service was in use chances are it will just pop back in again.
     *
     * @see remove( IService )
     * @see remove( IGeoResource )
     */
    protected void removeSelected(IStructuredSelection selected) {
        // Free selected data source - but only if it is not
        // in use...

        for (Iterator iter = selected.iterator(); iter.hasNext();) {
            @SuppressWarnings("all")
            Object o = iter.next();
            if (o instanceof IService)
                remove((IService) o);
            else if (o instanceof IGeoResource)
                remove((IGeoResource) o);
        }
    }

    /**
     * Straight call of CatalogPlugin.getDefault().getLocalCatalog().remove( service )
     *
     * @param service
     */
    private void remove(IService service) {
        CatalogPlugin.getDefault().getLocalCatalog().remove(service);
    }

    /**
     * Will remove the service of the selected resource.
     * <p>
     * We may try doing something more smart here on a service by service basis.
     *
     * @param georesource
     */
    private void remove(IGeoResource georesource) {
        try {
            remove(georesource.service(null));
        } catch (IOException e) {
            CatalogUIPlugin.log(null, e);
        }
    }

    void updateActionEnablement() {
        IStructuredSelection sel = (IStructuredSelection) treeviewer.getSelection();
        if (sel.isEmpty()) {
            removeAction.setEnabled(false);
            propertiesAction.setEnabled(false);
        } else {
            removeAction.setEnabled(true);
            propertiesAction.setEnabled(true);
        }
    }

    /**
     * Create menu with refresh option.
     */
    private void createMenu() {
        IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        mgr.add(saveAction);
        mgr.add(loadAction);
    }

    /**
     * Create toolbar with new and delete buttons.
     */
    private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();

        IWorkbenchWindow window = getSite().getWorkbenchWindow();
        IAction action = ActionFactory.IMPORT.create(window);

        action.setImageDescriptor(CatalogUIPlugin.getDefault()
                .getImageDescriptor(ImageConstants.PATH_ETOOL + "import_wiz.gif")); //$NON-NLS-1$
        mgr.add(action);

        mgr.add(removeAction);
    }

    /**
     * Asks this view take focus within the workbench.
     * <p>
     * From IWorkbenchPart: Clients should not call this method (the workbench calls this method at
     * appropriate times). To have the workbench activate a part, use
     * <code>IWorkbenchPage.activate(IWorkbenchPart) instead</code>.
     * </p>
     * <p>
     * Used to set the focus to the appropriate control, for us that is the treeviewer. But if we
     * were smart we could send the user off to a search field or something they actually need (like
     * a broken datastore) based on context.
     * </p>
     */
    @Override
    public void setFocus() {
        treeviewer.getControl().setFocus();
    }

    /**
     * @return Returns the TreeViewer.
     */
    public CatalogTreeViewer getTreeviewer() {
        return treeviewer;
    }

    @Override
    public void selectReveal(ISelection selection) {
        treeviewer.setSelection(selection, true);
    }

    @Override
    public Object getTarget(DropTargetEvent event) {
        return this;
    }
}
