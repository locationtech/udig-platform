/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.render.wms.basic.preferences;

import net.refractions.udig.render.wms.basic.WMSPlugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
