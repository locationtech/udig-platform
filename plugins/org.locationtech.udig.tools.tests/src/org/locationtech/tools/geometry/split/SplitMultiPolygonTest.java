/* uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.tools.geometry.split;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * This test is based a unit test developed by the client.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class SplitMultiPolygonTest {
	/**
	 * This test case belongs to the document: splitissues_A.pdf. The input data
	 * has been changed to be valid. It has been split into 2 geometries. This
	 * test behold the first geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_A_inputChanged_1() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON (((0 0, 5 0, 5 10, 0 10, 0 8, 3 8, 3 6, 0 6, 0 0)))"); //$NON-NLS-1$
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(6 1, 3 6, 3 8, 6 9)"); //$NON-NLS-1$

		assertTrue(splitee.isValid());

		Geometry partA = SplitTestUtil.read("POLYGON ((5 2.6666666666666665, 3 6, 0 6, 0 0, 5 0, 5 2.6666666666666665))"); // A //$NON-NLS-1$
		Geometry partB = SplitTestUtil.read("POLYGON ((5 8.666666666666666, 3 8, 3 6, 5 2.6666666666666665, 5 8.666666666666666))"); //$NON-NLS-1$
		Geometry partC = SplitTestUtil.read("POLYGON ((3 8, 0 8, 0 10, 5 10, 5 8.666666666666666, 3 8))"); //$NON-NLS-1$
		// wrong polygon
		// Geometry partD =
		// read("POLYGON ((3 8, 3 6, 0 6, 0 0, 5 0, 5 2.6666666666666665, 5 8.666666666666666, 5 10, 0 10, 0 8, 3 8))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);
		// expectedParts.add(partD);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * This test case belongs to the document: splitissues_A.pdf. The input data
	 * has been changed to be valid. It has been split into 2 geometries. This
	 * test behold the second geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_A_inputChanged_2() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON (((0 6, 3 6, 3 8, 0 8, 0 6)))"); //$NON-NLS-1$
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(6 1, 3 6, 3 8, 6 9)"); // same //$NON-NLS-1$
		// split
		// line

		assertTrue(splitee.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);

	}

	/**
	 * This test case belongs to the document: splitissues_0203_20100324.pdf.
	 * Case 2. The input data has been changed to be valid. It has been split
	 * into 2 geometries. This test behold the first geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_002_withValidData_1() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON(((1 1, 1 8, 10 8, 10 1, 1 1),(2 3, 8 3, 8 6, 2 6, 2 3)))"); //$NON-NLS-1$

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(5 5, 5 2, 2 2, 2 3)"); //$NON-NLS-1$

		assertTrue(splitee.isValid());

		Geometry partA = SplitTestUtil.read("POLYGON ((1 1, 1 8, 10 8, 10 1, 1 1), (5 3, 8 3, 8 6, 2 6, 2 3, 2 2, 5 2, 5 3))"); //$NON-NLS-1$

		Geometry partB = SplitTestUtil.read("POLYGON ((2 3, 2 2, 5 2, 5 3, 2 3))"); //$NON-NLS-1$

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * This test case belongs to the document: splitissues_0203_20100324.pdf.
	 * Case 2. The input data has been changed to be valid.It has been split
	 * into 2 geometries. This test behold the second geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_002_withValidData_2() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON (((2 3, 8 3, 8 6, 2 6, 2 3)))"); //$NON-NLS-1$

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(5 5, 5 2, 2 2, 2 3)"); //$NON-NLS-1$

		assertTrue(splitee.isValid());

		// there isn't any split

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * This test case belongs to the document: splitissues_0203_20100324.pdf
	 * Case 3.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_003() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON(((1 1, 1 8, 10 8, 10 1, 1 1),(2 3, 8 3, 8 6, 2 6, 2 3)))"); //$NON-NLS-1$

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(2 3, 2 2, 5 2, 5 5)"); //$NON-NLS-1$

		assertTrue(splitee.isValid());

		Geometry partA = SplitTestUtil.read("POLYGON ((1 1, 1 8, 10 8, 10 1, 1 1), (5 3, 8 3, 8 6, 2 6, 2 3, 2 2, 5 2, 5 3))"); //$NON-NLS-1$

		Geometry partB = SplitTestUtil.read("POLYGON ((2 3, 2 2, 5 2, 5 3, 2 3))"); //$NON-NLS-1$

		// this geometry already exist, is the one which lies inside the hole.
		// So it isn't an expected geometry.
		// Geometry partC = read("POLYGON ((2 3, 5 3, 8 3, 8 6, 2 6, 2 3))");

		// assertEquals(true, partC.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		// expectedParts.add(partC);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * This test case belongs to the document: splitissues_0203_20100324.pdf.
	 * Case 3. The input data has been changed to be valid. It has been split
	 * into 2 geometries. This test behold the first geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_003_withValidData_1() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON(((1 1, 1 8, 10 8, 10 1, 1 1),(2 3, 8 3, 8 6, 2 6, 2 3)))"); //$NON-NLS-1$

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(2 3, 2 2, 5 2, 5 5)"); //$NON-NLS-1$

		assertTrue(splitee.isValid());

		Geometry partA = SplitTestUtil.read("POLYGON ((1 1, 1 8, 10 8, 10 1, 1 1), (5 3, 8 3, 8 6, 2 6, 2 3, 2 2, 5 2, 5 3))"); //$NON-NLS-1$

		Geometry partB = SplitTestUtil.read("POLYGON ((2 3, 2 2, 5 2, 5 3, 2 3))"); //$NON-NLS-1$


		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * This test case belongs to the document: splitissues_0203_20100324.pdf.
	 * Case 3. The input data has been changed to be valid. It has been split
	 * into 2 geometries. This test behold the second geometry.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSzenario_003_withValidData_2() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil.read("MULTIPOLYGON (((2 3, 8 3, 8 6, 2 6, 2 3)))"); //$NON-NLS-1$

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(2 3, 2 2, 5 2, 5 5)"); //$NON-NLS-1$
		assertTrue(splitee.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

}
