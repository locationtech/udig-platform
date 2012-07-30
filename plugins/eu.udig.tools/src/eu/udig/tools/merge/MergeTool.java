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

import java.awt.Point;
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
import net.refractions.udig.project.ui.tool.ModalTool;
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
public class MergeTool extends SimpleTool  implements ModalTool{

    /**
     * Comment for <code>ID</code>
     */
	public static final String	ID = "eu.udig.tools.merge.MergeTool";	//$NON-NLS-1$

	private static final Logger LOGGER = Logger.getLogger(MergeTool.class.getName());

	private MergeContext		mergeContext;

	private SelectionBoxCommand selectionBoxCommand = null;

	
	/**
	 * The tool will respond to the mouse event and map motion stimulus.
	 */
	public MergeTool() {
		super(MOUSE | MOTION);
	}

	/**
	 * When the Merge tool is activated by a click in the toolbar, if the 
	 * Merge View is opened, it will be closed. 
	 */
	@Override
	public void setActive(final boolean active) {

		super.setActive(active);
		
		if(active){
			this.mergeContext = MergeContext.getInstance();
			this.mergeContext.setToolContext(getContext());

			// Set tool mode (also set in MergeOperation.op to MERGEMODE_OPERATION) 
	                // MergeContext mergeContextSingleton = MergeContext.getInstance();
			this.mergeContext.setMergeMode(MergeContext.MERGEMODE_TOOL);
			
			// feedback to the user indeed that he can select some features to merege
			StatusBar.setStatusBarMessage(this.mergeContext.getToolContext(), "");//$NON-NLS-1$
			if (this.mergeContext.getToolContext().getMapLayers().size() > 0) {

				String message = Messages.MergeTool_select_features_to_merge;
				StatusBar.setStatusBarMessage(this.mergeContext.getToolContext(), message); 
				
			} else {
				
				String message = "The current Map has no layers. The tool cannot operate.";//$NON-NLS-1$
				StatusBar.setStatusBarMessage(this.mergeContext.getToolContext(), message); 
			}
		} else {
			// if the merge view is opened it will be closed
			if( this.mergeContext.isMergeViewActive() ){
				
				closeMergeView();
			}
			this.mergeContext = null;
		}
	}

	/**
	 * Hide the merge view. 
	 */
	private void closeMergeView() {
		Display.getCurrent().asyncExec(new Runnable() {
			
			public void run() {

				// When the tool is deactivated, hide the view.
				ApplicationGIS.getView(false, MergeView.ID);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView(MergeView.ID);
				page.hideView(viewPart);
				mergeContext.disposeMergeView();
			}
		});
	}

	/**
	 * Begins the bbox selection. 
	 * Saves in the merge context the start point of bbox
	 */
    @Override
    protected void onMousePressed( MapMouseEvent e ) {
    
    	if(!isActive()) return;

    	if(e.button != MapMouseEvent.BUTTON1){
    		return;
    	}
    	// Draw the initial bbox
		selectionBoxCommand = new SelectionBoxCommand(); //this.mergeContext.getSelectionBoxCommand();
			
		Point start = e.getPoint();
		this.mergeContext.setBBoxStartPoint(start);

		selectionBoxCommand.setValid(true);
		selectionBoxCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
		context.sendASyncCommand(selectionBoxCommand);
    }
    
    
    /**
     * Uses the position of last event as second corner of bbox drawn to 
     * select one or more features.
     */
    @Override
    protected void onMouseDragged( MapMouseEvent e ) {
        
    	if(!isActive()) return;

    	// draw the selection box 
    	Point start = this.mergeContext.getBBoxStartPoint();
    	if (start == null) {
			start = e.getPoint();
		}
		
		selectionBoxCommand.setShape(
				new Rectangle(
							Math.min(start.x, e.x), Math.min(start.y, e.y),
							Math.abs(e.x - start.x), Math.abs(start.y - e.y)) );
		context.getViewportPane().repaint();		
		
    }
    /**
     * Remove the bbox drawn 
     * 
     * @param start
     * @param end 	
     */
    private void removeBBox(final Point start, final Point end){

		int x1 = Math.min(start.x, end.x);
		int y1 = Math.min(start.y, end.y);
		int x2 = Math.abs(end.x - start.x);
		int y2 = Math.abs(start.y - end.y);
		
		Coordinate c1 = context.getMap().getViewportModel()
				.pixelToWorld(x1, y1);
		Coordinate c2 = context.getMap().getViewportModel()
				.pixelToWorld(x2, y2);

		Envelope bounds = new Envelope(c1, c2);

		// remove the bounding box selection
		MapCommand cmd = new BBoxSelectionCommand(bounds, BBoxSelectionCommand.NONE);
		getContext().sendASyncCommand(cmd);

		selectionBoxCommand.setValid(false);
		getContext().getViewportPane().repaint();
    }
    

