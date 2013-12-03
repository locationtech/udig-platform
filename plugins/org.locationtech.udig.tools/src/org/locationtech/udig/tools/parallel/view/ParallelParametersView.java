/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel.view;

import java.util.Observable;
import java.util.Observer;

import org.locationtech.udig.project.ui.IUDIGView;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.simple.SimpleFeature;

//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.editingtools.precisionparallels.PrecisionParallelTool;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
import org.locationtech.udig.tools.parallel.ParallelTool;
import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsMode;



/**
 * The view of the {@link ParallelTool}.
 * 
 * While the user is working with the tool, its data, like reference line,
 * initial coordinate,etc... is showed on this view. Purpose of this view, is to
 * show the data, and also, if the user changes the data showed here, those
 * changes will be reflected on the map.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class ParallelParametersView extends ViewPart implements IUDIGView, Observer {

	private ParallelContext				parallelContext		= null;
	private ParallelParametersComposite	parametersComposite	= null;
	private IToolContext				context				= null;
	public static final String			id					= "org.locationtech.udig.tools.parallel.view.ParallelParametersView";	//$NON-NLS-1$

	private cancelButtonAction			cancelButton		= null;
	private acceptButtonAction			acceptButton		= null;

	/**
	 * Set the parallel context after creating it. Also set parameters view
	 * context that will set the context and add itself as observer.
	 * 
	 * @param context
	 */
	public void setParallelContext(ParallelContext context) {

		assert context != null;

		this.parallelContext = context;
		this.parametersComposite.setToolContext(this.parallelContext);
		this.parallelContext.addObserver(this);
	}

	@Override
	public void createPartControl(Composite parent) {

		parametersComposite = new ParallelParametersComposite(parent, SWT.NONE);

		createActions();
		createToolbar();
	}

	private void createToolbar() {

		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(acceptButton);
		toolbar.add(cancelButton);

	}

	private void createActions() {

		this.acceptButton = new acceptButtonAction();
		this.cancelButton = new cancelButtonAction();
		enableAcceptButton(false);
	}

	@Override
	public void setFocus() {

	}

	public void editFeatureChanged(SimpleFeature feature) {

	}

	public IToolContext getContext() {

		return this.context;
	}

	public void setContext(IToolContext newContext) {

		this.context = newContext;
		this.parametersComposite.setContext(newContext);
	}

	/**
	 * Create the cancelButtonAction
	 * 
	 */
	private class cancelButtonAction extends Action {

		public cancelButtonAction() {

			setToolTipText(Messages.PrecisionTool_cancel_tooltip_text);
			String imgFile = "images/reset_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(ParallelParametersView.class, imgFile));

		}

		@Override
		public void run() {
			parametersComposite.discardChanges();
		}

	}

	/**
	 * Creates the acceptButtonAction
	 * 
	 */
	private class acceptButtonAction extends Action {

		public acceptButtonAction() {

			setToolTipText(Messages.PrecisionTool_ok_tooltip_text);
			String imgFile = "images/apply_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(ParallelParametersView.class, imgFile));

		}

		@Override
		public void run() {
			parametersComposite.acceptChanges();
		}
	}

	/**
	 * Enable the runButton
	 * 
	 * @param enable
	 */
	public void enableAcceptButton(boolean enable) {

		if (this.acceptButton != null) {
			this.acceptButton.setEnabled(enable);
		}
	}

	public void update(Observable o, Object arg) {

		boolean enable;

		if (parallelContext.getMode() == PrecisionToolsMode.READY) {
			enable = true;
		} else {
			enable = false;
		}
		enableAcceptButton(enable);

	}

}
