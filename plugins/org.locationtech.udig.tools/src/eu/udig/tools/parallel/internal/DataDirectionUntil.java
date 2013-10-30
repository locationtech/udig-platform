/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
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
