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
package net.refractions.udig.catalog.internal.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Default handler for the reset service command.
 * <p>
 * This is used to whack the selected service really hard and force it to
 * try and reconnect. Since we do not control service implementations
 * that closely we simply create a new IService from scratch; and if the new service
 * can connect we replace the existing catalog event.
 * <p>
 * The replace in catalog event is broadcast to existing maps (indeed anyone using
 * a service is supposed to listen); so they can update their reference as needed.
 * <p>
 * @author Jody Garnett
 */
public class ResetServiceHandler extends AbstractHandler {

    /**
     * Reset the indicated service, we will make an effort to reconnect
     * to the service (and if successful) and replace the existing entry
     * in the catalogue.
     */
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        Object context = event.getApplicationContext();
        Object trigger = event.getTrigger();
        Map parameters = event.getParameters();
        
        if( context instanceof IStructuredSelection){
            reset( (IStructuredSelection) context );
        }
        return null;
    }

    private void reset( final IStructuredSelection context ) {
        PlatformGIS.run(new ISafeRunnable(){

            public void handleException( Throwable exception ) {
                CatalogUIPlugin.log("Error resetting: "+context, exception); //$NON-NLS-1$
            }

            public void run() throws Exception {
                List<IService> servers = new ArrayList<IService>();
                for( Iterator selection = context.iterator(); selection.hasNext(); ) {
                    try {
                        servers.add((IService) selection.next());
                    } catch (ClassCastException huh) {
                        CatalogUIPlugin.trace("Should not happen: " + huh); //$NON-NLS-1$
                    }
                }
                ResetService.reset(servers, null);
            }            
        });
    }

}
