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
package net.refractions.udig.project.tests.ui.internal.commands;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.map.LayerMoveBackCommand;
import net.refractions.udig.project.command.map.LayerMoveFrontCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.ApplicationGIS;

/**
 * Test class for LayerMoveBackCommand and LayerMoveFrontCommand 
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
@SuppressWarnings("nls")
public class LayerMoveBackFrontCommandTest extends AbstractProjectUITestCase {

    private Map map;

    private Layer layer0;
    private Layer layer1;
    private Layer layer2;
    private Layer layer3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final List<Layer> layersList = new ArrayList<Layer>();
        layer0 = ProjectFactory.eINSTANCE.createLayer();
        layer0.setName("0");
        layer0.setZorder(0);
        layersList.add(layer0);
        layer1 = ProjectFactory.eINSTANCE.createLayer();
        layer1.setName("1");
        layer1.setZorder(1);
        layersList.add(layer1);
        layer2 = ProjectFactory.eINSTANCE.createLayer();
        layer2.setName("2");
        layer2.setZorder(2);
        layersList.add(layer2);
        layer3 = ProjectFactory.eINSTANCE.createLayer();
        layer3.setName("3");
        layer3.setZorder(3);
        layersList.add(layer3);

        map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject(), "Map", layersList);
        ApplicationGIS.openMap(map, true);

    }

    public void testCommand() throws Exception {

        int sleepDuration = 300;

        int origIndex0 = map.getLayersInternal().indexOf(layer0);
        int origIndex1 = map.getLayersInternal().indexOf(layer1);
        int origIndex2 = map.getLayersInternal().indexOf(layer2);
        int origIndex3 = map.getLayersInternal().indexOf(layer3);

        // Back -- [0,1,2,3] -- Front
        int expectedFront = 3;
        int expectedBack = 0;

        // Send front to the back and undo
        map.sendCommandSync(new LayerMoveBackCommand(map, layer3));
        assertEquals(map.getLayersInternal().get(expectedBack), layer3);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex3), layer3);

        // Send back to the front and undo
        map.sendCommandSync(new LayerMoveFrontCommand(map, layer0));
        assertEquals(map.getLayersInternal().get(expectedFront), layer0);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex0), layer0);

        // Send 2 frontmost to the back and undo
        final List<ILayer> layersList = new ArrayList<ILayer>();
        layersList.add(layer3);
        layersList.add(layer2);
        map.sendCommandSync(new LayerMoveBackCommand(map, layersList));
        assertEquals(map.getLayersInternal().get(0), layer2);
        assertEquals(map.getLayersInternal().get(1), layer3);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex2), layer2);
        assertEquals(map.getLayersInternal().get(origIndex3), layer3);

        // Send 2 backmost to the front and undo
        layersList.clear();
        layersList.add(layer1);
        layersList.add(layer0);
        map.sendCommandSync(new LayerMoveFrontCommand(map, layersList));
        assertEquals(map.getLayersInternal().get(2), layer0);
        assertEquals(map.getLayersInternal().get(3), layer1);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex0), layer0);
        assertEquals(map.getLayersInternal().get(origIndex1), layer1);

        // Send back and next-next item to the front and undo
        layersList.clear();
        layersList.add(layer2);
        layersList.add(layer0);
        map.sendCommandSync(new LayerMoveFrontCommand(map, layersList));
        assertEquals(map.getLayersInternal().get(2), layer0);
        assertEquals(map.getLayersInternal().get(3), layer2);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex0), layer0);
        assertEquals(map.getLayersInternal().get(origIndex2), layer2);

        // Send front and next-next item to the back and undo
        layersList.clear();
        layersList.add(layer3);
        layersList.add(layer1);
        map.sendCommandSync(new LayerMoveBackCommand(map, layersList));
        assertEquals(map.getLayersInternal().get(0), layer1);
        assertEquals(map.getLayersInternal().get(1), layer3);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLayersInternal().get(origIndex1), layer1);
        assertEquals(map.getLayersInternal().get(origIndex3), layer3);

    }

}
