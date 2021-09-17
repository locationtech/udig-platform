/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.geoselection.AbstractGeoSelectionManager;
import org.locationtech.udig.project.geoselection.GeoSelectionChangedEvent;
import org.locationtech.udig.project.geoselection.GeoSelectionEntry;
import org.locationtech.udig.project.geoselection.IGeoSelection;
import org.locationtech.udig.project.geoselection.IGeoSelectionChangedListener;
import org.locationtech.udig.project.geoselection.IGeoSelectionEntry;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;

/**
 *
 * The UDIG platform standard geoselection manager instance.
 * <p>
 * It is returned by GeoSelectionService.getPlatformSelectionManager().
 *
 * @author Vitalus
 * @since UDIG 1.1
 */
public class PlatformGeoSelectionManager extends AbstractGeoSelectionManager {

    /**
     * ID of the manager.
     */
    public static final String ID = "org.locationtech.udig.project.ui.platformGeoSelectionManager"; //$NON-NLS-1$

    /**
     * Logger.
     */
    public static Logger LOGGER = Logger.getLogger("org.locationtech.udig.project.geoselection"); //$NON-NLS-1$

    /**
     * Key for selection bag in Map's blackboard.
     */
    public static final String PLATFORM_SELECTION_BAG = "org.locationtech.udig.project.ui.PLATFORM_SELECTION_BAG"; //$NON-NLS-1$

    /**
     * Key for cached selection bag in Map's blackboard.
     */
    public static final String PLATFORM_SELECTION_CACHE_KEY = "org.locationtech.udig.project.ui.PLATFORM_SELECTION_CACHE"; //$NON-NLS-1$

    private MapEditorPartListener partListener;

    private List<IGeoSelectionEntry> cachedSelections;

    private IGeoSelectionEntry latestGeoSelection = null;

    private boolean initialized = false;

    /**
     * Current active Map.
     */
    protected Map currentMap;

    /**
     *
     */
    public PlatformGeoSelectionManager() {
        super();
        partListener = new MapEditorPartListener();
        initialize();
    }

    private void initialize() {
        if (!initialized) {
            org.locationtech.udig.ui.PlatformGIS.syncInDisplayThread(new Runnable() {
                @Override
                public void run() {
                    IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    if (ww != null) {
                        IWorkbenchPage activePage = ww.getActivePage();
                        if (activePage != null) {
                            activePage.addPartListener(partListener);
                            initialized = true;

                            LOGGER.info("PlatformGeoSelectionManager is INITIALIZED.");

                            Map activeMap = ApplicationGISInternal.getActiveMap();
                            if (activeMap != ApplicationGIS.NO_MAP)
                                setCurrentMap(activeMap);
                        }
                    }

                }
            });
        }
    }

    protected void setCurrentMap(Map activeMap) {
        HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>) activeMap
                .getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
        if (selectionBag == null) {
            selectionBag = new HashMap<>();
            activeMap.getBlackBoardInternal().put(PLATFORM_SELECTION_BAG, selectionBag);

            cachedSelections = Collections.EMPTY_LIST;
        } else {

            ArrayList<IGeoSelectionEntry> cache = new ArrayList<>(selectionBag.values());
            cachedSelections = Collections.unmodifiableList(cache);
        }

