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
package org.locationtech.udig.tool.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.locationtech.udig.tools.internal.CursorPosition;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * CursorPositionTest class containing parse string tests that should succeed independant of the
 * used Locale.
 *
 */
public abstract class CursorPositionTest {

    @Test
    public void testParseStringCommon() throws Exception {

        // System.out.println(Locale.getDefault());

        Coordinate coord = CursorPosition.parse("124,88", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("(124,88)", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("124 88", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("(124 88)", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("124 88LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("(124 88)LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse(" (124 88 )", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("( 124, 88 )", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("(124, 88)LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("[124 88]", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("[124 88]LATLONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("[124 88]LAT LONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("124 88LAT LONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        coord = CursorPosition.parse("124 88ll", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124, 88), coord);
        CoordinateReferenceSystem albers = CRS.decode("EPSG:3005"); //$NON-NLS-1$
        coord = CursorPosition.parse("124 88LAT LONG", albers); //$NON-NLS-1$
        Coordinate expected = new Coordinate();
        JTS.transform(new Coordinate(124, 88), expected,
                CRS.findMathTransform(DefaultGeographicCRS.WGS84, albers));
        assertEquals(expected.x, coord.x, 0.00001);

        coord = CursorPosition.parse("aasdf asdf", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertNull(coord);

        coord = CursorPosition.parse("13g4", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertNull(coord);

        coord = CursorPosition.parse("124.88, 234.22", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124.88, 234.22), coord);

        // parse with dot decimal and ' ' separator should work in both Locales
        coord = CursorPosition.parse("124.88 234.22", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124.88, 234.22), coord);

        // parse with dot decimal ',' separator should work in both Locales
        coord = CursorPosition.parse(" 124.88,234.22", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124.88, 234.22), coord);

        // check that multiple spaces between coords do not cause a problem
        coord = CursorPosition.parse("124.88   234.22", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124.88, 234.22), coord);

        // check that multiple spaces between coords do not cause a problem
        coord = CursorPosition.parse("124.88,   234.22", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        assertEquals(new Coordinate(124.88, 234.22), coord);

    }
}
