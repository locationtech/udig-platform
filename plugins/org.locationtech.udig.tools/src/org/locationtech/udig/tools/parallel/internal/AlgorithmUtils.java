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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

/**
 * 
 * Contains commons/util algorithms used by the parallel classes.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public final class AlgorithmUtils {

    public static int forwardIntersectedSegmentsCounter(final LineString subjectLine,
            final Coordinate[] subjectLineCoord, int j, int n, Coordinate[] inputList,
            GeometryFactory gf, Coordinate intersectionCoord) {

		// the segment that will be tested if intersects.
		LineString interSegment = null;
		Coordinate candidateCoord = null;
		int counter = 0;
		// make a complete turn and get with the last intersection that
		// intersects with the provided segment.

		for (int h = j;;) {

			counter++;
			h = (h + 1) % n;
			// mount a segment.
			Coordinate[] interCoord = new Coordinate[2];
			interCoord[0] = inputList[h];
			interCoord[1] = (h == n - 1) ? inputList[0] : inputList[h + 1];
			interSegment = gf.createLineString(interCoord);
			// get the intersection coordinate if it intersects.
			candidateCoord = getIntersectionCoord(subjectLine, interSegment, subjectLineCoord);
			if (candidateCoord != null && candidateCoord.equals2D(intersectionCoord)) {
				break;
			}
			if (h == j) {
				// if it has been checked all the segment and any intersection
				// has been found, get out the loop and return nothing ??
				break;
			}
		}
		return counter;
	}

	public static int backwardIntersectedSegmentCounter(final Geometry subjectLine,
														final Coordinate[] subjectLineCoord,
														int j,
														int n,
														Coordinate[] inputList,
														GeometryFactory gf,
														Coordinate intersectionCoord) {

		// the segment that will be tested if intersects.
		LineString interSegment = null;
		Coordinate candidateCoord = null;
		int counter = 0;
		// make a complete turn and get with the last intersection that
		// intersects with the provided segment.
		for (int h = j;;) {

			counter++;
			// mount a segment.
			Coordinate[] interCoord = new Coordinate[2];
			interCoord[0] = inputList[h];
			interCoord[1] = (h == 0) ? inputList[n - 1] : inputList[h - 1];
			interSegment = gf.createLineString(interCoord);
			// get the intersection coordinate if it intersects.
			candidateCoord = getIntersectionCoord(subjectLine, interSegment, subjectLineCoord);
			if (candidateCoord != null && candidateCoord.equals2D(intersectionCoord)) {
				break;
			}

			h = (h == 0) ? n - 1 : h - 1;

			if (h == j) {
				// if it has been checked all the segment and any intersection
				// has been found, get out the loop and return nothing ??
				break;
			}
		}
		return counter;
	}

	/**
	 * Get the intersection coordinate if exist, otherwise will return null.
	 * 
	 * @param firstSegment
	 *            First segment to be checked.
	 * @param interSegment
	 *            Second segment to be checked.
	 * @param firstSegC
	 *            Both coordinates of the first segment.
	 * @return The coordinate or null if it doesn't exist.
	 */
	public static Coordinate getIntersectionCoord(	final Geometry firstSegment,
													final Geometry interSegment,
													final Coordinate[] firstSegC) {

		Coordinate candidateCoord = null;

		// if intersects.
		if (firstSegment.intersects(interSegment)) {

			Geometry resultPoints = firstSegment.intersection(interSegment);

			for (int k = 0; k < resultPoints.getNumGeometries(); k++) {

				Coordinate intersectionCoord = resultPoints.getGeometryN(k).getCoordinate();
				// check the intersection isn't the start and the end point of
				// the segment.
				if (!(firstSegC[1].equals2D(intersectionCoord)) && !(firstSegC[0].equals2D(intersectionCoord))) {

					candidateCoord = new Coordinate(intersectionCoord);
					break;
				}
			}
		}
		return candidateCoord;
	}
}
