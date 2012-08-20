package net.refractions.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class RenderContextTest extends AbstractProjectTestCase {

	private IRenderContext context;

	@Before
	public void setUp() throws Exception {
		SimpleFeature[] features=UDIGTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
		Map map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features,true), new Dimension(512,512));
		context=map.getRenderManagerInternal().getRenderExecutor().getContext();
	}
	
	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.getImage(int, int)'
	 */
	@Test
	public void testGetImageIntInt() {
		BufferedImage image = context.getImage(32,32);
		assertNotNull( "BufferedImage created", image );
		assertEquals( "width", 32, image.getWidth() );
		assertEquals( "height", 32, image.getHeight() );
	}

}
