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
package net.refractions.udig.catalog.internal.wmt.ui.preferences;


import net.refractions.udig.catalog.internal.wmt.WMTPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class WMTPreferenceInitializer extends AbstractPreferenceInitializer {
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = WMTPlugin.getDefault().getPreferenceStore();

        store.setDefault(WMTPreferenceConstants.P_WMT_SCALEFACTOR, 
                WMTPreferenceConstants.P_WMT_SCALEFACTOR_DEFAULT);
        
        store.setDefault(WMTPreferenceConstants.P_WMT_TILELIMIT_WARNING, 
                WMTPreferenceConstants.P_WMT_TILELIMIT_WARNING_DEFAULT);
        
        store.setDefault(WMTPreferenceConstants.P_WMT_TILELIMIT_ERROR, 
                WMTPreferenceConstants.P_WMT_TILELIMIT_ERROR_DEFAULT);
    }
    
}
