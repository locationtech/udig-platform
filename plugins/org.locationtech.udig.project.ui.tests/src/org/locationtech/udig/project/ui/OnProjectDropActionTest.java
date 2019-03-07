/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.internal.actions.OnProjectDropAction;
import org.locationtech.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void setUp() throws Exception {
        resource=MapTests.createGeoResource("OnProjectDropType", 3, true); //$NON-NLS-1$
        map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(10,10), null, false);
        layer=map.getLayersInternal().get(0);
        action=new OnProjectDropAction();
    }

    /**
     * Test method for {@link org.locationtech.udig.project.ui.internal.actions.OnProjectDropAction#accept()}.
     */
    @Test
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
     * Test method for {@link org.locationtech.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
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
     * Test method for {@link org.locationtech.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
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
     * Test method for {@link org.locationtech.udig.project.ui.internal.actions.OnProjectDropAction#perform(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
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
