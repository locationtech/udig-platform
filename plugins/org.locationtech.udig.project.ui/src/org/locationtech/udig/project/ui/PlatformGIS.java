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
package org.locationtech.udig.project.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.MapFactory;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * A facade into udig to simplify operations such as getting the active map
 * and openning a map editor.
 * 
 * @author jeichar
 * @since 0.9.0
 * @deprecated - use to {@link ApplicationGIS}
 */
public class PlatformGIS {
    /**
     * May return null of no project is active.
     * 
     * @return The current active project, or null if no such project exists.
     * @deprecated - use to {@link ApplicationGIS#getActiveProject()}
     */
    public static IProject getActiveProject() {
        Project project = ProjectPlugin.getPlugin().getProjectRegistry()
            .getCurrentProject();
        
        if (project != null) return project;
        
        return ProjectPlugin.getPlugin().getProjectRegistry()
            .getCurrentProject();
    }
    
    /**
     * May return null if the active editor is not a Map Editor.
     * @return the map contained by the current MapEditor or null if the active
     * editor is not a map editor.
      * @deprecated - use to {@link ApplicationGIS#getActiveMap()()}
    */
    public static IMap getActiveMap(){
        
            //need to be in an event thread
            final ArrayList<IMap> l = new ArrayList<IMap>();
            org.locationtech.udig.ui.PlatformGIS.syncInDisplayThread(
              new Runnable() {

                public void run() {
                    try {
                        IEditorPart editor=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                        if( editor instanceof MapEditorPart ){
                            l.add(((MapEditorPart)editor).getMap());
                        }
                    }
                    catch(NullPointerException e) {
                        //do nothing
                    }
                }
              }
            );
            
            if (!l.isEmpty())
                return l.get(0);
        
        return null;
    }
    
    /**
     * May return null if no Map Editors exist.
     * @return a list of maps contained or null if no Map Editors exist.
     * @deprecated - use to {@link ApplicationGIS#getOpenMaps()}
     */
    public static List<IMap> getMaps(){
        try{
            //For some reason, getting the active workbench doesn't seem to work??!! So looping 
            // through all the workbenches and all their pages was the slow way to go...
            //IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
            IWorkbenchWindow[] wWindows = PlatformUI.getWorkbench().getWorkbenchWindows();

            List<IMap> maps = new ArrayList<IMap>();
            for ( IWorkbenchWindow wWindow : wWindows ) {
                IWorkbenchPage[] wPages = wWindow.getPages();
                for ( IWorkbenchPage wPage : wPages ) {
                    IEditorReference[] editors = wPage.getEditorReferences();
                    for ( IEditorReference editor : editors ) {
                        if( editor.getEditor(false) instanceof MapEditorPart ){
                            maps.add(((MapEditorPart)editor.getEditor(false)).getMap());
                        }
                    }
                }
            }
            if (maps.size() == 0)
                return null;
            return maps;
        }catch(NullPointerException e){
            return null;
        }
    }

    /**
     * Opens a Map editor for the provided map.
     * @param map the map to open.  Must be an instance of Map.
     * @deprecated - use to {@link ApplicationGIS#openMap(IMap)}
     */
    public static void openMap(IMap map){
        ApplicationGIS.openProjectElement(map, false);
    }
    /**
     * creates a map and opens an editor for the map.
     * @param a list of IGeoResources.  Each resource will be a layer in the created
     * map.
      * @deprecated - use to {@link ApplicationGIS#createAndOpenMap(List))}
    */
    public static void createAndOpenMap(List<IGeoResource> resources){
        MapFactory.instance().process(null, resources, true);
    }
    /**
     * creates a map and opens an editor for the map.
     * @param a list of IGeoResources.  Each resource will be a layer in the created
     * map.
     * @param owner the project that will contain the map.  owner must be an instance of Project.  If it
     * is obtained using the framework then this will always be the case.
     * @deprecated - use to {@link ApplicationGIS#createAndOpenMap(List, IProject))}
     */
    public static void createAndOpenMap(List<IGeoResource> resources, IProject owner ){
        MapFactory.instance().process((Project)owner, resources, true);
    }
    /**
     * If an active map exists the layers will be added to that map.  Otherwise
     * an IllegalStateException will be thrown.
     * @param a list of IGeoResources.  Each resource will be a layer in the active
     * map.
     * @deprecated - use to {@link ApplicationGIS#addLayersToMap(IMap, List, int, Project))}
     */
    public static void addLayersToActiveMap(List<IGeoResource> resources) throws IllegalStateException{
        if( getActiveMap()==null )
            throw new IllegalStateException("No active map exists"); //$NON-NLS-1$
        MapFactory.instance().process(null, resources, false);
    }

    /**
     * Gets a reference to a view.  If the view has not been opened previously then the view will be
     * opened.
     * 
     * @param show whether to show the view or not.
     * @param id the id of the view to show.
     * @return returns the view or null if the view does not exist 
     * @deprecated - use to {@link ApplicationGIS#getView(boolean, String))}
     */
    public static IViewPart getView(boolean show, String id){
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewReference[] view = page.getViewReferences();
        IViewReference infoRef=null;
        for (IViewReference reference : view) {
            if( reference.getId().equals(id) ){
                infoRef=reference;
                break;
            }
        }
        // JONES: need to get the part and set the selection to null so that the last selected feature
        // will not flash (because it will not be in list any more).
        IViewPart infoView=null;
        if( infoRef == null ) {
            try {
                infoView= page.showView(id);
            } catch (PartInitException e1) {
                return null;
            }
            if( infoView==null ){
                return null;
            }
        }
        if( infoRef!=null )
            return (IViewPart) infoRef.getPart(show);
        
        return null;
    
    }

    /**
     * Runs the given runnable in a protected mode.   Exceptions
     * thrown in the runnable are logged and passed to the runnable's
     * exception handler.  Such exceptions are not rethrown by this method.
     * @deprecated - use to {@link org.locationtech.udig.ui.PlatformGIS#run(ISafeRunnable)}
    */
    public static void run( ISafeRunnable request){
        runner.addRequest(request);
    }
    
    private static Runner runner=new Runner();
    private static class Runner extends Job{
        /**
         * @param name
         */
        public Runner() {
            super("");  //$NON-NLS-1$
            setPriority(LONG);
            setSystem(true);
        }

        List<ISafeRunnable> requests=new LinkedList<ISafeRunnable>();
        IProgressMonitor current;
        
        /**
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            current=monitor;
            while( requests.size() > 0){
                ISafeRunnable runnable=requests.get(0);
                requests.remove(0);
                run(runnable);
            }
            return Status.OK_STATUS;
        }
        
        /**
         * Add a runnable object to be run.  
         * @param runnable
         */
        public void addRequest(ISafeRunnable runnable){
            if( getThread()==Thread.currentThread() ){
                if( current.isCanceled() )
                    return;
                run ( runnable );
            }else{
                requests.add(runnable);
                schedule();
            }
        }

        private void run(ISafeRunnable runnable) {
            try{
                runnable.run();
            }catch (Throwable e) {
                // be extra careful.  Maybe the handler is crazy.  If so log the exception.
                try{
                    runnable.handleException(e);
                }catch (Throwable e2) {
                    ProjectUIPlugin.log("",e );  //$NON-NLS-1$
                }
            }
        }
    }
}
