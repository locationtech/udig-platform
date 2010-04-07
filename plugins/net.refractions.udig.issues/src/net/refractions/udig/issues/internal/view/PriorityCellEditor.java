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

import net.refractions.udig.core.enums.Priority;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Edit the resolution column in the Issuesview
 * 
 * @author jones
 * @since 1.0.0
 */
public class PriorityCellEditor extends CellEditor {

    private Priority priority;

    public PriorityCellEditor( Composite parent ) {
        super(parent);
    }

    @Override
    public void activate() {
        switch( priority ) {
        case TRIVIAL:
            priority = Priority.LOW;
            break;
        case LOW:
            priority = Priority.WARNING;
            break;
        case WARNING:
            priority = Priority.HIGH;
            break;
        case HIGH:
            priority = Priority.CRITICAL;
            break;
        case CRITICAL:
            priority = Priority.TRIVIAL;
            break;
        }

        fireApplyEditorValue();
    }

    @Override
    protected Control createControl( Composite parent ) {
        return null;
    }

    @Override
    protected Object doGetValue() {
        return priority;
    }

    @Override
    protected void doSetFocus() {
    }

    @Override
    protected void doSetValue( Object value ) {
        this.priority = (Priority) value;
    }

}
