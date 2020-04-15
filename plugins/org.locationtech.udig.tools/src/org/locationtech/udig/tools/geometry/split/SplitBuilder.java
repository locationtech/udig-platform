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
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

/**
 * Split builder is responsible of doing the split operation and the add vertex
 * to neighbour in a chain operation.
 * 
 * <p>
 * <b>USAGE:</b>
 * 
 * <p>
 * <pre>
 * SplitBuilder builder = SplitBuilder(splitLine);
 * builder.buildSplit().buildNeighbours();
 * List<SimpleFeature> splitList = builder.getSplitResult();
 * List<SimpleFeature> neighboursList = builder.getNeighbourResult();
 * 
 * </pre>
 * </p>
 * <b>Note:</b> to get a correct result you should call first the method
 * {@link #buildSplit()} and then {@link #buildNeighbours()}.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public final class SplitBuilder {

	/** Maintains a copy of the list of geometries to split. */
	private List<Geometry>	originalGeometryList;

	/** The resultant geometries from the split operation. */
	private List<Geometry>	splitResultList;

	/** The resultant geometries from the neighbour operation. */
	private List<Geometry>	neighbourResultList;

	/** The splitLine. */
	private LineString		splitLine;

	/** Split strategy, responsible of doing the split operation. */
	private SplitStrategy	splitStrategy;

	/**
	 * Use {@link #newInstansceUsingSplitLine(LineString)}
	 */
	private SplitBuilder() {
		// nothing
	}

	/**
	 * Create a new instance of the builder giving the split line.
	 * 
	 * @param splitLine
	 *            The split line.
	 * @return New instance of the builder.
	 */
	public static SplitBuilder newInstansceUsingSplitLine(final LineString splitLine) {

		assert splitLine != null : "can't be null"; //$NON-NLS-1$

		SplitBuilder sb = new SplitBuilder();
		sb.splitLine = splitLine;
		sb.splitStrategy = new SplitStrategy(splitLine);
		sb.splitResultList = new ArrayList<Geometry>();
		sb.neighbourResultList = new ArrayList<Geometry>();
		sb.originalGeometryList = new ArrayList<Geometry>();

		return sb;
	}

	/**
	 * Gives a list of geometries, it will analyze if those geometries will
	 * suffer split or maybe will require the addition of a vertex because the
	 * meet the neighbour requirements.
	 * 
	 * @param allGeometries
	 *            A list of geometries.
	 * @return The builder instance.
	 */
	public SplitBuilder buildEntireProcess(final List<Geometry> allGeometries) {

		List<Geometry> goingToSplit = new ArrayList<Geometry>();
		List<Geometry> goingToNeighbour = new ArrayList<Geometry>();

		for (Geometry geom : allGeometries) {

			if (canSplit(geom)) {
				goingToSplit.add(geom);
			} else {
				goingToNeighbour.add(geom);
			}
		}
		// now that they are separated, run each method.
		buildSplit(goingToSplit);
		buildNeighbours(goingToNeighbour);

		return this;
	}

	/**
	 * Makes the split operation with the given geometries.
	 * 
	 * @param splitGeometries
	 *            A list of geometries to split.
	 * @return The builder instance.
	 */
	public SplitBuilder buildSplit(final List<Geometry> splitGeometries) {

		for (Geometry geom : splitGeometries) {

			if (canSplit(geom)) {
			    this.splitResultList.addAll(split(geom));
			}
		}
		return this;
	}

	/**
	 * After doing the {@link #buildSplit(List)} or
	 * {@link #buildEntireProcess(List)}, this method returns the resultant
	 * geometries from the split operation.
	 * 
	 * @return
	 */
	public List<Geometry> getSplitResult() {

		return this.splitResultList;
	}

	/**
	 * Check if the given geometry can be split or not.
	 * 
	 * @param geom
	 * @return True if it can.
	 */
	private boolean canSplit(Geometry geom) {

		return this.splitStrategy.canSplit(geom);
	}

	/**
	 * Make the split operation for the given geometry.
	 * 
	 * @param geomToSplit
	 * @return A list with the resultant geometries.
	 */
	private List<Geometry> split(final Geometry geomToSplit) {

		this.originalGeometryList.add(geomToSplit);

		return this.splitStrategy.split(geomToSplit);

	}

	/**
	 * For each geometry if it meets the requirements for adding a vertex, it
	 * will tagged as neighbour and a vertex will be added.
	 * 
	 * @param neighbourGeometries
	 * @return The builder instance.
	 */
	public SplitBuilder buildNeighbours(List<Geometry> neighbourGeometries) {

		for (Geometry geom : neighbourGeometries) {

			if (requireVertex(geom)) {

			    this.neighbourResultList.add(addVertexToNeighbour(geom));
			}
		}
		return this;
	}

	/**
	 * After using the method {@link #buildNeighbours(List)}, retrieve the list
	 * of geometries with added vertexes.
	 * 
	 * @return A list of geometries. Those are the neighbour geometries.
	 */
	public List<Geometry> getNeighbourResult() {

		return this.neighbourResultList;
	}

	/**
	 * Check if it requires the addition of a neighbour vertex.
	 * 
	 * @param geomToAddVertex
	 * @return True if it requires.
	 */
	private boolean requireVertex(Geometry geomToAddVertex) {

		for (Geometry geomNeighbor : this.originalGeometryList) {

			if (geomToAddVertex.touches(geomNeighbor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 
	 * Responsible of creating a new geometry that will be the given geometry
	 * with an extra vertex. That vertex will be the point where the given
	 * geometry and the line intersect.
	 * 
	 * </p>
	 * 
	 * @param geomToAddVertex
	 *            neighbor of a geometry that has suffered split operation
	 * @return geomToAddVertex with one or more added vertex.
	 */
	private Geometry addVertexToNeighbour(final Geometry geomToAddVertex) {

		return VertexStrategy.addIntersectionVertex(geomToAddVertex, this.splitLine, this.originalGeometryList);
	}

}
