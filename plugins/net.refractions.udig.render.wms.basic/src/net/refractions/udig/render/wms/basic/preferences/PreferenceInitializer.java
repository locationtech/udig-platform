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
	                "image/png8,image/png,image/gif,image/tiff,image/bmp,image/jpeg"); //$NON-NLS-1$
        }
        store.setDefault(PreferenceConstants.P_USE_DEFAULT_ORDER, true);
    }
    
}
