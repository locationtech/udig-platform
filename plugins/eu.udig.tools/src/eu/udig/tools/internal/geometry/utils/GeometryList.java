package eu.udig.tools.internal.geometry.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

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
