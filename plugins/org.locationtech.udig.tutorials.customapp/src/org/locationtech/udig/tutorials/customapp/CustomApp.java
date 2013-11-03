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
package org.locationtech.udig.tutorials.customapp;

import org.locationtech.udig.internal.ui.UDIGApplication;
import org.locationtech.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class CustomApp extends UDIGApplication implements IApplication {

    @Override
    protected WorkbenchAdvisor createWorkbenchAdvisor() {
        return new UDIGWorkbenchAdvisor() {
            @Override
            public String getInitialWindowPerspectiveId() {
                return "org.locationtech.udig.tutorials.customapp.perspective";
            }
        };
    }

}
