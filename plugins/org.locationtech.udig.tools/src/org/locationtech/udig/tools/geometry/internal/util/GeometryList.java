/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.geometry.internal.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * List of Geometries
 * <p>
 * It is a workaround to allow compare geometries by its coordinates 
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 * @param <T>
 */
public final class GeometryList< T extends Geometry> extends LinkedList<T> implements List<T>, Deque<T>, Cloneable, Serializable{

    private static final long serialVersionUID = 3477623090320403970L;
    

    public GeometryList() {
        super();
    }
    
    public GeometryList( Collection<T> values ) {
        super(values);
    }

    /**
     *  Compares the geometries using {@link Geometry#equalsExact(Geometry)}
     */
    @Override
    public boolean contains(Object o) {
        
        T requested = (T) o;
        for( T g: this ) {
            
            T p = (T)g;
            if(p.equalsExact(requested)){ 
                return true;
            }
        }
        return false;
    }
    
}
