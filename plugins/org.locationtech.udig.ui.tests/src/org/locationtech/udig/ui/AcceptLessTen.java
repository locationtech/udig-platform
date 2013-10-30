/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;


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
