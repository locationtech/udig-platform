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


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.style.raster.Activator;
import net.refractions.udig.style.raster.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;

/**
 * Dialog for computing the unique values
 * in a raster.
 * 
 * @author Emily Gouge (Refractions Research Inc.)
 *
 */
public class UniqueValuesDialog extends TitleAreaDialog{
	/*
	 * Maximum value for warning users to limit sample size
	 */
	private static final Long WARN_VALUE = 1000000l;
	
	private static final String COMPUTE_LABEL = Messages.UniqueValuesDialog_ComputingLabel;
	private Long sampleSize = 100000l;
	
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
			try{
				getShell().getDisplay().syncExec(new Runnable(){
					public void run(){
						btnRecompute.setEnabled(false);
						uniqueValues.clear();
						lstViewer.setInput(new String[]{COMPUTE_LABEL});
						lstViewer.refresh();
					}
				});
			
				HashSet<Number> v = new HashSet<Number>();
				
				try {
					GridCoverage gcRaw = layer.read(null);
					if (thisSampleSize != null){
						int rSize = (int) Math.ceil(Math.sqrt(thisSampleSize.doubleValue()));
						GridEnvelope2D gridRange = new GridEnvelope2D(new Rectangle(0,0, rSize, rSize));
						GridGeometry2D world = new GridGeometry2D(gridRange,  new ReferencedEnvelope(gcRaw.getEnvelope()));
						DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = new DefaultParameterDescriptor<GridGeometry>(
								AbstractGridFormat.READ_GRIDGEOMETRY2D.getName()
										.toString(), GridGeometry.class, null, world);

						ParameterGroup readParams = new ParameterGroup(
								new DefaultParameterDescriptorGroup(
										"Test", //$NON-NLS-1$
										new GeneralParameterDescriptor[] { gridGeometryDescriptor }));

						List<GeneralParameterValue> list = readParams.values();
						GeneralParameterValue[] values = list
								.toArray(new GeneralParameterValue[0]);
						gcRaw = layer.read(values);
					}
										
					GridCoordinates high = gcRaw.getGridGeometry().getGridRange().getHigh();
					GridCoordinates low = gcRaw.getGridGeometry().getGridRange().getLow();
					int width = high.getCoordinateValue(0) - low.getCoordinateValue(0);
					int height = high.getCoordinateValue(1) - low.getCoordinateValue(1);
					
					if (width * height > WARN_VALUE){
						final boolean[] ret = {true};
						Display.getDefault().syncExec(new Runnable(){
							@Override
							public void run() {

									if (!MessageDialog.openConfirm(getShell(), Messages.UniqueValuesDialog_ConfirmDialogTitle,
											MessageFormat.format(Messages.UniqueValuesDialog_LargeRasterWarning, new Object[]{WARN_VALUE}))){ 
										ret[0] = false;
									}
							}});
						if (!ret[0]){
							return Status.OK_STATUS;
						}
					}
					
					int recSize = 1000;
					for (int x = 0; x < width; x+=recSize){
						for (int y = 0; y < height; y += recSize){
							Rectangle r = new Rectangle(x, y, recSize, recSize);
							Raster rs = gcRaw.getRenderedImage().getData(r);
							DataBuffer df  = rs.getDataBuffer();
							for (int i = 0; i < df.getSize(); i ++){
								v.add(df.getElemDouble(i));
							}
						}
					}
				} catch (Exception e) {
					Activator.log(e.getMessage(), e);
				}

				uniqueValues.clear();
				uniqueValues.addAll(v);
				sort();
			}finally{
				getShell().getDisplay().syncExec(new Runnable(){
					@Override
					public void run() {
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

