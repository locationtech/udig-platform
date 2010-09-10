package net.refractions.udig.catalog.internal.wmt.wmtsource;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.wmt.WMTGeoResource;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.WMTServiceExtension;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.NASAControl;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.WMTWizardTreeItemData;
import net.refractions.udig.catalog.internal.wmt.ui.wizard.controls.WMTWizardControl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * Manages the access on the NASA TiledGroups configuration file 
 * see http://onearth.jpl.nasa.gov/wms.cgi?request=GetTileService
 * and http://onearth.jpl.nasa.gov/tiled.html
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class NASASourceManager {
    private static final String TILESERVICE_FILE = "NASA-GetTileService.xml"; //$NON-NLS-1$
    
    private Element tiledPatterns = null;
    private WMTServiceExtension serviceExtension;
    
    private static NASASourceManager instance = null;    
    public static synchronized NASASourceManager getInstance() {
        if (instance == null) {
            instance = new NASASourceManager();
        }
        
        return instance;
    }
    
    private NASASourceManager() {
        serviceExtension = new WMTServiceExtension();
        
        try{
            // open file
            URL url = NASASource.class.getResource(TILESERVICE_FILE);
            
            SAXBuilder builder = new SAXBuilder(false); 
            URLConnection connection = url.openConnection();            
            Document dom = builder.build(connection.getInputStream());
            
            Element root = dom.getRootElement();                    
            tiledPatterns = root.getChild("TiledPatterns"); //$NON-NLS-1$
            
        } catch(Exception exc) {
            WMTPlugin.log("[NASASourceManager] Loading tileservice-file failed: " + TILESERVICE_FILE, exc); //$NON-NLS-1$
            tiledPatterns = null;
        }
    }
    
    /**
     * Returns the prefix for the request: http://wms.jpl.nasa.gov/wms.cgi?
     *
     * @return
     */
    public String getBaseUrl() {
        Element onlineResource = tiledPatterns.getChild("OnlineResource"); //$NON-NLS-1$
        Namespace xlink = onlineResource.getNamespace("xlink"); //$NON-NLS-1$
        Attribute href = onlineResource.getAttribute("href", xlink); //$NON-NLS-1$        
        String baseUrl = href.getValue();
        
        return baseUrl;
    }
    
    //region Build TreeItem for Wizard
    public void buildWizardTree(TreeItem treeItem) {
        NASAControl controlFactory = new NASAControl();
        try {
            List<?> tiledGroups = tiledPatterns.getChildren("TiledGroup"); //$NON-NLS-1$
            
            WMTService service = serviceExtension.createService(NASASource.class);
            List<IGeoResource> geoResources = service.emptyResourcesList(null);
            geoResources.clear();
            

            WMTWizardTreeItemData data = new WMTWizardTreeItemData(service, controlFactory);
            treeItem.setData(data);
            
            buildWizardTreeFromTiledGroups(service, geoResources, treeItem, tiledGroups, 
                    "", controlFactory); //$NON-NLS-1$
        } catch(Exception exc) {
            WMTPlugin.log("[NASASourceManager.buildWizardTree] Failed: ", exc); //$NON-NLS-1$
        }
    }
    
    private void buildWizardTreeFromTiledGroups(WMTService service, List<IGeoResource> geoResources, 
            TreeItem treeItem, List<?> tiledGroups, String groupNames, WMTWizardControl controlFactory) {
        for(Object obj : tiledGroups) {
            if (obj instanceof Element) {
                Element tiledGroup = (Element) obj;
                
                String newGroupName = tiledGroup.getChildText("Name"); //$NON-NLS-1$
                String newGroupNames = getConcatenatedGroupName(groupNames, newGroupName);
                
                List<?> newTiledGroups = tiledGroup.getChildren("TiledGroup"); //$NON-NLS-1$
                
                // if there are no sub tile-groups
                if (newTiledGroups.isEmpty()) {
                    TreeItem newTreeItem = new TreeItem(treeItem, SWT.NONE);
                    
                    WMTGeoResource geoResource = new WMTGeoResource(service, newGroupNames);
                    geoResources.add(geoResource);
                    
                    newTreeItem.setText(newGroupName);
                    
                    WMTWizardTreeItemData data = new WMTWizardTreeItemData(geoResource, controlFactory);
                    newTreeItem.setData(data);
                    //newTreeItem.setData(geoResource);
                } else {
                    TreeItem newTreeItem = new TreeItem(treeItem, SWT.NONE);
                    newTreeItem.setText(newGroupName);
                    
                    WMTWizardTreeItemData data = new WMTWizardTreeItemData(null, controlFactory);
                    newTreeItem.setData(data);
                    
                    buildWizardTreeFromTiledGroups(service, geoResources, 
                            newTreeItem, newTiledGroups, newGroupNames, controlFactory);
                }
            }
        }
    }
    //endregion
    
    //region Build geo-resources list for service
    public void buildGeoResources(WMTService service, List<IGeoResource> geoResources) {
        geoResources.clear();    
        
        try {
            List<?> tiledGroups = tiledPatterns.getChildren("TiledGroup"); //$NON-NLS-1$    
            
            buildGeoResourcesFromTiledGroups(service, geoResources, tiledGroups, ""); //$NON-NLS-1$
        } catch(Exception exc) {
            WMTPlugin.log("[NASASourceManager.buildGeoResources] Failed: ", exc); //$NON-NLS-1$
        }       
    }
    
    private void buildGeoResourcesFromTiledGroups(WMTService service, List<IGeoResource> geoResources, List<?> tiledGroups, String groupNames) {
        for(Object obj : tiledGroups) {
            if (obj instanceof Element) {
                Element tiledGroup = (Element) obj;
                
                String newGroupName = tiledGroup.getChildText("Name"); //$NON-NLS-1$
                String newGroupNames = getConcatenatedGroupName(groupNames, newGroupName);
                
                List<?> newTiledGroups = tiledGroup.getChildren("TiledGroup"); //$NON-NLS-1$
                
                // if there are no sub tile-groups
                if (newTiledGroups.isEmpty()) {
                    
                    WMTGeoResource geoResource = new WMTGeoResource(service, newGroupNames);
                    geoResources.add(geoResource);
                } else {                    
                    buildGeoResourcesFromTiledGroups(service, geoResources, 
                            newTiledGroups, newGroupNames);
                }
            }
        }        
    }
    //endregion

    //region Load the XML TiledGroup element by a given name
    public Element getTiledGroup(String tileGroupName) {        
        List<?> tiledGroups = tiledPatterns.getChildren("TiledGroup"); //$NON-NLS-1$
        
        return searchTileGroup(tileGroupName, "", tiledGroups); //$NON-NLS-1$
    }
    
    private Element searchTileGroup(String groupToSearchFor, String groupNames, List<?> tiledGroups) {
        
        for(Object obj : tiledGroups) {
            if (obj instanceof Element) {
                Element tiledGroup = (Element) obj;
                
                String newGroupName = tiledGroup.getChildText("Name"); //$NON-NLS-1$
                String newGroupNames = getConcatenatedGroupName(groupNames, newGroupName);
                
                if (groupToSearchFor.startsWith(newGroupNames)) {
                    List<?> newTiledGroups = tiledGroup.getChildren("TiledGroup"); //$NON-NLS-1$
                    
                    // if there are no sub tile-groups
                    if (newTiledGroups.isEmpty()) {
                        // check if we have found the right tile-group, if not continue
                        if (groupToSearchFor.equals(newGroupNames)) {
                            return tiledGroup;
                        } else {
                            continue;
                        }
                    } else {
                        // search in sub tile-groups
                        Element foundTileGroup = searchTileGroup(groupToSearchFor, newGroupNames, newTiledGroups);
                        
                        if (foundTileGroup != null) {
                            return foundTileGroup;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    //endregion
    
    /**
     * The names for cascaded TileGroups are build by concatenating the several
     * group names.
     */
    private String getConcatenatedGroupName(String groupNames, String newGroupName) {
        /* Replace separator characters
         * - : separates TiledGroup names
         * # : separates the service id from the geo-resource id
         */
        newGroupName = newGroupName.replace('-', ' ').replace('#', ' ');
        
        if (groupNames.isEmpty()) {
            return newGroupName;
        } else {
            return groupNames + "-" + newGroupName; //$NON-NLS-1$
        }
    }
}
