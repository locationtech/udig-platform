package net.refractions.udig.tool.tests;

import junit.framework.TestCase;
import net.refractions.udig.tools.internal.CursorPosition;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

public class CursorPositionTest extends TestCase {
	public void testParseString() throws Exception {
		Coordinate coord=CursorPosition.parse("124,88", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("(124,88)", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("124 88", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("(124 88)", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("124 88LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("(124 88)LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse(" (124 88 )", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("( 124, 88 )", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("(124, 88)LL", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("[124 88]", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("[124 88]LATLONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("[124 88]LAT LONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("124 88LAT LONG", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		coord=CursorPosition.parse("124 88ll", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertEquals(new Coordinate(124,88), coord);
		CoordinateReferenceSystem albers = CRS.decode("EPSG:3005"); //$NON-NLS-1$
		coord=CursorPosition.parse("124 88LAT LONG", albers); //$NON-NLS-1$
		Coordinate expected=new Coordinate();
		JTS.transform(new Coordinate(124,88), expected, CRS.findMathTransform(DefaultGeographicCRS.WGS84,albers));
		assertEquals(expected.x, coord.x, 0.00001);

		coord=CursorPosition.parse("aasdf asdf", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertNull(coord);
		
		coord=CursorPosition.parse("13g4", DefaultGeographicCRS.WGS84); //$NON-NLS-1$
		assertNull(coord);
		
	}
}
