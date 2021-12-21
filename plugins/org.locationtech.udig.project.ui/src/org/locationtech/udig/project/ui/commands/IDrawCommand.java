/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.commands;

import java.awt.Rectangle;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Draw commands do not change the model, rather they simply draw on the top-most layer. They are
 * primarily used to provide user feedback. For example the rubber-banding box drawn by the victim
 * selection tool is drawn using a DrawCommand. The rubber-banding box is a decoration for the user,
 * other commands employed by the tool change the model. One class of commands that modifies the
 * models are the EditCommands. They edit the features themselves. Because draw commands are
 * executed every paint, instead of once like other types of commands, each DrawCommand has a valid
 * flag which is set false when the lifetime of the command is up. Once the valid flag is set to
 * false the command will be removed from the command stack the next update. The command must be
 * resent if the command is to be drawn again.
 *
 * @author jeichar
 * @since 0.3
 * @see MapCommand
 */
public interface IDrawCommand extends MapCommand {
    /**
     * Sets the graphics2D that this command will draw on. Will be called before execution of
     * command
     *
     * @param graphics the graphics2D that this command will draw on
     * @param display The display area that will be draw on.
     * @see ViewportGraphics
     * @see IMapDisplay
     */
    public void setGraphics(ViewportGraphics graphics, IMapDisplay display);

    /**
     * Returns the rectangle where this command is valid. Ie The area that this command draws to.
     * Null may be returned if the valid area is unknown or is the entire screen.
     *
     * @return Returns the rectangle where this command is valid. Ie The area that this command
     *         draws to. Null may be returned if the valid area is unknown or is the entire screen.
     */
    public Rectangle getValidArea();

    /**
     * Sets whether the current command should be drawn. If not then it will be removed from the
     * draw stack. Default value is true;
     *
     * @param valid true if the command should be drawn.
     */
    public void setValid(boolean valid);

    /**
     * Returns whether the current command should be drawn. If not then it will be removed from the
     * draw stack. Default value is true;
     *
     * @return true if the command should be drawn.
     */
    public boolean isValid();

    /**
     * Disposes of any resources that need to be disposed of.
     *
     * Called by the framework when draw command is removed from the viewport pane.
     */
    public void dispose();
}
