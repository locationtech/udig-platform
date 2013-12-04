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
package org.locationtech.udig.tools.arc.internal.beahaviour;

import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;

/**
 * Behaviour that draws the preview arc while the user specifies the anchor points
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 * @see EditToolFeedbackManager
 */
public class EditToolFeedbackBehaviour implements EventBehaviour {

    private EditToolFeedbackManager feedbackManager;

    public EditToolFeedbackBehaviour(EditToolFeedbackManager feedbackManager) {
        assert feedbackManager != null;
        this.feedbackManager = feedbackManager;
    }

    /**
     * Valid if event type is mouse moved, <code>handler<code>'s current state is
     * {@link EditState#CREATING}, and current editting shape has at leasr 1 point.
     */
    public boolean isValid(EditToolHandler handler, MapMouseEvent e, EventType eventType) {
        return feedbackManager.isValid(handler, e, eventType);
    }

    /**
     * Sets the arc to be drawn on the {@link ViewportPane}
     * 
     * @return <code>null</code>, as no undoable map command is needed
     */
    public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e,
            EventType eventType) {

        return feedbackManager.getFeedbackCommand(handler, e, eventType);

    }

    public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
        // TODO Auto-generated method stub
        error.printStackTrace();
    }

}
