package net.refractions.udig.project.internal.render.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class RenderExecutorTest extends AbstractProjectTestCase {

	private Map map;
	
	int w=512,h=512;

	public static boolean INTERACTIVE=true;

	@Override
	protected void setUp() throws Exception {
        super.setUp();

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
	
	@Override
	protected void tearDown() throws Exception {
		map.getRenderManagerInternal().dispose();
        super.tearDown();
	}
	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.setState(int)'
	 */
	public void testSetState() {
		
	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.render(Graphics2D, IProgressMonitor)'
	 */
	public void xtestRenderGraphics2DIProgressMonitor() throws Exception {
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
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope, IProgressMonitor)'
	 */
	public void xtestRenderEnvelopeIProgressMonitor() throws Exception {
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
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope, IProgressMonitor)'
	 */
	public void xtestRenderRectangle() throws Exception {
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
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.dispose()'
	 */
	public void testDispose() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.resyncState(Renderer)'
	 */
	public void testResyncState() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.setLayerState(IRenderContext, int)'
	 */
	public void testSetLayerState() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.stopRendering()'
	 */
	public void testStopRendering() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.render()'
	 */
	public void xtestRender() throws Exception {
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
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.getContext()'
	 */
	public void testGetContext() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.getRenderer()'
	 */
	public void testGetRenderer() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.addLayerListener(RenderContext)'
	 */
	public void testAddLayerListener() {

	}

	/*
	 * Test method for 'net.refractions.udig.project.internal.render.impl.RenderExecutorImpl.render(Envelope)'
	 */
	public void xtestRenderEnvelope() throws Exception {
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
