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

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

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
public class MergeTool extends AbstractModalTool implements ModalTool {

	private MergeContext		mergeContext	= new MergeContext();

	private static final String	EXTENSION_ID	= "eu.udig.tools.merge.MergeTool";	//$NON-NLS-1$
	
	public String getExtensionID() {

		return EXTENSION_ID;
	}

	@Override
	public void setActive(final boolean active) {

		super.setActive(active);
		IToolContext context = getContext();
		if (active && context.getMapLayers().size() > 0) {

			String message = Messages.MergeTool_select_features_to_merge;
			StatusBar.setStatusBarMessage(context, message);
		} else {
			StatusBar.setStatusBarMessage(context, "");//$NON-NLS-1$
		}
		if (!active) {

			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {

					// When the tool is deactivated, hide the view.
					ApplicationGIS.getView(false, MergeView.ID);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart viewPart = page.findView(MergeView.ID);
					page.hideView(viewPart);
				}
			});
			mergeContext.initContext();
		}
	}

	/**
	 * starts drawing the bbox.
	 */
    @Override
    public void mousePressed( MapMouseEvent e ) {

    	SelectionBoxCommand selectionBoxCommand = this.mergeContext.getSelectionBoxCommand();
    	
		this.mergeContext.setBBoxStartPoint(e.getPoint());
		selectionBoxCommand.setValid(true);
		selectionBoxCommand.setShape(new Rectangle(e.getPoint().x, e.getPoint().y, 0, 0));
		getContext().sendASyncCommand(selectionBoxCommand);

		getContext().getViewportPane().repaint();
    }
    
    @Override
    public void mouseDragged( MapMouseEvent e ) {

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

    @Override
    public void mouseReleased(MapMouseEvent e) {
    	
		java.awt.Point start = this.mergeContext.getBBoxStartPoint();;
    	
		// finish the draw of the bounds.
		Coordinate startPoint = getContext().getMap().getViewportModel().pixelToWorld(start.x, start.y);
		Coordinate endPoint = getContext().getMap().getViewportModel().pixelToWorld(e.getPoint().x,	e.getPoint().y);
		
		Envelope bound;
		if (startPoint.equals2D(endPoint)) {
			// when it was a click(start and end coordinates are equal)
			// get a little bbox around this point.
			bound = getContext().getBoundingBox(e.getPoint(), 3);
		} else {
			bound = new Envelope(startPoint, endPoint);
		}
		// updates the merge context with bounds
		this.mergeContext.addBound(bound);
		
		// builds a command to show the features selected to merge
		MapCommand selectFeaturesCommand = selectFeaturesUnderBBox(e, bound, getContext());
		
		getContext().sendASyncCommand(selectFeaturesCommand);

		SelectionBoxCommand selectionBoxCommand = this.mergeContext.getSelectionBoxCommand();
		selectionBoxCommand.setValid(false);

		getContext().getViewportPane().repaint();		
		
		try{
			MergeCommandViewLauncher viewlauncherCommand = new MergeCommandViewLauncher(this.mergeContext, context);
			getContext().sendASyncCommand(viewlauncherCommand);
			
		} catch (Exception ex){
			AnimationUpdater.runTimer(
					getContext().getMapDisplay(), 
					new MessageBubble(e.x, e.y, "It cannot be merge", PreferenceUtil.instance().getMessageDisplayDelay())); 
		}
		
		
    }
	/**
	 * Selects the features under the bbox.
	 * 
	 * @param e				mouse event
	 * @param boundDrawn 	the drawn bbox by the usr
	 * @param context 
	 */
	private MapCommand selectFeaturesUnderBBox(	MapMouseEvent 	e,
												Envelope 		boundDrawn,
												IToolContext 	context) {
		MapCommand command;

		if (e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK)) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.ADD);
		} else if (e.isControlDown()) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.SUBTRACT);
		} else {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.NONE);
		}

		return command;
	}


//	
//    protected void initActivators(Set<Activator> activators) {
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
