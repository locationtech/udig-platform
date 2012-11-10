/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.feature.split;


/**
 * Custom exception used on {@link SplitFeatureBuilder}.
 * This exception is thrown if the split build fail.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.0
 */
public class SplitFeatureBuilderFailException extends SplitFeatureBuilderException {

	/* UID */
	private static final long	serialVersionUID	= 2977282794220494837L;

	/**
	 * Default constructor.
	 * 
	 * @param ex
	 *            Exception.
	 */
	public SplitFeatureBuilderFailException(String ex) {
		super(ex);
	}

}
