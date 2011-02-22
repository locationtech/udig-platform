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
