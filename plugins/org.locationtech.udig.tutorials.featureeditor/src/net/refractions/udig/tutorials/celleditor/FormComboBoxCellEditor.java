/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.celleditor;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

/**
 * A sample extension of a ComboBoxCellEditor that overrides its activation/deactivation methods to make
 * the editor usable in forms. The control's style is also specified to make it visually form-like.
 * 
 * @author Naz Chan
 * @since 1.3.1
 */
public class FormComboBoxCellEditor extends ComboBoxCellEditor {

    private IMessageManager msgManager;
    
    public FormComboBoxCellEditor(Composite parent, String[] items, IMessageManager msgManager) {
        super(parent, items, SWT.SHADOW_IN | SWT.BORDER | SWT.READ_ONLY);
        this.msgManager = msgManager;
        
        addListener(new FormCellEditorListener(this, msgManager));
    }
    
    @Override
    public void activate() {
        // Do nothing
    }

    @Override
    public void activate( ColumnViewerEditorActivationEvent activationEvent ) {
        // Do nothing
    }

    @Override
    public void deactivate() {
        // Do nothing
    }

    @Override
    protected void deactivate( ColumnViewerEditorDeactivationEvent event ) {
        // Do nothing
    }
    
}
