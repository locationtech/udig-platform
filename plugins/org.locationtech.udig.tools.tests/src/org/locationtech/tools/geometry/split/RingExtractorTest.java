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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

import org.locationtech.udig.tools.geometry.split.RingExtractor;
import org.locationtech.udig.tools.geometry.split.RingExtractor.ResultRingExtractor;

/**
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
public class RingExtractorTest {

	/**
	 * Test method for
	 * {@link es.axios.lib.geometry.split.RingExtractor#RingExtractor(org.locationtech.jts.geom.LineString)}
	 * .
	 */
	@Test
	public void testRingExtractor() throws Exception {
		LineString line = (LineString) SplitTestUtil.read("LINESTRING(6 1, 3 6, 3 8, 6 9)");

		RingExtractor ringExt = new RingExtractor(line);

		assertNotNull(ringExt);
	}

	private void analyzeOutput(ResultRingExtractor output, List<Geometry> expectedParts) {

		List<Geometry> rings = (List<Geometry>) output.getRings();

		List<Geometry> resultParts = new ArrayList<Geometry>();
		resultParts.add((Geometry) output.getRemainingLine());
		resultParts.addAll((Collection<? extends Geometry>) output.getRings());

		int numGeoms = expectedParts.size();

		for (int expectedPartN = 0; expectedPartN < numGeoms; expectedPartN++) {
			boolean found = false;
			Geometry expectedPart = expectedParts.get(expectedPartN);
			expectedPart.normalize();

			for (int splittedPartN = 0; splittedPartN < resultParts.size(); splittedPartN++) {
				Geometry splittedPart = resultParts.get(splittedPartN);
				splittedPart.normalize();
				if (expectedPart.equals(splittedPart)) {
					found = true;
					assertTrue( splittedPart.isValid());
					break;
				}
			}
			if (!found) {
				fail(expectedPart + " not found in " + resultParts); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Test method for
	 * {@link es.axios.lib.geometry.split.RingExtractor#processExtraction()}.
	 */
	@Test
	public void testProcessExtraction0() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (100 230, 100 90, 190 90, 190 120, 30 120, 30 180, 230 180, 230 90)");

		Geometry partA = SplitTestUtil.read("LINESTRING (100 230, 100 180, 230 180, 230 90)");

		Geometry partB = SplitTestUtil.read("LINEARRING (100 120, 100 90, 190 90, 190 120, 100 120)");

		Geometry partC = SplitTestUtil.read("LINESTRING (30 180, 100 180, 100 120, 30 120, 30 180)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);

		RingExtractor ringExt = new RingExtractor(line);
		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testProcessExtraction1() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (150 310, 150 130, 100 130, 100 200, 300 200, 300 130, 230 130, 230 260)");

		Geometry partA = SplitTestUtil.read("LINESTRING (150 310, 150 200, 230 200, 230 260)");

		Geometry partB = SplitTestUtil.read("LINESTRING (100 200, 150 200, 150 130, 100 130, 100 200)");

		Geometry partC = SplitTestUtil.read("LINESTRING (300 200, 300 130, 230 130, 230 200, 300 200)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testProcessExtraction2() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (130 280, 130 160, 210 160, 210 190, 170 190, 170 140, 240 140, 240 210, 80 210)");

		Geometry partA = SplitTestUtil.read("LINESTRING (130 280, 130 210, 80 210)");

		Geometry partB = SplitTestUtil
					.read("LINESTRING (130 210, 240 210, 240 140, 170 140, 170 160, 130 160, 130 210)");

		Geometry partC = SplitTestUtil.read("LINESTRING (170 190, 210 190, 210 160, 170 160, 170 190)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testProcessExtraction3() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (250 270, 250 150, 110 150, 110 220, 210 220, 210 190, 170 190, 170 240, 130 240, 130 180, 90 180)");

		Geometry partA = SplitTestUtil.read("LINESTRING (250 270, 250 150, 110 150, 110 180, 90 180)");

		Geometry partB = SplitTestUtil.read("LINESTRING (170 220, 210 220, 210 190, 170 190, 170 220)");

		Geometry partC = SplitTestUtil.read("LINESTRING (130 240, 170 240, 170 220, 130 220, 130 240)");

		Geometry partD = SplitTestUtil.read("LINESTRING (110 220, 130 220, 130 180, 110 180, 110 220)");


		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);
		expectedParts.add(partD);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testProcessExtraction4() throws Exception {

		LineString line = (LineString) SplitTestUtil.read("LINESTRING (110 260, 280 260, 280 150, 110 150, 110 260)");

		Geometry partA = SplitTestUtil.read("LINESTRING (110 260, 280 260, 280 150, 110 150, 110 260)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testProcessExtraction5() throws Exception {

		LineString line = (LineString) SplitTestUtil.read("LINESTRING (150 260, 270 260, 270 180, 160 180, 160 270)");

		Geometry partA = SplitTestUtil.read("LINESTRING (160 270, 160 260, 150 260)");

		Geometry partB = SplitTestUtil.read("LINESTRING (160 180, 160 260, 270 260, 270 180, 160 180)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testEndLineFinishOnEdge() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (260 280, 260 140, 130 140, 130 220, 90 220, 90 190, 180 190, 180 280, 240 280, 240 220, 150 220, 150 160, 240 160, 240 190, 180 190)");

		Geometry partA = SplitTestUtil.read("LINESTRING (260 280, 260 140, 130 140, 130 190, 150 190)");

		Geometry partB = SplitTestUtil.read("LINESTRING (90 220, 130 220, 130 190, 90 190, 90 220)");

		Geometry partC = SplitTestUtil.read("LINESTRING (150 220, 180 220, 180 190, 150 190, 150 220)");

		Geometry partD = SplitTestUtil.read("LINESTRING (240 190, 240 160, 150 160, 150 190, 240 190)");

		Geometry partE = SplitTestUtil.read("LINESTRING (180 280, 240 280, 240 220, 180 220, 180 280)");

		
		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);
		expectedParts.add(partD);
		expectedParts.add(partE);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	@Ignore("LINESTRING (180 110, 180 70, 240 70, 240 110, 180 110) not found in .. order of points changed")
	public void testEndLineFinishOnEdge2Rings() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (180 160, 180 70, 240 70, 240 110, 130 110, 130 70, 180 70)");

		Geometry partA = SplitTestUtil.read("LINESTRING (180 160, 180 110)");

		Geometry partB = SplitTestUtil.read("LINESTRING (180 110, 240 110, 240 70, 180 70, 180 110)");

		Geometry partC = SplitTestUtil.read("LINESTRING (130 110, 180 110, 180 70, 130 70, 130 110)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

	@Test
	public void testpolygonizer() throws Exception {

		// LineString line = (LineString) SplitTestUtil
		// .read("LINESTRING (180 160, 180 70, 240 70, 240 110, 130 110, 130 70, 180 70)");

		// LineString line = (LineString) SplitTestUtil
		// .read("LINESTRING (100 230, 100 90, 190 90, 190 120, 30 120, 30 180, 230 180, 230 90)");

		// LineString line = (LineString) SplitTestUtil
		// .read("LINESTRING (150 310, 150 130, 100 130, 100 200, 300 200, 300 130, 230 130, 230 260)");
		// //
		// LineString line = (LineString) SplitTestUtil
		// .read("LINESTRING (130 280, 130 160, 210 160, 210 190, 170 190, 170 140, 240 140, 240 210, 80 210)");
		//
		// LineString line = (LineString) SplitTestUtil
		// .read("LINESTRING (250 270, 250 150, 110 150, 110 220, 210 220, 210 190, 170 190, 170 240, 130 240, 130 180, 90 180)");
		// //
		// LineString line = (LineString)
		// SplitTestUtil.read("LINESTRING (110 260, 280 260, 280 150, 110 150, 110 260)");
		//
		// LineString line = (LineString)
		// SplitTestUtil.read("LINESTRING (150 260, 270 260, 270 180, 160 180, 160 270)");
		//
		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (260 280, 260 140, 130 140, 130 220, 90 220, 90 190, 180 190, 180 280, 240 280, 240 220, 150 220, 150 160, 240 160, 240 190, 180 190)");

		List<LinearRing> ringList = new ArrayList<LinearRing>();

		Geometry multiLines = line.union();

		Polygonizer polygonizer = new Polygonizer();
		polygonizer.add(multiLines);
		Collection<Polygon> polyCollection = polygonizer.getPolygons();

		// add the rings to the ringList.
		for (Polygon pol : polyCollection) {

			Coordinate[] polCoord = pol.getExteriorRing().getCoordinates();

			LinearRing polygonRing = line.getFactory().createLinearRing(polCoord);
			ringList.add(polygonRing);
		}
		//
		// for (Geometry ring : ringList) {
		//
		// System.out.println(ring.toString());
		// }

		// test result=ok

	}

	@Test
	public void testLineOverLine() throws Exception {

		LineString line = (LineString) SplitTestUtil
					.read("LINESTRING (110 330, 110 210, 260 210, 260 280, 110 280, 110 80, 240 80)");

		Geometry partA = SplitTestUtil.read("LINESTRING (110 280, 260 280, 260 210, 110 210, 110 280)");

		Geometry partB = SplitTestUtil.read("LINESTRING (110 330, 110 280, 110 210, 110 80, 240 80)");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		RingExtractor ringExt = new RingExtractor(line);

		ResultRingExtractor output = ringExt.processExtraction();

		analyzeOutput(output, expectedParts);
	}

}
