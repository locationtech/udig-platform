/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog;

/**
 * This is a tag interface that indicates a resolve is transient.  IE it is not persisted between
 * runs.  If a Resolve resolves to this interface then it is known that what ever data is contains
 * will not be available the next run and the decision must be made to persist or not persist the
 * data.  
 * 
 * <p>For example a MemoryService resolves to this interface and the user should asked whether to
 * save the features in another Service.
 * </p>
 * @author jones
 * @since 1.1.0
 */
public interface ITransientResolve {

}
