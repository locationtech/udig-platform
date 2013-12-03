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
 * Cancel {@link Behaviour} that operates over a given {@link EditToolFeedbackManager}
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 * @see EditToolFeedbackManager
 */
public class CancelFeedbakBehaviour implements Behaviour {

    private EditToolFeedbackManager feedbackManager;

    /**
     * @return always <code>true</code> as it is not this behaviour responsibility to determine
     *         when to be ran, but assumes the cancel action on the associated tool has been called.
     */
    public boolean isValid( EditToolHandler handler ) {
        return true;
    }

    public CancelFeedbakBehaviour( EditToolFeedbackManager feedbackManager ) {
        assert feedbackManager != null;
        this.feedbackManager = feedbackManager;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        return feedbackManager.getCancelCommand(handler);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        // TODO Auto-generated method stub
    }

}
