/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.geoselection;

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
