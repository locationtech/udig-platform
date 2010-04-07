/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
 * A specialized pair where only one of the two should be set
 * 
 * @author jesse
 * @since 1.1.0
 */
public class Either<T1, T2> extends Pair<T1, T2> {

    private boolean isLeft;

    private Either( boolean isleft, T1 left, T2 right ) {
        super(left, right);
        this.isLeft = isleft;
    }
    
    public static <T1,T2> Either<T1, T2> createLeft(T1 value){
        return new Either<T1, T2>(true, value, null);
    }

    
    public static <T1,T2> Either<T1, T2> createRight(T2 value){
        return new Either<T1, T2>(false, null, value);
    }

    public boolean isLeft(){ return isLeft; }
    public boolean isRight(){ return !isLeft; }
    
    @Override
    public T1 getLeft() {
        if(!isLeft()){
            throw new IllegalStateException("The left value is not set.  Call isLeft first"); //$NON-NLS-1$
        }
        return super.getLeft();
    }
    
    @Override
    public T2 getRight() {
        if(!isRight()){
            throw new IllegalStateException("The right value is not set.  Call isRight first"); //$NON-NLS-1$
        }
        return super.getRight();
    }
}
