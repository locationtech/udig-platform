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
package eu.udig.style.advanced.raster;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

/**
 * Simple container for color rules.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RuleValues {
    public double fromValue;
    public double toValue;
    public Color fromColor;
    public Color toColor;

    public static org.eclipse.swt.graphics.Color asSWT( Color awtColor ) {
        org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(Display
                .getDefault(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
        return color;
    }
}