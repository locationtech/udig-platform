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

import org.locationtech.udig.core.IFixer;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.AbstractFixableIssue;
import org.locationtech.udig.issues.IIssue;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMemento;

public class DummyIssueFixer implements IFixer {

    /**
     * Simple flag read from the fixerMemento to determine if an issue can be fixed. This is not a
     * realistic case, as we would usually look to see if certain keys exist and if their values
     * conform to our ideals.
     */
    public static final String KEY_FIXABLE = "fixable"; //$NON-NLS-1$
    
    MessageDialog messageDialog = null;
    
    public boolean canFix( Object object, IMemento fixerMemento ) {
        //not null
        if (object == null || fixerMemento == null) {
            return false;
        }
        //must be instance
        if (!(object instanceof AbstractFixableIssue)) {
            return false;
        }
        IIssue issue = (IIssue) object;
        //not already resolved
        if (issue.getResolution() == Resolution.RESOLVED) {
            return false;
        }
        String fixable = fixerMemento.getString(KEY_FIXABLE);
        return fixable != null && fixable.equalsIgnoreCase("TRUE"); //$NON-NLS-1$
    }

    public void fix( Object object, IMemento fixerMemento ) {
        IIssue issue = (IIssue) object;
        //set resolution to "in progress" (optional, but a good idea)
        issue.setResolution(Resolution.IN_PROGRESS);
        //at this point, some mystical dialog or workflow process decides to call complete
    }

    public void complete( Object object ) {
        ((IIssue) object).setResolution(Resolution.RESOLVED);
    }
    
    /**
     * Obtains the dialog that pops up to ask question (this method is used to programatically click
     * the button).
     * 
     * @return MessageDialog
     */
    public MessageDialog getDialog() {
        return messageDialog;
    }
}
