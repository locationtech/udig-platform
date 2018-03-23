/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.internal;

/**
 * Enumeration of possible vendor options.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public enum VendorOptions {
    VENDOROPTION_MAXDISPLACEMENT("maxDisplacement", Messages.VendorOptions_1), // //$NON-NLS-1$
    VENDOROPTION_AUTOWRAP("autoWrap", Messages.VendorOptions_3), // //$NON-NLS-1$
    VENDOROPTION_SPACEAROUND("spaceAround", Messages.VendorOptions_5), // //$NON-NLS-1$
    VENDOROPTION_REPEAT("repeat", Messages.VendorOptions_7), // //$NON-NLS-1$
    VENDOROPTION_MAXANGLEDELTA("maxAngleDelta", Messages.VendorOptions_9), // //$NON-NLS-1$
    VENDOROPTION_FOLLOWLINE("followLine", Messages.VendorOptions_11), // //$NON-NLS-1$
    VENDOROPTION_OTHER("other", Messages.VendorOptions_13); //$NON-NLS-1$

    private String defString = null;
    private String guiString = null;
    VendorOptions( String defString, String guiString ) {
        this.defString = defString;
        this.guiString = guiString;
    }

    /**
     * Return the vendoroption based on the definition string.
     * 
     * @param defString the option definition string.
     * @return the {@link VendorOptions} or null.
     */
    public static VendorOptions toVendorOption( String defString ) {
        VendorOptions[] values = values();
        for( VendorOptions vendorOptions : values ) {
            if (defString.equals(vendorOptions.toString())) {
                return vendorOptions;
            }
        }
        return VendorOptions.VENDOROPTION_OTHER;
    }

    /**
     * Return the vendoroption based on the gui string.
     * 
     * @param guiString the option gui string.
     * @return the {@link VendorOptions} or null.
     */
    public static VendorOptions guiStringToVendorOption( String guiString ) {
        VendorOptions[] values = values();
        for( VendorOptions vendorOptions : values ) {
            if (guiString.equals(vendorOptions.toGuiString())) {
                return vendorOptions;
            }
        }
        return null;
    }

    public String toString() {
        return defString;
    }

    public String toGuiString() {
        return guiString;
    }
}
