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
    public AcceptFeedbackBehaviour( EditToolFeedbackManager feedbackManager ) {
        assert feedbackManager != null;
        this.feedbackManager = feedbackManager;
    }

    /**
     * @return always <code>true</code> as it is not this behaviour responsibility to decide when
     *         to be executed, but its expected to be called once the associated tool has been
     *         accepted
     */
    public boolean isValid( EditToolHandler handler ) {
        return true;
    }

    /**
     * @return the command returned by the provided EditToolFeedbackManager's
     *         {@link EditToolFeedbackManager#getAcceptCommand(EditToolHandler) getAcceptCommand}
     *         method.
     */
    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        return feedbackManager.getAcceptCommand(handler);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        // TODO Auto-generated method stub
    }

}
