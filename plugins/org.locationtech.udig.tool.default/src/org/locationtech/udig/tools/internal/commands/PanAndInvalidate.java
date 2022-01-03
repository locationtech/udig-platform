package org.locationtech.udig.tools.internal.commands;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.internal.commands.draw.TranslateCommand;

/**
 * Executes the specified pan command, and only after it is executed, expires the last translate command
 */
public class PanAndInvalidate implements Command, NavCommand {

    private NavCommand command;
    private TranslateCommand expire;

    public PanAndInvalidate(NavCommand command, TranslateCommand expire) {
        this.command = command;
        this.expire = expire;
    }

    @Override
    public String getName() {
        return "PanAndDiscard"; //$NON-NLS-1$
    }

    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        //first we need to expire the current translation
        expire.setValid(false);

        //then we can draw
        command.run(monitor);
    }

    @Override
    public void setViewportModel( ViewportModel model ) {
        command.setViewportModel(model);
    }

    @Override
    public Map getMap() {
        return command.getMap();
    }

    @Override
    public void setMap(IMap map) {
        command.setMap(map);
    }

}