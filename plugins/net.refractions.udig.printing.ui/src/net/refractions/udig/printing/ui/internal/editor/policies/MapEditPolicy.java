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
package net.refractions.udig.printing.ui.internal.editor.policies;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;
import net.refractions.udig.printing.ui.actions.EditMapAction;
import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectExplorer;

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
