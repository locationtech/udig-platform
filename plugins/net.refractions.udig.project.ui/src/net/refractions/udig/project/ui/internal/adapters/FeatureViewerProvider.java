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
package net.refractions.udig.project.ui.internal.adapters;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre><code>
 *  FeatureViewerProvider x = new FeatureViewerProvider( ... );
 *  TODO code example
 * </code></pre>
 * 
 * </p>
 * 
 * @author jones
 * @since 0.3
 */
public class FeatureViewerProvider extends LabelProvider implements ITreeContentProvider {

    public static class Databag {
        IPropertySource source;
        IPropertyDescriptor descriptor;
    }
    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
        Object[] children = getChildren(inputElement);
        if (children == null)
            return null;
        List list = new ArrayList();
        for( int i = 0; i < children.length; i++ ) {
            if (hasChildren(children[i])) {
                Object[] elements = getElements(children[i]);
                if (elements == null)
                    continue;
                for( int j = 0; j < elements.length; j++ ) {
                    list.add(elements[j]);
                }
            } else
                list.add(children[i]);
        }
        return list.toArray();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        // TODO implement method body
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof Databag) {
            Databag data = (Databag) parentElement;
            Object value = data.source.getPropertyValue(data.descriptor.getId());
            if (value instanceof IPropertySource)
                return createChildren((IPropertySource) value);
        } else if (Platform.getAdapterManager().hasAdapter(parentElement,
                "org.eclipse.ui.views.properties.IPropertySource")) { //$NON-NLS-1$
            IPropertySource source = (IPropertySource) Platform.getAdapterManager().getAdapter(
                    parentElement, "org.eclipse.ui.views.properties.IPropertySource"); //$NON-NLS-1$
            return createChildren(source);
        }
        return null;
    }

    Object[] createChildren( IPropertySource source ) {
        IPropertyDescriptor[] descriptors = source.getPropertyDescriptors();
        Databag[] data = new Databag[descriptors.length];
        for( int i = 0; i < data.length; i++ ) {
            data[i] = new Databag();
            data[i].source = source;
            data[i].descriptor = descriptors[i];
        }
        return data;
    }
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element ) {
        // TODO implement method body
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element ) {
        if (Platform.getAdapterManager().hasAdapter(element,
                "org.eclipse.ui.views.properties.IPropertySource")) //$NON-NLS-1$
            return true;
        if (element instanceof Databag) {
            Databag data = (Databag) element;
            Object value = data.source.getPropertyValue(data.descriptor.getId());
            if (value instanceof IPropertySource)
                return true;
        }
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element ) {
        // TODO implement method body
        if (element instanceof Databag) {
            Databag data = (Databag) element;
            return data.descriptor.getDisplayName();
        }
        return Messages.FeatureViewerProvider_unknown; 
    }

}
