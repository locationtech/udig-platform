package net.refractions.udig.project.tests.ui.dnd;

import java.util.ArrayList;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.LegendDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.NullProgressMonitor;

@SuppressWarnings("nls")
public class LegendDropActionTest extends AbstractProjectUITestCase {

    private Map map;
    private Map currentMap;

    private Folder folder0;
    private Folder folder1;
    
    private Layer layer0;
    private Layer layer1;
    private Layer layer2;
    private Layer layer3;
    private Layer layer4;
    private Layer layer5;
    
    private LegendDropAction action;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.action = new LegendDropAction();
        
        folder0 = ProjectFactory.eINSTANCE.createFolder();
        folder0.setName("f0");
        folder1 = ProjectFactory.eINSTANCE.createFolder();
        folder1.setName("f1");
    
        layer0 = ProjectFactory.eINSTANCE.createLayer();
        layer0.setName("l0");
        layer1 = ProjectFactory.eINSTANCE.createLayer();
        layer1.setName("l1");
        layer2 = ProjectFactory.eINSTANCE.createLayer();
        layer2.setName("l2");
        layer3 = ProjectFactory.eINSTANCE.createLayer();
        layer3.setName("l3");
        layer4 = ProjectFactory.eINSTANCE.createLayer();
        layer4.setName("l4");
        layer5 = ProjectFactory.eINSTANCE.createLayer();
        layer5.setName("l5");
        
