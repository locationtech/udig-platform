/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import java.util.List;

import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;

/**
 * Behaviour is valid only when the advanced preference is set.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class AdvancedFeaturesEventBehavior implements EventBehaviour, LockingBehaviour {


    private OrderedCompositeEventBehavior composite;
    private OrderedCompositeEventBehavior elseList;

    public AdvancedFeaturesEventBehavior( List<EventBehaviour> behaviours ) {
        composite=new OrderedCompositeEventBehavior(behaviours, false);
    }

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if (!PreferenceUtil.instance().isAdvancedEditingActive() ){
            return elseList!=null && elseList.isValid(handler, e, eventType);
        }
        
        return composite.isValid(handler, e,eventType);
        
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {

        if (!PreferenceUtil.instance().isAdvancedEditingActive() )
            return elseList.getCommand(handler, e, eventType);
        return composite.getCommand(handler, e, eventType );
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        composite.handleError(handler, error, command);
    }

    public Object getKey(EditToolHandler handler) {
        return composite.getKey(handler);
    }
    @Override
    public String toString() {
        return "Advanced Mode:"+composite.toString(); //$NON-NLS-1$
    }

    public void setElse( List<EventBehaviour> elseList ) {
        this.elseList=new OrderedCompositeEventBehavior(elseList,false);
    }
}
