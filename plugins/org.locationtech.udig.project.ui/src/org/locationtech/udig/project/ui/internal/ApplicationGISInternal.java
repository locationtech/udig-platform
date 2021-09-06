/* uDig - User Friendly Desktop Internet GIS client
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

import org.eclipse.jface.viewers.StructuredSelection;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Allows access to the internal types such as Map.
 * 
 * @author jones
 * @since 1.0.0
 */
public class ApplicationGISInternal {

    public static ToolContext createContext( IMap map ) {
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
    public static List< ? extends Project> getProjects() {
        return Collections.unmodifiableList(ProjectPlugin.getPlugin().getProjectRegistry()
                .getProjects());
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
    public static Collection< ? extends Map> getOpenMaps() {
        return (Collection< ? extends Map>) ApplicationGIS.getOpenMaps();
    }

    /**
     * Return a feature editor for the provided feature
     *
     * @return a feature editor for the provided feature
     */
    public static FeatureEditorLoader getFeatureEditorLoader( SimpleFeature feature ) {
        if (feature == null)
            return null;
        return ProjectUIPlugin.getDefault().getFeatureEditProcessor().getClosestMatch(
                new StructuredSelection(feature));
    }

    public static MapPart getActiveEditor() {
        IMap map = ApplicationGIS.getActiveMap();
        MapPart activeMapPart = ApplicationGIS.getActiveMapPart();

        if (activeMapPart != null && map != null && map.equals(activeMapPart.getMap())) {
            return activeMapPart;
        }
        return null;
    }

    /**
     * @param map the map to find the MapPart for.
     * @return MapPart for given Map, if it has been opened already, otherwise null.
     */
    public static MapPart findMapPart( final IMap map ) {
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
