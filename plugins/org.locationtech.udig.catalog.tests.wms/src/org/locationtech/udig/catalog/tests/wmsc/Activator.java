/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.wmsc;

import org.eclipse.core.runtime.Plugin;

public class Activator extends Plugin {
    private static Activator instance;
    public Activator() {
        instance = this;
    }
    public static Activator getDefault() {
        return instance;
    }
}
