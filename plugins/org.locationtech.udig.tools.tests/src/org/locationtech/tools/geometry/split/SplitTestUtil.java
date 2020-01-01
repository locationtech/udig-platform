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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import org.locationtech.udig.tools.geometry.split.SplitStrategy;

/**
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
public class SplitTestUtil {

    private SplitTestUtil() {
        // utility class
    }

    public static Geometry read(final String wkt) throws ParseException, IllegalStateException {

        WKTReader reader = new WKTReader();
        Geometry geometry;

        geometry = reader.read(wkt);

        if (!geometry.isValid()) {
            throw new IllegalStateException("the geometry is not valid: " + geometry.toText()); //$NON-NLS-1$
        }

        return geometry;
    }

    /**
     * Execute the split strategy and evaluate its result
     * 
     * @param geomToSplit
     * @param splitLine
     * @param expectedParts
     * 
     * @return the split result
     */
    public static List<Geometry> testSplitStrategy(final Geometry geomToSplit,
            final LineString splitLine, final List<Geometry> expectedParts) {

        final List<Geometry> splitResult = executeSplitStrategy(geomToSplit, splitLine);

        assertEquals(expectedParts.size(), splitResult.size());

        for (int i = 0; i < expectedParts.size(); i++) {
            Geometry expectedPart = expectedParts.get(i);
            expectedPart.normalize();

            boolean found = false;
            for (int j = 0; j < splitResult.size(); j++) {
                Geometry fragment = splitResult.get(j);
                fragment.normalize();
                if (expectedPart.equals(fragment)) {
                    found = true;
                    assertTrue(fragment.isValid());
                    break;
                }
            }
            if (!found) {
                fail(expectedPart + " not found in " + splitResult); //$NON-NLS-1$
            }
        }

        return splitResult;
    }

    public static List<Geometry> executeSplitStrategy(final Geometry geomTosplit,
            final LineString splitLine) {

        final SplitStrategy splitOp = new SplitStrategy(splitLine);
        List<Geometry> splitResult = new ArrayList<Geometry>();

        if (splitOp.canSplit(geomTosplit)) {

            splitResult = splitOp.split(geomTosplit);
        }
        return splitResult;
    }

    /**
     * Prints the features'attribute of the feature list
     * 
     * @param featureList
     * @return the list of features ready to pring
     */
    public static String prettyPrint(List<SimpleFeature> featureList) {

        StringBuilder strBuilder = new StringBuilder("\n"); //$NON-NLS-1$
        for (SimpleFeature f : featureList) {
            strBuilder.append("Feature Id -- Geometry: ").append(f.getID()) //$NON-NLS-1$
                    .append(" -- ").append(f.getDefaultGeometry()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return strBuilder.toString();
    }

    public static double convert(double degree) {

        double radiant = (degree * Math.PI) / 180;
        return radiant;
    }
}
