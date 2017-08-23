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
package org.locationtech.udig.tool.select.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.locationtech.udig.tool.select.SelectPlugin;

public class SelectionToolPreferenceInitializer extends AbstractPreferenceInitializer {

    public SelectionToolPreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = SelectPlugin.getDefault().getPreferenceStore();
        store.setDefault(SelectionToolPreferencePage.NAVIGATE_SELECTION, true);

        store.setDefault(PreferenceConstants.FEATURE_SELECTION_SCALEFACTOR,
                PreferenceConstants.DEFAULT_FEATURE_SELECTION_SCALEFACTOR);
    }

}