/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal;

import org.locationtech.udig.issues.AbstractIssue;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A placeholder issue for issues that failed loading.  Contains all the information for persisting the issue again and will show up in the list
 * but the fix it method opens an error dialog warning the user that the issue plugin seems to be missing.
 * @author Jesse
 * @since 1.1.0
 */
public class PlaceholderIssue extends AbstractIssue {

    private String extID;
    private IMemento memento;

    public void fixIssue( IViewPart part, IEditorPart editor ) {
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), Messages.PlaceholderIssue_shellTitle, Messages.PlaceholderIssue_messagePart1 +  
                Messages.PlaceholderIssue_messagePart2, Status.OK_STATUS); 
    }

    public String getExtensionID() {
        return extID;
    }
    
    public void setExtensionID(String id){
        extID=id;
    }

    public String getProblemObject() {
        return Messages.PlaceholderIssue_problemObject; 
    }

    public void init( IMemento memento, IMemento viewMemento, String issueId, String groupId,
            ReferencedEnvelope bounds ) {
        this.memento=memento;
        setId(issueId);
        setBounds(bounds);
        setGroupId(groupId);
        setViewMemento(viewMemento);
    }

    public void save( IMemento memento ) {
        memento.putMemento(this.memento);
    }

}
