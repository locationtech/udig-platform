/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import org.locationtech.udig.catalog.IResolve;

/**
 * Dummy class for testing, wraps around provided delegate.
 * @author Jesse
 * @since 1.1.0
 */
public class ResolvedTo {
	IResolve delegate;
	public ResolvedTo( IResolve delegate ){
		this.delegate = delegate;
	}
	@Override
	public String toString() {
		return "ResolvedTo<"+delegate.getIdentifier()+">";
	}

}
