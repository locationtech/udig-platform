package net.refractions.udig.catalog.internal;

import net.refractions.udig.catalog.CatalogPlugin;

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
		IPreferenceStore store = CatalogPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_TEMP_FT, false);
	}

}
