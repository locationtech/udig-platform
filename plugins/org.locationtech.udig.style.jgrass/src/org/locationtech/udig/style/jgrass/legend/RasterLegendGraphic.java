/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.legend;

import static java.lang.Math.round;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.core.color.JGrassColorTable;
import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RasterLegendGraphic implements MapGraphic {

    public static final String LEGEND_SEPERATOR = "|"; //$NON-NLS-1$

    private Thread run;

    private Display display;

    private Thread run2;

    private static Shell shell;

    private RasterLegendStyle legendStyle;

    @Override
    public void draw(MapGraphicContext context) {
        IMap activeMap = ApplicationGIS.getActiveMap();
        IMap currentMap = context.getLayer().getMap();
        if (!activeMap.equals(currentMap)) {
            return;
        }

        context.getLayer().setStatus(ILayer.WORKING);

        display = Display.getDefault();
        setShell();

        /**
         * get the blackboard from the map and not from the layer. This is due to the fact that we
         * need the MapReader here to get the legend string. The MapReader is put on blackboard by
         * the raster renderer.
         */

        IBlackboard blackboard = context.getMap().getBlackboard();
        legendStyle = (RasterLegendStyle) blackboard.get(RasterLegendStyleContent.ID);
        if (legendStyle == null) {
            legendStyle = RasterLegendStyleContent.createDefault();
            blackboard.put(RasterLegendStyleContent.ID, legendStyle);
        }

        String mapPath = legendStyle.mapPath;
        if (mapPath == null) {
            getMapPath(blackboard);
            mapPath = legendStyle.mapPath;
        }

        if (mapPath == null) {
            return;
        }

        File cellFile = new File(mapPath);
        if (!cellFile.exists()) {
            context.getLayer().setStatus(ILayer.ERROR);
            return;
        }
        JGrassMapEnvironment jGrassMapEnvironment = new JGrassMapEnvironment(cellFile);
        List<String> categories = null;
        List<String> colorRules = null;
        try {
            categories = jGrassMapEnvironment.getCategories();
            colorRules = jGrassMapEnvironment.getColorRules(null);
        } catch (IOException e) {
            e.printStackTrace();
            context.getLayer().setStatus(ILayer.ERROR);
            return;
        }

        final ViewportGraphics graphics = context.getGraphics();

        /** Draw the legend. */
        if (categories.isEmpty() || categories.size() != colorRules.size()) {
            // draw a color ramp legend

            int rulesNum = colorRules.size();

            // get initially the text properties from the title
            String titleString = legendStyle.titleString;
            int textheight = (int) round(graphics.getStringBounds(titleString).getHeight());
            if (titleString.length() == 0)
                textheight = 0;

            int w = round(legendStyle.legendWidth);
            int h = round(legendStyle.legendHeight);
            int x = round(legendStyle.xPos);
            int y = round(legendStyle.yPos);

            int bWidth = round(legendStyle.boxWidth);
            int bHeight = round((h - textheight) / (3f / 2f + rulesNum));
            int yInset = bHeight / 2;
            int xInset = 15;

            int currentX = x + xInset;
            int currentY = y + yInset + textheight;
            if (textheight == 0) {
                currentY = y;
                h = h - yInset;
            }

            if (legendStyle.isRoundedRectangle) {
                graphics.setColor(legendStyle.backgroundColor);
                graphics.fillRoundRect(round(x), round(y), round(w), round(h), 15, 15);
                graphics.setColor(legendStyle.foregroundColor);
                graphics.setBackground(legendStyle.backgroundColor);
                graphics.drawRoundRect(round(x), round(y), round(w), round(h), 15, 15);
            } else {
                graphics.setColor(legendStyle.backgroundColor);
                graphics.fillRect(round(x), round(y), round(w), round(h));
                graphics.setColor(legendStyle.foregroundColor);
                graphics.setBackground(legendStyle.backgroundColor);
                graphics.drawRect(round(x), round(y), round(w), round(h));
            }

            // draw the title
            if (textheight != 0) {
                graphics.setColor(legendStyle.fontColor);
                graphics.drawString(titleString, currentX, currentY, ViewportGraphics.ALIGN_LEFT,
                        ViewportGraphics.ALIGN_LEFT);
            }
            currentY = currentY + yInset;

            for (int i = 0; i < colorRules.size(); i++) {
                String rule = colorRules.get(i);

                double[] values = new double[2];
                Color[] colors = new Color[2];
                JGrassColorTable.parseColorRule(rule, values, colors);

                String firstValue = String.valueOf(values[0]);
                Color actualColor = colors[0];

                String secondValue = String.valueOf(values[1]);
                Color nextColor = colors[1];

                graphics.fillGradientRectangle(currentX, currentY, bWidth, bHeight, actualColor,
                        nextColor, true);

                int tx = round(currentX + 1.5f * bWidth);
                graphics.setColor(legendStyle.fontColor);
                graphics.drawString(String.format("%-8.2f", Float.parseFloat(firstValue)), //$NON-NLS-1$
                        tx, currentY, ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);

                currentY = currentY + bHeight;

                if (i == colorRules.size() - 1) {
                    // add also last text
                    tx = round(currentX + 1.5f * bWidth);
                    graphics.setColor(legendStyle.fontColor);
                    graphics.drawString(String.format("%-8.2f", Float.parseFloat(secondValue)), //$NON-NLS-1$
                            tx, currentY, ViewportGraphics.ALIGN_LEFT,
                            ViewportGraphics.ALIGN_MIDDLE);
                }

            }

        } else {

            // draw a categories legend
            int rulesNum = colorRules.size();

            // get initially the text properties from the title
            String titleString = legendStyle.titleString;
            int textheight = (int) round(graphics.getStringBounds(titleString).getHeight());
            if (titleString.length() == 0)
                textheight = 0;

            int w = round(legendStyle.legendWidth);
            int h = round(legendStyle.legendHeight);
            int x = round(legendStyle.xPos);
            int y = round(legendStyle.yPos);

            int bWidth = round(legendStyle.boxWidth);
            int bHeight = round((h - textheight) / (3f / 2f + 4f / 3f * rulesNum));
            int yInset = bHeight / 2;
            int xInset = 15;

            int currentX = x + xInset;
            int currentY = y + yInset + textheight;
            if (textheight == 0) {
                currentY = y;
                h = (int) (h - yInset - 1f / 3f * bHeight);
            }

            if (legendStyle.isRoundedRectangle) {
                graphics.setColor(legendStyle.backgroundColor);
                graphics.fillRoundRect(round(x), round(y), round(w), round(h), 15, 15);
                graphics.setColor(legendStyle.foregroundColor);
                graphics.setBackground(legendStyle.backgroundColor);
                graphics.drawRoundRect(round(x), round(y), round(w), round(h), 15, 15);
            } else {
                graphics.setColor(legendStyle.backgroundColor);
                graphics.fillRect(round(x), round(y), round(w), round(h));
                graphics.setColor(legendStyle.foregroundColor);
                graphics.setBackground(legendStyle.backgroundColor);
                graphics.drawRect(round(x), round(y), round(w), round(h));
            }

            // draw the title
            if (textheight != 0) {
                graphics.setColor(legendStyle.fontColor);
                graphics.drawString(titleString, currentX, currentY, ViewportGraphics.ALIGN_LEFT,
                        ViewportGraphics.ALIGN_LEFT);
            }
            currentY = currentY + yInset;

            for (int i = 0; i < colorRules.size(); i++) {
                String rule = colorRules.get(i);
                String cat = categories.get(i);

                int lastColon = cat.lastIndexOf(':');
                String attribute = cat.substring(lastColon + 1);

                double[] values = new double[2];
                Color[] colors = new Color[2];
                JGrassColorTable.parseColorRule(rule, values, colors);

                graphics.setColor(Color.black);
                graphics.drawRect(currentX, currentY, bWidth, bHeight);
                graphics.setColor(colors[0]);
                graphics.fillRect(currentX, currentY, bWidth, bHeight);

                int tx = round(currentX + 1.5f * bWidth);
                int tHeight = (int) round(graphics.getStringBounds(attribute).getHeight());
                graphics.setColor(legendStyle.fontColor);
                int ty = round(currentY - graphics.getFontAscent() + tHeight);

                graphics.drawString(attribute, tx, ty, ViewportGraphics.ALIGN_LEFT,
                        ViewportGraphics.ALIGN_MIDDLE);

                currentY = round(currentY + 4f / 3f * bHeight);

            }

        }

        context.getLayer().setStatus(ILayer.DONE);
    }

    private void getMapPath(final IBlackboard blackboard) {
        run = new Thread(new Runnable() {
            @Override
            public void run() {
                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        JGRasterChooserDialog chooserDialog = new JGRasterChooserDialog(null);
                        chooserDialog.open(shell, SWT.SINGLE);
                        List<JGrassMapGeoResource> resources = chooserDialog.getSelectedResources();
                        if (resources != null && resources.size() > 0) {
                            JGrassMapGeoResource res = resources.get(0);
                            File mapFile = res.getMapFile();
                            legendStyle.mapPath = mapFile.getAbsolutePath();
                            blackboard.put(RasterLegendStyleContent.ID, legendStyle);
                        }
                    }
                });
            }
        });
        run.start();

        while (run.isAlive()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void setShell() {
        if (shell != null)
            return;

        run2 = new Thread(new Runnable() {
            @Override
            public void run() {
                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                    }
                });
            }
        });
        run2.start();
        while (run2.isAlive()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
