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
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RenderManagerImplTest {

    @Mock
    private IMapDisplay mapDisplay;

    private RenderManager manager = RenderFactory.eINSTANCE.createRenderManager();

    @Test
    public void testGetRenderExecutorCaching() {
        RenderExecutor expectedRenderExecutor = manager.getRenderExecutor();
        RenderExecutor actualRenderExecutor = manager.getRenderExecutor();

        assertEquals(expectedRenderExecutor, actualRenderExecutor);
    }

    @Test
    public void testRendererCreatorAndContext() {
        RendererCreator rendererCreator = manager.getRendererCreator();
        assertNotNull(rendererCreator);
        RenderContext renderContext = rendererCreator.getContext();
        assertNotNull(renderContext);

        assertEquals(manager, renderContext.getRenderManagerInternal());
        assertEquals(manager, renderContext.getRenderManager());
    }

    @Test
    public void testIsRenderingEnabledWhenEnabledAndDisplaySizesGreaterZero() {
        when(mapDisplay.getWidth()).thenReturn(512);
        when(mapDisplay.getHeight()).thenReturn(512);
        manager.setMapDisplay(mapDisplay);

        manager.enableRendering();
        assertTrue(manager.isRenderingEnabled());
    }

    @Test
    public void testIsRenderingEnabledWhenEnabledAndDisplayNotSet() {
        manager.enableRendering();
        assertFalse(manager.isRenderingEnabled());
    }

    @Test
    public void testIsRenderingEnabledWhenEnabledAndDisplayWidthIsZero() {
        when(mapDisplay.getWidth()).thenReturn(0);
        manager.setMapDisplay(mapDisplay);

        manager.enableRendering();
        assertFalse(manager.isRenderingEnabled());
    }

    @Test
    public void testIsRenderingEnabledWhenEnabledAndDisplayHeightIsZero() {
        when(mapDisplay.getWidth()).thenReturn(512);
        when(mapDisplay.getHeight()).thenReturn(0);
        manager.setMapDisplay(mapDisplay);

        manager.enableRendering();
        assertFalse(manager.isRenderingEnabled());
    }

    @Test
    public void testIsRenderingEnabledWhenDisabled() {
        manager.disableRendering();
        assertFalse(manager.isRenderingEnabled());
    }

}
