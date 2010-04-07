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
 * Condition to wait for to become true;
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface WaitCondition {
    public static final WaitCondition FALSE_CONDITION = new WaitCondition() {
        
        public boolean isTrue()  {
            return false;
        }
    
    };
    public static final WaitCondition TRUE_CONDITION = new WaitCondition() {
        
        public boolean isTrue() {
            return true;
        }
    
    };

    /**
	 * @return true if condition is meth
	 * @throws Exception
	 */
	public boolean isTrue() ;
}