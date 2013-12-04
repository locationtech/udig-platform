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

import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;

import com.vividsolutions.jts.geom.Coordinate;

//TOTO remove old imports
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.command.SetInitialPointCommand;

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

    private ParallelContext parallelContext = null;

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
        // XXXX
        // if (parallelContext.getReferenceLine() != null ){// &&
        // !(PrecisionToolsUtil.isFeatureUnderCursor(handler, e))) {
        // return true;
        // }

        if (parallelContext.getReferenceFeature() != null) {// &&
                                                            // !(PrecisionToolsUtil.isFeatureUnderCursor(handler,
                                                            // e))) {
            return true;
        }
        return false;
    }

    public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e,
            EventType eventType) {

        if (!isValid(handler, e, eventType)) {
            throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
        }
        EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());
        Point currPoint = Point.valueOf(e.x, e.y);
        Coordinate coor = bb.toCoord(currPoint);

        SetInitialPointCommand setInitialPointCommand = new SetInitialPointCommand(
                this.parallelContext, coor);
        return setInitialPointCommand;
    }

    public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
