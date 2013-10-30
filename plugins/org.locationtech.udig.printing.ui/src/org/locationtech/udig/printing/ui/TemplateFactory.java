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
package org.locationtech.udig.printing.ui;

/**
 * The TemplateFactory is used by the printing system to instantiate
 * instances of Templates so that they can be used for printing.
 * 
 * @author rgould
 */
public interface TemplateFactory {

	/**
	 * Instantiates a new instance of a Template.
	 */
	public Template createTemplate();
	
	/**
	 * The name of the templates that this factory produces.
	 * This must be human-readable. For example, a list of all the
	 * names of the Templates available may be presented to a user.
	 */
	public String getName();
}
