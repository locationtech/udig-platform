package net.refractions.udig.catalog.internal.wmt.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.ww.WWService;
import net.refractions.udig.catalog.internal.wmt.ww.WWServiceExtension;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.wmt.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Based on the WMSWizardPage this WizardPage provides an interface 
 * to load NASA WorldWind configuration files by an URL or
 * from a local file. 
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WWWizardPage extends AbstractUDIGImportPage implements ModifyListener, UDIGConnectionPage {

    private static final String WW_WIZARD = "WW_WIZARD"; //$NON-NLS-1$
    private static final String WW_RECENT = "WW_RECENT"; //$NON-NLS-1$
    private static final String WW_PATH = "WW_PATH"; //$NON-NLS-1$
    private static final int COMBO_HISTORY_LENGTH = 15;

    private IDialogSettings settings;

    private Combo comboUrl;
    private Text txtLocalFile;
    
    private Button btnUrl;
    private Button btnLocalFile;
    private Button btnOpenFileDialog;
    
    private String url = ""; //$NON-NLS-1$
    
    private SelectionAdapter radioSelectionListener = new SelectionAdapter() {
        
        /**
         * Is called when one of the two radio-buttons is selected.
         */
        public void widgetSelected(SelectionEvent event) {
            boolean enableState = btnUrl.getSelection();
            
            // disable every component one by one, so that everyone is grayed out
            comboUrl.setEnabled(enableState);
            
            txtLocalFile.setEnabled(!enableState);
            btnOpenFileDialog.setEnabled(!enableState);
            
            // update state
            modifyText(null);
        }
    };
    

    public WWWizardPage() {
        super(Messages.Wizard_WW_Title); 

        settings = WMTPlugin.getDefault().getDialogSettings().getSection(WW_WIZARD);
        if (settings == null) {
            settings = WMTPlugin.getDefault().getDialogSettings().addNewSection(WW_WIZARD);
        }
    }

    public String getId() {
        return "net.refractions.udig.catalog.ui.WW"; //$NON-NLS-1$
    }
    
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        composite.setLayout(gridLayout);

        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText(Messages.Wizard_WW_Description); 
        GridData gridLabel = new GridData();
        urlLabel.setLayoutData(gridLabel);
        
        //region Enter URL
        btnUrl = new Button(composite, SWT.RADIO);
        btnUrl.addSelectionListener(radioSelectionListener);
        btnUrl.setSelection(true);
        btnUrl.setText(Messages.Wizard_WW_Url);
        
        comboUrl = new Combo(composite, SWT.BORDER);
        comboUrl.setItems(getRecentURLs());
        comboUrl.setVisibleItemCount(COMBO_HISTORY_LENGTH);
        
        GridData gridDataCombo = new GridData(GridData.FILL_HORIZONTAL);
        gridDataCombo.widthHint = 400;
        comboUrl.setLayoutData(gridDataCombo);
        
        // For Drag 'n Drop as well as for general selections
        // look for a url as part of the selection
        Map<String,Serializable> params = defaultParams(); // based on selection
        setDefaultUrl(getURL(params));
        
        comboUrl.addModifyListener(this);
        //endregion
        
        //region Select local file
        btnLocalFile = new Button(composite, SWT.RADIO);
        btnLocalFile.setText(Messages.Wizard_WW_LocalFile);
        
        Composite row = new Composite(composite, SWT.NONE);
        row.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        txtLocalFile = new Text (row, SWT.BORDER);
        txtLocalFile.setLayoutData(new RowData(350, 20));
        
        btnOpenFileDialog = new Button (row, SWT.PUSH);
        btnOpenFileDialog.setText (Messages.Wizard_WW_SelectFile);
        final Shell shell = parent.getShell();
        btnOpenFileDialog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText(Messages.Wizard_WW_SelectFile);
                setLastUsedPath(fd);
                
                // Set filter on XML-files
                String[] filterExtensions = {"*.xml"};  //$NON-NLS-1$
                String[] filterNames = {"NASA WorldWind Configuration File (*.xml)"}; //$NON-NLS-1$
                fd.setFilterExtensions(filterExtensions);                
                fd.setFilterNames(filterNames);
                
                // Open the dialog
                String selectedFile = fd.open();
                
                if (selectedFile != null) {
                    txtLocalFile.setText(selectedFile);                    
                    saveLastUsedPath(fd.getFilterPath());
                        
                    modifyText(null);
                }
            }

            private void saveLastUsedPath(String path) {
                if (settings != null && path != null) {
                    settings.put(WW_PATH, path);
                }
            }

            private void setLastUsedPath(FileDialog fd) {
                if (settings != null) {
                    fd.setFilterPath(settings.get(WW_PATH));
                }
            }
        });
        //endregion
        
        radioSelectionListener.widgetSelected(null);
        
        setControl(composite);
        getWizard().getContainer().updateButtons();
        
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                EndConnectionState currentState = getState();
                Map<IService, Throwable> errors = currentState.getErrors();
                if( errors!=null && !errors.isEmpty()){
                    for (Map.Entry<IService, Throwable> entry : errors.entrySet()) {
                        if( entry.getKey() instanceof WWService ){
                            Throwable value = entry.getValue();
                            String message = Messages.Wizard_WW_ConnectionProblem+value.getLocalizedMessage();
                            setErrorMessage(message);                            
                        }
                    }
                }

            }
        });
    }

    private void setDefaultUrl(URL selectedURL) {
        if (selectedURL != null) {
            comboUrl.setText(selectedURL.toExternalForm());
            url = selectedURL.toExternalForm();
            setPageComplete(true);
        } else if (url != null && url.length() != 0) {
            comboUrl.setText(url);
            setPageComplete(true);
        } else {
            url = null;
            comboUrl.setText("http://"); //$NON-NLS-1$
            setPageComplete(false);
        }
    }

    private String[] getRecentURLs() {
        String[] recentURLs = settings.getArray(WW_RECENT);
        if (recentURLs == null) {
            recentURLs = new String[0];
        }
        return recentURLs;
    }

    /** Can be called during createControl */
    protected Map<String,Serializable> defaultParams(){
        IStructuredSelection selection = (IStructuredSelection) PlatformUI
            .getWorkbench() .getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        Map<String, Serializable> toParams = toParams(selection);
        if( !toParams.isEmpty() ){
            return toParams;
        }
        
        WWConnectionFactory connectionFactory = new WWConnectionFactory();
        Map<String, Serializable> params = connectionFactory.createConnectionParameters( getState().getWorkflow().getContext() );
        if( params !=null )
            return params;
        
        return Collections.emptyMap();
    }
    
    /** Retrieve "best" WMS guess of parameters based on provided context */
    protected Map<String,Serializable> toParams( IStructuredSelection context){
        if(context != null) {
            WWConnectionFactory connectionFactory = new WWConnectionFactory();
            for(Iterator itr = context.iterator(); itr.hasNext();) {
                Map<String,Serializable> params = connectionFactory
                    .createConnectionParameters(itr.next());
                if(!params.isEmpty()) return params;
            }
        }
        return Collections.emptyMap();
    }
    
    @Override
    public void setErrorMessage(String newMessage) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setErrorMessage(newMessage);
        
        setPageComplete(newMessage == null);
        getWizard().getContainer().updateButtons();
    }

    @Override
    public void setMessage(String newMessage) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage);
    }
    
    @Override
    public void setMessage(String newMessage, int messageType) {
        WizardPage page=(WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage, messageType);
    }

    public EndConnectionState getState() {
        return (EndConnectionState) super.getState();
    }
    
    public URL getURL( Map<String,Serializable> params ){
        Object value = params.get(WWService.WW_URL_KEY);
        if( value == null ) return null;
        if( value instanceof URL ) return (URL) value;
        if( value instanceof String) {
            try {
                URL url = new URL( (String) value );   
                return url;
            }
            catch( MalformedURLException erp ){                
            }
        }
        return null;        
    }
    /**
     * Double click in list, or return from url control.
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        e.getClass();// kill warning
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    
    
    /**
     * This should be called using the Wizard .. job when next/finish is pressed.
     * (never called?)
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        URL location = new URL(url);
        
        WWServiceExtension creator = new WWServiceExtension();

        Map<String, Serializable> params = creator.createParams(location);
        IService service = creator.createService(location, params);
        service.getInfo(monitor); // load it

        List<IService> servers = new ArrayList<IService>();
        servers.add(service);

        /*
         * Success! Store the URL in history.
         */
        saveWidgetValues();

        return servers;
    }
    
    /**
     * When "Next" is pressed, check if we can load the file.
     */
    public boolean leavingPage() {  
        Collection<IService> services = getServices();
        
        for (IService service : services) {
            final IService runService = service;
            
            try {
                getContainer().run(false, true, 
                        new IRunnableWithProgress(){

                            public void run(IProgressMonitor monitor) 
                                    throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(Messages.Wizard_WW_Connecting, IProgressMonitor.UNKNOWN);
                
                                // try to load the file
                                try {
                                    runService.members(monitor);
                                } catch (IOException e) {
                                    throw (InvocationTargetException) new InvocationTargetException(e, e.getLocalizedMessage());
                                    
                                }
                                
                                monitor.done();
                            }});
            } catch(Exception exc) {
                // no, this is not going to work, cancel
                setErrorMessage(Messages.WWService_NoValidFile); 
                setPageComplete(false);
                getWizard().getContainer().updateButtons();
                
                return false;
            }
        }
        
        // everything worked fine        
        saveWidgetValues();
        
        return super.leavingPage();
    }
    

    public void modifyText(ModifyEvent e) {
        try {
            getState().getErrors().clear();
            
            if (btnUrl.getSelection()) {
                url = comboUrl.getText().trim();
            } else {
                String path = txtLocalFile.getText().trim();
                File file = new File(path);
                
                url = file.toURI().toURL().toString();
            }
            
            new URL(url);
            setErrorMessage(null);
            setPageComplete(true);
        } catch (MalformedURLException exception) {
            setErrorMessage(Messages.Wizard_WW_Url); 
            setPageComplete(false);
        }
        
        getWizard().getContainer().updateButtons();
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        // Update history
        if (settings != null) {
            String[] recentURLs = getRecentURLs();
            recentURLs = addToHistory(recentURLs, url);
            settings.put(WW_RECENT, recentURLs);
        }
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     * 
     * @param history the current history
     * @param newEntry the entry to add to the history
     * @return the history with the new entry appended Stolen from
     *         org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private String[] addToHistory(String[] history, String newEntry) {
        ArrayList<String> l = new ArrayList<String>(Arrays.asList(history));
        addToHistory(l, newEntry);
        String[] r = new String[l.size()];
        l.toArray(r);
        return r;
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     * 
     * @param history the current history
     * @param newEntry the entry to add to the history Stolen from
     *        org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private void addToHistory(List<String> history, String newEntry) {
        history.remove(newEntry);
        history.add(0, newEntry);

        // since only one new item was added, we can be over the limit
        // by at most one item
        if (history.size() > COMBO_HISTORY_LENGTH)
            history.remove(COMBO_HISTORY_LENGTH);
    }

    public Map<String, Serializable> getParams() {
        try {
            URL location = new URL(url);
            
            WWServiceExtension creator = new WWServiceExtension();
            String errorMessage=creator.reasonForFailure(location);
            if( errorMessage!=null ){
                setErrorMessage(errorMessage);
                return null;
                //return Collections.emptyMap();
            }else
                return creator.createParams(location);
        }
        catch(MalformedURLException e) {
            return null;
        }
    }

    public List<URL> getURLs() {
        try {
            ArrayList<URL> l = new ArrayList<URL>();
            l.add(new URL(url));
            
            return l;
        }
        catch(MalformedURLException e) {
            return null;
        }
    }
    
}

