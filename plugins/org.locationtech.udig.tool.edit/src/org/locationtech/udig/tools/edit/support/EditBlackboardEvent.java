/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Event detailing a change in the blackboard.
 *
 * @author jones
 * @since 1.1.0
 */
public class EditBlackboardEvent {

    public static enum EventType {
        /**
         * A point was added to the blackboard.
         * <ul>
         * <li>The source is the {@link PrimitiveShape} that the point was added to</li>
         * <li>The OldValue is null</li>
         * <li>The NewValue is a the new {@link Point}</li>
         * </ul>
         */
        ADD_POINT,
        /**
         * A point was removed from the blackboard.
         * <ul>
         * <li>The source is the {@link PrimitiveShape} that the point was removed from</li>
         * <li>The OldValue is the {@link Point} removed</li>
         * <li>The NewValue is null</li>
         * </ul>
         */
        REMOVE_POINT,
        /**
         * A point was moved in the blackboard.
         * <ul>
         * <li>The source is a Collection of {@link PrimitiveShape} that were affected by the
         * change</li>
         * <li>The OldValue is the {@link Point} moved</li>
         * <li>The NewValue is the new {@link Point}</li>
         * </ul>
         */
        MOVE_POINT,
        /**
         * One or more Geometries were added to the blackboard.
         * <ul>
         * <li>The source is the {@link EditBlackboard} that the geometries were added to</li>
         * <li>The OldValue is a null</li>
         * <li>The NewValue is a Collection of the EditGeoms that were added</li>
         * </ul>
         */
        ADD_GEOMS,
        /**
         * The geometries in the EditBlackboard were set.
         * <ul>
         * <li>The source is the {@link EditBlackboard} that was modified</li>
         * <li>The OldValue is the Collection of {@link EditGeom} objects that were in the
         * blackboard before the change</li>
         * <li>The NewValue is a Collection of {@link EditGeom} objects</li>
         * </ul>
         */
        SET_GEOMS,
        /**
         * One or more Geometries were removed from the blackboard.
         * <ul>
         * <li>The source is the {@link EditBlackboard} that the geometries were removed from</li>
         * <li>The OldValue is a Collection of {@link EditGeom}</li>
         * <li>The NewValue is a null</li>
         * </ul>
         */
        REMOVE_GEOMS,
        /**
         * The Map to Screen transform has changed
         * <ul>
         * <li>The source is a {@link EditBlackboard}</li>
         * <li>The OldValue is the previous AffineTransform of the Blackboard</li>
         * <li>The NewValue is a Map<Point, List<Point>> where the key is the original point and the
         * value is the new location(s) of the point after translation on a zoom in a single point
         * can become more than one point.</li>
         * </ul>
         */
        TRANFORMATION,
        /**
         * The Selection has changed
         * <ul>
         * <li>The source is a {@link EditBlackboard}</li>
         * <li>The OldValue is the previous selection</li>
         * <li>The NewValue is a the new selection</li>
         * </ul>
         */
        SELECTION,
        /**
         * A special case where a single point was added to many geometries.
         * <ul>
         * <li>The source is a Collection of PrimitiveShapes that had the point added</li>
         * <li>The OldValue is null</li>
         * <li>The NewValue is a added Point</li>
         * </ul>
         */
        ADD_POINT_TO_MANY, CLEARED
    }

    private final Object source;

    private final Object before;

    private final Object after;

    private final EventType type;

    private EditBlackboard editBlackboard;

    /**
     * Some data where information can be squirreled away for use by the framework. this should not
     * be depended on by non-uDig developers.
     */
    public Object privateData;

    public EditBlackboardEvent(EditBlackboard bb, Object source2, EventType type2, Object before2,
            Object after2) {
        assertLegalValues(source2, type2, before2, after2);
        this.editBlackboard = bb;
        source = source2;
        type = type2;
        this.before = before2;
        this.after = after2;
    }

    /**
     * @return Returns the source of the change this is determine by the type of the event.
     * @see EventType
     */
    public Object getSource() {
        return source;
    }

    /**
     * @return Returns the value after the change.
     * @see EventType
     */
    public Object getNewValue() {
        return after;
    }

    /**
     * @return Returns the value before the change.
     * @see EventType
     */
    public Object getOldValue() {
        return before;
    }

