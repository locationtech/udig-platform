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
package org.locationtech.udig.catalog.ui.workflow;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.IConnectionErrorHandler;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class ConnectionErrorHandlerProcessor implements ExtensionPointProcessor {

    IService s;
    Throwable t;
    ArrayList<IConnectionErrorHandler> handlers;

    public List<IConnectionErrorHandler> process( IService s, Throwable t ) {
        this.s = s;
        this.t = t;
        handlers = new ArrayList<IConnectionErrorHandler>();

        ExtensionPointUtil
                .process(CatalogUIPlugin.getDefault(), IConnectionErrorHandler.XPID, this);

        return handlers;
    }

    public void process( IExtension extension, IConfigurationElement element ) throws Exception {

        try {
            IConnectionErrorHandler handler = (IConnectionErrorHandler) element
                    .createExecutableExtension("class"); //$NON-NLS-1$
            if (handler.canHandle(s, t)) {
                handlers.add(handler);
            }
        } catch (Throwable t) {
            CatalogUIPlugin.log(t.getLocalizedMessage(), t);
        }
    }
}
