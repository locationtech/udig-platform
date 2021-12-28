/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.ui.IStartup;

public class JaiInstallerBootstrap implements IStartup {

    /**
     * This looks for a InstallJaiStartup class contributed by a fragment. If it finds it is
     * executes it. If it doesn't find it then it doesn't worry about it.
     */
    @Override
    public void earlyStartup() {
        try {
            Class<?> installerClass = Class.forName("org.locationtech.udig.InstallJaiStartup", true, //$NON-NLS-1$
                    getClass().getClassLoader());
            Runnable installer = (Runnable) installerClass.getDeclaredConstructor().newInstance();
            installer.run();
        } catch (ClassNotFoundException e) {
            // Its OK
        } catch (InstantiationException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (IllegalAccessException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (IllegalArgumentException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (NoSuchMethodException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (SecurityException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }
}
