/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.core.filter.AdaptingFilterFactory;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.internal.ui.UDIGDNDProcessor;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.internal.ui.UDigByteAndLocalTransfer;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.internal.ui.operations.OperationCategory;
import org.locationtech.udig.internal.ui.operations.OperationMenuFactory;
import org.locationtech.udig.internal.ui.operations.RunOperationsAction;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.actions.Delete;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.project.ui.internal.tool.util.ToolManagerUtils;
import org.locationtech.udig.project.ui.tool.IContextMenuContributionTool;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.Tool;
import org.locationtech.udig.project.ui.tool.ToolConstants;
import org.locationtech.udig.project.ui.tool.options.PreferencesShortcutToolOptionsContributionItem;
import org.locationtech.udig.project.ui.viewers.MapEditDomain;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.IDropHandlerListener;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.UDIGDragDropUtilities;
import org.locationtech.udig.ui.ViewerDropLocation;
import org.locationtech.udig.ui.operations.LazyOpFilter;
import org.locationtech.udig.ui.operations.OpAction;
import org.locationtech.udig.ui.operations.OpFilter;
import org.opengis.filter.Filter;

/**
 * Manages Edit tools activation and registration.
 * <p>
 * The tool manager is a used by the MapEditor to populate the menu and action bars. It is
 * responsible for processing the tools extension point and making action contributions as needed.
 * </p>
 * <p>
 * New for uDig 1.1:
 * <ul>
 * <li>We will check for an ActionSet with the same name as the tool category, this will allow you
 * to turn off actions that don't make sense for your current workflow (ie perspective change).</li>
 * </ul>
 * </p>
 *
 * @author Jesse Eichar (Refractions Research Inc)
 * @since 0.6.0
 */
public class ToolManager implements IToolManager {

    private static final boolean FT_ACTION_TOOL_PREF_LINKS = false;

    /**
     * This is a list of all tool actions(buttons) that are not part of the editor toolbar. For
     * example the info view may have a tool as part of its toolbar which is a proxy for the real
     * tool on the editor view.
     */
    Set<IAction> registeredToolActions = new HashSet<>();

    /**
     * List of categorieIds; these may or may not be associated with an ActionSet.
     */
    protected List<String> categoryIds = new ArrayList<>();

    /**
     * These represent modal tools that complete take over the map.
     */
    List<ModalToolCategory> modalCategories = new LinkedList<>();

    /**
     * These represent fire and forget actions like zoomIn and zoomOut.
     */
    List<ActionToolCategory> actionCategories = new LinkedList<>();

    /**
     * These represent additions made to the menu.
     */
    List<MenuToolCategory> menuCategories = new LinkedList<>();

    /**
     * I think these are the tools that lurk in the background updating state like the status bar.
     */
    List<ToolProxy> backgroundTools = new LinkedList<>();

    /**
     * Shared images associated with these tools; used for everything from cursors to button icons.
     */
    ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

    /**
     * Cache of all configured cursors.
     */
    protected java.util.Map<String, CursorProxy> cursorsCache = new HashMap<>();

    /**
     * Proxy for the active tool (ie state of the editor).
     * <p>
     * The category of this tool will behave a little like a perspective; since the nature of the
     * editor changes completely with the current tool. As such the Edit menu may change what
     * actions are available based on what subject matter this tool is working on.
     * </p>
     */
    private ToolProxy activeModalToolProxy;

    /**
     * Current active tool; this represents the state of the editor.
     * <p>
     * This is considered an internal detail of ToolManager.
     * </p>
     */
    private ModalTool activeTool;

    /**
     * Default modal tool. This tool is set during initialization of tools by ToolProxy.DEFAULT_ID.
     */
    ToolProxy defaultModalToolProxy;

    Lock redoLock = new ReentrantLock();

    Lock undoLock = new ReentrantLock();

    Lock forwardLock = new ReentrantLock();

    Lock backwardLock = new ReentrantLock();

    Lock deleteLock = new ReentrantLock();

    Lock enterLock = new ReentrantLock();

    Lock pasteLock = new ReentrantLock();

    Lock propertiesLock = new ReentrantLock();

    Lock copyLock = new ReentrantLock();

    Lock cutLock = new ReentrantLock();

    private volatile IAction redoAction;

    private volatile IAction undoAction;

    private volatile IAction forwardAction;

    private volatile IAction backwardAction;

    private volatile IAction deleteAction;

    private volatile IAction enterAction;

    private volatile IAction zoomToSelectionAction;

    private volatile IAction pasteAction;

    private volatile IAction copyAction;

    private volatile IAction cutAction;

    private AdapterImpl commandListener;

    private List<ContributionItem> optionsContribution = new ArrayList<>();

    /**
     * Construct <code>ToolManager</code>.
     */
    public ToolManager() {
        processCategories();
        processTools();
        removeEmptyCategories();
        Collections.sort(categoryIds, new CategorySorter());
        setCommandHandlers();
    }

    /**
     * Populates the categories with their associated tools
     */
    private void processTools() {
        List<IConfigurationElement> extensionList = ExtensionPointList
                .getExtensionPointList(Tool.EXTENSION_ID);
        for (IConfigurationElement element : extensionList) {
            IExtension extension = element.getDeclaringExtension();
            String type = element.getName();

            if (type.equals("backgroundTool")) { //$NON-NLS-1$

                ToolProxy proxy = new ToolProxy(extension, element, this);
                backgroundTools.add(proxy);
            } else if (type.equals("modalTool")) { //$NON-NLS-1$
                String categoryId = getCategoryIdAttribute(element); // $NON-NLS-1$
                ToolProxy proxy = new ToolProxy(extension, element, this);

                addToModalCategory(categoryId, proxy);
            } else if (type.equals("actionTool")) { //$NON-NLS-1$
                String categoryId = getCategoryIdAttribute(element); // $NON-NLS-1$
                ToolProxy proxy = new ToolProxy(extension, element, this);

                addToActionCategory(categoryId, proxy);
                addToMenuCategory(categoryId, proxy);
            } else if (type.equals("toolCursor")) { //$NON-NLS-1$
                CursorProxy cursorProxy = new CursorProxy(element);
                cursorsCache.put(cursorProxy.getID(), cursorProxy);
            }
        }

        if (activeModalToolProxy == null) {
            activeModalToolProxy = defaultModalToolProxy;
        }
    }

    private String getCategoryIdAttribute(IConfigurationElement element) {
        String id = element.getAttribute("categoryId"); //$NON-NLS-1$
        return id == null ? "" : id; //$NON-NLS-1$
    }

    /**
     * Finds cursor proxy by ID in cache.
     */
    @Override
    public Cursor findToolCursor(String cursorID) {

        CursorProxy cursorProxy = cursorsCache.get(cursorID);
        if (cursorProxy != null)
            return cursorProxy.getCursor();

        Cursor systemCursor = CursorProxy.getSystemCursor(cursorID);
        if (systemCursor != null)
            return systemCursor;

        return null;

    }

    /**
     * Find a tool with the provided ID.
     * <p>
     * In the current implementation finds only among modal tools.
     * </p>
     * TODO Extend findTool to search for non modal tools
     *
     * @param toolID toolId to search for
     * @return Modal tool if found, or null
     */
    @Override
    public Tool findTool(String toolID) {
        for (ModalToolCategory category : modalCategories) {
            for (ModalItem item : category) {
                if (toolID.equals(item.getId())) {
                    return ((ToolProxy) item).getTool();
                }
            }
        }
        return null;
    }

