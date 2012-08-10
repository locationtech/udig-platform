/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 * 		Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 * 		http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2010, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to license under Lesser General Public License (LGPL).
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
 * Store the direction (forward, backward) and the "until position", that will
 * be a number.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3
 */
final class DataDirectionUntil {

	private boolean	isForwardDirection;
	private int		untilPosition;

	public DataDirectionUntil(boolean isForwardDirection, int untilPosition) {

		assert isForwardDirection == true || isForwardDirection == false : "Must have a value"; //$NON-NLS-1$

		this.isForwardDirection = isForwardDirection;
		this.untilPosition = untilPosition;
	}

	public boolean getIsForwardDirection() {

		return isForwardDirection;
	}

	public int getUntilPosition() {

		return untilPosition;
	}
}
