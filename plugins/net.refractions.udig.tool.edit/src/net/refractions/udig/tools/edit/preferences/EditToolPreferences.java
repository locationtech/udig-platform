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
package net.refractions.udig.tools.edit.preferences;

import net.refractions.udig.project.ui.FeatureEditorFieldEditor;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

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
