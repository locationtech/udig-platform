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
package net.refractions.udig.tools.edit.behaviour.accept;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DifferenceFeatureCommand;

/**
 * Executes the {@link DifferenceFeatureCommand}
 *
 * <p>Requirements:
 * <ul>
 * <li>currentShape is not null</li>
 * <li>edit blackboard has a geometry that is not the currentShape</li>
 * <li></li>
 * </ul>
 * </p>
 * @author jones
 * @since 1.1.0
 */
public class DifferenceFeatureAcceptBehaviour implements Behaviour {

    public boolean isValid( EditToolHandler handler ) {
        return handler.getCurrentShape()!=null && handler.getCurrentShape().getNumPoints()>0;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        return new DifferenceFeatureCommand(handler, EditState.NONE);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
