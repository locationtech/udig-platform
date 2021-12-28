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
import org.geotools.data.DataStore;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;

/**
 * <p>
 * This class supplies a tree viewer containing the maps that are in the catalog.
 * </p>
 *
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class FeatureCatalogTreeViewer extends Composite
        implements ISelectionChangedListener, IResourcesSelector {

    public static final int SHAPELAYER = 0;

    public static final int GRASSRASTERLAYER = 1;

    private final HashMap<String, DataStore> itemsMap = new HashMap<>();

    private LabelProvider labelProvider = null;

    private List<DataStore> itemLayers;

    /**
     * @param parent
     * @param style
     * @param selectionStyle the tree selection style (single or multiple)
     */
    public FeatureCatalogTreeViewer(Composite parent, int style, int selectionStyle) {
        super(parent, style);
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
        tv.setInput("dummy"); // pass a non-null that will be ignored //$NON-NLS-1$
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
            for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
                Object domain = iterator.next();
                String value = labelProvider.getText(domain);
                itemNames.add(value);
            }
            itemLayers = new ArrayList<>();
            for (String name : itemNames) {
                DataStore tmpLayer = itemsMap.get(name);
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
            return null;
        }

        /**
         * Gets the parent of the specified object
         *
         * @param arg0 the object
         * @return Object
         */
        @Override
        public Object getParent(Object arg0) {
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
                // for jgrass locations add the service
                neededCatalogMembers = new ArrayList<>();
                for (IResolve catalogMember : allCatalogMembers) {
                    if (catalogMember.canResolve(DataStore.class)) {
                        neededCatalogMembers.add(catalogMember);
                        itemsMap.put(new File(catalogMember.getIdentifier().getPath()).getName(),
                                catalogMember.resolve(DataStore.class, null));
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
        private final Image vectorMaps;

        /**
         * Constructs a FileTreeLabelProvider
         */
        public LabelProvider() {
            // Create the list to hold the listeners
            listeners = new ArrayList<>();

            // Create the images
            vectorMaps = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.FEATURE_OBJ)
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
            if (arg0 instanceof IResolve) {
                return vectorMaps;
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
            if (arg0 instanceof IResolve) {
                text = new File(((IResolve) arg0).getIdentifier().getPath()).getName();
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
            if (vectorMaps != null)
                vectorMaps.dispose();
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

    @Override
    public List<DataStore> getSelectedLayers() {
        return itemLayers;
    }

}
