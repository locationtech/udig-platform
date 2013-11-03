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
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.CompositeRendererImpl;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RenderManagerDynamicTest extends AbstractProjectUITestCase {

    private Map map;

    @Before
    public void setUp() throws Exception {
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

    @Ignore
    @Test
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
