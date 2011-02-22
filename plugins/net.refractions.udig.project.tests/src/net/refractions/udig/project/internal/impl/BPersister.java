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
