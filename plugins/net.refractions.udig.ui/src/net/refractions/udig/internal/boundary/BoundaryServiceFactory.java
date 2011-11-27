/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package net.refractions.udig.internal.boundary;

import net.refractions.udig.boundary.IBoundaryService;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Responsible for creating our internal BoundaryServiceImpl.
 * 
 * @author pfeiffp
 */
public class BoundaryServiceFactory extends AbstractServiceFactory {

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class,
     * org.eclipse.ui.services.IServiceLocator, org.eclipse.ui.services.IServiceLocator)
     */
    @Override
    public IBoundaryService create( Class serviceInterface, IServiceLocator parentLocator,
            IServiceLocator locator ) {

        if (IBoundaryService.class.equals(serviceInterface)) {
            return new BoundaryServiceImpl();
        }

        return null;

    }

}
