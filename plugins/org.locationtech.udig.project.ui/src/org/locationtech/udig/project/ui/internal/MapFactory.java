/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.internal.ui.ResourceSelectionPage;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.UDIGEditorInput;
import org.locationtech.udig.ui.ExceptionDisplayer;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * A Factory for creating maps from different types of resources.
 * 
 * @author jeichar
 * @since 0.9.0
 * 
 * @deprecated use {@link ApplicationGIS}
 */
public class MapFactory {

    /**
     * The maximum number of resources per service that can be added to a map without asking the
     * user for permission. TODO expose this as a preference
     */
    public static final int MAX_RESOURCES_IN_SERVICE = 1;

    private static final MapFactory INSTANCE = new MapFactory();

    private MapFactory() {
        // do nothing
    }

    /**
     * For each URL in <code>resources</code>, load the services at that location and access
     * their layers. Then add the layers to the current map, or a new one if it doesn't exist. The
     * new map will be created in the current project.
     * <p>
     * This is equivalent to calling <code>processURLs(resources, null)</code>.
     * </p>
     * 
     * @param resources a List of URLs pointing to services (WMS, Shapefile, etc)
     * @deprecated use {@link ApplicationGIS#createAndOpenMap(List)}
     */
    public void processURLs( List<URL> resources ) {
        processURLs(resources, null);
    }

    /**
     * For each URL in <code>resources</code>, load the services at that location and access
     * their layers.
     * <p>
     * The layers will then be added to the current map, if it exists, otherwise, a new map will be
     * created in the project designated by <code>target</code>.
     * </p>
     * <p>
     * This is equivalent to calling <code>processURLs(resources, target, false)</code>.
     * </p>
     * 
     * @param resources a List of URLs pointing to services (WMS, Shapefile, etc)
     * @param target Project to use if a new map is going to be created
     * @deprecated use {@link ApplicationGIS#createAndOpenMap(List, org.locationtech.udig.project.IProject)}
     */
    public void processURLs( List<URL> resources, Project target ) {
        processURLs(resources, target, false);
    }

    /**
     * For each URL in <code>resources</code>, load the services at that location and access
     * their layers.
     * <p>
     * If <code>newMap</code> is set to <code>true</code> or there is no current map, the layers
     * will be added to a new map, contained in the project designated by <code>target</code>, or
     * the current project if <code>target</code> is null.
     * </p>
     * 
     * @param resources a List of URLs pointing to services (WMS, Shapefile, etc)
     * @param target Project to use if a new map is going to be created
     * @param newMap if true, a new map will be created even if there is one already open
     * @deprecated use {@link ApplicationGIS#addLayersToMap(org.locationtech.udig.project.IProject, List)} or {@link ApplicationGIS#createAndOpenMap(List, org.locationtech.udig.project.IProject)}
     * 
     */
    public void processURLs( List<URL> resources, Project target, boolean newMap ) {
        process(target, resources, newMap);
    }

    /**
     * For each IResolve in <code>resources</code>, load the services at that location and access
     * their layers.
     * <p>
     * If <code>newMap</code> is set to <code>true</code> or there is no current map, the layers
     * will be added to a new map, contained in the project designated by <code>target</code>, or
     * the current project if <code>target</code> is null.
     * </p>
     * 
     * @param resources a List of IResolves to load onto a map
     * @param target Project to use if a new map is going to be created
     * @param newMap if true, a new map will be created even if there is one already open
     */
    public void processResolves( List<IResolve> resources, Project target, boolean newMap ) {
        process(target, resources, newMap);
    }

