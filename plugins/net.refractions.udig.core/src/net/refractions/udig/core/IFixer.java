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
