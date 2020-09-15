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
package org.locationtech.udig.project.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.interceptor.ResourceCacheInterceptor;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    public static final String P_DEFAULT_FEATURE_EDITOR_VALUE = "org.locationtech.udig.tool.select.view"; //$NON-NLS-1$

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        store.setDefault(PreferenceConstants.P_ZOOM_REQUIRED_CLOSENESS, 0.7);
        store.setDefault(PreferenceConstants.P_REMOVE_LAYERS, true);
        store.setDefault(PreferenceConstants.P_WARN_IRREVERSIBLE_COMMAND, true);
        store.setDefault(PreferenceConstants.P_ANTI_ALIASING, true);
        store.setDefault(PreferenceConstants.P_DEFAULT_CRS, -1);
        store.setDefault(PreferenceConstants.P_TRANSPARENCY, true);
        store.setDefault(PreferenceConstants.P_HIGHLIGHT, PreferenceConstants.P_HIGHLIGHT_NONE);
        store.setDefault(PreferenceConstants.P_DEFAULT_PALETTE, "Dark2"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.P_STYLE_DEFAULT_PERPENDICULAR_OFFSET, "10"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.P_LAYER_RESOURCE_CACHING_STRATEGY,
                ResourceCacheInterceptor.ID);
        store.setDefault(PreferenceConstants.P_PROJECT_DELETE_FILES, true);
        store.setDefault(PreferenceConstants.P_SHOW_ANIMATIONS, true);
        store.setDefault(PreferenceConstants.P_MAX_UNDO, 10);
        store.setDefault(PreferenceConstants.P_DEFAULT_FEATURE_EDITOR,
                P_DEFAULT_FEATURE_EDITOR_VALUE);

        PreferenceConverter.setDefault(store, PreferenceConstants.P_BACKGROUND,
                new RGB(255, 255, 255));
        PreferenceConverter.setDefault(store, PreferenceConstants.P_SELECTION_COLOR,
                new RGB(255, 255, 0));
        PreferenceConverter.setDefault(store, PreferenceConstants.P_SELECTION2_COLOR,
                new RGB(0, 0, 0));

        store.setDefault(PreferenceConstants.P_FEATURE_EVENT_REFRESH_ALL, false);
        store.setDefault(PreferenceConstants.P_IGNORE_LABELS_OVERLAPPING, false);
        store.setDefault(PreferenceConstants.P_CHECK_DUPLICATE_LAYERS, false);
        store.setDefault(PreferenceConstants.P_HIDE_RENDER_JOB, false);
        store.setDefault(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, false);
        store.setDefault(PreferenceConstants.P_DISABLE_CRS_SELECTION, false);
    }

}