    /**
     * @return Returns the type.
     */
    public EventType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    private void assertLegalValues(Object source2, EventType type2, Object before2, Object after2) {
        switch (type2) {
        case ADD_GEOMS:
            if (!(source2 instanceof EditBlackboard)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "EditBlackboard was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (before2 != null) {
                throw new IllegalArgumentException("The before value of a " + type2 //$NON-NLS-1$
                        + " event be null was " + before2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(after2 instanceof Collection))
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be a Collection was " + after2.getClass().getName()); //$NON-NLS-1$
            if (((Collection) after2).isEmpty()) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be larger than 0"); //$NON-NLS-1$
            }
            for (Iterator<Object> iter = ((Collection<Object>) after2).iterator(); iter
                    .hasNext();) {
                Object obj = iter.next();
                if (!(obj instanceof EditGeom))
                    throw new IllegalArgumentException(
                            "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                    "be a Collection of EditGeoms was " + obj.getClass().getName()); //$NON-NLS-1$
            }
            break;
        case ADD_POINT:
            if (!(source2 instanceof PrimitiveShape)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "PrimitiveShape was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (before2 != null) {
                throw new IllegalArgumentException("The before value of a " + type2 //$NON-NLS-1$
                        + " event be null was " + before.getClass().getName()); //$NON-NLS-1$
            }
            if (!(after2 instanceof Point)) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be a Point was " + after2.getClass().getName()); //$NON-NLS-1$
            }
            break;
        case MOVE_POINT:
            if (!(source2 instanceof Set)) {
                throw new IllegalArgumentException("The source of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                        "be a Set of PrimitiveShapes was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (((Set) source2).isEmpty()) {
                throw new IllegalArgumentException(
                        "The source value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be larger than 0"); //$NON-NLS-1$
            }
            for (Iterator<Object> iter = ((Set<Object>) source2).iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (!(obj instanceof PrimitiveShape))
                    throw new IllegalArgumentException(
                            "The source of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                    "be a Set of PrimitiveShape was " + obj.getClass().getName()); //$NON-NLS-1$
            }

            if (!(before2 instanceof Point)) {
                throw new IllegalArgumentException("The before value of a " + type2 //$NON-NLS-1$
                        + " event be a Point was " + before.getClass().getName()); //$NON-NLS-1$
            }
            if (!(after2 instanceof Point)) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be a Point was " + after2.getClass().getName()); //$NON-NLS-1$
            }
            break;
        case REMOVE_GEOMS:
            if (!(source2 instanceof EditBlackboard)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "EditBlackboard was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(before2 instanceof Collection)) {
                throw new IllegalArgumentException("The before value of a " + type2 //$NON-NLS-1$
                        + " event be a EditGeom was " + before.getClass().getName()); //$NON-NLS-1$
            }
            if (after2 != null) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be null"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            break;

        case REMOVE_POINT:
            if (!(source2 instanceof Set)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "Set was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(before2 instanceof Point)) {
                throw new IllegalArgumentException("The before value of a " + type2 //$NON-NLS-1$
                        + " event be a Point was " + before.getClass().getName()); //$NON-NLS-1$
            }
            if (after2 != null) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be null"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            break;
        case SET_GEOMS:
            if (!(source2 instanceof EditBlackboard)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "EditBlackboard was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(before2 instanceof Collection)) {
                throw new IllegalArgumentException(
                        "The before value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be a Collection was " + before2.getClass().getName()); //$NON-NLS-1$
            }
            if (((Collection) before2).isEmpty()) {
                throw new IllegalArgumentException(
                        "The before value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be larger than 0"); //$NON-NLS-1$
            }
            for (Iterator<Object> iter = ((Collection<Object>) before2).iterator(); iter
                    .hasNext();) {
                Object obj = iter.next();
                if (!(obj instanceof EditGeom)) {
                    throw new IllegalArgumentException(
                            "The before value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                    "be a Collection of EditGeoms found a " //$NON-NLS-1$
                                    + obj.getClass().getName());
                }
            }

            if (!(after2 instanceof Collection)) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be a Collection was " + after2.getClass().getName()); //$NON-NLS-1$
            }
            if (((Collection) after2).isEmpty()) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                "be larger than 0"); //$NON-NLS-1$
            }
            for (Iterator<Object> iter = ((Collection<Object>) after2).iterator(); iter
                    .hasNext();) {
                Object obj = iter.next();
                if (!(obj instanceof EditGeom))
                    throw new IllegalArgumentException(
                            "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                    "be a Collection of EditGeoms was " + obj.getClass().getName()); //$NON-NLS-1$
            }
            break;
        case TRANFORMATION:
            if (!(source2 instanceof EditBlackboard)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "EditBlackboard was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(before2 instanceof AffineTransform)) {
                throw new IllegalArgumentException(
                        "The before value of a " + type2 + " event be an Affine transform" + //$NON-NLS-1$ //$NON-NLS-2$
                                " but was :" + before2.getClass().getName()); //$NON-NLS-1$
            }
            if (!(after2 instanceof Map)) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                " a java.util.Map was: " + after2.getClass().getName()); //$NON-NLS-1$
            }

            break;

        case SELECTION:
            if (!(source2 instanceof Selection)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "Selection was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            // TODO check before and after
            break;
        case ADD_POINT_TO_MANY:
            if (!(source2 instanceof Collection)) {
                throw new IllegalArgumentException(
                        "The source of a " + type2 + " event must be a " + //$NON-NLS-1$ //$NON-NLS-2$
                                "java.util.Collection was " + source2.getClass().getName()); //$NON-NLS-1$
            }
            for (Iterator<Object> iter = ((Collection<Object>) source2).iterator(); iter
                    .hasNext();) {
                Object obj = iter.next();
                if (!(obj instanceof PrimitiveShape))
                    throw new IllegalArgumentException(
                            "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                    "be a Collection of EditGeoms was " + obj.getClass().getName()); //$NON-NLS-1$
            }
            if (before2 != null) {
                throw new IllegalArgumentException(
                        "The before value of a " + type2 + " event be null");//$NON-NLS-1$ //$NON-NLS-2$
            }
            if (!(after2 instanceof Point)) {
                throw new IllegalArgumentException(
                        "The after value of a " + type2 + " event must be " + //$NON-NLS-1$ //$NON-NLS-2$
                                " a Point was: " + after2.getClass().getName()); //$NON-NLS-1$
            }
            break;

        default:
            break;
        }

    }

    @SuppressWarnings("unchecked")
    public Map<Point, List<Point>> getTransformationMap() {
        if (type != EventType.TRANFORMATION) {
            throw new IllegalStateException("Event must be a TRANSFORMATION event but was " + type); //$NON-NLS-1$
        }
        return (Map<Point, List<Point>>) after;
    }

    @Override
    public String toString() {
        return getType() + " before: " + before + " after:" + after; //$NON-NLS-1$//$NON-NLS-2$
    }

    public EditBlackboard getEditBlackboard() {
        return editBlackboard;
    }
}
