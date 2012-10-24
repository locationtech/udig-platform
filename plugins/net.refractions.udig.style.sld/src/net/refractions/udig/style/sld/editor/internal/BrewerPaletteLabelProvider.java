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
package net.refractions.udig.style.sld.editor.internal;

import java.awt.Color;

import net.refractions.udig.style.sld.editor.CustomDynamicPalette;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.geotools.brewer.color.BrewerPalette;

public class BrewerPaletteLabelProvider extends LabelProvider {
	public Image getImage(Object element) {
		if (element instanceof BrewerPalette) {
			BrewerPalette palette = (BrewerPalette) element;
			int maxColors = palette.getMaxColors();
            Color[] colors;
            try{
                colors = palette.getColors(maxColors);
            }catch (Exception e) {
                colors = palette.getColors();
                palette = new CustomDynamicPalette(palette.getName(), palette.getDescription(), colors);
                colors = palette.getColors(maxColors);
            }
            return Glyph.palette(colors).createImage();
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof BrewerPalette) {
			BrewerPalette palette = (BrewerPalette) element;
			String text = null;
            text = palette.getName() + ": " + palette.getDescription(); //$NON-NLS-1$
            if (text == null) text = palette.getName();
            return text; 
		}
		return null;
	}
}
