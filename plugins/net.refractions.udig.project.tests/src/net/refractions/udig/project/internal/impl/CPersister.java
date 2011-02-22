package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.IPersister;

import org.eclipse.ui.IMemento;

public class CPersister extends IPersister<C> {

	public static boolean enabled = true;

	@Override
	public Class<C> getPersistee() {
		if (enabled) return C.class;
		return null;
	}

	@Override
	public C load(IMemento memento) {
		return new C("c"); //$NON-NLS-1$
	}

	@Override
	public void save(C object, IMemento memento) {
		memento.putString("message", object.getMessage()); //$NON-NLS-1$
	}

}
