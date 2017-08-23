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
package org.locationtech.udig.project.ui.tool;


import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.Platform;
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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#setContext(org.locationtech.udig.project.ui.tool.IToolContext)
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
	 * @see org.locationtech.udig.project.ui.tool.ModalTool#getCursorID()
	 */
	public final String getCursorID() {
		return currentCursorID;
	}
	
	
	/**
	 *  (non-Javadoc)
	 * @see org.locationtech.udig.project.ui.tool.ModalTool#setCursorID(java.lang.String)
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
	 * @see org.locationtech.udig.project.ui.tool.Tool#setEnabled(boolean)
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
