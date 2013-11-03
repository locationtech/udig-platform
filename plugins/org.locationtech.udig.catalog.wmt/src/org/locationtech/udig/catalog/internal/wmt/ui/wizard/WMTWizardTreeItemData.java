/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.ui.wizard;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wmt.ui.wizard.controls.WMTWizardControl;

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
