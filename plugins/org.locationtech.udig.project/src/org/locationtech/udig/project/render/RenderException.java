/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
