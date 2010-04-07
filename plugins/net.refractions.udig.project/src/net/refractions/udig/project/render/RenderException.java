/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.render;

/**
 * Indicates that an exception during Rendering has occurred.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class RenderException extends Exception {

    /** <code>serialVersionUID</code> field */
    private static final long serialVersionUID = 3762814883208902450L;

    /**
     * Construct <code>RenderException</code>.
     * 
     * @param msg comment about the exception
     * @param t the exception that occurred
     */
    public RenderException( String msg, Throwable t ) {
        super(msg, t);
    }

    /**
     * Construct <code>RenderException</code>.
     * 
     * @param msg comment about the exception
     */
    public RenderException( String msg ) {
        super(msg);
    }

    /**
     * Construct <code>RenderException</code>.
     * 
     * @param msg comment about the exception
     */
    public RenderException() {
        super();
    }

    /**
     * Construct <code>RenderException</code>.
     * 
     * @param t the exception that occurred
     */
    public RenderException( Throwable t ) {
        super(t);
    }

}
