package net.refractions.udig.ui;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import junit.framework.TestCase;
import net.refractions.udig.ui.graphics.SWTGraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.geotools.styling.Mark;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class DrawingTest extends TestCase {
    Image image;

    @Override
    protected void tearDown() throws Exception {
        image.dispose();
    }

    public void testDrawFeaturePolygon() throws Exception {
        Drawing d = Drawing.create();
        Display display = Display.getCurrent();
        StyleBuilder builder = new StyleBuilder();
        Style rule = builder.createStyle(builder.createPolygonSymbolizer(Color.RED, Color.BLUE, 1));

        image = new Image(display, 16, 16);

        SWTGraphics graphics = new SWTGraphics(image, display);
        graphics.getGC().setAntialias(SWT.OFF);
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, 16, 16);
        d.drawFeature(graphics, d.feature(d.polygon(new int[]{2, 2, 2, 14, 14, 14, 14, 2, 2, 2})),
                new AffineTransform(), rule);
        graphics.dispose();
        int blue = image.getImageData().palette.getPixel(new RGB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue()));
        int red = image.getImageData().palette.getPixel(new RGB(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue()));
        int white = image.getImageData().palette.getPixel(new RGB(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue()));

        for( int i = 0; i < 16; i++ ) //white
            assertEquals("(" + i + "," + 0 + ")", white, image.getImageData().getPixel(i, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for( int i = 2; i < 15; i++ ) { //blue or off-blue
            int pixel = image.getImageData().getPixel(i, 2);
            assertTrue("(" + i + "," + 2 + ") != blue", Math.abs(pixel-blue) < 1000);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        for( int i = 3; i < 14; i++ ) //red
            assertEquals("(" + i + "," + 3 + ")", red, image.getImageData().getPixel(i, 3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    public void testLineWidth() throws Exception {
        Drawing d = Drawing.create();
        Display display = Display.getCurrent();
        GeometryFactory factory = new GeometryFactory();

        LineString line = factory.createLineString(new Coordinate[]{new Coordinate(0, 2),
                new Coordinate(15, 2)});

        StyleBuilder builder = new StyleBuilder();
        Style style = builder.createStyle(builder.createLineSymbolizer(Color.BLUE, 3));

        image = new Image(display, 16, 16);

        SWTGraphics graphics = new SWTGraphics(image, display);
        graphics.getGC().setAntialias(SWT.OFF);
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, 16, 16);
        d.drawFeature(graphics, d.feature(line), style, new AffineTransform());
        graphics.dispose();
        int blue = image.getImageData().palette.getPixel(new RGB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue()));
        int white = image.getImageData().palette.getPixel(new RGB(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue()));

        for( int y = 0; y < 16; y++ ) {
            for( int x = 1; x < 15; x++ ) { //anti-aliasing seems to stay on for windows
                int pixel = image.getImageData().getPixel(x, y);
                if (y > 0 && y < 4) //blue or off-blue
                    assertTrue("(" + x + "," + y + ") != blue", Math.abs(pixel-blue) < 1000);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                else
                    assertTrue(white == pixel);
            }
        }
    }

    public void testPoint() throws Exception {
        Drawing d = Drawing.create();
        Display display = Display.getCurrent();
        GeometryFactory factory = new GeometryFactory();

        Point point = factory.createPoint(new Coordinate(7, 7));

        StyleBuilder builder = new StyleBuilder();
        Mark mark = builder.createMark(StyleBuilder.MARK_SQUARE);
        mark.setStroke(builder.createStroke(Color.BLUE));
        mark.setFill(builder.createFill(Color.BLUE));
        mark.setSize(builder.getFilterFactory().literal(5));
        Style style = builder.createStyle(builder.createPointSymbolizer(builder.createGraphic(null,
                mark, null)));

        image = new Image(display, 16, 16);

        SWTGraphics graphics = new SWTGraphics(image, display);
        graphics.getGC().setAntialias(SWT.OFF);
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, 16, 16);
        d.drawFeature(graphics, d.feature(point), style, new AffineTransform());
        graphics.dispose();
        int blue = image.getImageData().palette.getPixel(new RGB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue()));
        int white = image.getImageData().palette.getPixel(new RGB(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue()));

        for( int y = 0; y < 16; y++ ) {
            for( int x = 0; x < 16; x++ ) {
                int pixel = image.getImageData().getPixel(x, y);
                if (x > 3 && x < 11 && y > 3 && y < 11)
                    assertTrue("(" + x + "," + y + ") != blue", Math.abs(pixel-blue) < 1000);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                else
                    assertEquals("(" + x + "," + y + ")", white, pixel); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }
        }


    }
}
