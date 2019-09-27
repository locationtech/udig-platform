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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

import org.locationtech.udig.tools.geometry.merge.MergeStrategy;

/**
 * 
 * This class is responsible of extracting the rings and the "remaining" line from a
 * line that has at least one self-ring.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
public final class RingExtractor {

	/** The geometry factory used to build geometries. */
	private GeometryFactory	gf		= null;

	/** The original line */
	private Geometry		line	= null;

	/**
	 * Default constructor.
	 * 
	 * @param line
	 *            The line to be analyzed.
	 */
	public RingExtractor(Geometry line) {

		// union will create "rings" but they are multilines.
		// so, remove repeated coordinates (the union between each multiline
		// geometry) and then create the line again.

		Geometry linegeom = line.union();
		Coordinate[] coordinates = linegeom.getCoordinates();
		coordinates = CoordinateArrays.removeRepeatedPoints(coordinates);

		this.line = linegeom.getFactory().createLineString(coordinates);
		this.gf = line.getFactory();
	}

	/**
	 * Will execute the extraction operation. First it get the rings, and then
	 * it "extracts" those rings from the original line getting by this way the
	 * remaining line. (it could be a line or a multiline geometry)
	 * 
	 * @return {@link ResultRingExtractor}. It contains 2 object, one is a list
	 *         with the rings and the other is the remaining geometry.
	 */
	public ResultRingExtractor processExtraction() {

		assert this.line != null : "the line can't be null"; //$NON-NLS-1$
		assert this.gf != null : "the geometry factory can't be null"; //$NON-NLS-1$

		// use the Polygonizer to get the rings.
		List<Geometry> ringList = new ArrayList<Geometry>();
		Geometry multiLines =this.line.union();
		Polygonizer polygonizer = new Polygonizer();
		polygonizer.add(multiLines);
		Collection<Polygon> polyCollection = polygonizer.getPolygons();

		// add the rings to the ringList.
		for (Polygon pol : polyCollection) {

			Coordinate[] polCoord = pol.getExteriorRing().getCoordinates();

			LinearRing polygonRing = this.gf.createLinearRing(polCoord);
			ringList.add(polygonRing);
		}

		// get the remaining line.
		Geometry result = getRemainingLine(line.getCoordinates(), ringList);

		// create the output data.
		ResultRingExtractor outputData = new ResultRingExtractor(result, ringList);
		return outputData;
	}

	/**
	 * Get the remaining line by doing the difference operation, and then seek
	 * for overlapped lines on the line with its holes. If it founds, add those
	 * overlapped lines to the remaining line.
	 * 
	 * @param originalCoord
	 *            Coordinates from the original line.
	 * @param ringList
	 *            The extracted rings.
	 * @return The remaining line.
	 */
	private Geometry getRemainingLine(Coordinate[] originalCoord, List<Geometry> ringList) {

		List<Geometry> segmentsToAdd = new LinkedList<Geometry>();
		// apply the difference operation.
		Geometry result = this.line;
		for (Geometry ring : ringList) {

			result = result.difference(ring);
		}
		// union for nodded multilineStrings.
		result = SplitUtil.buildLineUnion(result);
		// seek for overlapped segments.
		for (int i = 0; i < originalCoord.length - 1; i++) {

			// mount a segment and see if its intersection with any other
			// segment from the original line return a lineString as result,
			// this would mean that it is overlapped.
			Coordinate[] segCoords = new Coordinate[2];
			segCoords[0] = originalCoord[i];
			segCoords[1] = originalCoord[i + 1];
			LineString segmentToTest = this.gf.createLineString(segCoords);
			for (int j = i + 1; j < originalCoord.length - 1; j++) {

				Coordinate[] testCoord = new Coordinate[2];
				testCoord[0] = originalCoord[j];
				testCoord[1] = originalCoord[j + 1];
				LineString eachSegment = this.gf.createLineString(testCoord);

				if (segmentToTest.intersects(eachSegment)
							&& segmentToTest.intersection(eachSegment) instanceof LineString) {

					// first store those segments
					segmentsToAdd.add(segmentToTest.intersection(eachSegment));
				}
			}
		}
		if (segmentsToAdd.size() != 0) {
			result = nodSegments(segmentsToAdd, ringList, result);
		}
		return result;
	}

	/**
	 * Node contiguous segments.
	 * 
	 * @param segmentsToAdd
	 *            Segments to be nodded with the actualResultLine.
	 * @param ringList
	 *            The list of rings.
	 * @param actualResultLine
	 *            The actual lineString.
	 * @return The segments nodded to the actualResultLine if they can be
	 *         nodded.
	 */
	private Geometry nodSegments(List<Geometry> segmentsToAdd, List<Geometry> ringList, Geometry actualResultLine) {

		Geometry result = actualResultLine;
		// check if that lineString exist or is contained in any of
		// the rings.
		for (Geometry segment : segmentsToAdd) {

			assert segment.getCoordinates().length == 2 : "the segment should have 2 coordinates."; //$NON-NLS-1$
			// check with each ring.
			for (Geometry ring : ringList) {
				// contained
				if (ring.contains(segment) || ring.intersects(segment)) {

					result = MergeStrategy.mergeOp(result, segment);
				}
			}
		}

		return result;
	}

	/**
	 * Data class that stores the ring extractor data. It'll be the remaining
	 * line and the rings.
	 */
	public class ResultRingExtractor {

		/* Stores the remaining line. */
		private Geometry		remainingLine;
		/* Stores the extracted rings. */
		private List<Geometry>	rings;

		/**
		 * Constructor used to store the data.
		 * 
		 * @param remainingLine
		 *            The remaining line.
		 * @param rings
		 *            Rings obtained from the extraction process.
		 */
		public ResultRingExtractor(Geometry remainingLine, List<Geometry> rings) {

			this.remainingLine = remainingLine;
			this.rings = rings;
		}

		/**
		 * Get the remaining line.
		 * 
		 * @return A lineString or multiLineString.
		 */
		public Geometry getRemainingLine() {

			return this.remainingLine;
		}

		/**
		 * Get the extracted rings.
		 * 
		 * @return A list of linearRings.
		 */
		public List<Geometry> getRings() {

			return this.rings;
		}

	}

}
