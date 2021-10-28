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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.locationtech.udig.omsbox.core.FieldData;

/**
 * Class representing an swt label gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GuiLabel extends ModuleGuiElement {
    private final String constraints;
    private FieldData data;

    public int WRAP = 25;
    private org.eclipse.swt.widgets.Label swtlabel;
    private boolean isBold;

    public GuiLabel( FieldData data, String constraints, boolean isBold ) {
        this.data = data;
        this.constraints = constraints;
        this.isBold = isBold;
    }

    @Override
    public Control makeGui( final Composite parent ) {
        swtlabel = new org.eclipse.swt.widgets.Label(parent, SWT.WRAP | SWT.NONE);
        swtlabel.setLayoutData(constraints);

        if (isBold) {
            FontData[] fD = swtlabel.getFont().getFontData();
            fD[0].setStyle(SWT.BOLD);
            swtlabel.setFont(new Font(parent.getDisplay(), fD[0]));
        }
        
        String label = setLabel();
        swtlabel.setText(label);

        // parent.addControlListener(new ControlListener(){
        // public void controlResized( ControlEvent e ) {
        // setLabel();
        // }
        //
        // @Override
        // public void controlMoved( ControlEvent e ) {
        //
        // }
        // });

        return swtlabel;
    }

    private String setLabel() {
        String label = data.fieldDescription;
        int length = data.fieldDescription.length();

        if (length > WRAP) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while( i < length ) {
                int startIndex = i;
                i = i + WRAP;
                // find first space
                while( i < length && data.fieldDescription.charAt(i) != ' ' ) {
                    i = i + 1;
                }

                int endIndex = i;

                String sub = null;
                if (endIndex > length) {
                    sub = data.fieldDescription.substring(startIndex).trim();
                } else {
                    sub = data.fieldDescription.substring(startIndex, endIndex).trim();
                }
                sb.append(sub).append("\n");
            }
            label = sb.toString();
        }
        return label;
    }

    public FieldData getFieldData() {
        return data;
    }

    public boolean hasData() {
        return false;
    }

    @Override
    public String validateContent() {
        return null;
    }
}
