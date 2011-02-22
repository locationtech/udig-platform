package net.refractions.udig.project.internal.render;

import java.awt.Dimension;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.feature.Feature;

public class SelectionLayerTest extends AbstractProjectTestCase {

    IRenderContext context;
    SelectionLayer selectionLayer;
    Layer layer;

    public SelectionLayerTest() {
        super();
    }

    public SelectionLayerTest( String name ) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Feature[] features=UDIGTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
        Map map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features,true), new Dimension(512,512));
        context = map.getRenderManagerInternal().getRenderExecutor().getContext();
        layer = map.getLayersInternal().get(0);
//        layer.setZorder(5);
        selectionLayer = new SelectionLayer(layer);
    }

    public void testCompareTo(){
        assertTrue(layer.compareTo(selectionLayer) < 0);
        assertTrue(selectionLayer.compareTo(layer) > 0);
    }
}
