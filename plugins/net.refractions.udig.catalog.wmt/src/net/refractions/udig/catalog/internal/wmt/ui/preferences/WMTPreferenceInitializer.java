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
