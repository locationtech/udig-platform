/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

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

    public static final Option NONE = new None();

    private Option() {
    }

    public boolean isDefined() {
        return false;
    }

    public T value() {
        throw new NoSuchElementException();
    }

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
    private static final class None<V> extends Option<V> {
    }

    public static final class Some<V> extends Option<V> {
        private V value;

        public Some(V value) {
            this.value = value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public V value() {
            return value;
        }
    }
}
