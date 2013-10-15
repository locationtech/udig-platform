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