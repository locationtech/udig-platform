/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2005 IBM Corporation and others
 * ------
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.style.sld.editor.internal;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * The PreferenceNodeFilter is a filter that only matches
 * a set of ids.
 */
public class EditorNodeFilter extends ViewerFilter {

    Collection ids = new HashSet();

    /**
     * Create a new instance of the receiver on a
     * list of filteredIds.
     * @param filteredIds The collection of ids that
     * will be shown.
     */
    public EditorNodeFilter(String[] filteredIds) {
        super();
        for (int i = 0; i < filteredIds.length; i++) {
            ids.add(filteredIds[i]);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return checkNodeAndChildren((IPreferenceNode) element);
    }

    /**
     * Check to see if the node or any of its children 
     * have an id in the ids.
     * @param node WorkbenchPreferenceNode
     * @return boolean <code>true</code> if node or oe of its children
     * has an id in the ids.
     */
    private boolean checkNodeAndChildren(IPreferenceNode node) {
        if(ids.contains(node.getId()))
            return true;
        
        IPreferenceNode[] subNodes = node.getSubNodes();
        for (int i = 0; i < subNodes.length; i++) {
            if(checkNodeAndChildren(subNodes[i]))
                return true;
            
        }
        return false;
    }

}
