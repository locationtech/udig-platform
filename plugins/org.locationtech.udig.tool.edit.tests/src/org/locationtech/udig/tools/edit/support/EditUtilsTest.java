/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests EditUtils methods
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditUtilsTest {

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.EditUtils.intersectingLines(Point, Point, Point, Point)'
     */
    @Test
    public void testIntersectingLines() {
        assertNull(EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(20,20), Point.valueOf(30,30) ));
        Point result = EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(10,0), Point.valueOf(0,10) );
        assertEquals(Point.valueOf( 5,5), result );
        
        result = EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(10,10), Point.valueOf(0,20) );
        assertEquals(Point.valueOf( 10,10), result );
        
        result = EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(10,11), Point.valueOf(0,21) );
        assertNull( result );
        
        result = EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(10,10), Point.valueOf(30,30) );
        assertNull( result );

        result = EditUtils.instance.intersectingLines( Point.valueOf(0,0), Point.valueOf(10,10), 
                Point.valueOf(0,10), Point.valueOf(10,0) );
        assertEquals(Point.valueOf( 5,5), result );
    }

    @Test
    public void testSelfIntersection() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        EditGeom geom = bb.newGeom("id", null); //$NON-NLS-1$
        PrimitiveShape shell = geom.getShell();
        bb.addPoint(10, 10, shell);
        
        EditUtils editUtils = EditUtils.instance;
        assertFalse( editUtils.selfIntersection(shell) );
        
        bb.addPoint( 20, 10, shell );
        
        assertFalse( editUtils.selfIntersection(shell) );
        
        bb.addPoint(10, 10, shell);
        
        assertTrue( editUtils.selfIntersection(shell) );
        
        geom = bb.newGeom("id", null); //$NON-NLS-1$
        shell = geom.getShell();
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(10, 11, shell);
        
        assertFalse( editUtils.selfIntersection(shell));
        
        bb.removeCoordsAtPoint(10, 11);
        bb.addPoint(15, 15, shell);
        // now looks like:
        //    
        // -------
        //      /
        //    /
        
        assertFalse( editUtils.selfIntersection(shell) );
        
        bb.addPoint(15, 5, shell);
        // now looks like:
        //    |
        // ---|---
        //    | /
        //    |
        
        assertTrue( editUtils.selfIntersection(shell) );
        
        bb.clear();
        
        geom=bb.newGeom("id", null); //$NON-NLS-1$
        shell=geom.getShell();
        
        bb.addPoint(0, 0, shell);
        bb.addPoint(10, 10, shell);
        bb.addPoint(5, 5, shell);
        
        assertTrue( editUtils.selfIntersection(shell));

        geom=bb.newGeom("id", null); //$NON-NLS-1$
        shell=geom.getShell();
        
        bb.addPoint(0, 0, shell);
        bb.addPoint(0, 10, shell);
        bb.addPoint(0, 5, shell);
        
        assertTrue( editUtils.selfIntersection(shell));
        
        geom=bb.newGeom("id", null); //$NON-NLS-1$
        shell=geom.getShell();
        
        bb.addPoint(0, 0, shell);
        bb.addPoint(0, 10, shell);
        bb.addPoint(10, 10, shell);
        bb.addPoint(0, 0, shell);
        
        assertFalse( editUtils.selfIntersection(shell));
        
        // test:
        //  |
        //  |----
        //  | /
        //  |
        
        geom=bb.newGeom("id", null); //$NON-NLS-1$
        shell=geom.getShell();
        
        bb.addPoint(0, 0, shell);
        bb.addPoint(10, 0, shell);
        bb.addPoint(0, 10, shell);
        bb.addPoint(0, -5, shell);
        
        assertTrue( editUtils.selfIntersection(shell));
        
    }
}
