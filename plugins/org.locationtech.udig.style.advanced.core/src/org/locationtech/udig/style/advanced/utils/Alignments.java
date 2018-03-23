/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.utils;

import org.locationtech.udig.ui.graphics.SLDs;

/**
 * Enumeration of possible vendor options.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public enum Alignments {
    TOP("top"), //
    MIDDLE("middle"), //
    BOTTOM("bottom"), //
    LEFT("left"), //
    CENTER("center"), //
    RIGHT("right");

    private String defString = null;
    Alignments( String defString ) {
        this.defString = defString;
    }

    /**
     * Return the alignment based on the definition string.
     * 
     * @param defString the aliognment definition string.
     * @return the {@link Alignments} or null.
     */
    public static Alignments toAlignment( String defString ) {
        Alignments[] values = values();
        for( Alignments vendorOptions : values ) {
            if (defString.equals(vendorOptions.toString())) {
                return vendorOptions;
            }
        }
        return null;
    }

    public static Alignments verticalAlignmentfromDouble( String alignment ) {
        if (alignment == null)
            return TOP;
        double align = Double.parseDouble(alignment);
        if (align == 0.0) {
            return TOP;
        } else if (Math.abs(align - 0.5) < 0.00001) {
            return MIDDLE;
        } else if (align == 1.0) {
            return BOTTOM;
        }
        return TOP;
    }

    public static Alignments horizontalAlignmentfromDouble( String alignment ) {
        if (alignment == null)
            return RIGHT;
        double align = Double.parseDouble(alignment);
        if (align == 0.0) {
            return RIGHT;
        } else if (Math.abs(align - 0.5) < 0.00001) {
            return CENTER;
        } else if (align == 1.0) {
            return LEFT;
        }
        return RIGHT;
    }

    public double toDouble() {
        switch( this ) {
        case TOP:
            return SLDs.ALIGN_TOP;
        case MIDDLE:
            return SLDs.ALIGN_MIDDLE;
        case BOTTOM:
            return SLDs.ALIGN_BOTTOM;
        case LEFT:
            return SLDs.ALIGN_LEFT;
        case CENTER:
            return SLDs.ALIGN_CENTER;
        case RIGHT:
            return SLDs.ALIGN_RIGHT;
        default:
            break;
        }
        return 0.5;
    }

    public int toIndex() {
        switch( this ) {
        case TOP:
            return 0;
        case MIDDLE:
            return 1;
        case BOTTOM:
            return 2;
        case RIGHT:
            return 0;
        case CENTER:
            return 1;
        case LEFT:
            return 2;
        default:
            break;
        }
        return 1;
    }

    public String toString() {
        return defString;
    }

    public static String[] toVerticalStrings() {
        String[] valuesArray = new String[3];
        valuesArray[0] = TOP.toString();
        valuesArray[1] = MIDDLE.toString();
        valuesArray[2] = BOTTOM.toString();
        return valuesArray;
    }

    public static String[] toHorizontalStrings() {
        String[] valuesArray = new String[3];
        valuesArray[0] = RIGHT.toString();
        valuesArray[1] = CENTER.toString();
        valuesArray[2] = LEFT.toString();
        return valuesArray;
    }
}
