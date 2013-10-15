/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project;

import net.refractions.udig.project.internal.ProjectElement;

/**
 * Provides extension points with a mechanism for blinding creating IProjectElement objects.
 * Implementors simply need to implement the create method which will return a new IProjectElement
 * object.
 * 
 * @author Richard Gould
 * @since 0.3
 */
public interface IProjectElementFactory {
    /**
     * Instantiates an IProjectElement object. This class is only intended to be implemented by
     * extenders that wish to add new project elements to udig; it is not intended to be used by end
     * developers.
     * 
     * @return a newly created IProjectElement object
     */
    public ProjectElement create();
}
