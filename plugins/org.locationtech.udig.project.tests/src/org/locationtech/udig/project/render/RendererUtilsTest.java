/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;

public class RendererUtilsTest {

    Boolean backUpPrefHideRenderJobs = null;
    Boolean backUpPrefAdvancedProjection = null;

    private ScopedPreferenceStore preferenceStore;

    @Before
    public void setup() {
        preferenceStore = ProjectPlugin.getPlugin().getPreferenceStore();
        if (preferenceStore.contains(PreferenceConstants.P_HIDE_RENDER_JOB)) {
            backUpPrefHideRenderJobs = preferenceStore.getBoolean(PreferenceConstants.P_HIDE_RENDER_JOB);
        } else {
            assertFalse(RendererUtils.isRendererJobSystemJob());
        }
        if (preferenceStore.contains(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT)) {
            backUpPrefAdvancedProjection = preferenceStore.getBoolean(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT);
        } else {
            assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());
        }
    }

    @After
    public void after() {
        if (backUpPrefHideRenderJobs != null) {
            preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, backUpPrefHideRenderJobs);
        }
        if (backUpPrefAdvancedProjection != null) {
            preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, backUpPrefAdvancedProjection);
        }
    }

    @Test
    public void rendererJob_IsSystemJob_HIDEPreferencesIsSet() {
        preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, true);
        assertTrue(RendererUtils.isRendererJobSystemJob());
    }

    @Test
    public void rendererJob_IsNotSystemJob_HIDEPreferencesIsSetFalse() {
        preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, false);
        assertFalse(RendererUtils.isRendererJobSystemJob());
    }

    @Test
    public void advancedProjectionSupportIsEnabledIfPreferenceIsTrue() {
        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, true);
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "True");
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "TRUE");
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "true");
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void advancedProjectionSupportIsDisabledIfPreferenceIsFalseOrSomethingElseThanTrue() {
        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, false);
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "");
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "False");
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "FALSE");
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "false");
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        preferenceStore.setValue(PreferenceConstants.P_ADVANCED_PROJECTION_SUPPORT, "whatever");
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void advancedProjectionSupportIsDisabledPerDefault() {
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());
    }
}
