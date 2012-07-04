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
