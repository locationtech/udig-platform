package net.refractions.udig.project.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.refractions.udig.project.geoselection.AbstractGeoSelectionManager;
import net.refractions.udig.project.geoselection.GeoSelectionChangedEvent;
import net.refractions.udig.project.geoselection.GeoSelectionEntry;
import net.refractions.udig.project.geoselection.IGeoSelection;
import net.refractions.udig.project.geoselection.IGeoSelectionChangedListener;
import net.refractions.udig.project.geoselection.IGeoSelectionEntry;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapPart;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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
    public static final String ID = "net.refractions.udig.project.ui.platformGeoSelectionManager"; //$NON-NLS-1$
    
    /**
     * Logger.
     */
    public static Logger LOGGER = Logger.getLogger("net.refractions.udig.project.geoselection"); //$NON-NLS-1$

    /**
     * Key for selection bag in Map's blackboard.
     */
    public static final String PLATFORM_SELECTION_BAG = "net.refractions.udig.project.ui.PLATFORM_SELECTION_BAG"; //$NON-NLS-1$
    
    /**
     * Key for cached selection bag in Map's blackboard.
     */
    public static final String PLATFORM_SELECTION_CACHE_KEY = "net.refractions.udig.project.ui.PLATFORM_SELECTION_CACHE"; //$NON-NLS-1$
    
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

    private void initialize(){
        if(!initialized){
            net.refractions.udig.ui.PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    if(ww != null){
                        IWorkbenchPage activePage = ww.getActivePage();
                        if(activePage != null){
                            activePage.addPartListener(partListener);
                            initialized = true;
                            
                            LOGGER.info("PlatformGeoSelectionManager is INITIALIZED.");
                            
                            Map activeMap = ApplicationGISInternal.getActiveMap();
                            if(activeMap != ApplicationGIS.NO_MAP)
                                setCurrentMap(activeMap);
                        }
                    }

                }
            });
        }
    }

    protected void setCurrentMap( Map activeMap ) {
        HashMap<String, IGeoSelectionEntry> selectionBag = ( HashMap<String, IGeoSelectionEntry>)activeMap.getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
        if(selectionBag == null){
            selectionBag = new HashMap<String, IGeoSelectionEntry>();
            activeMap.getBlackBoardInternal().put(PLATFORM_SELECTION_BAG, selectionBag);
            
            cachedSelections = Collections.EMPTY_LIST;
        }else{
            
            ArrayList<IGeoSelectionEntry> cache = new ArrayList<IGeoSelectionEntry>(selectionBag.values());
            cachedSelections = Collections.unmodifiableList(cache);
        }
        
        this.currentMap = activeMap;
    }
    
    private void clearSelections(){
        
        if(currentMap != null){

            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>)currentMap.getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
            for( Entry<String, IGeoSelectionEntry> entry : selectionBag.entrySet() ) {
                String context = entry.getKey();
                IGeoSelectionEntry selectionEntry = entry.getValue();
                
                IGeoSelection oldSelection = selectionEntry.getSelection();
                GeoSelectionChangedEvent<Map> event = new GeoSelectionChangedEvent<Map>(context,
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
    public void setSelection( String context, IGeoSelection selection ) {

        if(currentMap == null && !initialized){
            /**
             * We need to initialize since during creation of manager the workbench
             * was not ready at that moment.
             */

            synchronized(this){
                if(!initialized){
                    initialize();
                }
            }

        }

        if (currentMap != null) {

            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>)currentMap.getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
            GeoSelectionEntry entry = (GeoSelectionEntry)selectionBag.get(context);
            
            IGeoSelection oldSelection = null;
            
            if(entry == null && selection != null){
                /*
                 * We need to create new IGeoSelectionEntry and reinitialize cache
                 * selection data structure.
                 */
                entry = new GeoSelectionEntry(context);
                entry.setSelection(selection);

                synchronized(this){
                    selectionBag.put(context, entry);
                    ArrayList<IGeoSelectionEntry> cache = new ArrayList<IGeoSelectionEntry>(selectionBag.values());

                    cachedSelections = Collections.unmodifiableList(cache);
                }

            }else if(entry != null && selection != null){
                
                oldSelection = entry.getSelection();
                entry.setSelection(selection);
                
            }else if(entry != null && selection == null){

                oldSelection = entry.getSelection();
                synchronized(this){
                    selectionBag.remove(context);
                    ArrayList<IGeoSelectionEntry> cache = new ArrayList<IGeoSelectionEntry>(selectionBag.values());
                    cachedSelections = Collections.unmodifiableList(cache);
                }
            }

            latestGeoSelection = (selection != null) ? entry : null;

            GeoSelectionChangedEvent<Map> event = new GeoSelectionChangedEvent<Map>(context,
                    currentMap, oldSelection, selection);

            fireSelectionChanged(event);

        }else{
            LOGGER.info("PlatformGeoSelectionManager: there is no active Map or manager is not initialized properly"); //$NON-NLS-1$
        }
    }

    /**
     * Gets selection from current map by context.
     * 
     * @param context
     * @return
     */
    public IGeoSelection getSelection( String context ) {
        if (currentMap != null) {
            
            HashMap<String, IGeoSelectionEntry> selectionBag = (HashMap<String, IGeoSelectionEntry>)currentMap.getBlackBoardInternal().get(PLATFORM_SELECTION_BAG);
            
            IGeoSelectionEntry entry = selectionBag.get(context);
            if(entry == null)
                return null;
            
            IGeoSelection selection = entry.getSelection();
            return selection;
//            Object selectionObj = selectionBag.get(context);
//            if (selectionObj != null && selectionObj instanceof IGeoSelection) {
//                IGeoSelection selection = (IGeoSelection) selectionObj;
//                return selection;
//            }
        }
        return null;
    }

    /**
     * Fires <code>GeoSelectionChangedEvent</code> to listeners.
     * 
     * @param event
     */
    protected void fireSelectionChanged( GeoSelectionChangedEvent event ) {

        LOGGER.fine("PlatformGeoSelectionManager: new IGeoSelection is set and an event is fired to listeners"); //$NON-NLS-1$

        Object[] listenersArray = listeners.getListeners();

        for( Object listenerObj : listenersArray ) {
            try {
                IGeoSelectionChangedListener listener = (IGeoSelectionChangedListener) listenerObj;
                listener.geoSelectionChanged(event);
                
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, t.getMessage(), t);
            }
        }

    }
    

    /* (non-Javadoc)
     * @see net.refractions.udig.project.geoselection.IGeoSelectionManager#getSelections()
     */
    public Iterator<IGeoSelectionEntry> getSelections() {
        Iterator<IGeoSelectionEntry> it;
        synchronized(this){
            it =  cachedSelections.iterator();
        }
        return it;
    }

    class MapEditorPartListener implements IPartListener2 {

        // GeoSelectionManager selectionManager;

        // public GeoSelectionServicePartListener(GeoSelectionManager selectionManager){
        // this.selectionManager = selectionManager;
        // }

        public void partActivated( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) instanceof MapEditor) {
                Map activeMap = ((MapPart) partRef.getPart(false)).getMap();
                PlatformGeoSelectionManager.this.setCurrentMap(activeMap);
            }
        }

        public void partBroughtToTop( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

        public void partClosed( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) instanceof MapEditor) {
                System.out.println("MapEditor is closed");

                if(!PlatformUI.getWorkbench().isClosing()){

                    IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().getActiveEditor();
                    
                    /*
                     * Means that the last MapEditor is being closed and we need
                     * to notify selection service listeners
                     * that there is no geoselection available.
                     */
                    if(part == null){
                        LOGGER.log(Level.FINE, "The last MapEditor was closed. Notify listeners with NULL geoselection");
                        clearSelections();
                    }
                }

            }

        }

        public void partDeactivated( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

        public void partHidden( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

        public void partInputChanged( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

        public void partOpened( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

        public void partVisible( IWorkbenchPartReference partRef ) {
            // TODO Auto-generated method stub

        }

    }

    public IGeoSelectionEntry getLatestSelection() {
        return latestGeoSelection;
    }

}
