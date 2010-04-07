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

import java.util.Collections;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Calls newGeom on the EditBlackboard.
 * 
 * @author jones
 * @since 1.1.0
 */
public class CreateEditGeomCommand extends AbstractCommand implements UndoableMapCommand,
    IBlockingProvider<EditGeom>{

    private String fid;
    private EditBlackboard blackboard;
    private EditGeom geom;
    private ShapeType shapeType;

    /**
     * New Instance
     * 
     * @param blackboard the blackboard to creat the new geom on.
     * @param fid the string to use as the feature id of the new Geom
     */
    public CreateEditGeomCommand( EditBlackboard blackboard, String fid ) {
        this(blackboard, fid, ShapeType.UNKNOWN);
    }

    /**
     * New Instance
     * 
     * @param blackboard the blackboard to creat the new geom on.
     * @param fid the string to use as the feature id of the new Geom
     * @param shapeType the type of shape to create.
     */
    public CreateEditGeomCommand( EditBlackboard blackboard, String fid, ShapeType shapeType ) {
        this.blackboard=blackboard;
        this.fid=fid;
        this.shapeType=shapeType;
        }

    public void run( IProgressMonitor monitor ) throws Exception {
        this.geom=blackboard.newGeom(fid, shapeType);
    }

    public String getName() {
        return Messages.CreateEditGeomCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        blackboard.removeGeometries(Collections.singleton(geom));
    }

    public EditGeom get(IProgressMonitor monitor, Object... params) {
        return geom;
    }

    public IBlockingProvider<EditGeom> getEditGeomProvider() {
        return new EditUtils.StaticEditGeomProvider(geom);
    }
    public IBlockingProvider<PrimitiveShape> getShapeProvider() {
        return new ShapeProvider();
    }
    
    class ShapeProvider implements IBlockingProvider<PrimitiveShape>{

        public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
            return geom.getShell();
        }
        
    }

}
