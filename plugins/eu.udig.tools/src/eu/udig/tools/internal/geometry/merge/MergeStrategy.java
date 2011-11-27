/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.internal.geometry.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

/**
 * <p>
 * 
 * <pre>
 * Strategy for union of geometries.
 * 
 * For Polygon and Point it makes the union defined by JTS, but for 
 * lineString and multiLineString it has the next behaviour:
 * 
 *  - Merge 2 or more lineStrings, if the result could be a simple lineString
 *   with 1 geometry, it does, doesn't matter the layer feature type(on JTS union,
 *    if the feature type is MultiLineString, the union of 3 lineString will be a MultiLineString 
 *    with 3 geometries.)
 *  - Merge 2 or more lineString, and the result couldn't be a simple lineString, so, it creates
 *  a MultiLineString with as many geometries as lineStrings are merged.
 * </pre>
 * 
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.2.0
 */
public class MergeStrategy {

	/* One of the geometries to merge. */
	private Geometry	mergeGeometry	= null;

	/**
	 * Default constructor.
	 * 
	 * Stores one of the geometries you want to merge.
	 * 
	 * @param mergeGeometry
	 *            One of the geometries to merge.
	 */
	public MergeStrategy(Geometry mergeGeometry) {

		if (mergeGeometry == null) {
			throw new NullPointerException();
		}

		this.mergeGeometry = mergeGeometry;
	}

	/**
	 * Realize merge operation between the given geometries.
	 * 
	 * @param mergeGeometry
	 *            The first geometry to merge.
	 * @param withGeometry
	 *            The second geometry to merge.
	 * @return The merged geometry.
	 */
	public static Geometry mergeOp(Geometry mergeGeometry, Geometry withGeometry) {

		MergeStrategy strategy = new MergeStrategy(mergeGeometry);

		Geometry merged = strategy.merge(withGeometry);
		return merged;
	}

	/**
	 * Realize the merge between the mergeGeometry (given on the constructor)
	 * and this geometry.
	 * 
	 * @param withGeometry
	 *            Geometry to merge with.
	 * @return The merged geometry.
	 */
	public Geometry merge(Geometry withGeometry) {

		if (withGeometry == null) {
			throw new NullPointerException();
		}

		Geometry mergeResult = null;

		if (mergeGeometry.getClass().equals(MultiLineString.class) || mergeGeometry.getClass().equals(LineString.class)) {

			mergeResult = unionLines(withGeometry);
		} else {

			mergeResult = mergeGeometry.union(withGeometry);
		}

		return mergeResult;
	}

	/**
	 * Function used to merge lineStrings. With the aid of lineMerger, merge
	 * lineStrings and MultiLineStrings.
	 * 
	 * @param withGeometry
	 *            Geometry to merge with, it'll be a lineString or a
	 *            MultiLineString.
	 * @return The merged geometry.
	 */
	private Geometry unionLines(Geometry withGeometry) {

		Geometry result = null;

		LineMerger merger = new LineMerger();

		merger.add(mergeGeometry);
		merger.add(withGeometry);

		Collection<? extends LineString> mergedLineStrings = merger.getMergedLineStrings();

		Iterator<? extends LineString> it = mergedLineStrings.iterator();

		if (mergedLineStrings.size() == 1) {
			result = it.next();
		} else {
			GeometryFactory gf = mergeGeometry.getFactory();
			ArrayList<LineString> lineList = new ArrayList<LineString>(mergedLineStrings);
			LineString[] lineStrings = lineList.toArray(new LineString[mergedLineStrings.size()]);
			result = gf.createMultiLineString(lineStrings);
		}

		return result;
	}
}
