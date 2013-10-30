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

import org.eclipse.ui.IMemento;

/**
 * Provides a resolution method for fixing a memento item.
 * 
 * @author chorner
 * @since 1.1.0
 */
public interface IFixer {
	
    /**
     * Determines if the object memento can be handled by this fixer implementation.
     *
     * @param object victim
     * @param memento additional tidbits for fixer initialization and saving state.
     * @return
     */
	public boolean canFix(Object object, IMemento memento);
	
	/**
     * Performs the fix operation. This could be a zoom to a feature, or simply firing up a cheat
     * sheet/dialog.
     * 
     * @param object victim
     * @param memento additional tidbits for fixer initialization and saving state.
     */
    public void fix(Object object, IMemento memento);
	
    /**
     * Informs the fixer that the fix was completed successfully.
     *   
     * @param object victim
     */
    public void complete(Object object);
    
}
