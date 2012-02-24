package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.IFolder;
import net.refractions.udig.project.internal.Folder;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides content control for the LegendView's tree viewer.
 * 
 * @author nchan
 * @since 1.2.0
 */
public class LegendViewContentProvider extends ArrayContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof Folder || parentElement instanceof IFolder) {
            final Folder folder = (Folder) parentElement;
            return folder.getItems().toArray();
        }
        return null;
    }

    @Override
    public Object getParent( Object element ) {
        return null;
    }

    @Override
    public boolean hasChildren( Object element ) {
        if (element instanceof Folder || element instanceof IFolder) {
            final Folder folder = (Folder) element;
            if (folder.getItems().size() > 0) {
                return true;
            }
        }
        return false;
    }
    
}
