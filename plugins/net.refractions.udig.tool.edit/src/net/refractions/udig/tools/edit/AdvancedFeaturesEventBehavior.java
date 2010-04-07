/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit;

import java.util.List;

import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

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
