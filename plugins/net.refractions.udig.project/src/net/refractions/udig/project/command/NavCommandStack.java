/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
