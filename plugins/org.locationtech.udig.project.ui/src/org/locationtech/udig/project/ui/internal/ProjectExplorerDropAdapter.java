/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;

public class ProjectExplorerDropAdapter extends ViewerDropAdapter {

    protected ProjectExplorerDropAdapter( Viewer viewer ) {
        super(viewer);
    }

    @Override
    public boolean performDrop( Object data ) {

        /*
         * First, get the data we can work with - Layers.
         */

        List<Layer> layers = getLayers(data);

        /*
         * What we do next depends on where the data is being dropped, and where it is coming from.
         */

        /*
         * They are being dropped on a Map
         */
        if (getCurrentLocation() == LOCATION_ON && getCurrentTarget() instanceof Map) {
            Map map = (Map) getCurrentTarget();

            /*
             * If all the layers belong to the target map, do nothing.
             */
            if (map.getContextModel().getLayers().containsAll(layers)) {
                return false;
            }
            copyToMap(map, layers);
        }

        /*
         * They are being dropped on a Project
         */
        if (getCurrentLocation() == LOCATION_ON && getCurrentTarget() instanceof Project) {
            createNewMap((Project) getCurrentTarget(), layers);
        }

        return true;
    }

    private void createNewMap( Project project, List<Layer> layers ) {
        MapFactory.instance().process(project, layers, true);
    }

    private void copyToMap( Map map, List<Layer> layers ) {
        Collection<Layer> clonedLayers = EcoreUtil.copyAll(layers);
        map.getContextModel().getLayers().addAll(clonedLayers);
    }

    private List<Layer> getLayers( Object data ) {
        List<Object> resources = new ArrayList<Object>();

        if (data instanceof String || data instanceof String[]) {
            if (data instanceof String) {
                resources.addAll(CorePlugin.stringsToURLs((String) data));
            } else {
                resources.addAll(CorePlugin.stringsToURLs((String[]) data));
            }
        } else if (data instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) data;

            resources.addAll(selection.toList());
        } else {
            resources.add(data);
        }

        return MapFactory.instance().processResources(null, resources);
    }

    @Override
    public boolean validateDrop( Object target, int operation, TransferData transferType ) {
        return true;
    }

}
