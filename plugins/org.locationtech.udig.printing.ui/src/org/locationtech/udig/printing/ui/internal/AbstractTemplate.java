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
package org.locationtech.udig.printing.ui.internal;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.Template;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

/**
 * An abstract implementation of a Template that provides basic functionality.
 *
 * Classes that extend this should typically create their own boxes and
 * organize them according to their own desires.
 *
 * @author Richard Gould
 */
public abstract class AbstractTemplate implements Template {

    protected List<Box> boxes;

    protected double scaleDenomHint;
    protected boolean zoomToSelectionHint;
    protected int numPages = 1;
    private int activePage = 0; // zero indexed

    /**
     * Create a basic AbstractTemplate. Initializes the boxes list.
     */
    public AbstractTemplate() {
        boxes = new ArrayList<Box>();
        scaleDenomHint = AbstractTemplate.SCALE_UNSPECIFIED;
        zoomToSelectionHint = false;
    }

    public Iterator<Box> iterator() {
        return boxes.iterator();
    }

    public String toString() {
        return getName();
    }

    public Template clone() {
        try {
            return (Template) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPreferredOrientation() {
        return ORIENTATION_UNSPECIFIED;
    }

    public void setMapScaleHint( double scaleDenom ) {
        this.scaleDenomHint = scaleDenom;
    }

    public double getMapScaleHint() {
        return scaleDenomHint;
    }

    public void setZoomToSelectionHint( boolean hint ) {
        this.zoomToSelectionHint = hint;
    }

    public boolean getZoomToSelectionHint() {
        return zoomToSelectionHint;
    }

    public Rectangle getMapBounds() throws IllegalStateException {
        Rectangle bounds = null;
        for( Box box : boxes ) {
            if (box.getBoxPrinter() instanceof MapBoxPrinter) {
                Dimension size = box.getSize();
                Point location = box.getLocation();
                bounds = new Rectangle(location.x, location.y, size.width, size.height);
            }
        }
        return bounds;
    }

    protected Font getFont( int size, int style ) {
        try {

            String face = "PLAIN";
            if (style == SWT.BOLD) {
                face = "BOLD";
            }

            Font font = Font.decode("Arial-" + face + "-" + size);
            return font;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getNumPages() {
        return 1;
    }

    public void setActivePage( int page ) {
        this.activePage = page;
    }

    public int getActivePage() {
        return activePage;
    }

    protected void setPageSizeFromPaperSize( Page page, Dimension paperSize ) {
        float factor = (float) paperSize.width / (float) paperSize.height;
        Dimension pageSize = page.getSize();
        int h = (int) ((float) pageSize.height * 0.97f);
        int w = (int) ((float) h * factor);
        page.setSize(new Dimension(w, h));
    }

    protected float scaleValue( Page page, Dimension paperSize, float previousValue ) {
        float factor = (float) page.getSize().width / (float) paperSize.height;
        int resizedValue = (int) ((float) previousValue * factor);
        if (resizedValue < 4) {
            resizedValue = 4;
        }
        return resizedValue;
    }
}
