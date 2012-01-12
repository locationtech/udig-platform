/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;

import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IFolder;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for the document view
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public class DocumentContentProvider implements ITreeContentProvider {

    private TreeViewer viewer;
    private IFolder folder;
    //private String selectedFeature = null;
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        if (folder == newInput) {
            return; // go away
        }
        if (oldInput == newInput) {
            return; // further away
        }
        if (oldInput != null) {
            // clean up old input - remove listeners
        }
        this.folder = (IFolder) newInput;
        if (newInput != null) {
            // setup document source - add listeners
        }
        if (this.viewer != viewer) {
            if (this.viewer != null) {
                // clean up any thing we need to (checkbox status, cell editor things, columns?)
            }
            this.viewer = (TreeViewer) viewer;
        }
    }

    @Override
    public Object[] getElements( Object inputElement ) {
        return getRows(inputElement);
    }

    /*
     * returns the rows in the element
     */
    private Object[] getRows( Object inputElement ) {
        
        if (inputElement instanceof IDocument) {
            IDocument document = (IDocument)inputElement;
            ArrayList<IDocument> contents = new ArrayList<IDocument>();
            contents.add(document);
            return contents.toArray();
        }
        else if (inputElement instanceof IFolder) {
            ArrayList<IFolder> contents = new ArrayList<IFolder>();
            IFolder folder = (IFolder)inputElement;
            int size = folder.getCount();
            for (int i=0; i<size; i++) {
                IFolder row = folder.getRow(i);
                contents.add(row);
            }
            return contents.toArray();
        }
        return null;
    }
    
    @Override
    public Object[] getChildren( Object inputElement ) {
        return getRows(inputElement);
    }

    @Override
    public Object getParent( Object element ) {
        return null;
    }

    @Override
    public boolean hasChildren( Object element ) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder)element;
            if (folder.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param selectedFeature
     */
//    public void setSelectedFeature( String selectedFeature ) {
//        this.selectedFeature = selectedFeature;
//    }

}
