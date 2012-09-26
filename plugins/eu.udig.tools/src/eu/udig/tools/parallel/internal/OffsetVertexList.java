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
package eu.udig.tools.parallel.internal;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Stores the offset vertex on a list, which is the same of stores the
 * coordinates of the parallel curve.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class OffsetVertexList {

	private static final Coordinate[]	COORDINATE_ARRAY_TYPE	= new Coordinate[0];

	private ArrayList<Coordinate>		ptList;
	private PrecisionModel				precisionModel			= null;

	/**
	 * The distance below which two adjacent points on the curve are considered
	 * to be coincident. This is chosen to be a small fraction of the offset
	 * distance.
	 */
	private double						minimimVertexDistance	= 0.0;

	/**
	 * Change when there is intersection.
	 */
	private boolean						isIntersected			= false;

	public OffsetVertexList() {
		ptList = new ArrayList<Coordinate>();
	}

	public void setPrecisionModel(PrecisionModel precisionModel) {
		this.precisionModel = precisionModel;
	}

	public void setMinimumVertexDistance(double minimimVertexDistance) {
		this.minimimVertexDistance = minimimVertexDistance;
	}

	public void addPt(Coordinate pt, boolean isIntersectionPt) {

		Coordinate ptToAdd = new Coordinate(pt);
		precisionModel.makePrecise(ptToAdd);
		// don't add duplicate (or near-duplicate) points
		if (isDuplicate(ptToAdd))
			return;

		if (isIntersectionPt) {
			ptList.remove(ptList.size() - 1);
			ptList.add(ptToAdd);
			isIntersected = isIntersectionPt;
			return;
		}
		if (isIntersected) {
			// don't add this point.
			isIntersected = false;
			return;
		}
		ptList.add(ptToAdd);
	}

	/**
	 * Tests whether the given point duplicates the previous point in the list
	 * (up to tolerance)
	 * 
	 * @param pt
	 * @return true if the point duplicates the previous point
	 */
	private boolean isDuplicate(Coordinate pt) {
		if (ptList.size() < 1)
			return false;
		Coordinate lastPt = (Coordinate) ptList.get(ptList.size() - 1);
		double ptDist = pt.distance(lastPt);
		if (ptDist < minimimVertexDistance)
			return true;
		return false;
	}

	private Coordinate[] getCoordinates() {

		Coordinate[] coord = (Coordinate[]) ptList.toArray(COORDINATE_ARRAY_TYPE);
		return coord;
	}

	@Override
	public String toString() {
		GeometryFactory fact = new GeometryFactory();
		LineString line = fact.createLineString(getCoordinates());
		return line.toString();
	}

	public int size() {

		return ptList.size();
	}

	public ArrayList<Coordinate> getList() {

		return ptList;
	}

	/**
	 * Delete the last added point.
	 */
	public void deleteLast() {

		ptList.remove(ptList.size() - 1);
	}

	/**
	 * Delete the last item from the list.
	 */
	public Coordinate getLastItem() {

		return ptList.get(ptList.size() - 1);
	}

	/**
	 * Delete the second to last item.
	 */
	public Coordinate getSecondToLastItem() {

		return ptList.get(ptList.size() - 2);
	}
}
