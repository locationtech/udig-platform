/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.udig.jconsole.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Manager for colors used in the Java editor
 */
public class JavaColorProvider {

    public static final RGB WHITE = new RGB(255, 255, 255);
    public static final RGB MULTI_LINE_COMMENT = new RGB(9, 144, 98);
    public static final RGB SINGLE_LINE_COMMENT = new RGB(9, 144, 98);
    public static final RGB KEYWORD = new RGB(141, 28, 104);
    public static final RGB METHOD = new RGB(0, 0, 188);
    public static final RGB TYPE = KEYWORD;// new RGB(231, 82, 231);
    public static final RGB CONSTANTS = new RGB(231, 82, 231);
    public static final RGB STRING = new RGB(0, 0, 255);
    public static final RGB GEOSCRIPT = new RGB(40, 40, 40);
    public static final RGB DEFAULT = new RGB(0, 0, 0);
    public static final RGB JAVADOC_KEYWORD = new RGB(0, 128, 0);
    public static final RGB JAVADOC_TAG = new RGB(128, 128, 128);
    public static final RGB JAVADOC_LINK = new RGB(128, 128, 128);
    public static final RGB JAVADOC_DEFAULT = new RGB(0, 128, 128);
    public static final RGB OMS_COMPONENTS = new RGB(55, 118, 179);
    // public static final RGB OMS_SIM = new RGB(225, 64, 75);
    public static final RGB OMS_MODULES = new RGB(225, 127, 64);
    public static final int grey = 153;
    public static final RGB MODULES_FIELDS = new RGB(grey, grey, grey);

    protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

    /**
     * Release all of the color resources held onto by the receiver.
     */
    public void dispose() {
        Iterator<Color> e = fColorTable.values().iterator();
        while( e.hasNext() )
            e.next().dispose();
    }

    /**
     * Return the color that is stored in the color table under the given RGB
     * value.
     *
     * @param rgb the RGB value
     * @return the color stored in the color table for the given RGB value
     */
    public Color getColor( RGB rgb ) {
        Color color = fColorTable.get(rgb);
        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            fColorTable.put(rgb, color);
        }
        return color;
    }
}
