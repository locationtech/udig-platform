package net.refractions.udig.catalog.shp.preferences;

import net.refractions.udig.catalog.internal.shp.ShpPlugin;

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
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ShpPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CREATE_INDEX, true);
        store.setDefault(PreferenceConstants.P_INDEX_TYPE,PreferenceConstants.QIX);
	}

}
