/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel.internal;

/**
 * 
 * Store the direction (forward, backward) and the "until position", that will be a number.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3
 */
final class DataDirectionUntil {

    private boolean isForwardDirection;

    private int untilPosition;

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
