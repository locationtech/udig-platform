/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.commands;

import java.awt.Rectangle;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Draw commands do not change the model, rather they simply draw on the top-most layer. They are
 * primarily used to provide user feedback. For example the rubber-banding box drawn by the victim
 * selection tool is drawn using a DrawCommand. The rubber-banding box is a decoration for the user,
 * other commands employed byt the tool change the model. One class of commands that modifies the
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
    public void setGraphics( ViewportGraphics graphics, IMapDisplay display );

    /**
     * Returns the rectangle where this command is valid.  Ie The area that this command draws to.
     * Null may be returned if the valid area is unknown or is the entire screen.
     *
     * @return Returns the rectangle where this command is valid.  Ie The area that this command draws to.
     * Null may be returned if the valid area is unknown or is the entire screen.
     */
    public Rectangle getValidArea( );
    
    /**
     * Sets whether the current command should be drawn. If not then it will be removed from the
     * draw stack. Default value is true;
     * 
     * @param valid true if the command should be drawn.
     */
    public void setValid( boolean valid );

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