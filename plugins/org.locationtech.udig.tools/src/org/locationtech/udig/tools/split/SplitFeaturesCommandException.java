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
package org.locationtech.udig.tools.split;

/**
 * Exception used when doing the split process, something goes wrong (i.e.
 * illegalArgument, transformException, etc. ), this exception is thrown.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */
final class SplitFeaturesCommandException extends Exception {

	private static final long	serialVersionUID	= -2661168727803487052L;

	public SplitFeaturesCommandException(String message) {
		super(message);
	}
}
