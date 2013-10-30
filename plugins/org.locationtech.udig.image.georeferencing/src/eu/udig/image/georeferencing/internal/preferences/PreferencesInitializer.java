/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.image.georeferencing.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Initializer for the <code>org.eclipse.core.runtime.preferences</code>
 * extension point that loads the system default values for this plug in
 * preferences.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Preferences.getPreferenceStore();
		store.setDefault(PreferenceConstans.IMAGE_PATH,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstans.OUTPUT_FILE_PATH,""); //$NON-NLS-1$
		store.setDefault(PreferenceConstans.SAVE_LOAD_PATH,""); //$NON-NLS-1$
	}

}
