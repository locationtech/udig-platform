package net.refractions.udig.internal.ui.operations;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.operations.OpAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class RunOperationsAction extends Action {

    @Override
    public void run() {
        RunOperationDialog dialog = new RunOperationDialog(Display.getDefault().getActiveShell(), 
                UiPlugin.getDefault().getOperationMenuFactory());
               
        dialog.open();
        
        if (dialog.getReturnCode() == Window.CANCEL)
            return;
        
        final OpAction[] actions = dialog.getSelection();
        for (OpAction action : actions) {
            action.run();
        }
    }
}
