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
package net.refractions.udig.tool.info;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * An InfoPanel is used to display a specific LayerPointInfo.
 * <p>
 * This interface is used by the net.refractions.udig.info.infoPanel
 * extention point to teach the InfoTool new tricks. The extention point
 * defines what MIME type this InfoPanel can respond to.
 * </p><p>
 * Responsibility is based on MIME type.
 * <ul>
 * <li>text/plain
 * <li>text/html
 * </ul>
 * </p><p>
 * I am a bit concerned that we will need to have LayerPointInfo remember the
 * http request. That is; as much as WMS wants to make requests on its
 * own - this is not what we need.
 * </p>
 * @author Jody Garnett
 * @since 0.6
 */
public abstract class InfoDisplay {

    /**
     * Some displays, like a browser, requre a url to function.
     * 
     * @return true if LayerPointInfo is required to have a request URL
     */
    public boolean isUrlRequired(){
        return false;
    }
    
    /**
     * Access control created by createDisplay.
     * 
     * @return Control used to display LayerPointInfo 
     */
    public abstract Control getControl();
    
    /**
     * Creates the SWT controls for this InfoDisplay.
     * <p>
     * For implementors this is a multi-step process:
     * <ol>
     *   <li>Create one or more controls within the parent.</li>
     * </ol>
     * </p>
     * @param parent the parent control
     */
    abstract public void createDisplay( Composite parent );
    
    /**
     * Called by the InfoView to request display.
     * <p>
     * This method is called just before getControl().setVisiable().
     * </p>
     * @param info LayerPointInfo to display, or null to disable.
     */
    abstract public void setInfo( LayerPointInfo info );
    
    /**
     * Clean up any used resources
     */
    public void dispose(){
        getControl().dispose();
    }
    
}
