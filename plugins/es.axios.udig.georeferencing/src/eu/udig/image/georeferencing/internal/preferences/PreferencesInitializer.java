/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
