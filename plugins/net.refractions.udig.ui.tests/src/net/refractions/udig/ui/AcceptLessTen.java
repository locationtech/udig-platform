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
package net.refractions.udig.ui;


/**
 * Accepts only values that are less than 10
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class AcceptLessTen extends AlwaysAcceptDropAction {
    public static class Data{
        public int i;

        public Data( int i ) {
            super();
            this.i = i;
        }
        
    }
    

    @Override
    public boolean accept() {
        if( !(getData() instanceof Data) )
            return false;
        
        if( ((Data)getData()).i<10 )
            return true;
            
        return false;
    }

}
