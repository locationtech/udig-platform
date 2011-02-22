package net.refractions.udig.tools.edit.support;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

public class PathToPathIteratorAdapterTest extends TestCase {
    GeneralPath gp=new GeneralPath();
    Path p=new Path(Display.getCurrent());

    public void testDraw() throws Exception {
        moveTo(10,10);  //1
        lineTo(20,10); //2
        curveTo(20,10,30,20,30,30);//4
        quadTo(40,40, 50, 10);//3
        close();//5

        PathToPathIteratorAdapter pi=new PathToPathIteratorAdapter(p);
        PathIterator i = gp.getPathIterator(new AffineTransform());

        while(!i.isDone()){
            assertFalse(pi.isDone());

            float[] expected=new float[6];
            float[] actual=new float[6];

            assertEquals(i.currentSegment(expected),
                    pi.currentSegment(actual));

            for( int j = 0; j < actual.length; j++ ) {
                assertEquals(expected[j], actual[j]);
            }
            i.next();
            pi.next();
        }
        assertTrue(pi.isDone());

        pi=new PathToPathIteratorAdapter(p);
        i = gp.getPathIterator(new AffineTransform());

        while(!i.isDone()){
            assertFalse(pi.isDone());

            float[] expected=new float[6];
            float[] actual=new float[6];

            assertEquals(i.currentSegment(expected),
                    pi.currentSegment(actual));

            for( int j = 0; j < actual.length; j++ ) {
                assertEquals(expected[j], actual[j]);
            }
            i.next();
            pi.next();
        }
        assertTrue(pi.isDone());

    }

    private void close() {
        gp.closePath();
        p.close();
    }

    private void quadTo( int i, int j, int k, int l ) {
        gp.quadTo(i, j, k, l);
        p.quadTo(i, j, k, l);
    }

    private void curveTo( int i, int j, int k, int l, int m, int n ) {
        gp.curveTo(i, j, k, l, m, n);
        p.cubicTo(i, j, k, l, m, n);
    }

    private void lineTo( int x, int y ) {
        gp.lineTo(x, y);
        p.lineTo(x, y);
    }

    private void moveTo( int i, int j ) {
        p.moveTo(i, j);
        gp.moveTo(i, j);
    }

}
