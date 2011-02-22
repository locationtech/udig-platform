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
