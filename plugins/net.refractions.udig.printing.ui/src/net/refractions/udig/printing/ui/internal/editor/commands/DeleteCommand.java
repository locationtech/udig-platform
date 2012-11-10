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
package net.refractions.udig.printing.ui.internal.editor.commands;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.Messages;

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
 * DeleteCommand x = new DeleteCommand( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class DeleteCommand extends Command {
    private Page parent;
    private Box child;
    
    public DeleteCommand() {
        super(Messages.DeleteCommand_delete); 
    }
    
    public void execute() {
        parent.getBoxes().remove(child);
    }
    public void undo() {
        parent.getBoxes().add(child);
    }
    
    public void setChild( Box child ) {
        this.child = child;
    }
    public void setParent( Page parent ) {
        this.parent = parent;
    }
}
