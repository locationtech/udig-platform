/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.awt.Color;

import org.locationtech.udig.ui.graphics.Glyph;

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
