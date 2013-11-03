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

import org.locationtech.udig.printing.model.impl.LabelBoxPrinter;
import org.locationtech.udig.printing.ui.internal.Messages;

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
