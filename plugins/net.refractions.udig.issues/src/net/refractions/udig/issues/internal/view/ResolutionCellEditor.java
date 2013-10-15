/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.internal.view;

import net.refractions.udig.core.enums.Resolution;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Edit the resolution column in the Issuesview
 * 
 * @author jones
 * @since 1.0.0
 */
public class ResolutionCellEditor extends CellEditor {

    private Resolution resolution;
    private Resolution[] values;

    public ResolutionCellEditor( Composite parent ) {
        super(parent);
        values = Resolution.values();
    }

    @Override
    public void activate() {
        int current = resolution.ordinal();
        current++;
        if (current == values.length) {
            current = 0;
        }
        resolution = values[current];

        fireApplyEditorValue();
    }

    @Override
    protected Control createControl( Composite parent ) {
        return null;
    }

    @Override
    protected Object doGetValue() {
        return resolution;
    }

    @Override
    protected void doSetFocus() {
    }

    @Override
    protected void doSetValue( Object value ) {
        this.resolution = (Resolution) value;
    }

}