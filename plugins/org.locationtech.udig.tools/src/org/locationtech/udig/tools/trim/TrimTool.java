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
package org.locationtech.udig.tools.trim;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
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
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

import org.locationtech.udig.tools.trim.internal.TrimGeometryBehaviour;
import org.locationtech.udig.tools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.ui.util.StatusBar;

/**
 * UDig modal tool to draw a linestring and cut the linestrings from the selected layer from the
 * intersection point with the line drawn to the right of the line drawn
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @author Marco Foi (www.mcfoi.it) [porting to uDig core]
 * @since 1.1.0
 */
public class TrimTool extends AbstractEditTool {
    

    @Override
    public void setActive( final boolean active ) {
        super.setActive(active);
        IToolContext context = getContext();
        if (active && context.getMapLayers().size() > 0) {
            String message = Messages.TrimTool_draw_line_to_trim;
            StatusBar.setStatusBarMessage(context, message);
        } else {
            StatusBar.setStatusBarMessage(context, "");//$NON-NLS-1$
        }
    }

    /**
     * Initializes the list of Activators that are run when the tool is activated and deactivated.
     * 
     * @param activators an empty list.
     */
    @Override
    protected void initActivators( Set<Activator> activators ) {
        activators.add(new EditStateListenerActivator());
        activators.add(new DrawGeomsActivator(DrawGeomsActivator.DrawType.LINE));
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

        acceptBehaviours.add(new TrimGeometryBehaviour());
    }

    /**
     * Initializes the behaviours that are ran when a cancel signal is received (the ESC key).
     * 
     * @param cancelBehaviours an empty list
     */
    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
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
        helper.add(new DrawCreateVertexSnapAreaBehaviour());
        helper.startMutualExclusiveList();
        helper.add(new AddVertexWhileCreatingBehaviour());
        // override so that editing will not be started if there are no geometries on the
        // blackboard.
        helper.add(new StartEditingBehaviour(ShapeType.LINE));
        helper.stopMutualExclusiveList();

		helper.add(new SetSnapSizeBehaviour());
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
        /*
         * enablementBehaviours.add(new WithinLegalLayerBoundsBehaviour());
         */
        enablementBehaviours.add(new ValidToolDetectionActivator(new Class[]{Geometry.class,
                LineString.class, MultiLineString.class}));
    }

}
