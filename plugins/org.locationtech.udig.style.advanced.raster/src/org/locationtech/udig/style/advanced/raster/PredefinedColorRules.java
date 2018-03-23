/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The class reading default colortables from disk.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class PredefinedColorRules {

    /**
     * The {@link HashMap map} holding all predefined color rules.
     */
    private static HashMap<String, String[][]> colorRules = new HashMap<String, String[][]>();

    /**
     * The rainbow colormap is the only one that has to exist.
     */
    public final static String[][] rainbow = new String[][]{ //
    {"255", "255", "0"}, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {"0", "255", "0"}, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {"0", "255", "255"}, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {"0", "0", "255"}, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {"255", "0", "255"}, // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {"255", "0", "0"}}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * Reads and returns the {@link HashMap map} holding all predefined color rules.
     * 
     * <p>
     * The map has the name of the colortable as key and 
     * and array of Strings as value, representing the colors and values
     * of the colortable.<br>
     * The array can be of two types:<br>
     * <ul>
     *  <li>
     *      3 values per row (r, g, b): in that case the colortable
     *      will be interpolated over a supplied range and be kept 
     *      continuos between the values. On example is the elevation map.
     *  </li>
     *  <li>
     *      8 values per row (v1, r1, g1, b1, v2, r2, g2, b2): 
     *      in that case the values of every step is defined and the 
     *      color rules are used as they come. One example is the corine 
     *      landcover map, that has to be consistent with the given values
     *      and colors.
     *  </li>
     * </ul>
     * </p>
     *
     * @param doReset if true the folder of colortables is reread.
     * @return the map of colortables.
     */
    public static HashMap<String, String[][]> getColorsFolder( boolean doReset ) {
        int size = colorRules.size();
        if (!doReset && size > 0) {
            return colorRules;
        }

        /*
         * read the default colortables from the plugin folder
         */
        try {
            // create the rainbow colortable, which has to exist
            colorRules.put("rainbow", rainbow); //$NON-NLS-1$

            File colorTablesFolder = null;
            Bundle bundle = Platform.getBundle(RasterStylePlugin.PLUGIN_ID);
            if (bundle != null) {
                URL queriesUrl = bundle.getResource("colortables"); //$NON-NLS-1$
                String colorTablesFolderPath = FileLocator.toFileURL(queriesUrl).getPath();
                colorTablesFolder = new File(colorTablesFolderPath);
            }

            if (colorTablesFolder != null && colorTablesFolder.exists()) {
                File[] files = colorTablesFolder.listFiles();
                for( File file : files ) {
                    String name = file.getName();
                    if (name.toLowerCase().endsWith(".clr")) { //$NON-NLS-1$
                        BufferedReader bR = new BufferedReader(new FileReader(file));
                        List<String[]> lines = new ArrayList<String[]>();
                        String line = null;
                        int cols = 0;
                        while( (line = bR.readLine()) != null ) {
                            if (line.startsWith("#")) { //$NON-NLS-1$
                                continue;
                            }
                            String[] lineSplit = line.trim().split("\\s+"); //$NON-NLS-1$
                            cols = lineSplit.length;
                            lines.add(lineSplit);
                        }
                        bR.close();
                        String[][] linesArray = (String[][]) lines.toArray(new String[lines.size()][cols]);
                        String ruleName = FilenameUtils.getBaseName(file.getName());
                        ruleName = ruleName.replaceAll("\\_", " "); //$NON-NLS-1$ //$NON-NLS-2$
                        colorRules.put(ruleName, linesArray);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colorRules;
    }

    public static List<RuleValues> getRulesValuesList( String[][] colorRules, double[] dataRange ) throws IOException {
        List<RuleValues> list = new ArrayList<RuleValues>();
        if (colorRules[0].length == 3) {
            /*
             * the colorrules are without values, so we ramp through them
             * over the range. 
             */
            if (dataRange == null) {
                dataRange = new double[]{-100.0, 5000};
            }

            // calculate the color increment
            float rinc = (float) (dataRange[1] - dataRange[0]) / (float) (colorRules.length - 1);

            for( int i = 0; i < colorRules.length - 1; i++ ) {
                try {
                    double from = dataRange[0] + (i * rinc);
                    Color fromColor = new Color(Integer.parseInt(colorRules[i][0]), Integer.parseInt(colorRules[i][1]),
                            Integer.parseInt(colorRules[i][2]));
                    double to = dataRange[0] + ((i + 1) * rinc);
                    Color toColor = new Color(Integer.parseInt(colorRules[i + 1][0]), Integer.parseInt(colorRules[i + 1][1]),
                            Integer.parseInt(colorRules[i + 1][2]));
                    RuleValues rV = new RuleValues();
                    rV.fromValue = from;
                    rV.toValue = to;
                    rV.fromColor = fromColor;
                    rV.toColor = toColor;
                    list.add(rV);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
            }

            // rule.append((dataRange[1] - rinc) + ":");
            // rule.append(colorRules[colorRules.length - 2][0] + ":"
            // + colorRules[colorRules.length - 2][1] + ":"
            // + colorRules[colorRules.length - 2][2] + " ");
            // rule.append((dataRange[1]) + ":");
            // rule.append(colorRules[colorRules.length - 1][0] + ":"
            // + colorRules[colorRules.length - 1][1] + ":"
            // + colorRules[colorRules.length - 1][2] + "\n");

        } else {
            /*
             * in this case we have also the values for the range defined
             * and the color rule has to be "v1 r1 g1 b1 v2 r2 g2 b2". 
             */
            if (colorRules[0].length != 8) {
                throw new IOException("The colortable can have records of 3 or 8 columns. Check your colortables."); //$NON-NLS-1$
            }

            for( int i = 0; i < colorRules.length; i++ ) {
                try {
                    double from = Double.parseDouble(colorRules[i][0]);
                    Color fromColor = new Color(Integer.parseInt(colorRules[i][1]), Integer.parseInt(colorRules[i][2]),
                            Integer.parseInt(colorRules[i][3]));
                    double to = Double.parseDouble(colorRules[i][4]);
                    Color toColor = new Color(Integer.parseInt(colorRules[i][5]), Integer.parseInt(colorRules[i][6]),
                            Integer.parseInt(colorRules[i][7]));
                    RuleValues rV = new RuleValues();
                    rV.fromValue = from;
                    rV.toValue = to;
                    rV.fromColor = fromColor;
                    rV.toColor = toColor;
                    list.add(rV);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }
        return list;

    }

}
