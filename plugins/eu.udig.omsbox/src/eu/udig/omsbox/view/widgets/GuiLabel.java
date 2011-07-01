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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.udig.omsbox.core.FieldData;

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

    public GuiLabel( FieldData data, String constraints ) {
        this.data = data;
        this.constraints = constraints;
    }

    @Override
    public Control makeGui( final Composite parent ) {
        swtlabel = new org.eclipse.swt.widgets.Label(parent, SWT.WRAP | SWT.NONE);
        swtlabel.setLayoutData(constraints);

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
