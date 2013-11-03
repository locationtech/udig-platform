/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.dialogs;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Duplicate class.
 * @deprecated Please use org.locationtech.udig.ui.ColorEditor
 */
public class ColorEditor extends org.locationtech.udig.ui.ColorEditor {
    /**
     * Construct <code>ColorEditor</code>.
     * 
     * @param parent
     */
    public ColorEditor( Composite parent ) {
        super( parent );
    }
    public void addSelectionListener( SelectionListener listener ) {
        getButton().addSelectionListener(listener);
    }
 
    public void removeSelectionListener( SelectionListener listener ) {
        getButton().removeSelectionListener(listener);
    }
}
