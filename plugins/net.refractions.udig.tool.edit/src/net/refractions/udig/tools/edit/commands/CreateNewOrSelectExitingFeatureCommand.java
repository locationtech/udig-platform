/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.tool.edit.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

/**
 * If there is no feature with the Feature ID in the layer then a new feature will be
 * created.  Otherwise the feature's geometry will be set.
 * 
 * @author jones
 * @since 1.1.0
 */
public class CreateNewOrSelectExitingFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    private Geometry geom;
    private ILayer layer;
    private String fid;
    private boolean createFeature;
    private UndoableMapCommand modifyCommand;
    private SimpleFeature feature;
    private UndoableMapCommand createCommand;

    public CreateNewOrSelectExitingFeatureCommand( String fid2, ILayer layer2, Geometry geom2 ) {
        this.fid=fid2;
        this.layer=layer2;
        this.geom=geom2;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.CreateOrSetFeature_name, 10);
        monitor.worked(1);
        FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(FeatureStore.class, new SubProgressMonitor(monitor,2));
        Filter id = FeatureUtils.id(fid);
        String typeName = store.getSchema().getTypeName();
        DefaultQuery query = new DefaultQuery(typeName, id);
        FeatureIterator<SimpleFeature> iter=store.getFeatures( query ).features();
        try{
            createFeature=!iter.hasNext();
        }finally{
            iter.close();
        }
        if( createFeature ){
            createFeature(new SubProgressMonitor(monitor, 8));
        }else{
            modifyFeature(new SubProgressMonitor(monitor, 8));
        }
    }

    /**
     * Modifies the existing feature.
     * 
     * @param monitor
     * @throws Exception 
     */
    private void modifyFeature( IProgressMonitor monitor ) throws Exception {
        
        modifyCommand=EditCommandFactory.getInstance().createSetGeomteryCommand(fid, layer, geom);
        modifyCommand.setMap(getMap());
        modifyCommand.run(monitor);
    }

    /**
     * Creates a new feature
     * @param monitor
     */
    private void createFeature( IProgressMonitor monitor ) throws Exception  {
        Object[] attributeArray = new Object[layer.getSchema().getAttributeCount()];
        feature=SimpleFeatureBuilder.build(layer.getSchema(), attributeArray, "newFeature"); //$NON-NLS-1$
        feature.setDefaultGeometry(geom);
        createCommand=EditCommandFactory.getInstance().createAddFeatureCommand(feature, layer);
        createCommand.setMap(getMap());
        createCommand.run(monitor);
    }

    public String getName() {
        return Messages.CreateOrSetFeature_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( createFeature ){
            createCommand.rollback(monitor);
        }else{
            modifyCommand.rollback(monitor);
        }
    }

}
