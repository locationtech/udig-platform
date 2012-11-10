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
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.arc.internal.beahaviour;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolHandler;

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
