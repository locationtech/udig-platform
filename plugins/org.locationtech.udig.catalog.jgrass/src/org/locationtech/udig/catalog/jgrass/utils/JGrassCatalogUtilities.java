/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.jai.RasterFactory;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.iterator.WritableRandomIter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.format.GrassCoverageFormat;
import org.geotools.gce.grassraster.format.GrassCoverageFormatFactory;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassService;
import org.locationtech.udig.catalog.memory.MemoryServiceExtensionImpl;
import org.locationtech.udig.catalog.memory.internal.MemoryServiceImpl;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Some jgrass catalog related helper methods
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGrassCatalogUtilities {

    public static final String GRASSMAPSET = "grassmapset";
    public static final String GRASSLOCATION = "grasslocation";
    public static final String JGRASS_WORKSPACE_FILENAME = "wks.jgrass";

    public static final String NORTH = "NORTH"; //$NON-NLS-1$
    public static final String SOUTH = "SOUTH"; //$NON-NLS-1$
    public static final String WEST = "WEST"; //$NON-NLS-1$
    public static final String EAST = "EAST"; //$NON-NLS-1$
    public static final String XRES = "XRES"; //$NON-NLS-1$
    public static final String YRES = "YRES"; //$NON-NLS-1$
    public static final String ROWS = "ROWS"; //$NON-NLS-1$
    public static final String COLS = "COLS"; //$NON-NLS-1$

    /**
     * Extract mapset path and mapname from a georesource containing a JGrassMapGeoResource
     * 
     * @param resource
     * @return a String array with mapset path and map name
     */
    public synchronized static String[] getMapsetpathAndMapnameFromJGrassMapGeoResource( IGeoResource resource ) {
        String[] mapsetPathAndMapName = new String[2];

        // check that the underlying resource is a propertyservice
        if (resource == null || !resource.canResolve(JGrassMapGeoResource.class)) {
            return null;
        }
        JGrassMapGeoResource mapResource = null;
        try {
            mapResource = resource.resolve(JGrassMapGeoResource.class, null);
            // String tmp = jg.getInfo(null).getDescription();
            // if (!tmp.equals(JGrassConstants.GRASSBINARYRASTERMAP))
            // return null;
        } catch (IOException e) {
            JGrassPlugin
                    .log("JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getMapsetpathAndMapnameFromJGrassMapGeoResource", e); //$NON-NLS-1$

            e.printStackTrace();
            return null;
        }

        JGrassMapsetGeoResource mapsetResource = null;
        try {
            mapsetResource = (JGrassMapsetGeoResource) mapResource.parent(null);
            mapsetPathAndMapName[0] = mapsetResource.getFile().getAbsolutePath();
            mapsetPathAndMapName[1] = mapResource.getTitle();
        } catch (IOException e) {
            JGrassPlugin
                    .log("JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getMapsetpathAndMapnameFromJGrassMapGeoResource", e); //$NON-NLS-1$
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
                    .log("JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#getDirectPathFromJGrassMapGeoResource", e); //$NON-NLS-1$

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
    public static synchronized void addServiceToCatalog( String serviceFile, IProgressMonitor progressMonitor ) {
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
            JGrassPlugin.log(
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
            final JGrassService originalJGrassService = catalog.getById(JGrassService.class, id, monitor);

            /*
             * create the same service
             */
            if (originalJGrassService == null)
                return;
            final URL ID = originalJGrassService.getIdentifier();
            Map<String, Serializable> connectionParams = originalJGrassService.getConnectionParams();
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
    public static void addResourceFromUrlToMap( URL url, IProgressMonitor progressMonitor, int addPosition, IMap map ) {

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
                            IGeoResource finalResolve = tmp.resolve(IGeoResource.class, progressMonitor);
                            geoResources.add(finalResolve);
                        }
                    }
                    List< ? extends ILayer> addedLayers = ApplicationGIS.addLayersToMap(map, geoResources, addPosition);
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
     * Remove a memory datastore from the catalog, since the update is not possible (Caused by:
     * java.lang.UnsupportedOperationException: Schema modification not supported)
     * 
     * @param typeName the name of the type to remove, if it is there
     */
    public static synchronized void removeMemoryServiceByTypeName( String typeName ) {
        MemoryServiceImpl service = null;
        try {
            List< ? extends IResolve> members = CatalogPlugin.getDefault().getLocalCatalog().members(new NullProgressMonitor());
            for( IResolve resolve : members ) {
                if (resolve instanceof MemoryServiceImpl) {
                    if (URLUtils.urlEquals(resolve.getIdentifier(), MemoryServiceExtensionImpl.URL, true)) {
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
    public static boolean createLocation( String locationPath, CoordinateReferenceSystem crs, JGrassRegion window ) {
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

    public static boolean createMapset( String locationPath, String mapset, CoordinateReferenceSystem crs, JGrassRegion window ) {
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
                    BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + JGrassConstants.DEFAULT_WIND));
                    out.write(JGrassRegion.BLACKBOARD_KEY);
                    out.close();

                    out = new BufferedWriter(new FileWriter(path + File.separator + JGrassConstants.WIND));
                    out.write(JGrassRegion.BLANK_REGION);
                    out.close();
                }

                /* Create projection files */
                if (crs != null) {
                    // FIXME create GRASS proj files

                    BufferedWriter prjOut = new BufferedWriter(new FileWriter(path + File.separator + JGrassConstants.PROJ_WKT));
                    prjOut.write(crs.toWKT());
                    prjOut.close();

                }
            } catch (IOException e) {
                JGrassPlugin.log("JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#createMapset", e);
                e.printStackTrace();
                return false;
            }

        } else {
            /* Copy WIND file from PERMANENT mapset of this location */
            try {
                BufferedReader in = new BufferedReader(new FileReader(locationPath + File.separator
                        + JGrassConstants.PERMANENT_MAPSET + File.separator + JGrassConstants.DEFAULT_WIND));
                BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + JGrassConstants.WIND));
                String line;
                while( (line = in.readLine()) != null ) {
                    out.write(line);
                    out.write("\n");
                }
                out.close();
                in.close();
            } catch (IOException e) {
                JGrassPlugin.log("JGrassPlugin problem: eu.hydrologis.udig.catalog.utils#JGrassCatalogUtilities#createMapset", e);
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
        if (!(new File(mapsetpath + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER).mkdirs()))
                return false;
        if (!(new File(mapsetpath + File.separator + JGrassConstants.ESRIASCIIRASTER).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.ESRIASCIIRASTER).mkdirs()))
                return false;

        if (!(new File(mapsetpath + File.separator + JGrassConstants.HORTON_MACHINE_PATH).exists()))
            if (!(new File(mapsetpath + File.separator + JGrassConstants.HORTON_MACHINE_PATH).mkdirs()))
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
        BufferedReader crsReader = null;
        try {
            projWtkFilePath = locationPath + File.separator + JGrassConstants.PERMANENT_MAPSET + File.separator
                    + JGrassConstants.PROJ_WKT;
            File projWtkFile = new File(projWtkFilePath);
            if (projWtkFile.exists()) {
                crsReader = new BufferedReader(new FileReader(projWtkFile));
                StringBuffer wtkString = new StringBuffer();
                String line = null;
                while( (line = crsReader.readLine()) != null ) {
                    wtkString.append(line.trim());
                }

                readCrs = CRS.parseWKT(wtkString.toString());
            }

        } catch (Exception e1) {
            JGrassPlugin.log(
                    "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassMapsetGeoResource#getJGrassCrs", e1); //$NON-NLS-1$

            e1.printStackTrace();
        } finally {
            try {
                if (crsReader != null)
                    crsReader.close();
            } catch (IOException e) {
                JGrassPlugin
                        .log("JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassMapsetGeoResource#getJGrassCrs", e); //$NON-NLS-1$
                e.printStackTrace();
            }
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
            List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog().find(MapGraphicService.SERVICE_URL, null);
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
            JGrassPlugin.log(
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
    public synchronized static JGrassMapGeoResource addMapToCatalog( String locationPath, String mapsetName, String mapName,
            String mapType ) {
        // URL mapsetId = JGrassMapsetGeoResource.createId(locationPath, mapsetName);

        JGrassMapsetGeoResource mapset = null;
        try {
            File locationFile = new File(locationPath);

            ID locationId = new ID(locationFile.toURI().toURL());
            URL mapsetUrl = JGrassMapsetGeoResource.createId(locationFile.getAbsolutePath(), mapsetName);
            ID mapsetId = new ID(mapsetUrl);
            ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
            mapset = localCatalog.getById(JGrassMapsetGeoResource.class, mapsetId, ProgressManager.instance().get());
            if (mapset == null) {
                // try with the service
                // URL locationId = JGrassService.createId(locationPath);
                JGrassService locationService = localCatalog.getById(JGrassService.class, locationId, ProgressManager.instance()
                        .get());
                mapset = locationService.getMapsetGeoresourceByName(mapsetName);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while adding the map to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
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
    public synchronized static void removeMapFromCatalog( String locationPath, String mapsetName, String mapName, String mapType ) {
        // URL mapsetId = JGrassMapsetGeoResource.createId(locationPath, mapsetName);
        File locationFile = new File(locationPath);
        URL mapsetUrl = JGrassMapsetGeoResource.createId(locationFile.getAbsolutePath(), mapsetName);
        ID mapsetId = new ID(mapsetUrl);

        JGrassMapsetGeoResource mapset = CatalogPlugin.getDefault().getLocalCatalog()
                .getById(JGrassMapsetGeoResource.class, mapsetId, ProgressManager.instance().get());
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
    public synchronized static JGrassMapsetGeoResource addMapsetToCatalog( String locationPath, String mapsetName ) {
        // URL locationId = JGrassService.createId(locationPath);
        File locationFile = new File(locationPath);
        try {
            ID locationId = new ID(locationFile.toURI().toURL());
            JGrassService location = CatalogPlugin.getDefault().getLocalCatalog()
                    .getById(JGrassService.class, locationId, ProgressManager.instance().get());
            return location.addMapset(mapsetName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while adding the mapset to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
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
            JGrassService location = CatalogPlugin.getDefault().getLocalCatalog()
                    .getById(JGrassService.class, locationId, ProgressManager.instance().get());
            location.removeMapset(mapsetName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String message = "An error occurred while removing the mapset to the catalog";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
        }
    }

    /**
     * Utility method to create read parameters for {@link GridCoverageReader} 
     * 
     * @param width the needed number of columns.
     * @param height the needed number of columns.
     * @param north the northern boundary.
     * @param south the southern boundary.
     * @param east the eastern boundary.
     * @param west the western boundary.
     * @param crs the {@link CoordinateReferenceSystem}. Can be null, even if it should not.
     * @return the {@link GeneralParameterValue array of parameters}.
     */
    public static GeneralParameterValue[] createGridGeometryGeneralParameter( int width, int height, double north, double south,
            double east, double west, CoordinateReferenceSystem crs ) {
        GeneralParameterValue[] readParams = new GeneralParameterValue[1];
        Parameter<GridGeometry2D> readGG = new Parameter<GridGeometry2D>(AbstractGridFormat.READ_GRIDGEOMETRY2D);
        GridEnvelope2D gridEnvelope = new GridEnvelope2D(0, 0, width, height);
        Envelope env;
        if (crs != null) {
            env = new ReferencedEnvelope(west, east, south, north, crs);
        } else {
            DirectPosition2D minDp = new DirectPosition2D(west, south);
            DirectPosition2D maxDp = new DirectPosition2D(east, north);
            env = new Envelope2D(minDp, maxDp);
        }
        readGG.setValue(new GridGeometry2D(gridEnvelope, env));
        readParams[0] = readGG;

        return readParams;
    }

    /**
     * Reads a {@link GridCoverage2D} from the grass raster. 
     * 
     * @param jGrassMapEnvironment the {@link JGrassMapEnvironment}.
     * @param readRegion the region to read, or <code>null</code>.
     * @return the read coverage.
     * @throws Exception
     */
    public static GridCoverage2D getGridcoverageFromGrassraster( JGrassMapEnvironment jGrassMapEnvironment,
            JGrassRegion readRegion ) throws Exception {
        CoordinateReferenceSystem crs = jGrassMapEnvironment.getCoordinateReferenceSystem();
        JGrassRegion jGrassRegion = readRegion;
        if (jGrassRegion == null)
            jGrassRegion = jGrassMapEnvironment.getActiveRegion();
        GeneralParameterValue[] readParams = JGrassCatalogUtilities.createGridGeometryGeneralParameter(jGrassRegion.getCols(),
                jGrassRegion.getRows(), jGrassRegion.getNorth(), jGrassRegion.getSouth(), jGrassRegion.getWest(),
                jGrassRegion.getEast(), crs);
        AbstractGridFormat format = (AbstractGridFormat) new GrassCoverageFormatFactory().createFormat();
        GridCoverageReader reader = format.getReader(jGrassMapEnvironment.getCELL());
        GridCoverage2D mapCoverage = ((GridCoverage2D) reader.read(readParams));
        return mapCoverage;
    }

    public static void writeGridCoverageFromGrassraster( File mapFile, JGrassRegion writeRegion, GridCoverage2D grassCoverage )
            throws Exception {
        JGrassMapEnvironment mapEnvironment = new JGrassMapEnvironment(mapFile);
        GrassCoverageFormat format = new GrassCoverageFormatFactory().createFormat();
        GridCoverageWriter writer = format.getWriter(mapEnvironment.getCELL(), null);

        GeneralParameterValue[] readParams = null;
        if (writeRegion == null) {
            writeRegion = mapEnvironment.getActiveRegion();
        }
        readParams = JGrassCatalogUtilities.createGridGeometryGeneralParameter(writeRegion.getCols(), writeRegion.getRows(),
                writeRegion.getNorth(), writeRegion.getSouth(), writeRegion.getEast(), writeRegion.getWest(),
                mapEnvironment.getCoordinateReferenceSystem());

        writer.write(grassCoverage, readParams);
    }

    /**
     * Returns the list of files involved in the raster map issues. If for example a map has to be
     * deleted, then all these files have to.
     * 
     * @param mapsetPath - the path of the mapset
     * @param mapname -the name of the map
     * @return the array of strings containing the full path to the involved files
     */
    public static String[] filesOfRasterMap( String mapsetPath, String mapname ) {
        String filesOfRaster[] = new String[]{
                mapsetPath + File.separator + JGrassConstants.FCELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CATS + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.HIST + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELLHD + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.COLR + File.separator + mapname,
                // it is very important that the folder cell_misc/mapname comes
                // before the files in it
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_FORMAT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_QUANT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_RANGE,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_NULL};
        return filesOfRaster;
    }

    public static GridCoverage2D removeNovalues( GridCoverage2D geodata ) {
        // need to adapt it, for now do it dirty
        HashMap<String, Double> params = getRegionParamsFromGridCoverage(geodata);
        int height = params.get(ROWS).intValue();
        int width = params.get(COLS).intValue();
        WritableRaster tmpWR = createDoubleWritableRaster(width, height, null, null, null);
        WritableRandomIter tmpIter = RandomIterFactory.createWritable(tmpWR, null);
        RenderedImage readRI = geodata.getRenderedImage();
        RandomIter readIter = RandomIterFactory.create(readRI, null);
        for( int r = 0; r < height; r++ ) {
            for( int c = 0; c < width; c++ ) {
                double value = readIter.getSampleDouble(c, r, 0);

                if (Double.isNaN(value) || Float.isNaN((float) value) || Math.abs(value - -9999.0) < .0000001) {
                    tmpIter.setSample(c, r, 0, Double.NaN);
                } else {
                    tmpIter.setSample(c, r, 0, value);
                }
            }
        }
        geodata = buildCoverage("newcoverage", tmpWR, params, geodata.getCoordinateReferenceSystem());
        return geodata;
    }

    /**
     * Creates a {@link GridCoverage2D coverage} from the {@link WritableRaster writable raster} and the necessary geographic Information.
     * 
     * @param name the name of the coverage.
     * @param writableRaster the raster containing the data.
     * @param envelopeParams the map of boundary parameters.
     * @param crs the {@link CoordinateReferenceSystem}.
     * @return the {@link GridCoverage2D coverage}.
     */
    public static GridCoverage2D buildCoverage( String name, WritableRaster writableRaster,
            HashMap<String, Double> envelopeParams, CoordinateReferenceSystem crs ) {

        double west = envelopeParams.get(WEST);
        double south = envelopeParams.get(SOUTH);
        double east = envelopeParams.get(EAST);
        double north = envelopeParams.get(NORTH);
        Envelope2D writeEnvelope = new Envelope2D(crs, west, south, east - west, north - south);
        GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);

        GridCoverage2D coverage2D = factory.create(name, writableRaster, writeEnvelope);
        return coverage2D;
    }

    /**
     * Creates a {@link WritableRaster writable raster}.
     * 
     * @param width width of the raster to create.
     * @param height height of the raster to create.
     * @param dataClass data type for the raster. If <code>null</code>, defaults to double.
     * @param sampleModel the samplemodel to use. If <code>null</code>, defaults to 
     *                  <code>new ComponentSampleModel(dataType, width, height, 1, width, new int[]{0});</code>.
     * @param value value to which to set the raster to. If null, the default of the raster creation is 
     *                  used, which is 0.
     * @return a {@link WritableRaster writable raster}.
     */
    public static WritableRaster createDoubleWritableRaster( int width, int height, Class< ? > dataClass,
            SampleModel sampleModel, Double value ) {
        int dataType = DataBuffer.TYPE_DOUBLE;
        if (dataClass != null) {
            if (dataClass.isAssignableFrom(Integer.class)) {
                dataType = DataBuffer.TYPE_INT;
            } else if (dataClass.isAssignableFrom(Float.class)) {
                dataType = DataBuffer.TYPE_FLOAT;
            } else if (dataClass.isAssignableFrom(Byte.class)) {
                dataType = DataBuffer.TYPE_BYTE;
            }
        }
        if (sampleModel == null) {
            sampleModel = new ComponentSampleModel(dataType, width, height, 1, width, new int[]{0});
        }

        WritableRaster raster = RasterFactory.createWritableRaster(sampleModel, null);
        if (value != null) {
            // autobox only once
            double v = value;

            for( int y = 0; y < height; y++ ) {
                for( int x = 0; x < width; x++ ) {
                    raster.setSample(x, y, 0, v);
                }
            }
        }
        return raster;
    }

    public static HashMap<String, Double> getRegionParamsFromGridCoverage( GridCoverage2D gridCoverage ) {
        HashMap<String, Double> envelopeParams = new HashMap<String, Double>();

        Envelope envelope = gridCoverage.getEnvelope();

        DirectPosition lowerCorner = envelope.getLowerCorner();
        double[] westSouth = lowerCorner.getCoordinate();
        DirectPosition upperCorner = envelope.getUpperCorner();
        double[] eastNorth = upperCorner.getCoordinate();

        GridGeometry2D gridGeometry = gridCoverage.getGridGeometry();
        GridEnvelope2D gridRange = gridGeometry.getGridRange2D();
        int height = gridRange.height;
        int width = gridRange.width;

        AffineTransform gridToCRS = (AffineTransform) gridGeometry.getGridToCRS();
        double xRes = XAffineTransform.getScaleX0(gridToCRS);
        double yRes = XAffineTransform.getScaleY0(gridToCRS);

        envelopeParams.put(NORTH, eastNorth[1]);
        envelopeParams.put(SOUTH, westSouth[1]);
        envelopeParams.put(WEST, westSouth[0]);
        envelopeParams.put(EAST, eastNorth[0]);
        envelopeParams.put(XRES, xRes);
        envelopeParams.put(YRES, yRes);
        envelopeParams.put(ROWS, (double) height);
        envelopeParams.put(COLS, (double) width);

        return envelopeParams;
    }
}