	/**
	 * This hook is used to catch two events:
	 * <lu>
	 * <li>Bbox drawing action to select one or more features was finished</li>
	 * <li>select individual feature for click (press and release in the same position)</li>
	 * <li>Unselect one feature using control key and mouse pressed.</li>
	 * </lu>
	 */
    @Override
    protected void onMouseReleased(MapMouseEvent mouseEvent) {

    	if(!isActive()) return;

    	if(mouseEvent.button != MapMouseEvent.BUTTON1){
    		return;
    	}    	
    	// draw the selection box 
    	Point start = this.mergeContext.getBBoxStartPoint();
    	
		// search an existent view or open a new one
		if(! this.mergeContext.isMergeViewActive() ){
			openMergeView(mouseEvent.x, mouseEvent.y, this.mergeContext);
		}
		MergeView mergeView = this.mergeContext.getMergeView();

		assert mergeView !=null;
		
		// presents the selected features in the map and the merge view
		ILayer selectedLayer = getContext().getSelectedLayer();
		if (!start.equals(mouseEvent.getPoint())) { // selection using a bbox

			removeBBox(start, mouseEvent.getPoint());
			displayFeaturesUnderBBox(mouseEvent.getPoint(), selectedLayer, mergeView);

		} else { // selection using click over the a feature
			if ( start.equals(mouseEvent.getPoint()) &&  !mouseEvent.isControlDown() ) {
				displayFeatureOnView(mouseEvent, selectedLayer, mergeView);
			}
		}
    }

	/**
	 * Display the feature selected on the view
	 * 
	 * @param e mouse event
	 * @param selectedLayer
	 * @param mergeView
	 */
    private void displayFeatureOnView(
    		MapMouseEvent e,
			ILayer selectedLayer, 
			MergeView mergeView) {

		Envelope bound = buildBoundForPoint(e.getPoint()); 

		// show selection in Map
		Filter filterSelectedFeatures = selectFeaturesUnderBBox(bound, BBoxSelectionCommand.NONE);

		// retrieve the feature and present its data in the merge view
		try {
			List<SimpleFeature> selectedFeatures = Util.retrieveFeatures(filterSelectedFeatures, selectedLayer);
			
			mergeView.addSourceFeatures(selectedFeatures);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private Envelope buildBoundForPoint(Point p) {
		return getContext().getBoundingBox(p, 3);
	}

    /**
     * Presents the features selected using the bbox interaction in the merge view
     * @param xyMouse
     * @param selectedLayer
     * @param mergeView
     */
	private void displayFeaturesUnderBBox(Point xyMouse, ILayer selectedLayer, MergeView mergeView) {

		Filter filterSelectedFeatures;
		Envelope bound;
    	// select features using the drawn bbox
		IViewportModel viewportModel = getContext().getMap().getViewportModel();
		Coordinate startPoint = viewportModel
									.pixelToWorld(
											mergeContext.getBBoxStartPoint().x,
											mergeContext.getBBoxStartPoint().y);
		Coordinate endPoint = viewportModel.pixelToWorld(xyMouse.x, xyMouse.y);

		if (startPoint.equals2D(endPoint)) {
			// when it was a click(start and end coordinates are equal)
			// get a little bbox around this point.
			bound = getContext().getBoundingBox(xyMouse, 3);
		} else {
			bound = new Envelope(startPoint, endPoint);
		}

		// builds a command to show the features selected to merge
		try {
			filterSelectedFeatures = selectFeaturesUnderBBox( bound, BBoxSelectionCommand.NONE);
			List<SimpleFeature>  selectedFeatures = Util.retrieveFeatures(filterSelectedFeatures, selectedLayer);
			
			mergeView.addSourceFeatures(selectedFeatures);
		
			mergeView.display();

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
			MergeView view = (MergeView)ApplicationGIS.getView(true, MergeView.ID);
			if(view == null){
				// crates a new merge view
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				view = (MergeView) page.findView(MergeView.ID);
			}
			assert view != null : "view is null"; //$NON-NLS-1$
			
			// associates this the merge view with the merge context
			view.setMergeContext(mergeContext);
			mergeContext.activeMergeView(view);
			
		} catch (Exception ex){
			AnimationUpdater.runTimer(
					getContext().getMapDisplay(), 
					new MessageBubble(eventX, eventY, "It cannot be merge",  //$NON-NLS-1$
							PreferenceUtil.instance().getMessageDisplayDelay())); 
		}
		
	}

	/**
	 * Selects the features under the bbox. This method builds a command to show the features selected to merge
	 * 
	 * @param boundDrawn 	the drawn bbox by the usr
	 * @param context 
	 * 
	 * @return {@link Filter} filter that contains the selected features 
	 */
	private Filter selectFeaturesUnderBBox(	Envelope 		boundDrawn,
											int 			SelectionType) {
		
		// updates the merge context with bounds
		this.mergeContext.addBound(boundDrawn);

		MapCommand command = context.getSelectionFactory().createBBoxSelectionCommand( boundDrawn, SelectionType);
		getContext().sendSyncCommand(command);

		//SelectionBoxCommand selectionBoxCommand = this.mergeContext.getSelectionBoxCommand();
		//selectionBoxCommand.setValid(true);

		getContext().getViewportPane().repaint();

		Filter filterSelectedFeatures = getContext().getSelectedLayer().getFilter();
		
		return filterSelectedFeatures;
	}


}
