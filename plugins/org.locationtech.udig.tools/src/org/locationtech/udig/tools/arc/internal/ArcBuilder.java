/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.arc.internal;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Triangle;

/**
 * Creates an {@link Arc2D} that passes through three points.
 * <p>
 * This builder might be used both to create a Java2D {@link Arc2D} or a JTS {@link LineString}
 * which linearly approximates an arc.
 * </p>
 * <p>
 * No coordinate transformation between device and coordinate space is performed, though. Instead,
 * this builder is coordinate system agnostic.
 * </p>
 * <p>
 * Sample usage to create an arc centered at <code>0,0</code>, which starts at <code>-10,0</code>,
 * passes through <code>0,10</code> and ends at <code>10,0</code>:
 * 
 * <pre><code>
 * ArcBuilder builder = new ArcBuilder();
 * builder.setPoints(-10, 0, 0, 10, 10, 0);
 * Arc2D arc = builder.getArc();
 * LineString approxLineStr = builder.getGeometry(15); //15 segments per quadrant
 * </code></pre>
 * 
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 */
public class ArcBuilder {

    /**
     * The starting arc point in device coordinates
     */
    private final Coordinate point1 = new Coordinate();

    /**
     * The second point of the arc in device coordinates. This one is a shared vertex of the two arc
     * chords used to calculate the arc
     */
    private final Coordinate point2 = new Coordinate();

    /**
     * The arc's end point.
     */
    private final Coordinate point3 = new Coordinate();

    /**
     * ring used to evaluate the clockwise orientation of the arc. Use of this array relies in the
     * fact that point1, point2 and point3 are final and their internal coordinates change
     * dynamically
     */
    private Coordinate[]     ring   = {point1, point2, point3, point1};

    /**
     * Builds and returns an arc of circumference defined by two consecutive chords set by
     * {@link #setPoints(double, double, double, double, double, double) setPoints}
     * 
     * @return a Java2D arc built upon the three coordinates settled at
     *         {@link #setPoints(double, double, double, double, double, double)}, or
     *         <code>null</code> of the coordinates are collinear.
     */
    public Arc2D getArc() {
        final Coordinate arcCenter = getArcCenter();
        if (arcCenter == null) {
            return null;
        }

        final double radius = arcCenter.distance(point1);

        double minx = arcCenter.x - radius;
        double miny = arcCenter.y - radius;
        Rectangle2D rect = new Rectangle2D.Double(minx, miny, 2 * radius, 2 * radius);

        Arc2D.Double arc = new Arc2D.Double(rect, 0, 0, Arc2D.OPEN);

        boolean ccw = CGAlgorithms.isCCW(ring);
        Coordinate start, end;
        if (ccw) {
            start = point3;
            end = point1;
        } else {
            start = point1;
            end = point3;
        }
        // set the arc's start and end points in ccw orientation
        arc.setAngles(start.x, start.y, end.x, end.y);

        return arc;
    }

    /**
     * Builds a JTS LineString
     * 
     * @param pointsPerQuadrant how many points per quadrant shall be used to approximate the arc
     *        using line segments
     * @return
     */
    public LineString getGeometry( final int pointsPerQuadrant ) {
        assert pointsPerQuadrant > 0;

        GeometryFactory gf = new GeometryFactory();
        Arc2D arc = getArc();
        if (arc == null) {
            return null;
        }

        double flatness = calculateMaxDistanceToCurve(arc, pointsPerQuadrant);
        PathIterator pathIterator = arc.getPathIterator((AffineTransform) null, flatness);
        List<Coordinate> coords = new LinkedList<Coordinate>();

        double[] coordsHolder = new double[6];

        while( !pathIterator.isDone() ) {
            int pathSegType = pathIterator.currentSegment(coordsHolder);
            double x = coordsHolder[0];
            double y = coordsHolder[1];
            switch( pathSegType ) {
            case PathIterator.SEG_MOVETO:
                assert coords.size() == 0;
                break;
            case PathIterator.SEG_LINETO:
                // ok
                break;
            default:
                throw new IllegalStateException("Unexpected path segment type: " + pathSegType);
            }
            coords.add(new Coordinate(x, y));
            pathIterator.next();
        }

        Coordinate[] coordinates = coords.toArray(new Coordinate[coords.size()]);
        LineString line = gf.createLineString(coordinates);
        return line;
    }

