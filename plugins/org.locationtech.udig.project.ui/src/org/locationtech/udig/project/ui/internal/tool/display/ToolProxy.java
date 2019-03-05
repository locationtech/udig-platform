/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.internal.ui.operations.OperationCategory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.MapEditorSelectionProvider;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.tool.AbstractTool;
import org.locationtech.udig.project.ui.tool.ActionTool;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.Tool;
import org.locationtech.udig.project.ui.tool.ToolConstants;
import org.locationtech.udig.project.ui.tool.ToolLifecycleListener;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.EnablementUtil;
import org.locationtech.udig.ui.operations.LazyOpFilter;
import org.locationtech.udig.ui.operations.OpFilter;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The tool proxy allows tools to be loaded lazily. It acts as a proxy for a tool as far as the Map
 * editors are concerned.
 * 
 * @author Jesse Eichar
 * @version 1.3.0
 */
public class ToolProxy extends ModalItem implements ModalTool, ActionTool {
 
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
    private static final String DEFAULT_ID = "org.locationtech.udig.tools.Zoom"; //$NON-NLS-1$

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
     * @param definition The configuration element which describes the tool
     * @param toolManager ToolManager responsible for this tool
     */
    public ToolProxy( IExtension extension, IConfigurationElement definition, ToolManager toolManager ) {
        super();
        
        this.toolManager = toolManager;
        
        categoryId = definition.getAttribute("categoryId"); //$NON-NLS-1$
        String type = definition.getName();
        String pluginid = extension.getNamespaceIdentifier() ;
        String id = definition.getAttribute("id"); //$NON-NLS-1$
        String name = definition.getAttribute("name"); //$NON-NLS-1$
        if (name == null)
            name = Messages.ToolProxy_unnamed; 
        String toolTip = definition.getAttribute("tooltip"); //$NON-NLS-1$
        String iconID = definition.getAttribute("icon"); //$NON-NLS-1$
        String largeIconID = definition.getAttribute("largeIcon"); //$NON-NLS-1$
        
        String preferencePageId = definition.getAttribute("preferencePageId"); //$NON-NLS-1$
        //setup attributes used by status bare tool options
        setPreferencePageId(preferencePageId);
        

        defaultCursorID = definition.getAttribute("toolCursorId"); //$NON-NLS-1$
        
        //FIXME For compatibility. To BE REMOVED later.
        if(defaultCursorID == null){
        	IConfigurationElement[] children = definition.getChildren("cursor"); //$NON-NLS-1$
        	if(children.length > 0){
        		CursorProxy cursorProxy = new CursorProxy(children[0]);
        		toolManager.cursorsCache.put(cursorProxy.getID(), cursorProxy);
        		defaultCursorID = cursorProxy.getID();
        	}else{
        		defaultCursorID = ModalTool.DEFAULT_CURSOR;
        	}
        }
        
        OpFilter parseEnablement = EnablementUtil.parseEnablement( extension.getNamespaceIdentifier()+"."+definition.getName(), definition.getChildren("enablement")); //$NON-NLS-1$ //$NON-NLS-2$;
        enablement = new LazyOpFilter(this, parseEnablement); 
        operationCategories = parseOperationCategories(definition);
        
        String bool = definition.getAttribute("hasCustomControl"); //$NON-NLS-1$
        hasControl = ((bool != null) && bool.equalsIgnoreCase("true")) ? true : false; //$NON-NLS-1$
        bool = definition.getAttribute("onToolbar"); //$NON-NLS-1$
        onToolbar = ((bool != null) && bool.equalsIgnoreCase("true")) ? true : false; //$NON-NLS-1$
        menuPath = definition.getAttribute("menuPath"); //$NON-NLS-1$
        ImageDescriptor icon;
        if (iconID == null) {
            icon = null;
        } else {
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(pluginid, iconID);
            setImageDescriptor(icon);
        }
        
        if (largeIconID == null ) {
            // default to normal size so we have something
            setLargeImageDescriptor( icon );
        }
        else {
            ImageDescriptor largeIcon = icon = AbstractUIPlugin.imageDescriptorFromPlugin(pluginid, largeIconID);
            setLargeImageDescriptor( largeIcon );
        }
        
        this.element = definition;
        setName(name);
        setToolTipText(toolTip);
        setId(id);
        if (type.equals("modalTool")) //$NON-NLS-1$
            this.type = MODAL;
        else if (type.equals("backgroundTool")) //$NON-NLS-1$
            this.type = BACKGROUND;
        else if (type.equals("actionTool")) //$NON-NLS-1$
            this.type = ACTION;
        handlerType = definition.getAttribute(HandlerProxy.ID);

        String unparsedCommandIds = definition.getAttribute("commandIds"); //$NON-NLS-1$
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

    /**
     * @see org.eclipse.jface.action.Action#getStyle()
     */
    public int getStyle() {
        if (type == MODAL)
            return IAction.AS_RADIO_BUTTON;
        return IAction.AS_PUSH_BUTTON;
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#setContext(org.locationtech.udig.project.ui.tool.IToolContext)
     */
    public void setContext( IToolContext toolContext ) {
        this.toolContext = toolContext;

        if (type == BACKGROUND) {
            getTool().setContext(toolContext);
            
        } else if ( type == MODAL && toolManager.getActiveToolProxy() == this) {
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
                                    	ModalTool modalTool = (ModalTool) tool;
                                    	modalTool.setSelectionProvider(selectionProvider);
                                    }

                                } catch (CoreException e) {
                                    ProjectUIPlugin.log("Error loading tool", e); //$NON-NLS-1$
                                }
                            }
                            
                            
                            IConfigurationElement[] toolElementChildren = element.getChildren();
                            
                            for ( IConfigurationElement toolElement : toolElementChildren){
                                
                                if(toolElement.getAttribute("class") != null){
                                    try {
                                        String contributionId = toolElement.getAttribute("id");
                                        
                                        Object optionsContribution = toolElement.createExecutableExtension("class");
                                        ContributionItem contributionItem = (ContributionItem) optionsContribution;
                                        contributionItem.setId(contributionId);
                                        
                                        addOptionsContribution((ContributionItem) optionsContribution);
                                    } catch (CoreException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
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
     * @see org.locationtech.udig.project.ui.tool.Tool#getContext()
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
     * @see org.locationtech.udig.project.ui.tool.Tool#isEnabled()
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
     * @see org.locationtech.udig.project.ui.tool.ModalTool#setEnabled(boolean)
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
     * Called when the user clicks on the button or otherwise "Activates" the
     * tool represented here.
     */
    @Override
    public void run() {
        if( isModeless() ){
            runModeless();
            if( isEnabled() ){
                return;
            }
            // the original code followed through into the activation
            // code I am not sure as to the logic? it may of been an accident of implementation
        }
        else {
            // we are going to carefully activate in this display thread
            // (so the user interface buttons can be updated)
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    runModal();
                }
            });
        }
    }
    
    /**
     * This method is responsible for for ensuring that this ToolProxy is activated and that the
     * tool manager knows about it.
     */
    @Override
    protected void runModal() {
        // activate the current tool
        // (we expect this will update the tool manager)
        setActive(true);
        
        // a bit of quality assurance here 
        // while we expect the above setActive method to update
        // that active item we will double check now
        ToolProxy activeToolProxy = getActiveItem();
    	if (activeToolProxy == this){
    	    // good that worked then
    	}
    	else {
    	    // okay we will chagne the active item ourself
    	    if(activeToolProxy != null ){
                activeToolProxy.setActive(false);    	        
    	    }
    	    // this is the current active item from the tool manager
            setActiveItem(this);
    	}
    }

    /**
     * We will call runModeless() if type == ACTION; if not runModal is used.
     */
    @Override
    protected boolean isModeless() {
        return type == ACTION;
    }
	/**
	 * Runs getModelessTool() safely in the display thread.
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#runModeless()
     */
    protected void runModeless() {
        if (type != ACTION) {
            throw new IllegalStateException("runModeless expects ACTION");
        }
        PlatformGIS.run(new ISafeRunnable(){
            public void run() throws Exception {
                getModelessTool().run();
            }
            public void handleException( Throwable exception ) {
                ProjectUIPlugin
                        .log("Error occured while executing tool: " + getId(), exception); //$NON-NLS-1$
            }
        });
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#setActive(boolean)
     */
    public void setActive( boolean active ) {
    	if (toolContext == null){
    		return; // cannot be active as the map is not ready yet
    	}
    	setChecked(active); // will check this in each menu / toolbar contribution 
    	
    	if (getTool() instanceof ModalTool) {
            if( isActive() != active ){
                ModalTool modalTool = getModalTool();
                if (active){
                    // allow tool manager to update the activeTool cursor etc.. --> calls setActive()
                    toolManager.setActiveModalToolProxy(this);
//                  String currentCursorID = modalTool.getCursorID();
//                  toolContext.getViewportPane().setCursor(toolManager.findToolCursor(currentCursorID));
                }
                else
                {
                    modalTool.setActive(active); // only called when deactivated, because it gets also called by toolManager.setActiveModalToolProxy
                }
            }
    	}
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#dispose()
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
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#getHandler(java.lang.String)
     */
    public IHandler getHandler( String commandId ) {
        if (handlerType != null && handlerType.length() > 0)
            return new HandlerProxy(element, this, commandId);
        return null;
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#getDefaultItem()
     */
    protected ModalItem getDefaultItem() {
        return toolManager.defaultModalToolProxy;
    }

    /**
     * 
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#getActiveItem()
     */
    protected ToolProxy getActiveItem() {
        return toolManager.getActiveToolProxy();
    }

    /**
     * @see org.locationtech.udig.project.ui.internal.tool.display.ModalItem#setActiveItem(org.locationtech.udig.project.ui.internal.tool.display.ModalItem)
     */
    protected void setActiveItem( ModalItem modalItem ) {
    	toolManager.setActiveModalToolProxy( (ToolProxy)modalItem );
    }


    /** (non-Javadoc)
     * @see org.locationtech.udig.project.ui.tool.ModalTool#isActive()
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
                IToolManager m = ApplicationGIS.getToolManager();
                ModalToolCategory cat = m.findModalCategory(getCategoryId());
                if (cat != null){
                    selectionProviderInstance = cat.getSelectionProvider();
                }
                else {
                    // default just provides the Map as the selection
                    selectionProviderInstance = new MapEditorSelectionProvider();
                }
            } finally {
                selectionProviderLock.unlock();
            }
        }
        return selectionProviderInstance;
    }
    
    
    /** (non-Javadoc)
     * @see org.locationtech.udig.project.ui.tool.ModalTool#getCursorID()
     */
    public String getCursorID() {
    	throw new UnsupportedOperationException("Call ToolProxy.getTool().getCursorID() method instead"); //$NON-NLS-1$
	}

    
	/**
	 * Empty implementation
	 * 
	 * @see org.locationtech.udig.project.ui.tool.ModalTool#setCursorID(java.lang.String)
	 */
	public void setCursorID(String id) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().setCursorID(String) method instead"); //$NON-NLS-1$
	}

	/** (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#getProperty(java.lang.String)
	 */
	public Object getProperty(String key) {
		throw new UnsupportedOperationException("Call ToolProxy.getTool().getProperty(String) method instead"); //$NON-NLS-1$
	}

	/** (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.Tool#setProperty(java.lang.String, java.lang.Object)
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
