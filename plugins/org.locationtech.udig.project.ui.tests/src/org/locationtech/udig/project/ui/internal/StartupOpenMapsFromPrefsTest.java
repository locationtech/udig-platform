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
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StartupOpenMapsFromPrefsTest {

    @Mock
    IPreferenceStore prefStore;

    @Test
    public void openMapCountZeroOnEmptyPrefStore() {
        assertEquals(0, StartupOpenMaps.getNumberOfOpenMaps(prefStore, false));
    }

    @Test
    public void openMapNumOfOpenMapsWithReset() {
        assertEquals(0, StartupOpenMaps.getNumberOfOpenMaps(prefStore, true));
        verify(prefStore, atLeastOnce()).setValue(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID, 0);
    }

    @Test
    public void mapPathIsNullOnEmptyPrefStore() {
        assertNull(StartupOpenMaps.getMapEditorForIndex(prefStore, 99, false));
        verify(prefStore, Mockito.atLeastOnce()).getString(Mockito.anyString());
        verifyNoMoreInteractions(prefStore);
    }

    @Test
    public void resetMapPathForGivenIndexOnRequest() {
        String mapIdForPrefStore = PreferenceConstants.P_OPEN_MAPS_PREFIX_ID + ":" + 99;
        assertNull(StartupOpenMaps.getMapEditorForIndex(prefStore, 99, true));

        verify(prefStore, Mockito.atLeastOnce()).getString(Mockito.anyString());
        verify(prefStore, atLeastOnce()).setValue(mapIdForPrefStore, "");
        verifyNoMoreInteractions(prefStore);
    }

    @Test
    public void persistMapInfoIfAMapIsInStoreAlready() {
        IPreferenceStore prefStore = new PreferenceStore();
        URI expectedMapId1 = URI.createURI("file://C:\\test\\project.udig\\Map1.umap");
        URI expectedMapId2 = URI.createURI("file://C:\\test\\project.udig\\Map2.umap");

        assertEquals(0, prefStore.getInt(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID));
        StartupOpenMaps.persistInfoForOpenMap(prefStore, expectedMapId1);

        // one map is stored, return quantity 1
        assertEquals(1, prefStore.getInt(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID));

        assertEquals(expectedMapId1.toString(),
                prefStore.getString(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID + ":" + 0));
        StartupOpenMaps.persistInfoForOpenMap(prefStore, expectedMapId2);

        assertEquals(2, prefStore.getInt(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID));
        assertEquals(expectedMapId2.toString(),
                prefStore.getString(PreferenceConstants.P_OPEN_MAPS_PREFIX_ID + ":" + 1));
    }
}
