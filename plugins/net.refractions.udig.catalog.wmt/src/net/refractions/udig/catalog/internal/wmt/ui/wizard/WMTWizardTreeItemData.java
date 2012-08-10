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
