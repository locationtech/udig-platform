package org.locationtech.udig.render.internal.feature.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

public class BasicFeatureRendererValidateBoundsTest {

    @Test
    public void  layerBoundsAreUnknownNULLDrawWhatIsOnScreen() throws Exception {
        // as used in LayerImpl
        ReferencedEnvelope emptyEnvelope = new ReferencedEnvelope(new Envelope(), null);
        
        assertTrue(emptyEnvelope.isNull());
        assertTrue(emptyEnvelope.isEmpty());
        
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(emptyEnvelope).anyTimes();

        EasyMock.replay(context, map, layer);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope(0,20,0,20, DefaultGeographicCRS.WGS84), new NullProgressMonitor(), context);
        
        assertEquals(imageBounds, result);

        EasyMock.verify(context, map, layer);
    }

    @Test
    public void  layerBoundsAreUnknownRefIsNullDrawWhatIsOnScreen() throws Exception {
        // as used in LayerImpl
        ReferencedEnvelope emptyEnvelope = new ReferencedEnvelope(new Envelope(), null);
        
        assertTrue(emptyEnvelope.isNull());
        assertTrue(emptyEnvelope.isEmpty());
        
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(null).anyTimes();

        EasyMock.replay(context, map, layer);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope(0,20,0,20, DefaultGeographicCRS.WGS84), new NullProgressMonitor(), context);
        
        assertEquals(imageBounds, result);

        EasyMock.verify(context, map, layer);
    }

    @Test
    public void  getBoundsFromContextOnNullViewBounds() throws Exception {
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getLayer()).andReturn(layer);
        EasyMock.replay(context, map);

        assertEquals(imageBounds,
                BasicFeatureRenderer.validateBounds(null, new NullProgressMonitor(), context));
        EasyMock.verify(context, map);
    }

    @Test
    public void  onAnyIntersectionOfLayerBoundsWithViewBoundsUseViewBoundsSameCRS() throws Exception {
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope expected = new ReferencedEnvelope(0, 70, 0, 80, DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(0, 50, 0, 50,
                DefaultGeographicCRS.WGS84);

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(layerBounds).anyTimes();

        EasyMock.replay(context, map, layer);

        assertEquals(expected, BasicFeatureRenderer.validateBounds(expected,
                new NullProgressMonitor(), context));
        EasyMock.verify(context, map, layer);
    }

    @Test
    public void layerBoundsDoNotIntersectRequestedBoundsReturnsCRSmaxBoundsIntersectionWithViewBounds()
            throws Exception {
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(110, 150, -20, -10,
                DefaultGeographicCRS.WGS84);

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(layerBounds).anyTimes();

        EasyMock.replay(context, map, layer);

        assertEquals(imageBounds, BasicFeatureRenderer.validateBounds(imageBounds,
                new NullProgressMonitor(), context));
        EasyMock.verify(context, map, layer);
    }
/*    
    @Test
    public void layerCRStransformationExecptionReturnsViewport() throws Exception {
//        ReferencedEnvelope transform = new ReferencedEnvelope(0, 100, 0, 90, DefaultGeographicCRS.WGS84).transform(DefaultGeocentricCRS.SPHERICAL, true);
        
        ReferencedEnvelope requestedCRS = new ReferencedEnvelope3D(DefaultGeocentricCRS.SPHERICAL);

        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeocentricCRS.SPHERICAL).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(110, 150, -20, -10,
                DefaultGeographicCRS.WGS84);
        

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(layerBounds).anyTimes();

        EasyMock.replay(context, map, layer);
        assertEquals(requestedCRS, BasicFeatureRenderer.validateBounds(requestedCRS, new NullProgressMonitor(), context));
        EasyMock.verify(context, map, layer);
        
    }
    */
/*
    @Test
    public void validateResponseNullRefEnvelopeForBC_ALBERS() throws Exception {
        CRSFactory fac = (CRSFactory) ReferencingFactoryFinder.getCRSFactories(null).iterator()
                .next();
        final CoordinateReferenceSystem BC_ALBERS_CRS = fac
                .createFromWKT(BasicFeatureRendererTest.BC_ALBERS_WKT);
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(-150, -120, 45, 65, BC_ALBERS_CRS);

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(layerBounds).anyTimes();

        EasyMock.replay(context, map, layer);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 20, 0, 20, BC_ALBERS_CRS), new NullProgressMonitor(),
                context);
        assertTrue(result.isNull());
        EasyMock.verify(context, map, layer);
    }

    @Test
    public void requestedBoundsAreLargerThenImageBounds()
            throws MismatchedDimensionException, IOException, FactoryException, RenderException {
        IRenderContext context = EasyMock.createNiceMock(IRenderContext.class);
        Map map = EasyMock.createNiceMock(Map.class);
        Layer layer = EasyMock.createNiceMock(Layer.class);

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        EasyMock.expect(context.getImageBounds()).andReturn(imageBounds).anyTimes();
        EasyMock.expect(context.getCRS()).andReturn(DefaultGeographicCRS.WGS84).anyTimes();
        EasyMock.expect(context.getLayer()).andReturn(layer).anyTimes();

        EasyMock.expect(layer.getBounds(EasyMock.anyObject(), EasyMock.anyObject()))
                .andReturn(new ReferencedEnvelope(DefaultGeographicCRS.WGS84)).anyTimes();

        EasyMock.replay(context, map, layer);
        Envelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 300, 0, 200, DefaultGeographicCRS.WGS84),
                new NullProgressMonitor(), context);
        assertEquals(imageBounds, result);

        EasyMock.verify(context, map, layer);
    }
    */
}
