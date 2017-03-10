package org.locationtech.udig.catalog.wms.preferences;

import org.locationtech.udig.catalog.internal.wms.WmsPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 *
 */
public class WmsPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	public WmsPreferencePage() {
		super(GRID);
	}


	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(WmsPlugin.getDefault().getPreferenceStore());
		setDescription("Configuration Options about WMS connections");
	}

	@Override
	protected void createFieldEditors() {
		
		addField(new IntegerFieldEditor(WmsPreferenceConstants.WMS_RESPONSE_TIMEOUT, 
				"WMS response timeout", getFieldEditorParent()));   
	}

}
