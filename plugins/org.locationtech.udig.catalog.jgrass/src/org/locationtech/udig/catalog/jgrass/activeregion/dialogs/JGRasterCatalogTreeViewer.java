/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.activeregion.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.gce.grassraster.JGrassConstants;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassService;
import org.locationtech.udig.catalog.jgrass.messages.Messages;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;

/**
 * <p>
 * This class supplies a tree viewer containing the JGrass raster maps that are in the catalog.
 * </p>
 *
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGRasterCatalogTreeViewer extends Composite
        implements ISelectionChangedListener, IResourcesSelector {

    private final HashMap<String, JGrassMapGeoResource> itemsMap = new HashMap<>();

    private LabelProvider labelProvider = null;

    private List<IGeoResource> itemLayers;

    private String mapset;

    /**
     * @param parent
     * @param style
     * @param selectionStyle the tree selection style (single or multiple)
     * @param mapset mapset path on which to limit the view
     */
    public JGRasterCatalogTreeViewer(Composite parent, int style, int selectionStyle,
            String mapset) {
        super(parent, style);
        if (mapset != null)
            this.mapset = mapset;
        setLayout(new GridLayout(1, false));
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        setLayoutData(gridData);

        // Create the tree viewer to display the file tree
        PatternFilter patternFilter = new PatternFilter();
        final FilteredTree filter = new FilteredTree(this, selectionStyle, patternFilter, false);
        final TreeViewer tv = filter.getViewer();
        tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        tv.setContentProvider(new ContentProvider());
        labelProvider = new LabelProvider();
        tv.setLabelProvider(labelProvider);
        tv.setInput("dummy2"); // pass a non-null that will be ignored //$NON-NLS-1$
        tv.addSelectionChangedListener(this);
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        // if the selection is empty clear the label
        if (event.getSelection().isEmpty()) {
            return;
        }
        if (event.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Vector<String> itemNames = new Vector<>();
            for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
                Object domain = iterator.next();
                String value = labelProvider.getText(domain);
                itemNames.add(value);
            }
            itemLayers = new ArrayList<>();
            for (String name : itemNames) {
                JGrassMapGeoResource tmpLayer = itemsMap.get(name);
                if (tmpLayer != null) {
                    itemLayers.add(tmpLayer);
                }
            }
        }
    }

    /**
     * This class provides the content for the tree in FileTree
     */

    private class ContentProvider implements ITreeContentProvider {
        /**
         * Gets the children of the specified object
         *
         * @param arg0 the parent object
         * @return Object[]
         */
        @Override
        public Object[] getChildren(Object arg0) {

            if (arg0 instanceof JGrassMapsetGeoResource) {
                JGrassMapsetGeoResource map = (JGrassMapsetGeoResource) arg0;
                List<IResolve> layers = map.members(null);
                if (layers == null)
                    return null;
                return filteredLayers(layers);
            } else if (arg0 instanceof JGrassMapGeoResource) {
                return null;
            }

            return null;
        }

        private Object[] filteredLayers(List<IResolve> layers) {
            try {
                Vector<JGrassMapGeoResource> filteredLayers = new Vector<>();
                for (IResolve layer : layers) {
                    if (layer instanceof JGrassMapGeoResource &&

                            (((JGrassMapGeoResource) layer).getType()
                                    .equals(JGrassConstants.GRASSBINARYRASTERMAP)
                                    || ((JGrassMapGeoResource) layer).getType()
                                            .equals(JGrassConstants.GRASSASCIIRASTERMAP)
                                    || ((JGrassMapGeoResource) layer).getType()
                                            .equals(JGrassConstants.ESRIRASTERMAP)
                                    || ((JGrassMapGeoResource) layer).getType()
                                            .equals(JGrassConstants.FTRASTERMAP))

                    ) {

                        filteredLayers.add((JGrassMapGeoResource) layer);
                        itemsMap.put(((JGrassMapGeoResource) layer).getInfo(null).getTitle(),
                                (JGrassMapGeoResource) layer);
                    }

                }

                /*
                 * now let's sort them for nice visualization
                 */
                HashMap<String, JGrassMapGeoResource> tmp = new HashMap<>();
                for (JGrassMapGeoResource resource : filteredLayers) {
                    tmp.put(resource.getInfo(null).getTitle(), resource);
                }
                Map<String, JGrassMapGeoResource> sortedMap = new TreeMap<>(tmp);
                filteredLayers.removeAllElements();
                for (JGrassMapGeoResource map : sortedMap.values()) {
                    filteredLayers.add(map);
                }

                return filteredLayers.toArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Gets the parent of the specified object
         *
         * @param arg0 the object
         * @return Object
         */
        @Override
        public Object getParent(Object arg0) {
            if (arg0 instanceof JGrassMapsetGeoResource) {
                return null;
            } else if (arg0 instanceof JGrassMapGeoResource) {
                try {
                    return ((JGrassMapGeoResource) arg0).parent(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Returns whether the passed object has children
         *
         * @param arg0 the parent object
         * @return boolean
         */
        @Override
        public boolean hasChildren(Object arg0) {
            if (arg0 instanceof JGrassMapsetGeoResource) {
                return true;
            } else if (arg0 instanceof JGrassMapGeoResource) {
                return false;
            }
            return false;
        }

        /**
         * Gets the root element(s) of the tree
         *
         * @param arg0 the input data
         * @return Object[]
         */
        @Override
        public Object[] getElements(Object arg0) {
            // add the service to the catalog
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            ArrayList<IResolve> neededCatalogMembers = null;
            try {
                List<? extends IResolve> allCatalogMembers = catalog.members(null);
                // for jgrass locations we extract the mapset out of the
                // locations = from
                // JGrassService
                neededCatalogMembers = new ArrayList<>();
                for (IResolve catalogMember : allCatalogMembers) {
                    if (catalogMember instanceof JGrassService) {
                        List<IResolve> layers = ((JGrassService) catalogMember).members(null);
                        for (IResolve resource : layers) {
                            if (mapset != null && resource instanceof JGrassMapsetGeoResource) {
                                JGrassMapsetGeoResource map = (JGrassMapsetGeoResource) resource;
                                File refFile = map.getFile();
                                File mapsetFile = new File(mapset);

                                if (refFile.getAbsolutePath()
                                        .equals(mapsetFile.getAbsolutePath())) {
                                    neededCatalogMembers.add(resource);
                                }
                            } else {
                                neededCatalogMembers.add(resource);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (neededCatalogMembers != null) {
                return neededCatalogMembers.toArray();
            } else {
                return null;
            }
        }

        /**
         * Disposes any created resources
         */
        @Override
        public void dispose() {
            // Nothing to dispose
        }

        /**
         * Called when the input changes
         *
         * @param arg0 the viewer
         * @param arg1 the old input
         * @param arg2 the new input
         */
        @Override
        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            // Nothing to change
        }
    }

    /**
     * This class provides the labels for the file tree
     */

    private static class LabelProvider implements ILabelProvider {
        // The listeners
        private final List<ILabelProviderListener> listeners;

        // Images for tree nodes
        private final Image rasterMaps;

        private final Image mainRasterMaps;

        private final Image grassasciiRasterMaps;

        private final Image esriasciiRasterMaps;

        private final Image fluidturtleRasterMaps;

        private final Image problemRasterMaps;

        /**
         * Constructs a FileTreeLabelProvider
         */
        public LabelProvider() {
            // Create the list to hold the listeners
            listeners = new ArrayList<>();

            // Create the images
            rasterMaps = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ)
                    .createImage();
            mainRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/jgrass_obj.gif") //$NON-NLS-1$
                    .createImage();
            grassasciiRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/grassascii.gif") //$NON-NLS-1$
                    .createImage();
            esriasciiRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/esrigrid.gif") //$NON-NLS-1$
                    .createImage();
            fluidturtleRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/ftraster.gif") //$NON-NLS-1$
                    .createImage();
            problemRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/problem.gif") //$NON-NLS-1$
                    .createImage();
        }

        /**
         * Gets the image to display for a node in the tree
         *
         * @param arg0 the node
         * @return Image
         */
        @Override
        public Image getImage(Object arg0) {
            if (arg0 instanceof JGrassMapsetGeoResource) {
                return mainRasterMaps;
            } else if (arg0 instanceof JGrassMapGeoResource) {
                // support all raster types known
                if (((JGrassMapGeoResource) arg0).getType()
                        .equals(JGrassConstants.GRASSBINARYRASTERMAP)) {

                    return rasterMaps;
                } else if (((JGrassMapGeoResource) arg0).getType()
                        .equals(JGrassConstants.GRASSASCIIRASTERMAP)) {
                    return grassasciiRasterMaps;
                } else if (((JGrassMapGeoResource) arg0).getType()
                        .equals(JGrassConstants.ESRIRASTERMAP)) {
                    return esriasciiRasterMaps;
                } else if (((JGrassMapGeoResource) arg0).getType()
                        .equals(JGrassConstants.FTRASTERMAP)) {
                    return fluidturtleRasterMaps;
                } else {
                    return problemRasterMaps;
                }

            } else {
                return null;
            }
        }

        /**
         * Gets the text to display for a node in the tree
         *
         * @param arg0 the node
         * @return String
         */
        @Override
        public String getText(Object arg0) {

            String text = null;
            try {
                if (arg0 instanceof JGrassMapsetGeoResource) {
                    String locationName = ((JGrassService) ((JGrassMapsetGeoResource) arg0)
                            .parent(null)).getInfo(null).getTitle();
                    String mapsetName;
                    mapsetName = ((JGrassMapsetGeoResource) arg0).getTitle();

                    text = locationName
                            + Messages.getString(
                                    "JGRasterCatalogTreeViewer.loc-mapset-name-delimiter") //$NON-NLS-1$
                            + mapsetName;
                } else if (arg0 instanceof JGrassMapGeoResource) {
                    text = ((JGrassMapGeoResource) arg0).getInfo(null).getTitle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return text;
        }

        /**
         * Adds a listener to this label provider
         *
         * @param arg0 the listener
         */
        @Override
        public void addListener(ILabelProviderListener arg0) {
            listeners.add(arg0);
        }

        /**
         * Called when this LabelProvider is being disposed
         */
        @Override
        public void dispose() {
            // Dispose the images
            if (rasterMaps != null)
                rasterMaps.dispose();
        }

        /**
         * Returns whether changes to the specified property on the specified element would affect
         * the label for the element
         *
         * @param arg0 the element
         * @param arg1 the property
         * @return boolean
         */
        @Override
        public boolean isLabelProperty(Object arg0, String arg1) {
            return false;
        }

        /**
         * Removes the listener
         *
         * @param arg0 the listener to remove
         */
        @Override
        public void removeListener(ILabelProviderListener arg0) {
            listeners.remove(arg0);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.hydrologis.jgrass.ui.utilities.ResourcesSelector#getSelectedLayers()
     */
    @Override
    public List<IGeoResource> getSelectedLayers() {
        return itemLayers;
    }

}
