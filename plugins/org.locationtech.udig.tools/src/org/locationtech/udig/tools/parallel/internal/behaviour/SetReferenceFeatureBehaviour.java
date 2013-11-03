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

//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsUtil;
import org.locationtech.udig.tools.parallel.internal.command.SetReferenceFeatureCommand;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.command.SetReferenceFeatureCommand;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsUtil;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;

/**
 * The user select the reference line.
 * <p>
 * Requirements:
 * <ul>
 * <li>state==MODIFYING or NONE</li>
 * <li>event type == RELEASED</li>
 * <li>button1 must be the button that was released</li>
 * <li>Mouse over a geometry</li>
 * </ul>
 * </p>
 * 
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class SetReferenceFeatureBehaviour implements EventBehaviour {

	ParallelContext	parallelContext	= null;

	public SetReferenceFeatureBehaviour(ParallelContext parallelContext) {

		this.parallelContext = parallelContext;
	}

	public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		if (!isValid(handler, e, eventType)) {
			throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
		}

		SetReferenceFeatureCommand cmd = new SetReferenceFeatureCommand(parallelContext, handler, e);
		return cmd;
	}

	public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {

		EditPlugin.log("", error); //$NON-NLS-1$
	}

	public boolean isValid(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		boolean legalState = handler.getCurrentState() == EditState.NONE
					|| handler.getCurrentState() == EditState.MODIFYING;
		boolean releaseButtonState = eventType == EventType.RELEASED;
		boolean legalButton = e.button == MapMouseEvent.BUTTON1;

		if (!(legalState && releaseButtonState && legalButton)) {
			return false;
		}

		if (PrecisionToolsUtil.isFeatureUnderCursor(handler, e) && parallelContext.getReferenceFeature() == null) {
			return true;
		}

		return false;
	}

}
