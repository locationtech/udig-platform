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
package org.locationtech.udig.tools.edit.behaviour;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.locationtech.udig.project.command.UndoRedoCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.SelectVertexCommand;
import org.locationtech.udig.tools.edit.commands.SelectVertexCommand.Type;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.Selection;

/**
 * <p>
 * Requirements:
 * <ul>
 * <li>eventType PRESSED</li>
 * <li>handler has currentGeom</li>
 * <li>edit state is modified or NONE</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>adds selected vertex</li>
 * <li>sets Edit State to Modified</li>
 * </ul>
 * </p>
 *
 * @author jones
 * @since 1.1.0
 */
public class SelectVertexOnMouseDownBehaviour implements EventBehaviour {

    @Override
    public boolean isValid(EditToolHandler handler, MapMouseEvent e, EventType eventType) {
        boolean currentGeomNotNull = handler.getCurrentGeom() != null;
        boolean eventTypePressed = eventType == EventType.PRESSED;
        boolean button1Changed = e.button == MapMouseEvent.BUTTON1;
        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        boolean overPoint = editBlackboard.overVertex(Point.valueOf(e.x, e.y),
                PreferenceUtil.instance().getVertexRadius()) != null;

        return currentGeomNotNull && eventTypePressed && button1Changed && !e.modifiersDown()
                && e.buttons == MapMouseEvent.BUTTON1 && overPoint;
    }

    @Override
    public UndoableMapCommand getCommand(EditToolHandler handler, MapMouseEvent e,
            EventType eventType) {
        if (!isValid(handler, e, eventType))
            throw new IllegalArgumentException("Not valid state", new Exception()); //$NON-NLS-1$

        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        Point point = handler.getEditBlackboard(handler.getEditLayer())
                .overVertex(Point.valueOf(e.x, e.y), PreferenceUtil.instance().getVertexRadius());
        List<EditGeom> geoms = null;
        if (point != null)
            geoms = editBlackboard.getGeoms(point.getX(), point.getY());
        else {
            EditPlugin.trace(EditPlugin.SELECTION, "VertexSelectorBehaviour: Not over vertex (" //$NON-NLS-1$
                    + e.x + "," + e.y + ")", null); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        Selection selection = editBlackboard.getSelection();
        List<UndoableMapCommand> commands = new ArrayList<>();

        if (!selection.contains(point))
            commands.add(new SelectVertexCommand(editBlackboard, point, Type.SET));

        if (geoms != null && geoms.contains(handler.getCurrentGeom())) {
            if (handler.getCurrentState() == EditState.NONE) {
                commands.add(new SetEditStateCommand(handler, EditState.MODIFYING));
            }
        }
        if (commands.isEmpty()) {
            return null;
        } else {
            UndoableComposite undoableComposite = new UndoableComposite(commands);
            undoableComposite.setMap(handler.getContext().getMap());
            try {
                undoableComposite.execute(new NullProgressMonitor());
            } catch (Exception e1) {
                throw (RuntimeException) new RuntimeException().initCause(e1);
            }
            return new UndoRedoCommand(undoableComposite);
        }
    }

    @Override
    public void handleError(EditToolHandler handler, Throwable error, UndoableMapCommand command) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
