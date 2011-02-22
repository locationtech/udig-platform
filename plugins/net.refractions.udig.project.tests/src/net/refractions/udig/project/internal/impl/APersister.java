package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.IPersister;

import org.eclipse.ui.IMemento;

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
