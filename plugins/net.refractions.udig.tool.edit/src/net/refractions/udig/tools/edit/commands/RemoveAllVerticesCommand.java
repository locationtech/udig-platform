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

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Removes all vertices from EditGeom's shell.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class RemoveAllVerticesCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private EditGeom oldGeom;


    public RemoveAllVerticesCommand( EditToolHandler handler ) {
        this.handler=handler;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        EditGeom geom = handler.getCurrentGeom();

        oldGeom=new EditGeom(geom);
        for( Point point : geom.getShell() ) {
            geom.getEditBlackboard().removeCoords(point.getX(), point.getY(), geom.getShell() );
        }
    }

    public String getName() {
        return "RemoveAllVerticesCommand"; //$NON-NLS-1$
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        EditBlackboard bb = oldGeom.getEditBlackboard();
        EditGeom geom = bb.newGeom(oldGeom.getFeatureIDRef().get(), oldGeom.getShapeType());
        for( Point p : oldGeom.getShell() ) {
            bb.addPoint(p.getX(), p.getY(), geom.getShell());
        }
        
        for( PrimitiveShape shape : oldGeom.getHoles() ) {
            PrimitiveShape hole = geom.newHole();
            for( Point p : shape ) {
                bb.addPoint(p.getX(), p.getY(), hole);
            }
        }

    }

}
