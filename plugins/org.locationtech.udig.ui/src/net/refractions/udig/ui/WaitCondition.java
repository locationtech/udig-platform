/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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