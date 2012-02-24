package net.refractions.udig.project.tests.ui.internal.commands;

import java.util.ArrayList;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.AddFolderItemCommand;
import net.refractions.udig.project.internal.commands.AddLayerItemCommand;
import net.refractions.udig.project.internal.commands.DeleteLayerItemCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

@SuppressWarnings("nls")
public class AddDeleteLayerItemTest extends AbstractProjectUITestCase {

    private Map map;
    
    private Folder folder;
    
    private Layer layer0;
    private Layer layer1;
    private Layer layer2;
    private Layer layerNull = null;
    
    private IGeoResource geoLayer0;
    private IGeoResource geoLayer1;
    private IGeoResource geoLayer2;
    private IGeoResource geoLayerNull = null;
    
 
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        System.out.println("Setup.");
        
        folder = ProjectFactory.eINSTANCE.createFolder();
        folder.setName("f");
        
        layer0 = ProjectFactory.eINSTANCE.createLayer();
        layer0.setName("l0");
        layer1 = ProjectFactory.eINSTANCE.createLayer();
        layer1.setName("l1");
        layer2 = ProjectFactory.eINSTANCE.createLayer();
        layer2.setName("l2");
        
        map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject(), "Map", new ArrayList<Layer>());
        ApplicationGIS.openMap(map, true);

    }
    
    public void testAddLayerCommands() throws InterruptedException {
        
        int sleepDuration = 300;
        
        assertNotNull("Map should not be null.", map);
        
        assertNotNull("Layer0 should not be null.", layer0);
        assertNotNull("Layer1 should not be null.", layer1);
        assertNotNull("Layer2 should not be null.", layer2);
        assertNull("LayerNull should be null.", layerNull);
     
        int size = map.getLegend().size();
        
        /**
         * Testing null layer
         */
        
        // Test rainy day: AddLayerItemCommand(null)
        map.sendCommandSync(new AddLayerItemCommand(layerNull));
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layerNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layerNull));
        
        // Test rainy day: AddLayerItemCommand(null, <less than size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layerNull, size - 1));
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        
        // Test rainy day: AddLayerItemCommand(null, <equal to size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layerNull, size));
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        
        // Test rainy day: AddLayerItemCommand(null, <more than size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layerNull, size + 1));
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layerNull));
        
        /**
         * Testing empty map
         */
        
        // Test sunny day: AddLayerItemCommand(Layer)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
                
        // Test sunny day: AddLayerItemCommand(Layer, <less than 0>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, -1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test sunny day: AddLayerItemCommand(Layer, <equal to size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, size));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test sunny day: AddLayerItemCommand(Layer, <more than size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, size + 1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        /**
         * Testing non-empty map
         */
        
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer1));
        map.sendCommandSync(new AddLayerItemCommand(layer2));
        assertEquals(map.getLegend().size(), size + 2);
        assertTrue(map.getLegend().contains(layer2));
        
        // Test sunny day: AddLayerItemCommand(Layer)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
                
        // Test sunny day: AddLayerItemCommand(Layer, <less than 0>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, -1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test sunny day: AddLayerItemCommand(Layer, <less than size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, size - 1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size - 1);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test sunny day: AddLayerItemCommand(Layer, <equal to size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, 1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), 1);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test sunny day: AddLayerItemCommand(Layer, <more than size>)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0, size + 1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        assertEquals(map.getLegend().indexOf(layer0), size);
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
    }
    
    public void testDeleteLayerCommands() throws InterruptedException {
        
        int sleepDuration = 300;
        
        assertNotNull("Map should not be null.", map);
        
        assertNotNull("Layer0 should not be null.", layer0);
        assertNotNull("Layer1 should not be null.", layer1);
        assertNotNull("Layer2 should not be null.", layer2);
        assertNull("LayerNull should be null.", layerNull);
     
        assertNotNull("Folder should not be null.", folder);
        
        int size = map.getLegend().size();
        
        /**
         * Testing null layer
         */
        
        // Test rainy day: DeleteLayerItemCommand(null)
        map.sendCommandSync(new DeleteLayerItemCommand(layerNull));
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layerNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layerNull));
        
        /**
         * Testing non-existing layer
         */
        
        // Test rainy day: DeleteLayerItemCommand(Layer)
        map.sendCommandSync(new DeleteLayerItemCommand(layer0));
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layer0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(layer0));
        
        // Test rainy day: DeleteLayerItemCommand(Layer)
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer1));
        map.sendCommandSync(new DeleteLayerItemCommand(layer0));
        assertEquals(map.getLegend().size(), size + 1);
        assertFalse(map.getLegend().contains(layer0));
        assertTrue(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size + 1);
        assertFalse(map.getLegend().contains(layer0));
        assertTrue(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer1));
        
        /**
         * Testing existing layer outside folder
         */
        
        // Test sunny day: DeleteLayerItemCommand(Layer) -- 1:1
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer1));
        map.sendCommandSync(new DeleteLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer1));
        
        // Test sunny day: DeleteLayerItemCommand(Layer) -- Multiple Layers
        size = map.getLegend().size();
        map.sendCommandSync(new AddLayerItemCommand(layer0));
        assertEquals(map.getLegend().size(), size + 1);
        assertTrue(map.getLegend().contains(layer0));
        map.sendCommandSync(new AddLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), size + 2);
        assertTrue(map.getLegend().contains(layer1));
        map.sendCommandSync(new AddLayerItemCommand(layer2));
        assertEquals(map.getLegend().size(), size + 3);
        assertTrue(map.getLegend().contains(layer2));
        
        int beforeActionSize = map.getLegend().size();
        map.sendCommandSync(new DeleteLayerItemCommand(layer1));
        assertEquals(map.getLegend().size(), beforeActionSize - 1);
        assertFalse(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), beforeActionSize);
        assertTrue(map.getLegend().contains(layer1));
        
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size + 2);
        assertFalse(map.getLegend().contains(layer2));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size + 1);
        assertFalse(map.getLegend().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), size);
        assertFalse(map.getLegend().contains(layer0));
        
        /**
         * Testing existing layer inside folder
         */

        // Test sunny day: DeleteLayerItemCommand(Layer)
        map.sendCommandSync(new AddFolderItemCommand(folder));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder));
        
        // Add one layer inside folder
        size = folder.getItems().size();
        folder.getItems().add(layer0);
        assertEquals(folder.getItems().size(), size + 1);
        assertTrue(folder.getItems().contains(layer0));
        
        // Send delete command
        map.sendCommandSync(new DeleteLayerItemCommand(layer0));
        assertEquals(folder.getItems().size(), size);
        assertFalse(folder.getItems().contains(layer0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(folder.getItems().size(), size + 1);
        assertTrue(folder.getItems().contains(layer0));
        
        //Add two more layers inside folder
        folder.getItems().add(layer1);
        assertEquals(folder.getItems().size(), size + 2);
        assertTrue(folder.getItems().contains(layer1));
        folder.getItems().add(layer2);
        assertEquals(folder.getItems().size(), size + 3);
        assertTrue(folder.getItems().contains(layer2));
        
        //Send delete command
        map.sendCommandSync(new DeleteLayerItemCommand(layer1));
        assertEquals(folder.getItems().size(), size + 2);
        assertFalse(folder.getItems().contains(layer1));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(folder.getItems().size(), size + 3);
        assertTrue(folder.getItems().contains(layer1));
        
        //Delete added layers inside folder
        folder.getItems().remove(layer2);
        assertEquals(folder.getItems().size(), size + 2);
        assertFalse(folder.getItems().contains(layer2));
        folder.getItems().remove(layer1);
        assertEquals(folder.getItems().size(), size + 1);
        assertFalse(folder.getItems().contains(layer1));
        folder.getItems().remove(layer0);
        assertEquals(folder.getItems().size(), size);
        assertFalse(folder.getItems().contains(layer0));
        
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folder));
        
    }
    
}
