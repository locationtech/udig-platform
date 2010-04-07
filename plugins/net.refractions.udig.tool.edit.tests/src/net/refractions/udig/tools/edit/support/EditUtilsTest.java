/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.support;

import junit.framework.TestCase;

/**
 * Tests EditUtils methods
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditUtilsTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.support.EditUtils.intersectingLines(Point, Point, Point, Point)'
     */
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