    /**
     * Find a tool proxy with the provided ID.
     * <p>
     * This searches modal tools, background tools, and action tools
     * </p>
     *
     * @param toolID toolId to search for
     * @return ToolProxy of tool if found or null
     */
    public ToolProxy findToolProxy(String toolID) {
        for (ModalToolCategory category : modalCategories) {
            for (ModalItem item : category) {
                if (toolID.equals(item.getId())) {
                    return (ToolProxy) item;
                }
            }
        }
        for (ActionToolCategory category : actionCategories) {
            for (ModalItem item : category) {
                if (toolID.equals(item.getId())) {
                    return (ToolProxy) item;
                }
            }
        }
        for (ToolProxy item : backgroundTools) {
            if (toolID.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    private void addToModalCategory(String categoryId, ToolProxy proxy) {
        if (filterTool(categoryId, proxy, ModalToolCategory.class)) {
            return;
        }
        ModalToolCategory modalCategory = findModalCategory(categoryId);
        if (modalCategory == null) {
            modalCategory = findModalCategory(Messages.ToolCategory_other);
            if (modalCategory == null) {
                modalCategory = new ModalToolCategory(this);
                modalCategories.add(modalCategory);
                if (!categoryIds.contains(Messages.ToolCategory_other))
                    categoryIds.add(Messages.ToolCategory_other);
            }
        }
        modalCategory.add(proxy);
    }

    /**
     * This method is called each time an action is about to be added to a category. If the message
     * returns true the tool <b>will not</b> be added. The default implementation always returns
     * false.
     *
     * @param categoryId the id of the category that the tool will be added to, this will never be
     *        null
     * @param proxy the proxy for the tool.
     * @param categoryType the type of category
     *
     * @return true if the tool will NOT be added to the category
     */
    protected boolean filterTool(String categoryId, ToolProxy proxy,
            Class<? extends ToolCategory> categoryType) {
        return false;
    }

    private void addToMenuCategory(String categoryId, ToolProxy proxy) {

        if (filterTool(categoryId, proxy, MenuToolCategory.class)) {
            return;
        }
        MenuToolCategory category = findMenuCategory(categoryId);
        if (category == null) {
            category = findMenuCategory(Messages.ToolCategory_other);

            if (category == null) {
                category = new MenuToolCategory(this);
                menuCategories.add(category);
                if (!categoryIds.contains(Messages.ToolCategory_other))
                    categoryIds.add(Messages.ToolCategory_other);
            }
        }
        category.add(proxy);
    }

    private void addToActionCategory(String categoryId, ToolProxy proxy) {
        if (filterTool(categoryId, proxy, ActionToolCategory.class)) {
            return;
        }

        ActionToolCategory category = findActionCategory(categoryId);
        if (category == null) {
            category = findActionCategory(Messages.ToolCategory_other);

            if (category == null) {
                category = new ActionToolCategory(this);
                actionCategories.add(category);
                if (!categoryIds.contains(Messages.ToolCategory_other))
                    categoryIds.add(Messages.ToolCategory_other);
            }
        }
        category.add(proxy);
    }

    /**
     * Processes the extension point and creates all the categories.
     */
    private void processCategories() {
        List<IConfigurationElement> extension = ExtensionPointList
                .getExtensionPointList(Tool.EXTENSION_ID);

        for (IConfigurationElement element : extension) {
            if (!element.getName().equals("category")) //$NON-NLS-1$
                continue;
            ModalToolCategory modalCategory;
            String id = element.getAttribute("id"); //$NON-NLS-1$
            categoryIds.add(id);
            modalCategory = findModalCategory(id);
            if (modalCategory == null) {
                modalCategory = new ModalToolCategory(element, ToolManager.this);
                modalCategories.add(modalCategory);
            }
            ActionToolCategory actionCategory;
            actionCategory = findActionCategory(id);
            if (actionCategory == null) {
                actionCategory = new ActionToolCategory(element, ToolManager.this);
                actionCategories.add(actionCategory);
            }
            MenuToolCategory category;
            category = findMenuCategory(id);
            if (category == null) {
                category = new MenuToolCategory(element, ToolManager.this);
                menuCategories.add(category);
            }
        }
    }

    private void removeEmptyCategories() {
        List<ToolCategory> toRemove = new ArrayList<>();
        for (ActionToolCategory category : actionCategories) {
            if (category.items.isEmpty()) {
                toRemove.add(category);
            }
        }
        actionCategories.removeAll(toRemove);
        for (ModalToolCategory category : modalCategories) {
            if (category.items.isEmpty()) {
                toRemove.add(category);
            }
        }
        modalCategories.removeAll(toRemove);
        for (MenuToolCategory category : menuCategories) {
            if (category.items.isEmpty()) {
                toRemove.add(category);
            }
        }
        menuCategories.removeAll(toRemove);
    }

    /**
     * Register commands handlers; so they can be used by the keyboard short cut system.
     */
    private void setCommandHandlers() {
        Set<String> ids = new HashSet<>();
        ICommandService service = PlatformUI.getWorkbench().getAdapter(ICommandService.class);
        for (ModalToolCategory category : modalCategories) {
            if (!ids.contains(category.getId())) {
                ids.add(category.getId());
                category.setCommandHandlers(service);
            }
            registerCommands(ids, service, category);
        }
        for (ActionToolCategory category : actionCategories) {
            if (!ids.contains(category.getId())) {
                ids.add(category.getId());
                category.setCommandHandlers(service);
            }
            registerCommands(ids, service, category);
        }
        for (MenuToolCategory category : menuCategories) {
            if (!ids.contains(category.getId())) {
                ids.add(category.getId());
                category.setCommandHandlers(service);
            }
            registerCommands(ids, service, category);
        }
    }

    /**
     * Register commands; so they can be picked up by command handlers.
     * <p>
     * These command/handler system is used to hook our tools up to the keyboard short cut system.
     * </p>
     *
     * @param ids
     * @param service
     * @param category
     */
    private void registerCommands(Set<String> ids, ICommandService service, ToolCategory category) {
        for (ModalItem tool : category) {
            if (!ids.contains(tool.getId())) {
                ids.add(category.getId());

                for (String currentId : tool.getCommandIds()) {
                    currentId = currentId.trim();
                    Command command = service.getCommand(currentId);
                    if (command != null)
                        command.setHandler(tool.getHandler(currentId));
                }
            }
        }
    }

    MapPart currentEditor;

    /**
     * This method is called to perform tools initialization when the map editor is selected.
     */
    @Override
    public void setCurrentEditor(MapPart editor) {

        if (editor == currentEditor) {
            return;
        }
        currentEditor = editor;
        if (editor != null && editor.getMap() != null) {
            setActiveTool(editor);
            setEnabled(editor.getMap(), actionCategories);
            setEnabled(editor.getMap(), menuCategories);
            setEnabled(editor.getMap(), modalCategories);
        } else {
            disable(actionCategories);
            disable(menuCategories);
            disable(modalCategories);
        }

    }

    /**
     * Churn through the category disabling all tools.
     *
     * @param categories
     */
    private void disable(List<? extends ToolCategory> categories) {
        for (ToolCategory category : categories) {
            for (ModalItem item : category) {
                OpFilter enablesFor = item.getEnablesFor();
                if (enablesFor instanceof LazyOpFilter)
                    ((LazyOpFilter) enablesFor).disable();
            }
        }
    }

    EditManagerListener selectedLayerListener;

    private PropertyDialogAction propertiesAction;

    /**
     * Listener for EditManager.
     *
     * @author Vitalus
     *
     */
    class EditManagerListener implements IEditManagerListener {

        public void setCurrentMap(IMap map) {

        }

        @Override
        public void changed(EditManagerEvent event) {

            if (selectedLayerListener != this) {
                event.getSource().removeListener(this);
                return;
            }
            if (event.getType() == EditManagerEvent.SELECTED_LAYER) {
                setEnabled(event.getSource().getMap(), actionCategories);
                setEnabled(event.getSource().getMap(), menuCategories);
                setEnabled(event.getSource().getMap(), modalCategories);
            }
        }
    }

    /**
     * Heads through the categories giving each tool a chance to enable/disable itself.
     * <p>
     * Specifically we grab the OpFilter and give it a chance to determine if the tool is enabled;
     * currently OpFilter is focused on the selectedLayer but I hope to break this out to process
     * more general "core expressions" in the future (but we have to wait for someone to ask first).
     * </p>
     *
     * @param map
     * @param categories
     */
    private void setEnabled(final IMap map, final Collection<? extends ToolCategory> categories) {

        if (selectedLayerListener == null)
            selectedLayerListener = new EditManagerListener();

        selectedLayerListener.setCurrentMap(map);

        // One listener is enough. Say NO to listeners hell:)
        if (!map.getEditManager().containsListener(selectedLayerListener))
            map.getEditManager().addListener(selectedLayerListener);

        PlatformGIS.syncInDisplayThread(new Runnable() {

            @Override
            public void run() {
                ILayer selectedLayer = map.getEditManager().getSelectedLayer();

                for (ToolCategory cat : categories) {
                    for (ModalItem item : cat) {
                        OpFilter enablesFor = item.getEnablesFor();
                        if (!(enablesFor instanceof LazyOpFilter)) {
                            enablesFor = new LazyOpFilter(item, enablesFor);
                        }

                        boolean accept = enablesFor.accept(selectedLayer);
                        item.setEnabled(accept);
                    }
                }
            }
        });

    }

    /**
     * Sets the context of the currently active tool and ensures that all tools are enabled.
     * <p>
     * This is called by the "support views" associated with the MapEditor, it is used so the tools
     * can be active even when the MapEditor does not have the focus. Without this modification you
     * would need to constantly select the MapEditor, change the tool and then get to work.
     * </p>
     * <p>
     * Aside: it would be good if selecting a tool made the MapEditor grab focus.
     * </p>
     *
     * @param editor MapEditor associated with the support view (such as the Layers view)
     */
    void setActiveTool(MapPart editor) {
        // ensure we are listening to this MapPart's Map
        Map map = editor.getMap();
        Adapter listener = getCommandListener(editor);
        if (map.eAdapters() != null && !map.eAdapters().contains(listener)) {
            map.eAdapters().add(listener);
        }

        // Define the tool context allowing tools to interact with this map
        ToolContext toolContext = new ToolContextImpl();
        toolContext.setMapInternal(map);

        // Provide each tool with the new tool context
        setContext(modalCategories, toolContext); // if active a modal tool is supposed to register
                                                  // listeners
        setContext(actionCategories, toolContext);
        setContext(menuCategories, toolContext);
        for (ToolProxy tool : backgroundTools) {
            tool.setContext(toolContext);
        }
        for (IAction action : registeredToolActions) {
            action.setEnabled(true);
        }

        setCommandActions(map);

        // wire in the current activeModalTool
        if (activeModalToolProxy != null) {
            if (!activeModalToolProxy.isActive()) {
                // work around to allow the 1st modal tool to be active
                if (activeTool == null) {
                    activeTool = activeModalToolProxy.getModalTool();
                    // add tool options to the status area
                    if (FT_ACTION_TOOL_PREF_LINKS) {
                        initToolOptionsContribution(editor.getStatusLineManager(),
                                activeModalToolProxy);
                    }
                }
                activeModalToolProxy.getModalTool().setActive(true);
            }
            activeModalToolProxy.setChecked(true);
            editor.setSelectionProvider(activeModalToolProxy.getSelectionProvider());
            if (editor instanceof MapEditorWithPalette) {
                // temporary cast while we sort out if MapPart can own an MapEditDomain
                MapEditorWithPalette editor2 = (MapEditorWithPalette) editor;
                MapEditDomain editDomain = editor2.getEditDomain();
                editDomain.setActiveTool(activeModalToolProxy.getId());
            }

        }
    }

    /**
     * Go through List of ToolCategory and update each Tool with the new tool context.
     *
     * @param categories
     * @param tools
     */
    private void setContext(List<? extends ToolCategory> categories, ToolContext tools) {
        for (ToolCategory category : categories) {
            for (ModalItem item : category.items) {
                ToolProxy tool = (ToolProxy) item;
                tool.setContext(tools);
            }
        }
    }

    /**
     * Adds an Action that executes a tool to the toolbar.
     *
     * @param action
     */
    @Override
    public void addToolAction(IAction action) {
        registeredToolActions.add(action);
        action.setEnabled(ApplicationGIS.getActiveMap() != ApplicationGIS.NO_MAP);
    }

    /**
     * Creates a action that acts as a proxy for the tool in the editor toolbar.
     * <p>
     * The client code must set the name image descriptor etc... of the Action
     * </p>
     *
     * @param toolID the id of the tool
     * @param categoryID the category the tool is part of
     * @return a proxy action that can be put in other toolbars
     */
    @Override
    public IAction createToolAction(final String toolID, final String categoryID) {
        final IAction toolAction = new Action() {
            @Override
            public void runWithEvent(Event event) {
                IAction action = getTool(toolID, categoryID);
                if (action != null && action.isEnabled()) {
                    action.runWithEvent(event);
                }
            }
        };
        toolAction.addPropertyChangeListener(new IPropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(IAction.ENABLED)) {
                    toolAction.setEnabled((Boolean) event.getNewValue());
                }
            }

        });
        toolAction.setEnabled(getTool(toolID, categoryID).isEnabled());
        addToolAction(toolAction);
        return toolAction;
    }

