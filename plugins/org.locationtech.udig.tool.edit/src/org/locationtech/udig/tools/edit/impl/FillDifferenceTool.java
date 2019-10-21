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

import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.DefaultEditToolBehaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.GridActivator;
import org.locationtech.udig.tools.edit.activator.ResetAllStateActivator;
import org.locationtech.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.AcceptWhenOverFirstVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import org.locationtech.udig.tools.edit.behaviour.StartEditingBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.DifferenceFeatureAcceptBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Creates a new SimpleFeature by calculating the difference between
 * the current shape and other shapes in the same layer.
 * <p>
 * @see DifferenceFeatureAcceptor
 * 
 * @author jones
 * @since 1.1.0
 */
public class FillDifferenceTool extends AbstractEditTool{

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.LINE;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(type);
        activators.addAll(defaults);

        activators.add(new ResetAllStateActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        acceptBehaviours.add(new DifferenceFeatureAcceptBehaviour());
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new DrawCreateVertexSnapAreaBehaviour());
        helper.startMutualExclusiveList();
        
        helper.startOrderedList(false);
        helper.add( new AddVertexWhileCreatingBehaviour());
        helper.add( new AcceptWhenOverFirstVertexBehaviour());
        helper.stopOrderedList();
        
        //override so that editing will not be started if there are no geometries on the blackboard.
        helper.add( new StartEditingBehaviour(ShapeType.POLYGON) );
        helper.stopMutualExclusiveList();


        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.done();
    }

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, LineString.class, MultiLineString.class,
                Polygon.class, MultiPolygon.class}));
    }

}
