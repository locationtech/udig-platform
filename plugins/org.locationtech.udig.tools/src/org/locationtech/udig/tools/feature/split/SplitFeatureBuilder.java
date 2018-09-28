/* 
 * uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.feature.split;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.data.DataUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import org.locationtech.udig.tools.feature.util.GeoToolsUtils;
import org.locationtech.udig.tools.geometry.internal.util.GeometryUtil;
import org.locationtech.udig.tools.geometry.split.SplitStrategy;
import org.locationtech.udig.tools.geometry.split.VertexStrategy;
import org.locationtech.udig.tools.internal.i18n.Messages;

/**
 * <p>
 * This class is responsible of doing the split operation and adding the vertex
 * to the neighbour.
 * </p>
 * <p>
 * <b>USAGE:</b>
 * <p>
 * 
 * <pre>
 * SplitFeatureBuilder builder = SplitFeatureBuilder.newInstance(featureList,
 * 		splitLine, mapCRS);
 * builder.buildSplit().buildNeighbours();
 * List&lt;SimpleFeature&gt; splitList = builder.getSplitResult();
 * List&lt;SimpleFeature&gt; neighboursList = builder.getNeighbourResult();
 * 
 * </pre>
 * 
 * </p>
 * <p>
 * Additionally, you could get the list of original features which have suffered
 * split, calling {@link #getFeaturesThatSufferedSplit()}.
 * </p>
 * <p>
 * <b>Note:</b> to get a correct result you should call first the method
 * {@link #buildSplit()} and then {@link #buildNeighbours()}.
 * </p>
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.0
 */
public final class SplitFeatureBuilder {

	/** Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(SplitFeatureBuilder.class.getName());

	/** Maintains a copy of the list of geometries to split. */
	private List<Geometry> originalGeometryList;

	/** The resultant features from the split operation. */
	private List<SimpleFeature> splitResultList;

	/** The resultant features from the neighbour operation. */
	private List<SimpleFeature> neighbourResultList = null;

	/** The splitLine. */
	private LineString splitLine;

	/** Split strategy, responsible of doing the split operation. */
	private SplitStrategy splitStrategy;

	/** Input featureList. */
	private List<SimpleFeature> featureList;

	/** The CRS where the operation will be made. */
	private CoordinateReferenceSystem desiredCRS;

	/** Maintains a copy of the features that had been split. */
	private List<SimpleFeature> featuresThatSufferedSplit;

	/**
	 * Use this {@link #newInstance(List)}
	 */
	private SplitFeatureBuilder() {
		// nothing
	}

	/**
	 * Create a new instance of the builder giving the split line, the feature
	 * list and the CRS which the operation will be based on.
	 * 
	 * @param featureList
	 *            A non empty list of features.
	 * @param splitLine
	 *            The split line.
	 * @param desiredCRS
	 *            the crs where the split operation is done it
	 * @return a new instance of the builder. 	 
	 * 
	 * @throws IllegalArgumentException
	 * @throws SplitFeatureBuilderFailException
	 */
	public static SplitFeatureBuilder newInstance(
			final List<SimpleFeature> featureList, final LineString splitLine,
			final CoordinateReferenceSystem desiredCRS)
			throws IllegalArgumentException, SplitFeatureBuilderFailException {

		if (featureList == null)
			throw new IllegalArgumentException("The source feature list is required"); //$NON-NLS-1$
		if (splitLine == null)
			throw new IllegalArgumentException("The split line is required"); //$NON-NLS-1$
		if (desiredCRS == null)
			throw new IllegalArgumentException(
					"The desired crs is required, i.e: map crs"); //$NON-NLS-1$
		if (!(splitLine.getUserData() instanceof CoordinateReferenceSystem))
			throw new IllegalArgumentException(
					"Set the crs of the line before calling newInstance (splitLine.setUserData(CoordinateReferenceSystem))"); //$NON-NLS-1$

		LOGGER.fine("featureList parameter:" + prettyPrint(featureList)); //$NON-NLS-1$
		LOGGER.fine("splitLine parameter:" + splitLine.toText()); //$NON-NLS-1$
		LOGGER.fine(((CoordinateReferenceSystem) splitLine.getUserData())
				.toWKT());

		try {
			LineString splitReprojected = (LineString) GeoToolsUtils.reproject(
					splitLine,
					(CoordinateReferenceSystem) splitLine.getUserData(),
					desiredCRS);
			SplitFeatureBuilder sfb = new SplitFeatureBuilder();
			sfb.splitLine = splitReprojected;
			sfb.splitStrategy = new SplitStrategy(splitLine);
			sfb.splitResultList = null;
			sfb.originalGeometryList = new LinkedList<Geometry>();
			sfb.featuresThatSufferedSplit = new LinkedList<SimpleFeature>();
			sfb.featureList = featureList;
			sfb.desiredCRS = desiredCRS;

			return sfb;

		} catch (Exception e) {
			throw makeFailException(e);
		}
	}

