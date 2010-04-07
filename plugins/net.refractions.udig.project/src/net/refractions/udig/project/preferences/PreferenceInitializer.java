package net.refractions.udig.project.preferences;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.interceptor.ResourceCacheInterceptor;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    public static final String P_DEFAULT_FEATURE_EDITOR_VALUE="net.refractions.udig.tool.select.view"; //$NON-NLS-1$
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ProjectPlugin.getPlugin()
				.getPreferenceStore();
        store.setDefault(PreferenceConstants.P_REMOVE_LAYERS, true);
        store.setDefault(PreferenceConstants.P_WARN_IRREVERSIBLE_COMMAND, true);
        store.setDefault(PreferenceConstants.P_ANTI_ALIASING, true);
		store.setDefault(PreferenceConstants.P_DEFAULT_CRS, -1);
        store.setDefault(PreferenceConstants.P_TRANSPARENCY, true);
        store.setDefault(PreferenceConstants.P_TILING_RENDERER, false);
        store.setDefault(PreferenceConstants.P_HIGHLIGHT, PreferenceConstants.P_HIGHLIGHT_NONE);
        store.setDefault(PreferenceConstants.P_DEFAULT_PALETTE, "Dark2"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.P_STYLE_DEFAULT_PERPENDICULAR_OFFSET, "10"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.P_LAYER_RESOURCE_CACHING_STRATEGY, ResourceCacheInterceptor.ID);
        store.setDefault(PreferenceConstants.P_PROJECT_DELETE_FILES, true);
        store.setDefault(PreferenceConstants.P_SHOW_ANIMATIONS, true);
        store.setDefault(PreferenceConstants.P_MAX_UNDO, 10);
        store.setDefault(PreferenceConstants.P_DEFAULT_FEATURE_EDITOR, P_DEFAULT_FEATURE_EDITOR_VALUE );
        
        PreferenceConverter.setDefault(store, PreferenceConstants.P_BACKGROUND, new RGB(255,255,255));
        PreferenceConverter.setDefault(store,PreferenceConstants.P_SELECTION_COLOR, new RGB(255,255,0));
        PreferenceConverter.setDefault(store,PreferenceConstants.P_SELECTION2_COLOR, new RGB(0,0,0));
        
        
        
      store.setDefault(PreferenceConstants.P_FEATURE_EVENT_REFRESH_ALL, false);
      store.setDefault(PreferenceConstants.P_IGNORE_LABELS_OVERLAPPING, false);
      store.setDefault(PreferenceConstants.P_CHECK_DUPLICATE_LAYERS, false);
    }

}
