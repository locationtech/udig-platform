/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.internal.ui.operations.OperationCategory;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditorSelectionProvider;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.tool.AbstractTool;
import net.refractions.udig.project.ui.tool.ActionTool;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.Tool;
import net.refractions.udig.project.ui.tool.ToolConstants;
import net.refractions.udig.project.ui.tool.ToolLifecycleListener;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.EnablementUtil;
import net.refractions.udig.ui.operations.LazyOpFilter;
import net.refractions.udig.ui.operations.OpFilter;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The tool proxy allows tools to be loaded lazily. It acts as a proxy for a tool as far as the Map
 * editors are concerned.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ToolProxy extends ModalItem implements ModalTool, ActionTool {
    /**
     * Handles lazy cursor loading.
     *
     * @author jeichar
     * @since 0.9.0
     *
     * @deprecated use {@link CursorProxy} class instead.
     */
    public static class CursorLoader {

        private volatile Cursor cursor;
        private String imagePath;
        private String hotspotX;
        private String hotspotY;
        private String cursorId;
        private String pluginID;
        /**
         * Construct <code>ToolProxy.CursorLoader</code>.
         */
        public CursorLoader( IConfigurationElement cursorDef ) {
            if (cursorDef != null) {
                imagePath = cursorDef.getAttribute("image"); //$NON-NLS-1$
                hotspotX = cursorDef.getAttribute("hotspotX"); //$NON-NLS-1$
                hotspotY = cursorDef.getAttribute("hotspotY"); //$NON-NLS-1$
                cursorId = cursorDef.getAttribute("id"); //$NON-NLS-1$
                pluginID = cursorDef.getNamespace();
            } else {
                cursorId = ModalTool.DEFAULT_CURSOR;
            }
        }

        /**
         * @return Returns the cursor.
         */
        public Cursor getCursor() {
            if (cursor == null) {
                synchronized (this) {
                    if (cursor == null) {
                        if (imagePath == null) {
                            cursor = parseCursorId(cursorId);
                        } else {
                            ImageDescriptor imageDescriptor = AbstractUIPlugin
                                    .imageDescriptorFromPlugin(pluginID, imagePath);
                            int x;
                            try {
                                x = Integer.parseInt(hotspotX);
                            } catch (Exception e) {
                                x = 0;
                            }
                            int y;
                            try {
                                y = Integer.parseInt(hotspotY);
                            } catch (Exception e) {
                                y = 0;
                            }
                            if (imageDescriptor == null || imageDescriptor.getImageData() == null)
                                cursor = parseCursorId(cursorId);
                            else
                                cursor = new Cursor(Display.getDefault(), imageDescriptor
                                        .getImageData(), x, y);
                        }
                    }
                }
            }

            return cursor;
        }

        Cursor parseCursorId( String cursor ) {
        	Display display = PlatformUI.getWorkbench().getDisplay();
            if (cursor == null)
                return display.getSystemCursor(SWT.CURSOR_ARROW);
            if (cursor.equals(ModalTool.CROSSHAIR_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_CROSS);
            if (cursor.equals(ModalTool.E_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZEE);
            if (cursor.equals(ModalTool.HAND_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_HAND);
            if (cursor.equals(ModalTool.MOVE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZEALL);
            if (cursor.equals(ModalTool.N_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZEN);
            if (cursor.equals(ModalTool.NE_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZENE);
            if (cursor.equals(ModalTool.NW_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZENW);
            if (cursor.equals(ModalTool.S_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZES);
            if (cursor.equals(ModalTool.SE_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZESE);
            if (cursor.equals(ModalTool.SW_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZESW);
            if (cursor.equals(ModalTool.TEXT_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_IBEAM);
            if (cursor.equals(ModalTool.W_RESIZE_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_SIZESW);
            if (cursor.equals(ModalTool.WAIT_CURSOR))
                return display.getSystemCursor(SWT.CURSOR_WAIT);
            return  display.getSystemCursor(SWT.CURSOR_ARROW);
        }

        /**
         * Dispose the cursor.
         */
        public void dispose() {
            if (cursor != null)
                cursor.dispose();
            cursor = null;
        }

    }

    /**
     * The actual tool implementation. To be lazily loaded.
     */
    Tool tool = null;

    /**
     * The current tool context.
     */
    IToolContext toolContext = null;

    /**
     * Configuration from extension registry for this tool.
     */
    IConfigurationElement element = null;

    //TODO make use of this.
    boolean hasControl = false;

    /*
     * The tool cursor ID if <code>toolCursorId</code> is specified in tool extension.
     */
    String defaultCursorID;

    private boolean onToolbar = true;
    int type = -1;
    static final int MODAL = 1;
    static final int BACKGROUND = 2;
    static final int ACTION = 3;

    /**
     * The tool category ID. It is configured in extension registry.
     */
    private String categoryId;

    /**
     * The ID of the default tool: Zoom
     */
    private static final String DEFAULT_ID = "net.refractions.udig.tools.Zoom"; //$NON-NLS-1$

    /**
     * The action object in UI for this tool proxy. It is lazy created.
     */
    private volatile IAction action;
    private String menuPath;
    private boolean disposed=false;
    private volatile IMapEditorSelectionProvider selectionProviderInstance;


    /**
     * The tool manager.
     */
    private ToolManager toolManager;

    /**
     * Creates an new instance of MapViewport.ToolAction
     *
     * @param extension The Tool extension
     * @param tool The configuration element which describes the tool
     * @param newParam TODO
     */
    public ToolProxy( IExtension extension, IConfigurationElement tool, ToolManager toolManager ) {
        super();

        this.toolManager = toolManager;

        categoryId = tool.getAttribute("categoryId"); //$NON-NLS-1$
        String type = tool.getName();
        String pluginid = extension.getNamespaceIdentifier() ;
        String id = tool.getAttribute("id"); //$NON-NLS-1$
        String name = tool.getAttribute("name"); //$NON-NLS-1$
        if (name == null)
            name = Messages.ToolProxy_unnamed;
        String toolTip = tool.getAttribute("tooltip"); //$NON-NLS-1$
        String iconID = tool.getAttribute("icon"); //$NON-NLS-1$

        defaultCursorID = tool.getAttribute("toolCursorId"); //$NON-NLS-1$

        //FIXME For compatibility. To BE REMOVED later.
        if(defaultCursorID == null){
        	IConfigurationElement[] children = tool.getChildren("cursor"); //$NON-NLS-1$
        	if(children.length > 0){
        		CursorProxy cursorProxy = new CursorProxy(children[0]);
        		toolManager.cursorsCache.put(cursorProxy.getID(), cursorProxy);
        		defaultCursorID = cursorProxy.getID();
        	}else{
        		defaultCursorID = ModalTool.DEFAULT_CURSOR;
        	}
        }

        OpFilter parseEnablement = EnablementUtil.parseEnablement( extension.getNamespaceIdentifier()+"."+tool.getName(), tool.getChildren("enablement")); //$NON-NLS-1$ //$NON-NLS-2$;
        enablement = new LazyOpFilter(this, parseEnablement);
        operationCategories = parseOperationCategories(tool);

        String bool = tool.getAttribute("hasCustomControl"); //$NON-NLS-1$
        hasControl = ((bool != null) && bool.equalsIgnoreCase("true")) ? true : false; //$NON-NLS-1$
        bool = tool.getAttribute("onToolbar"); //$NON-NLS-1$
        onToolbar = ((bool != null) && bool.equalsIgnoreCase("true")) ? true : false; //$NON-NLS-1$
        menuPath = tool.getAttribute("menuPath"); //$NON-NLS-1$
        ImageDescriptor icon;
        if (iconID == null) {
            icon = null;
        }else{
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(pluginid, iconID);
        }
        setImageDescriptor(icon);

        this.element = tool;
        setName(name);
        setToolTipText(toolTip);
        setId(id);
        if (type.equals("modalTool")) //$NON-NLS-1$
            this.type = MODAL;
        else if (type.equals("backgroundTool")) //$NON-NLS-1$
            this.type = BACKGROUND;
        else if (type.equals("actionTool")) //$NON-NLS-1$
            this.type = ACTION;
        handlerType = tool.getAttribute(HandlerProxy.ID);

        String unparsedCommandIds = tool.getAttribute("commandIds"); //$NON-NLS-1$
        if (unparsedCommandIds != null && unparsedCommandIds.length() > 0)
            commandIds = unparsedCommandIds.split(","); //$NON-NLS-1$
        else
            commandIds = new String[0];

        if (id.equals(DEFAULT_ID)) {
        	toolManager.defaultModalToolProxy = this;
        }
    }

    private List<OperationCategory> parseOperationCategories(IConfigurationElement toolElement) {
        IConfigurationElement[] children = toolElement.getChildren("operationCategory"); //$NON-NLS-1$

        if (children == null || children.length == 0) {
            return null;
        }

        Map<String, OperationCategory> categories = UiPlugin.getDefault().getOperationMenuFactory().getCategories();
        ArrayList<OperationCategory> results = new ArrayList<OperationCategory>();

        for (IConfigurationElement element : children) {
            String opCategoryID = element.getAttribute("categoryID"); //$NON-NLS-1$

            if (opCategoryID == null || opCategoryID.length() == 0) {
                ProjectUIPlugin.log("Warning: CategoryID attribute of operationCategory element in tool '"+id+"' is empty.",null); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            OperationCategory opCategory = categories.get(opCategoryID);
            if (opCategory == null) {
                ProjectUIPlugin.log("Warning: CategoryID attribute of operationCategory element in tool '"+id+"' cannot be found. Does it actually exist?", null); //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            results.add(opCategory);
        }
        return results;
    }

    CursorLoader parseCursor( IConfigurationElement toolDef ) {

        // should only be one
        IConfigurationElement[] children = toolDef.getChildren("cursor"); //$NON-NLS-1$

        return new CursorLoader(children.length > 0 ? children[0] : null);

    }

    /**
     * @see org.eclipse.jface.action.Action#getStyle()
     */
    public int getStyle() {
        if (type == MODAL)
            return IAction.AS_RADIO_BUTTON;
        return IAction.AS_PUSH_BUTTON;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#setContext(net.refractions.udig.project.ui.tool.IToolContext)
     */
    public void setContext( IToolContext toolContext ) {
        this.toolContext = toolContext;

        if (type == BACKGROUND) {
            getTool().setContext(toolContext);

        } else if ( type == MODAL && toolManager.activeModalToolProxy == this) {
        	ModalTool modalTool =  getModalTool();

        	modalTool.setContext(toolContext);
            String currentCursorID = modalTool.getCursorID();
            toolContext.getViewportPane().setCursor(
        			ApplicationGIS.getToolManager().findToolCursor(currentCursorID));

        } else if (tool != null) {
            getTool().setContext(toolContext);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
    }
    ModalTool getModalTool() {
        return (ModalTool) getTool();
    }
    ActionTool getModelessTool() {
        return (ActionTool) getTool();
    }

    /**
     * Returns proxy
     *
     * @return
     */
    public Tool getTool() {
        if (tool == null) {
            Display display = Display.getCurrent();
            if( display==null)
                display=Display.getDefault();
            final Display finalDisplay=display;
            Runnable runnable = new Runnable(){
                public void run() {
                    BusyIndicator.showWhile(finalDisplay, new Runnable(){
                        public void run() {
                            String klassName = element.getAttribute("class"); //$NON-NLS-1$
                            if (klassName != null) {
                                try {
                                    Object o = element.createExecutableExtension("class"); //$NON-NLS-1$
                                    tool = (Tool) o;

                                    if(tool instanceof AbstractTool){
                                        ((AbstractTool)tool).init(element);
                                    }

                                    /* Tool cursors framework */
                                    if(tool instanceof ModalTool){
                                        if(defaultCursorID != null){
                                            ((ModalTool)tool).setCursorID(defaultCursorID);
                                            tool.setProperty(ToolConstants.DEFAULT_CURSOR_ID_KEY, defaultCursorID);
                                        }
                                    }

                                    tool.setContext(toolContext);
                                    tool.setEnabled(isEnabled());

                                    if(tool instanceof ModalTool){
                                    	IMapEditorSelectionProvider selectionProvider = getSelectionProvider();
                                    	((ModalTool)tool).setSelectionProvider(selectionProvider);
                                    }

                                } catch (CoreException e) {
                                    ProjectUIPlugin.log("Error loading tool", e); //$NON-NLS-1$
                                }
                            }

                        }
                    });
                }
            };

            PlatformGIS.syncInDisplayThread(runnable);
        }
        return tool;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * If the current action is a ActionTool then the menu path of the tool action is returned
     *
     * @return the menu path of the tool action or null if the current tool is not an ActionTool.
     */
    public String getMenuPath() {
        return menuPath;
    }
    /**
     * @return Returns the onToolbar.
     */
    public boolean isOnToolbar() {
        return onToolbar;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.Tool#getContext()
     */
    public IToolContext getContext() {
        return toolContext;
    }

    /**
     * Returns ID of tool category from  extention registry.
     *
     * @return
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Creates an action that will run the tool referenced by this proxy
     *
     * @return an action that will run the tool referenced by this proxy
     */
    public IAction getAction() {
        if (action == null) {
            synchronized (this) {
                if (action == null) {
                    action = new ToolAction(this);
                }
                action.setEnabled(isEnabled());
            }
        }
        return action;
    }



    /**
     *  (non-Javadoc)
     * @see net.refractions.udig.project.ui.tool.Tool#isEnabled()
     */
    @Override
	public boolean isEnabled() {
    	if(tool == null)
    		return super.isEnabled();

    	return tool.isEnabled();
	}

    Lock enabledLock = new ReentrantLock(true);

	/**
     *  (non-Javadoc)
     * @see net.refractions.udig.project.ui.tool.ModalTool#setEnabled(boolean)
     */
    @Override
    public void setEnabled( final boolean enabled ) {
                PlatformGIS.syncInDisplayThread(new Runnable(){
                    public void run() {
                        toolProxySetEnabled(enabled);
                    }
                });
    }



    protected void toolProxySetEnabled( boolean enabled ) {
        enabledLock.lock();
        try {

            super.setEnabled(enabled);

            if(tool != null){
                tool.setEnabled(enabled);
            }

            if( action!=null )
                action.setEnabled(enabled);
        } finally {
            enabledLock.unlock();
        }
    }

    /**
     * (non-Javadoc)
     * @see net.refractions.udig.project.ui.tool.ActionTool#run()
     */
    @Override
    public void run() {
    	if (runModeless() && isEnabled )
    		return;

       PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                enableModalTool();
            }
        });
    }

    private void enableModalTool() {
        ToolProxy activeToolProxy = (ToolProxy)getActiveItem();
    	if (activeToolProxy != null)
    		activeToolProxy.setActive(false);

    	setActive(true);
     	setActiveItem(this);
    }

	/**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#runModeless()
     */
    protected boolean runModeless() {
        if (type == ACTION) {
            PlatformGIS.run(new ISafeRunnable(){

                public void run() throws Exception {
                    getModelessTool().run();
                }

                public void handleException( Throwable exception ) {
                    ProjectUIPlugin
                            .log("Error occured while executing tool: " + getId(), exception); //$NON-NLS-1$
                }

            });
            return true;
        }

        return false;
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#setActive(boolean)
     */
    public void setActive( boolean active ) {
    	if (toolContext == null)
    		return;

    	setChecked(active);

    	if (getTool() instanceof ModalTool) {
    		ModalTool modalTool = (ModalTool)getTool();

    		getModalTool().setActive(active);

    		if (active){
    			String currentCursorID = modalTool.getCursorID();
    			toolContext.getViewportPane().setCursor(
    					ApplicationGIS.getToolManager().findToolCursor(currentCursorID));
    		}
    	}
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#dispose()
     */
    public void dispose() {
        disposed=true;
        if (tool != null) {
            tool.dispose();
        }

//        if (this == toolManager.activeModalToolProxy)
//        	toolManager.activeModalToolProxy = null;

    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#getHandler(java.lang.String)
     */
    public IHandler getHandler( String commandId ) {
        if (handlerType != null && handlerType.length() > 0)
            return new HandlerProxy(element, this, commandId);
        return null;
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#getDefaultItem()
     */
    protected ModalItem getDefaultItem() {
        return toolManager.defaultModalToolProxy;
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#getActiveItem()
     */
    protected ModalItem getActiveItem() {
        return toolManager.activeModalToolProxy;
    }

    /**
     * @see net.refractions.udig.project.ui.internal.tool.display.ModalItem#setActiveItem(net.refractions.udig.project.ui.internal.tool.display.ModalItem)
     */
    protected void setActiveItem( ModalItem modalItem ) {
    	toolManager.activeModalToolProxy = (ToolProxy)modalItem;
    }


    /** (non-Javadoc)
     * @see net.refractions.udig.project.ui.tool.ModalTool#isActive()
     */
    public boolean isActive() {
        return getModalTool().isActive();
    }



    @Override
    public boolean isDisposed() {
        return disposed;
    }

    private Lock selectionProviderLock = new ReentrantLock();

    public IMapEditorSelectionProvider getSelectionProvider() {
        if (selectionProviderInstance == null) {
            selectionProviderLock.lock();
            try {
                if (selectionProviderInstance != null) {
                    return selectionProviderInstance;
                }
                if (element.getAttribute("selectionProvider") != null) { //$NON-NLS-1$
                    try {
                        selectionProviderInstance = (IMapEditorSelectionProvider) element
                                .createExecutableExtension("selectionProvider"); //$NON-NLS-1$
                    } catch (CoreException e) {
                        ProjectUIPlugin
                                .log(
                                        "Error instantiating selection provider for " + element.getNamespaceIdentifier() + "/" + element.getName(), e); //$NON-NLS-1$//$NON-NLS-2$
                    }
                    if (selectionProviderInstance != null)
                        return selectionProviderInstance;
                }
                ToolManager m = (ToolManager) ApplicationGIS.getToolManager();
                ModalToolCategory cat = m.findModalCategory(getCategoryId());
                if (cat != null)
                    selectionProviderInstance = cat.getSelectionProvider();
                else
                    selectionProviderInstance = new MapEditorSelectionProvider();
            } finally {
                selectionProviderLock.unlock();
            }
        }
        return selectionProviderInstance;
    }


    /** (non-Javadoc)
     * @see net.refractions.udig.project.ui.tool.ModalTool#getCursorID()
     */
    public String getCursorID() {
    	throw new UnsupportedOperationException("Call ToolProxy.getTool().getCursorID() method instead"); //$NON-NLS-1$
	}


	/**
	 * Empty implementation
	 *
	 * @see net.refractions.udig.project.ui.tool.ModalTool#setCursorID(java.lang.String)
	 */
	public void setCursorID(String id) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().setCursorID(String) method instead"); //$NON-NLS-1$
	}

	/** (non-Javadoc)
	 * @see net.refractions.udig.project.ui.tool.Tool#getProperty(java.lang.String)
	 */
	public Object getProperty(String key) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().getProperty(String) method instead"); //$NON-NLS-1$
	}

	/** (non-Javadoc)
	 * @see net.refractions.udig.project.ui.tool.Tool#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String key, Object value) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().setProperty(String, String) method instead"); //$NON-NLS-1$

	}

	public void addListener(ToolLifecycleListener listener) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().addListener(ToolLifecycleListener) method instead"); //$NON-NLS-1$

	}

	public void removeListener(ToolLifecycleListener listener) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().removeListener(ToolLifecycleListener) method instead"); //$NON-NLS-1$
	}

    public void notifyResultObtained( boolean result ) {
        setEnabled(result);
    }

	public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().setSelectionProvider(IMapEditorSelectionProvider) method instead"); //$NON-NLS-1$

	}


}