        this.currentMap = activeMap;
    }

    private void clearSelections() {

        if (currentMap != null) {

            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>) currentMap
                    .getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
            for (Entry<String, IGeoSelectionEntry> entry : selectionBag.entrySet()) {
                String context = entry.getKey();
                IGeoSelectionEntry selectionEntry = entry.getValue();

                IGeoSelection oldSelection = selectionEntry.getSelection();
                GeoSelectionChangedEvent<Map> event = new GeoSelectionChangedEvent<>(context,
                        currentMap, oldSelection, null);

                fireSelectionChanged(event);

            }

            cachedSelections = null;
            currentMap = null;
            latestGeoSelection = null;

        }
    }

    protected Map getCurrentMap() {
        return currentMap;
    }

    /**
     * @param context
     * @param selection
     */
    @Override
    public void setSelection(String context, IGeoSelection selection) {

        if (currentMap == null && !initialized) {
            /**
             * We need to initialize since during creation of manager the workbench was not ready at
             * that moment.
             */

            synchronized (this) {
                if (!initialized) {
                    initialize();
                }
            }

        }

        if (currentMap != null) {

            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>) currentMap
                    .getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
            GeoSelectionEntry entry = (GeoSelectionEntry) selectionBag.get(context);

            IGeoSelection oldSelection = null;

            if (entry == null && selection != null) {
                /*
                 * We need to create new IGeoSelectionEntry and reinitialize cache selection data
                 * structure.
                 */
                entry = new GeoSelectionEntry(context);
                entry.setSelection(selection);

                synchronized (this) {
                    selectionBag.put(context, entry);
                    ArrayList<IGeoSelectionEntry> cache = new ArrayList<>(selectionBag.values());

                    cachedSelections = Collections.unmodifiableList(cache);
                }

            } else if (entry != null && selection != null) {

                oldSelection = entry.getSelection();
                entry.setSelection(selection);

            } else if (entry != null && selection == null) {

                oldSelection = entry.getSelection();
                synchronized (this) {
                    selectionBag.remove(context);
                    ArrayList<IGeoSelectionEntry> cache = new ArrayList<>(selectionBag.values());
                    cachedSelections = Collections.unmodifiableList(cache);
                }
            }

            latestGeoSelection = (selection != null) ? entry : null;

            GeoSelectionChangedEvent<Map> event = new GeoSelectionChangedEvent<>(context,
                    currentMap, oldSelection, selection);

            fireSelectionChanged(event);

        } else {
            LOGGER.info(
                    "PlatformGeoSelectionManager: there is no active Map or manager is not initialized properly"); //$NON-NLS-1$
        }
    }

    /**
     * Gets selection from current map by context.
     *
     * @param context
     * @return
     */
    @Override
    public IGeoSelection getSelection(String context) {
        if (currentMap != null) {

            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>) currentMap
                    .getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);

            IGeoSelectionEntry entry = selectionBag.get(context);
            if (entry == null)
                return null;

            IGeoSelection selection = entry.getSelection();
            return selection;
            // Object selectionObj = selectionBag.get(context);
            // if (selectionObj != null && selectionObj instanceof IGeoSelection) {
            // IGeoSelection selection = (IGeoSelection) selectionObj;
            // return selection;
            // }
        }
        return null;
    }

    /**
     * Fires <code>GeoSelectionChangedEvent</code> to listeners.
     *
     * @param event
     */
    protected void fireSelectionChanged(GeoSelectionChangedEvent event) {

        LOGGER.fine(
                "PlatformGeoSelectionManager: new IGeoSelection is set and an event is fired to listeners"); //$NON-NLS-1$

        Object[] listenersArray = listeners.getListeners();

        for (Object listenerObj : listenersArray) {
            try {
                IGeoSelectionChangedListener listener = (IGeoSelectionChangedListener) listenerObj;
                listener.geoSelectionChanged(event);

            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, t.getMessage(), t);
            }
        }

    }

    @Override
    public Iterator<IGeoSelectionEntry> getSelections() {
        Iterator<IGeoSelectionEntry> it;
        synchronized (this) {
            it = cachedSelections.iterator();
        }
        return it;
    }

    class MapEditorPartListener implements IPartListener2 {

        // GeoSelectionManager selectionManager;

        // public GeoSelectionServicePartListener(GeoSelectionManager selectionManager){
        // this.selectionManager = selectionManager;
        // }

        @Override
        public void partActivated(IWorkbenchPartReference partRef) {

            Map map = partRef.getPart(false).getAdapter(Map.class);

            if (map != null) {
                PlatformGeoSelectionManager.this.setCurrentMap(map);
            }
        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
            IWorkbenchPart wbPart= partRef.getPart(false);
            if (wbPart == null) {
                return;
            }

            Map mapReference = wbPart.getAdapter(Map.class);
            if (mapReference == null) {
                return; // its not a Map
            }

            if (!PlatformUI.getWorkbench().isClosing() && mapReference == currentMap) {
                /*
                 * Means that the last Map is being closed and we need to notify selection service
                 * listeners that there is no geoselection available.
                 */
                LOGGER.log(Level.FINE,
                        "The current Map was closed. Notify listeners with NULL geoselection");
                clearSelections();

            }
        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {

        }

    }

    @Override
    public IGeoSelectionEntry getLatestSelection() {
        return latestGeoSelection;
    }

}
