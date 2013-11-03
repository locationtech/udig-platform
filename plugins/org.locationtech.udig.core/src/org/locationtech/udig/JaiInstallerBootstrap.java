/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig;

import org.eclipse.ui.IStartup;

public class JaiInstallerBootstrap implements IStartup {

    public void earlyStartup() {
        // This looks for a InstallJaiStartup class contributed by a fragment.  If it finds it is executes it.
        // If it doesn't find it then it doesn't worry about it.
        try{
            Class< ? > installerClass = Class.forName("org.locationtech.udig.InstallJaiStartup", true, getClass().getClassLoader()); //$NON-NLS-1$
            Runnable installer = (Runnable) installerClass.newInstance();
            installer.run();
        }catch (ClassNotFoundException e) {
            // Its ok
//            CorePlugin.log("JAI Installer class not found", e); //$NON-NLS-1$
        } catch (InstantiationException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (IllegalAccessException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
}
