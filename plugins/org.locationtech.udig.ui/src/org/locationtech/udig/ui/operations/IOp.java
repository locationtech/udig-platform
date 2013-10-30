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
package net.refractions.udig.ui.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Define user interface needs for a for an operation.
 * @author jgarnett
 * @since 0.3
 */
public interface IOp {
    /** <code>EXTENSION_POINT</code> field */
    public static final String EXTENSION_POINT="net.refractions.udig.ui.operation";  //$NON-NLS-1$
  
    /**
     * Each operation is called by the frame work in a seperate thread.  Interaction with the display must
     * be done by calling display.asyncExec() and display.syncExec().
     * <p>
     * Each operation implementation must define the steps needed to carry
     * out its function.
     * <p>
     * @param target the object that the operation operates on.   This parameter will be the same as the type declared
     * in targetClass of the operation extension.  If the enablesFor attribute of the operation extension 
     * is not 1 or undefined then target will be an array of objects of the type declared in targetClass.
     */
    public void op( final Display display, final Object target, final IProgressMonitor monitor )throws Exception;
  
}