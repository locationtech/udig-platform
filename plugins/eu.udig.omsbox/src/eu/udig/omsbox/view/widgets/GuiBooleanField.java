/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
