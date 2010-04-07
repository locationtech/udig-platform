/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.internal.ui;

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