    /**
     * Calculates the maximum distance that a segment used to approximate the curve is allowed to be
     * separated from the actual curve, if <code>pointsPerQuadrant</code> points per quadrant are
     * going to be used to approximate the curve.
     * 
     * @param arc
     * @return
     */
    private double calculateMaxDistanceToCurve( final Arc2D arc, final int pointsPerQuadrant ) {
        double centerX = arc.getCenterX();
        double centerY = arc.getCenterY();

        final Coordinate center = new Coordinate(centerX, centerY);
        Point2D sp = arc.getStartPoint();
        final Coordinate startPoint = new Coordinate(sp.getX(), sp.getY());
        final double radius = center.distance(startPoint);
        final double angleStep = Angle.PI_OVER_2 / pointsPerQuadrant;
        double dx = radius * Math.cos(angleStep);
        double dy = radius * Math.sin(angleStep);

        Coordinate c1 = new Coordinate(center.x + radius, center.y);
        Coordinate c2 = new Coordinate(center.x + dx, center.y + dy);
        Coordinate bisectorCoord = Triangle.angleBisector(c1, center, c2);

        double bisectorLength = center.distance(bisectorCoord);

        double flatness = radius - bisectorLength;
        return flatness;
    }

    /**
     * The segments point1-point2, point2-point3 are taken as the consecutive arc chords from which
     * to calculate the arc center by finding the intersection of their normals
     * 
     * @return
     */
    public Coordinate getArcCenter() {
        LineSegment chord1 = new LineSegment(point1, point2);
        LineSegment chord2 = new LineSegment(point2, point3);

        final double normalLength = 1E7;
        LineSegment midPointNormal1 = getMidpointNormal(chord1, normalLength);
        LineSegment midPointNormal2 = getMidpointNormal(chord2, normalLength);

        Coordinate center = midPointNormal1.intersection(midPointNormal2);

        return center;
    }

    /**
     * Returns a line segment of the specified <code>lenght</code> the passes through the midpoint
     * of <code>segment</code> and is perpendicular to it.
     * 
     * @param segment
     * @return
     */
    public LineSegment getMidpointNormal( final LineSegment segment, final double lenght ) {
        Coordinate midPoint = segment.midPoint();

        // the angle (in radians) of the segment respect to the X axis
        double chordAngle = segment.angle();

        // the angle (in radians) of the perpendicular segment respect to the X axis
        double normalAngle = chordAngle + Angle.PI_OVER_2;

        final double midLen = lenght / 2;

        double dx = midLen * Math.cos(normalAngle);
        double dy = midLen * Math.sin(normalAngle);

        double x1 = midPoint.x - dx;
        double y1 = midPoint.y - dy;
        double x2 = midPoint.x + dx;
        double y2 = midPoint.y + dy;
        Coordinate p1 = new Coordinate(x1, y1);
        Coordinate p2 = new Coordinate(x2, y2);

        LineSegment normalSegment = new LineSegment(p1, p2);
        return normalSegment;
    }

    /**
     * Sets the points in device space that define the two chords of the arc to build.
     * 
     * @param x1 X ordinate of the arc's start point
     * @param y1 Y ordinate of the arc's start point
     * @param x2 X ordinate of the arc point shared by the two chords
     * @param y2 Y ordinate of the arc point shared by the two chords
     * @param x3 X ordinate of the arc's end point
     * @param y3 Y ordinate of the arc's end point
     */
    public void setPoints( double x1, double y1, double x2, double y2, double x3, double y3 ) {
        this.point1.x = x1;
        this.point1.y = y1;
        this.point2.x = x2;
        this.point2.y = y2;
        this.point3.x = x3;
        this.point3.y = y3;
    }

    public Coordinate getPoint1() {
        return new Coordinate(point1);
    }

    public Coordinate getPoint2() {
        return new Coordinate(point2);
    }

    public Coordinate getPoint3() {
        return new Coordinate(point3);
    }
}
