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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.printing.model.AbstractBoxPrinter;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

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

    private static final String FONT_COLOR_KEY = "fontCOLOR"; //$NON-NLS-1$

    private static final String FONT_SCALE_FACTOR = "fontScaleFActor"; //$NON-NLS-1$

    private static final String LABEL_KEY = "label"; //$NON-NLS-1$

    private static final String HORIZ_ALIGN_KEY = "horizontalAlignment"; //$NON-NLS-1$

    private String text = "Set Text"; //$NON-NLS-1$

    private int padding;

    String preview;

    private int horizontalAlignment = SWT.LEFT;

    private int verticalAlignment = SWT.TOP;

    private boolean wrap;

    private Color fontColor = Color.BLACK;

    /**
     * The font used to draw on paper. The one set by the user and expected to be the real size.
     */
    private Font originalFont;

    /**
     * The scaled font used to draw on screen page. Resized to give the proper size feeling on
     * screen as if it was on paper.
     */
    private Font scaledFont;

    private boolean inPreviewMode = false;

    private float scaleFactor = Float.NaN;

    public LabelBoxPrinter() {
        super();

        this.padding = 0;
    }

    public LabelBoxPrinter(int padding) {
        super();

        this.padding = padding;
    }

    public LabelBoxPrinter(float scaleFactor) {
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

    public int getPadding() {
        return this.padding;
    }

    /**
     * Gets the text displayed in the box
     *
     * @return the box text, or null if no text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text to display in the box. Null represents no text.
     *
     * @param text the box text.
     */
    public void setText(String text) {
        this.text = text;
        setDirty(true);
    }

    @Override
    public void draw(Graphics2D graphics, IProgressMonitor monitor) {
        super.draw(graphics, monitor);

        int boxWidth = getBox().getSize().width;
        int boxHeight = getBox().getSize().height;
        int availableWidth = boxWidth - 2 * padding;
        int availableHeight = boxHeight - 2 * padding;

        Font drawFont = null;
        if (inPreviewMode) {
            drawFont = scaledFont;
        } else {
            drawFont = originalFont;
        }

        if (drawFont == null) {
            setDefaultFont();
            if (inPreviewMode) {
                drawFont = scaledFont;
            } else {
                drawFont = originalFont;
            }
        }

        // draw text
        if (drawFont != null && text != null) {
            graphics.setFont(drawFont);
            graphics.setColor(fontColor);
            int spaceBetweenLines = (int) (drawFont.getSize() / 4f);

            // calculate vertical position of the first line
            int y;
            List<String> lines = splitIntoLines(text, availableWidth, graphics, getWrap());

            switch (verticalAlignment) {
            case SWT.CENTER:
                int textHeight = textHeight(lines, spaceBetweenLines, graphics);
                y = padding + (availableHeight - textHeight) / 2;
                break;
            default:
                y = padding;
                break;
            } // switch

            // draw each line
            int x;
            for (int i = 0; i < lines.size(); i++) {

                String line = lines.get(i);
                Rectangle2D lineBounds = graphics.getFontMetrics().getStringBounds(line, graphics);

                // compute the horizontal alignment of each line
                switch (horizontalAlignment) {
                case SWT.CENTER:
                    x = (int) ((boxWidth - lineBounds.getWidth()) / 2);
                    break;
                case SWT.RIGHT:
                    x = (int) (boxWidth - lineBounds.getWidth()) - padding;
                    break;
                default:
                    // default is left
                    x = padding;
                    break;

                } // switch

                if (i == 0) {
                    y += drawFont.getSize(); // add "ascent"
                } else {
                    y += lineBounds.getHeight(); // add "ascent" + "descent"
                }
                graphics.drawString(line, x, y);
                y += spaceBetweenLines; // add "leading"

            } // for
        } // if
    }

    private void setDefaultFont() {

        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                // create a default
                FontData data = Display.getDefault().getSystemFont().getFontData()[0];
                data.setHeight(24);
                data.setStyle(SWT.BOLD);
                Font font = AWTSWTImageUtils.swtFontToAwt(data);
                setFont(font);
            }
        });

    }

    /**
     * Calculates the height of all lines together, including whitespace between lines
     *
     * @return height of text block
     */
    private int textHeight(List<String> lines, int spaceBetweenLines, Graphics2D graphics) {
        int height = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                height += spaceBetweenLines;
            }
            String line = lines.get(i);
            Rectangle2D lineBounds = graphics.getFontMetrics().getStringBounds(line, graphics);
            height += lineBounds.getHeight();
        }
        return height;
    }

    /**
     * A line wrap algorithm, which splits the given line of text into multiple lines based on the
     * current font size and the available line width
     *
     * @param text
     * @param availableWidth
     * @param graphics
     * @param wrap
     * @return a list of strings, representing the lines
     */
    private List<String> splitIntoLines(String text, int availableWidth, Graphics2D graphics,
            boolean wrap) {

        List<String> lines = new ArrayList<>();
        if (!wrap) {
            lines.add(text);
            return lines;
        }

        String[] words = text.split(" "); //$NON-NLS-1$
        String currentLine = ""; //$NON-NLS-1$
        for (int i = 0; i < words.length; i++) {

            String tryLine = (currentLine.equals("")) ? words[i] : (currentLine + " " + words[i]); //$NON-NLS-1$ //$NON-NLS-2$
            Rectangle2D lineBounds = graphics.getFontMetrics().getStringBounds(tryLine, graphics);
            if (lineBounds.getWidth() > availableWidth) {
                lines.add(currentLine);
                currentLine = words[i];
            } else {
                currentLine = tryLine;
            }
        }
        if (!currentLine.equals("")) { //$NON-NLS-1$
            lines.add(currentLine);
        }
        return lines;
    }

    @Override
    public void createPreview(Graphics2D graphics, IProgressMonitor monitor) {
        inPreviewMode = true;
        draw(graphics, monitor);
        preview = getText();
        setDirty(false);
        inPreviewMode = false;
    }

    @Override
    public void save(IMemento memento) {
        memento.putString(LABEL_KEY, text);
        memento.putInteger(HORIZ_ALIGN_KEY, horizontalAlignment);
        if (originalFont != null) {
            memento.putString(FONT_NAME_KEY, originalFont.getFamily());
            memento.putInteger(STYLE_KEY, originalFont.getStyle());
            memento.putInteger(SIZE_KEY, originalFont.getSize());
            memento.putFloat(FONT_SCALE_FACTOR, getScaleFactor());
        }
        if (fontColor != null) {
            StringBuilder clrString = new StringBuilder();
            clrString.append(fontColor.getRed());
            clrString.append(","); //$NON-NLS-1$
            clrString.append(fontColor.getGreen());
            clrString.append(","); //$NON-NLS-1$
            clrString.append(fontColor.getBlue());
            memento.putString(FONT_COLOR_KEY, clrString.toString());
        }
    }

    @Override
    public void load(IMemento memento) {
        text = memento.getString(LABEL_KEY);
        horizontalAlignment = memento.getInteger(HORIZ_ALIGN_KEY);
        setScaleFactor(memento.getFloat(FONT_SCALE_FACTOR));
        String family = memento.getString(FONT_NAME_KEY);
        if (family != null) {
            int size = memento.getInteger(SIZE_KEY);
            int style = memento.getInteger(STYLE_KEY);
            originalFont = new Font(family, style, size);
            int resizedValue = (int) (size * getScaleFactor());
            if (resizedValue < 4) {
                resizedValue = 4;
            }
            scaledFont = new Font(family, style, resizedValue);

            String colorString = memento.getString(FONT_COLOR_KEY);
            if (colorString != null) {
                String[] colorSplit = colorString.split(","); //$NON-NLS-1$
                Color clr = new Color(Integer.parseInt(colorSplit[0]),
                        Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                fontColor = clr;
            }

        }
    }

    @Override
    public String getExtensionPointID() {
        return "org.locationtech.udig.printing.ui.standardBoxes"; //$NON-NLS-1$
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        if (adapter.isAssignableFrom(String.class)) {
            return text;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /**
     * Set the font of the label
     *
     * @param newFont the new font
     */
    public void setFont(Font newFont) {
        originalFont = newFont;
        int resizedValue = (int) (originalFont.getSize() * getScaleFactor());
        if (resizedValue < 4) {
            resizedValue = 4;
        }
        scaledFont = new Font(newFont.getName(), newFont.getStyle(), resizedValue);

        setDirty(true);
    }

    /**
     * Get the font of the label
     *
     * @return the label font
     */
    public Font getFont() {
        return originalFont;
    }

    /**
     * Sets the horizontal alignment of the label. Options are: {@link SWT#CENTER},
     * {@link SWT#RIGHT}, {@link SWT#LEFT}
     *
     * @param newAlignment the new alignment. One of {@link SWT#CENTER}, {@link SWT#RIGHT},
     *        {@link SWT#LEFT}
     */
    public void setHorizontalAlignment(int newAlignment) {
        if (newAlignment != SWT.LEFT && newAlignment != SWT.CENTER && newAlignment != SWT.RIGHT) {
            throw new IllegalArgumentException("An illegal option was provided"); //$NON-NLS-1$
        }
        this.horizontalAlignment = newAlignment;
        setDirty(true);
    }

    /**
     * Sets the vertical alignment of the label. Options are: {@link SWT#CENTER}, {@link SWT#TOP}
     *
     * @param newAlignment the new alignment. One of {@link SWT#CENTER}, {@link SWT#TOP}
     */
    public void setVerticalAlignment(int newAlignment) {
        if (newAlignment != SWT.TOP && newAlignment != SWT.CENTER) {
            throw new IllegalArgumentException("An illegal option was provided"); //$NON-NLS-1$
        }
        this.verticalAlignment = newAlignment;
        setDirty(true);
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public boolean getWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public static String getHORIZ_ALIGN_KEY() {
        return HORIZ_ALIGN_KEY;
    }

}
