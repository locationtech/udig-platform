/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.graphics;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * A Graphics object that wraps SWT's GC object
 *
 * @author jeichar
 * @since 0.3
 */
public class NonAdvancedSWTGraphics implements ViewportGraphics {
    /** The <code>TRANSPARENT</code> color */
    public final static int TRANSPARENT = 0x220000 | 0x2200 | 0x22;

    private AffineTransform transform = new AffineTransform();

    private GC gc = null;

    private double[] current = new double[6];

    private double[] last = new double[6];

    private double[] move_to = new double[6];

    private Color fore = null;

    private Color back = null;

    private Display display;

    private Font font;


    /**
     * Construct <code>SWTGraphics</code>.
     *
     * @param Image
     *            image
     * @param display
     *            The display object
     */
    public NonAdvancedSWTGraphics(Image image, Display display) {
        this(new GC(image), display, new Dimension(image.getImageData().width,
                image.getImageData().height));
    }

    /**
     * Construct <code>SWTGraphics</code>.
     *
     * @param gc
     *            The GC object
     * @param display
     *            The display object
     */
    public NonAdvancedSWTGraphics(GC gc, Display display, Dimension displaySize) {
        setGraphics(gc, display);
    }

    void setGraphics(GC gc, Display display) {
        this.gc = gc;
        this.display=display;
        if (back != null)
            back.dispose();
        back = new Color(display, 255, 255, 255);
        gc.setBackground(back);
        gc.setAdvanced(false);
    }

