/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.impl;

import java.util.List;
import java.util.Set;

import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.AdvancedBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.activator.DeleteGlobalActionSetterActivator;
import net.refractions.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.GridActivator;
import net.refractions.udig.tools.edit.activator.SetRenderingFilter;
import net.refractions.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.behaviour.AcceptBehaviour;
import net.refractions.udig.tools.edit.behaviour.CursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;
import net.refractions.udig.tools.edit.behaviour.MoveVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.swt.SWT;
import org.geotools.filter.FilterType;

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
        activators.add(new EditStateListenerActivator());
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new DrawGeomsActivator(DrawGeomsActivator.DrawType.POINT));
        activators.add(new DrawCurrentGeomVerticesActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new AdvancedBehaviourCommandHandlerActivator());
        activators.add(new SetRenderingFilter());
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
        helper.add( new CursorControlBehaviour(handler, new ConditionalProvider(handler, Messages.PointTool_select_or_create_feature, Messages.PointTool_add_vertex_or_finish),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_SIZEALL),new ConditionalProvider(handler, Messages.PointTool_move_vertex,null),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS), new ConditionalProvider(handler, Messages.PointTool_add_vertex, null)));
        helper.stopAdvancedFeatures();

        // vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();
        helper.startAdvancedFeatures();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add( new SelectVertexBehaviour());
        SelectFeatureBehaviour selectGeometryBehaviour = new SelectFeatureBehaviour(new Class[]{Point.class, MultiPoint.class}, FilterType.GEOMETRY_BBOX);
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
