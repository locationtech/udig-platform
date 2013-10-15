/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.scalebar;

import net.refractions.udig.core.IProviderWithParam;

/**
 * Provides numbers like:  1, 2, 5, 10, 20, 50, 100, ... 
 * 
 * @author jesse
 * @since 1.1.0
 */
public class NiceIntegers implements IProviderWithParam<Integer, Integer> {

    public Integer get( Integer param ) {
        if( param.intValue()==-1 ){
            return 1;
        }
        String prev = String.valueOf(param);
        int next;
        switch( prev.charAt(0) ) {
        case '1':
            next = 2;
            break;
        case '2':
            next = 5;
            break;
        case '5':
            next=10;
            break;

        default:
            next = 10;
            break;
        }
        int digits = prev.length()-1;
        next *= Math.max(1, Math.pow(10, digits));
        return next;
    }

}
