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
package net.refractions.udig.project.ui.internal.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Allows SimpleFeatureType schemas to be edited.
 * 
 * @author jeichar
 * @since 0.3
 */
public class SchemaEditor extends DialogCellEditor {

    private SimpleFeatureType type;
    /**
     * Creates a new instance of SchemaEditor
     * 
     * @param parent The parent of this editor widget
     * @param type The feature type to edit.
     */
    public SchemaEditor( Composite parent, SimpleFeatureType type ) {
        super(parent);
        this.type = type;
    }
    /**
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
     */
    protected Object openDialogBox( Control cellEditorWindow ) {
        type.getName().getLocalPart(); // does nothing yet.
        // TODO Auto-generated method stub
        return null;
    }
}
