/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style.raster.ui;

import java.text.DecimalFormat;

import net.refractions.udig.style.raster.ui.ValueFormatter.DataType;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for determining value number format.
 * 
 * @author Emily
 *
 */
public class FormatDialog extends TitleAreaDialog {

	private ValueFormatter.DataType selectedDataType = null;
	private DecimalFormat custom = null;
	private ValueFormatter currentFormat;
	private Text txtCustom;
	private Combo formatCombo;
	private Label lblInfo, lblCustom;
	
	public FormatDialog(Shell parentShell, ValueFormatter currentformat) {
		super(parentShell);
		this.currentFormat = currentformat;
	}
	
	@Override
	public void okPressed(){
		String invalid = null;
		
		if (formatCombo.getSelectionIndex() == 3){
			
			if (txtCustom.getText().contains(",")){
				invalid = "Invalid number format.  Cannot contain commas";
			}
			try{
				custom = ((DecimalFormat)DecimalFormat.getInstance());
				custom.applyPattern(txtCustom.getText());
			}catch (Exception ex){
				invalid = "Invalid number format. " + ex.getLocalizedMessage();
			}
		}else{
			custom = null;
		}
		
		if (invalid != null){
			MessageDialog.openError(getShell(), "Error", invalid);
			return;
		}
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Value Format");
		setMessage("Format the value numbers.");
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Composite main = new Composite(composite, SWT.BORDER);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		main.setLayout(new GridLayout(2, false));
		
		Label lbl = new Label(main, SWT.NONE);
		lbl.setText("Format Type:");
		
		formatCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		formatCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		formatCombo.add("Raw (based on raster type)");
		formatCombo.add("Integer");
		formatCombo.add("Double");
		formatCombo.add("Custom");
		
		lblCustom = new Label(main, SWT.NONE);
		lblCustom.setText("Custom Format String:");
		
		txtCustom = new Text(main, SWT.BORDER);
		txtCustom.setText("#.####");
		txtCustom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label spacer = new Label(main, SWT.NONE);
		lblInfo = new Label(main, SWT.NONE | SWT.WRAP);
		lblInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData)lblInfo.getLayoutData()).widthHint = 100;
		lblInfo.setText("pound (#) denotes a digit, zero (0) denotes a manditory digit, period (.) a placeholder for the decimal.");
		
		formatCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboSelected();
			}
			
		});
		
		
		/* init dialog */
		DataType type = currentFormat.getDataType();
		DecimalFormat curr = currentFormat.getFormat();
		if (type == null && curr == null){
			formatCombo.select(0);
		}else if (curr == null && type == DataType.INTEGER){
			formatCombo.select(1);
		}else if (curr == null && type == DataType.DOUBLE){
			formatCombo.select(2);
		}else if (curr != null){
			formatCombo.select(3);
			txtCustom.setText(curr.toPattern());
		}
		comboSelected();
		
		//add controls to composite as necessary
		 return composite;
	}
	
	private void comboSelected(){
		boolean hide = true;
		int index = formatCombo.getSelectionIndex();
		if (index == 0 ){
			selectedDataType = null;
			custom = null;
		}else if (index == 1){
			selectedDataType = DataType.INTEGER;
			custom = null;
		}else if (index == 2){
			selectedDataType = DataType.DOUBLE;
			custom = null;
		}else if (index == 3){
			selectedDataType = null;
			custom = null;
			hide = false;
		}
		
		lblCustom.setVisible(!hide);
		txtCustom.setVisible(!hide);
		lblInfo.setVisible(!hide);
	}
	
	public ValueFormatter.DataType getSelectedDataType(){
		return selectedDataType;
	}
	
	public DecimalFormat getCustom(){
		return this.custom;
	}
	
}
