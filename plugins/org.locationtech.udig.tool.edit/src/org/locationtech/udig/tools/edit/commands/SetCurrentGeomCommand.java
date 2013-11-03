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

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

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
