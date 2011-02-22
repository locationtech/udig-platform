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

import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.MutualExclusiveBehavior;
import net.refractions.udig.tools.edit.activator.DrawEndPointsActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.SetRenderingFilter;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.behaviour.FreeHandPolygonDrawBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.geotools.filter.FilterType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

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
        helper.add( new SelectFeatureBehaviour(new Class[]{Polygon.class, MultiPolygon.class}, FilterType.GEOMETRY_BBOX));
        helper.add( new FreeHandPolygonDrawBehaviour() );
        AcceptOnDoubleClickBehaviour doubleClickRunAcceptBehaviour = new AcceptOnDoubleClickBehaviour();
        doubleClickRunAcceptBehaviour.setAddPoint(false);
        helper.add( doubleClickRunAcceptBehaviour );
        helper.done();
    }


}
