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
package net.refractions.udig.project.internal.commands.edit;

import java.util.List;

import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Deletes a set of features based on a filter.
 *
 * @author jones
 * @since 1.0.0
 */
public class DeleteManyFeaturesCommand extends AbstractCommand {

    private ILayer ilayer;
    private Filter filter;

    public DeleteManyFeaturesCommand( ILayer layer, Filter filter ) {
        this.ilayer=layer;
        this.filter=filter;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        FeatureStore fs = ilayer.getResource(FeatureStore.class, monitor);
        Layer layer=null;
        if( ilayer instanceof Layer ){
            layer=(Layer)ilayer;
        }
        int events=0;
        try{
            if( layer!=null ){
                layer.eSetDeliver(false);
                events=layer.getFeatureChanges().size();
            }
        fs.removeFeatures(filter);
        IEditManager editManager = getMap().getEditManager();
        Feature editFeature = editManager.getEditFeature();
        if (editFeature!=null && editManager.getEditLayer()==ilayer && filter.contains(editFeature))
            getMap().getEditManagerInternal().setEditFeature(null, null);
        }finally{
            fireFeatureChangeEvent(layer, events);
        }
    }

    private void fireFeatureChangeEvent( Layer layer, int events ) {
        if ( layer!=null ){
            layer.eSetDeliver(true);
            List<FeatureEvent> tmp=layer.getFeatureChanges();
            List<FeatureEvent> eventList=tmp.subList(events, tmp.size());
            Envelope bounds=new Envelope();
            for( FeatureEvent event : eventList ) {
                if( bounds.isNull() )
                    bounds.init(event.getBounds());
                else
                    bounds.expandToInclude(event.getBounds());
            }
            if( !eventList.isEmpty()){
                FeatureEvent event=eventList.get(0);
                tmp.add(new FeatureEvent(event.getFeatureSource(), event.getEventType(), bounds));
            }
        }
    }

    public String getName() {
        return Messages.DeleteManyFeaturesCommand_name;
    }

}
