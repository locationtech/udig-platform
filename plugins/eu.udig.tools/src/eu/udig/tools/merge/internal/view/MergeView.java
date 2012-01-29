/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
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
package eu.udig.tools.merge.internal.view;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.IUDIGView;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.StatusBar;
import eu.udig.tools.merge.MergeContext;

/**
 * This view shows the features to merge.
 * 
 * <p>
 * The view allows to select the attributes of the source features that will be merge in the 
 * target feature (the merge feature).
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeView extends ViewPart implements IUDIGView {

	public static final String	ID				= "eu.udig.tools.merge.internal.view.MergeView";	//$NON-NLS-1$

	private MergeComposite		mergeComposite	= null;

	private CancelButtonAction	cancelButton	= null;
	private MergeButtonAction	mergeButton		= null;
	private MergeContext		mergeContext	= null;
	private String				message;

	/** The merge operation is possible if this variable is sets in true value */
	private boolean 			canMerge;

	@Override
	public void createPartControl(Composite parent) {

		this.mergeComposite = new MergeComposite(parent, SWT.NONE);

		this.mergeComposite.setView(this);

		createActions();
		createToolbar();

		//this.doMergeButton.setEnabled(false);
	}

	private void createToolbar() {

		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(mergeButton);
		toolbar.add(cancelButton);

	}

	private void createActions() {

		this.mergeButton = new MergeButtonAction();
		this.cancelButton = new CancelButtonAction();
	}

	private class CancelButtonAction extends Action {

		public CancelButtonAction() {

			setToolTipText(Messages.MergeView_cancel_tool_tip);
			String imgFile = "images/reset_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
		}

		/**
		 * closes the view
		 */
		@Override
		public void run() {
			try {
				
				ApplicationGIS.getView(false, MergeView.ID);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView(MergeView.ID);
				page.hideView(viewPart);
			} finally {
				IToolContext context=  getContext();
				UndoableMapCommand clearSelectionCommand = context.getSelectionFactory().createNoSelectCommand();

				context.sendASyncCommand(clearSelectionCommand);
			}		
		}
	}

	private class MergeButtonAction extends Action {

		public MergeButtonAction() {

			setToolTipText(Messages.MergeView_finish_tool_tip);
			String imgFile = "images/apply_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
		}

		/**
		 * Executes the merge command
		 */
		@Override
		public void run() {

			// sets the command using the features present in the merge builder
			IToolContext context = getContext();
			final ILayer layer = context.getSelectedLayer();

			MergeFeatureBuilder mergeBuilder = mergeComposite.getMergeBuilder();
			final List<SimpleFeature> sourceFeatures = mergeBuilder.getSourceFeatures();
			final SimpleFeatureCollection sourceFeaturesCollection = DataUtilities.collection(sourceFeatures);

			final SimpleFeature mergedFeature = mergeBuilder.buildMergedFeature();

			MergeFeaturesCommand cmd = MergeFeaturesCommand.getInstance(layer, sourceFeaturesCollection, mergedFeature);

			context.getMap().sendCommandASync(cmd);

			StatusBar.setStatusBarMessage(context, Messages.MergeTool_successful);
			
			context.getViewportPane().repaint();
		}
	}

	@Override
	public void setFocus() {

		// TODO Auto-generated method stub
	}

	/**
	 * Set the mergeBuilder that contains all the data and populate the
	 * composite with these data.
	 * 
	 * @param builder
	 */
	public void setBuilder(MergeFeatureBuilder builder) {

		this.mergeComposite.setBuilder(builder);
	}


	/**
	 * Enables the merge button for merge the features depending on the boolean value.
	 * <ul>
	 * <li>It should be select two or more feature</li>
	 * </ul>
	 */
	protected void canMerge(boolean bValue) {
		
		this.canMerge = bValue;
		this.mergeButton.setEnabled(this.canMerge);
	}

	/**
	 * displays the error message
	 */
	private void handleError(IToolContext context, MapMouseEvent e) {

		AnimationUpdater.runTimer(context.getMapDisplay(), new MessageBubble(e.x, e.y, this.message, PreferenceUtil
					.instance().getMessageDisplayDelay())); //$NON-NLS-1$
	}




//	FIXME
//	public void deleteFromMergeList(List<SimpleFeature> featureToDeleteList) {
//		for (SimpleFeature simpleFeature : featureToDeleteList) {
//			deleteFromMergeList(simpleFeature);
//		}
//	}
	
	/**
	 * Called when the delete button is pressed. If a feature is selected on the
	 * tree view with the source features, it is deleted from there and launched
	 * again the mergeBuilder.
	 * 
	 * @param featureToDelete 
	 */
// FIXME Not used	
//	public void deleteFromMergeList(SimpleFeature featureToDelete) {
//
//		if (!canDelete(featureToDelete)) {
//
//			return;
//		}
//		addDeletedFeature(featureToDelete);
//
//		unselect(mergeContext.getToolContext());
//	}
	
	/**
	 * Add the features the merge feature list
	 * @param sourceFeatures
	 */
	public void addSourceFeatures(List<SimpleFeature> sourceFeatures, ILayer layer) {

		assert sourceFeatures != null;
		
		this.mergeComposite.addSourceFeatures(sourceFeatures, layer);
	}

	/**
	 * Sets the set of feature to merge. 
	 * 
	 * @param selectedFeatures
	 */
	public void display(List<SimpleFeature> selectedFeatures, ILayer layer) {
			
		this.mergeComposite.display(selectedFeatures, layer);
	}

	
	/**
	 * Checks if the feature to be deleted from the list could be deleted. If
	 * there is no selection or if it's only one feature on the list, will
	 * return false.
	 * 
	 * @param featureToDelete
	 * @return
	 */
//	private boolean canDelete(SimpleFeature featureToDelete) {
//
//		boolean isValid = true;
//		this.message = "";
//		
//		if (featureToDelete == null) {
//			// there is any feature to delete.
//			this.message = Messages.MergeFeatureView_no_feature_to_delete;
//			return false;
//		}
//		List<SimpleFeature> sourceFeatures = this.mergeComposite.getSourceFeatures();
//		if (sourceFeatures.size() == 1) {
//
//			this.message = Messages.MergeFeatureView_cant_remove;
//			isValid = false;
//		}
//		
//		this.mergeComposite.setMessage(this.message, IMessageProvider.WARNING);
//
//		return isValid;
//	}


	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public void editFeatureChanged(SimpleFeature feature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContext(IToolContext newContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IToolContext getContext() {
		
		return this.mergeContext.getToolContext();
	}

	public void setMergeContext(MergeContext mergeContext) {

		this.mergeContext = mergeContext;
	}

}
