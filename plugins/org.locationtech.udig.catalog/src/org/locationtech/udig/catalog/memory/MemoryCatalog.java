/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.memory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalogInfo;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ISearch;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.internal.Messages;
import org.locationtech.udig.catalog.util.AST;
import org.locationtech.udig.catalog.util.ASTFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Envelope;

/**
 * A very simple catalog to hold search results in memory.
 * 
 * @author Jody Garnett
 * @since 1.2.4
 */
public class MemoryCatalog extends ISearch {

    ICatalogInfo info;
    List<IService> services;
    private ID id;
    private String title;

    /**
     * Create a catalog; id often determined from search results.
     * <p>
     * Example:
     * 
     * <pre>
     * IRepository local = CatalogPlugin.getDefault().getLocalCatalog();
     * MemoryCatalog localFile = new ID(local.getID(), &quot;file&quot;);
     * </pre>
     * 
     * @param id
     */
    public MemoryCatalog( ID id, String title, List<IService> services ) {
        this.id = id;
        this.title = title;
        this.services = services;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        return false;
    }
    @Override
    public Status getStatus() {
        return Status.CONNECTED;
    }

    public Throwable getMessage() {
        return null;
    }

    public URL getIdentifier() {
        return id.toURL();
    }

    public ID getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) return null;

        if (monitor == null) monitor = new NullProgressMonitor();

        try {
            if (adaptee.isInstance(this)) {
                return adaptee.cast(this);
            }
            if (adaptee.isInstance(info)) {
                return adaptee.cast(info);
            }
        } finally {
            monitor.worked(1);
            monitor.done();
        }
        return null;
    }

    @Override
    public List<IResolve> find( ID resourceId, IProgressMonitor monitor ) {
        if (monitor == null) monitor = new NullProgressMonitor();

        URL query = resourceId.toURL();
        Set<IResolve> found = new LinkedHashSet<IResolve>();

        monitor.beginTask("find " + resourceId, services.size());
        for( IService service : services ) {
            ID id = service.getID();
            URL identifier = service.getIdentifier();

            if (URLUtils.urlEquals(query, identifier, true)) {
                if (query.getRef() == null && URLUtils.urlEquals(query, identifier, false)) {
                    found.add(service);
                } else {
                    IResolve res = getChildById(service, resourceId, monitor);
                    if (res != null) {
                        found.add(res);
                    }
                }
            }
            monitor.worked(1);
        }
        monitor.done();
        return new ArrayList<IResolve>(found);
    }

    @Override
    public <T extends IResolve> T getById( Class<T> type, ID id, IProgressMonitor monitor ) {
        return null;
    }

    IResolve getChildById( IResolve handle, final ID id, IProgressMonitor monitor ) {

        if (monitor == null) monitor = new NullProgressMonitor();

        if (id.equals(handle.getID())) {
            // rough match
            return handle;
        }

        try {
            List< ? extends IResolve> children = handle.members(monitor);

            if (children == null || children.isEmpty()) {
                return null;
            }
            for( IResolve child : children ) {
                IResolve found = getChildById(child, id, null);
                if (found != null) {
                    return found;
                }
            }
        } catch (IOException e) {
            CatalogPlugin.log("Could not search children of " + handle.getIdentifier(), e); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {
        if (CatalogPlugin.getDefault().isDebugging()) {
            if (Display.getCurrent() != null) {
                throw new IllegalStateException("search called from display thread");
            }
        }
        if (monitor == null) monitor = new NullProgressMonitor();
        
        List<IResolve> result = new LinkedList<IResolve>();
        if ((pattern == null || pattern.trim().length() == 0) && (bbox == null || bbox.isNull())) {
            // nothing to find
            return result;
        }
        AST ast = ASTFactory.parse(pattern);
        if (ast == null) {
            return result;
        }
        HashSet<IService> searchScope = new HashSet<IService>();
        searchScope.addAll(this.services);
        try {
            monitor.beginTask(Messages.CatalogImpl_finding, searchScope.size() * 10);
            SERVICE: for( IService service : searchScope ) {
                ID serviceID = service.getID();
                if (check(service, ast)) {
                    result.add(service);
                }
                // Iterator< ? extends IGeoResource> resources;
                SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
                try {
                    List< ? extends IGeoResource> members = service.resources(submonitor);
                    if (members == null) {
                        continue SERVICE;
                    }
                    for( IGeoResource resource : members ) {
                        ID resoruceID = resource.getID();
                        try {
                            if (check(resource, ast, bbox)) {
                                result.add(resource);
                            }
                        } catch (Throwable t) {
                            CatalogPlugin.log("Could not search in resource:" + resoruceID, t);
                        }
                    }
                } catch (IOException e) {
                    CatalogPlugin.log("Could not search in service:" + serviceID, e);
                } finally {
                    submonitor.done();
                }
                Thread.yield(); // allow other threads to have a go... makes search view more
                                // responsive
            }
            return result;
        } finally {
            monitor.done();
        }
    }

    /* check the fields we catre about */
    protected static boolean check( IService service, AST pattern ) {
        if (pattern == null) {
            return false;
        }
        IServiceInfo info;
        try {
            info = service == null ? null : service.getInfo(null);
        } catch (IOException e) {
            info = null;
            CatalogPlugin.log(null, e);
        }
        boolean t = false;
        if (info != null) {
            if (info.getTitle() != null)
                t = pattern.accept(info.getTitle());
            if (!t && info.getKeywords() != null) {
                String[] keys = info.getKeywords().toArray(new String[0]);
                for( int i = 0; !t && i < keys.length; i++ )
                    if (keys[i] != null)
                        t = pattern.accept(keys[i]);
            }
            if (!t && info.getSchema() != null)
                t = pattern.accept(info.getSchema().toString());
            if (!t && info.getAbstract() != null)
                t = pattern.accept(info.getAbstract());
            if (!t && info.getDescription() != null)
                t = pattern.accept(info.getDescription());
        }
        return t;
    }
    /* check the fields we catre about */
    protected static boolean check( IGeoResource resource, AST pattern ) {
        if (pattern == null) {
            return true;
        }
        IGeoResourceInfo info;
        try {
            info = (resource == null ? null : resource.getInfo(null));
        } catch (IOException e) {
            CatalogPlugin.log(null, e);
            info = null;
        }
        if (info == null) {
            return false;
        }
        if (pattern.accept(info.getTitle())) {
            return true;
        }
        if (pattern.accept(info.getName())) {
            return true;
        }
        if (info.getKeywords() != null) {
            for( String key : info.getKeywords() ) {
                if (pattern.accept(key)) {
                    return true;
                }
            }
        }
        if (info.getSchema() != null && pattern.accept(info.getSchema().toString())) {
            return true;
        }
        if (pattern.accept(info.getDescription())) {
            return true;
        }
        return false;
    }

    protected static boolean check( IGeoResource resource, AST pattern, Envelope bbox ) {
        if (!check(resource, pattern)) {
            return false;
        }
        if (bbox == null || bbox.isNull()) {
            return true; // no checking here
        }
        try {
            ReferencedEnvelope bounds = resource.getInfo(null).getBounds();
            if (bounds == null) {
                return true; // bounds are unknown!
            }
            return bbox.intersects(bounds);
        } catch (Throwable e) {
            CatalogPlugin.log(null, e);
            return false;
        }
    }
}
