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
package org.locationtech.udig.style.sld.raster.editor;

import java.text.DecimalFormat;

import org.locationtech.udig.style.sld.raster.editor.ValueFormatter.DataType;
import org.locationtech.udig.style.sld.raster.internal.Messages;
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
			
			if (txtCustom.getText().contains(",")){ //$NON-NLS-1$
				invalid = Messages.FormatDialog_CommaInvalidFormatError;
			}
			try{
				custom = ((DecimalFormat)DecimalFormat.getInstance());
				custom.applyPattern(txtCustom.getText());
			}catch (Exception ex){
				invalid = Messages.FormatDialog_InvalidFormatError + ex.getLocalizedMessage();
			}
		}else{
			custom = null;
		}
		
		if (invalid != null){
			MessageDialog.openError(getShell(), Messages.FormatDialog_ErrorDialotTitle, invalid);
			return;
		}
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.FormatDialog_DialogTitle);
		setMessage(Messages.FormatDialog_DialogMessage);
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Composite main = new Composite(composite, SWT.BORDER);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		main.setLayout(new GridLayout(2, false));
		
		Label lbl = new Label(main, SWT.NONE);
		lbl.setText(Messages.FormatDialog_FormatTypeLabel);
		
		formatCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		formatCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		formatCombo.add(Messages.FormatDialog_RawOp);
		formatCombo.add(Messages.FormatDialog_IntegerOp);
		formatCombo.add(Messages.FormatDialog_DoubleOp);
		formatCombo.add(Messages.FormatDialog_CustomOp);
		
		lblCustom = new Label(main, SWT.NONE);
		lblCustom.setText(Messages.FormatDialog_CustomLabel);
		
		txtCustom = new Text(main, SWT.BORDER);
		txtCustom.setText(Messages.FormatDialog_CustomFormat);
		txtCustom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// spacer label
		new Label(main, SWT.NONE);
		
		
		lblInfo = new Label(main, SWT.NONE | SWT.WRAP);
		lblInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		((GridData)lblInfo.getLayoutData()).widthHint = 100;
		lblInfo.setText(Messages.FormatDialog_CustomHelp);
		
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
