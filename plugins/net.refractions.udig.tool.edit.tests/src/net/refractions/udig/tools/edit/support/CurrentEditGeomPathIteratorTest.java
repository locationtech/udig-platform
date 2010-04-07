package net.refractions.udig.tools.edit.support;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.framework.TestCase;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.MathTransform;

public class CurrentEditGeomPathIteratorTest extends TestCase {

    private static final boolean INTERACTIVE = false;
    private static final long SLEEP = 1000;
    private int height = 500;
    private int width = 500;

    java.awt.Point SCREEN=new java.awt.Point(width,height);
    
    private BufferedImage testImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    private BufferedImage exampleImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    JFrame frame;

    private MathTransform layerToWorld;
    
    @Override
    protected void setUp() throws Exception {
        layerToWorld=CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);    
    if (INTERACTIVE) {
            frame = new JFrame("EditGeomPathIterator Test"); //$NON-NLS-1$
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new GridLayout(1,2));
            frame.getContentPane().add(new JPanel(){
                /** long serialVersionUID field */
                private static final long serialVersionUID = 1L;

                @Override
                public void paint( Graphics g ) {
                  g.drawImage(testImage, 0, 0, this);
                  g.drawRect(0,0,width, height);
                }
            });
            
            frame.getContentPane().add(new JPanel(){
                /** long serialVersionUID field */
                private static final long serialVersionUID = 1L;

                @Override
                public void paint( Graphics g ) {
                  g.drawImage(exampleImage, 0, 0, this);
                  g.drawRect(0,0,width, height);
                }
            });
            
            
            
            frame.setSize(width*2+20, height+50);
            
            frame.setVisible(true);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        if( frame != null )
            frame.dispose();
        super.tearDown();
    }
    private void update() throws Exception{
        if( INTERACTIVE ){
            frame.repaint();
            Thread.sleep(SLEEP);
        }
    }

    public void testDrawPolygon() throws Exception {
        
        Graphics2D testG = testImage.createGraphics();
        Graphics2D exampleG = exampleImage.createGraphics();
        
        exampleG.setBackground(Color.WHITE);
        exampleG.setColor(Color.BLUE);
        exampleG.clearRect(0,0,width,height);

        testG.setBackground(Color.WHITE);
        testG.setColor(Color.BLUE);
        testG.clearRect(0,0,width,height);

        EditBlackboard map = new EditBlackboard(SCREEN.x, SCREEN.y, AffineTransform.getTranslateInstance(
                        0, 0), layerToWorld);

        EditGeom geom = map.getGeoms().get(0);
        
        CurrentEditGeomPathIterator iter=CurrentEditGeomPathIterator.getPathIterator(geom);
        iter.setPolygon(true);
        iter.setLocation(Point.valueOf(10,10), geom.getShell());
        iter.toShape();
        
        
        //test get fill single point
        map.addPoint(10, 10, geom.getShell());
        iter.setLocation(Point.valueOf(450, 10), geom.getShell());

        GeneralPath path=new GeneralPath();
        path.moveTo(10,10);
        path.lineTo(450,10);
        

        testG.setColor(Color.BLUE);
        testG.fill(iter.toShape());
        testG.setColor(Color.RED);        
        testG.draw(iter.toShape());
        exampleG.setColor(Color.BLUE);
        exampleG.fill(path);
        exampleG.setColor(Color.RED);
        exampleG.draw(path);
        update();
        assertImagesEqual();

        map.addPoint(450, 10, geom.getShell());
        iter.setLocation(Point.valueOf(450, 100), geom.getShell());
        
        path.lineTo(450,100);

        testG.setColor(Color.BLUE);
        testG.fill(iter.toShape());
        testG.setColor(Color.RED);        
        testG.draw(iter.toShape());
        exampleG.setColor(Color.BLUE);
        exampleG.fill(path);
        exampleG.setColor(Color.RED);
        exampleG.draw(path);
        update();
        assertImagesEqual();
        
        iter.setLocation(Point.valueOf(50, 100), geom.getShell());

        map.addPoint(450, 100,geom.getShell() );
        map.addPoint(50, 100, geom.getShell());
        map.addPoint(10, 10, geom.getShell());
        

        path.lineTo(50,100);
        path.closePath();

        testG.setColor(Color.BLUE);
        testG.fill(iter.toShape());
        testG.setColor(Color.RED);        
        testG.draw(iter.toShape());
        exampleG.setColor(Color.BLUE);
        exampleG.fill(path);
        exampleG.setColor(Color.RED);
        exampleG.draw(path);
        update();
        assertImagesEqual();


        // if current shape is null then location should be ignored.
        iter.setLocation(Point.valueOf(450, 100), null);

        testG.setColor(Color.BLUE);
        testG.fill(iter.toShape());
        testG.setColor(Color.RED);        
        testG.draw(iter.toShape());
        exampleG.setColor(Color.BLUE);
        exampleG.fill(path);
        exampleG.setColor(Color.RED);
        exampleG.draw(path);
        update();
        assertImagesEqual();
        
        PrimitiveShape hole=geom.newHole();
        iter.toShape();
        map.addPoint(200,20,hole);
        map.addPoint(400,20,hole);
        map.addPoint(400,50,hole);
        map.addPoint(400,70,hole);
        map.addPoint(200,20,hole);
        
        hole=geom.newHole();
        map.addPoint(20,20,hole);
        map.addPoint(40,20,hole);
        map.addPoint(40,50,hole);
        map.addPoint(40,70,hole);
        map.addPoint(20,20,hole);
        
        path=new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        
        path.moveTo(10,10);
        path.lineTo(450,10);
        path.lineTo(450,100);
        path.lineTo(50,100);
        path.lineTo(10,10);
        
        path.moveTo(200,20);
        path.lineTo(400,20);
        path.lineTo(400,50);
        path.lineTo(400,70);
        path.lineTo(200,20);

        path.moveTo(20,20);
        path.lineTo(40,20);
        path.lineTo(40,50);
        path.lineTo(40,70);
        path.closePath();
        
        testG.clearRect(0,0,width,height);
        exampleG.clearRect(0,0,width,height);
        testG.setColor(Color.BLUE);
        testG.fill(iter.toShape());
        testG.setColor(Color.RED);        
        testG.draw(iter.toShape());
        exampleG.setColor(Color.BLUE);
        exampleG.fill(path);
        exampleG.setColor(Color.RED);
        exampleG.draw(path);
        update();
        assertImagesEqual();
        
        exampleG.dispose();
        testG.dispose();
    }
    
    
    private void assertImagesEqual() {
        
        for( int x=0; x<width; x++ ){
            for( int y=0; y<height; y++ ){
                int exampleRGB=exampleImage.getRGB(x,y);
                int testRGB=testImage.getRGB(x,y);
                
                assertEquals(exampleRGB, testRGB);
            }            
        }
    }
}
