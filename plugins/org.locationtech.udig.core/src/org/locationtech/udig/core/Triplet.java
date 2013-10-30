/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
