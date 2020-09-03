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
package org.locationtech.udig.style;

import org.eclipse.core.runtime.Plugin;

public class StylePlugin extends Plugin {

    /** The id of the plug-in */
    public static final String ID = "org.locationtech.udig.style"; //$NON-NLS-1$

    private static StylePlugin instance;

    /**
     * The constructor.
     */
    public StylePlugin() {
        super();
        instance = this;
    }

    public static StylePlugin getDefault() {
        return instance;
    }
}
