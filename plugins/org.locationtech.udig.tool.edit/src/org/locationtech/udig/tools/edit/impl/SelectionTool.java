/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.impl;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.DefaultEditToolBehaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.MutualExclusiveBehavior;
import org.locationtech.udig.tools.edit.activator.AdvancedBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.activator.DeleteGlobalActionSetterActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.GridActivator;
import org.locationtech.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.CursorControlBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import org.locationtech.udig.tools.edit.behaviour.InsertVertexOnEdgeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.MoveGeometryBehaviour;
import org.locationtech.udig.tools.edit.behaviour.MoveVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectVerticesWithBoxBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;

import org.eclipse.swt.SWT;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.spatial.Intersects;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Selects and modifies features.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectionTool extends AbstractEditTool {
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, LineString.class,
                MultiLineString.class, Polygon.class, MultiPolygon.class, Point.class,
                MultiPoint.class}));
    }

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultEditActivators(type);
        activators.addAll(defaults);
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new AdvancedBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        MutualExclusiveBehavior mutualExclusive = new MutualExclusiveBehavior();
        acceptBehaviours.add(mutualExclusive);
        mutualExclusive.getBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if (feature == null)
                    return false;
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Class< ? extends Geometry> class1 = geometry == null? null : geometry.getClass();
                
                return super.isValid(handler) && feature != null
                        && (class1 == Polygon.class || class1 == MultiPolygon.class);
            }
        });
        mutualExclusive.getBehaviours().add(new AcceptChangesBehaviour(LineString.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if (feature == null)
                    return false;
                Class< ? extends Geometry> class1 = ((Geometry) feature.getDefaultGeometry())
                        .getClass();
                return super.isValid(handler) && feature != null
                        && (class1 == LineString.class || class1 == MultiLineString.class);
            }
        });
        mutualExclusive.getBehaviours().add(new AcceptChangesBehaviour(Point.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if (feature == null)
                    return false;
                Class< ? extends Geometry> class1 = ((Geometry) feature.getDefaultGeometry())
                        .getClass();
                return super.isValid(handler) && feature != null
                        && (class1 == Point.class || class1 == MultiPoint.class);
            }
        });
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add(new DrawCreateVertexSnapAreaBehaviour());
        helper.add(new CursorControlBehaviour(handler, new StaticProvider<String>(
                Messages.SelectionTool_select), new CursorControlBehaviour.SystemCursorProvider(
                SWT.CURSOR_SIZEALL),
                new StaticProvider<String>(Messages.SelectionTool_move_vertex),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS),
                new StaticProvider<String>(Messages.SelectionTool_add_vertex)));

        // vertex selection OR geometry selection should not both happen so make them a mutual
        // exclusion behaviour
        helper.startMutualExclusiveList();

        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add(new SelectVertexBehaviour());
        helper.add(new SelectFeatureBehaviour(new Class[]{Geometry.class}, Intersects.class));

        helper.startAdvancedFeatures();
        helper.add(new InsertVertexOnEdgeBehaviour());
        helper.stopAdvancedFeatures();

        helper.stopMutualExclusiveList();

        helper.startMutualExclusiveList();
        helper.add( new MoveVertexBehaviour() );
        helper.add( new MoveGeometryBehaviour());
        helper.stopMutualExclusiveList();

        helper.add( new SelectVerticesWithBoxBehaviour() );
        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.add( new SetSnapSizeBehaviour());     
        helper.done();
    }

}
