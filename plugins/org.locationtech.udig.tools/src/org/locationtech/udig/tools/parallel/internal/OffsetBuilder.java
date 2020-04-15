/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.parallel.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.algorithm.LineIntersector;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geomgraph.Position;

//import es.axios.lib.geometry.util.GeometryUtil;
import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;

/**
 * Responsible of calculating the parallel curve.
 * 
 * Receives input coordinates, start position and offset position.
 * Calculate the parallel curve also called the offset curve.
 * 
 * Input coordinates: An array with the coordinates of the geometry.
 *  
 * Start position: The position of where a click have been done,
 * based on the direction of the line. It will be RIGHT or LEFT.
 * 
 * Offset position: Based on the reference line, the position of the parallel curve,
 * that means, if it's inside or outside the reference line.
 * </pre>
 * 
 * </p>
 * 
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class OffsetBuilder {

	/**
	 * The position of the offset curve.
	 */
	public OffsetPosition					currentPosition		= OffsetPosition.POSITION_UPPER;

	private static final double				MIN_CURVE_VERTEX_FACTOR	= 1.0E-6;
	private final double					deprecateValue;
	private double							distance				= 0.0;

	private OffsetVertexList				vertexList;
	
	// compute intersections in full precision, to provide accuracy
	// the points are rounded as they are inserted into the curve line
	private LineIntersector					lineIntersector = new RobustLineIntersector();
	
	private Map<LineSegment, LineSegment>	offsetList				= new LinkedHashMap<LineSegment, LineSegment>();

	private Coordinate						coord0, coord1, coord2;
	private LineSegment						seg0					= new LineSegment();
	private LineSegment						seg1					= new LineSegment();
	private LineSegment						offset0					= new LineSegment();
	private LineSegment						offset1					= new LineSegment();
	private LineSegment						lastOffset				= new LineSegment();

	private int								side					= 0;
	private Boolean							lastOutsideTurn			= null;

	private static PrecisionModel			precisionModel = new PrecisionModel();
	/**
	 * Start position of the offset curve respect the reference line and its
	 * direction.
	 */
	private int								startPosition;
	private Coordinate						newPt;
	private Coordinate						lastCoord				= new Coordinate();

	/**
	 * <p>
	 * The position of the offset.
	 * <li>UPPER means that the generated offset curve will be outside the
	 * reference line turn.</li>
	 * <li>UNDER means that the generated offset curve will be inside the
	 * reference line turn.</li>
	 * </p>
	 */
	public static enum OffsetPosition {
		POSITION_UPPER, POSITION_UNDER,
	}

	/**
	 * <p>
	 * Creates the offset builder.
	 * </p>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param offsetPosition
	 *            The position of the offset(outside curve or inside).
	 * @param startPosition
	 *            The position of the offset respect the segment and its
	 *            direction(Left or right).
	 */
	public OffsetBuilder(OffsetPosition offsetPosition, int startPosition, double errorMargin) {

		this.currentPosition = offsetPosition;
		this.startPosition = startPosition;

		// filletAngleQuantum = Math.PI / 2.0 / 1;
		this.deprecateValue = errorMargin;
	}

	/**
	 * This method handles single points as well as lines. Lines are assumed to
	 * <b>not</b> be closed (the function will not fail for closed lines, but
	 * will generate superfluous line caps).
	 * 
	 * @return a List of Coordinate[]
	 */
	public List<Geometry> getLineCurve(Coordinate[] inputPts, double distance, GeometryFactory geometryFactory)
		throws IllegalArgumentException {

		List<Coordinate> coordList = new ArrayList<Coordinate>();
		List<Geometry> resultGeom = new ArrayList<Geometry>();

		if (distance <= 0.0) {
			resultGeom.add(geometryFactory.createLineString(inputPts));
			return resultGeom;
		}

		init(distance);

		computeParallelLineCurve(inputPts);

		coordList = vertexList.getList();

		// if the original geometry doesn't has intersections, but the new one
		// has self-intersection, the piece of line between intersection must be
		// discarded.
		// if it is closed.
		boolean isClosed = false;
		if (inputPts[0].equals2D(inputPts[inputPts.length - 1])) {

			// the result will be the coordinates that are between the
			// intersection.
			// if there isn't any intersection: get the intersection between the
			// first segment and last segment, calculate its intersection, and
			// add this coordinate as first and last coordinate.
			coordList = closeRing(coordList, geometryFactory);
			isClosed = true;
		}

		List<Coordinate> sourceList = Arrays.asList(inputPts);

		if (currentPosition == OffsetPosition.POSITION_UNDER) {

			sourceList = new ArrayList<Coordinate>();
			for (int i = inputPts.length - 1; i >= 0; i--) {
				sourceList.add(inputPts[i]);
			}
		}

		// apply the post build operations (analysis, remove points, etc...)
		resultGeom = postBuild(coordList, geometryFactory, sourceList, isClosed, resultGeom);

		return resultGeom;
	}

	/**
	 * 
	 * Apply all the post-operations to the preBuild geometry.
	 * 
	 * @param coordList
	 *            The list off coordinates calculated during the
	 *            computeParallelCurve (preBuild geometry).
	 * @param gf
	 *            GeometryFactory.
	 * @param sourceList
	 *            Original geometry's coordinates.
	 * @param isClosed
	 *            True if the result geometry will be a closed geometry.
	 * @param resultGeom
	 *            The list of the resultan geometries.
	 * @return The final list with the resultant geometries.
	 */
	private List<Geometry> postBuild(	List<Coordinate> coordList,
										GeometryFactory gf,
										List<Coordinate> sourceList,
										boolean isClosed,
										List<Geometry> resultGeom) {

		Map<Integer, List<DataSegmentIntersection>> intersectionData = null;

		Coordinate[] resultCoord = coordList.toArray(new Coordinate[coordList.size()]);
		Geometry preBuildGeom = gf.createLineString(resultCoord);

		// before the analysis, apply the "intersectsWithOffset"
		intersectionData = intersectsWithOffset(preBuildGeom, gf, coordList);

		// TODO change the name ???.
		OffsetOrientationAnalyzer ori = new OffsetOrientationAnalyzer(coordList, gf, sourceList, intersectionData);

		if (isClosed) {
			resultGeom = ori.discardClosed();
		} else {
			resultGeom = ori.discardNonClosed();
		}
		return resultGeom;
	}

	/**
	 * Using the offsetList, mount lines that will intersect with the preBuild
	 * geometry. The result of that intersection, if it's a point, multiPoint or
	 * geomCollection, it will be stored for later usage.
	 * 
	 * @param preBuild
	 * @param gf
	 * @param coordList
	 *            Raw result of the builder.
	 */
	private Map<Integer, List<DataSegmentIntersection>> intersectsWithOffset(	Geometry preBuild,
																				GeometryFactory gf,
																				List<Coordinate> coordList) {

		Map<Integer, List<DataSegmentIntersection>> pointsList = new LinkedHashMap<Integer, List<DataSegmentIntersection>>();
		Iterator<Entry<LineSegment, LineSegment>> iterator = offsetList.entrySet().iterator();

		int index = 0;
		// go through the offsetList and intersects with them.
		while (iterator.hasNext()) {

			List<DataSegmentIntersection> relation = getRelationData(iterator, gf, preBuild, index);

			// bound the list of points to an specific index.
			if (!relation.isEmpty()) {
				pointsList.put(index, relation);
			}

			index++;
		}
		return pointsList;
	}

	/**
	 * Retrieve the data, mount the line, and finally intersect it with the
	 * preBuild geometry.
	 * 
	 * @param iterator
	 * @param gf
	 * @param preBuild
	 * @param index
	 * @return
	 */
	private List<DataSegmentIntersection> getRelationData(	Iterator<Entry<LineSegment, LineSegment>> iterator,
															GeometryFactory gf,
															Geometry preBuild,
															int index) {

		List<DataSegmentIntersection> relationSegmIntersect = new ArrayList<DataSegmentIntersection>();

		Entry<LineSegment, LineSegment> item = iterator.next();
		LineSegment key = item.getKey();
		LineSegment value = item.getValue();

		SpecificLineBuilder lineBuilder = new SpecificLineBuilder();

		// mount the line that will intersect with the preBuild geometry.
		LineString subjectLine = lineBuilder.mountTheLine(key, value, gf);

		relationSegmIntersect = getIntersectSegmentsData(subjectLine, preBuild, index, relationSegmIntersect);

		return relationSegmIntersect;
	}

	private List<DataSegmentIntersection> getIntersectSegmentsData(	LineString subjectLine,
																	Geometry preBuild,
																	int index,
																	List<DataSegmentIntersection> relationSegmIntersect) {

		Coordinate[] coords = preBuild.getCoordinates();
		GeometryFactory gf = preBuild.getFactory();

		for (int i = 0; i < coords.length - 2; i++) {
			// don't take into account the actual LINE segment nor one segment
			// just before and after.

			if (i >= (index - 1) && i <= (index + 1))
				continue;

			Coordinate[] part = new Coordinate[2];
			part[0] = coords[i];
			part[1] = coords[i + 1];
			LineString segment = gf.createLineString(part);

			if (subjectLine.intersects(segment)) {

				// get the intersection coordinate
				Coordinate intersectionCoord = subjectLine.intersection(segment).getCoordinate();
				// calculate if this segment is before or after the line.

				boolean isForward = calculateForwardOrBackward(subjectLine, subjectLine.getCoordinates(), index,
							coords.length, coords, gf, intersectionCoord);

				relationSegmIntersect.add(new DataSegmentIntersection(i, intersectionCoord, isForward));
			}
		}
		return relationSegmIntersect;
	}

	private boolean calculateForwardOrBackward(	LineString subjectLine,
												Coordinate[] subjectLineCoord,
												int i,
												int n,
												Coordinate[] inputList,
												GeometryFactory gf,
												Coordinate intersectionCoord) {

		// first, go segment by segment in forward direction and return how many
		// segment does it have gone through until it finds the intersection.

		int forwardCounter = AlgorithmUtils.forwardIntersectedSegmentsCounter(subjectLine, subjectLineCoord, i, n,
					inputList, gf, intersectionCoord);
		// second, do the same, but this time going backward.

		int backwardCounter = AlgorithmUtils.backwardIntersectedSegmentCounter(subjectLine, subjectLineCoord, i, n,
					inputList, gf, intersectionCoord);

		return forwardCounter < backwardCounter;
	}

	/**
	 * Close the ring, it could be an interior ring or an exterior ring.
	 * 
	 * @param arrayList
	 *            The resultant array list with the parallel coordinates.
	 * @param geometryFactory
	 * @return The list of coordinates that form a closed ring.
	 */
	private List<Coordinate> closeRing(List<Coordinate> arrayList, GeometryFactory gf) {

		// check if the first segment and the last segment intersects.
		Coordinate[] firstSeg = new Coordinate[2];
		firstSeg[0] = arrayList.get(0);
		firstSeg[1] = arrayList.get(1);
		LineString first = gf.createLineString(firstSeg);

		Coordinate[] lastSeg = new Coordinate[2];
		lastSeg[0] = arrayList.get(arrayList.size() - 2);
		lastSeg[1] = arrayList.get(arrayList.size() - 1);
		LineString last = gf.createLineString(lastSeg);
		Coordinate intersectionCoord;
		if (first.intersects(last)) {

			intersectionCoord = first.intersection(last).getGeometryN(0).getCoordinate();
			if (!lastSeg[0].equals2D(intersectionCoord) && !lastSeg[1].equals2D(intersectionCoord)) {

				// change the first and the last coordinate.
				arrayList.remove(0);
				arrayList.add(0, intersectionCoord);
				arrayList.remove(arrayList.size() - 1);
				arrayList.add(intersectionCoord);
				return arrayList;
			}
		}

		// get where the segment one and the last segment will intersects.
		intersectionCoord = new Coordinate();
		lineIntersector.computeIntersection(arrayList.get(0), arrayList.get(1), arrayList.get(arrayList.size() - 2), arrayList
					.get(arrayList.size() - 1));
		if (lineIntersector.hasIntersection()) {

			intersectionCoord = lineIntersector.getIntersection(0);
		} else {

			// addCornerPoint(offset0, offset1, false);
			intersectionCoord = GeometryUtil.intersection(arrayList.get(0), arrayList.get(1), arrayList.get(arrayList
						.size() - 2), arrayList.get(arrayList.size() - 1));
		}

		arrayList.remove(0);
		arrayList.add(0, intersectionCoord);
		arrayList.remove(arrayList.size() - 1);
		arrayList.add(intersectionCoord);

		return arrayList;
	}

	private void init(double distance) {

		this.distance = distance;
		vertexList = new OffsetVertexList();
		vertexList.setPrecisionModel(precisionModel);
		/**
		 * Choose the min vertex separation as a small fraction of the offset
		 * distance.
		 */
		vertexList.setMinimumVertexDistance(distance * MIN_CURVE_VERTEX_FACTOR);
	}

	private void computeParallelLineCurve(Coordinate[] inputPts) {

		int n = inputPts.length - 1;

		if (currentPosition == OffsetPosition.POSITION_UPPER) {

			// compute points for left side of line
			initSideSegments(inputPts[0], inputPts[1], startPosition);
			for (int i = 2; i <= n; i++) {
				addNextSegment(inputPts[i], true);
			}
			addLastSegment();
		} else { // When CURRENT_POSITION = OffsetPosition.POSITON_UNDER

			// compute points for right side of line
			initSideSegments(inputPts[n], inputPts[n - 1], startPosition);
			for (int i = n - 2; i >= 0; i--) {
				addNextSegment(inputPts[i], true);
			}
			addLastSegment();
		}
		addLastOffsetToList();
	}

	/**
	 * Add last offset point.
	 */
	private void addLastSegment() {

		if ((lastOutsideTurn != null && lastOutsideTurn) || vertexList.size() == 0) {
			vertexList.addPt(offset1.p0, false);
		}

		vertexList.addPt(offset1.p1, false);
	}

	private void initSideSegments(Coordinate s1, Coordinate s2, int side) {
		this.coord1 = s1;
		this.coord2 = s2;
		this.side = side;
		seg1.setCoordinates(s1, s2);
		computeOffsetSegment(seg1, side, distance, offset1);
	}

	/**
	 * Compute an offset segment for an input segment on a given side and at a
	 * given distance. The offset points are computed in full double precision,
	 * for accuracy.
	 * 
	 * @param seg
	 *            the segment to offset
	 * @param side
	 *            the side of the segment ({@link Position}) the offset lies on
	 * @param distance
	 *            the offset distance
	 * @param offset
	 *            the points computed for the offset segment
	 */
	private void computeOffsetSegment(LineSegment seg, int side, double distance, LineSegment offset) {
		int sideSign = side == Position.LEFT ? 1 : -1;
		double dx = seg.p1.x - seg.p0.x;
		double dy = seg.p1.y - seg.p0.y;
		double len = Math.sqrt(dx * dx + dy * dy);
		// u is the vector that is the length of the offset, in the direction of
		// the segment
		double ux = sideSign * distance * dx / len;
		double uy = sideSign * distance * dy / len;
		offset.p0.x = seg.p0.x - uy;
		offset.p0.y = seg.p0.y + ux;
		offset.p1.x = seg.p1.x - uy;
		offset.p1.y = seg.p1.y + ux;
	}

	private void addNextSegment(Coordinate p, boolean addStartPoint) {
		// coord0-coord1-coord2 are the coordinates of the previous segment and
		// the current one

		coord0 = coord1;
		coord1 = coord2;
		coord2 = p;
		// coord3 = next;
		seg0.setCoordinates(coord0, coord1);
		computeOffsetSegment(seg0, side, distance, offset0);
		seg1.setCoordinates(coord1, coord2);
		computeOffsetSegment(seg1, side, distance, offset1);

		// do nothing if points are equal
		if (coord1.equals(coord2))
			return;

		int orientation = CGAlgorithms.computeOrientation(coord0, coord1, coord2);

		boolean outsideTurn = (orientation == CGAlgorithms.CLOCKWISE && side == Position.LEFT)
					|| (orientation == CGAlgorithms.COUNTERCLOCKWISE && side == Position.RIGHT);
		if (lastOutsideTurn == null) {
			lastOutsideTurn = outsideTurn;
		}

		if (outsideTurn) {
			// when outsideTurn and lastOutsideTurn are different -don't add the
			// p0
			if (lastOutsideTurn) {
				vertexList.addPt(offset0.p0, false);
			}
			addOutsideTurn(addStartPoint);
		} else { // inside turn
			if (vertexList.size() == 0) {
				vertexList.addPt(offset0.p0, false);
			}
			if (lastOutsideTurn) {
				// add random point, because the last point added was marked
				// with vertexList.addPt(xxxx,TRUE)
				// so this point will be deleted.
				vertexList.addPt(offset0.p0, false);
			}
			addInsideTurn();
		}
		// set the last offset
		lastOffset.setCoordinates(offset0);
		lastCoord.setCoordinate(coord0);

		// create the key and value.
		// key
		LineSegment referencedFromList = new LineSegment();
		referencedFromList.setCoordinates(vertexList.getSecondToLastItem(), vertexList.getLastItem());
		// value
		LineSegment fromOffset = new LineSegment();
		fromOffset.setCoordinates(offset0);

		offsetList.put(referencedFromList, fromOffset);
		lastOutsideTurn = outsideTurn;
	}

	private void addLastOffsetToList() {

		// key
		LineSegment derivatedFromList = new LineSegment();
		derivatedFromList.setCoordinates(vertexList.getSecondToLastItem(), vertexList.getLastItem());
		// value
		LineSegment referenceOffset = new LineSegment();
		referenceOffset.setCoordinates(offset0);

		offsetList.put(derivatedFromList, referenceOffset);
	}

	/**
	 * When 2 offset segment intersects or will intersect if they would be
	 * larger, calculate the intersection and add this coordinate.
	 * 
	 * @param offset0
	 * @param offset1
	 * @param b
	 */
	private void addCornerPoint(LineSegment offset0, LineSegment offset1, boolean b) {
		Coordinate pt = new Coordinate();

		pt = GeometryUtil.intersection(offset0.p0, offset0.p1, offset1.p0, offset1.p1);
		vertexList.addPt(pt, b);
	}

	private void addOutsideTurn(boolean addStartPoint) {

		if (addStartPoint) {
			vertexList.addPt(offset0.p1, false);
		}
		addCornerPoint(offset0, offset1, true);

	}

	private void addInsideTurn() {

		/**
		 * add intersection point of offset segments (if any)
		 */
		if (!linesParallel(offset0.p0, offset0.p1, offset1.p0, offset1.p1, deprecateValue)) {
			newPt = new Coordinate();

			newPt = GeometryUtil.intersection(offset0.p0, offset0.p1, offset1.p0, offset1.p1);

			vertexList.addPt(newPt, false);
		} else {
			// TODO calculate the intersection, and if the resultant point is
			// contained in any of the 2 offset, or near of those offset, add
			// that point.
			// If not... TODO find a solution if not.
			newPt = GeometryUtil.intersection(offset0.p0, offset0.p1, offset1.p0, offset1.p1);

			GeometryFactory gf = new GeometryFactory();
			Point closestPoint = gf.createPoint(offset0.closestPoint(newPt));

			// calculate the distance
			double distance1 = offset0.distance(closestPoint.getCoordinate());
			double distance2 = offset1.distance(closestPoint.getCoordinate());

			if (distance1 <= deprecateValue || distance2 <= deprecateValue) {
				vertexList.addPt(newPt, false);
			} else {
				// TODO and when the distance is larger ?

			}
		}
	}

	/**
	 * <pre>
	 * NON ROBUST.
	 * Calculate if the given lines are parallel or not.
	 * </pre>
	 * 
	 * @param line1P1
	 * @param line1P2
	 * @param line2P1
	 * @param line2P2
	 * @return True if they are parallel or near to be parallel.
	 */
	private boolean linesParallel(	Coordinate line1P1,
									Coordinate line1P2,
									Coordinate line2P1,
									Coordinate line2P2,
									double despreciate_value) {
		double B1 = line1P1.x - line1P2.x;
		double B2 = line2P1.x - line2P2.x;
		double A1 = line1P2.y - line1P1.y;
		double A2 = line2P2.y - line2P1.y;

		double det = Math.abs(A1 * B2 - A2 * B1);
		if (det <= despreciate_value) {
			// Lines are parallel
			return true;
		}
		return false;
	}

}