    /**
     * This method will perform all of its work in a Job and will not block.
     * <p>
     * TODO fix this Process a list of X, try and make
     * 
     * @param resources a List of IResolves
     * @param newMap forces a new map to be created
     */
    public void process( final Project target, final List resources, final boolean newMap ) {
        if (resources == null) {
            throw new InvalidParameterException("Parameter 'resources' cannot be null."); //$NON-NLS-1$
        }

        Job job = new Job(Messages.ProjectUIPlugin_loadingServices_title){ 
            @SuppressWarnings("unchecked") 
            protected IStatus run( final IProgressMonitor monitor ) {

                List<Throwable> exceptions = new ArrayList<Throwable>();

                List<IResolve> services = new ArrayList<IResolve>();
                List<IGeoResource> geoResources = new ArrayList<IGeoResource>();
                List<Layer> layers = new ArrayList<Layer>();

                for( Object object : resources ) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                    monitor.beginTask(Messages.MapFactory_taskSorting, resources.size()); 
                    try {
                        if (object instanceof URL) {
                            services.addAll(handleURL((URL) object, monitor));
                        } else if (object instanceof File) {
                            try {
                                services.addAll(handleURL(((File) object).toURL(), monitor));
                            } catch (MalformedURLException e) {
                                // ignore non-URL strings
                            }
                        } else if (object instanceof String) {
                            try {
                                services.addAll(handleURL(new URL((String) object), monitor));
                            } catch (MalformedURLException e) {
                                // ignore non-URL strings
                            }
                        } else if (object instanceof IService) {
                            services.add((IService) object);
                        } else if (object instanceof IGeoResource) {
                            geoResources.add((IGeoResource) object);
                        } else if (object instanceof Layer) {
                            layers.add((Layer) object);
                        } else if (object instanceof java.util.Map) {
                            services.addAll(CatalogPlugin.getDefault().getServiceFactory().createService(
                                    (java.util.Map) object));
                        }
                    } catch (IOException e) {
                        exceptions.add(e);
                        ProjectUIPlugin.log(null, e);
                    }

                    monitor.worked(1);
                }

                List<IResolve> unChosenServices = new ArrayList<IResolve>();

                for( IResolve resolve : services ) {
                    monitor.beginTask(
                            Messages.ProjectUIPlugin_loadingServices_task, services.size()); 
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                    try {
                        List<IGeoResource> resources = handleResolve(resolve, monitor);

                        if (resources.size() > MAX_RESOURCES_IN_SERVICE) {
                            unChosenServices.add(resolve);
                        } else {
                            geoResources.addAll(resources);
                        }
                    } catch (IOException e) {
                        exceptions.add(e);
                        ProjectUIPlugin.log(null, e);
                    }
                    monitor.worked(1);
                }

                if (unChosenServices.size() > 0) {
                    geoResources.addAll(getResourcesFromUser(unChosenServices));
                }

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                Map map = new CurrentMapFinder().getCurrentMap();
                boolean mapExists = (map != null); // this is used later, and I don't understand
                                                    // what for!
                if (map == null || newMap) {
                    map = getMap(monitor, target, newMap);
                }
                
                if( map.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR)==null ){
                    IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
                    RGB background = PreferenceConverter.getColor(store, PreferenceConstants.P_BACKGROUND); 
                    map.getBlackboard().put(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR, new Color(background.red, background.green, background.blue ));
                }

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                LayerFactory factory = map.getLayerFactory();
                if (factory == null) {
                    factory = ProjectFactory.eINSTANCE.createLayerFactory();
                }

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                for( IGeoResource resource : geoResources ) {
                    monitor.beginTask(Messages.MapFactory_retrieveTask, geoResources.size()); 
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    try {
                        IService service = resource.service(monitor);
                        CatalogPlugin.getDefault().getLocalCatalog().add(service);

                        Layer layer = factory.createLayer(resource);
                        if (layer != null) {
                            layers.add(layer);
                        }
                    } catch (IOException e) {
                        exceptions.add(e);
                        ProjectUIPlugin.log(null, e);
                    }
                    monitor.worked(1);
                }
                if( !layers.isEmpty() )
                map.getLayersInternal().addAll(layers);
                if (map.getLayersInternal().size() > 0 || newMap == true) {
                    ProjectExplorer.getProjectExplorer().open(map);
                } else if (!mapExists) {
                    // this is very ambigious? what is going on here?
                    map.getProjectInternal().getElementsInternal().remove(map);
                }

                if (exceptions.size() != 0) {
                    String message = null;
                    if (exceptions.size() > 1) {
                        message = Messages.MapFactory_multiError; 
                    } else {
                        message = Messages.MapFactory_error; 
                    }

                    ExceptionDisplayer.displayExceptions(exceptions, message, ProjectUIPlugin.ID);
                }
                monitor.done();

                return new Status(IStatus.OK, ProjectUIPlugin.ID, IStatus.OK, 
                		Messages.ProjectUIPlugin_success, null);
            }
        };
        job.schedule();
    }

    private List<IGeoResource> getResourcesFromUser( List<IResolve> unChosenServices ) {
        final ResourceSelectionPage page = new ResourceSelectionPage(
        		Messages.ProjectUIPlugin_resourceSelectionPage_title);
        page.setResources(unChosenServices, null);
        final List<IGeoResource> chosenResources = new ArrayList<IGeoResource>();

        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                Wizard wizard = new Wizard(){
                    public void addPages() {
                        addPage(page);
                    }

                    @Override
                    public boolean performFinish() {
                        List<Object> list = page.getCheckedElements();
                        for( Object object : list ) {
                            if (object instanceof IGeoResource)
                                chosenResources.add((IGeoResource) object);
                        }
                        return true;
                    }

                };
                WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(),
                        wizard);
                dialog.setBlockOnOpen(true);
                dialog.open();
            }
        });

        return chosenResources;
    }

    private List<IGeoResource> handleResolve( IResolve resolve, IProgressMonitor monitor )
            throws IOException {
        if( resolve instanceof IService ){
            IService service = (IService) resolve;
            resources(service.resources(monitor));
        }
        return Collections.emptyList();
    }

    private List<IService> handleURL( URL url, IProgressMonitor monitor ) throws IOException {
        if (url.getFile().toLowerCase().endsWith(".udig")) { //$NON-NLS-1$
            handleProjectURL(url, monitor);
            return Collections.<IService>emptyList();
        }

        // Process URL (we are expecting a IService)
        List<IService> goodServices = acquireGoodServices(url, monitor);
        if (goodServices.isEmpty()) {
            throw new IOException("No service available for " + url); //$NON-NLS-1$
        } else {
            return goodServices;
        }
    }

    private void handleProjectURL( URL url, IProgressMonitor monitor ) {
        monitor = validateMonitor(monitor);
        monitor.subTask(Messages.ProjectUIPlugin_loadingProject_task); 
        File file = URLUtils.urlToFile(url);
        
        ProjectPlugin.getPlugin().getProjectRegistry().getProject( file.getAbsolutePath() );
        return;
    }

    /**
     * Same as processResources(monitor, resources, null);
     * 
     * @param monitor
     * @param resources
     * @return
     */
    public List<Layer> processResources( IProgressMonitor monitor, List<Object> resources ) {
        return processResources(monitor, resources, null);
    }

    /**
     * Will acquire services for a single URL, as long as one service works we don't have an error.
     * <p>
     * If no servies work we will just punt out the exception from the last entry.
     * </p>
     * 
     * @param url
     * @return
     * @throws IOException
     */
    List<IService> acquireGoodServices( URL url, IProgressMonitor monitor ) throws IOException {
        if (url == null)
            return Collections.<IService>emptyList();
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> result = factory.createService(url);
        if (result.isEmpty()) {
            throw new IOException("Could not acquire a working service for " + url); //$NON-NLS-1$
        }
        List<IService> good = new ArrayList<IService>();
        IOException notGood = null;
        for( IService service : result ) {

            try {
                if (service.resources(monitor) != null) {
                    good.add(service);
                }
            } catch (IOException bad) {
                notGood = bad;
            }
        }
        if (good.isEmpty()) {
            if (notGood != null) {
                throw notGood;
            }
            throw new IOException("Could not acquire a working service for " + url); //$NON-NLS-1$
        }

        return good;
    }
    /**
     * Processes each element within the list of resources and returns all the Layers that have been
     * discovered as a result. This is typically used to turn URLs, Files, IServices and
     * IGeoResources into Layers.
     * 
     * @param monitor a progress monitor to indicate a user of work, can be null
     * @param resources a list of objects to be processed
     * @param target a target indicating that this is being performed on some object, can be null
     * @return a List <Layer>containing all discovered layers
     */
    @SuppressWarnings("unchecked") 
    public List<Layer> processResources( IProgressMonitor monitor, List<Object> resources,
            Object target ) {

        monitor = validateMonitor(monitor);

        List<Layer> layers = new ArrayList<Layer>();
        List<IResolve> services = new ArrayList<IResolve>();
        List<IGeoResource> georesources = new ArrayList<IGeoResource>();

        if (resources.isEmpty())
            return layers;

        for( Object object : resources ) {
            if (monitor.isCanceled())
                return null;
            if (object instanceof Layer) {
                layers.add((Layer) object);
            }
            if (object instanceof IService) {
                services.add((IService) object);
            } else if (object instanceof IGeoResource) {
                georesources.add((IGeoResource) object);
            } else if (object instanceof java.util.Map) {
                services.addAll(CatalogPlugin.getDefault().getServiceFactory().createService(
                        (java.util.Map) object));
            }
        }

        return layers;
    }

    List<IGeoResource> resources( List< ? extends IResolve> resolveList ) throws IOException {
        List<IGeoResource> build = new ArrayList<IGeoResource>();
        for( IResolve resolve : resolveList ) {
            if (resolve instanceof IGeoResource) {
                build.add((IGeoResource) resolve);
            } else if( resolve instanceof IService){
                build.addAll(resources(((IService) resolve).resources(null)));
            }
        }
        return build;
    }

    public Map getMap( IProgressMonitor monitor, Project project2, boolean createMap ) {

        Map map = null;
        if (!createMap) {
            map = new CurrentMapFinder().getCurrentMap();
        }
        if (map != null) {
            return map;
        }
        Project project=project2;
        if (project == null) {
            project = ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject();
        }
        if (project == null) {
            project = ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject();
        }

        String mapName = getNewMapName(project);

        Map newmap = ProjectFactory.eINSTANCE.createMap(project, mapName, new ArrayList<Layer>());

        return newmap;

    }

    private static class CurrentMapFinder implements Runnable {
        Map map = null;
        /**
         * @return
         */
        Map getCurrentMap() {
            map = null;
            PlatformGIS.syncInDisplayThread(this);
            return map;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // TODO Auto-generated method stub
            if (isMapOpen()) {
                UDIGEditorInput input = (UDIGEditorInput) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage().getActiveEditor()
                        .getEditorInput();
                map = (Map) input.getProjectElement();
            }
        }

        boolean isMapOpen() {

            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor() != null) {
                if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getActiveEditor() instanceof MapPart) {
                    return true;
                }
            }
            return false;
        }
    }

    private String getNewMapName( Project currentProject ) {
        String name = Messages.ProjectUIPlugin_newMap_name; 

        int count = currentProject.getElementsInternal().size() + 1;

        return name + count;
    }

    IProgressMonitor validateMonitor( IProgressMonitor monitor ) {
        if (monitor == null) {
            return new NullProgressMonitor();
        }
        return monitor;
    }

    public static MapFactory instance() {
        return INSTANCE;
    }
}
