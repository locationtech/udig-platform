/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.preferences;

import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.vividsolutions.jts.operation.buffer.BufferOp;

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
        
        store.setDefault(PreferenceConstants.P_BUFFER_SEGMENTS, 8);
        store.setDefault(PreferenceConstants.P_BUFFER_DEFULT_SIZE, 20);
        store.setDefault(PreferenceConstants.P_BUFFER_MAX_SIZE, 200);
        store.setDefault(PreferenceConstants.P_BUFFER_CAP_TYPE, BufferOp.CAP_ROUND);
    }

}
