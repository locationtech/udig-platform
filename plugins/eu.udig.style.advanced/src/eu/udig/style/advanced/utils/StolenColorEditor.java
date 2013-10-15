/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.style.advanced.utils;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Duplicate class.
 * 
 * @deprecated Please use net.refractions.udig.ui.ColorEditor
 */
public class StolenColorEditor extends net.refractions.udig.ui.ColorEditor {
    /**
     * Construct <code>ColorEditor</code>.
     * 
     * @param parent
     */
    public StolenColorEditor( Composite parent ) {
        super( parent );
    }

    public StolenColorEditor(Composite parent, SelectionListener selectionListener) {
        super( parent );
        setListener( selectionListener );
    }
}