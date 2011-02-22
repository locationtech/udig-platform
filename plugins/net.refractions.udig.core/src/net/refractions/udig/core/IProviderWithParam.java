/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * ParamTypeersion 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
