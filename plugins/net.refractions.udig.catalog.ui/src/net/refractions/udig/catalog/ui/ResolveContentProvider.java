/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.ui;

import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.jface.viewers.ITreeContentProvider;
/**
 * A "normal" content provider for a tree viewer.   
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ResolveContentProvider extends AbstractResolveContentProvider implements ITreeContentProvider {
    /**
     * Returns the child elements of the given parent element.
     * <p>
     * The difference between this method and <code>IStructuredContentProvider.getElements</code>
     * is that <code>getElements</code> is called to obtain the tree viewer's root elements,
     * whereas <code>getChildren</code> is used to obtain the children of a given parent element
     * in the tree (including a root).
     * </p>
     * The result is not modified by the viewer.
     * </p>
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @param parent the parent element
     * @return an array of child elements
     */
    public Object[] getChildren( Object parent ) {
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
            return new Object[]{Messages.ResolveContentProvider_searching }; 
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element ) {
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
    public boolean hasChildren( Object element ) {
        try {
            if (element == null)
                return false;
            if (element instanceof IResolve){
            	List<IResolve> children = structure.get(element);
            	if( children != null ){
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
    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

}