	/**
	 * Create a new instance of the builder giving the split line, the feature
	 * list and the CRS which the operation will be based on.
	 * 
	 * @param feature
	 *            the feature to split
	 * @param splitLine
	 *            line used as reference to split the feature
	 * @param desiredCRS
	 *            the crs in which the split operation will be executed
	 */
	public static SplitFeatureBuilder newInstance(final SimpleFeature feature,
			final LineString splitLine,
			final CoordinateReferenceSystem desiredCRS)
			throws SplitFeatureBuilderFailException {

		List<SimpleFeature> featureList = new LinkedList<SimpleFeature>();
		featureList.add(feature);

		return newInstance(featureList, splitLine, desiredCRS);
	}

	/**
	 * Print information of the features contained in the list.
	 * 
	 * @param featureList
	 *            A list of features.
	 * @return An string containing information about all those features.
	 */
	private static String prettyPrint(List<SimpleFeature> featureList) {

		StringBuilder strBuilder = new StringBuilder("\n"); //$NON-NLS-1$
		for (SimpleFeature f : featureList) {
			strBuilder.append("Feature Id -- Geometry: ").append(f.getID()) //$NON-NLS-1$
					.append(" -- ").append(f.getDefaultGeometry()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return strBuilder.toString();
	}

	/**
	 * Analyze the feature list, and for those features that can suffer split
	 * operation, they'll be split.
	 * 
	 * @return The builder instance.
	 * @throws SplitFeatureBuilderFailException
	 *             if the operation fail
	 * @throws CannotSplitException
	 *             if the split line cannot divide the feature's geometry
	 */
	public SplitFeatureBuilder buildSplit()
			throws SplitFeatureBuilderFailException, CannotSplitException {

		try {
			this.splitResultList = new LinkedList<SimpleFeature>();
			boolean existSplit = false;
			for (SimpleFeature feature : this.featureList) {

				Geometry geomToSplit = (Geometry) feature.getDefaultGeometry();
				assert geomToSplit.isValid() : "No Valid Geometry: " + geomToSplit.toText(); //$NON-NLS-1$
				CoordinateReferenceSystem featureCrs = feature.getFeatureType()
						.getCoordinateReferenceSystem();
				geomToSplit = GeoToolsUtils.reproject(geomToSplit, featureCrs, this.desiredCRS);

				if (canSplit(geomToSplit)) {
					existSplit = true;
					this.featuresThatSufferedSplit.add(feature);

					List<Geometry> splitGeometriesResult = split(geomToSplit);
					this.splitResultList.addAll( createSplitFeatures(splitGeometriesResult, feature) );
				}
			}
			if (!existSplit) {
			        throw new CannotSplitException(Messages.SplitFeatureBuilder_cannotSplit);
			}
		} catch (OperationNotFoundException e) {
			throw makeFailException(e);
		} catch (TransformException e) {
			throw makeFailException(e);
		}
		return this;
	}

	private static SplitFeatureBuilderFailException makeFailException(
			Exception e) {

		e.printStackTrace();
		final String msg = e.getMessage();
		LOGGER.severe(msg);
		return new SplitFeatureBuilderFailException(msg);
	}

	/**
	 * Creates the resultant features.
	 * 
	 * @param splitGeometries
	 *            List with the new geometries.
	 * @param feature
	 *            The old feature.
	 * @throws OperationNotFoundException
	 * @throws TransformException
	 */
	private List<SimpleFeature> createSplitFeatures(
			final List<Geometry> splitGeometries,
			final SimpleFeature feature) 
		throws OperationNotFoundException, TransformException {

		final SimpleFeatureType featureType = feature.getFeatureType();
		final CoordinateReferenceSystem featureCrs = featureType
				.getCoordinateReferenceSystem();

		Class<? extends Geometry> geometryType = (Class<? extends Geometry>) featureType
				.getGeometryDescriptor().getType().getBinding();

		List<SimpleFeature> splitFeatureList = new LinkedList<SimpleFeature>();
		for (Geometry splittedPart : splitGeometries) {

			splittedPart = GeoToolsUtils.reproject(splittedPart, desiredCRS, featureCrs);

			splittedPart = GeometryUtil.adapt(splittedPart, geometryType);
			SimpleFeature newFeature = DataUtilities.template(featureType);
			GeoToolsUtils.copyAttributes(feature, newFeature);
			newFeature.setDefaultGeometry(splittedPart);
			
			splitFeatureList.add(newFeature);
		}
		
		return splitFeatureList;

	}

	/**
	 * Retrieve the result of the split operation. Use this function after
	 * calling {@link #buildSplit()}.
	 * 
	 * @return a SimpleFeature List or the empty list
	 */
	public List<SimpleFeature> getSplitResult() {

		if( splitResultList == null) {
			throw new IllegalStateException("the buildSplit method must be called before"); //$NON-NLS-1$
		}

		return splitResultList;
	}

	/**
	 * Check if the split operation can be applied.
	 * 
	 * @param geom
	 *            Geometry to validate.
	 * @return True if the geometry is able to suffer split operation.
	 */
	private boolean canSplit(Geometry geom) {

		return this.splitStrategy.canSplit(geom);
	}

	/**
	 * Makes the split operation.
	 * 
	 * @param geomToSplit
	 *            Geometry that will suffer split operation.
	 * @return A list with the resultant geometries.
	 */
	private List<Geometry> split(final Geometry geomToSplit) {

		this.originalGeometryList.add(geomToSplit);

		return this.splitStrategy.split(geomToSplit);

	}

	/**
	 * Analyzes the feature list, take the features that are neighbour and if
	 * they meet the requirements, add a vertex to them.
	 * 
	 * @return The builder instance.
	 * @throws SplitFeatureBuilderFailException
	 */
	public SplitFeatureBuilder buildNeighbours()
			throws SplitFeatureBuilderFailException {
		
		this.neighbourResultList = new ArrayList<SimpleFeature>();
		try {
			for (SimpleFeature feature : featureList) {

				Geometry geom = (Geometry) feature.getDefaultGeometry();
				CoordinateReferenceSystem featureCRS = feature.getFeatureType()
						.getCoordinateReferenceSystem();
				geom = GeoToolsUtils.reproject(geom, featureCRS, desiredCRS);

				if (!canSplit(geom) && (requireVertex(geom))) {

					Geometry geomWithAddedVertex = addVertexToNeighbour(geom);
					geomWithAddedVertex = GeoToolsUtils.reproject(
							geomWithAddedVertex, desiredCRS, featureCRS);
					feature.setDefaultGeometry(geomWithAddedVertex);
					this.neighbourResultList.add(feature);
				}
			}
		} catch (Exception e) {
			throw makeFailException(e);
		}
		return this;
	}

	/**
	 * Retrieve the neighbor features. Use this after calling
	 * {@link #buildNeighbours()}.
	 * 
	 * @return a SimpleFeature list or the empty list
	 */
	public List<SimpleFeature> getNeighbourResult() {

		if( this.neighbourResultList == null){
			throw new IllegalStateException("the method buildNeighbours() must be called before"); //$NON-NLS-1$
		}

		return neighbourResultList;
	}

	/**
	 * Check if it requires the addition of a neighbor vertex.
	 * 
	 * @param geomToAddVertex
	 * @return True if it requires.
	 */
	private boolean requireVertex(Geometry geomToAddVertex) {

		for (Geometry geomNeighbor : originalGeometryList) {

			if (geomToAddVertex.touches(geomNeighbor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Responsible of creating a new geometry that will be the given geometry
	 * with an extra vertex. That vertex will be the point where the given
	 * geometry and the line intersect.
	 * </p>
	 * 
	 * @param geomToAddVertex
	 *            neighbor of a geometry that has suffered split operation
	 * @return geomToAddVertex with one or more added vertex.
	 */
	private Geometry addVertexToNeighbour(final Geometry geomToAddVertex) {

		return VertexStrategy.addIntersectionVertex(geomToAddVertex,
				this.splitLine, originalGeometryList);
	}

	/**
	 * Get the list of the original features witch have suffered the split operation.
	 * 
	 * @return a SimpleFeature list or the EmptyList;
	 */
	public List<SimpleFeature> getFeaturesThatSufferedSplit() {
		assert featuresThatSufferedSplit != null;

		return featuresThatSufferedSplit;
	}
}
