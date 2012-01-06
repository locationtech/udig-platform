/* uDig-Spatial Operations plugins
 * http://b5m.gipuzkoa.net
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial.
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
package eu.udig.tools.split;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import eu.udig.tools.internal.i18n.Messages;
import eu.udig.tools.internal.ui.util.DialogUtil;

/**
 * Edit tool {@link Behaviour} that takes the current shape from the {@link EditToolHandler} to use
 * it as a splitting line and splits the crossing Features from the current {@link ILayer layer}.
 * <p>
 * This behavior will either:
 * <ul>
 * <li>Split one or more Simple Features using a line
 * <li>Split one polygon feature by creating a hole
 * </ul> 
 * @see SplitFeaturesCommand
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
class SplitGeometryBehaviour implements Behaviour {


    public SplitGeometryBehaviour() {
    }

    /**
     * Returns <code>true</code> if there's a linestring in the {@link EditBlackboard}'s
     * {@link EditToolHandler} to use as splitting line
     */
    public boolean isValid( EditToolHandler handler ) {
        PrimitiveShape currentShape = handler.getCurrentShape();
        if (currentShape == null) {
            return false;
        }
        int nCoords = currentShape.getNumCoords();
        return nCoords > 1;
    }

    /**
     * Returns an {@link UndoableMapCommand} that's responsible of using the
     * {@link EditToolHandler handler}'s current shape (as a LineString) to split the features of
     * the current layer that intersects the trimming line.
     * <p>
     * When a feature's geometry is split, the original Feature will be deleted and as many new
     * Features as geometries result from the split will be created, with the same attributes than
     * the original one except the geometry, which will be a split part for each one.
     * </p>
     * 
     * @return the command that splits the geometries under the handler's current shape
     * @see SplitFeaturesCommand
     */
    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        assert handler != null;
        
        UndoableComposite commands = new UndoableComposite();
        
        commands.addCommand(new SetEditStateCommand(handler, EditState.BUSY ));
        commands.addCommand( new SplitFeaturesCommand(handler) );
        commands.addCommand( new SetEditStateCommand(handler, EditState.NONE ));
        
        return commands;
    }

    /**
     * Shows up the message to user and reinitialize the handler
     * @param handler
     * @param error
     * 
     */
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        assert error != null;

        String message = error.getMessage();
        DialogUtil.openError(Messages.SplitGeometryBehaviour_transaction_failed, message);
        
        //re-initializes the handler
        handler.setCurrentState( EditState.NONE ); // start again
        handler.setCurrentShape( null ); // stop drawing this line
        handler.getEditBlackboard( handler.getEditLayer() ).clear();
    }

}
