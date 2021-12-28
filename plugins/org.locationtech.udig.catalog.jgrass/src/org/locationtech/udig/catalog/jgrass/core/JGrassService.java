/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.gce.grassraster.JGrassConstants;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveChangeEvent.Type;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IResolveDelta.Kind;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.utils.JGrassCatalogUtilities;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * The service handle for the JGrass database
 * </p>
 *
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGrassService extends IService {

    /** jgrass location URL field */
    private URL url = null;

    /** connection params field */
    private Map<String, Serializable> params = null;

    /** metadata info field */
    private JGrassServiceInfo info = null;

    /** the resources members field */
    private volatile List<IResolve> mapsetMembers = null;

    /** error message field */
    private Throwable msg = null;

    private ID id;

    private File locationFolderFile;

    private CoordinateReferenceSystem locationCrs;

    public JGrassService(Map<String, Serializable> params) {
        this.params = params;

        // get the file URL from the connection parameters
        url = (URL) this.params.get(JGrassServiceExtension.KEY);
        id = new ID(url);

        locationFolderFile = URLUtils.urlToFile(url);
        if (!locationFolderFile.isDirectory()) {
            throw new IllegalArgumentException("The GRASS location has to be a folder: " //$NON-NLS-1$
                    + locationFolderFile.getAbsolutePath());
        }
        locationCrs = JGrassCatalogUtilities.getLocationCrs(locationFolderFile.getAbsolutePath());
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    /**
     * check if the passed adaptee can resolve the file. Checks on location consistency were already
     * done in the service extension.
     */
    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        // garbage in, garbage out
        if (adaptee == null)
            return false;

        /*
         * in this case our resource is a folder, therefore of type File
         */
        return adaptee.isAssignableFrom(IServiceInfo.class) || // getInfo
                adaptee.isAssignableFrom(List.class) || // members
                adaptee.isAssignableFrom(File.class) || super.canResolve(adaptee);

    }

    /**
     * resolve the adaptee to the location folder file
     */
    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            // return the metadata object
            return adaptee.cast(getInfo(monitor));
        }
        if (adaptee.isAssignableFrom(List.class)) {
            // return the list of resources
            return adaptee.cast(members(monitor));
        }
        if (adaptee.isAssignableFrom(File.class)) {
            return adaptee.cast(locationFolderFile);
        }
        // bad call to resolve
        return super.resolve(adaptee, monitor);
    }

    @Override
    public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
        // seed the potentially null field
        members(monitor);
        List<JGrassMapGeoResource> children = new ArrayList<>();
        collectChildren(this, children, monitor);

        return children;
    }

    private void collectChildren(IResolve resolve, List<JGrassMapGeoResource> children,
            IProgressMonitor monitor) throws IOException {
        List<IResolve> resolves = resolve.members(monitor);

        if (resolve instanceof JGrassMapGeoResource && resolves.isEmpty()) {
            children.add((JGrassMapGeoResource) resolve);
        } else {
            for (IResolve resolve2 : resolves) {
                collectChildren(resolve2, children, monitor);
            }
        }
    }

    /**
     * scan the members of the service. In that case the members are mapset georesources
     */
    @Override
    public List<IResolve> members(IProgressMonitor monitor) throws IOException {
        // lazily load
        if (mapsetMembers == null) {
            // concurrent access
            synchronized (this) {
                if (mapsetMembers == null) {
                    // load the mapsets
                    Map<String, String> mapsetNamesAndPaths = loadMapsets(monitor);
                    if (mapsetNamesAndPaths == null) {
                        // error occurred
                        return null;
                    }

                    mapsetMembers = new ArrayList<>();
                    for (Map.Entry<String, String> item : mapsetNamesAndPaths.entrySet()) {

                        JGrassMapsetGeoResource jgrassMapsetGeoResource = new JGrassMapsetGeoResource(
                                this, item.getKey(), item.getValue());
                        mapsetMembers.add(jgrassMapsetGeoResource);
                    }
                    return mapsetMembers;
                }

            }

        }

        return mapsetMembers;
    }

    /**
     * scan for mapsets inside the location
     *
     * @param monitor
     * @return map of mapset names and their path
     */
    private Map<String, String> loadMapsets(IProgressMonitor monitor) {
        try {
            Map<String, String> props = new HashMap<>();

            // list the mapsets inside the location folder
            File[] mapsetFiles = locationFolderFile.listFiles();
            if (mapsetFiles == null) {
                msg = new Throwable("Either dir does not exist or is not a directory"); //$NON-NLS-1$
                return null;
            }

            // store the mapset name and the absolute path to it
            File permanentMapsetFile = null;
            for (File mapsetFile : mapsetFiles) {
                if (mapsetFile.getName().equals(JGrassConstants.PERMANENT_MAPSET)) {
                    permanentMapsetFile = mapsetFile;
                    continue;
                }
                // the folder has contain the region definition file to be
                // consistent
                File windInMapsetFile = new File(mapsetFile, JGrassConstants.WIND);
                if (mapsetFile.exists() && mapsetFile.isDirectory() && windInMapsetFile.exists()) {
                    props.put(mapsetFile.getName(), mapsetFile.getAbsolutePath());
                }
            }
            if (props.isEmpty() && permanentMapsetFile != null) {
                props.put(JGrassConstants.PERMANENT_MAPSET, permanentMapsetFile.getAbsolutePath());
            }

            // clear message, everything is ok
            msg = null;

            return props;

        } catch (Throwable e) {
            msg = e;
        }

        return null;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public ID getID() {
        return id;
    }

    /**
     * Getter for the location {@link CoordinateReferenceSystem}.
     *
     * @return the location CRS.
     */
    public CoordinateReferenceSystem getLocationCrs() {
        return locationCrs;
    }

    public static URL createId(String locationPath) {
        try {
            URL locationUrl = new File(locationPath).toURI().toURL();
            return locationUrl;
        } catch (MalformedURLException e) {
            throw (Error) new AssertionError("Url should always work this is a bug").initCause(e); //$NON-NLS-1$
        }
    }

    @Override
    public Throwable getMessage() {
        return msg;
    }

    @Override
    public Status getStatus() {
        if (mapsetMembers == null) {
            // if the folder hasn't been scanned yet
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    public File getFile() {
        return locationFolderFile;
    }

    public File getPermanetMapsetFile() {
        return new File(getFile(), JGrassConstants.PERMANENT_MAPSET);
    }

    public File getProjWktFile() {
        return new File(getPermanetMapsetFile(), JGrassConstants.PROJ_WKT);
    }

    class JGrassServiceInfo extends IServiceInfo {
        public JGrassServiceInfo() {
            File serviceFile = getFile();
            this.title = serviceFile.getName();
            this.description = "JGrass database service (" + this.title + ")"; //$NON-NLS-1$ //$NON-NLS-2$

        }

        @Override
        public Icon getIcon() {
            ImageDescriptor imgD = AbstractUIPlugin.imageDescriptorFromPlugin(
                    JGrassPlugin.PLUGIN_ID, "icons/obj16/jgrassloc_obj.gif"); //$NON-NLS-1$
            return AWTSWTImageUtils.imageDescriptor2awtIcon(imgD);

        }

        @Override
        public String getTitle() {
            return title;
        }
    }

    public JGrassMapsetGeoResource addMapset(String mapsetName) {

        File mapsetFile = new File(getFile(), mapsetName);
        JGrassMapsetGeoResource resource = new JGrassMapsetGeoResource(this, mapsetName,
                mapsetFile.getAbsolutePath());
        if (mapsetMembers == null) {
            try {
                mapsetMembers = members(ProgressManager.instance().get(null));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (!mapsetMembers.contains(resource)) {
            mapsetMembers.add(resource);

            ICatalog catalog = parent(null);
            if (catalog instanceof CatalogImpl) {
                IResolveDelta delta = new ResolveDelta(resource, Kind.ADDED);
                IResolveChangeEvent event = new ResolveChangeEvent(this, Type.POST_CHANGE, delta);
                ((CatalogImpl) catalog).fire(event);
            }
        }
        return resource;
    }

    public JGrassMapsetGeoResource removeMapset(String mapsetName) {
        File mapsetFile = new File(getFile(), mapsetName);
        JGrassMapsetGeoResource resource = new JGrassMapsetGeoResource(this, mapsetName,
                mapsetFile.getAbsolutePath());
        URL mapsetIdentifier = resource.getIdentifier();

        if (mapsetMembers == null) {
            try {
                mapsetMembers = members(ProgressManager.instance().get(null));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        for (int i = 0; i < mapsetMembers.size(); i++) {
            IResolve resolve = mapsetMembers.get(i);
            URL identifier = resolve.getIdentifier();
            if (mapsetIdentifier.toExternalForm().equals(identifier.toExternalForm())) {
                mapsetMembers.remove(i);

                ICatalog catalog = parent(null);
                if (catalog instanceof CatalogImpl) {
                    IResolveDelta delta = new ResolveDelta(resource, Kind.REMOVED);
                    IResolveChangeEvent event = new ResolveChangeEvent(this, Type.POST_CHANGE,
                            delta);
                    ((CatalogImpl) catalog).fire(event);
                }
                break;
            }

        }

        return resource;
    }

    /**
     * Retrieves the {@link JGrassMapsetGeoResource mapset resource} by its name.
     *
     * @param name the name to search for
     * @return the mapset resource or null, if none was found.
     */
    public JGrassMapsetGeoResource getMapsetGeoresourceByName(String name) {
        if (mapsetMembers == null) {
            try {
                members(new NullProgressMonitor());
            } catch (IOException e) {
                JGrassPlugin.log(
                        "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassService#getMapsetGeoresourceByName", //$NON-NLS-1$
                        e);

                e.printStackTrace();
                return null;
            }
        }
        for (IResolve mapsetResource : mapsetMembers) {
            if (mapsetResource instanceof JGrassMapsetGeoResource) {
                JGrassMapsetGeoResource mapsetGR = (JGrassMapsetGeoResource) mapsetResource;
                String mapsetName = mapsetGR.getTitle();
                if (mapsetName.equals(name)) {
                    return mapsetGR;
                }

            }
        }

        return null;
    }

    @Override
    protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        // lazy creation
        if (info == null) {
            info = new JGrassServiceInfo();
        }
        return info;
    }

}
