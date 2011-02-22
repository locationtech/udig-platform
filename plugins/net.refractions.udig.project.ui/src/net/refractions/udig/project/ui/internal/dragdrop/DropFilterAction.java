/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.dragdrop;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.ui.IDropAction;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.Feature;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactoryFinder;

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
        return true;
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
            	Feature feature = (Feature) ((IAdaptable) getData()).getAdapter(Feature.class);
                if( feature!=null )
                    filter = FilterFactoryFinder.createFilterFactory().createFidFilter(feature.getID());
                else
                    return;
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


}
