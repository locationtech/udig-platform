package net.refractions.udig.project.internal.commands;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.palette.ColourScheme;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;

public class LayerColoursTest extends AbstractProjectTestCase {

    //TODO: ensure removed colours are reused (they currently are not)
    //TODO: use more unique identifier than layer ID for scheme mapping key
    
    public void testColourScheme() throws Exception {
         //add and remove some layers, and make sure the colours are proper
        Map map = MapTests.createDefaultMap("test", 2, true, new Dimension(10,10)); //$NON-NLS-1$
        IGeoResource resource1 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type1", 4), false); //$NON-NLS-1$
        IGeoResource resource2 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type2", 4), false); //$NON-NLS-1$
        IGeoResource resource3 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type3", 4), false); //$NON-NLS-1$
        IGeoResource resource4 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type4", 4), false); //$NON-NLS-1$
        IGeoResource resource5 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type5", 4), false); //$NON-NLS-1$
        IGeoResource resource6 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(
                "type6", 4), false); //$NON-NLS-1$
        assertEquals(1, map.getMapLayers().size());
        
        assertEquals(8, map.getColourScheme().getColourPalette().getMaxColors());
        
        List<IGeoResource> resources = new ArrayList<IGeoResource>();
        resources.add(resource1);
        
        assertTrue(coloursAreUnique(map.getColourScheme()));
        
        addLayer(resource1, map);
        assertEquals(2, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);
        
        removeLayer(map, 0);
        assertEquals(1, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource2, map);
        assertEquals(2, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource3, map);
        assertEquals(3, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource4, map);
        assertEquals(4, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource5, map);
        assertEquals(5, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        removeLayer(map, 2);
        assertEquals(4, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource6, map);
        assertEquals(5, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

        addLayer(resource3, map);
        assertEquals(6, map.getMapLayers().size());
        assertTrue(coloursAreUnique(map.getColourScheme()));
        showDefaultColours(map);

    }

    private boolean coloursAreUnique(ColourScheme scheme) {
        int sizeScheme = scheme.getSizeScheme();
        for (int i = 0; i < sizeScheme; i++) {
            Color colour1 = scheme.getColour(i);
            for (int j = i + 1; j < sizeScheme; j++) {
                Color colour2 = scheme.getColour(j);
                if (colour1.equals(colour2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void addLayer(IGeoResource resource, IMap map) throws Exception {
        AddLayersCommand addCommand = new AddLayersCommand(getResource(resource));
        addCommand.setMap(map);
        addCommand.run(new NullProgressMonitor());
    }
    
    private void removeLayer(IMap map, int index) throws Exception {
        DeleteLayerCommand deleteCommand = new DeleteLayerCommand((Layer) map.getMapLayers().get(index));
        deleteCommand.setMap(map);
        deleteCommand.run(new NullProgressMonitor());
    }
    
    private List<IGeoResource> getResource(IGeoResource resource) {
        List<IGeoResource> resources = new ArrayList<IGeoResource>();
        resources.add(resource);
        return resources;
    }
    
    private void showDefaultColours(Map map) {
//        System.out.println("COLOURS:");
//        List<ILayer> mapLayers = map.getMapLayers();
//        for (ILayer layer : mapLayers) {
//            Color colour = ((LayerImpl) layer).getDefaultColor();
//            ;
//            System.out.println(colour + " " + layer.getID());
//        }
    }
}
