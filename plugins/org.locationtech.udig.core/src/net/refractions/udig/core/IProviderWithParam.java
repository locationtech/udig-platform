/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core;


/**
 * A generic interface for object that can be asked for a type of object.
 * 
 * <p>
 * Example: 
 * A draw point object requires a point to draw.  However a different object is responsible
 * for generating those points.  That object could implement the Provider<Point> interface
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 * 
 * @param <RetType> The return type
 * @param <ParamType> The parameter type
 */
public interface IProviderWithParam<RetType,ParamType> {
    /**
     * Gets the value 
     *
     * @param param a parameter that can be used to calculate the new value.
     * @return the value
     */
    RetType get(ParamType param);
}
