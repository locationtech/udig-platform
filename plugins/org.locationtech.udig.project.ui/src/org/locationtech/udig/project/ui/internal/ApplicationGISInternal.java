/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Allows access to the internal types such as Map.
 *
 * @author jones
 * @since 1.0.0
 */
public class ApplicationGISInternal {

    public static ToolContext createContext(IMap map) {
        return (ToolContext) ApplicationGIS.createContext(map);
    }

    /**
     * May return null of no project is active.
     *
     * @return The current active project, or null if no such project exists.
     */
    public static Project getActiveProject() {
        return (Project) ApplicationGIS.getActiveProject();
    }

    /**
     * Return all Projects. The list is unmodifiable.
     *
     * @return all Projects.
     */
    public static List<? extends Project> getProjects() {
        return Collections
                .unmodifiableList(ProjectPlugin.getPlugin().getProjectRegistry().getProjects());
    }

    /**
     * May return null if the active editor is not a Map Editor.
     *
     * @return the map contained by the current MapEditor or null if the active editor is not a map
     *         editor.
     */
    public static Map getActiveMap() {

        return (Map) ApplicationGIS.getActiveMap();
    }

    /**
     * Returns all open maps. May return null if no Map Editors exist.
     *
     * @return a list of maps contained or null if no Map Editors exist.
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends Map> getOpenMaps() {
        return (Collection<? extends Map>) ApplicationGIS.getOpenMaps();
    }

    /**
     * Return a feature editor for the provided feature
     *
     * @return a feature editor for the provided feature
     */
    public static FeatureEditorLoader getFeatureEditorLoader(SimpleFeature feature) {
        if (feature == null)
            return null;
        return ProjectUIPlugin.getDefault().getFeatureEditProcessor()
                .getClosestMatch(new StructuredSelection(feature));
    }

    /**
     * @return
     * @deprecated return the Active Editor
     */
    @Deprecated
    public static MapPart getActiveEditor() {
        return getActiveMapPart();
    }

    /**
     * @return the Active MapPart or null if there is no active MapPart.
     */
    public static MapPart getActiveMapPart() {
        try {
            final AtomicReference<MapPart> editor = new AtomicReference<>();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {

                        IWorkbenchWindow window = PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow();
                        IEditorReference[] refs = window.getActivePage().getEditorReferences();
                        IMap map = ApplicationGIS.getActiveMap();

                        for (IEditorReference ref : refs) {
                            IWorkbenchPart part = ref.getPart(false);
                            Map adapter = part.getAdapter(Map.class);
                            if (adapter != null && map == adapter && part instanceof MapPart) {
                                editor.set((MapPart) part);
                                break;
                            }
                        }

                    } catch (Throwable t) {
                        // do nothing
                    }
                }
            };

            PlatformGIS.syncInDisplayThread(runnable);

            return editor.get();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @deprecated Use {link {@link ApplicationGISInternal#findMapPart(IMap)}} instead!
     */
    @Deprecated
    public static MapPart findMapEditor(final IMap map) {
        return findMapPart(map);
    }

    /**
     * @param map the map to find the MapPart for.
     * @return MapPart for given Map, if it has been opened already, otherwise null.
     */
    public static MapPart findMapPart(final IMap map) {
        if (map == null)
            throw new NullPointerException("Map cannot be null"); //$NON-NLS-1$
        Collection<MapPart> openMapParts = ApplicationGIS.getOpenMapParts();
        for (MapPart mapPart : openMapParts) {
            if (map.equals(mapPart.getMap())) {
                return mapPart;
            }
        }
        return null;
    }

}
