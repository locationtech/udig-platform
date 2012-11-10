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
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;

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

    
    public EditToolFeedbackBehaviour(EditToolFeedbackManager feedbackManager){
        assert feedbackManager != null;
        this.feedbackManager = feedbackManager;
    }
    
    /**
     * Valid if event type is mouse moved, <code>handler<code>'s current state is
     * {@link EditState#CREATING}, and current editting shape has at leasr 1 point.
     */
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        return feedbackManager.isValid(handler, e, eventType);
    }

    /**
     * Sets the arc to be drawn on the {@link ViewportPane}
     * 
     * @return <code>null</code>, as no undoable map command is needed
     */
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                                          EventType eventType ) {

        return feedbackManager.getFeedbackCommand(handler, e, eventType);

    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        // TODO Auto-generated method stub
        error.printStackTrace();
    }

}
