/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

/**
 * Rollback current transaction
 * @author jgarnett
 * @since 0.6.0
 */
public class RollbackTool  extends AbstractActionTool {


    /*
     * @see net.refractions.udig.project.ui.tool.ActionTool#run()
     */
    public void run() {
        getContext().sendASyncCommand(getContext().getEditFactory().createRollbackCommand());
    }

    /*
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
    }

}
