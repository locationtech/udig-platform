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

import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsUtil;
import org.locationtech.udig.tools.parallel.internal.command.SetReferenceFeatureCommand;
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

    ParallelContext parallelContext = null;

    public SetReferenceFeatureBehaviour(ParallelContext parallelContext) {

        this.parallelContext = parallelContext;
    }

    public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e,
            EventType eventType) {

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

        if (PrecisionToolsUtil.isFeatureUnderCursor(handler, e)
                && parallelContext.getReferenceFeature() == null) {
            return true;
        }

        return false;
    }

}
