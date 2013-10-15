/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.internal.datastore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.issues.IIssuesList;
import net.refractions.udig.issues.IIssuesPreferencePage;
import net.refractions.udig.issues.IssuesListConfigurator;
import net.refractions.udig.issues.StrategizedIssuesList;
import net.refractions.udig.issues.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * Configures {@link StrategizedIssuesList}.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PostgisIssuesListConfigurator implements IssuesListConfigurator {

    private static final String URL_ID = "URL"; //$NON-NLS-1$
    private static final String LAYER_ID = "LAYER"; //$NON-NLS-1$
    private static final IMemento DEFAULT_MEMENTO=XMLMemento.createWriteRoot("configuration"); //$NON-NLS-1$
    static{
        DEFAULT_MEMENTO.putString(URL_ID, "jdbc.postgis://the_username:the_password@the_host:5432/the_database/public"); //$NON-NLS-1$
        DEFAULT_MEMENTO.putString(LAYER_ID, "") ;//$NON-NLS-1$
    }
    private Text urlInput;
    private IMemento memento;
    private PostgisDatastoreStrategy list=new PostgisDatastoreStrategy();
    private IIssuesPreferencePage page;
    private Text layerInput;
    private String error;
    private StrategizedIssuesList issuesList;
    private Button createButton;

    public Control getControl( Composite parent, IIssuesPreferencePage page ){
        this.page=page;
        Composite comp=new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(2,false));
        createLabel(parent, comp, Messages.PostgisIssuesListConfigurator_urlLabel); 
        createURLInput(comp);
        createLabel(parent, comp, Messages.PostgisIssuesListConfigurator_layerLabel); 
        createLayerInput(comp);
        createTestButton(comp);
        createCreateButton(comp);
        
        this.update();
        return comp;
    }

    private void createCreateButton( Composite comp ) {
        createButton=new Button(comp, SWT.DEFAULT);
        createButton.setText("Create");
        createButton.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            public void widgetSelected( SelectionEvent e ) {
                try {
					page.runWithProgress(true, new IRunnableWithProgress(){

					    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
					            InterruptedException {
					        monitor.beginTask("Attempting to create a new issues layer", IProgressMonitor.UNKNOWN); 
					        error=list.createConnection();
					        page.setErrorMessage(error);
					        createButton.setEnabled(false);
					        monitor.done();
					    }

					});
				} catch (InvocationTargetException e1) {
		            ProjectUIPlugin.log("", e1); //$NON-NLS-1$
				} catch (InterruptedException e1) {
		            ProjectUIPlugin.log("", e1); //$NON-NLS-1$
				}

            }
            
        });
    }

    private void createTestButton( Composite comp ) {
        Button button=new Button(comp, SWT.DEFAULT);
        button.setText(Messages.PostgisIssuesListConfigurator_testButton); 
        button.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            public void widgetSelected( SelectionEvent e ) {
                runTest();
                update();
            }
            
        });
    }
    private void createLayerInput( Composite comp ) {
        layerInput=new Text(comp, SWT.BORDER);        
        GridData data=new GridData(SWT.FILL, SWT.NONE, true,false);
        data.verticalAlignment=SWT.CENTER;
        layerInput.setLayoutData(data);
        layerInput.setText(memento.getString(LAYER_ID));

        layerInput.addModifyListener(new ModifyListener(){

            public void modifyText( ModifyEvent e ) {
                list.tested=false;
                error=null;
                update();
            }
            
        });        

        
    }

    private void createURLInput( Composite comp ) {
        urlInput=new Text(comp, SWT.BORDER);
        GridData data=new GridData(SWT.FILL, SWT.NONE, true,false);
        data.verticalAlignment=SWT.CENTER;
        urlInput.setLayoutData(data);
        urlInput.setText(memento.getString(URL_ID));
        urlInput.addModifyListener(new ModifyListener(){

            public void modifyText( ModifyEvent e ) {
                list.tested=false;
                error=null;
                update();
            }
            
        });
        
    }

    protected void update() {
        list.layer=layerInput.getText();
        list.url=urlInput.getText();
        page.setErrorMessage(getError());
        createButton.setEnabled(!list.tested);

    }

    private boolean urlValid() {
        if( list.url.length()==0 || !list.url.toUpperCase().startsWith("JDBC.POSTGIS://") || !hasTableName()){ //$NON-NLS-1$
            return false;
        }
        try {
            new URL(null, list.url, CorePlugin.RELAXED_HANDLER);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private boolean hasTableName() {
        String path=list.url.substring(list.url.indexOf("://")+3); //$NON-NLS-1$
        
        return path.split("/").length==3; //$NON-NLS-1$
    }

    private void createLabel( Composite parent, Composite comp, String string ) {
        Label label=new Label(comp,SWT.LEFT);
        label.setFont(parent.getFont());
        GridData data=new GridData(SWT.FILL, SWT.NONE, true,false);
        data.verticalAlignment=SWT.CENTER;
        label.setText(string);
        label.setData(data);
    }

    public void initConfiguration( IIssuesList list, IMemento memento ) {
        if( !(list instanceof StrategizedIssuesList) )
            throw new IllegalArgumentException("List is not an instance of WFSIssuesList!"); //$NON-NLS-1$
        this.issuesList=(StrategizedIssuesList) list;
        this.memento=memento;
        if (memento==null)
            this.memento=DEFAULT_MEMENTO;
        this.list.layer=this.memento.getString(LAYER_ID);
        this.list.url=this.memento.getString(URL_ID);
        issuesList.setStrategy(this.list);
    }

    public boolean isConfigured() {
        if( !list.tested ){
            runTest();
        }
        
        if( list.tested )
            try {
                issuesList.init(list);
            } catch (IOException e) {
               error=Messages.PostgisIssuesListConfigurator_loadingError+"\n"+e.getLocalizedMessage();   //$NON-NLS-1$
               return false;
            }
        return list.tested;
    }

    private void runTest() {
        try {
            if (Display.getCurrent() == null) {
                doTest();
            } else {
                page.runWithProgress(true, new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException {
                        monitor.beginTask(Messages.PostgisIssuesListConfigurator_testTaskName, IProgressMonitor.UNKNOWN); 
                        doTest();
                        monitor.done();
                    }

                });
            }
        } catch (Exception e) {
            ProjectUIPlugin.log("", e); //$NON-NLS-1$
        } 
    }
    
    void doTest() {
        error=null;
        try {
            list.tested=list.testConnection();
            if( !list.tested ){
                error=Messages.PostgisIssuesListConfigurator_connectionError; 
            }
        } catch (IOException e) {
            list.tested=false;
            error=Messages.PostgisIssuesListConfigurator_connectionError2+e.getLocalizedMessage(); 
        }
    }
    public String getError() {
        if ( error!=null )
            return error;
        String message;
        if( !urlValid() ){
            message=Messages.PostgisIssuesListConfigurator_urlValidation; 
        }else if( list.layer.length()==0 ){
            message=Messages.PostgisIssuesListConfigurator_layerValidationMessage; 
        }else if(!list.tested){
            message=Messages.PostgisIssuesListConfigurator_testConfiguration; 
        }else{
            message=null;
        }
        return message;
    }

    public void getConfiguration( IMemento memento ) {
        memento.putString(URL_ID, list.url);
        memento.putString(LAYER_ID, list.layer);
    }

}
