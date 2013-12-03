/*******************************************************************************
 * Copyright (c) 2010,2012 City of Vienna.
 *
 * This program and the accompanying materials are made available under the            
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0    
 * which accompanies this distribution.                                                
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at                                
 * http://www.eclipse.org/org/documents/edl-v10.php.                                   
 *                                                                                     
 * Contributors:                                                                       
 *    Aritz Davila (Axios) - initial implementation and documentation                  
 *    Mauricio Pazos (Axios) - initial implementation and documentation
 *******************************************************************************/
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
