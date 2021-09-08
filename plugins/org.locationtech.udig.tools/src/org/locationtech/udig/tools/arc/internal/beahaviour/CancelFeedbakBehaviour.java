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

    }

}
