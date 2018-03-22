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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.style.sld.raster.SLDRasterPlugin;
import org.locationtech.udig.style.sld.raster.internal.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * Classify dialog for classifying raster values
 * into different bins.
 * 
 * @author Emily
 *
 */
public class ClassifyDialog extends TitleAreaDialog{

	private static final String GENERATE_LABEL = Messages.ClassifyDialog_GenerateBreaksButtonText;
	

	
	private ComboViewer cmbClass;
	private ListViewer cmbRanges;
	private Text txtIgnore ;
	private Text txtSampleSize;
	
	private Label lblOp;
	private Text txtOp;
	private Button btnCompute; 
	private Button chSampleSize;
	private List<Double> breaks ;
	private GridCoverageReader layer;
	
	private ClassifyFunction currentSelection = null;
	private Number currentOption = null;
	private String currentIgnore = null;
	private Long currentSampleSize = null;
	
	private double[] defaultNoData = null;
	/**
	 * Supported classifications
	 *
	 */
	private enum ClassifyFunction{
		EQUAL_INTERNAL(Messages.ClassifyDialog_EqualIntervalLabel, Messages.ClassifyDialog_NumberofIntervalsLabel),
		DEFINED_INTERVAL(Messages.ClassifyDialog_DefinedIntervalLabel, Messages.ClassifyDialog_IntervalSizeLabel),
		QUANTILE(Messages.ClassifyDialog_QuantileLabel, Messages.ClassifyDialog_NumberOfBinsLabel);
		String guiName;
		String opName;
		
		private ClassifyFunction(String guiName, String opName){
			this.guiName = guiName;
			this.opName = opName;
		}
	}
	
