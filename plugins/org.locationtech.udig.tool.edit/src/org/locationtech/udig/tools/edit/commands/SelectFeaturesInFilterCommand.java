/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Add all the features to the EditBlackboard that are contained in the filter.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectFeaturesInFilterCommand extends AbstractCommand implements UndoableMapCommand {

    private Filter filter;
    private ILayer layer;
    private EditBlackboard bb;
    private ArrayList<UndoableMapCommand> commands;

    /**
     * Create new instance
     * @param blackboard blackboard to add selected features
     * @param layer layer used to obtain features.  Must have a FeatureStore resource.
     * @param filter filter used to select features
     */
    public SelectFeaturesInFilterCommand( EditBlackboard blackboard, ILayer layer, Filter filter ) {
        if( !layer.hasResource(FeatureStore.class) )
            throw new IllegalArgumentException("Layer must have a FeatureStore resource"); //$NON-NLS-1$
        this.filter=filter;
        this.layer=layer;
        this.bb=blackboard;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.AddFeaturesCommand_taskMessage, 10);
        monitor.worked(1);
        
        FeatureStore<SimpleFeatureType, SimpleFeature> store=layer.getResource(FeatureStore.class, new SubProgressMonitor(monitor, 2));
        String geomAttributeName = layer.getSchema().getGeometryDescriptor().getLocalName();
        String[] desiredProperties = new String[]{geomAttributeName};
        Query query=new Query(layer.getSchema().getTypeName(), filter, 
                desiredProperties);
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = store.getFeatures(query);

        FeatureIterator<SimpleFeature> iter=features.features();
        try{
            commands=new ArrayList<UndoableMapCommand>();
            while( iter.hasNext() ){
                SimpleFeature feature=iter.next();
                commands.add(new SelectFeatureCommand(bb, feature));
            }

            float index=0;
            float inc=(float)7/commands.size();
            for( UndoableMapCommand command : commands ) {
                command.setMap(getMap());
                index+=inc;
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, (int) index);
                command.run(subProgressMonitor);
                subProgressMonitor.done();
                if( index>1 ){
                    index=0;
                }
            }
        }finally{
            iter.close();
        }
        
        monitor.done();
        
    }

    public String getName() {
        return null;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.AddFeaturesCommand_undoTaskMessage, commands.size()*2);
        
        for( int i=commands.size()-1; i>-1; i--){
            SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 2);
            commands.get(i).rollback(subProgressMonitor);
            subProgressMonitor.done();
        }
        monitor.done();
    }

}
