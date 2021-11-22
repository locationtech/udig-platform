/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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

    public static ImageDescriptor createImageDescriptor(final RenderedImage image) {
        return new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return createImageData(image);
            }
        };
    }

    /** Create a buffered image that can be be converted to SWTland later */
    public static BufferedImage createBufferedImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }

    public static Image createSWTImage(RenderedImage image) {
        ImageData data = createImageData(image);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    public static final int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

    public static ImageData createImageData(RenderedImage image) {
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

        Raster raster = image.getData();
        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width * height * 3]);
        int step = swtdata.depth / 8;

        byte[] data = swtdata.data;
        int baseindex = 0;
        for (int y = 0; y < height; y++) {
            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

            for (int x = 0; x < width; x++) {
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

    public ImageDescriptor createWMSGylph(Layer target) {
        if (target.hasResource(WebMapServer.class))
            return null;
        try {
            WebMapServer wms = target.getResource(WebMapServer.class, null);
            org.geotools.ows.wms.Layer layer = target.getResource(org.geotools.ows.wms.Layer.class,
                    null);

            if (wms.getCapabilities().getRequest().getGetLegendGraphic() != null) {

                GetLegendGraphicRequest request = wms.createGetLegendGraphicRequest();
                request.setLayer(layer.getName());

                String desiredFormat = null;
                List formats = wms.getCapabilities().getRequest().getGetLegendGraphic()
                        .getFormats();
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
    }

    private SimpleFeature sampleFeature(Layer layer) {
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

    public Image createGlyph(Layer layer, Style styleObject) {
        int width = 16;
        int height = 16;

        Image image = new Image(Display.getDefault(), width, height);
        SimpleFeature feature = sampleFeature(layer);
        ViewportGraphics graphics = Drawing.createGraphics(new GC(image), Display.getDefault(),
                new Dimension(width - 1, width - 1));
        graphics.clearRect(0, 0, width, height);
        AffineTransform transform = Drawing.worldToScreenTransform(feature.getBounds(),
                new Rectangle(1, 0, width - 1, width - 1));
        Drawing.create().drawFeature(graphics, feature, transform, styleObject);
        return image;
    }

    public Object createGlyph(Layer layer) {
        try {
            ImageDescriptor glyph;
            if (layer.hasResource(WebMapServer.class)) {
                glyph = createWMSGylph(layer);
                if (glyph != null)
                    return glyph;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ProjectEditPlugin.INSTANCE.getImage("full/obj16/Layer"); //$NON-NLS-1$
    }

}
