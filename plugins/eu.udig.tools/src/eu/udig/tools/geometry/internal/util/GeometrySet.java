/**
 * 
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
