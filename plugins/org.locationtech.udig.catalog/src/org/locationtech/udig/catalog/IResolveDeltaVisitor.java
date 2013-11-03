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
package org.locationtech.udig.catalog;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

/**
 * Allows processing of resolve deltas.
 * <p>
 * Usage:
 * 
 * <pre>
 *  class Visitor implements IResolveDeltaVisitor {
 *      public boolean visit(IResolveDelta delta) {
 *          switch (delta.getKind()) {
 *          case IDelta.ADDED :
 *              // handle added handled
 *              break;
 *          case IDelta.REMOVED :
 *              // handle removed handled
 *              break;
 *          case IDelta.CHANGED :
 *              // handle changed handled
 *              break;
 *          case IDelta.REPLACED :
 *              // handle replaced handled
 *              break;    
 *          }
 *          return true;
 *      }
 *  }
 *  ICatalogDelta rootDelta = ...;
 *  rootDelta.accept(new Visitor());
 * </pre>
 * 
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @author Jody Garnett, Refractions Research
 * @since 0.9.0
 */
public interface IResolveDeltaVisitor {

    /**
     * Visits the given resolve delta.
     * 
     * @return <code>true</code> if the resource delta's children should be visited;
     *         <code>false</code> if they should be skipped.
     * @exception CoreException if the visit fails for some reason.
     */
    public boolean visit( IResolveDelta delta ) throws IOException;
}
