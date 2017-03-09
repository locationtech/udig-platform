package org.locationtech.udig.catalog.wms.preferences;

import org.locationtech.udig.catalog.internal.wms.WmsPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 *
 */
public class WmsPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = WmsPlugin.getDefault().getPreferenceStore();
       
        store.setDefault(WmsPreferencePage.WMS_RESPONSE_TIMEOUT, 30);

    }

}
