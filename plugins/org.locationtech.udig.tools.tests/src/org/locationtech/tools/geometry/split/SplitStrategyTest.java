/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.tools.geometry.split;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.split.SplitStrategy;

/**
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
public class SplitStrategyTest {

	/**
	 * Tests the validity of splitting a <code>geometry</code> (polygon,
	 * multipolygon, linestring or multilinestring) with the given
	 * <code>splitter</code> <code>LineString</code>.
	 * <p>
	 * To test the validity the following assertiong are ran:
	 * <ul>
	 * <li>The resulting geometry is not <code>null</code>
	 * <li>The resulting geometry is an instance of GeometryCollection
	 * <li>The number of parts of the resulting geometry is equal to the length
	 * of the passed <code>expectedParts</code> array
	 * <li>The parts of the resulting geometry collection corresponds with the
	 * ones passed as <code>expectedParts</code>, in the given order.
	 * <li>The union of the resulting parts is equal to the original
	 * <code>splitee</code> geometry.
	 * </ul>
	 * </p>
	 * 
	 * @param splitee
	 *            the JTS polygon to be splitted by <code>splitter</code>
	 * @param splitter
	 *            the splitting line
	 * @param expectedParts
	 *            the parts expected, in order, non <code>null</code>
	 */
	private List<Geometry> testSplitResults(Geometry splitee, LineString splitter, Geometry[] expectedParts) {

		List<Geometry> expectedPartsList = new ArrayList<Geometry>();

		for (int i = 0; i < expectedParts.length; i++) {

			expectedPartsList.add(expectedParts[i]);
		}

		return SplitTestUtil.testSplitStrategy(splitee, splitter, expectedPartsList);
	}

	private List<Geometry> testSplitResults(Geometry splitee, LineString splitter, int expectedPartCount) {

		final List<Geometry> splitted = testSplitResults(splitee, splitter);

		assertEquals(splitted.toString(), expectedPartCount, splitted.size());

		return splitted;
	}

	private List<Geometry> testSplitResults(Geometry splitee, LineString splitter) {

		SplitStrategy strategy = new SplitStrategy(splitter);
		List<Geometry> splitted = strategy.split(splitee);

		assertNotNull(splitted);

		Geometry union = null;
		for (int i = 0; i < splitted.size(); i++) {
			Geometry part = splitted.get(i);
			if (union == null) {
				union = part;
			} else {
				union = union.union(part);
			}
		}

		Geometry symDifference = splitee.symDifference(union);
		if (splitee instanceof Polygon) {
			double maxTolerableAreaDiff = splitee.getArea() / 10000;
			double area = symDifference.getArea();
			assertTrue("difference area greater than maximum allowed: " + area + " > " + maxTolerableAreaDiff,
						area < maxTolerableAreaDiff);
		}

		return splitted;
	}

	/**
	 * Simple test, rectangle splitted by center vertical line
	 * 
	 * <pre>
	 * <code>
	 *                   .
	 *                  /|\
	 *                   |
	 *             +-----|------+
	 *             |     |      |
	 *             |     |      |
	 *             |     |      |
	 *             |     |      |
	 *             |     |      |
	 *             |_____|______|
	 *                   |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_SplitPolygonOnce() throws Exception {
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(30 0, 30 60)");
		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON((20 20, 40 20, 40 40, 20 40, 20 20))");
		Geometry expectedLeft = SplitTestUtil.read("POLYGON ((30 20, 20 20, 20 40, 30 40, 30 20))");
		Geometry expectedRight = SplitTestUtil.read("POLYGON ((30 40, 40 40, 40 20, 30 20, 30 40))");

		testSplitResults(splitee, splitter, new Geometry[] { expectedLeft, expectedRight });
	}

	/**
	 * <pre>
	 * <code>
	 *              +--------------+
	 *              |              |
	 *              |              |
	 *              |   +------+   |
	 *              |   |      |   |
	 *              |   |      |   |
	 *              |___|______|___|
	 *                  |      |
	 *                  |     \|/
	 *                         .
	 *                                 
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_CutoutBlock() throws Exception {
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(20 0, 20 30, 30 30, 30 0)");
		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON((10 10, 40 10, 40 40, 10 40, 10 10))");
		final Geometry expectedLeft = SplitTestUtil
					.read("POLYGON ((20 10, 10 10, 10 40, 40 40, 40 10, 30 10, 30 30, 30 30, 20 30, 20 30, 20 10))");
		final Geometry expectedRight = SplitTestUtil.read("POLYGON ((30 10, 20 10, 20 30, 30 30, 30 10))");
		testSplitResults(splitee, splitter, new Geometry[] { expectedLeft, expectedRight });
	}

	/**
	 * <pre>
	 * <code>
	 *                             .
	 *                            /|\
	 *              10,40          |20,40       40,40
	 *                  +----------|-----------+
	 *                  |          |           |
	 *                  |15,30     |20,30      |
	 *                  |    +-----------+30,30|
	 *                  |    |     |     |     |
	 *                  |    |     |     |     |
	 *                  |    |     |     |     |
	 *                  |    |_____|_____|30,15|
	 *                  | 15,15    |20,15      |
	 *                  |          |           |
	 *                  |__________|___________| 
	 *                10,10        |20,10      40,10
	 *                             |
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_SplitGeomWithHole() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON((10 10, 40 10, 40 40, 10 40, 10 10), (15 15, 15 30, 30 30, 30 15, 15 15))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(20 0, 20 22, 20 60)");
		Geometry expectedLeft = SplitTestUtil
					.read("POLYGON ((20 10, 10 10, 10 40, 20 40, 20 30, 15 30, 15 15, 20 15, 20 10))");
		Geometry expectedRight = SplitTestUtil
					.read("POLYGON ((20 40, 40 40, 40 10, 20 10, 20 15, 30 15, 30 30, 20 30, 20 40))");

		testSplitResults(splitee, splitter, new Geometry[] { expectedLeft, expectedRight });
	}

	/**
	 * <pre>
	 * <code>
	 *                 15,60
	 *                   .
	 *                  /|\
	 *                   |15,40
	 *            10,40+-|------------+40,40
	 *                 | | 30,30      |
	 *                 | |  +-----+30,30
	 *                 | |  |     |   |
	 *                 | |  |_____|   | 
	 *                 | | 20,20  30,20
	 *                 |_|____________|
	 *             10,10 |15,10       10,40
	 *                  15,0
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_SplitGeomWithHoleNoHoleBisection() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON((10 10, 40 10, 40 40, 10 40, 10 10), (20 20, 20 30, 30 30, 30 20, 20 20))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(15 0, 15 60)");
		Geometry expectedLeft = SplitTestUtil.read("POLYGON ((15 10, 10 10, 10 40, 15 40, 15 10))");
		Geometry expectedRight = SplitTestUtil
					.read("POLYGON ((15 40, 40 40, 40 10, 15 10, 15 40), (20 20, 30 20, 30 30, 20 30, 20 20))");

		testSplitResults(splitee, splitter, new Geometry[] { expectedLeft, expectedRight });
	}

	/**
	 * <pre>
	 * <code>
	 *                      .
	 *                     /|\
	 *                      |
	 *                   +--|-------+
	 *                   |  |       |
	 *                   |  |       |
	 *                   +--|--+    |
	 *                      | /     |
	 *                       /      |
	 *                      /       |
	 *                     /        |
	 *                    |         |
	 *                    o_\_______|
	 *                      /
	 *                            
	 * </code>
	 * </pre>
	 * 
	 * *
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon2() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((10 10, 40 10, 40 40, 10 40, 10 30, 20 30, 10 20, 10 10))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (15 30, 15 40)");

		Geometry expectedLeft = SplitTestUtil
					.read("POLYGON ((10 10, 40 10, 40 40, 15 40, 15 30, 20 30, 10 20, 10 10))");
		Geometry expectedRight = SplitTestUtil.read("POLYGON ((15 30, 10 30, 10 40, 15 40, 15 30))");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                    .
	 *                   /|\
	 *                    |
	 *                 +--|-------+
	 *                 |  |       |
	 *                 |  |       |
	 *                 +--|--+    |
	 *                    | /     |
	 *                    |/      |
	 *                    /       |
	 *                   /|       |
	 *                  | |       |
	 *                  |_|_______|
	 *                    |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_DoubleIntersection() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON((10 10, 40 10, 40 40, 10 40, 10 30, 20 30, 10 20, 10 10))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(15 0, 15 60)");

		Geometry expectedLeft = SplitTestUtil.read("POLYGON ((15 10, 10 10, 10 20, 15 25, 15 10))");
		Geometry expectedMiddle = SplitTestUtil
					.read("POLYGON ((15 40, 40 40, 40 10, 15 10, 15 25, 20 30, 15 30, 15 40))");
		Geometry expectedRight = SplitTestUtil.read("POLYGON ((15 30, 10 30, 10 40, 15 40, 15 30))");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedMiddle, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                    |
	 *                  +-|-------+
	 *                  | |       |
	 *                  |_|___    |
	 *                    |   |   |
	 *                   _|___|   |
	 *                  | |       |
	 *                  |_|_______|
	 *                    |     
	 *                   \|/
	 *                    . 
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_DoubleIntersectionReversedCut() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON((10 10, 40 10, 40 40, 10 40, 10 30, 20 30, 20 20, 10 20, 10 10))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(15 60, 15 0)");

		Geometry expectedLeft = SplitTestUtil.read("POLYGON ((15 10, 10 10, 10 20, 15 20, 15 10))");
		Geometry expectedMiddle = SplitTestUtil
					.read("POLYGON ((15 40, 40 40, 40 10, 15 10, 15 20, 20 20, 20 30, 15 30, 15 40))");
		Geometry expectedRight = SplitTestUtil.read("POLYGON ((15 30, 10 30, 10 40, 15 40, 15 30))");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedMiddle, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                                     5,4 
	 *          |                          /|\
	 *    0,3   |             3,3   4,3     |      6,3
	 *      ----|---------------      ------|-------
	 *      |   |              |      |     |      |
	 *      | C |              |      |     |      |
	 *      |___|____ 2,2      |      |     |      |
	 *     0,2  |    |         |      |     |      |
	 *          |    |         |      |     |      |
	 *          |    |   B     |      |  D  |  E   |
	 *     0,1  |    |2,1      |      |     |      |
	 *      ----|----+         |      |     |      |
	 *      |   |              |      |     |      |
	 *      | A |              |      |     |      |
	 *      |___|______________|      |_____|______|
	 *     0,0  |              3,0    4,0   |      6,0
	 *          |___________________________|
	 *          1,-1                       5,-1
	 * 
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitMultiPolygon() throws Exception {
		MultiPolygon splitee = (MultiPolygon) SplitTestUtil
					.read("MULTIPOLYGON(((0 0, 3 0, 3 3, 0 3, 0 2, 2 2, 2 1, 0 1, 0 0)), ((4 0, 6 0, 6 3, 4 3, 4 0)))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(1 5, 1 -1, 5 -1, 5 4)");

		Geometry partA = SplitTestUtil.read("POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))");
		Geometry partB = SplitTestUtil.read("POLYGON((1 0, 3 0, 3 3, 1 3, 1 2, 2 2, 2 1, 1 1, 1 0))");
		Geometry partC = SplitTestUtil.read("POLYGON((0 2, 1 2, 1 3, 0 3, 0 2))");
		Geometry partD = SplitTestUtil.read("POLYGON((4 0, 5 0, 5 3, 4 3, 4 0))");
		Geometry partE = SplitTestUtil.read("POLYGON((5 0, 6 0, 6 3, 5 3, 5 0))");

		Geometry[] expectedParts = { partA, partB, partC, partD, partE };

		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                 .
	 *                /|\
	 *                 |
	 *                 | +------------+
	 *                 | |            |
	 *                 | |            |
	 *                 | |            |
	 *                 | |            |
	 *                 | |            |
	 *                 | |____________|
	 *                 |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitPolygon_LineRidesShapeLine() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON((10.5 10, 20 10, 20 20, 10.5 20, 10.5 10))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(10 0, 10 30)");

		Geometry expected = SplitTestUtil.read("POLYGON((10.5 10, 20 10, 20 20, 10.5 20, 10.5 10))");

		assertTrue(splitee.equals(expected));

		Geometry[] expectedParts = new Geometry[] {};
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                     .
	 *                    /|\
	 *                     |
	 *                     |
	 *             +-------+
	 *             |       |
	 *             |       |
	 *             |       |
	 *             |       +-----+
	 *             |       |     |
	 *             |       |     |
	 *             |       |     |
	 *             |       |     |
	 *             +-------+++---+
	 *                     |     
	 *                     |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
    @Test
	public void testSplitPolygon_IntersectsVertexAndEdge() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON((10 10, 15.5 10, 15.7 10, 15.8 10, 20 10, 20 20, 15.5 20, 15.5 30, 10 30, 10 10))");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING(15.5 0, 15.5 40)");

		// with the next input it fails.
		// LineString splitter = (LineString)
		// SplitTestUtil.read("LINESTRING(15.5 40, 15.5 0)");

		Geometry expectedLeft = SplitTestUtil.read("POLYGON ((15.5 10, 10 10, 10 30, 15.5 30, 15.5 10))");
		Geometry expectedRight = SplitTestUtil
					.read("POLYGON ((15.5 20, 20 20, 20 10, 15.8 10, 15.7 10, 15.5 10, 15.5 20))");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                    .
	 *                   /|\
	 *                    |      
	 *                    |      
	 *              +-----+-----+
	 *                    |     
	 *                    |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitLine_simple() throws Exception {
		LineString splitee = (LineString) SplitTestUtil.read("LINESTRING (15.5 10, 10 10)");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (12.5 0, 12.5 40)");

		Geometry expectedLeft = SplitTestUtil.read("LINESTRING (15.5 10, 12.5 10)");
		Geometry expectedRight = SplitTestUtil.read("LINESTRING (12.5 10, 10 10)");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 *                         +
	 *                         |
	 *                         | 
	 *                         |          \
	 *                 --------+-----------}
	 *                 |       |          /  
	 *                 |       |      
	 *                 |       |
	 *           +-----+-------+
	 *                 |     
	 *                 |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitLineMultipleIntersections() throws Exception {
		LineString splitee = (LineString) SplitTestUtil.read("LINESTRING (10 10, 20 10, 20 20)");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (15 5, 15 15, 25 15)");

		Geometry expectedLeft = SplitTestUtil.read("LINESTRING (10 10, 15 10)");
		Geometry expectedMiddle = SplitTestUtil.read("LINESTRING (15 10, 20 10, 20 15)");
		Geometry expectedRight = SplitTestUtil.read("LINESTRING  (20 15, 20 20)");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedMiddle, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 * 
	 *           +-------------+
	 *          / \     
	 *           |     
	 *           |     
	 *           |     
	 *           |     
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitLineSharesVertex() throws Exception {
		LineString splitee = (LineString) SplitTestUtil.read("LINESTRING (15.5 10, 10 10)");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (10.5 0, 15.5 10)");

		Geometry expected = SplitTestUtil.read("LINESTRING (15.5 10, 10 10)");

		Geometry[] expectedParts = new Geometry[] { expected };
		testSplitResults(splitee, splitter, expectedParts);
	}

	/**
	 * <pre>
	 * <code>
	 * 
	 *            +     /
	 *            |    /
	 *            |   / 
	 *            |  /  
	 *            | /   
	 *            |/    
	 *            +------+
	 *           /
	 *          /
	 *        |/_
	 *        
	 * </code>
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitLineCutsOnVertex() throws Exception {
		LineString splitee = (LineString) SplitTestUtil.read("LINESTRING (20 10, 10 10, 10 20)");
		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (20 20, 0 0 )");

		Geometry expectedLeft = SplitTestUtil.read("LINESTRING (20 10, 10 10)");
		Geometry expectedRight = SplitTestUtil.read("LINESTRING (10 10, 10 20)");

		Geometry[] expectedParts = new Geometry[] { expectedLeft, expectedRight };
		testSplitResults(splitee, splitter, expectedParts);
	}

	@Test
	public void testSplitPolygon() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((484931.33221207117 4823429.360774391, 576441.460505905 4823429.360773954, 576441.4605056487 4770404.800438415, 484931.3322121216 4770404.800438844, 484931.33221207117 4823429.360774391))");
		LineString splitter = (LineString) SplitTestUtil
					.read("LINESTRING (518738.74733960454 4831558.988908185, 519056.4924999358 4763720.397139888, 526682.3763498047 4760066.327794093)");

		testSplitResults(splitee, splitter, 2);
	}

	@Test
	public void testSplitPolygon3() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((-2.05630239088747 43.383785592776206, -2.060964497966142 43.08091005152631,-2.4598370614233303 43.08348812503837, -2.5916373277716898 43.08403436779535, -3.1851208791748506 43.08461326221585,-3.1857959859927067 43.30753769431183, -3.185956334129664 43.36011198917018, -3.1860512944056665 43.39118019719231,-3.1860873520587085 43.402964186987994, -2.5261988548074474 43.561229494969545, -2.0535830120584198 43.55829389231849,-2.05630239088747 43.383785592776206))");
		LineString splitter = (LineString) SplitTestUtil
					.read("LINESTRING(-2.3129794071592786 43.629039402056485, -2.9465587625515224 42.99163177261353)");

		testSplitResults(splitee, splitter, 2);
	}

	private List<Geometry> testSplitResults2(Geometry splitee, LineString splitter, int expectedPartCount) {

		final List<Geometry> splitted = testSplitResults2(splitee, splitter);

		assertEquals(splitted.toString(), expectedPartCount, splitted.size());

		return splitted;
	}

	private List<Geometry> testSplitResults2(Geometry splitee, LineString splitter) {

		final SplitStrategy splitOp = new SplitStrategy(splitter);
		List<Geometry> geomSplit = new ArrayList<Geometry>();

		if (splitOp.canSplit(splitee)) {

			geomSplit = splitOp.split(splitee);

		}

		return geomSplit;
	}

	/**
	 * SPLITISSUES_20100208 EXAMPLE C. 2A-2B
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatingAPolygonWithHoleInBoundary() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((20 25, 10 15, 10 5, 20 -5, 30 5, 30 15, 20 25), (15 15, 25 15, 25 5, 15 5, 15 15))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (17 10, 15 15, 20 25, 25 15, 23 10)");

		Geometry partA = SplitTestUtil.read("POLYGON ((20 25, 15 15, 25 15, 20 25))");
		Geometry partB = SplitTestUtil
					.read("POLYGON ((10 5, 10 15, 20 25, 30 15, 30 5, 20 -5, 10 5),  (20 25, 15 15, 15 5, 25 5, 25 15, 20 25))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * SPLITISSUES_20100208 EXAMPLE C. 1A-1B
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatingAPolygonWithHoleInBoundary2() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((20 25, 10 15, 10 5, 20 -5, 30 5, 30 15, 20 25), (15 15, 25 15, 25 5, 15 5, 15 15))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (15 15, 20 25, 25 15, 15 15, 12 11)");

		Geometry partA = SplitTestUtil.read("POLYGON ((15 15, 20 25, 25 15, 15 15))");

		Geometry partB = SplitTestUtil
					.read("POLYGON ((20 25, 30 15, 30 5, 20 -5, 10 5, 10 15, 20 25),  (20 25, 15 15, 15 5, 25 5, 25 15, 20 25))");
		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);

	}

	/**
	 * SPLITISSUES_20100208 EXAMPLE A. 2A-2B
	 * 
	 * it'll be fixed when closed lines are supported.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntersectionEdgeSharedWithHullEdge() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((35 35, 60 35, 60 5, 35 5, 35 15, 45 15, 45 25, 35 25, 35 35))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (65 30, 45 25, 45 15, 65 10, 65 10, 65 10)");

		Geometry partA = SplitTestUtil.read("POLYGON ((60 28.75, 45 25, 45 15, 60 11.25, 60 28.75)) ");
		Geometry partB = SplitTestUtil.read("POLYGON ((35 5, 35 15, 45 15, 60 11.25, 60 5, 35 5))");
		Geometry partC = SplitTestUtil.read("POLYGON ((45 25, 35 25, 35 35, 60 35, 60 28.75, 45 25))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);
		expectedParts.add(partC);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * SPLITISSUES_0203_20100324 EXAMPLE 3
	 * 
	 * FIXED!
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFromInsideBorderHoleToHoleArea() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((30 50, 30 10, 100 10, 100 50, 30 50),  (40 40, 90 40, 90 20, 40 20, 40 40))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (40 20, 40 15, 50 15, 50 30, 50 30)");

		Geometry partA = SplitTestUtil.read("POLYGON ((40 20, 40 15, 50 15, 50 20, 40 20))");

		Geometry partB = SplitTestUtil
					.read("POLYGON ((30 10, 30 50, 100 50, 100 10, 30 10),   (50 20, 90 20, 90 40, 40 40, 40 20, 40 15, 50 15, 50 20))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * Intersection of a polygon with a 2 coordinate line. Each coordinate lies
	 * on the boundary of the polygon.
	 * 
	 * The expected result is the polygon divided into 2 fragments.
	 */
	@Test
	public void twoCoordinateLineBothCoordinateInBoundary() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON ((15 35, 40 35, 40 10, 15 10, 15 35))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (25 35, 25 10)");

		assertTrue(splitee.isValid());

		Geometry partA = SplitTestUtil.read(" POLYGON ((15 10, 15 35, 25 35, 25 10, 15 10))");

		Geometry partB = SplitTestUtil.read("POLYGON ((25 35, 25 10, 40 10, 40 35, 25 35))");

		List<Geometry> expectedParts = new ArrayList<Geometry>();
		expectedParts.add(partA);
		expectedParts.add(partB);

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * Intersection of a polygon (with U shape) with a 2 coordinate line. Each
	 * coordinate lies on the boundary of the polygon. But the line don't
	 * intersects the interior of the polygon, so there shouldn't be any split.
	 * 
	 * Expected result is no polygon.
	 */
	@Test
	public void twoCoordinateLineBothCoordinateInBoundary2() throws Exception {
		Polygon splitee = (Polygon) SplitTestUtil
					.read("POLYGON ((10 35, 40 35, 40 25, 30 25, 30 15, 40 15, 40 10, 10 10, 10 35))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (35 25, 35 15)");

		assertTrue(splitee.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * A line of 3 coordinates that starts and ends on the polygon boundary but
	 * it doesn't intersects with the interior.
	 */
	@Test
	public void threeCoordinateLineBothCoordinateInBoundary3() throws Exception {

		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (30 15, 35 20, 30 25, 30 25, 30 25)");

		assertTrue(splitee.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}

	/**
	 * A line of 2 coordinates that shares a piece of the polygon boundary. It
	 * only intersects with the boundary, doesn't intersects with the polygon
	 * interior.
	 */
	@Test
	public void twoCoordinateLineOverlapsBoundaryPart() throws Exception {

		Polygon splitee = (Polygon) SplitTestUtil.read("POLYGON ((10 30, 30 30, 30 10, 10 10, 10 30))");

		LineString splitter = (LineString) SplitTestUtil.read("LINESTRING (30 15, 30 25)");

		assertTrue(splitee.isValid());

		List<Geometry> expectedParts = new ArrayList<Geometry>();

		SplitTestUtil.testSplitStrategy(splitee, splitter, expectedParts);
	}



}
