/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.internal.mediator;

import java.lang.reflect.InvocationTargetException;

import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

/**
 * PlatformGIS Mediator
 * <p>
 * This class delegate all its responsibilities in PaltformGIS facade
 * </p>
 * TODO (mauricio) this class doen't add anything, could be deleted
 * 
 * @see PlatformGIS
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public final class PlatformGISMediator {

    public static void syncInDisplayThread(Runnable runnable) {

        PlatformGIS.syncInDisplayThread(runnable);
    }

    public static void asyncInDisplayThread(Runnable runnable, final boolean executeIfInDisplay) {

        PlatformGIS.asyncInDisplayThread(runnable, false);
    }

    public static void runBlockingOperation(final IRunnableWithProgress runnable,
            final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        PlatformGIS.runBlockingOperation(runnable, monitor);
    }

    public static void runInDisplayThread(Runnable runnable) {

        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        display.asyncExec(runnable);

    }

}
