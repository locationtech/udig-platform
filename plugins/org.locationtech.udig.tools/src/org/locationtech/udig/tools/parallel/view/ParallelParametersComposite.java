/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.parallel.view;

import java.util.Observable;

import javax.measure.Unit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
//import es.axios.udig.ui.commons.message.InfoMessage.Type;
//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.i18n.Messages;
//TODO remove old imports
//import es.axios.udig.ui.commons.message.InfoMessage;
import org.locationtech.udig.tools.parallel.internal.InfoMessage;
import org.locationtech.udig.tools.parallel.internal.InfoMessage.Type;
import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisiontools.commons.view.AbstractParametersComposite;

/**
 * 
 * Main composite of the parallel view.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class ParallelParametersComposite extends AbstractParametersComposite {

	private ParallelContext		parallelContext			= null;

	private static final int	GRID_DATA_2a_WIDTH_HINT	= 200;

	private static final int	GRID_DATA_3a_WIDTH_HINT	= 185;

	private static final int	GRID_DATA_1a_WIDTH_HINT	= 90;

	private static final int	GRID_DATA_4a_WIDTH_HINT	= 115;

	private Button				buttonSwitchSide		= null;
	private Text				textDistance			= null;
	private CLabel				labelDistance			= null;
	private CLabel				labelUnit				= null;

	private String				distance				= "";	//$NON-NLS-1$

	private Unit<?>				mapUnits				= null;

	private String				unitName				= "";	//$NON-NLS-1$

	private Button				applyButton;

	public ParallelParametersComposite(Composite parent, int style) {

		super(parent, style);
		createParametersContent();
		createContent();
	}

	@Override
	protected void setCustomToolContext() {

		parallelContext = (ParallelContext) toolContext;
	}

	/**
	 * Creates widget.
	 */
	private void createParametersContent() {

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.widthHint = GRID_DATA_1a_WIDTH_HINT;
		gridData.verticalAlignment = GridData.FILL;

		labelDistance = new CLabel(compositeOperations, SWT.NONE);
		labelDistance.setLayoutData(gridData);
		labelDistance.setText(Messages.PrecisionParallel_distance + ":"); //$NON-NLS-1$
		labelDistance.setToolTipText(Messages.PrecisionParallel_distance + ":" + this.distance); //$NON-NLS-1$

		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.widthHint = GRID_DATA_2a_WIDTH_HINT;
		gridData1.verticalAlignment = GridData.FILL;

		textDistance = new Text(compositeOperations, SWT.BORDER);
		textDistance.setLayoutData(gridData1);
		textDistance.setText(distance);
		textDistance.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				// enter key
				if (e.keyCode == 13 || e.keyCode == 16777296) {
					applyChanges();
				}
			}
		});

		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = false;
		gridData3.grabExcessVerticalSpace = false;
		gridData3.widthHint = GRID_DATA_3a_WIDTH_HINT;
		gridData3.verticalAlignment = GridData.FILL;

		labelUnit = new CLabel(compositeOperations, SWT.NONE);
		labelUnit.setLayoutData(gridData3);
		labelUnit.setText(unitName);

		CLabel emptyLabel = new CLabel(compositeOperations, SWT.NONE);
		emptyLabel.setLayoutData(gridData3);

		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.widthHint = GRID_DATA_4a_WIDTH_HINT;
		gridData4.verticalAlignment = GridData.FILL;

		buttonSwitchSide = new Button(compositeOperations, SWT.NONE);
		buttonSwitchSide.setLayoutData(gridData4);
		buttonSwitchSide.setText(Messages.PrecisionParallel_buttonSwitchText);
		buttonSwitchSide.setToolTipText(Messages.PrecisionParallel_buttonSwitchToolTip);
		buttonSwitchSide.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				// Only change it when reference line exist.
				if (parallelContext.getReferenceFeature() != null) {
					parallelContext.changePosition();
				}

			}
		});
		buttonSwitchSide.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				// enter key.
				if ((e.keyCode == 13 || e.keyCode == 16777296) && parallelContext.getReferenceFeature() != null) {
					parallelContext.changePosition();
				}
			}
		});

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.END;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.widthHint = GRID_DATA_1a_WIDTH_HINT;
		gridData2.verticalAlignment = GridData.FILL;

		applyButton = new Button(compositeOperations, SWT.NONE);
		applyButton.setLayoutData(gridData2);
		applyButton.setText(Messages.PrecisionTool_apply_text);
		applyButton.setToolTipText(Messages.PrecisionTool_apply_tooltip_text);
		applyButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				applyChanges();
			}
		});

		applyButton.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				// enter key.
				if (e.keyCode == 13 || e.keyCode == 16777296) {
					applyChanges();
				}
			}
		});

	}

	/**
	 * Rollback
	 */
	@Override
	public void discardChanges() {

		if (parallelContext == null) {
			return;
		}
		if (parallelContext.getReferenceFeature() != null) {
			parallelContext.initContext();
		}

	}

	@Override
	protected void applyChanges() {

		if (parallelContext.getReferenceFeature() == null) {
			displayMessage(Type.ERROR);
			return;
		}

		distance = textDistance.getText();
		Double d;
		try {
			d = Double.valueOf(distance);
		} catch (NumberFormatException e) {
			InfoMessage message = new InfoMessage(Messages.PrecisionParallel_error_distance_NaN, Type.ERROR);
			messageImage.setImage(message.getImage());
			messageText.setText(message.getText());
			parallelContext.setMode(PrecisionToolsMode.ERROR);
			parallelContext.update(PrecisionToolsContext.UPDATE_VIEW);
			return;
		}

		if (d == 0) {
			InfoMessage message = new InfoMessage(Messages.PrecisionParallel_error_distance_zero, Type.ERROR);
			messageImage.setImage(message.getImage());
			messageText.setText(message.getText());
			parallelContext.setMode(PrecisionToolsMode.ERROR);
			parallelContext.update(PrecisionToolsContext.UPDATE_VIEW);
			return;
		}
		try {
			parallelContext.calculateParallelCurve(d);
			parallelContext.update(PrecisionToolsContext.UPDATE_LAYER);
		} catch (IllegalArgumentException iae) {
			parallelContext.setMode(PrecisionToolsMode.ERROR);
			displayMessage(iae.getMessage(), Type.ERROR);
		}
	}

	/**
	 * Initializes the parallel context and clear the data showed in the view.
	 */
	@Override
	protected void clearData() {

		if (parallelContext != null) {
			parallelContext.initContext();
			referenceLine = ""; //$NON-NLS-1$
			referenceLineToolTip = ""; //$NON-NLS-1$
			distance = ""; //$NON-NLS-1$
			unitName = ""; //$NON-NLS-1$

			populate();
		}
	}

	@Override
	protected void populate() {

		Display.findDisplay(fatherThread).asyncExec(new Runnable() {

			public void run() {

				if (!labelReferenceLine.isDisposed()) {
					labelReferenceLine.setText(Messages.PrecisionTool_reference_line + ": " + referenceLine); //$NON-NLS-1$
					labelReferenceLine.setToolTipText(Messages.PrecisionTool_reference_line + ": " //$NON-NLS-1$
								+ referenceLineToolTip);
				}

				if (!textDistance.isDisposed()) {
					textDistance.setText(distance);
				}
				if (!labelUnit.isDisposed()) {
					labelUnit.setText(unitName);
					labelUnit.setToolTipText(getUnitName(mapUnits));
				}
			}
		});
	}

	/**
	 * Reflect changes made on the map in the view. Set the parameters, later
	 * other event will updates the changes.
	 */
	@Override
	public void update(Observable o, Object arg) {

		if (PrecisionToolsContext.UPDATE_VIEW.equals(arg)) {

			return;
		}
		// Only will update data when the ParallelState is WAITING (have a
		// reference line and is
		// waiting for initial coor) and when the state is READY ( have a
		// reference line and initial
		// coor)
		if (parallelContext.mode == PrecisionToolsMode.BUSY || parallelContext.mode == PrecisionToolsMode.EDITING) {
			return;
		}

		if (parallelContext.getReferenceFeature() != null) {
			this.referenceLine = parallelContext.getFeatureText();
			this.referenceLineToolTip = parallelContext.getFeatureToolTip();
		} else {
			this.referenceLine = ""; //$NON-NLS-1$
			this.referenceLineToolTip = ""; //$NON-NLS-1$
		}

		if (parallelContext.getInitialCoordinate() != null) {

			this.distance = String.valueOf(parallelContext.getDistance());

		} else {

			this.distance = ""; //$NON-NLS-1$

		}

		if (parallelContext.getUnits() != null) {

			mapUnits = parallelContext.getUnits();

			// unitName = getUnitName( mapUnits);
			unitName = String.valueOf(mapUnits);
		} else {

			unitName = ""; //$NON-NLS-1$
		}

		populate();
		if (PrecisionToolsContext.UPDATE_ERROR.equals(arg)) {
			displayMessage(parallelContext.getErrorMessage(), Type.ERROR);
		} else {
			displayMessage(Type.INFORMATION);
		}
	}

	@Override
	protected void displayMessage(Type type) {

		if (parallelContext.getReferenceFeature() == null) {

			displayMessage(Messages.PrecisionParallel_InitialMessage, type);
		} else if (parallelContext.getInitialCoordinate() == null) {

			displayMessage(Messages.PrecisionParallel_Set_Distance, type);
		} else {

			displayMessage(Messages.PrecisionParallel_Set_Another_Distance, type);
		}
	}

	protected void displayMessage(String text, Type type) {

		final InfoMessage message = new InfoMessage();

		message.setText(text);
		message.setType(type);

		Display.findDisplay(fatherThread).asyncExec(new Runnable() {

			public void run() {

				if (!messageText.isDisposed()) {
					messageText.setText(message.getText());
				}
				if (!messageImage.isDisposed()) {
					messageImage.setImage(message.getImage());
				}
			}
		});
	}
}
