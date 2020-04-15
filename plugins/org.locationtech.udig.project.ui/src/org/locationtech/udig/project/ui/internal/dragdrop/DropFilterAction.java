/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.dragdrop;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.IDropAction;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * If the filter can adapt to a layer then features from the layer are copied to the destination
 * layer otherwise the filter is set on the destination as the selection filter.
 * 
 * @author jones
 * @since 1.1.0
 */
public class DropFilterAction extends IDropAction {

    /**
     * 
     */
    public DropFilterAction() {
        super();
    }

    @Override
    public boolean accept( ) {
        //allow drop only if destination Layer different than source layer
        ILayer destinationLayer = getDestinationLayer();
        ILayer sourceLayer = ((IAdaptable)getData()).getAdapter(ILayer.class);
        return ObjectUtils.notEqual(destinationLayer, sourceLayer);
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Layer destinationLayer;
        IMap destinationMap;

        if( getDestination() instanceof IMap ){
            destinationMap=(IMap) getDestination();
            destinationLayer=(Layer) destinationMap.getEditManager().getSelectedLayer();
        }else if( getDestination() instanceof Layer ){
            destinationLayer=(Layer)getDestination();
            destinationMap=destinationLayer.getMap();
        }else {
            return;
        }

        if( getData() instanceof IAdaptable ){
            ILayer layer=(ILayer) ((IAdaptable)getData()).getAdapter(ILayer.class);
            Filter filter=(Filter) ((IAdaptable)getData()).getAdapter(Filter.class);

            if (filter == null) {
                SimpleFeature feature = (SimpleFeature) ((IAdaptable) getData()).getAdapter(SimpleFeature.class);
                FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
                if (feature != null) {
                    filter = filterFactory.id(FeatureUtils.stringToId(
                            filterFactory, feature.getID()));
                } else {
                    return;
                }
            }

            if( layer==null){
                setSelection(filter==null?(Filter)getData():filter, destinationLayer, monitor);
            }else{
                MapCommand c=EditCommandFactory.getInstance().createCopyFeaturesCommand( layer, filter, destinationLayer);
                destinationMap.sendCommandASync(c);
            }
        }else{
            setSelection((Filter)getData(), destinationLayer, monitor);
        }
    }

    private void setSelection( Filter filter, Layer layer, IProgressMonitor monitor ) {
        monitor.beginTask(Messages.DropFilterAction_taskname, 2); 
        monitor.worked(1);
        layer.setFilter(filter);
        monitor.done();
    }


    private ILayer getDestinationLayer() {
        Layer destinationLayer = null;
        if( getDestination() instanceof IMap ){
            destinationLayer=(Layer)((IMap)getDestination()).getEditManager().getSelectedLayer();
        } else if( getDestination() instanceof Layer ){
            destinationLayer=(Layer)getDestination();
        }
        return destinationLayer;
    }
}
