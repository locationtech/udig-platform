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
package net.refractions.udig;

import org.eclipse.ui.IStartup;

public class JaiInstallerBootstrap implements IStartup {

    public void earlyStartup() {
        // This looks for a InstallJaiStartup class contributed by a fragment.  If it finds it is executes it.
        // If it doesn't find it then it doesn't worry about it.
        try{
            Class< ? > installerClass = Class.forName("net.refractions.udig.InstallJaiStartup", true, getClass().getClassLoader()); //$NON-NLS-1$
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
