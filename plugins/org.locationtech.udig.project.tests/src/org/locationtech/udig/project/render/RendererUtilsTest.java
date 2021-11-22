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

    final IProgressMonitor progressMonitor = new NullProgressMonitor();

    Boolean backUpPrefHideJobs = null;

    private ScopedPreferenceStore preferenceStore;

    @Before
    public void setup() {
        preferenceStore = ProjectPlugin.getPlugin().getPreferenceStore();
        if (preferenceStore.contains(PreferenceConstants.P_HIDE_RENDER_JOB)) {
            backUpPrefHideJobs = preferenceStore.getBoolean(PreferenceConstants.P_HIDE_RENDER_JOB);
        } else {
            assertFalse(RendererUtils.isRendererJobSystemJob());
        }
    }

    @After
    public void after() {
        if (backUpPrefHideJobs != null) {
            preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, backUpPrefHideJobs);
        }
        System.clearProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY);
    }

    @Test
    public void advancedProjectSupportIsOnIfPropertyIsTrue() {
        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "True"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "TRUE"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "true"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void advancedProjectSupportIsDisabledIfPropertyIsFalseOrSomethingElseThanFalse() {
        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, ""); // //$NON-NLS-1$
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "False"); // //$NON-NLS-1$
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "FALSE"); // //$NON-NLS-1$
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "false"); // //$NON-NLS-1$
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "whatever"); // //$NON-NLS-1$
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void advancedProjectSupportIsDisabledIfPropertyIsNotPresent() {
        System.clearProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY);
        assertFalse(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void rendererJob_IsSystemJob_HIDEPreferencesIsSet() throws Exception {
        preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, true);
        assertTrue(RendererUtils.isRendererJobSystemJob());
    }

    @Test
    public void rendererJob_IsNotSystemJob_HIDEPreferencesIsSetFalse() throws Exception {
        preferenceStore.setValue(PreferenceConstants.P_HIDE_RENDER_JOB, false);
        assertFalse(RendererUtils.isRendererJobSystemJob());
    }
}
