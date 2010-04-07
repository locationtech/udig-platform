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
package net.refractions.udig.tool.edit;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.provider.EditFeatureProvider;
import net.refractions.udig.project.command.provider.EditLayerProvider;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tools.edit.EditBlackboardUtil;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.SplitLineCommand;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.ui.operations.IOp;

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
