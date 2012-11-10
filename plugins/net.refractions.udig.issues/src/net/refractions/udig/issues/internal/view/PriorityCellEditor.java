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
