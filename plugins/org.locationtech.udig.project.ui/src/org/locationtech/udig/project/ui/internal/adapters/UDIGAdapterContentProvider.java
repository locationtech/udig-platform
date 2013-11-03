/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.adapters;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * Adapts UDIG objects to tree content and label providers.
 * 
 * @author jones
 * @since 0.3
 */
public class UDIGAdapterContentProvider implements ITreeContentProvider, ILabelProvider {

    protected ITreeContentProvider content;
    protected IBaseLabelProvider labels;

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement ) {
        if (content == null)
            return null;
        return content.getChildren(parentElement);
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element ) {
        if (content == null)
            return null;
        return content.getParent(element);
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element ) {
        if (content == null)
            return false;
        return content.hasChildren(element);
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element ) {
        if (labels == null)
            return null;
        return ((ILabelProvider) labels).getImage(element);
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element ) {
        if (labels == null)
            return null;
        return ((ILabelProvider) labels).getText(element);
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
        if (content == null)
            return null;
        return content.getElements(inputElement);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        if (content != null)
            content.dispose();
        if (labels != null)
            labels.dispose();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        content = (ITreeContentProvider) Platform.getAdapterManager().getAdapter(newInput,
                IContentProvider.class);
        labels = (ILabelProvider) Platform.getAdapterManager().getAdapter(newInput,
                ILabelProvider.class);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener( ILabelProviderListener listener ) {
        if (labels == null)
            return;
        labels.addListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty( Object element, String property ) {
        if (labels == null)
            return false;
        return labels.isLabelProperty(element, property);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener( ILabelProviderListener listener ) {
        if (labels == null)
            return;
        labels.removeListener(listener);
    }

}
