/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.project.ui.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Abstract super class of commands that simply draw on the acetate layer. The top-most layer. The
 * graphics object will be set just prior to the execution of the command and is used to execute
 * draw commands. Subclasses do not need to be concerned about resetting the graphics2D because the
 * RenderManager, which executes the command will handle the issue.
 * 
 * @author jeichar
 * @since 0.3
 */
public abstract class AbstractDrawCommand extends AbstractCommand implements IDrawCommand {

    /**
     * The graphics object will be set just prior to the execution of the command and is used to
     * execute draw commands.
     */
    protected ViewportGraphics graphics;
    private boolean valid = true;
    protected ViewportPane display;

    /**
     * @see net.refractions.udig.project.internal.commands.draw.IDrawCommand#setGraphics(net.refractions.udig.project.render.ViewportGraphics,
     *      net.refractions.udig.project.render.MapDisplay)
     */
    public void setGraphics( ViewportGraphics graphics, IMapDisplay display ) {
        this.graphics = graphics;
        this.display = (ViewportPane) display;
    }

    /**
     * @see net.refractions.udig.project.ui.commands.IDrawCommand#setValid(boolean)
     */
    public void setValid( boolean valid ) {
        this.valid = valid;
    }

    /**
     * @see net.refractions.udig.project.ui.commands.IDrawCommand#isValid()
     */
    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return Messages.AbstractDrawCommand_name; 
    }
    
    public void dispose(){
        valid = false;
        // do nothing by default
    }
}