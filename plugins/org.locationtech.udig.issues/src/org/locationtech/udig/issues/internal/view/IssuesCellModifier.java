/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.IIssue;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TreeItem;

/**
 * CellModifier to allow editting the issues in the issues list.
 * 
 * @author jones
 * @since 1.0.0
 */
public class IssuesCellModifier implements ICellModifier {

    private IssuesView view;

    public IssuesCellModifier( IssuesView view ) {
        this.view = view;
    }

    public boolean canModify( Object element, String property ) {
        return Integer.parseInt(property) != IssuesView.OBJECT_COLUMN;
    }

    public Object getValue( Object element, String property ) {
        switch( Integer.parseInt(property) ) {
        case IssuesView.RESOLUTION_COLUMN:
            return ((IIssue) element).getResolution();

        case IssuesView.PRIORITY_COLUMN:
            return ((IIssue) element).getPriority();

        case IssuesView.DESC_COLUMN:
            String desc = ((IIssue) element).getDescription();
            return desc != null ? desc : ""; //$NON-NLS-1$

        }
        return null;
    }

    public void modify( Object element, String property, Object value ) {
    	TreeItem item = (TreeItem) element;
    	if ( !(item.getData() instanceof IIssue) ) return ; 
			
		
        IIssue issue = (IIssue) item.getData();
        switch( Integer.parseInt(property) ) {
        case IssuesView.RESOLUTION_COLUMN:
            issue.setResolution((Resolution) value);
            view.viewer.refresh(issue, true);

            if (view.resolvedIssuesShown) {
                if (value != Resolution.RESOLVED) {
                    if (view.updateTimerJob.isUser())
                        view.updateTimerJob.setSystem(true);
                    view.updateTimerJob.schedule(2000);
                }
            } else {
                if (value == Resolution.RESOLVED) {
                    if (view.updateTimerJob.isUser())
                        view.updateTimerJob.setSystem(true);
                    view.updateTimerJob.schedule(2000);
                }
            }
            break;
        case IssuesView.PRIORITY_COLUMN:
            issue.setPriority((Priority) value);
            view.viewer.refresh(issue, true);

            break;
        case IssuesView.DESC_COLUMN:
            issue.setDescription((String) value);
            view.viewer.refresh(issue, true);
            break;

        default:
            break;
        }
    }

}