    @Override
    public final ActionToolCategory findActionCategory(String id) {
        for (ActionToolCategory category : actionCategories) {
            if (category.getId().equals(id))
                return category;
        }
        return null;
    }

    @Override
    public final MenuToolCategory findMenuCategory(String id) {
        for (MenuToolCategory category : menuCategories) {
            if (category.getId().equals(id))
                return category;
        }
        return null;
    }

    @Override
    public final ModalToolCategory findModalCategory(String id) {
        for (ModalToolCategory category : modalCategories) {
            String id2 = category.getId();
            if (id2.equals(id))
                return category;
        }
        return null;
    }

    /**
     * Used to contribute Tools to the provided menu manger.
     * <p>
     * The following contributions are made:
     * <ul>
     * <li>navigate: forward and backward buttons
     * <li>map: an entry for each tool category
     * </ul>
     * </p>
     */
    @Override
    public void contributeToMenu(IMenuManager manager) {
        IMenuManager navigateMenu = manager.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);

        if (navigateMenu == null) {
            // I would like to arrange for the Navigate menu to already
            // be in place before the ToolManager is kicked into action
            // (this is part of the missions to have uDig plugins walk
            // softly when being hosted in other RCP applications)
            // See UDIGActionBarAdvisor for hosting requirements.
            navigateMenu = new MenuManager(Messages.ToolManager_menu_manager_title,
                    IWorkbenchActionConstants.M_NAVIGATE);

            IContributionItem additions = manager.find(IWorkbenchActionConstants.MB_ADDITIONS);
            if (additions == null || !(additions instanceof GroupMarker)) {
                manager.add(navigateMenu);
            } else {
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, navigateMenu);
            }
            navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.NAV_START));
            navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.NAV_END));
        }
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        // we are using the global BACK and FORWARD actions here
        // and will register "handlers" for these commands
        navigateMenu.appendToGroup(IWorkbenchActionConstants.NAV_END,
                ActionFactory.BACK.create(window));
        navigateMenu.appendToGroup(IWorkbenchActionConstants.NAV_END,
                ActionFactory.FORWARD.create(window));

        if (!manager.isVisible()) {
            // since this is the top level menu bar why would it not be visible?
            manager.setVisible(true);
        }
        IMenuManager mapMenu = manager.findMenuUsingPath("map"); //$NON-NLS-1$
        if (mapMenu == null) {
            // Once again the hosting RCP application should of provided
            // us with a Map menu; but let's be careful and make our own here
            // if needed.
            // See UDIGActionBarAdvisor for hosting requirements.
            mapMenu = new MenuManager(Messages.ToolManager_menu_manager_title, "map"); //$NON-NLS-1$
            manager.add(mapMenu);
            mapMenu.add(new GroupMarker("mapStart")); //$NON-NLS-1$
            mapMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            mapMenu.add(new GroupMarker("mapEnd")); //$NON-NLS-1$
        }
        // churn through each category and add stuff as needed
        // note we check with the cmdService to see what we if the actionSet
        // associated with this category is turned on by the
        // current perspective.
        for (MenuToolCategory category : menuCategories) {
            category.contribute(manager);
        }
    }

    @Override
    public void contributeActiveModalTool(IMenuManager manager) {

        Tool activeTool = getActiveTool();
        if (activeTool instanceof IContextMenuContributionTool) {
            IContextMenuContributionTool contributionTool = (IContextMenuContributionTool) activeTool;
            ArrayList<IContributionItem> contributions = new ArrayList<>();
            contributionTool.contributeContextMenu(contributions);

            if (!contributions.isEmpty()) {
                manager.add(new Separator());
                for (IContributionItem item : contributions) {
                    manager.add(item);
                }
            }
        }
    }

    /**
     * Retrieves the redo action that is used by much of the map components such as the MapEditor
     * and the LayersView. Redoes the last undone command sent to the currently active map.
     */
    @Override
    public IAction getREDOAction() {
        Map activeMap = ApplicationGISInternal.getActiveMap();
        redoLock.lock();
        try {
            if (redoAction == null) {
                redoAction = new Action() {
                    @Override
                    public void run() {
                        Map activeMap = ApplicationGISInternal.getActiveMap();
                        if (activeMap != ApplicationGIS.NO_MAP)
                            activeMap.redo();
                    }
                };
                redoAction.setImageDescriptor(
                        sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
                redoAction.setText(Messages.ToolManager_redoAction);
                redoAction.setActionDefinitionId("org.eclipse.ui.edit.redo"); //$NON-NLS-1$
            }
            setActionEnabledState(activeMap, redoAction, true);
            return redoAction;
        } finally {
            redoLock.unlock();
        }
    }

    @Override
    public void setREDOAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        redoLock.lock();
        try {
            redoAction = action;
            redoAction.setActionDefinitionId("org.eclipse.ui.edit.redo"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(redoAction);
        } finally {
            redoLock.unlock();
        }
    }

    /**
     * Retrieves the undo action that is used by much of the map components such as the MapEditor
     * and the LayersView. Undoes the last command sent to the currently active map.
     *
     * @param part
     */
    @Override
    public IAction getUNDOAction() {
        Map activeMap = ApplicationGISInternal.getActiveMap();
        undoLock.lock();
        try {
            if (undoAction == null) {
                undoAction = new Action() {
                    @Override
                    public void run() {
                        Map activeMap = ApplicationGISInternal.getActiveMap();
                        if (activeMap != ApplicationGIS.NO_MAP)
                            activeMap.undo();
                    }
                };
                undoAction.setImageDescriptor(
                        sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
                undoAction.setText(Messages.ToolManager_undoAction);
                undoAction.setActionDefinitionId("org.eclipse.ui.edit.undo"); //$NON-NLS-1$
            }
            setActionEnabledState(activeMap, undoAction, false);
            return undoAction;
        } finally {
            undoLock.unlock();
        }
    }

    @Override
    public void setUNDOAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        undoLock.lock();
        try {
            undoAction = action;
            undoAction.setActionDefinitionId("org.eclipse.ui.edit.undo"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(undoAction);
        } finally {
            undoLock.unlock();
        }
    }

    /**
     * Retrieves the forward navigation action that is used by much of the map components such as
     * the MapEditor and the LayersView. Executes the last undone Nav command on the current map.
     */
    @Override
    public IAction getFORWARD_HISTORYAction() {
        Map activeMap = ApplicationGISInternal.getActiveMap();
        forwardLock.lock();
        try {
            if (forwardAction == null) {
                forwardAction = new Action() {
                    @Override
                    public void run() {
                        Map activeMap = ApplicationGISInternal.getActiveMap();
                        if (activeMap != ApplicationGIS.NO_MAP)
                            activeMap.forwardHistory();
                    }
                };
                forwardAction.setImageDescriptor(
                        sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
                forwardAction.setText(Messages.ToolManager_forward);
                forwardAction.setToolTipText(Messages.ToolManager_forward_tooltip);
                forwardAction.setActionDefinitionId("org.eclipse.ui.navigate.forward"); //$NON-NLS-1$
            }
            setNavActionEnabledState(activeMap, forwardAction, true);
            return forwardAction;
        } finally {
            forwardLock.unlock();
        }

    }

    private void setActionEnabledState(Map activeMap, IAction action, boolean isForwardAction) {
        if (activeMap != null && activeMap != ApplicationGIS.NO_MAP) {
            action.setEnabled(isForwardAction ? activeMap.getCommandStack().canRedo()
                    : activeMap.getCommandStack().canUndo());
        } else {
            action.setEnabled(false);
        }
    }

    private void setNavActionEnabledState(Map activeMap, IAction action, boolean isForwardAction) {
        if (activeMap != null && activeMap != ApplicationGIS.NO_MAP) {
            action.setEnabled(isForwardAction ? activeMap.getNavCommandStack().hasForwardHistory()
                    : activeMap.getNavCommandStack().hasBackHistory());
        } else {
            action.setEnabled(false);
        }
    }

    @Override
    public void setFORWARDAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        forwardLock.lock();
        try {
            forwardAction = action;
            forwardAction.setActionDefinitionId("org.eclipse.ui.navigate.forward"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(forwardAction);
        } finally {
            forwardLock.unlock();
        }
    }

    @Override
    public void registerActionsWithPart(IWorkbenchPart part) {

        IKeyBindingService service = part.getSite().getKeyBindingService();
        service.registerAction(getBACKWARD_HISTORYAction());
        service.registerAction(getFORWARD_HISTORYAction());
        service.registerAction(getCOPYAction(part));
        service.registerAction(getCUTAction(part));
        service.registerAction(getDELETEAction());
        service.registerAction(getPASTEAction(part));
        service.registerAction(getREDOAction());
        service.registerAction(getUNDOAction());

        addToolScope(part.getSite());
    }

    @Override
    public void unregisterActions(IWorkbenchPart part) {

        IKeyBindingService service = part.getSite().getKeyBindingService();

        service.unregisterAction(getBACKWARD_HISTORYAction());
        service.unregisterAction(getFORWARD_HISTORYAction());
        service.unregisterAction(getCOPYAction(part));
        service.unregisterAction(getCUTAction(part));
        service.unregisterAction(getDELETEAction());
        service.unregisterAction(getPASTEAction(part));
        service.unregisterAction(getREDOAction());
        service.unregisterAction(getUNDOAction());

        service.setScopes(new String[0]);
    }

    /**
     * Retrieves the backward navigation action that is used by much of the map components such as
     * the MapEditor and the LayersView. Undoes the last Nav command set to the current map.
     *
     * @param part
     */
    @Override
    public IAction getBACKWARD_HISTORYAction() {
        Map activeMap = ApplicationGISInternal.getActiveMap();
        backwardLock.lock();
        try {
            if (backwardAction == null) {
                backwardAction = new Action() {
                    @Override
                    public void run() {
                        Map activeMap = ApplicationGISInternal.getActiveMap();
                        if (activeMap != ApplicationGIS.NO_MAP)
                            activeMap.backwardHistory();
                    }

                };
                backwardAction.setImageDescriptor(
                        sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
                backwardAction.setText(Messages.ToolManager_back);
                backwardAction.setToolTipText(Messages.ToolManager_back_tooltip);
                backwardAction.setActionDefinitionId("org.eclipse.ui.navigate.back"); //$NON-NLS-1$
            }
            setNavActionEnabledState(activeMap, backwardAction, false);
            return backwardAction;
        } finally {
            backwardLock.unlock();
        }
    }

    @Override
    public void setBACKAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        backwardLock.lock();
        try {
            backwardAction = action;
            backwardAction.setActionDefinitionId("org.eclipse.ui.navigate.back"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(backwardAction);
        } finally {
            backwardLock.unlock();
        }

    }

    @Override
    public IAction getCUTAction(IWorkbenchPart part) {
        cutLock.lock();
        try {
            if (cutAction == null) {
                cutAction = new Action() {

                };
            }
            // JONES
            return cutAction;
        } finally {
            cutLock.unlock();
        }
    }

    @Override
    public void setCUTAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        cutLock.lock();
        try {
            cutAction = action;
            cutAction.setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(cutAction);
        } finally {
            cutLock.unlock();
        }
    }

    @Override
    public IAction getCOPYAction(final IWorkbenchPart part) {
        copyLock.lock();
        try {
            if (copyAction == null) {
                copyAction = new CopyAction();
                IAction template = ActionFactory.COPY.create(part.getSite().getWorkbenchWindow());
                copyAction.setText(template.getText());
                copyAction.setToolTipText(template.getToolTipText());
                copyAction.setImageDescriptor(template.getImageDescriptor());
                copyAction.setId(template.getId());
                copyAction.setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
            }
            if (copyAction instanceof CopyAction) {
                ((CopyAction) copyAction).setPart(part);
            }
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(copyAction);
            return copyAction;
        } finally {
            copyLock.unlock();
        }
    }

    @Override
    public void setCOPYAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        copyLock.lock();
        try {
            copyAction = action;
            copyAction.setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(copyAction);
        } finally {
            copyLock.unlock();
        }
    }

    public IAction getPropertiesAction(IWorkbenchPart part, ISelectionProvider selectionProvider) {
        propertiesLock.lock();
        try {
            if (propertiesAction == null
                    || propertiesAction.getSelectionProvider() != selectionProvider) {
                propertiesAction = new PropertyDialogAction(part.getSite().getWorkbenchWindow(),
                        selectionProvider);
            }
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(propertiesAction);
            return propertiesAction;
        } finally {
            propertiesLock.unlock();
        }
    }

    @Override
    public IAction getPASTEAction(IWorkbenchPart part) {
        pasteLock.lock();
        try {
            if (pasteAction == null) {
                pasteAction = new PasteAction();
                IAction template = ActionFactory.PASTE.create(part.getSite().getWorkbenchWindow());
                pasteAction.setText(template.getText());
                pasteAction.setToolTipText(template.getToolTipText());
                pasteAction.setImageDescriptor(template.getImageDescriptor());
                pasteAction.setId(template.getId());
                pasteAction.setActionDefinitionId("org.eclipse.ui.edit.paste"); //$NON-NLS-1$
            }
            if (pasteAction instanceof PasteAction) {
                ((PasteAction) pasteAction).setPart(part);
            }

            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(pasteAction);
            return pasteAction;
        } finally {
            pasteLock.unlock();
        }
    }

    @Override
    public void setPASTEAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        pasteLock.lock();
        try {
            pasteAction = action;
            pasteAction.setActionDefinitionId("org.eclipse.ui.edit.paste"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(pasteAction);
        } finally {
            pasteLock.unlock();
        }
    }

    @Override
    public synchronized IAction getDELETEAction() {
        deleteLock.lock();
        try {
            if (deleteAction == null) {
                deleteAction = new Action() {
                    @Override
                    public void run() {
                        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow();
                        ISelectionService selectionService = workbenchWindow.getSelectionService();
                        ISelection selection = selectionService.getSelection();

                        Delete delete = new Delete(false);
                        delete.selectionChanged(this, selection);
                        delete.run(this);
                    }
                };
                deleteAction.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$

                IWorkbenchAction actionTemplate = ActionFactory.DELETE
                        .create(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
                deleteAction.setText(actionTemplate.getText());
                deleteAction.setToolTipText(actionTemplate.getToolTipText());
                deleteAction.setImageDescriptor(actionTemplate.getImageDescriptor());
                deleteAction.setDescription(actionTemplate.getDescription());
                deleteAction
                        .setDisabledImageDescriptor(actionTemplate.getDisabledImageDescriptor());
            }

            return deleteAction;
        } finally {
            deleteLock.unlock();
        }
    }

    @Override
    public synchronized void setDELETEAction(IAction action, IWorkbenchPart part) {
        if (action == null)
            throw new NullPointerException("action must not be null"); //$NON-NLS-1$
        if (part == null)
            throw new NullPointerException("part must not be null"); //$NON-NLS-1$
        deleteLock.lock();
        try {
            deleteAction = action;
            deleteAction.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
            IKeyBindingService service = part.getSite().getKeyBindingService();
            service.registerAction(deleteAction);
        } finally {
            deleteLock.unlock();
        }
    }

    @Override
    public synchronized IAction getENTERAction() {
        enterLock.lock();
        try {
            if (enterAction == null) {
                enterAction = new Action() {
                    @Override
                    public void run() {
                        try {
                            Robot r = new Robot();
                            r.keyPress(KeyEvent.VK_ENTER);
                            r.keyRelease(KeyEvent.VK_ENTER);
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                };
                enterAction.setText(Messages.ToolManager_enterAction);
                enterAction.setToolTipText(Messages.ToolManager_enterActionTooltip);
                enterAction.setDescription(Messages.ToolManager_enterActionTooltip);
            }

            return enterAction;
        } finally {
            enterLock.unlock();
        }
    }

    @Override
    public synchronized IAction getZOOMTOSELECTEDAction() {
        enterLock.lock();
        try {
            if (zoomToSelectionAction == null) {
                zoomToSelectionAction = getToolAction(
                        "org.locationtech.udig.tool.default.show.selection", //$NON-NLS-1$
                        "org.locationtech.udig.tool.category.zoom"); //$NON-NLS-1$
            }

            return zoomToSelectionAction;
        } finally {
            enterLock.unlock();
        }
    }

    private void createModalToolToolbar(SubCoolBarManager cbmanager) {
        ToolBarManager manager = new ToolBarManager(SWT.FLAT);

        for (String id : categoryIds) {
            ModalToolCategory modalCategory = findModalCategory(id);
            if (modalCategory != null) {
                modalCategory.contribute(manager);
            }
        }
        if (manager != null && manager.getItems().length > 0)
            cbmanager.add(manager);
    }

    private void createActionToolToolbar(SubCoolBarManager cbmanager) {
        ToolBarManager manager = new ToolBarManager(SWT.FLAT);

        manager.add(getBACKWARD_HISTORYAction());
        manager.add(getFORWARD_HISTORYAction());
        for (String id : categoryIds) {
            ActionToolCategory category = findActionCategory(id);
            if (category != null)
                category.contribute(manager);
        }
        if (manager != null && manager.getItems().length > 0)
            cbmanager.add(manager);
    }

    /**
     * Adds both action tools and modal tools to the manager
     *
     * @deprecated
     *
     * @param cbmanager
     * @param bars
     * @see org.locationtech.udig.project.ui.tool.ModalTool
     * @see org.locationtech.udig.project.ui.tool.ActionTool
     */
    @Deprecated
    @Override
    public void contributeToCoolBar(SubCoolBarManager cbmanager, IActionBars bars) {
        cbmanager.setVisible(true);
        createActionToolToolbar(cbmanager);
        createModalToolToolbar(cbmanager);
    }

    @Override
    public void contributeTools(IToolBarManager toolBarManager, IActionBars bars) {
        toolBarManager.add(getBACKWARD_HISTORYAction());
        toolBarManager.add(getFORWARD_HISTORYAction());
        for (String id : categoryIds) {
            ActionToolCategory actionCategory = findActionCategory(id);
            if (actionCategory != null)
                actionCategory.contribute(toolBarManager);
            ModalToolCategory modalCategory = findModalCategory(id);
            if (modalCategory != null) {
                modalCategory.contribute(toolBarManager);
            }
        }
    }

    @Override
    public void contributeActionTools(IToolBarManager toolBarManager, IActionBars bars) {
        toolBarManager.add(getBACKWARD_HISTORYAction());
        toolBarManager.add(getFORWARD_HISTORYAction());
        for (String id : categoryIds) {
            ActionToolCategory category = findActionCategory(id);
            if (category != null)
                category.contribute(toolBarManager);
        }
    }

    @Override
    public void contributeModalTools(IToolBarManager toolBarManager, IActionBars bars) {
        for (String id : categoryIds) {
            ModalToolCategory modalCategory = findModalCategory(id);
            if (modalCategory != null) {
                modalCategory.contribute(toolBarManager);
            }
        }
    }

    private IAction actionCLOSE;

    private IAction actionSAVE;

    private IAction actionCLOSE_ALL;

    private PreferencesShortcutToolOptionsContributionItem preferencesShortcutToolOptions;

    /**
     * Contributes the common global actions.
     *
     * @param part WorkbenchPart such as a view or editor
     * @param bars ActionBar used to register global actions
     */
    @Override
    public void contributeGlobalActions(IWorkbenchPart part, IActionBars bars) {
        IKeyBindingService service = part.getSite().getKeyBindingService();
        bars.setGlobalActionHandler(ActionFactory.BACK.getId(), getBACKWARD_HISTORYAction());
        bars.setGlobalActionHandler(ActionFactory.FORWARD.getId(), getFORWARD_HISTORYAction());
        bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getUNDOAction());
        bars.setGlobalActionHandler(ActionFactory.REDO.getId(), getREDOAction());
        bars.setGlobalActionHandler(ActionFactory.CUT.getId(), getCUTAction(part));
        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), getCOPYAction(part));
        bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getPASTEAction(part));
        bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getDELETEAction());

        ISelectionProvider selection = part.getSite().getSelectionProvider();
        if (selection != null) {
            bars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
                    getPropertiesAction(part, selection));
        }

        if (actionCLOSE == null)
            actionCLOSE = ActionFactory.CLOSE.create(part.getSite().getWorkbenchWindow());
        service.registerAction(actionCLOSE);
        bars.setGlobalActionHandler(ActionFactory.CLOSE.getId(), actionCLOSE);

        if (actionSAVE == null)
            actionSAVE = ActionFactory.SAVE.create(part.getSite().getWorkbenchWindow());
        service.registerAction(actionSAVE);
        bars.setGlobalActionHandler(ActionFactory.SAVE.getId(), actionSAVE);

        if (actionCLOSE_ALL == null)
            actionCLOSE_ALL = ActionFactory.CLOSE_ALL.create(part.getSite().getWorkbenchWindow());
        bars.setGlobalActionHandler(ActionFactory.CLOSE_ALL.getId(), actionCLOSE_ALL);

    }

    Adapter getCommandListener(final MapPart editor) {
        if (commandListener == null) {
            commandListener = new AdapterImpl() {
                @Override
                public void notifyChanged(Notification msg) {
                    /**
                     * While this adapter is a singleton and added to all opened maps, each time
                     * target variable is reset.
                     */
                    Map map = null;
                    switch (msg.getFeatureID(msg.getNotifier().getClass())) {
                    case ProjectPackage.MAP__COMMAND_STACK:
                        map = (Map) msg.getNotifier();
                        setCommandActions(map);
                        break;
                    case ProjectPackage.MAP__NAV_COMMAND_STACK:
                        map = (Map) msg.getNotifier();
                        setCommandActions(map);
                        break;
                    }

                }
            };
        }
        return commandListener;
    }

    /**
     * Hook up the usual actions (UNDO,REDO,BACKWARD_HISTORY,FORWARD_HISTORY) to the provided
     * editor.
     *
     * @param map
     * @param editor
     */
    void setCommandActions(Map map) {
        setActionEnabledState(map, getREDOAction(), true);
        setActionEnabledState(map, getUNDOAction(), false);
        setNavActionEnabledState(map, getBACKWARD_HISTORYAction(), false);
        setNavActionEnabledState(map, getFORWARD_HISTORYAction(), true);
    }

    @Override
    public IAction getTool(String toolID, String categoryID) {
        return getToolAction(toolID, categoryID);
    }

    @Override
    public IAction getToolAction(String toolID, String categoryID) {
        final IAction tool = getToolInteral(toolID, categoryID);

        if (tool == null)
            return null;

        return new IAction() {
            IAction wrapped = tool;

            @Override
            public void addPropertyChangeListener(IPropertyChangeListener listener) {
                wrapped.addPropertyChangeListener(listener);
            }

            @Override
            public int getAccelerator() {
                return wrapped.getAccelerator();
            }

            @Override
            public String getActionDefinitionId() {
                return wrapped.getActionDefinitionId();
            }

            @Override
            public String getDescription() {
                return wrapped.getDescription();
            }

            @Override
            public ImageDescriptor getDisabledImageDescriptor() {
                return wrapped.getDisabledImageDescriptor();
            }

            @Override
            public HelpListener getHelpListener() {
                return wrapped.getHelpListener();
            }

            @Override
            public ImageDescriptor getHoverImageDescriptor() {
                return wrapped.getHoverImageDescriptor();
            }

            @Override
            public String getId() {
                return wrapped.getId();
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return wrapped.getImageDescriptor();
            }

            @Override
            public IMenuCreator getMenuCreator() {
                return wrapped.getMenuCreator();
            }

            @Override
            public int getStyle() {
                return wrapped.getStyle();
            }

            @Override
            public String getText() {
                return wrapped.getText();
            }

            @Override
            public String getToolTipText() {
                return wrapped.getToolTipText();
            }

            @Override
            public boolean isChecked() {
                return wrapped.isChecked();
            }

            @Override
            public boolean isEnabled() {
                return wrapped.isEnabled();
            }

            @Override
            public boolean isHandled() {
                return wrapped.isHandled();
            }

            @Override
            public void removePropertyChangeListener(IPropertyChangeListener listener) {
                wrapped.removePropertyChangeListener(listener);
            }

            @Override
            public void runWithEvent(Event event) {
                wrapped.runWithEvent(event);
            }

            @Override
            public void run() {
                wrapped.run();
            }

            @Override
            public void setAccelerator(int keycode) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setActionDefinitionId(String id) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setChecked(boolean checked) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setDescription(String text) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setDisabledImageDescriptor(ImageDescriptor newImage) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setEnabled(boolean enabled) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setHelpListener(HelpListener listener) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setHoverImageDescriptor(ImageDescriptor newImage) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setId(String id) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setImageDescriptor(ImageDescriptor newImage) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setMenuCreator(IMenuCreator creator) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setText(String text) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }

            @Override
            public void setToolTipText(String text) {
                throw new UnsupportedOperationException("This is an unmodifiable action"); //$NON-NLS-1$
            }
        };
    }

    /**
     * Returns the actual tool action.
     */
    private IAction getToolInteral(String toolID, String categoryID) {
        ToolCategory category = findModalCategory(categoryID);
        if (category != null) {
            IAction tool = searchCategoryForTool(toolID, category);
            if (tool != null)
                return tool;
        }

        category = findActionCategory(categoryID);
        if (category != null) {
            IAction tool = searchCategoryForTool(toolID, category);
            if (tool != null)
                return tool;
        }

        category = findMenuCategory(categoryID);
        if (category != null) {
            IAction tool = searchCategoryForTool(toolID, category);
            if (tool != null)
                return tool;
        }
        return null;
    }

    /**
     *
     * @param toolID
     * @param category
     * @return
     */
    private IAction searchCategoryForTool(String toolID, ToolCategory category) {
        for (Iterator iter2 = category.iterator(); iter2.hasNext();) {
            ToolProxy tool = (ToolProxy) iter2.next();
            String id = tool.getId();
            if (id.equals(toolID))
                return tool.getAction();
        }
        return null;
    }

    /**
     * Returns the list of categories containing modal tools.
     *
     * @return the list of categories containing modal tools.
     */
    @Override
    public List<ModalToolCategory> getModalToolCategories() {
        return modalCategories;
    }

    /**
     * Returns the tool category that is currently active.
     *
     * @return the tool category that is currently active.
     */
    @Override
    public ToolCategory getActiveCategory() {
        return findModalCategory(activeModalToolProxy.getCategoryId());
    }

    private static class CategorySorter implements Comparator<String>, Serializable {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        private static final java.util.Map<String, String> values = new HashMap<>();
        static {
            values.put(ToolConstants.RENDER_CA, "0100"); //$NON-NLS-1$
            values.put(ToolConstants.ZOOM_CA, "0200"); //$NON-NLS-1$
            values.put(ToolConstants.PAN_CA, "0300"); //$NON-NLS-1$
            values.put(ToolConstants.SELECTION_CA, "0400"); //$NON-NLS-1$
            values.put(ToolConstants.INFO_CA, "0500"); //$NON-NLS-1$
            values.put(ToolConstants.EDIT_CA, "0600"); //$NON-NLS-1$
            values.put(ToolConstants.TOOL_EDIT_CA, "0700"); //$NON-NLS-1$
            values.put(ToolConstants.TOOL_CREATE_CA, "0800"); //$NON-NLS-1$
            values.put(ToolConstants.TOOL_FEATURE_CA, "0900"); //$NON-NLS-1$
        }

        private static final int MAX = -1;

        private static final int MIN = 1;

        @Override
        public int compare(String arg0, String arg1) {
            arg0 = getOrDefault(values, arg0, "") + arg0; //$NON-NLS-1$
            arg1 = getOrDefault(values, arg1, "") + arg1; //$NON-NLS-1$
            if (arg0.equals(arg1))
                return 0;
            if (arg1.isEmpty() || arg1.equals(Messages.ToolCategory_other))
                return MAX;
            if (arg0.isEmpty() || arg0.equals(Messages.ToolCategory_other))
                return MIN;

            return arg0.compareTo(arg1);
        }

        private String getOrDefault(java.util.Map<String, String> map, String key,
                String defaultValue) {
            String value = map.get(key);
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        }
    }

    /**
     * This method enables the context that allows tool keybinding to work.
     *
     * @param site
     */
    public void addToolScope(IWorkbenchPartSite site) {
        String[] scopes = site.getKeyBindingService().getScopes();
        String[] newScopes = new String[scopes.length + 1];
        System.arraycopy(scopes, 0, newScopes, 1, scopes.length);
        newScopes[0] = "org.locationtech.udig.project.ui.tool"; //$NON-NLS-1$
        site.getKeyBindingService().setScopes(newScopes);
    }

    /**
     * This allows for customized operation menus that are based on the currently selected tool.
     */
    @Override
    public MenuManager createOperationsContextMenu(ISelection selection) {
        try {
            MenuManager contextManager = getOperationMenuFactory().createMenuManager();

            List<OperationCategory> primaryCategories = activeModalToolProxy
                    .getOperationCategories();
            Collection<OperationCategory> secondaryCategories = getOperationMenuFactory()
                    .getCategories().values();

            for (int i = 0; i < primaryCategories.size(); i++) {
                OperationCategory category = primaryCategories.get(i);

                // Limit the size of the context menu to 20, but don't ever display a portion of
                // a category, only always the entire thing.
                if (contextManager.getItems().length >= 15 && category.getActions().size() > 5) {
                    break;
                }

                MenuManager menu = category.createContextMenu();

                if ((i != 0 && menu.getItems().length != 0) && (secondaryCategories.size() != 0
                        || getOperationMenuFactory().getActions().size() != 0)) {

                    contextManager.add(new Separator());
                }

                for (IContributionItem item : menu.getItems()) {
                    contextManager.add(item);
                }
            }

            // if primaryCategories are present, create an "Other" submenu
            if (contextManager.getItems().length != 0) {
                RunOperationsAction action = new RunOperationsAction();
                action.setText(Messages.ToolCategory_other);

                contextManager.add(new Separator());
                contextManager.add(action);
            } else {
                Iterator iter = secondaryCategories.iterator();
                while (iter.hasNext()) {
                    OperationCategory category = (OperationCategory) iter.next();

                    for (OpAction action : category.getActions()) {
                        if (selection instanceof IStructuredSelection)
                            action.updateEnablement((IStructuredSelection) selection, true);
                        if (action.isEnabled())
                            contextManager.add(action);
                    }
                    if (iter.hasNext())
                        contextManager.add(new Separator());
                }

                if (getOperationMenuFactory().getActions().size() != 0) {
                    contextManager.add(new Separator());
                }
                for (OpAction action : getOperationMenuFactory().getActions()) {
                    if (selection instanceof IStructuredSelection)
                        action.updateEnablement((IStructuredSelection) selection, true);
                    if (action.isEnabled()) {
                        contextManager.add(action);
                    }
                }
            }

            return contextManager;
        } catch (Throwable e) {
            ProjectUIPlugin.log("error creating the Operations ContextMenu", e);//$NON-NLS-1$
            return new MenuManager();
        }
    }

    private OperationMenuFactory getOperationMenuFactory() {
        return UiPlugin.getDefault().getOperationMenuFactory();
    }

    @Override
    public Tool getActiveTool() {
        return activeModalToolProxy.getTool();
    }

    /**
     * Returns active tool proxy object.
     *
     * @return
     */
    @Override
    public ToolProxy getActiveToolProxy() {
        return activeModalToolProxy;
    }

    /**
     * Sets the current active modal tool; please note that the provided tool must be visible /
     * enabled and available in the user interface for this operation to work.
     *
     * @param activeModalToolProxy
     */
    @Override
    public void setActiveModalToolProxy(ToolProxy modalToolProxy) {
        if (modalToolProxy == null) {
            // we will have to use the default then
            modalToolProxy = defaultModalToolProxy;
        }

        // this is still the old ToolProxy
        if (activeModalToolProxy != null) {
            if (activeModalToolProxy.getId() == modalToolProxy.getId()) {
                return; // no change required
            }
            activeModalToolProxy.setChecked(false);
        }

        // and now for the new ToolProxy
        activeModalToolProxy = modalToolProxy;
        activeModalToolProxy.setChecked(true);

        // connect the tools to the map area
        setActiveModalTool(modalToolProxy.getModalTool());
        currentEditor.setSelectionProvider(modalToolProxy.getSelectionProvider());

        // add tool options to the status area
        if (FT_ACTION_TOOL_PREF_LINKS) {
            initToolOptionsContribution(currentEditor.getStatusLineManager(), getActiveToolProxy());
        }
    }

    /**
     * This method goes through the steps of deactivating the current tool option contribution and
     * activating the new tool option contribution.
     *
     * @param statusLine
     * @param modalToolProxy
     */
    private void initToolOptionsContribution(IStatusLineManager statusLine,
            ToolProxy modalToolProxy) {
        if (statusLine != null) {

            if (preferencesShortcutToolOptions == null
                    || preferencesShortcutToolOptions.isDisposed()) {
                preferencesShortcutToolOptions = new PreferencesShortcutToolOptionsContributionItem();
                statusLine.appendToGroup(StatusLineManager.BEGIN_GROUP,
                        preferencesShortcutToolOptions);
                preferencesShortcutToolOptions.setVisible(true);
            }
            preferencesShortcutToolOptions.update(modalToolProxy);

            // TODO, cache contributions instead of destroying them and recreating them

            // remove old tool contribution
            for (ContributionItem contribution : optionsContribution) {
                statusLine.remove(contribution.getId());
            }

            // get the new contributions
            optionsContribution = modalToolProxy.getOptionsContribution();

            // set all new contributions
            for (ContributionItem contribution : optionsContribution) {
                statusLine.appendToGroup(StatusLineManager.BEGIN_GROUP, contribution);
                contribution.setVisible(true);
            }

            statusLine.update(true);
        }
    }

    /**
     * This method goes through the steps of deactivating the current tool and activating the new
     * one.
     *
     * @param modalTool
     */
    private void setActiveModalTool(ModalTool modalTool) {
        if (modalTool == null) {
            // we cannot run without a tool; so we will use the default tool!
            modalTool = defaultModalToolProxy.getModalTool();
        }

        if (activeTool == null) {
            // the active tool was not set initially; so we will use the default tool!
            activeTool = defaultModalToolProxy.getModalTool();
        }

        if (activeTool == modalTool) {
            return; // no change required!
        }

        // ask the current tool to stop listening etc...
        activeTool.setActive(false);
        activeTool = null;

        if (modalTool.getContext() == null) {
            // the tool cannot be activated as it has not been connected to the map yet
            // Could we perform activeTool.setContext( toolContext )?
            return;
        }

        try {
            activeTool = modalTool;

            activeTool.setActive(true);// this should register itself with the tool manager

            // this was normally handled by the ToolProxy which we cannot get a hold of
            String currentCursorID = activeTool.getCursorID();
            Cursor toolCursor = findToolCursor(currentCursorID);

            activeTool.getContext().getViewportPane().setCursor(toolCursor);
        } catch (Throwable eek) {
            System.err.println("Trouble activating " + modalTool + ":" + eek); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                activeTool.setActive(false); // hope it does a better at cleaning up
            } catch (Throwable t) {
                // no it did not do a better job cleaning up
            }
            activeTool = defaultModalToolProxy.getModalTool();
            activeTool.setActive(true);
        }
    }

    static class CopyAction extends Action {
        final Set<Transfer> transfers = UDIGDragDropUtilities.getTransfers();

        IWorkbenchPart part;

        public IWorkbenchPart getPart() {
            return part;
        }

        public void setPart(IWorkbenchPart part) {
            this.part = part;
        }

        @Override
        public void runWithEvent(Event event) {
            Clipboard clipBoard = new Clipboard(event.display);
            try {
                IMap map = ApplicationGIS.getActiveMap();
                if (map == ApplicationGIS.NO_MAP)
                    return;
                ILayer selectedLayer = map.getEditManager().getSelectedLayer();
                if (selectedLayer == null)
                    return;

                Filter layerFilter = selectedLayer.getFilter();
                // TODO REVIEW comparison "layerFilter == org.geotools.filter.Filter.ALL"
                if (layerFilter == Filter.INCLUDE) {
                    return;
                }
                AdaptingFilter filter = null;

                if (layerFilter instanceof AdaptingFilter) {
                    AdaptingFilter adapting = (AdaptingFilter) layerFilter;
                    if (adapting.getAdapter(ILayer.class) != null) {
                        filter = adapting;
                    }
                }

                if (filter == null) {
                    filter = AdaptingFilterFactory.createAdaptingFilter(layerFilter, selectedLayer);
                }

                clipBoard.setContents(new Object[] { filter },
                        new Transfer[] { UDigByteAndLocalTransfer.getInstance() });
            } finally {
                clipBoard.dispose();
            }

        }
    }

    static class PasteAction extends Action {
        IWorkbenchPart part;

        public IWorkbenchPart getPart() {
            return part;
        }

        public void setPart(IWorkbenchPart part) {
            this.part = part;
        }

        @Override
        public void run() {
            final Object contents = getClipboardContent(part);

            if (contents == null) {
                return;
            }

            final Map finalMap = ToolManagerUtils.getTargetMap(part);
            final MapPart finalMapPart;
            if (finalMap != null) {
                finalMapPart = ApplicationGISInternal.findMapPart(finalMap);
            } else {
                finalMapPart = null;
            }
            UDIGDropHandler dropHandler = null;
            if (finalMapPart != null && finalMapPart.getDropHandler() != null) {
                dropHandler = finalMapPart.getDropHandler();
            }

            final UDIGDropHandler finalDropHandler = (dropHandler == null ? new UDIGDropHandler() : dropHandler);

            if (finalMap != null && finalMap.getEditManager() != null) {
                final ILayer selectedLayer = finalMap.getEditManager().getSelectedLayer();
                if (selectedLayer == null) {
                    finalDropHandler.setTarget(finalMap);
                } else {
                    finalDropHandler.setTarget(selectedLayer);
                }
                finalDropHandler.addListener(new IDropHandlerListener() {

                    @Override
                    public void done(IDropAction action, Throwable error) {
                        if (finalMapPart == null && finalMap.getMapLayers().isEmpty()) {
                            finalMap.getProjectInternal().getElementsInternal().remove(finalMap);
                        }

                        finalDropHandler.removeListener(this);
                    }

                    @Override
                    public void noAction(Object data) {
                        if (finalMapPart == null && finalMap.getMapLayers().isEmpty()) {
                            finalMap.getProjectInternal().getElementsInternal().remove(finalMap);
                        }
                        finalDropHandler.removeListener(this);
                    }

                    @Override
                    public void starting(IDropAction action) {
                    }

                });
                finalDropHandler.setViewerLocation(ViewerDropLocation.ON);
                finalDropHandler.performDrop(contents, null);
            }
        }

        private static Object getClipboardContent(final IWorkbenchPart part) {
            final Clipboard clipboard = new Clipboard(part.getSite().getShell().getDisplay());
            final Set<Transfer> transfers = UDIGDNDProcessor.getTransfers();
            Object contents = null;
            for (final Transfer transfer : transfers) {
                contents = clipboard.getContents(transfer);
                if (contents != null) {
                    break;
                }
            }
            return contents;
        }

    }

    @Override
    public List<ActionToolCategory> getActiveToolCategories() {
        return actionCategories;
    }

}
