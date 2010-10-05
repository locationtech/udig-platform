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
package eu.udig.style.jgrass.core;

import static java.lang.Integer.parseInt;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.core.color.JlsTokenizer;

/**
 * <p>
 * Representation of a GRASS colortable for raster maps
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class GrassColorTable extends ColorTable {

    private File colrFile;

    private int alpha = 255;

    public GrassColorTable( String mapsetPath, String mapName, double[] dataRange )
            throws IOException {
        this(mapsetPath + File.separator + JGrassConstants.COLR + File.separator + mapName,
                dataRange);
    }

    /** Creates a new instance of ColorTable */
    public GrassColorTable( String colorFilePath, double[] dataRange ) throws IOException {
        colrFile = new File(colorFilePath);
        if (colrFile.exists()) {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    colrFile)));
            String line = rdr.readLine();
            if (line == null) {
                colorTableEmpty = true;
                rdr.close();
                if (colrFile.delete()) {
                    System.out.println("removed empty color file"); //$NON-NLS-1$
                }
                return;
            }
            line = line.trim();
            /*
             * Read first line and if it starts with # then it is a 3.x color map. If it starts with %
             * then it it a 4.x/5.x color map
             */
            if (line == null)
                return;
            if (line.charAt(0) == '%') {
                String[] stringValues = line.split("\\s+"); //$NON-NLS-1$
                if (stringValues.length == 4) {
                    try {
                        alpha = Integer.parseInt(stringValues[3]);
                    } catch (NumberFormatException e) {
                        alpha = 255;
                    }
                } else {
                    alpha = 255;
                }
                /* Read all the color rules */
                while( (line = rdr.readLine()) != null ) {
                    if (line.charAt(0) != '%')
                        processGrass4ColorRule(line.trim());
                }
            } else if (line.charAt(0) == '#') {
                int catNumber = Integer.parseInt(line.substring(1, 2));
                /* Read second line which is the background colour */
                processGrass3ColorRule(-1, rdr.readLine());
                /* Now read the rest of the lines */
                while( (line = rdr.readLine()) != null ) {
                    processGrass3ColorRule(catNumber++, line.trim());
                }
            } else {
                int catNumber = 0;
                /* Now read the rest of the lines */
                while( (line = rdr.readLine()) != null ) {
                    processGrass3ColorRule(catNumber++, line.trim());
                }
            }
            rdr.close();
            colorTableEmpty = false;
            // System.out.println("================================COLORTABLE================================");
            // for (int i=0; i<rules.size(); i++)
            // System.out.println("RULE="+rules.elementAt(i));
            // System.out.println("================================");
        } else {
            colorTableEmpty = true;
        }
    }

    /**
     * 
     */
    @SuppressWarnings("nls")
    private void processGrass3ColorRule( int lineNumber, String line ) {
        String r, g, b;

        if (line == null)
            return;
        try {
            JlsTokenizer tk = new JlsTokenizer(line, " ");
            if (lineNumber < 0) {
                if (tk.hasMoreTokens()) {
                    r = g = b = tk.nextToken();
                    if (tk.hasMoreTokens())
                        g = tk.nextToken();
                    if (tk.hasMoreTokens())
                        b = tk.nextToken();
                    if (r.indexOf('.') == -1)
                        setBackgroundColor(new Color(Integer.parseInt(r), Integer.parseInt(g),
                                Integer.parseInt(b)));
                    else
                        setBackgroundColor(new Color(Float.parseFloat(r), Float.parseFloat(g),
                                Float.parseFloat(b)));
                }
            } else {
                r = g = b = tk.nextToken();
                if (tk.hasMoreTokens())
                    g = tk.nextToken();
                if (tk.hasMoreTokens())
                    b = tk.nextToken();
                if (r.indexOf('.') == -1)
                    addColorRule(lineNumber, Integer.parseInt(r), Integer.parseInt(g), Integer
                            .parseInt(b));
                else
                    addColorRule(lineNumber, (int) (Float.parseFloat(r) * 255f), (int) (Float
                            .parseFloat(g) * 255f), (int) (Float.parseFloat(b) * 255));
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     */
    @SuppressWarnings("nls")
    private void processGrass4ColorRule( String line ) {
        int r0 = -1, g0 = -1, b0 = -1;
        int r1 = -1, g1 = -1, b1 = -1;
        float cat0 = 0, cat1 = 0;

        if (line == null)
            return;
        // System.out.println(">>"+line);

        try {
            JlsTokenizer tk = new JlsTokenizer(line, " ");
            if (tk.hasMoreTokens()) {
                /* Some lines may have two colors seperated by a space. */
                JlsTokenizer tk1 = new JlsTokenizer(tk.nextToken(), ":");
                cat0 = Float.parseFloat(tk1.nextToken());
                /* The next value(s) describe the color as R:G:B */
                r0 = g0 = b0 = Integer.parseInt(tk1.nextToken());
                if (tk1.hasMoreTokens())
                    g0 = b0 = Integer.parseInt(tk1.nextToken());
                if (tk1.hasMoreTokens())
                    b0 = Integer.parseInt(tk1.nextToken());
                if (tk.hasMoreTokens()) {
                    /* Some lines may have two colors seperated by a space. */
                    tk1 = new JlsTokenizer(tk.nextToken(), ":");
                    cat1 = Float.parseFloat(tk1.nextToken());
                    /* The next value(s) describe the color as R:G:B */
                    r1 = g1 = b1 = Integer.parseInt(tk1.nextToken());
                    if (tk1.hasMoreTokens())
                        g1 = b1 = Integer.parseInt(tk1.nextToken());
                    if (tk1.hasMoreTokens())
                        b1 = Integer.parseInt(tk1.nextToken());
                }
                /* Add colour rule if specified for a range */
                if (r1 == -1) {
                    addColorRule((int) cat0, r0, g0, b0);
                } else {
                    addColorRule(cat0, r0, g0, b0, cat1, r1, g1, b1);
                }
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * create the default color table for a given dataRange for the case in which no hardcoded color
     * table is found
     * 
     * @param dataRange the datarange for which the colortable is created
     * @throws IOException 
     */
    @SuppressWarnings("nls")
    public void createDefaultColorTable( double[] dataRange ) throws IOException {
        String[][] rainbow = PredefinedColorRules.rainbow;
        ArrayList<String> rules = new ArrayList<String>();

        // calculate the color increment
        float rinc = (float) (dataRange[1] - dataRange[0]) / 5;
        for( int i = 0; i < 4; i++ ) {
            addColorRule((float) (dataRange[0] + (i * rinc)), parseInt(rainbow[i][0]),
                    parseInt(rainbow[i][1]), parseInt(rainbow[i][2]),
                    (float) (dataRange[0] + ((i + 1) * rinc)), parseInt(rainbow[i + 1][0]),
                    parseInt(rainbow[i + 1][1]), parseInt(rainbow[i + 1][2]));

            StringBuffer rule = new StringBuffer();
            rule.append((dataRange[0] + (i * rinc)) + ":");
            rule.append(rainbow[i][0] + ":" + rainbow[i][1] + ":" + rainbow[i][2] + " ");
            rule.append((dataRange[0] + ((i + 1) * rinc)) + ":");
            rule
                    .append(rainbow[i + 1][0] + ":" + rainbow[i + 1][1] + ":" + rainbow[i + 1][2]
                            + " ");
            rules.add(rule.toString());
        }

        addColorRule((float) (dataRange[1] - rinc), parseInt(rainbow[4][0]),
                parseInt(rainbow[4][1]), parseInt(rainbow[4][2]), (float) (dataRange[1]),
                parseInt(rainbow[5][0]), parseInt(rainbow[5][1]), parseInt(rainbow[5][2]));
        StringBuffer rule = new StringBuffer();
        rule.append((dataRange[1] - rinc) + ":");
        rule.append(rainbow[4][0] + ":" + rainbow[4][1] + ":" + rainbow[4][2] + " ");
        rule.append((dataRange[1]) + ":");
        rule.append(rainbow[5][0] + ":" + rainbow[5][1] + ":" + rainbow[5][2] + " ");
        rules.add(rule.toString());

        if (!colrFile.exists()) {
            BufferedWriter bw = null;
            // check also existence of colr folder, it could be missing
            File colFolder = colrFile.getParentFile();
            if (!colFolder.exists()) {
                colFolder.mkdirs();
            }
            bw = new BufferedWriter(new FileWriter(colrFile));

            String header = "% " + dataRange[0] + "   " + dataRange[1] + "   255";
            bw.write(header + "\n");

            for( String string : rules ) {
                bw.write(string + "\n");
            }

            bw.close();

        }

    }

    /**
     * Sets the colortable from a predefined set of rules as defined in {@link PredefinedColorRules}.
     * 
     * @param colrFile the color file to persist to.
     * @param dataRange the datarange or null.
     * @param colorRules the colorrules to apply.
     * @return the string representation of the color table file in GRASS.
     * @throws IOException
     */
    @SuppressWarnings("nls")
    public static String setColorTableFromRules( File colrFile, double[] dataRange,
            String[][] colorRules ) throws IOException {
        String name = colrFile.getName();
        File mapsetFile = colrFile.getParentFile().getParentFile();
        JGrassMapEnvironment jGrassMapEnvironment = new JGrassMapEnvironment(mapsetFile, name);

        if (colorRules[0].length == 3) {
            /*
             * the colorrules are without values, so we ramp through them
             * over the range. 
             */
            if (dataRange == null) {
                // first try to read the range file
                dataRange = jGrassMapEnvironment.getRangeFromRangeFile();

                if (dataRange == null) {
                    // try to guess it from the color file
                    dataRange = jGrassMapEnvironment.getRangeFromColorTable();
                }

                if (dataRange == null) {
                    // something went wrong. read the map and get the range
                    dataRange = jGrassMapEnvironment.getRangeFromMapScan();
                }
            }

            // calculate the color increment
            float rinc = (float) (dataRange[1] - dataRange[0]) / (float) (colorRules.length - 1);

            StringBuffer rule = new StringBuffer();
            rule.append("% " + dataRange[0] + "   " + dataRange[1] + "   255\n");

            for( int i = 0; i < colorRules.length - 2; i++ ) {
                rule.append((dataRange[0] + (i * rinc)) + ":");
                rule.append(colorRules[i][0] + ":" + colorRules[i][1] + ":" + colorRules[i][2]
                        + " ");
                rule.append((dataRange[0] + ((i + 1) * rinc)) + ":");
                rule.append(colorRules[i + 1][0] + ":" + colorRules[i + 1][1] + ":"
                        + colorRules[i + 1][2] + "\n");
            }

            rule.append((dataRange[1] - rinc) + ":");
            rule.append(colorRules[colorRules.length - 2][0] + ":"
                    + colorRules[colorRules.length - 2][1] + ":"
                    + colorRules[colorRules.length - 2][2] + " ");
            rule.append((dataRange[1]) + ":");
            rule.append(colorRules[colorRules.length - 1][0] + ":"
                    + colorRules[colorRules.length - 1][1] + ":"
                    + colorRules[colorRules.length - 1][2] + "\n");

            BufferedWriter bw = new BufferedWriter(new FileWriter(colrFile));
            bw.write(rule.toString());
            bw.close();

            return rule.toString();
        } else {
            /*
             * in this case we have also the values for the range defined
             * and the color rule has to be "v1 r1 g1 b1 v2 r2 g2 b2". 
             */
            if (colorRules[0].length != 8) {
                throw new IOException(
                        "The colortable can have records of 3 or 8 columns. Check your colortables.");
            }

            StringBuffer rule = new StringBuffer();
            rule.append("% " + colorRules[0][0] + "   " + colorRules[colorRules.length - 1][0]
                    + "   255\n");

            for( int i = 0; i < colorRules.length; i++ ) {
                System.out.println(i);
                rule.append(colorRules[i][0] + ":");
                rule.append(colorRules[i][1] + ":" + colorRules[i][2] + ":" + colorRules[i][3]
                        + " ");
                rule.append(colorRules[i][4] + ":");
                rule.append(colorRules[i][5] + ":" + colorRules[i][6] + ":"
                        + colorRules[i][7] + "\n");
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(colrFile));
            bw.write(rule.toString());
            bw.close();

            return rule.toString();
        }
    }

    /**
     * Create a default colorrules of the string format of the colorfile of the map.
     * 
     * @param dataRange can be null
     * @return the string format of the colorfile of the map
     * @throws Exception 
     */
    @SuppressWarnings("nls")
    public String createDefaultColorRulesString( double[] dataRange, boolean writeToDisk )
            throws IOException {
        String[][] rainbow = PredefinedColorRules.rainbow;
        if (dataRange == null) {
            dataRange = new double[2];
            // need to read the datarange

            String name = colrFile.getName();
            File mapsetFile = colrFile.getParentFile().getParentFile();
            JGrassMapEnvironment jGrassMapEnvironment = new JGrassMapEnvironment(mapsetFile, name);

            // first try to read the range file
            File rangeFile = jGrassMapEnvironment.getCELLMISC_RANGE();
            boolean fileok = true;
            int testread = 0;
            if (rangeFile.exists()) {
                // the range file exists
                InputStream is = new FileInputStream(rangeFile);
                byte[] numbers = new byte[16];

                testread = is.read(numbers);
                is.close();

                if (testread == 16) {
                    // the range was read right (is this check enough? dont' know...)
                    ByteBuffer rangeBuffer = ByteBuffer.wrap(numbers);

                    dataRange[0] = rangeBuffer.getDouble();
                    dataRange[1] = rangeBuffer.getDouble();
                } else {
                    // something went wrong while reading the range
                    fileok = false;
                }
            } else {
                // range file does not exist
                fileok = false;
            }

            if (!fileok) {
                // something went wrong. set some default value. The user will be able to reset it

                double min = JGrassMapEnvironment.defaultMapMin;
                double max = JGrassMapEnvironment.defaultMapMax;
                dataRange[0] = min;
                dataRange[1] = max;
            }
        }

        // calculate the color increment
        float rinc = (float) (dataRange[1] - dataRange[0]) / 5;

        StringBuffer rule = new StringBuffer();
        rule.append("% " + dataRange[0] + "   " + dataRange[1] + "   255\n");

        for( int i = 0; i < 4; i++ ) {
            rule.append((dataRange[0] + (i * rinc)) + ":");
            rule.append(rainbow[i][0] + ":" + rainbow[i][1] + ":" + rainbow[i][2] + " ");
            rule.append((dataRange[0] + ((i + 1) * rinc)) + ":");
            rule.append(rainbow[i + 1][0] + ":" + rainbow[i + 1][1] + ":" + rainbow[i + 1][2]
                    + "\n");
        }

        rule.append((dataRange[1] - rinc) + ":");
        rule.append(rainbow[4][0] + ":" + rainbow[4][1] + ":" + rainbow[4][2] + " ");
        rule.append((dataRange[1]) + ":");
        rule.append(rainbow[5][0] + ":" + rainbow[5][1] + ":" + rainbow[5][2] + "\n");

        if (writeToDisk) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(colrFile));
            bw.write(rule.toString());
            bw.close();
        }

        return rule.toString();

    }

    public File getColrFile() {
        return colrFile;
    }

    public int getAlpha() {
        return alpha;
    }
}
