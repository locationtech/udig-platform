/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.template;

import java.awt.Font;

import net.refractions.udig.legend.ui.LegendGraphic;
import net.refractions.udig.mapgraphic.MapGraphicChooserDialog;
import net.refractions.udig.mapgraphic.scalebar.ScalebarMapGraphic;
import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.impl.LabelBoxPrinter;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;
import net.refractions.udig.printing.model.impl.MapGraphicBoxPrinter;
import net.refractions.udig.printing.ui.internal.AbstractTemplate;
import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.lowagie.text.PageSize;

/**
 * Implementation of a Template at its most basic. Contains a title bar and a map.
 * 
 * @author Richard Gould
 * @author Andrea Antonello (www.hydrologis.com)
 */
public abstract class AbstractPrinterPageTemplate extends AbstractTemplate {

    protected static final float UPPER_MARGIN_PERCENT = 3;
    protected static final float BOTTOM_MARGIN_PERCENT = 3;
    protected static final float LEFT_MARGIN_PERCENT = 3;
    protected static final float RIGHT_MARGIN_PERCENT = 3;

    protected static final float MAP_WIDTH_PERCENT = 90;
    protected static final float MAP_HEIGHT_PERCENT = 80;
    protected static final float SCALE_WIDTH_PERCENT = 20;
    protected static final float SCALE_HEIGHT_PERCENT = 5;
    protected static final float LEGEND_WIDTH_PERCENT = 20;
    protected static final float LEGEND_HEIGHT_PERCENT = 15;
    protected static final float TITLE_WIDTH_PERCENT = MAP_WIDTH_PERCENT;
    protected static final float TITLE_HEIGHT_PERCENT = 8;

    protected static final float SPACING_PERCENT = 2;

    protected static final int BASEFONT_SIZE = 18;

    protected Rectangle mapBounds;
    private Page page;

    /**
     * Constructs the BasicTemplate and populates its two boxes with a title and a map.
     */
    public AbstractPrinterPageTemplate() {
        super();
    }

    /**
     * Populates the templates two boxes with a title and map
     * 
     * @param page the parent(owner) page
     * @param map the Map to be drawn
     */
    public void init( Page page, Map map ) {
        this.page = page;
        com.lowagie.text.Rectangle paperRectangle = getPaperSize();
        Dimension paperSize = new Dimension((int) paperRectangle.getWidth(), (int) paperRectangle
                .getHeight());
        // set the requested papersize
        page.setPaperSize(paperSize);
        // then apply the ratio of the papersize also to the page size.
        setPageSizeFromPaperSize(page, paperSize);

        float scaleFactor = (float) page.getSize().width / (float) page.getPaperSize().height;

        int height = page.getSize().height;
        int width = page.getSize().width;

        int xPos = getPercentagePieceOf(width, LEFT_MARGIN_PERCENT);
        int yPos = getPercentagePieceOf(height, UPPER_MARGIN_PERCENT);
        int w = getPercentagePieceOf(width, TITLE_WIDTH_PERCENT);
        int h = getPercentagePieceOf(height, TITLE_HEIGHT_PERCENT);
        // the base font size is good for the A4 size, scale every other proportional
        float scaledSize = (float) BASEFONT_SIZE * (float) paperSize.height
                / PageSize.A4.getHeight();
        // float scaledFontSize = scaleValue(page, paperSize, scaledSize);
        addLabelBox(formatName(map.getName()), xPos, yPos, w, h, (int) scaledSize, scaleFactor);

        xPos = getPercentagePieceOf(width, LEFT_MARGIN_PERCENT);
        yPos = getPercentagePieceOf(height, UPPER_MARGIN_PERCENT + TITLE_HEIGHT_PERCENT);
        w = getPercentagePieceOf(width, MAP_WIDTH_PERCENT);
        h = getPercentagePieceOf(height, MAP_HEIGHT_PERCENT);
        addMapBox(map, xPos, yPos, w, h, paperSize);

        // xPos = getPercentagePieceOf(width, 100f - RIGHT_MARGIN_PERCENT - SPACING_PERCENT * 3f
        // - LEGEND_WIDTH_PERCENT);
        // yPos = getPercentagePieceOf(height, 100f - BOTTOM_MARGIN_PERCENT - SPACING_PERCENT * 3f
        // - LEGEND_HEIGHT_PERCENT);
        // w = getPercentagePieceOf(width, LEGEND_WIDTH_PERCENT);
        // h = getPercentagePieceOf(height, LEGEND_HEIGHT_PERCENT);
        // addLegendBox(xPos, yPos, w, h);

        xPos = getPercentagePieceOf(width, LEFT_MARGIN_PERCENT + SPACING_PERCENT * 2f);
        yPos = getPercentagePieceOf(height, 100f - BOTTOM_MARGIN_PERCENT - SPACING_PERCENT * 3f
                - SCALE_HEIGHT_PERCENT);
        w = getPercentagePieceOf(width, SCALE_WIDTH_PERCENT);
        h = getPercentagePieceOf(height, SCALE_HEIGHT_PERCENT);
        addScale(xPos, yPos, w, h);
    }

