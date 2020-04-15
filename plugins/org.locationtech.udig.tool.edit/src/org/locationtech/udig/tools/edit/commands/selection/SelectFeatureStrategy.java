/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands.selection;

import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.commands.SelectFeatureAsEditFeatureCommand;
import org.locationtech.udig.tools.edit.commands.SelectFeatureCommand;
import org.locationtech.udig.tools.edit.commands.SelectionParameter;
import org.locationtech.udig.tools.edit.commands.SelectionStrategy;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;

/**
 * A strategy for adding the features to the edit blackboard if no modifiers are down.  The first feature
 * is selected for editing each of the others are not.
 * <p>
 * Recognises the following SelectionParameter settings:
 * <ul>
 * <li>onlyAdd - true to add the feature to the existing selection
 *    (same effect can be had by using MOD1, ie control key on windows)
 * <li>acceptableClasses
 * <li>event   
 * </ul>
 * 
 * @author jesse
 * @since 1.1.0
 */
public class SelectFeatureStrategy implements SelectionStrategy {
    /**
     * @param monitor report progress
     * @param commands UndoableComposte; any commands will be added to this composite
     * @param parameters information controlling the selection process see class description for details
     * @param feature Feature being selected
     * @param firstFeature true if this is the first feature returned for selection
     */
    public void run( IProgressMonitor monitor, UndoableComposite commands,
            SelectionParameter parameters, SimpleFeature feature, boolean firstFeature ) {
        EditToolHandler handler = parameters.handler;
        Class< ? extends Geometry>[] acceptableClasses = parameters.acceptableClasses;
        boolean onlyAdd = parameters.onlyAdd;
        MapMouseEvent event = parameters.event;

        for( Class< ? extends Geometry> clazz : acceptableClasses ) {
            if (clazz.isAssignableFrom(feature.getDefaultGeometry().getClass())) {
                EditPlugin.trace(EditPlugin.SELECTION,
                        "Feature is one of the acceptable classes " + feature.getID(), null); //$NON-NLS-1$

                if (firstFeature && !keyboardModifierIndicatesAdd(handler, event) && !onlyAdd ) {
                    commands.addCommand(handler.getCommand(handler.getAcceptBehaviours()));
                    commands.addCommand(new SelectFeatureAsEditFeatureCommand(handler, feature,
                            handler.getEditLayer(), Point.valueOf(event.x, event.y)));
                } else {
                    if (onlyAdd || keyboardModifierIndicatesAdd(handler, event)) {
                        // call SelectFeaturecommand so that it is the CurrentEditGeom
                        commands.addCommand(new SelectFeatureCommand(handler, feature, Point
                                .valueOf(event.x, event.y)));
                    } else {
                        // call SelectFeaturecommand so that it is just added to the EditBlackboard
                        // but
                        // not as the CurrentEditGeom
                        commands.addCommand(new SelectFeatureCommand(handler, feature, null));
                    }
                }
            } else {
                EditPlugin.trace(EditPlugin.SELECTION,
                        "Feature is not one of the acceptable classes " + feature.getID(), null); //$NON-NLS-1$
            }
        }
    }

    /**
     * Returns true if shift is down or MOD1 is down and the mouse is over the currently selected geometry
     */
    private boolean keyboardModifierIndicatesAdd( EditToolHandler handler, MapMouseEvent event ) {
        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        boolean noIntersectingSelection = editBlackboard.getGeoms(event.x, event.y).isEmpty();
        return event.isShiftDown()
                || (noIntersectingSelection && event
                        .isModifierDown(MapMouseEvent.MOD1_DOWN_MASK));
    }

}
