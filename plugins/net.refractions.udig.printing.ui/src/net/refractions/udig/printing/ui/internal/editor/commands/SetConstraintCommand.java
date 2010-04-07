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
package net.refractions.udig.printing.ui.internal.editor.commands;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.ui.internal.Messages;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * SetConstraintCommand x = new SetConstraintCommand( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class SetConstraintCommand extends Command {
    private Box box;
    private Point newPos;
    private Point oldPos;
    
    private Dimension newSize;
    private Dimension oldSize;
    
    public void setLocation(Point p) {
        this.newPos = p;
    }
    
    public void setNode(Box node) {
        this.box = node;
    }

    public void execute() {
        oldPos = this.box.getLocation();
        this.box.setLocation(newPos);
        
        oldSize = this.box.getSize();
        this.box.setSize(newSize);
    }
    
    public String getLabel() {
        return Messages.SetConstraintCommand_label; 
    }
    
    public void redo() {
        this.box.setLocation(newPos);
        this.box.setSize(newSize);
    }
    
    public void undo() {
        this.box.setLocation(oldPos);
        this.box.setSize(oldSize);
    }

    public void setSize( Dimension size ) {
        this.newSize = size;
    }
}
