/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
