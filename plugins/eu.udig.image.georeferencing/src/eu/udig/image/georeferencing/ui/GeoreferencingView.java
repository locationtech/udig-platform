/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.image.georeferencing.ui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.opengis.feature.simple.SimpleFeature;

import eu.udig.image.georeferencing.internal.i18n.Messages;
import eu.udig.image.georeferencing.internal.preferences.Preferences;
import eu.udig.image.georeferencing.internal.process.MarkModelFactory;
import eu.udig.image.georeferencing.internal.ui.GeoReferencingCommand;
import eu.udig.image.georeferencing.internal.ui.GeoreferencingCommandEventChange;
import eu.udig.image.georeferencing.internal.ui.MainComposite;
import eu.udig.image.georeferencing.internal.ui.message.InfoMessage;
import eu.udig.image.georeferencing.internal.ui.message.InfoMessage.Type;

/**
 * View for Georeferencing.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
 */
public class GeoreferencingView extends ViewPart implements IUDIGView, Observer {

	public static final String		id					= "eu.udig.image.georeferencing.ui.GeoreferencingView";	//$NON-NLS-1$
	private static final String 	GCP_FILE_EXT 		= "*.gcp"; //$NON-NLS-1$
	private static final String 	LOAD_IMAGE_BUTTON 	= "images/load.png"; //$NON-NLS-1$
	private static final String 	RUN_IMAGE_BUTTON 	= "images/run.gif"; //$NON-NLS-1$
	private static final String 	SAVE_IMAGE_BUTTON 	= "images/save.png"; //$NON-NLS-1$

	private GeoReferencingCommand	cmd				= null;
	private MainComposite			mainComposite	= null;
	private IToolContext			toolContext		= null;

	private runButtonAction			runButton		= null;
	private loadButtonAction		loadButton		= null;
	private saveButtonAction		saveButton		= null;

	private IAction					runAction;
	private IAction					loadAction;
	private IAction					saveAction;

	private Thread					displayThread	= null;
	private MarkStorage 			marksStore 		= new MarkStorage();


