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
package eu.udig.tools.merge;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.commands.SelectionBoxCommand;

import com.vividsolutions.jts.geom.Envelope;

import eu.udig.tools.merge.internal.view.MergeView;

/**
 * Stores the status values of merge interactions.
 * 
 * The inputs for the merge command are grabbed using different user interface techniques like
 * feature selection by bbox drawing and merge feature definition using {@link MergeView}. Thus, this context
 * object provides a site where the merge command's parameters are stored.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeContext {

	private Point				bboxStartPoint	= null;
	private SelectionBoxCommand	selectionBoxCommand	= new SelectionBoxCommand();
	private List<Envelope>		boundList = new ArrayList<Envelope>();
	private MergeView mergeView;

	/**
	 * Reinitializes the status context
	 */
	public synchronized void initContext() {

		bboxStartPoint = null;
		selectionBoxCommand = new SelectionBoxCommand();

		boundList.clear();
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
	 * Returns the start point of the bbox.
	 * 
	 * @return the left upper corner
	 */
	public Point getBBoxStartPoint() {

		return this.bboxStartPoint;
	}

	/**
	 * Returns the instance of {@link SelectionBoxCommand} maintained in this context.
	 * 
	 * @return {@link SelectionBoxCommand}
	 */
	public SelectionBoxCommand getSelectionBoxCommand() {

		return selectionBoxCommand;
	}

	/**
	 * Add an bound to the envelope list.
	 * 
	 * @param bound
	 */
	public void addBound(Envelope bound) {

		assert bound != null;

		boundList.add(bound);
	}

	/**
	 * Removes the indeed bound from the list of bounds.
	 * 
	 * @param bound
	 */
	public void removeBound(Envelope bound) {

		assert bound != null;

		boundList.remove(bound);
	}

	/**
	 * @return the list of bounds 
	 */
	public List<Envelope> getBoundList() {

		return boundList;
	}

	/**
	 * Set the associated merge view
	 */
	public void setMergeView(MergeView view) {
		this.mergeView = view;
		
	}

	/**
	 * 
	 * @return the associated merge view
	 */
	public MergeView getMergeView() {
		
		assert mergeView != null: "the merge view was not set"; //$NON-NLS-1$
		return mergeView;
	}
	
}
