/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.IFolder;
import net.refractions.udig.project.internal.Folder;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides content control for the LegendView's tree viewer.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendViewContentProvider extends ArrayContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof Folder || parentElement instanceof IFolder) {
            final Folder folder = (Folder) parentElement;
            return folder.getItems().toArray();
        }
        return null;
    }

    @Override
    public Object getParent( Object element ) {
        return null;
    }

    @Override
    public boolean hasChildren( Object element ) {
        if (element instanceof Folder || element instanceof IFolder) {
            final Folder folder = (Folder) element;
            if (folder.getItems().size() > 0) {
                return true;
            }
        }
        return false;
    }
    
}
