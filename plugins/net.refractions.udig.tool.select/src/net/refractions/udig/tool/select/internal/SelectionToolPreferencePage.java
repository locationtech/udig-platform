/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tool.select.internal;

import net.refractions.udig.tool.select.SelectPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This  preference page provides access to all preference relating to selection tools.
 * 
 * @author leviputna
 * @since 1.3.0
 */
public class SelectionToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    
    public static final String ZOOM_TO_SELECTION = "boundaryZoomToSelection"; //$NON-NLS-1$
    public static final String NAVIGATE_SELECTION = "boundaryNavigateSelection"; //$NON-NLS-1$

//    private BooleanFieldEditor zoomToSelection;
    private BooleanFieldEditor navigateSelection;
    
    public SelectionToolPreferencePage() {
        super(GRID);
        IPreferenceStore store = SelectPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setTitle(Messages.Select_Title);
        setDescription(Messages.Select_Description);
    }

    @Override
    protected void createFieldEditors() {
        
        Label boundarySelection = new Label (getFieldEditorParent(), SWT.HORIZONTAL | SWT.BOLD | SWT.TITLE);
        boundarySelection.setText(Messages.Group_Boundary);
        boundarySelection.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
        
//        zoomToSelection = new BooleanFieldEditor(ZOOM_TO_SELECTION, Messages.Zoom_To_Selection, getFieldEditorParent());
//        addField(zoomToSelection);
        
        navigateSelection = new BooleanFieldEditor(NAVIGATE_SELECTION, Messages.Navigate_Selection, getFieldEditorParent());
        addField(navigateSelection);
    }
    
    @Override
    public void init( IWorkbench workbench ) {
    }

   
}
