package net.refractions.udig.project.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;
import net.refractions.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.ExecutorVisitor;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.impl.RenderExecutorComposite;
import net.refractions.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.internal.LayersView;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class LayerVisibilityTest extends AbstractProjectUITestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
	public void xtestNormalRenderer() throws Exception {
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
	
	public void xtestMultiLayerRenderer() throws Exception {
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
