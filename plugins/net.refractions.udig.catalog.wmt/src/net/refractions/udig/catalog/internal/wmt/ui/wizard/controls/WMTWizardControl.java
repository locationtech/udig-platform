/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.ui.wizard.controls;

import net.refractions.udig.catalog.IService;

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
