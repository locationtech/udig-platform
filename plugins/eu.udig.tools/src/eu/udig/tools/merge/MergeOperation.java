package eu.udig.tools.merge;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import eu.udig.tools.merge.internal.view.MergeView;

public class MergeOperation implements IOp {
    
    @Override
    public void op(final Display display, Object target, IProgressMonitor monitor) throws Exception {

        Thread t = new Thread() {

            public void run() {
                try {
                    // getView has the effect of OPENING the view.
                    MergeView mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        display.asyncExec(t);

    }
}
