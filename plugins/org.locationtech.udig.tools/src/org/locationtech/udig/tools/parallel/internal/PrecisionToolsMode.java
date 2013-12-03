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
