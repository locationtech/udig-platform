/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.view.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.locationtech.udig.omsbox.core.FieldData;
import org.locationtech.udig.omsbox.utils.OmsBoxConstants;

/**
 * Class representing a gui for combobox choice.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiComboField extends ModuleGuiElement {

    private String constraints;
    private final FieldData data;
    private Combo combo;

    public GuiComboField( FieldData data, String constraints ) {
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

        String[] guiHintsSplit = data.guiHints.split(";");
        String[] imtemsSplit = new String[]{" - "};
        for( String guiHint : guiHintsSplit ) {
            if (guiHint.startsWith(OmsBoxConstants.COMBO_UI_HINT)) {
                String items = guiHint.replaceFirst(OmsBoxConstants.COMBO_UI_HINT, "").replaceFirst(":", "").trim();
                imtemsSplit = items.split(",");
                break;
            }
        }

        combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        combo.setItems(imtemsSplit);

        if (data.fieldValue != null) {
            for( int i = 0; i < imtemsSplit.length; i++ ) {
                if (data.fieldValue.equals(imtemsSplit[i])) {
                    combo.select(i);
                    data.fieldValue = combo.getItem(i);
                    break;
                }
            }
        } else {
            combo.select(0);
            data.fieldValue = combo.getItem(0);
        }
        combo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                int selectionIndex = combo.getSelectionIndex();
                data.fieldValue = combo.getItem(selectionIndex);
            }
        });

        return combo;
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
