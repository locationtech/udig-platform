/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import eu.udig.tools.geometry.split.SplitStrategy;

/**
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
public class SplitTestUtil {

    
    private SplitTestUtil(){
        // utility class
    }
    
	public static Geometry read(final String wkt) throws ParseException, IllegalStateException {

		WKTReader reader = new WKTReader();
		Geometry geometry;

		geometry = reader.read(wkt);
		
		if(!geometry.isValid()){
		    throw new IllegalStateException("the geometry is not valid: " + geometry.toText() ); //$NON-NLS-1$
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
        public static List<Geometry> testSplitStrategy(
        	final Geometry geomToSplit,
    	    	final LineString splitLine, 
    	    	final List<Geometry> expectedParts) {
    
        	final List<Geometry> splitResult = executeSplitStrategy(geomToSplit,splitLine);
        
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

	public static List<Geometry> executeSplitStrategy(final Geometry geomTosplit, final LineString splitLine) {

		final SplitStrategy splitOp = new SplitStrategy(splitLine);
		List<Geometry> splitResult = new ArrayList<Geometry>();

		if (splitOp.canSplit(geomTosplit)) {

			splitResult = splitOp.split(geomTosplit);
		}
		return splitResult;
	}


    /**
     * Prints the features'attribute of the feature list 
     * @param featureList
     * @return the list of features ready to pring
     */
    public static String prettyPrint( List<SimpleFeature> featureList ) {

        StringBuilder strBuilder = new StringBuilder("\n"); //$NON-NLS-1$
        for( SimpleFeature f : featureList ) {
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
