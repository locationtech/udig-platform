/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.ui.wizard;


import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;


public class WMTConnectionFactory extends UDIGConnectionFactory {

    public boolean canProcess(Object context) {
        return false;
    }

    public Map<String, Serializable> createConnectionParameters(Object context) {
        return null;
    }

    public URL createConnectionURL(Object context) {
        return null;
    }
}
