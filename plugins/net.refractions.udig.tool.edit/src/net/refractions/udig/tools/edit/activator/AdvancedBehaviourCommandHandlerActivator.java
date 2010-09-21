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
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.handler.AdvancedBehaviourCommandHandler;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * Enables the {@link net.refractions.udig.tools.edit.handler.SnapBehaviourCommandHandler} on the CycleSnapBehaviour command.
 * @author Jesse
 * @since 1.1.0
 */
public class AdvancedBehaviourCommandHandlerActivator implements Activator {
    private static final String COMMAND_ID = "net.refractions.udig.tool.edit.advanced.edit.command"; //$NON-NLS-1$
    ICommandService service = (ICommandService) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class);
    private IHandler commandHandler;;

    public void activate( EditToolHandler handler ) {
        commandHandler=new AdvancedBehaviourCommandHandler(handler.getContext().getMapDisplay());
        Command command = service.getCommand(COMMAND_ID);
        if (command != null)
            command.setHandler(commandHandler);
    }

    public void deactivate( EditToolHandler handler ) {
        commandHandler=null;
        Command command = service.getCommand(COMMAND_ID);
        if (command != null)
            command.setHandler(null);
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
