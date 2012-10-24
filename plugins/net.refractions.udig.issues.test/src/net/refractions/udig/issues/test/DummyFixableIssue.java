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

import net.refractions.udig.issues.FixableIssue;

/**
 * Subclass of FixableIssue for extension point testing. 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.0
 */
public class DummyFixableIssue extends FixableIssue {
    
    public static final String ID = "net.refractions.udig.issues.test.DummyFixableIssue"; //$NON-NLS-1$
    
    public String getExtensionID() {
        return ID;
    }
}
