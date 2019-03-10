/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ToolManagerTest {

    private Map map;
    private MapEditorPart editor;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("ToolmanagerTestType", 5, true, null); //$NON-NLS-1$
        ApplicationGIS.openMap(map, true);
        editor=ApplicationGISInternal.getActiveEditor();
    }
    
    @Ignore
    @Test
    public void testSetCurrentEditor() {
        IToolManager manager = ApplicationGIS.getToolManager();
        List<ActionToolCategory> categories = manager.getActiveToolCategories();
        ToolProxy tool=null;
        for( ActionToolCategory category : categories ) {
            for( ModalItem item : category ) {
                ToolProxy proxy=(ToolProxy) item;
                if( "org.locationtech.udig.project.tests.ui.actionTool1".equals(proxy.getId()) ){ //$NON-NLS-1$
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
