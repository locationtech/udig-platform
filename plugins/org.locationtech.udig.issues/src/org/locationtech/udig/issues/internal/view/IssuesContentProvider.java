/**
 * 
 */
package org.locationtech.udig.issues.internal.view;

import java.util.Set;

import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesContentProvider;
import org.locationtech.udig.issues.IIssuesList;
import org.locationtech.udig.issues.internal.Messages;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author jones
 */
public class IssuesContentProvider implements IIssuesContentProvider {
	private boolean showGroup=false;
	private IIssuesList list;
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IIssuesList) {
			if( showGroup ){
				Set<String> groups = list.getGroups();
				if( groups.contains(null) ){
					groups.remove(null);
					groups.add(Messages.IssuesContentProvider_defaultGroup);
				}
				return groups.toArray();
			}else{
				return list.toArray();
			}
		}
		if (parentElement instanceof String) {
			String groupId = (String) parentElement;
			if( groupId.equals(Messages.IssuesContentProvider_defaultGroup) ){
				return list.getIssues(null).toArray();
			}
			return list.getIssues(groupId).toArray();
			
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof String) {
			return list;
		}
		if (element instanceof IIssue) {
			if( showGroup ){
				IIssue issue = (IIssue) element;
				String groupId = issue.getGroupId();
				if ( groupId==null ){
					return Messages.IssuesContentProvider_defaultGroup;
				}
				return groupId;
			}else{
				return list;
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if ( element instanceof IIssuesList || element instanceof String )
			return true;
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.list=(IIssuesList)newInput;
	}

	/**
	 * List issues as groups of issues.
	 */
	public void setShowGroup(boolean showGroup) {
		this.showGroup = showGroup;
	}

    public String getExtensionID() {
        return ""; //$NON-NLS-1$
    }
	
}
