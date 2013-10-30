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
