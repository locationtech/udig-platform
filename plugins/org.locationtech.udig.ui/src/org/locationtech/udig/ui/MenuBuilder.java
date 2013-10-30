/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * This class is used to configure a menu bar and a cool bar. This is called
 * when the workbench starts up. The uDig ActionBarAdvisor delegates to this
 * class.
 * 
 * The implementation of this interface that is called by the framework 
 * is specified by the extension in the preferences under the key 
 * 'org.locationtech.udig.ui/menuBuilder'. You can place this in your 
 * product's plugin_customization.ini.
 * 
 * Example entry in plugin_customization.ini:
 * <pre>
 * org.locationtech.udig.ui/menuBuilder=org.locationtech.udig.ui.UDIGMenuBuilder
 * </pre>
 * 
 * See org.locationtech.udig.ui.UDIGMenuBuilder for an example implementation.
 * 
 * @deprecated Please use org.eclipse.ui.menus extension point
 * @author Richard Gould, Refractions Research Inc.
 * @since 1.1.0
 */
public interface MenuBuilder {

    public final String XPID = "org.locationtech.udig.ui.menuBuilders"; //$NON-NLS-1$
    /**
     * Points to id field of extension point attribute
     */
    public final String ATTR_ID = "id"; //$NON-NLS-1$
    
    /**
     * Points to class field of extension point attribute
     */
    public final String ATTR_CLASS = "class"; //$NON-NLS-1$
    
    /**
     * Instructs this class that it should configure and fill the provided 
     * IMenuManager with menus and actions.  
     * 
     * @param menuBar
     * @param window The window that contains this menu
     */
    public abstract void fillMenuBar( IMenuManager menuBar, IWorkbenchWindow window );

    /**
     * Instructs this class to configure and fill the provided ICoolBarManager 
     * with actions and buttons.
     *  
     * @param coolBar
     * @param window The window that contains the CoolBar
     */
    public abstract void fillCoolBar( ICoolBarManager coolBar, IWorkbenchWindow window );

}
