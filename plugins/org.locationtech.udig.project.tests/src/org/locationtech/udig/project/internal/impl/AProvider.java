/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import org.locationtech.udig.project.IProvider;

public class AProvider extends IProvider<A> {

	@Override
	public Class<A> getProvidee() {
		return A.class;
	}

	@Override
	public A provide() {
		return new A("a"); //$NON-NLS-1$
	}
	
}
