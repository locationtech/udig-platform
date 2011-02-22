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

import java.util.List;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Creates a hole in a EditGeom and sets it as the currentShape in the handler if a handler is provided.
 *
 * @author jones
 * @since 1.1.0
 */
public class CreateAndSelectHoleCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private PrimitiveShape hole;
    private PrimitiveShape oldShape;
    private IBlockingProvider<PrimitiveShape> newShape;


    public CreateAndSelectHoleCommand( EditToolHandler handler ) {
        this.handler=handler;
        newShape=new EditUtils.EditToolHandlerShapeProvider(handler);
    }

    public CreateAndSelectHoleCommand( IBlockingProvider<PrimitiveShape> shape ) {
        this.newShape=shape;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        PrimitiveShape primitiveShape = newShape.get(monitor);
        List<PrimitiveShape> holes = primitiveShape.getEditGeom().getHoles();
        for( PrimitiveShape shape : holes ) {
            if( shape.getNumPoints()==0 ){
                hole=shape;
                break;
            }
        }
        if( hole==null )
            this.hole = primitiveShape.getEditGeom().newHole();
        if( handler!=null ){
            this.oldShape=handler.getCurrentShape();
            handler.setCurrentShape(hole);
        }
    }

    public String getName() {
        return Messages.CreateAndSelectHoleCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( handler!=null)
            handler.setCurrentShape(oldShape);
        newShape.get(monitor).getEditGeom().getHoles().remove(hole);
    }

    /**
     *
     * @return
     */
    public IBlockingProvider<PrimitiveShape> getHoleProvider() {
        return new ShapeProvider();
    }

    class ShapeProvider implements IBlockingProvider<PrimitiveShape>{

        public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
            return hole;
        }

    }
}
