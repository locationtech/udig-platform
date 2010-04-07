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

import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.DefaultEditToolBehaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.MutualExclusiveBehavior;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.behaviour.CursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.RemoveVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A Tool that removes vertices from EditGeoms and selects features.
 * @author jones
 * @since 1.1.0
 */
public class RemoveVertexTool extends AbstractEditTool {

    @Override
    protected void initActivators( Set<Activator> activators ) {
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultEditActivators(DrawGeomsActivator.DrawType.POLYGON);
        activators.addAll(defaults);
   }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        MutualExclusiveBehavior mutualExclusive=new MutualExclusiveBehavior();
        acceptBehaviours.add(mutualExclusive);
        mutualExclusive.getBehaviours().add( new AcceptChangesBehaviour(Polygon.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if( feature==null )
                    return false;
                Class< ? extends Geometry> class1 = ((Geometry)feature.getDefaultGeometry()).getClass();
                return super.isValid(handler) && feature!=null && 
                    (class1==Polygon.class || class1==MultiPolygon.class);
            }
        });
        mutualExclusive.getBehaviours().add( new AcceptChangesBehaviour(LineString.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if( feature==null )
                    return false;
                Class< ? extends Geometry> class1 = ((Geometry)feature.getDefaultGeometry()).getClass();
                return super.isValid(handler) && feature!=null && 
                    (class1==LineString.class || class1==MultiLineString.class);
            }
        });
        mutualExclusive.getBehaviours().add( new AcceptChangesBehaviour(Point.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if( feature==null )
                    return false;
                Class< ? extends Geometry> class1 = ((Geometry)feature.getDefaultGeometry()).getClass();
                return super.isValid(handler) && feature!=null && 
                    (class1==Point.class || class1==MultiPoint.class);
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
        helper.add(new CursorControlBehaviour(handler, new StaticProvider<String>(Messages.RemoveVertexTool_select_feature),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS),
                new StaticProvider<String>(Messages.RemoveVertexTool_remove_vertex) , new IProvider<Cursor>(){
                    public Cursor get(Object... params) {
                        return null;
                    }

                }, null));
//      vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();
        helper.add(new SelectFeatureBehaviour(new Class[]{Geometry.class}, Intersects.class));
        helper.add(new RemoveVertexBehaviour());
        helper.stopMutualExclusiveList();
        
        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.done();
    }

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, LineString.class, MultiLineString.class,
                Polygon.class, MultiPolygon.class, Point.class, MultiPoint.class}));
    }


}
