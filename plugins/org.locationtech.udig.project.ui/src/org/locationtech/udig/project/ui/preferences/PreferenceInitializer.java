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
package org.locationtech.udig.project.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = ProjectUIPlugin.getDefault().getPreferenceStore();

        store.setDefault(PreferenceConstants.P_OPEN_MAPS_ON_STARTUP, true);
        store.setDefault(PreferenceConstants.MOUSE_SPEED,
                ProjectUIPlugin.DEFAULT_DOUBLECLICK_SPEED_MILLIS);
        store.setDefault(MapEditorWithPalette.ID, 0);
        store.setDefault("Test", 0);

        //added for finer control of certain map actions
        //node.put(PreferenceConstants.FEATURE_ATTRIBUTE_NAME, "name");
        store.setDefault(PreferenceConstants.FEATURE_SELECTION_SCALEFACTOR,PreferenceConstants.DEFAULT_FEATURE_SELECTION_SCALEFACTOR);
    }

}
