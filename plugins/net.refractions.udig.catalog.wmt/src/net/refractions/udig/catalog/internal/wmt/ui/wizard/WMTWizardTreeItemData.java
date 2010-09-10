package net.refractions.udig.catalog.internal.wmt.ui.wizard;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.WMTWizardControl;

/**
 * Class that is used to store a IService/IGeoResource and
 * a WMTWizardControl together as a TreeItem-Data.
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTWizardTreeItemData {
    private IResolve resolve;
    private WMTWizardControl controlFactory;
    
    public WMTWizardTreeItemData(IResolve resolve, WMTWizardControl controlFactory) {
        this.resolve = resolve;
        this.controlFactory = controlFactory;
    }
    
    public WMTWizardControl getControlFactory() {
        return controlFactory;
    }
    
    public IService getService() {
        if (resolve == null) {
            return controlFactory.getService();
        } else if (resolve instanceof IService) {
            return (IService) resolve;
        } else {
            return null;
        }
    }
    
    public IGeoResource getGeoResource() {
        if (resolve instanceof IGeoResource) {
            return (IGeoResource) resolve;
        } else {
            return null;
        }        
    }
}
