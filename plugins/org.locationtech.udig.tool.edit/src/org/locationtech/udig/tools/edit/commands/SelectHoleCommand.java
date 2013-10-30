/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.awt.geom.GeneralPath;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.PostDeterminedEffectCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.PrimitiveShapeIterator;

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
