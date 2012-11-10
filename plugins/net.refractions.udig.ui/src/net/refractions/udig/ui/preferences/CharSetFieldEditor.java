/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui.preferences;

import java.nio.charset.Charset;

import net.refractions.udig.ui.CharsetSelectionDialog;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public final class CharSetFieldEditor extends FieldEditor {
    private Text text;
    public CharSetFieldEditor( String name, String labelText, Composite parent ) {
        super(name, labelText, parent);
    }
    @Override
    public int getNumberOfControls() {
        return 3;
    }
    @Override
    protected void doStore() {
        getPreferenceStore().setValue(getPreferenceName(), text.getText());
    }
    @Override
    protected void doLoadDefault() {
        text.setText(getPreferenceStore().getDefaultString(getPreferenceName()));
    }
    @Override
    protected void doLoad() {
        text.setText(getPreferenceStore().getString(getPreferenceName()));
    }
    @Override
    protected void doFillIntoGrid( final Composite parent, int numColumns ) {
        Label label = new Label(parent,SWT.NONE);
        label.setText(getLabelText());
        label.setLayoutData(new GridData());
        
        text = new Text(parent, SWT.BORDER);
        GridData layout = new GridData(GridData.FILL_HORIZONTAL);
        layout.horizontalSpan = numColumns-2;
        text.setLayoutData(layout);
        
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(new GridData());
        button.setText(Messages.CharSetFieldEditor_select);
        button.addListener(SWT.Selection, new Listener(){

            public void handleEvent( Event event ) {
                CharsetSelectionDialog dialog = new CharsetSelectionDialog(parent.getShell(), false, text.getText());
                dialog.open();
                text.setText(((Charset) dialog.getFirstResult()).name());
                store();
            }
            
        });
    }
    @Override
    protected void adjustForNumColumns( int numColumns ) {
    }
}