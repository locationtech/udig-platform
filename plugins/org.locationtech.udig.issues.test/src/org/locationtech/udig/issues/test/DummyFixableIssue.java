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
package org.locationtech.udig.issues.test;

import org.locationtech.udig.issues.FixableIssue;

/**
 * Subclass of FixableIssue for extension point testing. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class DummyFixableIssue extends FixableIssue {
    
    public static final String ID = "org.locationtech.udig.issues.test.DummyFixableIssue"; //$NON-NLS-1$
    
    public String getExtensionID() {
        return ID;
    }
}
