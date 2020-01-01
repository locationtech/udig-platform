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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.split.SplitUtil;

/**
 * Test the SplitStrategy class
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class Testcatalog_20100414 {

    /**
     * 
     * Source: 1 Polygon feature
     * 
     * Result: 2 Polygon features, one of them inside the other.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_01() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (90 250, 170 250, 170 190, 90 190, 90 250)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270),  (90 250, 170 250, 170 190, 90 190, 90 250, 90 250, 90 250))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil.read("POLYGON ((90 250, 170 250, 170 190, 90 190, 90 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: The feature that will suffer the split operation is a Polygon feature with a hole.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_02() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270),  (90 250, 170 250, 170 190, 90 190, 90 250, 90 250, 90 250))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (120 210, 120 180, 90 180, 90 190)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 270, 70 170, 190 170, 190 270, 70 270),    (90 250, 90 180, 120 180, 120 190, 170 190, 170 250, 90 250))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil.read("POLYGON ((90 190, 120 190, 120 180, 90 180, 90 190))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: The feature that will suffer the split operation is a Polygon feature with a hole.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_02a() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270),  (90 250, 170 250, 170 190, 90 190, 90 250, 90 250, 90 250))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (140 220, 140 180, 170 180, 170 190)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270), (170 250, 170 180, 140 180, 140 190, 90 190, 90 250, 170 250))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((140 190, 170 190, 170 180, 140 180, 140 190))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: The feature that will suffer the split operation is a Polygon feature with a hole.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_03() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270),  (90 250, 170 250, 170 190, 90 190, 90 250, 90 250, 90 250))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (90 190, 90 180, 120 180, 120 220)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 270, 70 170, 190 170, 190 270, 70 270), (90 250, 90 180, 120 180, 120 190, 170 190, 170 250, 90 250))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil.read("POLYGON ((90 190, 120 190, 120 180, 90 180, 90 190))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: The feature that will suffer the split operation is a Polygon feature with a hole.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_03a() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270),  (90 250, 170 250, 170 190, 90 190, 90 250, 90 250, 90 250))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (170 190, 170 180, 140 180, 140 220)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 270, 190 270, 190 170, 70 170, 70 270), (170 250, 170 180, 140 180, 140 190, 90 190, 90 250, 170 250))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((140 190, 170 190, 170 180, 140 180, 140 190))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_04() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 230, 70 170, 190 170, 190 230, 170 230, 170 190, 160 190, 160 210, 100 210, 100 190, 90 190, 90 230, 70 230))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (100 190, 160 190, 160 190)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 170, 70 230, 90 230, 90 190, 100 190, 160 190, 170 190, 170 230, 190 230, 190 170, 70 170))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((100 210, 160 210, 160 190, 100 190, 100 210))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_05() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 230, 150 210, 170 210, 170 190, 90 190, 90 210, 110 210, 110 230, 110 230, 110 230)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 210, 90 210, 90 190, 170 190, 170 210, 150 210, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 270))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 210, 170 210, 170 190, 90 190, 90 210, 110 210, 110 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_06() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 230, 130 190, 110 230)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 130 190, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 270))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 230, 130 190, 110 230, 110 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * This feature also has 2 holes.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_07() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270),  (90 210, 110 210, 110 190, 90 190, 90 210),  (150 210, 170 210, 170 190, 150 190, 150 210))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (100 220, 110 230, 110 210, 150 210, 150 230, 160 220, 160 220)"); //$NON-NLS-1$

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 210, 150 210, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270), (90 210, 110 210, 110 190, 90 190, 90 210), (150 190, 150 210, 170 210, 170 190, 150 190))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 210, 110 210, 110 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * This feature also has 2 holes.
     * 
     * Result: 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_08() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270),  (90 210, 110 210, 110 190, 90 190, 90 210),  (150 210, 170 210, 170 190, 150 190, 150 210))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (100 240, 110 230, 110 210, 150 210, 150 230, 160 240, 160 240)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 210, 150 210, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270), (90 210, 110 210, 110 190, 90 190, 90 210), (150 190, 150 210, 170 210, 170 190, 150 190))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 210, 110 210, 110 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features but only one of them will suffer the split operation.
     * This feature also has 2 holes.
     * 
     * Result: 3 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_09() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270),  (90 210, 110 210, 110 190, 90 190, 90 210),  (150 210, 170 210, 170 190, 150 190, 150 210))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (110 210, 110 230, 150 230, 150 210, 110 210, 110 210, 110 210)"); //$NON-NLS-1$

        assertTrue(polygon.isValid());

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 210, 150 210, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270), (90 210, 110 210, 110 190, 90 190, 90 210), (150 190, 150 210, 170 210, 170 190, 150 190))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 230, 150 230, 150 210, 110 210, 110 230))"); //$NON-NLS-1$

        Geometry partC = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 230, 110 230, 110 230, 110 230, 110 250))"); //$NON-NLS-1$
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are at least 2 polygon features, one feature has 2 holes and the other feature
     * is inside one off those holes.
     * 
     * Result: The first feature is divided into 2 features and his 2 holes became into 1 hole. The
     * second feature is divided into 2 features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_10a1() throws Exception {

        // input
        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (120 230, 160 230, 170 220, 160 210, 120 210)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((70 260, 210 260, 210 180, 70 180, 70 180, 70 260), (90 230, 120 230, 120 210, 90 210, 90 230), (160 230, 190 230, 190 210, 160 210, 160 230))"); //$NON-NLS-1$

        // expected result
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((70 260, 210 260, 210 180, 70 180, 70 260),    (90 210, 190 210, 190 230, 90 230, 90 210))")); //$NON-NLS-1$
        expectedParts.add(SplitTestUtil
                .read("POLYGON ((120 230, 160 230, 160 210, 120 210, 120 230))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);

    }

    @Test
    public void testcatalog_10a2() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (120 230, 160 230, 170 220, 160 210, 120 210)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        // Test 2
        Polygon secondPolygon = (Polygon) SplitTestUtil
                .read("POLYGON ((160 230, 190 230, 190 210, 160 210, 160 230))"); //$NON-NLS-1$

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        Geometry partC = SplitTestUtil.read(" POLYGON ((160 230, 160 210, 170 220, 160 230))"); //$NON-NLS-1$

        Geometry partD = SplitTestUtil
                .read("POLYGON ((160 230, 190 230, 190 210, 160 210, 170 220, 160 230))"); //$NON-NLS-1$

        expectedParts2.add(partC);
        expectedParts2.add(partD);

        SplitTestUtil.testSplitStrategy(secondPolygon, splitLine, expectedParts2);
    }

    @Test
    public void testcatalog_10b1() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-41.3457000004128 337762.8442, -41.0771000003442 337768.4178, -42.71 337769.61, -44.1661000000313 337768.4178, -44.2330999998376 337762.8442)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((-44.0679000001401 337760.225400001, -43.3213999997824 337760.225400001, -41.0098999999464 337760.225400001, -41.1851000003517 337761.591600001, -41.3360000001267 337762.768300001, -41.3457000004128 337762.8442, -42.538200000301 337762.8442, -44.1235999995843 337762.8442, -44.2330999998376 337762.8442, -44.2330999998376 337760.225400001, -44.0679000001401 337760.225400001))"); //$NON-NLS-1$

        List<Geometry> emptyList = Collections.emptyList();

        SplitTestUtil.testSplitStrategy(polygon, splitLine, emptyList);
    }

    @Test
    public void testcatalog_10b2() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-41.3457000004128 337762.8442, -41.0771000003442 337768.4178, -42.71 337769.61, -44.1661000000313 337768.4178, -44.2330999998376 337762.8442)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((-36.7121999999508 337773.1855, -38.3289000000805 337773.1855, -42.6288999998942 337773.1855, -42.7030999995768 337773.1855, -47.7251000003889 337773.1855, -47.7251000003889 337769.0329, -47.7251000003889 337757.8079, -36.6452000001445 337757.8079, -36.6651999996975 337762.401900001, -36.6695999996737 337763.4022, -36.7023000000045 337770.906199999, -36.7121999999508 337773.1855),(-41.0889999996871 337768.4178, -44.1661000000313 337768.4178, -44.1929999999702 337769.658399999, -44.2330999998376 337771.5067, -42.5944999996573 337771.543099999, -41.2114000003785 337771.573899999, -41.1517000002787 337770.171399999, -41.0771000003442 337768.4178, -41.0889999996871 337768.4178),(-44.0679000001401 337760.225400001, -44.2330999998376 337760.225400001, -44.2330999998376 337762.8442, -44.1235999995843 337762.8442, -42.538200000301 337762.8442, -41.3457000004128 337762.8442, -41.3360000001267 337762.768300001, -41.1851000003517 337761.591600001, -41.0098999999464 337760.225400001, -43.3213999997824 337760.225400001, -44.0679000001401 337760.225400001))"); //$NON-NLS-1$

        List<Geometry> resultList = new LinkedList<Geometry>();
        resultList
                .add(SplitTestUtil
                        .read("POLYGON ((-41.3457000004128 337762.8442, -41.0771000003442 337768.4178, -41.0889999996871 337768.4178, -44.1661000000313 337768.4178, -44.2330999998376 337762.8442, -44.1235999995843 337762.8442, -42.538200000301 337762.8442, -41.3457000004128 337762.8442))")); //$NON-NLS-1$
        resultList
                .add(SplitTestUtil
                        .read("POLYGON ((-47.7251000003889 337757.8079, -47.7251000003889 337769.0329, -47.7251000003889 337773.1855, -42.7030999995768 337773.1855, -42.6288999998942 337773.1855, -38.3289000000805 337773.1855, -36.7121999999508 337773.1855, -36.7023000000045 337770.906199999, -36.6695999996737 337763.4022, -36.6651999996975 337762.401900001, -36.6452000001445 337757.8079, -47.7251000003889 337757.8079), (-44.2330999998376 337760.225400001, -44.0679000001401 337760.225400001, -43.3213999997824 337760.225400001, -41.0098999999464 337760.225400001, -41.1851000003517 337761.591600001, -41.3360000001267 337762.768300001, -41.3457000004128 337762.8442, -41.0771000003442 337768.4178, -41.1517000002787 337770.171399999, -41.2114000003785 337771.573899999, -42.5944999996573 337771.543099999, -44.2330999998376 337771.5067, -44.1929999999702 337769.658399999, -44.1661000000313 337768.4178, -44.2330999998376 337762.8442, -44.2330999998376 337760.225400001))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, resultList);
    }

    @Test
    public void testcatalog_10b3() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-41.3457000004128 337762.8442, -41.0771000003442 337768.4178, -42.71 337769.61, -44.1661000000313 337768.4178, -44.2330999998376 337762.8442)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((-44.2330999998376 337771.5067, -44.1929999999702 337769.658399999, -44.1661000000313 337768.4178, -41.0889999996871 337768.4178, -41.0771000003442 337768.4178, -41.1517000002787 337770.171399999, -41.2114000003785 337771.573899999, -42.5944999996573 337771.543099999, -44.2330999998376 337771.5067))"); //$NON-NLS-1$

        List<Geometry> resultList = new LinkedList<Geometry>();
        resultList
                .add(SplitTestUtil
                        .read("POLYGON ((-41.0771000003442 337768.4178, -42.71 337769.61, -44.1661000000313 337768.4178, -41.0889999996871 337768.4178, -41.0771000003442 337768.4178))")); //$NON-NLS-1$
        resultList
                .add(SplitTestUtil
                        .read("POLYGON ((-44.2330999998376 337771.5067, -42.5944999996573 337771.543099999, -41.2114000003785 337771.573899999, -41.1517000002787 337770.171399999, -41.0771000003442 337768.4178, -42.71 337769.61, -44.1661000000313 337768.4178, -44.1929999999702 337769.658399999, -44.2330999998376 337771.5067))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, resultList);
    }

    /**
     * Source: There are 2 polygon features, one feature has 2 holes.
     * 
     * Result: The first feature is divided into 3 features and one off his holes disappear.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_11() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 250, 150 250, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 270),  (90 210, 110 210, 110 190, 90 190, 90 210), (150 210, 170 210, 170 190, 150 190, 150 210))"); //$NON-NLS-1$
        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (100 200, 110 210, 110 230, 150 230, 150 210, 110 190)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 270, 90 270, 90 230, 110 230, 110 210, 90 210, 90 190, 110 190, 150 210, 150 230, 170 230, 170 270, 200 270, 200 170, 60 170, 60 170, 60 170, 60 270),  (170 210, 170 190, 150 190, 150 210, 170 210))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((110 210, 110 230, 150 230, 150 210, 110 190, 110 190, 110 190, 110 210))"); //$NON-NLS-1$

        Geometry partC = SplitTestUtil
                .read("POLYGON ((110 250, 150 250, 150 230, 110 230, 110 230, 110 230, 110 250))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features, one of them with one hole. This feature will suffer the
     * split operation.
     * 
     * Result: The feature will be divided into 4 features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_12() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((50 180, 240 180, 240 20, 50 20, 50 180), (100 140, 190 140, 190 80, 100 80, 100 140))"); //$NON-NLS-1$
        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (80 10, 80 100, 150 160, 200 110, 160 70, 160 10)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((160 20, 160 70, 170 80, 100 80, 100 117.14285714285714, 80 100, 80 20, 160 20))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((190 100, 200 110, 190 120, 190 100))"); //$NON-NLS-1$

        Geometry partC = SplitTestUtil
                .read("POLYGON ((170 140, 150 160, 126.66666666666667 140, 170 140))"); //$NON-NLS-1$

        Geometry partD = SplitTestUtil
                .read("POLYGON ((50 20, 50 180, 240 180, 240 20, 160 20, 160 70, 170 80, 190 80, 190 100, 200 110, 190 120, 190 140, 170 140, 150 160, 126.66666666666667 140, 100 140, 100 117.14285714285714, 80 100, 80 20, 50 20))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);
        expectedParts.add(partD);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 polygon features, one of them with one hole. This feature will suffer the
     * split operation.
     * 
     * Result: The feature will be divided into 2 features and its hole will disappear.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_13() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((40 185, 40 45, 195 45, 195 15, 265 15, 265 185, 40 185),  (110 150, 185 150, 185 90, 110 90, 110 150))"); //$NON-NLS-1$
        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (95 25, 95 130, 195 130, 195 45, 185 30)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((95 45, 95 130, 110 130, 110 150, 185 150, 185 130, 195 130, 195 15, 265 15, 265 185, 40 185, 40 45, 95 45))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((95 45, 95 130, 110 130, 110 90, 185 90, 185 130, 195 130, 195 45, 95 45))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are at least 1 Polygon feature with 3 holes and inside those holes there could
     * be some Polygon features (1-5).
     * 
     * Result: The feature with the holes will suffer the split operation and it will be divided
     * into 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_14() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((45 195, 255 195, 255 15, 45 15, 45 195),  (60 180, 240 180, 240 120, 195 120, 195 150, 105 150, 105 120, 60 120, 60 180),  (60 60, 105 60, 105 30, 60 30, 60 60),  (195 60, 240 60, 240 30, 195 30, 195 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (105 120, 105 60, 195 60, 195 120)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read(" POLYGON ((45 195, 255 195, 255 15, 45 15, 45 195),   (105 60, 60 60, 60 30, 105 30, 105 60),   (195 60, 240 60, 240 30, 195 30, 195 60),   (60 180, 60 120, 105 120, 105 60, 195 60, 195 120, 240 120, 240 180, 60 180))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read(" POLYGON ((105 150, 195 150, 195 60, 105 60, 105 60, 105 60, 105 60, 105 60, 105 60, 105 150))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are at least 1 Polygon feature with 3 holes and inside those holes there could
     * be some Polygon features (1-5).
     * 
     * Result: The feature with the holes will suffer the split operation and it will be divided
     * into 3 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_15() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((45 195, 255 195, 255 15, 45 15, 45 195),  (60 180, 240 180, 240 120, 195 120, 195 150, 105 150, 105 120, 60 120, 60 180),  (60 60, 105 60, 105 30, 60 30, 60 60),  (195 60, 240 60, 240 30, 195 30, 195 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (105 120, 195 120, 195 60, 105 60, 105 120, 105 120)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read(" POLYGON ((45 195, 255 195, 255 15, 45 15, 45 195),   (105 60, 60 60, 60 30, 105 30, 105 60),   (195 60, 240 60, 240 30, 195 30, 195 60),   (60 180, 60 120, 105 120, 105 60, 195 60, 195 120, 240 120, 240 180, 60 180))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read(" POLYGON ((105 150, 195 150, 195 120, 105 120, 105 150))"); //$NON-NLS-1$

        Geometry partC = SplitTestUtil
                .read(" POLYGON ((105 120, 195 120, 195 60, 105 60, 105 120))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is a Polygon feature with 2 holes.
     * 
     * Result: The feature will be divided into 2 features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_16() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 180, 240 180, 240 30, 60 30, 60 180),   (90 105, 135 105, 135 60, 90 60, 90 105),  (210 105, 165 105, 165 60, 210 60, 210 105))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (90 150, 135 105, 165 105, 210 150, 90 150)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read(" POLYGON ((60 180, 240 180, 240 30, 60 30, 60 180),  (90 105, 135 105, 135 60, 90 60, 90 105),  (165 105, 210 105, 210 60, 165 60, 165 105),  (90 150, 210 150, 165 105, 135 105, 90 150))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read(" POLYGON  ((90 150, 210 150, 165 105, 135 105, 90 150))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is a Polygon feature with 4 holes.
     * 
     * Result: The feature will be divided into 2 features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_17() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 180, 240 180, 240 30, 60 30, 60 180),  (75 165, 120 165, 120 120, 75 120, 75 165),  (225 165, 180 165, 180 120, 225 120, 225 120, 225 120, 225 120, 225 165),  (225 45, 180 45, 180 90, 225 90, 225 45),  (75 45, 75 90, 120 90, 120 45, 120 45, 75 45))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (105 75, 120 90, 120 120, 180 120, 180 90, 120 45, 105 75)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read(" POLYGON ((60 180, 60 180, 240 180, 240 30, 60 30, 60 180),  (75 165, 120 165, 120 120, 75 120, 75 165),  (180 165, 225 165, 225 120, 180 120, 180 165),  (180 90, 225 90, 225 45, 180 45, 180 90),   (120 90, 120 120, 180 120, 180 90, 120 45, 75 45, 75 90, 120 90))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((120 90, 120 120, 180 120, 180 90, 120 45, 120 90))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 3 Polygon features.
     * 
     * Result: 2 features will be divided into 5 polygon features and the other one remains
     * untouched.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_18() throws Exception {
        Polygon polygon1 = (Polygon) SplitTestUtil
                .read("POLYGON ((50 25, 50 100, 115 100, 115 25, 50 25))"); //$NON-NLS-1$

        Polygon polygon2 = (Polygon) SplitTestUtil
                .read("POLYGON ((50 100, 50 125, 140 125, 140 25, 115 25, 115 100, 50 100))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read(" LINESTRING (75 145, 75 60, 155 60)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        // from polygon 1
        Geometry partA = SplitTestUtil
                .read("POLYGON ((50 100, 75 100, 75 60, 115 60, 115 25, 50 25, 50 100))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil.read("POLYGON ((75 100, 115 100, 115 60, 75 60, 75 100))"); //$NON-NLS-1$

        // from polygon 2
        Geometry partC = SplitTestUtil
                .read("POLYGON ((50 100, 50 125, 75 125, 75 100, 75 100, 75 100, 50 100))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil
                .read("POLYGON ((75 125, 140 125, 140 60, 115 60, 115 100, 75 100, 75 125))"); //$NON-NLS-1$
        Geometry partE = SplitTestUtil.read("POLYGON ((115 60, 140 60, 140 25, 115 25, 115 60))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);

        SplitTestUtil.testSplitStrategy(polygon1, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partC);
        expectedParts2.add(partD);
        expectedParts2.add(partE);

        SplitTestUtil.testSplitStrategy(polygon2, splitLine, expectedParts2);
    }

    /**
     * Source: There are 4 Polygon features. One of then has a hole and inside it lies one of the
     * source polygon feature.
     * 
     * Result: The feature that has a hole will be divided into 5 polygon features and the others
     * remains untouched.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_19() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 40, 30 190, 60 190, 60 140, 90 140, 90 160, 150 160, 150 140, 180 140, 180 190, 210 190, 210 20, 150 20, 150 40, 30 40),   (50 70, 50 110, 80 110, 80 90, 70 90, 70 70, 50 70))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (180 10, 150 40, 70 70, 80 90, 150 140, 110 180)"); //$NON-NLS-1$
        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read(" POLYGON ((30 40, 150 40, 70 70, 50 70, 50 110, 80 110, 80 90, 150 140, 130 160, 90 160, 90 140, 60 140, 60 190, 30 190, 30 40))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((150 20, 150 40, 170 20, 170 20, 150 20))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil.read("POLYGON ((70 90, 70 70, 80 90, 80 90, 70 90))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil.read("POLYGON ((150 160, 130 160, 150 140, 150 160))"); //$NON-NLS-1$
        Geometry partE = SplitTestUtil
                .read("POLYGON ((210 20, 170 20, 150 40, 70 70, 80 90, 150 140, 180 140, 180 190, 210 190, 210 20))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);
        expectedParts.add(partD);
        expectedParts.add(partE);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 4 Polygon features. One of then has a hole and inside it lies one of the
     * source polygon feature.
     * 
     * Result: The feature that has a hole will be divided into 3 polygon features and the others
     * remains untouched.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_20() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 40, 30 190, 60 190, 60 140, 90 140, 90 160, 150 160, 150 140, 180 140, 180 190, 210 190, 210 20, 150 20, 150 40, 30 40),   (50 70, 50 110, 80 110, 80 90, 70 90, 70 70, 50 70))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 40, 70 70, 80 90, 150 140)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((30 40, 150 40, 70 70, 50 70, 50 110, 80 110, 80 90, 150 140, 150 160, 90 160, 90 140, 60 140, 60 190, 30 190, 30 40))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((70 90, 80 90, 70 70, 70 90))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil
                .read("POLYGON ((180 190, 210 190, 210 20, 150 20, 150 40, 70 70, 80 90, 150 140, 180 140, 180 190))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 4 Polygon features. One of then has a hole and inside it lies one of the
     * source polygon feature.
     * 
     * Result: The feature that has a hole will be divided into 4 polygon features and the others
     * remains untouched.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_21() throws Exception {

        // input
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 40, 30 190, 60 190, 60 140, 90 140, 90 160, 150 160, 150 140, 180 140, 180 190, 210 190, 210 20, 150 20, 150 40, 30 40),   (50 70, 50 110, 80 110, 80 90, 70 90, 70 70, 50 70))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 40, 70 70, 80 90, 150 140, 90 160)"); //$NON-NLS-1$

        // output
        assertFalse(SplitUtil.isClosedLine(splitLine));

        List<Geometry> expectedParts = new ArrayList<Geometry>();

        expectedParts
                .add(SplitTestUtil
                        .read(" POLYGON ((30 40, 30 190, 60 190, 60 140, 90 140, 90 160, 150 140, 80 90, 80 110, 50 110, 50 70, 70 70, 150 40, 30 40))")); //$NON-NLS-1$
        expectedParts.add(SplitTestUtil.read(" POLYGON ((70 70, 70 90, 80 90, 70 70))")); //$NON-NLS-1$
        expectedParts.add(SplitTestUtil.read("POLYGON ((150 160, 90 160, 150 140, 150 160))")); //$NON-NLS-1$
        expectedParts
                .add(SplitTestUtil
                        .read(" POLYGON ((180 190, 210 190, 210 20, 150 20, 150 40, 70 70, 80 90, 150 140, 180 140, 180 190))")); //$NON-NLS-1$

        // execute the split operation
        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes. (there could be features inside those
     * holes)
     * 
     * Result: The feature will be divided into 3 polygon features. (If there would be 2 features
     * inside the lower holes, those features inside the holes would also be divided, each one would
     * be divided into 2.)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_22() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 100, 170 100, 190 40, 60 50, 70 100)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((62 60, 70 100, 170 100, 183.33333333333334 60, 170 60, 170 41.53846153846154, 70 49.23076923076923, 70 60, 62 60))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((20 20, 20 180, 220 180, 220 20, 20 20), (40 30, 70 30, 70 49.23076923076923, 170 41.53846153846154, 170 30, 200 30, 200 60, 183.33333333333334 60, 170 100, 200 100, 200 160, 40 160, 40 100, 70 100, 62 60, 40 60, 40 30))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil.read("POLYGON ((70 100, 70 130, 170 130, 170 100, 70 100))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes. (there could be feature/s inside those
     * holes)
     * 
     * Result: The feature will be divided into 3 polygon features. (If there would be one feature
     * inside the upper hole, that feature inside the hole would also be divided into 3 features).
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_23() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 60, 170 60, 190 120, 50 110, 70 60)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((54 100, 70 100, 70 111.42857142857143, 170 118.57142857142857, 170 100, 183.33333333333334 100, 170 60, 70 60, 54 100))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((20 20, 20 180, 220 180, 220 20, 20 20), (40 30, 70 30, 70 60, 40 60, 40 30), (40 100, 54 100, 70 60, 170 60, 183.33333333333334 100, 200 100, 200 160, 40 160, 40 100), (170 30, 200 30, 200 60, 170 60, 170 30))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil
                .read("POLYGON ((70 111.42857142857143, 70 130, 170 130, 170 118.57142857142857, 70 111.42857142857143))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes.
     * 
     * Result: The feature will be divided into 2 polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_24() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (55 120, 70 60, 170 60, 185 120)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 30, 40 60, 70 60, 70 30, 40 30),  (170 30, 170 60, 200 60, 200 30, 170 30),  (40 160, 40 100, 60 100, 70 60, 170 60, 180 100, 200 100, 200 160, 40 160))"); //$NON-NLS-1$

        String wkt = "POLYGON ((70 130, 170 130, 170 100, 180 100, 170 60, 70 60, 60 100, 70 100, 70 130))"; //$NON-NLS-1$
        Geometry partB = SplitTestUtil.read(wkt);

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes. (there could be features inside those
     * holes)
     * 
     * Result: The feature will be divided into 2 polygon features. (If there would be 2 features
     * inside the lower holes, those features inside the holes would also be divided, each one would
     * be divided into 2.)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_25() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (50 120, 50 40, 190 40, 190 120)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("  POLYGON ((20 180, 20 20, 220 20, 220 180, 20 180),    (40 160, 40 100, 50 100, 50 60, 40 60, 40 30, 70 30, 70 40, 170 40, 170 30, 200 30, 200 60, 190 60, 190 100, 200 100, 200 160, 40 160))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((70 130, 170 130, 170 100, 190 100, 190 60, 170 60, 170 40, 70 40, 70 60, 50 60, 50 100, 70 100, 70 130))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes. (there could be features inside those
     * holes)
     * 
     * Result: The feature will be divided into 2 polygon features. (If there would be 2 features
     * inside the lower holes, those features inside the holes would also be divided, each one would
     * be divided into 2.)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_26() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 100, 55 40, 185 40, 170 100)"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((20 180, 20 20, 220 20, 220 180, 20 180),  (40 160, 40 100, 70 100, 60 60, 40 60, 40 30, 70 30, 70 40, 170 40, 170 30, 200 30, 200 60, 180 60, 170 100, 200 100, 200 160, 200 160, 40 160))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((70 130, 170 130, 170 100, 180 60, 170 60, 170 40, 70 40, 70 60, 60 60, 70 100, 70 130))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one polygon feature with 3 holes. (there could be features inside those
     * holes)
     * 
     * Result: The feature will be divided into 3 polygon features. (If there would be 2 features
     * inside the lower holes, those features inside the holes would also be divided, each one would
     * be divided into 2.)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_27() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((20 180, 220 180, 220 20, 20 20, 20 180),  (40 100, 40 160, 200 160, 200 100, 170 100, 170 130, 70 130, 70 100, 40 100),  (40 60, 70 60, 70 30, 40 30, 40 60),  (170 60, 200 60, 200 30, 170 30, 170 60))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (50 40, 190 40, 170 100, 70 100, 50 40)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((56.666666666666664 60, 70 100, 170 100, 183.33333333333334 60, 170 60, 170 40, 70 40, 70 60, 56.666666666666664 60))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((20 20, 20 180, 220 180, 220 20, 20 20), (40 30, 70 30, 70 40, 170 40, 170 30, 200 30, 200 60, 183.33333333333334 60, 170 100, 200 100, 200 160, 40 160, 40 100, 70 100, 56.666666666666664 60, 40 60, 40 30))"); //$NON-NLS-1$

        Geometry partC = SplitTestUtil
                .read(" POLYGON ((70 100, 70 130, 170 130, 170 100, 70 100))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one Polygon feature entirely surrounded by 2 Polygon features.
     * 
     * Result: The feature will be divided into 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_28() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((90 70, 90 130, 160 130, 160 70, 90 70))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (120 130, 120 100, 160 100)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((120 130, 90 130, 90 70, 160 70, 160 100, 120 100, 120 100, 120 100, 120 130))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((160 100, 120 100, 120 130, 160 130, 160 130, 160 100))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There is one Polygon feature entirely surrounded by 2 Polygon features.
     * 
     * Result: The feature will be divided into 2 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_29() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((90 70, 90 130, 160 130, 160 70, 90 70))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil.read("LINESTRING (120 130, 160 100)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((120 130, 160 100, 160 70, 90 70, 90 130, 120 130))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((160 130, 160 100, 120 130, 160 130))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 Polygon features.
     * 
     * Result: One feature will be divided into 2 Polygon features, the other remains intact.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_30() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 120, 70 120, 70 160, 100 160, 100 40, 220 40, 220 10, 30 10, 30 120))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (100 40, 50 40, 50 150)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("  POLYGON ((30 120, 50 120, 50 40, 220 40, 220 10, 30 10, 30 10, 30 10, 30 120))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((70 160, 100 160, 100 40, 50 40, 50 120, 70 120, 70 160))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 Polygon features.
     * 
     * Result: One feature will be divided into 2 Polygon features, the other remains intact.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_31() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 160, 30 40, 220 40, 220 160, 190 160, 190 70, 160 70, 160 110, 90 110, 90 70, 60 70, 60 160, 60 160, 30 160))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 100, 90 70, 160 70, 180 100)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((90 70, 160 70, 190 70, 190 160, 220 160, 220 40, 30 40, 30 160, 60 160, 60 70, 90 70, 90 70))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((90 70, 90 110, 160 110, 160 70, 90 70))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 Polygon features.
     * 
     * Result: One feature will be divided into 2 Polygon features, the other remains intact.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_32a() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 90, 90 70, 160 70, 180 50)"); //$NON-NLS-1$

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 160, 30 40, 220 40, 220 160, 190 160, 190 70, 160 70, 160 110, 90 110, 90 70, 60 70, 60 160, 60 160, 30 160))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((90 70, 160 70, 190 70, 190 160, 220 160, 220 40, 30 40, 30 160, 60 160, 60 70, 90 70, 90 70))")); //$NON-NLS-1$);
        expectedParts.add(SplitTestUtil.read("POLYGON ((90 70, 90 110, 160 110, 160 70, 90 70))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Like 32 a but the split line start and end in the vertex shared by the polygons.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_32b() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (462.842600000091 338094.383199999, 458.32880000025 338099.2083)"); //$NON-NLS-1$

        // first split return two polygons
        Polygon polygonToSplit = (Polygon) SplitTestUtil
                .read("POLYGON ((458.32880000025 338099.2083, 456.276200000197 338101.2609, 456.201600000262 338101.3355, 457.674599999562 338102.760199999, 459.248499999754 338104.282500001, 458.579499999993 338104.923599999, 457.342899999581 338106.1087, 453.403400000185 338102.2171, 448.834300000221 338097.7037, 449.043899999931 338097.488700001, 451.54629999958 338094.922499999, 460.372600000352 338085.8708, 460.498200000264 338085.742000001, 461.026700000279 338085.199999999, 461.053100000136 338085.227, 463.059100000188 338087.2805, 466.095499999821 338090.388800001, 468.649699999951 338093.0035, 469.794800000265 338094.1757, 468.959300000221 338094.976299999, 468.464999999851 338095.449999999, 465.07349999994 338092.1523, 462.842600000091 338094.383199999, 464.087799999863 338095.524599999, 460.143299999647 338099.469, 459.366299999878 338100.2459, 458.32880000025 338099.2083))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((462.842600000091 338094.383199999, 458.32880000025 338099.2083, 456.276200000197 338101.2609, 456.201600000262 338101.3355, 457.674599999562 338102.760199999, 459.248499999754 338104.282500001, 458.579499999993 338104.923599999, 457.342899999581 338106.1087, 453.403400000185 338102.2171, 448.834300000221 338097.7037, 449.043899999931 338097.488700001, 451.54629999958 338094.922499999, 460.372600000352 338085.8708, 460.498200000264 338085.742000001, 461.026700000279 338085.199999999, 461.053100000136 338085.227, 463.059100000188 338087.2805, 466.095499999821 338090.388800001, 468.649699999951 338093.0035, 469.794800000265 338094.1757, 468.959300000221 338094.976299999, 468.464999999851 338095.449999999, 465.07349999994 338092.1523, 462.842600000091 338094.383199999))")); //$NON-NLS-1$
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((458.32880000025 338099.2083, 459.366299999878 338100.2459, 460.143299999647 338099.469, 464.087799999863 338095.524599999, 462.842600000091 338094.383199999, 458.32880000025 338099.2083))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygonToSplit, splitLine, expectedParts);

        // the second split process has not effect in neighbor polygon
        Polygon neigbour = (Polygon) SplitTestUtil
                .read("POLYGON ((456.201600000262 338101.3355, 456.276200000197 338101.2609, 458.32880000025 338099.2083, 459.366299999878 338100.2459, 460.143299999647 338099.469, 464.087799999863 338095.524599999, 462.842600000091 338094.383199999, 465.07349999994 338092.1523, 468.464999999851 338095.449999999, 462.821399999782 338100.8585, 460.242700000294 338103.329700001, 459.248499999754 338104.282500001, 457.674599999562 338102.760199999, 456.201600000262 338101.3355))"); //$NON-NLS-1$

        List<Geometry> emptyList = Collections.emptyList();

        SplitTestUtil.testSplitStrategy(neigbour, splitLine, emptyList);
    }

    @Test
    public void testcatalog_32c() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (462.476 338091.36, 462.842600000091 338094.383199999, 458.32880000025 338099.2083, 458.409 338101.61)"); //$NON-NLS-1$

        // first split return two polygons
        Polygon polygonToSplit = (Polygon) SplitTestUtil
                .read("POLYGON ((458.32880000025 338099.2083, 456.276200000197 338101.2609, 456.201600000262 338101.3355, 457.674599999562 338102.760199999, 459.248499999754 338104.282500001, 458.579499999993 338104.923599999, 457.342899999581 338106.1087, 453.403400000185 338102.2171, 448.834300000221 338097.7037, 449.043899999931 338097.488700001, 451.54629999958 338094.922499999, 460.372600000352 338085.8708, 460.498200000264 338085.742000001, 461.026700000279 338085.199999999, 461.053100000136 338085.227, 463.059100000188 338087.2805, 466.095499999821 338090.388800001, 468.649699999951 338093.0035, 469.794800000265 338094.1757, 468.959300000221 338094.976299999, 468.464999999851 338095.449999999, 465.07349999994 338092.1523, 462.842600000091 338094.383199999, 464.087799999863 338095.524599999, 460.143299999647 338099.469, 459.366299999878 338100.2459, 458.32880000025 338099.2083))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((462.842600000091 338094.383199999, 458.32880000025 338099.2083, 456.276200000197 338101.2609, 456.201600000262 338101.3355, 457.674599999562 338102.760199999, 459.248499999754 338104.282500001, 458.579499999993 338104.923599999, 457.342899999581 338106.1087, 453.403400000185 338102.2171, 448.834300000221 338097.7037, 449.043899999931 338097.488700001, 451.54629999958 338094.922499999, 460.372600000352 338085.8708, 460.498200000264 338085.742000001, 461.026700000279 338085.199999999, 461.053100000136 338085.227, 463.059100000188 338087.2805, 466.095499999821 338090.388800001, 468.649699999951 338093.0035, 469.794800000265 338094.1757, 468.959300000221 338094.976299999, 468.464999999851 338095.449999999, 465.07349999994 338092.1523, 462.842600000091 338094.383199999))")); //$NON-NLS-1$
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((458.32880000025 338099.2083, 459.366299999878 338100.2459, 460.143299999647 338099.469, 464.087799999863 338095.524599999, 462.842600000091 338094.383199999, 458.32880000025 338099.2083))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygonToSplit, splitLine, expectedParts);

        // the second split process has not effect in neighbor polygon
        Polygon neigbour = (Polygon) SplitTestUtil
                .read("POLYGON ((456.201600000262 338101.3355, 456.276200000197 338101.2609, 458.32880000025 338099.2083, 459.366299999878 338100.2459, 460.143299999647 338099.469, 464.087799999863 338095.524599999, 462.842600000091 338094.383199999, 465.07349999994 338092.1523, 468.464999999851 338095.449999999, 462.821399999782 338100.8585, 460.242700000294 338103.329700001, 459.248499999754 338104.282500001, 457.674599999562 338102.760199999, 456.201600000262 338101.3355))"); //$NON-NLS-1$

        List<Geometry> emptyList = Collections.emptyList();

        SplitTestUtil.testSplitStrategy(neigbour, splitLine, emptyList);
    }

    /**
     * Source: There are 2 Polygon features.
     * 
     * Result: One feature will be divided into 2 Polygon features, the other remains intact.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_33() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 160, 30 40, 220 40, 220 160, 190 160, 190 70, 160 70, 160 110, 90 110, 90 70, 60 70, 60 160, 60 160, 30 160))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 50, 90 70, 160 70, 180 50)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((90 70, 160 70, 190 70, 190 160, 220 160, 220 40, 30 40, 30 160, 60 160, 60 70, 90 70, 90 70))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((90 70, 90 110, 160 110, 160 70, 90 70))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 Polygon features.
     * 
     * Result: One feature will be divided into 2 Polygon features, the other remains intact.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_34() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((30 30, 30 90, 220 90, 220 30, 30 30))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 120, 70 60, 180 60, 180 120)"); //$NON-NLS-1$

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((70 90, 30 90, 30 30, 220 30, 220 90, 180 90, 180 60, 70 60, 70 90))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read("POLYGON ((70 90, 180 90, 180 60, 70 60, 70 90))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 2 Polygon features. One has a hole and the other lies inside that hole.
     * 
     * Result: The feature with the hole will be split into 5 features and the other feature will
     * also be split into 5 features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_35() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 60, 60 160, 160 160, 160 60, 60 60),  (90 130, 130 130, 130 90, 90 90, 90 130))"); //$NON-NLS-1$

        Polygon polygon2 = (Polygon) SplitTestUtil
                .read("POLYGON ((90 130, 130 130, 130 90, 90 90, 90 130))"); //$NON-NLS-1$
        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (110 140, 80 110, 110 80, 140 110, 110 140)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        // result of doing split operation with the next input: Polygon
        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 160, 160 160, 160 60, 60 60, 60 160),    (80 110, 90 100, 90 90, 100 90, 110 80, 120 90, 130 90, 130 100, 140 110, 130 120, 130 130, 120 130, 110 140, 100 130, 90 130, 90 120, 80 110))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil.read(" POLYGON  ((90 120, 90 100, 80 110, 90 120))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil.read(" POLYGON ((100 90, 120 90, 110 80, 100 90))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil.read(" POLYGON ((130 100, 130 120, 140 110, 130 100))"); //$NON-NLS-1$
        Geometry partE = SplitTestUtil.read(" POLYGON ((100 130, 120 130, 110 140, 100 130))"); //$NON-NLS-1$

        // result of doing split operation with the next input: splitte2 Polygon
        Geometry partF = SplitTestUtil
                .read(" POLYGON ((90 100, 90 120, 100 130, 120 130, 130 120, 130 100, 120 90, 100 90, 90 100))"); //$NON-NLS-1$
        Geometry partG = SplitTestUtil.read(" POLYGON ((90 100, 100 90, 90 90, 90 100))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil.read(" POLYGON ((120 90, 130 100, 130 90, 120 90))"); //$NON-NLS-1$
        Geometry partI = SplitTestUtil.read(" POLYGON ((90 130, 100 130, 90 120, 90 130))"); //$NON-NLS-1$
        Geometry partJ = SplitTestUtil.read(" POLYGON ((120 130, 130 130, 130 120, 120 130))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);
        expectedParts1.add(partC);
        expectedParts1.add(partD);
        expectedParts1.add(partE);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partF);
        expectedParts2.add(partG);
        expectedParts2.add(partH);
        expectedParts2.add(partI);
        expectedParts2.add(partJ);

        SplitTestUtil.testSplitStrategy(polygon2, splitLine, expectedParts2);
    }

    /**
     * Test 36a Interior Polygon contained in polygon tested in 36b
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_36a() throws Exception {

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 120, 110 150, 150 110, 110 70, 70 100)"); //$NON-NLS-1$

        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((80 140, 140 140, 140 80, 80 80, 80 140))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        Geometry partE = SplitTestUtil.read("POLYGON ((140 100, 140 80, 120 80, 140 100))"); //$NON-NLS-1$
        Geometry partF = SplitTestUtil.read("POLYGON ((120 140, 140 140, 140 120, 120 140))"); //$NON-NLS-1$
        Geometry partG = SplitTestUtil
                .read("POLYGON ((80 127.5, 80 140, 96.66666666666667 140, 80 127.5))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil
                .read("POLYGON ((80 80, 80 92.5, 96.66666666666667 80, 80 80))"); //$NON-NLS-1$
        Geometry partI = SplitTestUtil
                .read("POLYGON ((80 92.5, 96.66666666666667 80, 120 80, 140 100, 140 120, 120 140, 96.66666666666667 140, 80 127.5, 80 92.5))"); //$NON-NLS-1$

        expectedParts.add(partE);
        expectedParts.add(partF);
        expectedParts.add(partG);
        expectedParts.add(partH);
        expectedParts.add(partI);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Test 36b: Exterior Polygon with a hole that contains the polygon tested 36a
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_36b() throws Exception {
        Polygon polygonToSplit = (Polygon) SplitTestUtil
                .read("POLYGON ((60 60, 60 160, 160 160, 160 60, 60 60), (80 140, 140 140, 140 80, 80 80, 80 140))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (70 120, 110 150, 150 110, 110 70, 70 100)"); //$NON-NLS-1$

        // result of doing split operation with the next input: splitte2 Polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        Geometry partA = SplitTestUtil
                .read("POLYGON ((60 60, 60 160, 160 160, 160 60, 60 60), (80 80, 96.66666666666667 80, 110 70, 120 80, 140 80, 140 100, 150 110, 140 120, 140 140, 120 140, 110 150, 96.66666666666667 140, 80 140, 80 127.5, 80 92.5, 80 80))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read(" POLYGON ((96.66666666666667 80, 110 70, 120 80, 96.66666666666667 80))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil
                .read(" POLYGON ((120 140, 110 150, 96.66666666666667 140, 120 140))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil.read(" POLYGON ((140 100, 150 110, 140 120, 140 100))"); //$NON-NLS-1$

        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);
        expectedParts.add(partD);

        SplitTestUtil.testSplitStrategy(polygonToSplit, splitLine, expectedParts);
    }

    /**
     * Source: There is Polygon feature with 'U' shape.
     * 
     * Result: The feature will be divided into 3 Polygon features.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_37() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((60 160, 60 130, 100 130, 100 100, 60 100, 60 70, 140 70, 140 160, 60 160))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (156 79, 100 100, 100 130, 156 144)"); //$NON-NLS-1$

        assertTrue(polygon.isValid());

        assertFalse(SplitUtil.isClosedLine(splitLine));

        Geometry partA = SplitTestUtil
                .read("POLYGON ((100 100, 60 100, 60 70, 140 70, 140 85, 100 100))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((100 130, 100 100, 140 85, 140 140, 100 130))"); //$NON-NLS-1$
        Geometry partC = SplitTestUtil
                .read("POLYGON ((140 140, 140 160, 60 160, 60 130, 100 130, 140 140))"); //$NON-NLS-1$

        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts.add(partA);
        expectedParts.add(partB);
        expectedParts.add(partC);

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Source: There are 4 Polygon features.
     * 
     * Result: Each feature will be divided into 2 Polygon features, so the total features will be
     * 8.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_38() throws Exception {
        Polygon polygon1 = (Polygon) SplitTestUtil
                .read("POLYGON ((120 300, 120 220, 200 220, 200 300, 120 300))"); //$NON-NLS-1$
        Polygon polygon2 = (Polygon) SplitTestUtil
                .read("POLYGON  ((200 300, 280 300, 280 220, 200 220, 200 300))"); //$NON-NLS-1$
        Polygon polygon3 = (Polygon) SplitTestUtil
                .read("POLYGON ((120 220, 120 140, 200 140, 200 220, 120 220))"); //$NON-NLS-1$
        Polygon polygon4 = (Polygon) SplitTestUtil
                .read("POLYGON  ((200 140, 280 140, 280 220, 200 220, 200 140))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 250, 240 270, 260 180, 150 160, 150 250)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        // from polygon
        Geometry partA = SplitTestUtil
                .read("POLYGON ((120 220, 120 300, 200 300, 200 261.1111111111111, 150 250, 150 220, 120 220))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((150 220, 150 250, 200 261.1111111111111, 200 220, 150 220))"); //$NON-NLS-1$

        // from splitte2
        Geometry partC = SplitTestUtil
                .read("POLYGON ((200 220, 200 261.1111111111111, 240 270, 251.11111111111111 220, 200 220))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil
                .read(" POLYGON ((200 261.1111111111111, 200 300, 280 300, 280 220, 251.11111111111111 220, 240 270, 200 261.1111111111111))"); //$NON-NLS-1$

        // from splitte3
        Geometry partE = SplitTestUtil
                .read("POLYGON ((150 160, 150 220, 200 220, 200 169.0909090909091, 150 160))"); //$NON-NLS-1$
        Geometry partF = SplitTestUtil
                .read(" POLYGON ((120 140, 120 220, 150 220, 150 160, 200 169.0909090909091, 200 140, 120 140))"); //$NON-NLS-1$

        // from splitte4
        Geometry partG = SplitTestUtil
                .read(" POLYGON ((200 169.0909090909091, 200 220, 251.11111111111111 220, 260 180, 200 169.0909090909091))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil
                .read(" POLYGON ((200 140, 200 169.0909090909091, 260 180, 251.11111111111111 220, 280 220, 280 140, 200 140))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);
        SplitTestUtil.testSplitStrategy(polygon1, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partC);
        expectedParts2.add(partD);

        SplitTestUtil.testSplitStrategy(polygon2, splitLine, expectedParts2);

        List<Geometry> expectedParts3 = new ArrayList<Geometry>();
        expectedParts3.add(partE);
        expectedParts3.add(partF);

        SplitTestUtil.testSplitStrategy(polygon3, splitLine, expectedParts3);

        List<Geometry> expectedParts4 = new ArrayList<Geometry>();
        expectedParts4.add(partG);
        expectedParts4.add(partH);

        SplitTestUtil.testSplitStrategy(polygon4, splitLine, expectedParts4);
    }

    /**
     * Source: There are 4 Polygon features.
     * 
     * Result: Each feature will be divided into 2 Polygon features, so the total features will be
     * 8.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_38a() throws Exception {
        Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((120 300, 120 220, 200 220, 200 300, 120 300))"); //$NON-NLS-1$
        Polygon polygon2 = (Polygon) SplitTestUtil
                .read("POLYGON  ((200 300, 280 300, 280 220, 200 220, 200 300))"); //$NON-NLS-1$
        Polygon polygon3 = (Polygon) SplitTestUtil
                .read("POLYGON ((120 220, 120 140, 200 140, 200 220, 120 220))"); //$NON-NLS-1$
        Polygon polygon4 = (Polygon) SplitTestUtil
                .read("POLYGON  ((200 140, 280 140, 280 220, 200 220, 200 140))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (150 250, 150 160, 260 180, 240 270, 150 250)"); //$NON-NLS-1$
        assertTrue(SplitUtil.isClosedLine(splitLine));

        // split result 1
        Geometry partA = SplitTestUtil
                .read("POLYGON ((120 220, 120 300, 200 300, 200 261.1111111111111, 150 250, 150 220, 120 220))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((150 220, 150 250, 200 261.1111111111111, 200 220, 150 220))"); //$NON-NLS-1$

        // split result 2
        Geometry partC = SplitTestUtil
                .read(" POLYGON ((200 261.1111111111111, 200 300, 280 300, 280 220, 251.11111111111111 220, 240 270, 200 261.1111111111111))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil
                .read(" POLYGON ((200 220, 200 261.1111111111111, 240 270, 251.11111111111111 220, 200 220))"); //$NON-NLS-1$

        // split result 3
        Geometry partE = SplitTestUtil
                .read("POLYGON ((120 140, 120 220, 150 220, 150 160, 200 169.0909090909091, 200 140, 120 140))"); //$NON-NLS-1$
        Geometry partF = SplitTestUtil
                .read(" POLYGON ((150 160, 150 220, 200 220, 200 169.0909090909091, 150 160))"); //$NON-NLS-1$

        // split result 4
        Geometry partG = SplitTestUtil
                .read("POLYGON ((200 169.0909090909091, 200 220, 251.11111111111111 220, 260 180, 200 169.0909090909091))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil
                .read(" POLYGON ((200 140, 200 169.0909090909091, 260 180, 251.11111111111111 220, 280 220, 280 140, 200 140))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);
        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partC);
        expectedParts2.add(partD);
        SplitTestUtil.testSplitStrategy(polygon2, splitLine, expectedParts2);

        List<Geometry> expectedParts3 = new ArrayList<Geometry>();
        expectedParts3.add(partE);
        expectedParts3.add(partF);
        SplitTestUtil.testSplitStrategy(polygon3, splitLine, expectedParts3);

        List<Geometry> expectedParts4 = new ArrayList<Geometry>();
        expectedParts4.add(partG);
        expectedParts4.add(partH);

        SplitTestUtil.testSplitStrategy(polygon4, splitLine, expectedParts4);
    }

    /**
     * Source: There are 4 Polygon features.
     * 
     * Result: Each feature will be divided into 2 Polygon features, so the total features will be
     * 8.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_39() throws Exception {
        Polygon polygon1 = (Polygon) SplitTestUtil
                .read("POLYGON ((120 280, 210 280, 210 230, 160 230, 160 200, 120 200, 120 280))"); //$NON-NLS-1$
        Polygon polygon2 = (Polygon) SplitTestUtil
                .read("POLYGON  ((210 230, 210 280, 330 280, 330 180, 260 180, 260 230, 210 230))"); //$NON-NLS-1$
        Polygon polygon3 = (Polygon) SplitTestUtil
                .read("POLYGON ((180 160, 180 120, 120 120, 120 200, 160 200, 160 160, 180 160))"); //$NON-NLS-1$
        Polygon polygon4 = (Polygon) SplitTestUtil
                .read("POLYGON  ((180 160, 260 160, 260 180, 330 180, 330 120, 180 120, 180 160))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (140 240, 300 260, 280 150, 160 130, 140 240)"); //$NON-NLS-1$
        assertTrue(SplitUtil.isClosedLine(splitLine));

        // split result 1
        Geometry partA = SplitTestUtil
                .read("POLYGON ((120 200, 120 280, 210 280, 210 248.75, 140 240, 147.27272727272728 200, 120 200))"); //$NON-NLS-1$
        Geometry partB = SplitTestUtil
                .read("POLYGON ((140 240, 210 248.75, 210 230, 160 230, 160 200, 147.27272727272728 200, 140 240))"); //$NON-NLS-1$

        // split result 2
        Geometry partC = SplitTestUtil
                .read("POLYGON ((210 230, 210 248.75, 300 260, 285.45454545454544 180, 260 180, 260 230, 210 230))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil
                .read("POLYGON ((210 248.75, 210 280, 330 280, 330 180, 285.45454545454544 180, 300 260, 210 248.75))"); //$NON-NLS-1$

        // split result 3
        Geometry partE = SplitTestUtil
                .read(" POLYGON ((120 120, 120 200, 147.27272727272728 200, 160 130, 180 133.33333333333334, 180 120, 120 120))"); //$NON-NLS-1$
        Geometry partF = SplitTestUtil
                .read(" POLYGON ((147.27272727272728 200, 160 200, 160 160, 180 160, 180 133.33333333333334, 160 130, 147.27272727272728 200))"); //$NON-NLS-1$

        // split result 4
        Geometry partG = SplitTestUtil
                .read(" POLYGON ((180 120, 180 133.33333333333334, 280 150, 285.45454545454544 180, 330 180, 330 120, 180 120))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil
                .read(" POLYGON ((180 133.33333333333334, 180 160, 260 160, 260 180, 285.45454545454544 180, 280 150, 180 133.33333333333334))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);
        SplitTestUtil.testSplitStrategy(polygon1, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partC);
        expectedParts2.add(partD);

        SplitTestUtil.testSplitStrategy(polygon2, splitLine, expectedParts2);

        List<Geometry> expectedParts3 = new ArrayList<Geometry>();
        expectedParts3.add(partE);
        expectedParts3.add(partF);

        SplitTestUtil.testSplitStrategy(polygon3, splitLine, expectedParts3);

        List<Geometry> expectedParts4 = new ArrayList<Geometry>();
        expectedParts4.add(partG);
        expectedParts4.add(partH);

        SplitTestUtil.testSplitStrategy(polygon4, splitLine, expectedParts4);
    }

    /**
     * Source: There are 4 Polygon features.
     * 
     * Result: Each feature will be divided into 2 Polygon features, so the total features will be
     * 8.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_39a() throws Exception {

        Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((120 280, 210 280, 210 230, 160 230, 160 200, 120 200, 120 280))"); //$NON-NLS-1$
        Polygon geomToSplit2 = (Polygon) SplitTestUtil
                .read("POLYGON  ((210 230, 210 280, 330 280, 330 180, 260 180, 260 230, 210 230))"); //$NON-NLS-1$
        Polygon geomToSplit3 = (Polygon) SplitTestUtil
                .read("POLYGON ((180 160, 180 120, 120 120, 120 200, 160 200, 160 160, 180 160))"); //$NON-NLS-1$
        Polygon geomToSplit4 = (Polygon) SplitTestUtil
                .read("POLYGON  ((180 160, 260 160, 260 180, 330 180, 330 120, 180 120, 180 160))"); //$NON-NLS-1$

        LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (140 240, 160 130, 280 150, 300 260, 140 240)"); //$NON-NLS-1$

        assertTrue(SplitUtil.isClosedLine(splitLine));

        // expected from split1
        Geometry partA = SplitTestUtil
                .read("POLYGON ((140 240, 210 248.75, 210 230, 160 230, 160 200, 147.27272727272728 200, 140 240))"); //$NON-NLS-1$

        Geometry partB = SplitTestUtil
                .read("POLYGON ((120 200, 120 280, 210 280, 210 248.75, 140 240, 147.27272727272728 200, 120 200))"); //$NON-NLS-1$

        // expected from split2
        Geometry partC = SplitTestUtil
                .read("POLYGON ((210 230, 210 248.75, 300 260, 285.45454545454544 180, 260 180, 260 230, 210 230))"); //$NON-NLS-1$
        Geometry partD = SplitTestUtil
                .read("POLYGON ((210 248.75, 210 280, 330 280, 330 180, 285.45454545454544 180, 300 260, 210 248.75))"); //$NON-NLS-1$

        // expected from split3
        Geometry partE = SplitTestUtil
                .read(" POLYGON ((147.27272727272728 200, 160 200, 160 160, 180 160, 180 133.33333333333334, 160 130, 147.27272727272728 200))"); //$NON-NLS-1$
        Geometry partF = SplitTestUtil
                .read("POLYGON ((120 120, 120 200, 147.27272727272728 200, 160 130, 180 133.33333333333334, 180 120, 120 120))"); //$NON-NLS-1$

        // expected from split4
        Geometry partG = SplitTestUtil
                .read(" POLYGON ((180 133.33333333333334, 180 160, 260 160, 260 180, 285.45454545454544 180, 280 150, 180 133.33333333333334))"); //$NON-NLS-1$
        Geometry partH = SplitTestUtil
                .read(" POLYGON ((180 120, 180 133.33333333333334, 280 150, 285.45454545454544 180, 330 180, 330 120, 180 120))"); //$NON-NLS-1$

        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1.add(partA);
        expectedParts1.add(partB);
        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);

        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2.add(partC);
        expectedParts2.add(partD);
        SplitTestUtil.testSplitStrategy(geomToSplit2, splitLine, expectedParts2);

        List<Geometry> expectedParts3 = new ArrayList<Geometry>();
        expectedParts3.add(partE);
        expectedParts3.add(partF);
        SplitTestUtil.testSplitStrategy(geomToSplit3, splitLine, expectedParts3);

        List<Geometry> expectedParts4 = new ArrayList<Geometry>();
        expectedParts4.add(partG);
        expectedParts4.add(partH);
        SplitTestUtil.testSplitStrategy(geomToSplit4, splitLine, expectedParts4);
    }

    /**
     * Two polygons (testcatalog 40a and 40b) that share a boundary. The split line cross the
     * boundary in zigzag way. (Reported by wien issue_20100825.png)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_40a() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (3307.555 344812.916, 3311.023 344814.879, 3311.481 344814.486, 3308.013 344812.458, 3308.602 344811.804, 3311.416 344813.374, 3312.201 344812.392, 3309.03 344810.887 )"); //$NON-NLS-1$

        // execute the first split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((3317.50270000007 344814.0458, 3314.5 344818.619999999, 3312.52773082279 344817.352559328, 3309.34229529964 344815.30550075, 3307.75040000025 344814.282500001, 3312.25389999989 344810.6798, 3317.50270000007 344814.0458))"); //$NON-NLS-1$

        // The First split
        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((3309.178858438242 344813.13976496906, 3311.481 344814.486, 3311.023 344814.879, 3308.669790932271 344813.54700767016, 3309.178858438242 344813.13976496906))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((3310.890869500569 344811.7701941338, 3312.201 344812.392, 3311.416 344813.374, 3309.9255405094445 344812.5424358919, 3310.890869500569 344811.7701941338))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((3307.75040000025 344814.282500001, 3309.34229529964 344815.30550075, 3312.52773082279 344817.352559328, 3314.5 344818.619999999, 3317.50270000007 344814.0458, 3312.25389999989 344810.6798, 3310.890869500569 344811.7701941338, 3312.201 344812.392, 3311.416 344813.374, 3309.9255405094445 344812.5424358919, 3309.178858438242 344813.13976496906, 3311.481 344814.486, 3311.023 344814.879, 3308.669790932271 344813.54700767016, 3307.75040000025 344814.282500001))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);
    }

    /**
     * Two polygons (testcatalog 40a and 40b) that share a boundary. The split line cross the
     * boundary in zigzag way. (Reported by wien issue_20100825.png)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_40b() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (3307.555 344812.916, 3311.023 344814.879, 3311.481 344814.486, 3308.013 344812.458, 3308.602 344811.804, 3311.416 344813.374, 3312.201 344812.392, 3309.03 344810.887 )"); //$NON-NLS-1$

        // execute the second split
        final Polygon geomToSplit = (Polygon) SplitTestUtil
                .read("POLYGON ((3312.25389999989 344810.6798, 3307.75040000025 344814.282500001, 3304.94159999955 344812.477499999, 3308.42009999976 344808.2213, 3312.25389999989 344810.6798))"); //$NON-NLS-1$

        // Expected fragments for the second polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((3309.178858438242 344813.13976496906, 3308.013 344812.458, 3308.602 344811.804, 3309.9255405094445 344812.5424358919, 3309.178858438242 344813.13976496906)), POLYGON ((3304.94159999955 344812.477499999, 3307.75040000025 344814.282500001, 3309.178858438242 344813.13976496906, 3308.013 344812.458, 3308.602 344811.804, 3309.9255405094445 344812.5424358919, 3312.25389999989 344810.6798, 3308.42009999976 344808.2213, 3304.94159999955 344812.477499999))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((3304.94159999955 344812.477499999, 3307.75040000025 344814.282500001, 3309.178858438242 344813.13976496906, 3308.013 344812.458, 3308.602 344811.804, 3309.9255405094445 344812.5424358919, 3312.25389999989 344810.6798, 3308.42009999976 344808.2213, 3304.94159999955 344812.477499999))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit, splitLine, expectedParts);
    }

    /**
     * Split line snap on the boundaries of the polygon to split and further on the corners of an
     * inner polygon
     * 
     * (Reported by wien i20100908_01_the_modified-neighbour-problem)
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_41a() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (3339.24000000022 344749.039999999, 3338.524 344749.15, 3337.926 344749.968, 3337.335 344749.527)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((3337.65769828104 344749.628001634, 3337.68906744777 344749.582152394, 3337.69439355432 344749.574367745, 3337.71329062782 344749.546747742, 3337.88559999969 344749.2949, 3338.69020000007 344749.889699999, 3338.13999999966 344750.74, 3337.86251098821 344750.558368478, 3337.69275969313 344750.447257101, 3337.28139999975 344750.177999999, 3337.65769828104 344749.628001634))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((3338.2312638305843 344749.55043175013, 3337.926 344749.968, 3337.5943801154913 344749.72054759884, 3337.65769828104 344749.628001634, 3337.68906744777 344749.582152394, 3337.69439355432 344749.574367745, 3337.71329062782 344749.546747742, 3337.88559999969 344749.2949, 3338.2312638305843 344749.55043175013))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((3337.28139999975 344750.177999999, 3337.69275969313 344750.447257101, 3337.86251098821 344750.558368478, 3338.13999999966 344750.74, 3338.69020000007 344749.889699999, 3338.2312638305843 344749.55043175013, 3337.926 344749.968, 3337.5943801154913 344749.72054759884, 3337.28139999975 344750.177999999))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);

        // execute the *second* split
        final Polygon geomToSplit2 = (Polygon) SplitTestUtil
                .read("POLYGON ((3339.24000000022 344749.039999999, 3338.69020000007 344749.889699999, 3337.88559999969 344749.2949, 3338.15221109267 344748.905211093, 3338.42650000006 344748.5043, 3339.24000000022 344749.039999999))"); //$NON-NLS-1$

        // Expected fragments for the second polygon
        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2
                .add(SplitTestUtil
                        .read("POLYGON ((3338.2312638305843 344749.55043175013, 3338.524 344749.15, 3339.24000000022 344749.039999999, 3338.69020000007 344749.889699999, 3338.2312638305843 344749.55043175013))")); //$NON-NLS-1$

        expectedParts2
                .add(SplitTestUtil
                        .read("POLYGON ((3337.88559999969 344749.2949, 3338.2312638305843 344749.55043175013, 3338.524 344749.15, 3339.24000000022 344749.039999999, 3338.42650000006 344748.5043, 3338.15221109267 344748.905211093, 3337.88559999969 344749.2949))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit2, splitLine, expectedParts2);
    }

    /**
     * Additional test for 41a
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_41b() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-70.89043516524241 10.690366136499097, -70.6582755938068 25.646584065432364, -10.422082037754961 25.18851415245858, 14.91428571428574 56.3142857142857)"); //$NON-NLS-1$

        // first split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((-99.25714285714284 13.970300751879694, -99.25714285714284 56.3142857142857, -42.17142857142858 56.3142857142857, -42.17142857142858 13.40695488721804, -99.25714285714284 13.970300751879694))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();
        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((-70.8438744608726 13.689906655863869, -70.6582755938068 25.646584065432364, -42.17142857142858 25.42995405005306, -42.17142857142858 13.40695488721804, -70.8438744608726 13.689906655863869))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((-99.25714285714284 13.970300751879694, -99.25714285714284 56.3142857142857, -42.17142857142858 56.3142857142857, -42.17142857142858 25.42995405005306, -70.6582755938068 25.646584065432364, -70.8438744608726 13.689906655863869, -99.25714285714284 13.970300751879694))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);

        // second split
        final Polygon geomToSplit2 = (Polygon) SplitTestUtil
                .read("POLYGON ((-42.17142857142858 56.3142857142857, -42.17142857142858 13.40695488721804, 14.91428571428574 12.843609022556386, 14.91428571428574 56.3142857142857, -42.17142857142858 56.3142857142857))"); //$NON-NLS-1$

        // Expected fragments for the second polygon
        List<Geometry> expectedParts2 = new ArrayList<Geometry>();
        expectedParts2
                .add(SplitTestUtil
                        .read("POLYGON ((-42.17142857142858 25.42995405005306, -10.422082037754961 25.18851415245858, 14.91428571428574 56.3142857142857, -42.17142857142858 56.3142857142857, -42.17142857142858 25.42995405005306))")); //$NON-NLS-1$

        expectedParts2
                .add(SplitTestUtil
                        .read("POLYGON ((-42.17142857142858 13.40695488721804, -42.17142857142858 25.42995405005306, -10.422082037754961 25.18851415245858, 14.91428571428574 56.3142857142857, 14.91428571428574 12.843609022556386, -42.17142857142858 13.40695488721804))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit2, splitLine, expectedParts2);
    }

    /**
     * Split line that intersect with the interior of a polygon with hole but the split strategy can
     * not produce an split result.
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_41c() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-131.88292680647845 76.67612023632466, -104.39999999999998 79.45714285714284, -28.330379078871456 20.630738645428096, 54.00000000000003 79.45714285714284, 69.9286216555281 73.60907542687168)"); //$NON-NLS-1$

        final Polygon geomToSplit = (Polygon) SplitTestUtil
                .read("POLYGON ((-146.57142857142856 -61.971428571428575, -146.57142857142856 86.65714285714284, -138.85714285714283 87.17142857142856, 97.20000000000002 88.7142857142857, 109.0285714285715 -65.05714285714286, -146.57142857142856 -61.971428571428575), (-104.39999999999998 -44.48571428571428, -27.863594470046056 -44.48571428571428, 54.00000000000003 -44.48571428571428, 54.00000000000003 19.73389355742296, 54.00000000000003 79.45714285714284, -28.752073732718877 79.45714285714284, -104.39999999999998 79.45714285714284, -104.39999999999998 21.459383753501392, -104.39999999999998 -44.48571428571428))"); //$NON-NLS-1$

        List<Geometry> emptyResult = Collections.emptyList();

        SplitTestUtil.testSplitStrategy(geomToSplit, splitLine, emptyResult);

    }

    /**
     * Split line snapping polygon boundaries
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_42a() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (30.751402786844434 56.3142857142857, 52.42468152031458 31.288671035386624, 38 19, 51.94285714285718 3.3428571428571416, 52.317337031900166 -35.22857142857143)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((-105.42857142857142 56.3142857142857, 30.751402786844434 56.3142857142857, 128.0571428571429 56.3142857142857, 128.0571428571429 -35.22857142857143, 52.317337031900166 -35.22857142857143, -105.42857142857142 -35.22857142857143, -105.42857142857142 56.3142857142857), (52.42468152031458 31.288671035386624, 92.57142857142858 32.14285714285713, 92.57142857142858 4.371428571428567, 51.94285714285718 3.3428571428571416, 52.42468152031458 31.288671035386624))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((52.317337031900166 -35.22857142857143, 51.94285714285718 3.3428571428571416, 38 19, 52.42468152031458 31.288671035386624, 30.751402786844434 56.3142857142857, -105.42857142857142 56.3142857142857, -105.42857142857142 -35.22857142857143, 52.317337031900166 -35.22857142857143))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((30.751402786844434 56.3142857142857, 128.0571428571429 56.3142857142857, 128.0571428571429 -35.22857142857143, 52.317337031900166 -35.22857142857143, 51.94285714285718 3.3428571428571416, 92.57142857142858 4.371428571428567, 92.57142857142858 32.14285714285713, 52.42468152031458 31.288671035386624, 30.751402786844434 56.3142857142857))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((51.94285714285718 3.3428571428571416, 38 19, 52.42468152031458 31.288671035386624, 51.94285714285718 3.3428571428571416))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts);
    }

    @Test
    public void testcatalog_43() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-177.19438157119657 35.34582008449436, -180.27018936581942 -21.249043336566118)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((-244.22233329581877 -46.724820561290755, -243.48650934997164 56.29053185730304, -137.52786114798948 56.29053185730304, -137.82404180002405 -48.93131348817178, -244.22233329581877 -46.724820561290755), (-216.9474065463471 -20.925319977604858, -180.27018936581942 -21.249043336566118, -161.20018103915774 -0.3335503331307166, -188.88245119076342 10.739357727511546, -161.8153425980823 20.58194267030467, -177.19438157119657 35.34582008449436, -216.9474065463471 35.97274108509599, -216.9474065463471 -20.925319977604858))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((-188.88245119076342 10.739357727511546, -178.3230045926106 14.579156490476205, -178.75191828421836 6.687144564893524, -188.88245119076342 10.739357727511546))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((-244.22233329581877 -46.724820561290755, -243.48650934997164 56.29053185730304, -137.52786114798948 56.29053185730304, -137.82404180002405 -48.93131348817178, -244.22233329581877 -46.724820561290755),  (-216.9474065463471 -20.925319977604858, -180.27018936581942 -21.249043336566118, -161.20018103915774 -0.3335503331307166, -178.75191828421836 6.687144564893524, -178.3230045926106 14.579156490476205, -161.8153425980823 20.58194267030467, -177.19438157119657 35.34582008449436, -216.9474065463471 35.97274108509599, -216.9474065463471 -20.925319977604858))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * this is like 43 changing the split line to an split line with oposite direction
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_43_reverse_splitLine() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (-180.27018936581942 -21.249043336566118, -177.19438157119657 35.34582008449436)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((-244.22233329581877 -46.724820561290755, -243.48650934997164 56.29053185730304, -137.52786114798948 56.29053185730304, -137.82404180002405 -48.93131348817178, -244.22233329581877 -46.724820561290755), (-216.9474065463471 -20.925319977604858, -180.27018936581942 -21.249043336566118, -161.20018103915774 -0.3335503331307166, -188.88245119076342 10.739357727511546, -161.8153425980823 20.58194267030467, -177.19438157119657 35.34582008449436, -216.9474065463471 35.97274108509599, -216.9474065463471 -20.925319977604858))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((-188.88245119076342 10.739357727511546, -178.3230045926106 14.579156490476205, -178.75191828421836 6.687144564893524, -188.88245119076342 10.739357727511546))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((-244.22233329581877 -46.724820561290755, -243.48650934997164 56.29053185730304, -137.52786114798948 56.29053185730304, -137.82404180002405 -48.93131348817178, -244.22233329581877 -46.724820561290755),  (-216.9474065463471 -20.925319977604858, -180.27018936581942 -21.249043336566118, -161.20018103915774 -0.3335503331307166, -178.75191828421836 6.687144564893524, -178.3230045926106 14.579156490476205, -161.8153425980823 20.58194267030467, -177.19438157119657 35.34582008449436, -216.9474065463471 35.97274108509599, -216.9474065463471 -20.925319977604858))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);
    }

    @Test
    public void testcatalog_43b() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (260 280, 260 160)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((140 420, 140 40, 900 40, 900 400, 140 420), (200 360, 200 280, 340 200, 200 160, 200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 340 360, 200 360))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedPieces = new ArrayList<Geometry>();

        expectedPieces
                .add(SplitTestUtil
                        .read("POLYGON ((260 177.14285714285714, 260 245.71428571428572, 340 200, 260 177.14285714285714))")); //$NON-NLS-1$

        expectedPieces
                .add(SplitTestUtil
                        .read("POLYGON ((140 40, 140 420, 900 400, 900 40, 140 40),   (200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 200 360, 200 280, 260 245.71428571428572, 260 177.14285714285714, 200 160, 200 100))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedPieces);
    }

    @Test
    public void testcatalog_43b_reverse_splitLine() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (260 160,260 280)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((140 420, 140 40, 900 40, 900 400, 140 420), (200 360, 200 280, 340 200, 200 160, 200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 340 360, 200 360))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((260 177.14285714285714, 260 245.71428571428572, 340 200, 260 177.14285714285714))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((140 40, 140 420, 900 400, 900 40, 140 40),   (200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 200 360, 200 280, 260 245.71428571428572, 260 177.14285714285714, 200 160, 200 100))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);
    }

    @Test
    public void testcatalog_43c() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (740 120, 740 280)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((140 420, 140 40, 900 40, 900 400, 140 420), (200 360, 200 280, 340 200, 200 160, 200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 340 360, 200 360))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();

        expectedParts1.add(SplitTestUtil.read("POLYGON ((720 160, 740 185, 740 144, 720 160))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((140 40, 140 420, 900 400, 900 40, 140 40), (200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 740 144, 740 185, 800 260, 800 360, 540 360, 460 240, 340 360, 200 360, 200 280, 340 200, 200 160, 200 100))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);
    }

    @Test
    public void testcatalog_43c_reverse_splitLine() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING ( 740 280,740 120)"); //$NON-NLS-1$

        // executes the *first* split
        final Polygon geomToSplit1 = (Polygon) SplitTestUtil
                .read("POLYGON ((140 420, 140 40, 900 40, 900 400, 140 420), (200 360, 200 280, 340 200, 200 160, 200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 720 160, 800 260, 800 360, 540 360, 460 240, 340 360, 340 360, 200 360))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts1 = new ArrayList<Geometry>();

        expectedParts1.add(SplitTestUtil.read("POLYGON ((720 160, 740 185, 740 144, 720 160))")); //$NON-NLS-1$

        expectedParts1
                .add(SplitTestUtil
                        .read("POLYGON ((140 40, 140 420, 900 400, 900 40, 140 40), (200 100, 460 100, 560 160, 580 240, 660 80, 820 80, 740 144, 740 185, 800 260, 800 360, 540 360, 460 240, 340 360, 200 360, 200 280, 340 200, 200 160, 200 100))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(geomToSplit1, splitLine, expectedParts1);
    }

    /**
     * Informed as i20101018_01_tc42
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_42b1() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (6653.563 346960.009, 6653.563 346962.528, 6656.158 346962.604, 6656.616 346955.658, 6653.792 346955.429, 6653.639 346958.482)"); //$NON-NLS-1$

        final Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((6660.25760000013 346957.212200001, 6660.25760000013 346961.382200001, 6650.85610000044 346961.1547, 6650.78029999975 346957.136299999, 6660.25760000013 346957.212200001))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();
        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((6656.244964534968 346961.28510117927, 6656.515496433053 346957.182230952, 6660.25760000013 346957.212200001, 6660.25760000013 346961.382200001, 6656.244964534968 346961.28510117927))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((6650.78029999975 346957.136299999, 6650.85610000044 346961.1547, 6656.244964534968 346961.28510117927, 6656.515496433053 346957.182230952, 6650.78029999975 346957.136299999))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

    /**
     * Informed as i20101018_01_tc42
     * 
     * @throws Exception
     */
    @Test
    public void testcatalog_42b2() throws Exception {

        final LineString splitLine = (LineString) SplitTestUtil
                .read("LINESTRING (6653.563 346960.009, 6653.563 346962.528, 6656.158 346962.604, 6656.616 346955.658, 6653.792 346955.429, 6653.639 346958.482)"); //$NON-NLS-1$

        final Polygon polygon = (Polygon) SplitTestUtil
                .read("POLYGON ((6666.98340000026 346966.3084, 6647.33430000022 346966.3084, 6647.09470000025 346953.3687, 6667.22300000023 346953.3687, 6666.98340000026 346966.3084), (6660.25760000013 346957.212200001, 6650.78029999975 346957.136299999, 6650.85610000044 346961.1547, 6660.25760000013 346961.382200001, 6660.25760000013 346957.212200001))"); //$NON-NLS-1$

        // Expected fragments for the first polygon
        List<Geometry> expectedParts = new ArrayList<Geometry>();

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((6647.09470000025 346953.3687, 6647.33430000022 346966.3084, 6666.98340000026 346966.3084, 6667.22300000023 346953.3687, 6647.09470000025 346953.3687), (6650.78029999975 346957.136299999, 6653.7052653420615 346957.15972490644, 6653.792 346955.429, 6656.616 346955.658, 6656.515496433053 346957.182230952, 6660.25760000013 346957.212200001, 6660.25760000013 346961.382200001, 6656.244964534968 346961.28510117927, 6656.158 346962.604, 6653.563 346962.528, 6653.563 346961.22020228714, 6650.85610000044 346961.1547, 6650.78029999975 346957.136299999))]")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((6656.244964534968 346961.28510117927, 6656.158 346962.604, 6653.563 346962.528, 6653.563 346961.22020228714, 6656.244964534968 346961.28510117927))")); //$NON-NLS-1$

        expectedParts
                .add(SplitTestUtil
                        .read("POLYGON ((6653.7052653420615 346957.15972490644, 6653.792 346955.429, 6656.616 346955.658, 6656.515496433053 346957.182230952, 6653.7052653420615 346957.15972490644))")); //$NON-NLS-1$

        SplitTestUtil.testSplitStrategy(polygon, splitLine, expectedParts);
    }

}
