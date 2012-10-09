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
package net.refractions.udig.tool.info.tests;

import java.awt.Dimension;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.tests.support.MapTests;

import org.junit.Test;

public class BetterWMSTest extends AbstractProjectUITestCase {

	@Test
	public void testWMS() throws Exception {
		Dimension displaySize = new Dimension(400, 400);
		IMap map = MapTests.createDefaultMap("bork", 4, true, displaySize);
		
	}
}
