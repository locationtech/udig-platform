package net.refractions.udig.project.ui;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.geoselection.IGeoSelectionChangedListener;
import net.refractions.udig.project.geoselection.IGeoSelectionManager;
import net.refractions.udig.project.geoselection.IGeoSelectionService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;


/**
 * Default UDIG implementation of IGeoSelectionService with a platform selection manager
 * working for the currently active MapEditor.
 * <p>
 * 
 * @author Vitalus
 *
 */
final public class GeoSelectionService implements IGeoSelectionService {
    
    /**
     * Logger.
     */
    public static Logger LOGGER = Logger.getLogger("net.refractions.udig.project.geoselection"); //$NON-NLS-1$

    
    /**
     * An ID of extension point to add static listeners declaratively.
     */
    public static final String EXTENSION_POINT_ID = "net.refractions.udig.project.geoselection"; //$NON-NLS-1$
    
    
    private static GeoSelectionService instance;

    private IGeoSelectionManager platformSelectionManager = null;

    private HashMap<String, IGeoSelectionManager> registeredManagers = new HashMap<String, IGeoSelectionManager>();

    private GeoSelectionService() {

        platformSelectionManager = new PlatformGeoSelectionManager();
        registerSelectionManager(PlatformGeoSelectionManager.ID, platformSelectionManager);
        try{
            processExtensionPoint();
        }catch(Throwable t){
            t.printStackTrace();
        }
    }

    /**
     * @return
     */
    public IGeoSelectionManager getPlatformSelectionManager() {
//        if (platformSelectionManager == null) {
//            platformSelectionManager = new PlatformGeoSelectionManager();
//            registerSelectionManager(PLATFORM_SELECTION_MANAGER_ID, platformSelectionManager);
//        }
        return platformSelectionManager;
    }

    public static GeoSelectionService getDefault() {
        if (instance == null) {
            instance = new GeoSelectionService();
        }
        return instance;
    }

    /**
     * @param selectionManager
     */
    public void registerSelectionManager(String id,  IGeoSelectionManager selectionManager ) {
        if(registeredManagers.containsKey(id))
            throw new IllegalArgumentException("The IGeoSelectionManager instance with id= "+id+" is already registered");
        
         registeredManagers.put(id, selectionManager);
    }

    /**
     * @param selectionManager
     */
    public void unregisterSelectionManager(String id ) {
        registeredManagers.remove(id);
    }
    
    public IGeoSelectionManager getSelectionManager(String id){
        return registeredManagers.get(id);
    }
    
    private void processExtensionPoint(){
        List<IConfigurationElement> extensionList = ExtensionPointList
        .getExtensionPointList(EXTENSION_POINT_ID);
        
        for( IConfigurationElement element : extensionList ) {
//          IExtension extension = element.getDeclaringExtension();
            String type = element.getName();
            
            
            if(type.equals("geoSelectionListener")){

                try {

                    String id = element.getAttribute("id");
                    String managerId = element.getAttribute("managerId");

                    Object listenerObj = element.createExecutableExtension("class");
                    IGeoSelectionChangedListener listener = (IGeoSelectionChangedListener)listenerObj;

                    IGeoSelectionManager manager = getSelectionManager(managerId);
                    if(manager != null){
                        manager.addListener(listener);
                    }

                } catch (InvalidRegistryObjectException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


        }
    }

}
