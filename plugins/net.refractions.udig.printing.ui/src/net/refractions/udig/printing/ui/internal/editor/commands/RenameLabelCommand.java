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

import net.refractions.udig.printing.model.impl.LabelBoxPrinter;
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
 * RenameNodeCommand x = new RenameNodeCommand( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class RenameLabelCommand extends Command{
    private LabelBoxPrinter labelBox;
    private String newName;
    private String oldName;
    
    public void setName(String name) {
        this.newName = name;
    }
    
    public void setNode(LabelBoxPrinter node) {
        this.labelBox = node;
    }
    
    public void execute() {
        oldName = this.labelBox.getText();
        this.labelBox.setText(newName);
    }
    
    public String getLabel() {
        return Messages.RenameLabelCommand_label; 
    }
    
    public void redo() {
        this.labelBox.setText(newName);
    }
    
    public void undo() {
        this.labelBox.setText(oldName);
    }

}
