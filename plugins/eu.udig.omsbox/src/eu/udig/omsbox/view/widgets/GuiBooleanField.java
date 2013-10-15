/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.view.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.udig.omsbox.core.FieldData;

/**
 * Class representing a gui for boolean choice.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiBooleanField extends ModuleGuiElement {

    private String constraints;
    private final FieldData data;
    private Button checkButton;

    public GuiBooleanField( FieldData data, String constraints ) {
        this.data = data;
        this.constraints = constraints;

    }

    @Override
    public Control makeGui( Composite parent ) {

        parent = new Composite(parent, SWT.NONE);
        parent.setLayoutData(constraints);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        checkButton = new Button(parent, SWT.CHECK);
        checkButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        checkButton.setText("");

        boolean checked = Boolean.parseBoolean(data.fieldValue);
        checkButton.setSelection(checked);
        checkButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                if (checkButton.getSelection()) {
                    data.fieldValue = String.valueOf(true);
                } else {
                    data.fieldValue = String.valueOf(false);
                }
            }
        });

        return checkButton;
    }

    public FieldData getFieldData() {
        return data;
    }

    public boolean hasData() {
        return true;
    }

    @Override
    public String validateContent() {
        return null;
    }

}
