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
package org.locationtech.udig.printing.ui.internal.editor.policies;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.actions.EditMapAction;
import org.locationtech.udig.printing.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;

/**
 * An action/policy for opening the MapBox's map for editing
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class MapEditPolicy extends ComponentEditPolicy {

    public Command getCommand( Request request ) {
        if (EditMapAction.EDIT_MAP_REQUEST.equals(request.getType())) {
            EditMapCommand command = new EditMapCommand();
            command.setMapBox(((MapBoxPrinter)((Box) getHost().getModel()).getBoxPrinter()));
            return command;
        }
        return super.getCommand(request);
    }
    
    static class EditMapCommand extends Command {
        private MapBoxPrinter mapBox;

        public EditMapCommand() {
            super(Messages.MapEditPolicy_label); 
        }


        public MapBoxPrinter getMapBox() {
            return mapBox;
        }


        public void setMapBox( MapBoxPrinter mapBox ) {
            this.mapBox = mapBox;
        }

        public void execute() {
            ProjectExplorer.getProjectExplorer().open(mapBox.getMap());
        }
    }
}
