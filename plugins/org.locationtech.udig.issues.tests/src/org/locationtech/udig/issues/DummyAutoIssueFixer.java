/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.IIssue;

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
