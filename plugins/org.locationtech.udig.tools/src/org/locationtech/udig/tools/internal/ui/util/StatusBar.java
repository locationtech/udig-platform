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
package org.locationtech.udig.tools.internal.ui.util;

import org.locationtech.udig.project.ui.tool.IToolContext;

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
