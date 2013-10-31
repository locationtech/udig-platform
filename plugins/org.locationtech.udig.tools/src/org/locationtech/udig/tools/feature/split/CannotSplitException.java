/* uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.feature.split;


/**
 * Custom exception used on {@link SplitFeatureBuilder}.
 * This exception is thrown if the {@link SplitFeatureBuilder} cannot split the feature with the 
 * split line provided
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 */
public final class CannotSplitException extends
		SplitFeatureBuilderException {

	private static final long serialVersionUID = -1420893046862002184L;


	public CannotSplitException(String ex) {
		super(ex);
	}
}
