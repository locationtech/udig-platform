/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
 * <p>Requirements: * <ul> * <li>currentShape is not null</li>
 * <li>edit blackboard has a geometry that is not the currentShape</li>
 * <li></li> * </ul> * </p> * @author jones
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
