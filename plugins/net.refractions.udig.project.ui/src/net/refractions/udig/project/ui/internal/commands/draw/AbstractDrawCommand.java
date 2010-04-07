/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.commands.draw;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Abstract super class of commands that simply draw on the acetate layer. The top-most layer. The
 * graphics object will be set just prior to the execution of the command and is used to execute
 * draw commands. Subclasses do not need to be concerned about resetting the graphics2D because the
 * RenderManager, which executes the command will handle the issue.
 * 
 * @author jeichar
 * @since 0.3
 * @deprecated
 */
public abstract class AbstractDrawCommand extends AbstractCommand implements IDrawCommand {

    /**
     * The graphics object will be set just prior to the execution of the command and is used to
     * execute draw commands.
     */
    protected ViewportGraphics graphics;
    private boolean valid = true;
    protected IMapDisplay display;

    /**
     * @see net.refractions.udig.project.internal.commands.draw.IDrawCommand#setGraphics(net.refractions.udig.project.render.ViewportGraphics,
     *      net.refractions.udig.project.render.MapDisplay)
     */
    public void setGraphics( ViewportGraphics graphics, IMapDisplay display ) {
        this.graphics = graphics;
        this.display = display;
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
}