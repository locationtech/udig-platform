/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.ui;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.internal.Messages;


/**
 * A "normal" content provider for a tree viewer.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ResolveContentProvider extends AbstractResolveContentProvider
        implements ITreeContentProvider {
    /**
     * Returns the child elements of the given parent element.
     * <p>
     * The difference between this method and <code>IStructuredContentProvider.getElements</code> is
     * that <code>getElements</code> is called to obtain the tree viewer's root elements, whereas
     * <code>getChildren</code> is used to obtain the children of a given parent element in the tree
     * (including a root).
     * </p>
     * The result is not modified by the viewer.
     * </p>
     *
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @param parent the parent element
     * @return an array of child elements
     */
    @Override
    public Object[] getChildren(Object parent) {
        if (parent == null)
            return null;
        if (parent instanceof List)
            return ((List) parent).toArray();
        if (parent instanceof String)
            return new Object[0];
        if (!(parent instanceof IResolve))
            return null;

        IResolve resolve = (IResolve) parent;
        if (structure.containsKey(resolve)) {
            List<IResolve> members = structure.get(resolve);
            return members != null ? members.toArray() : null;
        } else {
            update(resolve); // calculate
            return new Object[] { Messages.ResolveContentProvider_searching };
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element) {
        if (!(element instanceof IResolve))
            return null;
        IResolve resolve = (IResolve) element;
        try {
            // assume find parent is cheap
            if (element instanceof IService) {
                if (list != null && list.contains(resolve))
                    return list;
                if (catalog != null)
                    return catalog;
                return null; // service is probably the parent
            }
            if (element instanceof IGeoResource) {
                return ((IGeoResource) element).resolve(IService.class, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        try {
            if (element == null)
                return false;
            if (element instanceof IResolve) {
                List<IResolve> children = structure.get(element);
                if (children != null) {
                    return !children.isEmpty();
                }
                return true;
            }
            return ((element instanceof List) && !((List) element).isEmpty());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

}
