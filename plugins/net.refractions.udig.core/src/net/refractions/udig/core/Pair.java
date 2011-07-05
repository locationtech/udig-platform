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
public class Pair<T,V> {
    private T left;
    private V right;
    
    /**
     * Create a new insance
     * @param left
     * @param right
     */
    public Pair(final T left, final V right){
        this.left = left;
        this.right = right;
    }

    /**
     * Factory method
     * @return return a new instance
     */
    public static <L,R> Pair<L,R> create(L left, R right){
        return new Pair<L,R>(left,right);
    }
    
    public final T l(){
        return getLeft();
    }
    
    public final V r(){
        return getRight();
    }
    
    public final T left(){
        return getLeft();
    }
    
    public final V right(){
        return getRight();
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

    @Override
    public String toString() {
    	return "Pair("+left+", "+right+")";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pair other = (Pair) obj;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
            return false;
        return true;
    }
    
    
    
    
}
