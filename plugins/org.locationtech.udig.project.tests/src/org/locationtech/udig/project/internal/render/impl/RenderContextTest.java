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
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.core.testsupport.FeatureCreationTestUtil;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.testsupport.AbstractProjectTestCase;
import org.locationtech.udig.project.testsupport.MapTests;
import org.opengis.feature.simple.SimpleFeature;

public class RenderContextTest extends AbstractProjectTestCase {

	private IRenderContext context;

	@Before
	public void setUp() throws Exception {
		SimpleFeature[] features=FeatureCreationTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
		Map map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features,true), new Dimension(512,512));
		context=map.getRenderManagerInternal().getRenderExecutor().getContext();
	}
	
	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderContextImpl.getImage(int, int)'
	 */
	@Test
	public void testGetImageIntInt() {
		BufferedImage image = context.getImage(32,32);
		assertNotNull( "BufferedImage created", image );
		assertEquals( "width", 32, image.getWidth() );
		assertEquals( "height", 32, image.getHeight() );
	}

}
