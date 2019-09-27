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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.measure.Unit;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.EditToolHandler;
//import es.axios.geotools.util.UnitList;
import org.locationtech.udig.tools.feature.util.UnitList;
//import es.axios.udig.ui.commons.message.InfoMessage.Type;
//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.commons.message.InfoMessage;
import org.locationtech.udig.tools.parallel.internal.InfoMessage;
import org.locationtech.udig.tools.parallel.internal.InfoMessage.Type;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;

/**
 * Abstract composite for the parameters view of the precision tools.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public abstract class AbstractParametersComposite extends Composite implements Observer {

	/**
	 * The thread that creates the SWT controls, is needed because only the
	 * creator thread could edit SWT controls.
	 */
	protected Thread					fatherThread			= null;
	protected PrecisionToolsContext		toolContext				= null;
	protected IEditManagerListener		editManagerListener		= null;
	protected IToolContext				context					= null;

	protected CLabel					messageImage			= null;
	protected CLabel					messageText				= null;

	protected Composite					compositeLegend			= null;
	protected Composite					compositeOperations		= null;

	protected ViewForm					viewForm				= null;
	protected CLabel					labelReferenceLine		= null;

	protected String					referenceLine			= "";		//$NON-NLS-1$
	protected String					referenceLineToolTip	= "";		//$NON-NLS-1$
	private Set<Entry<String, Unit<?>>>	unitset;
	private boolean						loadComplete			= false;

	public AbstractParametersComposite(Composite parent, int style) {

		super(parent, style);
		init();
		this.setVisible(false);
	}

	/**
	 * Initializes the listener and layout.
	 */
	private void init() {

		// Store the thread which creates the controls.
		this.fatherThread = Thread.currentThread();
		initListener();

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 3;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.BEGINNING;

		viewForm = new ViewForm(this, SWT.NONE);
		viewForm.setLayout(gridLayout);
		viewForm.setLayoutData(gridData2);

		createCompositeLegend(viewForm);
		viewForm.setTopLeft(compositeLegend);

		createCompositeOperations(viewForm);
		viewForm.setContent(compositeOperations);

		initializeUnitSet();
	}

	private void initializeUnitSet() {

		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {

				Set<Unit<?>> commonLengthUnits;
				try {
					commonLengthUnits = UnitList.getInstance().getCommonLengthUnits();
					SortedMap<String, Unit<?>> units = new TreeMap<String, Unit<?>>();
					for (Unit<?> unit : commonLengthUnits) {
						units.put(UnitList.getInstance().getUnitName(unit), unit);
					}

					unitset = units.entrySet();
					loadComplete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	protected String getUnitName(Unit<?> mapUnits) {

		String shorUnitName = ""; //$NON-NLS-1$

		if (loadComplete) {

			for (Entry<String, Unit<?>> entry : unitset) {
				if (entry.getValue().equals(mapUnits)) {
					shorUnitName = entry.getKey();
				}
			}
		}

		return shorUnitName;
	}

	private void createCompositeOperations(Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 4;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.FILL;

		compositeOperations = new Composite(parent, SWT.NONE);
		compositeOperations.setLayout(gridLayout);
		compositeOperations.setLayoutData(gridData2);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 4;
		gridData.verticalAlignment = GridData.FILL;

		labelReferenceLine = new CLabel(compositeOperations, SWT.NONE);
		labelReferenceLine.setLayoutData(gridData);
		labelReferenceLine.setText(Messages.PrecisionTool_reference_line + ":" + referenceLine); //$NON-NLS-1$
		labelReferenceLine.setToolTipText(Messages.PrecisionTool_reference_line);
	}

	private void createCompositeLegend(Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalSpan = 3;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.BEGINNING;

		compositeLegend = new Composite(parent, SWT.NONE);
		compositeLegend.setLayout(gridLayout);
		compositeLegend.setLayoutData(gridData2);

		messageImage = new CLabel(compositeLegend, SWT.NONE);
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.BEGINNING;
		gridData7.minimumWidth = 30;
		gridData7.widthHint = 30;
		messageImage.setLayoutData(gridData7);

		messageText = new CLabel(compositeLegend, SWT.NONE);
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.verticalAlignment = GridData.FILL;
		messageText.setLayoutData(gridData8);
		messageText.setFont(JFaceResources.getDialogFont());

		InfoMessage infoMessage = new InfoMessage(Messages.PrecisionParallel_InitialMessage, Type.INFORMATION);
		messageImage.setImage(infoMessage.getImage());
		messageText.setText(infoMessage.getText());

	}

	/**
	 * Add an empty label.
	 */
	protected void addEmptyLabel(Composite parent) {

		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.verticalAlignment = GridData.BEGINNING;

		CLabel lbl = new CLabel(this, SWT.NONE);
		lbl.setLayoutData(gridData1);
		lbl.setText(""); //$NON-NLS-1$
	}

	/**
	 * Set the {@link PrecisionToolsContext}, add this composite as observer and
	 * show its widget.
	 * 
	 * @param toolContext
	 */
	public void setToolContext(PrecisionToolsContext toolContext) {

		assert toolContext != null : "Can not be null"; //$NON-NLS-1$

		this.toolContext = toolContext;
		this.toolContext.addObserver(this);
		this.setVisible(true);
		setCustomToolContext();
	}

	/**
	 * Each subclass set it's specific context.
	 */
	protected abstract void setCustomToolContext();

	/**
	 * Creates the common widget for all the composites.
	 */
	protected void createContent() {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.BEGINNING;

		Composite composite = new Composite(compositeOperations, SWT.NONE);
		composite.setLayout(gridLayout);
		composite.setLayoutData(gridData);
	}

	/**
	 * Apply the changes
	 */
	protected void applyChanges() {

		// implemented on each subclass
	}

	/**
	 * Rollback, cancel the current changes.
	 */
	public void discardChanges() {

		// implemented on each subclass
	}

	/**
	 * Commit. Not a real commit, it makes the same as a double-click, add the
	 * feature to the layer.
	 */
	public void acceptChanges() {

		if (toolContext == null) {
			return;
		}

		EditToolHandler handler = toolContext.getHandler();

		List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();

		commands.add(handler.getCommand(handler.getAcceptBehaviours()));
		UndoableComposite undoableComposite = new UndoableComposite(commands);

		undoableComposite.setMap(handler.getContext().getMap());
		handler.getContext().sendASyncCommand(undoableComposite);

		handler.getContext().getViewportPane().repaint();
	}

	/**
	 * Display an information message on the view form.
	 */
	protected abstract void displayMessage(Type type);

	/**
	 * Clears the data showed on the composite.
	 */
	protected abstract void clearData();

	/**
	 * Fill the object with data.
	 */
	protected abstract void populate();

	/**
	 * Updates the composite.
	 */
	public abstract void update(Observable o, Object arg);

	/**
	 * Creates the default listeners.
	 */
	private void initListener() {

		this.editManagerListener = new IEditManagerListener() {

			public void changed(EditManagerEvent event) {

				updateEditManagerAction(event);
			}
		};
	}

	/**
	 * When a roll back occurs or commit.
	 */
	private void updateEditManagerAction(EditManagerEvent event) {

		int eventType = event.getType();

		switch (eventType) {

		case EditManagerEvent.POST_ROLLBACK:
		case EditManagerEvent.POST_COMMIT:

			clearData();
			break;
		default:
			break;
		}
	}

	public void setContext(IToolContext context) {

		IEditManager editManager;
		if (context == null) {
			// initialize or reinitialize
			editManager = getCurrentEditManager();
			if (editManager != null) {
				removeListenerFrom(editManager);
			}
		} else {
			// sets maps and its layers as current
			editManager = context.getEditManager();
			if (editManager != null) {

				addListenersTo(editManager);
			}
		}
		this.context = context;

	}

	/**
	 * Removes the listener from the edit manager.
	 * 
	 * @param editManager
	 */
	private void removeListenerFrom(IEditManager editManager) {

		assert editManager != null;
		assert this.editManagerListener != null;

		editManager.removeListener(this.editManagerListener);
	}

	/**
	 * Get the current edit manager.
	 * 
	 * @return
	 */
	private IEditManager getCurrentEditManager() {

		if (this.context == null) {
			return null;
		}
		return context.getEditManager();
	}

	/**
	 * Add listener to the edit manager.
	 * 
	 * @param editManager
	 */
	private void addListenersTo(IEditManager editManager) {

		assert editManager != null;
		assert this.editManagerListener != null;

		editManager.addListener(this.editManagerListener);
	}

}
