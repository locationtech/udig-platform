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
