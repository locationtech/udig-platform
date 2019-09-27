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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.UDIGEditorInput;
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

    public static MapEditorPart getActiveEditor() {
        try {
            final ArrayList<IEditorPart> editor = new ArrayList<IEditorPart>();

            Runnable runnable = new Runnable(){
                public void run() {
                    try {
                        IWorkbenchWindow window = PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow();
                        IEditorReference[] refs = window.getActivePage().getEditorReferences();
                        IMap map = ApplicationGIS.getActiveMap();

                        for( IEditorReference ref : refs ) {
                            IEditorInput input = ref.getEditorInput();
                            if (input instanceof UDIGEditorInput) {
                                UDIGEditorInput in = (UDIGEditorInput) input;
                                if (in.getProjectElement() == map) {
                                    editor.add(ref.getEditor(false));
                                    break;
                                }
                            }
                        }
                    } catch (Throwable t) {
                        // do nothing
                    }
                }
            };

            PlatformGIS.syncInDisplayThread(runnable);

            if (!editor.isEmpty() && editor.get(0) instanceof MapEditorPart) {
                return (MapEditorPart) editor.get(0);
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
    public static MapEditorPart findMapEditor( final IMap map ) {
        if (map == null)
            throw new NullPointerException("Map cannot be null"); //$NON-NLS-1$

        final MapEditorPart[] result = new MapEditorPart[1];
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (win == null)
                    return;
                IWorkbenchPage page = win.getActivePage();
                if (page == null)
                    return;
                IEditorReference[] refs = page.getEditorReferences();
                for( IEditorReference reference : refs ) {
                    IEditorPart e = reference.getEditor(false);
                    if (e instanceof MapEditorPart) {
                        MapEditorPart me = (MapEditorPart) e;
                        if (map.equals(me.getMap())) {
                            result[0] = me;
                            return;
                        }
                    }
                }
            }
        });
        return result[0];
    }

}
