/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog.ui.export;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.internal.ui.ImageConstants;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardAdapter;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Used to configure a {@link WorkflowWizardDialog} and {@link WorkflowWizard} with
 * an initial workflow provided by {@link #createWorkflow()}.
 * 
 * @since 1.2.0
 */
public class CatalogExport {
    
    public static class CatalogExportAdapter extends WorkflowWizardAdapter{

        public CatalogExportAdapter() {
            super(new CatalogExport().wizard);
        }

    }

    
    Shell shell;
    private WorkflowWizardDialog dialog;
    protected WorkflowWizard wizard;
    
    static String SHAPEFILE_EXT = ".shp"; //$NON-NLS-1$
    static String POLY_SUFFIX = "_poly"; //$NON-NLS-1$
    static String POINT_SUFFIX = "_point"; //$NON-NLS-1$
    static String LINE_SUFFIX = "_line"; //$NON-NLS-1$

    /**
     * Creates a new instance and calls {@link #init()}
     */
    public CatalogExport() {
        initWorkflow();
    }

    /**
	 * Creates a new instance and calls {@link #init()} if initialize is true. This method
	 * is here so that initialization can be deferred until later so the
	 * subclass can do other initialization first.  If initialize is false make sure to call
	 * {@link #init()} before using the object.
	 * 
	 * @param initialize
	 *            if true initialize is called
	 */
    public CatalogExport(boolean initialize) {
    	if( initialize ){
    		initWorkflow();
    	}
    }


    /**
	 * This is a "template method" (see GOF patterns book). It first calls
	 * createWorkFlow() then createPageMapping() then createWorkflowWizard().
	 * It also creates a dialog for the workflow wizard to be opened up in by calling createShell().  
	 * The dialog is blocking.
	 */
	protected final void initDialog() {
        initWorkflow();

        PlatformGIS.syncInDisplayThread(
            new Runnable() {
                public void run() {
                    shell=createShell();
                }   
            }
        );

        dialog = new WorkflowWizardDialog(
            shell, wizard   
        );

        dialog.setBlockOnOpen(true);
    }

    protected void initWorkflow() {
        Workflow workflow = createWorkflow();
        Map<Class<? extends State>, WorkflowWizardPageProvider> map = createPageMapping();
        wizard = createWorkflowWizard(workflow,map);
    }   

    /**
     * Must be called in the Display thread.
     */
    Shell createShell() {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if( window!=null ){
            Shell temp = window.getShell();
            if( temp!=null )
                return temp;
        }

        Display d=PlatformUI.getWorkbench().getDisplay();

        if( d==null )
            d=Display.getCurrent();

        return new Shell(d.getActiveShell());

    }

    public WorkflowWizardDialog getDialog() {
        if( dialog==null ){
            initDialog();
        }
        
        return dialog;
    }

    public void open() {

        Display.getDefault().asyncExec(
            new Runnable() {
                public void run() {
                    getDialog().open();  
                };
            }
        );
    }

    public void run(IProgressMonitor monitor, Object context) {
        if( dialog==null ){
            initDialog();
        }

        dialog.getWorkflowWizard().getWorkflow().setContext(context);
        String name=Messages.CatalogExport_taskname; 
        monitor.beginTask(name, 100);
        monitor.setTaskName(name);
        try {
            dialog.runHeadless(new SubProgressMonitor(monitor, 100));
        } finally {
            monitor.done();
        }
    }

    protected Workflow createWorkflow() {
        // can we look up the work bench selection here?
        ExportResourceSelectionState layerState = new ExportResourceSelectionState();
        Workflow workflow = new Workflow(new State[]{layerState});
        return workflow;
    }

    protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {
        HashMap<Class<? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
        String title = Messages.LayerSelectionPage_title;
        ImageDescriptor banner = CatalogUIPlugin.getDefault().getImageDescriptor(ImageConstants.PATH_WIZBAN+"exportshapefile_wiz.gif"); //$NON-NLS-1$
        ExportResourceSelectionPage page = new ExportResourceSelectionPage("Select Layers", title, banner ); 
        map.put(ExportResourceSelectionState.class, new BasicWorkflowWizardPageFactory(page));

        //TODO: add export support for formats other than shapefile

        return map;
    }

    protected WorkflowWizard createWorkflowWizard(Workflow workflow, Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        return new CatalogExportWizard(workflow,map);
    }

    public static void setError(final WizardDialog wizardDialog, final String msg, Throwable e) {

        CatalogUIPlugin.log(msg, e);
        
		if( Display.getCurrent()==null ){
			wizardDialog.getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
			        wizardDialog.setErrorMessage(msg);
				}
			});
		}else{
			wizardDialog.setErrorMessage(msg);
		}
	}
}

