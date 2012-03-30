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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Test using closed split Line
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
public class ClosedSplitLineTest {

    @Test
    public void testClosedLines() throws Exception {

	Geometry inputGeometry = (Geometry) SplitTestUtil
		.read("POLYGON ((20 25, 30 15, 30 5, 20 -5, 10 5, 10 15, 20 25),  (15 15, 15 5, 25 5, 25 15, 15 15))"); //$NON-NLS-1$

	LineString line = (LineString) SplitTestUtil
		.read("LINESTRING (15 15, 20 25, 25 15, 15 15)"); //$NON-NLS-1$
	Assert.assertTrue(line.isClosed());

	Geometry partA = SplitTestUtil
		.read("POLYGON ((20 25, 30 15, 30 5, 20 -5, 10 5, 10 15, 20 25),  (20 25, 15 15, 15 5, 25 5, 25 15, 20 25))"); //$NON-NLS-1$

	Geometry partB = SplitTestUtil
		.read("POLYGON ((15 15, 20 25, 25 15, 15 15))"); //$NON-NLS-1$

	List<Geometry> expectedParts = new ArrayList<Geometry>();
	expectedParts.add(partA);
	expectedParts.add(partB);

	SplitTestUtil.testSplitStrategy(inputGeometry, line, expectedParts);

    }
}
