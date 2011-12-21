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
package eu.udig.tools.merge.internal;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Store the necessary values for doing the merge operation.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeContext {

	private Point				bboxStartPoint	= null;
	private SelectionBoxCommand	shapeCommand	= new SelectionBoxCommand();
	private List<Envelope>		envelopeList	= new ArrayList<Envelope>();
	private List<SimpleFeature>	deletedFeatures	= new ArrayList<SimpleFeature>();

	private MapMouseEvent		mapMouseEvent	= null;

	public synchronized void initContext() {

		bboxStartPoint = null;
		shapeCommand = new SelectionBoxCommand();

		envelopeList.clear();
		deletedFeatures.clear();
	}

	/**
	 * Set the start point of the bbox.
	 * 
	 * @param point
	 */
	public synchronized void setBBoxStartPoint(Point point) {

		assert point != null;

		this.bboxStartPoint = point;
	}

	/**
	 * Get the start point of the bbox.
	 * 
	 * @return
	 */
	public Point getBBoxStartPoint() {

		return this.bboxStartPoint;
	}

	/**
	 * The command uses to draw a bbox.
	 * 
	 * @return the shapeCommand
	 */
	public SelectionBoxCommand getShapeCommand() {

		return shapeCommand;
	}

	/**
	 * Add an envelope to the envelope list.
	 * 
	 * @param bounds
	 */
	public void addEnvelope(Envelope bounds) {

		assert bounds != null;

		envelopeList.add(bounds);
	}

	/**
	 * Remove an envelope from the list of envelopes.
	 * 
	 * @param lastEnvelope
	 */
	public void removeEnvelope(Envelope lastEnvelope) {

		assert lastEnvelope != null;

		envelopeList.remove(lastEnvelope);
	}

	/**
	 * The list of the stored envelopes.
	 * 
	 * @return
	 */
	public List<Envelope> getEnvelopeList() {

		return envelopeList;
	}

	/**
	 * The list with the deleted features.
	 * 
	 * @return
	 */
	public List<SimpleFeature> getDeletedFeatures() {

		return deletedFeatures;
	}

	/**
	 * Add a feature to the deleted feature list.
	 * 
	 * @param feature
	 */
	public void addDeletedFeature(SimpleFeature feature) {

		deletedFeatures.add(feature);
	}

	public void storeMouseLocation(MapMouseEvent e) {

		this.mapMouseEvent = e;

	}

	/**
	 * Return the last know mouse location. It will be the point were the last
	 * selected feature is.
	 * 
	 * @return
	 */
	public MapMouseEvent getMouseLocation() {

		return this.mapMouseEvent;
	}

	/**
	 * These features (lastAddedFeatures) are the last selected features to add.
	 * If they were deleted before, remove from the deleted list.
	 * 
	 * @param lastAddedFeatures
	 */
	public void updateDeletedFeatureList(List<SimpleFeature> lastAddedFeatures) {

		if (deletedFeatures.size() > 0) {
			deletedFeatures.removeAll(lastAddedFeatures);
		}

	}

	/**
	 * Add a list of features to the deleted feature list.
	 * 
	 * @param listOfFeatures
	 */
	public void addDeletedFeature(List<SimpleFeature> listOfFeatures) {

		deletedFeatures.addAll(listOfFeatures);

	}

}
