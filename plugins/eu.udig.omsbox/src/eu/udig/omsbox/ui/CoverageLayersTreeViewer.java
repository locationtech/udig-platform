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
package eu.udig.omsbox.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.opengis.coverage.grid.GridCoverage;

import eu.udig.omsbox.utils.ImageCache;

/**
 * Tree viewer containing coverage resources from the available layers.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageLayersTreeViewer extends Composite implements ISelectionChangedListener, IGeoResourcesSelector {

    private final HashMap<String, IGeoResource> itemsMap = new HashMap<String, IGeoResource>();
    private List<IGeoResource> selectedResources = new ArrayList<IGeoResource>();

    public CoverageLayersTreeViewer( Composite parent, int style, int selectionStyle ) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        setLayoutData(gridData);

        try {
            getCoverageResources();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the tree viewer to display the file tree
        PatternFilter patternFilter = new PatternFilter();
        final FilteredTree filter = new FilteredTree(this, selectionStyle, patternFilter, true);
        final TreeViewer tv = filter.getViewer();
        tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        tv.setContentProvider(new MyContentProvider());
        MyLabelProvider labelProvider = new MyLabelProvider();
        tv.setLabelProvider(labelProvider);
        tv.setInput(itemsMap);
        tv.addSelectionChangedListener(this);
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        // if the selection is empty clear the label
        ISelection iselection = event.getSelection();
        if (iselection.isEmpty()) {
            return;
        }
        if (iselection instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) iselection;
            for( Iterator< ? > iterator = selection.iterator(); iterator.hasNext(); ) {
                Object resource = iterator.next();
                IGeoResource iGeoResource = itemsMap.get(resource);
                if (iGeoResource != null)
                    selectedResources.add(iGeoResource);
            }
        }
    }

    private void getCoverageResources() throws IOException {
        IMap activeMap = ApplicationGIS.getActiveMap();
        List<ILayer> mapLayers = activeMap.getMapLayers();
        itemsMap.clear();
        for( ILayer layer : mapLayers ) {
            IGeoResource geoResource = layer.getGeoResource();
            if (geoResource.canResolve(GridCoverage.class)) {
                itemsMap.put(geoResource.getTitle(), geoResource);
            }
        }
    }

    private static class MyContentProvider implements ITreeContentProvider {
        public Object[] getElements( Object arg0 ) {
            return getChildren(arg0);
        }

        public void dispose() {
        }

        public void inputChanged( Viewer arg0, Object arg1, Object arg2 ) {
        }

        public Object[] getChildren( Object parentElement ) {
            if (parentElement instanceof HashMap< ? , ? >) {
                HashMap< ? , ? > map = (HashMap< ? , ? >) parentElement;
                Object[] array = map.keySet().toArray();
                Arrays.sort(array);
                return array;
            }
            return new Object[0];
        }

        public Object getParent( Object element ) {
            return null;
        }

        public boolean hasChildren( Object element ) {
            return getChildren(element).length > 0;
        }
    }

    private static class MyLabelProvider extends LabelProvider {
        public Image getImage( Object arg0 ) {
            return ImageCache.getInstance().getImage(ImageCache.GRID);
        }
        public String getText( Object arg0 ) {
            if (arg0 instanceof IGeoResource) {
                IGeoResource geoResr = (IGeoResource) arg0;
                return geoResr.getTitle();
            }
            if (arg0 instanceof String) {
                String name = (String) arg0;
                return name;
            }
            return null;
        }
    }

    public List<IGeoResource> getSelectedResources() {
        return selectedResources;
    }

}
