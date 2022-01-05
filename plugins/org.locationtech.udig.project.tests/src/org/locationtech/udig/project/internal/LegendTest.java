/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.project.tests.support.MapTests;

/**
 * Test map handling of legend items.
 *
 * @author Jody Garnett
 * @since 1.3.2
 */
public class LegendTest {
    private Map map;

    @Before
    public void baseline() throws Exception {
        map = MapTests.createDefaultMap("FTName", 4, true, new Dimension(500, 500)); //$NON-NLS-1$

        Folder folder = ProjectFactory.eINSTANCE.createFolder();
        folder.setName("Folder"); //$NON-NLS-1$
        folder.setShown(true);
        map.getLegend().add(folder);

        Layer layer = map.getLayersInternal().get(0);
        LayerLegendItem layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 0"); //$NON-NLS-1$
        layerItem.setLayer(layer);
        folder.getItems().add(layerItem);

        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 1"); //$NON-NLS-1$
        layerItem.setLayer(layer);
        folder.getItems().add(layerItem);

        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 2"); //$NON-NLS-1$
        layerItem.setLayer(layer);
        map.getLegend().add(layerItem);

        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 3"); //$NON-NLS-1$
        layerItem.setLayer(layer);
        map.getLegend().add(layerItem);
    }

    @Ignore
    @Test
    public void testLegendBaseline() throws Exception {
        assertTrue("Folder", map.getLegend().get(0) instanceof Folder); //$NON-NLS-1$

        Folder folder = (Folder) map.getLegend().get(0);

        assertSame("Reference Resource 0", map.getLayersInternal().get(0), //$NON-NLS-1$
                ((LayerLegendItem) folder.getItems().get(0)).getLayer());
        assertSame("Reference Resource 1", map.getLayersInternal().get(1), //$NON-NLS-1$
                ((LayerLegendItem) folder.getItems().get(1)).getLayer());

        assertSame("Reference Resource 2", map.getLayersInternal().get(2), //$NON-NLS-1$
                ((LayerLegendItem) map.getLegend().get(1)).getLayer());
        assertSame("Reference Resource 3", map.getLayersInternal().get(3), //$NON-NLS-1$
                ((LayerLegendItem) map.getLegend().get(2)).getLayer());
    }

    @Ignore
    @Test
    public void prototypeLayerSync() throws Exception {
        Layer layer = map.getLayersInternal().get(2);
        map.getLayersInternal().remove(layer);

        // check what happens to legend item
        LayerLegendItem item = (LayerLegendItem) map.getLegend().get(1);
        Layer reference = item.getLayer();
        assertSame(layer, reference); // reference was not cleared

    }

}
