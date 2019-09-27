/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import java.util.ArrayList;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.internal.FeatureEditorLoader;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * Editor for modifying the default SimpleFeature Editor
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
