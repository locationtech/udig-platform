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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import eu.udig.tools.geometry.internal.util.GeometryUtil;
import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.DialogUtil;
import eu.udig.tools.internal.ui.util.StatusBar;
import eu.udig.tools.merge.MergeContext;

/**
 * This view shows the feature to merge.
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

	private IToolContext		context			= null;
	private MergeComposite		mergeComposite	= null;
	private MergeFeatureBuilder	mergeBuilder	= null;
	private List<SimpleFeature>	sourceFeatures	= null;


	private CancelButtonAction	cancelButton	= null;
	private FinishButtonAction	finishButton	= null;
	private MergeContext		mergeContext	= null;
	private String				message;

	@Override
	public void createPartControl(Composite parent) {

		MergeComposite composite = new MergeComposite(parent, SWT.NONE);

		this.mergeComposite = composite;
		this.mergeComposite.setView(this);

		createActions();
		createToolbar();

		this.finishButton.setEnabled(false);
	}

	private void createToolbar() {

		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		toolbar.add(finishButton);
		toolbar.add(cancelButton);

	}

	private void createActions() {

		this.finishButton = new FinishButtonAction();
		this.cancelButton = new CancelButtonAction();
	}

	private class CancelButtonAction extends Action {

		public CancelButtonAction() {

			setToolTipText(Messages.MergeView_cancel_tool_tip);
			String imgFile = "images/reset_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
		}

		@Override
		public void run() {

			mergeCancel();
		}
	}

	private class FinishButtonAction extends Action {

		public FinishButtonAction() {

			setToolTipText(Messages.MergeView_finish_tool_tip);
			String imgFile = "images/apply_co.gif"; //$NON-NLS-1$
			setImageDescriptor(ImageDescriptor.createFromFile(MergeView.class, imgFile));
		}

		@Override
		public void run() {

			mergeFinish();
		}
	}

	@Override
	public void setFocus() {

		// TODO Auto-generated method stub

	}

	public void editFeatureChanged(SimpleFeature feature) {

		// TODO Auto-generated method stub

	}

	public IToolContext getContext() {

		return this.context;
	}

	public void setContext(IToolContext newContext) {

		this.context = newContext;

	}

	/**
	 * Set the mergeBuilder that contains all the data and populate the
	 * composite with these data.
	 * 
	 * @param builder
	 */
	public void setBuilder(MergeFeatureBuilder builder) {

		assert builder != null;

		this.mergeBuilder = builder;
		this.mergeComposite.setBuilder(mergeBuilder);
		this.mergeComposite.open();
		this.finishButton.setEnabled(true);
	}


	/**
	 * Checks the merge parameters:
	 * <ul>
	 * <li>It should be select two or more feature</li>
	 * </ul>
	 * 
	 * @return true if the input are OK
	 */
	public boolean isValid() {

		this.message = ""; //$NON-NLS-1$
		boolean valid = true;

		// Must select two or more feature
		if (sourceFeatures.size() < 2) {
			this.message = Messages.MergeFeatureBehaviour_select_two_or_more;
			valid = false;
		}
		this.mergeComposite.setMessage(this.message, IMessageProvider.WARNING);
		
		return valid;
	}

	/**
	 * displays the error message
	 */
	private void handleError(IToolContext context, MapMouseEvent e) {

		AnimationUpdater.runTimer(context.getMapDisplay(), new MessageBubble(e.x, e.y, this.message, PreferenceUtil
					.instance().getMessageDisplayDelay())); //$NON-NLS-1$
	}

	/**
	 * Perform the last action of the merge. Create the new feature, delete the
	 * old ones, and add it to the layer.
	 */
	private void mergeFinish() {

		MergeFeaturesCommand cmd = null;
		final String dlgTitle = Messages.MergeTool_title_tool;

		if (!isValid()) {
			this.mergeComposite.setMessage(this.message, IMessageProvider.ERROR);
			return;
		}

		try {
			final ILayer layer = this.context.getSelectedLayer();

			SimpleFeature mergedFeature = mergeBuilder.buildMergedFeature();
			FeatureCollection<SimpleFeatureType, SimpleFeature> features = DataUtilities.collection(sourceFeatures);
			cmd = MergeFeaturesCommand.getInstance(layer, features, mergedFeature);

			this.context.getMap().sendCommandASync(cmd);

			StatusBar.setStatusBarMessage(context, Messages.MergeTool_successful);
		} catch (Exception e1) {

			final String msg = Messages.MergeTool_failed_executing + ": " + e1.getMessage(); //$NON-NLS-1$
			DialogUtil.openError(dlgTitle, msg);

			if (cmd != null) {
				try {
					cmd.rollback(new NullProgressMonitor());
				} catch (Exception e2) {
					final String msg2 = Messages.MergeTool_failed_rollback;
					DialogUtil.openError(dlgTitle, msg2);
					throw new IllegalStateException(msg2, e2);
				}
			}
			throw new IllegalStateException(e1.getMessage(), e1);
		} finally {
			mergeCancel();
			context.getViewportPane().repaint();
		}
	}

	/**
	 * Unselects the merged features
	 * 
	 * @param context
	 */
	private static void unselect(final IToolContext context) {

		UndoableMapCommand clearSelectionCommand = context.getSelectionFactory().createNoSelectCommand();

		context.sendASyncCommand(clearSelectionCommand);
	}

	/**
	 * Perform the merge cancel action. Close the view.
	 */
	private void mergeCancel() {

		try {
			ApplicationGIS.getView(false, MergeView.ID);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart viewPart = page.findView(MergeView.ID);
			page.hideView(viewPart);
		} finally {
			unselect(context);
			if (mergeContext != null) {
				mergeContext.initContext();
			}
		}
	}

	
	public void deleteFromMergeList(List<SimpleFeature> featureToDeleteList) {
		for (SimpleFeature simpleFeature : featureToDeleteList) {
			deleteFromMergeList(simpleFeature);
		}
	}
	
	/**
	 * Called when the delete button is pressed. If a feature is selected on the
	 * tree view with the source features, it is deleted from there and launched
	 * again the mergeBuilder.
	 * 
	 * @param featureToDelete 
	 */
	public void deleteFromMergeList(SimpleFeature featureToDelete) {

		if (!canDelete(featureToDelete)) {

			return;
		}
		addDeletedFeature(featureToDelete);

		// creates the builder, an launch again.
		MergeFeatureBuilder builder = createMergeBuilder();
		this.addSourceFeatures(this.sourceFeatures);
		this.setBuilder(builder);
		unselect(this.context);
	}
	
	/**
	 * Add a feature to the deleted feature list.
	 * 
	 * @param feature
	 */
	public void addDeletedFeature(SimpleFeature feature) {

		if (!this.sourceFeatures.isEmpty()) {
			this.sourceFeatures.remove(feature);
		}
	}
	
	/**
	 * Add the features the merge feature list
	 * @param sourceFeatures
	 */
	public void addSourceFeatures(List<SimpleFeature> sourceFeatures) {

		assert sourceFeatures != null;

		if(this.sourceFeatures.isEmpty()){
			this.sourceFeatures = new LinkedList<SimpleFeature>();
		}
			
		this.sourceFeatures.addAll(sourceFeatures);

	}
	public boolean contains(List<SimpleFeature> selectedFeatures) {
		return this.sourceFeatures.contains(selectedFeatures);
	}

	/**
	 * Check if the feature to be deleted from the list could be deleted. If
	 * there is no selection or if it's only one feature on the list, will
	 * return false.
	 * 
	 * @param featureToDelete
	 * @return
	 */
	private boolean canDelete(SimpleFeature featureToDelete) {

		boolean isValid = true;

		if (featureToDelete == null) {
			// there is any feature to delete.
			this.message = Messages.MergeFeatureView_no_feature_to_delete;
			return false;
		}
		if (sourceFeatures.size() == 1) {

			this.message = Messages.MergeFeatureView_cant_remove;
			isValid = false;
		}
		
		this.mergeComposite.setMessage(this.message, IMessageProvider.WARNING);

		return isValid;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("unchecked")
	private MergeFeatureBuilder createMergeBuilder() throws IllegalStateException {

		SimpleFeatureType type = sourceFeatures.get(0).getFeatureType();
		final Class<?> expectedGeometryType = type.getGeometryDescriptor().getType().getBinding();
		Geometry union;
		union = GeometryUtil.geometryUnion(DataUtilities.collection(sourceFeatures));
		try {
			union = GeometryUtil.adapt(union, (Class<? extends Geometry>) expectedGeometryType);
			final ILayer layer = this.context.getSelectedLayer();
			MergeFeatureBuilder mergeBuilder = new MergeFeatureBuilder(sourceFeatures, union, layer);
			return mergeBuilder;
		} catch (IllegalArgumentException iae) {
			throw new IllegalStateException(iae.getMessage());
		}

	}

	/**
	 * Set the merge context.
	 * 
	 * @param mergeContext
	 */
	public void setMergeContext(MergeContext mergeContext) {

		assert mergeContext != null;

		this.mergeContext = mergeContext;

	}

	@Override
	public void dispose() {

		if (mergeContext != null) {
			mergeContext.initContext();
		}
		super.dispose();
	}

}