	/**
	 * 
	 */
	public GeoreferencingView() {

		this.displayThread = Display.getCurrent().getThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		MarkModelFactory.resetIdSecuence();

		this.cmd = new GeoReferencingCommand();
		MainComposite composite = new MainComposite(this.cmd, parent, SWT.NONE | SWT.NO_FOCUS);
		composite.setLayout(new FillLayout());
		composite.pack();

		createActions();
		createToolbar(composite);

		this.mainComposite = composite;
		this.cmd.addObserver(this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	@Override
	public void dispose() {

		this.mainComposite.close();

		super.dispose();
	}

	/**
	 * Create the toolbar
	 * 
	 * @param composite
	 */
	private void createToolbar(Composite composite) {

		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(runButton);
		toolbar.add(loadButton);
		toolbar.add(saveButton);
		this.runButton.setEnabled(false);
		this.loadButton.setEnabled(false);
		this.saveButton.setEnabled(false);

		// Create the menu on the toolbar
		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager viewMenu = actionBars.getMenuManager();

		viewMenu.add(runAction);
		viewMenu.add(loadAction);
		viewMenu.add(saveAction);
	}

	/**
	 * Create actions, linking view to current map.
	 */
	private void createActions() {
		this.runButton = new runButtonAction();
		this.runAction = new Action() {

			@Override
			public void run() {
				executeGeoReferencingCommand();
			}
		};
		this.runAction.setText(Messages.GeoreferencingView_runActionText);
		this.runAction.setToolTipText(Messages.GeoreferencingView_runActionTooltip);
		String imgFile = RUN_IMAGE_BUTTON;
		this.runAction.setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));
		this.runAction.setEnabled(false);

		this.loadButton = new loadButtonAction();
		this.loadAction = new Action() {

			@Override
			public void run() {
				loadMarks();
			};
		};
		this.loadAction.setText(Messages.GeoreferencingView_loadActionText);
		this.loadAction.setToolTipText(Messages.GeoreferencingView_loadActionTooltip);
		imgFile = LOAD_IMAGE_BUTTON; 
		this.loadAction.setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));
		this.loadAction.setEnabled(false);

		this.saveButton = new saveButtonAction();
		this.saveAction = new Action() {

			@Override
			public void run() {
				saveMarks();
			};
		};
		this.saveAction.setText(Messages.GeoreferencingView_saveActionText);
		this.saveAction.setToolTipText(Messages.GeoreferencingView_saveActionTooltip);
		imgFile = SAVE_IMAGE_BUTTON; 
		this.saveAction.setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));
		this.saveAction.setEnabled(false);
	}

	protected void executeGeoReferencingCommand(){
		try{
			showEnabled(false);
			cmd.execute();
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			showEnabled(true);
		}

	}
	
	/**
	 * Create the runButtonAction
	 * 
	 */
	private class runButtonAction extends Action {


		public runButtonAction() {

			setToolTipText(Messages.GeoreferencingView_runButtonText);
			String imgFile = RUN_IMAGE_BUTTON; 
			setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));

		}

		@Override
		public void run() {
			executeGeoReferencingCommand();
		}
	}

	private class loadButtonAction extends Action {


		public loadButtonAction() {

			setToolTipText(Messages.GeoreferencingView_loadButtonText);
			String imgFile = LOAD_IMAGE_BUTTON; 
			setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));
		}

		@Override
		public void run() {

			loadMarks();
		}
	}

	private class saveButtonAction extends Action {

		public saveButtonAction() {

			setToolTipText(Messages.GeoreferencingView_saveButtonText);
			String imgFile = SAVE_IMAGE_BUTTON;
			setImageDescriptor(ImageDescriptor.createFromFile(GeoreferencingView.class, imgFile));
		}

		@Override
		public void run() {

			saveMarks();
		}

	}

	private void saveMarks() {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setFilterExtensions(new String[] { GCP_FILE_EXT }); 
		fd.setText(Messages.GeoreferencingView_saveMarksFile); 
		fd.setFilterPath(Preferences.getSaveLoadPath());

		String marksFileName = fd.open();

		if (marksFileName != null && !"".equals(marksFileName)) { //$NON-NLS-1$
			try {
				marksStore.saveMarks(cmd.getMarks(), cmd.getCRS(), marksFileName);

				// store the path
				File file = new File(marksFileName);
				Preferences.setSaveLoadPath(file.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void loadMarks() {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setFilterExtensions(new String[] { GCP_FILE_EXT }); 
		fd.setText(Messages.GeoreferencingView_openMarksFile); 
		fd.setFilterPath(Preferences.getSaveLoadPath());

		String marksFile = fd.open();

		if (marksFile != null && ! "".equals(marksFile)) { //$NON-NLS-1$

			try {
				if (this.marksStore.canLoadMarks(marksFile, cmd.getCRS())) {

					this.mainComposite.deleteAllPoints();
					this.mainComposite.createMarks(this.marksStore.loadMarks(marksFile));
				} else {
					InfoMessage message = new InfoMessage(Messages.GeoreferencingView_errorLoadingMarks
								+ this.marksStore.getLoadedCrsName(), Type.WARNING);
					this.mainComposite.setMessage(message);
				}
				// store the path
				File file = new File(marksFile);
				Preferences.setSaveLoadPath(file.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void showEnabled(boolean enabled) {

		mainComposite.setEnabled(enabled);
		if (enabled) {
			mainComposite.setCursor(null);
		} else {
			mainComposite.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
		}

	}

	public void setContext(final IToolContext newContext) {

		assert this.displayThread != null;

		Display display = Display.findDisplay(this.displayThread);
		display.syncExec(new Runnable() {

			public void run() {

				toolContext = newContext;
				mainComposite.setContext(toolContext);
			}
		});
	}

	public IToolContext getContext() {
		return this.toolContext;
	}

	/**
	 * Enable the runButton
	 * 
	 * @param enable
	 */
	private void enableRunButton(boolean enable) {
		if (this.runButton != null) {
			this.runButton.setEnabled(enable);
		}
		if (this.runAction != null) {
			this.runAction.setEnabled(enable);
		}
	}

	private void enableLoadButton(boolean enable) {
		if (this.loadButton != null) {
			this.loadButton.setEnabled(enable);
		}
		if (this.loadAction != null) {
			this.loadAction.setEnabled(enable);
		}
	}

	private void enableSaveButton(boolean enable) {
		if (this.saveButton != null) {
			this.saveButton.setEnabled(enable);
		}
		if (this.saveAction != null) {
			this.saveAction.setEnabled(enable);
		}
	}

	public void editFeatureChanged(SimpleFeature feature) {
		// nothing yet.

	}

	public void update(Observable obs, Object arg) {

		assert obs instanceof GeoReferencingCommand;

		GeoReferencingCommand cmd = (GeoReferencingCommand) obs;

		enableRunButton(cmd.canExectue());
		enableSaveButton(cmd.canSave());

		if (!(arg instanceof GeoreferencingCommandEventChange))
			return;
		GeoreferencingCommandEventChange cmdEvent = (GeoreferencingCommandEventChange) arg;

		switch (cmdEvent.getEvent()) {
		case MARK_ADDED:
			enableSaveButton(true);
			break;
		case ALL_MARKS_DELETED:
			enableSaveButton(false);
			break;
		case IMAGE_LOADED:
			enableLoadButton(true);
			break;
		case MAP_CHANGE:
			enableRunButton(false);
			enableSaveButton(false);
			enableLoadButton(false);
			break;
		case MAP_CHANGE_TO_ORIGINAL:
			enableRunButton(cmd.canExectue());
			enableSaveButton(cmd.canSave());
			enableLoadButton(cmd.canLoad());
			break;
		default:
			break;
		}
	}

}
