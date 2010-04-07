package net.refractions.udig.catalog.internal.ui;

import java.io.Serializable;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.IntermediateState;
import net.refractions.udig.catalog.ui.workflow.Listener;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardDialog;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataSourceException;

public class ConnectionPageDecorator extends WorkflowWizardPage
        implements
            UDIGConnectionPage,
            Listener {

    /** underlying import page * */
    UDIGConnectionPage page;

    public ConnectionPageDecorator() {
        super("connection"); // set name later //$NON-NLS-1$
    }

    public Map<String, Serializable> getParams() {
        return page.getParams();
    }

    @Override
    public void setState( State state ) {
        super.setState(state);
        

        UDIGConnectionPage tmp;

        // create the specific connection page
        if (state instanceof IntermediateState) {
            IntermediateState intermediateState = (IntermediateState) state;
            EndConnectionState endConnectionState = (EndConnectionState) intermediateState
                    .getEndState();

            tmp = endConnectionState.getConnectionFactory().createConnectionPage(
                    intermediateState.getIndex());
        } else {
            EndConnectionState endConnectionState = (EndConnectionState) state;
            tmp = (endConnectionState).getConnectionFactory().createConnectionPage(
                    endConnectionState.getDescriptor().getWizardPageCount() - 1);
        }
        
        this.page = tmp;
        
        // we do the instance check to allow the connection page to
        // optionally extend DataPipelinePage.
        if (tmp instanceof WorkflowWizardPage) {
            ((WorkflowWizardPage) tmp).setState(state);
        }
        
        if( tmp.getWizard()!=getWizard() ){
            tmp.setWizard(getWizard());
        }

        // add a listener to the workflow to determine when the workflow
        // moves back, when this happens, we need to forget about the
        // decorated page
        state.getWorkflow().addListener(this);

    }

    @Override
    public void shown() {
        super.shown();

        if (page instanceof WorkflowWizardPage) {
            ((WorkflowWizardPage) page).shown();
        }

    }

    public void createControl( Composite parent ) { 
        page.createControl(parent);
        
        setControl(page.getControl());
    }

    @Override
    public void dispose() {
        page.dispose();
        setControl(null);
    }

      @Override
    public boolean isPageComplete() {
          boolean complete = page.isPageComplete();
          if (complete && getState() instanceof EndConnectionState) {
              // set some context for the connection state
              EndConnectionState state = (EndConnectionState) getState();
              state.setServices(page.getServices());
              if (page instanceof AbstractUDIGImportPage) {
                  AbstractUDIGImportPage importPage = (AbstractUDIGImportPage) page;
                  state.setSelectedResources(importPage.getResourceIDs());
              }
          }
        return complete;
    }
      
     @Override
    public boolean leavingPage() {
         if (getState() instanceof EndConnectionState) {
             // set some context for the connection state
             EndConnectionState state = (EndConnectionState) getState();
             Collection<IService> services = page.getServices();
			 state.setServices(services);
			 
             if (page instanceof AbstractUDIGImportPage) {
                 AbstractUDIGImportPage importPage = (AbstractUDIGImportPage) page;
                 Collection<URL> resourceIDs = importPage.getResourceIDs();
                 
				 state.setSelectedResources(resourceIDs);
             }
         }
         
         if (page instanceof AbstractUDIGImportPage) {
             AbstractUDIGImportPage importPage = (AbstractUDIGImportPage) page;
             if (!importPage.leavingPage()) {
                 return false;
             }
         }
         
         return true;
    }

    @Override
    public String getName() {
        return page.getName();
    }

    @Override
    public Image getImage() {
        return page.getImage();
    }

    @Override
    public String getDescription() {
        return page.getDescription();
    }

    @Override
    public String getTitle() {
        return page.getTitle();
    }

    @Override
    public String getMessage() {
        return page.getMessage();
    }

    @Override
    public void setWizard( IWizard newWizard ) {
        super.setWizard(newWizard);
    }

    public void forward( State current, State prev ) {
    }

    public void backward( State current, State next ) {
    }

    public void statePassed( State state ) {
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                setErrorMessage(null);
            }
        });
    }

    public void stateFailed( State state ) {
        if (state instanceof EndConnectionState) {
            EndConnectionState connectionState = (EndConnectionState) state;
            Map<IService, Throwable> errors = connectionState.getErrors();
            Iterator<Entry<IService, Throwable>> iterator = errors.entrySet().iterator();
            if (iterator.hasNext()) {
                Entry<IService, Throwable> entry = iterator.next();
                Throwable t = entry.getValue();
                final String message = formatException(entry.getKey(), t);
                if (Display.getCurrent() == null) {
                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                            page.setErrorMessage(message);
                        }
                    });
                } else {
                    page.setErrorMessage(message);
                }
            }
        }
    }

    private String formatException( IService key, Throwable t ) {
        if (t instanceof UnknownHostException) {
            return key.getIdentifier().getHost() + Messages.ConnectionPage_illegalHost;
        }
        if (t instanceof DataSourceException) {
            String message = t.getMessage();
            if (message.contains("user") && message.contains("does not exist")) //$NON-NLS-1$ //$NON-NLS-2$
                return Messages.ConnectionPage_badUsername;
            if (message.contains("password")) //$NON-NLS-1$
                return Messages.ConnectionPage_badPassword;
        }
        return Messages.ConnectionPage_genericError;
    }

    public void started( State first ) {

    }

    public void finished( State last ) {
        if (!(getContainer() instanceof WorkflowWizardDialog) && getContainer() != null) {
            getWizard().performFinish();
        }
    }

    /**
     * 52¡North changed Method returns the successor wizard page. If there is no successor wizard
     * page, null is returned.
     */
    @Override
    public IWizardPage getNextPage() {
        if (!getState().hasNext()) {
            return super.getNextPage();
        }
        return page.getNextPage();
    }

    @Override
    public Control getControl() {
        return super.getControl();
    }
        
    public void setPreviousPage( IWizardPage page ) {
        super.setPreviousPage(page);
        IWizardPage previousPage = page;
        if (page instanceof ConnectionPageDecorator) {
            previousPage = ((ConnectionPageDecorator) page).page;
        }
        this.page.setPreviousPage(previousPage);
    }

    public Collection<URL> getResourceIDs() {
        return page.getResourceIDs();
    }

    public Collection<IService> getServices() {
        return page.getServices();
    }
    
    @Override
    public State getState() {
        return super.getState();
    }

}
