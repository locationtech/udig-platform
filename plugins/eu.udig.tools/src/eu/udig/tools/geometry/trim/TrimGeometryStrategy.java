/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package eu.udig.tools.geometry.trim;

import java.text.MessageFormat;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

import eu.udig.tools.internal.i18n.Messages;

/**
 * Performs the trimming of a LineString or MultiLineString using a provided
 * LineString as cutting edge.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.1.0
 */
public class TrimGeometryStrategy {

	private LineString	trimmingLine;

	public TrimGeometryStrategy(LineString trimmingLine) {
		this.trimmingLine = trimmingLine;
	}

	public static Geometry trim(Geometry input, LineString trimmingLine) {
		TrimGeometryStrategy op = new TrimGeometryStrategy(trimmingLine);
		Geometry trimmed = op.trim(input);
		return trimmed;
	}

	public Geometry trim(final Geometry original) {
		Geometry trimmed;
		if (original == null || original.isEmpty()) {
			return original;
		}
		if (original instanceof LineString) {
			trimmed = trimLine((LineString) original, trimmingLine);
		} else if (original instanceof MultiLineString) {
			MultiLineString input = (MultiLineString) original;
			GeometryFactory gf = input.getFactory();

			int numGeometries = input.getNumGeometries();
			LineString[] lines = new LineString[numGeometries];
			for (int i = 0; i < numGeometries; i++) {
				LineString part = (LineString) input.getGeometryN(i);
				LineString trimmedPart = trimLine(part, trimmingLine);
				lines[i] = trimmedPart;
			}
			trimmed = gf.createMultiLineString(lines);
		} else {

			throw new IllegalArgumentException(Messages.TrimGeometryStrategy_defined_for_line_geometries);
		}
		return trimmed;
	}

	/**
	 * @param original
	 * @param trimmingLine
	 * @return if <code>original</code> intersects <code>trimmingLine</code>
	 *         at a single point, the result of cutting off the part of
	 *         <code>original</code> that lies at the right of
	 *         <code>trimmingLine</code>. If they do not intersect, returns
	 *         <code>original</code>.
	 * @throws IllegalArgumentException
	 */
	private LineString trimLine(final LineString original, final LineString trimmingLine)
		throws IllegalArgumentException {
		Geometry intersectGeom = original.intersection(trimmingLine);
		if (intersectGeom.isEmpty()) {
			return original;
		}

		assert intersectGeom instanceof Point : "The intersection between trim line and the feature must be a point."; 

		Point intersection = (Point) intersectGeom;
		Coordinate intersectionPoint = intersection.getCoordinate();

		final Coordinate lineFrom = getCoordinateBeforePoint(trimmingLine, intersectionPoint);
		// if it happened that the intersection point is the starting point of
		// the trimming line
		// then lineTo is the next coordinate. Otherwise it is the intersection
		// point itself
		final Coordinate lineTo = intersectionPoint.equals2D(lineFrom) ? trimmingLine.getCoordinateN(1)
					: intersectionPoint;

		// split the line at the intersection point
		Geometry difference = original.difference(trimmingLine);

		LineString splitLine1;
		LineString splitLine2;
		if (difference instanceof MultiLineString) {
			splitLine1 = (LineString) difference.getGeometryN(0);
			splitLine2 = (LineString) difference.getGeometryN(1);
		} else if (difference instanceof LineString) {
			// original touches trimmingLine but does not crosses it
			splitLine1 = (LineString) difference;
			splitLine2 = difference.getFactory().createLineString(new Coordinate[0]);
		} else {
			throw new IllegalStateException(Messages.TrimGeometryStrategy_difference_unknown_type + difference);
		}

		Coordinate firstLinePoint = getCoordinateBeforePoint(splitLine1, intersectionPoint);
		if (firstLinePoint.equals2D(intersectionPoint)) {
			// same case as the comment for line1, or computeOrientation will
			// return COLLINEAR,
			// and we'll have no way to tell wether the line is at the right or
			// the left
			firstLinePoint = splitLine1.getCoordinateN(1);
		}

		final int firstLineOrientation = CGAlgorithms.computeOrientation(lineFrom, lineTo, firstLinePoint);

		LineString lineAtTheRight;

		// return the segment at the left of the intersection point
		if (CGAlgorithms.CLOCKWISE == firstLineOrientation) {
			lineAtTheRight = splitLine2;
		} else {
			lineAtTheRight = splitLine1;
		}
		return lineAtTheRight;
	}

	/**
	 * Traverses the <code>line</code> in its digitizing order and returns the
	 * <code>Coordinate</code> right before the <code>intersectionPoint</code>
	 * on the segment that containst it.
	 * 
	 * @param line
	 * @param pointInLine
	 * @return
	 */
	private Coordinate getCoordinateBeforePoint(final LineString line, final Coordinate pointInLine) {
		Coordinate[] coordinates = line.getCoordinates();
		Coordinate[] segment = new Coordinate[2];

		final GeometryFactory gf = line.getFactory();
		final double HACK_DISTANCE = 0.0000001;
		for (int i = 1; i < coordinates.length; i++) {
			segment[0] = coordinates[i - 1];
			segment[1] = coordinates[i];
			// CGAlgorithms.isOnLine is too precise and due to round off errors
			// almost
			// never returns true, even though intersectionPoint should be
			// guaranteed
			// to be over the line as it was produced by intersecting both lines
			// so we're using this hack to determine if the point lies over the
			// segment
			Geometry lineSegment = gf.createLineString(segment);
			lineSegment = lineSegment.buffer(HACK_DISTANCE, 2);
			Point point = gf.createPoint(pointInLine);
			Geometry intersection = lineSegment.intersection(point);
			if (point.equals(intersection)) {
				return segment[0];
			}
		}

		final String msg = MessageFormat.format(Messages.TrimGeometryStrategy_point_not_on_line, pointInLine, line);
		throw new IllegalArgumentException(msg);
	}
}
