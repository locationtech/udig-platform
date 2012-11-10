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
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.parallel.internal;

/**
 * 
 * Store the state of the Precision tool. Depending on its
 * Mode, the tool will do different actions.
 *
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 *
 */
public enum PrecisionToolsMode {
	/**
	 * Waiting for set all the parameters.
	 */
	WAITING,
	/**
	 * When the tool is ready to draw the preview.
	 */
	READY,
	/**
	 * Its busy, so wont draw the preview until it finished.
	 */
	BUSY,
	/**
	 * Editing its data manually.
	 */
	EDITING,
	/**
	 * Will start doing DnD.
	 */
	PRE_DRAG,
	/**
	 * It's doing DnD operation.
	 */
	DRAG,
	/**
	 * Finished doing DnD.
	 */
	POST_DRAG,
	/**
	 * There is an error and won't do anything.
	 */
	ERROR,	

}
