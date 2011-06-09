/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.jgrass.legend;

import static java.lang.Math.round;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.core.color.JGrassColorTable;

import eu.udig.catalog.jgrass.activeregion.dialogs.JGRasterChooserDialog;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;


/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RasterLegendGraphic implements MapGraphic {

    public final static String LEGEND_SEPERATOR = "|"; //$NON-NLS-1$

    private Thread run;

    private Display display;

    private Thread run2;

    private static Shell shell;

    private RasterLegendStyle legendStyle;

    public void draw( MapGraphicContext context ) {
        IMap activeMap = ApplicationGIS.getActiveMap();
        IMap currentMap = context.getLayer().getMap();
        if (!activeMap.equals(currentMap)) {
            return;
        }

        context.getLayer().setStatus(ILayer.WORKING);

        display = Display.getDefault();
        setShell();

        /*
         * get the blackboard from the map and not from the layer. This
         * is due to the fact that we need the mapreader here to get the 
         * legend string. The mapreader is put on blackboard by the 
         * raster renderer.
         */

        IBlackboard blackboard = context.getMap().getBlackboard();
        legendStyle = (RasterLegendStyle) blackboard.get(RasterLegendStyleContent.ID);
        if (legendStyle == null) {
            legendStyle = RasterLegendStyleContent.createDefault();
            blackboard.put(RasterLegendStyleContent.ID, legendStyle);
            // ((IBlackboard) blackboard).setSelected(new String[]{RasterLegendStyleContent.ID});
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

        /* Draw the legend. */
        if (categories.size() == 0 || categories.size() != colorRules.size()) {
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
            /*
             * calculate the boxheight from:
             * legendHeight = 1/2 bHeight + textHeight + 1/2 bHeight + rulseNum*bHeight + 1/2 bHeight
             */
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
                graphics.drawString(titleString, currentX,// +horizontalMargin,
                        currentY,// +textVerticalOffset,
                        ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
            }
            currentY = currentY + yInset;

            for( int i = 0; i < colorRules.size(); i++ ) {
                String rule = colorRules.get(i);

                double[] values = new double[2];
                Color[] colors = new Color[2];
                JGrassColorTable.parseColorRule(rule, values, colors);

                String firstValue = String.valueOf(values[0]);
                Color actualColor = colors[0];

                String secondValue = String.valueOf(values[1]);
                Color nextColor = colors[1];

                graphics.fillGradientRectangle(currentX, currentY, bWidth, bHeight, actualColor, nextColor, true);

                int tx = round(currentX + 1.5f * bWidth);
                graphics.setColor(legendStyle.fontColor);
                graphics.drawString(String.format("%-8.2f", Float.parseFloat(firstValue)), //$NON-NLS-1$
                        tx,// +horizontalMargin,
                        currentY, // - graphics.getFontAscent(),// +textVerticalOffset,
                        ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);

                currentY = currentY + bHeight;

                if (i == colorRules.size() - 1) {
                    // add also last text
                    tx = round(currentX + 1.5f * bWidth);
                    graphics.setColor(legendStyle.fontColor);
                    graphics.drawString(String.format("%-8.2f", Float.parseFloat(secondValue)), //$NON-NLS-1$
                            tx,// +horizontalMargin,
                            currentY, // - graphics.getFontAscent(),// +textVerticalOffset,
                            ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);
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
            /*
             * calculate the boxheight from:
             * legendHeight = 1/2 bHeight + textHeight + 1/2 bHeight + rulseNum*(bHeight + 1/3 bHeight) + 1/2 bHeight
             */
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
                graphics.drawString(titleString, currentX,// +horizontalMargin,
                        currentY,// +textVerticalOffset,
                        ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
            }
            currentY = currentY + yInset;

            for( int i = 0; i < colorRules.size(); i++ ) {
                String rule = colorRules.get(i);
                String cat = categories.get(i);

                int lastColon = cat.lastIndexOf(':');
                String attribute = cat.substring(lastColon + 1);

                double[] values = new double[2];
                Color[] colors = new Color[2];
                JGrassColorTable.parseColorRule(rule, values, colors);
                String firstValue = String.valueOf(values[0]);

                graphics.setColor(Color.black);
                graphics.drawRect(currentX, currentY, bWidth, bHeight);
                graphics.setColor(colors[0]);
                graphics.fillRect(currentX, currentY, bWidth, bHeight);

                int tx = round(currentX + 1.5f * bWidth);
                int tHeight = (int) round(graphics.getStringBounds(attribute).getHeight());
                graphics.setColor(legendStyle.fontColor);
                int ty = round(currentY - graphics.getFontAscent() + tHeight);// + tHeight -
                // bHeight/3f);
                graphics.drawString(attribute, tx,// +horizontalMargin,
                        ty, // - graphics.getFontAscent(),// +textVerticalOffset,
                        ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);

                currentY = round(currentY + 4f / 3f * bHeight);

            }

        }

        System.out.println();

        // if (colorRules.size() > 0) {
        //
        // int leftentries = (legendSplit.length - 1) / 2;
        // String[][] legend = new String[leftentries][2];
        //
        // int numberOfLegendEntries = 0;
        // int index = 1;
        // for( int i = 0; i < legend.length; i++ ) {
        //
        // String legendText = legendSplit[index].replace('_', ' ');
        //
        // if (isDiscrete) {
        // // draw discrete legend
        // legend[i][0] = legendText;
        // } else {
        // // draw a legend with range blending
        //                    if (!legendText.equals("")) { //$NON-NLS-1$
        //                        String[] intervalMaxMin = legendText.split(" to "); //$NON-NLS-1$
        // float intervalmin = Float.parseFloat(intervalMaxMin[0]);
        // float intervalmax = Float.parseFloat(intervalMaxMin[1]);
        // float delta = (intervalmax - intervalmin) / 6f;
        // legend[i - 3][0] = String.valueOf(intervalmin);
        // legend[i - 2][0] = String.valueOf(intervalmin + delta);
        // legend[i - 1][0] = String.valueOf(intervalmin + 2f * delta);
        // legend[i][0] = String.valueOf(intervalmin + 3f * delta);
        // legend[i + 1][0] = String.valueOf(intervalmin + 4f * delta);
        // legend[i + 2][0] = String.valueOf(intervalmin + 5f * delta);
        // legend[i + 3][0] = String.valueOf(intervalmax);
        // }
        // }
        // String legendColor = legendSplit[index + 1];
        // legend[i][1] = legendColor;
        // index = index + 2;
        // }
        //
        // // remove double entries and search for the longest string
        // LinkedHashMap<String, String> leg = new LinkedHashMap<String, String>(6);
        // int maxStringLength = 0;
        // for( int j = 0; j < legend.length; j++ ) {
        // leg.put(legend[j][0], legend[j][1]);
        //
        // double textWidth = graphics.getStringBounds(legend[j][0]).getWidth();
        // if (textWidth > maxStringLength) {
        // maxStringLength = (int) textWidth;
        // }
        // }
        // // check if the supplied title is longer
        // String titleString = legendStyle.titleString;
        // double textWidth = graphics.getStringBounds(titleString).getWidth();
        // if (maxStringLength < (int) textWidth)
        // maxStringLength = (int) textWidth;
        //
        // int textheight = (int) Math
        // .ceil(graphics.getStringBounds(titleString).getHeight() / 5.0);
        //
        // legend = new String[leg.size()][2];
        // numberOfLegendEntries = legend.length;
        // // Iterate over the keys in the map
        // Iterator<String> it = leg.keySet().iterator();
        // for( int i = 0; i < legend.length; i++ ) {
        // // Get key
        // legend[i][0] = it.next();
        // legend[i][1] = leg.get(legend[i][0]);
        // }
        //
        // /*
        // * finally start to draw
        // */
        // float bWidth = legendStyle.boxWidth;
        // // estimate min width (inset + boxwidth + space before test + longest text + inset)
        // float minwidth = bWidth / 4f + bWidth + 0.5f * bWidth + maxStringLength + bWidth / 4f;
        // if (legendStyle.legendWidth < minwidth) {
        // legendStyle.legendWidth = (int) minwidth;
        // }
        //
        // float w = legendStyle.legendWidth;
        // float h = legendStyle.legendHeight;
        // float x = legendStyle.xPos;
        // float y = legendStyle.yPos;
        // /*
        // * draw the box
        // */
        // if (legendStyle.isRoundedRectangle) {
        // graphics.setColor(legendStyle.backgroundColor);
        // graphics.fillRoundRect((int) x, (int) y, (int) w, (int) h, 15, 15);
        // graphics.setColor(legendStyle.foregroundColor);
        // graphics.setBackground(legendStyle.backgroundColor);
        // graphics.drawRoundRect((int) x, (int) y, (int) w, (int) h, 15, 15);
        // } else {
        // graphics.setColor(legendStyle.backgroundColor);
        // graphics.fillRect((int) x, (int) y, (int) w, (int) h);
        // graphics.setColor(legendStyle.foregroundColor);
        // graphics.setBackground(legendStyle.backgroundColor);
        // graphics.drawRect((int) x, (int) y, (int) w, (int) h);
        // }
        // /*
        // * discrete or not dependent parts
        // */
        // float strokewidth = 1f;
        // // upper and lower inset
        // float yinset = bWidth / 2f;
        // yinset = (float) Math.ceil(yinset);
        // if (isDiscrete) {
        // // the size of the box
        // float ybox = (h - 2f * strokewidth * numberOfLegendEntries - 2f * yinset)
        // / (numberOfLegendEntries * 6f / 5f - 1f / 5f - 0.75f - boxheightcorrection);
        // ybox = (float) Math.ceil(ybox);
        // // space between a box and the other
        // float yglue = ybox / 5f;
        // yglue = (float) Math.ceil(yglue);
        //
        // float currentX = x + yinset / 2f;
        // float currentY = y + yinset / 2f;
        //
        // // draw the title
        // float dx = currentX;// + 1.5f * bWidth;
        // float dy = currentY + ybox * 3f / 4f;
        // graphics.setColor(legendStyle.fontColor);
        // graphics.drawString(titleString, (int) dx,// +horizontalMargin,
        // (int) dy,// +textVerticalOffset,
        // ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
        // currentY = currentY + ybox + yglue;
        //
        // // draw the colorbox
        // for( int i = 0; i < legend.length; i++ ) {
        //
        // graphics.setColor(legendStyle.foregroundColor);
        // graphics.setStroke(ViewportGraphics.LINE_SOLID, (int) strokewidth);
        // graphics.drawRect((int) currentX, (int) currentY, (int) bWidth, (int) ybox);
        //
        // String attribute = legend[i][0];
        // graphics.setColor(JGrassUtilities.getColor(legend[i][1], Color.black));
        // graphics.fillRect((int) currentX + 2, (int) currentY + 2, (int) bWidth - 4,
        // (int) ybox - 4);
        //
        // float tx = currentX + 1.5f * bWidth;
        // float ty = currentY + ybox * 3f / 4f;
        //
        // graphics.setColor(legendStyle.fontColor);
        // graphics.drawString(attribute, (int) tx,// +horizontalMargin,
        // (int) ty,// +textVerticalOffset,
        // ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
        //
        // currentY = currentY + ybox + yglue;
        // }
        //
        // } else {
        // // GRADIENT LEGEND
        // // the size of the box
        // float ybox = (h - 2f * yinset)
        // / (numberOfLegendEntries + boxheightcorrection + textheight);
        //
        // ybox = (float) Math.floor(ybox);
        //
        // float currentX = x + yinset;
        // float currentY = y + 2f * yinset;
        // float startX = x + yinset;
        // float startY = y + yinset;
        //
        // // draw the title
        // graphics.setColor(legendStyle.fontColor);
        // graphics.drawString(titleString, (int) currentX,// +horizontalMargin,
        // (int) currentY,// +textVerticalOffset,
        // ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_LEFT);
        // currentY = currentY + yinset;
        //
        // for( int i = 0; i < legend.length; i++ ) {
        // String attribute = legend[i][0];
        // Color actualColor = JGrassUtilities.getColor(legend[i][1], Color.black);
        // Color nextColor = null;
        //
        // if (i != legend.length - 1) {
        // nextColor = JGrassUtilities.getColor(legend[i + 1][1], Color.black);
        //
        // graphics.fillGradientRectangle((int) currentX, (int) currentY,
        // (int) bWidth, (int) ybox, actualColor, nextColor, true);
        // } else {
        // // in the last just make it without gradient
        // nextColor = actualColor;
        // graphics.fillGradientRectangle((int) currentX, (int) currentY,
        // (int) bWidth, (int) ybox, actualColor, nextColor, true);
        // }
        //
        // if (i % 3 == 0) {
        // float tx = currentX + 1.5f * bWidth;
        // float ty = currentY + ybox * 3f / 4f;
        // graphics.setColor(legendStyle.fontColor);
        //                        graphics.drawString(Format.sprintf("%-8.2f", Float.parseFloat(attribute)), //$NON-NLS-1$
        // (int) tx,// +horizontalMargin,
        // (int) ty, // - graphics.getFontAscent(),// +textVerticalOffset,
        // ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);
        // }
        //
        // currentY = currentY + ybox;
        //
        // }
        // /*
        // * correct the box errors due to the rounding
        // */
        // float end = currentY + yinset;
        // float falseEnd = y + h;
        // // delete
        // Rectangle eraseRec = new Rectangle((int) x - 1, (int) end, (int) w + 1,
        // (int) (falseEnd - end));
        // graphics.setColor(new Color(0, 0, 0));
        // graphics.setClip(eraseRec);
        //
        // // draw the colorbox
        // graphics.setColor(legendStyle.foregroundColor);
        // graphics.drawRect((int) startX, (int) startY, (int) bWidth,
        // (int) ((numberOfLegendEntries - 0.5f) * ybox));
        //
        // }
        //
        // }

        context.getLayer().setStatus(ILayer.DONE);
    }

    private void getMapPath( final IBlackboard blackboard ) {
        run = new Thread(new Runnable(){
            public void run() {
                display.syncExec(new Runnable(){
                    public void run() {
                        JGRasterChooserDialog chooserDialog = new JGRasterChooserDialog(null);
                        chooserDialog.open(shell, SWT.SINGLE);
                        List<JGrassMapGeoResource> resources = chooserDialog.getSelectedResources();
                        if (resources != null && resources.size() > 0) {
                            JGrassMapGeoResource res = resources.get(0);
                            File mapFile = res.getMapFile();
                            legendStyle.mapPath = mapFile.getAbsolutePath();
                            blackboard.put(RasterLegendStyleContent.ID, legendStyle);
                            // ((StyleBlackboard) blackboard).setSelected(new
                            // String[]{RasterLegendStyleContent.ID});
                        }
                    }
                });
            }
        });
        run.start();

        while( run.isAlive() ) {
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

        run2 = new Thread(new Runnable(){
            public void run() {
                display.syncExec(new Runnable(){
                    public void run() {
                        shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                    }
                });
            }
        });
        run2.start();
        while( run2.isAlive() ) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
