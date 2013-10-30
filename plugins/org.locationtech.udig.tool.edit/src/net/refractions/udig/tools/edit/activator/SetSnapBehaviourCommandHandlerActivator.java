/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.handler.SnapBehaviourCommandHandler;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * Enables the {@link net.refractions.udig.tools.edit.handler.SnapBehaviourCommandHandler} on the CycleSnapBehaviour command.
 * @author Jesse
 * @since 1.1.0
 */
public class SetSnapBehaviourCommandHandlerActivator implements Activator {
    private static final String COMMAND_ID = "net.refractions.udig.tool.edit.cycle.snap.behaviour"; //$NON-NLS-1$
    ICommandService service = (ICommandService) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class);
    private IHandler commandHandler;

    public void activate( EditToolHandler handler ) {
        Command command = service.getCommand(COMMAND_ID);
        IToolContext context = handler.getContext();
        commandHandler=new SnapBehaviourCommandHandler(context.getMapDisplay(), context.getMap());
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
