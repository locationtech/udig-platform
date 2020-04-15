/* uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;

/**
 * <p>
 * 
 * <pre>
 * Strategy class that will add a vertex to a geometry border.
 * The following conditions must be fulfilled:
 * -The border must touches with a geometry that has been split.
 * -The same split line must intersect the
 * 
 * Resume:
 * -Get the border of the geometry.
 * -Analyze the border and get the intersection between 
 * a geometry and a line, and add that point as vertex to the geometry.
 * -Do the same for each hole, if the geometry has holes.
 * </pre>
 * 
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
public final class VertexStrategy {

	/**
	 * <p>
	 * 
	 * <pre>
	 * Responsible of creating a new geometry that will be the given geometry
	 * with an extra vertex. That vertex will be the point where the given
	 * geometry and the line intersect.
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param geomToAddVertex
	 *            The geometry that will be modified.
	 * @param line
	 *            The split line.
	 * @param geomNeighbor
	 *            The neighbor geometry list, those one had been modified by the
	 *            split operation.
	 * @return The geometry with the vertex added.
	 */
	public static Geometry addIntersectionVertex(Geometry geomToAddVertex, Geometry line, List<Geometry> geomNeighbor) {

		assert line instanceof LineString || line instanceof MultiLineString : "Split line must be a line."; //$NON-NLS-1$
		assert geomToAddVertex instanceof Polygon || geomToAddVertex instanceof LineString
					|| geomToAddVertex instanceof MultiPolygon || geomToAddVertex instanceof MultiLineString : "Input geometry has unexpected class."; //$NON-NLS-1$

		Geometry result = null;

		if (geomToAddVertex instanceof Polygon || geomToAddVertex instanceof MultiPolygon) {

			Polygon polygon;
			// cast to polygon, we know that geomToAddVertex only has one
			// geometry.
			if (geomToAddVertex instanceof MultiPolygon) {
				MultiPolygon mpolygon = (MultiPolygon) geomToAddVertex;
				polygon = (Polygon) mpolygon.getGeometryN(0);
			} else {
				polygon = (Polygon) geomToAddVertex;

			}
			result = addIntersectionVertexToPolygon(polygon, line, geomNeighbor);
		} else if (geomToAddVertex instanceof LineString || geomToAddVertex instanceof MultiLineString) {

			result = addIntersectionVertexToLine(geomToAddVertex, line);
		}

		return result;
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Responsible of creating a new geometry that will be the given geometry
	 * with an extra vertex. That vertex will be the point where the passed
	 * geometry and the line intersects. 
	 * 
	 * Get the coordinates of the exterior boundary of the geometry to add vertex.
	 * The coordinates are returned in an sorted collection. Start
	 * creating a LineSegment between the first and the second coordinates, then
	 * check if that lineSegment intersect with our line. 
	 * If it doesn't intersect, add the coordinate to the list, and if it intersects 
	 * add to the list.
	 * 
	 * Get the coordinates of the interior boundary (holes) if it has.
	 * With each hole do the same as done with the exterior boundary.
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param polygonToAddVertex
	 *            The polygon geometry.
	 * @param line
	 *            The split line.
	 * @param geomNeighborList
	 *            The list with neighbor geometries.
	 * @return the geomToAddVertex with the intersection vertex added.
	 */
	private static Geometry addIntersectionVertexToPolygon(	Geometry polygonToAddVertex,
															Geometry line,
															List<Geometry> geomNeighborList) {

		Geometry result = null;

		Geometry[] holes = null;
		LinearRing[] holesRing = null;
		List<Coordinate> shellCoordinates = null;
		List<LinearRing> holesList = null;
		GeometryFactory fc = polygonToAddVertex.getFactory();

		// cast to polygon and get the exterior boundary.
		Polygon polygonGeom = (Polygon) polygonToAddVertex;
		Geometry boundary = polygonGeom.getExteriorRing();

		Coordinate[] shellClosed = null;
		Coordinate[] boundaryCoordinates = boundary.getCoordinates();

		// for each neighbor
		for (Geometry neighbor : geomNeighborList) {

			if (boundary.touches(neighbor)) {
				// store the modified boundaryCoordinates for add more vertex if
				// there are.
				shellCoordinates = addIntersection(boundaryCoordinates, line, fc, neighbor);
				boundaryCoordinates = shellCoordinates.toArray(new Coordinate[shellCoordinates.size()]);
				shellClosed = boundaryCoordinates;
			} else {
				shellClosed = boundaryCoordinates;
			}
		}

		holes = getInteriorHoles(polygonGeom);

		// if not null, the geometry has holes.
		if (holes.length > 0) {

			holesList = addIntersectionToHoles(holes, line, fc, geomNeighborList);
			holesRing = holesList.toArray(new LinearRing[holesList.size()]);
		}

		// form a closed linearRing.
		shellClosed = GeometryUtil.closeGeometry(shellClosed);
		LinearRing shellRing = fc.createLinearRing(shellClosed);

		result = fc.createPolygon(shellRing, holesRing);

		return result;
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Responsible of creating a new geometry that will be the given geometry
	 * with an extra vertex. That vertex will be the point where the passed
	 * geometry and the line intersects. 
	 * 
	 * Get the coordinates of the LineString to add vertex.
	 * The coordinates are returned in an sorted collection. Start
	 * creating a LineSegment between the first and the second coordinates, then
	 * check if that lineSegment intersect with our line. 
	 * If it doesn't intersect, add the coordinate to the list, and if it intersects 
	 * add to the list.
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param lineToAddVertex
	 *            The line to add vertex.
	 * @param line
	 *            The split line.
	 * @return the geomToAddVertex with the intersection vertex added.
	 */
	private static Geometry addIntersectionVertexToLine(Geometry lineToAddVertex, Geometry line) {

		Geometry result = null;

		List<Coordinate> shellCoordinates = null;
		GeometryFactory fc = lineToAddVertex.getFactory();
		Geometry boundary = lineToAddVertex.getBoundary();

		Coordinate[] boundaryCoordinates = boundary.getCoordinates();

		// add the intersection vertex to the shellCoordinates.
		shellCoordinates = addIntersection(boundaryCoordinates, line, fc, line);

		Coordinate[] shellClosed = shellCoordinates.toArray(new Coordinate[shellCoordinates.size()]);
		shellClosed = GeometryUtil.closeGeometry(shellClosed);

		result = fc.createLineString(shellClosed);

		return result;
	}

	/**
	 * For each hole, get it and if it touches with the neighbor geometry add
	 * the intersection vertex if exist to the hole boundary. Then add this hole
	 * to the holeRing list.
	 * 
	 * @param holes
	 *            Array of holes.
	 * @param line
	 *            The split line.
	 * @param fc
	 *            Geometry factory.
	 * @param geomNeighborList
	 *            The list with neighbor geometries.
	 * @return A list containing the modified holes.
	 */
	private static List<LinearRing> addIntersectionToHoles(	Geometry[] holes,
															Geometry line,
															GeometryFactory fc,
															List<Geometry> geomNeighborList) {

		List<LinearRing> holesRing = new ArrayList<LinearRing>();
		List<Coordinate> holeCoordinates = null;
		for (int i = 0; i < holes.length; i++) {

			Geometry eachHole = fc.createLinearRing(holes[i].getCoordinates());
			Coordinate[] boundaryCoordinates = eachHole.getCoordinates();
			Coordinate[] holeClosed = null;

			for (Geometry neighbor : geomNeighborList) {

				// if it touch, add the intersection vertex.
				if (eachHole.touches(neighbor)) {

					// store the modified boundaryCoordinates for add more
					// vertex if there are.
					holeCoordinates = addIntersection(boundaryCoordinates, line, fc, neighbor);
					boundaryCoordinates = holeCoordinates.toArray(new Coordinate[holeCoordinates.size()]);
					boundaryCoordinates = GeometryUtil.closeGeometry(boundaryCoordinates);
					holeClosed = boundaryCoordinates;
				} else {
					holeClosed = boundaryCoordinates;
				}
			}

			holesRing.add(fc.createLinearRing(holeClosed));
		}
		return holesRing;
	}

	/**
	 * Go through boundaryCoordinates and add a coordinate were the split line
	 * intersect with the boundaryCoordinates.
	 * 
	 * @param boundaryCoordinates
	 *            The boundary of a geometry, could be the exterior or interior
	 *            boundary.
	 * @param line
	 *            The split line.
	 * @param fc
	 *            The geometry factory.
	 * @param neighbor
	 *            The neighbor. The new point will touch this geometry.
	 * @return An array list with the coordinates.
	 */
	private static List<Coordinate> addIntersection(Coordinate[] boundaryCoordinates,
													Geometry line,
													GeometryFactory fc,
													Geometry neighbor) {

		List<Coordinate> resultCoordinates = new ArrayList<Coordinate>();
		int i = 0;
		resultCoordinates.add(boundaryCoordinates[i]);

		for (i = 0; i < boundaryCoordinates.length - 1; i++) {
			// Create the line segment between one coordinate and the next one.
			Coordinate[] segmentCoordinates = new Coordinate[2];
			segmentCoordinates[0] = boundaryCoordinates[i];
			segmentCoordinates[1] = boundaryCoordinates[i + 1];
			LineString segment = fc.createLineString(segmentCoordinates);
			// if intersect, this is the vertex we need to add, so add it to the
			// result coordinates list.
			if (segment.intersects(line)) {

				Geometry point = segment.intersection(line);

				Geometry[] sorted = sortPoints(point, segment);
				// add all the points.
				for (int j = 0; j < sorted.length; j++) {

					resultCoordinates = addPoint(segment, neighbor, sorted[j], resultCoordinates);
				}
			}
			// Don't add existing vertex twice
			if (!resultCoordinates.contains(boundaryCoordinates[i + 1])) {
				resultCoordinates.add(boundaryCoordinates[i + 1]);
			}
		}

		return resultCoordinates;
	}

	/**
	 * Will return the coordinates sorted, that relation depends on the
	 * lineString direction and the coordinates. Calculate which coordinate is
	 * the nearest to the beginning of the segment until the array is sorted.
	 * 
	 * @param point
	 * @param segment
	 * @return An array of points, all of them sorted respect the segment
	 *         orientation.
	 */
	private static Geometry[] sortPoints(Geometry point, LineString segment) {

		Geometry[] sorted = new Geometry[point.getNumGeometries()];
		// fill the array
		for (int i = 0; i < point.getNumGeometries(); i++) {
			sorted[i] = point.getGeometryN(i);
		}

		Geometry temp = null;
		double dist1;
		double dist2;
		// sort the array
		for (int i = sorted.length - 1; i > 0; i--) {

			for (int j = 0; j < i; j++) {

				Coordinate firstLineCoord = segment.getCoordinateN(0);
				Coordinate secondLineCoord = segment.getCoordinateN(1);
				Coordinate firstPoint = sorted[j].getCoordinate();
				Coordinate secondPoint = sorted[j + 1].getCoordinate();

				// if the segment isn't vertical
				// calculate the distance of each point respect the beginning of
				// the segment.
				if ((firstLineCoord.x - secondLineCoord.x) != 0) {

					dist1 = firstLineCoord.x - firstPoint.x;
					dist2 = firstLineCoord.x - secondPoint.x;
				} else {

					dist1 = firstLineCoord.y - firstPoint.y;
					dist2 = firstLineCoord.y - secondPoint.y;
				}
				if (Math.abs(dist1) > Math.abs(dist2)) {
					temp = sorted[j];
					sorted[j] = sorted[j + 1];
					sorted[j + 1] = temp;
				}
			}
		}
		return sorted;
	}

	/**
	 * Add a vertex to the coordinate list if the segment intersect with the
	 * neighbor and if the segment with the neighbor intersection has a
	 * dimension bigger than 0.
	 * 
	 * @param segment
	 *            The analyzed segment.
	 * @param neighbor
	 *            The neighbor boundary.
	 * @param point
	 *            The intersection point between the segment and the boundary.
	 * @param resultCoordinates
	 *            The list with the geomToAddVertex coordinates.
	 * @return The list with geomToAddVertex coordinates with/without the added
	 *         vertex.
	 */
	private static List<Coordinate> addPoint(	Geometry segment,
												Geometry neighbor,
												Geometry point,
												List<Coordinate> resultCoordinates) {

		// if the intersection dimension is 1, the segment and
		// the neighbor boundary are touching.
		if (segment.intersects(neighbor) && (segment.intersection(neighbor).getDimension() > 0)) {

			Coordinate coord = point.getCoordinate();
			// Don't add existing vertex twice
			if (!resultCoordinates.contains(coord)) {
				resultCoordinates.add(coord);
			}
		}

		return resultCoordinates;
	}

	/**
	 * If the geometry has holes, will return and array of geometries with all
	 * the holes. If it hasn't holes will return an empty array.
	 * 
	 * 
	 * @param geomToAddVertex
	 *            The geometry to analyze
	 * @return Array with holes if there are, otherwise an empty array.
	 */
	private static Geometry[] getInteriorHoles(Geometry geomToAddVertex) {

		Geometry[] holes = new Geometry[0];
		// get the interior rings.
		Polygon polygon = (Polygon) geomToAddVertex;

		holes = new Geometry[polygon.getNumInteriorRing()];
		for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

			Geometry interiorRing = polygon.getInteriorRingN(j);
			holes[j] = interiorRing;
		}

		return holes;
	}

}
