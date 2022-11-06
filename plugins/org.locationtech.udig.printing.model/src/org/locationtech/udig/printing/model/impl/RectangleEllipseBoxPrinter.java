/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.model.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.printing.model.AbstractBoxPrinter;
import org.locationtech.udig.printing.model.Page;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RectangleEllipseBoxPrinter extends AbstractBoxPrinter {

    private static final String LINEWIDTH_KEY = "linewidth"; //$NON-NLS-1$

    private static final String LINECOLOR_KEY = "linecolor"; //$NON-NLS-1$

    private static final String LINEALPHA_KEY = "linealpha"; //$NON-NLS-1$

    private static final String FILLCOLOR_KEY = "fillcolor"; //$NON-NLS-1$

    private static final String FILLALPHA_KEY = "fillalpha"; //$NON-NLS-1$

    private static final String SHAPETYPE_KEY = "shapetype"; //$NON-NLS-1$

    private static final String SCALEFACTOR_KEY = "scalefactor"; //$NON-NLS-1$

    public static final int RECTANGLE = 0;

    public static final int ROUNDEDRECTANGLE = 1;

    public static final int ELLIPSE = 2;

    private Color lineColor = Color.GRAY;

    private Color fillColor = Color.GRAY;

    private float lineWidth = 1f;

    private int lineAlpha = 255;

    private int fillAlpha = 128;

    private int type = RECTANGLE;

    private float scaleFactor = Float.NaN;

    public RectangleEllipseBoxPrinter() {
        super();
    }

    public RectangleEllipseBoxPrinter(float scaleFactor) {
        super();
        this.scaleFactor = scaleFactor;
    }

    private float getScaleFactor() {
        if (Float.isNaN(scaleFactor)) {
            // try to get it from the page
            Page page = getBox().getPage();
            if (page != null) {
                scaleFactor = (float) page.getSize().width / (float) page.getPaperSize().height;
            }
        }
        return scaleFactor;
    }

    private void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public void draw(Graphics2D graphics, IProgressMonitor monitor) {
        super.draw(graphics, monitor);

        int boxWidth = getBox().getSize().width - (int) lineWidth / 2;
        int boxHeight = getBox().getSize().height - (int) lineWidth / 2;
        int roundedEgde = 50;

        Shape shape = null;
        if (type == ROUNDEDRECTANGLE) {
            shape = new RoundRectangle2D.Double(0, 0, boxWidth, boxHeight, roundedEgde,
                    roundedEgde);
        } else if (type == ELLIPSE) {
            shape = new Ellipse2D.Double(0, 0, boxWidth, boxHeight);
        } else {
            shape = new Rectangle2D.Double(0, 0, boxWidth, boxHeight);
        }

        graphics.setPaint(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(),
                fillAlpha));
        graphics.fill(shape);
        BasicStroke stroke = new BasicStroke(lineWidth);
        graphics.setStroke(stroke);
        graphics.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                lineAlpha));
        graphics.draw(shape);

    }

    @Override
    public void createPreview(Graphics2D graphics, IProgressMonitor monitor) {
        draw(graphics, monitor);
        setDirty(false);
    }

    @Override
    public void save(IMemento memento) {
        memento.putFloat(LINEWIDTH_KEY, lineWidth);
        memento.putString(LINECOLOR_KEY, color2String(lineColor));
        memento.putInteger(LINEALPHA_KEY, lineAlpha);
        memento.putString(FILLCOLOR_KEY, color2String(fillColor));
        memento.putInteger(FILLALPHA_KEY, fillAlpha);
        memento.putInteger(SHAPETYPE_KEY, type);
        memento.putFloat(SCALEFACTOR_KEY, getScaleFactor());
    }

    @Override
    public void load(IMemento memento) {
        lineWidth = memento.getFloat(LINEWIDTH_KEY);
        lineColor = string2Color(memento.getString(LINECOLOR_KEY));
        lineAlpha = memento.getInteger(LINEALPHA_KEY);
        fillColor = string2Color(memento.getString(FILLCOLOR_KEY));
        fillAlpha = memento.getInteger(FILLALPHA_KEY);
        type = memento.getInteger(SHAPETYPE_KEY);
        setScaleFactor(memento.getFloat(SCALEFACTOR_KEY));
    }

    private String color2String(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Color string2Color(String string) {
        String[] split = string.split(","); //$NON-NLS-1$
        Color color = new Color(Integer.parseInt(split[0].trim()),
                Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()));
        return color;
    }

    @Override
    public String getExtensionPointID() {
        return "org.locationtech.udig.printing.ui.standardBoxes"; //$NON-NLS-1$
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    @Override
    public Color getFillColor() {
        return fillColor;
    }

    @Override
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineAlpha() {
        return lineAlpha;
    }

    public void setLineAlpha(int lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public int getFillAlpha() {
        return fillAlpha;
    }

    public void setFillAlpha(int fillAlpha) {
        this.fillAlpha = fillAlpha;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
