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
package net.refractions.udig.project.ui.tool;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IStatusLineManager;

/**
 * An abstract super class that modal tools can extend.
 * <p>
 * The editor will only maintain one modal tool in the "enabled" state at one time.
 * </p>
 * @author Vitalus 
 * @author jeichar
 * @since 0.3
 * @see AbstractTool
 * @see ModalTool
 */
public abstract class AbstractModalTool extends AbstractTool implements ModalTool {

    
//    String statusBarMessage;
//    String statusBarErrorMessage;
    
    private boolean active;

    
    /** 
     * Current ID of the tool cursor. 
     */
    private String currentCursorID;

    
	/**
	 * By default SimpleTool will simply respond to MOUSE.
	 * <p>
	 * To respond to additional stimulus please override your constuctor
	 * to call AbstractModalTool( targets ):<pre><code>
	 * public class MyTool extends AbstractModalTool {
	 *      public MyTool(){ // default consturctor called by extention point
	 *          super( MOUSE | WHEEL );
	 *      }
	 *      ...
	 * }
	 * </code></pre>
	 */
	public AbstractModalTool(){
		super( MOUSE );
	}	
    /**
     * Creates an new instance of AbstractModalTool
     * 
     * @see AbstractTool#AbstractTool(int)
     */
    public AbstractModalTool( int targets ) {
        super(targets);
    }

    public void setActive( boolean active ) {
        this.active=active;
        setStatusBarMessage(active);
        if (!active) {
            deregisterMouseListeners();
        } else {
        	if(isEnabled()){
        		registerMouseListeners();
        	}
        }
    }

    public boolean isActive() {
        return active;
    }

    private void setStatusBarMessage( final boolean active ) {
        getContext().updateUI(new Runnable(){
            public void run() {
                if( getContext().getActionBars()==null )
                    return;
                IStatusLineManager bar = getContext().getActionBars().getStatusLineManager();
                if (bar != null) {
                    bar.setMessage(""); //$NON-NLS-1$
                    bar.setErrorMessage(""); //$NON-NLS-1$
                }
            }
        });
    }
    
    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#setContext(net.refractions.udig.project.ui.tool.IToolContext)
     */
    public void setContext( IToolContext context ) {
        deregisterMouseListeners();
        this.context = context;
        if( isActive() && isEnabled()){
            registerMouseListeners();
        }
    }
    
    
    
	/**
	 *  (non-Javadoc)
	 * @see net.refractions.udig.project.ui.tool.ModalTool#getCursorID()
	 */
	public final String getCursorID() {
		return currentCursorID;
	}
	
	
	/**
	 *  (non-Javadoc)
	 * @see net.refractions.udig.project.ui.tool.ModalTool#setCursorID(java.lang.String)
	 */
	public final void setCursorID(String id) {
		this.currentCursorID = id;
		
		if(isActive() && getContext() != null && !getContext().getViewportPane().isDisposed()){
			getContext().getViewportPane().setCursor(
					ApplicationGIS.getToolManager().findToolCursor(currentCursorID));
		}
	}

	

	
	
	/** 
	 * (non-Javadoc)
	 * @see net.refractions.udig.project.ui.tool.Tool#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		boolean oldValue = isEnabled();
		
		boolean tempNotify = isNotifyListeners();
		setNotifyListeners(false);
			super.setEnabled(enabled);
		setNotifyListeners(tempNotify);

		if(oldValue != enabled){
			IToolContext toolContext = getContext();
			
			if(!enabled){
				if(toolContext != null){
					if(isActive()){
						deregisterMouseListeners();
					}
					setProperty("latestCursorId", getCursorID()); //$NON-NLS-1$
					setCursorID(ModalTool.NO_CURSOR);
				}
			}else{
				if(toolContext != null){
					if(isActive()){
						registerMouseListeners();
					}
					String defaultCursorId = (String)getProperty("latestCursorId"); //$NON-NLS-1$
					setCursorID(defaultCursorId);
				}
			}
			
		}
		
		if(isNotifyListeners() && oldValue != enabled){
			ToolLifecycleEvent event = new ToolLifecycleEvent(this, ToolLifecycleEvent.Type.ENABLE, enabled, oldValue);
			fireEvent(event);
		}
		
	}
	
	
}
