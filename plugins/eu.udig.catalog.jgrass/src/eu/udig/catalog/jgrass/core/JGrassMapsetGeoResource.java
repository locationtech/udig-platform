/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
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
package eu.udig.catalog.jgrass.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeEvent.Type;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveDelta.Kind;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.IResolveManager;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.gce.grassraster.JGrassUtilities;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * Represents the GRASS Mapset in the JGrass Location service for uDig.
 * 
 * <p>
 * The JGrass georesource is part of the JGrass service and takes care of
 * representing the Mapset, which is located inside the Location and holds the
 * information of the Active Region.
 * </p>
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 2.0
 */
public class JGrassMapsetGeoResource implements IResolveFolder {

    /** jgrass mapset url field */
    private URL mapsetUrl = null;

    /** the parent service field */
    private JGrassService parent = null;

    /** name field */
    private String name = null;

    /** error message field */
    private Throwable msg = null;

    /** the resources members field */
    private List<IResolve> jgrassMapMembers = null;

    private ImageDescriptor icon;

    private Map<String, String> mapNamesAndTypes;

    public JGrassMapsetGeoResource( JGrassService parent, String name, String absolutePath ) {
        this.parent = parent;
        this.name = name;

        try {
            this.mapsetUrl = new File(absolutePath).toURI().toURL();
        } catch (MalformedURLException e) {
            msg = new RuntimeException().initCause(e);
        }

    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        // garbage in, garbage out
        if (adaptee == null)
            return false;

        /*
         * in this case our resource is a folder, therefore of type File
         */
        if (adaptee.isAssignableFrom(IService.class) || adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(File.class))
            return true;

        return CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee);

    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(parent);
        }
        if (adaptee.isAssignableFrom(File.class)) {
            // turn the url into a file and be sure that it is a folder
            File locationFolder = getFile();
            if (locationFolder.isDirectory())
                return adaptee.cast(locationFolder);
        }
        // bad call to resolve
        IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
        if (rm.canResolve(this, adaptee)) {
            return rm.resolve(this, adaptee, monitor);
        }
        return null;
    }

    public File getFile() {
        File locationFolder = URLUtils.urlToFile(mapsetUrl);
        return locationFolder;
    }

    /**
     * scan for the maps in every mapset and define of which type they are,
     * create the map resource, which will be the final leaf of the database
     */

    public List<IResolve> members( IProgressMonitor monitor ) {
        // concurrent access
        synchronized (this) {
            if (jgrassMapMembers == null) {
                // load the properties
                Map<String, String> mapNamesAndTypes = loadMaps(monitor);
                if (mapNamesAndTypes == null) {
                    // error occurred
                    return null;
                }

                /*
                 * get rasters first, then vectors, then sites (filter out
                 * .svn)
                 */
                jgrassMapMembers = new ArrayList<IResolve>();

                for( Map.Entry<String, String> item : mapNamesAndTypes.entrySet() ) {

                    String name = item.getKey();
                    String type = item.getValue();

                    // TODO add support for vectors and sites, until that
                    // moment, ignore them
                    String typeString = type.split("\\|")[0];//$NON-NLS-1$
                    if (typeString.equals(JGrassConstants.GRASS6VECTORMAP) || typeString.equals(JGrassConstants.SITESMAP)) {
                        continue;
                    }
                    IResolve jgrassMapGeoResource = new JGrassMapGeoResource(parent, this, name, type);
                    jgrassMapMembers.add(jgrassMapGeoResource);
                }

                return jgrassMapMembers;
            }

        }

        return jgrassMapMembers;
    }

    /**
     * <p>
     * scan for all available maps in the mapset and check for their type
     * </p>
     * <p>
     * <b>Note:</b> the value of the hashmap is of the format TYPE|PATH
     * </p>
     * 
     * @param monitor
     * @return the hashmap with all the maps by name and type
     */
    private Map<String, String> loadMaps( IProgressMonitor monitor ) {
        try {
            // resolve to a file
            File file = resolve(File.class, monitor);
            if (file == null) {
                return null;
            }

            mapNamesAndTypes = new HashMap<String, String>();

            /*
             * binary grass raster maps
             */
            // check the raster maps folder
            String cellMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.CELL;
            File rasterFile = new File(cellMapFolder);
            String[] rasterMaps = rasterFile.list();
            if (rasterMaps == null) {
                msg = new Throwable("Either dir does not exist or is not a directory");
            } else {
                // check raster file bundle consistency and add map if ok
                for( String mapName : rasterMaps ) {
                    if (JGrassUtilities.checkRasterMapConsistence(file.getAbsolutePath(), mapName)) {
                        mapNamesAndTypes.put(mapName, JGrassConstants.GRASSBINARYRASTERMAP + "|" + cellMapFolder + File.separator
                                + mapName);
                    }
                }
            }

            /*
             * ascii grass raster maps
             */
            String grassasciiMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.GRASSASCIIRASTER;
            File grassasciirasterFile = new File(grassasciiMapFolder);
            String[] grassasciirasterMaps = grassasciirasterFile.list();
            if (grassasciirasterMaps == null) {
                msg = new Throwable("Either dir does not exist or is not a directory");
            } else {
                for( String mapName : grassasciirasterMaps ) {
                    mapNamesAndTypes.put(mapName, JGrassConstants.GRASSASCIIRASTERMAP + "|" + grassasciiMapFolder
                            + File.separator + mapName);
                }
            }

            /*
             * fluidturtle raster maps
             */
            String fluidturtleMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER;
            File fluidturtlerasterFile = new File(fluidturtleMapFolder);
            String[] fluidturtlerasterMaps = fluidturtlerasterFile.list();
            if (fluidturtlerasterMaps == null) {
                msg = new Throwable("Either dir does not exist or is not a directory");
            } else {
                for( String mapName : fluidturtlerasterMaps ) {
                    mapNamesAndTypes.put(mapName, JGrassConstants.FTRASTERMAP + "|" + fluidturtleMapFolder + File.separator
                            + mapName);
                }
            }

            /*
             * esri grid raster maps
             */
            String esrigridMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.ESRIASCIIRASTER;
            File esrigridrasterFile = new File(esrigridMapFolder);
            String[] esrigridrasterMaps = esrigridrasterFile.list();
            if (esrigridrasterMaps == null) {
                msg = new Throwable("Either dir does not exist or is not a directory");
            } else {
                for( String mapName : esrigridrasterMaps ) {
                    mapNamesAndTypes.put(mapName, JGrassConstants.ESRIRASTERMAP + "|" + esrigridMapFolder + File.separator
                            + mapName);
                }
            }

            /*
             * sites maps
             */
            // check the sites maps folder
            String sitesMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.SITE_LISTS;
            File siteFile = new File(sitesMapFolder);
            if (siteFile.exists()) {
                String[] sitesMaps = siteFile.list();
                if (sitesMaps == null) {
                    msg = new Throwable("Either dir does not exist or is not a directory");
                } else {
                    // add sites to hashmap
                    for( String mapName : sitesMaps ) {
                        mapNamesAndTypes.put(mapName, JGrassConstants.SITESMAP + "|" + sitesMapFolder + File.separator + mapName);
                    }
                }
            }

            /*
             * vector maps
             */
            // list the vector maps
            String vectorMapFolder = file.getAbsolutePath() + File.separator + JGrassConstants.VECTORS;
            File vectorFile = new File(vectorMapFolder);
            if (vectorFile.exists()) {
                String[] vectormaps = vectorFile.list();
                if (vectormaps == null) {
                    msg = new Throwable("Either dir does not exist or is not a directory");
                } else {
                    // add vector maps to hashmap
                    for( String mapName : vectormaps ) {
                        mapNamesAndTypes.put(mapName, JGrassConstants.GRASS6VECTORMAP + "|" + vectorMapFolder + File.separator
                                + mapName);
                    }
                }
            }
            // clear message, everything is ok
            msg = null;

            return mapNamesAndTypes;

        } catch (Throwable e) {
            msg = e;
        }

        return null;
    }

    public URL getIdentifier() {
        String parentFile = URLUtils.urlToFile(parent.getIdentifier()).getAbsolutePath();

        URL mapsetId = createId(parentFile, name);
        return mapsetId;
    }

    public Throwable getMessage() {
        return msg;
    }

    public Status getStatus() {
        // error occured
        if (msg != null) {
            return Status.BROKEN;
        }

        // if the folder hasn't been scanned yet
        if (jgrassMapMembers == null) {
            return Status.NOTCONNECTED;
        }

        return Status.CONNECTED;
    }

    /**
     * <p>
     * get some information about the mapset resource
     * </p>
     * 
     * @author Andrea Antonello - www.hydrologis.com
     * @since 1.1.0
     */
    class JGrassMapsetGeoResourceInfo extends IGeoResourceInfo {
        public JGrassMapsetGeoResourceInfo( IProgressMonitor monitor ) throws IOException {
            this.name = JGrassMapsetGeoResource.this.name;
            this.title = this.name;
            this.description = "JGrass Mapset (" + getIdentifier() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

            // calculate bounds
            JGrassRegion activeRegionWindow = getActiveRegionWindow();
            this.bounds = new ReferencedEnvelope(activeRegionWindow.getEnvelope(), getCRS());
            super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.CATALOG_OBJ);
        }

        public CoordinateReferenceSystem getCRS() {
            return getJGrassCrs();
        }
    }

    public JGrassRegion getActiveRegionWindow() {
        try {
            return new JGrassRegion(getActiveRegionWindowPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getActiveRegionWindowPath() {
        File windFile = new File(getFile() + File.separator + JGrassConstants.WIND);
        if (!windFile.exists()) {
            windFile = new File(getFile() + File.separator + JGrassConstants.WIND.toLowerCase());
            if (!windFile.exists()) {
                msg = new Throwable("Couldn't find a suitable region file in the mapset. Check your Location.");
                return null;
            }
        }
        return windFile.getAbsolutePath();
    }

    public CoordinateReferenceSystem getJGrassCrs() {
        CoordinateReferenceSystem readCrs = null;
        String locationPath = URLUtils.urlToFile(parent.getIdentifier()).getAbsolutePath();
        readCrs = JGrassCatalogUtilities.getLocationCrs(locationPath);
        return readCrs;
    }

    public ImageDescriptor getIcon( IProgressMonitor monitor ) {
        if (icon == null) {
            icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ);
        }
        return icon;
    }

    public String getTitle() {
        return name;
    }

    public void dispose( IProgressMonitor monitor ) {
        jgrassMapMembers = null;
    }

    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return parent;
    }

    public IService getService( IProgressMonitor monitor ) {
        return parent;
    }

    public ID getID() {
        ID id = new ID(getIdentifier());
        return id;
    }

    public static URL createId( String locationPath, String mapsetName ) {
        try {
            URL locationUrl = new File(locationPath).toURI().toURL();
            String locationUrlString = URLUtils.urlToString(locationUrl, false);
            return new URL(locationUrlString + "#/" + mapsetName);
        } catch (MalformedURLException e) {
            throw (Error) new AssertionError("Url should always work this is a bug").initCause(e);
        }
    }

    public Set<String> getMapnamesList() {
        Set<String> mapnamesSet = mapNamesAndTypes.keySet();
        return mapnamesSet;
    }

    /**
     * Adds the supplied raster map to the Mapset in the catalog.
     * 
     * <p>
     * The map is added to the catalog into the {@link JGrassService JGrass
     * service} without the need of resetting the service.
     * </p>
     * 
     * @param mapName
     *            the name of the map to be added.
     * @param mapType
     *            the type defining the format of the map to add.
     * @return the {@link JGrassMapGeoResource georesource} that was added.
     */
    public JGrassMapGeoResource addMap( String mapName, String mapType ) {
        String mapLocation = new File(getFile(), JGrassConstants.CELL + "/" + mapName).getAbsolutePath();
        String mapTypeAndPath = mapType + "|" + mapLocation;
        JGrassMapGeoResource resource = new JGrassMapGeoResource(parent, this, mapName, mapTypeAndPath);
        if (jgrassMapMembers == null) {
            jgrassMapMembers = members(ProgressManager.instance().get(null));
        }

        if (!jgrassMapMembers.contains(resource)) {
            jgrassMapMembers.add(resource);

            ICatalog catalog = getService(null).parent(null);
            if (catalog instanceof CatalogImpl) {
                IResolveDelta delta = new ResolveDelta(resource, Kind.ADDED);
                IResolveChangeEvent event = new ResolveChangeEvent(this, Type.POST_CHANGE, delta);
                ((CatalogImpl) catalog).fire(event);
            }
        } else {
            try {
                int index = jgrassMapMembers.indexOf(resource);
                IResolve iResolve = jgrassMapMembers.get(index);
                // ((JGrassMapGeoResource) iResolve).resetBoundInfo();
                resource = ((JGrassMapGeoResource) iResolve);
                resource.resetBoundInfo();
            } catch (IOException e) {
                String message = "An error occurred while reloading the file bounds info.";
                ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, JGrassPlugin.PLUGIN_ID, e);
            }
        }
        return resource;
    }

    public JGrassMapGeoResource removeMap( String mapName, String mapType ) {
        String mapLocation = new File(getFile(), JGrassConstants.CELL + "/" + mapName).getAbsolutePath();
        String mapTypeAndPath = mapType + "|" + mapLocation;
        JGrassMapGeoResource resource = new JGrassMapGeoResource(parent, this, mapName, mapTypeAndPath);
        if (jgrassMapMembers == null) {
            jgrassMapMembers = members(ProgressManager.instance().get(null));
        }
        if (jgrassMapMembers.contains(resource)) {
            jgrassMapMembers.remove(resource);

            ICatalog catalog = getService(null).parent(null);
            if (catalog instanceof CatalogImpl) {
                IResolveDelta delta = new ResolveDelta(resource, Kind.REMOVED);
                IResolveChangeEvent event = new ResolveChangeEvent(this, Type.POST_CHANGE, delta);
                ((CatalogImpl) catalog).fire(event);
            }
        }
        return resource;
    }

}
