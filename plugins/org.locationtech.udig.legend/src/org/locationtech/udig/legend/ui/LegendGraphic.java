/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.legend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.internal.MapGraphicResource;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.mapgraphic.style.FontStyleContent;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.ui.internal.LayerGeneratedGlyphDecorator;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.locationtech.udig.ui.graphics.SLDs;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.type.Name;

/**
 * Draw a legend based on looking at the current list layer list.
 *
 * @author Amr
 * @since 1.0.0
 */
public class LegendGraphic implements MapGraphic {

    private int verticalMargin; // distance between border and icons/text

    private int horizontalMargin; // distance between border and icons/text

    private int verticalSpacing; // distance between layers

    private int horizontalSpacing; // space between image and text

    private Color backgroundColour;

    private int indentSize;

    private int imageWidth;

    private int imageHeight; // size of glyph image

    @Override
    public void draw(MapGraphicContext context) {

        IBlackboard blackboard = context.getLayer().getStyleBlackboard();
        LegendStyle legendStyle = (LegendStyle) blackboard.get(LegendStyleContent.ID);
        if (legendStyle == null) {
            legendStyle = LegendStyleContent.createDefault();
            blackboard.put(LegendStyleContent.ID, legendStyle);
        }

        Rectangle locationStyle = (Rectangle) blackboard.get(LocationStyleContent.ID);
        if (locationStyle == null) {
            locationStyle = new Rectangle(-1, -1, -1, -1);
            blackboard.put(LocationStyleContent.ID, locationStyle);
        }

        FontStyle fontStyle = (FontStyle) blackboard.get(FontStyleContent.ID);
        if (fontStyle == null) {
            fontStyle = new FontStyle();
            blackboard.put(FontStyleContent.ID, fontStyle);
        }

        this.backgroundColour = legendStyle.backgroundColour;
        this.horizontalMargin = legendStyle.horizontalMargin;
        this.verticalMargin = legendStyle.verticalMargin;
        this.horizontalSpacing = legendStyle.horizontalSpacing;
        this.verticalSpacing = legendStyle.verticalSpacing;
        this.indentSize = legendStyle.indentSize;
        this.imageHeight = legendStyle.imageHeight;
        this.imageWidth = legendStyle.imageWidth;

        final ViewportGraphics graphics = context.getGraphics();

        if (fontStyle.getFont() != null) {
            graphics.setFont(fontStyle.getFont());
        }

        List<Map<ILayer, LegendEntry[]>> layers = new ArrayList<>();

        int longestRow = 0; // used to calculate the width of the graphic
        final int[] numberOfEntries = new int[1]; // total number of entries to
                                                  // draw
        numberOfEntries[0] = 0;

        /**
         * Set up the layers that we want to draw so we can operate just on those ones. Layers at
         * index 0 are on the bottom of the map, so we must iterate in reverse.
         *
         * While we are doing this, determine the longest row so we can properly draw the graphic's
         * border.
         */
        Dimension imageSize = new Dimension(imageWidth, imageHeight);
        Dimension textSize = new Dimension(0, graphics.getFontHeight());

        for (int i = context.getMapLayers().size() - 1; i >= 0; i--) {
            ILayer layer = context.getMapLayers().get(i);

            if (!(layer.getGeoResource() instanceof MapGraphicResource) && layer.isVisible()) {

                if (layer.hasResource(MapGraphic.class)) {
                    // don't include mapgraphics
                    continue;
                }
                String layerName = layer.getName();
                if (layerName == null) {
                    layerName = null;
                }
                LegendEntry layerEntry = new LegendEntry(layerName);

                FeatureTypeStyle[] styles = locateStyle(layer);
                LegendEntry[] entries = null;
                if (styles == null) {
                    // we should have a label but no style
                    entries = new LegendEntry[] { layerEntry };
                } else {
                    List<Rule> rules = rules(styles);
                    int ruleCount = rules.size();

                    if (ruleCount == 1 && layer.getGeoResource().canResolve(GridCoverage.class)) {
                        // grid coverage with single rule; lets see if it is a
                        // theming style
                        List<LegendEntry> cmEntries = ColorMapLegendCreator.findEntries(styles,
                                imageSize, textSize);
                        if (cmEntries != null) {
                            cmEntries.add(0, layerEntry); // add layer legend
                                                          // entry
                            entries = cmEntries.toArray(new LegendEntry[cmEntries.size()]);
                        }
                    }
                    if (entries == null) {
                        List<LegendEntry> localEntries = new ArrayList<>();
                        if (ruleCount == 1) {
                            // only one rule so apply this to the layer legend
                            // entry
                            layerEntry.setRule(rules.get(0));
                        }
                        localEntries.add(layerEntry); // add layer legend entry

                        if (ruleCount > 1) {
                            // we have more than one rule so there is likely
                            // some
                            // themeing going on; add each of these rules
                            for (Rule rule : rules) {
                                LegendEntry rentry = new LegendEntry(rule);
                                localEntries.add(rentry);
                            }
                        }
                        entries = localEntries.toArray(new LegendEntry[localEntries.size()]);
                    }
                }
                layers.add(Collections.singletonMap(layer, entries));

                // compute maximum length for each entry
                for (int j = 0; j < entries.length; j++) {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0; k < entries[j].getText().length; k++) {
                        sb.append(entries[j].getText()[k]);
                    }
                    Rectangle2D bounds = graphics.getStringBounds(sb.toString());
                    int length = indentSize + imageWidth + horizontalSpacing
                            + (int) bounds.getWidth();

                    if (length > longestRow) {
                        longestRow = length;
                    }
                    numberOfEntries[0]++;
                }
            }
        }

