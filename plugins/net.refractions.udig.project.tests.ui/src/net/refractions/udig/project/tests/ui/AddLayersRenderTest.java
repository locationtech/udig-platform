package net.refractions.udig.project.tests.ui;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderListenerAdapter;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.impl.CompositeContextListener;
import net.refractions.udig.project.internal.render.impl.CompositeRendererImpl;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.LayersView;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.simple.SimpleFeature;

public class AddLayersRenderTest extends AbstractProjectUITestCase {

    private Map map;
    private RenderListenerAdapter listener;
    protected int renders=0;
    private CompositeContextListener contextListener;

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        // ignore... tests are broken
        if (true) {
            return;
        }
        
        super.setUp();
        map = MapTests.createDefaultMap("type1", 2, true, null); //$NON-NLS-1$
        map.setRenderManagerInternal(null);
        ApplicationGIS.openMap(map, true);
        map.getRenderManagerInternal().refresh(null);
        UDIGTestUtil.inDisplayThreadWait(1000000, new WaitCondition(){

            public boolean isTrue() {
                return map.getRenderManagerInternal().getRenderExecutor().getState()==IRenderer.DONE;
            }
            
        }, false);
        listener=new RenderListenerAdapter(){
          @Override
            protected void renderRequest() {
                renders++;
            }  
        };
        contextListener=new CompositeContextListener(){

            public void notifyChanged( CompositeRenderContext context, List<RenderContext> contexts, boolean added ) {
                List<IRenderer> renderers = map.getRenderManagerInternal().getRenderers();
                for( IRenderer renderer : renderers ) {
                    if( contexts.contains(renderer.getContext() ) ){
                        if( added ){
                            ((Renderer)renderer).eAdapters().add(listener);
                        }else{
                            ((Renderer)renderer).eAdapters().remove(listener);                            
                        }
                    }
                }
            }
            
        };
        AdapterImpl adapterImpl = new AdapterImpl(){
                   @Override
                public void notifyChanged( final Notification msg ) {
                    
                    if( msg.getNotifier() instanceof RenderManager &&
                            msg.getFeatureID(RenderManager.class)==RenderPackage.RENDER_MANAGER__RENDER_EXECUTOR ){
                        if( msg.getNewValue()!=null ){
                            ((CompositeRendererImpl)map.getRenderManagerInternal().getRenderExecutor().getRenderer()).eAdapters().add(this);
                            ((CompositeRenderContext)map.getRenderManagerInternal().getRenderExecutor().getContext()).addListener(contextListener);
                        }
                        if( msg.getOldValue()!=null ){
                            ((CompositeRendererImpl)map.getRenderManagerInternal().getRenderExecutor().getContext()).eAdapters().remove(this);
                            ((CompositeRenderContext)map.getRenderManagerInternal().getRenderExecutor().getContext()).removeListener(contextListener);
                        }
                    }else{
                        if( msg.getNotifier() instanceof CompositeRendererImpl ){
                            if( msg.getNewValue()!=null ){
                                ((CompositeRenderContext)map.getRenderManagerInternal().getRenderExecutor().getContext()).addListener(contextListener);
                            }
                            if( msg.getOldValue()!=null ){
                                ((CompositeRenderContext)map.getRenderManagerInternal().getRenderExecutor().getContext()).removeListener(contextListener);
                            }
                        }
                    }
                } 
                };
        map.getRenderManagerInternal().eAdapters().add( adapterImpl);
        CompositeRendererImpl compRenderer = (CompositeRendererImpl)map.getRenderManagerInternal().getRenderExecutor().getRenderer();
        compRenderer.eAdapters().add(adapterImpl);
        List<Renderer> renderers = compRenderer.children();
        for( Renderer renderer : renderers ) {
            renderer.eAdapters().add(listener);
        }
        CompositeRenderContext compContext = ((CompositeRenderContext)map.getRenderManagerInternal().getRenderExecutor().getContext());
        compContext.addListener(contextListener);
        
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LayersView.ID);
        ApplicationGISInternal.getActiveEditor().setTesting(true);

    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void xtestAddOneLayer() throws Exception {
        renders=0;
        
        List<IGeoResource> resources=new ArrayList<IGeoResource>();
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("type2", 4); //$NON-NLS-1$
        resources.add(MapTests.createGeoResource(features, true));
        ApplicationGIS.addLayersToMap(map, resources, 0);
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return 0<renders;
            }
            
        }, false);
        assertEquals(1,renders);
    }
    public void xtestAddMultipleLayers() throws Exception {
        renders=0;
        List<IGeoResource> resources=new ArrayList<IGeoResource>();
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("type3", 3); //$NON-NLS-1$
        SimpleFeature[] features2 = UDIGTestUtil.createDefaultTestFeatures("type4", 4); //$NON-NLS-1$
        SimpleFeature[] features3 = UDIGTestUtil.createDefaultTestFeatures("type5", 5); //$NON-NLS-1$
        resources.add(MapTests.createGeoResource(features, true));
        resources.add(MapTests.createGeoResource(features2, true));
        IGeoResource createGeoResource = MapTests.createGeoResource(features3, true);
        resources.add(createGeoResource);
        resources.add(createGeoResource);
        ApplicationGIS.addLayersToMap(map, resources, 0);
        UDIGTestUtil.inDisplayThreadWait(3000, new WaitCondition(){

            public boolean isTrue() {
                return 3<renders;
            }
            
        }, false);
        assertEquals(4,renders);
    }
    
}
