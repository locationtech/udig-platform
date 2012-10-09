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


import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.graphics.Font;

/**
 * This PreferenceBoldLabelProvider will bold those elements which really match
 * the search contents
 */
public class EditorBoldLabelProvider extends EditorPageLabelProvider
        implements IFontProvider {

    
    private FilteredComboTree comboTree;

    public EditorBoldLabelProvider(FilteredComboTree comboTree) {
        this.comboTree = comboTree;
    }
    /**
     * Using "false" to construct the filter so that this filter can filter
     * supernodes of a matching node
     */
    PatternItemFilter filterForBoldElements = new PatternItemFilter(false);

    public Font getFont(Object element) {

        String filterText = comboTree.getFilterControlText();

        // Do nothing if it's empty string
        if (!(filterText.equals("") || filterText.equals(comboTree.getInitialText()))) {//$NON-NLS-1$

            boolean initial = comboTree.getInitialText() != null
                    && filterText.equals(comboTree.getInitialText());
            if (initial) {
                filterForBoldElements.setPattern(null);
            } else {
                filterForBoldElements.setPattern(filterText);
            }

            ITreeContentProvider contentProvider = (ITreeContentProvider) comboTree.getViewer()
                    .getContentProvider();
            Object parent = contentProvider.getParent(element);

            if (filterForBoldElements.select(comboTree.getViewer(), parent, element)) {
                return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
            }
        }
        return null;
    }

}