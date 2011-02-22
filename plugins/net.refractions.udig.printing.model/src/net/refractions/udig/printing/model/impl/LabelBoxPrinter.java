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
package net.refractions.udig.printing.model.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.refractions.udig.printing.model.AbstractBoxPrinter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IMemento;

/**
 * Box printer for map labels.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class LabelBoxPrinter extends AbstractBoxPrinter {

    /**
     * The amount the Text is inset from the edges
     */
    public static final int INSET = 10;

    private static final String SIZE_KEY = "size"; //$NON-NLS-1$
    private static final String STYLE_KEY = "style"; //$NON-NLS-1$
    private static final String FONT_NAME_KEY = "fontName"; //$NON-NLS-1$
    private static final String LABEL_KEY = "label"; //$NON-NLS-1$
    private static final String HORIZ_ALIGN_KEY = "horizontalAlignment"; //$NON-NLS-1$

    private String text = "Set Text"; //$NON-NLS-1$
    private Font font;
    String preview;
    private int horizontalAlignment = SWT.LEFT;

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
        setDirty(true);
    }

    public void draw( Graphics2D graphics, IProgressMonitor monitor ) {
        if (font != null) {
            graphics.setFont(font);
        }
        graphics.setColor(Color.BLACK);
        Rectangle2D textBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
        int x;
        int y = Math.min(getBox().getSize().height, (int) (INSET + textBounds.getHeight()));

        switch( horizontalAlignment ) {
        case SWT.CENTER:
            x = (int) ((getBox().getSize().width-textBounds.getWidth())/2);
            break;
        case SWT.RIGHT:
            x = (int) (getBox().getSize().width-textBounds.getWidth());
            break;

        default:
            // default is left
            x=INSET;
            break;
        }
        graphics.drawString(getText(), x, y);
    }

    public void createPreview( Graphics2D graphics, IProgressMonitor monitor ) {
        draw(graphics, monitor);
        preview = getText();
        setDirty(false);
    }

    public void save( IMemento memento ) {
        memento.putString(LABEL_KEY, text);
        memento.putInteger(HORIZ_ALIGN_KEY, horizontalAlignment);
        if (font != null) {
            memento.putString(FONT_NAME_KEY, font.getFamily());
            memento.putInteger(STYLE_KEY, font.getStyle());
            memento.putInteger(SIZE_KEY, font.getSize());
        }
    }

    public void load( IMemento memento ) {
        text = memento.getString(LABEL_KEY);
        horizontalAlignment = memento.getInteger(HORIZ_ALIGN_KEY);
        String family = memento.getString(FONT_NAME_KEY);
        if (family != null) {
            int size = memento.getInteger(SIZE_KEY);
            int style = memento.getInteger(STYLE_KEY);
            font = new Font(family, style, size);
        }
    }

    public String getExtensionPointID() {
        return "net.refractions.udig.printing.ui.standardBoxes"; //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if (adapter.isAssignableFrom(String.class)) {
            return text;
        }
        return null;
    }

    /**
     * Set the font of the label
     *
     * @param newFont the new font
     */
    public void setFont( Font newFont ) {
        this.font = newFont;
        setDirty(true);
    }

    /**
     * Get the font of the label
     *
     * @return the label font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the horizontal alignment of the label.  Options are: {@link SWT#CENTER}, {@link SWT#RIGHT}, {@link SWT#LEFT}
     *
     * @param newAlignment the new alignment.  One of {@link SWT#CENTER}, {@link SWT#RIGHT}, {@link SWT#LEFT}
     */
    public void setHorizontalAlignment( int newAlignment ) {
        if( newAlignment!=SWT.LEFT && newAlignment!=SWT.CENTER && newAlignment!=SWT.RIGHT){
            throw new IllegalArgumentException("An illegal option was provided"); //$NON-NLS-1$
        }
        this.horizontalAlignment = newAlignment;
        setDirty(true);
    }

}
