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
package org.locationtech.udig.project.ui.internal.commands.draw;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

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
     * @see org.locationtech.udig.project.internal.commands.draw.IDrawCommand#setGraphics(org.locationtech.udig.project.render.ViewportGraphics,
     *      org.locationtech.udig.project.render.MapDisplay)
     */
    public void setGraphics( ViewportGraphics graphics, IMapDisplay display ) {
        this.graphics = graphics;
        this.display = display;
    }

    /**
     * @see org.locationtech.udig.project.ui.commands.IDrawCommand#setValid(boolean)
     */
    public void setValid( boolean valid ) {
        this.valid = valid;
    }

    /**
     * @see org.locationtech.udig.project.ui.commands.IDrawCommand#isValid()
     */
    public boolean isValid() {
        return valid;
    }
}
