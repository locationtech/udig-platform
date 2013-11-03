/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.ui.wizard.controls;

import org.locationtech.udig.catalog.IService;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Used inside the wizard to display informations about an TreeItem.
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public abstract class WMTWizardControl {
    protected Control control;
    
    protected abstract Control buildControl(Composite composite);
    
    public synchronized Control getControl(Composite composite) {
        if (control == null) {
            control = buildControl(composite);
        }
        
        return control;
    }
    
    public IService getService() {
        return null;
    }
}
