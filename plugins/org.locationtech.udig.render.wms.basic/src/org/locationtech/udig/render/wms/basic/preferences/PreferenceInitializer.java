/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.render.wms.basic.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.render.wms.basic.WMSPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = WMSPlugin.getDefault()
        .getPreferenceStore();
        if( Platform.getOS().equals(Platform.OS_MACOSX) ){
	        store.setDefault(PreferenceConstants.P_IMAGE_TYPE_ORDER,
            "image/gif,image/tiff,image/bmp,image/jpeg,image/png8,image/png"); //$NON-NLS-1$
        }else{
	        store.setDefault(PreferenceConstants.P_IMAGE_TYPE_ORDER,
	                "image/png,image/png8,image/gif,image/tiff,image/bmp,image/jpeg"); //$NON-NLS-1$
        }
        store.setDefault(PreferenceConstants.P_USE_DEFAULT_ORDER, true);
    }
    
}
