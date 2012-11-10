/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
