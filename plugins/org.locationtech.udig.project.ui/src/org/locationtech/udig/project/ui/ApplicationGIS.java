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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.element.ElementFactory;
import org.locationtech.udig.project.element.IGenericProjectElement;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.commands.AddLayersCommand;
import org.locationtech.udig.project.internal.commands.CreateMapCommand;
import org.locationtech.udig.project.internal.impl.ProjectRegistryImpl;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.RenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.locationtech.udig.project.ui.commands.OpenProjectElementCommand;
import org.locationtech.udig.project.ui.internal.ActiveMapTracker;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.UDIGEditorInputDescriptor;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.internal.tool.display.ToolManager;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.coverage.grid.GridCoverage;

/**
 * A facade into udig to simplify operations such as getting the active map and
 * openning a map editor.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ApplicationGIS {

    private static IToolManager                                          toolManager;
    private static ActiveMapTracker activeMapTracker;

    /**
     * Obtains the current project.
     * 
     * @return The current active project
     */
    public static IProject getActiveProject() {
        Project project = ProjectPlugin.getPlugin().getProjectRegistry()
                .getCurrentProject();

        if (project != null)
            return project;

        return ProjectPlugin.getPlugin().getProjectRegistry()
                .getDefaultProject();
    }

    /**
     * Return all Projects. The list is unmodifiable.
     * 
     * @return all Projects.
     */
    public static List<? extends IProject> getProjects() {
        return Collections.unmodifiableList(ProjectPlugin.getPlugin()
                .getProjectRegistry().getProjects());
    }


    /**
     * Returns the active map.  Returns {@link #NO_MAP} if there is no open map.
     * 
     * @return the map contained by the current MapEditor or null if the active editor is not a map
     *         editor.
     */
    public static IMap getActiveMap() {
        return activeMapTracker.getActiveMap();
    }
    /**
     * Returns all open maps.
     * 
     * @return a Collection of maps contained.
     */
    public static Collection< ? extends IMap> getOpenMaps() {
        return activeMapTracker.getOpenMaps();
    }

    /**
     * Returns all visible maps.
     * 
     * @return a Collection of maps contained.
     */
    public static Collection< ? extends IMap> getVisibleMaps() {
        return activeMapTracker.getVisibleMaps();
    }

    /**
     * Opens a Map editor for the provided map, This is a non-blocking call.
     * Equivalent to openMap(map, false);
     * 
     * @param map
     *            the map to open. Must be an instance of Map.
     */
    public static void openMap(IMap map) {
        openMap(map, false);
    }

    /**
     * Opens a Map editor for the provided map.
     * 
     * @param map
     *            the map to open. Must be an instance of Map.
     * @param wait
     *            indicates whether to wait for the map to open before
     *            returning.
     */
    public static void openMap(IMap map, boolean wait) {
        openProjectElement(map, wait);
    }

    /**
     * creates a map and opens an editor for the map.
     * 
     * @param a
     *            list of IGeoResources. Each resource will be a layer in the
     *            created map.
     */
    public static void createAndOpenMap(List<IGeoResource> resources) {
        CreateMapCommand command = new CreateMapCommand(null, resources, null);
        getActiveProject().sendSync(command);
        openMap(command.getCreatedMap());
    }

    /**
     * creates a map and opens an editor for the map.
     * 
     * @param a
     *            list of IGeoResources. Each resource will be a layer in the
     *            created map.
     * @param owner
     *            the project that will contain the map. owner must be an
     *            instance of Project. If it is obtained using the framework
     *            then this will always be the case.
     */
    public static void createAndOpenMap(List<IGeoResource> resources,
            IProject owner) {
        CreateMapCommand command = new CreateMapCommand(null, resources, owner);
        getActiveProject().sendSync(command);
        openMap(command.getCreatedMap());
    }
    
    /**
     * creates a map and opens an editor for the map.
     * 
     * @param a
     *            list of IGeoResources. Each resource will be a layer in the
     *            created map.
     * @param owner
     *            the project that will contain the map. owner must be an
     *            instance of Project. If it is obtained using the framework
     *            then this will always be the case.
     * @param wait
     *            indicates whether to wait for the map to open before
     *            returning.
     */
    public static void createAndOpenMap(List<IGeoResource> resources,
            IProject owner, boolean wait) {
        CreateMapCommand command = new CreateMapCommand(null, resources, owner);
        getActiveProject().sendSync(command);
        openMap(command.getCreatedMap(), wait);
    }

    /**
     * Gets a reference to a view. If the view has not been opened previously
     * then the view will be opened.
     * 
     * @param show
     *            whether to show the view or not.
     * @param id
     *            the id of the view to show.
     * @return returns the view or null if the view does not exist
     */
    public static IViewPart getView(boolean show, String id) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        IViewReference[] view = page.getViewReferences();
        IViewReference infoRef = null;
        for (IViewReference reference : view) {
            if (reference.getId().equals(id)) {
                infoRef = reference;
                break;
            }
        }
        // JONES: need to get the part and set the selection to null so that the
        // last selected
        // feature will not flash (because it will not be in list any more).
        IViewPart infoView = null;
        if (infoRef == null) {
            try {
                infoView = page.showView(id);
            } catch (PartInitException e1) {
                return null;
            }
            if (infoView == null) {
                return null;
            }
        }
        if (infoRef != null)
            return (IViewPart) infoRef.getPart(show);

        return null;

    }

    /**
     * Runs the given runnable in a protected mode. Exceptions thrown in the
     * runnable are logged and passed to the runnable's exception handler. Such
     * exceptions are not rethrown by this method.
     */
    public static void run(ISafeRunnable request) {
        PlatformGIS.run(request);
    }

    /**
     * Returns an editor input for the type passed in. Processes the
     * editorInputs extension point.
     * 
     * @param type
     * @return an editor input for the type passed in.
     */
    public static List<UDIGEditorInputDescriptor> getEditorInputs(
            final IProjectElement projectElement) {

        final List<UDIGEditorInputDescriptor> newInputs = new ArrayList<UDIGEditorInputDescriptor>();

        List<IConfigurationElement> extensions = ExtensionPointList.getExtensionPointList("org.locationtech.udig.project.ui.editorInputs");
        Class toMatch;
        if( projectElement instanceof ProjectElementAdapter){
            toMatch = ((ProjectElementAdapter)projectElement).getBackingObject().getClass();
        }else{
            toMatch = projectElement.getClass();
        }
        for (IConfigurationElement element : extensions) {
            String projectElementClassName = element
                    .getAttribute("projectElement"); //$NON-NLS-1$
            Class match = match(toMatch, projectElementClassName);
            if (match != null) {
                UDIGEditorInputDescriptor input = new UDIGEditorInputDescriptor();
                input.setEditorID(element.getAttribute("editorPartID")); //$NON-NLS-1$
                input.setName(element.getAttribute("name")); //$NON-NLS-1$
                input.setExtensionElement(element);
                input.setType(match);
                newInputs.add(input);
            }
        }
        return newInputs;
    }

    @SuppressWarnings("unchecked")
    private static Class match(Class toMatch, String projectElementClassName) {
        if(toMatch.getName().equals(projectElementClassName) || toMatch.getCanonicalName().equals(projectElementClassName)){
            return toMatch;
        }
        Class superClass = toMatch.getSuperclass();
        if( superClass!=Object.class && superClass!=null){
            Class result = match(superClass,projectElementClassName);
            if( result!=null){
                return result;
            }
        }
        Class[] interfaces = toMatch.getInterfaces();
        for (Class iFace : interfaces) {
            Class result = match(iFace,projectElementClassName);
            if( result!=null){
                return result;
            }
        }
        return null;
    }


    /**
     * Returns the ToolManager singleton.
     * 
     * @return the ToolManager singleton.
     */
    public static IToolManager getToolManager() {
        synchronized (ToolManager.class) {
            if (toolManager == null) {
                
                String prefConstant = IToolManager.P_TOOL_MANAGER;
                String xpid = IToolManager.XPID;
                String idField = IToolManager.ATTR_ID;
                String classField = IToolManager.ATTR_CLASS;
                
                IToolManager result = (IToolManager) UiPlugin.lookupConfigurationObject(
                        IToolManager.class, ProjectUIPlugin.getDefault().getPreferenceStore(),
                        ProjectUIPlugin.ID,
                        prefConstant, xpid, idField, classField);
                if (result != null) {
                    toolManager = result;
                } else {
                    toolManager = new ToolManager();
                }
            }
        }
        return toolManager;
   }

    /**
     * Returns the IEditorInput instance that wraps the element argument.
     * 
     * @return the IEditorInput instance that wraps the element argument.
     */
    public static UDIGEditorInput getInput(IProjectElement element) {
        List<UDIGEditorInputDescriptor> descriptors = getEditorInputs(element);
        for (UDIGEditorInputDescriptor descriptor : descriptors) {
            UDIGEditorInput input = descriptor.createInput(element);
            if (input != null) {
                return input;
            }
        }
        return null;
    }

    /**
     * Creates a Tools Context out of Map.
     * 
     * @param map
     *            that the context interacts with
     * @return a ToolContext
     * @see ToolContext
     */
    public static IToolContext createContext(IMap map) {
        if (map instanceof org.locationtech.udig.project.internal.Map) {
            ToolContext context = new ToolContextImpl();
            context
                    .setMapInternal((org.locationtech.udig.project.internal.Map) map);
            return context;
        }
        return null;
    }

    /**
     * Make layers from the resourceList and adds the layers to the map.
     * <p>
     * <b>NOTE</b> map may be null. If it is then the current open map will be
     * used (see {@link #getActiveMap()} or a new map will be created if that is
     * null.
     * </p>
     * 
     * @param map
     *            the map to add the layers to. If null the current active map
     *            will be used or a new one will be created
     * @param resourceList
     *            Resources to add to the map.
     * @param startPosition
     *            z-position of the layers to add. if -1 it will be added to the
     *            top of the map (0 is the bottom of the map and
     *            map.getMapLayer.size() is the top of the map).
     * @return layers that were added.
     */
    public static List<? extends ILayer> addLayersToMap(IMap map,
            List<IGeoResource> resourceList, int startPosition) {
        return addLayersToMap(map, resourceList, startPosition, null, false);
    }

    /**
     * Make layers from the resourceList, creates a new map, adds layers to map
     * and adds map to the project.
     * 
     * @param project
     *            project that new map should be added to
     * @param resourceList
     *            Resources to add to the map.
     * @param startPosition
     *            z-position of the layers to add. if -1 it will be added to the
     *            top of the map (0 is the bottom of the map and
     *            map.getMapLayer.size() is the top of the map).
     * 
     * @return layers that were added.
     */
    public static List<? extends ILayer> addLayersToMap(IProject project,
            List<IGeoResource> resourceList) {
        return addLayersToMap(null, resourceList, 0, project, false);
    }

    /**
     * Make layers from the resourceList and adds the layers to the map.
     * <p>
     * <b>NOTE</b> map may be null. If it is then the current open map will be
     * used (see {@link #getActiveMap()} or a new map will be created if that is
     * null.
     * </p>
     * 
     * @param map
     *            the map to add the layers to. If null the current active map
     *            will be used or a new one will be created
     * @param resourceList
     *            Resources to add to the map.
     * @param startPosition
     *            z-position of the layers to add. if -1 it will be added to the
     *            top of the map (0 is the bottom of the map and
     *            map.getMapLayer.size() is the top of the map).
     * @param project
     *            project that map should be added to... Only used if there is
     *            no current map. If project is then the default project is
     *            used.
     * @return layers that were added.
     * @deprecated
     */
    public static List<? extends ILayer> addLayersToMap(IMap map,
            List<IGeoResource> resourceList, int startPosition, Project project) {
        return addLayersToMap(map, resourceList, startPosition, project, false);
    }

    /**
     * Make layers from the resourceList and adds the layers to the map.
     * <p>
     * <b>NOTE</b> map may be null. If it is then the current open map will be
     * used (see {@link #getActiveMap()} or a new map will be created if that is
     * null.
     * </p>
     * 
     * @param map
     *            the map to add the layers to. If null the current active map
     *            will be used or a new one will be created
     * @param resourceList
     *            Resources to add to the map.
     * @param startPosition
     *            z-position of the layers to add. if -1 it will be added to the
     *            top of the map (0 is the bottom of the map and
     *            map.getMapLayer.size() is the top of the map).
     * @param project
     *            project that map should be added to... Only used if there is
     *            no current map. If project is then the default project is
     *            used.
     * @param wait
     *            if true then method will block until map has been opened
     *            otherwise will return without blocking.
     */
    public static List<? extends ILayer> addLayersToMap(IMap map2,
            List<IGeoResource> resourceList, int startPosition2,
            IProject project2, boolean wait) {
        
        IMap map = map2;
        if (map == null && project2 == null)
            map = (IMap) getActiveMap();

        if(map==NO_MAP){
            map = null;
        }
        
        IProject project = project2;
        if (project == null) {
            if (map == null)
                project = ProjectPlugin.getPlugin().getProjectRegistry()
                        .getCurrentProject();
            else
                project = map.getProject();
        }
        List<? extends ILayer> layers;
        
        /*
         * Check or not for duplicate layers in context of the map where georesources are added.
         */
        List<IGeoResource> cleanedGeoResources;
        if(ProjectPlugin.getPlugin().getPluginPreferences().getBoolean(PreferenceConstants.P_CHECK_DUPLICATE_LAYERS)){
            cleanedGeoResources = ProjectUtil.cleanDuplicateGeoResources(resourceList, map);
        }else{
            cleanedGeoResources = resourceList;
        }
        
        if (map == null) {
            CreateMapCommand cmCommand = new CreateMapCommand(null, cleanedGeoResources, project);
            project.sendSync(cmCommand);
            map = cmCommand.getCreatedMap();
            layers=map.getMapLayers();
        } else {
            AddLayersCommand alCommand = new AddLayersCommand(cleanedGeoResources, startPosition2);
            map.sendCommandSync(alCommand);
            layers=alCommand.getLayers();
        }

        if (!ApplicationGISInternal.getOpenMaps().contains(map)) {
            openMap(map, wait);
        }
        return layers;
    }

    /**
     * Loads the project element indicated by the url and adds the map to the
     * provided project.
     * 
     * @param url
     *            the project element to load
     * @param project
     *            the project to add the project element to.
     * @return returns the loaded project element.
     * @throws IOException
     *             thrown if there is a problem reading the project element file
     * @throws IllegalArgumentException
     *             thrown if the file indicated by the URL is not a project
     *             element file.
     */
    public static IProjectElement loadProjectElement(URL url, IProject project)
            throws IOException, IllegalArgumentException {
        URI uri = URI.createURI(url.toString());

        Resource mapResource;
        try {
            mapResource = ProjectRegistryImpl.getProjectRegistry().eResource()
                    .getResourceSet().getResource(uri, true);
        } catch (Exception e) {
            throw new IOException(Messages.ApplicationGIS_loadError + uri);
        }

        Object obj;
        try {
            obj = mapResource.getContents().get(0);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    Messages.ApplicationGIS_illegalArgumentPart1 + uri
                            + Messages.ApplicationGIS_illegalArgumentPart2);
        }
        if (!(obj instanceof ProjectElement))
            throw new IllegalArgumentException(
                    Messages.ApplicationGIS_noProjectElement
                            + obj.getClass().getSimpleName());

        ProjectElement elem = (ProjectElement) obj;
        ((Project) project).getElementsInternal().add(elem);

        return elem;
    }
   
    /**
     * Opens a {@link IProjectElement} for editing/viewing.
     * 
     * @param obj
     *            object to open
     * @param wait
     *            whether or not to perform the action asynchronously
     */
    public static void openProjectElement(IProjectElement obj, boolean wait) {
        OpenProjectElementCommand command = new OpenProjectElementCommand(obj);
        if (wait)
            ApplicationGIS.getActiveProject().sendSync(command);
        else
            ApplicationGIS.getActiveProject().sendASync(command);
    }

    /**
     * Parameter class for
     * {@link ApplicationGIS#drawMap(org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter)}
     * 
     * @author jesse
     * 
     * @see ApplicationGIS#drawMap(org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter)
     */
    public static class DrawMapParameter {
        final BoundsStrategy boundsStrategy;
        final Graphics2D graphics;
        final Dimension destinationSize;
        final IMap toDraw;
        final IProgressMonitor monitor;
        final SelectionStyle selectionStyle;
        final int dpi;
        final boolean transparent;
        final boolean doBufferedImageForGrids;

        /**
         * New instance
         * 
         * @param graphics The graphics to draw to
         * @param destinationSize the size of the area to draw to
         * @param toDraw the map to draw.
         * @param boundsStrategy An object for determining how to set the bounds on the map.  Feel free to extend the current implementation
         * @param dpi the dpi of the map.  The standard PDF is 72, the standard according to the OGC is 90
         * @param selectionStyle how to handle the selection (getFilter()) on a lyaer
         * @param monitor a progress monitor
         */
        public DrawMapParameter(Graphics2D graphics, Dimension destinationSize,
                IMap toDraw, BoundsStrategy boundsStrategy, int dpi, SelectionStyle selectionStyle,
                IProgressMonitor monitor, boolean transparent, boolean doBufferedImageForGrids) {
            this.graphics = graphics;
            this.destinationSize = destinationSize;
            this.toDraw = toDraw;
            this.dpi = dpi;
            this.doBufferedImageForGrids = doBufferedImageForGrids;
            if (boundsStrategy == null) {
                this.boundsStrategy = new BoundsStrategy(toDraw.getViewportModel().getScaleDenominator());
            }
            else {
                this.boundsStrategy = boundsStrategy;
            }
            this.monitor = monitor;
            this.selectionStyle=selectionStyle;
            this.transparent = transparent;
        }
        
        /**
         * New instance
         * 
         * @param graphics The graphics to draw to
         * @param destinationSize the size of the area to draw to
         * @param toDraw the map to draw.
         * @param boundsStrategy An object for determining how to set the bounds on the map.  Feel free to extend the current implementation
         * @param dpi the dpi of the map.  The standard PDF is 72, the standard according to the OGC is 90
         * @param selectionStyle how to handle the selection (getFilter()) on a lyaer
         * @param monitor a progress monitor
         */
        public DrawMapParameter(Graphics2D graphics, Dimension destinationSize,
                IMap toDraw, BoundsStrategy boundsStrategy, int dpi, SelectionStyle selectionStyle,
                IProgressMonitor monitor) {
            this(graphics, 
                    destinationSize, 
                    toDraw, 
                    boundsStrategy, 
                    dpi, 
                    selectionStyle, 
                    monitor, 
                    false, false);
        }

        /**
         * Create a new instance with a DPI of 90 (OGC default).
         * 
         * @param graphics the graphics to draw on.
         * @param destinationSize the destination size.
         * @param toDraw the map to draw
         * @param monitor the progress monitor
         */
        public DrawMapParameter(Graphics2D graphics, Dimension destinationSize,
                IMap toDraw, IProgressMonitor monitor) {
            this(graphics, destinationSize, toDraw, new BoundsStrategy(toDraw.getViewportModel().getScaleDenominator()), 90, SelectionStyle.OVERLAY, monitor);            
        }
        
        /**
         * Create a new instance with the given DPI
         * 
         * @param graphics the graphics to draw on.
         * @param destinationSize the destination size.
         * @param toDraw the map to draw
         * @param dpi the output DPI
         * @param monitor the progress monitor
         */
        public DrawMapParameter(Graphics2D graphics, Dimension destinationSize,
                IMap toDraw, int dpi, IProgressMonitor monitor) {
            this(graphics, 
                    destinationSize, 
                    toDraw, 
                    new BoundsStrategy(toDraw.getViewportModel().getScaleDenominator()), 
                    dpi, 
                    SelectionStyle.OVERLAY, 
                    monitor);            
        }
        
        /**
         * Create a new instance with a DPI of 90 (OGC default).
         * 
         * @param graphics the graphics to draw on.
         * @param destinationSize the destination size.
         * @param toDraw the map to draw
         * @param monitor the progress monitor
         * @param transparent is the background transparent?
         */
        public DrawMapParameter(Graphics2D graphics, Dimension destinationSize,
                IMap toDraw, IProgressMonitor monitor, boolean transparent) {
            this(graphics, destinationSize, toDraw, new BoundsStrategy(toDraw.getViewportModel().getScaleDenominator()), 90, SelectionStyle.OVERLAY, monitor, transparent, false);            
        }

        /**
         * Copies the parameters as well as the graphics and destinationSize objects.  DOES NOT COPY map ToDraw
         * @param params2 the parameters to copy
         */
        public DrawMapParameter(DrawMapParameter params2) {
            this((Graphics2D) params2.graphics.create(), new Dimension(
                    params2.destinationSize), params2.toDraw,
                    params2.boundsStrategy, params2.dpi,  params2.selectionStyle,
                    params2.monitor,params2.transparent, params2.doBufferedImageForGrids);
        }
        
    }
    
    /**
     * Renders the provided map on to the provided graphics2D object.
     * @param params parameters that describe how and where to draw the map
     * 
     * @throws RenderException
     *             Thrown if an error occurs such as a unreachable server.
     *             
     * @return the map that was rendered.  It will not be saved or and is not part of any project.
     */
    public static IMap drawMap(final DrawMapParameter drawMapParams) throws RenderException {
        final DrawMapParameter params = new DrawMapParameter( drawMapParams );
        IProgressMonitor monitor = params.monitor;
        
        final Map map = (Map) EcoreUtil.copy((EObject) params.toDraw);
        
        map.getBlackboard().addAll(drawMapParams.toDraw.getBlackboard());
        
        for (int i = 0; i < map.getMapLayers().size(); i++) {
            ILayer source = params.toDraw.getMapLayers().get(i);
            Layer dest = map.getLayersInternal().get(i);
            dest.setFilter(source.getFilter());
            dest.getBlackboard().addAll(source.getBlackboard());
        }
        
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                // Load IGeoResources using original map. The new map can't do this because it doesn't have a
                // Resource(file) and therefore can't resolve relative URIs
                List<ILayer> layers = drawMapParams.toDraw.getMapLayers();
                for (ILayer layer : layers) {
                    layer.getGeoResources();
                }
                
                Color background = (Color) map.getBlackboard().get(ProjectBlackboardConstants.MAP__BACKGROUND_COLOR);
                params.graphics.setBackground(background);
                if (!drawMapParams.transparent) {
                    params.graphics.clearRect(0, 0, params.destinationSize.width, params.destinationSize.height);
                }
                List<Layer> layersToRender = params.selectionStyle.handleSelection(map.getLayersInternal());
                
                ProjectUIPlugin
                        .trace(
                                ApplicationGIS.class,
                                "ApplicationGIS.drawMap() beginning rendering of map '" + map.getName() + "'", null); //$NON-NLS-1$ //$NON-NLS-2$

                ReferencedEnvelope bounds = (ReferencedEnvelope) params.toDraw.getViewportModel().getBounds();
                ReferencedEnvelope boundsCopy = new ReferencedEnvelope(bounds);
                RenderContext tools = configureMapForRendering(map, params.destinationSize, params.dpi, params.boundsStrategy, boundsCopy);
                
                RendererCreator decisive = new RendererCreatorImpl();
                decisive.setContext(tools);

                decisive.getLayers().addAll(layersToRender);

                SortedSet<RenderContext> sortedContexts = new TreeSet<RenderContext>(
                        decisive.getConfiguration());

                render(params, monitor, decisive, sortedContexts);

            }

            private void render(final DrawMapParameter params,
                    IProgressMonitor monitor, RendererCreator decisive,
                    SortedSet<RenderContext> sortedContexts)
                    throws InvocationTargetException {
                
                monitor.beginTask("Rendering map", sortedContexts.size());
                RenderContext mainContext = decisive.getContext();

                ILabelPainter labelPainter = mainContext.getLabelPainter();
                labelPainter.clear();
                labelPainter.start();
                
                Dimension displaySize = params.destinationSize;
                Iterator<RenderContext> iter = sortedContexts.iterator();
                while (iter.hasNext()) {
                    RenderContext context = (RenderContext) iter.next();

                    ILayer layer = context.getLayer();
                    boolean isLayerFromGrid = layer.getGeoResource().canResolve(GridCoverage.class);
                    String layerId = getLayerId(layer);

                    if( !(layer instanceof SelectionLayer) ||
                            ((layer instanceof SelectionLayer) && params.selectionStyle.getShowLabels()) ){
                        labelPainter.startLayer(layerId);
                    }
                    try {
                        if (context instanceof CompositeRenderContext) {
                            CompositeRenderContext compositeContext = (CompositeRenderContext) context;
                            List<ILayer> layers = compositeContext.getLayers();
                            boolean visible = false;
                            for (ILayer tmpLayer : layers) {
                                visible = visible || tmpLayer.isVisible();
                            }
                            if (!visible)
                                continue;
                        } else if (!layer.isVisible())
                            continue;
                        Renderer renderer = decisive.getRenderer(context);
                        ProjectUIPlugin
                                .trace(
                                        ApplicationGIS.class,
                                        "Issuing render call to " + renderer.getName(), null); //$NON-NLS-1$
                        try {
                            Graphics2D graphics = (Graphics2D) params.graphics.create();
                            if (params.doBufferedImageForGrids && isLayerFromGrid) {
                                Rectangle clipBounds = graphics.getClipBounds();
                                BufferedImage bi = new BufferedImage(clipBounds.width,
                                        clipBounds.height, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D biG2D = (Graphics2D) bi.getGraphics();
                                renderer.render(biG2D, monitor);
                                graphics.drawImage(bi, null, 0, 0);
                                biG2D.dispose();
                            }else{
                                renderer.render(graphics, monitor);
                            }
                            
                        } catch (RenderException e) {
                            throw new InvocationTargetException(e);
                        }
                    } finally {
                        labelPainter.endLayer(layerId, params.graphics,
                                new Rectangle(displaySize));
                    }
                }
                labelPainter.end(
                        params.graphics,
                        new Rectangle(displaySize));
                labelPainter.clear();
            }

            private String getLayerId(ILayer layer ) {
                String layerId = layer.getID().toString();
                if ( layer instanceof SelectionLayer )
                    layerId = layerId+"-Selection"; //$NON-NLS-1$
                return layerId;
            }

        };

        try {
            PlatformGIS.runBlockingOperation(runnable, monitor);
        } catch (InvocationTargetException e) {
            throw (RenderException) e.getCause();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    /**
     * Creates a ViewportModel and RenderManager; configures them correctly and sets them on the RenderContext.  
     * The layer and the georesource still must be set on the context.
     * Returns the Context. 
     *
     * @param map the map 
     * @param destinationSize the size of the destination area
     * @param dpi the dpi of the destination
     * @param boundsStrategy the strategy to use for setting the bounds on the viewport model
     * @param baseMapBounds the bounds of the reference map, depending on the boundsStrategy it may be null.
     * @return a render context
     */
    public static RenderContext configureMapForRendering(Map map, final Dimension destinationSize, final int dpi, BoundsStrategy boundsStrategy, ReferencedEnvelope baseMapBounds) {
        RenderManager manager = RenderFactory.eINSTANCE.createRenderManagerViewer();

        map.setRenderManagerInternal(manager);

        RenderContext tools = new RenderContextImpl();
        tools.setMapInternal(map);
        tools.setRenderManagerInternal(manager);

        ProjectUIPlugin
                .trace(
                        ApplicationGIS.class,
                        "Firing size changed event. Changing to size: " + destinationSize.width + " by " + destinationSize.height, null); //$NON-NLS-1$ //$NON-NLS-2$

        manager.setMapInternal(map);
        manager.setMapDisplay(new IMapDisplay(){
            public java.awt.Dimension getDisplaySize() {
                return new java.awt.Dimension(destinationSize.width,
                        destinationSize.height);
            }

            public int getWidth() {
                return destinationSize.width;
            }

            public int getHeight() {
                return destinationSize.height;
            }

            public int getDPI() {
                return dpi;
            }
        });

        ViewportModel model = map.getViewportModelInternal();

        manager.setViewportModelInternal(model);

        model.setCRS(map.getViewportModel().getCRS());
        model.zoomToBox(map.getViewportModel().getBounds());

        ProjectUIPlugin.trace(ApplicationGIS.class,
                "Using bounds: " + map.getViewportModel().getBounds(), null); //$NON-NLS-1$

        model.setMapInternal(map);

        model
                .sizeChanged(new MapDisplayEvent(null, new java.awt.Dimension(0, 0),
                        new java.awt.Dimension(destinationSize.width,
                                destinationSize.height)));

        boundsStrategy.setBounds(model, baseMapBounds);
        
        return tools;

    }

    /**
     * This method should only be called by uDig.  If it is called by any one else an exception will be thrown.
     *
     * @param activeMapTracker the tracker that managers
     */
    public static void setActiveMapTracker( ActiveMapTracker activeMapTrackerToSet ) {
        if (activeMapTracker != null ){
            throw new Error("This method has already been called! It is an error for non-uDig code to call this method");
        }
        activeMapTracker = activeMapTrackerToSet;
    }

    public static final Map NO_MAP = ProjectFactory.eINSTANCE.createMap();
    

    /**
     * Performs a deep copy of a map.
     *
     * @param mapToCopy
     * @return a new instance of the map.
     */
    public static IMap copyMap(IMap mapToCopy){
        // Load IGeoResources using original map. The new map can't do this because it doesn't have a
        // Resource(file) and therefore can't resolve relative URIs
        List<ILayer> layers = mapToCopy.getMapLayers();
        for (ILayer layer : layers) {
            layer.getGeoResources();
        }

        final Map copy = (Map) EcoreUtil.copy((EObject) mapToCopy);
        copy.getBlackboard().addAll(mapToCopy.getBlackboard());
        for (int i = 0; i < copy.getMapLayers().size(); i++) {
            ILayer source = mapToCopy.getMapLayers().get(i);
            Layer dest = copy.getLayersInternal().get(i);
            dest.setFilter(source.getFilter());
            dest.getBlackboard().addAll(source.getBlackboard());
            
            dest.setStyleBlackboard((StyleBlackboard)dest.getStyleBlackboard().clone());
            
        }
        
        return copy;
    }

    /**
     * Creates an instance of the typeToCreate and wraps it with the {@link ProjectElementAdapter}.
     *
     * This is part of the mechanism for adding custom items to a Project without needing to learn
     * the EMF framework.  See the org.locationtech.udig.project.element Extension Point.
     * 
     * If the typeToCreate is NOT the same or a superclass of the object created or if an object cannot
     * be created a {@link IllegalArgumentException} will be thrown 
     * @param project 
     * 
     * @param typeToCreate The type of object that is expected to be created.  This is provided as a
     * check to ensure that the correct type is returned.
     * @param extensionId the extension to use to create a new instance.
     * 
     * @return A {@link ProjectElementAdapter} that wraps/adapts the object created using the extension
     */
    public static ProjectElementAdapter createGeneralProjectElement(
            IProject project, Class< ? extends IGenericProjectElement> typeToCreate, String extensionId ) throws IllegalArgumentException{
        return ElementFactory.eINSTANCE.createProjectElementAdapter(project, typeToCreate, extensionId);
    }

    /**
     * Creates an instance of the typeToCreate and wraps it with the {@link ProjectElementAdapter}.
     *
     * This is part of the mechanism for adding custom items to a Project without needing to learn
     * the EMF framework.  See the org.locationtech.udig.project.element Extension Point.
     * 
     * If the typeToCreate is NOT the same or a superclass of the object created or if an object cannot
     * be created a {@link IllegalArgumentException} will be thrown 
     * @param project the project to add the newly created adapter to
     * @param elementName the name of the project to create
     * @param typeToCreate The type of object that is expected to be created.  This is provided as a
     * check to ensure that the correct type is returned.
     * @param extensionId the extension to use to create a new instance.
     * 
     * @return A {@link ProjectElementAdapter} that wraps/adapts the object created using the extension
     */
    public static ProjectElementAdapter createGeneralProjectElement(
            IProject project, String elementName, Class< ? extends IGenericProjectElement> typeToCreate, String extensionId ) throws IllegalArgumentException{
        return ElementFactory.eINSTANCE.createProjectElementAdapter(project, elementName, typeToCreate, extensionId);
    }

}
