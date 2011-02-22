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
package net.refractions.udig.project.ui;

import java.util.ArrayList;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.ui.internal.FeatureEditorLoader;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Editor for modifying the default Feature Editor
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureEditorFieldEditor extends FieldEditor {
    private Combo combo;
    private GridData data;
    private ArrayList<String> choices = new ArrayList<String>();
    private ArrayList<String> ids = new ArrayList<String>();

    public FeatureEditorFieldEditor( Composite parent ) {
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
        FeatureEditorLoader[] loaders = ProjectUIPlugin.getDefault().getFeatureEditProcessor()
                .getEditorLoaders();
        for( FeatureEditorLoader loader : loaders ) {
            choices.add(loader.getName());
            ids.add(loader.getId());
        }
        createControl(parent);
    }

    @Override
    protected void adjustForNumColumns( int numColumns ) {
        if (data != null)
            data.horizontalSpan = numColumns-1;
    }

    @Override
    protected void doFillIntoGrid( Composite parent, int numColumns ) {
        Label label=new Label(parent, SWT.NONE);
        label.setText(Messages.FeatureEditorFieldEditor_label);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        combo = new Combo(parent, SWT.DEFAULT);
        data = new GridData(SWT.FILL, SWT.TOP, true, true);
        data.horizontalSpan = numColumns-1;
        combo.setLayoutData(data);
        combo.setItems(choices.toArray(new String[0]));
    }

    @Override
    protected void doLoad() {
        if (combo != null) {
            int indexOf = ids.indexOf(getPreferenceStore().getString(
                    PreferenceConstants.P_DEFAULT_FEATURE_EDITOR));
            if (indexOf == -1) {
                doLoadDefault();
            } else {
                combo.select(indexOf);
            }
        }
    }
    @Override
    protected void doLoadDefault() {
        int indexOf = ids.indexOf(getPreferenceStore().getDefaultString(
                PreferenceConstants.P_DEFAULT_FEATURE_EDITOR));
        if (indexOf == -1) {
            combo.select(0);
        } else {
            combo.select(indexOf);
        }
    }

    @Override
    protected void doStore() {
        int selectionIndex = combo.getSelectionIndex();
        String defaultEditorID = "";
        if(selectionIndex != -1)
            defaultEditorID = this.ids.get(selectionIndex);
        getPreferenceStore().setValue(PreferenceConstants.P_DEFAULT_FEATURE_EDITOR, defaultEditorID);
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

}
