/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.style.sld.editor.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A filter used in conjunction with <code>FilteredTree</code>.  This filter is 
 * inefficient - in order to see if a node should be filtered it must use the 
 * content provider of the tree to do pattern matching on its children.  This 
 * causes the entire tree structure to be realized.
 */
public class PatternFilter extends ViewerFilter {

    private Map<Object,Object[]> cache = new HashMap<Object,Object[]>();

    private StringMatcher matcher;

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object[])
     */
    @Override
    public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
        if (matcher == null)
            return elements;

        Object[] filtered = (Object[]) cache.get(parent);
        if (filtered == null) {
            filtered = super.filter(viewer, parent, elements);
            cache.put(parent, filtered);
        }
        return filtered;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        Object[] children = ((ITreeContentProvider) ((AbstractTreeViewer) viewer)
                .getContentProvider()).getChildren(element);
        if (children.length > 0)
            return filter(viewer, element, children).length > 0;

        String labelText = ((ILabelProvider) ((StructuredViewer) viewer)
                .getLabelProvider()).getText(element);
        if(labelText == null)
            return false;
        return match(labelText);
    }

    /**
     * 
     * @param patternString
     */
    public void setPattern(String patternString) {
        cache.clear();
        if (patternString == null || patternString.equals("")) //$NON-NLS-1$
            matcher = null;
        else
            matcher = new StringMatcher(patternString + "*", true, false); //$NON-NLS-1$
    }

    /**
     * Answers whether the given String matches the pattern.
     * 
     * @param string the String to test
     * @return whether the string matches the pattern
     */
    protected boolean match(String string) {
        return matcher.match(string);
    }
}
