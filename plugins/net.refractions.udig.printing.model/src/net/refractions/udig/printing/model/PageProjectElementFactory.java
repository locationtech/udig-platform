/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
