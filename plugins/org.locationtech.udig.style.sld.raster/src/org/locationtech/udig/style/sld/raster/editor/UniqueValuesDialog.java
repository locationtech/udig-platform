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


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.style.sld.raster.SLDRasterPlugin;
import org.locationtech.udig.style.sld.raster.internal.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * Dialog for computing the unique values
 * in a raster.
 * 
 * @author Emily Gouge (Refractions Research Inc.)
 *
 */
public class UniqueValuesDialog extends TitleAreaDialog{
	
	private static final String COMPUTE_LABEL = Messages.UniqueValuesDialog_ComputingLabel;
	private Long sampleSize = ClassificationEngine.WARN_VALUE;
	
	private Text txtSampleSize;
	private Button chSampleSize;
	private GridCoverageReader layer;
	private ListViewer lstViewer;
	private List<Number> uniqueValues = null;
	private Button btnRecompute;
	
	/*
	 * Job to compute the unique values
	 */
	Job computeJob = new Job(Messages.UniqueValuesDialog_JobName){

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Long thisSampleSize = sampleSize;
			Set<Double> values = null;
			final ClassificationEngine engine = new ClassificationEngine();
			try{
				getShell().getDisplay().syncExec(new Runnable(){
					public void run(){
						btnRecompute.setEnabled(false);
						uniqueValues.clear();
						lstViewer.setInput(new String[]{COMPUTE_LABEL});
						lstViewer.refresh();
					}
				});
			
				try {
					values = engine.computeUniqueValues(layer, thisSampleSize, monitor);
				} catch (Exception e) {
					SLDRasterPlugin.log(e.getMessage(), e);
				}
				if (values != null){
					uniqueValues.addAll(values);
					sort();
				}

			}finally{
				if (getShell() == null){
					return Status.CANCEL_STATUS;
				}
				getShell().getDisplay().syncExec(new Runnable(){
					@Override
					public void run() {
						if (btnRecompute.isDisposed()){
							return;
						}
						setErrorMessage(engine.getLastErrorMessage());
						btnRecompute.setEnabled(true);
						lstViewer.setInput(uniqueValues);
						lstViewer.refresh();
					}			
				});
		
			}
			return Status.OK_STATUS;
		}};
		
	/**
	 * Creates a new unique values dialog
	 * @param parentShell parent
	 * @param layer grid coverate to compute unique values for
	 */
	protected UniqueValuesDialog(Shell parentShell, GridCoverageReader layer) {
		super(parentShell);
		this.layer = layer;
		this.uniqueValues = new ArrayList<Number>();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		computeJob.cancel();
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = gl.marginHeight = 20;
		main.setLayout(gl);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		
		Composite sampleSizeComp = new Composite(main, SWT.NONE);
		sampleSizeComp.setLayout(new GridLayout(3, false));
		sampleSizeComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lbls = new Label(sampleSizeComp, SWT.NONE);
		lbls.setText(Messages.UniqueValuesDialog_LimitLabel);
		lbls.setToolTipText(Messages.UniqueValuesDialog_LimitTooltip);
	
		chSampleSize = new Button(sampleSizeComp, SWT.CHECK);
		chSampleSize.setSelection(false);
		chSampleSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtSampleSize.setEnabled(chSampleSize.getSelection());
			}
				
		});
		txtSampleSize = new Text(sampleSizeComp, SWT.BORDER);
		txtSampleSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSampleSize.setText("100000"); //$NON-NLS-1$
		txtSampleSize.setEnabled(false);
		
		btnRecompute = new Button(sampleSizeComp, SWT.NONE);
		btnRecompute.setText(Messages.UniqueValuesDialog_ReCalcButtonName);
		btnRecompute.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false,3,1));
		btnRecompute.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				computeValues();
			}
		});

		Label lblSep = new Label(main, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblSep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		
		Composite lowerPanel = new Composite(main, SWT.NONE);
		lowerPanel.setLayout(new GridLayout(2, false));
		lowerPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblValues = new Label(lowerPanel, SWT.NONE);
		lblValues.setText(Messages.UniqueValuesDialog_ValuesToAddLabel);
		lblValues.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
		
		lstViewer = new ListViewer(lowerPanel);
		lstViewer.setContentProvider(ArrayContentProvider.getInstance());
		lstViewer.setLabelProvider(new LabelProvider());
		lstViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		((GridData)lstViewer.getControl().getLayoutData()).widthHint = 150;
		((GridData)lstViewer.getControl().getLayoutData()).heightHint = 200;
		
		Composite buttons = new Composite(lowerPanel, SWT.NONE);
		buttons.setLayout(new GridLayout(1, false));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		
		Button btnAdd = new Button(buttons, SWT.PUSH);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnAdd.setText(Messages.UniqueValuesDialog_AddButton);
		btnAdd.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getParentShell(), Messages.UniqueValuesDialog_AddValueDialogTitle, Messages.UniqueValuesDialog_AddValueDialogMessage, "0", new IInputValidator() { //$NON-NLS-1$
					@Override
					public String isValid(String newText) {
						try{
							Double.parseDouble(newText);
							return null;
						}catch(Exception ex){
							return Messages.UniqueValuesDialog_InvalidNumberText;
						}
					}
				});
				if (id.open() == InputDialog.OK){
					Double d = Double.valueOf(id.getValue());
					uniqueValues.add(d);
					lstViewer.refresh();
				}
			}
		});
		
		Button btnRemove = new Button(buttons, SWT.PUSH);
		btnRemove.setText(Messages.UniqueValuesDialog_RemoveButton);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnRemove.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Iterator<?> iterator = ((IStructuredSelection)lstViewer.getSelection()).iterator(); iterator
						.hasNext();) {
					Object x = (Object) iterator.next();
					uniqueValues.remove(x);
				}
				lstViewer.refresh();
			}
		});
		
		

		
		super.setTitle(Messages.UniqueValuesDialog_DialogTitle);
		super.setMessage(Messages.UniqueValuesDialog_DialogMessage);
		super.getShell().setText(Messages.UniqueValuesDialog_ShellTitle);
		
		return main;
		
	}
	
	/**
	 * Updates the provided panel with the selected colors.
	 * 
	 * @param panel must be UniqueValuesPanel
	 */
	public void updatePanel(IColorMapTypePanel panel){
		if (!(panel instanceof UniqueValuesPanel)){
			return;
		}
		
		List<ColorEntry> entries = new ArrayList<ColorEntry>();
		for (Number n :uniqueValues){
			ColorEntry ce = new ColorEntry(Color.BLACK, 1, n.doubleValue(),""); //$NON-NLS-1$
			entries.add(ce);
		}
		((UniqueValuesPanel)panel).setBreaks(entries);
	}
	
	/*
	 * sorts values
	 */
	private void sort(){
		Collections.sort(this.uniqueValues, new Comparator<Number>() {

			@Override
			public int compare(Number o1, Number o2) {
				if (o1.doubleValue() < o2.doubleValue()){
					return -1;
				}else if (o1.doubleValue() > o2.doubleValue()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private void computeValues(){
		this.sampleSize = null;
		if (chSampleSize.getSelection()){
			this.sampleSize = Long.valueOf(txtSampleSize.getText());
		}
		computeJob.cancel();
		computeJob.schedule();
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
}

