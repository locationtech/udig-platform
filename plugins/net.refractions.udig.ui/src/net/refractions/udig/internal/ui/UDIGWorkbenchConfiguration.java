package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.WorkbenchConfiguration;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

public class UDIGWorkbenchConfiguration implements WorkbenchConfiguration {


    public void configureWorkbench( IWorkbenchWindowConfigurer configurer ) {
        configurer.setShowProgressIndicator(true);
//        Rectangle bounds = Display.getDefault().getPrimaryMonitor().getClientArea();
        configurer.setInitialSize(new Point(1024, 768));
//        configurer.setInitialSize(new Point(bounds.width, bounds.height));

//        configurer.setShowPerspectiveBar( true );
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowFastViewBars(true);

// Menu contribution is too early
//        IMenuManager menubar = configurer.getActionBarConfigurer().getMenuManager();
//        final OperationMenuFactory operationMenuFactory = UiPlugin.getDefault().getOperationMenuFactory();
//
//        menubar.add( operationMenuFactory.getMenu() );
    }

}
