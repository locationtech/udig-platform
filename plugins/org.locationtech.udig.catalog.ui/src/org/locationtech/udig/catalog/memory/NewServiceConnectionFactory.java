/**
 * 
 */
package org.locationtech.udig.catalog.memory;

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.memory.internal.MemoryGeoResourceImpl;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;
import org.locationtech.udig.catalog.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A ConnectionFactory for creating a new layer.  The data will reside on disk.
 * 
 * @author jones
 */
public class NewServiceConnectionFactory extends UDIGConnectionFactory {

    @Override
    public boolean canProcess( Object context ) {
        return context instanceof MemoryGeoResourceImpl;
    }

    @Override
    public Map<String, Serializable> createConnectionParameters( Object context) {
        Map<String, Serializable> params=null;
        
        IProgressMonitor monitor=new NullProgressMonitor();
        try {
            MemoryServiceExtensionImpl ext = new MemoryServiceExtensionImpl();
            URL id = new URL("http://localhost/scratch"); //$NON-NLS-1$
            params = ext.createParams(id);
            IService service = ext.createService(id, params);
            DataStore ds = service.resolve(DataStore.class, monitor);
            int i=0;
            String typename="New_Type_"; //$NON-NLS-1$
            List<String> typenames = Arrays.asList(ds.getTypeNames());
            while( typenames.contains(typename+i)){
                i++;
            }
            SimpleFeatureTypeBuilder build = new SimpleFeatureTypeBuilder();
            build.setName(typename+i);
            build.setNamespaceURI( "http://udig.refractions.net");
            build.setAbstract(false);
            build.add(Messages.NewServiceConnectionFactory_defaultGeom,org.locationtech.jts.geom.Geometry.class);
            
            SimpleFeatureType schema = build.buildFeatureType(); 
            
            ds.createSchema( schema ); //$NON-NLS-1$
        } catch (Exception e) {
            CatalogUIPlugin.log("Error creating MemoryDatastore or feature type", e); //$NON-NLS-1$
            return null;
        }finally{
            monitor.done();
        }
        return params;
    }

    @Override
    public URL createConnectionURL( Object context ) {
        return null;
    }



}
