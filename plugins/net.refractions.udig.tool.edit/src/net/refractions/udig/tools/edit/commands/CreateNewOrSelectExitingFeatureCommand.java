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
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.tool.edit.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FilterFactoryFinder;

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
    private Feature feature;
    private UndoableMapCommand createCommand;

    public CreateNewOrSelectExitingFeatureCommand( String fid2, ILayer layer2, Geometry geom2 ) {
        this.fid=fid2;
        this.layer=layer2;
        this.geom=geom2;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.CreateOrSetFeature_name, 10);
        monitor.worked(1);
        FeatureStore store = layer.getResource(FeatureStore.class, new SubProgressMonitor(monitor,2));
        FeatureIterator iter=store.getFeatures( new DefaultQuery(store.getSchema().getTypeName(), FilterFactoryFinder.createFilterFactory().createFidFilter(fid)) ).features();
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
        feature=layer.getSchema().create(new Object[layer.getSchema().getAttributeCount()]);
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
