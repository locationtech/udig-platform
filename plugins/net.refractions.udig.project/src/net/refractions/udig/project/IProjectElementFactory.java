/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
