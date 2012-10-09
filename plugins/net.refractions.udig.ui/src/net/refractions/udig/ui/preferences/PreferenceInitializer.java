/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.ui.preferences;

import java.nio.charset.Charset;

import net.refractions.udig.internal.ui.MapPerspective;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = UiPlugin.getDefault()
				.getPreferenceStore();
        store.setDefault(PreferenceConstants.P_ADVANCED_GRAPHICS, true);
        store.setDefault(PreferenceConstants.P_DEFAULT_PERSPECTIVE, MapPerspective.ID_PERSPECTIVE);
        store.setDefault(PreferenceConstants.P_SHOW_TIPS, true);
        store.setDefault(PreferenceConstants.P_DEFAULT_CHARSET, Charset.defaultCharset().name()); 
	}

}
