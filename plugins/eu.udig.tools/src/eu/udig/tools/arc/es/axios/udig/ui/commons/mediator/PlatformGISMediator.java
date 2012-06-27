/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.arc.es.axios.udig.ui.commons.mediator;

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
