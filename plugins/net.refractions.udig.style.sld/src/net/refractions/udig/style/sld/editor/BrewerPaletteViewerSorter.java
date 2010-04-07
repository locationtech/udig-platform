/**
 * 
 */
package net.refractions.udig.style.sld.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.geotools.brewer.color.BrewerPalette;

final class BrewerPaletteViewerSorter extends ViewerSorter {
	@Override
	public int compare( Viewer viewer, Object e1, Object e2 ) {
	    if (e1 instanceof BrewerPalette && e2 instanceof BrewerPalette) {
	        BrewerPalette p1 = (BrewerPalette) e1;
	        BrewerPalette p2 = (BrewerPalette) e2;
	        //alphabetical by name
	        return p1.getName().compareTo(p2.getName());
	        //TODO: alternatives (colour hue?)
	    } else return super.compare(viewer, e1, e2);
	}
}