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
package net.refractions.udig.catalog.shp;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.internal.shp.ShpServiceExtension;
import net.refractions.udig.ui.CharsetSelectionDialog;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;

/**
 * An Operation for changing the charset of a shapefile datastore. Operations on
 * {@link ShpGeoResourceImpl} or {@link IService}
 * 
 * @author jesse
 * @since 1.1.0
 */
public class CharsetChange implements IOp {
    private final ShpServiceExtension extension = new ShpServiceExtension();

    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        
        final IService[] services = toService((Object[]) target);

        display.asyncExec(new Runnable(){

            public void run() {
                FilteredItemsSelectionDialog dialog;
                try {
                    dialog = new CharsetSelectionDialog(display.getActiveShell(), false,
                            getCharset(services[0]).name());
                    dialog.open();
                    final Charset newCharset = (Charset) dialog.getFirstResult();

                    if( newCharset!=null){
                        setNewCharset(services, newCharset);
                    }
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException().initCause(e);
                }
            }

            private void setNewCharset( final IService[] services, final Charset newCharset ) {
                PlatformGIS.run(new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor )
                            throws InvocationTargetException, InterruptedException {
                        for( IService service : services ) {
                            Map<String, Serializable> params = new HashMap<String, Serializable>(service
                                    .getConnectionParams());
                            params.put(ShapefileDataStoreFactory.DBFCHARSET.key, newCharset.name());
                            
                            IService newService = extension.createService(service.getIdentifier(), params);
                            CatalogPlugin.getDefault().getLocalCatalog().replace(service.getID(), newService);
                        }
                    }
                    
                });
            }

            private Charset getCharset( IService serviceImpl ) throws IOException {
                Object lookUp = ShapefileDataStoreFactory.DBFCHARSET.lookUp(serviceImpl
                        .getConnectionParams());
                String name = null;
                if (lookUp instanceof String) {
                    name = (String) lookUp;
                }
                if( name==null ){
                    return Charset.defaultCharset();
                }
                return Charset.forName(name);
            }

        });


    }

    private IService[] toService( Object[] target ) throws IOException {
        Set<IService> result = new HashSet<IService>(target.length);
        for( Object object : target ) {
            if (object instanceof IService) {
                result.add((IService) object);
            } else {
                result.add((IService) ((IGeoResource) object).service(new NullProgressMonitor()));
            }
        }
        return result.toArray(new IService[result.size()]);
    }

}
