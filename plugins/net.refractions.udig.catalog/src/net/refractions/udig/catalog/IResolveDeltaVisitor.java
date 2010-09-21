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
package net.refractions.udig.catalog;

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