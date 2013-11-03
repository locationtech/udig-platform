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
package org.locationtech.udig.printing.ui.internal.editor.commands;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.ui.internal.Messages;

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
