/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * 
 * @author jones
 * @since 1.1.0
 */
public class BusyIndicator {
    public static void showWhile(ViewportPane pane, Runnable runnable) {
        Display display=Display.getCurrent();
        if( display==null)
            display=Display.getDefault();
        showWhile(pane, display, runnable);
    }
    
    public static void showWhile(final ViewportPane pane, final Display display, final Runnable runnable) {
        
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                pane.setCursor( display.getSystemCursor(SWT.CURSOR_WAIT) );
                org.eclipse.swt.custom.BusyIndicator.showWhile(display, runnable);
                pane.setCursor(null);
            }
        });
        
        
    }
}
