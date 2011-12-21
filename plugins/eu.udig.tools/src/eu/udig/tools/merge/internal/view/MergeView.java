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

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.SelectionCommandFactory;
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
import eu.udig.tools.internal.util.DialogUtil;
import eu.udig.tools.internal.util.StatusBar;
import eu.udig.tools.merge.internal.MergeContext;
import eu.udig.tools.merge.internal.MergeFeatureBuilder;
import eu.udig.tools.merge.internal.MergeFeaturesCommand;

/**
 * The view that will show the Merge UI.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeView extends ViewPart implements IUDIGView {

	private IToolContext		context			= null;
	private MergeComposite		mergeComposite	= null;
	private MergeFeatureBuilder	mergeBuilder	= null;
	private SimpleFeature[]		sourceFeatures	= null;

	public static final String	id				= "es.axios.udig.ui.editingtools.merge.ui.MergeView";	//$NON-NLS-1$

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

	public void setSourceFeatures(SimpleFeature[] sourceFeatures) {

		assert sourceFeatures != null;

		this.sourceFeatures = sourceFeatures;

	}

	/**
	 * Valid the merge data:
	 * <ul>
	 * <li>Must select two or more feature</li>
	 * <li>The features selected are polygons they must intersect</li>
	 * </ul>
	 * 
	 * @return true if the input are OK
	 */
	public boolean isValid() {

		this.message = ""; //$NON-NLS-1$
		boolean valid = true;

		// Must select two or more feature
		int selectionCount = sourceFeatures.length;
		if (selectionCount < 2) {
			this.message = Messages.MergeFeatureBehaviour_select_two_or_more;
			valid = false;
		}
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
			handleError(context, mergeContext.getMouseLocation());
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

	    throw new UnsupportedOperationException("createNoSelectCommand is not accessible");
//	    
//		UndoableMapCommand clearSelectionCommand = context.getSelectionFactory().createNoSelectCommand();
//
//		context.sendASyncCommand(clearSelectionCommand);
	}

	/**
	 * Perform the merge cancel action. Close the view.
	 */
	private void mergeCancel() {

		try {
			ApplicationGIS.getView(false, MergeView.id);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart viewPart = page.findView(MergeView.id);
			page.hideView(viewPart);
		} finally {
			unselect(context);
			if (mergeContext != null) {
				mergeContext.initContext();
			}
		}
	}

	/**
	 * Called when the delete button is pressed. If a feature is selected on the
	 * tree view with the source features, it is deleted from there and launched
	 * again the mergeBuilder.
	 */
	public void mergeDelete() {

		// get the ID of the feature to be deleted.
		SimpleFeature featureToDelete = mergeComposite.getSelectedFeature();

		if (!isValidDeletion(featureToDelete)) {
			handleError(context, mergeContext.getMouseLocation());
			return;
		}
		// delete from sourceFeatures array
		List<SimpleFeature> arrayList = new ArrayList<SimpleFeature>();

		for (int i = 0; i < sourceFeatures.length; i++) {

			if (!sourceFeatures[i].getID().equals(featureToDelete.getID())) {
				arrayList.add(sourceFeatures[i]);
			}

		}

		mergeContext.addDeletedFeature(featureToDelete);

		// create the builder, an launch again.
		sourceFeatures = arrayList.toArray(new SimpleFeature[arrayList.size()]);
		MergeFeatureBuilder builder = createMergeBuilder();
		this.setSourceFeatures(sourceFeatures);
		this.setBuilder(builder);
		unselect(context);
	}

	/**
	 * Check if the feature to be deleted from the list could be deleted. If
	 * there is no selection or if it's only one feature on the list, will
	 * return false.
	 * 
	 * @param featureToDelete
	 * @return
	 */
	private boolean isValidDeletion(SimpleFeature featureToDelete) {

		boolean isValid = true;

		if (featureToDelete == null) {
			// there is any feature to delete.
			this.message = Messages.MergeFeatureView_no_feature_to_delete;
			return false;
		}
		if (sourceFeatures.length == 1) {

			this.message = Messages.MergeFeatureView_cant_remove;
			isValid = false;
		}

		return isValid;
	}

	/**
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("unchecked")
	private MergeFeatureBuilder createMergeBuilder() throws IllegalStateException {

		SimpleFeatureType type = sourceFeatures[0].getFeatureType();
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
