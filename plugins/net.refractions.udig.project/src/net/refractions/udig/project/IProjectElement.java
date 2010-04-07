/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import java.util.List;

/**
 * A element that is contained by a project. The Map and Page classes implement this interface and
 * can therefore be part of a project.
 * 
 * @author jeichar
 * @since 0.3
 */
public interface IProjectElement {
    /**
     * Returns the owner project of element.
     * 
     * @return the owner project of element.
     */
    public IProject getProject();

    /**
     * Returns the name of the element.
     * 
     * @return the name of the element.
     */
    public String getName();
    

    /**
     * Returns an unmodifiable list of the type requested.
     * <p>
     * Some currently valid options are IMap and Page
     */
    public <E> List<E> getElements( Class<E> type );

    /**
     * Returns a List with all elements in the project
     * <p>
     * This is an immutable list
     * </p>
     * 
     * @return a list with all in the project
     */
    public List<?> getElements();

}
