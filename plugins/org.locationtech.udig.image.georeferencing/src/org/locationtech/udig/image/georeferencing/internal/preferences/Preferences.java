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
package org.locationtech.udig.image.georeferencing.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import org.locationtech.udig.image.georeferencing.Activator;

/**
 * Single access for Image Georeferencing preferences.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
public class Preferences {

	/**
	 * Provides access to the plugin's preference store
	 * 
	 * @return this plugin's preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		Activator activator = Activator.getDefault();
		IPreferenceStore store = activator.getPreferenceStore();
		return store;
	}

	private static String getString(final String preferenceName) {
		IPreferenceStore store = Preferences.getPreferenceStore();
		String value = store.getString(preferenceName);
		return value;
	}

	private static void setString(final String preferenceName, final String value) {
		IPreferenceStore store = Preferences.getPreferenceStore();
		store.setValue(preferenceName, value);
	}

	public static String getImagePath() {
		return getString(PreferenceConstans.IMAGE_PATH);
	}

	public static void setImagePath(String value) {
		setString(PreferenceConstans.IMAGE_PATH, value);
	}

	public static String getSaveLoadPath() {
		return getString(PreferenceConstans.SAVE_LOAD_PATH);
	}

	public static void setSaveLoadPath(String value) {
		setString(PreferenceConstans.SAVE_LOAD_PATH, value);
	}

	public static String getOutputFilePath() {
		return getString(PreferenceConstans.OUTPUT_FILE_PATH);
	}

	public static void setOutputFilePath(String value) {
		setString(PreferenceConstans.OUTPUT_FILE_PATH, value);
	}

}
