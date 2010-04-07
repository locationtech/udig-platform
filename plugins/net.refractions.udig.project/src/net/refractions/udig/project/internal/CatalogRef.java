/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ServiceParameterPersister;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.ecore.resource.Resource;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * This class is an EMF bridge for persisting the connection parameters required to add the services
 * required by the layer into the catalog if the catalog does not have those services. For example
 * if a map is moved to another uDig install.
 * <p>
 * One important point to remember is that a layer can be associated with many resources and
 * therefore may have to store the parameters for many services
 * </p>
 * <p>
 * The {@link #marshalConnectionParameters()} method is used to persist the connection parameters
 * and the {@link #parseResourceParameters(String)} is used to provide the CatalogRef with the
 * connection parameters again. However, they services are not loaded until load is called. This is
 * so that uDig doesn't block until it is a good time to block. (IE when getGeoResources is called).
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class CatalogRef {
    protected Layer layer;

    protected static final String ENCODING = ProjectPlugin.Implementation.ENCODING;

    private static final String ERROR_SAVING = "Error saving"; //$NON-NLS-1$

    protected Map<ID, Map<String, Serializable>> connectionParams = Collections
            .synchronizedMap(new HashMap<ID, Map<String, Serializable>>());

    private volatile boolean loaded = false;

    /**
     * Construct <code>LayerRef</code>.
     * 
     * @param layer
     */
    public CatalogRef( Layer layer ) {
        this.layer = layer;
    }

    /**
     * Construct <code>LayerRef</code>.
     */
    public CatalogRef() {
        // do nothing
    }

    /**
     * Marshals the connection parameters into an XML safe string. IE as string that can be embedded
     * in an XML document.
     * <p>
     * Basic process is to use the {@link ServiceParameterPersister} class to persist the
     * parameters. It writes to a Preferences object so a node is obtained from the
     * PreferenceServices and the parameters are written to that node which is then exported to a
     * string. The string is encoded by URLEncoder (so it can go in an XML doc) and the string is
     * returned
     * </p>
     */
    public String marshalConnectionParameters() {
        try {
            List<IGeoResource> resources = layer.getGeoResources();
            Preferences toSave = Platform.getPreferencesService().getRootNode().node(
                    ProjectPlugin.ID).node(layerIDToString());
            if (resources != LayerImpl.NULL) {
                connectionParams.clear();

                ServiceParameterPersister persister = new LayerCatalogRefPersister(
                        connectionParams, getMapFile());

                persister.store(ProgressManager.instance().get(), toSave, resources);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Platform.getPreferencesService().exportPreferences((IEclipsePreferences) toSave, out,
                    null);
            toSave.clear();

            return URLEncoder.encode(out.toString(), ENCODING);
        } catch (Throwable t) {
            ProjectPlugin
                    .log(
                            "Error saving parameters for layer: " + (layer == null ? "unkown id" : layer.getID()) + " this map cannot be sent to a collegue", t); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            return ERROR_SAVING;
        }
    }

    /**
     * Create a File object that is the map file. This is used so that the File URLs can be saved
     * relative to the map file
     */
    private File getMapFile() {
        Resource resource = layer.getMapInternal().eResource();
        if (resource != null) {
            return new File(resource.getURI().toFileString());
        } else {
            return null;
        }
    }

    private String layerIDToString() throws UnsupportedEncodingException {
        if (layer != null && layer.getID() != null)
            return URLEncoder.encode(layer.getID().toString(), ENCODING);
        else
            return "ID_" + System.currentTimeMillis(); //$NON-NLS-1$
    }

    /**
     * Reloads the parameters for the resource's parameters from the string and stores them. Services
     * aren't created until load() is called.
     * 
     * @param string
     */
    public void parseResourceParameters( String string ) {
        if (string == null || string.length() == 0) {
            return;
        }
        String decoded;
        try {
            decoded = URLDecoder.decode(string, ENCODING);
        } catch (UnsupportedEncodingException e) {
            decoded = string;
        }

        ByteArrayInputStream input = new ByteArrayInputStream(decoded.getBytes());
        try {
            IPreferencesService preferencesService = Platform.getPreferencesService();
            IExportedPreferences paramsNode = preferencesService.readPreferences(input);

            ServiceParameterPersister persister = new LayerCatalogRefPersister(connectionParams,
                    null);

            persister.restore(findParameterNode(paramsNode));
        } catch (Throwable e) {
            // ok maybe it is an from an older version of uDig so try the oldCatalogRef
            ProjectPlugin.log("CatalogRef#parseResourceParameters, couldn't load paramters", e); //$NON-NLS-1$
        }

    }

    private Preferences findParameterNode( IExportedPreferences paramsNode )
            throws BackingStoreException {
        String[] name = paramsNode.childrenNames();

        Preferences plugin = paramsNode.node(name[0]);
        name = plugin.childrenNames();

        return plugin.node(name[0]);
    }

    /**
     * @return Returns the layer.
     * @uml.property name="layer"
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * @param layer The layer to set.
     * @uml.property name="layer"
     */
    public void setLayer( Layer layer ) {
        this.layer = layer;
    }

    /**
     * Adds the required services into the catalog.
     */
    public void load() {

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        synchronized (connectionParams) {
            for( Map.Entry<ID, Map<String, Serializable>> entry : connectionParams.entrySet() ) {
                Map<String, Serializable> params = entry.getValue();
                boolean couldResolve = resolveURLs(params);
                if (!couldResolve) {
                    // if couldn't resolve log the warning and continue. There is no way we can load
                    // the resource
                    ProjectPlugin
                            .log(
                                    "Warning: couldn't find the layer's resources in the catalog and can't construct it because the current map" //$NON-NLS-1$
                                            + " does not have a Resource:" + layer.getID(), //$NON-NLS-1$
                                    new Exception("Failure loading layer")); //$NON-NLS-1$
                    continue;
                }

                List<IService> createdServices = serviceFactory.createService(params);
                updateLayerID(createdServices);
                if (serviceExistsInCatalog(createdServices, catalog)) {
                    continue;
                } else {
                    addServicesToCatalog(createdServices, catalog);
                }

            }
        }
        loaded = true;
    }

    
    /**
     * This is confusing so read on...
     * <p>
     * Because a Service's id is determined by where the resource is located (for shapefile it is
     * the file of the .shp). The id of the service could be different from the last time the layer
     * was loaded (the shp file moved). Or if this layer is added to a different uDig the shp file
     * may be in a different location than on the previous system.
     * </p>
     * <p>
     * Because of this the ids services must be inspected and layer's id must be checked to ensure
     * it is still valid. If not it must be updated to refer to a valid service
     * </p>
     * 
     * @param createdServices
     */
    private void updateLayerID( List<IService> createdServices ) {
        if (createdServices.isEmpty()) {
            return;
        }

        URL layerid = getLayer().getID();
        for( IService service : createdServices ) {
            if (URLUtils.urlEquals(layerid, service.getIdentifier(), true)) {
                return;
            }
        }

        // layer id is bad :( create a new one from the 1st service.
        URL id = createdServices.get(0).getIdentifier();
        String serviceIdAsString = URLUtils.urlToString(id, false);
        String externalizedLayerID = layerid.toExternalForm();

        String[] segments = externalizedLayerID.split("#"); //$NON-NLS-1$

        if (segments.length < 2) {
            // oh crap the service that this layer was originally created from does not obey the
            // convention where georesources must be the serviceid#subid
            ProjectPlugin
                    .log("Some service doesn't obey the convention where georesources must be the serviceid#subid. \n\nLayer id = " //$NON-NLS-1$
                            + layerid + "\n\nThe potential culprits are: " + createdServices); //$NON-NLS-1$
            return;
        }

        try {
            layer.setID(new URL(serviceIdAsString + "#" + segments[1])); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }

    }
    
    private void addServicesToCatalog( List<IService> createdServices, ICatalog catalog ) {
        for( IService service : createdServices ) {
            catalog.add(service);
        }
    }

    private boolean serviceExistsInCatalog( List<IService> createdServices, ICatalog catalog ) {
        boolean found = false;
        for( IService service : createdServices ) {
            if (catalog.getById(IService.class, service.getID(), ProgressManager.instance()
                    .get()) != null) {
                found = true;
            } else {
                catalog.add(service);
            }
        }
        return found;
    }

    /**
     * Takes the params and tries to change them so if they are relative URLs then they are relative
     * to the map.
     * 
     * @return Return true if the map has a resource and therefore a URI false if resolving failed.
     *         This may happen if the map was copied and no longer has a URL. In this case just
     *         search the catalog and don't load resources from the given URI.
     */
    private boolean resolveURLs( Map<String, Serializable> params ) {
        Set<Entry<String, Serializable>> entries = params.entrySet();
        for( Entry<String, Serializable> entry : entries ) {
            if (entry.getValue() instanceof URL) {
                String url = ((URL) entry.getValue()).toExternalForm();
                try {
                    File mapFile = getMapFile();
                    if (mapFile == null) {
                        return false;
                    }
                    entry.setValue(URLUtils.constructURL(mapFile, url));
                } catch (MalformedURLException e) {
                    // oh well we'll keep see what happens, maybe this isn't possible
                    ProjectPlugin.log("Couldn't resolve the URL", e); //$NON-NLS-1$
                }
            }
        }
        return true;

    }

    /**
     * Returns true if the required GeoResources have been added to the catalog.
     * 
     * @return
     * @uml.property name="loaded"
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Used to store a catalog reference into a project file.
     * <p>
     * Please note that catalog references here are stored as connectionParameters (rather than a
     * simple ID). This allows a project file to find and locate services when loaded up in another
     * copy of uDig.
     */
    private static class LayerCatalogRefPersister extends ServiceParameterPersister {

        private Map<ID, Map<String, Serializable>> allParams;

        public LayerCatalogRefPersister( Map<ID, Map<String, Serializable>> allParams, File mapFile ) {
            super(CatalogPlugin.getDefault().getLocalCatalog(), CatalogPlugin.getDefault()
                    .getServiceFactory(), mapFile);
            this.allParams = allParams;
        }

        @Override
        protected void locateService( ID id, Map<String, Serializable> map, Map<String, Serializable> properties  ) {
            if (allParams.containsKey(id))
                ProjectPlugin
                        .log("LayerCatalogRefPersister#locateService: duplicate resource ids when loading paramers"); //$NON-NLS-1$

            allParams.put(id, map);
        }

    }
}
