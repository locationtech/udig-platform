package eu.udig.tools.tests.arc.internal;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import eu.udig.tools.arc.internal.ArcBuilder;

public class ArcBuilderTest extends TestCase {

    ArcBuilder builder;

    public ArcBuilderTest( String name ) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        builder = new ArcBuilder();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        builder = null;
    }

    public void testGetArcClockWise() {
        builder.setPoints(-10, 0, 0, 10, 10, 0);

        Arc2D arc = builder.getArc();
        assertEquals(Arc2D.OPEN, arc.getArcType());

        assertEquals(0D, arc.getCenterX(), 1E-6);
        assertEquals(0D, arc.getCenterY(), 1E-6);
        assertEquals(20D, arc.getWidth(), 1E-6);
        assertEquals(20D, arc.getHeight(), 1E-6);

        Point2D startPoint = arc.getStartPoint();
        Point2D endPoint = arc.getEndPoint();

        assertEquals(-10, startPoint.getX(), 1E-6);
        assertEquals(0, startPoint.getY(), 1E-6);

        assertEquals(10, endPoint.getX(), 1E-6);
        assertEquals(0, endPoint.getY(), 1E-6);
        
        assertEquals(-180, arc.getAngleStart(), 1E-6);
        assertEquals(180, arc.getAngleExtent(), 1E-6);
    }

    public void testGetArcCollinear() {
        builder.setPoints(-10, 0, 0, 0, 10, 0);

        Arc2D arc = builder.getArc();
        assertNull(arc);
    }

    public void testGetArcCounterClockWise() {
        builder.setPoints(0, 10, -10, 0, 0, -10);
        Arc2D arc = builder.getArc();
        assertEquals(Arc2D.OPEN, arc.getArcType());

        assertEquals(0D, arc.getCenterX(), 1E-6);
        assertEquals(0D, arc.getCenterY(), 1E-6);
        assertEquals(20D, arc.getWidth(), 1E-6);
        assertEquals(20D, arc.getHeight(), 1E-6);

        Point2D startPoint = arc.getStartPoint();
        Point2D endPoint = arc.getEndPoint();
        double angleStart = arc.getAngleStart();
        double angleExtent = arc.getAngleExtent();
        
        assertEquals(0, startPoint.getX(), 1E-6);
        assertEquals(-10, startPoint.getY(), 1E-6);

        assertEquals(0, endPoint.getX(), 1E-6);
        assertEquals(10, endPoint.getY(), 1E-6);
        
        assertEquals(90, angleStart, 1E-6);
        assertEquals(180, angleExtent, 1E-6);
    }

    public void testGetArcCenter() {
        builder.setPoints(0, 0, 10, 10, 20, 0);

        Coordinate arcCenter = builder.getArcCenter();
        assertEquals(10D, arcCenter.x, 1E-6);
        assertEquals(0D, arcCenter.y, 1E-6);

        builder.setPoints(-10, 0, 0, -10, 10, 0);

        arcCenter = builder.getArcCenter();
        assertEquals(0D, arcCenter.x, 1E-6);
        assertEquals(0D, arcCenter.y, 1E-6);
    }

    public void testGetMidpointNormal() {
        testGetMidpointNormal(0, 0, 10, 0, 5, -5, 5, 5);

        testGetMidpointNormal(0, 0, 10, 10, 0, 10, 10, 0);

        testGetMidpointNormal(10, 10, 20, 0, 10, 0, 20, 10);
    }

    /**
     * @param x1 minx coord of the line segment
     * @param y1 miny coord of the line segment
     * @param x2 maxx coord of the line segment
     * @param y2 maxy coord of the line segment
     * @param nx1 expected minx coord of the normal segment
     * @param ny1 expected miny coord of the normal segment
     * @param nx2 expected maxx coord of the normal segment
     * @param ny2 expected maxy coord of the normal segment
     */
    public void testGetMidpointNormal( double x1, double y1, double x2, double y2, double nx1,
                                       double ny1, double nx2, double ny2 ) {

        final LineSegment segment = new LineSegment(c(x1, y1), c(x2, y2));

        final double length = segment.getLength();
        LineSegment normal = builder.getMidpointNormal(segment, length);

        assertEquals(length, normal.getLength(), 1E-6);

        System.out.println("segment: " + segment);
        System.out.println("normal: " + normal);

        Coordinate p0 = normal.p0;
        Coordinate p1 = normal.p1;

        try {
            assertEquals(nx1, p1.x, 1E-6);
            assertEquals(ny1, p1.y, 1E-6);

            assertEquals(nx2, p0.x, 1E-6);
            assertEquals(ny2, p0.y, 1E-6);
        } catch (AssertionFailedError e) {
            // may be the returned segment is
            // just in the opposite direction
            assertEquals(nx1, p0.x, 1E-6);
            assertEquals(ny1, p0.y, 1E-6);

            assertEquals(nx2, p1.x, 1E-6);
            assertEquals(ny2, p1.y, 1E-6);
        }
    }

    private Coordinate c( double x, double y ) {
        return new Coordinate(x, y);
    }
}
