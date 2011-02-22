package net.refractions.udig.mapgraphic.internal.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.mapgraphic.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class MapGraphicWizardPage extends WizardPage implements UDIGConnectionPage {

    public static final String ID = "net.refractions.udig.mapgraphic.wizardPageId"; //$NON-NLS-1$

    public MapGraphicWizardPage() {
        super(Messages.MapGraphic_title);
    }

    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> services = factory.createService(MapGraphicService.SERVICE_URL);

        return services;
    }

    public String getId() {
        return ID;
    }

    public Map<String, Serializable> getParams() {
        return null;
    }

    public List<URL> getURLs() {
        ArrayList<URL> l = new ArrayList<URL>();
        l.add(MapGraphicService.SERVICE_URL);
        return l;
    }

    public void createControl( Composite parent ) {
        setControl(new Composite(parent, SWT.NONE));
        parent.getDisplay().asyncExec(new Runnable(){
            public void run() {
                final IWizard wizard = getWizard();
                if( wizard instanceof WorkflowWizard){
                    try {
                        getContainer().run(true, true, new IRunnableWithProgress(){

                            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                                ((WorkflowWizard)wizard).getWorkflow().next(monitor);
                            }

                        });
                    } catch (Exception e) {
                        MapGraphicPlugin.log("", e); //$NON-NLS-1$
                    }
                }else{
                    getWizard().performFinish();
                }
                if (Display.getCurrent() == null) { //wizard is invisible
                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                            close();
                        }
                    });
                }
            }
        });
    }

    void close() {
        if( getContainer()!=null && getContainer().getShell()!=null && !getContainer().getShell().isDisposed() ){
            getContainer().getShell().close();
        }
    }
}
