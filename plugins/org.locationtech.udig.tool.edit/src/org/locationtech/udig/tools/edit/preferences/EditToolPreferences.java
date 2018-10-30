/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.preferences;

import org.locationtech.udig.project.ui.FeatureEditorFieldEditor;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for all tools.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditToolPreferences extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public EditToolPreferences() {
        super(GRID);
        IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription(Messages.EditToolPreferences_description);
    }

    @Override
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(PreferenceConstants.P_ADVANCED_ACTIVE, Messages.EditToolPreferences_advanced_editing_name,
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(PreferenceConstants.P_SELECT_POST_ACCEPT, Messages.EditToolPreferences_CreateFeaturePreference,
                getFieldEditorParent()));
        addField(new IntegerFieldEditor(PreferenceConstants.P_SNAP_RADIUS, 
        		Messages.EditToolPreferences_snapRadius,
                getFieldEditorParent()));
        addField(new RadioGroupFieldEditor(
                PreferenceConstants.P_SNAP_BEHAVIOUR,
                Messages.EditToolPreferences_behaviour,
                2,
                new String[][]{
                        {
                                Messages.EditToolPreferences_noSnapping, SnapBehaviour.OFF.toString()},
                        {
                                Messages.EditToolPreferences_selected, SnapBehaviour.SELECTED.toString()},
                        {
                                Messages.EditToolPreferences_current, SnapBehaviour.CURRENT_LAYER.toString()},
                        {
                                Messages.EditToolPreferences_all, SnapBehaviour.ALL_LAYERS.toString()},
                        {
                                Messages.EditToolPreferences_grid, SnapBehaviour.GRID.toString()}}, getFieldEditorParent(), true));
        addField(new IntegerFieldEditor(PreferenceConstants.P_VERTEX_SIZE, 
        		Messages.EditToolPreferences_vertexDiameter,
                getFieldEditorParent()));

        IntegerFieldEditor fillOpacity = new IntegerFieldEditor(PreferenceConstants.P_FILL_OPACITY,
                Messages.EditToolPreferences_fillOpacity,
                getFieldEditorParent());
        fillOpacity.setValidRange(0, 100);
        addField(fillOpacity);

        IntegerFieldEditor vertexOpacity = new IntegerFieldEditor(PreferenceConstants.P_VERTEX_OPACITY,
                Messages.EditToolPreferences_vertexOpacity,
                getFieldEditorParent());
        vertexOpacity.setValidRange(0, 100);
        addField(vertexOpacity);

        addField(new ColorFieldEditor(PreferenceConstants.P_SNAP_CIRCLE_COLOR, 
        		Messages.EditToolPreferences_feedbackColor,
                getFieldEditorParent()));
        
        addField(new FeatureEditorFieldEditor(getFieldEditorParent()));
    }

    public void init( IWorkbench workbench ) {
    }

   static class DoubleFieldEditor extends FieldEditor {

        Label label;
        Text text;
       public DoubleFieldEditor( String name, String labelText, Composite parent ) {
            super(name, labelText, parent);
        }

       protected void adjustForNumColumns( int numColumns ) {
            
        }

        @Override
        protected void doFillIntoGrid( Composite comp, int numColumns ) {
            label = new Label(comp, SWT.NONE);
            label.setText(super.getLabelText());
            label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
            text = new Text(comp, SWT.SINGLE|SWT.BORDER);
            text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        }

        @Override
        protected void doLoad() {
            text.setText(String.valueOf(getPreferenceStore().getDouble(super.getPreferenceName())));
        }

        @Override
        protected void doLoadDefault() {
            text.setText(String.valueOf(getPreferenceStore().getDefaultDouble(super.getPreferenceName())));
        }

        @Override
        protected void doStore() {
            try{
                getPreferenceStore().setValue(super.getPreferenceName(), Double.valueOf(text.getText()));
            }catch (Exception e) {
                // you've been warned
            }
        }

        @Override
        public int getNumberOfControls() {
            return 2;
        }

    }
}
