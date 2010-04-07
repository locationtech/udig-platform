package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.ChangeCRSCommand;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class SyncProjection implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer layer = (ILayer) target;
        IMap map = layer.getMap();
        
        final CoordinateReferenceSystem after = layer.getCRS();
        
        MapCommand changeProjection = new ChangeCRSCommand(after);
        map.sendCommandASync( changeProjection );
        
    }

}
