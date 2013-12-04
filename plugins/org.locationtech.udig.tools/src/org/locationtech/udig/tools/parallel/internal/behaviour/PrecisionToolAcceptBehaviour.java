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
package org.locationtech.udig.tools.parallel.internal.behaviour;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditGeom;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsContext;

/**
 * Behaviour executed after committing a feature or adding to the blackboard. When the new feature
 * is created, clear the blackboard(no geometries selected), and initializes the precision tool
 * context.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class PrecisionToolAcceptBehaviour implements Behaviour {

    private PrecisionToolsContext toolContext = null;

    public PrecisionToolAcceptBehaviour(PrecisionToolsContext context) {

        this.toolContext = context;
    }

    public UndoableMapCommand getCommand(EditToolHandler handler) {

        if (!isValid(handler)) {
            throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
        }
        UndoableComposite composite = new UndoableComposite();
        List<EditGeom> list = new LinkedList<EditGeom>();
        list.add(handler.getCurrentGeom());
        // composite.getCommands().add(new
        // DeselectEditGeomCommand(handler,list));
        // composite.getCommands().add(new SetEditStateCommand(handler,
        // EditState.NONE));
        toolContext.initContext();
        handler.getContext().getViewportPane().repaint();
        return composite;
    }

    public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public boolean isValid(EditToolHandler handler) {
        EditGeom currentGeom = handler.getCurrentGeom();

        boolean currentGeomNotNull = currentGeom != null;
        return currentGeomNotNull;
    }

}
