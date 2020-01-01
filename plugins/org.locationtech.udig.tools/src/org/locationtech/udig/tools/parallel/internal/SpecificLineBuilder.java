/**
 * 
 */
package org.locationtech.udig.tools.parallel.internal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;

/**
 * Builder responsible of creating the line. 
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
final class SpecificLineBuilder {

	private enum CoordName {

		/* It refers the first coordinate of the offset segment */
		OFFSET_1,
		/* It refers the second coordinate of the offset segment */
		OFFSET_2,
		/* It refers the first coordinate of the reference segment */
		REFERENCE_1,
		/* It refers the second coordinate of the reference segment */
		REFERENCE_2,
		/* Nothing */
		EMPTY
	}

	/**
	 * Mount the line. This function will have to join 2 lineString following
	 * the next rule:
	 * 
	 * It will analyze the 4 coordinates, and will mount a line with the 2
	 * coordinates that are most far-off.
	 * 
	 * @param key
	 * @param value
	 * @param gf
	 * @return
	 */
	public LineString mountTheLine(LineSegment key, LineSegment value, GeometryFactory gf) {

		// convert key and value to geometry class allowing them the use of
		// spatial relations.
		Coordinate[] coordsFromRef = new Coordinate[2];
		coordsFromRef[0] = key.getCoordinate(0);
		coordsFromRef[1] = key.getCoordinate(1);

		Coordinate[] coordsFromOff = new Coordinate[2];
		coordsFromOff[0] = value.getCoordinate(0);
		coordsFromOff[1] = value.getCoordinate(1);

		// calculate the distance between coordinates and mount the largest
		// segment.

		// coordsFromRef[0] = Ref1
		// coordsFromRef[1] = Ref2
		// coordsFromOff[0] = Off1
		// coordsFromOff[1] = Off2

		// using Ref1
		CoordName resultRef1 = calculateReference1(coordsFromRef, coordsFromOff);

		// using Ref2
		CoordName resultRef2 = calculateReference2(coordsFromRef, coordsFromOff);

		// using Off1
		CoordName resultOff1 = calculateOffset1(coordsFromOff, coordsFromRef);

		// using Off2
		CoordName resultOff2 = calculateOffset2(coordsFromOff, coordsFromRef);

		// now depending on the analysis, mount the final segment.
		// if Ref1 and Ref2 or Off1 and Off2 point to the same, the result will
		// be that, and the one that is pointed the referenced before.
		Coordinate[] finalCoords = new Coordinate[2];
		if (resultRef1.equals(resultRef2)) {

			finalCoords = mountLineReferencesEqual(resultRef1, resultOff1, resultOff2, finalCoords, coordsFromRef,
						coordsFromOff);
		}
		if (resultOff1.equals(resultOff2)) {

			finalCoords = mountLineOffsetEqual(resultOff1, resultRef1, resultRef2, finalCoords, coordsFromRef,
						coordsFromOff);
		}
		finalCoords = mountLineRestOfTheCases(resultRef1, resultRef2, resultOff1, resultOff2, finalCoords,
					coordsFromRef, coordsFromOff);

		LineString resultLine = gf.createLineString(finalCoords);
		return resultLine;
	}

	/**
	 * Analyze the reference 1 segment. Will return which coordinate is the
	 * farthest to this point.
	 * 
	 * @param coordsFromRef
	 * @param coordsFromOff
	 * @return
	 */
	private CoordName calculateReference1(Coordinate[] coordsFromRef, Coordinate[] coordsFromOff) {

		double distRef1Ref2 = coordsFromRef[0].distance(coordsFromRef[1]);
		double distRef1Off1 = coordsFromRef[0].distance(coordsFromOff[0]);
		double distRef1Off2 = coordsFromRef[0].distance(coordsFromOff[1]);
		// the largest distance is...
		double max = calculateLargestDistance(distRef1Ref2, distRef1Off1, distRef1Off2);

		assert max != -1;

		CoordName resultRef1 = CoordName.EMPTY;

		if (max == distRef1Ref2) {
			resultRef1 = CoordName.REFERENCE_2;
		} else if (max == distRef1Off1) {
			resultRef1 = CoordName.OFFSET_1;
		} else if (max == distRef1Off2) {
			resultRef1 = CoordName.OFFSET_2;
		} else {
			assert false : "impossible situation."; //$NON-NLS-1$
		}
		return resultRef1;
	}

	/**
	 * Analyze the reference 2 segment. Will return which coordinate is the
	 * farthest to this point.
	 * 
	 * @param coordsFromRef
	 * @param coordsFromOff
	 * @return
	 */
	private CoordName calculateReference2(Coordinate[] coordsFromRef, Coordinate[] coordsFromOff) {

		double distRef2Ref1 = coordsFromRef[1].distance(coordsFromRef[0]);
		double distRef2Off1 = coordsFromRef[1].distance(coordsFromOff[0]);
		double distRef2Off2 = coordsFromRef[1].distance(coordsFromOff[1]);

		double max = calculateLargestDistance(distRef2Ref1, distRef2Off1, distRef2Off2);

		assert max != -1;

		CoordName resultRef2 = CoordName.EMPTY;
		if (max == distRef2Ref1) {
			resultRef2 = CoordName.REFERENCE_1;
		} else if (max == distRef2Off1) {
			resultRef2 = CoordName.OFFSET_1;
		} else if (max == distRef2Off2) {
			resultRef2 = CoordName.OFFSET_2;
		} else {
			assert false : "impossible situation."; //$NON-NLS-1$
		}
		return resultRef2;
	}

	/**
	 * Analyze the offset 1 segment. Will return which coordinate is the
	 * farthest to this point.
	 * 
	 * @param coordsFromOff
	 * @param coordsFromRef
	 * @return
	 */
	private CoordName calculateOffset1(Coordinate[] coordsFromOff, Coordinate[] coordsFromRef) {

		double distOff1Off2 = coordsFromOff[0].distance(coordsFromOff[1]);
		double distOff1Ref1 = coordsFromOff[0].distance(coordsFromRef[0]);
		double distOff1Ref2 = coordsFromOff[0].distance(coordsFromRef[1]);

		double max = calculateLargestDistance(distOff1Off2, distOff1Ref1, distOff1Ref2);

		assert max != -1;

		CoordName resultOff1 = CoordName.EMPTY;
		if (max == distOff1Off2) {
			resultOff1 = CoordName.OFFSET_2;
		} else if (max == distOff1Ref1) {
			resultOff1 = CoordName.REFERENCE_1;
		} else if (max == distOff1Ref2) {
			resultOff1 = CoordName.REFERENCE_2;
		} else {
			assert false : "impossible situation."; //$NON-NLS-1$
		}
		return resultOff1;
	}

	/**
	 * Analyze the offset 2 segment. Will return which coordinate is the
	 * farthest to this point.
	 * 
	 * @param coordsFromOff
	 * @param coordsFromRef
	 * @return
	 */
	private CoordName calculateOffset2(Coordinate[] coordsFromOff, Coordinate[] coordsFromRef) {

		double distOff2Off1 = coordsFromOff[1].distance(coordsFromOff[0]);
		double distOff2Ref1 = coordsFromOff[1].distance(coordsFromRef[0]);
		double distOff2Ref2 = coordsFromOff[1].distance(coordsFromRef[1]);

		double max = calculateLargestDistance(distOff2Off1, distOff2Ref1, distOff2Ref2);

		assert max != -1;

		CoordName resultOff2 = CoordName.EMPTY;
		if (max == distOff2Off1) {
			resultOff2 = CoordName.OFFSET_1;
		} else if (max == distOff2Ref1) {
			resultOff2 = CoordName.REFERENCE_1;
		} else if (max == distOff2Ref2) {
			resultOff2 = CoordName.REFERENCE_2;
		} else {
			assert false : "impossible situation."; //$NON-NLS-1$
		}
		return resultOff2;
	}

	/**
	 * Mount the line for this case, when the farthest coordinate is the same
	 * for both reference 1 and 2 segment.
	 * 
	 * @param resultRef1
	 * @param resultOff1
	 * @param resultOff2
	 * @param finalCoords
	 * @param coordsFromRef
	 * @param coordsFromOff
	 * @return
	 */
	private Coordinate[] mountLineReferencesEqual(	CoordName resultRef1,
													CoordName resultOff1,
													CoordName resultOff2,
													Coordinate[] finalCoords,
													Coordinate[] coordsFromRef,
													Coordinate[] coordsFromOff) {

		if (resultRef1.equals(CoordName.OFFSET_1) && resultOff1.equals(CoordName.REFERENCE_1)) {
			finalCoords[0] = coordsFromOff[0];
			finalCoords[1] = coordsFromRef[0];
		}
		if (resultRef1.equals(CoordName.OFFSET_1) && resultOff1.equals(CoordName.REFERENCE_2)) {
			finalCoords[0] = coordsFromOff[0];
			finalCoords[1] = coordsFromRef[1];
		}
		if (resultRef1.equals(CoordName.OFFSET_2) && resultOff2.equals(CoordName.REFERENCE_1)) {
			finalCoords[0] = coordsFromOff[1];
			finalCoords[1] = coordsFromRef[0];
		}
		if (resultRef1.equals(CoordName.OFFSET_2) && resultOff2.equals(CoordName.REFERENCE_2)) {
			finalCoords[0] = coordsFromOff[1];
			finalCoords[1] = coordsFromRef[1];
		}
		return finalCoords;
	}

	/**
	 * Mount the line for this case, when the farthest coordinate is the same
	 * for both offset 1 and 2 offset.
	 * 
	 * @param resultOff1
	 * @param resultRef1
	 * @param resultRef2
	 * @param finalCoords
	 * @param coordsFromRef
	 * @param coordsFromOff
	 * @return
	 */
	private Coordinate[] mountLineOffsetEqual(	CoordName resultOff1,
												CoordName resultRef1,
												CoordName resultRef2,
												Coordinate[] finalCoords,
												Coordinate[] coordsFromRef,
												Coordinate[] coordsFromOff) {

		if (resultOff1.equals(CoordName.REFERENCE_1) && resultRef1.equals(CoordName.OFFSET_1)) {
			finalCoords[0] = coordsFromRef[0];
			finalCoords[1] = coordsFromOff[0];
		}
		if (resultOff1.equals(CoordName.REFERENCE_1) && resultRef1.equals(CoordName.OFFSET_2)) {
			finalCoords[0] = coordsFromRef[0];
			finalCoords[1] = coordsFromOff[1];
		}
		if (resultOff1.equals(CoordName.REFERENCE_2) && resultRef2.equals(CoordName.OFFSET_1)) {
			finalCoords[0] = coordsFromRef[1];
			finalCoords[1] = coordsFromOff[0];
		}
		if (resultOff1.equals(CoordName.REFERENCE_2) && resultRef2.equals(CoordName.OFFSET_2)) {
			finalCoords[0] = coordsFromRef[1];
			finalCoords[1] = coordsFromOff[1];
		}
		return finalCoords;
	}

	/**
	 * The rest of the cases. Analyze each of them and return the proper
	 * coordinates.
	 * 
	 * @param resultRef1
	 * @param resultRef2
	 * @param resultOff1
	 * @param resultOff2
	 * @param finalCoords
	 * @param coordsFromRef
	 * @param coordsFromOff
	 * @return
	 */
	private Coordinate[] mountLineRestOfTheCases(	CoordName resultRef1,
													CoordName resultRef2,
													CoordName resultOff1,
													CoordName resultOff2,
													Coordinate[] finalCoords,
													Coordinate[] coordsFromRef,
													Coordinate[] coordsFromOff) {

		if (resultRef1.equals(CoordName.REFERENCE_2) && resultRef2.equals(CoordName.REFERENCE_1)) {
			finalCoords[0] = coordsFromRef[0];
			finalCoords[1] = coordsFromRef[1];
		}
		if (resultOff1.equals(CoordName.OFFSET_2) && resultOff2.equals(CoordName.OFFSET_1)) {
			finalCoords[0] = coordsFromOff[0];
			finalCoords[1] = coordsFromOff[1];
		}
		if (resultRef1.equals(CoordName.OFFSET_1) && resultOff1.equals(CoordName.REFERENCE_1)) {
			finalCoords[0] = coordsFromRef[0];
			finalCoords[1] = coordsFromOff[0];
		}
		if (resultRef1.equals(CoordName.OFFSET_2) && resultOff2.equals(CoordName.REFERENCE_1)) {
			finalCoords[0] = coordsFromRef[0];
			finalCoords[1] = coordsFromOff[1];
		}
		if (resultRef2.equals(CoordName.OFFSET_1) && resultOff1.equals(CoordName.REFERENCE_2)) {
			finalCoords[0] = coordsFromRef[1];
			finalCoords[1] = coordsFromOff[0];
		}
		if (resultRef2.equals(CoordName.OFFSET_2) && resultOff2.equals(CoordName.REFERENCE_2)) {
			finalCoords[0] = coordsFromRef[1];
			finalCoords[1] = coordsFromOff[1];
		}
		return finalCoords;
	}

	/**
	 * Calculate the greatest distance of those 3.
	 * 
	 * @param dist1
	 * @param dist2
	 * @param dist3
	 * @return
	 */
	private double calculateLargestDistance(double dist1, double dist2, double dist3) {

		// return the largest distance.
		if (dist1 > dist2) {
			if (dist1 > dist3) {
				return dist1;
			}
			return dist3;
		} else if (dist2 > dist3) {
			return dist2;
		} else {
			return dist3;
		}
	}
}
