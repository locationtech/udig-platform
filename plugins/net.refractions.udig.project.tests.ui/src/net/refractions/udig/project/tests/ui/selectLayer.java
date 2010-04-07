package net.refractions.udig.project.tests.ui;

import java.util.List;

import net.refractions.udig.project.internal.impl.MapImpl;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class selectLayer implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        List<MapImpl> maps = ApplicationGIS.getActiveProject().getElements(MapImpl.class);

        ProjectExplorer.getProjectExplorer().setSelection(maps.get(1), maps.get(1).getMapLayers().get(1));
    }

}
