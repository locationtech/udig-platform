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
package org.locationtech.udig.tools.geometry.internal.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.algorithm.HCoordinate;
import org.locationtech.jts.algorithm.NotRepresentableException;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.polygonize.Polygonizer;

import org.locationtech.udig.tools.geometry.merge.MergeStrategy;
import org.locationtech.udig.tools.internal.i18n.Messages;

/**
 * Geometry utility methods
 * <p>
 * Collection of method which gets feature or feature collection to applay
 * geometry operations
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
public class GeometryUtil {

	/**
	 * unused
	 */
	private GeometryUtil() {

		// util class
	}

	/**
	 * Returns a geometry which is the union of all the non null default
	 * geometries from the features in <code>featureCollection</code>
	 * 
	 * @param featureCollection
	 * @param expectedGeometryClass
	 * 
	 * @return Geometry Union
	 */
	public static Geometry geometryUnion(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {

		Geometry resultGeom = null;
		FeatureIterator<SimpleFeature>  iterator = featureCollection.features();
		try {
			SimpleFeature currFeature;
			while (iterator.hasNext()) {

				currFeature = iterator.next();
				Geometry featureGeom = (Geometry) currFeature.getDefaultGeometry();
				assert featureGeom != null : "the feature must have almost one geometry"; //$NON-NLS-1$

				featureGeom.normalize();
				if (resultGeom == null) {
					resultGeom = featureGeom;
				} else {
					resultGeom = MergeStrategy.mergeOp(resultGeom, featureGeom);
				}
			}

		} finally {
			iterator.close();
		}
		return resultGeom;
	}

	/**
	 * Extracts the geometries and makes a geometry array.
	 * <p>
	 * Note the resulting array size might be lower than the featureCollection
	 * size, as it will not contain null geometries.
	 * </p>
	 * 
	 * @param featureCollection
	 * @return Geometry[] geometries present in features
	 */
	public static Geometry[] extractGeometries(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {

		FeatureIterator<SimpleFeature> iter = featureCollection.features();
		try {
			SimpleFeature feature;
			ArrayList<Geometry> geometries = new ArrayList<Geometry>(featureCollection.size());
			int finalSize = 0;
			while (iter.hasNext()) {

				feature = iter.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				if (geometry == null) {
					continue;
				} else if (geometry instanceof GeometryCollection) {

					GeometryCollection geomSet = (GeometryCollection) geometry;
					final int size = geomSet.getNumGeometries();
					for (int j = 0; j < size; j++) {
						geometries.add(geomSet.getGeometryN(j));
					}
					finalSize += size;

				} else {
					geometries.add(geometry);
					finalSize++;
				}
			}

			return geometries.toArray(new Geometry[finalSize]);

		} finally {
			iter.close();
		}

	}

	/**
	 * Adapts a Geometry <code>geom</code> to another type of geometry given the
	 * desired geometry class.
	 * <p>
	 * Currently implemented adaptations:
	 * <ul>
	 * <li>Point -> MultiPoint. Wraps the Point on a single part MultiPoint.
	 * <li>Polygon -> MultiPolygon. Wraps the Polygon on a single part
	 * MultiPolygon.
	 * <li>LineString -> MultiLineString. Wraps the LineString on a single part
	 * MultiLineString.
	 * <li>MultiLineString -> LineString. Succeeds if merging the parts result
	 * in a single LineString, fails otherwise.
	 * <li>MultiPolygon -> Polygon. Succeeds if merging the parts result in a
	 * single Polygon, fails otherwise.
	 * <li>* -> GeometryCollection
	 * </ul>
	 * 
	 * If the given type of geometry is the same as the desired one, returns
	 * that type of geometry.
	 * </p>
	 * TODO: add more adaptations on an as needed basis
	 * 
	 * @param inputGeom
	 * @param adaptTo
	 * @return a new Geometry adapted
	 * @throws IllegalArgumentException
	 *             if <code>geom</code> cannot be adapted as <code>adapTo</code>
	 */
	public static Geometry adapt(final Geometry inputGeom, final Class<? extends Geometry> adaptTo) {

		assert inputGeom != null : "inputGeom can't be null"; //$NON-NLS-1$
		assert adaptTo != null : "adaptTo can't be null"; //$NON-NLS-1$

		if (Geometry.class.equals(adaptTo)) {
			return inputGeom;
		}
		final Class<?> geomClass = inputGeom.getClass();

		if (geomClass.equals(adaptTo)) {
			return inputGeom;
		}

		final GeometryFactory gf = inputGeom.getFactory();

		if (MultiPoint.class.equals(adaptTo) && Point.class.equals(geomClass)) {
			return gf.createMultiPoint(new Point[] { (Point) inputGeom });
		}

		if (Polygon.class.equals(adaptTo)) {
			if (adaptTo.equals(geomClass)) {
				return inputGeom;
			}
			Polygonizer polygonnizer = new Polygonizer();
			polygonnizer.add(inputGeom);
			Collection<? extends Polygon> polys = polygonnizer.getPolygons();
			Polygon[] polygons = new ArrayList<Polygon>(polys).toArray(new Polygon[polys.size()]);

			if (polygons.length == 1) {
				return polygons[0];
			}
		}

		if (MultiPolygon.class.equals(adaptTo)) {
			if (adaptTo.equals(geomClass)) {
				return inputGeom;
			}
			if (Polygon.class.equals(geomClass)) {
				return gf.createMultiPolygon(new Polygon[] { (Polygon) inputGeom });
			}
			/*
			 * Polygonizer polygonnizer = new Polygonizer();
			 * polygonnizer.add(inputGeom); Collection polys =
			 * polygonnizer.getPolygons(); Polygon[] polygons = new
			 * ArrayList<Polygon>(polys).toArray(new Polygon[polys.size()]); if
			 * (MultiPolygon.class.equals(adaptTo)) { return
			 * gf.createMultiPolygon(polygons); } if (polygons.length == 1) {
			 * return polygons[0]; }
			 */
		}

		if (GeometryCollection.class.equals(adaptTo)) {
			return gf.createGeometryCollection(new Geometry[] { inputGeom });
		}

		if (MultiLineString.class.equals(adaptTo) || LineString.class.equals(adaptTo)) {
			LineMerger merger = new LineMerger();
			merger.add(inputGeom);
			Collection<? extends LineString> mergedLineStrings = merger.getMergedLineStrings();
			ArrayList<LineString> lineList = new ArrayList<LineString>(mergedLineStrings);
			LineString[] lineStrings = lineList.toArray(new LineString[mergedLineStrings.size()]);

			assert lineStrings.length >= 1;

			if (MultiLineString.class.equals(adaptTo)) {
				MultiLineString line = gf.createMultiLineString(lineStrings);
				return line;
			} else { // adapts to LineString class
				if (lineStrings.length == 1) {
					Geometry mergedResult = (Geometry) lineStrings[0];
					return mergedResult;

				} else { // it has more than one geometry
					final String msg = MessageFormat.format(Messages.GeometryUtil_DonotKnowHowAdapt,
								geomClass.getSimpleName(), adaptTo.getSimpleName());
					throw new IllegalArgumentException(msg);
				}
			}
		}
		if (Polygon.class.equals(adaptTo) && (MultiPolygon.class.equals(geomClass))) {
			// adapts multipolygon to polygon

			assert inputGeom.getNumGeometries() == 1 : "the collection must have 1 element to adapt to Polygon"; //$NON-NLS-1$
			return inputGeom.getGeometryN(1);

		} else if (LineString.class.equals(adaptTo) && (MultiLineString.class.equals(geomClass))) {
			// adapts MultiLinestring to Linestring

			assert inputGeom.getNumGeometries() == 1 : "the collection must have 1 element to adapt to Polygon"; //$NON-NLS-1$
			return inputGeom.getGeometryN(1);
		}
		// Adapt a geometry collection that contains points, to Point or
		// MultiPoint.
		if (GeometryCollection.class.equals(geomClass)
					&& (MultiPoint.class.equals(adaptTo) || Point.class.equals(adaptTo))) {
			List<Geometry> geomList = new ArrayList<Geometry>();
			for (int i = 0; i < inputGeom.getNumGeometries(); i++) {
				Geometry geom = inputGeom.getGeometryN(i);
				Class<?> clazz = geom.getClass();
				if (MultiPoint.class.equals(clazz) || Point.class.equals(clazz)) {
					geomList.add(geom);
				}
			}
			return gf.buildGeometry(geomList);
		}

		// Adapt a geometry collection that contains lines, to LineString or
		// MultiLineString.
		if (GeometryCollection.class.equals(geomClass)
					&& (MultiLineString.class.equals(adaptTo) || LineString.class.equals(adaptTo))) {
			List<Geometry> geomList = new ArrayList<Geometry>();
			for (int i = 0; i < inputGeom.getNumGeometries(); i++) {
				Geometry geom = inputGeom.getGeometryN(i);
				Class<?> clazz = geom.getClass();
				if (MultiLineString.class.equals(clazz) || LineString.class.equals(clazz)) {
					geomList.add(geom);
				}
			}
			return gf.buildGeometry(geomList);
		}

		// Adapt a geometry collection that contains polygons, to Polyong or
		// MultiPolygon.
		if (GeometryCollection.class.equals(geomClass)
					&& (MultiPolygon.class.equals(adaptTo) || Polygon.class.equals(adaptTo))) {
			List<Geometry> geomList = new ArrayList<Geometry>();
			for (int i = 0; i < inputGeom.getNumGeometries(); i++) {
				Geometry geom = inputGeom.getGeometryN(i);
				Class<?> clazz = geom.getClass();
				if (MultiPolygon.class.equals(clazz) || Polygon.class.equals(clazz)) {
					geomList.add(geom);
				}
			}
			return gf.buildGeometry(geomList);
		}

		final String msg = MessageFormat.format(Messages.GeometryUtil_DonotKnowHowAdapt, geomClass.getSimpleName(),
					adaptTo.getSimpleName());

		throw new IllegalArgumentException(msg);
	}

	/**
	 * Creates a geometry collection using the simple geometries contained in
	 * the gem List.
	 * 
	 * @param simpleGeometries
	 *            homogeneous list of zero or more geometries of the class:
	 *            Points, LineStrings or Polygons
	 * @param expectedClass
	 *            expected result class
	 * 
	 * @return a new instance of GeometryCollection with the simple geometries
	 */
	public static GeometryCollection adaptToGeomCollection(	ArrayList<Geometry> simpleGeometries,
															Class<? extends GeometryCollection> expectedClass) {

		if (simpleGeometries.size() == 0) {

			return GeometryUtil.getNullGeometryCollection();
		}

		final GeometryFactory geomFactory = simpleGeometries.get(0).getFactory();
		GeometryCollection geomResult = null;
		if (MultiPolygon.class.equals(expectedClass)) {
			geomResult = geomFactory.createMultiPolygon(simpleGeometries.toArray(new Polygon[simpleGeometries.size()]));
		} else if (MultiLineString.class.equals(expectedClass)) {
			geomResult = geomFactory.createMultiLineString(simpleGeometries.toArray(new LineString[simpleGeometries
						.size()]));
		} else if (MultiPoint.class.equals(expectedClass)) {
			geomResult = geomFactory.createMultiPoint(simpleGeometries.toArray(new Point[simpleGeometries.size()]));

		} else {

			assert false : "illegal argument: the expectedClass parameter must be a subclass of GeometryCollection"; //$NON-NLS-1$
		}

		return geomResult;
	}

	/**
	 * 
	 * @return an empty geometry collection
	 */
	private static GeometryCollection getNullGeometryCollection() {

		GeometryFactory gf = new GeometryFactory();
		return gf.createGeometryCollection(new Geometry[] {});
	}

	/**
	 * creates the source geometry array to the class required
	 * 
	 * @param sourceGeomArray
	 *            one or more geometries
	 * @param requiredGeom
	 * 
	 * @return new geometry of the required class
	 */
	public static Geometry adapt(final ArrayList<Geometry> sourceGeomArray, final Class<? extends Geometry> requiredGeom) {

		assert sourceGeomArray.size() >= 1 : "one or more geometries are required in the source geometry collection"; //$NON-NLS-1$

		final GeometryFactory geomFactory = sourceGeomArray.get(0).getFactory();

		Geometry result;
		if (GeometryCollection.class.equals(requiredGeom.getSuperclass())) {

			Class<? extends GeometryCollection> geomCollectionClass = (Class<? extends GeometryCollection>) requiredGeom;
			result = adaptToGeomCollection(sourceGeomArray, geomCollectionClass);

		} else {// simple geometry is required

			result = geomFactory.buildGeometry(sourceGeomArray);
		}

		return result;
	}

	/**
	 * @param geomClass
	 * @return the geometry class's dimension
	 */
	public static int getDimension(final Class<?> geomClass) {

		if ((Point.class.equals(geomClass)) || (MultiPoint.class.equals(geomClass))) {
			return 0;
		} else if ((LineString.class.equals(geomClass)) || (MultiLineString.class.equals(geomClass))) {
			return 1;
		} else if ((Polygon.class.equals(geomClass)) || (MultiPolygon.class.equals(geomClass))) {
			return 2;
		} else {
			final String msg = MessageFormat.format(Messages.GeometryUtil_CannotGetDimension, geomClass.getName());
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * @param simpleGeometry
	 *            Point, LineString or Polygon class
	 * @return a compatible Geometry collection for the simple geometry
	 */
	public static Class<? extends GeometryCollection> getCompatibleCollection(final Class<? extends Geometry> simpleGeometry) {

		if (Point.class.equals(simpleGeometry)) {
			return MultiPoint.class;
		} else if (LineString.class.equals(simpleGeometry)) {
			return MultiLineString.class;
		} else if (Polygon.class.equals(simpleGeometry)) {
			return MultiPolygon.class;
		} else {
			throw new IllegalArgumentException(Messages.GeometryUtil_ExpectedSimpleGeometry);
		}
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Converts the feature LineString into Polygon. If the LineString geometry
	 * can't be converted, (i.e, the line has 2 coordinates, so its impossible
	 * to create a linearRing) throws {@link IllegalArgumentException}.
	 * 
	 * The line must has at least 3 coordinates.
	 * 
	 * - Get the line geometry.
	 * - For each geometry, create a LinerRing.
	 * - Combine all the LinearRing into one Geometry.
	 * 
	 * Return the combined geometry.
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param feature
	 * @return
	 */
	public static Geometry convertLineStringIntoPolygonGeometry(SimpleFeature feature) {

		GeometryFactory factory = new GeometryFactory();
		Geometry featureGeom = (Geometry) feature.getDefaultGeometry();

		List<Geometry> ringsList = new ArrayList<Geometry>();

		for (int i = 0; i < featureGeom.getNumGeometries(); i++) {

			Geometry line = featureGeom.getGeometryN(i);

			Coordinate[] lineCoor = line.getCoordinates();
			if (lineCoor.length < 3) {
				// not a valid geometry for convert to polygon, so continue.
				continue;
			}
			// for creating a linearRing, the geometry must form a closed
			// geometry.
			// close the geometry.
			Coordinate[] closedLine = closeGeometry(lineCoor);
			// try to create a linearRing.
			LinearRing ring;
			try {
				ring = factory.createLinearRing(closedLine);
				// Create the polygon geometry and add to the list.
				Geometry polygonGeometry = factory.createPolygon(ring, null);
				ringsList.add(polygonGeometry);
			} catch (Exception e) {
				// Couldn't create a ring from this geometry,so continue.
				continue;
			}
		}

		Geometry combined = factory.buildGeometry(ringsList);
		// combined.union();

		return combined;
	}

	/**
	 * This coordinates belong to a LineString geometry, it's coordinates aren't
	 * closed, close the coordinate. The first coordinate must be equal to the
	 * last coordinate.
	 * 
	 * @param lineCoor
	 * @return
	 */
	public static Coordinate[] closeGeometry(Coordinate[] lineCoor) {

		if (lineCoor[0].equals(lineCoor[lineCoor.length - 1])) {
			// it's closed so return it.
			return lineCoor;
		}

		Coordinate[] closedCoor = new Coordinate[lineCoor.length + 1];
		int i;
		for (i = 0; i < lineCoor.length; i++) {
			closedCoor[i] = lineCoor[i];
		}
		// closed the geometry by setting the last coordinate equal to the first
		// coordinate
		closedCoor[i] = lineCoor[0];

		assert closedCoor[0].equals(closedCoor[closedCoor.length - 1]);

		return closedCoor;

	}

	private static Coordinate[][]	inputLines	= new Coordinate[2][2];

	/**
	 * This method computes the actual value of the intersection point. To
	 * obtain the maximum precision from the intersection calculation, the
	 * coordinates are normalized by subtracting the minimum ordinate values (in
	 * absolute value). This has the effect of removing common significant
	 * digits from the calculation to maintain more bits of precision.
	 * 
	 * @param precisionModel
	 */
	public static Coordinate intersection(Coordinate p1, Coordinate p2, Coordinate q1, Coordinate q2) {
		inputLines[0][0] = p1;
		inputLines[0][1] = p2;
		inputLines[1][0] = q1;
		inputLines[1][1] = q2;
		Coordinate intPt = intersectionWithNormalization(p1, p2, q1, q2);

		return intPt;
	}

	private static Coordinate intersectionWithNormalization(Coordinate p1, Coordinate p2, Coordinate q1, Coordinate q2) {
		Coordinate n1 = new Coordinate(p1);
		Coordinate n2 = new Coordinate(p2);
		Coordinate n3 = new Coordinate(q1);
		Coordinate n4 = new Coordinate(q2);
		Coordinate normPt = new Coordinate();
		normalizeToEnvCentre(n1, n2, n3, n4, normPt);

		Coordinate intPt = safeHCoordinateIntersection(n1, n2, n3, n4);

		intPt.x += normPt.x;
		intPt.y += normPt.y;

		return intPt;
	}

	/**
	 * Normalize the supplied coordinates to so that the midpoint of their
	 * intersection envelope lies at the origin.
	 * 
	 * @param n00
	 * @param n01
	 * @param n10
	 * @param n11
	 * @param normPt
	 */
	private static void normalizeToEnvCentre(	Coordinate n00,
												Coordinate n01,
												Coordinate n10,
												Coordinate n11,
												Coordinate normPt) {
		double minX0 = n00.x < n01.x ? n00.x : n01.x;
		double minY0 = n00.y < n01.y ? n00.y : n01.y;
		double maxX0 = n00.x > n01.x ? n00.x : n01.x;
		double maxY0 = n00.y > n01.y ? n00.y : n01.y;

		double minX1 = n10.x < n11.x ? n10.x : n11.x;
		double minY1 = n10.y < n11.y ? n10.y : n11.y;
		double maxX1 = n10.x > n11.x ? n10.x : n11.x;
		double maxY1 = n10.y > n11.y ? n10.y : n11.y;

		double intMinX = minX0 > minX1 ? minX0 : minX1;
		double intMaxX = maxX0 < maxX1 ? maxX0 : maxX1;
		double intMinY = minY0 > minY1 ? minY0 : minY1;
		double intMaxY = maxY0 < maxY1 ? maxY0 : maxY1;

		double intMidX = (intMinX + intMaxX) / 2.0;
		double intMidY = (intMinY + intMaxY) / 2.0;
		normPt.x = intMidX;
		normPt.y = intMidY;

		/*
		 * // equilavalent code using more modular but slower method Envelope
		 * env0 = new Envelope(n00, n01); Envelope env1 = new Envelope(n10,
		 * n11); Envelope intEnv = env0.intersection(env1); Coordinate intMidPt
		 * = intEnv.centre();
		 * 
		 * normPt.x = intMidPt.x; normPt.y = intMidPt.y;
		 */

		n00.x -= normPt.x;
		n00.y -= normPt.y;
		n01.x -= normPt.x;
		n01.y -= normPt.y;
		n10.x -= normPt.x;
		n10.y -= normPt.y;
		n11.x -= normPt.x;
		n11.y -= normPt.y;
	}

	/**
	 * Computes a segment intersection using homogeneous coordinates. Round-off
	 * error can cause the raw computation to fail, (usually due to the segments
	 * being approximately parallel). If this happens, a reasonable
	 * approximation is computed instead.
	 * 
	 * @param p1
	 *            a segment endpoint
	 * @param p2
	 *            a segment endpoint
	 * @param q1
	 *            a segment endpoint
	 * @param q2
	 *            a segment endpoint
	 * @return the computed intersection point
	 */
	private static Coordinate safeHCoordinateIntersection(Coordinate p1, Coordinate p2, Coordinate q1, Coordinate q2) {

		Coordinate intPt = null;
		try {
			intPt = HCoordinate.intersection(p1, p2, q1, q2);
		} catch (NotRepresentableException e) {
			// compute an approximate result
			//TODO: TEST THIS
			RobustLineIntersector i = new RobustLineIntersector();
			i.computeIntersection(p1, p2, q1, q2);
			intPt = i.getIntersection(0);
//			intPt = CentralEndpointIntersector.getIntersection(p1, p2, q1, q2);
		}
		return intPt;
	}

	/**
	 * Rotate the given lineString with the provided angle.
	 * 
	 * @param input
	 *            The lineString to rotate.
	 * @param angle
	 *            The angle in radiant.
	 * @return The rotated lineString.
	 */
	public static LineString rotation(LineString input, double angle) {

		Coordinate center = input.getCentroid().getCoordinate();
		return rotation(input, angle, center);
	}

	/**
	 * Rotate the given lineString with the provided angle and a center point as
	 * a reference point required for rotate.
	 * 
	 * @param input
	 *            The input lineString to rotate.
	 * @param angle
	 *            The angle in radiant.
	 * @param center
	 *            The center point as reference to rotate.
	 * @return The rotated lineString.
	 */
	public static LineString rotation(LineString input, double angle, Coordinate center) {

		GeometryFactory gf = input.getFactory();

		Coordinate[] lineCoords = input.getCoordinates();
		List<Coordinate> rotationCoords = new LinkedList<Coordinate>();

		for (int i = 0; i < lineCoords.length; i++) {

			Coordinate actualCoord = lineCoords[i];
			rotationCoords.add(rotate(actualCoord, center, angle));
		}

		Coordinate[] newCoords = rotationCoords.toArray(new Coordinate[rotationCoords.size()]);

		return gf.createLineString(newCoords);
	}

	/**
	 * Rotate the given polygon.
	 * 
	 * @param input
	 *            The polygon to rotate.
	 * @param angle
	 *            Angle in radiant.
	 * @return The rotated polygon.
	 */
	public static Polygon rotation(Polygon input, double angle) {

		Coordinate center = input.getCentroid().getCoordinate();
		return rotation(input, angle, center);
	}

	/**
	 * Rotates the polygon.
	 * 
	 * @param input
	 *            The polygon to rotate
	 * @param angle
	 *            Angle in radiant.
	 * @param pivot
	 *            x,y coordinate reference needed to rotate.
	 * @return a new polygon
	 */
	public static Polygon rotation(Polygon input, double angle, Coordinate pivot) {

		GeometryFactory gf = input.getFactory();

		Coordinate[] shellCoords = input.getExteriorRing().getCoordinates();
		List<Coordinate> rotationShell = new LinkedList<Coordinate>();

		for (int i = 0; i < shellCoords.length; i++) {

			Coordinate actualCoord = shellCoords[i];
			rotationShell.add(rotate(actualCoord, pivot, angle));
		}

		List<LinearRing> holes = new LinkedList<LinearRing>();

		for (int j = 0; j < input.getNumInteriorRing(); j++) {

			List<Coordinate> rotationHole = new LinkedList<Coordinate>();
			Coordinate[] holeCoords = input.getInteriorRingN(j).getCoordinates();
			for (int i = 0; i < holeCoords.length; i++) {

				Coordinate actualCoord = holeCoords[i];
				rotationHole.add(rotate(actualCoord, pivot, angle));
			}
			Coordinate[] rotatedHole = rotationHole.toArray(new Coordinate[rotationHole.size()]);
			holes.add(gf.createLinearRing(rotatedHole));
		}

		Coordinate[] newCoords = rotationShell.toArray(new Coordinate[rotationShell.size()]);
		LinearRing shell = gf.createLinearRing(newCoords);

		LinearRing[] rings = holes.toArray(new LinearRing[holes.size()]);

		return gf.createPolygon(shell, rings);
	}

	/**
	 * Rotate the given coordinate.
	 * 
	 * @param actualCoord
	 *            Coordinate to rotate.
	 * @param center
	 *            Reference coordinate needed to rotate.
	 * @param angle
	 *            Angle in radiant.
	 * @return The rotated coordinate.
	 */
	private static Coordinate rotate(Coordinate actualCoord, Coordinate center, double angle) {

		Coordinate rotateCoord = new Coordinate();
		rotateCoord.x = (Math.cos(angle) * (actualCoord.x - center.x)) - (Math.sin(angle) * (actualCoord.y - center.y))
					+ center.x;
		rotateCoord.y = (Math.sin(angle) * (actualCoord.x - center.x)) + (Math.cos(angle) * (actualCoord.y - center.y))
					+ center.y;

		assert rotateCoord != null;

		return rotateCoord;
	}


}