        if (numberOfEntries[0] == 0) {
            // nothing to draw!
            return;
        }

        final int rowHeight = Math.max(imageHeight, graphics.getFontHeight()); // space
                                                                               // allocated
                                                                               // to
                                                                               // each
                                                                               // layer

        if (locationStyle.width == 0 || locationStyle.height == 0) {
            // we want to change the location style as needed
            // but not change the saved one so we create a copy here
            locationStyle = new Rectangle(locationStyle);
            if (locationStyle.width == 0) {
                // we want to grow to whatever size we need
                int width = longestRow + horizontalMargin * 2;
                locationStyle.width = width;
            }
            if (locationStyle.height == 0) {
                // we want to grow to whatever size we need
                int height = rowHeight * numberOfEntries[0] + verticalMargin * 2;
                for (int i = 0; i < layers.size(); i++) {
                    Map<ILayer, LegendEntry[]> map = layers.get(i);
                    final LegendEntry[] entries = map.values().iterator().next();
                    for (int j = 0; j < entries.length; j++) {
                        if (entries[j].getSpacingAfter() == null) {
                            height += verticalSpacing;
                        } else {
                            height += entries[j].getSpacingAfter();
                        }
                    }
                }
                locationStyle.height = height - verticalSpacing;
            }
        }

        // ensure box within the display
        Dimension displaySize = context.getMapDisplay().getDisplaySize();
        if (locationStyle.x < 0) {
            locationStyle.x = displaySize.width - locationStyle.width + locationStyle.x;
        }
        if ((locationStyle.x + locationStyle.width + 6) > displaySize.width) {
            locationStyle.x = displaySize.width - locationStyle.width - 5;
        }

        if (locationStyle.y < 0) {
            locationStyle.y = displaySize.height - locationStyle.height - 5 + locationStyle.y;
        }
        if ((locationStyle.y + locationStyle.height + 6) > displaySize.height) {
            locationStyle.y = displaySize.height - locationStyle.height - 5;
        }

