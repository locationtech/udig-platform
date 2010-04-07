/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.model;

import net.refractions.udig.project.IProjectElementFactory;
import net.refractions.udig.project.internal.ProjectElement;

/**
 * Provides a way to arbitrarily create a Page
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class PageProjectElementFactory implements IProjectElementFactory {

    /**
     * Asks the ModelFactory to create a page. Used for uDig's IProjectElement aspect.
     * @see Page
     * @return a new Page object
     */
    public ProjectElement create() {
        return ModelFactory.eINSTANCE.createPage();
    }

}
