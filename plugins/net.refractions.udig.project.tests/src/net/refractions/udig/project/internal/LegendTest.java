/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.tests.support.MapTests;

import org.junit.Before;
import org.junit.Test;

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
        map=MapTests.createDefaultMap("FTName", 4, true, new Dimension(500,500)); //$NON-NLS-1$
        
        Folder folder = ProjectFactory.eINSTANCE.createFolder();
        folder.setName("Folder");
        folder.setShown(true);
        map.getLegend().add(folder);
        
        Layer layer = map.getLayersInternal().get(0);
        LayerLegendItem layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 0");
        layerItem.setLayer( layer );
        folder.getItems().add( layerItem );

        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 1");
        layerItem.setLayer( layer);
        folder.getItems().add( layerItem );
        
        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 2");
        layerItem.setLayer( layer );
        map.getLegend().add( layerItem );
        
        layer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(layer);
        layerItem = ProjectFactory.eINSTANCE.createLayerLegendItem();
        layerItem.setName("Layer 3");
        layerItem.setLayer( layer );
        map.getLegend().add( layerItem );
    }
    @Test
    public void testLegendBaseline() throws Exception {
        assertTrue( "Folder", map.getLegend().get(0) instanceof Folder );
        
        Folder folder = (Folder) map.getLegend().get(0);
        
        assertSame( "Reference Resource 0", map.getLayersInternal().get(0), ((LayerLegendItem)folder.getItems().get(0)).getLayer() );
        assertSame( "Reference Resource 1", map.getLayersInternal().get(1), ((LayerLegendItem)folder.getItems().get(1)).getLayer() );
        
        assertSame( "Reference Resource 2", map.getLayersInternal().get(2), ((LayerLegendItem)map.getLegend().get(1)).getLayer() );
        assertSame( "Reference Resource 3", map.getLayersInternal().get(3), ((LayerLegendItem)map.getLegend().get(2)).getLayer() );
    }
    @Test
    public void prototypeLayerSync() throws Exception {
        Layer layer = map.getLayersInternal().get(2);
        map.getLayersInternal().remove( layer );
        
        // check what happens to legend item
        LayerLegendItem item = (LayerLegendItem) map.getLegend().get(1);
        Layer reference = item.getLayer();
        assertSame( layer, reference  ); // reference was not cleared
        
    }
    
}
