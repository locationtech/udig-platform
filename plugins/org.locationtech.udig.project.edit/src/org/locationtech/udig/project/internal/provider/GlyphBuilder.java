/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.internal.provider;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetLegendGraphicRequest;
import org.geotools.styling.Style;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.ui.Drawing;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Builds SWT images for to represent layers.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class GlyphBuilder {

    public static ImageDescriptor createImageDescriptor( final RenderedImage image ) {
        return new ImageDescriptor(){
            public ImageData getImageData() {
                return createImageData(image);
            }
        };
    }
    /** Create a buffered image that can be be coverted to SWTland later */
    public static BufferedImage createBufferedImage( int w, int h ) {
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }
    public static Image createSWTImage( RenderedImage image ) {
        // Rectangle size = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        ImageData data = createImageData(image);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    public final static int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

    public static ImageData createImageData( RenderedImage image ) {
        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = TRANSPARENT;

        byte blueT = (byte) ((TRANSPARENT) & 0xFF);
        byte greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
        byte redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
        // System.out.println("red="+redT+"blue"+blueT+"green"+greenT);
        // System.out.println("Transparent"+TRANSPARENT);

        // awtImage2.getRGB();
        Raster raster = image.getData();
        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * 3]);// raster.getNumBands()]);
        int step = swtdata.depth / 8;

        byte[] data = swtdata.data;
        int baseindex = 0;
        // System.out.println( "AWT size:" + awtdata.length );
        for( int y = 0; y < height; y++ ) {
            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

            for( int x = 0; x < width; x++ ) {
                baseindex = (x + (y * width)) * 4;

                if (awtdata[baseindex + 3] == 0) {
                    data[idx++] = blueT;
                    data[idx++] = greenT;
                    data[idx++] = redT;
                } else {
                    data[idx++] = (byte) awtdata[baseindex];
                    data[idx++] = (byte) awtdata[baseindex + 1];
                    data[idx++] = (byte) awtdata[baseindex + 2];
                }
            }
        }
        return swtdata;
    }
    public ImageDescriptor createWMSGylph( Layer target ) {
        if (target.isType(WebMapServer.class))
            return null;
        try {
            WebMapServer wms = target.getResource(WebMapServer.class, null);
            org.geotools.ows.wms.Layer layer = target.getResource(
                    org.geotools.ows.wms.Layer.class, null);

            if (wms.getCapabilities().getRequest().getGetLegendGraphic() != null) {

                GetLegendGraphicRequest request = wms.createGetLegendGraphicRequest();
                request.setLayer(layer.getName());

                String desiredFormat = null;
                List formats = wms.getCapabilities().getRequest()
                        .getGetLegendGraphic().getFormats();
                if (formats.contains("image/png")) { //$NON-NLS-1$
                    desiredFormat = "image/png"; //$NON-NLS-1$
                }
                if (desiredFormat == null && formats.contains("image/gif")) { //$NON-NLS-1$
                    desiredFormat = "image/gif"; //$NON-NLS-1$
                }
                if (desiredFormat == null) {
                    return null;
                }
                request.setFormat(desiredFormat);

                return ImageDescriptor.createFromURL(request.getFinalURL());
            }
        } catch (Exception e) {
            // darn
        }
        return null;
        /*
         * BufferedImage image = createBufferedImage( target, 16, 16); Graphics2D g2 = (Graphics2D)
         * image.getGraphics(); g2.setColor(Color.GREEN); g2.fillRect(1, 1, 14, 14);
         * g2.setColor(Color.BLACK); g2.drawRect(0, 0, 15, 15); return createImageDescriptor(image);
         */
    }
    private SimpleFeature sampleFeature( Layer layer ) {
        FeatureIterator<SimpleFeature> reader = null;
        try {
            reader = layer.getResource(FeatureSource.class, null).getFeatures().features();
        } catch (Throwable ignore) {
            return null;
        }
        try {
            return reader.next();
        } catch (Throwable e) {
            return null;
        } finally {
            reader.close();            
        }
    }

    public Image createGlyph( Layer layer, Style styleObject ) {
        int width = 16;
        int height = 16;

        Image image = new Image(Display.getDefault(), width, height);
        SimpleFeature feature = sampleFeature(layer);
        ViewportGraphics graphics = Drawing.createGraphics(new GC(image), Display.getDefault(),
                new Dimension(width - 1, width - 1));
        graphics.clearRect(0, 0, width, height);
        // graphics.clearRect(0,0,16,16);
        AffineTransform transform = Drawing.worldToScreenTransform(feature.getBounds(),
                new Rectangle(1, 0, width - 1, width - 1));
        // Drawing.createGraphics(image.createGraphics());
        Drawing.create().drawFeature(graphics, feature, transform, styleObject);
        // return createSWTImage(image);
        return image;
        // StyleImpl imp = (StyleImpl) styleObject;
        // FeatureTypeStyle style = imp.getFeatureTypeStyles()[0];
        // Rule rule = style.getRules()[0];
        // Symbolizer symbolizer = rule.getSymbolizers()[0];
        // SimpleFeature feature = sampleFeature( layer );
        //
        // if (symbolizer instanceof LineSymbolizer) {
        // try {
        // LineSymbolizer line = (LineSymbolizer) symbolizer;
        // Stroke stroke = line.getStroke();
        // Color color = stroke.getColor(feature);
        //
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setColor(color);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        // g2.drawLine(0, 2, 11, 11);
        // g2.drawLine(11, 11, 15, 8);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // } catch (Exception e) {
        // BufferedImage image = createBufferedImage(16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setColor(Color.BLACK);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        // g2.drawLine(0, 2, 11, 11);
        // g2.drawLine(11, 11, 15, 8);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // }
        // } else if (symbolizer instanceof PolygonSymbolizer) {
        // try {
        // PolygonSymbolizer poly = (PolygonSymbolizer) symbolizer;
        // Stroke stroke = poly.getStroke();
        // Color color = stroke.getColor(feature);
        // Fill fill = poly.getFill();
        // Paint fillColor = (Paint) fill.getColor().getValue(feature);
        //
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        // g2.setPaint(fillColor);
        // g2.fillArc(4, 4, 24, 24, 90, 90);
        // g2.setPaint(color);
        // g2.drawArc(4, 4, 24, 24, 90, 90);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 16, 16);
        // return createImageDescriptor(image);
        // } catch (Exception e) {
        // BufferedImage image = createBufferedImage( layer, 16, 16);
        // Graphics2D g2 = (Graphics2D) image.getGraphics();
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(2, 2, 11, 11);
        // g2.setColor(Color.BLUE);
        // g2.fillRect(3, 3, 10, 10);
        //
        // g2.setColor(Color.BLACK);
        // g2.drawRect(0, 0, 15, 15);
        // return createImageDescriptor(image);
        // }
        // }
        // return null;
    }
    public Object createGlyph( Layer layer ) {
        try {
            ImageDescriptor glyph;
            if (layer.isType(WebMapServer.class)) {
                glyph = createWMSGylph(layer);
                if (glyph != null)
                    return glyph;
            }
            /*
             * // This so does not work right now if (layer.getStyle() != null) { glyph =
             * createGylph( layer, layer.getStyle() ); if( glyph != null ) return glyph; }
             */
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ProjectEditPlugin.INSTANCE.getImage("full/obj16/Layer"); //$NON-NLS-1$
    }

}
