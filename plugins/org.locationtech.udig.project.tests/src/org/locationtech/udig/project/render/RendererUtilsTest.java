/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2020, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Test;

public class RendererUtilsTest {

    final IProgressMonitor progressMonitor = new NullProgressMonitor();

    @After
    public void shutdown() {
        System.clearProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY);
    }

    @Test
    public void advancedProjectSupportIsOnIfPropertyExsistOrIsTrue() {
        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, ""); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "True"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "TRUE"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());

        System.setProperty(RendererUtils.ADVANCED_PROJECTION_PROPERTY, "true"); // //$NON-NLS-1$
        assertTrue(RendererUtils.isAdvancedProjectionSupportEnabled());
    }

    @Test
    public void advancedProjectSupportIsDisabledIfPropertyIsFalseOrSomethingElseThanFalse() {
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
}
