/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.parallel.internal.behaviour;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsContext;

/**
 * Behaviour executed after committing a feature or adding to the blackboard.
 * When the new feature is created, clear the blackboard(no geometries
 * selected), and initializes the precision tool context.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class PrecisionToolAcceptBehaviour implements Behaviour {

	private PrecisionToolsContext	toolContext	= null;

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
