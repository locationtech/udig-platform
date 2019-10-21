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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class RenderExecutorTest extends AbstractProjectTestCase {

	private Map map;
	
	int w=512,h=512;

	public static boolean INTERACTIVE=true;

	@Before
	public void setUp() throws Exception {
		GeometryFactory factory=new GeometryFactory();
		
		LinearRing ring1=factory.createLinearRing(new Coordinate[]{
				new Coordinate(0,0),
				new Coordinate(512,0),
				new Coordinate(512,256),
				new Coordinate(0,256),
				new Coordinate(0,0),
				
		});
		
		Polygon poly1=factory.createPolygon( ring1, new LinearRing[]{});
		
		LinearRing ring2=factory.createLinearRing(new Coordinate[]{
				new Coordinate(0,256),
				new Coordinate(512,256),
				new Coordinate(512,512),
				new Coordinate(0,512),
				new Coordinate(0,256),
				
		});
		
		Polygon poly2=factory.createPolygon( ring2, new LinearRing[]{});
		
		SimpleFeature[] features=UDIGTestUtil.createTestFeatures("testType", new Geometry[]{poly1, poly2},new String[]{},  //$NON-NLS-1$
				DefaultEngineeringCRS.CARTESIAN_2D);
		map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features, true), new Dimension(w,h));
		StyleBuilder sb=new StyleBuilder();
		Style style=sb.createStyle(features[0].getName().getLocalPart(), sb.createPolygonSymbolizer(Color.BLACK,Color.BLACK, 1));
		SLDContent.apply(map.getLayersInternal().get(0), style, null);
        List<IRenderer> renderers = map.getRenderManagerInternal().getRenderers();
        for( IRenderer renderer : renderers ) {
            ((Renderer)renderer).eSetDeliver(false);
        }
	}
	
	@After
	public void tearDown() throws Exception {
		map.getRenderManagerInternal().dispose();
	}
	
	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.setState(int)'
	 */
	@Test
	public void testSetState() {
		
	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.render(Graphics2D, IProgressMonitor)'
	 */
	@Ignore
	@Test
	public void testRenderGraphics2DIProgressMonitor() throws Exception {
		BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);

		
		Graphics2D g=image.createGraphics();
		g.setBackground(new Color( 0,0,0,0) );
		g.clearRect(0,0,w-1,h-1);
		

		testImage(image,true, -1,-1,-1,-1);
		
		map.getRenderManagerInternal().getRenderExecutor().render(g, null);
		showImage("testRenderGraphics2DIProgressMonitor", image); //$NON-NLS-1$

		testImage(image, false, 0,0,512,512);
		
	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope, IProgressMonitor)'
	 */
	@Ignore
    @Test
	public void testRenderEnvelopeIProgressMonitor() throws Exception {
		RenderExecutor ex=map.getRenderManagerInternal().getRenderExecutor();
		BufferedImage image = ex.getContext().getImage(w,h);

        testImage(image,true, -1,-1,-1,-1);
        
		int minx=30;
		int miny=0;
		int maxx=300;
		int maxy=120;
		ex.setRenderBounds(new Envelope(minx, maxx, miny, maxy));
		ex.render();
		
		Platform.getJobManager().join(map.getRenderManager(), new NullProgressMonitor());
		
		showImage( "testRenderEnvelopeIProgressMonitor", image); //$NON-NLS-1$
		testImage(image, false, minx, 512-maxy,maxx,512-miny );
		assertTrue(image==ex.getContext().getImage(w,h));
		
	}



	/**
	 * @param image image to test
	 * @param notSame -1 if all pixels should be non-transparent.  Of the minimum y-value of the non-transparent pixels. 
	 */
	private void testImage(BufferedImage image, boolean clear, int minx, int miny, int maxx, int maxy) {
		for( int x=0; x<512;x++){
			for( int y=0; y<512;y++){
				if( (!clear && x>=minx && x<maxx && y>=miny && y<maxy) )
					assertNotSame("x="+x+", y="+y,0,image.getRaster().getSample(x,y, 3)); //$NON-NLS-1$ //$NON-NLS-2$
				else 
					assertEquals("x="+x+", y="+y,0,image.getRaster().getSample(x,y, 3)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

    /*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope, IProgressMonitor)'
	 */
	@Ignore
    @Test
	public void testRenderRectangle() throws Exception {
		RenderExecutor ex=map.getRenderManagerInternal().getRenderExecutor();
		BufferedImage image = ex.getContext().getImage(w,h);


        testImage(image,true, -1,-1,-1,-1);
        
		
		int minx=30;
		int miny=270;
		int maxx=300;
		int maxy=470;
        ex.setRenderBounds(new Rectangle(minx,miny, maxx-minx, maxy-miny));
		ex.render();
        

		Platform.getJobManager().join(map.getRenderManager(), new NullProgressMonitor());
		
		BufferedImage image2 = ex.getContext().getImage(w,h);
		assertEquals( image, image2 );
		
		showImage( "testRenderRectangle", image ); //$NON-NLS-1$

		testImage(image, false, minx, miny,maxx,maxy);
		assertTrue(image==ex.getContext().getImage());

	}

	
	public static JFrame showImage( String name,final Image i ) throws Exception{
		if( !INTERACTIVE )
			return new JFrame(name);
		
		JFrame frame=new JFrame(name);
		frame.getContentPane().add(new JPanel(){
			private static final long serialVersionUID = 1L;

			public void paint(java.awt.Graphics g) {
				g.clearRect(0,0,i.getWidth(null), i.getHeight(null));
				g.drawImage(i, 0, 0, this);
			};
		});
		frame.setSize(i.getWidth(frame)+10, i.getHeight(frame)+10);
//        frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		synchronized (frame) {
			frame.wait(1000);
		}
		return frame;
	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.dispose()'
	 */
    @Test
	public void testDispose() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.resyncState(Renderer)'
	 */
    @Test
	public void testResyncState() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.setLayerState(IRenderContext, int)'
	 */
    @Test
	public void testSetLayerState() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.stopRendering()'
	 */
    @Test
	public void testStopRendering() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.render()'
	 */
	@Ignore
    @Test
	public void testRender() throws Exception {
		RenderExecutor ex=map.getRenderManagerInternal().getRenderExecutor();
		BufferedImage image = ex.getContext().getImage(w,h);

        testImage(image,true, -1,-1,-1,-1);
        
		
		map.getRenderManagerInternal().getRenderExecutor().render();
		

		Platform.getJobManager().join(map.getRenderManager(), new NullProgressMonitor());

		BufferedImage image2 = ex.getContext().getImage(w,h);
		assertEquals( image, image2 );
		
		showImage("testRender",image); //$NON-NLS-1$
		

		testImage(image, false, 0,0,512,512);
		assertTrue(image==ex.getContext().getImage(w,h));
	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.getContext()'
	 */
    @Test
	public void testGetContext() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.getRenderer()'
	 */
    @Test
	public void testGetRenderer() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.addLayerListener(RenderContext)'
	 */
    @Test
	public void testAddLayerListener() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope)'
	 */
    @Ignore
    @Test
	public void testRenderEnvelope() throws Exception {
		RenderExecutor ex=map.getRenderManagerInternal().getRenderExecutor();
		BufferedImage image = ex.getContext().getImage();


        testImage(image,true, -1,-1,-1,-1);
        
		
		int minx=30;
		int miny=0;
		int maxx=300;
		int maxy=120;
        ex.setRenderBounds(new Envelope(minx, maxx, miny, maxy));
		ex.render();

		Platform.getJobManager().join(map.getRenderManager(), new NullProgressMonitor());
		
		BufferedImage image2 = ex.getContext().getImage();
		assertEquals( image, image2 );
		RenderExecutor executor = ((CompositeRendererImpl) ex.getRenderer()).getRenderExecutors().iterator().next();
        BufferedImage rendererImage=executor.getContext().getImage();
        showImage( "testRenderEnvelope", image ); //$NON-NLS-1$
        
        testImage(image, false, minx, 512-maxy,maxx,512-miny );
		assertTrue(image==ex.getContext().getImage());
        Graphics2D graphics = rendererImage.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0,0,512,512);
        graphics.dispose();
        ex.setRenderBounds(new Envelope(minx, maxx, miny, maxy));
        ex.render();
        Platform.getJobManager().join(map.getRenderManager(), new NullProgressMonitor());

        testImage(image, false, 0, 0, 512, 512);
    }

}
