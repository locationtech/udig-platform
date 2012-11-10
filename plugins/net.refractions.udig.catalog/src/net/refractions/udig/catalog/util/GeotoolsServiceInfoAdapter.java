/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.util;

import java.net.URI;
import java.util.Set;

import javax.swing.Icon;

import net.refractions.udig.catalog.IServiceInfo;

import org.geotools.data.ServiceInfo;

/**
 * Adapts the geotools {@link ServiceInfo} info to a IServiceInfo
 * @author jesse
 * @since 1.1.0
 */
public class GeotoolsServiceInfoAdapter extends IServiceInfo implements ServiceInfo {

    private final ServiceInfo delegate;

    public GeotoolsServiceInfoAdapter(ServiceInfo delegate){
        this.delegate = delegate;
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public Icon getIcon() {
        //return delegate.getIcon();
    	return null;
    }

    public Set<String> getKeywords() {
        return delegate.getKeywords();
    }

    public URI getPublisher() {
        return delegate.getPublisher();
    }

    public URI getSchema() {
        return delegate.getSchema();
    }

    public URI getSource() {
        return delegate.getSource();
    }

    public String getTitle() {
        return delegate.getTitle();
    }
}
