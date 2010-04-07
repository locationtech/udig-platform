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
package net.refractions.udig.printing.ui.internal;

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
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Implementation of a Template at its most basic. Contains a title bar and a map.
 * 
 * @author Richard Gould
 */
public class BasicTemplate extends AbstractTemplate {

    private static final int MARGIN = 10;
    private static final int SPACING = 10;
    
    private Rectangle mapBounds;
    
    /**
     * Constructs the BasicTemplate and populates its two boxes with a title and a map.
     */
    public BasicTemplate() {
        super();
    }

    /**
     * Populates the templates two boxes with a title and map
     * 
     * @param page the parent(owner) page
     * @param map the Map to be drawn
     */
    public void init( Page page, Map map ) {
        int height = page.getSize().height;
        int width = page.getSize().width;
        final int labelWidth = width;
        final int labelHeight;
        int legendWidth = 150;
        int legendHeight = 150;
        int scaleHeight = 20;
        int scaleWidth = 150;

        labelHeight = addLabelBox(map, width, labelWidth);

        mapBounds = addMapBox(map, width, height, labelHeight, scaleHeight, legendWidth);

        addLegendBox(height, legendWidth, legendHeight, labelHeight, mapBounds);

        addScale(height, scaleHeight, scaleWidth);
    }

    private void addScale( int height, int scaleHeight, int scaleWidth ) {
        Box scaleBox = ModelFactory.eINSTANCE.createBox();
        MapGraphicBoxPrinter scale = new MapGraphicBoxPrinter(null);
        scale.setMapGraphic(MapGraphicChooserDialog.findResource(ScalebarMapGraphic.class));
        scaleBox.setBoxPrinter(scale);
        scaleBox.setID("Scalebar Box"); //$NON-NLS-1$
        scaleBox.setLocation(new Point(MARGIN, height - MARGIN - scaleHeight));
        scaleBox.setSize(new Dimension(scaleWidth, scaleHeight));
        boxes.add(scaleBox);
    }

    private int addLabelBox( Map map, int width, final int labelWidth) {
        Box labelBox = ModelFactory.eINSTANCE.createBox();
        LabelBoxPrinter labelBoxPrinter = new LabelBoxPrinter();
        labelBoxPrinter.setText(formatName(map.getName()));
        labelBoxPrinter.setHorizontalAlignment(SWT.CENTER);
        try {
            FontData data = Display.getDefault().getSystemFont().getFontData()[0];

            data.setHeight( 18 );
            data.setStyle( SWT.BOLD );
            Font font = AWTSWTImageUtils.swtFontToAwt(data);
            labelBoxPrinter.setFont(font);

        } catch (Exception e) {
            // oh well don't have that font type
        }
        labelBox.setBoxPrinter(labelBoxPrinter);
        labelBox.setID("Standard Label"); //$NON-NLS-1$
        // TODO base it on the font
        int labelHeight = 30+LabelBoxPrinter.INSET*2;
        labelBox.setSize(new Dimension(labelWidth, labelHeight));
        labelBox.setLocation(new Point((width - labelWidth) / 2, MARGIN));
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

    private Rectangle addMapBox( Map map, int width, int height, final int labelHeight, int scaleHeight,
            int legendWidth ) {
        Box mapBox = ModelFactory.eINSTANCE.createBox();
        MapBoxPrinter mapBoxPrinter = new MapBoxPrinter();
        mapBox.setID("Standard Map Box"); //$NON-NLS-1$
        mapBox.setBoxPrinter(mapBoxPrinter);
        mapBoxPrinter.setMap(map);

        // calculate mapSize
        int bothMargins = (MARGIN * 2);
        int mapWidth = width - bothMargins - legendWidth-SPACING;
        int labelAndSpacing = labelHeight + SPACING;
        int scaleAndSpacing = scaleHeight + SPACING;
        int mapHeight = height - bothMargins - labelAndSpacing - scaleAndSpacing;

        int mapX = MARGIN;
        int mapY = MARGIN + labelAndSpacing;
        
        Rectangle mapBounds = new Rectangle(
                mapX,
                mapY,
                mapWidth, 
                mapHeight);
        mapBox.setSize(new Dimension(mapBounds.width, mapBounds.height));

        mapBox.setLocation(new Point(mapBounds.x, mapBounds.y));
        boxes.add(mapBox);
        return mapBounds;
    }

    private void addLegendBox( int height, final int legendWidth, int legendHeight, int labelHeight,
            Rectangle mapBounds ) {
        Box legendBox = ModelFactory.eINSTANCE.createBox();
        MapGraphicBoxPrinter legend = new MapGraphicBoxPrinter(null);
        legend.setMapGraphic(MapGraphicChooserDialog.findResource(LegendGraphic.class));
        legendBox.setBoxPrinter(legend);
        legendBox.setID("Legend Box"); //$NON-NLS-1$
        legendBox.setLocation(new Point(MARGIN + mapBounds.width + SPACING, MARGIN+labelHeight+MARGIN));
        legendBox.setSize(new Dimension(legendWidth, legendHeight));
        boxes.add(legendBox);
    }

    public String getName() {
        return Messages.BasicTemplate_name;
    }

    public Rectangle getMapBounds() throws IllegalStateException {
        if (mapBounds == null)
            throw new IllegalStateException("Please initialize the template before calling this method.");
        return mapBounds;
    }

    public String getAbbreviation() {
        return getName();
    }
}
