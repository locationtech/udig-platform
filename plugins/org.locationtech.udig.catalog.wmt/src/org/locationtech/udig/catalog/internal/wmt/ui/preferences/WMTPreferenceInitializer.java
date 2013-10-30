/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
