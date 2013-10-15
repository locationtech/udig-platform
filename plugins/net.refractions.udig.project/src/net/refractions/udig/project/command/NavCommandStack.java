/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command;

/**
 * API comment me Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre><code>
 *  
 *   NavCommandStack x = new NavCommandStack( ... );
 *   TODO code example
 *   
 * </code></pre>
 * 
 * </p>
 * API access the stack elements?
 * 
 * @author Jesse
 * @since 0.3
 */
public interface NavCommandStack {
    /**
     * @return <code>true</code> if there is a position in the command stack that the viewport can
     *         return to.
     */
    public boolean hasBackHistory();

    /**
     * @return <code>true</code> if the backHistory method has been called and no new nav commands
     *         have occurred;
     */
    public boolean hasForwardHistory();

}
