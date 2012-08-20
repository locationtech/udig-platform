package net.refractions.udig.project.tests.support;

import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.impl.CompositeRenderContextImpl;
import net.refractions.udig.project.internal.render.impl.RenderContextImpl;
import net.refractions.udig.project.internal.render.impl.RenderManagerImpl;
import net.refractions.udig.project.internal.render.impl.RendererCreatorImpl;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

public class MapTests {
	/**
	 * Creates a map that will not respond to events but is ready to render to the internal RenderContexts.  
	 * <p>
	 * The viewport model will contain all the features in the resource and will be the same CRS.
	 * <p>
	 * map.getRenderExecutor().getRenderContext.getImage() contains the rendered image.  (After a refresh is called).
	 * @param resource
	 * @param displaySize
	 * @return
	 * @throws Exception
	 */
	public static Map createNonDynamicMapAndRenderer(IGeoResource resource, Dimension displaySize) throws Exception{
		return createNonDynamicMapAndRenderer(resource,displaySize, null);
	}

    /**
     * Will create a rendermanager
     *
     * @param resource
     * @param displaySize
     * @param style
     * @return
     * @throws Exception
     */
    public static Map createNonDynamicMapAndRenderer(IGeoResource resource, Dimension displaySize, Style style) throws Exception {
        return createNonDynamicMapAndRenderer(resource, displaySize, style, true);
    }

    public static Map createNonDynamicMapAndRenderer(IGeoResource resource, Dimension displaySize, Style style, boolean createRenderManager) throws Exception {
		final Map map=ProjectFactory.eINSTANCE.createMap(ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject(),
                "testMap", new ArrayList<Layer>()); //$NON-NLS-1$

		Layer tmp=map.getLayerFactory().createLayer(resource);
        Layer layer=new TestLayer();
        layer.setID(tmp.getID());
        layer.setStyleBlackboard(tmp.getStyleBlackboard());
        layer.setDefaultColor(tmp.getDefaultColor());
        layer.setName(tmp.getName());

        if( style!=null ){
            StyleBlackboard styleBlackboard = layer.getStyleBlackboard();
            styleBlackboard.put(SLDContent.ID, style);
        }
		map.getLayersInternal().add(layer);
        if( createRenderManager){
    		map.setRenderManagerInternal(new RenderManagerImpl(){
                
    		    @Override
    		    public boolean isViewer() {
    		        return true;
    		    }
                
                @Override
                public void refresh( Envelope bounds ) {
                    // do nothing
                    
                }
                
                @Override
                public void refresh( ILayer layer, Envelope bounds ) {
                    //do nothing
                }
                
                @Override
                public void refreshSelection( ILayer layer, Envelope bounds ) {
                    // do nothing
                }
                
                @Override
                public void refreshImage() {
                    //do nothing
                }
            });
    		RenderManager rm=map.getRenderManagerInternal();
    		rm.setMapDisplay(new TestMapDisplay(displaySize));
    		rm.getRendererCreator().getLayers().add(layer);
            
            map.getViewportModelInternal().setCRS(layer.getCRS());
            
            final Runnable job = new Runnable() {
                @Override
                public void run() {
                    map.getViewportModelInternal().zoomToExtent();
                }
            };
            final Thread jobThread = new Thread(job);
            jobThread.start();
            jobThread.join();
            
    		MultiLayerRenderer renderer=(MultiLayerRenderer) RenderFactory.eINSTANCE.createCompositeRenderer();
    		RenderContext context=new CompositeRenderContextImpl();
    		context.setRenderManagerInternal(rm);
    		context.setMapInternal(map);
            context.setGeoResourceInternal(layer.getGeoResources().get(0));
            context.setLayerInternal(layer);
    		renderer.setContext(context);
    		context = rm.getRendererCreator().getConfiguration().iterator().next();
    		((CompositeRenderContext) renderer.getContext()).addContexts(Collections.singleton(context));
    		RenderExecutor ex=RenderFactory.eINSTANCE.createRenderExecutor(renderer);
    		rm.setRenderExecutor(ex);
        }
		return map;
	}
	/**
	 * Calles createService and finds the georesource containing the features.
     * @deprecated
	 */
	public static IGeoResource createGeoResource(SimpleFeature [] features, boolean deleteService) throws IOException{
		return CatalogTests.createGeoResource(features, deleteService);
	}

