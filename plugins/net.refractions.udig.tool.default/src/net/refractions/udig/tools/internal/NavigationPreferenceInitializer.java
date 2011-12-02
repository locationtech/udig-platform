package net.refractions.udig.tools.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

/**
 * Fill in the default value for our navigation tool preferences.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.3
 */
public class NavigationPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Preferences node = DefaultScope.INSTANCE.getNode(ToolsPlugin.ID);
        node.putBoolean(NavigationToolPreferencePage.SCALE,false);
        node.putBoolean(NavigationToolPreferencePage.TILED,false);
    }

}
