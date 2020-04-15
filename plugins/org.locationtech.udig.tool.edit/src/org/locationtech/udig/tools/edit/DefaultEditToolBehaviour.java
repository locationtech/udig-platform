/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.tools.edit.activator.ClearCurrentSelectionActivator;
import org.locationtech.udig.tools.edit.activator.DrawCurrentGeomVerticesActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.EditStateListenerActivator;
import org.locationtech.udig.tools.edit.activator.SetRenderingFilter;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Provides some methods for adding basic/common behaviours to a tools.
 * <p>
 * This is NOT a complete set of behaviours.
 * </p>
 * 
 * @author jesse
 * @since 1.1.0
 */
public final class DefaultEditToolBehaviour {


    /**
     * Adds the activators that almost all edit tools use.
     * Used by the tools that creates new geometries, and not the ones that edit existent ones.
     * 
     * @param geometryType the type of geometries drawn. If all types then default to POLYGON
     * @return
     */
    public static Set<Activator> createDefaultCreateActivators( DrawType geometryType ) {
        if (geometryType == null) {
            throw new NullPointerException("geomType cannot be null"); //$NON-NLS-1$
        }
        Set<Activator> activators = new HashSet<Activator>();
        activators.add(new EditStateListenerActivator());
        activators.add(new DrawGeomsActivator(geometryType));
        activators.add(new DrawCurrentGeomVerticesActivator());
        activators.add(new SetRenderingFilter());
        activators.add(new ClearCurrentSelectionActivator());
        return activators;
    }
    
    /**
     * Adds the activators that almost all edit tools use.
     * Used by the tools that edit, and not the ones that creates.
     * 
     * @param geometryType the type of geometries drawn. If all types then default to POLYGON
     * @return
     */
    public static Set<Activator> createDefaultEditActivators( DrawType geometryType ) {
        if (geometryType == null) {
            throw new NullPointerException("geomType cannot be null"); //$NON-NLS-1$
        }
        Set<Activator> activators = new HashSet<Activator>();
        activators.add(new EditStateListenerActivator());
        activators.add(new DrawGeomsActivator(geometryType));
        activators.add(new DrawCurrentGeomVerticesActivator());
        activators.add(new SetRenderingFilter());
        return activators;
    }

    /**
     * Creates an set of Behaviours can be added to the AcceptBehaviours. The behaviours will detect
     * changes made to a EditGeom and also detect when a EditGeom was created by a tool and complete
     * the change by either updating the affect Feature or creating a new feature. This is different
     * from {@link #createDefaultAcceptBehaviour(Class)} only in that it will work for any type of
     * geometry rather than just one. The other side is it is less efficient.
     * 
     * @return a list of behaviours
     */
    public static List<Behaviour> createAcceptAllChanges() {
        List<Behaviour> acceptBehaviours = new ArrayList<Behaviour>();

        MutualExclusiveBehavior mutualExclusive = new MutualExclusiveBehavior();
        acceptBehaviours.add(mutualExclusive);
        mutualExclusive.getBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                SimpleFeature feature = handler.getContext().getEditManager().getEditFeature();
                if (feature == null)
                    return false;
                Class< ? > class1 = feature.getDefaultGeometry().getClass();
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
                Class< ? > class1 = feature.getDefaultGeometry().getClass();
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
                Class< ? > class1 = feature.getDefaultGeometry().getClass();
                return super.isValid(handler) && feature != null
                        && (class1 == Point.class || class1 == MultiPoint.class);
            }
        });
        return acceptBehaviours;
    }

    /**
     * Creates a Behaviour that can be added to the AcceptBehaviours. The behaviour will detect
     * changes made to a EditGeom and also detect when a EditGeom was created by a tool and complete
     * the change by either updating the affect Feature or creating a new feature. This is different
     * from {@link #createAcceptAllChanges()} only in that it will for only one type of geometry.
     * The other side is it is more efficient.
     * 
     * @param type
     * @return
     */
    public static List<Behaviour> createDefaultAcceptBehaviour( Class< ? extends Geometry> type ) {
        List<Behaviour> acceptBehaviours = new ArrayList<Behaviour>();
        acceptBehaviours.add(new AcceptChangesBehaviour(type, false));
        return acceptBehaviours;
    }

    /**
     * Creates the default cancel behaviour
     * 
     * @return the default cancel behaviour
     */
    public static List<Behaviour> createDefaultCancelBehaviours() {
        List<Behaviour> cancelBehaviours = new ArrayList<Behaviour>();
        cancelBehaviours.add(new DefaultCancelBehaviour());

        return cancelBehaviours;
    }

    /**
     * Create a EnablementBehaviour that verifies that the tool is valid for the currently selected
     * layer. The behaviour checks the GeometryType of the layer and if it is one of the classes
     * passed in as a parameter it will permit the tool to be enabled otherwise it won't.
     * 
     * @param classes the geometry types that are legal for the tool
     * @return the enablement behaviour in a list
     */
    public static List<EnablementBehaviour> createValidToolEnablementBehaviour(
            Class< ? extends Geometry>[] classes ) {
        List<EnablementBehaviour> enablementBehaviours = new ArrayList<EnablementBehaviour>();
        enablementBehaviours.add(new ValidToolDetectionActivator(classes));
        return enablementBehaviours;
    }

    /**
     * Calls {@link #createValidToolEnablementBehaviour(Class[])} with a array of all geometry
     * types.
     * 
     * @return the enablement behaviour in a list
     */
    @SuppressWarnings("unchecked")
    public static List<EnablementBehaviour> createEnabledWithAllGeometryLayerBehaviour() {
        Class< ? extends Geometry>[] classes = new Class[]{Geometry.class, LineString.class,
                MultiLineString.class, Polygon.class, MultiPolygon.class, Point.class,
                MultiPoint.class};

        return createValidToolEnablementBehaviour(classes);
    }

}
