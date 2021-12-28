/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.internal.mediator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.FeatureStore;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.BusyIndicator;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Adapter for {@link ApplicationGIS}
 * <p>
 * Implements utility functions to access {@link ApplicationGIS} facade.
 * </p>
 *
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 *
 */
public final class AppGISAdapter {

    private AppGISAdapter() {
        // util class
    }

    /**
     * Delegate in ApplicationGIS.addLayersToMap
     *
     * @param map
     * @param resources
     * @param index
     * @return list of layers
     */
    public static List<? extends ILayer> addLayersToMap(IMap map, List<IGeoResource> resources,
            int index) {
        return ApplicationGIS.addLayersToMap(map, resources, index);
    }

    /**
     * @return List of layers displayed on active map.If active map is null return
     *         <code>Collections.EMPTY_LIST<code>.
     */
    public static List<ILayer> getActiveMapLayers() {

        IMap map = ApplicationGIS.getActiveMap();

        return getMapLayers(map);

    }

    /**
     * Layers sorted by name
     *
     * @return a Sorted List of layers displayed on active map, if map is null return
     *         <code>LayerUtil.EMPTY_LIST<code>.
     */
    public static List<ILayer> getMapLayers(IMap map) {

        if (map == null) {
            return Collections.emptyList();
        }
        List<ILayer> list = map.getMapLayers();

        // Does the filter of layers that can be resolved as feature store
        List<ILayer> sortedLayerList = new LinkedList<>();
        for (ILayer layerToInsert : list) {

            if (canResolveFeautreStore(layerToInsert) && (layerToInsert.getName() != null)) {

                // inserts in the current layer in lexicographic order
                if (sortedLayerList.isEmpty()) {
                    sortedLayerList.add(layerToInsert);
                } else {
                    int insertPosition = -1;
                    for (int i = 0; i < sortedLayerList.size(); i++) {
                        String curName = sortedLayerList.get(i).getName();
                        if (layerToInsert.getName().compareTo(curName) < 0) {
                            insertPosition = i;
                        }
                    }
                    if (insertPosition != -1) {
                        sortedLayerList.add(insertPosition, layerToInsert);
                    } else {
                        sortedLayerList.add(layerToInsert);
                    }
                }
            }
        }
        return sortedLayerList;
    }

    /**
     *
     * @param layer
     * @return true if can resolve to FeatureStore
     */
    private static boolean canResolveFeautreStore(final ILayer layer) {

        IGeoResource resource = layer.getGeoResource();

        boolean retValue = resource.canResolve(FeatureStore.class);

        return retValue;
    }

    /**
     * @return the active map;
     */
    public static IMap getActiveMap() {

        IMap map = ApplicationGIS.getActiveMap();

        return map;
    }

    public static ICatalog getCatalog() {

        return CatalogPlugin.getDefault().getLocalCatalog();
    }

    /**
     * Creates a GeoResource for the indeed feature type
     *
     * @param featureType
     * @return a new GeoResource
     */
    public static IGeoResource createTempGeoResource(final SimpleFeatureType featureType) {

        assert featureType != null;

        // new resource is required because new layer was selected
        final ICatalog catalog = AppGISAdapter.getCatalog();
        assert catalog != null;

        IGeoResource resource = catalog.createTemporaryResource(featureType);

        return resource;
    }

    /**
     * @return the selected layer
     */
    public static ILayer getSelectedLayer() {

        IMap map = AppGISAdapter.getActiveMap();
        if (map == null)
            return null;

        IEditManager edit = map.getEditManager();
        if (edit == null)
            return null;

        ILayer layer = edit.getSelectedLayer();
        if (layer == null)
            return null;
        if (layer.getName() == null)
            return null;

        return layer;
    }

    /**
     * Returns an instance (singleton object) of EditCommandFactory
     *
     * @return EditCommandFactory
     */
    public static EditCommandFactory getEditCommandFactory() {
        return EditCommandFactory.getInstance();
    }

    /**
     * Runs the given <code>runnable</code> *synchronously* in the display thread while showing a
     * busy indicator (wait cursor) on the {@link ViewportPane pane}
     *
     * @param pane
     * @param runnable
     */
    public static void showWhile(ViewportPane pane, Runnable runnable) {
        BusyIndicator.showWhile(pane, runnable);
    }

}
