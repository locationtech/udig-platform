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
