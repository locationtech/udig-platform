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
package org.locationtech.udig.tools.parallel.internal;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.geomgraph.Position;

//import es.axios.udig.ui.editingtools.precisionparallels.internal.OffsetBuilder.OffsetPosition;
import org.locationtech.udig.tools.parallel.internal.OffsetBuilder.OffsetPosition;

/**
 * The context of the parallel.
 * 
 * Stores the necessary parameters to draw a parallel.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class ParallelContext extends PrecisionToolsContext {

	// Context parameters.
	private EditGeom			referenceLine		= null;
	private SimpleFeature		referenceFeature	= null;
	private Coordinate[]		inputCoordinates	= null;
	private List<Geometry>		outputCoordinates	= null;
	private OffsetPosition		offsetPosition		= null;

	// TODO change the name for one more explainable.
	private static final double	DEPRECIATE_VALUE	= 6.0E-3;
	/**
	 * Start position of the parallel curve respect the reference line and its
	 * direction.
	 */
	private int					startPosition		= Position.LEFT;
	private double				distance			= 0;
	private String				errorMessage		= "";				//$NON-NLS-1$

	/**
	 * Initializes its context. Sets the initial data to null. Set the parallel
	 * state waiting.
	 * 
	 */
	@Override
	public synchronized void initContext() {

		initialCoordinate = null;
		referenceLine = null;
		referenceFeature = null;
		reverse = false;
		distanceCoorX = 0;
		distanceCoorY = 0;
		referenceCoor = null;
		length = null;
		units = null;
		previousMode = PrecisionToolsMode.WAITING;
		mode = PrecisionToolsMode.WAITING;
		update(UPDATE_LAYER);
	}

	/**
	 * Get the referenceLine which parallel will be based on.
	 * 
	 * @return
	 */
	public EditGeom getReferenceLine() {

		return referenceLine;
	}

	/**
	 * Get the reference feature which parallel will be based on.
	 * 
	 * @return
	 */
	public synchronized SimpleFeature getReferenceFeature() {

		return referenceFeature;
	}

	/**
	 * Set the reference feature. Also stores its coordinates on
	 * {@link #inputCoordinates}.ç
	 * 
	 * @param feature
	 *            The reference feature.
	 */
	public synchronized void setReferenceFeature(SimpleFeature feature, Coordinate clickCoordinate) {

		assert feature != null;

		this.referenceFeature = feature;

		inputCoordinates = getLineCoordinates(clickCoordinate);
		// once we get the coordinates, have to remove the repeated coordinates,
		// and also remove the coordinates of a straight segment.
		inputCoordinates = CoordinateArrays.removeRepeatedPoints(inputCoordinates);
		// if there are more than 3 coordinates.
		if (inputCoordinates.length > 2) {
			removeVertexOfTheSameLine(inputCoordinates);
		}
		update(UPDATE_LAYER);
	}

	/**
	 * <pre>
	 * Removes the insignificant vertex.
	 * Those vertex are contained in a straight line. 
	 * A straight line must be a line with only 2 vertex, one at the start
	 * and another at the end, if there are more vertex between them, remove it.
	 * </pre>
	 * 
	 * @param inputCoordinates
	 */
	private void removeVertexOfTheSameLine(Coordinate[] inputCoordinates) {

		List<Coordinate> removedCoordinates = new ArrayList<Coordinate>();
		List<Coordinate> initialCoordinates = new ArrayList<Coordinate>();
		int length = inputCoordinates.length;
		for (int i = 0; i < length - 2; i++) {

			initialCoordinates.add(inputCoordinates[i]);
			if (sameTangent(inputCoordinates, i)) {
				// add to the coordinate i+1 to the removed list.
				removedCoordinates.add(inputCoordinates[i + 1]);
			}
		}

		if (length > 2) {

			initialCoordinates.add(inputCoordinates[length - 2]);
			initialCoordinates.add(inputCoordinates[length - 1]);
		}

		initialCoordinates.removeAll(removedCoordinates);

		this.inputCoordinates = initialCoordinates.toArray(new Coordinate[initialCoordinates.size()]);
	}

	/**
	 * Calculate the tangents of the segment (i and i +1) and segment (i+1 and
	 * i+2) and if they are the same return true, otherwise return false. If the
	 * number is nearly the same, the difference is depreciated
	 * 
	 * @param inputCoordinates
	 * @param i
	 * @return
	 */
	private boolean sameTangent(Coordinate[] inputCoordinates, int i) {
		//TODO do the same thing like split is doing.
		Coordinate[] newCoor;
		// calculate the tangent of the first segment. (i and i+1)
		newCoor = new Coordinate[2];
		newCoor[0] = inputCoordinates[i];
		newCoor[1] = inputCoordinates[i + 1];

		double dx1 = newCoor[1].x - newCoor[0].x;
		double dy1 = newCoor[1].y - newCoor[0].y;
		double tangent = dx1 / dy1;

		// calculate the tangent of the second segment (i+1 and i+2)
		newCoor = new Coordinate[2];
		newCoor[0] = inputCoordinates[i + 1];
		newCoor[1] = inputCoordinates[i + 2];

		double dx2 = newCoor[1].x - newCoor[0].x;
		double dy2 = newCoor[1].y - newCoor[0].y;
		double tangentToCompare = dx2 / dy2;

		double diff = Math.abs(tangent - tangentToCompare);

		return (diff < DEPRECIATE_VALUE) ? true : false;

	}

	/**
	 * Set the initial coordinate. And also calculate the parallel.
	 * 
	 * @param coordinate
	 */
	@Override
	public synchronized void setInitialCoordinate(Coordinate coordinate) {

		this.initialCoordinate = coordinate;

		try {
			calculateParallelCurve();
			update(UPDATE_LAYER);
		} catch (IllegalArgumentException iae) {
			// update with an error message.
			errorMessage = iae.getMessage();
			setMode(PrecisionToolsMode.ERROR);
			update(UPDATE_ERROR);
		}
	}

	/**
	 * Change the current position of the parallel preview respect the reference
	 * line. Position will be upper or under.
	 */
	public synchronized void changePosition() {

		if (OffsetPosition.POSITION_UNDER.equals(offsetPosition)) {

			offsetPosition = OffsetPosition.POSITION_UPPER;
		} else if (OffsetPosition.POSITION_UPPER.equals(offsetPosition)) {

			offsetPosition = OffsetPosition.POSITION_UNDER;
		}
		try {
			calculateParallelCurve(offsetPosition, startPosition);
			update(UPDATE_LAYER);
		} catch (IllegalArgumentException iae) {
			// update with an error message.
			errorMessage = iae.getMessage();
			setMode(PrecisionToolsMode.ERROR);
			update(UPDATE_ERROR);
		}

	}

	/**
	 * <p>
	 * Calculate the parallel curve (offset curve).
	 * </p>
	 * <p>
	 * Given the offset position and the start position will calculate the
	 * offset curve taking on account only the new position. Distance will be
	 * the same as last time.
	 * </p>
	 * 
	 * @param offsetPosition2
	 * @param startPosition2
	 */
	private void calculateParallelCurve(OffsetPosition offsetPosition2, int startPosition2)
		throws IllegalArgumentException {

		assert referenceFeature != null;
		assert inputCoordinates.length > 1 : "At least 2 coordinate must be"; //$NON-NLS-1$

		OffsetBuilder builder = new OffsetBuilder(offsetPosition2, startPosition2, DEPRECIATE_VALUE);

		outputCoordinates = builder.getLineCurve(inputCoordinates, distance, ((Geometry) referenceFeature
					.getDefaultGeometry()).getFactory());

		// OffsetBuilderPlus builder2 = new OffsetBuilderPlus((Geometry)
		// referenceFeature.getDefaultGeometry());
		// outputCoordinates = builder2.getLineCurve(distance);
	}

	/**
	 * <p>
	 * Calculate the parallel curve (offset curve).
	 * </p>
	 * <p>
	 * First calculate the distance between the initial point and the nearest
	 * segment. Second set the offset position and start position respect the
	 * reference feature. At last, calculates the offset curve and stores the
	 * given coordinates on {@link #outputCoordinates}.
	 * </p>
	 */
	private void calculateParallelCurve() throws IllegalArgumentException {

		assert referenceFeature != null;
		assert inputCoordinates.length > 1 : "At least 2 coordinate must be"; //$NON-NLS-1$

		distance = calculateDistanceAndCurrentPostion();

		OffsetBuilder builder = new OffsetBuilder(offsetPosition, startPosition, DEPRECIATE_VALUE);

		outputCoordinates = builder.getLineCurve(inputCoordinates, distance, ((Geometry) referenceFeature
					.getDefaultGeometry()).getFactory());

		// OffsetBuilderPlus builder2 = new OffsetBuilderPlus((Geometry)
		// referenceFeature.getDefaultGeometry());
		// outputCoordinates = builder2.getLineCurve(distance);
	}

	/**
	 * Set the new distance of the parallel curve and calculate the resultant
	 * parallel curve.
	 * 
	 * @param distance
	 *            The new distance of the curve.
	 */
	public void calculateParallelCurve(Double distance) throws IllegalArgumentException {

		assert referenceFeature != null;
		assert inputCoordinates.length > 1 : "At least 2 coordinate must be"; //$NON-NLS-1$

		this.distance = distance;

		OffsetBuilder builder = new OffsetBuilder(offsetPosition, startPosition, DEPRECIATE_VALUE);

		outputCoordinates = builder.getLineCurve(inputCoordinates, this.distance, ((Geometry) referenceFeature
					.getDefaultGeometry()).getFactory());
		// OffsetBuilderPlus builder2 = new OffsetBuilderPlus((Geometry)
		// referenceFeature.getDefaultGeometry());
		// outputCoordinates = builder2.getLineCurve(distance);

	}

	/**
	 * <p>
	 * Calculate the distance between the initial point and its closest line
	 * segment.
	 * </p>
	 * <p>
	 * Calculate the distance of each segment of the reference line with the
	 * provided point. Once it found the nearest segment, it compute the
	 * orientation of the provided point.
	 * </p>
	 * <p>
	 * Calculate the offsetPosition {@link OffsetBuilder.OffsetPosition}
	 * <li>UPPER means that the generated offset curve will be outside the
	 * reference line turn.</li>
	 * <li>UNDER means that the generated offset curve will be inside the
	 * reference line turn.</li>
	 * </p>
	 * <p>
	 * Calculate the start position respect the reference line segment and its
	 * direction.
	 * <li>LEFT means that if you go from reference segment point 0 to point 1,
	 * the start point is located at the left.</li>
	 * <li>RIGHT means that if you go from reference segment point 0 to point 1,
	 * the start point is located at the right.</li>
	 * </p>
	 * 
	 * @return the distance
	 */
	private double calculateDistanceAndCurrentPostion() {

		LineSegment seg = new LineSegment();
		LineSegment closestSeg = new LineSegment();
		double distance, closestDistance = Double.MAX_VALUE;

		// get the closest segment.
		for (int i = 0; i < inputCoordinates.length - 1; i++) {
			seg.setCoordinates(inputCoordinates[i], inputCoordinates[i + 1]);
			distance = CGAlgorithms.distancePointLine(initialCoordinate, inputCoordinates[i], inputCoordinates[i + 1]);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestSeg = new LineSegment(inputCoordinates[i], inputCoordinates[i + 1]);
			}
		}
		startPosition = Position.LEFT;
		int segmentOrientation = CGAlgorithms.computeOrientation(closestSeg.p0, closestSeg.p1, initialCoordinate);
		// offset position respect the first segment and the initial point.
		// Useful when is only one segment.
		offsetPosition = (segmentOrientation == -1) ? OffsetPosition.POSITION_UNDER : OffsetPosition.POSITION_UPPER;

		int refLineOrientation, length;
		boolean outsideTurn = true;
		length = inputCoordinates.length;
		if (length > 2) {

			if (segmentOrientation == 1) {
				refLineOrientation = CGAlgorithms.computeOrientation(inputCoordinates[0], inputCoordinates[1],
							inputCoordinates[2]);
				outsideTurn = (refLineOrientation == CGAlgorithms.CLOCKWISE && 1 == Position.LEFT)
							|| (refLineOrientation == CGAlgorithms.COUNTERCLOCKWISE && 1 == Position.RIGHT);

				offsetPosition = (outsideTurn) ? OffsetPosition.POSITION_UPPER : OffsetPosition.POSITION_UNDER;
				startPosition = (outsideTurn) ? Position.LEFT : Position.RIGHT;
			} else {
				refLineOrientation = CGAlgorithms.computeOrientation(inputCoordinates[length - 1],
							inputCoordinates[length - 2], inputCoordinates[length - 3]);
				outsideTurn = (refLineOrientation == CGAlgorithms.CLOCKWISE && 1 == Position.LEFT)
							|| (refLineOrientation == CGAlgorithms.COUNTERCLOCKWISE && 1 == Position.RIGHT);

				offsetPosition = (outsideTurn) ? OffsetPosition.POSITION_UPPER : OffsetPosition.POSITION_UNDER;
				startPosition = (outsideTurn) ? Position.RIGHT : Position.LEFT;
			}

		}
		return closestDistance;
	}

	/**
	 * Get the coordinates of the offset curve.
	 * 
	 * @return
	 */
	public List<Geometry> getOutputCoordinates() {

		return outputCoordinates;
	}

	/**
	 * Get an array with the coordinates of the reference line.
	 * 
	 * @return
	 */
	private Coordinate[] getLineCoordinates(Coordinate clickCoordinate) {

		Geometry geom = (Geometry) referenceFeature.getDefaultGeometry();

		if (geom.getNumGeometries() > 1) {
			geom = getGeometry(geom, clickCoordinate);
		}

		return geom.getCoordinates();
	}

	private Geometry getGeometry(Geometry multiGeom, Coordinate clickCoordinate) {

		List<?> linesList = LineStringExtracter.getLines(multiGeom);
		Geometry geom = null, bboxGeometry = null;

		int snapRadious = 5;
		Point point = handler.getContext().worldToPixel(clickCoordinate);
		// if snap is on, set an accurate snapRadious to use later for getting
		// its area.
		if (!SnapBehaviour.OFF.equals(PreferenceUtil.instance().getSnapBehaviour())) {
			snapRadious = PreferenceUtil.instance().getSnappingRadius()
						+ (PreferenceUtil.instance().getSnappingRadius() / 2);
		}
		// get the bbox based on the snapRadious
		Envelope bbox = handler.getContext().getBoundingBox(point, snapRadious);

		for (Object obj : linesList) {

			LineString line = (LineString) obj;
			GeometryFactory gfac = line.getFactory();
			bboxGeometry = gfac.toGeometry(bbox);

			if (bboxGeometry.intersects(line)) {
				geom = line;
			}
		}

		assert geom != null;

		return geom;
	}

	/**
	 * Set the distance.
	 * 
	 * @param distance
	 */
	public void setDistance(Double distance) {

		this.distance = distance;
	}

	/**
	 * Get the distance.
	 * 
	 * @return
	 */
	public double getDistance() {

		return this.distance;
	}

	public String getFeatureText() {

		StringBuffer text = new StringBuffer();

		text.append(referenceFeature.getID());

		return text.toString();
	}

	public String getFeatureToolTip() {
		StringBuffer text = new StringBuffer();

		text.append(referenceFeature.getID());
		text.append(" ");//$NON-NLS-1$
		text.append(((Geometry) referenceFeature.getDefaultGeometry()).toText());

		return text.toString();
	}

	public String getErrorMessage() {

		return this.errorMessage;
	}

}
