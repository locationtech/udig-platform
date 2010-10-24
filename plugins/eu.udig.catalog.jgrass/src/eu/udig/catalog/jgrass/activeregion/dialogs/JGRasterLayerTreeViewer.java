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
package eu.udig.catalog.jgrass.activeregion.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
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

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;

/**
 * <p>
 * This class supplies a tree viewer containing the JGrass raster maps that are visible inside the
 * active map. When a layer is selected it is passed to the WidgetObservers that are registered with
 * this class.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGRasterLayerTreeViewer extends Composite implements ISelectionChangedListener, IResourcesSelector {

    public final static int GRASSRASTERLAYER = 1;

    private final HashMap<String, ILayer> itemsMap = new HashMap<String, ILayer>();
    private LabelProvider labelProvider = null;

    private List<IGeoResource> itemLayers;

    private String mapsetPath;

    /**
     * @param parent
     * @param style
     * @param selectionStyle the tree selection style (single or multiple)
     * @param mapsetPath mapset path on which to limit the view (can be null to visualize
     *        everything)
     */
    public JGRasterLayerTreeViewer( Composite parent, int style, int selectionStyle, String mapsetPath ) {
        super(parent, style);
        if (mapsetPath != null)
            this.mapsetPath = mapsetPath;
        setLayout(new GridLayout(1, false));
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        setLayoutData(gridData);

        // Create the tree viewer to display the file tree
        PatternFilter patternFilter = new PatternFilter();
        final FilteredTree filter = new FilteredTree(this, selectionStyle, patternFilter);
        final TreeViewer tv = filter.getViewer();
        tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        tv.setContentProvider(new ContentProvider());
        labelProvider = new LabelProvider();
        tv.setLabelProvider(labelProvider);
        tv.setInput("dummy"); // pass a non-null that will be ignored //$NON-NLS-1$
        tv.addSelectionChangedListener(this);
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        // if the selection is empty clear the label
        if (event.getSelection().isEmpty()) {
            return;
        }
        if (event.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Vector<String> itemNames = new Vector<String>();
            for( Iterator iterator = selection.iterator(); iterator.hasNext(); ) {
                Object domain = iterator.next();
                String value = labelProvider.getText(domain);
                itemNames.add(value);
            }
            itemLayers = new ArrayList<IGeoResource>();
            for( String name : itemNames ) {
                ILayer tmpLayer = itemsMap.get(name);
                if (tmpLayer != null) {
                    if (tmpLayer.getGeoResource().canResolve(JGrassMapGeoResource.class)) {
                        JGrassMapGeoResource rasterMapResource = null;
                        try {
                            rasterMapResource = tmpLayer.getGeoResource().resolve(JGrassMapGeoResource.class, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (rasterMapResource != null) {
                            itemLayers.add(rasterMapResource);
                        }
                    }

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
        public Object[] getChildren( Object arg0 ) {

            if (arg0 instanceof IMap) {
                IMap map = (IMap) arg0;

                List<ILayer> layers = map.getMapLayers();

                return filteredLayers(layers);
            } else if (arg0 instanceof ILayer) {
                return null;
            }

            return null;
        }

        private Object[] filteredLayers( List<ILayer> layers ) {
            Vector<ILayer> filteredLayers = new Vector<ILayer>();
            try {
                for( ILayer layer : layers ) {
                    if (layer.getGeoResource().canResolve(JGrassMapGeoResource.class)
                            && ((layer.getGeoResource().resolve(JGrassMapGeoResource.class, null)).getType().equals(
                                    JGrassConstants.GRASSBINARYRASTERMAP)
                                    || (layer.getGeoResource().resolve(JGrassMapGeoResource.class, null)).getType().equals(
                                            JGrassConstants.GRASSASCIIRASTERMAP)
                                    || (layer.getGeoResource().resolve(JGrassMapGeoResource.class, null)).getType().equals(
                                            JGrassConstants.FTRASTERMAP) || (layer.getGeoResource().resolve(
                                    JGrassMapGeoResource.class, null)).getType().equals(JGrassConstants.ESRIRASTERMAP)))

                    {
                        JGrassMapGeoResource mapResolve = layer.getGeoResource().resolve(JGrassMapGeoResource.class,
                                new NullProgressMonitor());
                        IResolve mapsetResolve = mapResolve.parent(new NullProgressMonitor());
                        // check for the limiting mapset
                        if (mapsetPath != null && mapsetResolve instanceof JGrassMapsetGeoResource) {
                            JGrassMapsetGeoResource map = (JGrassMapsetGeoResource) mapsetResolve;
                            File refFile = map.getFile();
                            File mapsetFile = new File(mapsetPath);

                            if (refFile.getAbsolutePath().equals(mapsetFile.getAbsolutePath())) {
                                filteredLayers.add(layer);
                                itemsMap.put(layer.getName(), layer);
                            }
                        } else {
                            filteredLayers.add(layer);
                            itemsMap.put(layer.getName(), layer);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
             * now let's sort them for nice visualization
             */
            HashMap<String, ILayer> tmp = new HashMap<String, ILayer>();
            for( ILayer resource : filteredLayers ) {
                tmp.put(resource.getName(), resource);
            }
            Map<String, ILayer> sortedMap = new TreeMap<String, ILayer>(tmp);
            filteredLayers.removeAllElements();
            for( ILayer value : sortedMap.values() ) {
                filteredLayers.add(value);
            }

            return filteredLayers.toArray();
        }

        /**
         * Gets the parent of the specified object
         * 
         * @param arg0 the object
         * @return Object
         */
        public Object getParent( Object arg0 ) {
            if (arg0 instanceof IMap) {
                return null;
            } else if (arg0 instanceof ILayer) {
                return ((ILayer) arg0).getMap();
            }
            return null;
        }

        /**
         * Returns whether the passed object has children
         * 
         * @param arg0 the parent object
         * @return boolean
         */
        public boolean hasChildren( Object arg0 ) {
            if (arg0 instanceof IMap) {
                return true;
            } else if (arg0 instanceof ILayer) {
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
        public Object[] getElements( Object arg0 ) {
            IMap map = ApplicationGIS.getActiveMap();
            return new Object[]{map};
        }

        /**
         * Disposes any created resources
         */
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
        public void inputChanged( Viewer arg0, Object arg1, Object arg2 ) {
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
            listeners = new ArrayList<ILabelProviderListener>();

            // Create the images
            rasterMaps = CatalogUIPlugin.getDefault().getImages().getImageDescriptor(ISharedImages.GRID_OBJ).createImage();
            mainRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/jgrass_obj.gif").createImage(); //$NON-NLS-1$
            grassasciiRasterMaps = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID,
                    "icons/obj16/grassascii.gif").createImage(); //$NON-NLS-1$
            esriasciiRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/esrigrid.gif").createImage(); //$NON-NLS-1$
            fluidturtleRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/ftraster.gif").createImage(); //$NON-NLS-1$
            problemRasterMaps = AbstractUIPlugin
                    .imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/problem.gif").createImage(); //$NON-NLS-1$
        }

        /**
         * Gets the image to display for a node in the tree
         * 
         * @param arg0 the node
         * @return Image
         */
        public Image getImage( Object arg0 ) {
            if (arg0 instanceof IMap) {
                return mainRasterMaps;
            } else if (arg0 instanceof ILayer) {
                // support all raster types known
                try {
                    if (((ILayer) arg0).getGeoResource().resolve(JGrassMapGeoResource.class, null).getType()
                            .equals(JGrassConstants.GRASSBINARYRASTERMAP)) {

                        return rasterMaps;
                    } else if (((ILayer) arg0).getGeoResource().resolve(JGrassMapGeoResource.class, null).getType()
                            .equals(JGrassConstants.GRASSASCIIRASTERMAP)) {
                        return grassasciiRasterMaps;
                    } else if (((ILayer) arg0).getGeoResource().resolve(JGrassMapGeoResource.class, null).getType()
                            .equals(JGrassConstants.ESRIRASTERMAP)) {
                        return esriasciiRasterMaps;
                    } else if (((ILayer) arg0).getGeoResource().resolve(JGrassMapGeoResource.class, null).getType()
                            .equals(JGrassConstants.FTRASTERMAP)) {
                        return fluidturtleRasterMaps;
                    } else {
                        return problemRasterMaps;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;

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
        public String getText( Object arg0 ) {

            String text = null;
            if (arg0 instanceof IMap) {
                text = ((IMap) arg0).getName();
            } else if (arg0 instanceof ILayer) {
                text = ((ILayer) arg0).getName();
            }

            return text;
        }

        /**
         * Adds a listener to this label provider
         * 
         * @param arg0 the listener
         */
        public void addListener( ILabelProviderListener arg0 ) {
            listeners.add(arg0);
        }

        /**
         * Called when this LabelProvider is being disposed
         */
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
        public boolean isLabelProperty( Object arg0, String arg1 ) {
            return false;
        }

        /**
         * Removes the listener
         * 
         * @param arg0 the listener to remove
         */
        public void removeListener( ILabelProviderListener arg0 ) {
            listeners.remove(arg0);
        }
    }

    public List<IGeoResource> getSelectedLayers() {
        return itemLayers;
    }

}
