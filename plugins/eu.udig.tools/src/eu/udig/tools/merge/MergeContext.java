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
import net.refractions.udig.project.ui.tool.IToolContext;

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

	//private static final MergeContext THIS = new MergeContext();
	
	private Point				bboxStartPoint	= null;
	private SelectionBoxCommand	selectionBoxCommand	= new SelectionBoxCommand();
	private List<Envelope>		boundList = new ArrayList<Envelope>();
	private MergeView 			mergeView = null;
	private IToolContext 		toolContext = null;

	/**
	 * Singleton use the getInstance methods
	 */
	private MergeContext(){
		// singleton
	}
	
	/**
	 * Singleton
	 * @return return the instance of {@link MergeContext}
	 * 
	 */
	public static MergeContext getInstance(){
		return new MergeContext();
	}
	
	public IToolContext getToolContext() {
		return toolContext;
	}

	public void setToolContext(IToolContext toolContext) {
		this.toolContext = toolContext;
	}

	/**
	 * Reinitializes the status context
	 */
	public void initContext() {

		bboxStartPoint = null;
		selectionBoxCommand = new SelectionBoxCommand();
		
		mergeView = null;
		toolContext = null;

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
	 * 
	 * @return the associated merge view
	 */
	public MergeView getMergeView() {
		return mergeView;
	}

	/**
	 * 
	 * @return true If a merge view is opened, false in other case
	 */
	public boolean isMergeViewActive() {
		
		return (mergeView != null) && !mergeView.isDisposed();
	}

	/**
	 * 
	 */
	public void disposeMergeView() {

		this.mergeView = null;
	}
	

	/**
	 * Set the associated merge view
	 */
	public void activeMergeView(MergeView view) {
		this.mergeView = view;
		
	}
	
	
}
