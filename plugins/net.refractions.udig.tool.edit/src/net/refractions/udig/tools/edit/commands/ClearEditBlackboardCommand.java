/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Removes all the geoms from the edit blackboard.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class ClearEditBlackboardCommand extends AbstractCommand implements UndoableMapCommand {

    private EditBlackboard blackboard;
    private EditToolHandler handler;
    private DeselectEditGeomCommand command;

    public ClearEditBlackboardCommand( EditToolHandler handler, EditBlackboard blackboard ) {
        this.blackboard = blackboard;
        this.handler = handler;
    }

    public String getName() {
        return Messages.ClearEditBlackboardCommand_name;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        command = new DeselectEditGeomCommand(handler, blackboard.getGeoms());
        command.setMap(getMap());
        command.run(monitor);
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        command.rollback(monitor);
    }

}
