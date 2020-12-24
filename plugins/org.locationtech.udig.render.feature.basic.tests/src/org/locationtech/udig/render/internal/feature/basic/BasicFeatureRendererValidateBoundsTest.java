package org.locationtech.udig.render.internal.feature.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.IRenderContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

@RunWith(MockitoJUnitRunner.class)
public class BasicFeatureRendererValidateBoundsTest {

    @Mock
    IRenderContext renderContext;

    @Mock
    Map map;

    @Mock
    Layer layer;

    @Test
    public void layerBoundsAreUnknownNULLDrawWhatIsOnScreen() throws Exception {
        // as used in LayerImpl
        ReferencedEnvelope emptyEnvelope = new ReferencedEnvelope(new Envelope(), null);

        assertTrue(emptyEnvelope.isNull());
        assertTrue(emptyEnvelope.isEmpty());

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);

        when(renderContext.getImageBounds()).thenReturn(imageBounds);
        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        when(layer.getBounds(any(), any())).thenReturn(emptyEnvelope);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 20, 0, 20, DefaultGeographicCRS.WGS84),
                new NullProgressMonitor(), renderContext);

        assertEquals(imageBounds, result);

        verify(renderContext, atLeastOnce()).getImageBounds();
        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void layerBoundsAreUnknownRefIsNullDrawWhatIsOnScreen() throws Exception {
        // as used in LayerImpl
        ReferencedEnvelope emptyEnvelope = new ReferencedEnvelope(new Envelope(), null);

        assertTrue(emptyEnvelope.isNull());
        assertTrue(emptyEnvelope.isEmpty());

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        when(renderContext.getImageBounds()).thenReturn(imageBounds);
        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        when(layer.getBounds(any(), any())).thenReturn(null);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 20, 0, 20, DefaultGeographicCRS.WGS84),
                new NullProgressMonitor(), renderContext);

        assertEquals(imageBounds, result);
        verify(renderContext, atLeastOnce()).getImageBounds();
        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void getBoundsFromContextOnNullViewBounds() throws Exception {
        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        when(renderContext.getImageBounds()).thenReturn(imageBounds);
        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        assertEquals(imageBounds, BasicFeatureRenderer.validateBounds(null,
                new NullProgressMonitor(), renderContext));
        verify(renderContext, atLeastOnce()).getImageBounds();
        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map);
    }

    @Test
    public void onAnyIntersectionOfLayerBoundsWithViewBoundsUseViewBoundsSameCRS()
            throws Exception {
        ReferencedEnvelope expected = new ReferencedEnvelope(0, 70, 0, 80,
                DefaultGeographicCRS.WGS84);

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(0, 50, 0, 50,
                DefaultGeographicCRS.WGS84);

        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);
        when(layer.getBounds(any(), any())).thenReturn(layerBounds);

        assertEquals(expected, BasicFeatureRenderer.validateBounds(expected,
                new NullProgressMonitor(), renderContext));
        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void layerBoundsDoNotIntersectRequestedBoundsReturnsCRSmaxBoundsIntersectionWithViewBounds()
            throws Exception {
        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(110, 150, -20, -10,
                DefaultGeographicCRS.WGS84);

        when(layer.getBounds(any(), any())).thenReturn(layerBounds);

        assertEquals(imageBounds, BasicFeatureRenderer.validateBounds(imageBounds,
                new NullProgressMonitor(), renderContext));
        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void contextCRSmissmatchLayerCRSleadsToViewportImageBounds() throws Exception {
        ReferencedEnvelope givenCRS = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope layerBounds = new ReferencedEnvelope(110, 150, -20, -10,
                DefaultGeographicCRS.WGS84);

        when(renderContext.getImageBounds()).thenReturn(imageBounds);
        when(renderContext.getCRS()).thenReturn(DefaultGeocentricCRS.SPHERICAL);
        when(renderContext.getLayer()).thenReturn(layer);
        when(layer.getBounds(any(), any())).thenReturn(layerBounds);

        assertEquals(imageBounds, BasicFeatureRenderer.validateBounds(givenCRS,
                new NullProgressMonitor(), renderContext));

        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(renderContext, atLeastOnce()).getImageBounds();
        verify(layer, atLeastOnce()).getBounds(any(), any());

        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void validateResponseNullRefEnvelopeForBC_ALBERS() throws Exception {
        CRSFactory fac = ReferencingFactoryFinder.getCRSFactories(null).iterator().next();
        final CoordinateReferenceSystem BC_ALBERS_CRS = fac
                .createFromWKT(BasicFeatureRendererTest.BC_ALBERS_WKT);

        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        ReferencedEnvelope layerBounds = new ReferencedEnvelope(-150, -120, 45, 65, BC_ALBERS_CRS);

        when(layer.getBounds(any(), any())).thenReturn(layerBounds);

        ReferencedEnvelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 20, 0, 20, BC_ALBERS_CRS), new NullProgressMonitor(),
                renderContext);

        assertTrue(result.isNull());

        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

    @Test
    public void requestedBoundsAreLargerThenImageBounds() throws Exception {

        ReferencedEnvelope imageBounds = new ReferencedEnvelope(0, 100, 0, 90,
                DefaultGeographicCRS.WGS84);

        when(renderContext.getImageBounds()).thenReturn(imageBounds);
        when(renderContext.getCRS()).thenReturn(DefaultGeographicCRS.WGS84);
        when(renderContext.getLayer()).thenReturn(layer);

        when(layer.getBounds(any(), any()))
                .thenReturn(new ReferencedEnvelope(DefaultGeographicCRS.WGS84));

        Envelope result = BasicFeatureRenderer.validateBounds(
                new ReferencedEnvelope(0, 300, 0, 200, DefaultGeographicCRS.WGS84),
                new NullProgressMonitor(), renderContext);

        assertEquals(imageBounds, result);

        verify(renderContext, atLeastOnce()).getCRS();
        verify(renderContext, atLeastOnce()).getLayer();
        verify(renderContext, atLeastOnce()).getImageBounds();
        verify(layer, atLeastOnce()).getBounds(any(), any());
        verifyNoMoreInteractions(renderContext, map, layer);
    }

}
