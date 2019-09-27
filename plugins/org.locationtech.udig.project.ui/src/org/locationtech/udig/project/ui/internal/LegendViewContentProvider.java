/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.locationtech.udig.project.IFolder;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.Map;

/**
 * Provides content control for the LegendView's tree viewer.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendViewContentProvider extends AdapterFactoryContentProvider {

    private LegendView view;
    
    public LegendViewContentProvider( LegendView view ) {
        super(ProjectUIPlugin.getDefault().getAdapterFactory());
        this.view = view;
    }

    @Override
    public Object[] getElements( Object object ) {
        if (object != null && object instanceof Map) {
            return ((Map) object).getLegend().toArray();    
        }
        return super.getElements(object);
    }
    
    
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
