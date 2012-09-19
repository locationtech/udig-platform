/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.ui.wizard;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.internal.ui.ConnectionPageDecorator;
import net.refractions.udig.catalog.internal.ui.ReflectionWorkflowWizardPageProvider;
import net.refractions.udig.catalog.ui.ConnectionErrorPage;
import net.refractions.udig.catalog.ui.DataSourceSelectionPage;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.ConnectionErrorState;
import net.refractions.udig.catalog.ui.workflow.ConnectionFailurePage;
import net.refractions.udig.catalog.ui.workflow.ConnectionFailureState;
import net.refractions.udig.catalog.ui.workflow.DataSourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.IntermediateState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardAdapter;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.PlatformUI;

/**
 * Used to configure a {@link WorkflowWizardDialog} and {@link WorkflowWizard} with
 * an initial workflow provided by {@link #createWorkflow()}.
 * <p>
 * Example use: dragging and dropping a URL into the catalog.
 * </p>
 * @since 1.2.0
 */
public class CatalogImport { 

	Shell shell;
	private WorkflowWizardDialog dialog;
    protected WorkflowWizard wizard;
	
	public CatalogImport() {
		initWorkflow();
	}
	
    private void initDialog() {
        if( Display.getCurrent()==null ){
		PlatformGIS.syncInDisplayThread(
			new Runnable() {
				public void run() {
                    shell=createShell();
				}	
			}
		);
        }else{
            shell=createShell();
        }
		
		dialog = new WorkflowWizardDialog(
			shell, wizard	
		);
		dialog.setBlockOnOpen(true);
    }

    private void initWorkflow() {
        Workflow workflow = createWorkflow();
		Map<Class<? extends State>, WorkflowWizardPageProvider> map = createPageMapping();
		wizard = createWorkflowWizard(workflow,map);
    }	
	
    /**
     * Must be called in the Display thread.
     */
	Shell createShell() {
        Display d=PlatformUI.getWorkbench().getDisplay();
        if( d==null )
            d=Display.getCurrent();
        Shell parent = d.getActiveShell();
		return new Shell(parent);
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
					dialog.open();	
				};
			}
		);
	}
	
	/**
	 * Runs the workflow.
	 *
	 * @param monitor the monitor for 
	 * @param context
	 * @return
	 */
	public boolean run(IProgressMonitor monitor, Object context) {
	    if( dialog==null ){
	        initDialog();
	    }
		dialog.getWorkflowWizard().getWorkflow().setContext(context);
        String bind = MessageFormat.format(Messages.CatalogImport_monitor_task, new Object[] {format(context)});
        monitor.beginTask(bind, IProgressMonitor.UNKNOWN); 
        monitor.setTaskName(bind);
		try{
		   return dialog.runHeadless(new SubProgressMonitor(monitor,100));
		}finally{
			monitor.done();
		}
	}

    
    private String format( Object data ) {
        if( data instanceof URL ){
            return formatURL((URL)data);
        }if( data instanceof IGeoResource ){
            return ((IGeoResource)data).getIdentifier().getRef();
        }if (data instanceof IResolve ){
            return formatURL(((IResolve)data).getIdentifier());
        }
        return data.toString();
    }

    private String formatURL( URL url ) {
        return url.getProtocol()+"://"+url.getPath(); //$NON-NLS-1$
    }

	protected Workflow createWorkflow() {
		DataSourceSelectionState state = new DataSourceSelectionState(false);
		Workflow workflow = new Workflow(new State[]{state});
		
		return workflow;
	}
	
	protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {
		HashMap<Class<? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
		
		addToMap(map, DataSourceSelectionState.class, DataSourceSelectionPage.class);
        
        WorkflowWizardPageProvider provider = new ReflectionWorkflowWizardPageProvider(ConnectionPageDecorator.class);
        map.put(IntermediateState.class, provider);
        map.put(EndConnectionState.class, provider);
        
        addToMap(map, ConnectionErrorState.class, ConnectionErrorPage.class);
        
        addToMap(map, ConnectionFailureState.class, ConnectionFailurePage.class);
		return map;
	}

    private void addToMap( Map<Class< ? extends State>, WorkflowWizardPageProvider> map, Class<? extends State> key, 
            Class<? extends WorkflowWizardPage> page ) {
        WorkflowWizardPageProvider pageFactory = new ReflectionWorkflowWizardPageProvider(page);
        map.put(key, pageFactory);
    }
	

    protected WorkflowWizard createWorkflowWizard(Workflow workflow, Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
		return new CatalogImportWizard(workflow,map);
	}
	/**
	 * Extends {@link WorkflowWizardAdapter} by passing the CatalogImport wizard to the constructor.
	 * @author jesse
	 * @since 1.1.0
	 */
	public static class CatalogImportAdapter extends WorkflowWizardAdapter implements IImportWizard {

	    public CatalogImportAdapter() {
	        super(new CatalogImport().wizard);
	    }
	}

}

