/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.geotools.process;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.process.ProcessFactory;
import org.opengis.feature.type.Name;
import org.opengis.util.InternationalString;

public class LocalProcessFolder implements IResolve {

    private ProcessFactory factory;
    private LocalProcessService service;

    /**
     * Contents are presented as a series of folders; once for each process factory.
     */
    private volatile List<IResolve> members;
    private ID id;

    /**
     * LocalProcessFolder constructor with package visibility as it should only be constructed by
     * LocalProcessService.
     * 
     * @param service
     * @param factory
     */
    LocalProcessFolder( LocalProcessService service, ProcessFactory factory ) {
        this.service = service;
        this.factory = factory;
        this.id = new ID( service.getID() + "/" + factory.getClass().getSimpleName(), "local" );
    }
    
    public ProcessFactory getFactory() {
        return factory;
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isInstance(factory)) {
            return adaptee.cast(factory);
        }
        IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
        if (rm.canResolve(this, adaptee)) {
            return rm.resolve(this, adaptee, monitor);
        }
        return null; // no adapter found (check to see if ResolveAdapter is registered?)
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        return adaptee.isInstance(factory)
                || CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee);

    }

    public LocalProcessService parent( IProgressMonitor monitor ) throws IOException {
        return service;
    }

    public synchronized List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            members = new ArrayList<IResolve>();
            for( Name name : factory.getNames() ) {
                LocalProcess member = new LocalProcess(this, name);
                members.add(member);
            }
        }
        return members;
    }

    public Status getStatus() {
        if (factory == null) {
            return Status.NOTCONNECTED;
        } else if (factory.isAvailable()) {
            return Status.CONNECTED;
        } else {
            return Status.BROKEN;
        }
    }

    public Throwable getMessage() {
        if (factory == null) {
            return new IllegalStateException("Factory registered, but not available");
        } else if (factory.isAvailable()) {
            return null;
        } else {
            return new IllegalStateException(factory.getClass().getName() + " not avaialble");
        }
    }

    public URL getIdentifier() {
        return getID().toURL();
    }

    public ID getID() {
        return id;
    }

    public String getTitle() {
        if( factory == null ){
            return "Unavailable";
        }
        InternationalString title = factory.getTitle();
        String text = title == null ? null : title.toString();
        if( text == null ){
            return factory.getClass().getSimpleName();
        }
        else {
            return text;
        }
    }

    public void dispose( IProgressMonitor monitor ) {
        // TODO Auto-generated method stub

    }

}
