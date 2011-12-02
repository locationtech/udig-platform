package net.refractions.udig.tool.select;

import net.refractions.udig.tool.select.internal.SelectionToolPreferencePage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

public class SelectionToolPreferenceInitializer extends AbstractPreferenceInitializer {

    public SelectionToolPreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences node = DefaultScope.INSTANCE.getNode(SelectPlugin.ID);        
        node.putBoolean(SelectionToolPreferencePage.NAVIGATE_SELECTION,true);
    }

}
