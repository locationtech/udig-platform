package net.refractions.udig.project.ui.internal;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.internal.render.impl.CompositeRendererImpl;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

public class RenderManagerDynamicTest extends AbstractProjectUITestCase {

    private Map map;

    protected void setUp() throws Exception {
        super.setUp();
        map=MapTests.createDefaultMap("RMDTestType", 3, true, null, false); //$NON-NLS-1$
        Map m2 = MapTests.createDefaultMap("RMDTestType2", 3, true, null, false); //$NON-NLS-1$
        map.getLayersInternal().add(m2.getLayersInternal().get(0));

        ApplicationGIS.openMap(map, true);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return !map.getRenderManagerInternal().getRenderers().isEmpty();
            }

        }, true);
    }

    public void testRefreshLayer() throws Exception {
        final RenderExecutor renderExecutor = map.getRenderManagerInternal().getRenderExecutor();
        renderExecutor.setState(IRenderer.NEVER);
        final CompositeRendererImpl impl=(CompositeRendererImpl) renderExecutor.getRenderer();
        impl.setState(IRenderer.NEVER);
        for( Renderer renderer: impl.children() ) {
            renderer.setState(IRenderer.NEVER);
        }
        for( Renderer renderer: impl.children() ) {
            renderer.setState(IRenderer.NEVER);
            assertEquals(IRenderer.NEVER, renderer.getState());
        }

        final ILayer refreshLayer = map.getMapLayers().get(0);
        map.getRenderManagerInternal().refresh(refreshLayer, null);
        UDIGTestUtil.inDisplayThreadWait(3000000, new WaitCondition(){

            public boolean isTrue() {
                for( Renderer renderer: impl.children() ) {
                    ILayer currentLayer = renderer.getContext().getLayer();
                    if( currentLayer==refreshLayer ){
                        if( IRenderer.DONE!=renderer.getState() )
                        return false;
                    } else if( currentLayer instanceof SelectionLayer
                            && ((SelectionLayer)currentLayer).getWrappedLayer()==refreshLayer ){
                        if( IRenderer.DONE!=renderer.getState() )
                            return false;
                    } else {
                        assertEquals(currentLayer+" is supposed to be NEVER", IRenderer.NEVER, renderer.getState()); //$NON-NLS-1$
                    }
                }
                return true;
            }

        }, false);

        for( Renderer renderer: impl.children() ) {
            ILayer currentLayer = renderer.getContext().getLayer();
            if( currentLayer==refreshLayer)
                assertEquals(IRenderer.DONE, renderer.getState());
            else if( currentLayer instanceof SelectionLayer && ((SelectionLayer)currentLayer).getWrappedLayer()==refreshLayer){
                    assertEquals(IRenderer.DONE, renderer.getState());
            } else {
                assertEquals(IRenderer.NEVER, renderer.getState());
            }
        }
    }
}
