/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.libs.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.libs.internal.messages"; //$NON-NLS-1$
    public static String Activator_1;
    public static String Activator_EPSG_DATABASE;
    public static String CHECK;
    public static String COORDINATE_REFERENCE_SYSTSMS;
    public static String COORDINATE_SYSTEMS;
    public static String DATUM_DEFINITIONS;
    public static String DATUMS;
    public static String EPSG_SETUP;
    public static String LOADING;
    public static String MATH_TRANSFORMS;
    public static String OPERATIONS_DEFINITIONS;
    public static String PLEASE_WAIT;
    public static String REGISTER;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {
    }
}
