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
package net.refractions.udig.project;

/**
 * The interface for all classes that decorate non-eclipse classes. This allows access to the "Real"
 * object if necessary.
 * 
 * @author jeichar
 * @since 0.3
 */
public interface UDIGAdaptableDecorator {
    /**
     * Get the "real" object.
     * 
     * @return the "real object
     */
    Object getObject();
}
