/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.impl;

import java.util.List;
import java.util.Set;

import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.DefaultEditToolBehaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.AdvancedBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.activator.DeleteGlobalActionSetterActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.GridActivator;
import net.refractions.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import net.refractions.udig.tools.edit.behaviour.AcceptBehaviour;
import net.refractions.udig.tools.edit.behaviour.CursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.MoveVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.BBOX;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Creates and edits points. 
 * 
 * @author jones
 * @since 1.1.0
 */
public class PointTool extends AbstractEditTool {
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, Point.class, MultiPoint.class}));
    }

    @Override
    protected void initActivators( Set<Activator> activators ) {
        
        DrawType type = DrawGeomsActivator.DrawType.POINT;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(type);
        activators.addAll(defaults);
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new AdvancedBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        acceptBehaviours.add( new AcceptChangesBehaviour(Point.class, false) );
        acceptBehaviours.add( new DeselectEditShapeAcceptBehaviour() );
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new DrawCreateVertexSnapAreaBehaviour());
        
        helper.startAdvancedFeatures();
        ConditionalProvider defaultMessage = new ConditionalProvider( handler, Messages.PointTool_select_or_create_feature,Messages.PointTool_add_vertex_or_finish);
        CursorControlBehaviour.SystemCursorProvider overVertexCursor = new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_SIZEALL);
        ConditionalProvider overVertexMessage = new ConditionalProvider( handler, Messages.PointTool_move_vertex,null );
        CursorControlBehaviour.SystemCursorProvider overEdgeCursor = new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS);
        ConditionalProvider overEdgeMessage = new ConditionalProvider( handler, Messages.PointTool_add_vertex, null);
        helper.add(
                new CursorControlBehaviour(
                        handler,
                        defaultMessage,
                        overVertexCursor,
                        overVertexMessage, 
                        overEdgeCursor,
                        overEdgeMessage
                )
        );
        helper.stopAdvancedFeatures();

        // vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();
        helper.startAdvancedFeatures();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add( new SelectVertexBehaviour());

        SelectFeatureBehaviour selectGeometryBehaviour = new SelectFeatureBehaviour(new Class[]{Point.class, MultiPoint.class}, BBOX.class);
        selectGeometryBehaviour.initDefaultStrategies(ShapeType.POINT);
        helper.add(selectGeometryBehaviour);

        helper.startElseFeatures();
        helper.add(new StartEditingBehaviour(ShapeType.POINT));
        helper.stopElseFeatures();
        
        helper.stopAdvancedFeatures();
        helper.stopMutualExclusiveList();
        
        helper.startAdvancedFeatures();
        helper.add( new MoveVertexBehaviour() );
        helper.stopAdvancedFeatures();
        helper.add( new AcceptBehaviour() );
        helper.add( new SetSnapSizeBehaviour());
        helper.done();
    }

}
