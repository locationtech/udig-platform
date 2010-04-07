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
package net.refractions.udig.tools.edit.commands;

import java.awt.geom.GeneralPath;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.PostDeterminedEffectCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.PrimitiveShapeIterator;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Selects the hole (sets the handler's current shape) that the mouse is over or 
 * nothing if mouse is not over a hole.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectHoleCommand extends AbstractCommand implements UndoableMapCommand, PostDeterminedEffectCommand {

    private MapMouseEvent event;
    private EditToolHandler handler;
    private PrimitiveShape currentShape;

    public SelectHoleCommand( EditToolHandler handler, MapMouseEvent e ) {
        this.handler=handler;
        this.event=e;
    }

    public String getName() {
        return "Select Hole"; //$NON-NLS-1$
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        execute(monitor);
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        handler.setCurrentShape(currentShape);
    }

    public boolean execute( IProgressMonitor monitor ) throws Exception {
        this.currentShape=handler.getCurrentShape();

        if( currentShape==null )
            return false;
        
        for( PrimitiveShape shape : currentShape.getEditGeom() ) {
            if( shape==currentShape || currentShape.getEditGeom().getShell()==shape )
                continue;
            
            GeneralPath path=new GeneralPath();
            path.append(PrimitiveShapeIterator.getPathIterator(shape), false);
            
            if( path.contains( event.x, event.y) ){
                handler.setCurrentShape(shape);
                break;
            }
        }
        return true;
    }

}