	/**
	 * Creates a new classify dialog
	 * @param parentShell parent shell
	 * @param layer layer to classify
	 */
	public ClassifyDialog(Shell parentShell, GridCoverageReader layer, double[] noDataValues) {
		super(parentShell);
		this.layer = layer;
		this.defaultNoData = noDataValues;
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		computeValuesJob.cancel();
		return super.close();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		breaks = new ArrayList<Double>();
		
		Composite main = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = gl.marginHeight = 20;
		main.setLayout(gl);
		
		Label lbl = new Label(main, SWT.NONE);
		lbl.setText(Messages.ClassifyDialog_ClassificationFunctionLabel);
		
		cmbClass = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		cmbClass.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cmbClass.setContentProvider(ArrayContentProvider.getInstance());
		cmbClass.setLabelProvider(new LabelProvider(){
			public String getText(Object x){
				if (x instanceof ClassifyFunction){
					return ((ClassifyFunction) x).guiName;
				}
				return super.getText(x);
			}
		});
		cmbClass.setInput(ClassifyFunction.values());
		cmbClass.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				lblOp.setText(getCurrentSelection().opName);
				lblOp.getParent().layout();
			}
		});
		
		lblOp = new Label(main, SWT.NONE);
		lblOp.setText(ClassifyFunction.EQUAL_INTERNAL.opName);
		
		txtOp = new Text(main, SWT.BORDER);
		txtOp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtOp.setText("10"); //$NON-NLS-1$
		
		Label lbl4 = new Label(main, SWT.NONE);
		lbl4.setText(Messages.ClassifyDialog_ValuesToIgnoreLabel + "*"); //$NON-NLS-1$
	
		txtIgnore = new Text(main, SWT.BORDER);
		txtIgnore .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (defaultNoData == null){
			txtIgnore.setText(String.valueOf(IColorMapTypePanel.DEFAULT_NO_DATA));
		}else{
			StringBuilder sb = new StringBuilder();
			for (double d : defaultNoData){
				sb.append(d);
				sb.append(","); //$NON-NLS-1$
			}
			//remove last ","
			if (sb.length() > 0){
				sb.deleteCharAt(sb.length() -1);
			}
			txtIgnore.setText(sb.toString());
		}
		
		Label lbls = new Label(main, SWT.NONE);
		lbls.setText(Messages.ClassifyDialog_LimitSizeLabel);
		lbls.setToolTipText(Messages.ClassifyDialog_LimitSizeTooltip);
	
		Composite compSample = new Composite(main, SWT.NONE);
		GridLayout gla = new GridLayout(2, false);
		gla.marginHeight = gla.marginWidth = 0;
		compSample.setLayout(gla);
		compSample.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		chSampleSize = new Button(compSample, SWT.CHECK);
		chSampleSize.setSelection(false);
		chSampleSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtSampleSize.setEnabled(chSampleSize.getSelection());
			}
				
		});
		txtSampleSize = new Text(compSample, SWT.BORDER);
		txtSampleSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSampleSize.setText("100000"); //$NON-NLS-1$
		txtSampleSize.setEnabled(false);
		
		
		btnCompute = new Button(main, SWT.PUSH);
		btnCompute.setText(GENERATE_LABEL);
		btnCompute.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1));
		btnCompute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				computeValues();
			}
		});
		
		
		Label lblSep = new Label(main, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblSep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblRange = new Label(main, SWT.NONE);
		lblRange.setText(Messages.ClassifyDialog_BreaksLabel);
		lblRange.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		cmbRanges = new ListViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd =new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd.widthHint = 150;
		gd.heightHint = 200;
		cmbRanges.getControl().setLayoutData(gd);
		cmbRanges.setLabelProvider(new LabelProvider());
		cmbRanges.setContentProvider(ArrayContentProvider.getInstance());
		cmbRanges.setInput(breaks);
		
		
		Label lbl3 = new Label(main, SWT.WRAP);
		lbl3.setText("*" + Messages.ClassifyDialog_IgnoreValuesInfo); //$NON-NLS-1$
		lbl3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		cmbClass.setSelection(new StructuredSelection(ClassifyFunction.EQUAL_INTERNAL));
		
		setMessage(Messages.ClassifyDialog_DialogMessage);
		setTitle(Messages.ClassifyDialog_DialogTitle);
		getShell().setText(Messages.ClassifyDialog_ShellTitle);
		return main;
	}
	
	/**
	 * 
	 * @return current selected classification function
	 */
	private ClassifyFunction getCurrentSelection(){
		return (ClassifyFunction) ((IStructuredSelection)cmbClass.getSelection()).getFirstElement();
	}
	
	
	/*
	 * computes the breaks
	 */
	private void computeValues(){
		computeValuesJob.cancel();
		currentIgnore = txtIgnore.getText();
		currentSelection = getCurrentSelection();
		currentSampleSize = null;
		if (chSampleSize.getSelection()){
			try{
				currentSampleSize = Long.parseLong(txtSampleSize.getText());
			}catch (Exception ex){
				MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle, Messages.ClassifyDialog_ErrorMessage);
				return;
			}
		}
		
		if (currentSelection == ClassifyFunction.EQUAL_INTERNAL || currentSelection == ClassifyFunction.QUANTILE){
			try{
				currentOption = Integer.parseInt(txtOp.getText());
			}catch (Exception ex){
				MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle, MessageFormat.format(Messages.ClassifyDialog_InvalidValueOption, new Object[]{currentSelection.opName }));
				return;
			}
			if (currentOption.intValue() >= SingleBandEditorPage.MAX_ENTRIES){
				MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle, MessageFormat.format(Messages.ClassifyDialog_MaxValueError, new Object[]{SingleBandEditorPage.MAX_ENTRIES-1 }));
				return;
			}
			 
		}else if (currentSelection == ClassifyFunction.DEFINED_INTERVAL){
			try{
				currentOption = Double.parseDouble(txtOp.getText());
			}catch (Exception ex){
				MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle3, MessageFormat.format(Messages.ClassifyDialog_InvalidValueOption2, new Object[]{currentSelection.opName }));
			}
		
		}
		if (currentOption.doubleValue() <= 0){
			MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle3, MessageFormat.format(Messages.ClassifyDialog_InvalidValue, new Object[]{currentSelection.opName }));
		}
		computeValuesJob.schedule();
	}
	
	
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/**
	 * Updates the given panel with the new breaks.
	 * 
	 * @param panel must be IntervalValuesPanel or RampValuesPanel
	 */
	public void updatePanel(IColorMapTypePanel panel){
		if (panel instanceof IntervalValuesPanel ||
				panel instanceof RampValuesPanel){
			List<ColorEntry> entries = new ArrayList<ColorEntry>();
			for (Double d : breaks){
				ColorEntry ce = new ColorEntry(Color.BLACK, 1, d, ""); //$NON-NLS-1$
				entries.add(ce);
			}
			
			if (panel instanceof IntervalValuesPanel){
				((IntervalValuesPanel)panel).setBreaks(entries);
			}else if (panel instanceof RampValuesPanel){
				((RampValuesPanel)panel).setBreaks(entries);
			}
		}
	}
	
	/*
	 * Job for computing breaks
	 */
	private Job computeValuesJob = new Job(Messages.ClassifyDialog_ComputeBreaksJobName){

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			ClassifyFunction function = currentSelection;
			Number op = currentOption;
			
			Long sampleSize = currentSampleSize;
			String ignore = currentIgnore;
			
			List<Double> toIgnore = new ArrayList<Double>();
			if (ignore.trim().length() > 0){
				String[] str = ignore.split(","); //$NON-NLS-1$
				for (int i = 0; i < str.length; i ++){
					try{
						Double d = Double.parseDouble(str[i]);
						toIgnore.add(d);
					}catch (Exception ex){
						//eatme
					}
				}
			}
			double[] valuesToIgnore = new double[toIgnore.size()];
			for(int i = 0; i < toIgnore.size(); i++){
				valuesToIgnore[i] = toIgnore.get(i);
			}
			
			breaks.clear();
			final ClassificationEngine engine = new ClassificationEngine();
			if (function == null || op == null){
				return Status.CANCEL_STATUS;
			}
			try{
				Display.getDefault().syncExec(new Runnable(){
					@Override
					public void run() {
						cmbRanges.refresh();
						btnCompute.setEnabled(false);
					}});
				
				List<Double> newBreaks = null;
				if (function == ClassifyFunction.EQUAL_INTERNAL){
					newBreaks = engine.computeEqualInterval(((Integer)op).intValue(), valuesToIgnore, layer, sampleSize);
				}else if (function== ClassifyFunction.DEFINED_INTERVAL){
					newBreaks = engine.computeDefinedInterval(((Double)op).doubleValue(), valuesToIgnore, layer, sampleSize);
				}else if (function == ClassifyFunction.QUANTILE){
					newBreaks = engine.computeQuantile(((Integer)op).intValue(), valuesToIgnore, layer, sampleSize, monitor);
				}
				
				if (newBreaks == null || monitor.isCanceled()){
					return Status.CANCEL_STATUS;
				}else{
					breaks.clear();
					breaks.addAll(newBreaks);
				}
			}catch (final Exception ex){
				Display.getDefault().syncExec(new Runnable(){
					@Override
					public void run() {
						MessageDialog.openError(getShell(), Messages.ClassifyDialog_ErrorDialogTitle5, MessageFormat.format(Messages.ClassifyDialog_ErrorComputingValues, new Object[]{ex.getLocalizedMessage()}));
					}});
				SLDRasterPlugin.log("Error classifying values", ex); //$NON-NLS-1$
			}finally{
				Display.getDefault().syncExec(new Runnable(){
					@Override
					public void run() {
						if (!cmbRanges.getControl().isDisposed()){
							setErrorMessage(engine.getLastErrorMessage());
							cmbRanges.refresh();
							btnCompute.setEnabled(true);
						}
					}});
				
			}
			return Status.OK_STATUS;
		}
		
		
	};
}
