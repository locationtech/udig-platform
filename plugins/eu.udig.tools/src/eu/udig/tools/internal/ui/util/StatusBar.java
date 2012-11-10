/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
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
package eu.udig.tools.internal.ui.util;

import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.jface.action.IStatusLineManager;

/**
 * Presents the messages on status bar
 * <p>
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 * @version $Id: StatusBar.java 653 2009-03-09 09:54:22Z mauro $
 */
public final class StatusBar {

    /**
     * Private constructor to indicate it is a pure utility class
     */
    private StatusBar() {
        // do nothing
    }

    public static void setStatusBarMessage( final IToolContext context, final String message ) {
        context.updateUI(new Runnable(){
            public void run() {
                if (context.getActionBars() == null)
                    return;
                IStatusLineManager bar = context.getActionBars().getStatusLineManager();
                if (bar != null) {
                    bar.setMessage(message);
                    bar.setErrorMessage(null);
                }
            }
        });
    }

    public static void setStatusErrorBarMessage( final IToolContext context, final String message ) {
        context.updateUI(new Runnable(){
            public void run() {
                if (context.getActionBars() == null)
                    return;
                IStatusLineManager bar = context.getActionBars().getStatusLineManager();
                if (bar != null) {
                    bar.setErrorMessage(message);
                    bar.setMessage(null);
                }
            }
        });
    }
}
