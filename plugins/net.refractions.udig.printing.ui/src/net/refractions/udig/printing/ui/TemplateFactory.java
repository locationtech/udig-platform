/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui;

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
