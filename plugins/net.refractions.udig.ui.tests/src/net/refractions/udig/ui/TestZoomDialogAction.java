package net.refractions.udig.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.junit.Ignore;

@Ignore
public class TestZoomDialogAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    public void init( IWorkbenchWindow window ) {
    }
    
    @Override
    public void run( IAction action ) {
        MessageDialog d=new MessageDialog(Display.getDefault().getActiveShell(), 
                "Test", null, "Test zoom dialog", MessageDialog.INFORMATION,
                new String[]{"OK"}, 1);
        ZoomingDialog zd=new ZoomingDialog(Display.getDefault().getActiveShell(), d, new Rectangle(0,0,10,10));
        zd.open();
    }

}
