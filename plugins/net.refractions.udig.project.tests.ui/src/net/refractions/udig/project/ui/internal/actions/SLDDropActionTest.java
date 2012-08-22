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
package net.refractions.udig.project.ui.internal.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.internal.ui.UDIGDropHandler;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.TestsUIPlugin;
import net.refractions.udig.ui.ViewerDropLocation;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.styling.Style;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class SLDDropActionTest {

    private Map map;
    private SLDDropAction action;
    private URL sldURL;
    private File sldFile;
    private UDIGDropHandler handler;

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
        action = new SLDDropAction();

        sldURL = FileLocator.toFileURL(TestsUIPlugin.getDefault().getBundle().getEntry("/test-data/teststyle.sld")); //$NON-NLS-1$
        sldFile = new File( sldURL.getFile() );
        handler=new UDIGDropHandler();
        assertTrue(sldFile.exists());
    }

    /**
     * Test method for {@link net.refractions.udig.project.ui.internal.actions.SLDDropAction#accept()}.
     */
    @Test
    public void testAccept() {
        
        // All locations but NONE should be acceptable.
        Layer destination = map.getLayersInternal().get(1);
        action.init(null, null, ViewerDropLocation.AFTER, destination, sldURL);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.ON, destination, sldURL);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.BEFORE, destination, sldURL);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.NONE, destination, sldURL);
        assertFalse(action.accept());
        
        action.init(null, null, ViewerDropLocation.BEFORE, destination, sldFile);
        assertTrue(action.accept());

        // can drop on map
        action.init(null, null, ViewerDropLocation.ON, map, sldFile);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.BEFORE, map, sldFile);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.AFTER, map, sldFile);
        assertTrue(action.accept());
        action.init(null, null, ViewerDropLocation.NONE, map, sldFile);
        assertFalse(action.accept());
        
        // can't drop on say... a ViewportModel
        action.init(null, null, ViewerDropLocation.ON, map.getViewportModel(), sldFile);
        assertFalse(action.accept());
    }

    @Test
    public void testDropURLOnLayer() throws Exception {
        final Layer destination = map.getLayersInternal().get(1);

        action.init(null, null, ViewerDropLocation.ON, destination, sldURL);
        action.perform(new NullProgressMonitor());
        final String expectedName="Test Style"; //$NON-NLS-1$
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return isTestStyle(destination, expectedName); 
            }
            
        }, false);
        
        assertTrue(isTestStyle(destination, expectedName));
    }

    @Test
    public void testDropFileOnLayer() throws Exception {
        final Layer destination = map.getLayersInternal().get(2);

        action.init(null, null, ViewerDropLocation.ON, destination, sldFile);
        action.perform(new NullProgressMonitor());
        final String expectedName="Test Style"; //$NON-NLS-1$
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return isTestStyle(destination, expectedName); 
            }
            
        }, false);
        
        assertTrue(isTestStyle(destination, expectedName));
    }

    @Test
    public void testDropOnMap() throws Exception {
        map.getEditManagerInternal().setSelectedLayer(map.getLayersInternal().get(3));
        action.init(null, null, ViewerDropLocation.ON, map, sldURL);
        action.perform(new NullProgressMonitor());
        final String expectedName="Test Style"; //$NON-NLS-1$
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return isTestStyle(map.getEditManager().getSelectedLayer(), expectedName); 
            }
            
        }, false);
        
        assertTrue(isTestStyle(map.getEditManager().getSelectedLayer(), expectedName));
    }

    @Ignore
    @Test
    public void testDropOnMapIntegration() throws Exception {
        map.getEditManagerInternal().setSelectedLayer(map.getLayersInternal().get(3));
        handler.setTarget(map);
        handler.performDrop(sldFile, null);
        final String expectedName="Test Style"; //$NON-NLS-1$
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return isTestStyle(map.getEditManager().getSelectedLayer(), expectedName); 
            }
            
        }, false);
        
        assertTrue(isTestStyle(map.getEditManager().getSelectedLayer(), expectedName));
    }
    
    @Test
    public void testDropOnLayerIntegration() throws Exception {
        final Layer destination = map.getLayersInternal().get(2);

        handler.setTarget(destination);
        handler.performDrop(sldURL, null);
        
        final String expectedName="Test Style"; //$NON-NLS-1$
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return isTestStyle(destination, expectedName); 
            }
            
        }, false);
        
        assertTrue(isTestStyle(destination, expectedName));
    }

    boolean isTestStyle( final ILayer destination, final String expectedName ) {
        Style style=(Style) destination.getStyleBlackboard().get("net.refractions.udig.style.sld"); //$NON-NLS-1$
        String name = style.getName();
        return name.equals(expectedName);
    }
}
