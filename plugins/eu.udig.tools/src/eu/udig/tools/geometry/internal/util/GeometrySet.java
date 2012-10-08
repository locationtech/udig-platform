/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.geometry.internal.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Overide LinkedHashSet in order to reimplement the contains method
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
public class GeometrySet<T extends Geometry> extends LinkedHashSet< T > implements Set<T> , Cloneable, Serializable{

    private static final long serialVersionUID = 3635060680946966450L;


    public GeometrySet() {
        super();
    }
    
    public GeometrySet( Collection<T> values ) {
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
