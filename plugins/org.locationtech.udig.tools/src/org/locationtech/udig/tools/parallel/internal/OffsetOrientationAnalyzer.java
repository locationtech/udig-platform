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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geomgraph.Quadrant;
import org.locationtech.jts.operation.polygonize.Polygonizer;

//import es.axios.lib.geometry.util.GeometryUtil;
import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.QuadrantAnalyzer.AnalyzerPosition;
import org.locationtech.udig.tools.parallel.internal.QuadrantAnalyzer.AnalyzerPosition;

/**
 * <p>
 * 
 * <pre>
 * This class will analyze the orientation of the source geometry and the
 * orientation of the output geometry, and based on the differences between the
 * results, will discard some points of the output geometry.
 * </pre>
 * 
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
final class OffsetOrientationAnalyzer {

	private final GeometryFactory						gf;
	private List<Coordinate>							inputList;
	private final List<Coordinate>						sourceList;
	private Map<Integer, List<DataSegmentIntersection>>	intersectionData;
	private List<Coordinate>							eliminatedCoords	= new ArrayList<Coordinate>();
	private Map<Integer, Map<Coordinate, Boolean>>		addedCoords			= new HashMap<Integer, Map<Coordinate, Boolean>>();
	private int											intersectionLastPosition;

	private int											badCoordinate;
	private int											totalIntersection;
	private static final double							SMALL_DISTANCE		= 1.0E-6;

	// private static final double DEPRECIATE_VALUE = 6.0E-3;

	public OffsetOrientationAnalyzer(	List<Coordinate> inputList,
										GeometryFactory gf,
										List<Coordinate> sourceList,
										Map<Integer, List<DataSegmentIntersection>> intersectionData) {

		this.inputList = inputList;
		this.gf = gf;
		this.sourceList = sourceList;
		this.intersectionData = intersectionData;
	}

	/**
	 * <pre>
	 * 
	 * Given the input geometry, it will be a list off nodded coordinates, which will form
	 * a closed lineString.
	 * It will analyze it, and as result will give a list of geometries, those geometries
	 * will be rings extracted from that previous closed lineString.
	 * 
	 * The code bellow works like this way:
	 * -Obtain all the rings from the input geometry.
	 * -Find intersections.
	 * -Use the quadrant operation using the intersection coordinate and the coordinates
	 * of the segments that intersects each other.
	 * -The quadrant operation will return a coordinate that is wrong, so the ring that contain
	 * that coordinate will be discarded.
	 * 
	 * </pre>
	 * 
	 * @param segmentIntersectsWithOthers
	 * 
	 * @return The list of geometry rings.
	 */
	public List<Geometry> discardClosed() {

		assert inputList.size() == sourceList.size() : "size not equal"; //$NON-NLS-1$

		int n = inputList.size();
		boolean found = false;
		List<LinearRing> ringList = getRings();
		// we suppose that all the rings are OK, so add all of them to the final
		// list.
		List<LinearRing> finalRings = new ArrayList<LinearRing>();
		finalRings.addAll(ringList);

		// will store the position and if it's forward or backward. (True is
		// forward.)
		DataDirectionUntil[] indexList = new DataDirectionUntil[n];

		// go through the result coordinates finding an intersection.
		for (int i = 0; (i < n - 1); i++) {

			found = false;
			Coordinate intersectionCoord = null;

			// create a segment
			Coordinate[] segCoord = new Coordinate[2];
			segCoord[0] = inputList.get(i);
			segCoord[1] = (i == n - 1) ? inputList.get(0) : inputList.get(i + 1);
			LineString segment = gf.createLineString(segCoord);
			// go forward and find the intersection,
			intersectionCoord = findIntersectionForward(segment, segCoord, i, n);

			if (intersectionCoord == null) {
				// when it's null, check if the point is contained on the list.

				if (intersectionData.containsKey(i)) {

					indexList = storeIndex(i, n, indexList, segment, segCoord);
				}
			} else {

				// set the end coordinate position of the segment that
				// intersects.
				intersectionLastPosition = (intersectionLastPosition == n - 1) ? 0 : intersectionLastPosition + 1;
				// go backward until the same coordinate is found.
				for (int h = i; !found; h = (h == 0) ? n - 1 : (h - 1) % n) {

					// try to find the same candidateCoord. It will
					// return true when it founds.
					found = findIntersectionBackward(segment, intersectionCoord, h, n);
				}

				// Compute the imaginary intersection using the source
				// segments.
				Coordinate[] firstOriginalSegment = new Coordinate[2];
				firstOriginalSegment[0] = sourceList.get(i);
				firstOriginalSegment[1] = sourceList.get(i + 1);
				Coordinate[] secondOriginalSegment = new Coordinate[2];
				secondOriginalSegment[0] = sourceList.get(intersectionLastPosition);
				secondOriginalSegment[1] = sourceList.get((intersectionLastPosition == 0 ? n - 1
							: intersectionLastPosition - 1));

				// get its intersection.
				Coordinate imaginaryCoord = GeometryUtil.intersection(firstOriginalSegment[0], firstOriginalSegment[1],
							secondOriginalSegment[0], secondOriginalSegment[1]);

				assert imaginaryCoord != null : "can't be null"; //$NON-NLS-1$

				// using the quadrant analyzer will know which segments are
				// valid and which aren't valid.
				if (belongsToSegment(firstOriginalSegment, secondOriginalSegment, imaginaryCoord)
							&& !checkQuadrantClosedLines(i, n, intersectionCoord, imaginaryCoord)) {
					// the ring which contain the badCoordinate will be
					// eliminated from the final list.
					List<LinearRing> deletedRings = getDeletedRings(ringList);

					// when there are more than 1 ring to be eliminated, that
					// means that the eliminatedPoint was a point common between
					// those 2 rings. So now, eliminate the ring that has less
					// size.
					double min = Double.MAX_VALUE;
					LinearRing candidate = null;
					for (LinearRing ring : deletedRings) {
						double size = ring.getCoordinates().length;
						if (size < min) {
							min = size;
							candidate = ring;
						}
					}
					finalRings.remove(candidate);
				}
			}
		}

		List<Geometry> resultList = new ArrayList<Geometry>();
		List<Integer> usedIndexes = new ArrayList<Integer>();
		for (LinearRing result : finalRings) {

			// get that ring and modify it
			Coordinate[] ringCoords = result.getCoordinates();
			// this list will take into account the deleted coordinates, so the
			// second time a ring is modified to not add them again.
			eliminatedCoords.clear();
			addedCoords.clear();
			for (int i = 0; i < ringCoords.length; i++) {

				Coordinate actualCoord = ringCoords[i];

				for (int j = 0; j < indexList.length; j++) {

					DataDirectionUntil data = indexList[j];
					if (data != null) {
						Integer indexKey = j;
						Boolean forward = data.getIsForwardDirection();
						int until = data.getUntilPosition();

						// if one of those coordinates equal the actualCoord,
						// that
						// means, this rings is the one to be modified.
						if (actualCoord.equals2D(inputList.get(indexKey)) && !(usedIndexes.contains(indexKey))) {
							try {
								result = improveTheRing(i, indexKey, n, ringCoords, forward, until);
								usedIndexes.add(indexKey);
							} catch (InvalidDistanceException ide) {
								// it has encounters difficulties during the
								// process, so
								// don't modify the ring and continue.
								continue;
							}
						}
					}
				}
			}
			resultList.add(result);
		}
		return resultList;
	}

	/**
	 * Get the rings that could be deleted.
	 * 
	 * @param ringList
	 * @return A list of linearRings.
	 */
	private List<LinearRing> getDeletedRings(List<LinearRing> ringList) {

		List<LinearRing> deletedRings = new ArrayList<LinearRing>();
		for (LinearRing ring : ringList) {

			Coordinate eliminateCoord = inputList.get(badCoordinate);
			Geometry eliminatePoint = gf.createPoint(eliminateCoord);

			if (ring.intersects(eliminatePoint)) {
				deletedRings.add(ring);
			}
		}
		return deletedRings;
	}

	/**
	 * Modify the ring, taking into account the direction(forward or not), set
	 * the index of the coordinates that will be removed.
	 * 
	 * Also, calculate the "intersection coordinate", because from the pointList
	 * will recover one coordinate, this calculated one and the retrieved one,
	 * must be very very similar. If that doesn't happens, it throws an error.
	 * 
	 * Finally, when it is decided which coordinates will be deleted
	 * (secondIndex and thirdIndex), they are deleted and in the middle of
	 * those, there is inserted the calculate coordinate or the retrieved one.
	 * 
	 * @param i
	 * @param j
	 * @param n
	 * @param ringCoords
	 * @param forward
	 * @param until
	 * @param ringLength
	 * @return The modified geometry.
	 * @throws InvalidDistanceException
	 */
	private LinearRing improveTheRing(int i, int j, int n, Coordinate[] ringCoords, Boolean forward, int until)
		throws InvalidDistanceException {

		LinearRing result = null;
		int firstIndex, secondIndex, thirdIndex, fourthIndex, untilDifference = 0, ringLength = ringCoords.length;

		untilDifference = calculateUntilDifference(forward, until, j, i, n, ringLength);

		if (forward) {
			// mount a segment with j and j+1, then intersect with
			// j+2-j+3 and that intersection must be the same as the
			// one contained in the mapList.
			firstIndex = j;
			secondIndex = (firstIndex == n - 2) ? 0 : j + 1;
			thirdIndex = until - 1;
			fourthIndex = (thirdIndex == n - 2) ? 0 : thirdIndex + 1;
		} else {
			firstIndex = j + 1;
			secondIndex = (firstIndex == 0) ? n - 2 : firstIndex - 1;
			thirdIndex = until + 1;
			fourthIndex = (thirdIndex == 0) ? n - 2 : thirdIndex - 1;
		}
		LineString firstLine = createLineSegment(firstIndex, secondIndex);
		LineString secondLine = createLineSegment(thirdIndex, fourthIndex);

		Coordinate intersection = GeometryUtil.intersection(firstLine.getCoordinateN(0), firstLine.getCoordinateN(1),
					secondLine.getCoordinateN(0), secondLine.getCoordinateN(1));

		List<DataSegmentIntersection> pointsThatBelongs = intersectionData.get(j);

		// when modifying the ring, use the index provided by
		// 'i' and not 'j'.
		if (forward) {
			firstIndex = i;
			secondIndex = (i == ringLength - 2) ? 0 : i + 1;
			thirdIndex = untilDifference - 1;
			fourthIndex = (thirdIndex == ringLength - 2) ? 0 : thirdIndex + 1;
		} else {
			firstIndex = i + 1;
			secondIndex = (firstIndex == 0) ? ringLength - 2 : firstIndex - 1;
			thirdIndex = untilDifference + 1;
			fourthIndex = (thirdIndex == 0) ? ringLength - 2 : thirdIndex - 1;
		}
		// calculate the closest one using the intersection
		// coordinate, if it is too short, it is OK.
		Coordinate coordinateToAdd = getClosestPoint(intersection, pointsThatBelongs);
		result = modifyRing(coordinateToAdd, firstIndex, secondIndex, thirdIndex, fourthIndex, ringCoords, forward,
					untilDifference);

		return result;
	}

	private int calculateUntilDifference(boolean forward, int until, int j, int i, int n, int ringLength) {

		int diff, untilDifference;
		if (forward) {

			// diff=0;
			if (until > j) {

				diff = until - j;
				if (i + diff > ringLength - 2) {
					untilDifference = ringLength - diff - i + 1;
				} else {
					untilDifference = i + diff;
				}
			} else {
				diff = n - j;
				if (i + diff > ringLength - 2) {
					untilDifference = ringLength - diff - i;
				} else {
					untilDifference = i + diff;
				}
			}

		} else {
			if (j > until) {
				diff = j - until;
				// check if untilDiff is negative.
				if (i - diff < 0) {
					untilDifference = ringLength - diff + i - 1;
				} else {
					untilDifference = i - diff;
				}

			} else {
				diff = n - until;

				if (i - diff < 0) {
					untilDifference = ringLength - diff + i;
				} else {
					untilDifference = i - diff;
				}

			}
		}
		return untilDifference;
	}

	/**
	 * First check the orientation of 3 coordinates, if it return false, check
	 * again but this time using the quadrant method. If this also return false,
	 * store that index and mark as forward.
	 * 
	 * If the first check has failed, analyze the previous 2 segments seeking
	 * for intersections, if there aren't check orientation of the actual
	 * segment but in reverse order, using the last 3 coordinates. If that
	 * return false, do the same but using the quadrant, and if this also return
	 * false, store the index but mark as backward.
	 * 
	 * @param i
	 * @param n
	 * @param indexList
	 * @param segment
	 * @param segCoord
	 * @return The map with the stored index and its direction.
	 */
	private DataDirectionUntil[] storeIndex(int i,
											int n,
											DataDirectionUntil[] indexList,
											LineString segment,
											Coordinate[] segCoord) {

		List<DataSegmentIntersection> dataList = intersectionData.get(i);

		// will treat the ones with only one item.
		if (dataList.size() > 1) {
			return indexList;
		}

		for (DataSegmentIntersection data : dataList) {

			int startSegmentIndex = data.getStartSegmentIndex();
			boolean isForward = data.getIsForward();
			boolean isValidForStore = false;
			boolean same;

			if (isForward) {

				// TODO maybe here also is need to do something similar like the
				// END-INDEX??
				for (int j = i; j < startSegmentIndex - 1; j++) {

					same = hasSameOrientation(i, true);
					// check if it has the same orientation
					if (!same) {
						isValidForStore = true;
					} else {
						isValidForStore = false;
						break;
					}
				}
				if (isValidForStore) {
					indexList[i] = new DataDirectionUntil(isForward, startSegmentIndex + 1);
				}

			} else {

				i = i + 1;
				int endIndex = startSegmentIndex + 2;
				if (endIndex == n - 1) {
					endIndex = 0;
				} else if (endIndex == 0) {
					endIndex = 1;
				}
				for (int j = i;;) {

					same = hasSameOrientationReverse(i, true);
					if (!same) {
						isValidForStore = true;
					} else {
						isValidForStore = false;
						break;
					}

					j = (j == 0) ? n - 2 : j - 1;

					if (j == endIndex) {
						break;
					}
				}
				if (isValidForStore) {
					indexList[i - 1] = new DataDirectionUntil(isForward, startSegmentIndex);
				}

			}
		}
		return indexList;
	}

	/**
	 * From input coordinates, create a lineString. Then, with that lineString
	 * form polygons using the polygonizer, and retrieve its shell that will be
	 * the linearRings needed.
	 * 
	 * @return All the rings.
	 */
	private List<LinearRing> getRings() {

		List<LinearRing> ringList = new ArrayList<LinearRing>();
		// before create the ring, calculate the orientation of the input list.
		boolean isCCWinputList = CGAlgorithms.isCCW(inputList.toArray(new Coordinate[inputList.size()]));
		// create the rings.
		LineString inputLineString = gf.createLineString(inputList.toArray(new Coordinate[inputList.size()]));
		Geometry multiLines = inputLineString.union();
		Polygonizer polygonizer = new Polygonizer();
		polygonizer.add(multiLines);
		Collection<Polygon> polyCollection = polygonizer.getPolygons();

		// add the rings to the ringList.
		for (Polygon pol : polyCollection) {

			Coordinate[] polCoord = pol.getExteriorRing().getCoordinates();
			boolean isCCWpolygon = CGAlgorithms.isCCW(polCoord);
			// if they don't have the same orientation, reverse this ring.
			if (isCCWinputList != isCCWpolygon) {

				CoordinateArrays.reverse(polCoord);
			}
			LinearRing polygonRing = gf.createLinearRing(polCoord);
			ringList.add(polygonRing);
		}
		return ringList;
	}

	private LinearRing modifyRing(	Coordinate coordinateToAdd,
									Integer firstIndex,
									int secondIndex,
									int thirdIndex,
									int fourthIndex,
									Coordinate[] ringCoords,
									Boolean forward,
									int untilDifference) {

		List<Coordinate> createdRing = new ArrayList<Coordinate>();
		for (int i = 0; i < ringCoords.length; i++) {

			if (eliminateCoords(forward, firstIndex, untilDifference, i, ringCoords)) {
				continue;
			}

			// if this coordinate was eliminated before, don't add it again.
			if (eliminatedCoords.contains(ringCoords[i])) {
				continue;
			}
			// adding the previous change on this ring.
			if (addedCoords.containsKey(i)) {

				Map<Coordinate, Boolean> values = addedCoords.get(i);
				Entry<Coordinate, Boolean> entry = values.entrySet().iterator().next();
				Coordinate coordToAdd = entry.getKey();
				Boolean whereToAdd = entry.getValue();
				if (whereToAdd) {

					createdRing.add(ringCoords[i]);
					createdRing.add(coordToAdd);
				} else {
					createdRing.add(coordToAdd);
					createdRing.add(ringCoords[i]);
				}
			} else {
				// add the rest of the points.
				createdRing.add(ringCoords[i]);
			}

			if (i == firstIndex) {
				Map<Coordinate, Boolean> added = new HashMap<Coordinate, Boolean>();
				added.put(coordinateToAdd, forward);
				addedCoords.put(i, added);
				if (forward) {
					// after adding the point of j, add the new calculated
					// coord.
					createdRing.add(coordinateToAdd);
				} else {
					createdRing.remove(createdRing.size() - 1);
					createdRing.add(coordinateToAdd);
					createdRing.add(ringCoords[i]);
				}
			}
		}

		// with the list, create a linearRing, before doing that, close the
		// rings.
		int size = createdRing.size();
		if (!createdRing.get(0).equals2D(createdRing.get(size - 1))) {
			createdRing.add(createdRing.get(0));
		}

		Coordinate[] createdCoords = createdRing.toArray(new Coordinate[createdRing.size()]);

		LinearRing result = gf.createLinearRing(createdCoords);
		return result;
	}

	private boolean eliminateCoords(boolean forward, int firstIndex, int untilDifference, int i, Coordinate[] ringCoords) {

		// those coords will be removed.
		if (forward) {

			if (firstIndex > untilDifference) {
				if (i > firstIndex || i < untilDifference) {

					eliminatedCoords.add(ringCoords[i]);
					return true;
				}
			} else {

				if (i > firstIndex && i < untilDifference) {

					eliminatedCoords.add(ringCoords[i]);
					return true;
				}
			}
		} else {

			if (firstIndex > untilDifference) {

				if (i > untilDifference && i < firstIndex) {
					eliminatedCoords.add(ringCoords[i]);
					return true;
				}
			} else {
				if (i < firstIndex || i > untilDifference) {
					eliminatedCoords.add(ringCoords[i]);
					return true;
				}
			}
		}

		return false;
	}

	private Coordinate getClosestPoint(Coordinate intersection, List<DataSegmentIntersection> pointsThatBelongs)
		throws InvalidDistanceException {

		Double min = Double.MAX_VALUE;
		Coordinate closest = null;

		for (DataSegmentIntersection point : pointsThatBelongs) {
			double dist = point.getIntersectionCoordinate().distance(intersection);
			if (dist < min) {
				min = dist;
				closest = point.getIntersectionCoordinate();
			}
		}

		if (!(min < SMALL_DISTANCE)) {
			throw new InvalidDistanceException(min.toString());
		}

		return closest;
	}

	/**
	 * If the distance of the imaginary intersection with any of those segment
	 * is to little, (intersects says false) we can assume they intersect.
	 * 
	 * Taking into account that, and also knowing how many intersection that
	 * segment has, the function will work like that way:
	 * 
	 * <pre>
	 * -If there are only 1 intersection, return true.
	 * -If there are 2 intersection, but the distance between the point
	 * and any of the segment is to small, like point would intersect the segments,
	 * will return true.
	 * -If there are 2 intersection, but the distance is not to small, will return false.
	 * </pre>
	 * 
	 * @param firstOriginalSegment
	 * @param secondOriginalSegment
	 * @param imaginaryCoord
	 * @return
	 */
	private boolean belongsToSegment(	Coordinate[] firstOriginalSegment,
										Coordinate[] secondOriginalSegment,
										Coordinate imaginaryCoord) {

		if (totalIntersection == 1) {
			return true;
		}
		LineString firstSegment = gf.createLineString(firstOriginalSegment);
		LineString secondSegment = gf.createLineString(secondOriginalSegment);
		Geometry point = gf.createPoint(imaginaryCoord);

		if (totalIntersection == 2
					&& (firstSegment.distance(point) < SMALL_DISTANCE || secondSegment.distance(point) < SMALL_DISTANCE)) {
			return true;
		}
		return false;
	}

	/**
	 * Check the orientation of a segment (p0-p1) with a point (p2).
	 * 
	 * Will check the orientation of 3 points from the input list and compare
	 * with the respective orientation of that 3 points of the source list.
	 * 
	 * If the orientations are the same, will check the quadrant of those
	 * segment and return the result.
	 * 
	 * @param i
	 *            The position of the first point. Will check i, i+1 and i+2.
	 * @param isClosedLine
	 * @return True if the orientation matches.
	 */
	private boolean hasSameOrientation(int i, boolean isClosedLine) {

		int n = inputList.size();
		int second = i + 1, third = i + 2;

		if (isClosedLine) {

			if (i == n - 1) {
				second = 1;
				third = 2;
			} else if (i == n - 2) {
				third = 1;
			}
		} else {
			if (i == n - 1) {
				second = 0;
				third = 1;
			} else if (i == n - 2) {
				third = 0;
			}
		}
		int offsetOrientation = CGAlgorithms.orientationIndex(inputList.get(i), inputList.get(second), inputList
					.get(third));
		int originalOrientation = CGAlgorithms.orientationIndex(sourceList.get(i), sourceList.get(second), sourceList
					.get(third));

		boolean result = offsetOrientation == originalOrientation;

		if (result) {
			Coordinate secondCoordInput = inputList.get(second);
			Coordinate thirdCoordInput = inputList.get(third);
			Coordinate secondCoordSrc = sourceList.get(second);
			Coordinate thirdCoordSrc = sourceList.get(third);
			if (!secondCoordInput.equals2D(thirdCoordInput) && !secondCoordSrc.equals2D(thirdCoordSrc)) {
				// just in case, check the quadrants are the same.
				int offsetQuadrant = Quadrant.quadrant(secondCoordInput, thirdCoordInput);
				int orientationQuadrant = Quadrant.quadrant(secondCoordSrc, thirdCoordSrc);
				result = offsetQuadrant == orientationQuadrant;
			}
		}

		return result;
	}

	/**
	 * Check the orientation of a segment (p0-p1) with a point (p2). But those
	 * segment will be the end-segment of the list.
	 * 
	 * Will check the orientation of 3 points from the input list and compare
	 * with the respective orientation of that 3 points of the source list.
	 * 
	 * If the orientations are the same, will check the quadrant of those
	 * segment and return the result.
	 * 
	 * @param i
	 *            Size of the list, so it will check n -1, n-2, and n-3.
	 * @return True if the orientation matches.
	 */
	private boolean hasSameOrientationReverse(int i, boolean isClosedLine) {

		int n = inputList.size();
		int second = i - 1, third = i - 2;

		if (isClosedLine) {
			if (i == 0) {
				second = n - 2;
				third = n - 3;
			} else if (i == 1) {
				third = n - 2;
			}
		} else {
			if (i == 0) {
				second = n - 1;
				third = n - 2;
			} else if (i == 1) {
				third = n - 1;
			}
		}

		int offsetOrientation = CGAlgorithms.orientationIndex(inputList.get(i), inputList.get(second), inputList
					.get(third));
		int originalOrientation = CGAlgorithms.orientationIndex(sourceList.get(i), sourceList.get(second), sourceList
					.get(third));

		boolean result = originalOrientation == offsetOrientation;

		if (result) {
			Coordinate secondCoordInput = inputList.get(second);
			Coordinate thirdCoordInput = inputList.get(third);
			Coordinate secondCoordSrc = sourceList.get(second);
			Coordinate thirdCoordSrc = sourceList.get(third);
			if (!secondCoordInput.equals2D(thirdCoordInput) && !secondCoordSrc.equals2D(thirdCoordSrc)) {
				int offsetQuadrant = Quadrant.quadrant(secondCoordInput, thirdCoordInput);
				int orientationQuadrant = Quadrant.quadrant(secondCoordSrc, thirdCoordSrc);
				result = offsetQuadrant == orientationQuadrant;
			}
		}
		return result;
	}

	/**
	 * Giving a segment and an intersectionCoordinate, will create a second
	 * segment with the provided index
	 * 
	 * @param segment
	 * @param firstCandidateCoord
	 * @param i
	 * @param n
	 * @return True if it finds the same intersection.
	 */
	private boolean findIntersectionBackward(LineString segment, Coordinate firstCandidateCoord, int i, int n) {

		Coordinate[] segCoord = new Coordinate[2];
		segCoord[0] = inputList.get(i);
		segCoord[1] = (i == 0) ? inputList.get(n - 1) : inputList.get(i - 1);
		LineString secondSegment = gf.createLineString(segCoord);

		// test if exist intersection and it matches with the
		// firstCandidateCoord.
		// if intersects.
		if (segment.intersects(secondSegment)) {

			Geometry resultPoints = segment.intersection(secondSegment);

			for (int k = 0; k < resultPoints.getNumGeometries(); k++) {

				Coordinate intersectionCoord = resultPoints.getGeometryN(k).getCoordinate();

				if (firstCandidateCoord.equals2D(intersectionCoord)) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Will find the intersection if exist. If exist, return the coordinate. If
	 * doesn't exist will return null.
	 * 
	 * @param segment
	 * @param segCoord
	 * @param j
	 * @param n
	 * @return If found, the intersection coordinate, else null.
	 */
	private Coordinate findIntersectionForward(final Geometry segment, final Coordinate[] segCoord, int j, int n) {

		// the segment that will be tested if intersects.
		LineString interSegment = null;
		Coordinate candidateCoord = null, finalCoordinate = null;
		intersectionLastPosition = -1;
		// make a complete turn and get with the last intersection that
		// intersects with the provided segment.
		int count = 0;
		for (int h = j;;) {

			h = (h + 1) % n;
			// mount a segment.
			Coordinate[] interCoord = new Coordinate[2];
			interCoord[0] = inputList.get(h);
			interCoord[1] = (h == n - 1) ? inputList.get(0) : inputList.get(h + 1);
			interSegment = gf.createLineString(interCoord);
			// get the intersection coordinate if it intersects.
			candidateCoord = AlgorithmUtils.getIntersectionCoord(segment, interSegment, segCoord);
			if (candidateCoord != null) {
				finalCoordinate = new Coordinate(candidateCoord);
				intersectionLastPosition = h;
				count++;
			}
			if (h == j) {
				// if it has been checked all the segment and any intersection
				// has been found, get out the loop and return nothing ??
				break;
			}
		}
		this.totalIntersection = count;
		return finalCoordinate;
	}

	/**
	 * <pre>
	 * Analyze the line and discard pieces that are wrong.
	 * 
	 * First, find an intersection, when it has been found, using the quadrant algorithm 
	 * determine which piece must be eliminated.
	 * </pre>
	 * 
	 * @return The line without the wrong pieces.
	 */
	public List<Geometry> discardNonClosed() {

		// if less than 4 coordinates, return it without modify.
		if (inputList.size() < 4) {
			List<Geometry> resultList = new ArrayList<Geometry>();
			Coordinate[] resultCoord = inputList.toArray(new Coordinate[inputList.size()]);
			resultList.add(gf.createLineString(resultCoord));
			return resultList;
		}
		assert inputList.size() == sourceList.size() : "size not equal"; //$NON-NLS-1$
		// go through the result coordinates, checking its orientation with the
		// original orientation.
		int n = inputList.size();
		boolean lastAddedWasIntersection = false;
		// the list with the resultant coordinates.
		List<Coordinate> filteredResult = new ArrayList<Coordinate>();

		Coordinate intersectCoord = null;
		Coordinate candidateCoord = null;
		int start = 0;

		// check the first 3 coordinates.
		if (!hasSameOrientation(0, false) && !hasBeginIntersections()) {

			// they haven't the same orientation.
			// as AutoCAD do, remove the first segment.
			start = 1;
		}
		// go through the line picking segments of it and analyzing them.
		for (int i = start; i < n - 1;) {

			candidateCoord = null;
			intersectionLastPosition = -1;
			// get the first segment (i->i+1) or (intersectCoor->i+1)
			LineString firstSegment = intersectCoord == null ? createLineSegment(i, i + 1) : createLineSegment(
						intersectCoord, i + 1);
			// initialize intersectCoord.
			intersectCoord = null;
			candidateCoord = findIntersectionWithProvidedSegment(firstSegment, i + 1, n);

			if (candidateCoord != null) {

				assert intersectionLastPosition != -1 : "intersection must be found"; //$NON-NLS-1$

				// If intersection is found, now must check the quadrant of
				// the segments between that intersection.
				boolean sameQuadrant = true;
				// QUADRANT BETWEEN i ->i+1 & intersectionLastPosition ->
				// intersectionLastPosition +1

				sameQuadrant = checkQuadrantOpenLines(i);
				if (!sameQuadrant) {
					// if last added coordinate was an intersection coordinate,
					// now add only this coordinate.
					if (lastAddedWasIntersection) {
						filteredResult.add(candidateCoord);
					} else {
						// if not, add the beginning of the segment and the
						// intersection coordinate.
						filteredResult.add(inputList.get(i));
						filteredResult.add(candidateCoord);
					}
					lastAddedWasIntersection = true;
					// set the intersection coordinate.
					intersectCoord = new Coordinate(candidateCoord);
				} else {
					// when the last added coordinate was an intersection, don't
					// add the one that belongs to "i"
					if (!lastAddedWasIntersection) {
						filteredResult.add(inputList.get(i));
					}
					lastAddedWasIntersection = false;
				}
			} else {
				// when the last added coordinate was an intersection, don't add
				// the one that belongs to "i"
				if (!lastAddedWasIntersection) {
					filteredResult.add(inputList.get(i));
				}
				lastAddedWasIntersection = false;
			}
			i = lastAddedWasIntersection ? intersectionLastPosition : i + 1;
		}
		// check the orientation of the last segments
		// if they match, add the last segment.
		if (hasSameOrientationReverse(n - 1, false) || hasEndIntersections(n)) {
			filteredResult.add(inputList.get(n - 1));
		}

		List<Geometry> resultList = new ArrayList<Geometry>();
		Coordinate[] resultCoord = filteredResult.toArray(new Coordinate[filteredResult.size()]);
		resultList.add(gf.createLineString(resultCoord));
		return resultList;
	}

	/**
	 * Run the quadrant analyzer. If everything is OK, it will return true,
	 * false otherwise.
	 * 
	 * @param i
	 * @return True if it's OK.
	 */
	private boolean checkQuadrantOpenLines(int i) {

		boolean isOk = false;

		QuadrantAnalyzer qAnalyzer = new QuadrantAnalyzer(inputList, sourceList, i, intersectionLastPosition);
		qAnalyzer.analyzeNonClosedLines();
		isOk = qAnalyzer.isValid();

		// rarely the quadrant algorithm fails.
		// On those cases, check the orientation of the segments between
		// the intersection.
		if (isOk) {
			boolean sameOrientation = hasSameOrientation(i + 1, false);
			isOk = sameOrientation;
		}
		return isOk;
	}

	/**
	 * Analyze running the quadrantAnalyzer which will return true or false. If
	 * return false, some coordinate is wrong; so it will retrieve which one is
	 * and set as badCoordinate.
	 * 
	 * @param i
	 * @param size
	 * @param realIntersectionCoord
	 * @param imaginaryCoord
	 * @return
	 */
	private boolean checkQuadrantClosedLines(	int i,
												int size,
												Coordinate realIntersectionCoord,
												Coordinate imaginaryCoord) {

		boolean isOk = false;

		QuadrantAnalyzer qAnalyzer = new QuadrantAnalyzer(inputList, sourceList, i, intersectionLastPosition, size,
					realIntersectionCoord, imaginaryCoord);
		qAnalyzer.analyzeClosedLines();
		isOk = qAnalyzer.isValid();

		if (!isOk) {

			// retrieve that coordinate, its position.
			AnalyzerPosition positionIndex = qAnalyzer.getResultIndex();

			switch (positionIndex) {

			case SEGMENT_1_POINT_1:
				// wrong coordinate = i
				this.badCoordinate = i;
				break;
			case SEGMENT_1_POINT_2:
				// wrong coordinate = i +1
				this.badCoordinate = i + 1;
				break;
			case SEGMENT_2_POINT_1:
				// wrong coordinate = intersectionLast
				this.badCoordinate = intersectionLastPosition;
				break;
			case SEGMENT_2_POINT_2:
				// wrong coordinate = intersectionLastPosition == 0 ? size - 1 :
				// intersectionLastPosition - 1;
				this.badCoordinate = intersectionLastPosition == 0 ? size - 1 : intersectionLastPosition - 1;
				break;
			default:
				break;
			}
		}

		return isOk;
	}

	/**
	 * Function called when checking the orientation of the beginning. Will
	 * check if the first and third segments intersects each other.
	 * 
	 * @return
	 */
	private boolean hasBeginIntersections() {
		// check the segment 0-1 and segment 2-3 if intersects.

		LineString firstSegment = createLineSegment(0, 1);
		LineString secondSegment = createLineSegment(2, 3);

		Coordinate[] firstSegC = new Coordinate[2];
		firstSegC[0] = inputList.get(0);
		firstSegC[1] = inputList.get(1);
		Coordinate interCoord = AlgorithmUtils.getIntersectionCoord(firstSegment, secondSegment, firstSegC);

		return interCoord != null;
	}

	/**
	 * Function called when checking the orientation of the ending. Will check
	 * if the last and third to last segments intersects each other.
	 * 
	 * @return
	 */
	private boolean hasEndIntersections(int n) {
		// check the segment 0-1 and segment 2-3 if intersects.

		LineString firstSegment = createLineSegment(n - 1, n - 2);
		LineString secondSegment = createLineSegment(n - 3, n - 4);

		Coordinate[] firstSegC = new Coordinate[2];
		firstSegC[0] = inputList.get(n - 1);
		firstSegC[1] = inputList.get(n - 2);
		Coordinate interCoord = AlgorithmUtils.getIntersectionCoord(firstSegment, secondSegment, firstSegC);

		return interCoord != null;
	}

	/**
	 * Create a new lineSegment using the provided coordinate.
	 * 
	 * @param firstCoord
	 *            The provided coordinate.
	 * @param second
	 *            The position respect the list of the second coordinate.
	 * @return The desired lineString segment.
	 */
	private LineString createLineSegment(final Coordinate firstCoord, int second) {

		Coordinate[] firstSegC = new Coordinate[2];
		firstSegC[0] = new Coordinate(firstCoord);
		firstSegC[1] = inputList.get(second);

		return gf.createLineString(firstSegC);
	}

	/**
	 * Create a LineString segment between first and second coords.
	 * 
	 * @param first
	 *            The position respect the list of the first coordinate.
	 * @param second
	 *            The position respect the list of the second coordinate.
	 * @return The desired lineString segment.
	 */
	private LineString createLineSegment(int first, int second) {

		Coordinate[] firstSegC = new Coordinate[2];
		firstSegC[0] = inputList.get(first);
		firstSegC[1] = inputList.get(second);
		return gf.createLineString(firstSegC);
	}

	/**
	 * Find the first intersection of the provided segment. Return the
	 * intersection coordinate if exist. Also set the intersectionLastPosition.
	 * 
	 * @param firstSegment
	 *            The segment which will be intersected.
	 * @param start
	 *            Start position.
	 * @param n
	 *            List size.
	 * @return The intersection coordinate or null.
	 */
	private Coordinate findIntersectionWithProvidedSegment(LineString firstSegment, int start, int n) {

		// the segment that will be tested if intersects.
		LineString interSegment = null;
		Coordinate candidateCoord = null, finalCoordinate = null;

		Coordinate[] firstSegC = new Coordinate[2];
		firstSegC[0] = firstSegment.getCoordinateN(0);
		firstSegC[1] = firstSegment.getCoordinateN(1);
		// reset the variable.
		intersectionLastPosition = -1;
		for (int h = start; h < n - 1; h++) {

			// mount a segment.
			interSegment = createLineSegment(h, h + 1);
			// get the intersection coordinate if exist, otherwise will return
			// null.
			candidateCoord = AlgorithmUtils.getIntersectionCoord(firstSegment, interSegment, firstSegC);
			if (candidateCoord != null) {
				finalCoordinate = new Coordinate(candidateCoord);
				intersectionLastPosition = h;
			}
		}
		return finalCoordinate;
	}
}
