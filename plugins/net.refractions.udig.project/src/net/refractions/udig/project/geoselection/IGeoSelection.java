/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.geoselection;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;

/**
 * The interface for GeoSelection containers implementations.
 * <p>
 * The containers follow to IAdaptable design pattern to hide the 
 * implementation specifics. Usually <code>IGeoSelectionListener</code>s should not be
 * aware about actual implementation class of IGeoSelection.
 * <p>
 * 
 * 
 * @author Vitalus
 */
public interface IGeoSelection extends IAdaptable {

    /**
     * Custom implementations of interface can iterate through arbitrary set of objects with
     * "selected" semantic.
     * <p>
     * 
     * @return
     */
    public Iterator iterator();
    
    

}
