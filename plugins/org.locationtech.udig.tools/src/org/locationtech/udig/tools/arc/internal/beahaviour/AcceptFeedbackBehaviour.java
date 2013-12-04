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
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolHandler;

/**
 * Edit tool feedback behaviour to be ran as an accept behaviour
 * <p>
 * Operates upon a provided {@link EditToolFeedbackManager}, and is intended to be used by an
 * implementation of
 * <code>AbstractEditTool.initAcceptBehaviours( List<Behaviour> acceptBehaviours )</code> to
 * establish the feedback actions to run when the tool interaction ends up being accepted.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 * @see EditToolFeedbackManager#getAcceptCommand(EditToolHandler)
 */
public class AcceptFeedbackBehaviour implements Behaviour {

    private EditToolFeedbackManager feedbackManager;

    /**
     * @param feedbackManager the feedback manager to rely on when
     *        {@link #getCommand(EditToolHandler)} is called
     */
    public AcceptFeedbackBehaviour(EditToolFeedbackManager feedbackManager) {
        assert feedbackManager != null;
        this.feedbackManager = feedbackManager;
    }

    /**
     * @return always <code>true</code> as it is not this behaviour responsibility to decide when to
     *         be executed, but its expected to be called once the associated tool has been accepted
     */
    public boolean isValid(EditToolHandler handler) {
        return true;
    }

    /**
     * @return the command returned by the provided EditToolFeedbackManager's
     *         {@link EditToolFeedbackManager#getAcceptCommand(EditToolHandler) getAcceptCommand}
     *         method.
     */
    public UndoableMapCommand getCommand(EditToolHandler handler) {
        return feedbackManager.getAcceptCommand(handler);
    }

    public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
        // TODO Auto-generated method stub
    }

}