        map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject(), "Map", new ArrayList<Layer>());
        ApplicationGIS.openMap(map);
        
        currentMap = (Map) ApplicationGIS.getActiveMap();
        currentMap.getLegend().clear();
        
    }
    
    public void testAccept() { 
        
        currentMap.getLegend().add(folder0);
        currentMap.getLegend().add(folder1);
        currentMap.getLegend().add(layer0);
        currentMap.getLegend().add(layer1);
        currentMap.getLegend().add(layer2);
        currentMap.getLegend().add(layer3);
        
        // Drop BEFORE - destination is null
        action.init(null, null, ViewerDropLocation.BEFORE, null, folder0);
        assertFalse(action.accept());
        // Drop BEFORE - source is null
        action.init(null, null, ViewerDropLocation.BEFORE, folder0, null);
        assertFalse(action.accept());
        // Drop BEFORE - source and destination are null
        action.init(null, null, ViewerDropLocation.BEFORE, null, null);
        assertFalse(action.accept());
        
        // Drop BEFORE - itself
        action.init(null, null, ViewerDropLocation.BEFORE, folder0, folder0);
        assertFalse(action.accept());
        // Drop ON - itself
        action.init(null, null, ViewerDropLocation.ON, folder0, folder0);
        assertFalse(action.accept());
        // Drop AFTER - itself
        action.init(null, null, ViewerDropLocation.AFTER, folder0, folder0);
        assertFalse(action.accept());
           
        // Drop ON - multiple sources/mixed source
        action.init(null, null, ViewerDropLocation.ON, folder0, new Object[] {layer0, layer1});
        assertFalse(action.accept());
        action.init(null, null, ViewerDropLocation.ON, folder0, new Object[] {layer0, folder1});
        assertFalse(action.accept());

        // Drop ON - folder source
        action.init(null, null, ViewerDropLocation.ON, null, folder0);
        assertFalse(action.accept());
        action.init(null, null, ViewerDropLocation.ON, layer0, folder0);
        assertFalse(action.accept());
        action.init(null, null, ViewerDropLocation.ON, folder1, folder0);
        assertFalse(action.accept());
        
        // Drop ON - layer source and layer destination
        action.init(null, null, ViewerDropLocation.ON, layer0, layer1);
        assertFalse(action.accept());
        
        // Drop ON - layer source and layer-parent destination
        folder0.getItems().add(layer0);
        action.init(null, null, ViewerDropLocation.ON, folder0, layer0);
        assertFalse(action.accept());
        
        // Drop BEFORE/AFTER - folder source and layer-inside folder destination
        action.init(null, null, ViewerDropLocation.BEFORE, layer0, folder1);
        assertFalse(action.accept());
        action.init(null, null, ViewerDropLocation.AFTER, layer0, folder1);
        assertFalse(action.accept());
        
        
    }
    
    public void testMoveSameLevel() {
        
        int index = 0;
        
        currentMap.getLegend().add(folder0);
        currentMap.getLegend().add(folder1);
        currentMap.getLegend().add(layer0);
        currentMap.getLegend().add(layer1);
        currentMap.getLegend().add(layer2);
        currentMap.getLegend().add(layer3);
        

        //Init index of source item
        index = currentMap.getLegend().indexOf(layer1);
        
        // Drop BEFORE - move to first item
        action.init(null, null, ViewerDropLocation.BEFORE, folder0, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(0, currentMap.getLegend().indexOf(layer1));
        currentMap.getLegend().remove(layer1);
        currentMap.getLegend().add(index, layer1);
        
        //Drop AFTER - move to first item
        action.init(null, null, ViewerDropLocation.AFTER, folder0, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(1, currentMap.getLegend().indexOf(layer1));
        currentMap.getLegend().remove(layer1);
        currentMap.getLegend().add(index, layer1);
        
        // Drop BEFORE - move to last item
        action.init(null, null, ViewerDropLocation.BEFORE, layer3, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(4, currentMap.getLegend().indexOf(layer1));
        currentMap.getLegend().remove(layer1);
        currentMap.getLegend().add(index, layer1);
        
        // Drop AFTER - move to last item
        action.init(null, null, ViewerDropLocation.AFTER, layer3, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(5, currentMap.getLegend().indexOf(layer1));
        currentMap.getLegend().remove(layer1);
        currentMap.getLegend().add(index, layer1);
        
        // Drop AFTER - move to previous item
        action.init(null, null, ViewerDropLocation.AFTER, layer0, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(index, currentMap.getLegend().indexOf(layer1));
        
        // Drop BEFORE - move to next item
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(index, currentMap.getLegend().indexOf(layer1));
        
        // Drop AFTER - same item
        action.init(null, null, ViewerDropLocation.AFTER, layer1, layer1);
        if (action.accept()) {
            action.perform(new NullProgressMonitor());    
        }
        assertEquals(index, currentMap.getLegend().indexOf(layer1));
        
        // Drop BEFORE - same item
        action.init(null, null, ViewerDropLocation.BEFORE, layer1, layer1);
        if (action.accept()) {
            action.perform(new NullProgressMonitor());    
        }
        assertEquals(index, currentMap.getLegend().indexOf(layer1));
        
        // Drop BEFORE - move to mid item (not adjacent)
        index = currentMap.getLegend().indexOf(layer0);
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertEquals(3, currentMap.getLegend().indexOf(layer0));
        assertEquals(4, currentMap.getLegend().indexOf(layer2));
        currentMap.getLegend().remove(layer0);
        currentMap.getLegend().add(index, layer0);
        
        // Drop AFTER - move to mid item (not adjacent)
        index = currentMap.getLegend().indexOf(layer0);
        action.init(null, null, ViewerDropLocation.AFTER, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertEquals(3, currentMap.getLegend().indexOf(layer2));
        assertEquals(4, currentMap.getLegend().indexOf(layer0));
        currentMap.getLegend().remove(layer0);
        currentMap.getLegend().add(index, layer0);
        
    }

    public void testMoveSameLevelInFolder() {
        
        int index = 0;
        
        currentMap.getLegend().add(folder0);
        
        folder0.getItems().add(layer4);
        folder0.getItems().add(layer5);
        folder0.getItems().add(layer0);
        folder0.getItems().add(layer1);
        folder0.getItems().add(layer2);
        folder0.getItems().add(layer3);
        

        //Init index of source item
        index = folder0.getItems().indexOf(layer1);
        
        // Drop BEFORE - move to first item
        action.init(null, null, ViewerDropLocation.BEFORE, layer4, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(0, folder0.getItems().indexOf(layer1));
        folder0.getItems().remove(layer1);
        folder0.getItems().add(index, layer1);
        
        //Drop AFTER - move to first item
        action.init(null, null, ViewerDropLocation.AFTER, layer4, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(1, folder0.getItems().indexOf(layer1));
        folder0.getItems().remove(layer1);
        folder0.getItems().add(index, layer1);
        
        // Drop BEFORE - move to last item
        action.init(null, null, ViewerDropLocation.BEFORE, layer3, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(4, folder0.getItems().indexOf(layer1));
        folder0.getItems().remove(layer1);
        folder0.getItems().add(index, layer1);
        
        // Drop AFTER - move to last item
        action.init(null, null, ViewerDropLocation.AFTER, layer3, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(5, folder0.getItems().indexOf(layer1));
        folder0.getItems().remove(layer1);
        folder0.getItems().add(index, layer1);
        
        // Drop AFTER - move to previous item
        action.init(null, null, ViewerDropLocation.AFTER, layer0, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(index, folder0.getItems().indexOf(layer1));
        
        // Drop BEFORE - move to next item
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer1);
        action.perform(new NullProgressMonitor());
        assertEquals(index, folder0.getItems().indexOf(layer1));
        
        // Drop AFTER - same item
        action.init(null, null, ViewerDropLocation.AFTER, layer1, layer1);
        if (action.accept()) {
            action.perform(new NullProgressMonitor());    
        }
        assertEquals(index, folder0.getItems().indexOf(layer1));
        
        // Drop BEFORE - same item
        action.init(null, null, ViewerDropLocation.BEFORE, layer1, layer1);
        if (action.accept()) {
            action.perform(new NullProgressMonitor());    
        }
        assertEquals(index, folder0.getItems().indexOf(layer1));
        
        // Drop BEFORE - move to mid item (not adjacent)
        index = folder0.getItems().indexOf(layer0);
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertEquals(3, folder0.getItems().indexOf(layer0));
        assertEquals(4, folder0.getItems().indexOf(layer2));
        folder0.getItems().remove(layer0);
        folder0.getItems().add(index, layer0);
        
        // Drop AFTER - move to mid item (not adjacent)
        index = folder0.getItems().indexOf(layer0);
        action.init(null, null, ViewerDropLocation.AFTER, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertEquals(3, folder0.getItems().indexOf(layer2));
        assertEquals(4, folder0.getItems().indexOf(layer0));
        folder0.getItems().remove(layer0);
        folder0.getItems().add(index, layer0);
        
    }
    
    public void testMoveInFolder() {

        int index = 0;
        int size = 0;
        
        currentMap.getLegend().add(folder0);
        currentMap.getLegend().add(folder1);
        currentMap.getLegend().add(layer0);
        currentMap.getLegend().add(layer1);
        currentMap.getLegend().add(layer2);
        currentMap.getLegend().add(layer3);

        // Init index of source item
        index = currentMap.getLegend().indexOf(layer1);
        
        // Drop IN - empty folder
        action.init(null, null, ViewerDropLocation.ON, folder0, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer0));
        assertTrue(folder0.getItems().contains(layer0));
        assertEquals(0, folder0.getItems().indexOf(layer0));
        
        // Drop IN - non-empty folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.ON, folder0, layer1);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer1));
        assertTrue(folder0.getItems().contains(layer1));
        assertEquals(0, folder0.getItems().indexOf(layer1));
        assertEquals(size + 1, folder0.getItems().size());
        
        // Drop IN - from inside folder to empty folder
        int size0 = folder0.getItems().size();
        int size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.ON, folder1, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertTrue(folder1.getItems().contains(layer0));
        assertEquals(size0 - 1, folder0.getItems().size());
        assertEquals(size1 + 1, folder1.getItems().size());
        
        // Drop IN - from inside folder to non-empty folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.ON, folder1, layer1);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer1));
        assertTrue(folder1.getItems().contains(layer1));
        assertEquals(size0 - 1, folder0.getItems().size());
        assertEquals(size1 + 1, folder1.getItems().size());
        
    }
 
    public void testMoveInFolderWithLocation() {
        
        int index = 0;
        int size = 0;
        
        currentMap.getLegend().add(folder0);
        folder0.getItems().add(layer0);
        folder0.getItems().add(layer1);
        folder0.getItems().add(layer2);
        currentMap.getLegend().add(layer3);
        
        //Init index of source item
        index = currentMap.getLegend().indexOf(layer3);
        
        // Drop BEFORE - move to first item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer0, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(0, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
        // Drop AFTER - move to first item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer0, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(1, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
        // Drop BEFORE - move to last item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(2, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
        // Drop AFTER - move to last item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer2, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(3, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
        // Drop BEFORE - move to mid item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer1, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(1, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
        // Drop AFTER - move to mid item inside folder
        size = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer1, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(currentMap.getLegend().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(2, folder0.getItems().indexOf(layer3));
        assertEquals(size + 1, folder0.getItems().size());
        folder0.getItems().remove(layer3);
        currentMap.getLegend().add(index, layer3);
        
    }
    
    public void testTransferFolderWithLocation() {

        currentMap.getLegend().add(folder0);
        folder0.getItems().add(layer0);
        folder0.getItems().add(layer1);
        folder0.getItems().add(layer2);
        currentMap.getLegend().add(folder1);
        folder1.getItems().add(layer3);

        // Drop BEFORE - move to first item inside another folder
        int size0 = folder0.getItems().size();
        int size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer0, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(0, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);

        // Drop AFTER - move to first item inside another folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer0, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(1, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);
        
        // Drop BEFORE - move to last item inside another folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(2, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);

        // Drop AFTER - move to last item inside another folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer2, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(3, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);

        // Drop BEFORE - move to mid item inside folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer1, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(1, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);

        // Drop AFTER - move to mid item inside folder
        size0 = folder0.getItems().size();
        size1 = folder1.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer1, layer3);
        action.perform(new NullProgressMonitor());
        assertFalse(folder1.getItems().contains(layer3));
        assertTrue(folder0.getItems().contains(layer3));
        assertEquals(2, folder0.getItems().indexOf(layer3));
        assertEquals(size0 + 1, folder0.getItems().size());
        assertEquals(size1 - 1, folder1.getItems().size());
        folder0.getItems().remove(layer3);
        folder1.getItems().add(layer3);
        
    }
    
    public void testMoveOutFolder() {
        
        int sizeTarget = 0;
        int sizeSource = 0;
        
        currentMap.getLegend().add(folder0);
        folder0.getItems().add(layer0);
        currentMap.getLegend().add(layer1);
        currentMap.getLegend().add(layer2);
        currentMap.getLegend().add(layer3);
        currentMap.getLegend().add(layer4);
        
        // Drop BEFORE - move to first item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, folder0, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(0, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
        // Drop AFTER - move to first item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, folder0, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(1, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
        // Drop BEFORE - move to mid item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(2, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
        // Drop AFTER - move to mid item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer2, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(3, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
        // Drop BEFORE - move to last item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.BEFORE, layer4, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(4, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
        // Drop AFTER - move to last item outside folder
        sizeTarget = currentMap.getLegend().size();
        sizeSource = folder0.getItems().size();
        action.init(null, null, ViewerDropLocation.AFTER, layer4, layer0);
        action.perform(new NullProgressMonitor());
        assertFalse(folder0.getItems().contains(layer0));
        assertEquals(sizeSource - 1, folder0.getItems().size());
        assertTrue(currentMap.getLegend().contains(layer0));
        assertEquals(5, currentMap.getLegend().indexOf(layer0));
        assertEquals(sizeTarget + 1, currentMap.getLegend().size());
        currentMap.getLegend().remove(layer0);
        folder0.getItems().add(layer0);
        
    }
    
}

