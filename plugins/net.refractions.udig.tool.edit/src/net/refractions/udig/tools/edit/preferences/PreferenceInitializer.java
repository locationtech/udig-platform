/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.preferences;

import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Initializes Editing preferences
 * @author Jesse
 * @since 1.1.0
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {

        IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();
        PreferenceConverter.setDefault(store, PreferenceConstants.P_SNAP_CIRCLE_COLOR, new RGB(0,
                0, 0));
        store.setDefault(PreferenceConstants.P_SNAP_RADIUS, 30);
        store.setDefault(PreferenceConstants.P_VERTEX_SIZE, 4);
        store.setDefault(PreferenceConstants.P_FILL_POLYGONS, true);
        store.setDefault(PreferenceConstants.P_FILL_VERTICES, true);
        store.setDefault(PreferenceConstants.P_HIDE_SELECTED_FEATURES, true);
        store.setDefault(PreferenceConstants.P_SNAP_BEHAVIOUR, SnapBehaviour.OFF.toString());
        store.setDefault(PreferenceConstants.P_ADVANCED_ACTIVE, false);
        store.setDefault(PreferenceConstants.P_SELECT_POST_ACCEPT, true);
    }

}
