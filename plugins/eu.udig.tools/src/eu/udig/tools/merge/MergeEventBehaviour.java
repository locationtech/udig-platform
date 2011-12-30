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
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Or;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.DialogUtil;

/**
 * 
 * <p>
 * Create a bbox and and launch the view.
 * </p>
 * <p>
 * Requirements:
 * <ul>
 * <li>state==MODIFYING or NONE</li>
 * <li>event type == RELEASED or DRAGGED or PRESSED</li>
 * <li>button1 must be the button that was released</li>
 * <li>when dragging doesn't take in account the released button</li>
 * </ul>
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
class MergeEventBehaviour implements EventBehaviour {

	private MergeContext	mergeContext	= null;
	private Envelope		lastEnvelope;

	/**
	 * Behaviour that will merge the features contained on the bbox.
	 * 
	 * @param mergeContext
	 */
	public MergeEventBehaviour(MergeContext mergeContext) {

		this.mergeContext = mergeContext;
	}

	public boolean isValid(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		boolean legalState = handler.getCurrentState() == EditState.NONE
					|| handler.getCurrentState() == EditState.MODIFYING;
		boolean releaseButtonState = eventType == EventType.RELEASED || eventType == EventType.DRAGGED
					|| eventType == EventType.PRESSED;

		boolean legalButton = e.button == MapMouseEvent.BUTTON1;
		boolean legal = true;

		if (!(legalState && releaseButtonState && legalButton)) {

			legal = false;
		}
		if (legalState && eventType == EventType.DRAGGED) {

			legal = true;
		}

		return legal;
	}

	/**
	 * Draws a bbox selecting the features under it. If the merge preconditions
	 * are OK lunches the merge view
	 * 
	 */
	public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e, EventType eventType) {

		if (!isValid(handler, e, eventType)) {
			throw new IllegalArgumentException("Behaviour is not valid for the current state"); //$NON-NLS-1$
		}

		IToolContext context = handler.getContext();

		SelectionBoxCommand selectBoxCommand = this.mergeContext.getSelectionBoxCommand();
		Point start = this.mergeContext.getBBoxStartPoint();
		switch (eventType) {
		case PRESSED:
			// start drawing the bbox.
			this.mergeContext.setBBoxStartPoint(e.getPoint());
			selectBoxCommand.setValid(true);
			selectBoxCommand.setShape(new Rectangle(e.getPoint().x, e.getPoint().y, 0, 0));
			handler.getContext().sendASyncCommand(selectBoxCommand);
			break;
		case DRAGGED:
			// Dragged event is always preceded by the pressed event, but
			// sometimes it
			// reach dragged event and the start point is null.
			if (start == null) {
				start = e.getPoint();
			}
			selectBoxCommand.setShape(
					new Rectangle(	Math.min(start.x, e.x), Math.min(start.y, e.y),
									Math.abs(e.x - start.x), Math.abs(start.y - e.y)));
			handler.getContext().getViewportPane().repaint();
			break;
		case RELEASED:
			// finish the draw of the bbox.
			Coordinate c1 = handler.getContext().getMap().getViewportModel().pixelToWorld(start.x, start.y);
			Coordinate c2 = handler.getContext().getMap().getViewportModel().pixelToWorld(e.getPoint().x,
						e.getPoint().y);
			Envelope bounds;

			if (c1.equals2D(c2)) {
				// when it was a click(start and end coordinates are equal)
				// get a little bbox around this point.
				bounds = handler.getContext().getBoundingBox(e.getPoint(), 3);
			} else {
				bounds = new Envelope(c1, c2);
			}

			selectFeaturesUnderBBox(e, bounds, context);
			selectBoxCommand.setValid(false);
			context.getViewportPane().repaint();
			
			mergeContext.addEnvelope(bounds);
			mergeContext.storeMouseLocation(e);
			this.lastEnvelope = bounds;

			viewLauncher(e, context);

			break;
		default:
			break;
		}
		return null; // FIXME it looks like part of design was lost!
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Get the features under the bbox area.
	 * Validates the features in bounding box and launch the view.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param e
	 * @param context
	 * 
	 * FIXME refactor create a command to lunch the view 
	 */
	private void viewLauncher(MapMouseEvent e, IToolContext context) {

		final String dlgTitle = Messages.MergeTool_title_tool;
		SimpleFeature[] features = null;
		List<SimpleFeature> allFeatures, lastSelectedFeatures;

		try {
			List<Envelope> bboxList = new ArrayList<Envelope>();
			bboxList.addAll(mergeContext.getEnvelopeList());
			// gets the features in bounding box(from this bbox and previous
			// bbox)
			allFeatures = getFeatures(bboxList, context);
			bboxList.clear();
			// get the features only for this bbox
			bboxList.add(lastEnvelope);
			lastSelectedFeatures = getFeatures(bboxList, context);

			if (e.isControlDown()) {
				// control is down, so those feature are supposed to be deleted.
				mergeContext.addDeletedFeature(lastSelectedFeatures);
				mergeContext.removeEnvelope(lastEnvelope);
			} else {
				// if a feature is removed from the view of the merge, and here
				// is added again, update the list.
				mergeContext.updateDeletedFeatureList(lastSelectedFeatures);
			}
			// exclude the deleted features from the allFeatures list.
			if (mergeContext.getDeletedFeatures().size() > 0) {
				allFeatures.removeAll(mergeContext.getDeletedFeatures());
			}

			features = allFeatures.toArray(new SimpleFeature[allFeatures.size()]);
		} catch (IOException e1) {
			final String msg = Messages.MergeTool_failed_getting_selection;
			DialogUtil.openError(dlgTitle, msg);
			throw (RuntimeException) new RuntimeException(msg).initCause(e1);
		}
		assert features != null;

		final MergeFeatureViewLauncher launcher = new MergeFeatureViewLauncher(context, mergeContext);

		launcher.setSourceFeatures(features);

		if (!launcher.isValid()) {
			launcher.handleError(context, e);
			launcher.closeView();
			mergeContext.initContext();
			return;
		}
		try {
			launcher.launchView();
		} catch (IllegalStateException ise) {
			DialogUtil.openError(dlgTitle, ise.getMessage());
			launcher.closeView();
			mergeContext.initContext();
			throw ise;
		}
	}

	/**
	 * Get the features contained on the envelope/s. If there are more than one
	 * envelope, create a {@link Filter} of those envelopes and return the
	 * features contained in it.
	 * 
	 * @param bbox
	 * @param context
	 * @return
	 * @throws IOException
	 */
	private List<SimpleFeature> getFeatures(List<Envelope> bbox, IToolContext context) throws IOException {

		ILayer selectedLayer = context.getSelectedLayer();

		FeatureSource<SimpleFeatureType, SimpleFeature> source = selectedLayer.getResource(FeatureSource.class, null);

		String typename = source.getSchema().getTypeName();

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

		Filter filter = selectedLayer.createBBoxFilter(bbox.get(0), null);
		Filter mergedFilter;
		Or filterOR = null;
		for (int index = 0; index < bbox.size(); index++) {

			mergedFilter = selectedLayer.createBBoxFilter(bbox.get(index), null);
			filterOR = ff.or(filter, mergedFilter);
		}

		Query query = new DefaultQuery(typename, filterOR);

		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(query);

		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
		FeatureIterator<SimpleFeature> iter = null;
		try {
			iter = features.features();
			SimpleFeature f;
			while (iter.hasNext()) {
				f = iter.next();
				featureList.add(f);
			}
		} finally {
			if (iter != null) {
				features.close(iter);
			}
		}

		return featureList;
	}

	/**
	 * Selects the features under the bbox.
	 * 
	 * @param e	mouse event
	 * @param boundDrawn the bbox drawn by the usr
	 * @param context 
	 */
	private void selectFeaturesUnderBBox(	MapMouseEvent e,
											Envelope boundDrawn,
											IToolContext context) {

		MapCommand command;

		if (e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK)) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.ADD);
		} else if (e.isControlDown()) {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.SUBTRACT);
		} else {
			command = context.getSelectionFactory().createBBoxSelectionCommand(boundDrawn, BBoxSelectionCommand.NONE);
		}

		context.sendASyncCommand(command);
	}

	public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {

		// TODO Auto-generated method stub

	}
}
