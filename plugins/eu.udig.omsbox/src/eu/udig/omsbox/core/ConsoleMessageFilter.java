/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility to help filter out messages from the console.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ConsoleMessageFilter {

    private static List<String> containsStrings = new ArrayList<String>();
    private static List<String> endStrings = new ArrayList<String>();

    static {
        containsStrings.add("Kakadu");
        containsStrings.add("Error while parsing JAI registry");
        containsStrings.add("A descriptor is already registered");
        containsStrings.add("Error in registry file");

        endStrings.add("factory.epsg.ThreadedEpsgFactory <init>");
        endStrings.add("to a 1800000ms timeout");
        endStrings.add("Native library load failed.");
        endStrings.add("gdalframework.GDALUtilities loadGDAL");
        endStrings.add("org.gdal.gdal.gdalJNI.HasThreadSupport()I");
        endStrings.add("org.gdal.gdal.gdalJNI.VersionInfo__SWIG_0(Ljava/lang/String;)Ljava/lang/String;");
    }

    public static boolean doRemove( String line ) {
        for( String string : endStrings ) {
            if (line.endsWith(string)) {
                return true;
            }
        }
        for( String string : containsStrings ) {
            if (line.contains(string)) {
                return true;
            }
        }
        return false;
    }

}
