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

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Sets the current geometry in the handler to be the passed in geometry
 * 
 * @author jones
 * @since 1.1.0
 */
public class SetCurrentGeomCommand extends AbstractCommand implements UndoableMapCommand {

    EditToolHandler handler;
    PrimitiveShape oldShape;
    PrimitiveShape newShape;
    private IBlockingProvider<PrimitiveShape> provider;
    private SimpleFeature oldEditFeature;
    private Layer oldEditLayer;
    
    /**
     * @param handler2
     * @param newShape2
     */
    public SetCurrentGeomCommand( EditToolHandler handler2, PrimitiveShape newShape2 ) {
        this.handler=handler2;
        this.newShape=newShape2;
    }

    /**
     * @param handler2
     * @param provider
     */
    public SetCurrentGeomCommand( EditToolHandler handler2, IBlockingProvider<PrimitiveShape> provider ) {
        this.provider=provider;
        this.handler=handler2;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        oldEditFeature=getMap().getEditManager().getEditFeature();
        oldEditLayer=getMap().getEditManagerInternal().getEditLayerInternal();
        if( oldEditFeature!=null ){
            getMap().getEditManagerInternal().setEditFeature(null, null);
        }
        
        if( oldShape==null )
            oldShape=handler.getCurrentShape();
        if(newShape==null && provider!=null)
            newShape=provider.get(monitor);
        handler.setCurrentShape(newShape);
    }

    public String getName() {
        return Messages.SetCurrentGeomCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        handler.setCurrentShape(oldShape);
        getMap().getEditManagerInternal().setEditFeature(oldEditFeature, oldEditLayer);
    }

}
