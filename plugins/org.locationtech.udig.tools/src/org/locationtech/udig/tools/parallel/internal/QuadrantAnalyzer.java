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

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geomgraph.Quadrant;

/**
 * <p>
 * 
 * <pre>
 * Quadrant analyzer.
 * Based on the algorithm of the quadrant, will analyze the provided coordinate, 
 * and determinate if they must be eliminated or not.
 * It will validate a coordinate or coordinates.
 * </pre>
 * 
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
final class QuadrantAnalyzer {

	private int						index;
	private int						size;
	private int						intersectionLastPosition;
	private Coordinate				imaginaryCoord;
	private Coordinate				realIntersectionCoord;
	private final List<Coordinate>	inputList;
	private final List<Coordinate>	sourceList;

	/* Is valid segment1-point1 */
	private boolean					valids1p1;
	/* Is valid segment1-point2 */
	private boolean					valids1p2;
	/* Is valid segment2-point1 */
	private boolean					valids2p1;
	/* Is valid segment2-point2 */
	private boolean					valids2p2;

	public enum AnalyzerPosition {

		/* The coordinate 1 of the segment 1 */
		SEGMENT_1_POINT_1,
		/* The coordinate 2 of the segment 1 */
		SEGMENT_1_POINT_2,
		/* The coordinate 1 of the segment 2 */
		SEGMENT_2_POINT_1,
		/* The coordinate 2 of the segment 2 */
		SEGMENT_2_POINT_2,
		/* None of each */
		RESULTANT_NONE
	}

	/**
	 * Constructor used for analyze non-closed lines.
	 * 
	 * @param inputList
	 *            List with the created coordinates.
	 * @param sourceList
	 *            List with the original coordinates.
	 * @param index
	 *            Index of the first segment.
	 * @param intersectionLastPosition
	 *            Index of second segment.
	 */
	public QuadrantAnalyzer(final List<Coordinate> inputList,
							final List<Coordinate> sourceList,
							int index,
							int intersectionLastPosition) {

		assert inputList != null : "cannot be null"; //$NON-NLS-1$
		assert sourceList != null : "cannot be null"; //$NON-NLS-1$

		this.index = index;
		this.inputList = inputList;
		this.sourceList = sourceList;
		this.intersectionLastPosition = intersectionLastPosition;

	}

	/**
	 * 
	 * Constructor used for analyze closed lines.
	 * 
	 * @param inputList
	 *            List with the created coordinates.
	 * @param sourceList
	 *            List with the original coordinates.
	 * @param index
	 *            Index of the first segment.
	 * @param intersectionLastPosition
	 *            Index of second segment.
	 * @param size
	 *            Size of the input list.
	 * @param realIntersectionCoord
	 *            The intersection of the segment off the input list.
	 * @param imaginaryCoord
	 *            The non existent intersection of the segment off the source
	 *            list.
	 */
	public QuadrantAnalyzer(final List<Coordinate> inputList,
							final List<Coordinate> sourceList,
							int index,
							int intersectionLastPosition,
							int size,
							Coordinate realIntersectionCoord,
							Coordinate imaginaryCoord) {

		this(inputList, sourceList, index, intersectionLastPosition);

		this.size = size;
		this.realIntersectionCoord = realIntersectionCoord;
		this.imaginaryCoord = imaginaryCoord;
	}

	/**
	 * Get the coordinates of the analyzed segment. Those coordinate will depend
	 * on the provided 'index' and 'intersectionLastPosition'. Will get the
	 * coordinates from the input list and from the source list and analyze
	 * them.
	 */
	public void analyzeNonClosedLines() {

		Coordinate s1p1, s1p2, s2p1, s2p2, s1p1src, s1p2src, s2p1src, s2p2src;
		// input coordinates
		s1p1 = inputList.get(index);
		s1p2 = inputList.get(index + 1);
		s2p1 = inputList.get(intersectionLastPosition);
		s2p2 = inputList.get(intersectionLastPosition + 1);
		// source coordinates
		s1p1src = sourceList.get(index);
		s1p2src = sourceList.get(index + 1);
		s2p1src = sourceList.get(intersectionLastPosition);
		s2p2src = sourceList.get(intersectionLastPosition + 1);

		analyze(s1p1, s1p2, s2p1, s2p2, s1p1src, s1p2src, s2p1src, s2p2src);
	}

	/**
	 * Meanwhile the analyze process, for each coordinate is stored if it was
	 * valid or not. This function will return true if all of those analyzed
	 * coordinates were true.
	 * 
	 * @return True if all of the coordinates were valid.
	 */
	public boolean isValid() {

		return valids1p1 && valids1p2 && valids2p1 && valids2p2;
	}

	/**
	 * Get the coordinates of the analyzed segment. Those coordinate will depend
	 * on the provided 'index' and 'intersectionLastPosition'. Will get the
	 * coordinates from the input list and from the source list and analyze them
	 * using the provided intersections as referent points.
	 * 
	 */
	public void analyzeClosedLines() {

		// this will test if for one coordinate of each segment, the quadrant
		// operation with each coordinate of the other segment, both will be
		// false.

		Coordinate s1p1, s1p2, s2p1, s2p2, s1p1src, s1p2src, s2p1src, s2p2src;
		int secondIndex = intersectionLastPosition == 0 ? size - 1 : intersectionLastPosition - 1;
		// input coordinates
		s1p1 = inputList.get(index);
		s1p2 = inputList.get(index + 1);
		s2p1 = inputList.get(intersectionLastPosition);
		s2p2 = inputList.get(secondIndex);
		// source coordinates
		s1p1src = sourceList.get(index);
		s1p2src = sourceList.get(index + 1);
		s2p1src = sourceList.get(intersectionLastPosition);
		s2p2src = sourceList.get(secondIndex);

		analyzeUsingIntersection(s1p1, s1p2, s2p1, s2p2, s1p1src, s1p2src, s2p1src, s2p2src);
	}

	/**
	 * Will analyze each coordinate respect the provided intersection
	 * coordinate. And compare with the result of the analyzed source coordinate
	 * with the imaginary segment.
	 * 
	 * The parameters are the coordinates, 2 segment, 4 coordinates and its
	 * respective coordinate off the source list. Totally 8 coordinate will be
	 * part of that.
	 * 
	 * @param s1p1
	 * @param s1p2
	 * @param s2p1
	 * @param s2p2
	 * @param s1p1src
	 * @param s1p2src
	 * @param s2p1src
	 * @param s2p2src
	 */
	private void analyzeUsingIntersection(	final Coordinate s1p1,
											final Coordinate s1p2,
											final Coordinate s2p1,
											final Coordinate s2p2,
											final Coordinate s1p1src,
											final Coordinate s1p2src,
											final Coordinate s2p1src,
											final Coordinate s2p2src) {

		// coord s1p1 (segment 1, point 1)
		int qs1p1s2p1 = Quadrant.quadrant(s1p1, realIntersectionCoord);
		int qs1p1s2p1src = Quadrant.quadrant(s1p1src, imaginaryCoord);

		// checking the quadrant.
		// a valid quadrant for segment 1 point 1, must have the same value
		// respect the quadrant of segment 2 point 1 and segment 2 point 2.

		// if any of those is false, the result will be FALSE.
		valids1p1 = qs1p1s2p1 == qs1p1s2p1src;

		// coord s1p2 (segment 1, point 2)
		int qs1p2s2p1 = Quadrant.quadrant(s1p2, realIntersectionCoord);
		int qs1p2s2p1src = Quadrant.quadrant(s1p2src, imaginaryCoord);

		// checking the quadrant.
		// a valid quadrant for segment 1 point 2, must have the same value
		// respect the quadrant of segment 2 point 1 and segment 2 point 2.

		valids1p2 = qs1p2s2p1 == qs1p2s2p1src;

		// coord s2p1 (segment 2, point 1)
		int qs2p1s1p1 = Quadrant.quadrant(s2p1, realIntersectionCoord);
		int qs2p1s1p1src = Quadrant.quadrant(s2p1src, imaginaryCoord);

		// checking the quadrant.
		// a valid quadrant for segment 2 point 1, must have the same value
		// respect the quadrant of segment 1 point 1 and segment 1 point 2.

		valids2p1 = qs2p1s1p1 == qs2p1s1p1src;

		// coord s2p2 (segment 2, point 2)
		int qs2p2s1p1 = Quadrant.quadrant(s2p2, realIntersectionCoord);
		int qs2p2s1p1src = Quadrant.quadrant(s2p2src, imaginaryCoord);

		// checking the quadrant.
		// a valid quadrant for segment 2 point 2, must have the same value
		// respect the quadrant of segment 1 point 1 and segment 1 point 2.

		valids2p2 = qs2p2s1p1 == qs2p2s1p1src;
	}

	/**
	 * 
	 * <p>
	 * 
	 * <pre>
	 * Will analyze each coordinate in that way: 
	 * Coordinate 1 of the segment 1, apply quadrant operation with coordinate 1 and 2 
	 * from the other segment. And do the same with the source coordinate.
	 * Then, compare the result of s1p1 (segment 1-coord 1) respect s2p1, with the result
	 * of s1p1src(segment 1-coord 1 of source)  respect s2p1src.
	 * 
	 * Do that for all the coordinates.
	 * 
	 * The parameters are the coordinates, 2 segment, 4 coordinates and its
	 * respective coordinate off the source list. Totally 8 coordinate will be
	 * part of that.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param s1p1
	 * @param s1p2
	 * @param s2p1
	 * @param s2p2
	 * @param s1p1src
	 * @param s1p2src
	 * @param s2p1src
	 * @param s2p2src
	 */
	private void analyze(	final Coordinate s1p1,
							final Coordinate s1p2,
							final Coordinate s2p1,
							final Coordinate s2p2,
							final Coordinate s1p1src,
							final Coordinate s1p2src,
							final Coordinate s2p1src,
							final Coordinate s2p2src) {

		// coord s1p1 (segment 1, point 1)
		int qs1p1s2p1 = Quadrant.quadrant(s1p1, s2p1);
		int qs1p1s2p1src = Quadrant.quadrant(s1p1src, s2p1src);
		int qs1p1s2p2 = Quadrant.quadrant(s1p1, s2p2);
		int qs1p1s2p2src = Quadrant.quadrant(s1p1src, s2p2src);

		// checking the quadrant.
		// a valid quadrant for segment 1 point 1, must have the same value
		// respect the quadrant of segment 2 point 1 and segment 2 point 2.

		// if any of those is false, the result will be FALSE.
		valids1p1 = (qs1p1s2p1 == qs1p1s2p1src) && (qs1p1s2p2 == qs1p1s2p2src);

		// coord s1p2 (segment 1, point 2)
		int qs1p2s2p1 = Quadrant.quadrant(s1p2, s2p1);
		int qs1p2s2p1src = Quadrant.quadrant(s1p2src, s2p1src);
		int qs1p2s2p2 = Quadrant.quadrant(s1p2, s2p2);
		int qs1p2s2p2src = Quadrant.quadrant(s1p2src, s2p2src);

		// checking the quadrant.
		// a valid quadrant for segment 1 point 2, must have the same value
		// respect the quadrant of segment 2 point 1 and segment 2 point 2.

		valids1p2 = (qs1p2s2p1 == qs1p2s2p1src) && (qs1p2s2p2 == qs1p2s2p2src);

		// coord s2p1 (segment 2, point 1)
		int qs2p1s1p1 = Quadrant.quadrant(s2p1, s1p1);
		int qs2p1s1p1src = Quadrant.quadrant(s2p1src, s1p1src);
		int qs2p1s1p2 = Quadrant.quadrant(s2p1, s1p2);
		int qs2p1s1p2src = Quadrant.quadrant(s2p1src, s1p2src);

		// checking the quadrant.
		// a valid quadrant for segment 2 point 1, must have the same value
		// respect the quadrant of segment 1 point 1 and segment 1 point 2.

		valids2p1 = (qs2p1s1p1 == qs2p1s1p1src) && (qs2p1s1p2 == qs2p1s1p2src);

		// coord s2p2 (segment 2, point 2)
		int qs2p2s1p1 = Quadrant.quadrant(s2p2, s1p1);
		int qs2p2s1p1src = Quadrant.quadrant(s2p2src, s1p1src);
		int qs2p2s1p2 = Quadrant.quadrant(s2p2, s1p2);
		int qs2p2s1p2src = Quadrant.quadrant(s2p2src, s1p2src);

		// checking the quadrant.
		// a valid quadrant for segment 2 point 2, must have the same value
		// respect the quadrant of segment 1 point 1 and segment 1 point 2.

		valids2p2 = (qs2p2s1p1 == qs2p2s1p1src) && (qs2p2s1p2 == qs2p2s1p2src);
	}

	/**
	 * Get an indicator of which coordinate is not valid.
	 * 
	 * @return The corresponding coordinate and the segment which it belongs.
	 */
	public AnalyzerPosition getResultIndex() {

		if (!valids1p1) {
			return AnalyzerPosition.SEGMENT_1_POINT_1;
		} else if (!valids1p2) {
			return AnalyzerPosition.SEGMENT_1_POINT_2;
		} else if (!valids2p1) {
			return AnalyzerPosition.SEGMENT_2_POINT_1;
		} else if (!valids2p2) {
			return AnalyzerPosition.SEGMENT_2_POINT_2;
		} else {
			return AnalyzerPosition.RESULTANT_NONE;
		}
	}
}
