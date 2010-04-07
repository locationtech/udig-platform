/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.tests.internal;

import net.refractions.udig.catalog.IResolve;

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
