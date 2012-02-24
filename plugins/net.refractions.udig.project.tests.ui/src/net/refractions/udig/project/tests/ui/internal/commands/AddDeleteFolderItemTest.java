package net.refractions.udig.project.tests.ui.internal.commands;

import java.util.ArrayList;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.AddFolderItemCommand;
import net.refractions.udig.project.internal.commands.AddLayerItemCommand;
import net.refractions.udig.project.internal.commands.DeleteFolderItemCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

@SuppressWarnings("nls")
public class AddDeleteFolderItemTest extends AbstractProjectUITestCase {

    private Map map;

    private Folder folder0;
    private Folder folder1;
    private Folder folderNull = null;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        folder0 = ProjectFactory.eINSTANCE.createFolder();
        folder0.setName("f0");
        folder1 = ProjectFactory.eINSTANCE.createFolder();
        folder1.setName("f1");
    
        map = ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject(), "Map", new ArrayList<Layer>());
        ApplicationGIS.openMap(map, true);

    }
    
    public void testCommands() throws InterruptedException {
        
        int sleepDuration = 300;
        
        assertNotNull("Map should not be null.", map);
        
        assertNotNull("Folder0 should not be null.", folder0);
        assertNotNull("Folder1 should not be null.", folder1);
        assertNull("FolderNull should be null.", folderNull);
        
        /**
         * Test add folder methods 
         */
        
        //Test with null parameter
        map.sendCommandSync(new AddFolderItemCommand(folderNull));
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folderNull));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folderNull));
        
        //Test correct settings
        map.sendCommandSync(new AddFolderItemCommand(folder0));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folder0));

        /**
         * Test delete folder methods 
         */
        
        map.sendCommandSync(new AddFolderItemCommand(folder0));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        //Test with null parameter
        map.sendCommandSync(new DeleteFolderItemCommand(folderNull));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        //Test with non-existing folder
        map.sendCommandSync(new DeleteFolderItemCommand(folder1));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folder0));
        
        //Test correct settings
        map.sendCommandSync(new AddFolderItemCommand(folder0));
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.sendCommandSync(new DeleteFolderItemCommand(folder0));
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 1);
        assertTrue(map.getLegend().contains(folder0));
        map.undo();
        Thread.sleep(sleepDuration);
        assertEquals(map.getLegend().size(), 0);
        assertFalse(map.getLegend().contains(folder0));
        
    }
    
}
