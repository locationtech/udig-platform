/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
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
package net.refractions.udig.legend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.mapgraphic.style.FontStyle;
import net.refractions.udig.mapgraphic.style.FontStyleContent;
import net.refractions.udig.mapgraphic.style.LocationStyleContent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.ui.internal.LayerGeneratedGlyphDecorator;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;
import net.refractions.udig.ui.graphics.SLDs;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.graphics.Image;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;

/**
 * Draw a legend based on looking at the current list layer list.

 * @author Amr
 * @since 1.0.0
 */
public class LegendGraphic implements MapGraphic {

    private int verticalMargin; //distance between border and icons/text
    private int horizontalMargin; //distance between border and icons/text
    private int verticalSpacing; //distance between layers
    private int horizontalSpacing; //space between image and text
    private Color foregroundColour;
    private Color backgroundColour;
    private int indentSize;
    private int imageWidth;
    private int imageHeight; //size of image
    private int maxHeight;
    private int maxWidth;

    public void draw( MapGraphicContext context ) {
        
        IBlackboard blackboard = context.getLayer().getStyleBlackboard();
        LegendStyle legendStyle = (LegendStyle) blackboard.get(LegendStyleContent.ID);
        if (legendStyle == null) {
            legendStyle = LegendStyleContent.createDefault();
            blackboard.put(LegendStyleContent.ID, legendStyle);
        }
        
        Rectangle locationStyle = (Rectangle) blackboard.get(LocationStyleContent.ID);
        if (locationStyle == null) {
            locationStyle = new Rectangle(-1,-1,-1,-1);
            blackboard.put(LocationStyleContent.ID, locationStyle);
        }
        
        FontStyle fontStyle = (FontStyle) blackboard.get(FontStyleContent.ID);
        if( fontStyle==null ){
            fontStyle = new FontStyle();
            blackboard.put(FontStyleContent.ID, fontStyle);
        }
        
        this.backgroundColour = legendStyle.backgroundColour;
        this.foregroundColour = legendStyle.foregroundColour;
        this.horizontalMargin = legendStyle.horizontalMargin;
        this.verticalMargin = legendStyle.verticalMargin;
        this.horizontalSpacing = legendStyle.horizontalSpacing;
        this.verticalSpacing = legendStyle.verticalSpacing;
        this.indentSize = legendStyle.indentSize;
        this.imageHeight = legendStyle.imageHeight;
        this.imageWidth = legendStyle.imageWidth;
        
        this.maxHeight = locationStyle.width;
        this.maxWidth = locationStyle.height;
        
        final ViewportGraphics graphics = context.getGraphics();
        
        if(fontStyle.getFont()!=null){
            graphics.setFont(fontStyle.getFont());
        }
        
        List<Map<ILayer, FeatureTypeStyle[]>> layers = new ArrayList<Map<ILayer, FeatureTypeStyle[]>>();
        
        int longestRow = 0; //used to calculate the width of the graphic
        final int[] numberOfEntries = new int[1]; //total number of entries to draw
        numberOfEntries[0]=0;
        /*
         * Set up the layers that we want to draw so we can operate just on
         * those ones. Layers at index 0 are on the bottom of the map, so we 
         * must iterate in reverse.
         * 
         * While we are doing this, determine the longest row so we can properly
         * draw the graphic's border.
         */
        for (int i = context.getMapLayers().size()-1; i >= 0; i--) {
            ILayer layer = context.getMapLayers().get(i);
            if (!(layer.getGeoResource() instanceof MapGraphicResource)
                    && layer.isVisible()) {
                
                //String layerName = LayerGeneratedGlyphDecorator.generateLabel((Layer) layer);
                String layerName = layer.getName();
                if (layerName != null && layerName.length() != 0) {
                    
                    FeatureTypeStyle[] styles = locateStyle(layer);
                    
                    if (styles != null && rules(styles).size()>0 ) {
                        numberOfEntries[0] += rules(styles).size();
                        
                        for (Rule rule : rules(styles)) {
                            String text = getText(rule);
                            Rectangle2D bounds = graphics.getStringBounds(text);
                            int length = indentSize+imageWidth+horizontalSpacing+(int) bounds.getWidth();
                            
                            if (length > longestRow) {
                                longestRow = length;
                            }
                        }
                    } else if( !layer.hasResource(MapGraphic.class) ){
                        //TODO for other layer types 
                        continue;
                    } else{
                        continue;
                    }
                    
                    Map<ILayer, FeatureTypeStyle[]> map = Collections.singletonMap(layer, styles);
                    layers.add(map);
                    numberOfEntries[0]++; //add a line for the layer label
                    Rectangle2D bounds = graphics.getStringBounds(layerName);
                    int length = (int) bounds.getWidth();
                    if (styles != null && rules(styles).size() < 2) {
                        length += imageWidth+horizontalSpacing;
                    }
                    
                    if (length > longestRow) {
                        longestRow = length;
                    }
                }
            }
        }
        
        if (numberOfEntries[0] == 0) {
            //nothing to draw!
            return;
        }
        
        final int rowHeight = Math.max(imageHeight, graphics.getFontHeight()); //space allocated to each layer
        
        //total width of the graphic
        int width = longestRow+horizontalMargin*2;
        if (maxWidth > 0) {
            width = Math.min(width, maxWidth);
        }
        //total height of the graphic
        int height = rowHeight*numberOfEntries[0]+verticalMargin*2+verticalSpacing*(numberOfEntries[0]-1);
        if (maxHeight > 0) {
            height = Math.min(height, maxHeight);
        }
        
        if( locationStyle.width==0 || locationStyle.getHeight()==0 ){
            // we want to grow and shrink as we desire so we'll use a different
            // rectangle than the one on the blackboard.
            int x = locationStyle.x;
            int y = locationStyle.y;
            locationStyle = new Rectangle(); 
            locationStyle.x = x;
            locationStyle.y = y;
            locationStyle.width = width;
            locationStyle.height = height;
        }
        //ensure box within the display
        Dimension displaySize = context.getMapDisplay().getDisplaySize();
        if (locationStyle.x < 0){
            locationStyle.x = displaySize.width - locationStyle.width + locationStyle.x;
        }
        if ((locationStyle.x + locationStyle.width + 6) > displaySize.width ){
            locationStyle.x = displaySize.width - width-5;
        }
        
        if (locationStyle.y < 0){
            locationStyle.y = displaySize.height - locationStyle.height - 5 + locationStyle.y;
        }
        if ((locationStyle.y + height+6) > displaySize.height){
            locationStyle.y = displaySize.height - locationStyle.height - 5;
        }

        
        graphics.setClip(new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width+1, locationStyle.height+1));
        
