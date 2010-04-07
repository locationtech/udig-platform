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
