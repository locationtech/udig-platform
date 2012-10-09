/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui.internal;

import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;

/**
 * Turns off annoying features for developer sanity.
 * 
 * @author chorner
 * @since 1.1.0
 */
public class DeveloperStartup implements IStartup {

    public void earlyStartup() {
        if (CorePlugin.isDeveloping()) {
            closeIntro();
        }
    }
    /**
     * Used to close the intro part to facilitate development.
     */
    private void closeIntro() {
        PlatformGIS.syncInDisplayThread(new Runnable(){

            public void run() {
                IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                IIntroPart intro = introManager.getIntro();
                if (intro != null)
                    introManager.closeIntro(intro);
            }

        });
    }


}
