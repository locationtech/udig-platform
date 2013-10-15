/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
