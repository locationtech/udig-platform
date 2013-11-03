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
package org.locationtech.udig.ui.internal;

import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.ui.PlatformGIS;

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
