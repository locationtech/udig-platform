package net.refractions.udig.project.tests.ui;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class TestSelectProject implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ProjectExplorer.getProjectExplorer().setSelection(ApplicationGIS.getProjects().get(1), true);
    }

}
