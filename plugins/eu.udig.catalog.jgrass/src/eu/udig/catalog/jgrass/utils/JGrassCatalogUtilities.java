/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.memory.MemoryServiceExtensionImpl;
import net.refractions.udig.catalog.memory.internal.MemoryServiceImpl;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.hydrologis.jgrass.libs.map.JGrassRasterMapReader;
import eu.hydrologis.jgrass.libs.map.JGrassRasterMapWriter;
import eu.hydrologis.jgrass.libs.map.RasterData;
import eu.hydrologis.jgrass.libs.region.JGrassRegion;
import eu.hydrologis.jgrass.libs.utils.JGrassConstants;
import eu.hydrologis.jgrass.libs.utils.dialogs.ProblemDialogs;
import eu.hydrologis.jgrass.libs.utils.monitor.IProgressMonitorJGrass;
import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.core.JGrassService;

/**
 * Some jgrass catalog related helper methods
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGrassCatalogUtilities {

    public static final String GRASSMAPSET = "grassmapset";
    public static final String GRASSLOCATION = "grasslocation";
    public static final String JGRASS_WORKSPACE_FILENAME = "wks.jgrass";

    /**
     * Extract mapset path and mapname from a georesource containing a JGrassMapGeoResource
     * 
     * @param resource
     * @return a String array with mapset path and map name
     */
    public synchronized static String[] getMapsetpathAndMapnameFromJGrassMapGeoResource(
            IGeoResource resource ) {
        String[] mapsetPathAndMapName = new String[2];

        // check that the underlying resource is a propertyservice
        if (resource == null || !resource.canResolve(JGrassMapGeoResource.class)) {
            return null;
        }
        JGrassMapGeoResource jg = null;
        try {
            jg = resource.resolve(JGrassMapGeoResource.class, null);
            String tmp = jg.getInfo(null).getDescription();
            if (!tmp.equals(JGrassConstants.GRASSBINARYRASTERMAP))
                return null;
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getMapsetpathAndMapnameFromJGrassMapGeoResource", e); //$NON-NLS-1$

            e.printStackTrace();
            return null;
        }

        JGrassMapsetGeoResource mapsetResource = null;
        try {
            mapsetResource = (JGrassMapsetGeoResource) jg.parent(null);
            mapsetPathAndMapName[0] = mapsetResource.getFile().getAbsolutePath();
            mapsetPathAndMapName[1] = jg.getInfo(null).getTitle();
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getMapsetpathAndMapnameFromJGrassMapGeoResource", e); //$NON-NLS-1$
            e.printStackTrace();
        }
        return mapsetPathAndMapName;
    }

    /**
     * @param resource
     * @return the full path to the resource
     */
    public synchronized static String getDirectPathFromJGrassMapGeoResource( IGeoResource resource ) {
        String mapPath = null;
        // check that the underlying resource is a propertyservice
        if (resource == null || !resource.canResolve(JGrassMapGeoResource.class)) {
            return null;
        }
        JGrassMapGeoResource jg = null;
        try {
            jg = resource.resolve(JGrassMapGeoResource.class, null);
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getDirectPathFromJGrassMapGeoResource", e); //$NON-NLS-1$

            e.printStackTrace();
            return null;
        }

        mapPath = URLUtils.urlToFile(jg.getIdentifier()).getAbsolutePath();
        return mapPath;
    }

    /**
     * @param outputFile
     * @param addToCatalog
     * @param progressMonitor
     */
    public static synchronized void addServiceToCatalog( String serviceFile,
            IProgressMonitor progressMonitor ) {
        try {
            URL fileUrl = new File(serviceFile).toURI().toURL();
            // add the service to the catalog and map
            IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            List<IService> services = sFactory.createService(fileUrl);
            for( IService service : services ) {
                catalog.add(service);
            }
        } catch (Exception e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#addServiceToCatalog", e); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * Reload the service, to which the location refers to. This is ment to be used when new maps
     * are added inside a mapset and the catalog doesn't care to see them. So the tree has to be
     * reloaded.
     * 
     * @param locationPath the path to the affected location
     * @param monitor the progress monitor
     */
    public static void refreshJGrassService( String locationPath, final IProgressMonitor monitor ) {

        System.out.println("Lock on locationPath = " + Thread.holdsLock(locationPath));
        synchronized (locationPath) {

            /*
             * if the catalog is active, refresh the location in the catalog window
             */
            ID id = null;
            if (JGrassPlugin.getDefault() != null) {
                File locationFile = new File(locationPath);
                try {
                    id = new ID(locationFile.toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                return;
            }
            /*
             * test code to make the catalog understand that the map should be added
             */
            final ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            final JGrassService originalJGrassService = catalog.getById(JGrassService.class, id,
                    monitor);

            /*
             * create the same service
             */
            if (originalJGrassService == null)
                return;
            final URL ID = originalJGrassService.getIdentifier();
            Map<String, Serializable> connectionParams = originalJGrassService
                    .getConnectionParams();
            IServiceFactory locator = CatalogPlugin.getDefault().getServiceFactory();
            final List<IService> rereadService = locator.acquire(ID, connectionParams);

            /*
             * replace the service
             */
            if (rereadService.size() > 0) {
                Runnable refreshCatalogRunner = new Runnable(){
                    public void run() {
                        final IService newJGrassService = rereadService.get(0);
                        catalog.remove(originalJGrassService);
                        catalog.add(newJGrassService);
                    }
                };

                new Thread(refreshCatalogRunner).start();
            }
        }
    }

    /**
     * Find a resource from its url and add it to the map
     * 
     * @param url the resources url
     * @param progressMonitor
     * @param addPosition the position into which to put it, 0 is most lower
     * @param map the map to which to add the resource to
     */
    public static void addResourceFromUrlToMap( URL url, IProgressMonitor progressMonitor,
            int addPosition, IMap map ) {

        synchronized (url) {
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            // first search the local catalog.
            List<IResolve> matches = catalog.find(url, new SubProgressMonitor(progressMonitor, 2));

            for( IResolve resolve : matches ) {
                List<IResolve> members = null;
                try {
                    List<IGeoResource> geoResources = new ArrayList<IGeoResource>();
                    members = resolve.members(progressMonitor);
                    if (members.size() < 1 && resolve.canResolve(IGeoResource.class)) {
                        // if it is a map, it has no members
                        geoResources.add(resolve.resolve(IGeoResource.class, progressMonitor));
                    } else if (members.get(0).canResolve(IGeoResource.class)) {
                        for( IResolve tmp : members ) {
                            IGeoResource finalResolve = tmp.resolve(IGeoResource.class,
                                    progressMonitor);
                            geoResources.add(finalResolve);
                        }
                    }
                    List< ? extends ILayer> addedLayers = ApplicationGIS.addLayersToMap(map,
                            geoResources, addPosition);
                    if (addedLayers.size() == 0) {
                        System.out.println("strange");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param monitor
     * @param mapsetResource
     * @param type
     * @return the name of the added map
     */
    public static synchronized String importMapForType( IProgressMonitorJGrass monitor,
            JGrassMapsetGeoResource mapsetResource, String type, final double[] defaultNovalue,
            final String[] selpath ) {
        try {

            File mapsetFile = mapsetResource.getFile();

            final boolean[] doExit = {false};
            final boolean[] goGo = {false};

            // create a thread and inside do a syncExec
            Display.getDefault().syncExec(new Runnable(){

                public void run() {
                    FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(),
                            SWT.OPEN);

                    selpath[0] = fileDialog.open();
                    if (selpath == null || selpath[0] == null || selpath[0].length() < 1) {
                        doExit[0] = true;
                        return;
                    }

                    InputDialog iDialog = new InputDialog(
                            Display.getDefault().getActiveShell(),
                            "Novalue definition",
                            "Please enter the number to be considered a novalue ('*' is automatically dealth with as novalue)",
                            "-9999.0", null);
                    iDialog.open();
                    String ret = iDialog.getValue();
                    if (ret != null && ret.length() > 0) {
                        try {
                            double tmp = Double.parseDouble(ret);
                            defaultNovalue[0] = tmp;
                        } catch (Exception e) {
                        }
                    } else {
                        doExit[0] = true;
                    }
                    goGo[0] = true;
                }
            });

            if (doExit[0]) {
                monitor.done();
                return null;
            }

            /*
             * import the file
             */
            JGrassRasterMapReader mr = new JGrassRasterMapReader.BuilderFromMapPath(null,
                    selpath[0]).maptype(type).monitor(monitor).build();
            RasterData data = null;
            if (mr.open() && mr.hasMoreData()) {
                data = mr.getNextData();
            }
            mr.close();
            
            if (data == null) {
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                        ProblemDialogs.errorDialog(null,
                                "Error occurred in reading the map. Check the map type.", true);
                    }
                });
                return null;
            }
            
            for( int i = 0; i < data.getRows(); i++ ) {
                for( int j = 0; j < data.getCols(); j++ ) {
                    if (data.getValueAt(i, j) == defaultNovalue[0]) {
                        data.setValueAt(i, j, JGrassConstants.doubleNovalue);
                    }
                }
            }
            
            File mapFile = new File(selpath[0]);
            String mapName = mapFile.getName();
            String mapsetName = mapsetFile.getName();
            String locationPath = mapsetFile.getParent();
            JGrassRasterMapWriter mw = new JGrassRasterMapWriter(mr.getReader().getMapWindow(),
                    mapName, mapsetName, locationPath, defaultNovalue[0], monitor);
            if (mw.open()) {
                mw.write(data);
                mw.close();
            }

            return mapName;

        } catch (Exception e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#importMapForType", e); //$NON-NLS-1$
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param monitor
     * @param mapResource
     * @param type
     * @param selpath
     */
    public static synchronized void exportMapToType( IProgressMonitorJGrass monitor,
            JGrassMapGeoResource mapResource, String type, final String[] selpath ) {

        try {

            // create a thread and inside do a syncExec
            Display.getDefault().syncExec(new Runnable(){
                public void run() {
                    FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(),
                            SWT.SAVE);
                    selpath[0] = fileDialog.open();
                }
            });
            if (selpath == null || selpath[0].length() < 1) {
                return;
            }

            JGrassRegion wind = mapResource.getFileWindow();
            String mapPath = mapResource.getMapFile().getAbsolutePath();
            JGrassRasterMapReader reader = new JGrassRasterMapReader.BuilderFromMapPath(wind,
                    mapPath).monitor(monitor).build();

            RasterData data = null;
            if (reader.open() && reader.hasMoreData()) {
                data = reader.getNextData();
            }
            reader.close();
            if (data == null) {
                ProblemDialogs.errorDialog(null,
                        "Error occurred in reading the map. Check the map type.", true);
                return;
            }

            JGrassRasterMapWriter mw = new JGrassRasterMapWriter(wind, selpath[0], -9999.0, type,
                    monitor);

            if (mw.open()) {
                mw.write(data);
                mw.close();
            } else {
                ProblemDialogs.errorDialog(null,
                        "Error occurred writing the map. Map could not be written.", true);
                return;
            }

            File mapFile = new File(selpath[0]);
            ProblemDialogs.infoDialog(null, "Map successfully written to "
                    + mapFile.getAbsolutePath(), true);
        } catch (Exception e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#exportMapToType", e); //$NON-NLS-1$
            e.printStackTrace();
        }
    }
    /**
     * Remove a memory datastore from the catalog, since the update is not possible (Caused by:
     * java.lang.UnsupportedOperationException: Schema modification not supported)
     * 
     * @param typeName the name of the type to remove, if it is there
     */
    public static synchronized void removeMemoryServiceByTypeName( String typeName ) {
        MemoryServiceImpl service = null;
        try {
            List< ? extends IResolve> members = CatalogPlugin.getDefault().getLocalCatalog()
                    .members(new NullProgressMonitor());
            for( IResolve resolve : members ) {
                if (resolve instanceof MemoryServiceImpl) {
                    if (URLUtils.urlEquals(resolve.getIdentifier(), MemoryServiceExtensionImpl.URL,
                            true)) {
                        service = (MemoryServiceImpl) resolve;
                        break;
                    }
                }
            }
            if (service == null)
                return;
            MemoryDataStore ds = service.resolve(MemoryDataStore.class, new NullProgressMonitor());
            if (Arrays.asList(ds.getTypeNames()).contains(typeName)) {
                CatalogPlugin.getDefault().getLocalCatalog().remove(service);
            }
        } catch (IOException e) {
            CatalogPlugin.log("Error finding services", e); //$NON-NLS-1$
        }
    }

    /**
     * @param locationPath the path to the location to be created
     * @return true if no problems occurred
     */
    public static boolean createLocation( String locationPath, CoordinateReferenceSystem crs,
            JGrassRegion window ) {
        /* Create root directory for GRASS location. */
        if (!(new File(locationPath).mkdirs()))
            return false;

        /* create the jgrass workspace file */
        String jgFile = locationPath + File.separator + JGRASS_WORKSPACE_FILENAME; //$NON-NLS-1$
        File jgF = new File(jgFile);
        try {
            jgF.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createMapset(locationPath, JGrassConstants.PERMANENT_MAPSET, crs, window);
    }

    public static boolean createMapset( String locationPath, String mapset,
            CoordinateReferenceSystem crs, JGrassRegion window ) {
        String path = locationPath + File.separator + mapset;

        /* Create mapset directory */
        if (!(new File(path).mkdirs()))
            return false;

        if (mapset.equals(JGrassConstants.PERMANENT_MAPSET)) {
            /* Create blank DEFAULT_WIND and WIND files */
            try {
                if (window != null) {
                    JGrassRegion.writeWINDToMapset(path, window);
                    JGrassRegion.writeDEFAULTWINDToLocation(locationPath, window);
                } else {
                    // create blank windows
                    BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator
                            + JGrassConstants.DEFAULT_WIND));
                    out.write(JGrassRegion.BLACKBOARD_KEY);
                    out.close();

                    out = new BufferedWriter(new FileWriter(path + File.separator
                            + JGrassConstants.WIND));
                    out.write(JGrassRegion.BLANK_REGION);
                    out.close();
                }

                /* Create projection files */
                if (crs != null) {
                    // FIXME create GRASS proj files

                    BufferedWriter prjOut = new BufferedWriter(new FileWriter(path + File.separator
                            + JGrassConstants.PROJ_WKT));
                    prjOut.write(crs.toWKT());
                    prjOut.close();

                }
            } catch (IOException e) {
                JGrassPlugin
                        .log(
                                "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#createMapset",
                                e);
                e.printStackTrace();
                return false;
            }

        } else {
            /* Copy WIND file from PERMANENT mapset of this location */
            try {
                BufferedReader in = new BufferedReader(new FileReader(locationPath + File.separator
                        + JGrassConstants.PERMANENT_MAPSET + File.separator
                        + JGrassConstants.DEFAULT_WIND));
                BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator
                        + JGrassConstants.WIND));
                String line;
                while( (line = in.readLine()) != null ) {
                    out.write(line);
                    out.write("\n");
                }
                out.close();
                in.close();
            } catch (IOException e) {
                JGrassPlugin
                        .log(
                                "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#createMapset",
                                e);
                e.printStackTrace();
                return false;
            }
        }

        /* Create point/site directories */
        if (!(new File(path + File.separator + JGrassConstants.SITE_LISTS).mkdirs()))
            return false;

        /* Create raster directories */
        if (!(new File(path + File.separator + JGrassConstants.FCELL).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.CELL).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.CELLHD).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.CATS).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.COLR).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.CELL_MISC).mkdirs()))
            return false;

        /* Create vector directories */
        if (!(new File(path + File.separator + JGrassConstants.DIG).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.DIG_ATTS).mkdirs()))
            return false;
        if (!(new File(path + File.separator + JGrassConstants.DIG_CATS).mkdirs()))
            return false;

        if (!createJGrassFolders(path))
            return false;

        return true;
    }

    /**
     * Creates additional folder used with certain JGrass commands
     * 
     * @param mapsetpath - path to the mapset
     * @return
     */
    public static boolean createJGrassFolders( String mapsetpath ) {
        /* Create jgrass directories */
        if (!(new File(mapsetpath + File.separator + JGrassConstants.GRASSASCIIRASTER).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.GRASSASCIIRASTER).mkdirs()))
                return false;
        if (!(new File(mapsetpath + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER)
                .exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER)
                    .mkdirs()))
                return false;
        if (!(new File(mapsetpath + File.separator + JGrassConstants.ESRIASCIIRASTER).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.ESRIASCIIRASTER).mkdirs()))
                return false;

        if (!(new File(mapsetpath + File.separator + JGrassConstants.HORTON_MACHINE_PATH).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.HORTON_MACHINE_PATH)
                    .mkdirs()))
                return false;

        return true;
    }

    /**
     * @param locationPath
     * @return the crs of the supplied location
     */
    public static synchronized CoordinateReferenceSystem getLocationCrs( String locationPath ) {
        CoordinateReferenceSystem readCrs = null;
        String projWtkFilePath;
        try {
            projWtkFilePath = locationPath + File.separator + JGrassConstants.PERMANENT_MAPSET
                    + File.separator + JGrassConstants.PROJ_WKT;
            File projWtkFile = new File(projWtkFilePath);
            if (projWtkFile.exists()) {

                BufferedReader crsReader = new BufferedReader(new FileReader(projWtkFile));
                StringBuffer wtkString = new StringBuffer();
                String line = null;
                while( (line = crsReader.readLine()) != null ) {
                    wtkString.append(line.trim());
                }

                readCrs = CRS.parseWKT(wtkString.toString());
            }

        } catch (Exception e1) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassMapsetGeoResource#getJGrassCrs", e1); //$NON-NLS-1$

            e1.printStackTrace();
        }
        return readCrs;
    }

    /**
     * Returns a mapgraphics layer by its class.
     * 
     * @param theClass the class of the mapgraphics.
     * @return the layer or null, if none was found (not visible or not existing).
     */
    public static ILayer getMapgraphicLayerByClass( Class< ? > theClass ) {

        try {
            List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog().find(
                    MapGraphicService.SERVICE_URL, null);
            List<IResolve> members = mapgraphics.get(0).members(null);
            for( IResolve resolve : members ) {
                if (resolve.canResolve(theClass)) {
                    IGeoResource resolve2 = resolve.resolve(IGeoResource.class, null);
                    String resName = resolve2.getInfo(new NullProgressMonitor()).getName();

                    List<ILayer> mapLayers = ApplicationGIS.getActiveMap().getMapLayers();
                    for( ILayer layer : mapLayers ) {
                        if (layer.getName().trim().equals(resName.trim())) {
                            return layer;
                        }
                    }
                }
            }
        } catch (IOException e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getMapgraphicLayerByClass", e); //$NON-NLS-1$
            e.printStackTrace();
            return null;
        }
        return null;

    }

    /**
     * Adds a map to the catalog into the right Mapset.
     * 
     * <p>Note: this doesn't add the file. The file adding has to be done separately</p>
     *
     * @param locationPath the path to the Location folder.
     * @param mapsetName the name of the Mapset into which to put the map.
     * @param mapName the name of the map to add.
     * @param mapType the format of the map to add.
     * @return the resource that has been added.
     */
    public synchronized static JGrassMapGeoResource addMapToCatalog( String locationPath,
            String mapsetName, String mapName, String mapType ) {
        // URL mapsetId = JGrassMapsetGeoResource.createId(locationPath, mapsetName);

        JGrassMapsetGeoResource mapset = null;
        try {
            File locationFile = new File(locationPath);

            ID locationId = new ID(locationFile.toURI().toURL());
            URL mapsetUrl = JGrassMapsetGeoResource.createId(locationFile.getAbsolutePath(),
                    mapsetName);
            ID mapsetId = new ID(mapsetUrl);
            ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
            mapset = localCatalog.getById(JGrassMapsetGeoResource.class, mapsetId, ProgressManager
                    .instance().get());
            if (mapset == null) {
                // try with the service
                // URL locationId = JGrassService.createId(locationPath);
                JGrassService locationService = localCatalog.getById(JGrassService.class,
                        locationId, ProgressManager.instance().get());
                mapset = locationService.getMapsetGeoresourceByName(mapsetName);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while adding the map to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID,
                    e);
        }
        if (mapset == null)
            return null;
        return mapset.addMap(mapName, mapType);
    }

    /**
     * Remove a map from the mapset in the catalog.
     * 
     * <p>Note: this doesn't remove the file. The file removal has to be done separately</p>
     * 
     * @param locationPath the path to the Location folder.
     * @param mapsetName the name of the Mapset from which to remove the map.
     * @param mapName the name of the map to remove.
     * @param mapType the format of the map to remove.
     */
    public synchronized static void removeMapFromCatalog( String locationPath, String mapsetName,
            String mapName, String mapType ) {
        // URL mapsetId = JGrassMapsetGeoResource.createId(locationPath, mapsetName);
        File locationFile = new File(locationPath);
        URL mapsetUrl = JGrassMapsetGeoResource
                .createId(locationFile.getAbsolutePath(), mapsetName);
        ID mapsetId = new ID(mapsetUrl);

        JGrassMapsetGeoResource mapset = CatalogPlugin.getDefault().getLocalCatalog().getById(
                JGrassMapsetGeoResource.class, mapsetId, ProgressManager.instance().get());
        mapset.removeMap(mapName, mapType);
    }

    /**
     * Add a mapset to a Location in the catalog.
     *
     * <p>Note: this doesn't add the file. The file adding has to be done separately</p>
     *      
     * @param locationPath path to the location to which the mapset has to be added.
     * @param mapsetName the name of the mapset to add
     * @return the added {@linkplain JGrassMapsetGeoResource mapset}
     */
    public synchronized static JGrassMapsetGeoResource addMapsetToCatalog( String locationPath,
            String mapsetName ) {
        // URL locationId = JGrassService.createId(locationPath);
        File locationFile = new File(locationPath);
        try {
            ID locationId = new ID(locationFile.toURI().toURL());
            JGrassService location = CatalogPlugin.getDefault().getLocalCatalog().getById(
                    JGrassService.class, locationId, ProgressManager.instance().get());
            return location.addMapset(mapsetName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while adding the mapset to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID,
                    e);
            return null;
        }
    }

    /**
     * Remove a mapset from a Location in the catalog.
     *
     * <p>Note: this doesn't remove the file. The file removal has to be done separately</p>
     *      
     * @param locationPath path to the location from which the mapset has to be removed.
     * @param mapsetName the name of the mapset to remove
     */
    public synchronized static void removeMapsetFromCatalog( String locationPath, String mapsetName ) {
        // URL locationId = JGrassService.createId(locationPath);
        try {
            File locationFile = new File(locationPath);
            ID locationId = new ID(locationFile.toURI().toURL());
            JGrassService location = CatalogPlugin.getDefault().getLocalCatalog().getById(
                    JGrassService.class, locationId, ProgressManager.instance().get());
            location.removeMapset(mapsetName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while removing the mapset to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID,
                    e);
        }
    }
}
