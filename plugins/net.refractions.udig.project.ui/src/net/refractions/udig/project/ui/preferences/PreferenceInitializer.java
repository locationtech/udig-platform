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
package net.refractions.udig.project.ui.preferences;

import net.refractions.udig.project.ui.internal.MapEditorWithPalette;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ProjectUIPlugin.getDefault().getPreferenceStore();

        store.setDefault(PreferenceConstants.P_OPEN_MAPS_ON_STARTUP, true);
        store.setDefault(PreferenceConstants.MOUSE_SPEED,
                ProjectUIPlugin.DEFAULT_DOUBLECLICK_SPEED_MILLIS);
        store.setDefault(MapEditorWithPalette.ID, 0);
        store.setDefault("Test", 0);
    }

}
