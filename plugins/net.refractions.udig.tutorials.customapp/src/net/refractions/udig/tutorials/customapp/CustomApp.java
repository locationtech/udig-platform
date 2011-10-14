package net.refractions.udig.tutorials.customapp;

import net.refractions.udig.internal.ui.UDIGApplication;
import net.refractions.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class CustomApp extends UDIGApplication implements IApplication {

    @Override
    protected WorkbenchAdvisor createWorkbenchAdvisor() {
        return new UDIGWorkbenchAdvisor() {
            @Override
            public String getInitialWindowPerspectiveId() {
                return "net.refractions.udig.tutorials.customapp.perspective";
            }
        };
    }

}