        /*
         * Draw the box containing the layers/icons
         */
        drawOutline(graphics, context, locationStyle);
        
        /*
         * Draw the layer names/icons
         */
        final int[] rowsDrawn = new int[1];
        rowsDrawn[0]=0;
        final int[] x = new int[1];
        x[0]=locationStyle.x + horizontalMargin;
        final int[] y = new int[1];
        y[0]=locationStyle.y + verticalMargin;

        if(fontStyle.getFont()!=null){
            graphics.setFont(fontStyle.getFont());
        }
        
        for( int i = 0; i < layers.size(); i++ ) {
            Map<ILayer, FeatureTypeStyle[]> map = layers.get(i);
            final ILayer layer = map.keySet().iterator().next();
            final FeatureTypeStyle[] styles = map.values().iterator().next();

            final String layerName = layer.getName();
            try{
            	layer.getGeoResources().get(0).getInfo(null);
            }catch (Exception ex){}
            
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    if (styles != null && rules(styles).size() > 1) {
                        drawRow(graphics, x[0], y[0], null, layerName, false);

                        y[0] += rowHeight;
                        if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                            y[0] += verticalSpacing;
                        }
                        rowsDrawn[0]++;
                        for( Rule rule : rules(styles) ) {
                            Image swtIcon = LayerGeneratedGlyphDecorator.generateStyledIcon(layer,
                                    rule).createImage();
                            BufferedImage awtIcon = AWTSWTImageUtils.convertToAWT(swtIcon.getImageData());

                            drawRow(graphics, x[0], y[0], awtIcon, getText(rule), true);

                            y[0] += rowHeight;
                            if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                                y[0] += verticalSpacing;
                            }
                            rowsDrawn[0]++;
                        }
                    } else {
                        Image swtIcon = LayerGeneratedGlyphDecorator.generateIcon((Layer) layer)
                                .createImage();
                        BufferedImage awtIcon = AWTSWTImageUtils.convertToAWT(swtIcon.getImageData());

                        drawRow(graphics, x[0], y[0], awtIcon, layerName, false);

                        y[0] += rowHeight;
                        if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                            y[0] += verticalSpacing;
                        }
                        rowsDrawn[0]++;
                    }

                }
            });
        }
        //clear the clip so we don't affect other rendering processes
        graphics.setClip(null);
    }
   

    private List<Rule> rules( FeatureTypeStyle[] styles ) {
        List<Rule> rules = new ArrayList<Rule>();
        for( FeatureTypeStyle featureTypeStyle : styles ) {
            rules.addAll(Arrays.asList(featureTypeStyle.getRules()));
        }
        
        return rules;
    }

    private String getText( Rule rule ) {
        String text = ""; //$NON-NLS-1$
        if (rule.getTitle() != null && !"".equals(rule.getTitle())) { //$NON-NLS-1$
            text = rule.getTitle();
        } else if (rule.getName() != null && !"".equals(rule.getName())) { //$NON-NLS-1$
            text = rule.getName();
        } else if (rule.getFilter() != null){
            text = rule.getFilter().toString();
        }
        
        if (text.length() > 19) {
            return text.substring(0, 18) + "..."; //$NON-NLS-1$
        } else {
            return text;
        }
    }

    private void drawRow(ViewportGraphics graphics, int x, int y, 
            RenderedImage icon, String text, boolean indent) {
        
        Rectangle2D stringBounds = graphics.getStringBounds(text);

        /*
         * Center the smaller item (text or icon) according to the taller one.
         */
        int textVerticalOffset = 0;
        int iconVerticalOffset = 0;
        if (imageHeight == (int) stringBounds.getHeight()) {
            //items are the same height; do nothing.
        } else if (imageHeight > (int) stringBounds.getHeight()) {
            int difference = imageHeight - (int) stringBounds.getHeight();
            textVerticalOffset = difference / 2;
        } else if (imageHeight < (int) stringBounds.getHeight()){
            int difference = (int) stringBounds.getHeight() - imageHeight;
            iconVerticalOffset = difference / 2;
        }
        
        if (indent) {
            x += indentSize;
        }
        
        if (icon != null) {
            graphics.drawImage(icon, x, y+iconVerticalOffset);
            
            x += imageWidth;
        }
        
        if (text != null && text.length() != 0) {
            graphics.drawString(text, 
                    x+horizontalMargin, 
                    y+graphics.getFontAscent()+textVerticalOffset,
                    ViewportGraphics.ALIGN_LEFT, 
                    ViewportGraphics.ALIGN_LEFT);
        }
    }

    private FeatureTypeStyle[] locateStyle( ILayer layer ) {
        StyleBlackboard blackboard = (StyleBlackboard) layer.getStyleBlackboard();
        if (blackboard == null) {
            return null;
        }
        
        Style sld = (Style) blackboard.lookup( Style.class );
        if (sld == null) {
            return null;
        }

        List<FeatureTypeStyle> styles = new ArrayList<FeatureTypeStyle>();
        for (FeatureTypeStyle style : sld.getFeatureTypeStyles()) {
            if (style.getFeatureTypeName() == null || style.getFeatureTypeName().equals(SLDs.GENERIC_FEATURE_TYPENAME)) { 
                styles.add(style);
            } else { 
                if (layer.getSchema() != null && layer.getSchema().getTypeName() != null) {
                    if (layer.getSchema().getTypeName().equals(style.getFeatureTypeName())) {
                        //Direct match!
                        styles.add(style);
                    }
                }
            }
        }
        return styles.toArray(new FeatureTypeStyle[0]);
    }

    private void drawOutline(ViewportGraphics graphics, MapGraphicContext context, Rectangle locationStyle) {
        Rectangle outline = new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width, locationStyle.height);

        // reserve this area free of labels!
        context.getLabelPainter().put( outline );

        graphics.setColor(backgroundColour);
        graphics.fill(outline);

        graphics.setColor(foregroundColour);
        graphics.setBackground(backgroundColour);
        graphics.draw(outline);
    }
}
