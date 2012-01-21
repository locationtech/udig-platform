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
package eu.udig.tools.merge;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.StatusBar;
import eu.udig.tools.merge.internal.view.MergeView;

/**
 * Merge the features in bounding box
 * <p>
 * This implementation is based in BBoxSelection. The extension add behavior object 
 * which displays the merge dialog.
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class MergeTool extends SimpleTool  {

	private static final Logger LOGGER = Logger.getLogger(MergeTool.class.getName());

	private MergeContext		mergeContext	= MergeContext.getInstance();

	private static final String	EXTENSION_ID	= "eu.udig.tools.merge.MergeTool";	//$NON-NLS-1$
	
	public String getExtensionID() {

		return EXTENSION_ID;
	}

	@Override
	public void setActive(final boolean active) {

		super.setActive(active);
		IToolContext toolContext = getContext();
		this.mergeContext.setToolContext(toolContext);
		if (active && toolContext.getMapLayers().size() > 0) {

			String message = Messages.MergeTool_select_features_to_merge;
			StatusBar.setStatusBarMessage(toolContext, message);
		} else {
			StatusBar.setStatusBarMessage(toolContext, "");//$NON-NLS-1$
		}
		if (!active) {

			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {

					// When the tool is deactivated, hide the view.
					ApplicationGIS.getView(false, MergeView.ID);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart viewPart = page.findView(MergeView.ID);
					page.hideView(viewPart);
					page.close();
				}
			});
			mergeContext.initContext();
		}
	}

	/**
	 * Saves in the merge context the start point of bbox
	 */
    @Override
    public void onMousePressed( MapMouseEvent e ) {

			SelectionBoxCommand selectionBoxCommand = this.mergeContext
					.getSelectionBoxCommand();
			
			
			this.mergeContext.setBBoxStartPoint(e.getPoint());
			selectionBoxCommand.setValid(true);
			selectionBoxCommand.setShape(new Rectangle(e.getPoint().x, e
					.getPoint().y, 0, 0));
			getContext().sendASyncCommand(selectionBoxCommand);

			getContext().getViewportPane().repaint();
    }
    
    /**
     * Uses the position of last event as second corner of bbox drawn to 
     * select one or more features.
     * 
     * FIXME it looks like this hook method is not called
     */
    @Override
    public void onMouseDragged( MapMouseEvent e ) {

		java.awt.Point start = this.mergeContext.getBBoxStartPoint();;
    	if (start == null) {
			start = e.getPoint();
		}
    	SelectionBoxCommand selectionBoxCommand = this.mergeContext.getSelectionBoxCommand();
		selectionBoxCommand.setShape(
				new Rectangle(	Math.min(start.x, e.x), Math.min(start.y, e.y),
								Math.abs(e.x - start.x), Math.abs(start.y - e.y)));

		getContext().sendASyncCommand(selectionBoxCommand);

		getContext().getViewportPane().repaint();
    }

	/**
	 * This hook is used to catch two events:
	 * <lu>
	 * <li>Bbox drawing action to select one or more features was finished</li>
	 * <li>Select one feature using control key and mouse pressed.</li>
	 * </lu>
	 */
    @Override
    public void onMouseReleased(MapMouseEvent e) {
    	
		// search an existent view
		MergeView mergeView = this.mergeContext.getMergeView();
		
		// presents the selected features in the view. The view must be synchronized 
		if(mergeView == null || !mergeView.isValid()){
			// opens a new view
			openMergeView(e.x, e.y, this.mergeContext);
		} 
		// set the selected features in the merge view
		ILayer selectedLayer = getContext().getSelectedLayer();
		if (!e.isControlDown()) {

			displayFeaturesUnderBBox(e, selectedLayer, mergeView);

		} else {
			// a control key + mouse press has occurred. Then the feature
			// under the cursor must be added or removed from the merge feature
			// list.

			if (isSelectionUnderCursor(e)) {

				displayFeatureOnView(e, selectedLayer, mergeView);
			} else {
				removeFeatureFromView(e, selectedLayer, mergeView);
			}
		}
		
// FIXME		
//		if(mergeView == null || !mergeView.isValid()){
//			// opens a new view
//			openMergeVeiw(e.x, e.y, this.mergeContext, context);
//		} else {
//			// adds the selected feature in the existent merge view
//			List<SimpleFeature> selectedFeatures;
//			try {
//				selectedFeatures = Util.retrieveFeaturesInBBox(bound, this.getContext());
//			} catch (IOException e1) {
//				LOGGER.warning(e1.getMessage()); 
//				return;
//			}
//			if( selectedFeatures.isEmpty() ){
//				LOGGER.warning("nothing was selected to merge"); //$NON-NLS-1$
//				return;
//			}
//			if(mergeView.contains(selectedFeatures)){
//				mergeView.deleteFromMergeList(selectedFeatures);
//			} else {
//				mergeView.addSourceFeatures(selectedFeatures);
//			}
//			
//		}
		
    }
    

	private void removeFeatureFromView(MapMouseEvent e, ILayer selectedLayer,
			MergeView mergeView) {
		// TODO Auto-generated method stub
		
	}

	private boolean isSelectionUnderCursor(MapMouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
    private void displayFeatureOnView(
    		MapMouseEvent e,
			ILayer selectedLayer, 
			MergeView mergeView) {

		Envelope bound = getContext().getBoundingBox(e.getPoint(), 3); // FIXME it should be a better solution to retrieve the feature under the cursor
		Filter filterSelectedFeatures = selectFeaturesUnderBBox(
											e, bound, getContext());
		try {
			List<SimpleFeature> selectedFeatures = Util.retrieveFeaturesInBBox(filterSelectedFeatures,
							selectedLayer);
			mergeView.addSourceFeatures(selectedFeatures);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void displayFeaturesUnderBBox(MapMouseEvent e, ILayer selectedLayer, MergeView mergeView) {

		Filter filterSelectedFeatures;
		Envelope bound;
    	// select features using the drawn bbox
		IViewportModel viewportModel = getContext().getMap().getViewportModel();
		Coordinate startPoint = viewportModel
									.pixelToWorld(
											mergeContext.getBBoxStartPoint().x,
											mergeContext.getBBoxStartPoint().y);
		Coordinate endPoint = viewportModel.pixelToWorld(e.getPoint().x, e.getPoint().y);

		if (startPoint.equals2D(endPoint)) {
			// when it was a click(start and end coordinates are equal)
			// get a little bbox around this point.
			bound = getContext().getBoundingBox(e.getPoint(), 3);
		} else {
			bound = new Envelope(startPoint, endPoint);
		}

		// builds a command to show the features selected to merge
		filterSelectedFeatures = selectFeaturesUnderBBox(e, bound,
				getContext());

		List<SimpleFeature> selectedFeatures;
		try {
			selectedFeatures = Util.retrieveFeaturesInBBox(filterSelectedFeatures,
							selectedLayer);
			mergeView.setFeatures(selectedFeatures);
		} catch (IOException e1) {
			LOGGER.warning(e1.getMessage());
			return;
		}
	}

	/**
     * Opens the Merge view
     * 
     * @param eventX
     * @param eventY
     * @param context
     */
	private void openMergeView(int eventX, int eventY, MergeContext mergeContext) {
		
		try{
			MergeViewOpenCommand viewlauncherCommand = new MergeViewOpenCommand(mergeContext);
			getContext().sendASyncCommand(viewlauncherCommand);
			
		} catch (Exception ex){
			AnimationUpdater.runTimer(
					getContext().getMapDisplay(), 
					new MessageBubble(eventX, eventY, "It cannot be merge", 
							PreferenceUtil.instance().getMessageDisplayDelay())); 
		}
		
	}

	/**
	 * 
	 * Selects the features under the bbox. This method builds a command to show the features selected to merge
	 * 
	 * @param e				mouse event
	 * @param boundDrawn 	the drawn bbox by the usr
	 * @param context 
	 * 
	 * @return {@link Filter} filter that contains the selected features 
	 */
	private Filter selectFeaturesUnderBBox(	MapMouseEvent 	e,
												Envelope 		boundDrawn,
												IToolContext 	context) {
		MapCommand command;
		
		// updates the merge context with bounds
		this.mergeContext.addBound(boundDrawn);
		

		if (e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK)) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.ADD);
		} else if (e.isControlDown()) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.SUBTRACT);
		} else {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.NONE);
		}
		getContext().sendSyncCommand(command);

		SelectionBoxCommand selectionBoxCommand = this.mergeContext
				.getSelectionBoxCommand();
		selectionBoxCommand.setValid(false);

		getContext().getViewportPane().repaint();

		Filter filterSelectedFeatures = getContext().getSelectedLayer().getFilter();
		
		return filterSelectedFeatures;
	}


//	@Override
//	protected void initActivators(Set<Activator> activators) {
//
//		activators.add(new EditStateListenerActivator());
//		activators.add(new ResetAllStateActivator());
//	}

//	@Override
//	protected void initCancelBehaviours(List<Behaviour> cancelBehaviours) {
//
//		cancelBehaviours.add(new DefaultCancelBehaviour());
//	}

//	@Override
//	protected void initEnablementBehaviours(List<EnablementBehaviour> enablementBehaviours) {
//
//		enablementBehaviours.add(new ValidToolDetectionActivator(new Class[] {
//				Geometry.class,
//				LineString.class,
//				MultiLineString.class,
//				Polygon.class,
//				MultiPolygon.class,
//				Point.class,
//				MultiPoint.class,
//				GeometryCollection.class }));
//		enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
//	}

//	@Override
//	protected void initEventBehaviours(EditToolConfigurationHelper helper) {
//
//		helper.add(new MergeEventBehaviour(mergeContext));
//		helper.done();
//	}

}
