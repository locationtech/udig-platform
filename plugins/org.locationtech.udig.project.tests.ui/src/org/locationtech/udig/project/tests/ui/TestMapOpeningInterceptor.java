/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import org.junit.Ignore;

import org.locationtech.udig.project.interceptor.MapInterceptor;
import org.locationtech.udig.project.internal.Map;

/**
 * prints message when map is opened.
 * @author Jesse
 * @since 1.1.0
 */
@Ignore
public class TestMapOpeningInterceptor implements MapInterceptor {

    public TestMapOpeningInterceptor() {
        super();
    }

    public void run( Map map ) {
        System.out.println(map.getName()+" is being opened.  This is a test interceptor"); //$NON-NLS-1$
    }

}
