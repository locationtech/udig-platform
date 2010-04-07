/**
 * 
 */
package net.refractions.udig.style.sld.editor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.brewer.color.ColorBrewer;

final class BrewerPaletteContentProvider implements
		IStructuredContentProvider {
	/**
	 * 
	 */
	private final StyleThemePage styleThemePage;

	/**
	 * @param styleThemePage
	 */
	BrewerPaletteContentProvider(StyleThemePage styleThemePage) {
		this.styleThemePage = styleThemePage;
	}

	@SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
	  if (inputElement instanceof ArrayList) {
	      ArrayList<Object> list = (ArrayList<Object>) inputElement;
	      return list.toArray();
	  } else 
	    if (inputElement instanceof ColorBrewer) {
	        ColorBrewer brewer = (ColorBrewer) inputElement;
	        int selection = this.styleThemePage.getCombo(StyleThemePage.COMBO_PALETTES).getSelectionIndex();
	        if (selection == 0) //All
	            return brewer.getPalettes(ColorBrewer.ALL); 
	        else if (selection == 1) //Numerical
	            return brewer.getPalettes(ColorBrewer.SUITABLE_RANGED);
	        else if (selection == 2) //Sequential
	            return brewer.getPalettes(ColorBrewer.SEQUENTIAL);
	        else if (selection == 3) //Diverging
	            return brewer.getPalettes(ColorBrewer.DIVERGING);
	        else if (selection == 4) //Categorical
	            return brewer.getPalettes(ColorBrewer.SUITABLE_UNIQUE);
	        else
	            return brewer.getPalettes();
	    } else {
	        return new Object[0];
	    }
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}