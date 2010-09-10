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
