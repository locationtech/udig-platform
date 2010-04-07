/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues.internal;

import net.refractions.udig.issues.AbstractIssue;

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