    public void dispose() {
        if (fore != null)
            fore.dispose();
        if (back != null)
            back.dispose();
        gc.dispose();
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void draw(Shape s) {
        PathIterator p = s.getPathIterator(transform, 1);
        boolean singlePoint = true;
        if( p.isDone() )
            return;

        p.currentSegment(current);
        move_to = current.clone();
        p.next();
        while (!p.isDone()) {
            singlePoint = false;
            last = current.clone();
            switch (p.currentSegment(current)) {
            case PathIterator.SEG_CLOSE:
                gc.drawLine((int) current[0], (int) current[1],
                        (int) move_to[0], (int) move_to[1]);
                break;
            case PathIterator.SEG_LINETO:
                gc.drawLine((int) last[0], (int) last[1], (int) current[0],
                        (int) current[1]);
                break;
            case PathIterator.SEG_MOVETO:
                move_to = current;
                break;
            case PathIterator.SEG_QUADTO:
            case PathIterator.SEG_CUBICTO:
            default:
            }
            p.next();
        }
        if (singlePoint == true) {
            gc.drawPoint((int) current[0], (int) current[1]);
        }
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#draw(java.awt.Shape)
     */
    public void fill(Shape s) {
        gc.setBackground(fore);
        PathIterator p = s.getPathIterator(transform);
        ArrayList<Integer> pts = new ArrayList<Integer>();

        p.currentSegment(current);
        pts.add((int)current[0]);
        pts.add((int)current[1]);


        p.next();
        while (!p.isDone()) {
            switch (p.currentSegment(current)) {

            case PathIterator.SEG_LINETO:
                p.currentSegment(current);

                pts.add((int)current[0]); // "line_to"
                pts.add((int)current[1]); // "line_to"
                break;

            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_CLOSE: {
                pts.add((int)current[0]);
                pts.add((int)current[1]);
                final int SIZE = pts.size();
                int polygon[] = new int[SIZE];
                for (int i = 0; i < SIZE; i++)
                    polygon[i] = pts.get(i);
                gc.fillPolygon(polygon);

                pts.clear(); // closed we can start again now
            }
                break;

            case PathIterator.SEG_QUADTO:
            case PathIterator.SEG_CUBICTO:
            default:

            }
            p.next();
        }
        gc.setBackground(fore);
    }

    /**
     * Sets an affine transformation for drawing shapes.
     *
     * @param t
     *            The transform.
     */
    public void setAffineTransform(AffineTransform t) {
        this.transform = t;
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#fillRect(int,
     *      int, int, int)
     */
    public void fillRect(int x, int y, int width, int height) {
        int x2 = x+(int)transform.getTranslateX();
        int y2 = y+(int)transform.getTranslateY();
        gc.setBackground(fore);
        gc.fillRectangle(new Rectangle(x2, y2, width, height));
        gc.setBackground(back);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#setColor(java.awt.Color)
     */
    public void setColor(java.awt.Color c) {
        Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
        gc.setForeground(color);
        if (fore != null)
            fore.dispose();
        fore = color;
    }

    /**
     * This is hard because - background doesn't mean what we think it means.
     *
     * @see net.refractions.udig.project.render.ViewportGraphics#setBackground(java.awt.Color)
     */
    public void setBackground(java.awt.Color c) {
        Color color = new Color(display, c.getRed(), c.getGreen(), c.getBlue());
        gc.setBackground(color);
        if (back != null)
            back.dispose();
        back = color;
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#setStroke(int,
     *      int)
     */
    public void setStroke(int style, int width) {
        switch (style) {
        case LINE_DASH: {
            gc.setLineStyle(SWT.LINE_DASH);
            break;
        }
        case LINE_DASHDOT: {
            gc.setLineStyle(SWT.LINE_DASHDOT);
            break;
        }
        case LINE_DASHDOTDOT: {
            gc.setLineStyle(SWT.LINE_DASHDOTDOT);
            break;
        }
        case LINE_DOT: {
            gc.setLineStyle(SWT.LINE_DOT);
            break;
        }
        case LINE_SOLID: {
            gc.setLineStyle(SWT.LINE_SOLID);
            break;
        }

        case LINE_SOLID_ROUNDED: {
            gc.setLineCap(SWT.CAP_ROUND);
            gc.setLineJoin(SWT.JOIN_ROUND);
            gc.setLineStyle(SWT.LINE_SOLID);
            break;
        }
        }
        gc.setLineWidth(width);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#setClip(java.awt.Rectangle)
     */
    public void setClip(java.awt.Rectangle r) {
        gc.setClipping(r.x, r.y, r.width, r.height);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#translate(java.awt.Point)
     */
    public void translate(Point offset) {
        transform.translate(offset.x, offset.y);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#clearRect(int,
     *      int, int, int)
     */
    public void clearRect(int x, int y, int width, int height) {
        Color c = gc.getForeground();
        gc.setForeground(gc.getBackground());
        gc.fillRectangle(x, y, width, height);
        gc.setForeground(c);
    }

    /**
     * @see net.refractions.udig.project.render.ViewportGraphics#drawImage(javax.media.jai.PlanarImage,
     *      int, int)
     */
    public void drawImage(RenderedImage rimage, int x, int y) {
        drawImage(rimage, 0, 0, rimage.getWidth(), rimage.getHeight(), x, y, x
                + rimage.getWidth(), y + rimage.getHeight());
    }

    public static Image createDefaultImage(Display display, int width,
            int height) {
        ImageData swtdata = null;
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        swtdata.transparentPixel = -1;
        swtdata.alpha = -1;
        swtdata.alphaData = new byte[swtdata.data.length];
        for (int i = 0; i < swtdata.alphaData.length; i++) {
            swtdata.alphaData[i] = (byte) i;
        }
        return new Image(display, swtdata);

    }


    public static ImageDescriptor createImageDescriptor(
            final RenderedImage image, final boolean transparent) {
        return new ImageDescriptor() {
            public ImageData getImageData() {
                return createImageData(image, transparent);
            }
        };
    }

    /** Create a buffered image that can be be coverted to SWTland later */
    public static BufferedImage createBufferedImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    }

    public static Image createSWTImage(RenderedImage image, boolean transparent) {
        ImageData data = createImageData(image, transparent);

        return new org.eclipse.swt.graphics.Image(Display.getDefault(), data);
    }

    // optimized version that works if the image is rgb with a byte data buffer
    public static ImageData createImageDataFromBytes(RenderedImage image) {
        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;
        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);

        Raster raster = image.getData();
        raster.getDataElements(0, 0, width, height, swtdata.data);

        return swtdata;
    }

    public static ImageData createImageData(RenderedImage image,
            boolean transparent) {

        // if(
        // image.getData().getDataBuffer().getDataType()==DataBuffer.TYPE_BYTE
        // && !image.getColorModel().hasAlpha() )
        // return createImageDataFromBytes(image);
        ImageData swtdata = null;
        int width = image.getWidth();
        int height = image.getHeight();
        PaletteData palette;
        int depth;

        depth = 24;
        palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
        swtdata = new ImageData(width, height, depth, palette);
        byte blueT = (byte) 255;
        byte greenT = (byte) 255;
        byte redT = (byte) 255;
        if (transparent) {
            swtdata.transparentPixel = TRANSPARENT;

            blueT = (byte) ((TRANSPARENT) & 0xFF);
            greenT = (byte) ((TRANSPARENT >> 8) & 0xFF);
            redT = (byte) ((TRANSPARENT >> 16) & 0xFF);
        }
        Raster raster = image.getData();
        int numbands = raster.getNumBands();
        int[] awtdata = raster.getPixels(0, 0, width, height, new int[width
                * height * numbands]);
        int step = swtdata.depth / 8;

        byte[] data = swtdata.data;
        int baseindex = 0;
        for (int y = 0; y < height; y++) {
            int idx = ((0 + y) * swtdata.bytesPerLine) + (0 * step);

            for (int x = 0; x < width; x++) {
                baseindex = (x + (y * width)) * numbands;

                if (numbands == 4 && awtdata[baseindex + 3] == 0) {
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

    public void drawString(String string, int x, int y, int alignx, int aligny) {
        org.eclipse.swt.graphics.Point text = gc.stringExtent(string);
        int w = (int)text.x;
        int h = (int)text.y;

        int x2 = (alignx == 0) ? x - w/2 : (alignx > 0) ? x - w : x;
        int y2 = (aligny == 0) ? y + h/2 : (aligny > 0) ? y + h : y;

        gc.drawString(string, x2, y2,true);
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    /**
     * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
     *      int, int)
     *
     * Current version can only draw Image if the image is an RenderedImage
     */
    public void drawImage(java.awt.Image image, int x, int y) {
        RenderedImage rimage = (RenderedImage) image;
        drawImage(rimage, x, y);
    }

    /**
     * @see net.refractions.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image,
     *      int, int, int, int, int, int, int, int)
     */
    public void drawImage(java.awt.Image image, int dx1, int dy1, int dx2,
            int dy2, int sx1, int sy1, int sx2, int sy2) {
        RenderedImage rimage = (RenderedImage) image;
        drawImage(rimage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    public void drawImage(RenderedImage rimage, int dx1, int dy1, int dx2,
            int dy2, int sx1, int sy1, int sx2, int sy2) {
        assert rimage != null;
        Image swtImage = null;
        try {
            swtImage = createSWTImage(rimage, true);
            int translatedX = (int) (dx1 + transform.getTranslateX());
            int translatedY = (int) (dy1 + transform.getTranslateY());
            int translatedWidth = (int) (transform.getScaleX() * (dx2 - dx1));
            int translatedHeight = (int) (transform.getScaleY() * (dy2 - dy1));
            if (swtImage != null) {
                gc.drawImage(swtImage, sx1, sy1, sx2 - sx1, sy2 - sy1,
                        translatedX, translatedY, translatedWidth,
                        translatedHeight);
                swtImage.dispose();
            }
        } finally {
            if (swtImage != null)
                swtImage.dispose();
        }

    }

    public int getFontHeight() {
        return gc.getFontMetrics().getHeight();
    }

    public int stringWidth(String str) {
        return -1;
    }

    public int getFontAscent() {
        return gc.getFontMetrics().getAscent();
    }

    public Rectangle2D getStringBounds(String str) {
        org.eclipse.swt.graphics.Point extent = gc.textExtent(str);

        return new java.awt.Rectangle(0,0,extent.x, extent.y);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        gc.drawLine(x1, y1, x2, y2);
    }

    public void drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2) {
        int translatedX = (int) (dx1 + transform.getTranslateX());
        int translatedY = (int) (dy1 + transform.getTranslateY());
        int translatedWidth = (int) (transform.getScaleX() * (dx2 - dx1));
        int translatedHeight = (int) (transform.getScaleY() * (dy2 - dy1));
        gc.drawImage(image, sx1, sy1, sx2 - sx1, sy2 - sy1, translatedX,
                translatedY, translatedWidth, translatedHeight);

    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void drawPath( Path path ) {
        gc.drawPath(path);
    }

    public void fillPath( Path path ) {
    }

    public void drawRect( int x, int y, int width, int height ) {
        gc.drawRectangle(new Rectangle(x,y,width, height));
    }

    public void drawOval( int x, int y, int width, int height ){
        gc.drawOval(x,y,width, height);
    }

    public void fillOval( int x, int y, int width, int height ) {
        gc.fillOval(x, y, width, height);
    }

    public void drawImage( Image swtImage, int x, int y ) {
        gc.drawImage(swtImage, x, y);
    }

    public Shape getClip() {
        Rectangle clipping = gc.getClipping();
        return new java.awt.Rectangle(clipping.x, clipping.y, clipping.width, clipping.height);
    }

    public void setClipBounds( java.awt.Rectangle newBounds ) {
        gc.setClipping(new Rectangle(newBounds.x, newBounds.y, newBounds.width, newBounds.height));
    }

	public java.awt.Color getBackgroundColor() {
		return SWTGraphics.swt2awt(gc, gc.getBackground());
	}

	public java.awt.Color getColor() {
		return SWTGraphics.swt2awt(gc, gc.getForeground());
	}

    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        gc.setBackground(fore);
        gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
        gc.setBackground(back);
    }

    public void setLineDash( int[] dash ) {
        gc.setLineDash(dash);
    }

    public void setLineWidth( int width ) {
        gc.setLineWidth(width);
    }

    public void setFont(java.awt.Font f){
        Font swtFont;

        int size = f.getSize() * getDPI() / 72;
        int style = SWTGraphics.toFontStyle( f );

        swtFont = new Font( gc.getDevice(),f.getFamily(), size, style );
        if (font != null){
            font.dispose();
        }
        font = swtFont;
        gc.setFont(font);
    }
    public int getDPI() {
        return gc.getDevice().getDPI().y;
    }

    public void fillGradientRectangle( int x, int y, int width, int height,
            java.awt.Color startColor, java.awt.Color endColor, boolean isVertical ) {
        Color color1 = new Color(display, startColor.getRed(), startColor.getGreen(), startColor
                .getBlue());
        Color color2 = new Color(display, endColor.getRed(), endColor.getGreen(), endColor
                .getBlue());
        gc.setForeground(color1);
        gc.setBackground(color2);

        gc.fillGradientRectangle(x, y, width, height, isVertical);
        color1.dispose();
        color2.dispose();
    }
}
