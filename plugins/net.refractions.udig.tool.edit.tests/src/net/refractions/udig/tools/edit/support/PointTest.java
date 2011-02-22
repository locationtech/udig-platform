package net.refractions.udig.tools.edit.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

public class PointTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.support.Point.valueOf(int, int)'
     */
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
