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
package org.locationtech.udig.tool.select;

import org.locationtech.udig.tool.select.internal.SelectionToolPreferencePage;

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
