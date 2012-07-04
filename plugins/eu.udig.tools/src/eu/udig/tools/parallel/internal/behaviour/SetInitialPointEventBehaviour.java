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

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;

import com.vividsolutions.jts.geom.Coordinate;

//TOTO remove old imports
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import eu.udig.tools.parallel.internal.ParallelContext;
import eu.udig.tools.parallel.internal.command.SetInitialPointCommand;

/**
 * 
 * The user points the initial point.
 * <p>
 * Requirements:
 * <ul>
 * <li>state==MODIFYING or NONE</li>
 * <li>event type == RELEASED</li>
 * <li>button1 must be the button that was released</li>
 * <li>Must exist a previous reference line</li>
 * <li>Must not be over a feature</li>
 * </ul>
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class SetInitialPointEventBehaviour implements EventBehaviour {

	private ParallelContext	parallelContext	= null;

	public SetInitialPointEventBehaviour(ParallelContext parallelContext) {

		this.parallelContext = parallelContext;
	}

	public boolean isValid(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		boolean legalState = handler.getCurrentState() == EditState.NONE
					|| handler.getCurrentState() == EditState.MODIFYING;
		boolean legalButton = e.button == MapMouseEvent.BUTTON1;
		boolean releasedEvent = eventType == EventType.RELEASED;

		if (!legalState || !legalButton || !releasedEvent) {
			return false;
		}
		//XXXX
//		if (parallelContext.getReferenceLine() != null ){// && !(PrecisionToolsUtil.isFeatureUnderCursor(handler, e))) {
//			return true;
//		}
		
		if (parallelContext.getReferenceFeature() != null ){// && !(PrecisionToolsUtil.isFeatureUnderCursor(handler, e))) {
			return true;
		}
		return false;
	}

	public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		if (!isValid(handler, e, eventType)) {
			throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
		}
		EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());
		Point currPoint = Point.valueOf(e.x, e.y);
		Coordinate coor = bb.toCoord(currPoint);

		SetInitialPointCommand setInitialPointCommand = new SetInitialPointCommand(this.parallelContext, coor);
		return setInitialPointCommand;
	}

	public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
		EditPlugin.log("", error); //$NON-NLS-1$
	}

}
