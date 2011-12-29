/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.split;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.edit.AbstractEditCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;

import eu.udig.tools.geometry.internal.util.GeometryUtil;
/**
 * Modify the geomety of feature
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 */

final class ModifyGeometryFeatureCommand extends AbstractEditCommand
        implements
            UndoableMapCommand {

    private final String fid;
    private final Geometry newGeometry;
    private final Geometry oldGeometry;
    private final ILayer layer;



    /**
     * New instance of ModifyGeometryFeatureCommand
     * 
     * @param fid	feature id
     * @param newGeometry the new geometry
     * @param oldGeometry the old geometry
     * @param layer			the layer that contains the feature that will be update
     */
    public ModifyGeometryFeatureCommand( 
    		final String fid,
            final Geometry newGeometry, 
            final Geometry oldGeometry,
            final ILayer layer ) {
        
        this.fid = fid;
        this.newGeometry = newGeometry;
        this.oldGeometry = oldGeometry;
        this.layer = layer;
    }


    public String getName() {
        return ModifyGeometryFeatureCommand.class.getName();
    }


    /**
     * 
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        execute(monitor);
        
    }

    /**
     * 
     * @param monitor
     * @return true to be added in the undo stack
     * @throws Exception
     */
    public boolean execute( IProgressMonitor monitor ) throws Exception {


        // modify the feature
        modifyFeatureInStore(
        		this.fid,
                (Geometry) this.newGeometry, 
                this.layer);

        ProjectPlugin
        .log(ModifyGeometryFeatureCommand.class.getName()
                + " - Feature Modified: fid - "+ this.fid +" - "+  this.newGeometry.toText()); //$NON-NLS-1$ //$NON-NLS-2$
        
        return true; 
    }
    
    /**
     * back to the old geometry
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {

    	
    	modifyFeatureInStore(this.fid, this.oldGeometry, this.layer);    
    	
    }

    /**
     * Find the feature on the store, and update its geometry.
     * 
     * @param fidToUpdate
     *            FID of the feature to be updated.
     * @param geometry
     *            The new geometry
     * @param store
     *            The feature store.
     * @throws SplitFeaturesCommandException if the feature cannot be modify
     */
    private void modifyFeatureInStore(  final String fidToUpdate,
                                        final Geometry geometry,
                                        final ILayer layer) 
                throws IOException {

        FeatureStore<SimpleFeatureType, SimpleFeature> store = layer.getResource(
                FeatureStore.class, new NullProgressMonitor());

        GeometryDescriptor geomAttr = store.getSchema().getGeometryDescriptor();
        Class< ? extends Geometry> expectedClass = (Class< ? extends Geometry>) geomAttr.getType()
                .getBinding();

        Geometry adaptedGeom = GeometryUtil.adapt(newGeometry, expectedClass);

        FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        FeatureId fid = ff.featureId(fidToUpdate);
        Set<FeatureId> ids = new HashSet<FeatureId>(1);
        ids.add(fid);
        Id filter = ff.id(ids);

        store.modifyFeatures(geomAttr, adaptedGeom, filter);
    }

    

}
