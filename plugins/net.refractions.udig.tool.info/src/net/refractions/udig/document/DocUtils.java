/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.document;

/**
 * Utility methods for {@link DocumentView} and related classes.
 * 
 * @author Naz Chan 
 */
public final class DocUtils {

    /**
     * Converts the string to camel case.
     * 
     * @param text
     * @return string in camel case
     */
    public static String toCamelCase(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
}
