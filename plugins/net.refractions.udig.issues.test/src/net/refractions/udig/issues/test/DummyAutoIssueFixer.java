/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.issues.test;

import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.IIssue;

import org.eclipse.ui.IMemento;

/**
 * Issues are automatically fixed by this fixer, upon calling the fix method. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class DummyAutoIssueFixer extends DummyIssueFixer {

    public void fix( Object object, IMemento fixerMemento ) {
        IIssue issue = (IIssue) object;
        //resolve it right now
        issue.setResolution(Resolution.RESOLVED);
    }

}
