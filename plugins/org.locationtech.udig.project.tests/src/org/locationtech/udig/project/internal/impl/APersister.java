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

import org.eclipse.ui.IMemento;
import org.locationtech.udig.project.IPersister;

public class APersister extends IPersister<A> {

	public static boolean enabled = true;
	
	@Override
	public Class<A> getPersistee() {
		if (enabled) return A.class;
		return null;
	}

	@Override
	public A load(IMemento memento) {
		return new A("a"); //$NON-NLS-1$
	}

	@Override
	public void save(A object, IMemento memento) {
		memento.putString("message", object.getMessage()); //$NON-NLS-1$
	}
	
}
