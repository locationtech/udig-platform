package net.refractions.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;

public class PrimitiveShapePathIteratorTest {

    private static final boolean INTERACTIVE = false;
    private static final long SLEEP = 1000;
    private int height = 500;
    private int width = 500;

    java.awt.Point SCREEN=new java.awt.Point(width,height);
    
    private BufferedImage testImage = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
    private BufferedImage exampleImage = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
    JFrame frame;

    private MathTransform layerToWorld;

    @Before
    public void setUp() throws Exception {
        layerToWorld = CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
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

    @After
    public void tearDown() throws Exception {
        if (frame != null)
            frame.dispose();
    }
    
    private void update() throws Exception {
        if (INTERACTIVE) {
            frame.repaint();
            Thread.sleep(SLEEP);
        }
    }

    @Test
    public void testDrawPoint() throws Exception {
        Graphics2D testG = testImage.createGraphics();
        Graphics2D exampleG = exampleImage.createGraphics();

        exampleG.setBackground(Color.WHITE);
        exampleG.setColor(Color.BLUE);
        exampleG.clearRect(0, 0, width, height);

        testG.setBackground(Color.WHITE);
        testG.setColor(Color.BLUE);
        testG.clearRect(0, 0, width, height);

        EditBlackboard map = new EditBlackboard(SCREEN.x,
                SCREEN.y, AffineTransform.getTranslateInstance(0, 0), layerToWorld);

        EditGeom geom = map.getGeoms().get(0);

        
        GeneralPath path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);

        map.addPoint(10, 10, geom.getShell());
        
        path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);
        
        testG.draw(path);
        exampleG.drawRect(8,8,4,4);
        update();
        assertImagesEqual();
    }
    
    @Test
    public void testDrawLine() throws Exception {
        Graphics2D testG = testImage.createGraphics();
        Graphics2D exampleG = exampleImage.createGraphics();

        exampleG.setBackground(Color.WHITE);
        exampleG.setColor(Color.BLUE);
        exampleG.clearRect(0, 0, width, height);

        testG.setBackground(Color.WHITE);
        testG.setColor(Color.BLUE);
        testG.clearRect(0, 0, width, height);

        EditBlackboard map = new EditBlackboard(SCREEN.x,
                SCREEN.y, AffineTransform.getTranslateInstance(0, 0), layerToWorld);

        EditGeom geom = map.getGeoms().get(0);


        map.addPoint(10, 10, geom.getShell());
        map.addPoint(450, 10, geom.getShell());

        GeneralPath path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);
        testG.draw(path);
        exampleG.drawLine(10, 10, 450, 10);
        update();
        assertImagesEqual();

        map.addPoint(450, 100, geom.getShell());
        path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);
        testG.draw(path);
        exampleG.drawLine(450, 10, 450, 100);
        update();
        assertImagesEqual();

        map.addPoint(50, 100, geom.getShell());
        path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);
        testG.draw(path);
        exampleG.drawLine(450, 100, 50, 100);
        update();
        assertImagesEqual();

        map.addPoint(10, 10, geom.getShell());
        path=new GeneralPath();
        path.append(PrimitiveShapeIterator.getPathIterator(geom.getShell()), false);
        testG.draw(path);
        exampleG.drawLine(50, 100, 10, 10);
        update();
        assertImagesEqual();

        exampleG.dispose();
        testG.dispose();
    }

    @Test
    public void testDrawPolygon() throws Exception {

        Graphics2D testG = testImage.createGraphics();
        Graphics2D exampleG = exampleImage.createGraphics();

        exampleG.setBackground(Color.WHITE);
        exampleG.setColor(Color.BLUE);
        exampleG.clearRect(0, 0, width, height);

        testG.setBackground(Color.WHITE);
        testG.setColor(Color.BLUE);
        testG.clearRect(0, 0, width, height);

        EditBlackboard map = new EditBlackboard(SCREEN.x,
                SCREEN.y, AffineTransform.getTranslateInstance(0, 0), layerToWorld);

        EditGeom geom = map.getGeoms().get(0);

        
        // test get fill single point
        map.addPoint(0, 10, geom.getShell());

        GeneralPath path = new GeneralPath();
        path.moveTo(0, 10);
        map.addPoint(450, 10, geom.getShell());
        map.addPoint(450, 100, geom.getShell());
        path.lineTo(450, 10);
        path.lineTo(450, 100);

        testG.fill(getIterator(geom));
        exampleG.fill(path);
        update();
        assertImagesEqual();

        map.addPoint(50, 100, geom.getShell());
        map.addPoint(0, 10, geom.getShell());

        path.lineTo(50, 100);
        path.closePath();

        testG.fill(getIterator(geom));
        exampleG.fill(path);
        update();
        assertImagesEqual();

        exampleG.dispose();
        testG.dispose();
    }

    /**
     *
     * @param geom
     * @return
     */
    private Shape getIterator( EditGeom geom ) {
        PrimitiveShapeIterator iter = PrimitiveShapeIterator.getPathIterator(geom.getShell());
        iter.setPolygon(true);

        GeneralPath testShape=new GeneralPath();
        testShape.append(iter, false);
        return testShape;
    }

    private void assertImagesEqual() {

        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                int exampleRGB = exampleImage.getRGB(x, y);
                int testRGB = testImage.getRGB(x, y);

                assertEquals(exampleRGB, testRGB);
            }
        }
    }
}
