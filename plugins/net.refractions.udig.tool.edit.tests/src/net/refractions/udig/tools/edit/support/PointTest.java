/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class PointTest {

    /*
     * Test method for 'net.refractions.udig.tools.edit.support.Point.valueOf(int, int)'
     */
    @Test
    public void testValueOf() {
        List<Point> points=new ArrayList<Point>();
        List<Integer> xs=new ArrayList<Integer>();
        List<Integer> ys=new ArrayList<Integer>();
        Random random=new Random();
        
        int trials=10000;
        for( int i=0; i<trials; i++ ){
            int x=random.nextInt(30000);
            int y=random.nextInt(30000);
            xs.add(x);
            ys.add(y);
            Point point = Point.valueOf(x,y);
            points.add(point);
            assertEquals(x,point.getX());
            assertEquals(y,point.getY());
        }
        
        for( int i=0; i<trials; i++ ){
            int x=xs.get(i);
            int y=ys.get(i);
            Point point = points.get(i);
            assertEquals(x,point.getX());
            assertEquals(y,point.getY());
        }
        
    }

}
