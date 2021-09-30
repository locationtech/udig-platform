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
package org.locationtech.udig.bookmarks.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.locationtech.udig.bookmarks.IBookmarkService;

/**
 * Responsible for creating our internal BookmarkServiceImpl.
 *
 * @author paul.pfeiffer
 */
public class BookmarkServiceFactory extends AbstractServiceFactory {

    @Override
    public IBookmarkService create(Class serviceInterface, IServiceLocator parentLocator,
            IServiceLocator locator) {

        if (IBookmarkService.class.equals(serviceInterface)) {
            return new BookmarkServiceImpl();
        }

        return null;
    }

}
