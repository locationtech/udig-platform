package net.refractions.udig.project.internal.render.impl;

import java.awt.Dimension;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.feature.Feature;

public class RenderContextTest extends AbstractProjectTestCase {

	private IRenderContext context;

	@Override
	protected void setUp() throws Exception {
        super.setUp();

		Feature[] features=UDIGTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
		Map map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features,true), new Dimension(512,512));
		context=map.getRenderManagerInternal().getRenderExecutor().getContext();
	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.getImage(int, int)'
	 */
	public void testGetImageIntInt() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.hasContent(Point)'
	 */
	public void testHasContent() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.copyImage(Rectangle)'
	 */
	public void testCopyImage() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.compareTo(RenderContext)'
	 */
	public void testCompareTo() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.clearImage()'
	 */
	public void testClearImage() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderContextImpl.clearImage(Rectangle)'
	 */
	public void testClearImageRectangle() {

	}

}
