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
package net.refractions.udig.core;

import java.util.NoSuchElementException;

import org.opengis.feature.simple.SimpleFeature;



/**
 * An object that indicates that it can either be None or have a value. This for return values so
 * that they don't return null as a value and is dangerously NullPointerException prone.
 * <p>
 * A user will always do an instance of check to see if it is a {@link Some} or {@link None}. This
 * class should not be further extended
 * </p>
 * 
 * @author jesse
 * @since 1.1.0
 * @see None
 * @see Some
 */
public abstract class Option<T> {

    @SuppressWarnings("unchecked")
    public static final Option NONE = new None();

    private Option() {
    }

    public boolean isDefined(){return false;}
    public T value(){ throw new NoSuchElementException(); }

	public boolean isNone() {
		return !isDefined();
	}

    @SuppressWarnings("unchecked")
	public static <V> None<V> none() {
    	return (None<V>) NONE;
    }

	public static Option<SimpleFeature> some(SimpleFeature next) {
		return new Some(next);
	}
    
    /**
     * Indicates a none or null value.
     * 
     * @author jesse
     * @since 1.1.0
     * @param <V>
     */
    private final static class None<V> extends Option<V> {
    }

    public final static class Some<V> extends Option<V> {
        private V value;

        public Some( V value ) {
            this.value = value;
        }


        public boolean isDefined(){return true;}        

        public V value() {
            return value;
        }
    }
}
