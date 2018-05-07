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
package org.locationtech.udig.info.tests;

import java.awt.Dimension;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.tests.support.MapTests;

import org.junit.Test;

public class BetterWMSTest extends AbstractProjectUITestCase {

	@Test
	public void testWMS() throws Exception {
		Dimension displaySize = new Dimension(400, 400);
		IMap map = MapTests.createDefaultMap("bork", 4, true, displaySize);
		
	}
}
