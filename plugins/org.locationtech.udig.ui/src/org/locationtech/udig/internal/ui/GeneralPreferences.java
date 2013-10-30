/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * General uDig preferences
 * @author Jesse
 * @since 1.1.0
 */
public class GeneralPreferences extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    /**
     * @param style
     */
    public GeneralPreferences( ) {
        super(GRID);
    }

    /**
     * @param title
     * @param style
     */
    public GeneralPreferences( String title, int style ) {
        super(title, style);
    }

    /**
     * @param title
     * @param image
     * @param style
     */
    public GeneralPreferences( String title, ImageDescriptor image, int style ) {
        super(title, image, style);
    }

    @Override
    protected void createFieldEditors() {
    }

    public void init( IWorkbench workbench ) {
    }

}