    private int getPercentagePieceOf( int width, float percent ) {
        int res = (int) ((float) width * percent / 100f);
        return res;
    }

    protected void addScale( int xPos, int yPos, int scaleWidth, int scaleHeight ) {
        Box scaleBox = ModelFactory.eINSTANCE.createBox();
        MapGraphicBoxPrinter scale = new MapGraphicBoxPrinter(page);
        scale.setMapGraphic(MapGraphicChooserDialog.findResource(ScalebarMapGraphic.class));
        scaleBox.setBoxPrinter(scale);
        scaleBox.setID("Scalebar Box"); //$NON-NLS-1$
        scaleBox.setLocation(new Point(xPos, yPos));
        scaleBox.setSize(new Dimension(scaleWidth, scaleHeight));
        boxes.add(scaleBox);
    }

    /**
     * @return the iText Rectangle size of the paper. Used in the init method.
     */
    protected abstract com.lowagie.text.Rectangle getPaperSize();

    protected int addLabelBox( String text, int xPos, int yPos, int labelWidth, int labelHeight,
            int fontSize, float scaleFactor ) {
        Box labelBox = ModelFactory.eINSTANCE.createBox();
        labelBox.setSize(new Dimension(labelWidth, labelHeight));
        labelBox.setLocation(new Point(xPos, yPos));
        LabelBoxPrinter labelBoxPrinter = new LabelBoxPrinter(scaleFactor);
        labelBox.setBoxPrinter(labelBoxPrinter);

        labelBox.setID("Standard Label"); //$NON-NLS-1$
        labelBoxPrinter.setText(text);
        labelBoxPrinter.setHorizontalAlignment(SWT.CENTER);
        try {
            FontData data = Display.getDefault().getSystemFont().getFontData()[0];

            data.setHeight(fontSize);
            data.setStyle(SWT.BOLD);

            Font font = AWTSWTImageUtils.swtFontToAwt(data);
            labelBoxPrinter.setFont(font);

        } catch (Exception e) {
            // oh well don't have that font type
        }
        boxes.add(labelBox);
        return labelHeight;
    }

    private String formatName( String name ) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        name = name.replaceAll("[_\\s]", " "); //$NON-NLS-1$//$NON-NLS-2$
        StringBuilder builder = new StringBuilder();
        char prev = ' ';
        for( int i = 0; i < name.length(); i++ ) {
            char current = name.charAt(i);
            if (prev == ' ') {
                builder.append(Character.toUpperCase(current));
            } else {
                builder.append(current);
            }
            prev = current;
        }
        return builder.toString();
    }

    protected Rectangle addMapBox( Map map, int xPos, int yPos, int mapWidth, int mapHeight, Dimension paperSize ) {
        Box mapBox = ModelFactory.eINSTANCE.createBox();
        MapBoxPrinter mapBoxPrinter = new MapBoxPrinter();
        mapBox.setID("Standard Map Box"); //$NON-NLS-1$
        mapBox.setBoxPrinter(mapBoxPrinter);
        mapBoxPrinter.setMap(map);
        

        Rectangle mapBounds = new Rectangle(xPos, yPos, mapWidth, mapHeight);
        mapBox.setSize(new Dimension(mapBounds.width, mapBounds.height));
        mapBox.setPaperSize(paperSize);

        mapBox.setLocation(new Point(mapBounds.x, mapBounds.y));
        boxes.add(mapBox);
        return mapBounds;
    }

    protected void addLegendBox( int xPos, int yPos, int legendWidth, int legendHeight ) {
        Box legendBox = ModelFactory.eINSTANCE.createBox();
        MapGraphicBoxPrinter legend = new MapGraphicBoxPrinter(page);
        legend.setMapGraphic(MapGraphicChooserDialog.findResource(LegendGraphic.class));
        legendBox.setBoxPrinter(legend);
        legendBox.setID("Legend Box"); //$NON-NLS-1$
        legendBox.setLocation(new Point(xPos, yPos));
        legendBox.setSize(new Dimension(legendWidth, legendHeight));
        boxes.add(legendBox);
    }

    public String getName() {
        return Messages.Landscape_Template_Name;
    }

    public Rectangle getMapBounds() throws IllegalStateException {
        if (mapBounds == null)
            throw new IllegalStateException(
                    "Please initialize the template before calling this method.");
        return mapBounds;
    }

    public String getAbbreviation() {
        return getName();
    }
}
