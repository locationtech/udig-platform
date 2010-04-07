package net.refractions.udig.issues.internal.view;

import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IIssuesLabelProvider;
import net.refractions.udig.issues.internal.ImageConstants;
import net.refractions.udig.issues.internal.Images;
import net.refractions.udig.issues.internal.Messages;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Provides images for each of the resolution and priority types.
 * If the object is a String then the string is displayed in the Problem Object column. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesLabelProvider extends LabelProvider implements
		ITableLabelProvider, IColorProvider, IIssuesLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof IIssue) {
			IIssue issue = (IIssue) element;
			switch (columnIndex) {
			case IssuesView.PRIORITY_COLUMN:
				switch (issue.getPriority()) {
				case CRITICAL:
					return Images.get(ImageConstants.PRIORITY_CRITICAL);
				case HIGH:
					return Images.get(ImageConstants.PRIORITY_HIGH);
				case WARNING:
					return Images.get(ImageConstants.PRIORITY_WARNING);
				case LOW:
					return Images.get(ImageConstants.PRIORITY_LOW);
				case TRIVIAL:
					return Images.get(ImageConstants.PRIORITY_TRIVIAL);

				default:
					break;
				}
			case IssuesView.RESOLUTION_COLUMN:
				switch (issue.getResolution()) {
				case RESOLVED:
					return Images.get(ImageConstants.RESOLUTION_RESOLVED);
				case UNKNOWN:
					return Images.get(ImageConstants.RESOLUTION_UNKNOWN);
				case UNRESOLVED:
					return Images.get(ImageConstants.RESOLUTION_UNRESOLVED);
				case IN_PROGRESS:
					return Images.get(ImageConstants.RESOLUTION_VIEWED);

				default:
					break;
				}
			default:
				break;
			}
		}

		return null;
	}

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
		}else if (element instanceof String) {
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

	public Color getForeground(Object element) {
		if (element instanceof IIssue) {
			IIssue issue = (IIssue) element;
			switch (issue.getResolution()) {
			case UNKNOWN:
				return Display.getCurrent().getSystemColor(
						SWT.COLOR_DARK_YELLOW);
			case IN_PROGRESS:
				return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);

			default:
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			}
		}
		return null;
	}

	public Color getBackground(Object element) {
		if ( element instanceof String )
			return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		return null;
	}

    public String getHeaderText( Column column ) {
        switch( column ) {
        case DESCRIPTION:
            return Messages.IssuesView_desc_title; 
        case PROBLEM_OBJECT:
            return Messages.IssuesView_name_title; 
        default:
        }
        return ""; //$NON-NLS-1$
    }

    public String getExtensionID() {
        return null;
    }


}
