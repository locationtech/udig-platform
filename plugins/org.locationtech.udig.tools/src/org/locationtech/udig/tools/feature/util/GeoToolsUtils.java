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
package org.locationtech.udig.tools.feature.util;

import java.util.HashMap;
import java.util.Map;

import javax.measure.Unit;

import org.geotools.geometry.jts.CoordinateSequenceTransformer;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.util.CRSUtilities;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

/**
 * GeoTools Utility
 * <p>
 * This class does have convenient methods used in this module to work with GeoTools
 * </p>
 * 
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * 
 * @since 1.1.0
 */
public class GeoToolsUtils {

	private GeoToolsUtils() {

		// util class
	}

	/**
	 * Adds the matching attributes from <code>source</code> to
	 * <code>target</code>
	 * <p>
	 * Two attributes match if they have the same name and type.
	 * </p>
	 * 
	 * @param source
	 * @param target
	 * @throws IllegalAttributeException
	 */
	public static void copyAttributes(SimpleFeature source, SimpleFeature target) throws IllegalAttributeException {

		Map<String, Class<?>> sourceTypes = new HashMap<String, Class<?>>();
		for (AttributeDescriptor att : source.getFeatureType().getAttributeDescriptors()) {
			sourceTypes.put(att.getLocalName(), att.getType().getBinding());
		}
		for (AttributeDescriptor att : target.getFeatureType().getAttributeDescriptors()) {
			String name = att.getLocalName();
			Class<?> sourceBinding = sourceTypes.get(name);
			if (sourceBinding != null && sourceBinding == att.getType().getBinding()) {
				Object attribute = source.getAttribute(name);
				target.setAttribute(name, attribute);
			}
		}
	}

	/**
	 * Returns a representative (first axis with a length) unit of the given
	 * crs.
	 * 
	 * @param crs
	 * @return a representative unit of the given crs.
	 */
	public static Unit<?> getDefaultCRSUnit(CoordinateReferenceSystem crs) {

		assert crs != null;
		CoordinateSystem coordinateSystem = crs.getCoordinateSystem();
		Unit<?> unit = CRSUtilities.getUnit(coordinateSystem);
		if (unit == null) {
			CoordinateSystemAxis axis = coordinateSystem.getAxis(0);
			unit = axis.getUnit();
		}
		return unit;
	}

	/**
	 * @param gFactory
	 * @param geomCrs
	 * @param reprojectCrs
	 * @return a GeometryCoordinateSequenceTransformer configured to transform
	 *         geometries from <code>geomCrs</code> to <code>reprojectCrs</code>
	 * @throws OperationNotFoundException
	 */
	public static GeometryCoordinateSequenceTransformer getTransformer(	final GeometryFactory gFactory,
																		final CoordinateReferenceSystem geomCrs,
																		final CoordinateReferenceSystem reprojectCrs)
		throws OperationNotFoundException {

		assert geomCrs != null;
		assert reprojectCrs != null;

		CoordinateSequenceFactory csFactory;
		CoordinateSequenceTransformer csTransformer;
		GeometryCoordinateSequenceTransformer transformer;

		csFactory = gFactory.getCoordinateSequenceFactory();
		csTransformer = new CoordSeqFactoryPreservingCoordinateSequenceTransformer(csFactory);
		transformer = new GeometryCoordinateSequenceTransformer(csTransformer);
		MathTransform mathTransform;
		try {
			mathTransform = CRS.findMathTransform(geomCrs, reprojectCrs, true);
		} catch (FactoryException e) {
			throw new OperationNotFoundException(e.getMessage());
		}

		transformer.setMathTransform(mathTransform);

		return transformer;
	}

	/**
	 * Reprojects <code>geom</code> from <code>geomCrs</code> to
	 * <code>reprojectCrs</code>
	 * 
	 * @param geom
	 *            the geometry to reproject
	 * @param geomCrs
	 *            the original CRS of <code>geom</code>
	 * @param reprojectCrs
	 *            the CRS to reproject <code>geom</code> onto
	 * @return <code>geom</code> reprojected from <code>geomCrs</code> to
	 *         <code>reprojectCrs</code>
	 * @throws OperationNotFoundException
	 *             if there isn't a transformation in GeoTools to convert from
	 *             <code>geomCrs</code> to <code>reprojectCrs</code>
	 * @throws TransformException
	 */
	public static Geometry reproject(	final Geometry geom,
										final CoordinateReferenceSystem geomCrs,
										final CoordinateReferenceSystem reprojectCrs)
		throws OperationNotFoundException, TransformException {

		assert geom != null : "geom cannot be null";
		assert geomCrs != null : "geomCrs cannot be null";
		assert reprojectCrs != null : "reprojectCrs cannot be null";

		if (geomCrs.equals(reprojectCrs)) {
			return geom;
		}
		if (CRS.equalsIgnoreMetadata(geomCrs, reprojectCrs)) {
			return geom;
		}

		GeometryFactory gFactory = geom.getFactory();

		GeometryCoordinateSequenceTransformer transformer;
		transformer = getTransformer(gFactory, geomCrs, reprojectCrs);
		Geometry geometry;
		try {
			geometry = transformer.transform(geom);
		} catch (TransformException e) {
			throw e;
		}
		return geometry;
	}

	/**
	 * @param featureType
	 * @return the dimension of default geometry
	 */
	public static int getDimensionOf(final SimpleFeatureType featureType) {

		GeometryDescriptor geomAttr = featureType.getGeometryDescriptor();

		Class<?> geomClass = geomAttr.getType().getBinding();

		int dim = GeometryUtil.getDimension(geomClass);

		return dim;
	}

	/**
	 * Utility method to easily reproject a line segment as LineSegment is not a
	 * Geometry
	 * 
	 * @param segment
	 *            the segment to reproject
	 * @param segmentCrs
	 *            the CRS the segment coordinates are in
	 * @param reprojectCrs
	 *            the CRS to reproject the segment to
	 * @return a new line segment built from the reprojected coordinates of
	 *         <code>segment</code> from <code>segmentCrs</code> to
	 *         <code>reprojectCrs</code>
	 */
	public static LineSegment reproject(LineSegment segment,
										CoordinateReferenceSystem segmentCrs,
										CoordinateReferenceSystem reprojectCrs) {

		assert segment != null;
		assert segmentCrs != null;
		assert reprojectCrs != null;

		if (segmentCrs.equals(reprojectCrs)) {
			return segment;
		}
		if (CRS.equalsIgnoreMetadata(segmentCrs, reprojectCrs)) {
			return segment;
		}

		MathTransform mathTransform;
		try {
			mathTransform = CRS.findMathTransform(segmentCrs, reprojectCrs, true);
		} catch (FactoryException e) {
			throw new RuntimeException(e.getMessage());
		}
		double[] src = { segment.p0.x, segment.p0.y, segment.p1.x, segment.p1.y };
		double[] dst = new double[4];
		try {
			mathTransform.transform(src, 0, dst, 0, 2);
		} catch (TransformException e) {
			throw new RuntimeException(e.getMessage());
		}
		Coordinate p0 = new Coordinate(dst[0], dst[1]);
		Coordinate p1 = new Coordinate(dst[2], dst[3]);
		LineSegment lineSegment = new LineSegment(p0, p1);
		return lineSegment;
	}
}
