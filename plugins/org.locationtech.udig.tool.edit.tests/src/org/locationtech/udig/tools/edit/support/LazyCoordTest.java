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

import java.awt.geom.AffineTransform;

import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.matrix.GeneralMatrix;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;

/**
 * Test LazyCoord class
 * @author jones
 * @since 1.1.0
 */
public class LazyCoordTest {
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.LazyCoord.get(Point)'
     */
    @Test
    public void testGet() throws Exception{
        MathTransform layerTransform = ReferencingFactoryFinder.getMathTransformFactory(null).createAffineTransform(new GeneralMatrix(new AffineTransform()));
        EditBlackboard bb = new EditBlackboard(100,100, new AffineTransform(), layerTransform);

        Point startingPoint = Point.valueOf(10,10);
        LazyCoord coord=new LazyCoord(startingPoint, bb.toCoord(startingPoint),bb);
        
        Point endPoint = Point.valueOf(20,20);
        assertEquals(new Coordinate(20.5, 20.5), coord.get(endPoint));
        
        bb.setToScreenTransform(AffineTransform.getTranslateInstance(2,0));
        assertEquals(new Coordinate(18.5, 20.5), coord.get(endPoint));
        
        bb.setToScreenTransform(new AffineTransform());
        assertEquals(new Coordinate(20.5, 20.5), coord.get(endPoint));

        bb.setToScreenTransform(AffineTransform.getScaleInstance(2,1));
        assertEquals(new Coordinate(10.25, 20.5), coord.get(endPoint));
        
        bb.setToScreenTransform(new AffineTransform());
        assertEquals(new Coordinate(20.5, 20.5), coord.get(endPoint));

        bb.setToScreenTransform(AffineTransform.getScaleInstance(2,1));
        assertEquals(new Coordinate(5.25, 10.5), coord.get(Point.valueOf(10,10)));
        
        assertEquals(new Coordinate(10.25, 20.5), coord.get(endPoint));

        bb.setToScreenTransform(new AffineTransform());
        assertEquals(new Coordinate(20.5, 20.5), coord.get(endPoint));
        
    }

}
