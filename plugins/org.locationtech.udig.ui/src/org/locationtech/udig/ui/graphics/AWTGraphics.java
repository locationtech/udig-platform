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
package org.locationtech.udig.ui.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;

public class AWTGraphics implements ViewportGraphics {

    public Graphics2D g;

    /**
     * Accept a DPI setting; fonts will be scaled based on this setting. defaults to 72 DPI.
     */
    int dpi;

    public AWTGraphics(Graphics2D g) {
        this.g = g;
        g.setBackground(Color.WHITE);
        dpi = 72;
    }

    /**
     * Construct a AWTGraphics with the indicated DPI
     *
     * @param g
     * @param dpi
     */
    public AWTGraphics(Graphics2D g, int dpi) {
        this.g = g;
        this.dpi = dpi;
        g.setBackground(Color.WHITE);

        if (dpi != 72) {
            Font font = g.getFont();
            String name = font.getName();
            int style = font.getStyle();
            int size = (font.getSize() * dpi) / 72;
            g.setFont(new Font(name, style, size));
        }
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#draw(java.awt.Shape)
     */
    @Override
    public void draw(Shape s) {
        g.draw(s);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#draw(java.awt.Shape)
     */
    @Override
    public void fill(Shape s) {
        g.fill(s);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#setColor(java.awt.Color)
     */
    @Override
    public void setColor(Color c) {
        g.setColor(c);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#setBackground(java.awt.Color)
     */
    @Override
    public void setBackground(Color c) {
        g.setBackground(c);
    }

    /**
     * Make use of the provided font.
     * <p>
     * Please note that the provided AWT Font makes use of a size in *points* (which are documented
     * to be 72 DPI). Internally we adjust this size by the getDPI() value for this AWTGraphics.
     *
     * @param f Font in 72 DPI
     */
    @Override
    public void setFont(Font f) {
        String name = f.getFamily();
        int style = f.getStyle();
        int size = (f.getSize() * dpi) / 72;

        Font font = new Font(name, style, size);
        g.setFont(font);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#setClip(java.awt.Rectangle)
     */
    @Override
    public void setClip(Rectangle r) {
        g.setClip(r);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#fillRect(int, int, int, int)
     */
    @Override
    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#translate(java.awt.Point)
     */
    @Override
    public void translate(Point offset) {
        g.setTransform(AffineTransform.getTranslateInstance(offset.x, offset.y));
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#clearRect(int, int, int, int)
     */
    @Override
    public void clearRect(int x, int y, int width, int height) {
        g.clearRect(x, y, width, height);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#drawImage(javax.media.jai.PlanarImage,
     *      int, int)
     */
    @Override
    public void drawImage(RenderedImage image, int x, int y) {
        g.drawRenderedImage(image, AffineTransform.getTranslateInstance(x, y));
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#drawString(String, int, int)
     */
    @Override
    public void drawString(String string, int x, int y, int alignx, int aligny) {
        Rectangle2D text = g.getFontMetrics().getStringBounds(string, g);
        int w = (int) text.getWidth();
        int h = (int) text.getHeight();

        int x2 = (alignx == 0) ? x - w / 2 : (alignx > 0) ? x - w : x;
        int y2 = (aligny == 0) ? y + h / 2 : (aligny > 0) ? y + h : y;
        g.drawString(string, x2, y2);
    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#setTransform(java.awt.geom.AffineTransform)
     */
    @Override
    public void setTransform(AffineTransform transform) {
        g.setTransform(transform);

    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#drawImage(java.awt.Image, int, int)
     */
    @Override
    public void drawImage(Image image, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    @Override
    public void drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
            int sx2, int sy2) {
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    @Override
    public int getFontHeight() {
        return g.getFontMetrics().getHeight();
    }

    @Override
    public int stringWidth(String str) {
        return g.getFontMetrics().stringWidth(str);
    }

    @Override
    public int getFontAscent() {
        return g.getFontMetrics().getAscent();
    }

    @Override
    public Rectangle2D getStringBounds(String str) {
        return g.getFontMetrics().getStringBounds(str, g);
    }

    /**
     * Converts an SWT image to an AWT BufferedImage
     *
     * @param swtImageData
     * @return
     *
     * @deprecated use {@link AWTSWTImageUtils}
     */
    @Deprecated
    public static BufferedImage toAwtImage(ImageData swtImageData) {
        return AWTSWTImageUtils.convertToAWT(swtImageData);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawImage(org.eclipse.swt.graphics.Image image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2) {
        BufferedImage awtImage = AWTSWTImageUtils.convertToAWT(image.getImageData());
        drawImage(awtImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    @Override
    public AffineTransform getTransform() {
        return g.getTransform();
    }

    @Override
    public void dispose() {
        g.dispose();
    }

    @Override
    public void drawPath(Path path) {
        PathData pathData = path.getPathData();
        float[] points = pathData.points;
        GeneralPath p = new GeneralPath();
        p.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i = i + 2) {
            p.lineTo(points[i], points[i + 1]);
        }
        draw(p);
    }

    @Override
    public void fillPath(Path path) {
        PathData pathData = path.getPathData();
        float[] points = pathData.points;
        GeneralPath p = new GeneralPath();
        p.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i = i + 2) {
            p.lineTo(points[i], points[i + 1]);
        }
        fill(p);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x, y, width, height);
    }

    @Override
    public void drawImage(org.eclipse.swt.graphics.Image swtImage, int x, int y) {
        BufferedImage awtImage = AWTSWTImageUtils.convertToAWT(swtImage.getImageData());
        drawImage((Image) awtImage, x, y);

    }

    @Override
    public Shape getClip() {
        return g.getClip();
    }

    @Override
    public void setClipBounds(Rectangle newBounds) {
        g.setClip(newBounds);
    }

    @Override
    public Color getBackgroundColor() {
        return g.getBackground();
    }

    @Override
    public Color getColor() {
        return g.getColor();
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void setLineDash(int[] dash) {
        Stroke stroke = g.getStroke();
        if (!(stroke instanceof BasicStroke)) {
            stroke = new BasicStroke();
        }
        BasicStroke basicStroke = (BasicStroke) stroke;
        g.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(),
                basicStroke.getLineJoin(), basicStroke.getMiterLimit(), toFloatArray(dash), 0));
    }

    private float[] toFloatArray(int[] dash) {
        float[] result = new float[dash.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = dash[i];
        }
        return result;
    }

    @Override
    public void setLineWidth(int width) {
        Stroke stroke = g.getStroke();
        if (!(stroke instanceof BasicStroke)) {
            stroke = new BasicStroke();
        }
        BasicStroke basicStroke = (BasicStroke) stroke;
        g.setStroke(new BasicStroke(width, basicStroke.getEndCap(), basicStroke.getLineJoin(),
                basicStroke.getMiterLimit(), basicStroke.getDashArray(),
                basicStroke.getDashPhase()));

    }

    /**
     * @see org.locationtech.udig.ui.graphics.ViewportGraphics#setStroke(int, int)
     */
    @Override
    public void setStroke(int style, int width) {
        switch (style) {
        case LINE_DASH: {
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[] { width * 2.0f, width * 2.0f }, 0.0f));
            break;
        }
        case LINE_DASHDOT: {
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[] { width * 2.0f, width * 2.0f, width * 1.0f, width * 2.0f },
                    0.0f));
            break;
        }
        case LINE_DASHDOTDOT: {
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[] { width * 2.0f, width * 2.0f, width * 1.0f, width * 2.0f,
                            width * 1.0f, width * 2.0f },
                    0.0f));
            break;
        }
        case LINE_DOT: {
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[] { width * 1.0f, width * 2.0f }, 0.0f));
            break;
        }
        case LINE_SOLID: {
            g.setStroke(new BasicStroke(width));
            break;
        }

        case LINE_SOLID_ROUNDED: {
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            break;
        }
        }
    }

    @Override
    public int getDPI() {
        return dpi;
    }

    /**
     * Set the viewport graphics to use the provided dpi.
     * <p>
     * Please note the DPI setting is only used to control font size.
     */
    public void setDPI(int dpi) {
        if (this.dpi == dpi)
            return;
        Font font = g.getFont();
        String name = font.getName();
        int style = font.getStyle();
        int size = (font.getSize() * this.getDPI()) / dpi;
        g.setFont(new Font(name, style, size));
        this.dpi = dpi;
    }

    @Override
    public void fillGradientRectangle(int x, int y, int width, int height, Color startColor,
            Color endColor, boolean isVertical) {

        GradientPaint gradPaint = null;
        if (isVertical) {
            gradPaint = new GradientPaint(x, y, startColor, x, y + height, endColor);
        } else {
            gradPaint = new GradientPaint(x, y, startColor, x + width, y, endColor);
        }
        g.setPaint(gradPaint);
        g.fillRect(x, y, width, height);
    }

    @Override
    public <T> T getGraphics(Class<T> adaptee) {
        if (adaptee.isAssignableFrom(Graphics2D.class)) {
            return adaptee.cast(g);
        }
        return null;
    }

}
