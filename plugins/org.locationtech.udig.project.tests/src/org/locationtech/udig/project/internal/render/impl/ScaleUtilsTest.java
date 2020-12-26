/**
 * 
 */
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.Point;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author jesse
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ScaleUtilsTest {

    @Mock
    private IMapDisplay mapDisplay;

    /**
     * Sets up the map display to return 200x120 dimension and 90 DPI
     */
    private void configureDisplaySize(final int width, final int height, final int dpi) {
        Dimension dim = new Dimension(width, height);

        when(mapDisplay.getDisplaySize()).thenReturn(dim);
        when(mapDisplay.getDPI()).thenReturn(dpi);
    }

    @Test
    public void roundTripSetScaleDenomWithinWorld() throws Exception {
        configureDisplaySize(360, 180, 100);

        double scale = 1000000;

        Dimension displaySize = mapDisplay.getDisplaySize();
        int dpi = mapDisplay.getDPI();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope originalExtent = new ReferencedEnvelope(-180, 180, -90, 90, crs);
        ReferencedEnvelope bounds = ScaleUtils.calculateBoundsFromScale(scale, displaySize, dpi,
                originalExtent);

        assertEquals(scale, ScaleUtils.calculateScaleDenominator(bounds,
                mapDisplay.getDisplaySize(), mapDisplay.getDPI()), ScaleUtils.ACCURACY);

        verify(mapDisplay, atLeastOnce()).getDPI();
        verify(mapDisplay, atLeastOnce()).getDisplaySize();
        Mockito.verifyNoMoreInteractions(mapDisplay);
    }

    @Test
    public void roundTripSetScaleDenomAlbers() throws Exception {
        configureDisplaySize(360, 180, 100);

        double scale = 1000000;

        Dimension displaySize = mapDisplay.getDisplaySize();
        int dpi = mapDisplay.getDPI();
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3005");
        ReferencedEnvelope originalExtent = new ReferencedEnvelope(700000, 14000000, 700000,
                14000000, crs);
        ReferencedEnvelope bounds = ScaleUtils.calculateBoundsFromScale(scale, displaySize, dpi,
                originalExtent);

        assertEquals(scale, ScaleUtils.calculateScaleDenominator(bounds,
                mapDisplay.getDisplaySize(), mapDisplay.getDPI()), ScaleUtils.ACCURACY);

        verify(mapDisplay, atLeastOnce()).getDPI();
        verify(mapDisplay, atLeastOnce()).getDisplaySize();
        Mockito.verifyNoMoreInteractions(mapDisplay);
    }

    @Test
    @Ignore("expected:<1000000.0> but was:<999999.9999993108>")
    public void roundTripSetScaleDenomOutOfBounds() throws Exception {
        configureDisplaySize(360, 180, 100);

        double scale = 1000000;

        Dimension displaySize = mapDisplay.getDisplaySize();
        int dpi = mapDisplay.getDPI();
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope originalExtent = new ReferencedEnvelope(-800, 200, -90, 90, crs);
        ReferencedEnvelope bounds = ScaleUtils.calculateBoundsFromScale(scale, displaySize, dpi,
                originalExtent);

        assertEquals(scale, ScaleUtils.calculateScaleDenominator(bounds,
                mapDisplay.getDisplaySize(), mapDisplay.getDPI()), ScaleUtils.ACCURACY);
    }

    @Test
    @Ignore("IllegalAccess toValidPixelBoundsClosestToCenter is not public")
    public void testToValidPixelBoundsClosestToCenter1() {
        Dimension d = new Dimension(360, 180);
        ReferencedEnvelope currentBounds = new ReferencedEnvelope(-370, -10, -90, 90,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope result = ScaleUtils.toValidPixelBoundsClosestToCenter(d, currentBounds);

        ReferencedEnvelope expected = new ReferencedEnvelope(-180, -179, -1, 0,
                DefaultGeographicCRS.WGS84);
        assertEquals(expected, result);
    }

    @Ignore
    @Test
    public void testToValidPixelBoundsClosestToCenter2() {
        Dimension d = new Dimension(753, 371);
        ReferencedEnvelope currentBounds = new ReferencedEnvelope(678905.6721597526,
                1438170.5040988692, 1396154.5085447333, 1770241.1654496635,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope result = ScaleUtils.toValidPixelBoundsClosestToCenter(d, currentBounds);

        // I don't know the answer but I do know it shouldn't blow up :-) and be
        // within the valid world
        assertTrue(result + " should be within the lat long world bounds",
                new Envelope(-180, 180, -90, 90).contains(result));
    }

    @Ignore
    @Test
    public void worldToPixelToWorld() throws Exception {
        Dimension displaySize = new Dimension(753, 371);
        ReferencedEnvelope bounds = new ReferencedEnvelope(678905.6721597526, 1438170.5040988692,
                1396154.5085447333, 1770241.1654496635, DefaultGeographicCRS.WGS84);
        Coordinate coordinate = new Coordinate(0, 0);
        Point pixel = ScaleUtils.worldToPixel(coordinate, bounds, displaySize);
        assertEquals(coordinate, ScaleUtils.pixelToWorld(pixel.x, pixel.y, bounds, displaySize));
    }

    @Test
    @Ignore("IllegalAccess nearestPixel is not public")
    public void testNearestPixel() {
        Dimension display = new Dimension(360, 180);
        ReferencedEnvelope world = new ReferencedEnvelope(-180, 180, -90, 90,
                DefaultGeographicCRS.WGS84);
        assertEquals(new Point(190, 100), ScaleUtils.nearestPixel(10, -10, world, display));
        assertEquals(new Point(360, 100), ScaleUtils.nearestPixel(190, -10, world, display));
        assertEquals(new Point(190, 180), ScaleUtils.nearestPixel(10, -100, world, display));
        assertEquals(new Point(360, 180), ScaleUtils.nearestPixel(190, -100, world, display));

        assertEquals(new Point(0, 100), ScaleUtils.nearestPixel(-190, -10, world, display));
        assertEquals(new Point(190, 0), ScaleUtils.nearestPixel(10, 100, world, display));
        assertEquals(new Point(0, 0), ScaleUtils.nearestPixel(-180, 100, world, display));

    }

    @Ignore
    @Test
    public void testPixelBounds() {
        ReferencedEnvelope currentBounds = new ReferencedEnvelope(-180, 180, -90, 90,
                DefaultGeographicCRS.WGS84);
        Dimension displaySize = new Dimension(360, 180);
        ReferencedEnvelope bounds = ScaleUtils.pixelBounds(0, 0, currentBounds, displaySize);
        ReferencedEnvelope expected = new ReferencedEnvelope(-180, -179, 89, 90,
                DefaultGeographicCRS.WGS84);
        assertEquals(expected, bounds);
    }

    @Test
    @Ignore("IllegalAccess shiftToWorld is not public")
    public void shiftToWorld() throws Exception {
        ReferencedEnvelope env = new ReferencedEnvelope(-200, 200, -20, 20,
                DefaultGeographicCRS.WGS84);
        ReferencedEnvelope result = ScaleUtils.shiftToWorld(env);

        assertEquals(env, result);

        env = new ReferencedEnvelope(-20, 20, -200, 200, DefaultGeographicCRS.WGS84);
        result = ScaleUtils.shiftToWorld(env);

        assertEquals(env, result);
    }

}
