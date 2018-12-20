/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2018, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.render.internal.gridcoverage.basic;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.geotools.styling.StyleBuilder;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.CreateMapCommand;
import org.locationtech.udig.project.internal.render.impl.RenderContextImpl;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.ui.internal.RenderManagerDynamic;
import org.locationtech.udig.style.sld.SLDPlugin;

/**
 * Test BasicGridCoverageRenderer
 * 
 * @author HendrikPeilke
 */
public class BasicGridCoverageRendererTest {

    private RenderContextImpl context;

    private void createContextAndRenderManager(Map map) {
        map.setRenderManagerInternal(new RenderManagerDynamic());
        context = new RenderContextImpl();
        context.setGeoResourceInternal(map.getLayersInternal().get(0).getGeoResources().get(0));
        context.setMapInternal(map);
        context.setRenderManagerInternal(map.getRenderManagerInternal());
        context.setLayerInternal(map.getLayersInternal().get(0));
    }

    /**
     * Test method for looking at layer scales before rendering in
     * {@link org.locationtech.udig.render.internal.gridcoverage.basic.GridCoverageReaderRenderer#.render(java.awt.Graphics2D
     * graphics, org.eclipse.core.runtime.IProgressMonitor monitor)}.
     */
    @Test
    public void testLookAtLayerScalesBeforeRendering() throws Exception {
        // create fake geoResource
        BasicGridCoverage2DTestReader reader = new BasicGridCoverage2DTestReader();
        IGeoResource resource = CatalogTests.createResource(null, reader);
        List<IGeoResource> list = new ArrayList<IGeoResource>();
        list.add(resource);

        // create map and get layer
        Project project = ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject();
        CreateMapCommand command = new CreateMapCommand("BasicGridCoverageRendererTest", list, //$NON-NLS-1$
                project);
        project.sendSync(command);
        Map map = (Map) command.getCreatedMap();
        Layer layer = map.getLayersInternal().get(0);

        // build the layer style with min and max scale 10, 100
        StyleBuilder sb = new StyleBuilder();
        layer.getStyleBlackboard().put(SLDPlugin.ID, sb.createStyle("BasicGridCoverageRendererTest", //$NON-NLS-1$
                sb.createRasterSymbolizer(), 10, 100));

        // setup rendering and viewport
        createContextAndRenderManager(map);
        context.getRenderManagerInternal()
                .setMapDisplay(new BasicGridCoverageTestViewportPane(1000, 1000));

        // test a scale within min and max
        map.getViewportModelInternal().setScale(50);
        // need a valid scale denominator
        assertTrue("could not setup the viewport model", //$NON-NLS-1$
                map.getViewportModelInternal().getScaleDenominator() != -1);
        // test if the reader is read
        triggerRendering(layer);
        assertTrue("did not read the GridCoverage, although inside min and max scales", //$NON-NLS-1$
                reader.getReads() == 1);

        // test a scale outside min and max
        map.getViewportModelInternal().setScale(200);
        // test if the reader is not read (still 1 read operation)
        triggerRendering(layer);
        assertTrue("read the GridCoverage, although out of min and max scales", //$NON-NLS-1$
                reader.getReads() == 1);

    }

    private void triggerRendering(Layer layer) {
        // wait for a clean state before
        while (context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                .getState() != IRenderer.DONE
                && context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                        .getState() != IRenderer.NEVER)
            while (Display.getDefault().readAndDispatch())
                ;
        // refresh layer and wait for rendering to be done
        layer.refresh(null);
        while (context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                .getState() != IRenderer.RENDERING
                && context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                        .getState() != IRenderer.STARTING
                && context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                        .getState() != IRenderer.RENDER_REQUEST)
            while (Display.getDefault().readAndDispatch())
                ;
        while (context.getRenderManagerInternal().getRenderExecutor().getRenderer()
                .getState() != IRenderer.DONE)
            while (Display.getDefault().readAndDispatch())
                ;
    }

}
