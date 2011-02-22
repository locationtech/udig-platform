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

import java.awt.geom.AffineTransform;

import junit.framework.TestCase;

import org.geotools.referencing.FactoryFinder;
import org.geotools.referencing.operation.matrix.GeneralMatrix;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Test LazyCoord class
 * @author jones
 * @since 1.1.0
 */
public class LazyCoordTest extends TestCase {



    /*
     * Test method for 'net.refractions.udig.tools.edit.support.LazyCoord.get(Point)'
     */
    public void testGet() throws Exception{
        MathTransform layerTransform = FactoryFinder.getMathTransformFactory(null).createAffineTransform(new GeneralMatrix(new AffineTransform()));
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
