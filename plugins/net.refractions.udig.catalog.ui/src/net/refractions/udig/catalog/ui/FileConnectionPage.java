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
package net.refractions.udig.catalog.ui;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard page that opens a file dialog and closes the wizard when dialog is closed.
 * 
 * @author jeichar
 * @since 0.9.0
 * @version 1.2.0
 */
public class FileConnectionPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    private final Set<URL> list = new HashSet<URL>();
    private Composite comp;

    private FileConnectionFactory factory = new FileConnectionFactory();
    private FileDialog fileDialog;
    private Collection<URL> resourceIds = new HashSet<URL>();
    private ListViewer viewer;

    public String getId() {
        return "net.refractions.udig.catalog.ui.openFilePage"; //$NON-NLS-1$
    }

    /**
     * Construct <code>OpenFilePage</code>.
     */
    public FileConnectionPage() {
        super(Messages.OpenFilePage_pageTitle);
    }

    /**
     * Process a list of URLs resulting in candidate IService instances
     * that may need to be added to the catalog.
     *
     * @param urls
     * @param monitor
     * @return Candidate IServices
     */
    List<IService> process( List<URL> urls, IProgressMonitor monitor ) {
        List<IService> resources = new ArrayList<IService>();
        monitor.beginTask(Messages.OpenFilePage_1, list.size());
        int worked = 0;
        for( URL url : urls ) {
            if (monitor.isCanceled())
                return null;
            try {
                monitor.subTask(url.toExternalForm());
                List<IService> acquire = CatalogPlugin.getDefault().getServiceFactory()
                        .createService(url);
                resources.addAll(acquire);
            } catch (Throwable e) {
                CatalogUIPlugin.log("error obtaining services from service factory", e); //$NON-NLS-1$
            }
            monitor.worked(worked++);
        }
        return resources;
    }

    private void pushButton( final int buttonId ) {
        try {

            findButton(getShell().getChildren(), buttonId).notifyListeners(SWT.Selection,
                    new Event());
        } catch (Exception e) {
            CatalogUIPlugin.log("", e); //$NON-NLS-1$
        }
    }

    Button findButton( Control[] children, int id ) {
        if (((Integer) getShell().getDefaultButton().getData()).intValue() == id)
            return getShell().getDefaultButton();

        for( Control child : children ) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.getData() != null && ((Integer) button.getData()).intValue() == id)
                    return button;
            }
            if (child instanceof Composite) {
                Composite composite = (Composite) child;
                Button button = findButton(composite.getChildren(), id);
                if (button != null)
                    return button;
            }
        }
        return null;
    }
    
    /**
     * Check if only one resource is available; in which case we can skip asking.
     *
     * @param monitor
     * @param services
     * @return
     * @throws IOException
     */
    protected boolean hasOneResource( SubProgressMonitor monitor, List<IService> services )
            throws IOException {
        if (services.size() > 1 || services.isEmpty())
            return false;

        if (services.get(0).resources(monitor).size() == 1)
            return true;
        return false;
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage() {
        return (list != null && list.size() > 1);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1,true));

        Label label = new Label(comp,SWT.NONE);
        GridDataFactory.swtDefaults().applyTo(label);
        label.setText(Messages.FileConnectionPage_waitMessage);
        
        viewer = new ListViewer(comp,SWT.READ_ONLY| SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());
        
        setControl(comp);
    }

    @Override
    public void shown() {
        Runnable openFileDialog = new Runnable(){
            public void run() {
                selectAndContinueWizard();
            }
        };
        // file dialog must be opened asynchronously so that the workflow can finish the
        // next action. Otherwise we will deadlock
        PlatformGIS.asyncInDisplayThread(openFileDialog, false);
    }

    private void selectAndContinueWizard() {
        boolean okPressed;
        list.clear();
        okPressed = openFileDialog(comp);
        viewer.setInput(list);
        getContainer().updateButtons();
        
        /*
         * XXX I'm not liking this. I think the workflow should be used to drive the pages because
         * by trying to put the buttons it is dependent the implementation of
         * ConnectionPageDecorator's isPageComplete method as well as what order the
         * WorkflowWizard's canFinish method is implemented. IE if canFinish does not call
         * isPageComplete before calling dryRun() the finish button will not be activated.
         */
        if (okPressed) {
            if (findButton(getShell().getChildren(), IDialogConstants.FINISH_ID).isEnabled()) {
                pushButton(IDialogConstants.FINISH_ID);
            } else {
                pushButton(IDialogConstants.NEXT_ID);
            }
        } else {
            pushButton(IDialogConstants.BACK_ID);
        }

    }
    private boolean checkDND( FileDialog fileDialog ) {
        try {

            Object context = getState().getWorkflow().getContext();

            // IStructuredSelection selection = ((IDataWizard) getWizard()).getSelection();

            Set<URL> urlList = new HashSet<URL>();

            URL url = factory.createConnectionURL(context);
            if (url != null) {
                urlList.add(url);
            }

            if (urlList.size() != 0) {
                list.addAll(urlList);
                String file = urlList.iterator().next().getFile();
                String ext = file.substring(file.lastIndexOf('.'));
                String dir = new File(file).getParent();

                file = file.substring(file.lastIndexOf(File.separator) + 1);
                fileDialog.setFilterPath(dir);
                fileDialog.setFileName(file);

                String[] filters = fileDialog.getFilterExtensions();
                if (filters == null || filters.length == 0) {
                    // no filters set, set em up
                    fileDialog.setFilterExtensions(new String[]{"*" + ext, "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$	
                } else {
                    // we have some filters, look for the one in question
                    // in the list
                    int i = 0;
                    for( ; i < filters.length; i++ ) {
                        if (("*" + ext).equals(filters[i]))break; //$NON-NLS-1$
                    }

                    if (i < filters.length) {
                        // we found it, reorganize the array so that
                        // it is first
                        String[] nfilters = new String[filters.length];
                        nfilters[0] = filters[i];
                        System.arraycopy(filters, 0, nfilters, 1, i);
                        System.arraycopy(filters, i + 1, nfilters, i + 1, filters.length - i - 1);
                        fileDialog.setFilterExtensions(nfilters);
                    } else {
                        // no dice, add the filter
                        String[] nfilters = new String[filters.length + 1];
                        nfilters[0] = "*" + ext; //$NON-NLS-1$
                        System.arraycopy(filters, 0, nfilters, 1, filters.length);
                        fileDialog.setFilterExtensions(nfilters);
                    }
                }

                return true;
            }
        } catch (Exception e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        }

        return false;

    }

    public FileDialog getFileDialog() {
        return fileDialog;
    }

    private boolean openFileDialog( Composite parent ) {
        String lastOpenedDirectory = PlatformUI.getPreferenceStore().getString(
                CatalogUIPlugin.PREF_OPEN_DIALOG_DIRECTORY);
        fileDialog = new FileDialog(parent.getShell(), SWT.MULTI | SWT.OPEN);

        List<String> fileTypes = factory.getExtensionList();

        StringBuffer all = new StringBuffer();
        for( Iterator<String> i = fileTypes.iterator(); i.hasNext(); ) {
            all.append(i.next());
            if (i.hasNext())
                all.append(";"); //$NON-NLS-1$ //semicolon is magic in eclipse FileDialog
        }
        fileTypes.add(0, all.toString());

        fileTypes.add("*.*"); //$NON-NLS-1$

        fileDialog.setFilterExtensions(fileTypes.toArray(new String[fileTypes.size()]));

        if (lastOpenedDirectory != null && !checkDND(fileDialog)) {
            fileDialog.setFilterPath(lastOpenedDirectory);
        }

        // //this is a HACK to check for headless execution
        // if (getContainer() instanceof HeadlessWizardDialog) {
        // if (dnd)
        // return true; //there was a workbench selection that allowed us select a file
        // }
        //        
        String result = fileDialog.open();
        if (result == null) {
            return false;
        }
        String path = fileDialog.getFilterPath();
        PlatformUI.getPreferenceStore().setValue(CatalogUIPlugin.PREF_OPEN_DIALOG_DIRECTORY, path);
        String[] filenames = fileDialog.getFileNames();
        for( int i = 0; i < filenames.length; i++ ) {
            try {
                //URL url = new File(path + System.getProperty("file.separator") + filenames[i]).toURL(); //$NON-NLS-1$
                URL url = new File(path + System.getProperty("file.separator") + filenames[i]).toURI().toURL(); //$NON-NLS-1$
                list.add(url);
            } catch (Throwable e) {
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
        }
        return true;
    }

    @Override
    public Collection<IService> getServices() {
        resourceIds.clear();

        final ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        final Collection<IService> services = new ArrayList<IService>();

        IRunnableWithProgress runnable = new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {

                List<IService> availableServices = null;

                if (!list.isEmpty()) {
                    for( Iterator<URL> URLIterator = list.iterator(); URLIterator.hasNext(); ) {
                        URL url = URLIterator.next();

                        try {
                            availableServices = catalog.constructServices(url, monitor);
                            if (!availableServices.isEmpty()) {
                                IService service = availableServices.iterator().next();
                                resourceIds.add(service.getIdentifier());
                                services.add(service);// add the first service
                            }
                        } catch (IOException e) {
                            throw (RuntimeException) new RuntimeException().initCause(e);
                        } finally {
                            List<IService> members = catalog.checkMembers(availableServices);

                            for( Iterator<IService> iterator = members.iterator(); iterator
                                    .hasNext(); ) {
                                IService service = iterator.next();

                                if (service.equals(service)) continue;

                                service.dispose(new SubProgressMonitor(monitor, 10));
                            }
                            monitor.done();
                        }

                    }
                }
            }

        };

        try {
            getContainer().run(false, true, runnable);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }

        return services;
    }

    @Override
    public Collection<URL> getResourceIDs() {
        return resourceIds;
    }

}
