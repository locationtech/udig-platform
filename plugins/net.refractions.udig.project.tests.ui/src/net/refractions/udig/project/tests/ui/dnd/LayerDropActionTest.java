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
package net.refractions.udig.project.tests.ui.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.LayerDropAction;
import net.refractions.udig.ui.ViewerDropLocation;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the LayerDropAction
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LayerDropActionTest extends AbstractProjectUITestCase {

    private LayerDropAction action;
    private Map map;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("DropActionTestFeatures", 2, true, null); //$NON-NLS-1$
        IGeoResource[] resources = new IGeoResource[]{
                MapTests.createGeoResource("DropActionTestFeatures2", 3, true), //$NON-NLS-1$
                MapTests.createGeoResource("DropActionTestFeatures2", 4, true), //$NON-NLS-1$
                MapTests.createGeoResource("DropActionTestFeatures2", 5, true), //$NON-NLS-1$
                MapTests.createGeoResource("DropActionTestFeatures2", 6, true) //$NON-NLS-1$
                
        };
        ApplicationGIS.addLayersToMap(map, Arrays.asList(resources),0,null, true);
        action = new LayerDropAction();
    }

    @Test
    public void testAccept() throws Exception {
        //      layer dropped on another layer in same map.
        action.init(null, null, ViewerDropLocation.ON, map.getLayersInternal().get(0), map.getLayersInternal().get(1));
        assertTrue(action.accept());
        //      layer dropped before another layer in same map.
        action.init(null, null, ViewerDropLocation.BEFORE, map.getLayersInternal().get(0), map.getLayersInternal().get(1));
        assertTrue(action.accept());
        //      layer dropped after another layer in same map.
        action.init(null, null, ViewerDropLocation.AFTER, map.getLayersInternal().get(0), map.getLayersInternal().get(1));
        assertTrue(action.accept());
        
        // layer dropped on itself
        action.init(null, null, ViewerDropLocation.ON, map.getLayersInternal().get(0), map.getLayersInternal().get(0));
        assertFalse(action.accept());
        // layer dropped before itself
        action.init(null, null, ViewerDropLocation.BEFORE, map.getLayersInternal().get(0), map.getLayersInternal().get(0));
        assertFalse(action.accept());
        //      layer dropped after itself
        action.init(null, null, ViewerDropLocation.AFTER, map.getLayersInternal().get(0), map.getLayersInternal().get(0));
        assertFalse(action.accept());
        //      layer dropped not on any layer (target ends up being itself)
        action.init(null, null, ViewerDropLocation.NONE, map.getLayersInternal().get(0), map.getLayersInternal().get(0));
        assertTrue(action.accept());
        
        //      layer dropped on a layer in another map.
        action.init(null, null, ViewerDropLocation.AFTER, ProjectFactory.eINSTANCE.createLayer(), map.getLayersInternal().get(0));
        assertFalse(action.accept());
        
    }

//  if dropped on previous or next layer it should ALWAY swap
    @Test
    public void testDropOnNextLayer() throws Exception {
        Layer data = map.getLayersInternal().get(0);
        Layer destination = map.getLayersInternal().get(1);
        action.init(null, null, ViewerDropLocation.ON, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 1, data.getZorder());
        assertEquals( 0, destination.getZorder());

        data = map.getLayersInternal().get(0);
        destination = map.getLayersInternal().get(1);
        action.init(null, null, ViewerDropLocation.BEFORE, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 1, data.getZorder());
        assertEquals( 0, destination.getZorder());

        data = map.getLayersInternal().get(0);
        destination = map.getLayersInternal().get(1);
        action.init(null, null, ViewerDropLocation.AFTER, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 1, data.getZorder());
        assertEquals( 0, destination.getZorder());
    }
    
//  if dropped on previous or next layer it should ALWAY swap
    @Test
    public void testDropOnPreviousLayer() throws Exception {
        Layer data = map.getLayersInternal().get(1);
        Layer destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.ON, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 1, destination.getZorder());

        data = map.getLayersInternal().get(1);
        destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.BEFORE, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 1, destination.getZorder());

        data = map.getLayersInternal().get(1);
        destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.AFTER, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 1, destination.getZorder());
    }
    
    @Test
    public void testDropBeforeOtherLayer() throws Exception {
        Layer data = map.getLayersInternal().get(0);
        Layer destination = map.getLayersInternal().get(2);
        action.init(null, null, ViewerDropLocation.BEFORE, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 2, data.getZorder());
        assertEquals( 1, destination.getZorder());
        
        data = map.getLayersInternal().get(2);
        destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.BEFORE, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 1, data.getZorder());
        assertEquals( 0, destination.getZorder());
        
        
    }

    @Test
    public void testDropOnOtherLayer() throws Exception {
        Layer data = map.getLayersInternal().get(0);
        Layer destination = map.getLayersInternal().get(2);
        action.init(null, null, ViewerDropLocation.ON, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 2, data.getZorder());
        assertEquals( 1, destination.getZorder());
        
        data = map.getLayersInternal().get(2);
        destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.ON, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 1, destination.getZorder());
    }

    @Test
    public void testDropAfterOtherLayer() throws Exception {
        Layer data = map.getLayersInternal().get(0);
        Layer destination = map.getLayersInternal().get(2);
        action.init(null, null, ViewerDropLocation.AFTER, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 1, data.getZorder());
        assertEquals( 2, destination.getZorder());

        data = map.getLayersInternal().get(2);
        destination = map.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.AFTER, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 1, destination.getZorder());
    }

    @Test
    public void testDropNoWhere() throws Exception {
        Layer data = map.getLayersInternal().get(2);
        Layer destination = map.getLayersInternal().get(1);
        action.init(null, null, ViewerDropLocation.NONE, destination, data);
        action.perform(new NullProgressMonitor());
        
        assertEquals( 0, data.getZorder());
        assertEquals( 2, destination.getZorder());
    }

    @Test
    public void testIntegrationTest() throws Exception {
        UDIGDropHandler handler = new UDIGDropHandler();
        final Layer data = map.getLayersInternal().get(0);
        Object destination = map.getLayersInternal().get(2);
        
        handler.setTarget(destination);
        handler.setViewerLocation(ViewerDropLocation.BEFORE);
        handler.performDrop(data, null);
        final Object finalDest=destination;
        UDIGTestUtil.inDisplayThreadWait(4000, new WaitCondition(){

            public boolean isTrue() {
                return data.getZorder()==2 && ((Layer) finalDest).getZorder()==1;
            }
            
        }, false);
        
        assertEquals(2, data.getZorder());
        assertEquals(1, ((Layer) destination).getZorder());
        assertEquals(5, map.getLayersInternal().size());
        
    }
}
