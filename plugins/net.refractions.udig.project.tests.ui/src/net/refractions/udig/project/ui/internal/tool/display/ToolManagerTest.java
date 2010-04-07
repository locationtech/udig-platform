package net.refractions.udig.project.ui.internal.tool.display;

import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.MapEditor;

public class ToolManagerTest extends TestCase {

    private Map map;
    private MapEditor editor;

    protected void setUp() throws Exception {
        super.setUp();
        map=MapTests.createDefaultMap("ToolmanagerTestType", 5, true, null); //$NON-NLS-1$
        ApplicationGIS.openMap(map, true);
        editor=ApplicationGISInternal.getActiveEditor();
    }

    public void testSetCurrentEditor() {
        ToolManager manager = (ToolManager) ApplicationGIS.getToolManager();
        List<ActionToolCategory> categories = manager.getActiveToolCategories();
        ToolProxy tool=null;
        for( ActionToolCategory category : categories ) {
            for( ModalItem item : category ) {
                ToolProxy proxy=(ToolProxy) item;
                if( "net.refractions.udig.project.tests.ui.actionTool1".equals(proxy.getId()) ){ //$NON-NLS-1$
                    tool = proxy;
                    break;
                }
                    
            }
        }
        assertTrue( tool.isEnabled() );
        assertEquals(1, TestProperty.listeners.size());
        
        TestProperty.returnVal=false;
        TestProperty.listeners.get(0).notifyChange(TestProperty.lastObj);
        
        assertFalse( tool.isEnabled() );
        
        manager.setCurrentEditor(null);
        
        TestProperty.returnVal=true;
        assertEquals(0, TestProperty.listeners.size());
        
        assertFalse( tool.isEnabled() );
        
        manager.setCurrentEditor(editor);
        
        TestProperty.returnVal=true;
        TestProperty.listeners.get(0).notifyChange(TestProperty.lastObj);
        
        assertTrue( tool.isEnabled() );
        
        
    }

}
