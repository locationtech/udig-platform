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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;

/**
 * Tracks which map is the active map. Essentially just listens to the workbench for part changes
 * and when a part is an MapEditorPart it marks that as the active map.
 *
 * @author jesse
 * @since 1.1.0
 */
public class ActiveMapTracker implements IStartup, IPartListener2, IWindowListener, IPageListener {

    /**
     * All the parts that are have been active in the order of activation.
     *
     */
    private List<MapPart> activeParts = new CopyOnWriteArrayList<>();

    /**
     * All the maps that are currently visible
     */
    private Set<MapPart> visibleMaps = new CopyOnWriteArraySet<>();

    /**
     * All the maps that are currently open
     */
    private List<MapPart> openMaps = new CopyOnWriteArrayList<>();

    /**
     * Returns the set of the visible maps.
     *
     * @return the set of the visible maps.
     */
    public Collection<? extends IMap> getVisibleMaps() {
        HashSet<IMap> maps = new HashSet<>();
        for (MapPart part : visibleMaps) {
            maps.add(part.getMap());
        }
        return Collections.unmodifiableCollection(maps);
    }

    /**
     * Returns the most recently active map or {@link ApplicationGIS#NO_MAP} if all maps have been
     * closed
     *
     * @return
     */
    public Map getActiveMap() {
        MapPart activePart = getActiveMapPartInternal();
        if (activePart != null) {
            return activePart.getMap();
        }

        MapPart visibleMapPart = getFirstVisibleMapPart();
        return (visibleMapPart == null ? ApplicationGIS.NO_MAP : visibleMapPart.getMap());
    }

    /**
     * @return most recent opened MapPart or null.
     */
    public MapPart getMostRecentOpenedPart() {
        if (!openMaps.isEmpty()) {
            return openMaps.get(0);
        }
        return null;
    }

    private MapPart getFirstVisibleMapPart() {
        // lets first look at activeParts
        MapPart mapPart = null;
        if (!activeParts.isEmpty()) {
            mapPart = activeParts.get(0);
        }
        if (mapPart == null) {
            // not activeParts found so lets look at the open maps.
            // lets see if we can find one that is open and visible
            for (MapPart part : openMaps) {
                if (visibleMaps.contains(part)) {
                    mapPart = part;
                    break;
                }
            }
        }
        return mapPart;
    }

    public MapPart getActiveMapPart() {
        return getActiveMapPartInternal();
    }

    private MapPart getActiveMapPartInternal() {
        if (Display.getCurrent() != null) {
            MapPart part = getActiveMapPartFromWorkbench();
            if (part == null) {
                part = getMostRecentOpenedPart();
            }
            if (!activeParts.isEmpty() && part != activeParts.get(0)) {
                addActivePart(part);
            }
            return part;
        }
        return null;
    }

    private MapPart getActiveMapPartFromWorkbench() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();
        if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part instanceof MapPart) {
                return (MapPart) part;
            }
        }
        return null;
    }

    /**
     * Returns all currently open maps;
     *
     * @return all currently open maps and empty set if no maps are open.
     */
    public Collection<? extends IMap> getOpenMaps() {
        HashSet<IMap> maps = new HashSet<>();
        for (MapPart part : openMaps) {
            maps.add(part.getMap());
        }
        return Collections.unmodifiableCollection(maps);
    }

    private void addActivePart(MapPart part) {
        while (activeParts.remove(part)) {
            ;
        }
        activeParts.add(0, part);
    }

    private void removePart(MapPart part) {
        while (activeParts.remove(part)) {
            ;
        }
        visibleMaps.remove(part);
        openMaps.remove(part);
    }

    private void addOpenMap(IWorkbenchPart part) {
        while (openMaps.remove((MapPart)part)) {
            ;
        }
        openMaps.add(0, (MapPart) part);
    }

    @Override
    public void windowClosed(IWorkbenchWindow window) {
        // stop listening to pages and parts
        IWorkbenchPage[] pages = window.getPages();
        for (IWorkbenchPage workbenchPage : pages) {
            workbenchPage.removePartListener(this);
        }
        window.removePageListener(this);
    }

    @Override
    public void windowOpened(IWorkbenchWindow window) {
        // start listening to pages and parts
        // stop listening to pages and parts
        IWorkbenchPage[] pages = window.getPages();
        for (IWorkbenchPage workbenchPage : pages) {
            pageOpened(workbenchPage);
        }
        window.addPageListener(this);
    }

    @Override
    public void pageClosed(IWorkbenchPage page) {
        page.removePartListener(this);
    }

    @Override
    public void pageOpened(IWorkbenchPage page) {
        page.addPartListener(this);
        IEditorReference[] editors = page.getEditorReferences();
        for (IEditorReference reference : editors) {
            IWorkbenchPart workbenchPart = reference.getPart(false);
            if (workbenchPart instanceof MapPart) {
                addOpenMap(workbenchPart);

                if (page.isPartVisible(workbenchPart)) {
                    visibleMaps.add((MapPart) workbenchPart);
                }
            }
        }
        IViewReference[] views = page.getViewReferences();
        for (IViewReference reference : views) {
            IWorkbenchPart workbenchPart = reference.getPart(false);
            if (workbenchPart instanceof MapPart) {
                addOpenMap(workbenchPart);

                if (page.isPartVisible(workbenchPart)) {
                    visibleMaps.add((MapPart) workbenchPart);
                }
            }
        }
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        // make this the active map(if it is a MapPart)
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof MapPart) {
            addActivePart((MapPart) part);
        }
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        // if active map then make previous map be the active map
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof MapPart) {
            removePart((MapPart) part);
        }
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        // if no active map then make this the active map (if it is a MapPart)
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof MapPart) {
            visibleMaps.add((MapPart) part);
            if (activeParts.isEmpty()) {
                activeParts.add((MapPart) part);
            }
        }
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof MapPart) {
            visibleMaps.remove((MapPart)part);
        }
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof MapPart) {
            addOpenMap(part);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // do nothing
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // do nothing
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // do nothing
    }

    @Override
    public void windowActivated(IWorkbenchWindow window) {
        // do nothing
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window) {
        // do nothing
    }

    @Override
    public void pageActivated(IWorkbenchPage page) {
        // do nothing
    }

    /**
     * Registers the object with the platform
     */
    @Override
    public void earlyStartup() {
        ApplicationGIS.setActiveMapTracker(this);
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.addWindowListener(this);
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        for (IWorkbenchWindow workbenchWindow : windows) {
            windowOpened(workbenchWindow);
        }
    }

    /**
     * @return Collection of open MapParts.
     */
    public Collection<MapPart> getOpenMapParts() {
        return Collections.unmodifiableCollection(openMaps);
    }

}
