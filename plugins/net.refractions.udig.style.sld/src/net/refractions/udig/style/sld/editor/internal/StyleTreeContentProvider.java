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

import java.util.List;

import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;

/**
 * Adapt a GeoTools Style (or FeatureTypeStyle) into a tree of Rules.
 */
public class StyleTreeContentProvider implements ITreeContentProvider {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    public void dispose() {
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }

    public Object[] getChildren( Object parentElement ) {
        if (parentElement == null)
            return EMPTY_ARRAY;
        if (parentElement instanceof Style) {
            Style style = (Style) parentElement;
            Rule[] rule = SLDs.rules(style);
            return rule; // return rules
        } else if (parentElement instanceof FeatureTypeStyle) {
            FeatureTypeStyle fts = (FeatureTypeStyle) parentElement;
            List<Rule> rules = fts.rules();
            Rule[] rulesArray = (Rule[]) rules.toArray(new Rule[rules.size()]);
            return rulesArray;
        }
        return EMPTY_ARRAY;
    }

    public Object getParent( Object element ) {
        return null;
    }

    public boolean hasChildren( Object element ) {
        return false;
    }

    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

}
