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
package net.refractions.udig.validation.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.geotools.validation.dto.ArgumentDTO;

public class AmbiguousCellEditor extends CellEditor {

	private static final int defaultStyle = SWT.SINGLE;
	
	TextCellEditor textCellEditor;
	ComboBoxCellEditor comboBoxCellEditor;
	CellEditor cellEditor;
	Composite parent; //store the parent for "lazy loading"
	
	Table table; //we'll save a table reference for future use
	String[] layers; //list of layers to offer (comboBox labels)
	String[] typeRefs; //list of actual values to store (comboBox values)
	
    public AmbiguousCellEditor(Composite parent, Table table, ArrayList<String> layersList, ArrayList<String> typeRefsList) {
        this(parent, defaultStyle);
        this.table = table;
        this.typeRefs = typeRefsList.toArray(new String[typeRefsList.size()]);
        this.layers = layersList.toArray(new String[layersList.size()]);

    }
    
    public AmbiguousCellEditor(Composite parent, int style) {
        super(parent, style);
    }
	
	@Override
	protected Control createControl(Composite parent) {
		// normally one would create a control here, but instead we'll create
		// the controls the first time getControl() is called
		this.parent = parent;
		//return cellEditor.getControl();
		return null; 
	}

	@Override 
    public Control getControl() {
		if (textCellEditor == null) createAmbiguousControls(); 
		selectAmbiguousControl();
		return cellEditor.getControl();
    }

	@Override
	protected Object doGetValue() {
		if (cellEditor instanceof ComboBoxCellEditor) {
			Object value = cellEditor.getValue();
			if (value instanceof Integer) {
				int intVal = ((Integer) value).intValue();
				if (intVal == -1) {
					//the user selected nothing (clicked the combo, didn't select anything, and clicked elsewhere)
					return ""; //$NON-NLS-1$
				}
				//translate the integer (comboBox index) into a string value (typeRef)
				Object newValue = typeRefs[intVal];
				return newValue;
			} else {
				return ""; //$NON-NLS-1$
			}
		}
		return cellEditor.getValue();
	}

	@Override
	protected void doSetFocus() {
		cellEditor.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		if (cellEditor == null) {
			createAmbiguousControls();
			selectAmbiguousControl();
		}
		if (cellEditor instanceof ComboBoxCellEditor) {
			//			//translate string value (from "layers") into an integer (comboBox index)
			int newValue = -1;
			for (int i = 0; i < typeRefs.length; i++) {
				if (typeRefs[i].equals(value)) {
					newValue = i;
					break;
				}
			}
			cellEditor.setValue(newValue); //use an index, rather than string value
		} else {
			cellEditor.setValue(value);
		}
	}
	
	private void createAmbiguousControls() {
		textCellEditor = new TextCellEditor(parent);
		comboBoxCellEditor = new ComboBoxCellEditor(parent, layers);
	}
	
	private void selectAmbiguousControl() {
		TableItem item = table.getItem(table.getSelectionIndex());
		ArgumentDTO argDTO = (ArgumentDTO) item.getData();
		if (argDTO.getName().toLowerCase().contains("typeref")) { //$NON-NLS-1$
			cellEditor = comboBoxCellEditor;
		} else {
			cellEditor = textCellEditor;
		}
	}
}
