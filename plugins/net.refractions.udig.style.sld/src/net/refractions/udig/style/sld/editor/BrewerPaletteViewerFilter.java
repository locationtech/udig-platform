/**
 * 
 */
package net.refractions.udig.style.sld.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.PaletteSuitability;

final class BrewerPaletteViewerFilter extends ViewerFilter {
	/**
	 * 
	 */
	private final StyleThemePage styleThemePage;

	/**
	 * @param styleThemePage
	 */
	BrewerPaletteViewerFilter(StyleThemePage styleThemePage) {
		this.styleThemePage = styleThemePage;
	}

	@Override
	public boolean select( Viewer viewer, Object parentElement, Object element ) {
	    if (element instanceof BrewerPalette) {
	        BrewerPalette pal = (BrewerPalette) element;
	        int numClasses = new Integer(this.styleThemePage.getCombo(StyleThemePage.COMBO_CLASSES).getText()).intValue();
	        if (pal.getMaxColors() < numClasses) {
	            return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_COLORBLIND).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_COLORBLIND) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_CRT).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_CRT) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_LCD).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_LCD) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PHOTOCOPY).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_PHOTOCOPY) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PRINT).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_PRINT) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	        if (this.styleThemePage.getButton(StyleThemePage.BUTTON_PROJECTOR).getSelection()) {
	            if (pal.getPaletteSuitability().getSuitability(numClasses, PaletteSuitability.VIEWER_PROJECTOR) != PaletteSuitability.QUALITY_GOOD)
	                return false;
	        }
	    }
	    return true;
	}
}