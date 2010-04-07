package net.refractions.udig.style.sld.editor.internal;

import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.geotools.brewer.color.BrewerPalette;

public class BrewerPaletteLabelProvider extends LabelProvider {
	public Image getImage(Object element) {
		if (element instanceof BrewerPalette) {
			BrewerPalette palette = (BrewerPalette) element;
			return Glyph.palette(palette.getColors(palette.getMaxColors())).createImage();
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
