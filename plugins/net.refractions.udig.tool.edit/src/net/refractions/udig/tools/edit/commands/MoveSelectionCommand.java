/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Selection;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Moves a selection on the edit blackboard. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MoveSelectionCommand extends AbstractCommand implements UndoableCommand {

    private int deltaX;
    private int deltaY;
    private EditBlackboard editBlackboard;
    private Selection selection;

    public MoveSelectionCommand( EditBlackboard editBlackboard, int deltaX, int deltaY, Selection toMove ) {
        this.editBlackboard=editBlackboard;
        this.deltaX=deltaX;
        this.deltaY=deltaY;
        this.selection=toMove;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        editBlackboard.moveSelection(-deltaX, -deltaY, selection);
        ((ViewportPane) getMap().getRenderManagerInternal().getMapDisplay()).repaint();
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        editBlackboard.moveSelection(deltaX, deltaY, selection);
        ((ViewportPane) getMap().getRenderManagerInternal().getMapDisplay()).repaint();
    }

    public String getName() {
        return Messages.MoveSelectionCommand_name;
    }

}
