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
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
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

public class EditGeomPathIteratorTest {

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

        EditGeomPathIterator iter = EditGeomPathIterator.getPathIterator(geom);
        iter.toShape();

        map.addPoint(10, 10, geom.getShell());

        testG.draw(iter.toShape());
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

        EditGeomPathIterator iter = EditGeomPathIterator.getPathIterator(geom);
        iter.toShape();

        map.addPoint(10, 10, geom.getShell());
        map.addPoint(450, 10, geom.getShell());

        testG.draw(iter.toShape());
        exampleG.drawLine(10, 10, 450, 10);
        update();
        assertImagesEqual();

        map.addPoint(450, 100, geom.getShell());
        testG.draw(iter.toShape());
        exampleG.drawLine(450, 10, 450, 100);
        update();
        assertImagesEqual();

        map.addPoint(50, 100, geom.getShell());
        testG.draw(iter.toShape());
        exampleG.drawLine(450, 100, 50, 100);
        update();
        assertImagesEqual();

        map.addPoint(10, 10, geom.getShell());
        testG.draw(iter.toShape());
        exampleG.drawLine(50, 100, 10, 10);
        update();
        assertImagesEqual();

        PrimitiveShape hole = geom.newHole();
        iter.toShape();
        map.addPoint(100, 20, hole);
        map.addPoint(400, 20, hole);

        testG.draw(iter.toShape());
        exampleG.drawLine(100, 20, 400, 20);
        update();
        assertImagesEqual();

        map.addPoint(400, 50, hole);
        map.addPoint(400, 70, hole);
        map.addPoint(300, 70, hole);
        map.addPoint(200, 70, hole);
        map.addPoint(100, 70, hole);
        map.addPoint(100, 20, hole);

        testG.draw(iter.toShape());
        exampleG.drawLine(400, 20, 400, 50);
        exampleG.drawLine(400, 50, 400, 70);
        exampleG.drawLine(400, 70, 300, 70);
        exampleG.drawLine(300, 70, 200, 70);
        exampleG.drawLine(200, 70, 100, 70);
        exampleG.drawLine(100, 70, 100, 20);
        update();
        assertImagesEqual();

        hole = geom.newHole();
        map.addPoint(20, 20, hole);
        map.addPoint(40, 20, hole);
        map.addPoint(40, 50, hole);
        map.addPoint(40, 70, hole);
        map.addPoint(20, 20, hole);

        testG.draw(iter.toShape());
        exampleG.drawLine(20, 20, 40, 20);
        exampleG.drawLine(40, 20, 40, 50);
        exampleG.drawLine(40, 50, 40, 70);
        exampleG.drawLine(40, 70, 20, 20);
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
        exampleG.clearRect(0, 0, width, height);

        testG.setBackground(Color.WHITE);
        testG.clearRect(0, 0, width, height);

        EditBlackboard map = new EditBlackboard(SCREEN.x,
                SCREEN.y, AffineTransform.getTranslateInstance(0, 0), layerToWorld);

        EditGeom geom = map.getGeoms().get(0);

        EditGeomPathIterator iter = EditGeomPathIterator.getPathIterator(geom);
        iter.setPolygon(true);
        iter.toShape();

        // test get fill single point
        map.addPoint(0, 10, geom.getShell());

        GeneralPath path = new GeneralPath();
        path.moveTo(0, 10);
        map.addPoint(450, 10, geom.getShell());
        map.addPoint(450, 100, geom.getShell());
        path.lineTo(450, 10);
        path.lineTo(450, 100);

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

        map.addPoint(50, 100, geom.getShell());
        map.addPoint(0, 10, geom.getShell());

        path.lineTo(50, 100);
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

        PrimitiveShape hole = geom.newHole();
        iter.toShape();
        map.addPoint(200, 20, hole);
        map.addPoint(400, 20, hole);
        map.addPoint(400, 50, hole);
        map.addPoint(400, 70, hole);
        map.addPoint(200, 20, hole);

        hole = geom.newHole();
        map.addPoint(20, 20, hole);
        map.addPoint(40, 20, hole);
        map.addPoint(40, 50, hole);
        map.addPoint(40, 70, hole);
        map.addPoint(20, 20, hole);

        path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        path.moveTo(0, 10);
        path.lineTo(450, 10);
        path.lineTo(450, 100);
        path.lineTo(50, 100);
        path.lineTo(0, 10);

        path.moveTo(200, 20);
        path.lineTo(400, 20);
        path.lineTo(400, 50);
        path.lineTo(400, 70);
        path.lineTo(200, 20);

        path.moveTo(20, 20);
        path.lineTo(40, 20);
        path.lineTo(40, 50);
        path.lineTo(40, 70);
        path.closePath();

        testG.clearRect(0, 0, width, height);
        exampleG.clearRect(0, 0, width, height);
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

        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                int exampleRGB = exampleImage.getRGB(x, y);
                int testRGB = testImage.getRGB(x, y);

                assertEquals(exampleRGB, testRGB);
            }
        }
    }
}
