/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.testsupport;

import java.awt.Dimension;

import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

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
