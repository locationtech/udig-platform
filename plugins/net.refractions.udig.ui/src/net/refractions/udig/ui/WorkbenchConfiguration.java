/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * The WorkbenchConfiguration configures the Eclipse WorkbenchWindow. 
 * This is called when the workbench is starting up and gives the client
 * application a chance to customize the properties of the WorkbenchWindow.
 * 
 * The implementation of this interface that is called by the framework 
 * is specified by the extension in the preferences under the key 
 * 'net.refractions.udig.ui/workbenchConfiguration'. You can place this in your 
 * product's plugin_customization.ini.
 * 
 * Example:
 * <pre>
 * net.refractions.udig.ui/workbenchConfiguration=net.refractions.udig.internal.ui.uDigWorkbenchConfiguration
 * </pre>
 * 
 * Example class implementation:
 * <pre>
 *    public void configureWorkbench( IWorkbenchWindowConfigurer configurer ) {
 *      configurer.setShowProgressIndicator(true);
 *      configurer.setInitialSize(new Point(800, 600));
 *    
 *      configurer.setShowPerspectiveBar( true );          
 *      configurer.setShowCoolBar(true);        
 *      configurer.setShowStatusLine(true);        
 *      configurer.setShowFastViewBars(true);
 *    }
 * </pre>
 * 
 * @author rgould
 * @since 1.1.0
 */
public interface WorkbenchConfiguration {
    
    public final String XPID = "net.refractions.udig.ui.workbenchConfigurations"; //$NON-NLS-1$
    /**
     * Points to id field of extension point attribute
     */
    public final String ATTR_ID = "id"; //$NON-NLS-1$
    
    /**
     * Points to class field of extension point attribute
     */
    public final String ATTR_CLASS = "class"; //$NON-NLS-1$

    /**
     * Called by the framework when the WorkbenchWindow is being initialized. 
     * Implementing code can set the various properties here.
     *
     * @param configurer IWorkbenchWindowConfigurer used for configuring the workbench window
     */
    public void configureWorkbench( IWorkbenchWindowConfigurer configurer );
}