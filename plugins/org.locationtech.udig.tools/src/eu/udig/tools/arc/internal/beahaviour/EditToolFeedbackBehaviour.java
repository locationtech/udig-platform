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
