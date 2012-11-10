/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.internal.mediator;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.ui.PlatformGIS;

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
    

    
    public static void syncInDisplayThread(Runnable runnable){
        
        PlatformGIS.syncInDisplayThread(runnable);
    }

    public static void asyncInDisplayThread(Runnable runnable, final boolean executeIfInDisplay){
        
        PlatformGIS.asyncInDisplayThread(runnable, false);
    }
    
    public static void runBlockingOperation( final IRunnableWithProgress runnable,
                                            final IProgressMonitor monitor ) 
            throws InvocationTargetException, InterruptedException {

        PlatformGIS.runBlockingOperation(runnable, monitor);
    }
    
    public static void runInDisplayThread( Runnable runnable ) {

        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        display.asyncExec(runnable);

    }    
    
    

}
