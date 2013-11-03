/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.celleditor;

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
