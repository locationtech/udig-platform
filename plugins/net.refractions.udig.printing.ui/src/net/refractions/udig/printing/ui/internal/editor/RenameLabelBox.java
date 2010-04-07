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
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.model.impl.LabelBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.figures.BoxFigure;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;
import net.refractions.udig.printing.ui.internal.editor.parts.LabelCellEditorLocator;
import net.refractions.udig.printing.ui.internal.editor.parts.LabelDirectEditManager;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

/**
 * Changes the label in a LabelBox
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class RenameLabelBox implements IBoxEditAction {

    private BoxPart owner;
    private String value;
    private boolean disposed = false;

    public void perform() {
        value = null;
        disposed = false;
        BoxFigure nodeFigure = (BoxFigure) owner.getFigure();
        LabelDirectEditManager manager = new LabelDirectEditManager(owner, TextCellEditor.class,
                new LabelCellEditorLocator(nodeFigure), nodeFigure){

            private boolean committing;

            @Override
            protected void initCellEditor() {
                super.initCellEditor();
                Text text = (Text) getCellEditor().getControl();
                text.setText(getText());
            }


            @Override
            protected void commit() {
                if (committing)
                    return;
                committing = true;
                try {
                    eraseFeedback();
                    value = (String) getCellEditor().getValue();
                } finally {
                    bringDown();
                    committing = false;
                }

            }
            @Override
            protected void bringDown() {
                super.bringDown();
                disposed = true;
            }
        };

        manager.show();
    }
    

    private String getText( ) {
        return getLabelBoxPrinter().getText();
    }

    public Command getCommand() {
        if (value == null)
            return null;
        return new Command(){

            private LabelBoxPrinter labelBox = getLabelBoxPrinter();
            private String oldValue = getLabelBoxPrinter().getText();
            private String newValue = value;

            @Override
            public boolean canExecute() {
                return isDone();
            }

            @Override
            public void execute() {
                labelBox.setText(newValue);
            }

            @Override
            public void redo() {
                labelBox.setText(newValue);
            }

            @Override
            public void undo() {
                labelBox.setText(oldValue);
            }
        };
    }
    
    private LabelBoxPrinter getLabelBoxPrinter(){
        return (LabelBoxPrinter) owner.getBoxPrinter();
    }

    public void init( BoxPart owner ) {
        this.owner = owner;
    }

    public boolean isDone() {
        return disposed;
    }

}
