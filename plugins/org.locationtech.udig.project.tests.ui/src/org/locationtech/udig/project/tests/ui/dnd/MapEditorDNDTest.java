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
package org.locationtech.udig.project.tests.ui.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.catalog.ui.ConnectionFactoryManager;
import org.locationtech.udig.internal.ui.UDIGControlDropListener;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.IDropHandlerListener;
import org.locationtech.udig.ui.UDIGDragDropUtilities;
import org.locationtech.udig.ui.ViewerDropLocation;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MapEditorDNDTest extends AbstractProjectUITestCase {
	
	UDIGDropHandler handler;
	boolean done;
	
	@Before
	public void setUp() throws Exception {
        ConnectionFactoryManager.instance().getConnectionFactoryDescriptors();
		
		UDIGControlDropListener dropper = UDIGDragDropUtilities.getEditorDropListener();
		handler = dropper.getHandler();
		handler.setTarget(new MapEditorWithPalette());
		done = false;
	}
    
	@Ignore
	@Test
	public void testSingle() throws Exception {
		Object data = getDataSingleResource();
		
		handler.performDrop(data, null);
		
        final int[] expectedLayers=new int[1];
        expectedLayers[0]=1;

        WaitCondition c=new WaitCondition(){
			public boolean isTrue() {
				Map map = ApplicationGISInternal.getActiveMap();
				if( map==null )
					return false;
				List<ILayer> layers = map.getMapLayers();
				if (layers.size()<expectedLayers[0])
					return false;
				return true;
			}
		};

		UDIGTestUtil.inDisplayThreadWait(15000,c, true);
		
		IMap map = ApplicationGIS.getActiveMap();
		assertNotNull(map);
		
		List<ILayer> layers = map.getMapLayers();
		assertFalse(layers.isEmpty());
		
		List<String> typeNames=Arrays.asList(new String[]{"streams"});  //$NON-NLS-1$
		
		for (ILayer layer : layers) {
			assertLayerType(layer,typeNames, ShapefileDataStore.class);
		}
        

        MapEditorPart activeEditor = ApplicationGISInternal.getActiveEditor();
        UDIGDropHandler dropHandler = activeEditor.getDropHandler();
        dropHandler.setTarget(activeEditor);
        dropHandler.setViewerLocation(ViewerDropLocation.NONE);
        dropHandler.performDrop(data, null);

        expectedLayers[0]=2;
        
        UDIGTestUtil.inDisplayThreadWait(4000,c, true);
		
        map = ApplicationGIS.getActiveMap();
        assertNotNull(map);
        
        layers = map.getMapLayers();
        assertEquals(2,layers.size());
        
        for (ILayer layer : layers) {
            assertLayerType(layer, typeNames, ShapefileDataStore.class);
        }
	}
    
	@Ignore
    @Test
    public void testHTMLTableDrop() throws Exception {
    	URL url=FileLocator.toFileURL(CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/lakes.shp")); //$NON-NLS-1$
    	String data="<td class='confluenceTd'> <span class=\"nobr\"><a href=\""+url.toString()+"\" title=\"Visit page outside Confluence\" rel=\"nofollow\">DM Solutions WMS<sup><img class=\"rendericon\" src=\"/confluence/images/icons/linkext7.gif\" height=\"7\" width=\"7\" align=\"absmiddle\" alt=\"\" border=\"0\"/></sup></a></span> </td>";  //$NON-NLS-1$//$NON-NLS-2$

    	final int[] baseLayers=new int[1];
        baseLayers[0]=0;
        if( ApplicationGIS.getActiveMap()!=null ){
            baseLayers[0]=ApplicationGIS.getActiveMap().getMapLayers().size();
        }
        
		handler.performDrop(data, null);
        WaitCondition c=new WaitCondition(){
			public boolean isTrue() {
				Map map = ApplicationGISInternal.getActiveMap();
				if( map==null )
					return false;
				List<ILayer> layers = map.getMapLayers();
				if (layers.size()<baseLayers[0]+1)
					return false;
				return true;
			}
		};

		UDIGTestUtil.inDisplayThreadWait(8000,c, false);
//		UDIGTestUtil.inDisplayThreadWait(800000,c, false);
		IMap map = ApplicationGIS.getActiveMap();
		System.out.println("current maps="+map.getProject().getElements()); //$NON-NLS-1$
		assertNotNull(map);
		List<ILayer> layers = map.getMapLayers();
		assertEquals(map.getName()+" should have "+(baseLayers[0]+1)+" number of layers but instead layers="+layers,  //$NON-NLS-1$ //$NON-NLS-2$
                baseLayers[0]+1, layers.size());
        

        List<String> typeNames=Arrays.asList(new String[]{"lakes"});  //$NON-NLS-1$
        
		for (ILayer layer : layers) {
			assertLayerType(layer, typeNames, ShapefileDataStore.class);
		}
		
		assertEquals("Should only be one map open", 1,ApplicationGIS.getOpenMaps().size()); //$NON-NLS-1$
        
    }

	@Ignore
    @Test
    public void testMulti() throws Exception {
        URL[] urls  = new URL[]{
        	FileLocator.toFileURL(CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/streams.shp")), //$NON-NLS-1$
        	FileLocator.toFileURL(CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/lakes.shp")) //$NON-NLS-1$
        };
        Object data = urls;
                
        int base = 0;
        final IMap currentMap = ApplicationGIS.getActiveMap();
        
        ApplicationGIS.createAndOpenMap(Collections.<IGeoResource>emptyList());

        UDIGTestUtil.inDisplayThreadWait(4000, new WaitCondition(){

            public boolean isTrue()  {
                IMap map = ApplicationGIS.getActiveMap();
                if( map==null || currentMap==map )
                    return false;
                
                return true;
            }
            
        }, true);
        Map activeMap = ApplicationGISInternal.getActiveMap();


        final int[] numberLayerAdds=new int[1];
        numberLayerAdds[0]=0;
        activeMap.addMapCompositionListener(new IMapCompositionListener(){

            public void changed( MapCompositionEvent event ) {
                numberLayerAdds[0]++;
            }
            
        });

        IMap map = ApplicationGIS.getActiveMap();
        if (map != null) {
            List<ILayer> layers = map.getMapLayers();
            base = layers.size();
        }
        
        final int[] numberActionsRan=new int[1];
        numberActionsRan[0]=0;
        
        handler.setTarget(map);
        handler.addListener(new IDropHandlerListener(){

            public void done( IDropAction action, Throwable error ) {
                numberActionsRan[0]++;
            }

            public void noAction( Object data ) {
            }

            public void starting( IDropAction action ) {
            }
            
        });
        
        handler.performDrop(data, null);
        
        final int base2=base;
        WaitCondition c=new WaitCondition(){
            public boolean isTrue() {
                Map map = ApplicationGISInternal.getActiveMap();
                if( map==null )
                    return false;
                List<ILayer> layers = map.getMapLayers();
                if (layers.size()<base2 + 2 || numberActionsRan[0]<1 || numberLayerAdds[0]<1 )
                    return false;
                return true;
            }
        };

        UDIGTestUtil.inDisplayThreadWait(8000,c, false);
//        UDIGTestUtil.inDisplayThreadWait(800000,c, false);
        
        map = ApplicationGIS.getActiveMap();
        assertNotNull(map);
        
        List<ILayer> layers = map.getMapLayers();
        assertEquals(layers.size(),base+2);
        
        assertEquals(1, numberLayerAdds[0]);
        assertEquals(1, numberActionsRan[0]);

        List<String> typeNames=Arrays.asList(new String[]{"lakes", "streams"});  //$NON-NLS-1$ //$NON-NLS-2$
        for (ILayer layer : layers) {
            assertLayerType(layer, typeNames, ShapefileDataStore.class);
        }
        
        assertEquals("Should only be one map open", 1,ApplicationGIS.getOpenMaps().size()); //$NON-NLS-1$
    }

	@Ignore
    @Test
    public void testMultiGeoResources() throws Exception {
        Object data = new Object[]{ 
                MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures("test1", 2), true), //$NON-NLS-1$
                MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures("test2", 2), true) //$NON-NLS-1$
        };
                
        int base = 0;

        final Map currentMap = ApplicationGISInternal.getActiveMap();
        
        ApplicationGIS.createAndOpenMap(Collections.<IGeoResource>emptyList());

        UDIGTestUtil.inDisplayThreadWait(4000, new WaitCondition(){

            public boolean isTrue()  {
                IMap map = ApplicationGIS.getActiveMap();
                if( map==null || currentMap==map )
                    return false;
                
                return true;
            }
            
        }, true);
        Map activeMap = ApplicationGISInternal.getActiveMap();


        final int[] numberLayerAdds=new int[1];
        numberLayerAdds[0]=0;
        activeMap.addMapCompositionListener(new IMapCompositionListener(){

            public void changed( MapCompositionEvent event ) {
                numberLayerAdds[0]++;
            }
            
        });

        IMap map = ApplicationGIS.getActiveMap();
        if (map != null) {
            List<ILayer> layers = map.getMapLayers();
            base = layers.size();
        }
        
        final int[] numberActionsRan=new int[1];
        numberActionsRan[0]=0;
        
        handler.setTarget(map);
        handler.addListener(new IDropHandlerListener(){

            public void done( IDropAction action, Throwable error ) {
                numberActionsRan[0]++;
            }

            public void noAction( Object data ) {
            }

            public void starting( IDropAction action ) {
            }
            
        });
        
        handler.performDrop(data, null);
        
        final int base2=base;
        WaitCondition c=new WaitCondition(){
            public boolean isTrue() {
                Map map = ApplicationGISInternal.getActiveMap();
                if( map==null )
                    return false;
                List<ILayer> layers = map.getMapLayers();
                if (layers.size()<base2 + 2 || numberActionsRan[0]<1 || numberLayerAdds[0]<1)
                    return false;
                return true;
            }
        };

        UDIGTestUtil.inDisplayThreadWait(8000,c, true);
        
        map = ApplicationGIS.getActiveMap();
        assertNotNull(map);
        
        List<ILayer> layers = map.getMapLayers();
        assertEquals(layers.size(),base+2);
        
        assertEquals(1, numberLayerAdds[0]);
        assertEquals(1, numberActionsRan[0]);
        
        List<String> typeNames=Arrays.asList(new String[]{"test1","test2"});  //$NON-NLS-1$//$NON-NLS-2$
        
        for (ILayer layer : layers) {
            assertLayerType(layer, typeNames, MemoryDataStore.class);
        }
        
        assertEquals("Should only be one map open", 1,ApplicationGIS.getOpenMaps().size()); //$NON-NLS-1$
    }
    
	public Object getDataSingleResource() throws Exception {
		URL url = CatalogTestsUIPlugin.getDefault().getBundle()
			.getEntry("data/streams.shp");	 //$NON-NLS-1$
		return FileLocator.toFileURL(url);
	}
	
	void assertLayerType(ILayer layer, List<String> featureTypes, Class type) throws Exception {
		assertTrue(layer.getGeoResources().get(0).parent(null).canResolve(type));
        String typeName = layer.getSchema().getName().getLocalPart();
        assertTrue( typeName+" is not the expected typeName for the layer", featureTypes.contains(typeName));
	}
}
