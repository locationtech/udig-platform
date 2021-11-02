/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal;

import static org.locationtech.udig.issues.internal.PreferenceConstants.KEY_ACTIVE_LIST;
import static org.locationtech.udig.issues.internal.PreferenceConstants.VALUE_MEMORY_LIST;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IIssuesPreferencePage;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.issues.IssuesListConfigurator;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * The Preference Page for configuring what issues list is used.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IIssuesPreferencePage {

    static final String PREFERENCE_ID = KEY_ACTIVE_LIST;
    static final String MEMORY = VALUE_MEMORY_LIST;

    private Combo combo;
    private Composite configComposite;
    private List<IConfigurationElement> extensionMapping;
    private List<String> names;
    private ListData issuesList;
    private Map<Integer, ListData> lists=new HashMap<>();
    private Composite progressArea;

    public IssuesPreferencePage() {
        super(Messages.IssuesPreferencePage_pageTitle);
        setDescription(Messages.IssuesPreferencePage_pageDesc);
        setPreferenceStore(IssuesActivator.getDefault().getPreferenceStore());

        //Ping the issues manager to make sure it is initialized
        IIssuesManager.defaultInstance.getIssuesList();
    }

    @Override
    protected Control createContents( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        createLabel(parent, composite);
        createCombo(composite);
        createConfigArea(composite);
        createProgressArea(composite);
        createList();
        return composite;
    }

    private void createProgressArea( Composite composite ) {
        progressArea=new Composite(composite, SWT.NONE);
        progressArea.setLayout(new FillLayout());
        GridData data=new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan=2;
        data.heightHint=50;
        progressArea.setLayoutData(data);

    }

    private void createConfigArea( Composite c ) {
        configComposite=new Composite(c, SWT.BORDER);
        GridData data=new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan=2;
        configComposite.setLayoutData(data);
        configComposite.setLayout(new FillLayout());
    }

    private void createCombo( Composite c ) {
        combo=new Combo(c, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        combo.setItems(getListNames());
        combo.select(getCurrentIndex());
        combo.addSelectionListener(new SelectionListener(){

            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                createList();
            }

        });
    }


    void createList() {
        setErrorMessage(null);
        setMessage(getTitle());
        int selectionIndex = combo.getSelectionIndex();
        IConfigurationElement elem=extensionMapping.get(selectionIndex);
        ListData data=new ListData(selectionIndex);
        if( lists.containsKey(selectionIndex )){
            data=lists.get(selectionIndex);
        }else{
            lists.put(selectionIndex, data);
        }

        if( data==issuesList )
            return;

        try {
            storeCurrentConfiguration();
        } catch (Exception e2) {
            LoggingSupport.log(IssuesActivator.getDefault(), e2); //$NON-NLS-1$
        }

        try {
            createList(elem, data);
            if( data.configurator!=ERROR_CONFIG)
                createConfiguration(elem,data);
            initializeConfiguration(data);

            issuesList=data;
        } catch (Exception e1) {
            data.configurator=ERROR_CONFIG;
            setErrorMessage(Messages.IssuesPreferencePage_ListCreationError);
            LoggingSupport.log(IssuesActivator.getDefault(),e1); //$NON-NLS-1$
        }
    }

    private void initializeConfiguration(ListData data) {
        if( issuesList!=null && issuesList.configurationWidget!=null ){
            issuesList.configurationWidget.dispose();
            issuesList.configurationWidget=null;
        }
        if ( data.configurator==null || data.configurator==ERROR_CONFIG ){
            return;
        }
        data.configurator.initConfiguration(data.list, createMemento(data));
        data.configurationWidget=data.configurator.getControl(configComposite, IssuesPreferencePage.this);
        configComposite.layout();
    }

    private void createList( IConfigurationElement elem, ListData data ) throws CoreException {
        if( data.configurator==null ){
            Object obj = elem.createExecutableExtension("class"); //$NON-NLS-1$
            if( obj==null || obj instanceof IIssuesList ){
                data.list=(IIssuesList)obj;
            }else{
                data.configurator=ERROR_CONFIG;
            }
        }
        if( data.configurator==ERROR_CONFIG )
            setErrorMessage(Messages.IssuesPreferencePage_ListCreationError);
    }

    private void createConfiguration( IConfigurationElement elem, ListData data ) throws CoreException {
        if( data.configurator!=null )
            return;

        if( elem.getAttribute("configurator")==null ){ //$NON-NLS-1$
            data.configurator=null;
            return;
        }
        Object obj = elem.createExecutableExtension("configurator"); //$NON-NLS-1$
        if( obj==null || obj instanceof IssuesListConfigurator ){
            data.configurator=(IssuesListConfigurator)obj;
        }else{
            data.configurator=ERROR_CONFIG;
            setErrorMessage("An error occurred while creating the configuration panel for this issues list.  Please choose another."); //$NON-NLS-1$
        }
    }

    protected IMemento createMemento( ListData data ) {
        try {
            String id = getId(data.index);
            String memento=getPreferenceStore().getString(PREFERENCE_ID+"/"+id); //$NON-NLS-1$
            return XMLMemento.createReadRoot(new StringReader(memento));
        } catch (WorkbenchException e) {
            return null;
        }
    }



    protected void storeCurrentConfiguration() throws IOException {
        if( issuesList==null || issuesList.configurator==null || issuesList.configurator==ERROR_CONFIG)
            return;
        String id = getId(issuesList.index);
        String configuration=getXMLConfiguration();
        getPreferenceStore().setValue(PREFERENCE_ID+"/"+id, configuration); //$NON-NLS-1$
    }

    private String getId(int index) {
        IConfigurationElement configurationElement = getExtensionList().get(index);
        String id = configurationElement.getNamespaceIdentifier()+"."+configurationElement.getAttribute("id"); //$NON-NLS-1$ //$NON-NLS-2$
        return id;
    }

    private void createLabel( Composite parent, Composite c ) {
        Label label=new Label(c, SWT.LEFT);
        label.setFont(parent.getFont());
        GridData gridData = new GridData(SWT.NONE, SWT.NONE, false, false);
        gridData.verticalAlignment=SWT.CENTER;
        label.setLayoutData(gridData);
        label.setText(Messages.IssuesPreferencePage_currentLabelText);
    }

    private int getCurrentIndex() {
        String id=getPreferenceStore().getString(PREFERENCE_ID);
        return indexOf(id);
    }

    private int indexOf( String id ) {
        List<IConfigurationElement> list = getExtensionList();
        IConfigurationElement elem;
        for ( int i=0; i<list.size();i++ ){
            elem=list.get(i);
            if( id.equals(elem.getAttribute("id"))){ //$NON-NLS-1$
                return i;
            }
        }
        return 0;
    }

    private String[] getListNames() {
        return getNamesMap().toArray(new String[0]);
    }

    private List<String> getNamesMap() {
        if (names == null) {
            processExtensionPoint();
        }

        return names;
    }

    private List<IConfigurationElement> getExtensionList() {
        if (extensionMapping == null) {
            processExtensionPoint();
        }

        return extensionMapping;
    }

    private void processExtensionPoint() {
        names=new ArrayList<>();
        extensionMapping=ExtensionPointList.getExtensionPointList(IssueConstants.ISSUES_LIST_EXTENSION_ID);
        for( IConfigurationElement element : extensionMapping ) {
            names.add(element.getAttribute("name")); //$NON-NLS-1$
        }
    }

    @Override
    protected void performDefaults() {
        combo.select(indexOf(getPreferenceStore().getDefaultString(PREFERENCE_ID)));
        createList();
    }

    @Override
    public boolean performOk() {
        if( issuesList.configurator==ERROR_CONFIG )
            return false;
        if( issuesList.configurator!=null ){
            try {
                final boolean[] configured=new boolean[1];
                final int selectionIndex = combo.getSelectionIndex();
                runWithProgress(true, new IRunnableWithProgress(){

                    @Override
                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(Messages.IssuesPreferencePage_TestTaskName, IProgressMonitor.UNKNOWN);
                        if ( issuesList.configurator.isConfigured() ){
                            storeListIDInPrefs(selectionIndex);
                            try {
                                storeCurrentConfiguration();
                            } catch (IOException e) {
                                LoggingSupport.log(IssuesActivator.getDefault(), e);
                                throw new InvocationTargetException(e,Messages.IssuesPreferencePage_StorageError);
                            }
                            configured[0]=true;
                        } else {
                            configured[0]=false;
                        }
                        monitor.done();
                    }

                });
                if( !configured[0] ){
                    setErrorMessage(issuesList.configurator.getError());

                    getPreferenceStore().setValue(PREFERENCE_ID,
                            getPreferenceStore().getDefaultString(PREFERENCE_ID));
                }else{
                    setErrorMessage(""); //$NON-NLS-1$
                    setList();
                }

                return configured[0];
            } catch (Exception e) {
                setErrorMessage(Messages.IssuesPreferencePage_testError+e.getLocalizedMessage());
                return false;
            }
        }

        storeListIDInPrefs(combo.getSelectionIndex());

        setList();
        return super.performOk();
    }


    private void storeListIDInPrefs( final int selectionIndex ) {
//        String id = getExtensionList().get(selectionIndex).getAttribute("id"); //$NON-NLS-1$
        IConfigurationElement element=extensionMapping.get(selectionIndex);

        String string = element.getNamespaceIdentifier() + "." + element.getAttribute("id");//$NON-NLS-1$//$NON-NLS-2$
        getPreferenceStore().setValue(PREFERENCE_ID, string);
    }

    private void setList() {
        IIssuesList list = issuesList.list;
        if( list!=null )
            IIssuesManager.defaultInstance.setIssuesList(list);
        else
            IIssuesManager.defaultInstance.setIssuesList(IssuesManager.createListFromPreferences());
    }

    protected String getXMLConfiguration() throws IOException {
        if( issuesList==null || issuesList.configurator==null || issuesList.configurator==ERROR_CONFIG )
            return null;

        XMLMemento memento=XMLMemento.createWriteRoot("configuration"); //$NON-NLS-1$
        issuesList.configurator.getConfiguration(memento);
        StringWriter stringWriter=new StringWriter();
        memento.save(stringWriter);
        String configuration = stringWriter.toString();
        stringWriter.close();
        return configuration;
    }

    @Override
    public void runWithProgress( boolean mayBlock, final IRunnableWithProgress runnable )
            throws InvocationTargetException, InterruptedException {
        if (Display.getCurrent() == null)
            throw new SWTException(SWT.ERROR_THREAD_INVALID_ACCESS);
        final ProgressMonitorPart part = new ProgressMonitorPart(progressArea, new GridLayout(
                1, true), 10);
        try {
            progressArea.layout();
            PlatformGIS.runBlockingOperation(runnable, part);
        } finally {
            part.dispose();
            progressArea.layout();
        }

    }

    @Override
    public void init( IWorkbench workbench ) {
    }


    protected static final IssuesListConfigurator ERROR_CONFIG = new IssuesListConfigurator(){

        @Override
        public void getConfiguration( IMemento memento ) {
        }

        @Override
        public Control getControl( Composite parent, IIssuesPreferencePage page ) {
            return null;
        }

        @Override
        public String getError() {
            return null;
        }

        @Override
        public void initConfiguration( IIssuesList list, IMemento memento ) {
        }

        @Override
        public boolean isConfigured() {
            return false;
        }
    };

    private static class ListData{
        IIssuesList list;
        IssuesListConfigurator configurator;
        Control configurationWidget;
        final int index;
        ListData(int index){ this.index=index; }
    }

}
