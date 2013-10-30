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
package net.refractions.udig.issues;

import org.eclipse.ui.IMemento;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class FixableIssue extends AbstractFixableIssue {

    public static final String ID = "net.refractions.udig.issues.FixableIssue"; //$NON-NLS-1$
    public static final String KEY_PROBLEMOBJECT = "problemObject"; //$NON-NLS-1$
    
    String problemObject = null;
    
    public String getExtensionID() {
        return ID;
    }

    public String getProblemObject() {
        return problemObject;
    }
    
    public void setProblemObject(String problemObject) {
        this.problemObject = problemObject;
    }

    @Override
    public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId, ReferencedEnvelope bounds ) {
        //do other initializations
        super.init(memento, viewMemento, issueId, groupId, bounds);
        //get the problem object
        if (memento == null) {
            problemObject = null;
        } else {
            problemObject = memento.getString(KEY_PROBLEMOBJECT);
        }
    }
    
    @Override
    public void save( IMemento memento ) {
        //save the other stuff
        super.save(memento);
        //save the problem object
        memento.putString(KEY_PROBLEMOBJECT, problemObject);
    }
}
