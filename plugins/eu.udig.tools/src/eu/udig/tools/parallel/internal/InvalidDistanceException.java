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
 * Manage the distance exception, it will be throw when the calculated distance
 * doesn't feet with the established one.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * 
 */
class InvalidDistanceException extends Exception {

	private static final long	serialVersionUID	= 4448248719766755145L;

	public InvalidDistanceException(String message) {
		super(message);
	}
}
