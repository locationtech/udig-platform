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
