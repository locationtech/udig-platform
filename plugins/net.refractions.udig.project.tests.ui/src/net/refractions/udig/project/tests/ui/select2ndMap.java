package net.refractions.udig.project.tests.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.impl.MapImpl;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class select2ndMap implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        List<MapImpl> maps = ApplicationGIS.getActiveProject().getElements(MapImpl.class);
        Set<Map> selection=new HashSet<Map>();
        selection.add(maps.get(1));
        ProjectExplorer.getProjectExplorer().setSelection(selection, true);
    }

}
