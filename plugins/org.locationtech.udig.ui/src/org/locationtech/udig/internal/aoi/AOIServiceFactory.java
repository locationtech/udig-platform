/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.aoi;

import org.locationtech.udig.aoi.IAOIService;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Responsible for creating our internal AOIServiceImpl (Area of Interest).
 *
 * @author pfeiffp
 */
public class AOIServiceFactory extends AbstractServiceFactory {

    @Override
    public IAOIService create( Class serviceInterface, IServiceLocator parentLocator,
            IServiceLocator locator ) {

        if (IAOIService.class.equals(serviceInterface)) {
            return new AOIServiceImpl();
        }

        return null;

    }

}
