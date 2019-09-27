/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.arc;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import org.locationtech.udig.tools.edit.activator.EditStateListenerActivator;
import org.locationtech.udig.tools.edit.activator.ResetAllStateActivator;
import org.locationtech.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.StartEditingBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.arc.internal.ArcFeedbackManager;
import org.locationtech.udig.tools.arc.internal.CreateArcBehaviour;
/*
import es.axios.udig.ui.editingtools.arc.internal.ArcFeedbackManager;
import es.axios.udig.ui.editingtools.arc.internal.CreateArcBehaviour;
*/

import org.locationtech.udig.tools.arc.internal.beahaviour.AcceptFeedbackBehaviour;
import org.locationtech.udig.tools.arc.internal.beahaviour.CancelFeedbakBehaviour;
import org.locationtech.udig.tools.arc.internal.beahaviour.EditToolFeedbackBehaviour;
import org.locationtech.udig.tools.arc.internal.beahaviour.EditToolFeedbackManager;
import org.locationtech.udig.tools.arc.internal.beahaviour.NumOfPointsRunAcceptBehaviour;
/*
import es.axios.udig.ui.editingtools.internal.commons.behaviour.AcceptFeedbackBehaviour;
import es.axios.udig.ui.editingtools.internal.commons.behaviour.CancelFeedbakBehaviour;
import es.axios.udig.ui.editingtools.internal.commons.behaviour.EditToolFeedbackBehaviour;
import es.axios.udig.ui.editingtools.internal.commons.behaviour.EditToolFeedbackManager;
import es.axios.udig.ui.editingtools.internal.commons.behaviour.NumOfPointsRunAcceptBehaviour;
*/
import org.locationtech.udig.tools.arc.internal.presentation.StatusBar;

/**
 * Edit tool that allows to create a linear approximation of an arc of circumference by specifying
 * three points, taken as two consecutive arc chords.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it) [porting to uDig core]
 * @since 1.1.0
 * @see ArcFeedbackManager
 * @see CreateArcBehaviour
 */
public class ArcTool extends AbstractEditTool {

    private static final String     EXTENSION_ID = "es.axios.udig.ui.editingtools.arc.ArcTool"; //$NON-NLS-1$

    private EditToolFeedbackManager arcFeedbackManager;

    /**
     * 
     */
    public ArcTool() {
        super();
    }

    private EditToolFeedbackManager getFeedbackManager() {
        if (arcFeedbackManager == null) {
            arcFeedbackManager = new ArcFeedbackManager();
        }
        return arcFeedbackManager;
    }


    @Override
    public void setActive( final boolean active ) {
        super.setActive(active);
        IToolContext context = getContext();
        if (active && context.getMapLayers().size() > 0) {
            String message = "Arc Tool activated, specify first point";
            StatusBar.setStatusBarMessage(context, message);
        } else {
            StatusBar.setStatusBarMessage(context, "");//$NON-NLS-1$
        }
    }

    /**
     * Initializes the list of Activators that are ran when the tool is activated and deactivated.
     * 
     * @param activators an empty list.
     */
    @Override
    protected void initActivators( Set<Activator> activators ) {
        activators.add(new EditStateListenerActivator());
        activators.add(new DrawCurrentGeomVerticesActivator());
        activators.add(new ResetAllStateActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
    }

    /**
     * Initializes the list of Behaviours to run when the current edit has been accepted. Acceptance
     * is signalled by a double click or the Enter key
     * 
     * @param acceptBehaviours an empty list
     */
    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        acceptBehaviours.add(new CreateArcBehaviour());
        acceptBehaviours.add(new AcceptFeedbackBehaviour(getFeedbackManager()));
    }

    /**
     * Initializes the behaviours that are ran when a cancel signal is received (the ESC key).
     * 
     * @param cancelBehaviours an empty list
     */
    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new CancelFeedbakBehaviour(getFeedbackManager()));
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    /**
     * Initializes the Event Behaviours that are run when an event occurs. Since this can be complex
     * a helper class is provided to build the complex datastructure of Behaviours.
     * 
     * @see EditToolConfigurationHelper
     * @param helper a helper for constructing the complicated structure of EventBehaviours.
     */
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        //show the snap area
        helper.add(new DrawCreateVertexSnapAreaBehaviour());
        
        //run only the first valid behaviour
        helper.startMutualExclusiveList();
        helper.add(new AddVertexWhileCreatingBehaviour());
        // override so that editing will not be started if there are no geometries on the
        // blackboard.
        helper.add(new StartEditingBehaviour(ShapeType.POINT));
        helper.stopMutualExclusiveList();

        // End interaction trigger when the third coordinate is entered
        NumOfPointsRunAcceptBehaviour acceptBehaviour = new NumOfPointsRunAcceptBehaviour(3);
        // acceptBehaviour.setAddPoint(true); FIXME it is not present in rc15

        
        helper.add(acceptBehaviour);
		helper.add(new SetSnapSizeBehaviour());
        helper.add(new EditToolFeedbackBehaviour(getFeedbackManager()));
		helper.add(new AcceptOnDoubleClickBehaviour());
        helper.done();
    }

    /**
     * Initializes the list of {@link EnablementBehaviour}s that are ran to determine if the tool
     * is enabled given an event. For example if the mouse cursor is outside the valid bounds of a
     * CRS for a layer an EnablementBehaviour might signal that editing is illegal and provide a
     * message for the user indicating why.
     * 
     * @param enablementBehaviours an empty list
     */
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> enablementBehaviours ) {
		enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
		enablementBehaviours.add(new ValidToolDetectionActivator(new Class[] {
				Geometry.class,
				Polygon.class,
				MultiPolygon.class,
				LineString.class,
				MultiLineString.class }));
    }

}
