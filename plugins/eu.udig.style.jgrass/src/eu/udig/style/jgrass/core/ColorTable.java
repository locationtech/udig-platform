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

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Vector;

import org.geotools.gce.grassraster.core.color.ColorRule;

/**
 * <p>
 * A JGrass colortable.
 * </p>
 * <p>
 * The color table consists of a list of catagory intervals sorted by starting catagory
 * (cat0:RR:GG:BB - cat1:RR:GG:BB).
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public abstract class ColorTable {

    public static final byte[] blank = {0, 0, 0, 0};

    protected Color backgroundColor = new Color(0, 0, 0, 0);

    protected static final int FIXED_COLORMAP = 1;

    protected static final int GRADIENT_COLORMAP = 2;

    protected boolean colorTableEmpty = false;

    /*  */
    protected Vector<ColorRule> rules;

    /** Creates a new instance of ColorTable */
    public ColorTable() {
        rules = new Vector<ColorRule>();
    }

    public Enumeration<ColorRule> getColorRules() {
        return rules.elements();
    }

    /**
     * 
     */
    public int size() {
        return rules.size();
    }

    /**
     * 
     */
    public void setBackgroundColor( Color clr ) {
        backgroundColor = clr;
    }

    /**
     * 
     */
    public boolean isEmpty() {
        return colorTableEmpty;
    }

    /**
     * 
     */
    protected void addColorRule( int cat, int r, int g, int b ) {
        insertRule(cat, new ColorRule(cat, r, g, b));
    }

    /**
     * 
     */
    protected void addColorRule( float cat0, int r0, int g0, int b0, float cat1, int r1, int g1,
            int b1 ) {
        insertRule(cat0, new ColorRule(cat0, r0, g0, b0, cat1, r1, g1, b1));
    }

    /**
     * Create the buffer with the rbg colormap.
     * 
     * @param mapType - type of map, from which to understand the single value offset
     * @param data - the buffer with the data
     * @param dataOffset - offset to define rows
     * @return the colormap byte buffer
     */
    public ByteBuffer interpolateColorMap( int mapType, ByteBuffer data, int dataOffset ) {
        int dataLength = data.capacity() - dataOffset;
        /* Allocate colour map output buffer using 4 bytes per cell value (RGBA) */
        ByteBuffer cmapBuffer = ByteBuffer.allocate(dataLength);

        /* Set buffer position */
        data.position(dataOffset);

        if (mapType > 0) {
            while( data.hasRemaining() ) {
                /*
                 * For each value in the array get the index from the rules map and then translate
                 * the rule.
                 */
                int f = data.getInt();

                cmapBuffer.put(f == Integer.MAX_VALUE ? blank : getColor((float) f));
            }
        } else if (mapType == -1) {
            while( data.hasRemaining() ) {
                /*
                 * For each value in the array get the index from the rules map and then translate
                 * the rule.
                 */
                float f = data.getFloat();
                cmapBuffer.put(Float.isNaN(f) ? blank : getColor(f));
            }
        } else if (mapType == -2) {
            while( data.hasRemaining() ) {
                /*
                 * For each value in the array get the index from the rules map and then translate
                 * the rule.
                 */
                float f = (float) data.getDouble();
                cmapBuffer.put(Float.isNaN(f) ? blank : getColor(f));

                // if (!Float.isNaN(f) && f > 0)
                // System.out.println("COLOR-TABLE->" + f + " has color "
                // + (Float.isNaN(f) ? blank : getColor(f)));

                /*
                 * test the data map
                 */
                /*
                 * System.out.print(f + " "); if (counter == 1024) { System.out.println(); counter =
                 * 1; } else { counter++; }
                 */

            }
        }
        return cmapBuffer;
    }

    /**
     * Create the buffer with the rbg colormap.
     */
    public void interpolateColorValue( ByteBuffer cmapBuffer, int cell ) {
        cmapBuffer.put(cell == Integer.MAX_VALUE ? blank : getColor((float) cell));
    }

    /**
     * Create the buffer with the rbg colormap.
     */
    public void interpolateColorValue( ByteBuffer cmapBuffer, float cell ) {
        cmapBuffer.put(Float.isNaN(cell) ? blank : getColor(cell));
    }

    /**
     * Create the buffer with the rbg colormap.
     */
    public void interpolateColorValue( ByteBuffer cmapBuffer, double cell ) {
        cmapBuffer.put(Float.isNaN((float) cell) ? blank : getColor((float) cell));
    }

    /**
     * 
     */
    public void insertRule( float cat, ColorRule newrule ) {
        int i = 0;
        int low = 0;
        int high = rules.size() - 1;

        while( low <= high ) {
            i = (low + high) / 2;
            ColorRule crule = (ColorRule) rules.elementAt(i);
            int c = crule.compare(cat);
            // System.out.println("C="+c+", I="+i+", cat="+cat+",
            // CRULE="+crule);
            if (c == 0) {
                /*
                 * Attribute found with equal value so break and insert using this index.
                 */
                i++;
                low = high + 1;
            } else if (c < 0) {
                high = i - 1;
            } else {
                low = i++ + 1;
            }
        }
        rules.insertElementAt(newrule, i);
    }

    /**
     * 
     */
    private ColorRule get( float cat ) {
        int low = 0;
        int high = rules.size() - 1;

        while( low <= high ) {
            int i = (low + high) / 2;
            ColorRule crule = (ColorRule) rules.elementAt(i);
            int c = crule.compare(cat);
            // System.out.println("C="+c+", I="+i+", cat="+cat+",
            // CRULE="+crule);
            if (c == 0) {
                return crule;
            } else if (c < 0) {
                high = i - 1;
            } else {
                low = i++ + 1;
            }
        }
        // System.out.println("FOUND NOHING..................");
        return null;
    }

    /**
     * 
     */
    public byte[] getColor( float x ) {
        ColorRule crule = get(x);

        return crule == null ? blank : crule.getColor(x);
    }

    /**
     * Create a default colortable based on the range.
     * 
     * @param dataRange the range. If null, a hardcoded default value is used.
     * @throws IOException
     */
    public void createDefaultColorTable( double[] dataRange ) throws IOException {

    }

    public int getAlpha() {
        return 255;
    }

}
