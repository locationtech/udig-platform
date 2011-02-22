package net.refractions.udig.tutorials.genericprojectelement;

import java.util.Collections;
import java.util.Random;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.element.ProjectElementAdapter;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * This is here so that we can create elements through the UI.  It randomly chooses a
 * label.
 *
 * @author jesse
 * @since 1.1.0
 */
public class AddElementAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    public final Random random = new Random();

    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
    }

    public void run( IAction action ) {
        IProject project = ApplicationGIS.getActiveProject();
        String name = null;
        if( random.nextBoolean() ){
            StringBuilder builder = new StringBuilder();
            for(int i =0; i<(random.nextInt(5)+2);i++ ){
                builder.append(String.valueOf(random.nextInt(9)));
            }
            name = builder.toString();
        }
        ProjectElementAdapter element = ApplicationGIS.createGeneralProjectElement(project, name,
                MyProjectElement.class, MyProjectElement.EXT_ID);
        MyProjectElement myElement = (MyProjectElement) element.getBackingObject();
        myElement.setLabel(name);

        ProjectExplorer.getProjectExplorer().setSelection(Collections.singleton(element),true);
    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
