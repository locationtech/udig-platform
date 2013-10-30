/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Selection;

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
