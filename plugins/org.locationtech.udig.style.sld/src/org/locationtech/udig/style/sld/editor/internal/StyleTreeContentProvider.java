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
package org.locationtech.udig.style.sld.editor.internal;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.locationtech.udig.ui.graphics.SLDs;

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
