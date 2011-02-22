/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

/**
 * Cancel all currently running jobs.
 *
 * @author jgarnett
 * @since 0.6.0
 */
public class CancelTool  extends AbstractActionTool {


    /*
     * @see net.refractions.udig.project.ui.tool.ActionTool#run()
     */
    public void run() {
       getContext().getRenderManager().stopRendering();
    }

    /*
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        //do nothing.
    }

}
