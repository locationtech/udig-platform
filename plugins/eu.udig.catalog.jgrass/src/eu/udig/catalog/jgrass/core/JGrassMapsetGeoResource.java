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

    private CoordinateReferenceSystem locationCrs;

    private File mapsetFile;

    private File cellFolderFile;

    public JGrassMapsetGeoResource( JGrassService parent, String name, String mapsetPath ) {
        this.parent = parent;
        this.name = name;

        mapsetFile = new File(mapsetPath);
        if (!mapsetFile.exists() || !mapsetFile.isDirectory()) {
            throw new IllegalArgumentException("The GRASS mapset has to be a folder: " + mapsetPath);
        }
        cellFolderFile = new File(mapsetFile, JGrassConstants.CELL);

        locationCrs = parent.getLocationCrs();
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
            return adaptee.cast(mapsetFile);
        }
        // bad call to resolve
        IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
        if (rm.canResolve(this, adaptee)) {
            return rm.resolve(this, adaptee, monitor);
        }
        return null;
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
            mapNamesAndTypes = new HashMap<String, String>();
            /*
             * binary grass raster maps
             */
            // check the raster maps folder

            File[] rasterFiles = cellFolderFile.listFiles();
            if (rasterFiles == null) {
                msg = new Throwable("Either dir does not exist or is not a directory");
            } else {
                // check raster file bundle consistency and add map if ok
                for( File rasterFile : rasterFiles ) {
                    if (JGrassUtilities.checkRasterMapConsistence(mapsetFile.getAbsolutePath(), rasterFile.getName())) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(JGrassConstants.GRASSBINARYRASTERMAP);
                        sb.append("|");
                        sb.append(rasterFile.getAbsolutePath());
                        mapNamesAndTypes.put(rasterFile.getName(), sb.toString());
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
            return locationCrs;
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
        File windFile = new File(mapsetFile, JGrassConstants.WIND);
        if (!windFile.exists()) {
            windFile = new File(mapsetFile, JGrassConstants.WIND.toLowerCase());
            if (!windFile.exists()) {
                msg = new Throwable("Couldn't find a suitable region file in the mapset. Check your Location.");
                return null;
            }
        }
        return windFile.getAbsolutePath();
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
        String mapPath = new File(cellFolderFile, mapName).getAbsolutePath();
        String mapTypeAndPath = mapType + "|" + mapPath;
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
        String mapPath = new File(cellFolderFile, mapName).getAbsolutePath();
        String mapTypeAndPath = mapType + "|" + mapPath;
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

    /**
     * Getter for the mapset file.
     * 
     * @return the mapset file.
     */
    public File getFile() {
        return mapsetFile;
    }

    /**
     * Getter for the location {@link CoordinateReferenceSystem}.
     * 
     * @return the location's crs.
     */
    public CoordinateReferenceSystem getLocationCrs() {
        return locationCrs;
    }
}
