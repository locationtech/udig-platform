/**
 * 
 */
package org.locationtech.udig.project.ui.wizard.export;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;

public final class ContentProvider implements ITreeContentProvider {
	@SuppressWarnings("unchecked")
	public Object[] getChildren(Object element) {
		if (element instanceof Collection) {
			Collection collection = (Collection) element;
			return collection.toArray();
		} else if (element instanceof IProject) {
			return ((IProject) element).getElements(IMap.class).toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof Collection || element instanceof IProject;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothong
	}
}
