/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui;

import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

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