	/**
	 * Creates a MemoryDatastore service from an array of features.  Does not add to catalog. 
	 * @param deleteService 
     * @deprecated
	 */
	public static IService getService(SimpleFeature[] features, boolean deleteService) throws IOException {
		return CatalogTests.getService(features, deleteService);
	}

    /**
     * Will create a RenderManager and MapDisplay
     *
     * @param featureTypeName
     * @param numFeatures
     * @param deleteExistingService
     * @param displaySize
     * @return
     * @throws Exception
     */
    public static Map createDefaultMap(String featureTypeName, int numFeatures, 
            boolean deleteExistingService, Dimension displaySize) throws Exception{
        return createDefaultMap(featureTypeName, numFeatures, deleteExistingService, displaySize, true);
    }

    public static Map createDefaultMap(String featureTypeName, int numFeatures, 
            boolean deleteExistingService, Dimension displaySize, boolean createRenderManager) throws Exception {
        int toCreate=numFeatures;
        if( numFeatures == 0)
            toCreate=1;
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures(featureTypeName, toCreate);
        IGeoResource resource = CatalogTests.createGeoResource(features, deleteExistingService);
        if( numFeatures == 0)
            resource.resolve(FeatureStore.class, new NullProgressMonitor()).removeFeatures(Filter.INCLUDE);
        return createNonDynamicMapAndRenderer(resource, displaySize, null, createRenderManager);
    }

    /**
     * @deprecated Moved to CatalogTests
     */
    public static IGeoResource createGeoResource( String typeName, int numFeatures, boolean deleteService ) throws IOException, SchemaException, IllegalAttributeException {
        return CatalogTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(typeName, numFeatures), deleteService);
    }

    public static LayerImpl createLayer( URL id, Object resolveTo, Map map ) throws Exception {
        IGeoResource resource = CatalogTests.createResource(id, resolveTo);
        List<IGeoResource> list=new ArrayList<IGeoResource>();
        list.add(resource);
        TestLayer testLayer = new TestLayer(list);
        if( map==null )
            ProjectFactory.eINSTANCE.createMap(null, "TestMap",  //$NON-NLS-1$
                    Collections.singletonList(testLayer));
        else
            map.getLayersInternal().add(testLayer);
        return testLayer;
    }

    public static RendererCreatorImpl createRendererCreator( Map map ) {
        RendererCreatorImpl creator= new RendererCreatorImpl();
        RenderContextImpl renderContextImpl = new RenderContextImpl();
        renderContextImpl.setMapInternal(map);
        renderContextImpl.setRenderManagerInternal(map.getRenderManagerInternal());
        creator.setContext(renderContextImpl);
        if( !creator.getConfiguration().isEmpty() )
            throw new AssertionError("configuration should be empty on creation"); //$NON-NLS-1$
        return creator;
    }

    /**
     * Creates a layer does NOT add to the map.
     *
     * @param map
     * @param desiredService
     * @param serviceURL
     * @param resourceURL
     * @param name
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Layer createLayer(Map map, Class<? extends IService> desiredService, String serviceURL, String resourceURL, String name) throws MalformedURLException, IOException {
    	List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(new URL(serviceURL));
        IService s=null;
        for( IService service : services ) {
            if( service.getClass().isAssignableFrom(desiredService)){
                s=service;
                break;
            }
        }
        
        if( s==null )
            throw new AssertionError();
        if( !(s.resources(null).size()>0))
            throw new AssertionError();
        
    	CatalogPlugin.getDefault().getLocalCatalog().add(s);
    	List<IResolve> resources = CatalogPlugin.getDefault().getLocalCatalog().find(new URL(resourceURL), null); 
    	if( !(resources.size()>0) )
            throw new AssertionError();
        
        Layer layer = map.getLayerFactory().createLayer((IGeoResource) resources.get(0));
    	layer.setName(name);
    	return layer;
    }

    public static Map createDefaultMapNoRenderManager( String featureTypeName, int numFeatures, boolean deleteExistingService ) throws Exception {
        return createDefaultMap(featureTypeName, numFeatures, deleteExistingService, null, false);
    }
	
}
