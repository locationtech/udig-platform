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
package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.internal.shp.ShpServiceImpl;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ExecutorVisitor;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorComposite;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.internal.LayersView;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Ignore;
import org.junit.Test;

public class LayerVisibilityTest extends AbstractProjectUITestCase {
    
    @Ignore
    @Test
	public void testNormalRenderer() throws Exception {
		Map map = ProjectFactory.eINSTANCE.createMap();
		URL url1 = FileLocator.toFileURL(CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/streams.shp")); //$NON-NLS-1$
		String fragment1=url1.getFile();
		fragment1=fragment1.substring(0,fragment1.lastIndexOf('.'));
		fragment1="#"+fragment1.substring(fragment1.lastIndexOf("/")+1); //$NON-NLS-1$ //$NON-NLS-2$ 
		URL url2 = FileLocator.toFileURL(CatalogTestsUIPlugin.getDefault().getBundle().getEntry("data/lakes.shp")); //$NON-NLS-1$
		String fragment2=url2.getFile();
		fragment2=fragment2.substring(0,fragment2.lastIndexOf('.'));
		fragment2="#"+fragment2.substring(fragment2.lastIndexOf("/")+1); //$NON-NLS-1$ //$NON-NLS-2$ 
		Layer layer = MapTests.createLayer(map, ShpServiceImpl.class,
				url1.toExternalForm(),
				url1.toExternalForm()+fragment1, 
		"layer1");//$NON-NLS-1$
		Layer layer2 = MapTests.createLayer(map, ShpServiceImpl.class,
				url2.toExternalForm(),
				url2.toExternalForm()+fragment2, 
				"layer2");//$NON-NLS-1$
		
		runVisibilityTest(map, layer, layer2, true);
	}
	
    @Ignore
    @Test
	public void testMultiLayerRenderer() throws Exception {
		Map map = ProjectFactory.eINSTANCE.createMap();
				
		Layer layer = MapTests.createLayer(map, MapGraphicService.class,
				"file:/localhost/mapgraphic",//$NON-NLS-1$
		"file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
		"layer1");//$NON-NLS-1$
        Layer layer2 = MapTests.createLayer(map,  MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
                "layer2");//$NON-NLS-1$
        
		runVisibilityTest(map, layer, layer2, false);

		
	}

	private void runVisibilityTest(Map map, Layer layer, Layer layer2, boolean isShapefileTest) throws PartInitException, Exception {
		map.getLayersInternal().add(layer);
		map.setProjectInternal(ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject());
		map.setName("SetVisibilityTest"); //$NON-NLS-1$
		assertFalse(layer.getGeoResources().isEmpty());
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LayersView.ID);

		ApplicationGIS.openMap(map, true);
		map.getRenderManagerInternal().refresh(null);
		Thread.yield();
		waitInUI(map);

		int expectedlayers=isShapefileTest?2:1;
		assertion(map, expectedlayers);
		

		map.getLayersInternal().add(layer2);
		map.getRenderManagerInternal().refresh(null);
		waitInUI(map);
		
		expectedlayers=isShapefileTest?4:1;
		assertion(map,expectedlayers );

		
		layer.setVisible(false);
		waitInUI(map);
		assertion(map,expectedlayers );
		
		layer.setVisible(true);
		waitInUI(map);
		assertion(map,expectedlayers );
		
		layer.setVisible(false);
		layer2.setVisible(false);
		waitInUI(map);
		assertion(map,expectedlayers );
//		assertBlank(map.getRenderManagerInternal());

		layer.setVisible(true);
		layer2.setVisible(true);
		waitInUI(map);
		assertion(map,expectedlayers );

		layer2.setVisible(false);
		waitInUI(map);
		assertion(map,expectedlayers );
		//TESTING - currently this causes a random failure.  Something to do with waitInUI
//		assertNotBlank(map.getRenderManagerInternal());

		layer.setVisible(false);
		waitInUI(map);
		assertion(map,expectedlayers );
//		assertBlank(map.getRenderManagerInternal());
	}

	private void assertNotBlank(RenderManager renderManagerInternal) {
		BufferedImage image = renderManagerInternal.getRenderExecutor().getContext().getImage();
		for( int x=0; x<image.getWidth(); x++){
			for( int y=0; y<image.getHeight(); y++){
				
				if (image.getRGB(x,y)!=Color.WHITE.getRGB()){
					return;
				}
				
			}
			
		}
		fail("Image is blank when it should not be"); //$NON-NLS-1$
	}

	private void assertBlank(RenderManager renderManagerInternal) {
		BufferedImage image = renderManagerInternal.getRenderExecutor().getContext().getImage();
		for( int x=0; x<image.getWidth(); x++){
			for( int y=0; y<image.getHeight(); y++){
				
				if (image.getRGB(x,y)!=Color.WHITE.getRGB()){
					fail("Image is not blank when it should be"); //$NON-NLS-1$
				}
				
			}
			
		}
	}

	@SuppressWarnings("unchecked") 
	private void assertion(Map map, int expectedlayers) {
		RenderManager renderManagerInternal = map.getRenderManagerInternal();
        List<IRenderer> renderer = renderManagerInternal.getRenderers();
		for (IRenderer renderer2 : renderer) {
			assertNotNull(renderer2.getContext());
		}

		final int[] countedRenderers=new int[1];
        countedRenderers[0]=0;
        renderManagerInternal.getRenderExecutor().visit(new ExecutorVisitor(){

            public void visit( RenderExecutor executor ) {
                countedRenderers[0]++;
            }

            public void visit( RenderExecutorMultiLayer executor ) {
                countedRenderers[0]++;
            }

            public void visit( RenderExecutorComposite executor ) {
                for( RenderExecutor child : executor.getRenderer().getRenderExecutors() ) {
                    child.visit(this);
                }
            }
            
        });
        
        //		List<Layer> layers = map.getLayersInternal();
//		for (Layer layer : layers) {
//			int foundExecutor=0;
//			List<Adapter> adapters = layer.eAdapters();
//			for (Adapter adapter : adapters) {
//				if( adapter instanceof RenderExecutorImpl.LayerListener ){
//				foundExecutor++;
//				if( foundExecutor>2 )
//					fail("More than one RenderExecutor is registered with renderer"); //$NON-NLS-1$
//			}	
//				
//			}
//			if (foundExecutor==0)
//				fail("A render executor should be listening to the layer: "+layer.getName()); //$NON-NLS-1$
//		}
		assertEquals(expectedlayers, countedRenderers[0]);

	}

	private void waitInUI(Map map) throws Exception {
        long start=System.currentTimeMillis();
		while ( map.getRenderManagerInternal().getRenderExecutor().getState()!=IRenderer.DONE 
                && 
                System.currentTimeMillis()-start<20000
                &&
                !noVisibleLayers(map)){
		    if( !Display.getCurrent().readAndDispatch()){
		        Thread.sleep(200);
            }
        }
        
        //let screen update
		while (Display.getCurrent().readAndDispatch() );
		
	}

    private boolean noVisibleLayers( Map map ) {
        for( ILayer layer : map.getMapLayers() ) {
            if( layer.isVisible() )
                return false;
        }
        return true;
    }
}
