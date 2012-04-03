/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tutorials.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;

/**
 * A sample implementation of an ICellEditorListener that integrates a form's IMessageManager to
 * display the cell editor's error messages from validation.
 * 
 * @author Naz Chan
 * @since 1.3.1
 */
public class FormCellEditorListener implements ICellEditorListener {

    private IMessageManager msgManager;
    private CellEditor cellEditor;

    public FormCellEditorListener( CellEditor cellEditor, IMessageManager msgManager ) {
        this.cellEditor = cellEditor;
        this.msgManager = msgManager;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellEditorListener#applyEditorValue()
     */
    @Override
    public void applyEditorValue() {

        final Control control = cellEditor.getControl();
        msgManager.removeMessages(control);

        final String errMsg = cellEditor.getErrorMessage();
        if (errMsg != null) {
            msgManager.addMessage(control, errMsg, null, IMessage.ERROR, control);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellEditorListener#cancelEditor()
     */
    @Override
    public void cancelEditor() {
        // Nothing
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellEditorListener#editorValueChanged(boolean, boolean)
     */
    @Override
    public void editorValueChanged( boolean oldValidState, boolean newValidState ) {
        // Nothing
    }

}
