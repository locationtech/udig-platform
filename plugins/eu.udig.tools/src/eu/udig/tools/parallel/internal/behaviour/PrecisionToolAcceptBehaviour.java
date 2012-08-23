/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.parallel.internal.behaviour;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditGeom;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsContext;
import eu.udig.tools.parallel.internal.PrecisionToolsContext;

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
