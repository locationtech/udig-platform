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
package net.refractions.udig.project.ui.internal.adapters;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Adapts UDIG objects to table content and label providers.
 * <p>
 * Used to display UDIG Objects in tables, such as propertie view?
 * </p>
 * 
 * @author jones
 * @since 0.3
 */
public class UDIGTableProviderAdapter extends UDIGAdapterContentProvider
        implements
            IContentProvider,
            ITableColorProvider,
            ITableLabelProvider {

    private ITableColorProvider colors;

    /**
     * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
     */
    public Color getForeground( Object element, int columnIndex ) {
        return colors.getForeground(element, columnIndex);
    }

    /**
     * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
     */
    public Color getBackground( Object element, int columnIndex ) {
        return colors.getBackground(element, columnIndex);
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage( Object element, int columnIndex ) {
        return ((ITableLabelProvider) labels).getColumnImage(element, columnIndex);
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText( Object element, int columnIndex ) {
        return ((ITableLabelProvider) labels).getColumnText(element, columnIndex);
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        if (newInput != null && newInput != oldInput) {
            content = (ITreeContentProvider) Platform.getAdapterManager().getAdapter(newInput,
                    ITreeContentProvider.class);
            labels = (ITableLabelProvider) Platform.getAdapterManager().getAdapter(newInput,
                    ITableLabelProvider.class);
            colors = (ITableColorProvider) Platform.getAdapterManager().getAdapter(newInput,
                    ITableColorProvider.class);
        }
    }

}
