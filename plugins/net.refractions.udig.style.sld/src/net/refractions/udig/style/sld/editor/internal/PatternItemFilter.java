/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.style.sld.editor.internal;

import java.text.BreakIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.preferences.WorkbenchPreferenceExtensionNode;

/**
 * A class which handles filtering preferences nodes based on a supplied
 * matching string.
 * 
 * @since 3.1
 * 
 */
public class PatternItemFilter extends PatternFilter {

    /**
     * this cache is needed because
     * WorkbenchPreferenceExtensionNode.getKeywordLabels() is expensive. When it
     * tracks keyword changes effectivly than this cache can be removed.
     */
    private Map keywordCache = new HashMap();

    protected boolean matchItem;

    /**
     * Create a new instance of a PatternItemFilter
     * 
     * @param isMatchItem
     */
    public PatternItemFilter(boolean isMatchItem) {
        super();
        matchItem = isMatchItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        ITreeContentProvider contentProvider = (ITreeContentProvider) ((TreeViewer) viewer)
                .getContentProvider();

        IEditorNode node = (IEditorNode) element;
        Object[] children = contentProvider.getChildren(node);
        String text = node.getLabelText();

        if(wordMatches(text))
            return true;
        
        if (matchItem) {

            // Will return true if any subnode of the element matches the search
            if (filter(viewer, element, children).length > 0)
                return true;
        }

        if (node instanceof WorkbenchPreferenceExtensionNode) {
            WorkbenchPreferenceExtensionNode workbenchNode = (WorkbenchPreferenceExtensionNode) node;

            Collection keywordCollection = (Collection) keywordCache.get(node);
            if (keywordCollection == null) {
                keywordCollection = workbenchNode.getKeywordLabels();
                keywordCache.put(node, keywordCollection);
            }
            if (keywordCollection.isEmpty())
                return false;
            Iterator keywords = keywordCollection.iterator();
            while (keywords.hasNext()) {
                if (wordMatches((String) keywords.next()))
                    return true;
            }
        }
        return false;

    }

    /**
     * Return whether or not if any of the words in text satisfy the
     * match critera.
     * @param text
     * @return boolean <code>true</code> if one of the words in text 
     * satisifes the match criteria.
     */
    private boolean wordMatches(String text) {
        
        //If the whole text matches we are all set
        if(match(text))
            return true;
        
        // Break the text up into words, separating based on whitespace and
        // common punctuation.
        // Previously used String.split(..., "\\W"), where "\W" is a regular
        // expression (see the Javadoc for class Pattern).
        // Need to avoid both String.split and regular expressions, in order to
        // compile against JCL Foundation (bug 80053).
        // Also need to do this in an NL-sensitive way. The use of BreakIterator
        // was suggested in bug 90579.
        BreakIterator iter = BreakIterator.getWordInstance();
        iter.setText(text);
        int i = iter.first();
        while (i != java.text.BreakIterator.DONE && i < text.length()) {
            int j = iter.following(i);
            if (j == java.text.BreakIterator.DONE)
                j = text.length();
            if (Character.isLetterOrDigit(text.charAt(i))) {
                String word = text.substring(i, j);
                if (match(word))
                    return true;
            }
            i = j;
        }
        return false;
    }

}
