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