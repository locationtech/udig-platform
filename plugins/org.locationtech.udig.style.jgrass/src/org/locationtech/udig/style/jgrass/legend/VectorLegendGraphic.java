/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.internal.MapGraphicResource;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.mapgraphic.style.FontStyleContent;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.SLDs;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;

/**
 * Draw a legend based on looking at the current list layer list.
 * 
 * Based on uDig's legend.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class VectorLegendGraphic implements MapGraphic {

    private int verticalMargin; // distance between border and icons/text
    private int horizontalMargin; // distance between border and icons/text
    private int verticalSpacing; // distance between layers
    private int horizontalSpacing; // space between image and text
    private Color foregroundColour;
    private Color backgroundColour;
    private Color fontColour;
    private boolean isRounded = false;
    private int indentSize;
    private int boxWidth;
    private int boxHeight; // size of image
    private int maxHeight;
    private int maxWidth;
    private Display display;

    private static Java2dDrawing java2dDrawing = Java2dDrawing.create();

    public void draw( MapGraphicContext context ) {
        IMap activeMap = ApplicationGIS.getActiveMap();
        IMap currentMap = context.getLayer().getMap();
        if (!activeMap.equals(currentMap)) {
            return;
        }

        IBlackboard blackboard = context.getLayer().getStyleBlackboard();
        VectorLegendStyle legendStyle = (VectorLegendStyle) blackboard.get(VectorLegendStyleContent.ID);
        if (legendStyle == null) {
            legendStyle = VectorLegendStyleContent.createDefault();
            blackboard.put(VectorLegendStyleContent.ID, legendStyle);
        }

        Rectangle locationStyle = null;
        // (Rectangle) blackboard.get(LocationStyleContent.ID);
        if (locationStyle == null) {
            locationStyle = new Rectangle(-1, -1, -1, -1);
            blackboard.put(LocationStyleContent.ID, locationStyle);
        }

        FontStyle fontStyle = (FontStyle) blackboard.get(FontStyleContent.ID);
        if (fontStyle == null) {
            fontStyle = new FontStyle();
            blackboard.put(FontStyleContent.ID, fontStyle);
        }

        this.backgroundColour = legendStyle.backgroundColor;
        this.foregroundColour = legendStyle.foregroundColor;
        this.fontColour = legendStyle.fontColor;
        this.isRounded = legendStyle.isRoundedRectangle;
        this.horizontalMargin = 5;
        this.verticalMargin = 5;
        this.horizontalSpacing = 3;
        this.verticalSpacing = 3;
        this.indentSize = 5;
        this.boxHeight = legendStyle.boxWidth;
        this.boxWidth = legendStyle.boxWidth;

        this.maxHeight = legendStyle.legendHeight;
        this.maxWidth = legendStyle.legendWidth;

        locationStyle.x = legendStyle.xPos;
        locationStyle.y = legendStyle.yPos;

        final ViewportGraphics graphics = context.getGraphics();
        GC gc = graphics.getGraphics(GC.class);
        if (gc != null) {
            gc.setAntialias(SWT.ON);
        }

        final int rowHeight = Math.max(boxHeight, graphics.getFontHeight()); // space allocated to
        // each layer
        Font oldFont = fontStyle.getFont();

        int fontHeight = rowHeight < 12 ? 8 : rowHeight - 8;
        Font font = new Font(oldFont.getName(), fontStyle.getFont().getStyle(), fontHeight);
        if (font != null) {
            graphics.setFont(font);
        } else {
            graphics.setFont(fontStyle.getFont());
        }

        List<Map<ILayer, FeatureTypeStyle[]>> layers = new ArrayList<Map<ILayer, FeatureTypeStyle[]>>();

        int longestRow = 0; // used to calculate the width of the graphic
        final int[] numberOfEntries = new int[1]; // total number of entries to draw
        numberOfEntries[0] = 0;
        /*
         * Set up the layers that we want to draw so we can operate just on
         * those ones. Layers at index 0 are on the bottom of the map, so we 
         * must iterate in reverse.
         * 
         * While we are doing this, determine the longest row so we can properly
         * draw the graphic's border.
         */
        for( int i = context.getMapLayers().size() - 1; i >= 0; i-- ) {
            ILayer layer = context.getMapLayers().get(i);
            IGeoResource geoResource = layer.getGeoResource();
            boolean isMapgraphic = geoResource.canResolve(MapGraphicResource.class);
            if (!isMapgraphic && layer.isVisible()) {

                // String layerName = LayerGeneratedGlyphDecorator.generateLabel((Layer) layer);
                String layerName = layer.getName();
                if (layerName != null && layerName.length() != 0) {

                    FeatureTypeStyle[] styles = locateStyle(layer);

                    if (styles != null && rules(styles).size() > 0) {
                        numberOfEntries[0] += rules(styles).size();

                        List<Rule> rules = rules(styles);
                        for( Rule rule : rules ) {
                            String text = getText(rule);
                            Rectangle2D bounds = graphics.getStringBounds(text);
                            int length = indentSize + boxWidth + horizontalSpacing
                                    + (int) bounds.getWidth();

                            if (length > longestRow) {
                                longestRow = length;
                            }
                        }
                    } else if (!layer.hasResource(MapGraphic.class)) {
                        // TODO for other layer types
                        continue;
                    } else {
                        continue;
                    }

                    Map<ILayer, FeatureTypeStyle[]> map = Collections.singletonMap(layer, styles);
                    layers.add(map);
                    if (styles != null && rules(styles).size() > 1) {
                        numberOfEntries[0]++; // add a line for the layer label
                    }
                    Rectangle2D bounds = graphics.getStringBounds(layerName);
                    int length = (int) bounds.getWidth();
                    if (styles != null && rules(styles).size() < 2) {
                        length += boxWidth + horizontalSpacing;
                    }

                    if (length > longestRow) {
                        longestRow = length;
                    }
                }
            }
        }

        if (numberOfEntries[0] == 0) {
            // nothing to draw!
            return;
        }

        // total width of the graphic
        int width = longestRow + horizontalMargin * 2;
        if (maxWidth > 0) {
            if (maxWidth > width) {
                width = maxWidth;
            }
            // width = Math.min(width, maxWidth);
        }
        // total height of the graphic
        int height = rowHeight * numberOfEntries[0] + verticalMargin * 2 + verticalSpacing
                * (numberOfEntries[0] - 1);
        if (maxHeight > 0) {
            if (maxHeight > height) {
                height = maxHeight;
            }
            // height = Math.min(height, maxHeight);
        }

        if (locationStyle.width < 1 || locationStyle.getHeight() < 1) {
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
        // ensure box within the display
        Dimension displaySize = context.getMapDisplay().getDisplaySize();
        if (locationStyle.x < 0) {
            locationStyle.x = displaySize.width - locationStyle.width + locationStyle.x;
        }
        if ((locationStyle.x + locationStyle.width + 6) > displaySize.width) {
            locationStyle.x = displaySize.width - width - 5;
        }

        if (locationStyle.y < 0) {
            locationStyle.y = displaySize.height - locationStyle.height - 5 + locationStyle.y;
        }
        if ((locationStyle.y + height + 6) > displaySize.height) {
            locationStyle.y = displaySize.height - locationStyle.height - 5;
        }

        graphics.setClip(new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width + 1,
                locationStyle.height + 1));

        /*
         * Draw the box containing the layers/icons
         */
        drawOutline(graphics, context, locationStyle);

        /*
         * Draw the layer names/icons
         */
        final int[] rowsDrawn = new int[1];
        rowsDrawn[0] = 0;
        final int[] x = new int[1];
        x[0] = locationStyle.x + horizontalMargin;
        final int[] y = new int[1];
        y[0] = locationStyle.y + verticalMargin;

        for( int i = 0; i < layers.size(); i++ ) {
            Map<ILayer, FeatureTypeStyle[]> map = layers.get(i);
            final ILayer layer = map.keySet().iterator().next();
            final FeatureTypeStyle[] styles = map.values().iterator().next();

            final String layerName = layer.getName();

            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    if (styles != null && rules(styles).size() > 1) {
                        drawRow(graphics, x[0], y[0], null, layerName, false);

                        y[0] += rowHeight;
                        if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                            y[0] += verticalSpacing;
                        }
                        rowsDrawn[0]++;
                        List<Rule> rules = rules(styles);
                        for( Rule rule : rules ) {

                            BufferedImage awtIcon = null;
                            if (layer.hasResource(FeatureSource.class) && rule != null) {
                                SimpleFeatureType type = layer.getSchema();
                                GeometryDescriptor geom = type.getGeometryDescriptor();
                                if (geom != null) {
                                    Class geom_type = geom.getType().getBinding();
                                    if (geom_type == Point.class || geom_type == MultiPoint.class) {
                                        awtIcon = point(rule, boxWidth, boxHeight);
                                    } else if (geom_type == LineString.class
                                            || geom_type == MultiLineString.class) {
                                        awtIcon = line(rule, boxWidth, boxHeight);
                                    } else if (geom_type == Polygon.class
                                            || geom_type == MultiPolygon.class) {
                                        awtIcon = polygon(rule, boxWidth, boxHeight);
                                    } else if (geom_type == Geometry.class
                                            || geom_type == GeometryCollection.class) {
                                        awtIcon = geometry(rule, boxWidth, boxHeight);
                                    } else {
                                        continue;
                                    }
                                }
                            }
                            // swtIcon = LayerGeneratedGlyphDecorator.generateStyledIcon(layer,
                            // rule)
                            // .createImage();

                            drawRow(graphics, x[0], y[0], awtIcon, getText(rule), true);

                            y[0] += rowHeight;
                            if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                                y[0] += verticalSpacing;
                            }
                            rowsDrawn[0]++;
                        }
                    } else {
                        BufferedImage awtIcon = generateIcon((Layer) layer, boxWidth, boxHeight);

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
        // clear the clip so we don't affect other rendering processes
        graphics.setClip(null);
    }

    private List<Rule> rules( FeatureTypeStyle[] styles ) {
        List<Rule> rules = new ArrayList<Rule>();
        for( FeatureTypeStyle featureTypeStyle : styles ) {
        	rules.addAll(featureTypeStyle.rules());
        }

        return rules;
    }

    private String getText( Rule rule ) {
        String text = ""; //$NON-NLS-1$
        String title = rule.getDescription().getTitle().toString();
        if (title != null && !"".equals(title)) { //$NON-NLS-1$
            text = title;
        } else if (rule.getName() != null && !"".equals(rule.getName())) { //$NON-NLS-1$
            text = rule.getName();
        } else if (rule.getFilter() != null) {
            text = rule.getFilter().toString();
        }

        if (text.length() > 19) {
            return text.substring(0, 18) + "..."; //$NON-NLS-1$
        } else {
            return text;
        }
    }

    private void drawRow( ViewportGraphics graphics, int x, int y, RenderedImage icon, String text,
            boolean indent ) {

        Rectangle2D stringBounds = graphics.getStringBounds(text);

        /*
         * Center the smaller item (text or icon) according to the taller one.
         */
        int textVerticalOffset = 0;
        int iconVerticalOffset = 0;
        if (boxHeight == (int) stringBounds.getHeight()) {
            // items are the same height; do nothing.
        } else if (boxHeight > (int) stringBounds.getHeight()) {
            int difference = boxHeight - (int) stringBounds.getHeight();
            textVerticalOffset = difference / 2;
        } else if (boxHeight < (int) stringBounds.getHeight()) {
            int difference = (int) stringBounds.getHeight() - boxHeight;
            iconVerticalOffset = difference / 2;
        }

        if (indent) {
            x += indentSize;
        }

        if (icon != null) {
            graphics.drawImage(icon, x, y + iconVerticalOffset);

            x += boxWidth;
        }

        if (text != null && text.length() != 0) {
            graphics.setColor(fontColour);
            graphics.drawString(text, x + horizontalMargin, y + graphics.getFontAscent()
                    + textVerticalOffset, ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
        }
    }

    private FeatureTypeStyle[] locateStyle( ILayer layer ) {
        StyleBlackboard blackboard = (StyleBlackboard) layer.getStyleBlackboard();
        if (blackboard == null) {
            return null;
        }

        Style sld = (Style) blackboard.lookup(Style.class);
        if (sld == null) {
            return null;
        }

        List<FeatureTypeStyle> styles = new ArrayList<FeatureTypeStyle>();
        for( FeatureTypeStyle style : sld.featureTypeStyles() ) {
        	
            if (style.featureTypeNames() == null) {
            	styles.add(style);
            }else {
            	boolean found = false;
            	for (Name n : style.featureTypeNames()) {
            		if (n.getLocalPart().equals(SLDs.GENERIC_FEATURE_TYPENAME)) {
            			found = true;
            		}
            	}
            	if (found) {
            		styles.add(style);
            	}else {
            		if (layer.getSchema() != null && layer.getSchema().getTypeName() != null) {
            			found = false;
            			for (Name n : style.featureTypeNames()) {
                    		if (n.getLocalPart().equals(layer.getSchema().getTypeName())) {
                                // Direct match!
                    			found = true;
                    		}
                    	}
                        if (found) styles.add(style);
                    }
            	}
            }
               
        }
        return styles.toArray(new FeatureTypeStyle[0]);
    }

    private void drawOutline( ViewportGraphics graphics, MapGraphicContext context,
            Rectangle locationStyle ) {
        Rectangle outline = new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width,
                locationStyle.height);

        // reserve this area free of labels!
        context.getLabelPainter().put(outline);

        // graphics.setColor(backgroundColour);
        // graphics.fill(outline);
        //
        // graphics.setColor(foregroundColour);
        // graphics.setBackground(backgroundColour);
        // graphics.draw(outline);

        if (isRounded) {
            graphics.setColor(backgroundColour);
            graphics.fillRoundRect(locationStyle.x, locationStyle.y, locationStyle.width,
                    locationStyle.height, 15, 15);
            graphics.setColor(foregroundColour);
            graphics.setBackground(backgroundColour);
            graphics.drawRoundRect(locationStyle.x, locationStyle.y, locationStyle.width,
                    locationStyle.height, 15, 15);
        } else {
            graphics.setColor(backgroundColour);
            graphics.fillRect(locationStyle.x, locationStyle.y, locationStyle.width,
                    locationStyle.height);
            graphics.setColor(foregroundColour);
            graphics.setBackground(backgroundColour);
            graphics.drawRect(locationStyle.x, locationStyle.y, locationStyle.width,
                    locationStyle.height);
        }
    }

    /**
     * Complex render of Geometry allowing presentation of point, line and polygon styles.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1 
     *  2
     *  3           L                 L                  
     *  4       p  L L           PPPPPP
     *  5         L   L     PPPPP   L p
     *  6        L     LPPPP       L  p
     *  7       L    PPPL         L   p
     *  8      L   PP    L       L    p
     *  9     L   P       L     L     P
     * 10    L   P         L   L      P
     * 11   L   P           L L       P
     * 12  L   P             L        P
     * 13      p                      P
     * 14      PPPPPPPPPPPPPPPPPPPPPPPP    
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public BufferedImage geometry( final Rule rule, final int width, final int height ) {

        BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        java2dDrawing.drawDirect(bI, display, java2dDrawing.feature(java2dDrawing
                .line(new int[]{scale(0), scale(12), scale(6), scale(3), scale(11), scale(12),
                        scale(15), scale(3)})), rule);
        java2dDrawing.drawDirect(bI, display, java2dDrawing.feature(java2dDrawing.point(scale(4),
                scale(4))), rule);

        return bI;

    }

    /**
     * Render a icon based on the current style.
     * <p>
     * Simple render of point in the center of the screen.
     * </p>
     * @param style
     * @return Icon representing style applyed to an image
     */
    public BufferedImage point( final Rule rule, final int width, final int height ) {
        BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        java2dDrawing.drawDirect(bI, display, java2dDrawing.feature(java2dDrawing.point(scale(7),
                scale(7))), rule);
        return bI;
    }

    /**
     * Complex render of Geometry allowing presentation of point, line and polygon styles.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1          LL                 L  
     *  2          L L                L
     *  3         L  L               L                   
     *  4        L    L             L  
     *  5        L     L            L  
     *  6       L      L           L   
     *  7      L        L         L    
     *  8      L         L        L    
     *  9     L          L       L     
     * 10    L            L     L      
     * 11    L             L    L      
     * 12   L              L   L       
     * 13  L                L L        
     * 14  L                 LL            
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public BufferedImage line( final Rule rule, final int width, final int height ) {
        BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] coords = new int[]{scale(1), scale(14), scale(6), scale(0), scale(11), scale(14),
                scale(15), scale(1)};
        final SimpleFeature feature = java2dDrawing.feature(java2dDrawing.line(coords));
        java2dDrawing.drawDirect(bI, display, feature, rule);
        return bI;
    }

    /**
     * Render of a polygon allowing style.
     * <p>
     * Layout:<pre><code>
     *    1 2 3 4 5 6 7 8 9101112131415
     *   0
     *  1             
     *  2                      PPPPPPPP
     *  3                PPPPPP       P                  
     *  4           PPPPPP            P
     *  5        PPP                  p
     *  6      PP                     p
     *  7     P                       p
     *  8    P                        p
     *  9   P                         P
     * 10   P                         P
     * 11  P                          P
     * 12  P                          P
     * 13  P                          P
     * 14  PPPPPPPPPPPPPPPPPPPPPPPPPPPP    
     * 15
     * </code><pre>
     * </p>
     * @param style 
     * @return Icon representing geometry style
     */
    public BufferedImage polygon( final Rule rule, final int width, final int height ) {
        BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] coords = new int[]{scale(1), scale(14), scale(3), scale(9), scale(4), scale(6),
                scale(6), scale(4), scale(9), scale(3), scale(14), scale(1), scale(14), scale(14)};
        java2dDrawing.drawDirect(bI, display, java2dDrawing.feature(java2dDrawing.polygon(coords)),
                rule);
        return bI;
    }

    /**
     * Genearte label and place in label.getProperties().getSTring( GENERATED_NAME ).
     * <p>
     * Label is genrated from Resource.
     * </p>
     * 
     * @return gernated layer
     */
    public BufferedImage generateIcon( Layer layer, int width, int height ) {
        StyleBlackboard style = layer.getStyleBlackboard();

        if (style != null && !style.getContent().isEmpty()) {
            BufferedImage icon = generateStyledIcon(layer);
            if (icon != null)
                return icon;
        }
        // ImageDescriptor icon = generateDefaultIcon(layer);
        // if (icon != null)
        // return icon;
        return null;
    }

    /**
     * Generate icon based on style information.
     * <p>
     * Will return null if an icom based on the current style could not be generated. You may
     * consult generateDefaultIcon( layer ) for a second opionion based on just the layer
     * information.
     * 
     * @param layer
     * @return ImageDecriptor for layer, or null in style could not be indicated
     */
    public BufferedImage generateStyledIcon( Layer layer ) {
        StyleBlackboard blackboard = layer.getStyleBlackboard();
        if (blackboard == null)
            return null;

        Style sld = (Style) blackboard.lookup(Style.class); // or
        // blackboard.get(
        // "org.locationtech.udig.style.sld"
        // );
        if (sld != null) {
            Rule rule = getRule(sld);

            BufferedImage swtIcon = null;
            if (layer.hasResource(FeatureSource.class) && rule != null) {
                SimpleFeatureType type = layer.getSchema();
                GeometryDescriptor geom = type.getGeometryDescriptor();
                if (geom != null) {
                    Class geom_type = geom.getType().getBinding();
                    if (geom_type == Point.class || geom_type == MultiPoint.class) {
                        swtIcon = point(rule, boxWidth, boxHeight);
                    } else if (geom_type == LineString.class || geom_type == MultiLineString.class) {
                        swtIcon = line(rule, boxWidth, boxHeight);
                    } else if (geom_type == Polygon.class || geom_type == MultiPolygon.class) {
                        swtIcon = polygon(rule, boxWidth, boxHeight);
                    } else if (geom_type == Geometry.class || geom_type == GeometryCollection.class) {
                        swtIcon = geometry(rule, boxWidth, boxHeight);
                    } else {
                        return null;
                    }
                }
            }

            return swtIcon;
        }
        return null;
    }

    private Rule getRule( Style sld ) {
        Rule rule = null;
        int size = 0;

        for( FeatureTypeStyle style : sld.featureTypeStyles() ) {
            for( Rule potentialRule : style.rules() ) {
                if (potentialRule != null) {
                    Symbolizer[] symbs = potentialRule.getSymbolizers();
                    for( int m = 0; m < symbs.length; m++ ) {
                        if (symbs[m] instanceof PointSymbolizer) {
                            int newSize = SLDs.pointSize((PointSymbolizer) symbs[m]);
                            if (newSize > 16 && size != 0) {
                                // return with previous rule
                                return rule;
                            }
                            size = newSize;
                            rule = potentialRule;
                        } else {
                            return potentialRule;
                        }
                    }
                }
            }
        }
        return rule;
    }

    private int scale( int value ) {
        float scaled = (float) (value * boxHeight) / 16f;
        return (int) Math.round(scaled);
    }
}
