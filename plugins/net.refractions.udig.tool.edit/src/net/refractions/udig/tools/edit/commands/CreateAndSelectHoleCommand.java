/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
 * Creates a hole in a EditGeom and sets it as the currentShape in the handler (if a handler is provided).
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
