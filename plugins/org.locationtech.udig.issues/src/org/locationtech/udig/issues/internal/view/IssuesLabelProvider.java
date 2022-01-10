/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.issues.Column;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesLabelProvider;
import org.locationtech.udig.issues.internal.ImageConstants;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.issues.internal.Messages;

/**
 * Provides images for each of the resolution and priority types. If the object is a String then the
 * string is displayed in the Problem Object column.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesLabelProvider extends LabelProvider
        implements ITableLabelProvider, IColorProvider, IIssuesLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof IIssue) {
            IIssue issue = (IIssue) element;
            switch (columnIndex) {
            case IssuesView.PRIORITY_COLUMN:
                switch (issue.getPriority()) {
                case CRITICAL:
                    return IssuesActivator.getDefault().getImage(ImageConstants.PRIORITY_CRITICAL);
                case HIGH:
                    return IssuesActivator.getDefault().getImage(ImageConstants.PRIORITY_HIGH);
                case WARNING:
                    return IssuesActivator.getDefault().getImage(ImageConstants.PRIORITY_WARNING);
                case LOW:
                    return IssuesActivator.getDefault().getImage(ImageConstants.PRIORITY_LOW);
                case TRIVIAL:
                    return IssuesActivator.getDefault().getImage(ImageConstants.PRIORITY_TRIVIAL);

                default:
                    break;
                }
                break;
            case IssuesView.RESOLUTION_COLUMN:
                switch (issue.getResolution()) {
                case RESOLVED:
                    return IssuesActivator.getDefault()
                            .getImage(ImageConstants.RESOLUTION_RESOLVED);
                case UNKNOWN:
                    return IssuesActivator.getDefault().getImage(ImageConstants.RESOLUTION_UNKNOWN);
                case UNRESOLVED:
                    return IssuesActivator.getDefault()
                            .getImage(ImageConstants.RESOLUTION_UNRESOLVED);
                case IN_PROGRESS:
                    return IssuesActivator.getDefault().getImage(ImageConstants.RESOLUTION_VIEWED);

                default:
                    break;
                }
                break;
            default:
                break;
            }
        }

        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof IIssue) {
            IIssue issue = (IIssue) element;
            switch (columnIndex) {
            case IssuesView.OBJECT_COLUMN:
                return issue.getProblemObject();
            case IssuesView.DESC_COLUMN:
                return issue.getDescription();
            default:
                break;
            }
        } else if (element instanceof String) {
            String groupId = (String) element;
            switch (columnIndex) {
            case IssuesView.OBJECT_COLUMN:
                return groupId;
            default:
                break;
            }
        }
        return null;
    }

    @Override
    public Color getForeground(Object element) {
        if (element instanceof IIssue) {
            IIssue issue = (IIssue) element;
            switch (issue.getResolution()) {
            case UNKNOWN:
                return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
            case IN_PROGRESS:
                return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);

            default:
                return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
            }
        }
        return null;
    }

    @Override
    public Color getBackground(Object element) {
        if (element instanceof String)
            return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        return null;
    }

    @Override
    public String getHeaderText(Column column) {
        switch (column) {
        case DESCRIPTION:
            return Messages.IssuesView_desc_title;
        case PROBLEM_OBJECT:
            return Messages.IssuesView_name_title;
        default:
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public String getExtensionID() {
        return null;
    }

}
