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
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.IPersister;

import org.eclipse.ui.IMemento;

public class BPersister extends IPersister<B> {

	public static boolean enabled = true;
	
	@Override
	public Class<B> getPersistee() {
		if (enabled) return B.class;
		return null;
	}

	@Override
	public B load(IMemento memento) {
		return new B("b"); //$NON-NLS-1$
	}

	@Override
	public void save(B object, IMemento memento) {
		memento.putString("message", object.getMessage()); //$NON-NLS-1$
	}
	
}