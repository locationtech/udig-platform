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

/**
 * A simple class for wrapping a couple of objects.  Often for return types.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class Triplet<T,U,V> {
    private final  T left;
    private final U middle;
    private final V right;
    
    public Triplet(final T left, final U middle, final V right){
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    /**
     * @return Returns the left.
     */
    public T getLeft() {
        return left;
    }

    /**
     * @return Returns the right.
     */
    public V getRight() {
        return right;
    }

	public U getMiddle() {
		return middle;
	}
    
    
}
