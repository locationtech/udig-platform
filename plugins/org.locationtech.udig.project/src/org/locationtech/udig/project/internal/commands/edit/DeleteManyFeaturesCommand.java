/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

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
    	FeatureStore<SimpleFeatureType, SimpleFeature> fs = ilayer.getResource(FeatureStore.class, monitor);
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
        SimpleFeature editFeature = editManager.getEditFeature();
        if (editFeature!=null && editManager.getEditLayer()==ilayer && filter.evaluate(editFeature))
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
            ReferencedEnvelope bounds=new ReferencedEnvelope();
            for( FeatureEvent event : eventList ) {
                if( bounds.isNull() )
                    bounds.init(event.getBounds().getMinX(), event.getBounds().getMaxX(), event.getBounds().getMinY(), event.getBounds().getMaxY());
                else
                    bounds.expandToInclude(event.getBounds());
            }
            if( !eventList.isEmpty()){
                FeatureEvent event=eventList.get(0);
                tmp.add(new FeatureEvent(event.getFeatureSource(), event.getType(), bounds));
            }
        }
    }

    public String getName() {
        return Messages.DeleteManyFeaturesCommand_name; 
    }

}
