/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.edit;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.provider.EditFeatureProvider;
import org.locationtech.udig.project.command.provider.EditLayerProvider;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.tools.edit.EditBlackboardUtil;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.commands.SplitLineCommand;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Splits a line at the selected points.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SplitLineOp implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        Point[] points=(Point[]) target;
        
        IMap map = ApplicationGIS.getActiveMap();
        
        EditBlackboard editBlackboard = EditBlackboardUtil.getEditBlackboard(ApplicationGIS.createContext(map), 
                map.getEditManager().getSelectedLayer());
        
        ShapeProvider shapeProvider=new ShapeProvider(map);
        
         map.sendCommandASync(new SplitLineCommand(editBlackboard, shapeProvider, 
                 new EditFeatureProvider(map), new EditLayerProvider(map), 
                 new HashSet<Point>(Arrays.asList(points))));
    }
    
    static class ShapeProvider implements IBlockingProvider<PrimitiveShape>{

        private IMap map;

        ShapeProvider(IMap map2){
            this.map=map2;
        }
        
        public PrimitiveShape get( IProgressMonitor monitor, Object... params ) throws IOException {
            monitor.beginTask("", 1); //$NON-NLS-1$
            try{
                return (PrimitiveShape) map.getBlackboard().get(EditToolHandler.CURRENT_SHAPE);
            }finally{
                monitor.done();
            }
        }
        
    }

}
