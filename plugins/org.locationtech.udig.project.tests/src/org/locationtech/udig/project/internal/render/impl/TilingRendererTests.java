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

import org.locationtech.udig.project.testsupport.AbstractProjectTestCase;

public class TilingRendererTests extends AbstractProjectTestCase {

//	private Map map;
//	private TestTilingRenderer renderer;
//	private IGeoResource resource;
//	private StyleBuilder sb;
//	public static boolean INTERACTIVE=false;
//
//	public TilingRendererTests() throws Exception {
//		GeometryFactory factory=new GeometryFactory();
//		int screenSize=1024, featureSize=16;
//		int step=screenSize/featureSize;
//		Polygon[] polys = new Polygon[step*step];
//		LinearRing[] rings = new LinearRing[step*step];
//		for( int i=0; i<step; i++){
//			int x=i*featureSize;
//			for (int j = 0; j < step; j++) {
//				int y=j*featureSize;
//				int index = i*step+j;
//				rings[index]=factory.createLinearRing(new Coordinate[]{
//						new Coordinate(x,y),
//						new Coordinate(x+featureSize,y),
//						new Coordinate(x+featureSize,y+featureSize),
//						new Coordinate(x,y+featureSize),
//						new Coordinate(x,y),	
//				});
//				polys[index]=factory.createPolygon( rings[index], new LinearRing[]{});
//			}
//		}
//		
//		SimpleFeature[] features = MapTests.createTestFeatures("features", polys, null);//$NON-NLS-1$
//		resource = MapTests.createGeoResource(features, true);
//	}
//	
//	protected void setUp() throws Exception {
//        super.setUp();
//
//		sb = new StyleBuilder();
//		map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512), sb.createStyle(sb.createPolygonSymbolizer(null, sb.createFill(Color.BLACK, 0.5))));
//		map.getViewportModelInternal().setBounds(256,256+512,256,256+512);
//		Renderer child=(Renderer) map.getRenderManagerInternal().getRenderers().get(0);
//		if( child instanceof TilingRenderer)
//			child=((TilingRenderer)child).getChild();
//
//		renderer=new TestTilingRenderer(child);
//		map.getRenderManagerInternal().getRenderExecutor().setRenderer(renderer);
//		child.eSetDeliver(false);
//		if( System.getProperty("java.awt.headless")!=null && System.getProperty("java.awt.headless").equals("true")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			INTERACTIVE=false;
//		RenderExecutorTest.INTERACTIVE=INTERACTIVE;
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.render(Graphics2D, IProgressMonitor)'
//	 */
//	public void testRenderGraphics2DIProgressMonitor() throws Exception {
//		
//		renderer.render(renderer.getContext().getImage().createGraphics(),new NullProgressMonitor());
//		testImage(renderer.getContext().getImage(), 128);
//		RenderExecutorTest.showImage("Graphics2D full render",renderer.getContext().getImage() ); //$NON-NLS-1$
//	}
//
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.render(Envelope, IProgressMonitor)'
//	 */
//	public void testRenderEnvelopeIProgressMonitor() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		testImage(renderer.getContext().getImage(), 128);
//		RenderExecutorTest.showImage("full render",renderer.getContext().getImage() ); //$NON-NLS-1$
//	}
//
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.render(Envelope, IProgressMonitor)'
//	 */
//	public void testRenderNullEnvelope() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		testImage(renderer.getContext().getImage(), 128);
//		RenderExecutorTest.showImage("full render",renderer.getContext().getImage() ); //$NON-NLS-1$
//	}
//
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.render(Envelope, IProgressMonitor)'
//	 */
//	public void testRenderPan() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		map.getViewportModelInternal().panUsingScreenCoords(-256,0);
//		JFrame frame=RenderExecutorTest.showImage("pan render",renderer.getContext().getImage() ); //$NON-NLS-1$
//		Graphics2D g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 256, 0, 512,512);
//
//		map.getViewportModelInternal().panUsingScreenCoords(256,0);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 0, 0, 256,512);
//
//		map.getViewportModelInternal().panUsingScreenCoords(0,256);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 0, 0, 512,256);
//
//		map.getViewportModelInternal().panUsingScreenCoords(0,-256);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 0, 256, 512, 512);
//
//		map.getViewportModelInternal().panUsingScreenCoords(-256,156);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 256, 0, 512,356);
//
//		map.getViewportModelInternal().panUsingScreenCoords(256,-156);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 0, 156, 256,512);
//
//		map.getViewportModelInternal().panUsingScreenCoords(-156,-256);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 156, 256, 512,512);
//
//		map.getViewportModelInternal().panUsingScreenCoords(156,256);
//		g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		frame.getContentPane().repaint();
//		testImage(renderer.getContext().getImage(), 0, 0, 356,256);
//	}
//	
//	/**
//	 * The Rectangle between minx,miny and maxx and maxy mst be red or the assertion fails
//	 * @param image image to test
//	 */
//	private void testImage(BufferedImage image, int minx, int miny, int maxx, int maxy) {
//		for( int x=0; x<512;x++){
//			for( int y=0; y<512;y++){
//				if( (x>=minx && x<maxx && y>=miny && y<maxy) ){
//					assertEquals("x="+x+", y="+y+", band=alpha",255, image.getRaster().getSample(x,y,3));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
//					assertEquals("x="+x+", y="+y+", band=red",255,image.getRaster().getSample(x,y,0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				}else{
//					assertEquals("x="+x+", y="+y+", band=alpha",128, image.getRaster().getSample(x,y,3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//					assertEquals("x="+x+", y="+y+", band=red",0,image.getRaster().getSample(x,y,0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				}
//				assertEquals("x="+x+", y="+y+", band=green",0,image.getRaster().getSample(x,y,1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				assertEquals("x="+x+", y="+y+", band=blue",0,image.getRaster().getSample(x,y,2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			}
//		}
//	}
//
//	public void testPan() throws Exception {
//		map.getViewportModelInternal().setBounds(0,512,0,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//
//		JFrame frame=RenderExecutorTest.showImage("pan render",renderer.getContext().getImage() ); //$NON-NLS-1$		map.getViewportModelInternal().panUsingScreenCoords(-200,0);
//		Graphics2D g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);		
//		map.getViewportModelInternal().panUsingScreenCoords(-200,0);
//		renderer.panImage();
//		assertEquals( new Envelope(0,312,0,512), renderer.getPaintedArea() );
//		renderer.setOld();
//		frame.getContentPane().repaint();
//		renderer.render((Envelope)null,new NullProgressMonitor());
//
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		map.getViewportModelInternal().panUsingScreenCoords(200,0);
//		renderer.panImage();
//		assertEquals( new Envelope(0,312,0,512), renderer.getPaintedArea() );
//		renderer.setOld();
//		frame.getContentPane().repaint();
//		renderer.render((Envelope)null,new NullProgressMonitor());
//
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		map.getViewportModelInternal().panUsingScreenCoords(0,200);
//		renderer.panImage();
//		assertEquals( new Envelope(0,512,0,312), renderer.getPaintedArea() );
//		renderer.setOld();
//		frame.getContentPane().repaint();
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		map.getViewportModelInternal().panUsingScreenCoords(0,-200);
//		renderer.panImage();
//		assertEquals( new Envelope(0,512,0,312), renderer.getPaintedArea() );
//		renderer.setOld();
//		frame.getContentPane().repaint();
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		map.getViewportModelInternal().panUsingScreenCoords(-200,100);
//		renderer.panImage();
//		assertEquals( new Envelope(0,312,0,412), renderer.getPaintedArea() );
//		renderer.setOld();
//		frame.getContentPane().repaint();
//		renderer.render((Envelope)null,new NullProgressMonitor());
//	}
//	
//	public void testStateRenderRequest() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		renderer.setState(IRenderer.RENDER_REQUEST);
//		Graphics2D g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		assertTrue(renderer.getPaintedArea().toString(), renderer.getPaintedArea().isNull());
//		testImage(renderer.getContext().getImage(), 128);
//	}
//	
//	
//	public void testStateNever() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		renderer.setState(IRenderer.NEVER);
//		Graphics2D g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		assertTrue(renderer.getPaintedArea().toString(), renderer.getPaintedArea().isNull());
//		testImage(renderer.getContext().getImage(), 128);
//	}
//	
//	public void testStateRendering() throws Exception {
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		renderer.setState(IRenderer.RENDERING);
//		Graphics2D g=renderer.getContext().getImage().createGraphics();
//		g.setColor(Color.RED);
//		g.fillRect(0,0,512,512);
//		renderer.render((Envelope)null,new NullProgressMonitor());
//		assertTrue(renderer.getPaintedArea().toString(), renderer.getPaintedArea().isNull());
//		testImage(renderer.getContext().getImage(), 128);
//	}
//	
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.dispose()'
//	 */
//	public void testDispose() {
//
//	}
//
//	/*
//	 * Test method for 'org.locationtech.udig.project.internal.render.impl.TilingRenderer.isZoomChanged()'
//	 */
//	public void testIsZoomChanged() {
//
//	}
//
//	/**
//	 * @param image image to test
//	 * @param notSame -1 if all pixels should be non-transparent.  Of the minimum y-value of the non-transparent pixels. 
//	 */
//	private void testImage(BufferedImage image, int alpha) {
//		boolean blank=true;
//		for( int x=0; x<512;x++){
//			for( int y=0; y<512;y++){
//				
//					if(0!=image.getRaster().getSample(x,y, 3))
//						blank=false;
//					assertEquals(alpha, image.getRaster().getSample(x,y,3));
//			}
//		}
//		assertFalse("Image should not be blank", blank); //$NON-NLS-1$
//	}
//	
//	class TestTilingRenderer extends TilingRenderer{
//
//		public TestTilingRenderer(Renderer child) {
//			super(child);
//		}
//		
//		public void setOld() {
//			oldViewport = (ViewportModel) EcoreUtil.copy(getContext().getViewportModelInternal());
//			oldDisplaySize = getContext().getMapDisplay().getDisplaySize();
//		}
//
//		public Envelope getPaintedArea() {
//			return paintedAreaInWorld;
//		}
//		
//		public void setPaintedArea(Envelope area){
//			paintedAreaInWorld=area;
//		}
//		
//		@Override
//		public void render(Envelope bounds, IProgressMonitor monitor) throws RenderException {
//			super.render(bounds, monitor);
//			setState(DONE);
//		}
//		
//		@Override
//		public void panImage() {
//			super.panImage();
//		}
//		
//	}
	
}
