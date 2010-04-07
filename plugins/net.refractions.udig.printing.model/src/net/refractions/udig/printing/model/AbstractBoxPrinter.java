/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.printing.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IMemento;

/**
 * Provides simple/stupid implementation for the optional methods in BoxPrinter.
 * <p>
 * Nothing is saved when save is called.  So everything must be hard coded.  Preview simply calls
 * draw() and only does so once.
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractBoxPrinter implements BoxPrinter {

    private Box box;
    private boolean dirty = false;
    private Color borderColor;
    private Stroke borderStroke;
    private Color fillColor;

    private PropertyListener listener = new PropertyListener(){
        @Override
        protected void locationChanged() {
            boxLocationChanged();
        }

        @Override
        protected void sizeChanged() {
            boxSizeChanged();
        }
    };

    public AbstractBoxPrinter() {
        fillColor = null; // no fill
        borderColor = null;
        borderStroke = null;
    }

    /**
     * default implementation which draws only the border and fill
     */
    public void draw( Graphics2D graphics, IProgressMonitor monitor ) {
        int boxWidth = getBox().getSize().width;
        int boxHeight = getBox().getSize().height;

        // draw fill
        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillRect(0, 0, boxWidth - 1, boxHeight - 1);
        }

        // draw border
        if (borderStroke != null && borderColor != null) {
            graphics.setColor(borderColor);
            graphics.setStroke(borderStroke);
            graphics.drawRect(0, 0, boxWidth - 1, boxHeight - 1);
        }
    }

    /**
     * Gets the border color.  If either border color is null or border width
     * is zero, then the border is not drawn.
     *
     * @return the border color.  Null represents no border.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the border color.  Null represents no border.
     *
     * @param borderColor the border color
     */
    public void setBorderColor( Color borderColor ) {
        this.borderColor = borderColor;
        setDirty(true);
    }

    /**
     * Gets the border Stroke.  If either border color is null or border Stroke
     * is null, then the border is not drawn.
     *
     * @return the border Stroke.  null represents no border.
     */
    public Stroke getBorderStroke() {
        return borderStroke;
    }

    /**
     * Sets the border stroke.  null represents no border.
     *
     * @param borderStroke the border Stroke
     */
    public void setBorderStroke( Stroke borderStroke ) {
        this.borderStroke = borderStroke;
        setDirty(true);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor( Color fillColor ) {
        this.fillColor = fillColor;
        setDirty(true);
    }

    /**
     * called when the location of the box has changed
     */
    protected void boxLocationChanged() {
        dirty = true;
    }

    /**
     * called when the size of the box has changed
     */
    protected void boxSizeChanged() {
        dirty = true;
    }

    /**
     * By default this method does nothing
     */
    public void save( IMemento memento ) {
    }

    /**
     * By default this method does nothing
     */
    public void load( IMemento memento ) {
    }

    /**
     * By default this method calls draw and sets dirty to be false.
     */
    public void createPreview( Graphics2D graphics, IProgressMonitor monitor ) {
        draw(graphics, monitor);
        dirty = false;
    }

    /**
     * By default this will return false when ever the size of location of the box has been changed.
     */
    public boolean isNewPreviewNeeded() {
        return dirty;
    }

    public void setDirty( boolean dirty ) {
        boolean oldDirty = this.dirty;
        this.dirty = dirty;
        // trigger re-render
        if (dirty && getBox() != null) {
            getBox().notifyPropertyChange(new PropertyChangeEvent(this, "dirty", oldDirty, dirty)); //$NON-NLS-1$
        }
    }

    public Box getBox() {
        return box;
    }

    public void setBox( Box box2 ) {
        if (box != null)
            box.eAdapters().remove(listener);
        this.box = box2;
        if (box != null)
            box.eAdapters().add(listener);
    }

    /**
     * Conversion from cm in typographic unit of measurement (point).
     * 
     * <p>
     * info: http://1t3xt.info/tutorials/faq.php?branch=faq.pdf_in_general&node=measurements
     * </p>
     * 
     * @param cm the centimeter to convert.
     * @return the converted points.
     */
    public static float cm2point( float cm ) {
        return inch2point(cm2inch(cm));
    }

    public static float cm2inch( float cm ) {
        return cm / 2.54f;
    }

    public static float inch2point( float inch ) {
        return inch * 72f;
    }

    /**
     * Conversion from typographic unit of measurement (point) to cm.
     * 
     * <p>
     * info: http://1t3xt.info/tutorials/faq.php?branch=faq.pdf_in_general&node=measurements
     * </p>
     * 
     * @param point the points to convert.
     * @return the converted centimeter.
     */
    public static float point2cm( float point ) {
        return inch2cm(point2inch(point));
    }

    public static float inch2cm( float inch ) {
        return inch * 2.54f;
    }

    public static float point2inch( float point ) {
        return point / 72f;
    }

}
