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
package net.refractions.udig.tools.edit.handler;

import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Toggles Advanced Editing on and off.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class AdvancedBehaviourCommandHandler extends AbstractHandler implements IHandler {

    private IMapDisplay mapDisplay;
    private MessageBubble messageBubble;

    public AdvancedBehaviourCommandHandler( IMapDisplay mapDisplay ) {
        this.mapDisplay=mapDisplay;
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        PreferenceUtil.instance().setAdvancedEditingActive(!PreferenceUtil.instance().isAdvancedEditingActive());

        displayNewStatus();
        return null;
    }

    private void displayNewStatus(  ) {
        Display display = Display.getCurrent();
        String message=null;       
        boolean active=PreferenceUtil.instance().isAdvancedEditingActive();
        if( active ){
            message=Messages.AdvancedBehaviourCommandHandler_enabledLabel;
        }else{
            message=Messages.AdvancedBehaviourCommandHandler_disabledLabel;
        }
        
        if( message!=null ){
            if( messageBubble!=null && messageBubble.isValid() )
                messageBubble.setValid(false);
            Control control=(Control) mapDisplay;
            Point mouseLocation = control.toControl(display.getCursorLocation());
            messageBubble = new MessageBubble(mouseLocation.x, mouseLocation.y,
                                message, PreferenceUtil.instance().getMessageDisplayDelay());
            AnimationUpdater.runTimer(mapDisplay, messageBubble);
        }
    }

}