        graphics.setClip(new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width + 1,
                locationStyle.height + 1));

        /**
         * Draw the box containing the layers/icons
         */
        drawOutline(graphics, context, locationStyle, fontStyle);

        /**
         * Draw the layer names/icons
         */
        final int[] rowsDrawn = new int[1];
        rowsDrawn[0] = 0;
        final int[] x = new int[1];
        x[0] = locationStyle.x + horizontalMargin;
        final int[] y = new int[1];
        y[0] = locationStyle.y + verticalMargin;

        if (fontStyle.getFont() != null) {
            graphics.setFont(fontStyle.getFont());
        }

        for (int i = 0; i < layers.size(); i++) {
            Map<ILayer, LegendEntry[]> map = layers.get(i);
            final ILayer layer = map.keySet().iterator().next();
            final LegendEntry[] entries = map.values().iterator().next();

            try {
                layer.getGeoResources().get(0).getInfo(null);
            } catch (Exception ex) {
            }

            PlatformGIS.syncInDisplayThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < entries.length; i++) {
                        BufferedImage awtIcon = null;
                        if (entries[i].getRule() != null) {
                            // generate icon from use
                            ImageDescriptor descriptor = LayerGeneratedGlyphDecorator
                                    .generateStyledIcon(layer, entries[i].getRule());
                            if (descriptor == null) {
                                descriptor = LayerGeneratedGlyphDecorator
                                        .generateIcon((Layer) layer);
                            }
                            if (descriptor != null) {
                                awtIcon = AWTSWTImageUtils
                                        .convertToAWT(descriptor.getImageData(100));
                            }
                        } else if (entries[i].getIcon() != null) {
                            // use set icon
                            awtIcon = AWTSWTImageUtils
                                    .convertToAWT(entries[i].getIcon().getImageData(100));
                        } else {
                            // no rule, no icon, try default for layer
                            ImageDescriptor descriptor = LayerGeneratedGlyphDecorator
                                    .generateIcon((Layer) layer);
                            if (descriptor != null) {
                                awtIcon = AWTSWTImageUtils
                                        .convertToAWT(descriptor.getImageData(100));
                            }
                        }
                        drawRow(graphics, x[0], y[0], awtIcon, entries[i].getText(), i != 0,
                                entries[i].getTextPosition());

                        y[0] += rowHeight;
                        if ((rowsDrawn[0] + 1) < numberOfEntries[0]) {
                            if (entries[i].getSpacingAfter() != null) {
                                y[0] += entries[i].getSpacingAfter();
                            } else {
                                y[0] += verticalSpacing;
                            }
                        }
                        rowsDrawn[0]++;
                    }

                }
            });
        }
        // clear the clip so we don't affect other rendering processes
        graphics.setClip(null);
    }

    private List<Rule> rules(FeatureTypeStyle[] styles) {
        List<Rule> rules = new ArrayList<>();
        for (FeatureTypeStyle featureTypeStyle : styles) {
            rules.addAll(featureTypeStyle.rules());
        }
        return rules;
    }

    private void drawRow(ViewportGraphics graphics, int x, int y, RenderedImage icon, String[] text,
            boolean indent, int position) {

        if (text.length == 0) {
            return;
        }
        Rectangle2D stringBounds = graphics.getStringBounds(text[0]);

        /**
         * Center the smaller item (text or icon) according to the taller one.
         */
        int textVerticalOffset = 0;
        int iconVerticalOffset = 0;

        if ((position | SWT.CENTER) == position) {
            if (imageHeight == (int) stringBounds.getHeight()) {
                // items are the same height; do nothing.
            } else if (imageHeight > (int) stringBounds.getHeight()) {
                int difference = imageHeight - (int) stringBounds.getHeight();
                textVerticalOffset = difference / 2;
            } else if (imageHeight < (int) stringBounds.getHeight()) {
                int difference = (int) stringBounds.getHeight() - imageHeight;
                iconVerticalOffset = difference / 2;
            }
        } else if ((position | SWT.TOP) == position) {
            // do nothing; position everything at top
            textVerticalOffset = (int) (graphics.getFontAscent() * -0.6);
        }

        if (indent) {
            x += indentSize;
        }

        if (icon != null) {
            graphics.drawImage(icon, x, y + iconVerticalOffset);
            x += imageWidth;
        }

        if (text != null && text[0].length() != 0) {
            graphics.drawString(text[0], x + horizontalMargin,
                    y + textVerticalOffset - (graphics.getFontHeight() - graphics.getFontAscent()),
                    ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_TOP);
        }

        if (text != null && text.length > 1) {
            // draw last label at bottom of range
            String end = text[text.length - 1];

            graphics.drawString(end, x + horizontalMargin,
                    y + imageHeight + (int) (graphics.getFontAscent() * 0.3),
                    ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_BOTTOM);
        }

    }

    private FeatureTypeStyle[] locateStyle(ILayer layer) {
        StyleBlackboard blackboard = (StyleBlackboard) layer.getStyleBlackboard();
        if (blackboard == null) {
            return null;
        }

        Style sld = (Style) blackboard.lookup(Style.class);
        if (sld == null) {
            return null;
        }

        List<FeatureTypeStyle> styles = new ArrayList<>();
        String layerTypeName = null;
        if (layer.getSchema() != null && layer.getSchema().getTypeName() != null) {
            layerTypeName = layer.getSchema().getTypeName();
        }
        for (FeatureTypeStyle style : sld.featureTypeStyles()) {
            Set<Name> names = style.featureTypeNames();
            if (names.isEmpty()) {
                styles.add(style);
            } else {
                for (Name name : names) {
                    if (name.getLocalPart().equals(SLDs.GENERIC_FEATURE_TYPENAME)
                            || (layerTypeName != null
                                    && layerTypeName.equals(name.getLocalPart()))) {
                        styles.add(style);
                        break;
                    }
                }
            }
        }
        return styles.toArray(new FeatureTypeStyle[0]);
    }

    private void drawOutline(ViewportGraphics graphics, MapGraphicContext context,
            Rectangle locationStyle, FontStyle fs) {
        Rectangle outline = new Rectangle(locationStyle.x, locationStyle.y, locationStyle.width,
                locationStyle.height);

        // reserve this area free of labels!
        context.getLabelPainter().put(outline);

        graphics.setColor(backgroundColour);
        graphics.fill(outline);

        graphics.setColor(fs.getColor());
        graphics.setBackground(backgroundColour);
        graphics.draw(outline);
    }
}
