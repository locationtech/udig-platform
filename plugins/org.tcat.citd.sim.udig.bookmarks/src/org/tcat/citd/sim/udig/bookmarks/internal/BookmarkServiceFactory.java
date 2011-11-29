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
package org.tcat.citd.sim.udig.bookmarks.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.tcat.citd.sim.udig.bookmarks.IBookmarkService;

/**
 * Responsible for creating our internal BookmarkServiceImpl.
 * 
 * @author paul.pfeiffer
 */
public class BookmarkServiceFactory extends AbstractServiceFactory {

    @Override
    public IBookmarkService create( Class serviceInterface, IServiceLocator parentLocator,
            IServiceLocator locator ) {
        
        if (IBookmarkService.class.equals(serviceInterface)) {
            return new BookmarkServiceImpl();
        }

        return null;
    }

}
