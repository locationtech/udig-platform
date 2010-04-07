/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.internal.actions.OnProjectDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class OnProjectDropActionTest extends AbstractProjectUITestCase{

    private IGeoResource resource;
    private Map map;
    private Layer layer;
    private OnProjectDropAction action;

    protected void setUp() throws Exception {
        super.setUp();
        resource=MapTests.createGeoResource("OnProjectDropType", 3, true); //$NON-NLS-1$
        map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(10,10), null, false);
        layer=map.getLayersInternal().get(0);
        action=new OnProjectDropAction();
    }

    /**
     * Test method for {@link net.refractions.udig.project.ui.internal.actions.OnProjectDropAction#accept()}.
     */
    public void testAccept() throws IOException {
        assertFalse(action.accept());
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), resource);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), map);
        assertFalse(action.accept());
        
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), resource.service(new NullProgressMonitor()));
        assertTrue(action.accept());
        
        // now test dropping collections
        List<Object> list=new ArrayList<Object>();
        
        list.add("hi"); //$NON-NLS-1$
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), list);
        assertFalse(action.accept());
        
        list.add(resource);
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), list);
        assertTrue(action.accept());
        
        list.clear();
        list.add(resource);
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), list);
        assertTrue(action.accept());
        
        list.clear();
        list.add(resource.service(new NullProgressMonitor()));
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), list);
        assertTrue(action.accept());
        
    }

    /**
     * Test method for {@link net.refractions.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    public void testPerformAddResource() {

        assertEquals(1, map.getProject().getElements().size());

        action.init(null, null, ViewerDropLocation.ON, map.getProject(), resource);
        action.perform(new NullProgressMonitor());
        
        assertEquals(2, map.getProject().getElements().size());
        IMap newMap = (IMap) map.getProject().getElements().get(1);
        
        assertEquals( 1, newMap.getMapLayers().size());
        assertNotSame( layer, newMap.getMapLayers().get(0));
    }
    /**
     * Test method for {@link net.refractions.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    public void testPerformAddIllegalObject() {

        assertEquals(1, map.getProject().getElements().size());

        action.init(null, null, ViewerDropLocation.ON, map.getProject(), "Object"); //$NON-NLS-1$
        try{
            action.perform(new NullProgressMonitor());
            fail();
        }catch (Exception e) {
            // good
        }
    }
    /**
     * Test method for {@link net.refractions.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    public void testPerformAddCollection() {

        assertEquals(1, map.getProject().getElements().size());


        List<Object> list=new ArrayList<Object>();
        list.add(resource);
        list.add(resource);
        
        action.init(null, null, ViewerDropLocation.ON, map.getProject(), list);
        action.perform(new NullProgressMonitor());
        
        assertEquals(2, map.getProject().getElements().size());
        IMap newMap = (IMap) map.getProject().getElements().get(1);
        
        assertEquals( 2, newMap.getMapLayers().size());
        assertNotSame( layer, newMap.getMapLayers().get(0));
        assertNotSame( layer, newMap.getMapLayers().get(1));
    }
    
}
