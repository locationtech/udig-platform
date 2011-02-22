package net.refractions.udig.style.sld.editor.internal;

import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;

public class StyleTreeContentProvider implements ITreeContentProvider {

    private static final Object[] EMPTY_ARRAY = new Object[0];

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement == null) return EMPTY_ARRAY;
		if (parentElement instanceof Style) {
			Style style = (Style) parentElement;
			Rule[] rule = SLDs.rules(style);
 			return rule; //return rules
		} else if (parentElement instanceof FeatureTypeStyle) {
            FeatureTypeStyle fts = (FeatureTypeStyle) parentElement;
            return fts.getRules();
        }
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

}
