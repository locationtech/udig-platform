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
package net.refractions.udig.project.ui.internal.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.feature.FeatureType;

/**
 * Allows FeatureType schemas to be edited.
 *
 * @author jeichar
 * @since 0.3
 */
public class SchemaEditor extends DialogCellEditor {

    private FeatureType type;
    /**
     * Creates a new instance of SchemaEditor
     *
     * @param parent The parent of this editor widget
     * @param type The feature type to edit.
     */
    public SchemaEditor( Composite parent, FeatureType type ) {
        super(parent);
        this.type = type;
    }
    /**
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
     */
    protected Object openDialogBox( Control cellEditorWindow ) {
        type.getTypeName(); // does nothing yet.
        // TODO Auto-generated method stub
        return null;
    }
}
