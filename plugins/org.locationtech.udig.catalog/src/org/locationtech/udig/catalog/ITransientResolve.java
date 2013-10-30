/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
