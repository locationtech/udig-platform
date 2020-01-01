/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2003, 2005 IBM Corporation and others
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.style.sld.editor.internal;

import org.eclipse.jface.preference.PreferenceContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.locationtech.udig.style.sld.editor.EditorPageManager;

/**
 * Provides a tree model for <code>EditorPageManager</code> content.
 * 
 * @see PreferenceContentProvider
 */
public class EditorPageContentProvider implements ITreeContentProvider {

    private EditorPageManager manager;

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        manager = null;
    }

    /**
     * Find the parent of the provided node.  Will search recursivly through the
     * preference tree.
     * 
     * @param parent the possible parent node.
     * @param target the target child node.
     * @return the parent node of the child node.
     */
    private IEditorNode findParent(IEditorNode parent,
            IEditorNode target) {
        if (parent.getId().equals(target.getId()))
            return null;

        IEditorNode found = parent.findSubNode(target.getId());
        if (found != null)
            return parent;

        IEditorNode[] children = parent.getSubNodes();

        for (int i = 0; i < children.length; i++) {
            found = findParent(children[i], target);
            if (found != null)
                return found;
        }

        return null;
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {//must be an instance of <code>IPreferenceNode</code>.
        return ((IEditorNode) parentElement).getSubNodes();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {// must be an instance of <code>PreferenceManager</code>.
        return getChildren(((EditorPageManager) inputElement).getRoot());
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {//must be an instance of <code>IEditorNode</code>.
        IEditorNode targetNode = (IEditorNode) element;
        IEditorNode root = manager.getRoot();
        return findParent(root, targetNode);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        manager = (EditorPageManager) newInput;
    }
	/**
	 * Set the manager for the preferences.
	 * @param manager The manager to set.
	 * 
	 * @since 3.1
	 */
	protected void setManager(EditorPageManager manager) {
		this.manager = manager;
	}
}
