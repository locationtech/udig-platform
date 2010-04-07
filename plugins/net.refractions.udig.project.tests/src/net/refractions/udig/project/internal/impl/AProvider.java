package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.IProvider;

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