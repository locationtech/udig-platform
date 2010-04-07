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
package net.refractions.udig.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * A Cell editor that tells the user a message.  For example:  I'm sorry this cell cannot be edited ( or some such ).  
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class WarningCellEditor extends CellEditor {

    private final String warning;
    private Object value;
    private Control label;

    public WarningCellEditor( Composite control, String warning ) {
        this.warning=warning;
        this.label=createControl(control);
    }

    @Override
    protected Control createControl( Composite parent ) {
        Label label=new Label(parent, SWT.FLAT);
        label.setText(warning);
        label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
        return label;
    }

    @Override
    public Control getControl() {
        return label;
    }
    
    @Override
    protected Object doGetValue() {
        return value;
    }

    @Override
    protected void doSetFocus() {
    }

    @Override
    protected void doSetValue( Object value ) {
        this.value=value;
    }
    
    @Override
    public void dispose() {
        if( !label.isDisposed() )
            label.dispose();
    }

    @Override
    public void deactivate() {
        label.setVisible(false);
    }
    
    @Override
    public void activate() {
        label.setVisible(true);
    }
}
