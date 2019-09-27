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
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.MutualExclusiveBehavior;
import org.locationtech.udig.tools.edit.activator.ClearCurrentSelectionActivator;
import org.locationtech.udig.tools.edit.activator.DrawEndPointsActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.EditStateListenerActivator;
import org.locationtech.udig.tools.edit.activator.SetRenderingFilter;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.FreeHandPolygonDrawBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.opengis.filter.spatial.BBOX;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Create shapes by drawing free hand.
 * 
 * @author jones
 * @since 1.1.0
 */
public class FreeHandTool extends AbstractEditTool {

    public FreeHandTool() {
        super();
    }
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, Polygon.class, MultiPolygon.class, LineString.class, MultiLineString.class}));
   }

    @Override
    protected void initActivators( Set<Activator> activators ) {
        activators.add(new EditStateListenerActivator());
        DrawGeomsActivator drawGeomsActivator = new DrawGeomsActivator(DrawGeomsActivator.DrawType.POLYGON);
        drawGeomsActivator.setShowMouseLocation(false);
        activators.add(drawGeomsActivator);
        activators.add(new DrawEndPointsActivator());
        activators.add(new SetRenderingFilter());
        activators.add(new ClearCurrentSelectionActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        MutualExclusiveBehavior mutualExclusive=new MutualExclusiveBehavior();
        acceptBehaviours.add(mutualExclusive);
        
        mutualExclusive.getBehaviours().add( new AcceptChangesBehaviour(Polygon.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                
                return super.isValid(handler) && handler.getCurrentGeom()!=null && 
                    handler.getCurrentGeom().getShapeType()==ShapeType.POLYGON;
            }
        });
        
        mutualExclusive.getBehaviours().add( new AcceptChangesBehaviour(LineString.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                return super.isValid(handler)  && handler.getCurrentGeom()!=null && 
                handler.getCurrentGeom().getShapeType()==ShapeType.LINE;
            }
        });

        acceptBehaviours.add( new DeselectEditShapeAcceptBehaviour() );

    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new SelectFeatureBehaviour(new Class[]{Polygon.class, MultiPolygon.class}, BBOX.class));
        helper.add( new FreeHandPolygonDrawBehaviour() );
        AcceptOnDoubleClickBehaviour doubleClickRunAcceptBehaviour = new AcceptOnDoubleClickBehaviour();
        //doubleClickRunAcceptBehaviour.setAddPoint(false);
        helper.add( doubleClickRunAcceptBehaviour );
        helper.done();
    }


}
