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
package net.refractions.udig.project.tests.support;

import java.awt.Dimension;

import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

public class TestMapDisplay implements IMapDisplay {

	private Dimension size;

	public TestMapDisplay(Dimension displaySize) {
		size=displaySize;
	}

	public Dimension getDisplaySize() {
		return size;
	}

	public int getWidth() {
		return size.width;
	}

	public int getHeight() {
		return size.height;
	}

	public int getDPI() {
		return 72;
	}

}
