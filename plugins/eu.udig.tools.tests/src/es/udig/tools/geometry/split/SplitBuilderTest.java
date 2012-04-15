/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package es.udig.tools.geometry.split;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import eu.udig.tools.geometry.split.SplitBuilder;

/**
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class SplitBuilderTest {

	private void analyzeOutput(List<Geometry> output, List<Geometry> expectedParts) {

		int numGeoms = expectedParts.size();

		for (int expectedPartN = 0; expectedPartN < numGeoms; expectedPartN++) {
			boolean found = false;
			Geometry expectedPart = expectedParts.get(expectedPartN);
			expectedPart.normalize();

			for (int splittedPartN = 0; splittedPartN < output.size(); splittedPartN++) {
				Geometry splittedPart = output.get(splittedPartN);
				splittedPart.normalize();
				if (expectedPart.equals(splittedPart)) {
					found = true;
					assertTrue( splittedPart.isValid());
					break;
				}
			}
			if (!found) {
				fail(expectedPart + " not found in " + output); //$NON-NLS-1$
			}
		}
	}

	@Test
	public void testBuilder2Steps() throws Exception {

		Geometry inputGeometry1 = (Geometry) SplitTestUtil.read("POLYGON ((5 35, 35 35, 35 10, 5 10, 5 35))");
		Geometry inputGeometry2 = (Geometry) SplitTestUtil.read("POLYGON ((35 35, 50 35, 50 10, 35 10, 35 35))");

		LineString line = (LineString) SplitTestUtil.read("LINESTRING (0 25, 40 25)");

		Geometry partA = SplitTestUtil.read("POLYGON ((5 35, 5 25, 35 25, 35 35, 5 35))");

		Geometry partB = SplitTestUtil.read("POLYGON ((5 25, 35 25, 35 10, 5 10, 5 25))");

		// to addvertex
		Geometry partC = SplitTestUtil.read(" POLYGON ((35 35, 50 35, 50 10, 35 10, 35 25, 35 35))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		List<Geometry> goingToSplit = new ArrayList<Geometry>();
		goingToSplit.add(inputGeometry1);

		List<Geometry> goingToNeighbour = new ArrayList<Geometry>();
		goingToNeighbour.add(inputGeometry2);

		SplitBuilder builder = SplitBuilder.newInstansceUsingSplitLine(line);
		builder.buildSplit(goingToSplit).buildNeighbours(goingToNeighbour);

		List<Geometry> result = builder.getSplitResult();

		analyzeOutput(result, expectedParts);

		expectedParts.clear();
		expectedParts.add(partC);

		result.clear();
		result = builder.getNeighbourResult();

		analyzeOutput(result, expectedParts);

	}

	@Test
	public void testBuilder1Steps() throws Exception {

		Geometry inputGeometry1 = (Geometry) SplitTestUtil.read("POLYGON ((5 35, 35 35, 35 10, 5 10, 5 35))");
		Geometry inputGeometry2 = (Geometry) SplitTestUtil.read("POLYGON ((35 35, 50 35, 50 10, 35 10, 35 35))");

		LineString line = (LineString) SplitTestUtil.read("LINESTRING (0 25, 40 25)");

		Geometry partA = SplitTestUtil.read("POLYGON ((5 35, 5 25, 35 25, 35 35, 5 35))");

		Geometry partB = SplitTestUtil.read("POLYGON ((5 25, 35 25, 35 10, 5 10, 5 25))");

		// to addvertex
		Geometry partC = SplitTestUtil.read(" POLYGON ((35 35, 50 35, 50 10, 35 10, 35 25, 35 35))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		List<Geometry> all = new ArrayList<Geometry>();
		all.add(inputGeometry1);
		all.add(inputGeometry2);

		SplitBuilder builder = SplitBuilder.newInstansceUsingSplitLine(line);
		builder.buildEntireProcess(all);

		List<Geometry> result = builder.getSplitResult();

		analyzeOutput(result, expectedParts);

		expectedParts.clear();
		expectedParts.add(partC);

		result.clear();
		result = builder.getNeighbourResult();

		analyzeOutput(result, expectedParts);

	}

}
