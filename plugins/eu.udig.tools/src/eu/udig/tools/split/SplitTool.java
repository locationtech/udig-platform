/* uDig-Spatial Operations plugins
 * http://b5m.gipuzkoa.net
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.tools.split;

import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.ResetAllStateActivator;
import net.refractions.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.RefreshLayersBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.support.ShapeType;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.StatusBar;

/**
 * Splits one or more Feature using a {@link SplitGeometryBehaviour} to
 * accomplish the task once the user finished drawing the splitting line.
 * <p>
 * Users can use this tool to either:
 * <ul>
 * <li>Split one or more Simple Features using a line
 * <li>Split one polygon feature by creating a hole
 * </ul>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @author Marco Foi (www.mcfoi.it) [porting to uDig core]
 * @since 1.1.0
 */
public class SplitTool extends AbstractEditTool {

	private static final String	EXTENSION_ID	= "eu.udig.tools.split.SplitTool";	//$NON-NLS-1$
	
	@Override
	public void setActive(final boolean active) {
		super.setActive(active);
		IToolContext context = getContext();
		if (active && context.getMapLayers().size() > 0) {
			String message = Messages.SplitTool_draw_line_to_split;
			StatusBar.setStatusBarMessage(context, message);
		} else {
			StatusBar.setStatusBarMessage(context, "");//$NON-NLS-1$
		}
	}

	/**
	 * Initializes the list of Activators that are ran when the tool is
	 * activated and deactivated.
	 * 
	 * @param activators
	 *            an empty list.
	 */
	@Override
	protected void initActivators(Set<Activator> activators) {
		activators.add(new EditStateListenerActivator());
		activators.add(new DrawGeomsActivator(DrawGeomsActivator.DrawType.LINE));
		activators.add(new DrawCurrentGeomVerticesActivator());
		activators.add(new ResetAllStateActivator());
		activators.add(new SetSnapBehaviourCommandHandlerActivator());
	}

	/**
	 * Initializes the list of Behaviours to run when the current edition change has been
	 * accepted. Acceptance is informed by a double click or the Enter key
	 * 
	 * @param acceptBehaviours
	 *            an empty list
	 */
	@Override
	protected void initAcceptBehaviours(List<Behaviour> acceptBehaviours) {
		acceptBehaviours.add(new SplitGeometryBehaviour());
		acceptBehaviours.add(new RefreshLayersBehaviour());
	}

	/**
	 * Initializes the behaviours that are ran when a cancel signal is received
	 * (the ESC key).
	 * 
	 * @param cancelBehaviours
	 *            an empty list
	 */
	@Override
	protected void initCancelBehaviours(List<Behaviour> cancelBehaviours) {
		cancelBehaviours.add(new DefaultCancelBehaviour());
	}

	/**
	 * Initializes the Event Behaviours that are run when an event occurs. Since
	 * this can be complex a helper class is provided to build the complex
	 * data structure of the Behaviours.
	 * 
	 * @see EditToolConfigurationHelper
	 * @param helper
	 *            a helper for constructing the complicated structure of
	 *            EventBehaviours.
	 */
	@Override
	protected void initEventBehaviours(EditToolConfigurationHelper helper) {
		helper.add(new DrawCreateVertexSnapAreaBehaviour());
		helper.startMutualExclusiveList();
		helper.add(new AddSplitVertexBehaviour());
		// override so that editing will not be started if there are no
		// geometries on the
		// blackboard.
		helper.add(new StartEditingBehaviour(ShapeType.LINE));
		helper.stopMutualExclusiveList();

		helper.add(new SetSnapSizeBehaviour());
		helper.add(new AcceptOnDoubleClickBehaviour());
		helper.done();
	}

	/**
	 * Initializes the list of {@link EnablementBehaviour}s that are ran to
	 * determine if the tool is enabled given an event. For example if the mouse
	 * cursor is outside the valid bounds of a CRS for a layer an
	 * EnablementBehaviour might signal that editing is illegal and provide a
	 * message for the user indicating why.
	 * 
	 * @param enablementBehaviours
	 *            an empty list
	 */
	@Override
	protected void initEnablementBehaviours(List<EnablementBehaviour> enablementBehaviours) {
		/*
		 * enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
		 */
		enablementBehaviours.add(new ValidToolDetectionActivator(new Class[] {
				LineString.class,
				MultiLineString.class,
				MultiPolygon.class,
				Polygon.class}));
	}

}
