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

import java.awt.Color;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.ui.graphics.Glyph;
import org.locationtech.udig.ui.graphics.SLDs;

public class StyleTreeLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getImage( Object element ) {
        if (element instanceof Rule) {
            // grab the color for the current rule
            Rule rule = (Rule) element;
            String[] colors = SLDs.colors(rule);
            if (colors.length == 0)
                return null;
            if (colors.length > 1) {
                SLDPlugin.log("Multiple colours received unexpectedly, proceeding with first colour only.", null); //$NON-NLS-1$
            }
            final Color color = SLDs.toColor(colors[0]);
            return Glyph.swatch(color).createImage();
        }
        return null;
    }

    public String getText( Object element ) {
        if (element instanceof Style) {
            // shouldn't be called, as the root object is hidden
            Style style = (Style) element;
            return style.getDescription().getTitle().toString();
        } else if (element instanceof Rule) {
            Rule rule = (Rule) element;
            if (rule.getName().startsWith("rule")) //$NON-NLS-1$
                return StyleGenerator.toStyleExpression(rule.getFilter());
            else
                return rule.getDescription().getTitle().toString();
        }
        return super.getText(element); // unknown type
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

    public Image getColumnImage( Object element, int columnIndex ) {
        if (columnIndex == 0) { // image
            if (element instanceof Rule) {
                // grab the color for the current rule
                Rule rule = (Rule) element;
                String[] colors = SLDs.colors(rule);
                if (colors.length == 0)
                    return null;
                Color color = SLDs.toColor(colors[0]);

                List<Symbolizer> symbolizers = rule.symbolizers();
                for( Symbolizer symbolizer : symbolizers ) {
                    if (symbolizer instanceof PointSymbolizer) {
                        return Glyph.point(rule).createImage();
                    } else if (symbolizer instanceof LineSymbolizer) {
                        return Glyph.line(rule).createImage();
                    } else if (symbolizer instanceof PolygonSymbolizer) {
                        return Glyph.polygon(rule).createImage();
                    }
                }

                return Glyph.swatch(color).createImage();
            }
        }
        return null;
    }

    public String getColumnText( Object element, int columnIndex ) {
        if (columnIndex == 0) { // colour
            return ""; //$NON-NLS-1$
        } else if (columnIndex == 1) { // label
            if (element instanceof String) {
                SLDPlugin.log("already knew label", null); //$NON-NLS-1$
                return (String) element;
            } else if (element instanceof Rule) {
                Rule rule = (Rule) element;
                return rule.getDescription().getTitle().toString();
            }
        } else if (columnIndex == 2) { // style expression
            if (element instanceof Rule) {
                Rule rule = (Rule) element;
                if (rule.getName().startsWith("rule")) //$NON-NLS-1$
                    return StyleGenerator.toStyleExpression(rule.getFilter());
                else
                    return null;
            }
        }
        return null;
    }

    public void addListener( ILabelProviderListener listener ) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty( Object element, String property ) {
        return true;
    }

}
