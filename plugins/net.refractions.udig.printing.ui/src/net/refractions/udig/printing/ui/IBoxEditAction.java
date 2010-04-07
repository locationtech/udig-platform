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
package net.refractions.udig.printing.ui;

import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

import org.eclipse.gef.commands.Command;


/**
 * Provides a UI for creating commands to edit a box's model 
 * and creates the command for modifying the model.
 * @author Jesse
 * @since 1.1.0
 */
public interface IBoxEditAction {
    /**
     * Initializes the box edit action.
    * @param parent the parent composite that can be used to create the user interface.
    */
    public void init(BoxPart owner);
    /**
     * Creates the UI for the user to make a change to the model.
     * <p><b>DOES NOT CHANGE THE MODEL!!!!</b></p>
     * Only the command should change the model.
     */
    public void perform();
    /**
     * Called by framework to determine if the command can be executed.  
     * 
     * This should always return true unless an operation continues to run in the background after
     * {@link #perform()}. In that case it should return false until the action is done.
     * 
     * Note this is how to do heavy processing in an action.  The command is ran in the
     * UI thread so don't do serious processing in a command
     * 
     */
    public boolean isDone();
    
    /**
     * Creates the command that will change the model.  
     *
     * @return  the command that will change the model.
     */
    public Command getCommand() ;
}
